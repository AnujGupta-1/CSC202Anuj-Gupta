package com.example.task_3_greenspot_anuj_gupta_1

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.task_3_greenspot_anuj_gupta_1.databinding.FragmentPlantListBinding
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID


class PlantListFragment : Fragment() {

    private var _binding: FragmentPlantListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val plantListViewModel: PlantListViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlantListBinding.inflate(inflater, container, false)

        binding.plantRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                plantListViewModel.plants.collect { plants ->
                    binding.plantRecyclerView.adapter =
                        PlantListAdapter(plants){plantId ->
                            findNavController().navigate(
                        PlantListFragmentDirections.showPlantDetail(plantId)
                            )
                }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_plant_list, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_record -> {
                showNewRecord()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showNewRecord() {
        viewLifecycleOwner.lifecycleScope.launch {
            val newLocation = Location("")
            val newRecord = Plants(
                id = UUID.randomUUID(),
                title = "",
                place = "",
                date = Date(),
                isSolved = false,
                location= newLocation
            )
            plantListViewModel.addRecord(newRecord)
            findNavController().navigate(
                PlantListFragmentDirections.showPlantDetail(newRecord.id)
            )
        }
    }
}
