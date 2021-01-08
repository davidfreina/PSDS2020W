# https://stackoverflow.com/a/56193442
# https://pythonexamples.org/python-opencv-image-filter-convolution-cv2-filter2d/

from re import findall
from skimage.measure import compare_ssim
from skimage import io
import boto3
import cv2
import numpy as np


def sort_frames(video_bucket_id, subfolder_link, s3):
    """This function takes the S3 bucket containing the videos and frames,
    the respective subfolderlink where the frames for the analysis are stored and an S3 object.

    Args:
        video_bucket_id (str): ID of the S3 bucket containing the videos
        subfolder_link (str): HTTPS-Link to the subfolder containing the extracted frames
        s3 (ServiceResource): S3 object

    Returns:
        list[str]: sorted list containing the frame names in ascending order
    """
    # video_id = video_id[video_id.rfind('/')+1:video_id.rfind('.')]
    subfolder_link_split = subfolder_link.split('/')
    split_folder_name = subfolder_link_split[-1]
    video_name = subfolder_link_split[-2]
    # Get all frames from VIDEO_NAME/SPLIT_FOLDER_NAME
    frames = s3.list_objects_v2(
        Bucket=video_bucket_id, Prefix=video_name + '/' + split_folder_name + '/')
    # Get only the names of the return frames by first only taking all .jpg and then replacing the folder prefix with nothing
    frame_names = list(map(lambda frame: frame["Key"].replace(
        video_name + '/' + split_folder_name + '/', ''), (filter(lambda frame: frame["Key"].find(".jpg") != -1, frames["Contents"]))))
    # Get only the numbers of the frames to sort them ascending and not lexicographically
    frame_numbers = list(map(lambda frame: int(
        findall(r'\d+', frame)[0]), frame_names))
    # Sort the frame names ascending by using their numbers
    frame_names_sorted = list(zip(*sorted(zip(frame_numbers, frame_names))))[1]
    return frame_names_sorted, video_name, split_folder_name


def analyze_frames(subfolder_link, frame_names_sorted, s3, video_bucket_id, subfolder_name):
    """This function takes the subfolder_link where the frames are stored and
    the sorted list of frames as input and returns a list containing
    the difference values between two subsequent frames.

    Args:
        subfolder_link (str): HTTPS-Link to the subfolder containing the extracted frames
        frame_names_sorted (list[str]): sorted list containing the frame names in ascending order

    Returns:
        list[number]: list which contains the differences between images in intervall [-1,1].
    """
    kernel = np.array([[0.0, -8.0, 0.0],
                       [-8.0, 32.0, -8.0],
                       [0.0, -8.0, 0.0]])
    kernel = kernel/(np.sum(kernel) if np.sum(kernel) != 0 else 1)

    for frame in frame_names_sorted:
        with open('/tmp/' + frame, 'wb') as data:
            s3.download_fileobj(video_bucket_id, subfolder_name + '/' + frame, data)

    images = list(map(io.imread, ('/tmp/' + frame_name for frame_name in frame_names_sorted)))

    image_data = list(map(lambda imageFilter: cv2.filter2D(imageFilter, -1, kernel), map(lambda imageBGR2GRAY: cv2.cvtColor(
        imageBGR2GRAY, cv2.COLOR_BGR2GRAY), map(lambda imageRGB2BGR: cv2.cvtColor(imageRGB2BGR, cv2.COLOR_RGB2BGR), images))))
    differences = []
    for i, j in zip(range(len(frame_names_sorted)), range(1, len(frame_names_sorted))):
        differences.append(compare_ssim(
            image_data[i], image_data[j], full=True))
    return [frame[0] for frame in differences]


def remove_frames_from_bucket(video_bucket_id, video_name, split_folder_name, frame_differences, frame_names_sorted, s3):
    """This function gets the S3 bucket id, the name of the analyzed video and the name of the split which it should analze,
    the differences between the frames, the sorted frame names and the s3 object.

    Args:
        video_bucket_id (str): ID of the S3 bucket containing the videos
        video_name (str): Name of the video which should be analyzed
        split_folder_name (str): Name of the folder where to be analyzed frames are stored
        list[number]: list which contains the differences between images in intervall [-1,1].
        frame_names_sorted (list[str]): sorted list containing the frame names in ascending order
        s3 (ServiceResource): S3 object
    """
    frames_to_remove = list(filter(lambda difference_frame: difference_frame[0] > 0.75, zip(
        frame_differences, frame_names_sorted)))
    if (len(frames_to_remove) > 1):
        frames_to_remove = list(zip(*frames_to_remove))[1]
        frames_to_remove = [{'Key': video_name + '/' + split_folder_name +
                             '/' + frame_name} for frame_name in frames_to_remove]
        print(frames_to_remove)
        res = s3.delete_objects(Bucket=video_bucket_id, Delete={
                                "Objects": frames_to_remove})
        assert res["ResponseMetadata"]["HTTPStatusCode"] == 200
    else:
        print("No frames to remove!")


def lambda_handler(event, context):
    subfolder_link = event['extractedFramesSplitFolder']
    video_bucket_id = subfolder_link[subfolder_link.find(
        '/'):subfolder_link.find('.')].replace('/', '')
    s3 = boto3.client('s3')
    sorted_frames, video_name, split_folder_name = sort_frames(
        video_bucket_id, subfolder_link, s3)
    frame_differences = analyze_frames(subfolder_link, sorted_frames, s3, video_bucket_id, video_name + '/' + split_folder_name)
    remove_frames_from_bucket(video_bucket_id, video_name,
                              split_folder_name, frame_differences, sorted_frames, s3)
    return {'analyzeFramesSplitFolder': subfolder_link}

if __name__ == "__main__":
    lambda_handler(
        {'extractedFramesSplitFolder': 'https://videobucketfreinathoeni.s3.amazonaws.com/1603377206/split1'}, 0)