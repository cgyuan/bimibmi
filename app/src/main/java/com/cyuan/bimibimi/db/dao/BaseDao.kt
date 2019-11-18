package com.cyuan.bimibimi.db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDao<T> {

    @Insert
    fun insert(t: T)

    @Insert
    fun insert(ts: List<T>)

    @Delete
    fun delete(t: T)

    @Delete
    fun delete(ts: List<T>)

    @Update
    fun update(t: T)

    @Update
    fun update(ts: List<T>)
}