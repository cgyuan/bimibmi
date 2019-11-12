package com.cyuan.bimibimi.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class MovieDetail {

    var title: String = ""

    var headers: String = ""

    var intro: String = ""

    var cover: String = ""

    var dataSources = mutableListOf<DataSource>()
}

class DataSource {
    var name: String = ""
    var episodes = arrayListOf<Episode>()
}

@Parcelize
class Episode(var title: String = "",var href: String = "",var url: String = "") : Parcelable {
}