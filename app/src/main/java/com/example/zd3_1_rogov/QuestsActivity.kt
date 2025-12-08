package com.example.zd3_1_rogov

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class QuestsActivity : Activity() {
    private val apiKey = "4d7e9eff"
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var spinnerGenres: Spinner
    private lateinit var adapter: MovieAdapter
    private lateinit var progressBar: ProgressBar

    private val allMovies = ArrayList<Movie>()

    private val allGenres = ArrayList<String>()

    private var loadedMoviesCount = 0
    private var totalMoviesToLoad = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quests)

        recyclerView = findViewById(R.id.recycler)
        searchView = findViewById(R.id.search)
        spinnerGenres = findViewById(R.id.genres_spinner)
        progressBar = findViewById(R.id.progress)

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        adapter = MovieAdapter { movie ->
            openMovieDetails(movie)
        }
        recyclerView.adapter = adapter

        setupSpinner()

        setupSearchView()

        loadInitialMovies()
    }

    private fun setupSpinner() {
        allGenres.clear()
        allGenres.add("Все жанры")

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            allGenres
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGenres.adapter = spinnerAdapter

        spinnerGenres.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedGenre = allGenres[position]
                filterMoviesByGenre(selectedGenre)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
            }
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchMovies(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.length >= 3) {
                        searchMovies(it)
                    } else if (it.isEmpty()) {
                        adapter.data = allMovies
                        loadedMoviesCount = 0
                        totalMoviesToLoad = 0
                    }
                }
                return false
            }
        })
    }

    private fun loadInitialMovies() {
        searchMovies("movie")
    }

    private fun searchMovies(query: String) {
        if (query.isEmpty()) return

        progressBar.visibility = View.VISIBLE
        loadedMoviesCount = 0
        totalMoviesToLoad = 0
        allMovies.clear()

        val url = "https://www.omdbapi.com/?s=$query&apikey=$apiKey"

        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                try {
                    val obj = JSONObject(response)
                    if (obj.getString("Response") == "True") {
                        val moviesArray = obj.getJSONArray("Search")
                        totalMoviesToLoad = moviesArray.length()

                        if (totalMoviesToLoad == 0) {
                            adapter.data = emptyList()
                            updateSpinnerWithEmptyList()
                            progressBar.visibility = View.GONE
                            return@StringRequest
                        }

                        for (i in 0 until moviesArray.length()) {
                            val movieJson = moviesArray.getJSONObject(i)
                            val imdbID = movieJson.getString("imdbID")
                            loadMovieDetails(imdbID)
                        }
                    } else {
                        adapter.data = emptyList()
                        updateSpinnerWithEmptyList()
                        progressBar.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    progressBar.visibility = View.GONE

                }
            },
            { error ->
                progressBar.visibility = View.GONE

            }
        )
        queue.add(stringRequest)
    }

    private fun loadMovieDetails(imdbID: String) {
        val url = "https://www.omdbapi.com/?apikey=$apiKey&i=$imdbID"

        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                try {
                    val movieObj = JSONObject(response)

                    val genresString = movieObj.getString("Genre")
                    val genresList = if (genresString != "N/A") {
                        genresString.split(", ").toList()
                    } else {
                        listOf("Не указано")
                    }

                    val movie = Movie(
                        Title = movieObj.getString("Title"),
                        Poster = movieObj.getString("Poster"),
                        Genre = genresList,
                        Plot = movieObj.getString("Plot"),
                        Year = movieObj.getString("Year"),
                        Runtime = movieObj.getString("Runtime"),
                        imdbID = imdbID
                    )

                    allMovies.add(movie)
                    loadedMoviesCount++

                    if (loadedMoviesCount == totalMoviesToLoad) {
                        onAllMoviesLoaded()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    loadedMoviesCount++
                    if (loadedMoviesCount == totalMoviesToLoad) {
                        onAllMoviesLoaded()
                    }
                }
            },
            { error ->
                loadedMoviesCount++
                if (loadedMoviesCount == totalMoviesToLoad) {
                    onAllMoviesLoaded()
                }
            }
        )

        queue.add(stringRequest)
    }

    private fun onAllMoviesLoaded() {
        runOnUiThread {
            adapter.data = allMovies

            updateGenresList()

            progressBar.visibility = View.GONE

            if (allMovies.isEmpty()) {

            }
        }
    }

    private fun updateGenresList() {
        val uniqueGenres = HashSet<String>()

        allMovies.forEach { movie ->
            movie.Genre.forEach { genre ->

                if (genre != "N/A" && genre != "Не указано" && genre.isNotBlank()) {
                    uniqueGenres.add(genre.trim())
                }
            }
        }

        val newGenresList = ArrayList<String>()
        newGenresList.add("Все жанры")
        newGenresList.addAll(uniqueGenres.sorted())

        if (newGenresList != allGenres) {
            allGenres.clear()
            allGenres.addAll(newGenresList)
            updateSpinnerAdapter()
        }
    }

    private fun updateSpinnerWithEmptyList() {
        allGenres.clear()
        allGenres.add("Все жанры")
        updateSpinnerAdapter()
    }

    private fun updateSpinnerAdapter() {
        runOnUiThread {
            val spinnerAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                allGenres
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGenres.adapter = spinnerAdapter
        }
    }

    private fun filterMoviesByGenre(genre: String) {
        if (genre == "Все жанры") {
            adapter.data = allMovies
        } else {
            val filteredMovies = allMovies.filter { movie ->
                movie.Genre.any { it.contains(genre, ignoreCase = true) }
            }
            adapter.data = filteredMovies
        }
    }

    private fun openMovieDetails(movie: Movie) {
        val dialog = AlertDialog.Builder(this)
            .setTitle(movie.Title)
            .setMessage("""
            Год: ${movie.Year}
            Длительность: ${movie.Runtime}
            Жанры: ${movie.Genre.joinToString(", ")}
            Описание: ${movie.Plot}
        """.trimIndent())
            .setPositiveButton("OK", null)
            .create()

        dialog.show()
    }
}