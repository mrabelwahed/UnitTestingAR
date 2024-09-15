package com.droidcourses.unittestingar.testdoubles

data class Dependency1(val value: Int)
data class Dependency2(val value: Int)
class MathService {
    fun add(a: Int, b: Int) = a + b
}

class Calculator(val dependency1: Dependency1, val dependency2: Dependency2) {
    fun add() = dependency1.value + dependency2.value
    fun subtract() = dependency1.value - dependency2.value
}

class CalculatorV2(val mathService: MathService) {
    fun add(n: Int) = mathService.add(n, 10)
}