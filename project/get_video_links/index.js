var AWS = require('aws-sdk');

exports.handler = (event, context, callback) => {
    var s3 = new AWS.S3();
    var videoBucketId = event['videoBucketId'];
    const bucketUrl = 'https://' + videoBucketId + '.s3.amazonaws.com/';

    let params = {Bucket: videoBucketId};

    var retVals = [];

    s3.listObjectsV2(params, function(err, data) {
        if(err){
            console.log(err);
        } else {
            input = data['Contents'];
            var video;
            for (var element in input) {
                if ((video = input[element]['Key']).includes('.mp4'))
                    retVals.push(bucketUrl + video);
            }
        }
        return callback(null, {"numberOfVideos": retVals.length, "videoLinks": retVals});
    });
};