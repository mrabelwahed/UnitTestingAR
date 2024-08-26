package com.droidcourses.unittestingar.coroutines

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import kotlin.coroutines.ContinuationInterceptor

class GetUserProfileTest {
    @Test
    fun `Given happy scenario when call getProfileDataAsync() then the profile should be constructed correctly`() = runTest{

       //GIVEN
        val fakeRepo = FakeUserRepo()
        val useCase = GetUserProfile(fakeRepo,this.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher)
        //WHEN
        val result =  useCase.getProfileDataAsync()
        // THEN
        assertEquals(
            TestingUtils.dummyProfileData, result
        )
    }

    @Test
    fun `Given stable server  when call getProfileDataAsync() then I should expect concurrent calling  `() = runTest {
        //GIVEN
        val fakeRepo = FakeUserRepoV2()
        val useCase = GetUserProfile(fakeRepo, this.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher)
        //WHEN
        val result =  useCase.getProfileDataAsync()
        // THEN
        assertEquals( 1000, currentTime)
    }

    @Test
    fun `Given stable server  when call getProfileDataSync() then I should expect sequential calling  `() = runTest {
        //GIVEN
        val fakeRepo = FakeUserRepoV2()
        val useCase = GetUserProfile(fakeRepo, this.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher)
        //WHEN
        val result =  useCase.getProfileDataSync()
        // THEN
        assertEquals( 2000, currentTime)
    }

    @Test
    fun `test using Mocks with coroutine`() = runTest {
        //GIVEN
        val repo: UserRepository = mockk()
        val useCase = GetUserProfile(repo, this.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher)
        coEvery { repo.getName() } coAnswers   { "ramadan"}
        coEvery { repo.getRate() } coAnswers  {
            delay(1000)
            4.8f
        }
        coEvery { repo.getFriends() } coAnswers  {
            delay(1000)
            listOf(Friend("1","alex"))
        }
        //WHEN
        val result =  useCase.getProfileDataSync()

        // THEN
        assertEquals( 2000, currentTime)
    }
}

object TestingUtils {
    val dummyProfileData = Profile(
        name = "ramadan",
        rate = 4.8f,
        friends = listOf(
            Friend("1", "Ali"),
            Friend("2", "Mohamed"),
        )
    )
}