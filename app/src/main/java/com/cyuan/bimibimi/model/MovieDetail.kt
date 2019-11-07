package com.cyuan.bimibimi.model

class MovieDetail {

    var title: String = ""

    var headers: String = ""

    var intro: String = ""

    var cover: String = ""

    var dataSources = mutableListOf<DataSource>()
}

class DataSource {
    var name: String = ""
    var episodes = mutableListOf<Episode>()
}

class Episode {
    var title: String = ""
    var href: String = ""
    var url: String = ""
}