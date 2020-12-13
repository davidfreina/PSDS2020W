import cv2
import boto3
import re

def handler_function(event, context):

    s3 = boto3.client('s3')

    file = event['file']
    numberOfFramesToAnalyzePerInstance = int(event['numberOfFramesToAnalyzePerInstance'])

    folder = re.search('[0-9]+.mp4', file).group(0).split(".mp4")[0]
    subfolder = ""
    link = file.split(folder)[0]

    ret_links = []

    vidcap = cv2.VideoCapture(file)
    half_fps = int(vidcap.get(cv2.CAP_PROP_FPS)/2)

    print("Extracting every " + str(half_fps) + " frames")

    success, image = vidcap.read()
    count, foldercount = 0, 0
    while success:

        # Split frames to analyze into subfolders
        if count % numberOfFramesToAnalyzePerInstance == 0:
            subfolder = "split" + str(foldercount)
            foldercount += 1
            ret_links.append(link + folder + "/" + subfolder)

        file_name = folder + "/" + subfolder + "/frame%d.jpg" % count

        # Extract a frame every 0.5 seconds using the FPS number
        if count % half_fps == 0:
            image_string = cv2.imencode('.jpg', image)[1].tobytes()
            s3.put_object(Bucket="videobucketthoenifreina", Key = file_name, Body=image_string)

        # Read next frame
        success,image = vidcap.read()
        if not success:
            print('End of file')
        count += 1

    vidcap.release()
    cv2.destroyAllWindows()

    print(ret_links)

    return ret_links

if __name__ == "__main__":
    handler_function({"file": "https://videobucketthoenifreina.s3.amazonaws.com/1603366941.mp4", "numberOfFramesToAnalyzePerInstance": 100}, None)