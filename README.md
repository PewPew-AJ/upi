# UPI Spend Tracker - Android Native App

An intelligent Android expense tracker that automatically captures, parses, and arranges your spending transactions across all UPI apps (**Google Pay, PhonePe, Paytm, CRED, BHIM, and Bank SMS**).

---

## 🌟 How It Works

1. **Notification Listener Service (`UpiNotificationListenerService`)**:
   - Detects instant payment notifications generated whenever you make a payment using any UPI app.
   - Extracts amount, merchant, UPI reference, and associates it with the specific app.

2. **Bank SMS Receiver (`BankSmsReceiver`)**:
   - Captures transactional SMS alerts from major Indian banks (SBI, HDFC, ICICI, Axis, Kotak, etc.) for debit confirmations.

3. **Intelligent Regex Parser (`UpiParser`)**:
   - Cleans the text, extracts the exact rupee figure, merchant name, and auto-categorizes it (Food & Dining, Groceries, Shopping, Travel, Bills, Entertainment).

4. **Local Room Database (`AppDatabase`)**:
   - 100% private and on-device. No financial data leaves your phone.

5. **Jetpack Compose UI**:
   - Clean, modern dashboard displaying total spendings, breakdown by UPI app, category filters, and chronological timeline.

---

## 🚀 How to Build the APK

### Option A: 1-Click Cloud Build via GitHub (No Android Studio required!)
1. Push this project folder to a free GitHub repository.
2. Go to the **Actions** tab in your repository.
3. The workflow `Build UPI Spend Tracker APK` will run automatically.
4. Once completed (approx. 2 minutes), download the ready-to-install `upi-spend-tracker-debug-apk` from the **Artifacts** section!

### Option B: Local Build via Android Studio
1. Open Android Studio (Hedgehog or newer).
2. Click **Open Project** and select this directory.
3. Allow Gradle to sync.
4. Click **Build** > **Build Bundle(s) / APK(s)** > **Build APK(s)**.
5. The generated `.apk` will be in `app/build/outputs/apk/debug/app-debug.apk`.
6. Transfer the `.apk` to your phone and install it!

---

## 🔒 Permissions & Setup
After installing the app on your phone:
1. Tap the **Notification icon** in the top bar to grant **Notification Listener Permission**.
2. Allow SMS permissions when prompted so the app can record bank confirmation messages.
