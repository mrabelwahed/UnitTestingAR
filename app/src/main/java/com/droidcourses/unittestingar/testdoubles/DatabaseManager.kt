package com.droidcourses.unittestingar.testdoubles

interface Database {
    fun save(data: String)
    fun get(): String
}

// fake impl
class InMemoryDatabase : Database {
    var dataHolder: String = ""
    override fun save(data: String) {
        dataHolder = data
    }

    override fun get(): String {
        return dataHolder
    }
}

class DatabaseManager(val database: Database) {
    fun save(data: String) {
        database.save(data)
    }

    fun get(): String {
        return database.get()
    }
}