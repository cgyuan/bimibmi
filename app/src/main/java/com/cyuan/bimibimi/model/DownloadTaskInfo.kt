package com.cyuan.bimibimi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "download_task"
)
class DownloadTaskInfo(
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "task_id") var taskId: String,
    @ColumnInfo(name = "task_url") var taskUrl: String,
    @ColumnInfo(name = "total_size") var totalSize: String,
    @ColumnInfo(name = "receive_size") var receiveSize: String,
    @ColumnInfo(name = "local_path") var localPath: String,
    @ColumnInfo(name = "file_path") var filePath: String,
    @ColumnInfo(name = "task_status") var taskStatus: Int,
    @ColumnInfo(name = "cover_url") var coverUrl: String,
    var speed: String
) {
    constructor() : this("", "", "", "", "", "", "", 0, "", "")

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0

    /**
     * 数据源索引号
     */
    @ColumnInfo(name = "data_source_index") var dataSourceIndex: Int = 0
    /**
     * 剧集名
     */
    @ColumnInfo(name = "episode_name") var episodeName: String = ""
    /**
     * 剧集索引
     */
    @ColumnInfo(name = "episode_Index") var episodeIndex: Int = 0

    /**
     * 视频资源地址，用于加载剧集和【确认剧集是否同属一部影片】
     */
    @ColumnInfo(name = "href") var href: String = ""

    /**
     * 当集视频资源网页地址
     */
    @ColumnInfo(name = "episode_href") var episodeHref: String = ""

}