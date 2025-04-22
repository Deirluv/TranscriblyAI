# 🎙️ TranscriblyAI

![Java](https://img.shields.io/badge/Java-17-%23ED8B00?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-%236DB33F?style=for-the-badge&logo=spring-boot)
![Version](https://img.shields.io/badge/Version-0.8-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

Welcome to **TranscriblyAI** — a modern web application that allows users to transcribe audio files with high accuracy using AI. Powered by **Google Cloud Speech-to-Text**, this platform is ideal for content creators, students, journalists, and anyone who works with audio data.

---

## 🌟 Features

- 🔐 **User Authentication** – Secure login and registration using Spring Security.
- 📤 **Chunked File Upload** – Upload large audio files by splitting them into smaller parts.
- 🧠 **AI Transcription** – Audio is transcribed using Google Cloud Speech-to-Text.
- 🗃️ **Dashboard** – Users can view, manage, and delete their transcriptions.
- 👁️ **Visibility Toggle** – Make your transcription public or private.
- (Coming Soon!)🚨 **Real-Time Notifications** – Transcription status updates via Redis Pub/Sub and Server-Sent Events (SSE).

---

## 🛠️ Tech Stack

- **Backend:** Java 17, Spring Boot 3
- **Frontend:** Thymeleaf, Bootstrap 5, Vanilla JS
- **Database:** MariaDB
- **AI Integration:** Google Cloud Speech-to-Text API
- **Security:** Spring Security (JWT-auth or session-based)
- **File Storage:** Local filesystem (`/storage/public`)

---

## 🚀 Getting Started

### ⚙️ Prerequisites

- Java 17+
- Maven
- MariaDB
- Google Cloud account + enabled Speech-to-Text API

---

### 🧪 Installation

```bash
git clone https://github.com/Deirluv/TranscriblyAI
cd TranscriblyAI
```

#### 1. Configure application.properties

```spring.datasource.url=jdbc:mariadb://localhost:3306/transcriblyai
spring.datasource.username=your_user
spring.datasource.password=your_password

spring.servlet.multipart.max-file-size=2MB
spring.servlet.multipart.max-request-size=2MB

google.api.key=YOUR_GOOGLE_API_KEY
```

#### 2. Run the application

```
./mvnw spring-boot:run
```

## 📂 Project Structure

```
├── modules/
│   ├── auth/          # User registration, login
│   ├── transcribe/    # Transcription logic and models
├── templates/         # Thymeleaf views (login, dashboard, transcribe)
├── storage/           # Public uploaded audio files
└── TranscriblyAiApplication.java
```

## ✅ Example Use Case

1. 👤 **Register or Login**  
   The user accesses the system by registering or logging in.

2. 🎧 **Upload Audio File**  
   Using the form, the user selects and uploads a `.mp3` audio file.

3. 📤 **Chunked Upload**  
   The file is automatically split into chunks and uploaded sequentially to the server.

4. 🔄 **File Assembly & Storage**  
   Once all chunks are received, the server assembles the full file and stores it in the `storage/public` directory.

5. 🧠 **Transcription**  
   The file is sent to the Google Cloud Speech-to-Text API for transcription.

6. 💾 **Save to Database**  
   The transcribed text is saved in the database and linked to the current user.

7. 🧑‍💻 **View & Manage**  
   The user can view, make private/public, or delete their transcriptions from the dashboard.


## 📜 License
This project is licensed under the MIT License.

## 📧 Contact

Feel free to reach out:

- **Email:** timurzubalwork@gmail.com
- **GitHub:** [@Deirluv](https://github.com/Deirluv)

