package com.cyuan.bimibimi.repository
import android.os.SystemClock
import com.cyuan.bimibimi.core.utils.SharedUtil
import com.cyuan.bimibimi.model.HomeInfo
import com.cyuan.bimibimi.model.MovieDetail
import com.cyuan.bimibimi.parser.HtmlDataParser
import com.cyuan.bimibimi.parser.ParseResultCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

open class OnlineMovieRepository private constructor() {

    private val HOME_INFO = "HOME_INFO"

    suspend fun fetchHomeInfo() = withContext(Dispatchers.IO) {
        val homeInfo = SharedUtil.read(HOME_INFO, HomeInfo::class.java)
        val hoursAgo = homeInfo?.let {  (SystemClock.uptimeMillis() - homeInfo.updateTimeStamp) / (1000 * 3600) } ?: 0
        if (homeInfo == null || hoursAgo >= 1) {
            val result =
                suspendCoroutine<HomeInfo> { continuation ->
                    HtmlDataParser.parseHomePage(object :
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
            HtmlDataParser.parseMovieDetail(url, object : ParseResultCallback<MovieDetail> {
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

    companion object {
        val instance by lazy { OnlineMovieRepository() }
    }
}