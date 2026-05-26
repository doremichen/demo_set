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

This project follows a layered design inspired by **MVVM + System Architecture**:

```
UI Layer
 ├── Activity / Fragment
 ├── ViewBinding / DataBinding
 │
ViewModel Layer
 ├── LiveData / State handling / Flows
 │
Domain / Repository Layer
 ├── Repository Pattern (Room, DataStore)
 ├── Service / WorkManager / Binder
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

* Material Design Components
* DataBinding / ViewBinding
* Animation & GIF rendering
* Floating Dialog / Floating Window
* Table Layout rendering
* Compose UI
* StateFlow / SharedFlow
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

# 🔥 Highlight: ML Kit & Strategy Pattern

The ML Kit module showcases an optimized **State-Driven Strategy Pattern** integrated with **CameraX**:

* **Consolidated Enum Strategy**: Detection logic is encapsulated within a single `VisionDetectionMode` enum, ensuring tight coupling between the mode and its processing logic.
* **CameraX Analyzer Integration**: Uses `ImageAnalysis.Analyzer` for non-blocking, real-time frame processing.
* **Adaptive Lens Switching**: Automatically flips between **Front (Selfie) Camera** for Face Detection and **Rear Camera** for Barcode/Text scanning.
* **Dynamic Lifecycle Re-binding**: Real-time camera hardware re-configuration handled by the ViewModel without activity restarts.
* **Multi-language Support**: Logic returns `R.string` IDs, ensuring all UI text is resolved through the Android localization system.

---

# 🔥 Highlight: JNI Architecture Demo

The JNI module demonstrates:

* Native ↔ Java method calls
* Static vs Instance field access
* Callback from C++ to Java
* Integration with **MVVM architecture**

```
JNI → NativeUtils → ViewModel → LiveData → UI
```

This ensures a **clean separation of concerns** and avoids tight coupling with Activity.

---

# 🔥 Highlight: Security & Data Protection

The Security module demonstrates enterprise-level data protection strategies:

* **Hardware Isolation**: Secrets are never accessible to the app code; encryption is handled by the **TEE (Trusted Execution Environment)**.
* **Layered Defense**: Combining SQLCipher for file-level protection and Keystore for logic-level protection.
* **MVVM + DataBinding**: Clean handling of encrypted state transitions without bloating the UI code.

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

Planned upgrades:
* **Performance & Diagnostics**: Benchmarking, App Startup optimization, and LeakCanary integration.
* **Advanced Graphics**: Rendering Engine with Vulkan or advanced Canvas 2D effects.

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
