package com.example.music_player_app.Dialog

import android.app.Activity
import android.app.Dialog
import android.net.ConnectivityManager
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.music_player_app.databinding.ExitDialogBinding

class ExitDialog(var activity: Activity, onExit: () -> Unit) {
    var dialog: Dialog = Dialog(activity)
    var binding: ExitDialogBinding = ExitDialogBinding.inflate(LayoutInflater.from(activity))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.cancel.setOnClickListener {
            dialog.dismiss()
        }
        binding.exist.setOnClickListener {
            dialog.dismiss()
            onExit.invoke()
        }
        dialog.show()
    }

}