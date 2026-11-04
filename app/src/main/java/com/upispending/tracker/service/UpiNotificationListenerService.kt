package com.upispending.tracker.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.upispending.tracker.UpiTrackerApp
import com.upispending.tracker.parser.UpiParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpiNotificationListenerService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val TAG = "UpiNotificationListener"

        // Recognized UPI application package names
        private val UPI_PACKAGES = mapOf(
            "com.google.android.apps.nbu.paisa.user" to "Google Pay",
            "com.phonepe.app" to "PhonePe",
            "net.one97.paytm" to "Paytm",
            "in.org.npci.upiapp" to "BHIM",
            "com.dreamplug.androidapp" to "CRED",
            "in.amazon.mShop.android.shopping" to "Amazon Pay",
            "com.sbi.upi" to "BHIM SBI Pay",
            "com.csam.icici.bank.imobile" to "iMobile ICICI",
            "com.hdfcbank.payzapp" to "HDFC PayZapp"
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return

        val packageName = sbn.packageName
        val appName = UPI_PACKAGES[packageName] ?: return

        val extras = sbn.notification.extras
        val title = extras.getString("android.title") ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val bigText = extras.getCharSequence("android.bigText")?.toString() ?: ""

        val fullText = "$title $text $bigText".trim()
        Log.d(TAG, "Captured UPI notification from $appName: $fullText")

        val transaction = UpiParser.parse(fullText, appName)
        if (transaction != null) {
            serviceScope.launch {
                val db = (applicationContext as UpiTrackerApp).database
                val id = db.transactionDao().insert(transaction)
                Log.i(TAG, "Recorded $appName UPI transaction #$id: ₹${transaction.amount} to ${transaction.merchant}")
            }
        }
    }
}
