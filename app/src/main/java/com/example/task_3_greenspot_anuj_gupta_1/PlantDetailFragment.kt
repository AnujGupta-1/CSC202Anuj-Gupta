package com.example.task_3_greenspot_anuj_gupta_1

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import java.net.URI
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        Log.d("PlantDetailFragment", "plantId: ${args.plantId}")
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
            binding.deleteButton.setOnClickListener {
                showDeleteConfirmationDialog()
            }
            val isNewRecord = args.plantId == null
            Log.d("PlantDetailFragment", "Is new record: $isNewRecord")
            binding.deleteButton.visibility = if (isNewRecord) View.GONE else View.VISIBLE

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
            binding.newLocationButton.setOnClickListener {
                getCurrentLocation()
            }

            binding.showMap.setOnClickListener {
                if (hasLocationPermission()) {
                    showMapIfLocationAvailable()
                } else {
                    requestLocationPermission()
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

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete this record?")
            .setPositiveButton("Delete") { _, _ ->
                plantDetailViewModel.deletePlant()
                findNavController().navigateUp()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            "android.permission.ACCESS_FINE_LOCATION"
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf("android.permission.ACCESS_FINE_LOCATION"),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showMapIfLocationAvailable()
                } else {
                    // Handle the case where the user denies the permission.
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun showMapIfLocationAvailable() {
        this.plants?.location?.let { location ->
            val gmmIntentUri = Uri.parse("geo:${location.latitude},${location.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            }
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
    private fun getCurrentLocation() {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude

                plantDetailViewModel.updateLocation(latitude, longitude)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener
            )
        } catch (e: SecurityException) {
            // Handle the exception
        }
    }
companion object{
    const val PHOTO_DIALOG = "PhotoDialog"
    const val LOCATION_PERMISSION_REQUEST_CODE = 1
}

}