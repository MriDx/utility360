package tech.sumato.utility360.presentation.activity.customer.verification

import android.os.Bundle
import android.util.Log
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import tech.sumato.utility360.data.remote.model.tasks.SiteVerificationTaskRequest
import tech.sumato.utility360.data.remote.web_service.source.customer.CustomerDataSource
import tech.sumato.utility360.data.remote.web_service.source.tasks.PendingSiteVerificationDataSource
import tech.sumato.utility360.data.utils.FragmentNavigation
import tech.sumato.utility360.domain.use_case.customer.GetCustomersWithDocumentUseCase
import tech.sumato.utility360.domain.use_case.tasks.GetPendingSiteVerificationsUseCase
import tech.sumato.utility360.presentation.fragments.base.listing.ListingViewModel
import tech.sumato.utility360.presentation.utils.Navigation
import javax.inject.Inject

@HiltViewModel
class CustomerVerificationActivityViewModel
@Inject constructor(
    private val getCustomersWithDocumentUseCase: GetCustomersWithDocumentUseCase,
    private val getPendingSiteVerificationsUseCase: GetPendingSiteVerificationsUseCase,
) : ListingViewModel(), Navigation {


    private var navigation_ = MutableSharedFlow<FragmentNavigation>()
    val navigation: SharedFlow<FragmentNavigation> = navigation_


    fun getCustomers(query: MutableMap<String, String> = mutableMapOf()) = Pager(
        config = PagingConfig(pageSize = 2, prefetchDistance = 2),
        pagingSourceFactory = {
            CustomerDataSource(getCustomersWithDocumentUseCase, query = query)
        })
        .flow
        .cachedIn(viewModelScope)

    fun getPendingSiteVerifications() = Pager(
        config = PagingConfig(pageSize = 2, prefetchDistance = 2),
        pagingSourceFactory = {
            PendingSiteVerificationDataSource(
                getPendingSiteVerificationsUseCase = getPendingSiteVerificationsUseCase
            )
        })
        .flow
        .cachedIn(viewModelScope)


    override fun navigate(fragment: Class<*>) {
        super.navigate(fragment)
    }

    override fun navigate(fragment: Class<*>, args: Bundle) {
        viewModelScope.launch {
            val tmpFragment = (fragment.newInstance() as? Fragment)
                ?: throw IllegalArgumentException("Pass a fragment instance to navigate")
            navigation_.emit(
                FragmentNavigation(
                    fragment = tmpFragment,
                    args = args
                )
            )
        }
    }


    fun submitVerification(params: JSONObject) {
        viewModelScope.launch {

            Log.d("mridx", "submitVerification: $params")
        }
    }




}