package com.upispending.tracker.parser

import com.upispending.tracker.data.TransactionEntity
import java.util.regex.Pattern

object UpiParser {

    private val AMOUNT_PATTERN = Pattern.compile(
        "(?:Rs\.?|INR|₹)\s*([0-9,]+(?:\.[0-9]{1,2})?)",
        Pattern.CASE_INSENSITIVE
    )

    private val MERCHANT_PATTERNS = listOf(
        Pattern.compile("(?:to|paid to|sent to|at|vpa)\s+([A-Za-z0-9\s&._-]+?)(?:\s+on|\s+ref|\s+upi|\s+avl|\s+bal|\.|,|$)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?:transferred to)\s+([A-Za-z0-9\s&._-]+)", Pattern.CASE_INSENSITIVE)
    )

    private val UPI_REF_PATTERN = Pattern.compile(
        "(?:UPI Ref(?: no)?|Ref no|Ref|Txn ID)[:\s]*([0-9A-Za-z]+)",
        Pattern.CASE_INSENSITIVE
    )

    fun parse(text: String, detectedApp: String = "Unknown"): TransactionEntity? {
        val lower = text.lowercase()

        // Check if debit or spending
        val isDebit = lower.contains("debited") || lower.contains("paid") ||
                lower.contains("sent") || lower.contains("spent") ||
                lower.contains("transferred")

        if (!isDebit && !lower.contains("upi")) {
            return null
        }

        // Extract amount
        val amountMatcher = AMOUNT_PATTERN.matcher(text)
        if (!amountMatcher.find()) {
            return null
        }

        val rawAmount = amountMatcher.group(1)?.replace(",", "") ?: return null
        val amount = rawAmount.toDoubleOrNull() ?: return null

        // Extract merchant/recipient
        var merchant = "UPI Payment"
        for (pattern in MERCHANT_PATTERNS) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val match = matcher.group(1)?.trim()
                if (!match.isNullOrEmpty() && match.length > 2 && !match.equals("upi", true)) {
                    merchant = match.take(30)
                    break
                }
            }
        }

        // Extract Ref ID
        var refId: String? = null
        val refMatcher = UPI_REF_PATTERN.matcher(text)
        if (refMatcher.find()) {
            refId = refMatcher.group(1)
        }

        val category = categorize(merchant, text)

        return TransactionEntity(
            amount = amount,
            merchant = merchant,
            upiApp = detectedApp,
            category = category,
            referenceId = refId,
            rawText = text,
            isDebit = isDebit
        )
    }

    fun categorize(merchant: String, text: String): String {
        val combined = "$merchant $text".lowercase()
        return when {
            combined.contains("swiggy") || combined.contains("zomato") ||
            combined.contains("restaurant") || combined.contains("cafe") ||
            combined.contains("tea") || combined.contains("chai") ||
            combined.contains("burger") || combined.contains("pizza") ||
            combined.contains("bistro") || combined.contains("food") -> "Food & Dining"

            combined.contains("blinkit") || combined.contains("zepto") ||
            combined.contains("instamart") || combined.contains("grocery") ||
            combined.contains("supermarket") || combined.contains("mart") ||
            combined.contains("kirana") -> "Groceries"

            combined.contains("uber") || combined.contains("ola") ||
            combined.contains("rapido") || combined.contains("metro") ||
            combined.contains("irctc") || combined.contains("fuel") ||
            combined.contains("petrol") || combined.contains("diesel") ||
            combined.contains("flight") -> "Travel & Transport"

            combined.contains("amazon") || combined.contains("flipkart") ||
            combined.contains("myntra") || combined.contains("ajio") ||
            combined.contains("store") || combined.contains("retail") -> "Shopping"

            combined.contains("electricity") || combined.contains("bill") ||
            combined.contains("recharge") || combined.contains("jio") ||
            combined.contains("airtel") || combined.contains("broadband") ||
            combined.contains("gas") || combined.contains("water") -> "Bills & Utilities"

            combined.contains("netflix") || combined.contains("prime") ||
            combined.contains("spotify") || combined.contains("bookmyshow") ||
            combined.contains("cinema") || combined.contains("hotstar") -> "Entertainment"

            else -> "General / Transfer"
        }
    }
}
