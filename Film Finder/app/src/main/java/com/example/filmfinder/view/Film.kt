package com.example.filmfinder.view

data class Film (val title: String,
                 val poster_path: String,
                 val overview: String,
                 val runtime: Int,
                 var vote_average: Double,
                 val directors: List<String>,
                 val actors: List<String>,
                 val genres: List<String>
){

    fun checkGenre(genre:String):Boolean{
        return genres.contains(genre)
    }
}
