# Informations

## Task 2.1.2

### Compound functions

We will use the compound functions: ````parallelFor````. We might also use the ````if-then-else````-function but we are not yet sure if we need it.

### Dependencies

Our function dependencies are pretty much linear so every function depends on the results of its predecessors.

### Level of Parallelism

Except for our first function ````getVideoLinks```` everything in our workflow is parallelized based on the number of videos. So we should have a pretty high level of parallelism.

## Task 2.1.3

We have to pass through the ````numberOfVideos```` from the function ````getVideoLinks```` which is needed for every other function in the workflow. This value will always be used with ````REPLICATE(*)```` because we want to have it in all functions. The ````extractedFramesBucketCollection```` from the ````extractFramesParallel```` function is also needed by the latter two functions ````analyzeFramesParallel```` and ````awsRekognitionParallel````. We will use this value with ````BLOCK(1)```` because it is a collection an we want one bucket id per function which represent the extracted frames of one video. This is the same as for the ````analyzedFrameNamesCollection```` which contains a collection of collections of the names of the files after they were analyzed (significant delta) per day.

## Task 2.1.4

We actually build the FC using Visual Studio Code and just writing the YAML-File ourselves. We uploaded this handcrafted [YAML](objectRecognition.yaml) to the [FC Editor](http://fceditor.dps.uibk.ac.at:8180/#/editor) and converted the files according to [Task 2.1.5](##-Task-2.1.5)

## Task 2.1.5

see [YAML](objectRecognitionFCEditor.yaml) and [XML](objectRecognitionFCEditor.xml)

## Task 2.2

We did not write a single function ````allInOuts```` but rather wrote a function according to every function we will need in our final implementation. The functions are receiving their input data and returning an expected value which we can use to evalute our FC.

## Task 2.3.2

- Validating the function in the FC Editor produced no output so we think it is okay.
- Validating against the JSON schema validator was fine without any errors.

## Assumptions

- The video file names are unix timestamps or any other kind of time information so that we can use it in our final output to indicate when a dog/kid was detected.
- Almost everything we are going to do with S3 is based on assumptions because we both have not worked with it up until this assignment.

## Function explainations

### Step 1: Extract frames

We are going to give some kind of bucket id (string: ````videoBucketId````) to access the bucket. We plan to save all extracted frames into one S3 bucket (string: ````extractedFramesBucket````) which is filled by the parallel executions of our ````extractFramesParallel````-Function. This function uses the number: ````videos```` given in the ````objectRecognitionInput.json```` to create ````videos```` many ````extractFrames````-Functions which then all extract the frames of one video and save them to the ````extractedFramesBucket````

### Step 2: image comparison

We are going to parallelize this step as well. We want to analyze ````numberOfFramesToAnalyzePerInstance```` frames on one ec2 instance so our loop counter goes from 0 to ````extractedFrames```` with step size ````numberOfFramesToAnalyzePerInstance````. We have to think about overlapping frames on the different instances which is why we maybe have to alter the counter a little bit when implementing this. After we have analyzed the given number of frames we are going to start one AWS Rekognition instance which receives the ````extractedFramesBucket```` and the positively analyzed images as input.

### Step 3: AWS Rekognition

By starting an AWS Rekognition instance after analyzing ````numberOfFramesToAnalyzePerInstance```` we should be able to lower our cost by not analyzing every image itself but it should also increase our speed by not analyzing all images at once in the end. The awsRekognition-function will then output a string if the identification was positive (dog or kid).