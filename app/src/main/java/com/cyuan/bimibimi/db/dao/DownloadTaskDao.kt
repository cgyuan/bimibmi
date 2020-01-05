package com.cyuan.bimibimi.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.cyuan.bimibimi.model.DownloadTaskInfo

@Dao
interface DownloadTaskDao: BaseDao<DownloadTaskInfo> {

    @Query("SELECT * FROM download_task WHERE task_url = :taskUrl")
    fun queryTaskByUrl(taskUrl: String): DownloadTaskInfo?

    @Query("SELECT * FROM download_task WHERE task_status != 2")
    fun queryDownloadingTasks(): LiveData<List<DownloadTaskInfo>>

    @Query("SELECT * FROM download_task WHERE task_status = 2")
    fun queryFinishTasks(): LiveData<List<DownloadTaskInfo>>

    @Query("DELETE FROM download_task WHERE id = :id")
    fun deleteTaskById(id: Long)

    @Query("DELETE FROM download_task WHERE task_url = :url")
    fun deleteTaskByUrl(url: String)

    @Query("SELECT * FROM download_task WHERE (task_url = :taskUrl OR file_path = :filePath) AND task_status IN (1, 2)")
    fun isTaskDownloading(taskUrl: String, filePath: String): Boolean

    @Query("SELECT * FROM download_task WHERE task_status = 2 AND episode_href = :url")
    fun queryFinishTaskByEpisodeUrl(url: String): DownloadTaskInfo?
}