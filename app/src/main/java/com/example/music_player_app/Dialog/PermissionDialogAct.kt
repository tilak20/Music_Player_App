package com.example.music_player_app.Dialog

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.music_player_app.R
import com.example.music_player_app.databinding.ActivityPermissionDialogBinding
import com.example.music_player_app.databinding.ExitDialogBinding

class PermissionDialogAct(var activity: Activity, onExit: () -> Unit) {

    var dialog: Dialog = Dialog(activity)
    var binding: ActivityPermissionDialogBinding =
        ActivityPermissionDialogBinding.inflate(LayoutInflater.from(activity))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.allow.setOnClickListener {
            dialog.dismiss()
            onExit.invoke()
        }
        dialog.show()
    }

}

