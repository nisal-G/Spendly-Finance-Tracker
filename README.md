# 💰 Spendly – Personal Finance Tracker

Spendly is an Android-based Personal Finance Tracker application developed for the IT2010 – Mobile Application Development (MAD) Lab Exam.

The application enables users to manage income and expenses, monitor budget limits, visualize financial data using charts, and securely store transaction history locally.

---

## 📱 Key Features

- ✅ Add Income & Expenses with Categories
- ✅ Budget Management System
- ✅ Budget Threshold Alerts (80% warning)
- ✅ Transaction Notifications
- ✅ Pie Chart Visualization (Income vs Expenses)
- ✅ Recent Transactions View
- ✅ Login System
- ✅ Data Backup & Restore (JSON)
- ✅ Bottom Navigation UI
- ✅ Local Data Storage using SharedPreferences

---

## 📊 Financial Visualization

Spendly uses **MPAndroidChart** to display:

- Expense Distribution by Category
- Income Distribution by Category
- Percentage-based Pie Charts
- Dynamic Data Updates

---

## 🔔 Notification System

The app generates:

- 📢 Transaction Notifications (Income / Expense added)
- ⚠️ Budget Alert Notification (When 80% of total budget is reached)

Notifications are also saved locally for viewing later.

---

## 💾 Data Storage

Spendly uses:

- **SharedPreferences** for:
  - Transactions
  - Budget Data
  - Login Status
  - Notifications

- **Gson** for:
  - JSON Data Backup
  - Restore Functionality

All data is stored locally on the device.

---

## 🛠 Tech Stack

- Kotlin
- Android SDK (Min SDK 24)
- SharedPreferences
- MPAndroidChart
- Gson
- Material Design Components
- RecyclerView
- BottomNavigationView

---

## 🧩 Application Modules

- 🔐 Authentication (Login)
- ➕ Add Transaction Module
- 📜 Transactions History
- 📊 Dashboard (Charts + Summary)
- 💰 Budget Update Module
- 🔔 Notifications Module
- 💾 Backup & Restore Module
- ⚙️ Settings Module

---

## 🚀 How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/nisal-G/Spendly-Finance-Tracker.git
