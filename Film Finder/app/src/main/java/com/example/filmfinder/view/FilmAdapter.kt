package com.example.filmfinder.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.filmfinder.R
import com.squareup.picasso.Picasso

class FilmAdapter(
    private val films: List<Film>
): RecyclerView.Adapter<FilmAdapter.ViewHolder>() {

    private lateinit var mListener : OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    //if user click on item in the recycler view
    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    class ViewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val poster = itemView.findViewById(R.id.film_poster) as ImageView
        val ratingBar = itemView.findViewById(R.id.film_ratingBar) as RatingBar
        val title = itemView.findViewById(R.id.film_title) as TextView
        val director = itemView.findViewById(R.id.film_director) as TextView
        val actor = itemView.findViewById(R.id.film_actor) as TextView
        val overview = itemView.findViewById(R.id.film_overview) as TextView
        val runtime = itemView.findViewById(R.id.film_runtime) as TextView

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film, parent, false)
        return ViewHolder(viewItem,mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val film = films[position]
        Picasso.get().load(film.poster_path).into(holder.poster);
        holder.ratingBar.rating = film.vote_average.toFloat()
        holder.title.text = film.title
        holder.director.text = film.directors.joinToString(",")
        holder.actor.text = film.actors.joinToString(",")
        holder.overview.text = film.overview
        holder.runtime.text = film.runtime.toString()
    }

    override fun getItemCount(): Int {
        return films.size
    }


}