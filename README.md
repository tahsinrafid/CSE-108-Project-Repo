# 📘 Exam System Project

**ExamAce** - A desktop-based Exam System built using JavaFX. The system allows teachers to manage questions and exams, and students to participate in exams with instant feedback.

---

## 👥 Contributors

* **Tahsin Rafid** — 2405173
* **Mahathir Mohammad Auntor** — 2405174

---

## 🚀 Features

* User authentication (Login & Signup)
* Role-based access (Teacher / Student)
* Question Bank management
* Exam creation and participation
* Timer-based exams
* Instant result and feedback
* File-based data storage (no database)
* Community feature for Q&A posts and discussion
* **Client-Server architecture using Networking (TCP)**
* **Multithreading for handling multiple users (Server-side)**

---

## ⚙️ Installation & Setup

### Option 1: Run using `.jar` file

1. Clone or download the repository.
2. Navigate to:

   ```
   target/ExamAce - Online Exam System.jar
   ```
3. Run the `.jar` file.

---

### Option 2: Run from Source Code (IntelliJ IDEA)

1. Open IntelliJ IDEA.
2. Click **Open Project** and select the project folder.
3. Ensure JDK is properly configured (Java 17+ recommended).
4. Add JavaFX SDK to the project libraries.

### ▶️ Important Run Order

5. First, run `ExamServer.java` (starts the server).
6. Then, run the client application via `Main` / `Launcher.java`.

---

## 📂 Project Structure

* `controllers/` → JavaFX controllers
* `fxml/` → UI layout files
* `models/` → Data models
* `files/` → Text-based storage (users, questions, etc.)
* `server/` → Server-side networking and threading logic

---

## 📝 Notes

* No external database is used; all data is stored in text files.
* Server must be running before using the application.
* Make sure required files (e.g., `users.txt`, `questions.txt`) exist before running.
* If running from source, JavaFX must be configured properly.

---

## 📦 Submission

This repository includes:

* Source Code
* `README.md` (this file)
* Executable `.jar` file (inside `target/` directory)

Repository Link:
https://github.com/tahsinrafid/CSE-108-Project-Repo

---

**Thank you!**
