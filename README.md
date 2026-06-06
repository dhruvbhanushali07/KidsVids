


# KidsVids 🎬🧒  
KidsVids is a kid-friendly online video library where parents can create profiles for their children, control what they watch, and ensure a safe viewing environment. Kids can explore videos, save favorites, view history, and filter content by category — all in a clean, simple UI built with Jetpack Compose.

---

# 📱 **Screenshots**

## 🔐 Authentication

### **Login Screen**

<p align="center">
  <img src="https://github.com/user-attachments/assets/daeb2b75-1baa-4fd0-8710-87eac40becda" width="280" />
</p>

### **Signup Screen**

<p align="center">
  <img src="https://github.com/user-attachments/assets/a9f198a4-0e9e-486c-a33f-fa2c832662c7" width="280" />
</p>

---

## 👤 Profiles

### **Profile Selection Screen**

<p align="center">
  <img src="https://github.com/user-attachments/assets/1f0dd005-3fe7-4a00-97bd-d46fb5959e36" width="280" />
</p>

### **Add Profile Screen**

<p align="center">
  <img src="https://github.com/user-attachments/assets/55e41361-d845-4a75-a264-bdae7d534ca5" width="280" />
</p>

---

## 🏠 Main App

### **Home Screen**

<p align="center">
  <img src="https://github.com/user-attachments/assets/d7b53365-b9ab-4828-ba69-71ae0ada4f40" width="280" />
</p>

---

### **Video Player Screen**

<p align="center">
  <img src="https://github.com/user-attachments/assets/5bf8f8f3-1b92-481f-a60c-923462e3fae7" width="800" />
</p>

---

## ⭐ Your Stuff

### **Your Stuff Screens**

<p align="center">

  <img src="https://github.com/user-attachments/assets/5b6e7b67-3916-4d32-b9b5-b4c7426b881e" width="280" />
  <img src="https://github.com/user-attachments/assets/de960e69-504c-4587-864d-1eab71eb4635" width="280" />
</p>

---

## ⚙️ Account & Settings

### **Account Screens**

<p align="center">
    <img src="https://github.com/user-attachments/assets/434306ec-df70-4081-873c-5d52f191451d" width="280" />
  <img src="https://github.com/user-attachments/assets/6a724de4-7c58-4413-bdb0-734456c96352" width="280" />
</p>

---

## 🚫 Restricted Content

### **Blocked Videos Screen**

<p align="center">
    <img src="https://github.com/user-attachments/assets/1d1910a4-c368-416a-9e39-6a7a0c25f090" width="280" />

</p>

---

# 🛠 Admin Panel (Mobile)

### **Admin Login Screen**

<p align="center">
    <img src="https://github.com/user-attachments/assets/8b4f8165-f4e8-40e4-a805-7988b69a1615" width="280" />

</p>

### **Admin Dashboard (Mobile)**

<p align="center">
    <img src="https://github.com/user-attachments/assets/fc212185-a904-4b28-9c4a-d96aad29b8fd" width="280" />

</p>

### **Manage Videos Screen**

<p align="center">
    <img src="https://github.com/user-attachments/assets/e16d0695-1c40-4fc8-81e7-9b8856798818" width="280" />

</p>

### **Add / Edit Video Screens**

<p align="center">
    <img src="https://github.com/user-attachments/assets/379df3b4-ad29-4dd2-8ab2-e02f923f2ba4" width="280" />

  <img src="https://github.com/user-attachments/assets/a829a58e-1068-4ae0-a6a2-301df529aca4" width="280" />
</p>

### **Manage Categories Screen**

<p align="center">
    <img src="https://github.com/user-attachments/assets/f1ab1838-2ef8-4ba7-ab97-5758815b610b" width="280" />

</p>

### **Reports Screen**
<p align="center">
  <img src="https://github.com/user-attachments/assets/46218b5a-b95b-439a-81ca-372ae8781a0c" width="280" />
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
- StateFlow 

### 🗄 Local Storage
- Room Database  
- DAOs + Entities  
- KSP compiler for Room  

### 🎞 Video Playback
- Media3 ExoPlayer  
- Media3 UI    

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
