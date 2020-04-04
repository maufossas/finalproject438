package com.example.finalproject.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.APIViewModel
import com.example.finalproject.Adapters.MovieListAdapter
import com.example.finalproject.Data.Movie

import com.example.finalproject.R
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_trending.*


class SearchFragment : Fragment() {

    lateinit var viewModel: APIViewModel
    var movieList: ArrayList<Movie> = ArrayList()

    var country: String = ""
    var language: String = ""
    var rating: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // api viewmodel
        viewModel = ViewModelProvider(this).get(APIViewModel::class.java)

        // set up recycler view with grid layout adapter
        val recyclerView = searchRecyclerView
        val movieAdapter = MovieListAdapter(movieList, this.context!!)
        recyclerView.adapter = movieAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this.context!!,
            R.array.languages_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            languageSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this.context!!,
            R.array.country_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            countrySpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this.context!!,
            R.array.ratings_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            ratingSpinner.adapter = adapter
        }

        //Listener for language spinner
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //If they don't choose the "original language" option.
                if(position != 0){
                    language = resources.getStringArray(R.array.language_abbreviations)[position].toString()
                    //TODO: figure out why this is not displaying; it has something to do with how the API call is being made it is not fetching anything
                    //This will work:
                    //viewModel.getByTrending()
                    //This will also work:
                    //viewModel.getByDiscover("en","","")
                    //But this does not work:
                    viewModel.getByDiscover(language, rating, country)
                    alertAdapterOfChange(movieAdapter)
                    //For testing purposes:
                    //Toast.makeText(parent!!.context, language, Toast.LENGTH_LONG).show()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                //I don't think we have to do anything here.
            }
        }

        //Listener for country spinner
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //If they don't choose the "original language" option.
                if(position != 0){
                    //TODO: see how countries are formatted for the API call.
                    //country = resources.getStringArray(R.array.country_array)[position].toString()
                    //This does not work:
                    viewModel.getByDiscover(language, rating, country)
                    alertAdapterOfChange(movieAdapter)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                //I don't think we have to do anything here.
            }
        }

    }

    //Function to alert change whenever the user types in a title or filters by rating, language, or country.
    private fun alertAdapterOfChange(movieAdpater : MovieListAdapter) {
        viewModel.movieList.observe(viewLifecycleOwner, Observer {
            movieList.clear()
            movieList.addAll(it)
            movieAdpater.notifyDataSetChanged()
        })
    }

}
