package com.cyuan.bimibimi.model

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "history"
)
class History(

    /**
     * 视频资源地址，用于加载剧集和【确认剧集是否同属一部影片】
     */
    @ColumnInfo(name = "href") var href: String,
    /**
     * 播放地址
     */
    @ColumnInfo(name = "url") var url: String,
    /**
     * 标题：片名
     */
    @ColumnInfo(name = "title") var title: String,

    /**
     * 数据源索引号
     */
    @ColumnInfo(name = "data_source_index") var dataSourceIndex: Int,
    /**
     * 剧集名
     */
    @ColumnInfo(name = "episode_name") var episodeName: String,
    /**
     * 剧集索引
     */
    @ColumnInfo(name = "episode_Index") var episodeIndex: Int,
    /**
     * 播放进度
     */
    @ColumnInfo(name = "position") var position: Long,
    /**
     * 视频时长
     */
    @ColumnInfo(name = "duration") var duration: Long,
    /**
     * 封面
     */
    @ColumnInfo(name = "cover") var cover: String,
    /**
     * 标签，暂时留白
     */
    @Nullable
    @ColumnInfo(name = "label", defaultValue = "") var label: String,
    /**
     * 修改时间
     */
    @ColumnInfo(name = "date") val date: Calendar
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}