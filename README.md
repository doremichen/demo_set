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

## 🔹 UI Demos

* Material Design Components
* DataBinding / ViewBinding
* Animation & GIF rendering
* Floating Dialog / Floating Window
* Table Layout rendering
* Compose UI
* StateFlow / SharedFlow

---

## ⚙️ System & Background Execution

* Started / Bound Service
* JobScheduler / JobService
* WorkManager (Modern background tasks)
* AlarmManager
* HandlerThread
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

## 📂 Storage

* File Explorer (Scoped Storage ready)
* SQLite (ContentProvider)
* Room Database

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

# 🏗️ Design Principles

* **Separation of Concerns**
* **Dependency Flow Control (DIP)**
* **MVVM Pattern**
* **Reactive UI Updates**
* **System-level abstraction**

---

# 🧪 Use Cases Covered

This project covers real-world scenarios such as:

* Background task scheduling
* Cross-process communication
* Hardware interaction
* Native performance integration
* Data persistence strategies
---

# 🛠️ Tech Stack

* Java / Kotlin
* Android SDK
* ViewBinding / DataBinding
* LiveData / ViewModel
* WorkManager
* Retrofit
* JNI (C/C++)

---

# 📌 Future Roadmap

Planned upgrades:
* Paging 3
* DataStore
* Modern permission model
* AIDL-based IPC
* AI / ML integration (TensorFlow Lite)

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

MIT License (or your preferred license)
