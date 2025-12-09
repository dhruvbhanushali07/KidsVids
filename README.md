


# KidsVids 🎬🧒  
KidsVids is a kid-friendly online video library where parents can create profiles for their children, control what they watch, and ensure a safe viewing environment. Kids can explore videos, save favorites, view history, and filter content by category — all in a clean, simple UI built with Jetpack Compose.

---

## 📱 Screenshots
<p align="center">
  <img width="547" height="1125" alt="image" src="https://github.com/user-attachments/assets/d7b53365-b9ab-4828-ba69-71ae0ada4f40" />

</p>

---

## 🚀 Features

### 👨‍👩‍👧 Parent Features
- Parent signup & login  
- Create multiple kids profiles  
- Select child age category  
- Age-filtering: kids only see videos within their age range  
- 4-digit PIN-locked Settings page  
- View profile details + parent email  
- Switch between multiple kids profiles  
- Block specific videos  
- Access blocked videos list  
- Secure logout  

### 🧒 Kids Features
- Watch age-appropriate videos  
- Save videos to favorites  
- See viewing history  
- Category tabs (All, Music, Educational, Cartoons…)  
- Bottom navigation: **Home**, **Your Stuff**, **Account**  
- Clean & safe UI

### 🛠 Admin Panel
- Admin login  
- Manage videos (add/edit/delete via public Cloudinary URL)  
- Manage categories  
- Manage users  
- View reports  
- Manual video upload to Cloudinary (URL stored in Room DB)

---

## 🧩 How It Works

### 📦 Video Storage
- Videos uploaded manually to **Cloudinary**  
- Only Cloudinary **public URL** is stored  
- Room Database stores:
  - title  
  - description  
  - age category  
  - video URL  
  - blocked status  
  - other metadata  

### 🏗 Architecture
Your app uses a clean, simple, scalable architecture:



UI Screens (Compose)
↓
ViewModels
↓
DatabaseProvider / DAO (Room)
↓
AppDatabase



- Jetpack Compose UI  
- ViewModel for state management  
- Room for persistence  
- ExoPlayer for streaming

---

## 🏗 Project Structure


```
app/
├── src/
│   ├── androidTest/java/com/example/kidsvids/
│   ├── main/
│   │   ├── java/com/example/kidsvids/
│   │   │    ├── data/
│   │   │    │    ├── dao/
│   │   │    │    ├── entities/
│   │   │    │    ├── AppDatabase.kt
│   │   │    │    └── DatabaseProvider.kt
│   │   │    ├── ui/theme/
│   │   │    ├── uiscreens/
│   │   │    ├── viewmodels/
│   │   │    └── MainActivity.kt
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── test/java/com/example/kidsvids/
├── .gitignore

```
---

## 🏗 Tech Stack

### 🎨 UI / UX
- **Jetpack Compose**
- Material Design 3
- Compose Navigation
- Media3 UI Compose
- Coil (image loading)

### 🧠 State & Logic
- ViewModel (Lifecycle)
- Kotlin Coroutines
- StateFlow / LiveData (if used)

### 🗄 Local Storage
- Room Database  
- DAOs + Entities  
- KSP compiler for Room  

### 🎞 Video Playback
- Media3 ExoPlayer  
- Media3 UI  
- DASH support  

### ☁ Cloud Storage
- Cloudinary public video URLs

---

## 📦 Dependencies Used

```kotlin
val room_version = "2.7.2"
val nav_version = "2.9.3"
val lifecycle_version = "2.9.3"

implementation("androidx.media3:media3-exoplayer:1.8.0")
implementation("androidx.media3:media3-exoplayer-dash:1.8.0")
implementation("androidx.media3:media3-ui:1.8.0")
implementation("androidx.media3:media3-ui-compose:1.8.0")

implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
implementation("androidx.navigation:navigation-compose:$nav_version")

implementation("androidx.room:room-runtime:$room_version")
implementation("androidx.room:room-ktx:$room_version")
ksp("androidx.room:room-compiler:$room_version")

implementation("io.coil-kt:coil-compose:2.6.0")
implementation("androidx.compose.material:material-icons-extended")
````

---

## ▶️ Running the Project

1. Clone the repo

   ```bash
   git clone https://github.com/your-username/kidsvids.git
   ```
2. Open in Android Studio
3. Sync Gradle
4. Run on emulator or physical device

---

## 🛡 License

This project is for educational and portfolio purposes.
(If you'd like MIT license added, I can include it.)

---

## ⭐ Support

If this project helped or inspired you, give it a ⭐ on GitHub!
