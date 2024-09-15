package com.droidcourses.unittestingar.flows

import com.droidcourses.unittestingar.coroutines.Friend
import com.droidcourses.unittestingar.coroutines.Profile
import com.droidcourses.unittestingar.coroutines.TestingUtils
import com.droidcourses.unittestingar.coroutines.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import java.io.IOException
import kotlin.coroutines.ContinuationInterceptor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.Test

class GetUserProfileV2Test {

    @Test
    fun `Get Profile Data, flow emits successfully`() = runTest {
        val repo: UserRepository = mockk()
        val useCase = GetUserProfileV2(
            repo,
            this.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher
        )
        val profileData = TestingUtils.dummyProfileData

        coEvery { repo.getName() } coAnswers { "ramadan" }
        coEvery { repo.getRate() } coAnswers {
            delay(1000)
            4.8f
        }
        coEvery { repo.getFriends() } coAnswers {
            delay(1000)
            listOf(
                Friend("1", "Ali"),
                Friend("2", "Mohamed")
            )
        }
        val flow = useCase.getProfileDataSync()

        flow.collect { result: Result<Profile> ->
            result.isSuccess.shouldBeTrue()
            result.onSuccess {
                it shouldBeEqualTo profileData
            }
        }
    }

    @Test
    fun `Get Profile Data, should retry with error`() = runTest {
        val repo: UserRepository = mockk()
        val useCase = GetUserProfileV2(
            repo,
            this.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher
        )

        coEvery { repo.getName() } coAnswers {
            throw IOException()
        }

        val flow = useCase.getProfileDataSync()

        flow.collect { result: Result<Profile> ->
            result.isFailure.shouldBeTrue()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Get Profile Data, should retry with success`() = runTest {
        val repo: UserRepository = mockk()
        val useCase = GetUserProfileV2(
            repo,
            this.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher
        )

        // Mock
        var throwError = true
        val profileData = TestingUtils.dummyProfileData

        coEvery { repo.getName() } coAnswers {
            if (throwError) throw IOException() else profileData.name
        }

        coEvery { repo.getRate() } coAnswers {
            if (throwError) throw IOException() else profileData.rate
        }

        coEvery { repo.getFriends() } coAnswers {
            if (throwError) throw IOException() else profileData.friends
        }

        // Test
        val flow = useCase.getProfileDataSync()

        launch {
            flow.collect { result ->
                assert(result.isSuccess)
            }
        }

        advanceTimeBy(1000)
        throwError = false
        advanceTimeBy(1000)
    }
}