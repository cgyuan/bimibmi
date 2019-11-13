package com.cyuan.bimibimi.parser

interface ParseResultCallback<T> {

    fun onSuccess(data: T)

    fun onFail(msg: String)
}
