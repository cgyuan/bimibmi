package com.cyuan.bimibimi.constant

interface Constants {
    companion object {
        const val SET_PLAYER = "SET_PLAYER"
        const val SKIN_NAME: String = "SKIN_NAME"
        const val SKIN_NAME_BEFORE_NIGHT_MODE: String = "SKIN_NAME_BEFORE_NIGHT_MODE"
        const val DEFAULT_SKIN: String = "cyan"
        const val IS_NIGHT_MODE_KEY: String = "IS_NIGHT_MODE_KEY"
        const val UUID = "ud"
        const val BIMIBIMI_INDEX = "http://www.bimibimi.tv/"
    }

    interface Player {
        companion object {
            const val MEDIA_PLAYER = "Media Player"
            const val EXO_PLAYER = "Exo Player"
            const val IJK_PLAYER = "Ijk Player"
        }
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