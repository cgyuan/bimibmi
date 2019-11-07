package com.cyuan.bimibimi.parser

import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.model.DataSource
import com.cyuan.bimibimi.model.Episode
import com.cyuan.bimibimi.model.MovieDetail
import org.jsoup.Jsoup
import java.util.regex.Pattern


object HtmlDataParser {
    fun parseBanner(response: String) : MutableList<Movie> {
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

        return list
    }

    fun parseMovieDetail(response: String): MovieDetail {
        val movieDetail = MovieDetail()
        val document = Jsoup.parse(response)!!
        val introEle = document.select("div[class=vod-jianjie]")[0].child(0)!!
        movieDetail.intro = introEle.text()

        movieDetail.cover = document.select("div[class=v_pic]")[0].child(0)!!.attr("data-original")

        val dataSourceEles = document.select("div[class=js_bt]")
        val baiduPanElement = dataSourceEles.removeAt(dataSourceEles.size - 1)
        val playListEles = document.select("ul[class=player_list]")
        val dataSources = mutableListOf<DataSource>()
        dataSourceEles.forEachIndexed { index, element ->
            val dataSource = DataSource()
            val dataSourceLabel = element.getElementsByTag("span")[0].text()
            dataSource.name = dataSourceLabel

            val playList = playListEles[index].getElementsByTag("a")
            val episodes = mutableListOf<Episode>()
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
//                append(key)
//                println(element.text())
//                for (i in 0 until actors.size) {
//                    if (i > 3) {
//                        append("...")
//                        break
//                    }
//                    append(actors[i].text()).append(" ")
//                }
//                append("\n")
            }
        }
        movieDetail.headers = stringBuilder.toString()


        movieDetail.dataSources = dataSources
        return movieDetail
    }
}