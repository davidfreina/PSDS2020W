exports.handler = (event, context, callback) => {
    let flatArray = event.extractedFramesSplitFoldersCombined.flat()
    return callback(null, {"extractedFramesSplitFoldersCombined": flatArray, "extractedFramesSplitFoldersNumber": flatArray.length});
};