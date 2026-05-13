# Kreeda-Prerana Scout (Sports)

## 📌 Problem Statement
Talented young athletes in rural schools often go unnoticed because there is no structured way to record their physical milestones (sprint times, jump heights). Coaches and scouts cannot identify "Diamonds in the Rough" without a historical data trail of their performance.

## 💡 The Vision
Kreeda-Prerana is a grassroots sports talent tracker. It acts as a "Digital Scout" for physical education teachers. The app allows teachers to create profiles for students and log their performance in standard athletic tests. Over time, it creates a "Talent Curve," making it easy to spot students who have the potential to compete at state or national levels.

## ✨ Features
*   **Athlete Profile:** Name, Age, and primary sport (e.g., Kabaddi, Athletics).
*   **Trial Logger:** A stopwatch and distance logger for sprints, long jumps, etc.
*   **Milestone Badges:** Automatically awards "District Level Ready" badges based on preset benchmarks.
*   **Leaderboard:** A simple internal school ranking to boost healthy competition.
*   **Batch Entry:** Allows entry for an entire class of 30 students.
*   **Talent Curve Graph:** Visual representation of athlete's progression over time.

## 🛠️ Tech Stack
*   **Language:** Kotlin
*   **Framework:** Android SDK
*   **Database:** Room Database (for storing athlete performance history)
*   **UI Architecture:** XML / Jetpack Compose (Modern Android UI)
*   **Core Components:** High-precision Chronometer (accurate to two decimal places)
*   **Analytics:** Sorting Algorithms for Leaderboard

## 🚀 Installation Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/deekshareddym1002/Kreeda-Prerana.git
   ```
2. Open the project in **Android Studio**.
3. Let Gradle sync and download the required dependencies.
4. Set up an Android Emulator or connect a physical Android device via USB debugging.
5. Click on the **Run** button (green play icon) in Android Studio to build and install the app.

## ▶️ Run Command
To build and run the app via terminal (if you have standard Android SDK tools configured):
```bash
./gradlew installDebug
```

## 📁 Folder Structure
```
Kreeda-Prerana/
├── app/                      # Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/         # Kotlin/Java source code
│   │   │   ├── res/          # UI layouts, drawables, and values
│   │   │   └── AndroidManifest.xml
│   │   └── test/             # Unit testing code
│   └── build.gradle.kts      # App-level build configurations
├── gradle/                   # Gradle wrapper files
├── build.gradle.kts          # Project-level build configurations
└── README.md                 # Project documentation
```

## 🔮 Future Improvements
*   **Cloud Synchronization:** Sync local Room DB data to a cloud backend (Firebase or Supabase) to prevent data loss.
*   **Khelo India Integration:** Export athlete reports in a format compatible with national sports academy databases.
*   **Video Analysis:** Integrate a simple video recording feature to review athlete form and technique.
*   **Multi-language Support:** Add support for regional languages to help rural physical education teachers use the app comfortably.
