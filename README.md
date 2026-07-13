# 📱 DemoSet – Android Architecture & System Lab

DemoSet is a comprehensive Android laboratory project that demonstrates **modern Android development**, **system-level APIs**, and **cross-layer architecture design**.

This project is designed not only as a collection of demos, but as an **architecture reference** for real-world Android applications.

---

# 🚀 Project Goals

* Demonstrate **Android system components**
* Explore **modern architecture (MVVM, Coroutine)**
* Showcase **IPC & JNI integration**
* Provide reusable patterns for **production-ready apps**

---

# 🧠 Architecture Overview

This project follows a layered design inspired by **Clean Architecture + MVVM**:

```
UI Layer
 ├── Activity / Fragment (ViewBinding / DataBinding)
 ├── Adapter (RecyclerView + ListAdapter + DiffUtil)
 │
ViewModel Layer
 ├── Hilt @HiltViewModel
 ├── LiveData / State handling / Flows
 │
Domain Layer
 ├── Use Cases (Business Logic)
 │
Data / Repository Layer
 ├── Repository Pattern (Google Hilt @Singleton)
 ├── Room / DataStore / SQLite
 ├── Service / WorkManager / BroadcastReceiver
 │
Native Layer
 ├── JNI / C++
```

# 🧩 Feature Modules

## 📊 System Monitor (Modularization Strategy)

* **Feature-based Modularization**: A standalone Android Library module (`:features:sysmonitor`) demonstrating how to decouple features from the main app.
* **Full Clean Architecture**:
    * **Domain Layer**: Contains pure business logic and contracts (`UseCase`, `Repository Interface`).
    * **Data Layer**: Direct interaction with Android System APIs (`BatteryManager`, `StatFs`, `ActivityManager`).
    * **Presentation Layer**: Reactive UI using **DataBinding** and **HiltViewModel**.
* **System Insights**: Real-time monitoring of **Battery Level**, **Internal Storage**, and **Memory Utilization**.
* **Modern UI/UX**:
    * **FitsSystemWindows**: Full support for edge-to-edge display and system bar insets.
    * **Material 3 Design**: Card-based layout with elevation and real-time progress indicators.
    * **I18n & Dark Mode**: Full support for English/Traditional Chinese and adaptive system themes.

## 🔐 Android Security (Encryption)

* **Device Administrator**: Demonstrates requesting elevated privileges to perform system-level tasks like immediate screen locking and credential confirmation via **DevicePolicyManager**.
* **Multi-level Database Encryption**:
    * **Standard (Field-level)**: Protects sensitive columns using **Android Keystore (AES-GCM)**.
    * **Military-grade (Full-DB)**: Encrypts the entire database file using **SQLCipher** integration with Room.
* **EncryptedSharedPreferences**: Secure Key-Value storage using **Jetpack Security** with hardware-backed master keys.
* **Biometric Authentication**: Modern fingerprint and face unlock integration using the **BiometricPrompt API** with fallback to device credentials.
* **Hacker View**: Visualization of raw encrypted ciphertext vs. safe decrypted views for security contrast.

## 🛡️ Privacy & Screen Capture (Android 14/15)

*   **Advanced Privacy Safeguards**: A comprehensive system to detect when the screen is being captured or recorded.
*   **Version-Aware Strategy Pattern**:
    *   **Android 14 (API 34)**: Utilizes `ScreenCaptureCallback` primarily for screenshot detection.
    *   **Android 15 (API 35)**: Integrates the new `ScreenRecordingCallback` for precise video recording state monitoring.
*   **Security Enforcement**: Integrated with **FLAG_SECURE** to dynamically protect sensitive UI components from appearing in captures.
*   **Reactive Feedback**: Real-time UI updates using **Data Binding** and **Hilt** to notify users of active recording sessions.

---

## 🔹 UI Demos

* **Jetpack Compose (Advanced)**: Full implementation of declarative UI with:
    * **Material 3 & Dynamic Color**: Interface automatically adapts to the system's color scheme (Android 12+).
    * **Adaptive Layouts**: Responsive architecture using `BoxWithConstraints` that dynamically switches between mobile (single column) and tablet/foldable (split-pane) layouts.
    * **UDF (Unidirectional Data Flow)**: Strict state management using ViewModels and StateFlow.
* **Modern Flow Lab**: A dedicated environment for **Kotlin Coroutines & Flow**:
    * **StateFlow**: Used for persistent UI state.
    * **SharedFlow**: Handling "Hot" streams for one-time events like Toasts or Navigation.
    * **Lifecycle-aware Collection**: Utilizing `repeatOnLifecycle` for safe data consumption.
* **Advanced Graphics (Strategy Pattern)**: A unified graphics engine demonstrating the transition from 2D Canvas to 3D hardware acceleration:
    * **2D Canvas**: Real-time path animations, dynamic shaders (`LinearGradient`), and mathematical transformations (Sine waves & Spirals).
    * **3D OpenGL ES**: Hardware-accelerated rendering using **OpenGL ES 3.0**, featuring programmable pipelines and rotating primitive geometries.
* DataBinding / ViewBinding
* Animation & GIF rendering
* Floating Dialog / Floating Window
* Table Layout rendering
* Paging 3

---

## ⚙️ System & Background Execution

* **Dynamic Delivery**: Exploration of Split Install & Dynamic Feature Modules.
* Started / Bound Service
* WorkManager (Modern background tasks)
* AlarmManager
* HandlerThread
* Modern Permission Model (ActivityResult API)
* System UI control
* Quick Settings Tile
* **Notification Management**: Advanced notification demos including intent handling and result callbacks.
* **Background Execution Evolution (Android 15)**:
    *   **Legacy vs. Modern Comparison**: Side-by-side demonstration of traditional **Foreground Services (FGS)** vs. the new **User-initiated Data Transfer Jobs**.
    *   **Scenario-based Execution**:
        *   **User-initiated Job**: Immediate high-priority data transfer with system-managed notifications.
        *   **Deferred Maintenance Job**: Intelligent scheduling based on constraints like **Idle**, **Charging**, and **Wi-Fi** availability.
    *   **Safety & Optimization**: Automatic validation to prevent illegal constraint combinations (e.g., UI jobs cannot have Idle constraints).

---

## 🔗 IPC (Inter-Process Communication)

* Binder communication
* Messenger-based Service
* ContentProvider sharing
* ShareActionProvider (Clean Architecture refactor)
* JNI (Java ↔ Native bridge)

---

## 🤖 AI / Machine Learning

* **Google ML Kit Vision**:
    * **Barcode Scanning**: Real-time multi-format barcode detection.
    * **Face Detection**: Fast on-device face presence tracking.
    * **Text Recognition**: Latin-based OCR for real-time text extraction.
* **TensorFlow Lite**:
    * Image Classification using the TFLite Task Library.
    * Full MVVM integration for asynchronous model inference.

---

## 📂 Storage
* File Explorer (Scoped Storage ready)
* SQLite (ContentProvider)
* Room Database
* DataStore

---

## 🚀 Performance & Diagnostics

*   **LeakCanary Integration**: In-app memory leak detection and analysis. Demonstrates how to catch leaks in static references and custom objects.
*   **App Startup Optimization**: Contrast between Synchronous Blocking and Asynchronous Coroutine-based initialization using **Jetpack Startup** concepts.
*   **Benchmarking**:
    *   **Micro-benchmark**: Algorithmic performance measurement (e.g., Bubble Sort vs. Quick Sort simulation).
    *   **Macro-benchmark**: Real-time **Frame Metrics** monitoring to detect UI jank and calculate drop-frame rates.

---

## 📡 Network

* Wi-Fi management
* Retrofit + Coroutine (REST API)

---

## 🔌 Hardware Integration

* Bluetooth (Classic)
* BLE (Low Energy)
* Camera2 API
* Video Recording
* Flashlight control
* CameraX API (Modern lifecycle-aware)
* **USB Host / Mass Storage (OTG)**: File management on external USB drives using `libaums`.

---

# 🔥 Highlight: Modularization Strategy vs. Dynamic Feature

The project demonstrates two distinct modularization approaches to handle scale and delivery requirements:

| Feature | **System Monitor** (`:features:sysmonitor`) | **Dynamic Feature** (`:features:dynamic_feature`) |
| :--- | :--- | :--- |
| **Plugin Type** | `com.android.library` (Android Library) | `com.android.dynamic-feature` |
| **Delivery** | **Static Integration**: Compiled into the main APK/Bundle. Available immediately upon installation. | **On-Demand**: Downloaded from Play Store at runtime. Reduces initial APK size. |
| **Dependency** | `:app` depends on `:features:sysmonitor`. | `:features:dynamic_feature` depends on `:app` (Reverse). |
| **Primary Goal** | Compilation speed, code visibility, and architectural decoupling (Lego-brick approach). | Storage optimization and modular feature roll-out. |
| **Tech Stack** | Java, Hilt, Clean Architecture, UseCases. | Kotlin, Play Core SplitCompat, AAB. |

---

# 🛠️ Gradle Configuration Notes

To ensure proper integration of these modular strategies, specific Gradle configurations are required:

### 1. Static Modularization (`:features:sysmonitor`)
* **`app/build.gradle`**: Must explicitly declare a dependency on the library module.
  ```gradle
  dependencies {
      implementation project(':features:sysmonitor')
  }
  ```
* **`features:sysmonitor/build.gradle`**: Uses the standard library plugin.
  ```gradle
  plugins {
      id 'com.android.library'
  }
  ```

### 2. Dynamic Delivery (`:features:dynamic_feature`)
* **`app/build.gradle`**: Must register the dynamic feature module using the `dynamicFeatures` property.
  ```gradle
  android {
      // ...
      dynamicFeatures = [':features:dynamic_feature']
  }
  ```
* **`features:dynamic_feature/build.gradle`**: Uses the specialized dynamic-feature plugin and **must depend on the app module** (Dependency Inversion).
  ```gradle
  plugins {
      id 'com.android.dynamic-feature'
  }
  dependencies {
      implementation project(':app')
  }
  ```

---

# 🔥 Highlight: Modularization Strategy & System Monitor

The System Monitor module serves as the primary demonstration of the **Modularization Strategy** for scaling large Android projects:

*   **Static Library Integration**: Demonstrates the "Lego-brick" approach where features are developed in isolation as independent modules (`com.android.library`) and statically integrated into the `:app` shell at compile time.
*   **Encapsulated Logic (DIP)**: The UI layer depends only on the `Domain` layer interfaces. The concrete implementation is hidden in the `Data` layer and provided via **Hilt Dependency Injection**, preventing leaking system API details into the ViewModel.
*   **UseCase Pattern**: Business logic is abstracted into `GetSystemStatusUseCase`, ensuring the ViewModel remains a thin coordinator between the Domain and UI.
*   **Zero Magic Numbers**: Clean coding standards with named constants for all system intervals, conversion factors, and data formats.
*   **Robust Resource Handling**: Carefully handled `ColorStateList` and `Theme` attributes to prevent inflation crashes across different OEM Android skins (e.g., Samsung OneUI).

---

# 🔥 Highlight: Modern Bluetooth & BLE Architecture

The Bluetooth module has been fully refactored using **Clean Architecture** and **Modern Android Development (MAD)** principles:

*   **Hilt Dependency Injection**: Standardized lifecycle management for `BluetoothRepository`, `ViewModel`, and `BroadcastReceiver`.
*   **MVVM + Data Binding**: Complete decoupling of UI from business logic. Optimized list rendering using `RecyclerView` with `ListAdapter` and `DiffUtil` for smooth UI updates.
*   **State-Driven Design**: Uses a centralized `BtState` enum to manage device lifecycle (Scanning, Connecting, etc.) reactively via LiveData.
*   **Law of Demeter (LoD)**: Strict encapsulation where the UI only interacts with the ViewModel, preventing "Dot Hell" and complex dependency chains.
*   **Security & Stability**: 
    *   **Permission Safety**: Robust runtime permission handling for Android 12+ (S).
    *   **Memory Leak Prevention**: Centralized `ScanCallback` management in Repository (Singleton) to prevent Activity-leak scenarios.

---

# 🔥 Highlight: ML Kit & Strategy Pattern

The ML Kit module showcases an optimized **State-Driven Strategy Pattern** integrated with **CameraX**:

* **Consolidated Enum Strategy**: Detection logic is encapsulated within a single `VisionDetectionMode` enum, ensuring tight coupling between the mode and its processing logic.
* **CameraX Analyzer Integration**: Uses `ImageAnalysis.Analyzer` for non-blocking, real-time frame processing.
* **Adaptive Lens Switching**: Automatically flips between **Front (Selfie) Camera** for Face Detection and **Rear Camera** for Barcode/Text scanning.
* **Dynamic Lifecycle Re-binding**: Real-time camera hardware re-configuration handled by the ViewModel without activity restarts.
* **Multi-language Support**: Logic returns `R.string` IDs, ensuring all UI text is resolved through the Android localization system.

---

# 🔥 Highlight: System UI & Immersive Mode Architecture

The System UI demo has been refactored to demonstrate **Clean Architecture** and **Modern Android Development** practices:

*   **WindowInsetsControllerCompat**: Utilizes the modern `WindowInsetsControllerCompat` API for managing system bar visibility and appearance, ensuring compatibility across different Android versions.
*   **MVVM + Data Binding**: Decouples UI logic from the Activity. The `SystemUIViewModel` manages the UI state, while Data Binding reactively updates the view components.
*   **Clean Architecture (Domain Layer)**: Business logic for toggling system modes is encapsulated in the `ToggleSystemUIModeUseCase`, making it platform-independent and testable.
*   **Edge-to-Edge Integration**: Demonstrates how to handle system bar insets properly using `ViewCompat.setOnApplyWindowInsetsListener` to prevent UI elements from being obscured by system bars.

---

# 🔥 Highlight: JNI Architecture Demo

The JNI module demonstrates a full **Clean Architecture** implementation for cross-layer communication:

* **Domain-Driven Design**: Business logic is encapsulated in the `JniUseCase` enumeration, ensuring single-responsibility and easy extension.
* **GRASP Principles**: Implements **Creator** and **Information Expert** patterns by allowing the Use Case layer to manage Repository lifecycles.
* **Native ↔ Java Interop**:
    * Static vs Instance field access via JNI reflection.
    * Parameterized calculations performed in C++.
    * System property retrieval (`ro.product.cpu.abi`) from the Native layer.
* **Architecture Flow**:

```
JNI (Native) → Data (NativeUtils/Repository) → Domain (Use Cases) → UI (ViewModel)
```

This ensures a **clean separation of concerns**, making the JNI logic testable and independent of the Android UI framework.

---

# 🔥 Highlight: Security & Data Protection

The Security module demonstrates enterprise-level data protection strategies:

* **Hardware Isolation**: Secrets are never accessible to the app code; encryption is handled by the **TEE (Trusted Execution Environment)**.
* **Layered Defense**: Combining SQLCipher for file-level protection and Keystore for logic-level protection.
* **MVVM + DataBinding**: Clean handling of encrypted state transitions without bloating the UI code.

---

# 🔥 Highlight: Advanced Compose & Modern Flow

The latest updates demonstrate a complete transition to **Modern Android Development (MAD)**:

*   **Responsive Engine**: The Compose UI detects device posture and screen size. On wide-screen devices (Tablets/Foldables), the UI automatically restructures from a vertical stack to a **multi-column Dashboard** for enhanced productivity.
*   **Reactive Pipeline**: Leveraging the power of `Flow`, the app maintains a "Single Source of Truth" in the ViewModel. UI components react to state changes with zero-latency, while `SharedFlow` ensures side-effects (like Toasts) are only processed when the UI is in the foreground.
*   **Micro-animations**: Instead of static assets, the UI uses **Compose Canvas** and **Animation APIs** to create light-weight, high-performance visual feedback (e.g., the pulsating service status indicators).

---

# 🔥 Highlight: Share Provider & Clean Architecture

The Share Provider demo showcases a modern implementation of the Android sharing mechanism integrated with **Clean Architecture**:

*   **UseCase-Driven Sharing**: Sharing logic is encapsulated in `GetShareIntentUseCase`, decoupling the intent construction (MIME types, flags) from the UI layer.
*   **ViewModel as UI Controller**: The Activity is stripped of business logic, delegating the management of `ShareActionProvider` directly to the `ShareViewModel`.
*   **Reactive Intent Updates**: Uses `MediatorLiveData` (or internal observers) to reactively regenerate the sharing `Intent` as the user types text or toggles content types (Text vs. Image).
*   **Secure File Sharing**: Demonstrates the use of **FileProvider** to safely share internal app files (from `cacheDir`) with external applications, adhering to Android's strict security model.
*   **Data Binding & I18n**: Fully utilizes Data Binding for real-time UI previews and supports multi-language (English/Traditional Chinese) localized strings.

---

# 🏗️ Design Principles

* **Separation of Concerns**
* **Dependency Flow Control (DIP)**
* **MVVM Pattern**
* **Reactive UI Updates**
* **System-level abstraction**

---

# 🧪 Use Cases Covered

* Background task scheduling
* Cross-process communication
* Hardware interaction
* Native performance integration
* **Advanced Data Persistence & Encryption**
---

# 🛠️ Tech Stack

* Java / Kotlin
* Android SDK / Jetpack
* **Google Hilt (Dependency Injection)**
* ViewBinding / **DataBinding**
* LiveData / ViewModel
* **Room / SQLCipher**
* **Jetpack Security (EncryptedSP)**
* **libaums** (USB Mass Storage)
* WorkManager
* Retrofit
* JNI (C/C++)

---

# 📌 Future Roadmap

Planned upgrades to align with **Modern Android Development (MAD)** standards and architectural best practices (Targeting Android 15, 16, and 17):

*   **On-Device Generative AI (AICore)**: Integrating **Gemini Nano** via the Google AI Edge SDK for localized text summarization and smart replies without internet dependency.
*   **Privacy Space & Sandbox Awareness**: Demonstrating app behavior adaptation for Android 15's **Private Space**, ensuring sensitive data is correctly isolated when the profile is locked.
*   **Predictive Back Gestures (Modern UI)**: Implementing the new system back navigation pattern for Android 14+ with custom cross-activity and cross-fragment animations.
*   **Edge-to-Edge Layout (Phase II)**: Full migration to the mandatory Edge-to-Edge display standard required by Android 15.
*   **Unified Health Data (Health Connect)**: Migrating fragmented health/fitness sensor data tracking to the centralized **Health Connect API** as the industry standard.
*   **Advanced Privacy Safeguards**:
    *   **Screen Recording Detection**: ✅ Implemented using Strategy Pattern for Android 14 and 15 (API 35).
    *   **Photo Picker (Cloud Integration)**: Moving away from `READ_EXTERNAL_STORAGE` to the privacy-centric **Photo Picker** with support for cloud-provider media.
*   **Modern Background Execution**: ✅ Refactored legacy Foreground Services to use **User-initiated data transfer jobs** and updated **JobScheduler** constraints for Android 15.
*   **Satellite Connectivity Lab**: Exploring the **Non-Terrestrial Network (NTN)** APIs for satellite messaging detection and status monitoring.
*   **Automated Testing Suite**: Implementing a comprehensive testing strategy including **Screenshot Testing**, **Hilt-based Unit Tests**, and **Macrobenchmarks**.

---

# 💉 Dependency Injection with Hilt

This project utilizes **Google Hilt** for standardized dependency injection across all modules. This ensures better modularity, easier testing, and lifecycle-aware dependency management.

### 🏗️ Hilt Architecture & Workflow

1.  **Application Entry Point**: The app is initialized with `@HiltAndroidApp` in `DemoApplication.java`. This triggers Hilt's code generation and creates the `SingletonComponent`.
2.  **Entry Points**: Activities and Fragments are annotated with `@AndroidEntryPoint` to enable member injection.
3.  **ViewModel Injection**: ViewModels use `@HiltViewModel` and `@Inject` constructors, allowing Hilt to provide dependencies like `UseCases` or `Repositories` automatically.
4.  **Clean Architecture Flow**:
    *   **UI (Activity/Fragment)** → Injects **ViewModel**.
    *   **ViewModel** → Injects **UseCases**.
    *   **UseCase** → Injects **Repository (Interface)**.
    *   **Module** → Binds **Repository (Interface)** to **Repository (Implementation)**.

### 🧩 Hilt Modules & Binding Strategies

#### `@Binds` vs `@Provides`: Choosing the Right Tool

| Feature | **`@Binds`** | **`@Provides`** |
| :--- | :--- | :--- |
| **Logic** | **Zero Logic**: Simply maps an implementation to an interface. | **Complex Logic**: Can include initialization, conditional logic, or builder patterns. |
| **Class Source** | **Internal**: Use it for classes you wrote and can annotate with `@Inject`. | **External/Complex**: Necessary for 3rd-party libraries (e.g., Retrofit, Room) or objects requiring custom setup. |
| **Performance** | **Higher**: Dagger generates less code (no factory class for the binding). | **Standard**: Dagger generates a factory class to execute the provider method. |
| **Definition** | An `abstract` method in an `interface` or `abstract class`. | A concrete `static` (preferred) or instance method. |

### 🛠️ Development Techniques & Best Practices

*   **Constructor Injection**: Always prefer `@Inject constructor(...)` over member injection. It makes dependencies explicit and ensures the class is always in a valid state.
*   **Interface Binding**: Use `@Binds` in an `abstract` module to map interfaces (Domain Layer) to implementations (Data Layer). This keeps the Domain Layer pure and independent of Android-specific implementation details.
*   **Scoping for Single Source of Truth**:
    *   Use `@Singleton` for Repositories and Database instances to maintain a single state across the app.
    *   Use `@ActivityRetainedScoped` for components that should survive configuration changes but not the entire app lifecycle.
*   **Qualifiers for Multiple Implementations**: When providing multiple instances of the same type (e.g., different OkHttp interceptors), use custom `@Qualifier` annotations to distinguish them.
*   **Memory Leak Prevention**: While Hilt manages lifecycles, always manually unregister listeners or observers in `onCleared()` (ViewModels) or `onDestroy()` (Activities) if they are held by long-lived `@Singleton` objects.
*   **Testing**: Leverage `HiltAndroidRule` in instrumented tests to swap production modules with test doubles using `@TestInstallIn`.

---

# 👨‍💻 Author

Adam Chen

---

# ⭐ Notes

This project is intended for:

* Android developers learning system internals
* Engineers preparing for technical interviews
* Teams looking for architecture reference

---

# 📄 License
This project is licensed under the MIT License
