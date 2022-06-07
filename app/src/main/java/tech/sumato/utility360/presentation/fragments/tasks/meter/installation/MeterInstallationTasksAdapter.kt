package tech.sumato.utility360.presentation.fragments.tasks.meter.installation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.databinding.TaskViewBinding
import tech.sumato.utility360.presentation.fragments.tasks.adapter.CustomerResourceComparator
import javax.inject.Inject

class MeterInstallationTasksAdapter @Inject constructor() :
    PagingDataAdapter<CustomerResource, MeterInstallationTasksAdapter.MeterInstallationTaskViewHolder>(
        CustomerResourceComparator
    ) {

    private var onItemClicked: ((data: CustomerResource) -> Unit)? = null

    fun setOnItemClickListener(listener: ((data: CustomerResource) -> Unit)) {
        onItemClicked = listener
    }



    override fun onBindViewHolder(holder: MeterInstallationTaskViewHolder, position: Int) {
        holder.bind(customerResource = getItem(position)!!)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MeterInstallationTaskViewHolder {
        return MeterInstallationTaskViewHolder(
            binding = TaskViewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }


    inner class MeterInstallationTaskViewHolder(private val binding: TaskViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(customerResource: CustomerResource) {
            with(binding) {
                statusView.text = customerResource.type?.replaceFirstChar {
                    it.uppercase()
                }
                uinView.text = customerResource.name
                nameView.text = buildSpannedString {
                    append("PBG id - ")
                    bold { append(if (customerResource.pbg_id!!.isEmpty()) "n/a" else customerResource.pbg_id) }
                    append("\n")
                    bold { append(customerResource.geographicalArea?.name ?: "") }
                }
                root.setOnClickListener {
                    onItemClicked?.invoke(customerResource)
                }
            }
        }

    }


}