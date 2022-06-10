package tech.sumato.utility360.domain.use_case.customer

import com.undabot.izzy.models.JsonDocument
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.utils.Resource
import tech.sumato.utility360.domain.repository.customer.CustomerRepository
import javax.inject.Inject

class GetCustomerUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {

    suspend operator fun invoke(
        uuid: String,
        query: Map<String, String>
    ): Resource<JsonDocument<CustomerResource>> {
        return customerRepository.getCustomer(uuid, query)
    }

}