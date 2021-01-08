//test([{'1603377206/split1/frame0.jpg': {'Dog': false, 'Child': true}, '1603377206/split1/frame9.jpg': {'Dog': true, 'Child': true}}], null);

exports.handler = async (event, context) => {
    // [{'1603377206/split1/frame0.jpg': {'Dog': false, 'Child': true}}, {}]
    var countDogs = 0;
    var countChildren = 0;
    var unixTimestamp = 0;

    for(var video in event){
        for(var split in event[video]){
            var currSplit = event[video][split];
            for(var frame in currSplit){
                var currFrame = currSplit[frame];
                if(unixTimestamp == 0){
                    unixTimestamp = frame.split("/", 1)[0];
                }
                if(currFrame['Dog'] == true){
                    countDogs++;
                }
                if(currFrame['Child'] == true){
                    countChildren++;
                }
            }
        }
    
        var milliseconds = unixTimestamp * 1000;
        var date = new Date(milliseconds);
        var am_pm = date.getHours() >= 12 ? "pm" : "am";
        var resultString = "";
        if (countChildren != 0){
            resultString += "I recognized a child at around " + date.getHours() % 12 + ":" + date.getMinutes() + am_pm + (countChildren == 1 ? ". " : ", " + countChildren + " times in a row. ");
        }
        if (countDogs != 0){
            resultString += "I recognized a dog at around " + date.getHours() % 12 + ":" + date.getMinutes() + am_pm + (countDogs == 1 ? "." : ", " + countDogs + " times in a row.");
        }
    }
    

    return resultString;
}