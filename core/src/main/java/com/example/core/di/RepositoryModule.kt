package com.example.core.di

import com.example.core.data.GameRepository
import com.example.core.data.LoginRepository
import com.example.core.domain.repository.IGameRepository
import com.example.core.domain.repository.ILoginRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [NetworkModule::class, DatabaseModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun provideGameRepository(gameRepository: GameRepository)
            :IGameRepository

    @Binds
    abstract fun provideLoginRepository(loginRepository: LoginRepository)
            :ILoginRepository
}