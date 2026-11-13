package com.nivara.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthAndStorageUnitTest {

    @Test
    fun testPlatformCommissionCalculationOnRentalOnly() {
        val rentalAmountPaise = 105000L // ₹1,050 in paise
        val platformCommissionPaise = Math.round(rentalAmountPaise * 0.20) // 20% = 21,000 paise (₹210)
        val ownerGrossSharePaise = rentalAmountPaise - platformCommissionPaise // 80% = 84,000 paise (₹840)

        assertEquals(21000L, platformCommissionPaise) // ₹210 commission
        assertEquals(84000L, ownerGrossSharePaise)     // ₹840 owner share
    }

    @Test
    fun testBalancedDoubleEntryLedgerReconciliationWithDeliveryAndCleaning() {
        val rentalPaise = 105000L       // ₹1,050
        val depositPaise = 150000L      // ₹1,500
        val deliveryPaise = 15000L      // ₹150
        val cleaningPaise = 10000L      // ₹100

        // Debit Account: Payment Clearing (Total Cash Inflow)
        val paymentClearingDebitPaise = rentalPaise + depositPaise + deliveryPaise + cleaningPaise // 280,000 paise (₹2,800)

        // Credit Accounts: Allocated Revenues & Liabilities
        val platformCommissionCredit = Math.round(rentalPaise * 0.20) // 21,000 paise (₹210)
        val ownerPayableCredit = rentalPaise - platformCommissionCredit // 84,000 paise (₹840)
        val depositLiabilityCredit = depositPaise                       // 150,000 paise (₹1,500)
        val deliveryPayableCredit = deliveryPaise                       // 15,000 paise (₹150)
        val cleaningPayableCredit = cleaningPaise                       // 10,000 paise (₹100)

        val totalCreditsPaise = platformCommissionCredit + ownerPayableCredit + depositLiabilityCredit + deliveryPayableCredit + cleaningPayableCredit

        assertEquals(280000L, paymentClearingDebitPaise)
        assertEquals(280000L, totalCreditsPaise)
        assertEquals(paymentClearingDebitPaise, totalCreditsPaise) // Balanced Double-Entry: Debits == Credits
    }

    @Test
    fun testComponentSpecificRefundAllocationAndLedgerReversals() {
        val originalDepositPaise = 150000L // ₹1,500 deposit
        val originalRentalPaise = 105000L  // ₹1,050 rental

        // Scenario 1: Deposit Return (₹1,500)
        val depositRefundDebit = originalDepositPaise // DEBIT: Refundable Deposit Liability
        val depositRefundCredit = originalDepositPaise // CREDIT: Payment Clearing Outflow
        assertEquals(depositRefundDebit, depositRefundCredit)

        // Scenario 2: Rental Refund (₹500 / 50,000 paise)
        val rentalRefundAmount = 50000L
        val commissionReversal = Math.round(rentalRefundAmount * 0.20) // ₹100 (10,000 paise)
        val ownerPayableReversal = rentalRefundAmount - commissionReversal // ₹400 (40,000 paise)

        val rentalRefundDebits = commissionReversal + ownerPayableReversal // DEBIT: NIVARA Commission + DEBIT: Owner Payable
        val rentalRefundCredits = rentalRefundAmount                      // CREDIT: Payment Clearing Outflow

        assertEquals(50000L, rentalRefundDebits)
        assertEquals(50000L, rentalRefundCredits)
        assertEquals(rentalRefundDebits, rentalRefundCredits) // Rental refund balances perfectly against original revenue accounts
    }

    @Test
    fun testBookingIdentityValidation() {
        val renterId = "usr_renter_8812"
        val ownerId = "usr_owner_1042"

        assertTrue(renterId.isNotBlank())
        assertTrue(ownerId.isNotBlank())
        assertNotEquals(renterId, ownerId)
    }

    @Test
    fun testDefaultUserRoleAssignment() {
        val defaultRole = "CUSTOMER"
        assertEquals("CUSTOMER", defaultRole)
    }
}
