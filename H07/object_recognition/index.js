var AWS = require("aws-sdk");

AWS.config.update({region: 'us-east-1'});

AWS.config.getCredentials(function(err) {
  if (err) console.log(err.stack);
  // credentials not loaded
  else {
    console.log("Access key:", AWS.config.credentials.accessKeyId);
  }
});

console.log("Region: ", AWS.config.region);



detectDogsAndChildren(null);

function detectDogsAndChildren(imageData) {
    var rekognition = new AWS.Rekognition();
    var ret_vals = {};
    var params = {
        Image: {
            S3Object: {
                Bucket: "videobucketthoenifreina",
                Name: "1603376072/split5/frame504.jpg"
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
                console.log(data['Labels'][label]);
                currLabel = data['Labels'][label];
                if(currLabel['Name'] == 'Dog'){
                    ret_vals['Dog'] = true;
                }
            }
        }
    });
}