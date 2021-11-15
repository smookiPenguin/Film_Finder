package com.example.filmfinder.view

import android.graphics.drawable.Drawable
import com.example.filmfinder.R

data class Platform (val icon: Int){

    companion object {
        const val NETFLIX = 0
        const val PRIME_VIDEO = 1
    }

    fun getImage():Int{
        var d:Int = R.drawable.netflix_icon
        when(icon){
            NETFLIX-> d = R.drawable.netflix_icon
            PRIME_VIDEO-> d = R.drawable.prime_video_icon
        }
        return d
    }

}