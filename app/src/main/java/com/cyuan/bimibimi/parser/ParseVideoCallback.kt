package com.cyuan.bimibimi.parser

interface ParseVideoCallback {

    fun onSuccess(url: String)

    fun onFail(msg: String)
}
