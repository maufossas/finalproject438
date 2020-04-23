package com.example.finalproject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.finalproject.Data.Movie
import com.example.finalproject.Network.MovieAPIRepository

class APIViewModel(application: Application): AndroidViewModel(application) {

    // api view model to access movie database
    private var movieAPIRepository: MovieAPIRepository = MovieAPIRepository()
    var movie: MutableLiveData<Movie> = MutableLiveData()
    // multiple lists to prevent data races on simultaneous access (for example, getting favorites and recents)
    var movieList: MutableLiveData<List<Movie>> = MutableLiveData()
    var secondMovieList : MutableLiveData<List<Movie>> = MutableLiveData()
    var thirdMovieList : MutableLiveData<List<Movie>> = MutableLiveData()

    // functions to access things from the repository

    fun getByTrending() {
        movieAPIRepository.getByTrending(movieList)
    }

    // title search
    fun getBySearch(search: String) {
        movieAPIRepository.getBySearch(movieList, search)
    }

    fun getByID(id: Int){
        movieAPIRepository.getByID(movie, id)
    }

    // three id functions to access the different lists
    fun getByIDList(ids : ArrayList<Int>){
        movieAPIRepository.getByIDList(movieList, ids)
    }

    fun getSecondIDList(ids: ArrayList<Int>){
        movieAPIRepository.getByIDList(secondMovieList, ids)
    }

    fun getThirdIDList(ids : ArrayList<Int>){
        movieAPIRepository.getByIDList(thirdMovieList, ids)
    }

    // discover options of langauge and year (rating handled separately through our database)
    fun getByDiscover(lang: String, year: String){
        movieAPIRepository.getByDiscover(movieList, lang, year)
    }

}