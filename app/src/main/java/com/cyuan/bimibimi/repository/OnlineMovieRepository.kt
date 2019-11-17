package com.cyuan.bimibimi.repository
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.model.MovieDetail
import com.cyuan.bimibimi.model.Section
import com.cyuan.bimibimi.parser.HtmlDataParser
import com.cyuan.bimibimi.parser.ParseResultCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

open class OnlineMovieRepository private constructor() {

    suspend fun fetchHomeInfo() = withContext(Dispatchers.IO) {
        val result =
            suspendCoroutine<Pair<MutableList<Movie>, List<Section>>> { continuation ->
                HtmlDataParser.parseHomePage(object :
                    ParseResultCallback<Pair<MutableList<Movie>, List<Section>>> {
                    override fun onSuccess(data: Pair<MutableList<Movie>, List<Section>>) {
                        continuation.resume(data)
                    }

                    override fun onFail(msg: String) {
                        continuation.resumeWithException(Throwable(msg))
                    }

                })
            }
        result
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