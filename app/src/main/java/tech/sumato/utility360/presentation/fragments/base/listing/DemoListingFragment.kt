package tech.sumato.utility360.presentation.fragments.base.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.databinding.DemoListingFragmentBinding

@AndroidEntryPoint
abstract class DemoListingFragment : Fragment() {


    private var binding_: DemoListingFragmentBinding? = null
    private val binding get() = binding_!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = DataBindingUtil.inflate<DemoListingFragmentBinding?>(
            inflater,
            R.layout.demo_listing_fragment,
            container,
            false
        ).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getTitle())

        binding.searchIcon.isVisible = showSearch()
        binding.filterBtn.isVisible = showFilter()


        binding.itemHolder.apply {
            setItemCount(getItemsCount())
            itemBuilder = { parent, index -> buildItem(parent, index) }
            itemBinding { holder, index -> bindItem(parent = holder.itemView, position = index) }
        }.render()

        binding.swipeRefreshLayout.setOnRefreshListener { onRefreshing() }

        binding.filterBtn.setOnClickListener {
            onFilterClicked()
        }


    }


    open fun showSearch() = true

    open fun showFilter() = true

    open fun setItemCount(count: Int) {
        binding.itemHolder.apply {
            setItemCount(count)
        }.render()
    }

    open fun onSortClicked() {}

    open fun searchIcon(show: Boolean = false) {
        binding.searchIcon.isVisible = false
    }

    open fun filterIcon(show: Boolean = false) {
        binding.filterBtn.isVisible = false
    }

    open fun onFilterClicked() {}

    fun setTitle(title: String) {
        binding.heading.text = title
    }

    open fun getTitle(): String = "Demo Listing"

    open fun onRefreshing() {}

    open fun stopRefreshing() {
        binding.swipeRefreshLayout.isRefreshing = false
    }

    abstract fun getItemsCount(): Int

    abstract fun buildItem(parent: ViewGroup, position: Int): View

    open fun bindItem(parent: View, position: Int) {}


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }

}