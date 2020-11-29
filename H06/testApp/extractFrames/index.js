exports.handler = async (event, context) => {
    
    // Input: String of video bucket ID

    // Extract frames of video and save to s3 bucket

    let success = true;

    const response = {
        statusCode: 200,
        body: JSON.stringify({success: success})
    };
    return response;
};
