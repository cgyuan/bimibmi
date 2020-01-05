package com.cyuan.bimibimi.model

class DownloadTaskInfo(
    var title: String,
    var urlMd5: String,
    var taskId: String,
    var taskUrl: String,
    var totalSize: String,
    var receiveSize: String,
    var localPath: String,
    var filePath: String,
    var taskStatus: Int,
    var coverUrl: String,
    var speed: String
) {
    constructor(): this("", "", "", "", "", "", "","", 0, "", "")
}