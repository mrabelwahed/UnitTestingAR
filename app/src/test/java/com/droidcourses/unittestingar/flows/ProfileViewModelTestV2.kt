package com.droidcourses.unittestingar.flows

import app.cash.turbine.test
import com.droidcourses.unittestingar.coroutines.TestingUtils
import io.mockk.coEvery
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.amshove.kluent.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class ProfileViewModelTestV2 {

    @get: Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `test success scenario`() = runTest {
        val usecase: GetUserProfileV2 = mockk()
        coEvery { usecase.getProfileDataSync() } coAnswers {
            flowOf(
                Result.success(TestingUtils.dummyProfileData)
            )
        }
        val viewModel = ProfileViewModel(usecase)

        viewModel.getUserProfile()

        advanceUntilIdle()

        viewModel.profileUIState.test {
//            awaitItem() shouldBe ProfileUIState.Idle
//            awaitItem() shouldBe ProfileUIState.Loading
            (awaitItem() as ProfileUIState.Success).data shouldBe TestingUtils.dummyProfileData
        }
    }

    @Test
    fun `test failure scenario`() = runTest {
        val usecase: GetUserProfileV2 = mockk()
        coEvery { usecase.getProfileDataSync() } coAnswers { flow { throw IOException("Oops!") } }
        val viewModel = ProfileViewModel(usecase)

        viewModel.getUserProfile()

        viewModel.profileUIState.test {
            awaitItem() shouldBe ProfileUIState.Idle
            awaitItem() shouldBe ProfileUIState.Loading
            (awaitItem() as ProfileUIState.Error).message shouldBe "Oops!"
        }
    }
}

class MainDispatcherRule(val testDispatcher: TestDispatcher = StandardTestDispatcher()) :
    TestWatcher() {
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}