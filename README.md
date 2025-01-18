# SoundMonster 🎵

**SoundMonster** is a modern **Media Player App** designed to provide users with a seamless and personalized music experience. Built using cutting-edge Android technologies, the app allows users to select their favorite artists, fetch their albums, and enjoy smooth music playback with intuitive controls.

---
## Demo Video 

Check out the demo on YouTube: [Watch on YouTube](https://youtu.be/V4lW9XebDhk?si=O13qRT3zv_p71v9H)

## Features ✨

- **Personalized Music Experience**:  
  Easily select your favorite artists and fetch their albums using the **Spotify Web API**.

- **Background Music Playback**:  
  Enjoy uninterrupted music, powered by **Foreground Service** and **Bound Service**.

- **Smart Media Controls**:  
  Control playback directly from the notification tray with **MediaStyle notifications** provided by Google.

- **Modern UI**:  
  Built with **Jetpack Compose** for a sleek, responsive, and user-friendly interface.

---

## Tech Stack 🛠

- **Architecture**: MVVM (Model-View-ViewModel)
- **UI**: Jetpack Compose
- **Dependency Injection**: Dagger Hilt
- **Music Data**: Spotify Web API
- **Networking**: Retrofit
- **Image Loading**: Coil
- **Background Services**: Foreground Service and Bound Service

---

## Project Setup 🚀

Follow these steps to set up the project on your local machine:

1. **Clone the Repository**:
   ```bash  
   git clone https://github.com/adi0515sharma/SoundMonster.git  
   
2. **Open the Project**:

   Open the project in Android Studio.

   Add Spotify API Credentials:

   Navigate to ```\app\src\main\java\com\kft\soundmonster\utils\Constants```
   Replace the placeholders for CLIENT_ID and CLIENT_SECRET with your own credentials from the Spotify Developer Dashboard.

3. **Sync the Project with Gradle**:  

   In **Android Studio**, click on **File** > **Sync Project with Gradle Files** to ensure all dependencies are downloaded and configured.


## Acknowledgments 🙏

1. Thanks to **Spotify** for providing the Web API.
2. Huge shoutout to the **Android community** for guidance and support.  
