package tech.sumato.utility360.presentation.fragments.tasks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.databinding.DemoTaskViewBinding
import javax.inject.Inject

class CustomersAdapter @Inject constructor() :
    PagingDataAdapter<CustomerResource, CustomerViewHolder>(CustomerResourceComparator) {

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.bind(customer = getItem(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        return CustomerViewHolder(
            binding = DemoTaskViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}


class CustomerViewHolder(private val binding: DemoTaskViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(customer: CustomerResource) {
        with(binding) {
            uinView.text = customer.pbg_id
            nameView.text = customer.name
            statusView.text = customer.type
        }
    }


}

object CustomerResourceComparator : DiffUtil.ItemCallback<CustomerResource>() {
    override fun areItemsTheSame(oldItem: CustomerResource, newItem: CustomerResource): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CustomerResource, newItem: CustomerResource): Boolean {
        return oldItem == newItem
    }

}