package com.cyuan.bimibimi.db.repository

import com.cyuan.bimibimi.db.dao.FavoriteMovieDao
import com.cyuan.bimibimi.model.FavoriteMovie
import com.cyuan.bimibimi.model.Movie
import java.util.*

class FavoriteMovieRepository(private val favoriteMovieDao: FavoriteMovieDao) {

    fun isFavorite(movie: Movie) = favoriteMovieDao.isFavorite(movie.href)

    fun addMovie(movie: Movie) {
        val favoriteMovie = FavoriteMovie(movie.href, movie.title, movie.cover, movie.label, Calendar.getInstance())
        favoriteMovieDao.insert(favoriteMovie)
    }

    fun removeMovie(movie: Movie) = favoriteMovieDao.deleteMovie(movie.href)

    fun getAllMovieByPage() = favoriteMovieDao.queryAllMovie()
}