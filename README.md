# 6thSense Based Matching App Architecture

📲 [Try Now](https://rb.gy/htgz2s)

This repository demonstrates the modular architecture of the 6thSense Android application, showcasing a clean separation of concerns and modern Android development practices.

The implementation code has been removed to focus on structural design.

## Architecture

- **Feature-based Modularization**: Code is organized by business features to ensure scalability and maintainability.
- **Clean Architecture**: Strict separation between Presentation, Domain, and Data layers.
- **Repository Pattern**: Abstracted data sources for better testability and decoupled logic.
- **MVI (Model-View-Intent)**: Predictable state management across the UI components.

## Tech Stack

- **Kotlin**: 100% Kotlin-based development.
- **Jetpack Compose**: Modern, declarative UI toolkit.
- **Hilt (Dagger)**: Dependency Injection (as seen in `DIModule.kt`).
- **Firebase Suite**:
  - Authentication & Google Sign-In: User identity management.
  - Cloud Firestore: NoSQL real-time database.
  - Realtime Database: Live data synchronization.
  - Firebase Storage: Cloud storage for user media.
- **Coroutines & Flow**: Reactive programming and asynchronous task handling.
- **Google Play Billing**: Integrated in-app purchase and subscription management.

## Modules

Based on the dependency injection structure, the following feature modules are implemented:

- **activity**: Core activity tracking and logic.
- **auth**: Authentication workflows and Google Sign-In integration.
- **billing**: Subscription and payment processing.
- **contact**: Support and contact management.
- **conversations**: Chat list and conversation management.
- **discover**: User exploration and discovery features.
- **friends**: Social connections and friendship management.
- **home**: Main dashboard and landing experience.
- **invite**: Referral and invitation system.
- **matchhistory**: Past match tracking and statistics.
- **messaging**: Real-time messaging infrastructure.
- **onboarding**: Initial user setup and walk-through.
- **premium**: Premium feature access and logic.
- **profile**: User profile management and media uploads.
- **settings**: App configuration and user preferences.
- **similarity**: Core algorithm for user matching.
- **soulsync**: Specialized matching logic.

## Layers (Per Module)

Each module follows the Clean Architecture pattern:

- **presentation**: UI components (Compose) and ViewModels.
- **domain**: Business logic, Use Cases, and Repository Interfaces.
- **data**: Repository implementations, Data Sources (Firebase), and DTOs.
- **core**: Cross-cutting concerns such as Analytics, Base classes, and shared utilities.
- **common / firebase**: Centralized DI modules and shared infrastructure for Firebase services.
