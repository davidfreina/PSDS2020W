exports.handler = async (event, context) => {

    // Input: bucket ID of extracted frames and name of
    // Extract frames of video and save to s3 bucket

    return {"analyzedFrameNames": ["frame13", "frame27", "frame3124"], "numberOfVideos": event.numberOfVideos, "extractedFramesBucket": event.extractedFramesBucket};
};
