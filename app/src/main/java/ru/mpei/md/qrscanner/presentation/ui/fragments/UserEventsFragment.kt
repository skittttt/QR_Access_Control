package ru.mpei.md.qrscanner.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.mpei.md.qrscanner.databinding.FragmentUserEventsBinding
import ru.mpei.md.qrscanner.presentation.ui.fragments.UserEventsFragmentDirections
import ru.mpei.md.qrscanner.presentation.viewmodels.UserEventsViewModel

@AndroidEntryPoint
class UserEventsFragment : Fragment() {
    
    private var _binding: FragmentUserEventsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: UserEventsViewModel by viewModels()
    
    private lateinit var eventsAdapter: EventsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserEventsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeViewModel()
        viewModel.loadUserEvents()
    }
    
    private fun setupRecyclerView() {
        eventsAdapter = EventsAdapter { eventId ->
            val action = UserEventsFragmentDirections.actionUserEventsToEventDetails(eventId)
            findNavController().navigate(action)
        }
        binding.rvEvents.apply {
            adapter = eventsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { events ->
                    eventsAdapter.submitList(events)
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { error ->
                    error?.let {
                        // Show error message
                    }
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}