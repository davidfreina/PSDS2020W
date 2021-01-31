# Deployment and Configuration

## Configuration

### credentials.properties

This file is needed by the xAFCL tool to authenticate against AWS. It should be provided in the same directory as the xAFCL.jar. Filling in the required values is mandatory.

### objectRecognitionInput.json

The objectRecognitionInput.json file has two parameters which are configurable. First is the ````videoBucketId```` which should be the name of your S3 bucket which contains the given videos. The second parameter is the ````numberOfFramesToAnalyzePerInstance```` which configures how many frames one instance receives for analysis or recognition.

## Deployment

### Node.js functions

The Node.js functions should simply be uploaded as separated AWS Lambda functions. All of them should have 128MB memory and 30 seconds execution time.

### Python functions

The Python functions are deployed as a docker image due to their dependencies to other packages like OpenCV. We decided to use AWS ECR as our container registry because we could then specify those images directly when creating the AWS Lambda functions. To build the images using the provided Dockerfile's and push them to the AWS ECR we adhered to the push commands which can be viewed directly from the AWS ECR console.

Because those two functions do much more computation than the rest of the functions we also had to increase their RAM and time. ````analyzeFrames```` needs 4GB RAM for 100+ frames per instance and ````extractFrames```` is satisfied with 2GB RAM. For both of the functions we increased to time limit to 5 minutes.

#### Pushing to AWS ECR

````shell
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin $ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com

docker build -t $IMAGE_NAME .

docker tag $IMAGE_NAME:latest $ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/$IMAGE_NAME:latest

docker push $ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/$IMAGE_NAME:latest
````