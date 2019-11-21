package com.cyuan.bimibimi.parser

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.text.TextUtils
import android.webkit.*
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.extension.logWarn
import com.cyuan.bimibimi.core.utils.UrlHelper
import com.cyuan.bimibimi.model.*
import com.cyuan.bimibimi.network.Callback
import com.cyuan.bimibimi.network.request.SearchRequest
import com.cyuan.bimibimi.network.request.StringRequest
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URLDecoder
import java.util.regex.Pattern


object HtmlDataParser {
    fun parseHomePage(callback: ParseResultCallback<HomeInfo>?) {
        StringRequest().listen(object : Callback{
            override fun onFailure(e: Exception) {
                callback?.onFail(e.message ?: "解析首页数据失败")
            }

            override fun onResponseString(response: String) {
                var start = response.indexOf("<section class=\"area clearfix area-slider\">")
                var end = response.indexOf("</section>", 0)
                val banner = response.substring(start, end)
                val pattern = Pattern.compile("<li.*?</li>")
                val matcher = pattern.matcher(banner)
                val list = mutableListOf<Movie>()
                while (matcher.find()) {
                    val video = Movie()
                    val itemString = matcher.group()

                    start = itemString.indexOf("href=\"")
                    end = itemString.indexOf("\"", start + "href=\"".length)
                    val href = itemString.substring(start + "href=\"".length, end)
                    video.href = href
                    start = itemString.indexOf("title=\"")
                    end = itemString.indexOf("\"", start + "title=\"".length)
                    val title = itemString.substring(start + "title=\"".length, end)
                    video.title = title

                    start = itemString.indexOf("src=\"")
                    end = itemString.indexOf("\"", start + "src=\"".length)
                    var cover = itemString.substring(start + "src=\"".length, end)
                    if (!cover.startsWith("http")) {
                        cover = Constants.BIMIBIMI_INDEX + cover
                    }
                    video.cover = cover


                    start = itemString.indexOf("<b class=\"text-overflow\">")
                    end = itemString.indexOf("</b>", start + "<b class=\"text-overflow\">".length)
                    var label: String
                    if (start > 0 && end > 0) {
                        label = itemString.substring(start + "<b class=\"text-overflow\">".length, end)
                        video.label = label.trim()
                    } else {
                        start = itemString.indexOf("<p>")
                        end = itemString.indexOf("</p>", start + "<p>".length)
                        label = itemString.substring(start + "<p>".length, end)
                        video.label = label
                    }
                    list.add(video)
                }


                val sections = parseSection(response)
                val homeInfo = HomeInfo()
                homeInfo.bannerList = list
                homeInfo.sectionList = sections
                homeInfo.updateTimeStamp = SystemClock.uptimeMillis()
                callback?.onSuccess(homeInfo)
            }
        })
    }

    private fun parseSection(response: String) : List<Section> {
        val titleList = listOf("今日热播", "新番放送", "国产动漫", "番组计划", "剧场动画", "影视")
        val moreList = listOf("", "/type/riman", "/type/guoman", "/type/fanzu", "/type/juchang", "/type/move")
        val document = Jsoup.parse(response)!!
        // <ul class="drama-module clearfix tab-cont">
        val sectionEles = document.select("ul[class=drama-module clearfix tab-cont]")
        val sectionList = mutableListOf<Section>()
        sectionEles.forEachIndexed { index, element ->
            val section = Section()
            section.title = titleList[index]
            section.moreLink = moreList[index]
            val movieList = mutableListOf<Movie>()
            val movieELes = element.getElementsByTag("li")
            for (movieELe in movieELes) {
                val movie = Movie()
                val linkEle = movieELe.getElementsByTag("a")[0]
                val imgEle = movieELe.getElementsByTag("img")[0]
                movie.cover = imgEle.attr("data-original")
                if (!movie.cover.startsWith("http")) {
                    movie.cover = Constants.BIMIBIMI_INDEX + movie.cover
                }
                movie.title = linkEle.attr("title")
                movie.href = linkEle.attr("href")
                movie.label = movieELe.select("span[class=fl]")[0].text()
                movieList.add(movie)
            }
            section.list = movieList
            sectionList.add(section)
        }
        return sectionList
    }

//    <li class="item">
//    <a href="/bangumi/bi/1998/" title="刀剑神域 Alicization War of Underworld" target="_blank" class="img">
//    <img class="lazy" data-original="https://wxt.sinaimg.cn/large/006MDjU7ly1g7q2kg95e5j307i0am3z5.jpg" src="/template/bimibimi_pc/images/grey.png" alt="刀剑神域 Alicization War of Underworld" width="170" height="224"/>
//    <span class="mask"><i class="iconfont icon-play"></i></span></a><div class="info">
//    <a href="/bangumi/bi/1998/">刀剑神域 Alicization War of Underworld</a><p><span class="fl">更新至05话</span></p></div></li>

    fun parseMovieDetail(url: String, callback: ParseResultCallback<MovieDetail>?) {
        StringRequest().url(url)
            .listen(object: Callback {
                override fun onFailure(e: Exception) {
                    callback?.onFail(e.message ?: "解析影视数据失败")
                }

                override fun onResponseString(response: String) {
                    val movieDetail = MovieDetail()
                    val document = Jsoup.parse(response)!!
                    val introEle = document.select("div[class=vod-jianjie]")[0]
//                    val introEle = introEle.child(0)!!
                    movieDetail.intro = introEle.text()

                    movieDetail.cover = document.select("div[class=v_pic]")[0].child(0)!!.attr("data-original")
                    if (!movieDetail.cover.startsWith("http")) {
                        movieDetail.cover = Constants.BIMIBIMI_INDEX + movieDetail.cover
                    }


                    val dataSourceEles = document.select("div[class=js_bt]")
                    //        val baiduPanElement = dataSourceEles.removeAt(dataSourceEles.size - 1)
                    val playListEles = document.select("ul[class=player_list]")
                    val dataSources = mutableListOf<DataSource>()
                    playListEles.forEachIndexed { index, element ->
                        val dataSource = DataSource()
                        val dataSourceLabel = dataSourceEles[index].getElementsByTag("span")[0].text()
                        dataSource.name = dataSourceLabel
                        if (dataSourceLabel.contains("百度")) {
                            return@forEachIndexed
                        }

                        val playList = element.getElementsByTag("a")
                        val episodes = arrayListOf<Episode>()
                        playList.forEach {
                            val episode = Episode()
                            episode.title = it.text()
                            episode.href = Constants.BIMIBIMI_INDEX + it.attr("href")
                            episodes.add(episode)
                        }
                        dataSource.episodes = episodes
                        dataSources.add(dataSource)
                    }

                    val headEles = document.select("ul[class=txt_list clearfix]")[0].getElementsByTag("li")
                    headEles.removeAt(0)
                    headEles.removeAt(headEles.size - 1)
                    headEles.removeAt(headEles.size - 1)
                    headEles.removeAt(headEles.size - 1)
                    val stringBuilder = StringBuilder()
                    with(stringBuilder) {
                        for (element in headEles) {
                            val key = element.child(0).text()
                            val actors = element.getElementsByTag("a")
                            append(element.text()).append("\n")
//                            append(key)
//                            println(element.text())
//                            for (i in 0 until actors.size) {
//                                if (i > 3) {
//                                    append("...")
//                                    break
//                                }
//                                append(actors[i].text()).append(" ")
//                            }
//                            append("\n")
                        }
                    }

                    val movieList = parseMovieList(document)

                    movieDetail.headers = stringBuilder.toString()

                    movieDetail.dataSources = dataSources

                    movieDetail.recommendList = movieList

                    callback?.onSuccess(movieDetail)
                }
            })
    }

    fun parseVideoSource(context: Context, episode: Episode, callback: ParseResultCallback<String>?) {
        StringRequest().url(episode.href)
            .listen(object: Callback {
                override fun onFailure(e: Exception) {
                    callback?.onFail(e.message ?: "解析视频失败")
                }

                override fun onResponseString(response: String) {
                    // 从页面解析视频url
                    var url = parseVideoUrlFromPage(response)
                    if (url.isEmpty()) {
                        callback?.onFail("应版权方要求,该番剧已下架！")
                        return
                    }
                    if (!url.startsWith("http")) {
                        // 获得视频后缀部分，并据此从静态页面解析视频url
                        parseVideoUrlById(url, callback)
                    } else if (url.endsWith(".html")) {
                        // parseVideoUrlWithWebView(context, url, callback)
                        // "https://api.nmbaojie.com/api/video/youkuplay.php?url=https://api.nmbaojie.com/api/data/iqiyim3u8/19rrok4pg4.m3u8"
                        // https://www.iqiyi.com/v_19rrok4pg4.html
                        if (url.contains("iqiyi")) {
                            val vid = UrlHelper.extractIqyVideoId(url)
                            url = "https://api.nmbaojie.com/api/video/youkuplay.php?url=https://api.nmbaojie.com/api/data/iqiyim3u8/${vid}.m3u8"
                            callback?.onSuccess(url)
                        } else if (url.contains("v.qq.com")) {
                            // https://v.qq.com/x/cover/enj7gj9pcksq89p/g0761hr9ih3.html
                            // http://dalaowangsan.cn/wangerjiexi/api.php?url=https://m.v.qq.com/cover/0/0gsf9fytppje54d.html?vid=k0027nolupz&cb=jQuery18205677586477461618_1574253655192&_=1574253655788

                            parseVideoUrl(context, url, callback)
//                            url = UrlHelper.rebuildQQVideoUrl(url)
//                            StringRequest().url(url)
//                                .listen(object : Callback {
//                                    override fun onFailure(e: Exception) {
//                                        callback?.onFail(e.message ?: "")
//                                    }
//
//                                    override fun onResponseString(response: String) {
//                                        val result = response.replace("(", "").replace(");", "")
//                                        try {
//                                            url = JSONObject(result).getString("url")
//                                            callback?.onSuccess(url)
//                                        } catch (e: Exception) {
//
//                                        }
//                                    }
//                                })
                        }
                    } else {
                        callback?.onSuccess(url)
                    }
                }
            })
    }

    fun parseVideoUrl(context: Context, url: String, callback: ParseResultCallback<String>?) {
        context as Activity
        val webView = context.findViewById<WebView>(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webView.loadUrl("https://okjx.lrkdzx.com/okbyjiexi/?url=$url")
//        webView.loadUrl("http://okjx.cc/?url=https://v.qq.com/x/cover/mu01sbah4rauf44.html")
        webView.webViewClient = object : WebViewClient() {
            var tryTime = 0
            override fun onPageFinished(view: WebView?, webUrl: String?) {
                tryTime = 1
                webView.postDelayed({
                    extractVideoUrl()
                }, 100)
            }

            private fun extractVideoUrl() {
                tryTime++
                if (tryTime > 10) return
                webView.evaluateJavascript("document.getElementsByTagName('video')[0].currentSrc") { value ->
                    if (TextUtils.isEmpty(value) || value == "null" || value == "\"\"") {
                        webView.postDelayed({
                            extractVideoUrl()
                        }, 100)
                        return@evaluateJavascript
                    }
                    val newUrl = value.replace("\"", "")
                    callback?.onSuccess(newUrl)
                    webView.removeAllViews()
                }
                webView.removeAllViews()
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                logWarn("console", "["+consoleMessage.messageLevel()+"] "+ consoleMessage.message() + "(" +consoleMessage.sourceId()  + ":" + consoleMessage.lineNumber()+")")
                return super.onConsoleMessage(consoleMessage)
            }
        }
    }

    private fun parseVideoUrlById(url: String, callback: ParseResultCallback<String>?) {
        var targetUrl = ""
        var isDanmaFSource = false
        when {
            url.contains("/") -> targetUrl = "http://119.23.209.33/static/danmu/bit.php?$url"
            url.contains(".") -> targetUrl = "http://119.23.209.33/static/danmu/niux.php?id=$url"
            else -> {
                targetUrl = "http://119.23.209.33/static/danmu/dogecloud.php?vcode=$url"
                isDanmaFSource = true
            }
        }
        StringRequest().url(targetUrl)
            .listen(object : Callback {
                override fun onFailure(e: Exception) {
                    callback?.onFail(e.message ?: "解析视频失败")
                }
                override fun onResponseString(response: String) {
                    if (isDanmaFSource) {
                        val start = response.indexOf("hls.loadSource('")
                        val end = response.indexOf("')", start + "hls.loadSource('".length)
                        val newUrl = response.substring(start + "hls.loadSource('".length, end)
                        callback?.onSuccess(newUrl)
                    } else {
                        val document = Jsoup.parse(response)!!
                        val newUrl = document.getElementsByTag("source")[0].attr("src")
                        callback?.onSuccess(newUrl)
                    }
                }
            })
    }

    private fun parseVideoUrlFromPage(response: String): String {
        val start = response.indexOf("player_data=")
        val end = response.indexOf("}", start + "player_data=".length) + 1
        val jsonStr = response.substring(start + "player_data=".length, end)
        var url: String
        try {
            val jsonObj = JSONObject(jsonStr)
            url = jsonObj.getString("url")
        } catch (e: Exception) {
            return ""
        }

        url = URLDecoder.decode(url, "utf-8")
        return url
    }


    fun parseCategoryMovie(path: String, callback: ParseResultCallback<List<Movie>>?) {
        val url = "http://www.bimibimi.tv$path/"
        StringRequest().url(url)
            .listen(object : Callback {
                override fun onFailure(e: Exception) {
                    callback?.onFail(e.message ?: "解析分类数据失败")
                }

                override fun onResponseString(response: String) {
                    val document = Jsoup.parse(response)
                    val movieList = parseMovieList(document)
                    callback?.onSuccess(movieList)
                }
            })
    }

    private fun parseMovieList(document: Document): MutableList<Movie> {
        val movieEles =
            document.select("ul[class=drama-module clearfix tab-cont]")[0].getElementsByTag("li")
        val movieList = mutableListOf<Movie>()
        for (movieELe in movieEles) {
            val movie = Movie()
            val linkEle = movieELe.getElementsByTag("a")[0]
            val imgEle = movieELe.getElementsByTag("img")[0]
            movie.cover = imgEle.attr("data-original")
            if (!movie.cover.startsWith("http")) {
                movie.cover = Constants.BIMIBIMI_INDEX + movie.cover
            }
            movie.title = linkEle.attr("title")
            movie.href = linkEle.attr("href")
            movie.label = movieELe.select("span[class=fl]")[0].text()
            movieList.add(movie)
        }
        return movieList
    }

    fun parseSearch(searchKeyWord: String, callback: ParseResultCallback<List<Movie>>?) {
        SearchRequest().addParam("wd", searchKeyWord)
            .listen(object : Callback {
                override fun onFailure(e: Exception) {
                    callback?.onFail(e.message ?: "")
                }

                override fun onResponseString(response: String) {
                    val document = Jsoup.parse(response)
                    val movieList = parseMovieList(document)
                    callback?.onSuccess(movieList)
                }
            })
    }
}