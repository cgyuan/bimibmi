package com.cyuan.bimibimi.constant

interface Constants {
    companion object {
        const val UUID = "ud"
        const val BIMIBIMI_INDEX = "http://www.bimibimi.tv/"
    }

    interface Search {
        companion object {
            const val KEYWORD = "KW"
        }
    }

    interface ViewState {
        companion object {
            const val LOADING = 0
            const val EMPTY = 1
            const val ERROR = 2
            const val DONE = 3
        }
    }
}