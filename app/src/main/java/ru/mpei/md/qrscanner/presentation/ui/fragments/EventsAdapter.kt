package ru.mpei.md.qrscanner.presentation.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.mpei.md.qrscanner.databinding.ItemEventBinding
import ru.mpei.md.qrscanner.domain.models.DomainEvent

class EventsAdapter(
    private val onItemClick: (String) -> Unit
) : ListAdapter<DomainEvent, EventsAdapter.EventViewHolder>(EventDiffCallback) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class EventViewHolder(
        private val binding: ItemEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(event: DomainEvent) {
            binding.apply {
                tvTitle.text = event.title
                tvDateTime.text = event.dateTime
                tvLocation.text = event.location
                tvDescription.text = event.description
                
                root.setOnClickListener {
                    onItemClick(event.eventId)
                }
            }
        }
    }
    
    companion object {
        val EventDiffCallback = object : DiffUtil.ItemCallback<DomainEvent>() {
            override fun areItemsTheSame(oldItem: DomainEvent, newItem: DomainEvent): Boolean {
                return oldItem.eventId == newItem.eventId
            }
            
            override fun areContentsTheSame(oldItem: DomainEvent, newItem: DomainEvent): Boolean {
                return oldItem == newItem
            }
        }
    }
}