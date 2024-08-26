package com.droidcourses.unittestingar.testdoubles

import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test

class UserManagerTest{

    @Test
    fun `test dummy test doubles`(){
        val logger: Logger = mockk()
        val userManager = UserManager(logger)
        userManager.addUser(User("exhdsghdsg"))
        assertEquals(1,userManager.usersList.size)
    }
}