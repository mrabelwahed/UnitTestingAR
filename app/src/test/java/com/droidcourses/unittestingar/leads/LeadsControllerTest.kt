package com.droidcourses.unittestingar.leads

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class LeadsControllerTest {
    // RED -- GREEN -- REFACTOR
    private var controller: LeadsController? = null

    @Before
    fun setup() {
        controller = LeadsController()
        println("setup ..... called")
    }

    @Test
    fun `Given valid lead  when call addLead() then we should expect leads list has on item`() {
        val lead = Lead("Mahmoud", "Ramadan", "11111111111")
        controller?.addLead(lead)
        assertEquals(1, controller?.getLeads()?.size)
    }

    @Test
    fun `Given lead with two chars in  first name   when call addLead() then we should expect that leads is empty`() {
        val lead = Lead("fd", "ramadan", "1111111")
        controller?.addLead(lead)
        assertTrue(controller?.getLeads()?.isEmpty() ?: false)
    }

    @Test
    fun `Given lead with two chars in  last name   when call addLead() then we should expect that leads is empty`() {
        val lead = Lead("mahmoud", "", "1111111")
        controller?.addLead(lead)
        assertTrue(controller?.getLeads()?.isEmpty() ?: false)
    }

    @Test
    fun `Given lead with not valid phone number  when call addLead() then we should expect that leads is empty`() {
        val lead = Lead("mahmoud", "ramadan", "1111111")
        controller?.addLead(lead)
        assertTrue(controller?.getLeads()?.isEmpty() ?: false)
    }

    @Test
    fun `Given lead with not valid phone number with 12 digits  when call addLead() then we should expect that leads is empty`() {
        val lead = Lead("mahmoud", "ramadan", "111111111111")
        controller?.addLead(lead)
        assertTrue(controller?.getLeads()?.isEmpty() ?: false)
    }

    @Test
    fun `Given lead with  valid phone number with 1 digits  when call addLead() then we should expect that leads has one item`() {
        val lead = Lead("mahmoud", "ramadan", "11111111111")
        controller?.addLead(lead)
        assertEquals(1, controller?.getLeads()?.size)
    }

    @Test
    fun `Given lead with  phone number with 11 chars  when call addLead() then we should expect that leads are empty`() {
        val lead = Lead("mahmoud", "ramadan", "aaaaaaaaaaa")
        controller?.addLead(lead)
        assertTrue(controller?.getLeads()?.isEmpty() ?: false)
    }

    @Test
    fun `Given lead with duplicated leads when call addLead() then we should expect that leads length is one item`() {
        val lead = Lead("mahmoud", "ramadan", "11111111111")
        controller?.addLead(lead)
        controller?.addLead(lead)
        assertEquals(1, controller?.getLeads()?.size)
    }

    @Test
    fun `Given  duplicated lead   when call addLead() then we should expect that leads list size is 1 `() {
        val lead1 = Lead("mahmoud", "ramadan", "11111111111")
        val lead2 = Lead("mahmoud", "ramadan", "11111111111")
        controller?.addLead(lead1)
        controller?.addLead(lead2)
        assertEquals(1, controller?.getLeads()?.size)
    }

    @Ignore("ignore this test for now")
    @Test
    fun `Given lead with 5 number phone digits  when call addLead() then we should expect that leads is empty`() {
        val lead = Lead("mahmoud", "ramadan", "11111")
        controller?.addLead(lead)
        assertTrue(controller?.getLeads()?.isEmpty() ?: false)
    }

//
//    @Test(expected = IllegalArgumentException::class)
//    fun `Given lead with 11 number phone chars  when call addLead() then we should expect that leads is empty`(){
//        val lead = Lead("mahmoud","ramadan","aaaaaaaaaaa")
//        controller?.addLead(lead)
//        assertTrue(controller?.getLeads()?.isEmpty() ?: false)
//    }

    @After
    fun close() {
        println("close ..... called")
        controller = null
    }
}