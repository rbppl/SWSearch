package com.example.swsearch


import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class ApiClient {
    private val client = OkHttpClient()

    fun searchCharacterOrStarship(name: String): String {
        val url = "https://swapi.dev/api/people/?search=$name"
        val request = Request.Builder().url(url).build()
        val response: Response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }

    fun searchStarship(name: String): String {
        val url = "https://swapi.dev/api/starships/?search=$name"
        val request = Request.Builder().url(url).build()
        val response: Response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }
    fun searchFilmByUrl(url: String): String {
        val request = Request.Builder().url(url).build()
        val response: Response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }
    fun getFilmDetails(url: String): String {
        val request = Request.Builder().url(url).build()
        val response: Response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }
}
