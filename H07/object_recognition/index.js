var AWS = require("aws-sdk");

AWS.config.update({region: 'us-east-1'});

AWS.config.getCredentials(function(err) {
  if (err) console.log(err.stack);
  // credentials not loaded
  else {
    console.log("Access key:", AWS.config.credentials.accessKeyId);
  }
});

test({video_bucket_id: 'videobucketthoenifreina', video_name: '1603376072', split_folder_name: 'split8'}, null);

console.log("Region: ", AWS.config.region);

async function test (event, context) {

    // {'video_bucket_id': video_bucket_id, 'video_name': video_name, 'split_folder_name': split_folder_name}
    var bucketId = event['video_bucket_id'];
    var videoName = event['video_name'];
    var splitFolderName = event['split_folder_name'];
    var s3 = new AWS.S3();
    var input = null;

    let params = {
        Bucket: bucketId,
        Delimiter: '/',
        Prefix: videoName + '/' + splitFolderName + '/'
    }

    s3.listObjectsV2(params, (err, data) => {
        if (err)
            console.log(err, err.stack);
        else{
            input = data['Contents'];
            for(element in input){
                console.log(input[element]['Key']);
                detectDogsAndChildren(bucketId, input[element]['Key'], ret);
                getTime(bucketId, input[element]['Key'], ret);
            }
        }
    })
    
    function ret(data){
        console.log("Callback: ", data);
    }
}

function detectDogsAndChildren(bucketId, imageSource, callback) {
    var rekognition = new AWS.Rekognition();
    var retVals = {'Image': imageSource, 'Dog': false, 'Child': false};
    var params = {
        Image: {
            S3Object: {
                Bucket: bucketId,
                Name: imageSource
            }
        },
        MinConfidence: 85
    }
    rekognition.detectLabels(params, function(err, data){
        if(err){
            console.log(err, err.stack);
        }
        else{
            for (label in data['Labels']){
                //console.log(data['Labels'][label]);
                currLabel = data['Labels'][label];
                if(currLabel['Name'] == 'Dog'){
                    retVals['Dog'] = true;
                }
                if(currLabel['Name'] == 'Human'){
                    retVals['Child'] = true;
                }
            }
            return callback(retVals);
        }
    });
}

function getTime(bucketId, imageSource, callback){
    var rekognition = new AWS.Rekognition();
    params = {
        Image: {
            S3Object: {
                Bucket: bucketId,
                Name: imageSource
            }
        }
    }

    rekognition.detectText(params, function(err, data){
        if(err){
            console.log(err, err.stack);
        }
        else{
            return callback(data);
        }
    });
}