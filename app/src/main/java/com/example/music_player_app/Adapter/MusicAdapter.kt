package com.example.music_player_app.Adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.music_player_app.Activity.Application
import com.example.music_player_app.Activity.Full_music
import com.example.music_player_app.Activity.MainActivity
import com.example.music_player_app.DataBase.Music
import com.example.music_player_app.Services.log
import com.example.music_player_app.databinding.MusicItemBinding
import com.google.gson.Gson


class MusicAdapter(
    val mainActivity: MainActivity,
    var audioList: List<Music>,
    val onClick: (View, Int) -> Unit
) : RecyclerView.Adapter<MusicAdapter.ViewData>() {


    var Cposition: Int = 0

    class ViewData(var binding: MusicItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewData(MusicItemBinding.inflate(LayoutInflater.from(mainActivity)))

    override fun onBindViewHolder(holder: ViewData, position: Int) {

        holder.binding.apply {
            txtTitle.text = audioList[position].title
            txtArtist.text = audioList[position].artist

            Glide.with(mainActivity).load(audioList[position].albumArt).into(imgThumbnail)

            Option.setOnClickListener {
                onClick(Option, position)
            }

            CrdView1.setOnClickListener {
                Cposition = position
                mainActivity.startActivity(Intent(mainActivity, Full_music::class.java).putExtra("position", position))
            }
        }
    }


    override fun getItemCount() = audioList.size

    @SuppressLint("NotifyDataSetChanged")
    fun SearchData(searchList: ArrayList<Music>) {
        audioList = searchList
        notifyDataSetChanged()
    }


}