package tech.sumato.utility360.presentation.fragments.tasks.meter.installation

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.sumato.utility360.data.local.entity.utils.UIError
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.presentation.activity.meter.installation.MeterInstallationActivityViewModel
import tech.sumato.utility360.presentation.adapter.LoadingStateAdapter
import tech.sumato.utility360.presentation.fragments.base.listing.ChildListingFragment
import tech.sumato.utility360.presentation.fragments.base.listing.ListingFragment
import tech.sumato.utility360.presentation.fragments.customer.verification.SiteVerificationFragment
import tech.sumato.utility360.presentation.fragments.meter.installation.form.MeterInstallationFormFragment
import tech.sumato.utility360.utils.parseException
import javax.inject.Inject

@AndroidEntryPoint
class MeterInstallationTasksFragment : ListingFragment() {


    private val viewModel by activityViewModels<MeterInstallationActivityViewModel>()

    @Inject
    lateinit var meterInstallationTasksAdapter: MeterInstallationTasksAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader(show = false)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getPendingMeterInstallations().collectLatest { pagingData ->
                        meterInstallationTasksAdapter.submitData(pagingData)
                    }
                }
                /*launch {
                    viewModel.getCustomers(
                        query = mutableMapOf(
                            "filter[connection_status]" to "applied",
                            "include" to "geographicalArea",
                            "filter[has_no_site_verification]" to "1"
                        )
                    ).collectLatest { pagingData ->
                        meterInstallationTasksAdapter.submitData(pagingData)
                    }
                }*/
                launch {
                    meterInstallationTasksAdapter.loadStateFlow.collectLatest { loadState ->
                        if (loadState.source.refresh is LoadState.NotLoading || loadState.source.refresh is LoadState.Error) {
                            setRefreshing(isRefreshing = false)
                        }
                        if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && meterInstallationTasksAdapter.itemCount < 1) {
                            viewModel.setUIError(
                                UIError(
                                    showError = true,
                                    errorMessage = "No pending meter installation found !"
                                )
                            )
                        } else if (loadState.source.refresh is LoadState.Error && meterInstallationTasksAdapter.itemCount < 1) {
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

    override fun onRefreshing() {
        meterInstallationTasksAdapter.refresh()
    }

    override fun <T> getViewModel(): T? {
        return viewModel as T
    }


    override fun <T> getAdapter(): T? {
        meterInstallationTasksAdapter.setOnItemClickListener { data, position ->
            onItemClicked(data = data, position = position)
        }
        return meterInstallationTasksAdapter.withLoadStateHeaderAndFooter(
            header = LoadingStateAdapter(retryCallback = { onRetry() }),
            footer = LoadingStateAdapter(retryCallback = { onRetry() })
        ) as T
    }

    private fun onRetry() {
        meterInstallationTasksAdapter.retry()
    }

    private fun onItemClicked(data: CustomerResource, position: Int) {
        viewModel.navigate(
            MeterInstallationFormFragment::class.java, args = bundleOf(
                "data" to data,
                "position" to 0
            )
        )
    }


}