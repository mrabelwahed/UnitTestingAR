package com.droidcourses.unittestingar.testdoubles

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import junit.framework.TestCase
import org.junit.Assert.*
import org.junit.Test

class CalculatorTest{
    /*

      mockk types : strict : default
                    relaxed

                    Dependency1 ( val value: Int , isPrem: Boolean, status Status)
                    every{ dependency1.value} return 3
                     every{ dependency1.isPrem} return true
                       every{ dependency1.status} return Status( "id")

                       mockk(relaxed = true) default values
                        Dependency1 ( 0 , false, null)
     */
    @Test
    fun `test strict nature of mockk`() {
        val dependency1: Dependency1 = mockk()
        val dependency2: Dependency2 = mockk()
        every { dependency1.value } returns 3
        every { dependency2.value } returns 4
        val calculator = Calculator(dependency1,dependency2)
        val result = calculator.add()
        assertEquals(7,result)
    }

    @Test
    fun `test relaxed nature of mockk`() {
        val dependency1: Dependency1 = mockk(relaxed = true) // 0
        val dependency2: Dependency2 = mockk()
        every { dependency2.value } returns 3
        val calculator = Calculator(dependency1,dependency2)
        val result = calculator.add()
        assertEquals(3,result)
    }

    @Test
    fun `test spy stub nature of mockk`() {
        val dependency1: Dependency1 = mockk()
        val dependency2: Dependency2 = Dependency2(4) // real
        val spyDependency2 = spyk(dependency2)
        every { dependency1.value } returns 3
//        every { spyDependency2.value } returns 7
        val calculator = Calculator(dependency1,spyDependency2)
        val result = calculator.add()
        assertEquals(7,result)
    }

    @Test
    fun `test arg catching nature of mockk`() {
        val mathService: MathService = mockk()
        val sut = CalculatorV2(mathService)
//        val slot = slot<Int>()
        every { mathService.add(any(), any()) }  answers { arg<Int>(0) + arg<Int>(1)}
        val res =  sut.add(2)

        assertEquals(12,res)
//        assertEquals(2, slot.captured)
        verify {
          mathService.add(withArg {
              assertTrue(it == 2)
          }, any())
        }



    }
}