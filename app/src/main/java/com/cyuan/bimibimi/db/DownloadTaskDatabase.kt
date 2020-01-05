package com.cyuan.bimibimi.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.db.dao.DownloadTaskDao
import com.cyuan.bimibimi.model.DownloadTaskInfo

@Database(entities = [DownloadTaskInfo::class], version = 3)
abstract class DownloadTaskDatabase: RoomDatabase() {

    abstract fun downloadTaskDao(): DownloadTaskDao

    companion object {

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE download_task ADD COLUMN episode_href TEXT NOT NULL DEFAULT ''")
            }

        }

        val instance by lazy {
            Room.databaseBuilder(App.getContext(), DownloadTaskDatabase::class.java, "download_task_db")
                .allowMainThreadQueries()
//                .fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_2_3)
                .build()
        }
    }
}