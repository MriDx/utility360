package tech.sumato.utility360.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import tech.sumato.utility360.data.repository.customer.CustomerRepositoryImpl
import tech.sumato.utility360.data.repository.firebase.FirebaseRepositoryImpl
import tech.sumato.utility360.data.repository.instructions.InstructionsRepositoryImpl
import tech.sumato.utility360.data.repository.tasks.TasksRepositoryImpl
import tech.sumato.utility360.data.repository.user.UserRepositoryImpl
import tech.sumato.utility360.domain.repository.customer.CustomerRepository
import tech.sumato.utility360.domain.repository.firebase.FirebaseRepository
import tech.sumato.utility360.domain.repository.instructions.InstructionsRepository
import tech.sumato.utility360.domain.repository.tasks.TasksRepository
import tech.sumato.utility360.domain.repository.user.UserRepository


@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {


    @Provides
    fun provideCustomerRepository(
        customerRepositoryImpl: CustomerRepositoryImpl
    ): CustomerRepository = customerRepositoryImpl


    @Provides
    fun provideUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository = userRepositoryImpl


    @Provides
    fun provideTasksRepository(
        tasksRepositoryImpl: TasksRepositoryImpl
    ): TasksRepository = tasksRepositoryImpl


    @Provides
    fun provideFirebaseRepository(
        firebaseRepositoryImpl: FirebaseRepositoryImpl
    ): FirebaseRepository = firebaseRepositoryImpl


    @Provides
    fun provideInstructionsRepository(
        instructionsRepositoryImpl: InstructionsRepositoryImpl
    ): InstructionsRepository = instructionsRepositoryImpl

}