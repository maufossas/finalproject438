package com.example.finalproject.Network

import androidx.lifecycle.MutableLiveData
import com.example.finalproject.Data.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

// implement methods from song interface
class MovieAPIRepository {
    val service = ApiClient.makeRetrofitService()

    // all methods use coroutines to avoid making api requests on the main thread
    // get top charts results
    fun getByTrending(resBody: MutableLiveData<List<Movie>>) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getByTrending()

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