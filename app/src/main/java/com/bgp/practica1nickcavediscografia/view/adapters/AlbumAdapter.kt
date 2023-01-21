package com.bgp.practica1nickcavediscografia.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bgp.practica1nickcavediscografia.databinding.RecordItemBinding
import com.bgp.practica1nickcavediscografia.model.Album
import com.bgp.practica1nickcavediscografia.view.activities.DiscographyActivity
import com.bumptech.glide.Glide

class AlbumAdapter(private val context: Context,private val albums: ArrayList<Album>):RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
    class ViewHolder(view: RecordItemBinding): RecyclerView.ViewHolder(view.root) {
        val ivAlbum = view.ivAlbum
        val tvName = view.tvAlbum
        val tvDate = view.tvDate


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = RecordItemBinding.inflate(LayoutInflater.from(context))
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tvName.text = albums[position].album
        holder.tvDate.text = albums[position].year

        Glide.with(context)
            .load(albums[position].cover)
            .into(holder.ivAlbum)
        //para clicks
        holder.itemView.setOnClickListener{
            if(context is DiscographyActivity) context.selectedAlbum(albums[position])
        }

    }

    override fun getItemCount(): Int = albums.size
}