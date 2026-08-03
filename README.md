# Online Food Ordering App

A complete Android application built with modern Android development practices, using Kotlin, Jetpack Compose, Material 3, and MVVM Architecture.

## Features

- **Splash Screen**: Animated logo before launching.
- **Authentication**: Firebase Authentication (Email/Password) with Validation.
- **Home Screen**: Browse groceries fetched from a free API, search for items.
- **Food Details**: View details, rating, and add items to the cart.
- **Cart**: Manage cart items, adjust quantities, calculate totals with GST and delivery fees. Saved locally via Room Database.
- **Checkout**: Form for delivery address, select payment methods, place order.
- **Orders**: View order history saved in Firebase Firestore.
- **Profile**: View user information and logout.

## Architecture & Tech Stack

- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose & Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Network**: Retrofit & Gson (using `dummyjson.com/products/category/groceries`)
- **Image Loading**: Coil
- **Local Database**: Room
- **Cloud/Backend**: Firebase Authentication & Firestore
- **Asynchronous**: Coroutines & Flow
- **Build System**: Gradle Kotlin DSL (.kts) & Version Catalogs (libs.versions.toml)

## Folder Structure

```text
com.cognevance.foodapp
├── data
│   ├── api          # Retrofit interfaces
│   ├── model        # Data classes for API, Room, and Firestore
│   ├── repository   # Repository layer combining data sources
│   └── room         # Room database and DAOs
├── di               # Hilt Dependency Injection modules
├── firebase         # Firebase Auth and Firestore Managers
├── ui
│   ├── components   # Reusable Compose UI elements (Cards, Buttons, etc.)
│   ├── navigation   # AppNavigation (Navigation Compose)
│   ├── screens      # UI Screens (Splash, Auth, Home, Details, Cart, Checkout, Orders, Profile)
│   └── theme        # Material 3 Theme, Typography, Colors
├── viewmodel        # MVVM ViewModels
└── FoodApp.kt       # Application class (Hilt initialization)
```

## Installation & Firebase Setup

1. **Clone/Open the Repository**: Open the project folder (`FoodOrderingApp`) in the latest stable version of Android Studio.
2. **Setup Firebase**:
   - Go to the [Firebase Console](https://console.firebase.google.com/).
   - Create a new project (e.g. "Food Ordering App").
   - Add an Android App with the package name `com.cognevance.foodapp`.
   - Download your `google-services.json` file from the Firebase Console.
   - Place `google-services.json` in the `app/` directory of this project (`app/google-services.json`).
   - Enable **Email/Password Authentication** in Firebase Auth.
   - Enable **Cloud Firestore** and start in test mode (or update security rules for authenticated users).
3. **Sync Project**: Click "Sync Project with Gradle Files" in Android Studio.
4. **Run App**: Select an emulator or physical device and run the app.

## Screenshots

(Place your screenshots here)

## APK Generation Steps

1. In Android Studio, go to **Build** > **Generate Signed Bundle / APK**.
2. Select **APK**.
3. Create a new key store path or use an existing one, enter aliases and passwords.
4. Select **Release** build variant.
5. Click **Finish**. The APK will be generated in `app/release/`.

## Coding Standards

- Clean Architecture principles applied.
- Avoided deprecated APIs.
- Proper Kotlin Coroutines and Flow usage.
- Hilt used for strict Dependency Injection to prevent tight coupling.
