package com.droidcourses.unittestingar.testdoubles

interface  UserService {
    fun getUsersCount(): Int
}

class RealUserService: UserService {
    override fun getUsersCount() =  20

}

class PremiumUsersManager (val userService: UserService){
    fun getUsersCount() = userService.getUsersCount()
}