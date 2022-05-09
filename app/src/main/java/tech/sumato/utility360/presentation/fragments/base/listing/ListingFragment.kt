package tech.sumato.utility360.presentation.fragments.base.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.databinding.ListingFragmnetBinding

@AndroidEntryPoint
open class ListingFragment : Fragment() {


    private var binding_: ListingFragmnetBinding? = null
    private val binding get() = binding_!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = DataBindingUtil.inflate<ListingFragmnetBinding>(
            inflater,
            R.layout.listing_fragmnet,
            container,
            false
        ).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
            this@ListingFragment.getViewModel<ListingViewModel>()?.let {
                viewModel = it
            }

        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.itemHolder.apply {
            adapter = this@ListingFragment.getAdapter()
            layoutManager = this@ListingFragment.getLayoutManager()
        }


        binding.searchIcon.setOnClickListener {
            onSearchAction()
        }

        binding.swipeRefreshLayout.setOnRefreshListener { onRefreshing() }

        setupHeader(show = true, heading = "Base Listing")

    }

    open fun <T> getViewModel(): T? {
        return null
    }

    open fun setupHeader(show: Boolean, heading: String = "") {
        binding.heading.text = heading
        binding.headerView.isVisible = show
    }

    open fun onSearchAction() {

    }


    open fun <T> getAdapter(): T? {
        return null
    }

    open fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(requireContext())
    }

    /*open fun showEmptyList(show: Boolean, message: String? = "No item found !") {
        //for show == true show empty list
        binding.apply {
            errorUI.apply {
                textView.text = message
            }.root.isVisible = show
            itemHolder.isVisible = !show
        }
    }*/

    /*open fun showError(message: String? = null, show: Boolean = true) {
        //
        binding.apply {
            errorUI.apply {
                textView.text = message
            }.root.isVisible = show
            itemHolder.isVisible = !show
        }
    }*/

    open fun onRefreshing() {}

    open fun setRefreshing(isRefreshing: Boolean = false) {
        binding.swipeRefreshLayout.isRefreshing = isRefreshing
    }

    open fun hideProgressbar() {
        binding.progressbar.isVisible = false
    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }

}