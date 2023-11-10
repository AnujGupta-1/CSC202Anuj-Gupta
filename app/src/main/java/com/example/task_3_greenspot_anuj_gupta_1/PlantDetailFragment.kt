package com.example.task_3_greenspot_anuj_gupta_1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.task_3_greenspot_anuj_gupta_1.databinding.FragmentPlantDetailBinding
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID



class PlantDetailFragment : Fragment() {
    private lateinit var plants: Plants
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
                    oldPlant.copy(title = text.toString())
                }
            }
            recordDate.apply {

                isEnabled = false
            }
            recordSolved.setOnCheckedChangeListener { _, isChecked ->
                plantDetailViewModel.updatePlant { oldPlant ->
                    oldPlant.copy(isSolved = isChecked)
                }

            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    plantDetailViewModel.plant.collect { plant ->
                        plants?.let { updateUi(it) }
                    }
                }
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
            recordSolved.isChecked = plants.isSolved
        }
    }
}