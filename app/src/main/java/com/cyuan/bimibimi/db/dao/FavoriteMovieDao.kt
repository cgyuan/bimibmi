package com.cyuan.bimibimi.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.cyuan.bimibimi.model.FavoriteMovie

@Dao
interface FavoriteMovieDao: BaseDao<FavoriteMovie> {

    @Query("SELECT * from favorite_movie WHERE href = :href")
    fun isFavorite(href: String): Boolean

    @Query("DELETE FROM favorite_movie WHERE href = :href")
    fun deleteMovie(href: String)

    @Query("SELECT * FROM favorite_movie ORDER BY add_date DESC")
    fun queryAllMovie(): DataSource.Factory<Int, FavoriteMovie>
}