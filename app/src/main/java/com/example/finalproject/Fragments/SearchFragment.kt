package com.example.finalproject.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

    var year: String = ""
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
            R.array.year_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            yearSpinner.adapter = adapter
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
                    viewModel.getByDiscover(language, rating, year)
                    alertAdapterOfChange(movieAdapter)
                } else{
                    language = ""
                    viewModel.getByDiscover(language, rating, year)
                    alertAdapterOfChange(movieAdapter)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Nothing
            }
        }

        //Listener for year spinner
        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //If they don't choose the "original language" option.
                if(position != 0){
                    //TODO: see how countries are formatted for the API call.
                    year = resources.getStringArray(R.array.year_array)[position].toString()
                    viewModel.getByDiscover(language, rating, year)
                    alertAdapterOfChange(movieAdapter)
                } else{
                    year = ""
                    viewModel.getByDiscover(language, rating, year)
                    alertAdapterOfChange(movieAdapter)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Nothing
            }
        }

        //Listener for our textbox
        titleSearch.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                //DO NOTHING
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //DO NOTHING
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(titleSearch.text.toString().length != 0){
                    viewModel.getBySearch(titleSearch.text.toString())
                    alertAdapterOfChange(movieAdapter)
                } else {
                    viewModel.getByDiscover(language, rating, year)
                    alertAdapterOfChange(movieAdapter)
                }
            }
        })

    }

    //Function to alert change whenever the user types in a title or filters by rating, language, or year.
    private fun alertAdapterOfChange(movieAdpater : MovieListAdapter) {
        viewModel.movieList.observe(viewLifecycleOwner, Observer {
            movieList.clear()
            movieList.addAll(it)
            movieAdpater.notifyDataSetChanged()
        })
    }

}
