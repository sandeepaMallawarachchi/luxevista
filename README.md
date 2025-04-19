# 🌴 LuxeVista Resort App

Welcome to **LuxeVista** — a modern Android application tailored for resort guests to explore rooms, services, and local attractions, and make bookings seamlessly.

![LuxeVista Banner](4tos/home.png)

---

## 📱 Features

- 🔐 **User Authentication** – Login/Register with validation.
- 🛏️ **Room Booking** – Browse available rooms, filter by type, price, and availability.
- 🧘 **Service Booking** – Book relaxing spa treatments, romantic candlelight dinners, and more.
- 🌍 **Nearby Attractions** – Discover local attractions with details and images.
- 👤 **User Profile** – Upload profile pictures, view booking stats (confirmed/cancelled/total).
- 🔄 **Firestore Integration** – All data (users, rooms, bookings, services) handled via Firebase Firestore.
- ☁️ **Cloudinary Support** – Profile image uploads are stored on Cloudinary.

---

## ✨ UI Screenshots

| Home | Room Details | Profile |
|------|---------------|---------|
| ![Home](4tos/home.png) | ![Details](4tos/details.png) | ![Profile](4tos/profile.png) |

| Login | Register | Services |
|------|---------|----------|
| ![Login](4tos/login.png) | ![Register](4tos/register.png) | ![Services](4tos/services.png) |

| Attractions |
|-------------|
| ![Attractions](4tos/attractions.png) |

---

## 🛠️ Tech Stack

- **Android** (Java)
- **Firebase Firestore** – Realtime database
- **Firebase Auth** – Authentication system
- **Cloudinary** – Image storage (profile uploads)
- **Glide** – Image loading
- **Material UI + ConstraintLayout** – Clean and responsive layout

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (latest)
- Firebase project + Cloudinary account
- Enable Firestore & Firebase Auth in Firebase Console

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/luxevista.git
Open with Android Studio and sync Gradle.

2. Add your Firebase config:

3. Place google-services.json inside app/.

4. Replace Cloudinary credentials in ProfileFragment.java:

new Cloudinary(ObjectUtils.asMap(
  "cloud_name", "your_cloud_name",
  "api_key", "your_api_key",
  "api_secret", "your_api_secret"
));

5. Run on emulator or Android device.

##🔒 Permissions
Make sure to include this permission in your AndroidManifest.xml:

```bash
<uses-permission android:name="android.permission.INTERNET"/>

##🤝 Contribution
Feel free to fork the repo and submit pull requests. You can improve the UI/UX, optimize performance, or integrate new features like payment gateways!

##📬 Contact
Developer: sandeepa
Email: sandeepamallawarachchi444@gmail.com
LinkedIn: [[your-linkedin-profile]](https://www.linkedin.com/in/sandeepa-mallawarachchi/)

© 2025 LuxeVista. All rights reserved.
