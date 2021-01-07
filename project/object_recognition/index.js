var AWS = require('aws-sdk');

exports.handler = (event, context, callback) => {
    // {'video_bucket_id': video_bucket_id, 'video_name': video_name,
    // 'split_folder_name': split_folder_name}
    var rekognition = new AWS.Rekognition();
    var bucketId = event['video_bucket_id'];
    var videoName = event['video_name'];
    var splitFolderName = event['split_folder_name'];
    var s3 = new AWS.S3();
    var input = null;
    var retVals = {};

    let params = {
        Bucket: bucketId,
        Delimiter: '/',
        Prefix: videoName + '/' + splitFolderName + '/',
    };

    var promises = [];

    s3.listObjectsV2(params, function(err, data) {
        if(err){
            console.log(err);
        } else {
            input = data['Contents'];
            for (var element in input) {
                var params = {
                    Image: {
                        S3Object: {
                            Bucket: bucketId,
                            Name: input[element]['Key'],
                        },
                    },
                    MinConfidence: 85,
                };
                promises.push(rekognition.detectLabels(params).promise());
            }
        }
        Promise.all(promises).then(function(values) {
            for (var value in values) {
                var retVal = {
                    Dog: false,
                    Child: false
                };
                for (var label in values[value]['Labels']) {
                    currLabel = values[value]['Labels'][label];
                    if (currLabel['Name'] == 'Dog') {
                        retVal['Dog'] = true;
                    }
                    if (currLabel['Name'] == 'Human') {
                        retVal['Child'] = true;
                    }
                }
                if (retVal['Child'] || retVal['Dog']){
                    retVals[input[value]['Key']] = retVal;
                }
            }
            return callback(null, retVals);
        });
    });
};