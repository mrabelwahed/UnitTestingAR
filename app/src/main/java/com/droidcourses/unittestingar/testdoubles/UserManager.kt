package com.droidcourses.unittestingar.testdoubles

class UserManager(val logger: Logger) {
    val usersList = mutableListOf<User>()
    fun addUser(user: User) {
        usersList.add(user)
    }
}

data class User(val username: String)