package com.example.music_player_app.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.music_player_app.Activity.Full_music
import com.example.music_player_app.Activity.MainActivity
import com.example.music_player_app.R
import com.example.music_player_app.databinding.FragmentNowPlayingBinding


class NowPlayingFrag : Fragment() {

    lateinit var binding: FragmentNowPlayingBinding

    var fullMusic = Full_music

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        FragmentNowPlayingBinding.bind(
            inflater.inflate(
                R.layout.fragment_now_playing,
                container,
                false
            )
        )


        binding.root.visibility = View.INVISIBLE
        binding.playPauseBtnNP.setOnClickListener {
            if (fullMusic.mediaPlayer.isPlaying) pauseMusic() else playMusic()
        }
        binding.nextBtnNP.setOnClickListener {
            Glide.with(requireContext())
                .load(MainActivity.musicList[fullMusic.currentPosition].albumArt)
                .apply(
                    RequestOptions().placeholder(R.drawable.music_player)
                        .centerCrop()
                )
                .into(binding.songImgNP)
            binding.songNameNP.text = MainActivity.musicList[fullMusic.currentPosition].title
            fullMusic.musicService!!.shownotification(R.drawable.baseline_pause_24, 1f)
            playMusic()
        }
        binding.root.setOnClickListener {
            val intent = Intent(requireContext(), fullMusic::class.java)
            intent.putExtra("index", fullMusic.currentPosition)
            intent.putExtra("class", "NowPlaying")
            ContextCompat.startActivity(requireContext(), intent, null)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (fullMusic.musicService != null) {
            binding.root.visibility = View.VISIBLE
            binding.songNameNP.isSelected = true
            Glide.with(requireContext())
                .load(MainActivity.musicList[fullMusic.currentPosition].albumArt)
                .apply(RequestOptions().placeholder(R.drawable.music_player).centerCrop())
                .into(binding.songImgNP)
            binding.songNameNP.text = MainActivity.musicList[fullMusic.currentPosition].title
            if (fullMusic.mediaPlayer.isPlaying) binding.playPauseBtnNP.setIconResource(R.drawable.baseline_pause_24)
            else binding.playPauseBtnNP.setIconResource(R.drawable.baseline_play_arrow_24)
        }
    }


    fun playMusic() {
        fullMusic.mediaPlayer.isPlaying
        fullMusic.mediaPlayer.start()
        binding.playPauseBtnNP.setIconResource(R.drawable.baseline_pause_24)
        fullMusic.musicService!!.shownotification(R.drawable.baseline_pause_24, 1f)

    }

    fun pauseMusic() {
        !fullMusic.mediaPlayer.isPlaying
        fullMusic.mediaPlayer.pause()
        binding.playPauseBtnNP.setIconResource(R.drawable.baseline_play_arrow_24)
        fullMusic.musicService!!.shownotification(R.drawable.baseline_play_arrow_24, 0f)
    }

}