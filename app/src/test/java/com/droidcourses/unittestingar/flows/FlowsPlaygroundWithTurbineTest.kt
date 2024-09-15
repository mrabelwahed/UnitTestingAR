package com.droidcourses.unittestingar.flows

import app.cash.turbine.test
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class FlowsPlaygroundWithTurbineTest {
    // testing of flows : 1- testing flow itself
    //                  2- testing flow consumer
    @Test
    fun `test flow itself`() = runTest {
        val flow = flowOf(1, 2, 3, 4)
        flow.test {
            1 shouldBeEqualTo awaitItem()
            2 shouldBeEqualTo awaitItem()
            3 shouldBeEqualTo awaitItem()
            4 shouldBeEqualTo awaitItem()
            awaitComplete()
        }
    }

    @Test
    fun `test flow consumer`() = runTest {
        val flow = flowOf(1, 2, 3)
        val res = mutableListOf<Int>()
        flow.collect {
            res.add(it)
        }
        assertEquals(listOf(1, 2, 3), res)
    }

    @Test
    fun `test flow consumer cont`() = runTest {
        val flow = flow {
            for (i in 1..3) {
                emit(i)
            }
        }
        flow.test {
            assertEquals(1, awaitItem())
            assertEquals(2, awaitItem())
            assertEquals(3, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `test flow consumer cont with delay`() = runTest {
        val flow = flow {
            for (i in 1..3) {
                emit(i)
                delay(200)
            }
        }
        val res = mutableListOf<Int>()
        flow.onEach {
            res.add(it)
        }
            .launchIn(this)

        advanceUntilIdle()

        assertEquals(listOf(1, 2, 3), res)
    }

    @Test
    fun `test flow consumer with exception`() = runTest {
        val flow = flow {
            emit(1)
            throw IllegalStateException("error happened")
        }

        flow.test {
            assertEquals(1, awaitItem())
            assertEquals("error happened", awaitError().message)
        }
    }

    @Test
    fun `test cold flow`() = runTest {
        val flow = flowOf(1, 2, 3).map { it * 10 }
        flow.test {
            awaitItem() shouldBeEqualTo 10
            awaitItem() shouldBeEqualTo 20
            awaitItem() shouldBeEqualTo 30
            awaitComplete()
        }

        flow.test {
            awaitItem() shouldBeEqualTo 10
            awaitItem() shouldBeEqualTo 20
            awaitItem() shouldBeEqualTo 30
            awaitComplete()
        }
    }

    @Test
    fun `convert cold flow`() = runTest {
        val flow = flowOf(1, 2, 3).map { it * 10 }.stateIn(this)
        flow.test {
            awaitItem() shouldBeEqualTo 30
        }
    }

    @Test
    fun `test stateflow conflated`() = runTest {
        val flow = MutableStateFlow<UIState>(UIState.Loading)

        flow.test {
            awaitItem() shouldBe UIState.Loading
            flow.tryEmit(UIState.Success)
            awaitItem() shouldBe UIState.Success
        }
    }

    @Test
    fun `test shared flow`() = runTest {
        val flow = MutableSharedFlow<Int>()
        val job = launch(start = CoroutineStart.LAZY) {
            flow.emit(1)
        }

        flow.test {
            job.start()
            awaitItem() shouldBeEqualTo 1
        }
    }

    @Test
    fun `test shared flows with SharingStarted WhileSubscribed`() = runTest {
        val flow = flowOf(
            "Event 1",
            "Event 2",
            "Event 3"
        )

        val sharedFlow = flow
            .onCompletion { println("SHARED FLOW COMPLETED") }
            .shareIn(
                this,
                SharingStarted.WhileSubscribed(),
                1
            )

        sharedFlow.test {
            awaitItem() shouldBeEqualTo "Event 1"
            awaitItem() shouldBeEqualTo "Event 2"
            awaitItem() shouldBeEqualTo "Event 3"
        }

        coroutineContext.cancelChildren()
    }

    @Test
    fun `test shared flows with SharingStarted Lazily`() = runTest {
        val flow = flowOf(
            "Event 1",
            "Event 2",
            "Event 3"
        )

        val sharedFlow = flow
            .onCompletion { println("SHARED FLOW COMPLETED") }
            .shareIn(
                this,
                SharingStarted.Lazily,
                1
            )

        sharedFlow.test {
            awaitItem() shouldBeEqualTo "Event 1"
            awaitItem() shouldBeEqualTo "Event 2"
            awaitItem() shouldBeEqualTo "Event 3"
        }

        coroutineContext.cancelChildren()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `collect with eager strategy`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(
            "Event 1",
            "Event 2",
            "Event 3"
        )
        val sharedFlow = flow
            .shareIn(
                this,
                SharingStarted.Eagerly,
                1
            )

        sharedFlow.test {
            awaitItem() shouldBeEqualTo "Event 3"

            coroutineContext.cancelChildren()
        }
    }
}
sealed class UIState {
    object Success : UIState()
    object Error : UIState()
    object Loading : UIState()
}