exports.handler = (event, context, callback) => {
    var detections = event.detections;

    var resultJson = {};

    for(var video in detections){
        var videoName = Object.keys(detections[video])[0];
        if (resultJson[videoName] == undefined)
            resultJson[videoName] = {"video": videoName, "detections": []};
        var splits = detections[video][videoName];
        for(var split in splits){
            var currSplit = splits[split];
            for (var frame in currSplit) {
                var currFrame = currSplit[frame];
                for (var detection in currFrame) {
                    var currDetection = currFrame[detection];
                    resultJson[videoName].detections.push(currDetection);
                }

            }
        }
    }

    return callback(null, resultJson);
};