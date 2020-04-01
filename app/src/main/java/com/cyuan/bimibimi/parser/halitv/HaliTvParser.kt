package com.cyuan.bimibimi.parser.halitv

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.webkit.*
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.db.repository.RepositoryProvider
import com.cyuan.bimibimi.model.*
import com.cyuan.bimibimi.network.Callback
import com.cyuan.bimibimi.network.request.SearchRequest
import com.cyuan.bimibimi.network.request.StringRequest
import com.cyuan.bimibimi.parser.ParseResultCallback
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


object HaliTvParser {

    fun parseHomePage(callback: ParseResultCallback<HomeInfo>?) {
        StringRequest().url(Constants.HALITV_INDEX).listen(object : Callback {

            override fun onResponseString(response: String) {
                val document: Document = Jsoup.parse(response)
                val banners = parseBanner(document)
                val sections = parseSections(document)
                val homeInfo = HomeInfo()
                homeInfo.bannerList = banners
                homeInfo.sectionList = sections
                homeInfo.updateTimeStamp = SystemClock.uptimeMillis()
                homeInfo.host = Constants.HALITV_INDEX
                callback?.onSuccess(homeInfo)
            }

            override fun onFailure(e: Exception) {
                callback?.onFail(e.message ?: "解析首页数据失败")
            }

        })
    }

    fun parseMovieDetail(href: String, callback: ParseResultCallback<MovieDetail>?) {
        val url = Constants.HALITV_INDEX.substring(0, Constants.HALITV_INDEX.length - 1) + href
        StringRequest().url(url)
            .listen(object: Callback {
                override fun onFailure(e: Exception) {
                    callback?.onFail(e.message ?: "解析影视数据失败")
                }

                override fun onResponseString(response: String) {
                    val movieDetail = parseMovieDetail(response)
                    callback?.onSuccess(movieDetail)
                }
            })
    }

    fun parseDailyUpdate(callback: ParseResultCallback<List<List<Movie>>>?) {
        StringRequest().url(Constants.HALITV_INDEX).listen(object : Callback {
            override fun onFailure(e: Exception) {
                callback?.onFail(e.message!!)
            }

            override fun onResponseString(response: String) {
                val document = Jsoup.parse(response)
                val dailyEles = document.select("div.layui-tab-content").select("ul")
                val dailyList = mutableListOf<List<Movie>>()
                dailyEles.forEachIndexed { index, element ->
                    val movieEles = element.getElementsByTag("li")
                    val movieList = mutableListOf<Movie>()
                    for (movieELe in movieEles) {
                        val movie = Movie()
                        val linkEle = movieELe.getElementsByTag("a")[0]
                        val imgEle = movieELe.getElementsByTag("img")[0]
                        movie.cover = imgEle.attr("src")
                        if (!movie.cover.startsWith("http")) {
                            movie.cover = "http:${movie.cover}"
                        }
                        movie.title = imgEle.attr("alt")
                        movie.href = linkEle.attr("href")
                        movie.label = movieELe.select("p>a")[0].text();
                        movieList.add(movie)
                    }
                    dailyList.add(movieList)
                }
                callback?.onSuccess(dailyList)
            }
        })
    }

    fun parseSearch(searchKeyWord: String, callback: ParseResultCallback<List<Movie>>?) {
        SearchRequest().addParam("wd", searchKeyWord)
            .listen(object : Callback {
                override fun onFailure(e: Exception) {
                    callback?.onFail(e.message ?: "")
                }

                override fun onResponseString(response: String) {
                    val document = Jsoup.parse(response)
                    val movieList = parseSearchMovieList(document)
                    callback?.onSuccess(movieList)
                }
            })
    }

    fun parseCategoryMovie(path: String, callback: ParseResultCallback<List<Movie>>?) {
        val arr = path.split("-")
        val url = String.format(Constants.HALITV_CATEGORY_TEMPLATE, arr[0], arr[1])
        StringRequest().url(url)
            .listen(object : Callback {
                override fun onFailure(e: Exception) {
                    callback?.onFail(e.message ?: "解析分类数据失败")
                }

                override fun onResponseString(response: String) {
                    val document = Jsoup.parse(response)
                    val movieList = mutableListOf<Movie>()
                    val elements =
                        document.select("ul[class=ty-top-video layui-col-md12 layui-col-sm12 layui-col-xs12]")
                    for (element in elements) {
                        val sectionEles = element.getElementsByTag("li")
                        for (ele in sectionEles) {
                            val movie = Movie()
                            movie.href = ele.getElementsByTag("a")[0].attr("href")
                            movie.cover = ele.getElementsByTag("img")[0].attr("data-original")
                            movie.label = ele.getElementsByTag("span")[2].text()
                            movie.title = ele.getElementsByTag("h5")[0].text()
                            if (!movie.cover.startsWith("http")) {
                                movie.cover = "http:${movie.cover}"
                                movieList.add(movie)
                            }

                        }
                    }
                    callback?.onSuccess(movieList)
                }
            })
    }

    private fun parseSearchMovieList(document: Document): List<Movie> {
        val movieList = mutableListOf<Movie>()
        val elements = document.select("div.seach-video")
        elements.forEach {
            val movie= Movie()
            val linkEle = it.getElementsByTag("a")[0]
            movie.href = linkEle.attr("href")
            movie.cover = linkEle.attr("data-original")
            movie.title = it.selectFirst("div.video-info .tit a").text()
            if (!movie.cover.startsWith("http")) {
                movie.cover = "http:${movie.cover}"
            }
            movie.label = it.getElementsByTag("li")[5].text()
            movieList.add(movie)
        }
        return movieList
    }

    private fun parseMovieDetail(response: String): MovieDetail {
        val movieDetail = MovieDetail()
        val document = Jsoup.parse(response)
        try {
            movieDetail.intro = document.select("div[class=bt-content]")[0].textNodes()[0].text()
        } catch (e: Exception){
            movieDetail.intro = document.select("div.bt-content p")[0].text()
        }
        val headers = document.select("div[class=txt-list]")[0].text()
        movieDetail.title = document.select("h1")[0].text()
        movieDetail.cover = "http:" + document.select("img")[0].attr("src")

        val dataSourceEles = document.select("ul[class=layui-tab-title]")[0].getElementsByTag("li")
        val playListEles = document.select("ul.layui-tab-item.bt-playlist")
        val dataSources = mutableListOf<DataSource>()
        playListEles.forEachIndexed { index, element ->
            val dataSource = DataSource()
            dataSource.name = dataSourceEles[index].text()
            val episodes = arrayListOf<Episode>()
            val playList: Elements = element.getElementsByTag("a")
            playList.forEach {
                val episode = Episode()
                episode.title = it.text()
                episode.href = Constants.HALITV_INDEX.substring(0, Constants.HALITV_INDEX.length - 1) + it.attr("href")
                episodes.add(episode)
            }
            dataSource.episodes = episodes
            dataSources.add(dataSource)
        }
        val stringBuilder = StringBuilder()
        val headerEles = document.select("div[class=txt-list]")[0].select("p")
        with(stringBuilder) {
            headerEles.forEach {
                append(it.text() + "\n")
            }
        }
        movieDetail.headers = stringBuilder.toString()
        movieDetail.recommendList = parseMovieList(document)
        movieDetail.dataSources = dataSources
        return movieDetail
    }

    private fun parseMovieList(document: Document): MutableList<Movie> {
        val movieList = mutableListOf<Movie>()
        val recommendEles = document.select("ul.season-video").select("li")
        for (element in recommendEles) {
            val movie = Movie()
            val imgEle: Element = element.getElementsByTag("img")[0]
            var recSrc: String = imgEle.attr("src")
            movie.title = imgEle.attr("alt")
            movie.href = element.getElementsByTag("a")[0].attr("href")
            if (!recSrc.startsWith("http")) {
                recSrc = "http:$recSrc"
            }
            movie.cover = recSrc
            movie.label = recommendEles.select("span.play-continu")[0].text()
            movieList.add(movie)
        }
        return movieList
    }

    private fun parseSections(document: Document): MutableList<Section> {
        val elements =
            document.select("ul[class=ty-top-video layui-col-md12 layui-col-sm12 layui-col-xs12]")
        val titleList = listOf("热播推荐", "TV动画", "剧场版", "电影", "剧集")
//        val moreList = listOf("", "/tvban/", "/juchangban/", "/dianying/", "/dianshiju/")
        val moreList = listOf("", "62", "63", "1", "2")
        val sectionList = mutableListOf<Section>()
        elements.forEachIndexed { index, element ->
            val section = Section()
            section.title = titleList[index]
            section.moreLink = moreList[index]
            val sectionEles = element.getElementsByTag("li")
            val movieList = mutableListOf<Movie>()
            for (ele in sectionEles) {
                val movie = Movie()
                movie.href = ele.getElementsByTag("a")[0].attr("href")
                movie.title = ele.getElementsByTag("h5")[0].text()
                movie.label = ele.getElementsByTag("span")[2].text()
                var src = ele.getElementsByTag("img")[0].attr("data-original")
                if (!src.startsWith("http")) {
                    src = "http:$src"
                }
                movie.cover = src
                if (!movie.title.contains("风俗娘")) {
                    movieList.add(movie)
                }
            }
            section.list = movieList
            sectionList.add(section)
        }
        return sectionList
    }

    private fun parseBanner(document: Document): MutableList<Movie> {
        val elements: Elements = document.select("div[carousel-item]")[0].select("div>div")
        val list = mutableListOf<Movie>()
        for (element in elements) {
            val link = element.getElementsByTag("a")[0].attr("href")
            var src = element.getElementsByTag("img")[0].attr("src")
            val title = element.getElementsByTag("span")[0].text()
            src = "http:$src"
            val movie = Movie()
            movie.href = link
            movie.cover = src
            movie.title = title
            list.add(movie)
        }
        return list
    }

    private val GET_IFRAME_SRC = "var iframe = document.getElementsByTagName('iframe')[0];\n" +
            "var t = setInterval(function () {\n" +
            "if(iframe === undefined) {\n" +
            "iframe = document.getElementsByTagName('iframe')[0];\n" +
            "} else {\n" +
            "clearInterval(t);\n" +
            "console.log('IFRAME_SRC = ' + iframe.src);\n" +
            //  "video.addEventListener('canplay', function() {\n" +
            "javascript:app.getIframeSrc(iframe.src);\n" +
            // "};\n" +
            "}\n" +
            "}, 200)\n"

    class JsBridge(private val webView: WebView,
                   private val callback: ParseResultCallback<String>?) {
        @JavascriptInterface
        fun getIframeSrc(url: String) {
            App.getHandler().post {

//                callback?.onSuccess(url)
//                webView.removeAllViews()
            }
        }
    }

    fun parseVideoSource(
        context: Context,
        episode: Episode,
        callback: ParseResultCallback<String>?,
        dataSource: String = ""
    ) {
        val repository = RepositoryProvider.providerDownloadTaskRepository()
        val taskInfo = repository.getFinishedTask(episode.href)
        if (taskInfo != null) {
            callback?.onSuccess(taskInfo.filePath)
            return
        }
        context as Activity
        val webView = context.findViewById<WebView>(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
//        webView.addJavascriptInterface(JsBridge(webView, callback), "app")

        webView.loadUrl(episode.href)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, webUrl: String?) {
//                webView.evaluateJavascript(GET_IFRAME_SRC, null)
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                url: String?
            ): WebResourceResponse? {
                url?.let {
                    if (it.contains(".mp4")) {
                        App.getHandler().post {
                            webView.stopLoading()
                            callback?.onSuccess(url)
                            webView.removeAllViews()
                        }
                    }
                }
                return super.shouldInterceptRequest(view, url)
            }
        }
    }
}