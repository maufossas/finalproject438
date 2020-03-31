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
    fun getByTrending(resBody: MutableLiveData<List<Movie>>, time : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getByTrending(time, API_KEY)

            withContext(Dispatchers.Main) {
                try{
                    if(response.isSuccessful) {
                     //   resBody.value = response.body()!!
                    }
                } catch (e: HttpException) {
                    println("Http error")
                }
            }
        }
    }

    fun getBySearch(resBody: MutableLiveData<List<Movie>>, time : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getByTrending(time, API_KEY)

            withContext(Dispatchers.Main) {
                try{
                    if(response.isSuccessful) {
                        //   resBody.value = response.body()!!
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
                        //   resBody.value = response.body()!!
                    }
                } catch (e: HttpException) {
                    println("Http error")
                }
            }
        }
    }

    fun getByDiscover(resBody: MutableLiveData<List<Movie>>, lang : String, rating : String, region : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getByDiscover(lang, rating, region, API_KEY)

            withContext(Dispatchers.Main) {
                try{
                    if(response.isSuccessful) {
                        //   resBody.value = response.body()!!
                    }
                } catch (e: HttpException) {
                    println("Http error")
                }
            }
        }
    }

}