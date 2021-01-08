exports.handler = (event, context, callback) => {
    return callback(null, {"extractedFramesSplitFoldersCombined": event.extractedFramesSplitFoldersCombined.flat()});
};