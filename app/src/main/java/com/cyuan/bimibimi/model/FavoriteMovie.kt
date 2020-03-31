package com.cyuan.bimibimi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "favorite_movie"
)
class FavoriteMovie(
    @ColumnInfo(name = "href") var href: String,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "cover") var cover: String,
    @ColumnInfo(name = "label") var label: String,
    /**
     * 资源站点
     */
    @ColumnInfo(name = "host") var host: String,
    @ColumnInfo(name = "add_date" ) val date: Calendar
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}