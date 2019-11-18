package com.cyuan.bimibimi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
class Section {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 1

    @ColumnInfo(name = "title")
    var title: String = ""

    @ColumnInfo(name = "more_link")
    var moreLink: String = ""

    var list = mutableListOf<Movie>()
}
