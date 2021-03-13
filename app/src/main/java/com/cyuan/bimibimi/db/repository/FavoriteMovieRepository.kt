package com.cyuan.bimibimi.db.repository

import com.cyuan.bimibimi.core.utils.GlobalUtil
import com.cyuan.bimibimi.db.dao.FavoriteMovieDao
import com.cyuan.bimibimi.model.FavoriteMovie
import com.cyuan.bimibimi.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class FavoriteMovieRepository private constructor(private val favoriteMovieDao: FavoriteMovieDao) {

    suspend fun isFavorite(movie: Movie) = withContext(Dispatchers.IO) {
        favoriteMovieDao.isFavorite(movie.href)
    }


    suspend fun addMovie(movie: Movie) = withContext(Dispatchers.IO) {
        val favoriteMovie = FavoriteMovie(movie.href, movie.title, movie.cover, movie.label, GlobalUtil.host, Calendar.getInstance())
        favoriteMovieDao.insert(favoriteMovie)
    }

    suspend fun removeMovie(movie: Movie) = withContext(Dispatchers.IO) {
        favoriteMovieDao.deleteMovie(movie.href)
    }

    fun getAllMovieByPage(host: String) = favoriteMovieDao.queryAllMovie(host)

    companion object {
        @Volatile
        private var  instance: FavoriteMovieRepository? = null
        fun getInstance(favoriteMovieDao: FavoriteMovieDao): FavoriteMovieRepository {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = FavoriteMovieRepository(favoriteMovieDao)
                    }
                }
            }
            return instance!!
        }
    }
}