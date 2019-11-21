package com.cyuan.bimibimi.db.repository

import com.cyuan.bimibimi.db.dao.FavoriteMovieDao
import com.cyuan.bimibimi.model.FavoriteMovie
import com.cyuan.bimibimi.model.Movie
import java.util.*

class FavoriteMovieRepository private constructor(private val favoriteMovieDao: FavoriteMovieDao) {

    fun isFavorite(movie: Movie) = favoriteMovieDao.isFavorite(movie.href)

    fun addMovie(movie: Movie) {
        val favoriteMovie = FavoriteMovie(movie.href, movie.title, movie.cover, movie.label, Calendar.getInstance())
        favoriteMovieDao.insert(favoriteMovie)
    }

    fun removeMovie(movie: Movie) = favoriteMovieDao.deleteMovie(movie.href)

    fun getAllMovieByPage() = favoriteMovieDao.queryAllMovie()

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