//test([{'1603377206/split1/frame0.jpg': {'Dog': false, 'Child': true}, '1603377206/split1/frame9.jpg': {'Dog': true, 'Child': true}}], null);

exports.handler = async (event, context) => {
    var countDogs = 0;
    var countChildren = 0;
    var input = event['preprocessedDetections'];

    for(var video in input){
        var videoName = input[video]['video'];
        let unixTimestamp = parseInt(videoName.split(".")[0]);
        var milliseconds = unixTimestamp * 1000;
        var date = new Date(milliseconds);
        var am_pm = date.getHours() >= 12 ? "pm" : "am";
        var resultString = "";

        for(let detection in input[video]['detections']){
            let currDetection = input[video]['detections'][detection];
            if (currDetection['Dog']) countDogs++;
            if (currDetection['Child']) countChildren++;
        }

        if (countChildren != 0){
            resultString += "I recognized a child at around " + date.getHours() % 12 + ":" + date.getMinutes() + am_pm + (countChildren == 1 ? ". " : ", " + countChildren + " times in a row.\n");
        }
        if (countDogs != 0){
            resultString += "I recognized a dog at around " + date.getHours() % 12 + ":" + date.getMinutes() + am_pm + (countDogs == 1 ? "." : ", " + countDogs + " times in a row.\n");
        }
    }
    

    return resultString;
}