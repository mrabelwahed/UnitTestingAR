package com.droidcourses.unittestingar.flows

import com.droidcourses.unittestingar.coroutines.Profile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retry

class GetUserProfileV2(
    private val userRepository: com.droidcourses.unittestingar.coroutines.UserRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getProfileDataSync() = flow {
        println("STARTING FLOW")
        val name = userRepository.getName()
        val rate = userRepository.getRate()
        val friends = userRepository.getFriends()

        val profile = Profile(
            name = name,
            rate = rate,
            friends = friends
        )

        emit(Result.success(profile))
    }
        .retry(2) {
            println("STARTING FLOW")
            (it is Exception).also {
                println("BEFORE DELAY")
                delay(1000)
                println("AFTER DELAY")
            }
        }
        .catch {
            emit(Result.failure(it))
        }
        .flowOn(ioDispatcher)
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
        Friend("2", "Mohamed")
    )

    override suspend fun getRate() = 4.8f
}

class FakeUserRepoV2 : UserRepository {
    override suspend fun getName() = "ramadan"

    override suspend fun getFriends(): List<Friend> {
        delay(1000)
        return listOf(
            Friend("1", "Ali"),
            Friend("2", "Mohamed")
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