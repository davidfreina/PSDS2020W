var AWS = require("aws-sdk");

exports.handler = async (event, context) => {
  // {'video_bucket_id': video_bucket_id, 'video_name': video_name, 'split_folder_name': split_folder_name}
  var rekognition = new AWS.Rekognition();
  var bucketId = event["video_bucket_id"];
  var videoName = event["video_name"];
  var splitFolderName = event["split_folder_name"];
  var s3 = new AWS.S3();
  var input = null;
  var retVals = {};

  let params = {
    Bucket: bucketId,
    Delimiter: "/",
    Prefix: videoName + "/" + splitFolderName + "/",
  };

  let promise = new Promise((resolve, reject) => {
    s3.listObjectsV2(params, (err, data) => {
      if (err) console.log(err, err.stack);
      else {
        input = data["Contents"];
        for (element in input) {
          console.log(input[element]["Key"]);

          retVals[imageSource] = detectDogsAndChildren(
            rekognition,
            bucketId,
            input[element]["Key"],
            ret
          );
          //getTime(rekognition, bucketId, input[element]['Key'], ret);
        }
      }
    });
  });

  await promise;
  const response = {
    statusCode: 200,
    body: JSON.stringify(retVals),
  };
  return response;
};

function detectDogsAndChildren(rekognition, bucketId, imageSource, callback) {
  var retVals = { Dog: false, Child: false };
  var params = {
    Image: {
      S3Object: {
        Bucket: bucketId,
        Name: imageSource,
      },
    },
    MinConfidence: 85,
  };
  rekognition.detectLabels(params, function (err, data) {
    if (err) {
      console.log(err, err.stack);
    } else {
      for (var label in data["Labels"]) {
        //console.log(data['Labels'][label]);
        currLabel = data["Labels"][label];
        if (currLabel["Name"] == "Dog") {
          retVals["Dog"] = true;
        }
        if (currLabel["Name"] == "Human") {
          retVals["Child"] = true;
        }
      }
      return retVals;
    }
  });
}

function getTime(rekognition, bucketId, imageSource, callback) {
  params = {
    Image: {
      S3Object: {
        Bucket: bucketId,
        Name: imageSource,
      },
    },
  };

  rekognition.detectText(params, function (err, data) {
    if (err) {
      console.log(err, err.stack);
    } else {
      return callback(data);
    }
  });
}
