import json
import boto3
from re import findall
from functools import reduce

def downloadImages(videoBucketId, videoId):
    videoId = videoId[0:videoId.find('.')]
    s3 = boto3.client('s3')
    res = s3.list_objects(Bucket=videoBucketId, Prefix=videoId + "/")
    # print(res["Contents"])
    frameNumbers = list(map(lambda frame: int(findall(r'\d+', frame["Key"].replace(videoId + '/', ''))[0]), filter(lambda frame: frame["Key"].find(".jpg") != -1, res["Contents"])))
    frameNumbers.sort()


def lambda_handler(event, context):
    videoBucketId = event['videoBucketId']
    videoId = event['videoId']
    downloadImages(videoBucketId, videoId)

if __name__ == "__main__":
    lambda_handler({'videoBucketId': 'videobucketfreinathoeni', 'videoId': '1603365437.mp4'}, 0)
