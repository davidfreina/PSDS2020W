import cv2
import boto3
import re
import math


def handler_function(event, context):

    s3 = boto3.client('s3')

    file = event['videoLinks']
    numberOfFramesToAnalyzePerInstance = int(
        event['numberOfFramesToAnalyzePerInstance'])

    video_bucket_id = file.split('.')[0].replace('https://','')
    file_name = file.split('/')[-1]

    folder = re.search('[0-9]+.mp4', file).group(0).split(".mp4")[0]
    subfolder = ""
    image_name = ""
    last_image = None
    link = file.split(folder)[0]

    ret_links = []

    with open('/tmp/' + file_name, 'wb') as data:
        s3.download_fileobj(video_bucket_id, file_name, data)

    data.close()

    vidcap = cv2.VideoCapture('/tmp/' + file_name)

    fps = math.floor(vidcap.get(cv2.CAP_PROP_FPS)/2)  # frame rate

    print("Totel number of frames: %d" % vidcap.get(cv2.CAP_PROP_FRAME_COUNT))
    print("Extracting every " + str(fps) + " frames")

    success, image = vidcap.read()
    count, foldercount, images_of_current_folder = 0, 0, 0
    while success:

        frame_id = vidcap.get(cv2.CAP_PROP_POS_FRAMES)  # current frame number
        # print(frame_id)

        # Split frames to analyze into subfolders
        if images_of_current_folder == 0 or images_of_current_folder >= numberOfFramesToAnalyzePerInstance:
            foldercount += 1
            subfolder = "split" + str(foldercount)
            file_name = folder + "/" + subfolder + "/" + image_name
            images_of_current_folder = 0
            if last_image is not None:
                save_image(last_image, file_name, s3, video_bucket_id)
                images_of_current_folder += 1
            ret_links.append(link + folder + "/" + subfolder)

        # Extract a frame every 0.5 seconds using the FPS number
        if frame_id % fps == 1:
            image_name = "frame%d.jpg" % frame_id
            file_name = folder + "/" + subfolder + "/" + image_name
            last_image = save_image(image, file_name, s3, video_bucket_id)
            print("Extracted frame %d" % frame_id)

            images_of_current_folder += 1

        # Read next frame
        success, image = vidcap.read()
        if not success:
            print('End of file')
        count += 1

    vidcap.release()
    cv2.destroyAllWindows()

    print(ret_links)

    return {"extractedFramesSplitFolders": ret_links}


def save_image(image_data, file_name, s3, video_bucket_id):
    image_string = cv2.imencode('.jpg', image_data)[1].tobytes()
    s3.put_object(Bucket=video_bucket_id, Key=file_name,
                  Body=image_string)

    return image_data


if __name__ == "__main__":
    handler_function({"videoLinks": "https://videobucketfreinathoeni.s3.amazonaws.com/1603366941.mp4",
                      "numberOfFramesToAnalyzePerInstance": 200}, None)
