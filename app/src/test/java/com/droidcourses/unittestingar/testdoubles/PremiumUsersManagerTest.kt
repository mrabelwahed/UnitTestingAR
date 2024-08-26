package com.droidcourses.unittestingar.testdoubles

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Test

class PremiumUsersManagerTest {

    @Test
    fun `test mocking test double`() {
        val userService: UserService = mockk()
        val premiumUsersManager = PremiumUsersManager(userService)
        every { userService.getUsersCount() } returns  10
        val result  = premiumUsersManager.getUsersCount()
        assertEquals(10,result)
        verify(atLeast = 1) {
            userService.getUsersCount()
        }
    }

    @Test
    fun `test spy test double`() {
        val userService: UserService = RealUserService()
        val spyUserService = spyk(userService)
        val premiumUsersManager = PremiumUsersManager(spyUserService)
        val result  = premiumUsersManager.getUsersCount()
        assertEquals(20,result)
        verify(atLeast = 1) {
            spyUserService.getUsersCount()
        }
    }
}