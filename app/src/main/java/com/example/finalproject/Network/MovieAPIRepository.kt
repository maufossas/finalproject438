package com.example.finalproject.Network

import androidx.lifecycle.MutableLiveData
import com.example.finalproject.Data.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class MovieAPIRepository {
    val service = ApiClient.makeRetrofitService()
    val API_KEY = "39e54ff6a343fcda519d85f9e57d967b"

    // all methods use coroutines to avoid making api requests on the main thread
    fun getByTrending(resBody: MutableLiveData<List<Movie>>) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getByTrending("day", API_KEY)

            withContext(Dispatchers.Main) {
                try{
                    if(response.isSuccessful) {
                        resBody.value = response.body()!!.results
                    }
                } catch (e: HttpException) {
                    println("Http error")
                }
            }
        }
    }

    fun getBySearch(resBody: MutableLiveData<List<Movie>>, time : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getByTrending(API_KEY, time)

            withContext(Dispatchers.Main) {
                try{
                    if(response.isSuccessful) {
                         resBody.value = response.body()!!.results
                    }
                } catch (e: HttpException) {
                    println("Http error")
                }
            }
        }
    }

    fun getByID(resBody: MutableLiveData<Movie>, id : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getByID(id, API_KEY)

            withContext(Dispatchers.Main) {
                try{
                    if(response.isSuccessful) {
                         resBody.value = response.body()!!
                    }
                } catch (e: HttpException) {
                    println("Http error")
                }
            }
        }
    }

    fun getByDiscover(resBody: MutableLiveData<List<Movie>>, lang : String, rating : String, region : String) {
        CoroutineScope(Dispatchers.IO).launch {
            var args : String = ""
            if (lang.isNotEmpty()){
                args+= "&language=" + lang
            }
            if (rating.isNotEmpty()){
                args+="&vote_average.gte=" + rating
            }
            if (region.isNotEmpty()){
                args+="&region=" + region
            }
            val response = service.getByDiscover("?api_key="+API_KEY, args)

            withContext(Dispatchers.Main) {
                try{
                    if(response.isSuccessful) {
                        resBody.value = response.body()!!.results
                    }
                } catch (e: HttpException) {
                    println("Http error")
                }
            }
        }
    }

}