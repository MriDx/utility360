package tech.sumato.utility360.presentation.fragments.base.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.databinding.ChildListingFragmentBinding

@AndroidEntryPoint
abstract class ChildListingFragment : Fragment() {

    private var binding_: ChildListingFragmentBinding? = null
    private val binding get() = binding_!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = ChildListingFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.itemHolder.apply {
            adapter = this@ChildListingFragment.getAdapter()
            layoutManager = this@ChildListingFragment.getLayoutManager()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            onRefreshing()
        }

    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }


    open fun <T> getViewModel(): T? {
        return null
    }


    open fun <T> getAdapter(): T? {
        return null
    }

    open fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(requireContext())
    }

    open fun onRefreshing() {}

    open fun setRefreshing(isRefreshing: Boolean = false) {
        binding.swipeRefreshLayout.isRefreshing = isRefreshing
    }

}