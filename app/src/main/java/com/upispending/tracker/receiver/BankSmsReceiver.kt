package com.upispending.tracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.upispending.tracker.UpiTrackerApp
import com.upispending.tracker.parser.UpiParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BankSmsReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val TAG = "BankSmsReceiver"

        // Common Indian bank SMS sender identifiers (e.g., AD-HDFCBK, VK-SBIINB)
        private val BANK_PREFIXES = listOf(
            "HDFC", "SBI", "ICICI", "AXIS", "KOTAK", "PNB", "BOB", "INDUS", "YESBK", "IDFC"
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return

        for (sms in messages) {
            val sender = sms.originatingAddress?.uppercase() ?: ""
            val body = sms.messageBody ?: continue

            val isBank = BANK_PREFIXES.any { sender.contains(it) }
            val isUpiRelated = body.contains("UPI", ignoreCase = true) ||
                    body.contains("VPA", ignoreCase = true)

            if (isBank || isUpiRelated) {
                Log.d(TAG, "Processing Bank UPI SMS from $sender: $body")
                val transaction = UpiParser.parse(body, "Bank SMS ($sender)")
                if (transaction != null) {
                    scope.launch {
                        val db = (context.applicationContext as UpiTrackerApp).database
                        db.transactionDao().insert(transaction)
                    }
                }
            }
        }
    }
}
