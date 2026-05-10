# Automatic Liquid Dispensing (Dispenser App)

An Android application designed to monitor and control an automatic liquid dispensing system. This app integrates with Firebase for real-time data management and user authentication.

## Features

-   **User Authentication**: Secure login and registration using Firebase Authentication.
-   **Real-time Monitoring**: Monitor dispenser status, including water levels (Tank A & B), volume filled, and power state in real-time via Firebase Realtime Database.
-   **Remote Control**: Toggle dispenser power and update dispensing parameters remotely.
-   **Preset Management**: Create, save, and apply custom dispensing presets (recipes) stored in Firebase Firestore.
-   **Usage History**: Automatically track dispensing history, including duration and volumes, stored in Firestore for later review.
-   **Calibration**: Built-in tools for calibrating container heights and sensor readings.
-   **Profile Management**: User profile customization with image upload support via Cloudinary.
-   **Scheduling**: (In Development/Integrated) Ability to schedule dispensing tasks.

## Tech Stack

-   **Language**: Java
-   **Architecture**: MVVM (implied by ViewModels and Repositories found in code)
-   **Database**:
    -   Firebase Realtime Database (Real-time device status)
    -   Firebase Firestore (User history and presets)
    -   Room Persistence Library (Local data)
-   **Authentication**: Firebase Auth
-   **Image Hosting**: Cloudinary (MediaManager)
-   **UI Components**: Material Design, ViewBinding, Navigation Component
-   **Networking/Async**: RxJava 3, LiveData

## Project Structure

-   `com.example.dispenser.ui`: Contains Activities and Fragments for different screens (Home, Login, Register, History, Dispenser Details, etc.).
-   `com.example.dispenser.data`: Handles data logic, including Repositories and Remote Data Sources (Firebase).
-   `com.example.dispenser.adapter`: Custom RecyclerView adapters for lists like history and presets.

## Getting Started

### Prerequisites

-   Android Studio Iguana or newer.
-   A Firebase project with Realtime Database, Firestore, and Auth enabled.
-   Cloudinary account for media management.

### Setup

1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    ```
2.  **Firebase Configuration**:
    -   Add your `google-services.json` file to the `app/` directory.
3.  **Cloudinary Configuration**:
    -   Update the `cloud_name` and other credentials in `MainActivity.java` or your configuration file.
4.  **Build and Run**:
    -   Sync the project with Gradle files and run it on an emulator or physical device.

## Screenshots

*(Add screenshots here after implementation)*

---
Developed as a tool for managing smart liquid dispensing systems.
