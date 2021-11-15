package com.example.filmfinder.localData

import android.content.Context

import android.widget.Toast
import com.example.filmfinder.MainActivity
import com.example.filmfinder.view.Film
import com.google.gson.Gson
import org.json.JSONObject
import java.io.*


class LocalDatabase (private val context: Context, private val main:MainActivity){

    private val pathFilmUser = "film_user.json"
    private var fileFilmUser:File
    private var genre:String = ""

init {
    val path = context.filesDir
    val letDirectory = File(path, "LET")
    letDirectory.mkdirs()
    fileFilmUser = File(letDirectory, pathFilmUser)
}
    //genre chose by user
    fun setGenre(genre:String){
        this.genre = genre
    }

    //save the film in local db
    fun saveFilm(film: Film, rateUser: Float){
        val list = addFilmSavedInListFilmSaved(convertFilmToFilmSaved(film, rateUser),
            getFilmUser())
        val json = Gson().toJson(list)
        fileFilmUser.delete()
        fileFilmUser.appendText(json)
        Toast.makeText(context, "Film Saved", Toast.LENGTH_SHORT).show()
    }

    //display local film by genre
    fun loadFilm(allFilms:List<Film>){
        val json = JSONObject(fileFilmUser.readText())
        val films = mutableListOf<Film>()
        val jsonArray = json.getJSONArray("films")
        for (i in 0 until jsonArray.length()) {
            val title = jsonArray.getJSONObject(i).getString("title")
            val score = jsonArray.getJSONObject(i).getDouble("score")
            for(j in allFilms.indices){
                val film = allFilms[j]
                if(title==film.title){
                    film.vote_average = score
                    if (film.checkGenre(genre)){
                        films.add(film)
                    }
                }
            }
        }
        main.displayFilm(films)
    }

    //create a special object film for the save
    private fun convertFilmToFilmSaved(film:Film, rateUser: Float):FilmSaved{
        return FilmSaved(film.title, rateUser)
    }

    //check if the film isn't saved, if not we save it (so in this config, we cannot change this user score
    private fun addFilmSavedInListFilmSaved(film:FilmSaved, films:ListFilmSaved):ListFilmSaved{
        var lf = films
        if(!films.verifyFilmIsInList(film.title)){
            val l = mutableListOf<FilmSaved>()
            for (i in films.films.indices){
                l.add(films.films[i])
            }
            l.add(film)
            lf = ListFilmSaved(l)
        }
        return lf
    }

    //return a list film from json local file
    private fun getFilmUser():ListFilmSaved {
        val list = mutableListOf<FilmSaved>()
        try{
            val json = JSONObject(fileFilmUser.readText())
            val jsonArray = json.getJSONArray("films")
            for (i in 0 until jsonArray.length()) {
                val title = jsonArray.getJSONObject(i).getString("title")
                val score = jsonArray.getJSONObject(i).getDouble("score")
                list.add(FilmSaved(title, score.toFloat()))
            }
        }catch (e: IOException){

        }
        return ListFilmSaved(list)
    }
}