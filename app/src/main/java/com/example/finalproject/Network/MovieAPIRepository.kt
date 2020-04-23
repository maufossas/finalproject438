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

    fun getBySearch(resBody: MutableLiveData<List<Movie>>, title : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getBySearch(API_KEY, title)

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

    fun getByID(resBody: MutableLiveData<Movie>, id : Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getByID(id.toString(), API_KEY)

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

    fun getByIDList(resBody: MutableLiveData<List<Movie>>, ids: ArrayList<Int>){
        CoroutineScope(Dispatchers.IO).launch {
            val list = ArrayList<Movie>()
            for (id in ids){
                val response = service.getByID(id.toString(), API_KEY)
                if (response.isSuccessful){
                    list.add(response.body()!!)
                }
            }
            withContext(Dispatchers.Main) {
                try{
                    if(list.size > 0) {
                        resBody.value = list
                    }
                } catch (e: HttpException) {
                    println("Http error")
                }
            }
        }
    }

    fun getByDiscover(resBody: MutableLiveData<List<Movie>>, lang : String, year : String) {
        CoroutineScope(Dispatchers.IO).launch {
            var map = HashMap<String, String>()
            map.put("api_key", API_KEY)
            if (lang.isNotEmpty()){
                map.put("with_original_language", lang)
            }
            if (year.isNotEmpty()){
                map.put("primary_release_year", year)
            }
            val response = service.getByDiscover(map)

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