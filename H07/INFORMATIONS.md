# Informations

## Remarks

Our workflow for xAFCL is completely outdated and needs to be heavily revised for next weeks assignment.

We decided to use Docker images to reduce our upload "cost" because the are pretty
easy to use and should be cross-platfrom compatible. Furthermore we thought that
it should be really easy in comparison to upload zip-Files which then have to be
used as layers somehow.

We switch for extract_frames and analyze_frames from node.js to python because the OpenCV implementation for node.js was really unusable for us. We tried several node.js packages and the official usage tutorial from the OpenCV documentation but we failed with every approach. After trying for half a day we decided to switch to python.

## Functions

### video_links

This function takes the S3 bucket id as input and returns links to all videos in the bucket.

### extract_frames

This function takes a url to a specific video from the S3 bucket where the videos are stored and a variable ````numberOfFramesToAnalyzePerInstance```` as input. The first input is used to which is used to split the extracted frames in subfolders for further parallelization down the line. We extract the frames by using the OpenCV VideoCapture-Function. After splitting the video in frames and creating different directories containing ````numberOfFramesToAnalyzePerInstance```` frames we return a collection with all the directory paths.

### analyze_frames

This function takes one directory path from extract_frames as input. It then uses scikit-image to load and OpenCV to convert and filter all frames to better detect differences. Then scikit-image is used again for the difference detection which is then used to delete all non-important frames from the directory. It returns the S3 bucket-id, the name of the video and the directory which it worked in.

### object_recognition

This function takes the S3 bucket-id, the name of the video and the directory which it will work in as input. The images are then directly passed to AWS Rekognition which gives us the labels for the images back. After filtering them for positiv matches it returns the positively matched frames.

### format_detections

This function takes the combined outputs of the object_recognition as input an formats it into nice strings which are then output.