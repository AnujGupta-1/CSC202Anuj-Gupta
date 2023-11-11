package com.example.task_3_greenspot_anuj_gupta_1

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.example.task_3_greenspot_anuj_gupta_1.R.id.plant_photo
import java.io.File

class ImageZoomFragment : DialogFragment() {

    private lateinit var mImageView: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_image_zoom, null)
        mImageView = view.findViewById(R.id.plant_photo)
        updateImageView(getFileExtra())
        return AlertDialog.Builder(requireActivity()).setView(view).create()
    }

    fun getFileExtra() = arguments?.getSerializable(PHOTO_FILE_EXTRA) as File

    private fun updateImageView(photoFile: File) {
        if (photoFile.exists()) {
            // Get the size of the screen
            val displayMetrics = requireContext().resources.displayMetrics
            val destWidth = displayMetrics.widthPixels
            val destHeight = displayMetrics.heightPixels

            // Get the scaled bitmap
            val bitmap = PictureUtils.getScaledBitmap(photoFile.path, destWidth, destHeight)
            mImageView.setImageBitmap(bitmap)
        } else {
            mImageView.setImageDrawable(null)
        }
    }

    companion object {
        const val PHOTO_FILE_EXTRA = "photoFile"

        fun newInstance(photoFile: File): ImageZoomFragment {
            val bundle = Bundle().apply { putSerializable(PHOTO_FILE_EXTRA, photoFile) }
            return ImageZoomFragment().apply { arguments = bundle }
        }
    }
}