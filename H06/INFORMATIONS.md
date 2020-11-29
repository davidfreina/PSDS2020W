# Informations

## Assumptions

- The file names are unix timestamps or any other kind of time information

## Remarks

- Almost everything we are going to do with S3 is based on assumptions because we both have not worked with it up until this assignment.

### Step 1: Extract frames

We are going to give some kind of bucket id (string: ````videoBucketId````) to access the bucket. We plan to save all extracted frames into one S3 bucket (string: ````extractedFramesBucket````) which is filled by the parallel executions of our ````extractFramesParallel````-Function. This function uses the number: ````videos```` given in the ````objectRecognitionInput.json```` to create ````videos```` many ````extractFrames````-Functions which then all extract the frames of one video and save them to the ````extractedFramesBucket````

### Step 2: image comparison

We are going to parallelize this step as well. We want to analyze ````numberOfFramesToAnalyzePerInstance```` frames on one ec2 instance so our loop counter goes from 0 to ````extractedFrames```` with step size ````numberOfFramesToAnalyzePerInstance````. We have to think about overlapping frames on the different instances which is why we maybe have to alter the counter a little bit when implementing this. After we have analyzed the given number of frames we are going to start one AWS Rekognition instance which receives the ````extractedFramesBucket```` and the positively analyzed images as input.

### Step 3: AWS Rekognition

By starting an AWS Rekognition instance after analyzing ````numberOfFramesToAnalyzePerInstance```` we should be able to lower our cost by not analyzing every image itself but it should also increase our speed by not analyzing all images at once in the end. The awsRekognition-function will then output a string if the identification was positive (dog or kid).