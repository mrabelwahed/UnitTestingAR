package com.droidcourses.unittestingar.testdoubles

interface  PaymentService {
    fun processPayment(amount: Int): Boolean
}

class PaymentProcessor(val paymentService: PaymentService) {
    fun pay(amount: Int): Boolean {
        val result = paymentService.processPayment(amount)
        println("payment status: $result")
        return  result
    }
}