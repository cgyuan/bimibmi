package com.cyuan.bimibimi.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.db.dao.FavoriteMovieDao
import com.cyuan.bimibimi.db.dao.HistoryDao
import com.cyuan.bimibimi.model.FavoriteMovie
import com.cyuan.bimibimi.model.History


@Database(entities = [FavoriteMovie::class, History::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun favoriteMovieDao(): FavoriteMovieDao

    abstract fun historyDao(): HistoryDao

    companion object {
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE history(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "href TEXT  NOT NULL, " +
                        "url TEXT  NOT NULL, " +
                        "title TEXT NOT NULL," +
                        "episode_Index INTEGER  NOT NULL, " +
                        "position INTEGER NOT NULL, " +
                        "cover TEXT NOT NULL DEFAULT NULL, " +
                        "label TEXT DEFAULT '' NOT NULL, " +
                        "date INTEGER NOT NULL" +
                    ")")
            }

        }

        val instance by lazy {
            Room.databaseBuilder(App.getContext(), AppDatabase::class.java, "Bimibimi-db")
                .allowMainThreadQueries()
//                .fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_2_3)
                .build()
        }
    }

}