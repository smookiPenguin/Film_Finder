package com.example.filmfinder.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.filmfinder.R

class PlatformAdpater(
    private val listPlatforms: Array<Platform>
): RecyclerView.Adapter<PlatformAdpater.ViewHolder>() {

    private lateinit var mListener : OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    //if user click on item in the recycler view
    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    class ViewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        var icon = itemView.findViewById(R.id.film_poster) as ImageView

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_platform, parent, false)
        return ViewHolder(viewItem,mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val platform = listPlatforms[position]
        if (platform != null) {
            holder.icon.setImageResource(platform.getImage())
        }
    }

    override fun getItemCount(): Int {
        return listPlatforms.size
    }

}