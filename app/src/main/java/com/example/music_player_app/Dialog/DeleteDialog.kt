package com.example.music_player_app.Dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.music_player_app.Services.log
import com.example.music_player_app.databinding.ActivityDeleteDialogBinding
import java.io.File


class DeleteDialog(var activity: Activity, file: File, onExit: () -> Unit) {
    var dialog: Dialog = Dialog(activity)

    var binding: ActivityDeleteDialogBinding =
        ActivityDeleteDialogBinding.inflate(LayoutInflater.from(activity))


    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.No.setOnClickListener {
            dialog.dismiss()
        }
        binding.Yes.setOnClickListener {

            if (file.exists()) {

                val deleted = file.delete()
                if (deleted) {

                    onExit.invoke()

                    ("File Deleted").log()
                } else {
                    ("File Not Deleted").log()
                }
            }

            dialog.dismiss()
            onExit.invoke()
        }
        dialog.show()
    }


}