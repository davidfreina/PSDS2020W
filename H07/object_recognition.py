import cv2
import boto3


def download_files(*files):
    
    s3 = boto3.resource('s3')
    for bucket in s3.buckets.all():
        print(bucket.name)


    # for file in files:
    #     s3 = boto3.client('s3')
    #     s3.download_file('BUCKET_NAME', 'OBJECT_NAME', 'FILE_NAME')
    
    return 0

if __name__ == "__main__":
    download_files(None)