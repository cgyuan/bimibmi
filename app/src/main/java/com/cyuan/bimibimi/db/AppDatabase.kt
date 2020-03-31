package com.cyuan.bimibimi.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.db.dao.FavoriteMovieDao
import com.cyuan.bimibimi.db.dao.HistoryDao
import com.cyuan.bimibimi.model.FavoriteMovie
import com.cyuan.bimibimi.model.History


@Database(entities = [FavoriteMovie::class, History::class], version = 5, exportSchema = false)
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

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE history ADD COLUMN data_source_index INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE history ADD COLUMN episode_name TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE history ADD COLUMN duration INTEGER NOT NULL DEFAULT 0")
            }

        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE history ADD COLUMN host TEXT NOT NULL DEFAULT '${Constants.BIMIBIMI_INDEX}'")
                database.execSQL("ALTER TABLE favorite_movie ADD COLUMN host TEXT NOT NULL DEFAULT '${Constants.BIMIBIMI_INDEX}'")
            }

        }

        val instance by lazy {
            Room.databaseBuilder(App.getContext(), AppDatabase::class.java, "Bimibimi-db")
                .allowMainThreadQueries()
//                .fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_2_3)
                .addMigrations(MIGRATION_3_4)
                .addMigrations(MIGRATION_4_5)
                .build()
        }
    }

}