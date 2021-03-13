package com.cyuan.bimibimi.repository
import android.content.Context
import android.os.SystemClock
import com.cyuan.bimibimi.core.utils.GlobalUtil
import com.cyuan.bimibimi.core.utils.SharedUtil
import com.cyuan.bimibimi.model.Episode
import com.cyuan.bimibimi.model.HomeInfo
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.model.MovieDetail
import com.cyuan.bimibimi.parser.DataParserAdapter
import com.cyuan.bimibimi.parser.ParseResultCallback
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

open class OnlineMovieRepository private constructor() {

    private val HOME_INFO = "HOME_INFO"

    suspend fun fetchHomeInfo() = withContext(Dispatchers.IO) {
        val homeInfo = SharedUtil.read(HOME_INFO, HomeInfo::class.java)
        val hoursAgo = homeInfo?.let {  (SystemClock.uptimeMillis() - homeInfo.updateTimeStamp) / (1000 * 3600) } ?: 0
        if (homeInfo == null || hoursAgo >= 1 || GlobalUtil.host != homeInfo.host) {
            val result =
                suspendCoroutine<HomeInfo> { continuation ->
                    DataParserAdapter.parseHomePage(object :
                        ParseResultCallback<HomeInfo> {
                        override fun onSuccess(data: HomeInfo) {
                            SharedUtil.save(HOME_INFO, data)
                            continuation.resume(data)
                        }

                        override fun onFail(msg: String) {
                            continuation.resumeWithException(Throwable(msg))
                        }

                    })
                }
            result
        } else {
            homeInfo
        }
    }

    suspend fun fetchMovieDetail(url: String) = withContext(Dispatchers.IO) {
        val result = suspendCoroutine<MovieDetail> {
            DataParserAdapter.parseMovieDetail(url, object : ParseResultCallback<MovieDetail> {
                override fun onSuccess(data: MovieDetail) {
                    it.resume(data)
                }

                override fun onFail(msg: String) {
                    it.resumeWithException(Throwable(msg))
                }

            })
        }
        result
    }

    suspend fun fetchDailyUpdateMovie() = withContext(Dispatchers.IO) {
        val result = suspendCoroutine<List<List<Movie>>> {
            DataParserAdapter.parseDailyUpdate(object : ParseResultCallback<List<List<Movie>>> {
                override fun onSuccess(data: List<List<Movie>>) {
                    it.resume(data)
                }

                override fun onFail(msg: String) {
                    it.resumeWithException(Throwable(msg))
                }

            })
        }
        result
    }

    fun parseVideoSource(
        context: Context,
        episode: Episode,
        dataSourceName: String
    ) = callbackFlow<String> {
        DataParserAdapter.parseVideoSource(context, episode, object : ParseResultCallback<String> {
            override fun onSuccess(data: String) {
                offer(data)
                close()
            }

            override fun onFail(msg: String) {
                cancel(CancellationException(msg))
            }
        }, dataSourceName)
        awaitClose {}
    }

    companion object {
        val instance by lazy { OnlineMovieRepository() }
    }
}