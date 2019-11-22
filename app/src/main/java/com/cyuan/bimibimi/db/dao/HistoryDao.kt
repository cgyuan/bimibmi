package com.cyuan.bimibimi.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.cyuan.bimibimi.model.History

@Dao
interface HistoryDao: BaseDao<History> {

    @Query("SELECT * FROM history WHERE href = :href LIMIT 1")
    fun queryHistoryByHref(href: String) : History?

    @Query("DELETE FROM history WHERE href = :href")
    fun deleteHistory(href: String)

    @Query("SELECT * FROM history ORDER BY date DESC")
    fun queryAllHistoryByPage(): DataSource.Factory<Int, History>

    @Query("SELECT * FROM history ORDER BY date DESC")
    fun queryAllHistory(): List<History>

    @Query("DELETE FROM history")
    fun deleteAll()
}