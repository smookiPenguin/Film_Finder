package com.example.filmfinder.externData

import android.content.Context
import org.json.JSONObject
import com.example.filmfinder.MainActivity
import com.example.filmfinder.view.Film
import kotlin.random.Random


class ExternDatabase (private val context: Context, private val main: MainActivity)
{

    /*private val login: String = "i507_2_2"
    private val password: String = "Uapah8ee"*/
    private val url: String = "http://os-vps418.infomaniak.ch:1186/i507_2_2/movie_db.json"
    private var genre:String = ""
    private val maxItem:Int = 5 //max film to display
    private var showLocalFilm = false


    //call db for display random film
    fun getFilmByGenre(genre: String){
        this.genre = genre
        showLocalFilm = false
        callDB()
    }

    //call db to get information for played film
    fun getFilmFromLocalData(){
        showLocalFilm = true
        callDB()
    }

    private fun callDB(){
        MyRequest(context, url, this)
    }

    //show to the user random unplayed film or played film
    fun createListFilm(json: JSONObject){
        if (!showLocalFilm){
            recommend(getFilmFromDb(json))
        }
        else{
            main.getLocalFilm(getFilmFromDb(json))

        }
    }

    //create Film object form json data
    private fun getFilmFromDb(json: JSONObject):List<Film>{
        val jsonArray = json.getJSONArray("movies")
        val listFilm= mutableListOf<Film>()
        for (i in 0 until jsonArray.length()) {
            val title = jsonArray.getJSONObject(i).getString("title")
            val posterPath = jsonArray.getJSONObject(i).getString("poster_path")
            val overview = jsonArray.getJSONObject(i).getString("overview")
            val runtime = jsonArray.getJSONObject(i).getInt("runtime")
            var voteAverage = jsonArray.getJSONObject(i).getDouble("vote_average")
            voteAverage /= 2
            val jsonDirector = jsonArray.getJSONObject(i).getJSONArray("directors")
            val listDirector = mutableListOf<String>()
            for (j in 0 until jsonDirector.length()){
                listDirector.add(j, jsonDirector.getJSONObject(j).getString("name"))
            }
            val jsonActors = jsonArray.getJSONObject(i).getJSONArray("actors")
            val listActors = mutableListOf<String>()
            for (j in 0 until jsonActors.length()){
                listActors.add(j, jsonActors.getJSONObject(j).getString("name"))
                if(j>0){
                    break
                }
            }
            val jsonGenres = jsonArray.getJSONObject(i).getJSONArray("genres")
            val listGenres = mutableListOf<String>()
            for (j in 0 until jsonGenres.length()){
                listGenres.add(j, jsonGenres.getJSONObject(j).getString("name"))
            }
            val film = Film(title,
                posterPath,
                overview,
                runtime,
                voteAverage,
                listDirector,
                listActors,
                listGenres)

            listFilm.add(i, film)
        }
        return listFilm
    }

    //choose many film to show to the user from Db
    private fun recommend(list: List<Film>){
        val allFilms = mutableListOf<Film>()

        //get all film match with genre chose by user
        for (element in list){
            if (element.checkGenre(genre)) {
                allFilms.add(element)
            }
        }

        var i = 0
        val ids= mutableListOf<Int>()
        //get many different id number for to choose film
        while(i < maxItem){
            val id = Random.nextInt(0, allFilms.size-1)
            if(!ids.contains(id)){
                ids.add(id)
                i++
            }
        }

        val films = mutableListOf<Film>()
        //get film to show
        for (i in 0 until maxItem){
            films.add(allFilms[ids[i]])
        }
        main.displayFilm(films)
    }
}