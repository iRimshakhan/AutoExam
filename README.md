# AutoExam — AI-Powered Question Paper Generator

AutoExam is an Android application that uses **Google Gemini Generative AI** to automatically generate new question papers from syllabus content while following the structure of a reference question paper.

The project aims to reduce the manual effort involved in preparing question papers by combining **Generative AI, PDF processing, text recognition, and local database management** in a single Android application.

---

## 🚀 Project Overview

Preparing question papers manually can be time-consuming, especially when teachers need to maintain a specific examination pattern, marks distribution, sections, and question format.

AutoExam provides an automated workflow where users can provide:

* A syllabus document.
* A previous/reference question paper.
* Examination details such as class, subject, date, and duration.

The application processes this information and uses **Google Gemini Generative AI** to generate a completely new question paper based on the syllabus while maintaining the structure of the reference paper.

---

## ✨ Key Features

* 📄 Upload and process syllabus documents.
* 📋 Upload a previous/reference question paper.
* 🤖 Generate new questions using Google Gemini Generative AI.
* 🧠 Analyze the structure of a reference question paper.
* 📚 Use syllabus content as the source for generating new questions.
* 🚫 Instruct the AI not to copy questions from the reference paper.
* 📝 Maintain question numbering and sub-question formatting.
* 📊 Preserve sections, marks, and attempt instructions from the reference paper.
* 👤 User registration and login.
* 💾 Store application data using Room Database.
* 📑 View generated question papers.
* 📱 Android-based user interface.
* 🔄 Display progress and error messages during AI generation.

---

## 🔄 How It Works

```text
                User
                 │
                 ▼
        Select / Upload Files
                 │
        ┌────────┴─────────┐
        ▼                  ▼
   Syllabus PDF       Reference Paper
        │                  │
        ▼                  ▼
  Text Extraction    Pattern Analysis
        │                  │
        └────────┬─────────┘
                 ▼
          Prompt Creation
                 │
                 ▼
          Google Gemini AI
                 │
                 ▼
        New Question Paper
                 │
                 ▼
       Structured Formatting
                 │
                 ▼
          View Generated
             Paper
```

---

## 🤖 Generative AI Workflow

AutoExam uses a structured prompt to control how the question paper is generated.

### Step 1 — Analyze the reference paper

The application asks Gemini to analyze the reference paper only for its structure, including:

* Number of questions.
* Sections.
* Marks per question.
* Question types.
* Attempt instructions.
* Examination pattern.

The AI is explicitly instructed **not to copy or reuse questions from the reference paper**.

### Step 2 — Use the syllabus as the content source

The syllabus is provided separately to the model and is treated as the source from which new questions should be generated.

### Step 3 — Generate a new paper

Gemini generates a completely new question paper while following the structure of the reference paper.

The generated paper is instructed to maintain:

* Question numbering.
* Sub-question labels.
* Sections.
* Marks.
* Attempt instructions.
* Examination header information.
* Plain-text formatting.

This workflow is implemented in `GeminiAIHelper.java`.

---

## 🧠 AI Implementation

The application communicates with the **Google Gemini API** using the `generateContent` endpoint.

The current implementation uses:

```text
Gemini 2.0 Flash
```

The application creates a structured JSON request containing the generated prompt and sends it through an HTTP POST request.

The generation configuration includes:

```text
Temperature: 0.7
Maximum Output Tokens: 8192
```

The generated response is then parsed from the Gemini API response and returned to the application.

---

## 🛠️ Technology Stack

### Android Development

* Java
* Android SDK
* XML
* Android Activities

### Artificial Intelligence

* Google Gemini API
* Generative AI
* Prompt Engineering
* AI-assisted Question Generation

### Document & Text Processing

* Apache PDFBox
* Google ML Kit Text Recognition
* PDF processing

### Database

* Room Database
* SQLite

### Networking & Data

* HTTPURLConnection
* REST API
* JSON

### Development Tools

* Android Studio
* Gradle
* Git
* GitHub

---

## 📱 Application Components

The application contains multiple activities supporting the complete workflow, including:

* Splash Screen
* Login
* Registration
* Home
* Syllabus Selection
* Form / Examination Details
* PDF Upload
* Question Generation
* Generated PDF Viewing

---

## 🗄️ Database

AutoExam uses **Android Room Database** for local data management.

The project contains database components for handling application information, including:

* User data.
* Generated question-paper/report data.

The database layer includes entities and DAOs such as:

```text
AppDatabase
UserEntity
UserDao
ReportEntity
ReportDao
```

---

## 📂 Project Structure

```text
AutoExam/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/fahim/autoexam/
│           │       ├── db/
│           │       ├── LoginActivity.java
│           │       ├── RegisterActivity.java
│           │       ├── HomeActivity.java
│           │       ├── ChooseSyllabusActivity.java
│           │       ├── UploadPDFActivity.java
│           │       ├── GeneratingActivity.java
│           │       ├── ViewPDFActivity.java
│           │       ├── GeminiAIHelper.java
│           │       └── ...
│           │
│           └── res/
│               ├── drawable/
│               ├── layout/
│               ├── menu/
│               ├── mipmap/
│               ├── values/
│               └── xml/
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🔐 API Key Configuration

The Gemini API key is accessed through:

```java
BuildConfig.GEMINI_API_KEY
```

The API key should **not be hard-coded directly into the source code or committed to GitHub**.

Before running the project, configure the Gemini API key through the project's build configuration.

> ⚠️ For production applications, API credentials should ideally be handled through a secure backend rather than being distributed directly with an Android application.

---

## ⚙️ Setup & Installation

### 1. Clone the repository

```bash
git clone https://github.com/iRimshakhan/AutoExam.git
```

### 2. Open the project

Open the cloned project using **Android Studio**.

### 3. Configure the Gemini API key

Configure your Gemini API key using the project's `BuildConfig` setup.

Do not commit your actual API key to GitHub.

### 4. Sync Gradle

Allow Android Studio to download and configure the required dependencies.

### 5. Build the application

Build the project using Android Studio.

### 6. Run the application

Run the application on an Android emulator or compatible Android device.

---

## 🎯 Use Case

AutoExam can be useful for:

* Schools.
* Colleges.
* Universities.
* Teachers.
* Academic institutions.
* Educational content creators.

The application can help reduce the manual effort involved in creating examination papers while maintaining an existing examination pattern.

---

## 💡 Problem Solved

### Traditional Process

```text
Read syllabus
     ↓
Study previous papers
     ↓
Create questions manually
     ↓
Arrange questions
     ↓
Match marks and sections
     ↓
Format question paper
```

### AutoExam

```text
Upload syllabus + reference paper
                ↓
          AI processing
                ↓
        Generate questions
                ↓
       Apply paper structure
                ↓
       View generated paper
```

This helps automate repetitive parts of the question-paper preparation process.

---

## 🔮 Future Improvements

Potential future improvements include:

* Improved question-quality evaluation.
* Better control over question difficulty.
* Support for additional AI models.
* Backend-based secure API integration.
* Improved PDF generation and formatting.
* Question-paper history and management.
* Export to additional document formats.
* More advanced syllabus-to-question mapping.
* Automated validation of generated questions.
* Improved duplicate-question detection.

---

## 👩‍💻 Developer

**Rimsha Fatima Khan**

MSc IT — Artificial Intelligence
Mumbai University

Interested in:

* Artificial Intelligence
* Machine Learning
* Generative AI
* Python
* Data Science
* Intelligent Automation
* AI-powered Applications

---

## 📌 Project Status

**Academic / Portfolio Project**

AutoExam is developed as an AI-powered educational application demonstrating the practical integration of Generative AI with Android application development.

---

## 📄 License

This project is intended for educational and portfolio purposes.
