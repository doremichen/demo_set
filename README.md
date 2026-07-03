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

---

# 🧩 Feature Modules

## 🔐 Android Security (Encryption)

* **Device Administrator**: Demonstrates requesting elevated privileges to perform system-level tasks like immediate screen locking and credential confirmation via **DevicePolicyManager**.
* **Multi-level Database Encryption**:
    * **Standard (Field-level)**: Protects sensitive columns using **Android Keystore (AES-GCM)**.
    * **Military-grade (Full-DB)**: Encrypts the entire database file using **SQLCipher** integration with Room.
* **EncryptedSharedPreferences**: Secure Key-Value storage using **Jetpack Security** with hardware-backed master keys.
* **Biometric Authentication**: Modern fingerprint and face unlock integration using the **BiometricPrompt API** with fallback to device credentials.
* **Hacker View**: Visualization of raw encrypted ciphertext vs. safe decrypted views for security contrast.

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
* JobScheduler / JobService
* WorkManager (Modern background tasks)
* AlarmManager
* HandlerThread
* Modern Permission Model (ActivityResult API)
* System UI control
* Quick Settings Tile
* **Notification Management**: Advanced notification demos including intent handling and result callbacks.

---

## 🔗 IPC (Inter-Process Communication)

* Binder communication
* Messenger-based Service
* ContentProvider sharing
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

Planned upgrades to align with **Modern Android Development (MAD)** standards and architectural best practices:

*   **Dependency Injection (Hilt)**: Standardizing component lifecycles and simplifying dependency management (✅ Implemented in Bluetooth/BLE module).
*   **Clean Architecture Refactoring**: Introducing a formal **Domain Layer (Use Cases)** to further decouple business logic. (✅ Implemented in Bluetooth & JNI modules).
*   **Modularization Strategy**: Transitioning to a **Feature-based Modular Architecture** to demonstrate multi-module builds and encapsulated feature ownership.
*   **Automated Testing Suite**: Implementing a comprehensive testing strategy including **Screenshot Testing**, **Hilt-based Unit Tests**, and **Macrobenchmarks**.

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
