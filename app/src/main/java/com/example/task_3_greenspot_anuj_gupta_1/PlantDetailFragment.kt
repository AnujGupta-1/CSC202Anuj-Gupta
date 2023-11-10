package com.example.task_3_greenspot_anuj_gupta_1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.task_3_greenspot_anuj_gupta_1.databinding.FragmentPlantDetailBinding
import java.util.Date
import java.util.UUID

class PlantDetailFragment : Fragment() {
    private lateinit var plants: Plants
    private var _binding: FragmentPlantDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding){
            "cannot access binding, it is null. is view visible?"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        plants = Plants(
            id = UUID.randomUUID(),
            title = "",
            place = "",
            date = Date(),
            isSolved = false
        )
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
                plants = plants.copy(title = text.toString())
            }
            placeTitle.doOnTextChanged { text, _, _, _ ->
                plants = plants.copy(place = text.toString())
            }
            recordDate.apply {
                text= plants.date.toString()
                isEnabled = false
            }
            recordSolved.setOnCheckedChangeListener { _, isChecked ->
                plants = plants.copy(isSolved = isChecked)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding= null
    }
}