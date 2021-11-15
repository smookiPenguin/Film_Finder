package com.example.filmfinder.localData

import com.example.filmfinder.view.Film

data class ListFilmSaved(val films: List<FilmSaved>) {


    //check if is the same film
    fun verifyFilmIsInList(title: String):Boolean{
        for(i in films.indices){
            if(title == films[i].title){
                return true
            }
        }
        return false
    }
}