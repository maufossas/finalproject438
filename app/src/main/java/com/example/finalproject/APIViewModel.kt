package com.example.finalproject


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.finalproject.Data.Movie
import com.example.finalproject.Network.MovieAPIRepository

class APIViewModel(application: Application): AndroidViewModel(application) {

    // api view model to access deezer
    var movieAPIRepository: MovieAPIRepository = MovieAPIRepository()
    var movie: MutableLiveData<Movie> = MutableLiveData()
    var movieList: MutableLiveData<List<Movie>> = MutableLiveData()

    // functions to access things from the repository
    fun getByTrending() {
        movieAPIRepository.getByTrending(movieList)
    }

    fun getBySearch(search: String) {
        movieAPIRepository.getBySearch(movieList, search)
    }

    fun getByID(id: String){
        movieAPIRepository.getByID(movie, id)
    }

    fun getByIDList(ids : ArrayList<String>){
        movieAPIRepository.getByIDList(movieList, ids)
    }

    fun getByDiscover(lang: String, year: String){
        movieAPIRepository.getByDiscover(movieList, lang, year)
    }

}