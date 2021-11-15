package com.example.filmfinder.externData

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class MyRequest(
    context: Context,
    url: String,
    private val d: ExternDatabase
){

    init {
        val queue = Volley.newRequestQueue(context)
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                d.createListFilm(response)
            },
            { error ->
                println(error)
            }
        )
        queue.add(request)
        queue.start()
    }
}