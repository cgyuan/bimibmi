package com.cyuan.bimibimi.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.db.dao.FavoriteMovieDao
import com.cyuan.bimibimi.model.FavoriteMovie


@Database(entities = [FavoriteMovie::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun favoriteMovieDao(): FavoriteMovieDao

    companion object {
        val instance by lazy {
            Room.databaseBuilder(App.getContext(), AppDatabase::class.java, "Bimibimi-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
    }

}