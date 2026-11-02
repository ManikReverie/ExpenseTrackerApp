# Personal Expense Tracker App

## Project Overview
A comprehensive Android app developed using Kotlin and XML for the Veda Technology Android Development Track. It allows users to record daily expenses, categorize spending, view total expenses, and monitor their spending.

## Technologies Used
- Kotlin
- XML Layouts
- Room Database
- MVVM Architecture (ViewModel & LiveData)
- RecyclerView
- Material Design

## Features
- **Dynamic Expense Calculation**: Automatically computes and displays total spending across all recorded transactions.
- **Category Tracking**: Categorize expenses (Food & Dining, Transportation, Shopping, Bills & Utilities, Entertainment, Health & Medical, etc.) using an interactive dropdown selector.
- **Add, Edit, and Delete Transactions**: Tap the Floating Action Button (+) to add a new expense, or tap any existing item in the list to update its details or remove it.
- **Room Database Persistence**: Robust offline-first SQLite persistence using Room Entities, DAO, and Coroutines.
- **MVVM Architecture**: Clean separation of data, business logic, and presentation using Android Architecture Components (`ViewModel` and `LiveData`).
- **Modern UI & UX**: Material 3 theming, styled Material CardViews, responsive CoordinatorLayout, and empty state feedback.

## Project Structure
```
ExpenseTrackerApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/expensetrackerapp/
│   │   │   ├── data/
│   │   │   │   ├── AppDatabase.kt       # Room Database class with singleton instance
│   │   │   │   ├── Expense.kt           # Room Entity data class
│   │   │   │   └── ExpenseDao.kt         # Data Access Object with CRUD queries
│   │   │   ├── AddExpenseActivity.kt    # Activity for adding/editing expenses
│   │   │   ├── ExpenseAdapter.kt        # RecyclerView Adapter with click listener
│   │   │   ├── ExpenseViewModel.kt      # ViewModel providing LiveData and coroutine ops
│   │   │   └── MainActivity.kt          # Dashboard displaying total and expense list
│   │   └── res/
│   │       ├── layout/
│   │       │   ├── activity_add_expense.xml  # Add/Edit form layout
│   │       │   ├── activity_main.xml         # Main screen layout
│   │       │   └── item_expense.xml          # RecyclerView item card layout
│   │       └── values/
│   │           ├── colors.xml           # Color palette
│   │           ├── strings.xml          # String resources & category arrays
│   │           └── themes.xml           # Application theme
│   └── build.gradle.kts                 # App-level build script with Room & Lifecycle
├── build.gradle.kts                     # Project-level build script with KSP plugin
└── README.md
```

## How to Run
1. Open the project folder in **Android Studio**.
2. Let Gradle sync and download dependencies.
3. Choose an Android Emulator or connected physical device running Android 7.0 (API 24) or higher.
4. Click **Run** (`Shift + F10`) to build and launch the app.

