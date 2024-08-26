package com.droidcourses.unittestingar.flows

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FlowsPlaygroundTest {
// testing of flows : 1- testing flow itself
  //                  2- testing flow consumer
    @Test
    fun `test flow itself` ()  = runTest {
        val flow  = flowOf(1,2,3,4)
        val res = flow.toList()
        assertEquals(listOf(1,2,3,4),res)
    }

    @Test
    fun `test flow consumer`()= runTest {
        val flow = flowOf(1,2,3)
        val res = mutableListOf<Int>()
        flow.collect {
            res.add(it)
        }
        assertEquals(listOf(1,2,3),res)
    }

    @Test
    fun `test flow consumer cont`()= runTest {
        val flow = flow {
             for(i in 1..3){
                 emit(i)
             }
        }
        val res = mutableListOf<Int>()
        flow.collect {
            res.add(it)
        }
        assertEquals(listOf(1,2,3),res)
    }

    @Test
    fun `test flow consumer cont with delay`()= runTest {
        val flow = flow {
            for(i in 1..3){
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

        assertEquals(listOf(1,2,3),res)
    }


    @Test
    fun `test flow consumer with exception`()= runTest {
        val res = mutableListOf<Int>()
        try {
            val flow = flow {
                emit(1)
                throw IllegalStateException("error happened")
            }
            flow.collect {
                res.add(it)
            }
        }
        catch (e: Exception) {
            println("exception in test ${e.message}")
            res.add(-1)
        }

        assertEquals(listOf(1,-1),res)
    }

}