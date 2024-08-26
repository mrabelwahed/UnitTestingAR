package com.droidcourses.unittestingar.testdoubles

import org.junit.Assert.*
import org.junit.Test

class DatabaseManagerTest {

    @Test
    fun ` test fake test doubles scenario`() {
        val database  = InMemoryDatabase()
        val databaseManager = DatabaseManager(database)
        databaseManager.save("ramadan")
        val result = databaseManager.get()
        assertEquals("ramadan", result)
    }
}