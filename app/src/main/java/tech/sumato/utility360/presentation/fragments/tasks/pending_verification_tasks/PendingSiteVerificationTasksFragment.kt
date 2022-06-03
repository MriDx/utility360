package tech.sumato.utility360.presentation.fragments.tasks.pending_verification_tasks

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.sumato.utility360.data.local.entity.utils.UIError
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.presentation.activity.customer.verification.CustomerVerificationActivityViewModel
import tech.sumato.utility360.presentation.adapter.LoadingStateAdapter
import tech.sumato.utility360.presentation.fragments.base.listing.ListingFragment
import tech.sumato.utility360.presentation.fragments.customer.verification.SiteVerificationFragment
import tech.sumato.utility360.utils.parseException
import javax.inject.Inject

@AndroidEntryPoint
class PendingSiteVerificationTasksFragment : ListingFragment() {


    @Inject
    lateinit var pendingVerificationTasksAdapter: PendingVerificationTasksAdapter

    private val viewModel by activityViewModels<CustomerVerificationActivityViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader(show = false)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getCustomers(
                        query = mutableMapOf(
                            "filter[connection_status]" to "applied",
                            "include" to "geographicalArea",
                            "filter[has_no_site_verification]" to "1"
                        )
                    ).collectLatest { pagingData ->
                        pendingVerificationTasksAdapter.submitData(pagingData)
                    }
                }
                /*launch {
                    viewModel.getPendingSiteVerifications().collectLatest { pagingData ->
                        pendingVerificationTasksAdapter.submitData(pagingData)
                    }
                }*/
                launch {
                    pendingVerificationTasksAdapter.loadStateFlow.collectLatest { loadState ->
                        if (loadState.source.refresh is LoadState.NotLoading || loadState.source.refresh is LoadState.Error) {
                            setRefreshing(isRefreshing = false)
                        }
                        if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && pendingVerificationTasksAdapter.itemCount < 1) {
                            viewModel.setUIError(
                                UIError(
                                    showError = true,
                                    errorMessage = "No pending site verification found !"
                                )
                            )
                        } else if (loadState.source.refresh is LoadState.Error && pendingVerificationTasksAdapter.itemCount < 1) {
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


    }

    override fun <T> getViewModel(): T? {
        return viewModel as T
    }

    override fun <T> getAdapter(): T? {
        pendingVerificationTasksAdapter.setOnItemClickListener {
            onItemClicked(data = it)
        }
        return pendingVerificationTasksAdapter.withLoadStateHeaderAndFooter(
            header = LoadingStateAdapter(retryCallback = { onRetry() }),
            footer = LoadingStateAdapter(retryCallback = { onRetry() })
        ) as T
    }


    private fun onRetry() {
        pendingVerificationTasksAdapter.retry()
    }

    private fun onItemClicked(data: CustomerResource) {
        viewModel.navigate(
            SiteVerificationFragment::class.java, args = bundleOf(
                "data" to data
            )
        )
    }


}