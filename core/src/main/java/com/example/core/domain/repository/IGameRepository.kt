package com.example.core.domain.repository

import com.example.core.data.Resource
import com.example.core.domain.model.Game
import kotlinx.coroutines.flow.Flow

interface IGameRepository {
    fun getAllGameFromApi(key:String): Flow<Resource<List<Game>>>
    fun getGameDetail(id:String,key:String): Flow<Resource<Game>>
    fun getAllFavouritedGame(): Flow<List<Game>>
    suspend fun deleteFavouriteGame(game:Game)
    suspend fun insertFavourite(game: Game)
}