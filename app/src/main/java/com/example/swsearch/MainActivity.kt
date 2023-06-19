package com.example.swsearch

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
class MainActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var resultTextView: TextView
    private lateinit var apiClient: ApiClient
    private lateinit var addToFavoritesButton: Button
    private var resultText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchEditText = findViewById(R.id.editSearch)
        resultTextView = findViewById(R.id.TextResult)
        apiClient = ApiClient()
        addToFavoritesButton = findViewById(R.id.addToFavorites)
        addToFavoritesButton.visibility = View.GONE // Скрываем кнопку
        addToFavoritesButton.setOnClickListener {
            val resultText = resultTextView.text.toString()
            if (resultText.isNotEmpty() && !isResultInFavorites(resultText)) {
                addToFavorites(resultText)
            }
        }

    }

    fun openFavorites(view: View) {
        val intent = Intent(this, FavoritesActivity::class.java)
        startActivity(intent)
    }

    fun searchButtonClicked(view: View) {
        val searchTerm = searchEditText.text.toString().trim()
        if (searchTerm.isNotEmpty()) {
            performSearch(searchTerm)
        }
    }

    private fun performSearch(searchTerm: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val characterResult = apiClient.searchCharacterOrStarship(searchTerm)
            val characterResultText = parseCharacterSearchResult(characterResult)

            if (characterResultText.isNotEmpty()) {
                launch(Dispatchers.Main) {
                    resultTextView.text = characterResultText
                    resultText = characterResultText
                    addToFavoritesButton.visibility = View.VISIBLE // Показываем кнопку
                }
            } else {
                val starshipResult = apiClient.searchStarship(searchTerm)
                val starshipResultText = parseStarshipSearchResult(starshipResult)

                launch(Dispatchers.Main) {
                    resultTextView.text = starshipResultText
                    resultText = starshipResultText
                    addToFavoritesButton.visibility = View.VISIBLE // Показываем кнопку
                }
            }
        }
    }

    private fun parseCharacterSearchResult(searchResult: String): String {
        val jsonObject = JSONObject(searchResult)
        val resultsArray: JSONArray = jsonObject.getJSONArray("results")
        if (resultsArray.length() > 0) {
            val firstResult: JSONObject = resultsArray.getJSONObject(0)
            val name = firstResult.getString("name")
            val gender = firstResult.getString("gender")
            val starships = firstResult.getJSONArray("starships")
            val starshipCount = starships.length()

            val films = firstResult.getJSONArray("films")
            val filmsList = mutableListOf<String>()
            for (i in 0 until films.length()) {
                val filmUrl = films.getString(i)
                val filmResult = apiClient.getFilmDetails(filmUrl)
                val filmTitle = parseFilmDetails(filmResult)
                filmsList.add(filmTitle)
            }

            return "Имя: $name\nПол: $gender\nКоличество звездолетов: $starshipCount\nФильмы: ${filmsList.joinToString(", ")}"
        }
        return ""
    }

    private fun parseFilmDetails(filmResult: String): String {
        val jsonObject = JSONObject(filmResult)
        val title = jsonObject.getString("title")
        val director = jsonObject.getString("director")
        val producer = jsonObject.getString("producer")
        return "\nФильм: $title\nРежиссер: $director\nПродюсер: $producer"
    }
    private fun parseStarshipSearchResult(searchResult: String): String {
        val jsonObject = JSONObject(searchResult)
        val resultsArray: JSONArray = jsonObject.getJSONArray("results")
        if (resultsArray.length() > 0) {
            val firstResult: JSONObject = resultsArray.getJSONObject(0)
            val name = firstResult.getString("name")
            val model = firstResult.getString("model")
            val manufacturer = firstResult.getString("manufacturer")
            val passengers = firstResult.getString("passengers")

            val films = firstResult.getJSONArray("films")
            val filmsList = mutableListOf<String>()
            for (i in 0 until films.length()) {
                val filmUrl = films.getString(i)
                val filmResult = apiClient.getFilmDetails(filmUrl)
                val filmTitle = parseFilmDetails(filmResult)
                filmsList.add(filmTitle)
            }

            return "Имя: $name\nМодель: $model\nПроизводитель: $manufacturer\nПассажиры: $passengers\nФильмы: ${filmsList.joinToString(", ")}"
        }
        return ""
    }

    private fun addResultToFavorites(result: String) {
        val favoritesPref = getSharedPreferences("Favorites", Context.MODE_PRIVATE)
        val favoritesEditor = favoritesPref.edit()
        val favoritesSet = favoritesPref.getStringSet("FavoritesSet", HashSet<String>())

        favoritesSet?.let {
            it.add(result)
            favoritesEditor.putStringSet("FavoritesSet", it)
            favoritesEditor.apply()

            // Сохранение результата в базу данных SQLite
            val dbHelper = FavoritesDbHelper(applicationContext)
            dbHelper.insertFavorite(result)
        }
    }
    private fun isResultInFavorites(result: String): Boolean {
        val favoritesDbHelper = FavoritesDbHelper(this)
        val favorites = favoritesDbHelper.getAllFavorites()
        favoritesDbHelper.close()

        return favorites.contains(result)
    }

    private fun addToFavorites(result: String) {
        val favoritesDbHelper = FavoritesDbHelper(this)
        favoritesDbHelper.insertFavorite(result)
        favoritesDbHelper.close()

        Toast.makeText(this, "Добавлено в избранное", Toast.LENGTH_SHORT).show()
    }
    private fun parseFilmResults(searchResult: String): String {
        val jsonObject = JSONObject(searchResult)
        val resultsArray: JSONArray = jsonObject.getJSONArray("results")
        if (resultsArray.length() > 0) {
            val firstResult: JSONObject = resultsArray.getJSONObject(0)
            val films = firstResult.getJSONArray("films")

            val filmsList = mutableListOf<String>()
            for (i in 0 until films.length()) {
                val filmUrl = films.getString(i)
                val filmResult = apiClient.searchFilmByUrl(filmUrl)
                val filmTitle = parseFilmTitle(filmResult)
                filmsList.add(filmTitle)
            }

            return "Фильмы:\n${filmsList.joinToString("\n")}"
        }
        return ""
    }

    private fun parseFilmTitle(searchResult: String): String {
        val jsonObject = JSONObject(searchResult)
        return jsonObject.getString("title")
    }


}
