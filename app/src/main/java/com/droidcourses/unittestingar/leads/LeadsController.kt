package com.droidcourses.unittestingar.leads

class LeadsController {
    private val leadsSet = mutableSetOf<Lead>()

    fun addLead(lead: Lead) {
        if (isValidInput(lead.firstName) && isValidInput(lead.lastName) && isValidPhoneNumber(lead.phoneNumber))
            leadsSet.add(lead)
    }

    fun getLeads(): Set<Lead> {
     return  leadsSet
    }

    private fun isValidInput(input: String): Boolean {
        return  input.length >= 3
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.length == 11 && phoneNumber.all { char ->  char.isDigit() }
    }

}
