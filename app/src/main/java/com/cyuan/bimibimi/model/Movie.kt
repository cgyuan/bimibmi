package com.cyuan.bimibimi.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(var href: String,
                 var title: String,
                 var cover: String,
                 var errorCover: String,
                 var label: String) : Parcelable {
    constructor() : this("", "", "", "", "")

    override fun toString(): String {
        return "Movie(href='$href', title='$title', cover='$cover', errorCover='$errorCover', title='$label')"
    }
}