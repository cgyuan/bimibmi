package com.cyuan.bimibimi.parser

import android.content.Context
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.utils.GlobalUtil
import com.cyuan.bimibimi.model.Episode
import com.cyuan.bimibimi.model.HomeInfo
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.model.MovieDetail
import com.cyuan.bimibimi.parser.bimi.BimiTvParser
import com.cyuan.bimibimi.parser.halitv.HaliTvParser

object DataParserAdapter {


    fun parseHomePage(callback: ParseResultCallback<HomeInfo>?) {
        if (GlobalUtil.host == Constants.BIMIBIMI_INDEX) {
            BimiTvParser.parseHomePage(callback)
        } else if (GlobalUtil.host == Constants.HALITV_INDEX) {
            HaliTvParser.parseHomePage(callback)
        }
    }

    fun parseMovieDetail(url: String, callback: ParseResultCallback<MovieDetail>?) {
        if (GlobalUtil.host == Constants.BIMIBIMI_INDEX) {
            BimiTvParser.parseMovieDetail(url, callback)
        } else if (GlobalUtil.host == Constants.HALITV_INDEX) {
            HaliTvParser.parseMovieDetail(url, callback)
        }
    }

    fun parseDailyUpdate(callback: ParseResultCallback<List<List<Movie>>>?) {
        if (GlobalUtil.host == Constants.BIMIBIMI_INDEX) {
            BimiTvParser.parseDailyUpdate(callback)
        } else if (GlobalUtil.host == Constants.HALITV_INDEX) {
            HaliTvParser.parseDailyUpdate(callback)
        }
    }

    fun parseSearch(searchKeyWord: String, callback: ParseResultCallback<List<Movie>>?) {
        if (GlobalUtil.host == Constants.BIMIBIMI_INDEX) {
            BimiTvParser.parseSearch(searchKeyWord, callback)
        } else if (GlobalUtil.host == Constants.HALITV_INDEX) {
            HaliTvParser.parseSearch(searchKeyWord, callback)
        }
    }

    fun parseCategoryMovie(path: String, callback: ParseResultCallback<List<Movie>>?) {
        if (GlobalUtil.host == Constants.BIMIBIMI_INDEX) {
            BimiTvParser.parseCategoryMovie(path, callback)
        } else if (GlobalUtil.host == Constants.HALITV_INDEX) {
            HaliTvParser.parseCategoryMovie(path, callback)
        }
    }

    fun parseVideoSource(
        context: Context,
        episode: Episode,
        callback: ParseResultCallback<String>?,
        dataSource: String = ""
    ) {
        if (GlobalUtil.host == Constants.BIMIBIMI_INDEX) {
            BimiTvParser.parseVideoSource(context, episode, callback, dataSource)
        } else if (GlobalUtil.host == Constants.HALITV_INDEX) {
            HaliTvParser.parseVideoSource(context, episode, callback, dataSource)
        }
    }
}