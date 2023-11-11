package com.example.task_3_greenspot_anuj_gupta_1

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.task_3_greenspot_anuj_gupta_1.databinding.FragmentPlantDetailBinding
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import java.util.UUID


private const val DATE_FORMAT = "EEE, MMM, dd"

class PlantDetailFragment : Fragment() {
    private var photoName: String? = null
    private lateinit var plants: Plants
    private lateinit var mImageView: ImageView
    private var mPhotoFile: File? = null

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto && photoName != null) {
            plantDetailViewModel.updatePlant { oldPlants ->
                oldPlants.copy(photoFileName = photoName)
            }
        }
    }
    private val args: PlantDetailFragmentArgs by navArgs()
    private val plantDetailViewModel: PlantDetailViewModel by viewModels {
        PlantDetailViewModelFactory(args.plantId)
    }
    private var _binding: FragmentPlantDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding){
            "cannot access binding, it is null. is view visible?"
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentPlantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            recordTitle.doOnTextChanged { text, _, _, _ ->
                plantDetailViewModel.updatePlant { oldPlant ->
                    oldPlant.copy(title = text.toString())
                }
            }
            placeTitle.doOnTextChanged { text, _, _, _ ->
                plantDetailViewModel.updatePlant { oldPlant ->
                    oldPlant.copy(place = text.toString())
                }
            }

            recordSolved.setOnCheckedChangeListener { _, isChecked ->
                plantDetailViewModel.updatePlant { oldPlant ->
                    oldPlant.copy(isSolved = isChecked)
                }

            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    plantDetailViewModel.plant.collect { plants ->

                        plants?.let {
                            updateUi(plants) }
                    }
                }
            }
            setFragmentResultListener(
                DatePickerFragment.REQUEST_KEY_DATE
            ) { _, bundle ->
                val newDate =
                    bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
                plantDetailViewModel.updatePlant { it.copy(date = newDate) }
            }
//            plantCamera.setOnClickListener {
//                photoName = "IMG_${Date()}.JPG"
//                val photoFile = File(requireContext().applicationContext.filesDir,
//                    photoName)
//                val photoUri = FileProvider.getUriForFile(
//                    requireContext(),
//                    "com.example.task_3_greenspot_anuj_gupta_1.fileProvider",
//                    photoFile
//                )
//                takePhoto.launch(photoUri)
//            }
            plantCamera.setOnClickListener {
                photoName = "IMG_${DateFormat.format("yyyyMMdd_HHmmss", Date())}.jpg"
                val photoFile = File(requireContext().applicationContext.filesDir, photoName!!)
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.task_3_greenspot_anuj_gupta_1.fileProvider",
                    photoFile
                )

                if (photoUri != null) {
                    takePhoto.launch(photoUri)
                } else {
                    Log.e("PlantDetailFragment", "Cannot launch camera, URI is null")
                }
            }
                mImageView = view.findViewById(R.id.plant_photo)
            mImageView.setOnClickListener {
                mPhotoFile?.let {
                    if (it.exists()) {
                        ImageZoomFragment.newInstance(it).show(childFragmentManager, PHOTO_DIALOG)
                    }
                }
            }

            mImageView.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        updateImageView(mImageView.width, mImageView.height)
                        mImageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })


            }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding= null
    }
    private fun updateUi(plants: Plants) {
        binding.apply {
            if (recordTitle.text.toString() != plants.title) {
                recordTitle.setText(plants.title)
            }
            recordDate.text = plants.date.toString()
            recordDate.setOnClickListener {
                findNavController().navigate(
                    PlantDetailFragmentDirections.selectDate(plants.date)
                )
            }
            recordSolved.isChecked = plants.isSolved
            plantReport.setOnClickListener {
                val reportIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getReport(plants))
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        getString(R.string.report_subject)
                    )
                }
                val chooserIntent = Intent.createChooser(
                    reportIntent,
                    getString(R.string.send_report)
                )
                startActivity(chooserIntent)
            }
            updatePhoto(plants.photoFileName)
        }
    }
    private fun updateImageView(width: Int, height: Int) {
        mPhotoFile?.let {
            if (it.exists()) {
                val bitmap = PictureUtils.getScaledBitmap(it.path, width, height)
                mImageView.setImageBitmap(bitmap)

            } else {
                mImageView.setImageDrawable(null)
            }
        }
    }
    private fun getReport(plants: Plants): String {
        val solvedString = if (plants.isSolved) {
            getString(R.string.report_solved)
        } else {
            getString(R.string.report_unsolved)
        }
        val dateString = DateFormat.format(DATE_FORMAT, plants.date).toString()
        val place = plants.place

        return getString(
            R.string.plant_report,
            plants.title, dateString, solvedString, place
        )


    }
    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? =
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return resolvedActivity != null
    }
    private fun updatePhoto(photoFileName: String?) {
        if (binding.plantPhoto.tag != photoFileName) {
            val photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }
            if (photoFile?.exists() == true) {
                binding.plantPhoto.doOnLayout { measuredView ->
                    val scaledBitmap = PictureUtils.getScaledBitmap(
                        photoFile.path,
                        measuredView.width,
                        measuredView.height
                    )
                    binding.plantPhoto.setImageBitmap(scaledBitmap)
                    binding.plantPhoto.tag = photoFileName
                }
            } else {
                binding.plantPhoto.setImageBitmap(null)
                binding.plantPhoto.tag = null
            }
        }
    }
companion object{
    const val PHOTO_DIALOG = "PhotoDialog"
}

}