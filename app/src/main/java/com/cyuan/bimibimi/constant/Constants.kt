package com.cyuan.bimibimi.constant

interface Constants {
    companion object {
        const val HOST: String = "HOST"
        const val SET_PLAYER = "SET_PLAYER"
        const val SKIN_NAME: String = "SKIN_NAME"
        const val SKIN_NAME_BEFORE_NIGHT_MODE: String = "SKIN_NAME_BEFORE_NIGHT_MODE"
        const val DEFAULT_SKIN: String = "cyan"
        const val IS_NIGHT_MODE_KEY: String = "IS_NIGHT_MODE_KEY"
        const val UUID = "ud"
        const val BIMIBIMI_INDEX = "http://www.bimibimi.cc/"
        const val HALITV_INDEX = "https://www.halitv.com/"
        const val ALLOW_PLAY_IN_BACKGROUND = "ALLOW_PLAY_IN_BACKGROUND"
        const val PALY_BEHAVIOR = "PALY_BEHAVIOR"
        const val HALITV_CATEGORY_TEMPLATE = "https://www.halitv.com/index.php?s=home-vod-type-id-%s-mcid--area--year--letter--order--picm-1-p-%s"

        const val PATH_KEY = "PATH"
        const val TITLE_KEY = "TITLE"

        val CATEGORY_MAP :Map<String, Map<String, List<String>>> = mapOf(
            Pair(BIMIBIMI_INDEX, mapOf(
                Pair(PATH_KEY, listOf("/type/riman", "/type/guoman", "/type/fanzu", "/type/juchang", "/type/move")),
                Pair(TITLE_KEY, listOf("新番放送", "国产动漫", "番组计划", "剧场动画", "影视")))),
            Pair(HALITV_INDEX, mapOf(
                Pair(PATH_KEY, listOf("62", "63", "1", "2")),
                Pair(TITLE_KEY, listOf("TV动画", "剧场版", "电影", "剧集"))))
        )
    }

    interface Player {
        companion object {
            const val MEDIA_PLAYER = "Media Player"
            const val EXO_PLAYER = "Exo Player"
            const val IJK_PLAYER = "Ijk Player"
        }
    }

    interface PlayBehavior {
        companion object {
            const val PLAY_NEXT = 0
            const val LIST_CYCLE = 1
            const val SINGLE_CYCLE = 2
            const val PAUSE = 3
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