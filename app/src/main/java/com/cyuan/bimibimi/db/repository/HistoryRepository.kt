package com.cyuan.bimibimi.db.repository

import com.cyuan.bimibimi.db.dao.HistoryDao
import com.cyuan.bimibimi.model.History
import com.cyuan.bimibimi.model.Movie

class HistoryRepository private constructor(private val historyDao: HistoryDao) {

    fun saveHistory(history: History) {
        val record = historyDao.queryHistoryByHref(history.href)
        if (record != null) {
            history.id = record.id
            historyDao.update(history)
        } else {
            historyDao.insert(history)
        }
    }

    fun removeHistory(href: String) = historyDao.deleteHistory(href)

    fun removeHistory(history: History) = historyDao.deleteHistory(history.href)


    fun getAllHistoryByPage() = historyDao.queryAllHistoryByPage()

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