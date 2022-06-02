package tech.sumato.utility360.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import tech.sumato.utility360.databinding.AdapterLoadingStateBinding

class LoadingStateAdapter(private val retryCallback: () -> Unit) :
    LoadStateAdapter<LoadingStateAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState = loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder =
        ViewHolder(
            binding = AdapterLoadingStateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    inner class ViewHolder(private val binding: AdapterLoadingStateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            binding.apply {
                state = loadState
                retryCallback = this@LoadingStateAdapter.retryCallback
            }.executePendingBindings()
        }

    }

}