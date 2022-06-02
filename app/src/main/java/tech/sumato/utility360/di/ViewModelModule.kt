package tech.sumato.utility360.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import tech.sumato.utility360.data.repository.customer.CustomerRepositoryImpl
import tech.sumato.utility360.domain.repository.customer.CustomerRepository


@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {


    @Provides
    fun provideCustomerRepository(
        customerRepositoryImpl: CustomerRepositoryImpl
    ): CustomerRepository = customerRepositoryImpl

}