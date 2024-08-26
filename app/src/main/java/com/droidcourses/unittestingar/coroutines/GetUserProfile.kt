package com.droidcourses.unittestingar.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class GetUserProfile(private val userRepository: UserRepository, private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {

    suspend fun getProfileDataAsync() = withContext(ioDispatcher) {
       val name = async { userRepository.getName() }
        val rate = async { userRepository.getRate() }
        val friends = async { userRepository.getFriends() }

        Profile(
            name = name.await(),
            rate = rate.await(),
            friends = friends.await()
        )
    }

    suspend fun getProfileDataSync() = withContext(ioDispatcher) {
        val name =  userRepository.getName()
        val rate =  userRepository.getRate()
        val friends = userRepository.getFriends()

        Profile(
            name = name,
            rate = rate,
            friends = friends
        )
    }

}


interface UserRepository {
    suspend fun getName(): String
    suspend fun getFriends(): List<Friend>
    suspend fun getRate(): Float
}

class FakeUserRepo : UserRepository {
    override suspend fun getName() = "ramadan"

    override suspend fun getFriends() = listOf(
        Friend("1", "Ali"),
        Friend("2", "Mohamed"),
    )

    override suspend fun getRate() = 4.8f
}

class FakeUserRepoV2 : UserRepository {
    override suspend fun getName() = "ramadan"

    override suspend fun getFriends(): List<Friend> {
        delay(1000)
        return listOf(
            Friend("1", "Ali"),
            Friend("2", "Mohamed"),
        )
    }

    override suspend fun getRate(): Float {
        delay(1000)
        return 1.8f
    }
}


data class Profile(
    val name: String,
    val rate: Float,
    val friends: List<Friend>
)

data class Friend(val id: String, val userName: String)