package com.cyuan.bimibimi.db.repository

import com.cyuan.bimibimi.db.dao.HistoryDao
import com.cyuan.bimibimi.model.History
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryRepository private constructor(private val historyDao: HistoryDao) {


    suspend fun saveHistory(history: History) = withContext(Dispatchers.IO) {
        val record = historyDao.queryHistoryByHref(history.href)
        if (record != null) {
            history.id = record.id
            historyDao.update(history)
        } else {
            historyDao.insert(history)
        }
    }

    fun removeHistory(href: String) = historyDao.deleteHistory(href)

    suspend fun removeHistory(history: History) = withContext(Dispatchers.IO) {
        historyDao.deleteHistory(history.href)
    }


    fun getAllHistoryByPage(host: String) = historyDao.queryAllHistoryByPage(host)

    fun getAllHistory() = historyDao.queryAllHistory()

    fun clearAll() = historyDao.deleteAll()

    companion object {
        @Volatile
        private var  instance: HistoryRepository? = null
        fun getInstance(dao: HistoryDao): HistoryRepository {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = HistoryRepository(dao)
                    }
                }
            }
            return instance!!
        }
    }
}