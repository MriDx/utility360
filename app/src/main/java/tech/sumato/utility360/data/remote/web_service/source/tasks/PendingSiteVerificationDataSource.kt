package tech.sumato.utility360.data.remote.web_service.source.tasks

import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.domain.use_case.tasks.GetPendingSiteVerificationsUseCase

class PendingSiteVerificationDataSource(
    private val getPendingSiteVerificationsUseCase: GetPendingSiteVerificationsUseCase,
) : PagingSource<Int, CustomerResource>() {

    override fun getRefreshKey(state: PagingState<Int, CustomerResource>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CustomerResource> =
        withContext(Dispatchers.IO) {

            try {

                val currentPageNumber = params.key ?: 1

                val query = mapOf(
                    "page[number]" to currentPageNumber.toString()
                )


                val response = getPendingSiteVerificationsUseCase(query)

                if (response.isFailed()) {
                    throw Exception(response.message)
                }

                val nextPageNumber = response.data!!.links?.next?.let {
                    Uri.parse(it.href).getQueryParameter("page[number]")?.toInt()
                }

                LoadResult.Page(
                    prevKey = null,
                    nextKey = nextPageNumber,
                    data = response.data.data!!
                )


            } catch (e: Exception) {
                e.printStackTrace()
                LoadResult.Error(e)
            }

        }
}