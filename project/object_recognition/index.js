var AWS = require('aws-sdk');

exports.handler = (event, context, callback) => {
    // {'analyzeFramesSplitFolder': 'https://videobucketthoenifreina.s3.amazonaws.com/1603366941/split1'}
    var rekognition = new AWS.Rekognition();
    var analyzeFramesSplitFolder = event['analyzeFramesSplitFolder'];
    let split = analyzeFramesSplitFolder.split("https://")[1].split("/");
    var bucketId = split[0].split(".")[0];
    var videoName = split[1];
    var splitFolderName = split[2];
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
                    let currLabel = values[value]['Labels'][label];
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
            return callback(null, {"detections": retVals});
        });
    });
};