package tech.sumato.utility360.presentation.fragments.tasks

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tech.sumato.utility360.data.local.entity.utils.UIError
import tech.sumato.utility360.data.remote.web_service.source.customer.CustomerDataSource
import tech.sumato.utility360.domain.use_case.customer.GetCustomersUseCase
import tech.sumato.utility360.domain.use_case.customer.GetCustomersWithDocumentUseCase
import tech.sumato.utility360.presentation.fragments.base.listing.ListingViewModel
import javax.inject.Inject

@HiltViewModel
class TasksFragmentViewModel @Inject constructor(
    private val getCustomersWithDocumentUseCase: GetCustomersWithDocumentUseCase,
) : ListingViewModel() {


    init {
        //setUIError(UIError.show(message = "Is it working ??"))
        //removeError()
    }

    private fun removeError() {
        viewModelScope.launch {
            delay(1000 * 2)
            setUIError(UIError.hide())
        }
    }


    fun getCustomers(query: MutableMap<String, String> = mutableMapOf()) = Pager(
        config = PagingConfig(pageSize = 2, prefetchDistance = 2),
        pagingSourceFactory = { CustomerDataSource(getCustomersWithDocumentUseCase, query = query) }
    ).flow.cachedIn(viewModelScope)


}