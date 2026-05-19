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
 ├── LiveData / State handling
 │
Domain / System Layer
 ├── Service / WorkManager / Binder
 │
Native Layer
 ├── JNI / C++
```

---

# 🧩 Feature Modules

## 🔐 Android Security (Encryption)

* **Multi-level Database Encryption**:
    * **Standard (Field-level)**: Protects sensitive columns using **Android Keystore (AES-GCM)**.
    * **Military-grade (Full-DB)**: Encrypts the entire database file using **SQLCipher** integration with Room.
* **EncryptedSharedPreferences**: Secure Key-Value storage using **Jetpack Security** with hardware-backed master keys.
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

* Started / Bound Service
* JobScheduler / JobService
* WorkManager (Modern background tasks)
* AlarmManager
* HandlerThread
* Modern Permission Model (ActivityResult API)
* System UI control
* Quick Settings Tile
* Device Admin

---

## 🔗 IPC (Inter-Process Communication)

* Binder communication
* Messenger-based Service
* ContentProvider sharing
* JNI (Java ↔ Native bridge)

---

## 🤖 AI / Machine Learning

* TensorFlow Lite (Image Classification)
* MVVM integration with TFLite Task Library

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
* WorkManager
* Retrofit
* JNI (C/C++)

---

# 📌 Future Roadmap

Planned upgrades:
* **On-Device Search & Vision**: Integration with Google ML Kit (Barcode, Face, Text detection).
* **Biometric Authentication**: Fingerprint & Face Unlock (BiometricPrompt API).
* **Dynamic Delivery**: Exploration of Split Install & Dynamic Feature Modules.
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
