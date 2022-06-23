package tech.sumato.utility360.presentation.fragments.customer.find

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.sumato.utility360.data.local.entity.utils.UIError
import tech.sumato.utility360.databinding.UserFinderFragmentV2Binding
import tech.sumato.utility360.presentation.activity.meter.reading.MeterReadingActivityViewModel
import tech.sumato.utility360.presentation.adapter.LoadingStateAdapter
import tech.sumato.utility360.presentation.fragments.customer.find.adapter.SearchedCustomerAdapter
import tech.sumato.utility360.presentation.fragments.meter.reading.form.MeterReadingFragment
import tech.sumato.utility360.utils.hideKeyboard
import tech.sumato.utility360.utils.parseException
import javax.inject.Inject

@AndroidEntryPoint
class FindCustomerFragmentV2 : Fragment() {


    private var binding_: UserFinderFragmentV2Binding? = null
    private val binding get() = binding_!!

    private val viewModel by activityViewModels<MeterReadingActivityViewModel>()

    @Inject
    lateinit var searchedCustomerAdapter: SearchedCustomerAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = UserFinderFragmentV2Binding.inflate(inflater, container, false).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
            viewModel = this@FindCustomerFragmentV2.viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchBtn.setOnClickListener {
            val searchTerm = binding.searchField.text.toString().trim()

            it.hideKeyboard()

            //do search

            doSearch(searchTerm)

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.searchedCustomer.collectLatest {
                        searchedCustomerAdapter.submitData(it)
                    }
                }
                launch {
                    searchedCustomerAdapter.loadStateFlow.collectLatest { loadState ->
                        if (loadState.source.refresh is LoadState.NotLoading || loadState.source.refresh is LoadState.Error) {
                            //setRefreshing(isRefreshing = false)
                        }
                        if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && searchedCustomerAdapter.itemCount < 1) {
                            viewModel.setUIError(
                                UIError(
                                    showError = true,
                                    errorMessage = "No consumer found !"
                                )
                            )
                        } else if (loadState.source.refresh is LoadState.Error && searchedCustomerAdapter.itemCount < 1) {
                            viewModel.setUIError(
                                UIError(
                                    showError = true,
                                    errorMessage = parseException(e = (loadState.source.refresh as LoadState.Error).error)
                                )
                            )
                        } else {
                            viewModel.setUIError(UIError.hide())
                        }
                    }
                }
            }
        }


        binding.usersHolder.apply {
            adapter = this@FindCustomerFragmentV2.getAdapter()
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    fun getAdapter() = searchedCustomerAdapter.apply {
        setItemClickedListener { data, position ->
            if (!data.isReadyToMeterReading()) {
                showSnackbar(message = "Site verification or Meter installation is not completed yet !")
                return@setItemClickedListener
            }
            viewModel.navigate(
                fragment = MeterReadingFragment::class.java,
                args = bundleOf(
                    "data" to data,
                    "position" to position
                )
            )
        }
    }.withLoadStateHeaderAndFooter(
        header = LoadingStateAdapter(retryCallback = { onRetry() }),
        footer = LoadingStateAdapter(retryCallback = { onRetry() })
    )

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    fun onRetry() {
        searchedCustomerAdapter.retry()
    }

    fun doSearch(query: String) {
        viewModel.doSearchByQuery(searchQuery = query)
    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }

}