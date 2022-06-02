package tech.sumato.utility360.domain.use_case.customer

import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.utils.Resource
import tech.sumato.utility360.domain.repository.customer.CustomerRepository
import javax.inject.Inject

class GetCustomersUseCase @Inject constructor(private val customerRepository: CustomerRepository) {

    suspend operator fun invoke(query: Map<String, String> = mapOf()): Resource<List<CustomerResource>> {
        return customerRepository.getCustomers(query)
    }

}