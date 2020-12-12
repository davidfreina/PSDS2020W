# https://stackoverflow.com/a/56193442

import json
import boto3
import cv2
from skimage.measure import compare_ssim
from re import findall

def downloadImages(videoBucketId, videoId):
    videoId = videoId[0:videoId.find('.')]
    s3 = boto3.client('s3')
    res = s3.list_objects(Bucket=videoBucketId, Prefix=videoId + "/")
    # print(res["Contents"])
    frameNames = list(map(lambda frame: frame["Key"].replace(videoId + '/',''), (filter(lambda frame: frame["Key"].find(".jpg") != -1, res["Contents"]))))
    frameNumbers = list(map(lambda frame: int(findall(r'\d+', frame)[0]), frameNames))
    frameNamesSorted = list(zip(*sorted(zip(frameNumbers, frameNames))))[1]
    # imageData = list(map(lambda image: cv2.cvtColor(image, cv2.COLOR_BGR2GRAY), map(lambda frame: cv2.imread(frame), frameNames)))
    differences = []
    for i, j in zip(range(len(frameNamesSorted)), range(1, len(frameNamesSorted))):
        differences.append(compare_ssim(imageData[i], imageData[j], full=True))



def lambda_handler(event, context):
    videoBucketId = event['videoBucketId']
    videoId = event['videoId']
    downloadImages(videoBucketId, videoId)

if __name__ == "__main__":
    lambda_handler({'videoBucketId': 'videobucketfreinathoeni', 'videoId': '1603365437.mp4'}, 0)
