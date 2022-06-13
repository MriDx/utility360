package tech.sumato.utility360.presentation.fragments.customer.find.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sumato.etrack_agri.ui.utils.PlaceHolderDrawableHelper
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.databinding.TaskViewBinding
import tech.sumato.utility360.databinding.UserViewBinding
import tech.sumato.utility360.presentation.fragments.tasks.adapter.CustomerResourceComparator
import javax.inject.Inject

class SearchedCustomerAdapter @Inject constructor() :
    PagingDataAdapter<CustomerResource, SearchedCustomerAdapter.SearchedCustomerViewHolder>(
        CustomerResourceComparator
    ) {
    private var onItemClicked: ((data: CustomerResource, position: Int) -> Unit)? = null
    fun setItemClickedListener(listener: (data: CustomerResource, position: Int) -> Unit) {
        onItemClicked = listener
    }

    override fun onBindViewHolder(holder: SearchedCustomerViewHolder, position: Int) {
        holder.bind(customerResource = getItem(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchedCustomerViewHolder {
        return SearchedCustomerViewHolder(
            binding = UserViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    inner class SearchedCustomerViewHolder(private val binding: UserViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(customerResource: CustomerResource) {
            with(binding) {
                uinView.text = customerResource.name
                //nameView.text = "${customerResource.meterInstallation?.meter_serial_no} | ${customerResource.plan_type}"
                nameView.text =
                    if (customerResource.user?.lastMeterReading == null) "Last bill information not available" else buildSpannedString {
                        append("Last billed on ")
                        bold {
                            append(
                                customerResource.user.lastMeterReading.date_of_billing ?: "n/a"
                            )
                        }
                    }
                Glide.with(root.context)
                    .asBitmap()
                    .load(customerResource.photo)
                    .placeholder(
                        PlaceHolderDrawableHelper.getAvatar(
                            root.context,
                            customerResource.name,
                            position = bindingAdapterPosition
                        )
                    )
                    .into(avatarView)
                root.setOnClickListener {
                    onItemClicked?.invoke(customerResource, bindingAdapterPosition)
                }
            }
        }

    }


}