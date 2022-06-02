package tech.sumato.utility360.data.remote.web_service.source.customer

import android.net.Uri
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.utils.Status
import tech.sumato.utility360.domain.use_case.customer.GetCustomersUseCase
import tech.sumato.utility360.domain.use_case.customer.GetCustomersWithDocumentUseCase
import java.net.URL

class CustomerDataSource(
    private val getCustomersWithDocumentUseCase: GetCustomersWithDocumentUseCase,
    private val query: MutableMap<String, String> = mutableMapOf()
) : PagingSource<Int, CustomerResource>() {

    override fun getRefreshKey(state: PagingState<Int, CustomerResource>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CustomerResource> {
        return withContext(Dispatchers.IO) {
            try {

                val currentPageNumber = params.key ?: 1

                query["page[number]"] = currentPageNumber.toString()

                val customersResponse = getCustomersWithDocumentUseCase(query = query)

                if (customersResponse.isFailed()) {
                    throw Exception(customersResponse.message)
                }

                Log.d("mridx", "links : ${customersResponse.data?.links}")
                Log.d("mridx", "errors : ${customersResponse.data?.errors}")
                Log.d("mridx", "data : ${customersResponse.data?.data}")
                Log.d("mridx", "meta : ${customersResponse.data?.meta}")

                val nextPageNumber = customersResponse.data!!.links?.next?.let {
                    Uri.parse(it.href).getQueryParameter("page[number]")?.toInt()

                }

                LoadResult.Page(
                    prevKey = null,
                    nextKey = nextPageNumber,
                    data = customersResponse.data.data!!
                )


            } catch (e: Exception) {
                e.printStackTrace()
                LoadResult.Error(e)
            }

        }
    }
}