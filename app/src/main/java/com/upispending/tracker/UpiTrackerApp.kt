package com.upispending.tracker

import android.app.Application
import com.upispending.tracker.data.AppDatabase

class UpiTrackerApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }

    companion object {
        lateinit var instance: UpiTrackerApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
