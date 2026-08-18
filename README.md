# AutoExam — AI-Powered Question Paper Generator

AutoExam is an Android application that uses **Google Gemini Generative AI** to automatically generate new question papers from syllabus content while following the structure of a reference question paper.

The project aims to reduce the manual effort involved in preparing question papers by combining **Generative AI, PDF processing, text recognition, and local database management** in a single Android application.

---

## 🎯 Problem Statement

Creating examination question papers manually can be time-consuming.

Teachers and educators often need to:

- Study the syllabus.
- Review previous question papers.
- Maintain a specific examination pattern.
- Decide the number and type of questions.
- Maintain marks distribution.
- Format the final question paper.

AutoExam aims to automate these repetitive tasks using Generative AI.

---

## 💡 Solution

AutoExam allows users to create a question paper by providing:

1. **Paper details** through an input form.
2. **Syllabus PDF** containing the topics from which questions should be generated.
3. **Reference / Pattern Question Paper** whose structure is used as a reference.

The application processes these inputs and uses **Google Gemini Generative AI** to generate a new question paper.

The reference paper is used to understand the examination structure, while the syllabus provides the content for generating new questions.

---

## ✨ Key Features

- 🤖 AI-powered question paper generation.
- 📄 Upload syllabus PDF.
- 📋 Upload reference / pattern question paper.
- 🧠 Analyze the structure of a reference question paper.
- 📚 Generate questions from syllabus content.
- 🚫 Generate new questions instead of copying questions from the reference paper.
- 📝 Maintain question numbering and sub-question formatting.
- 📊 Maintain sections, marks, and attempt instructions.
- 📝 Collect examination details through a dedicated form.
- 👤 User registration and login.
- 💾 Store application data using Room Database.
- 📑 View generated question papers.
- 🔍 Search saved question papers.
- 📱 Android-based user interface.
- ⚡ Display processing and generation progress.
- ❌ Handle API and generation errors.

---

# 🔄 Application Workflow

```text
                     Login / Register
                           │
                           ▼
                  Question Papers
                     Dashboard
                           │
                           ▼
                    Create New Paper
                           │
                           ▼
                  Paper Details Form
                           │
                           ▼
                    Choose Files
                     /          \
                    /            \
                   ▼              ▼
             Syllabus PDF    Reference Paper
                   │              │
                   └──────┬───────┘
                          ▼
                  Process / Read Files
                          │
                          ▼
                   Gemini AI Workflow
                          │
                          ▼
                 Generate Questions
                          │
                          ▼
                 Format Question Paper
                          │
                          ▼
                 View Generated Paper
                          │
                          ▼
                Save / Manage Papers
```
---

# 📱 Application Screens

## 1. Question Papers Dashboard

The application provides a dashboard where users can view and manage their generated question papers.

Users can also use the **+** button to create a new question paper.

<img width="300" height="500" alt="Question Papers Dashboard" src="https://github.com/user-attachments/assets/d840f4d4-0954-43f7-a275-c87aecd65cf9" />

---

## 2. Paper Details Form

Before uploading documents, the user provides the required details for the question paper through a dedicated form.

These details are used to configure the question-paper generation process.

<img width="300" height="500" alt="Paper Details Form" src="https://github.com/user-attachments/assets/7a444e78-1c65-49d0-bf46-61dc66949052" />

---

## 3. Choose Files 

The user uploads two important documents:

- **Syllabus PDF** — provides the content from which questions should be generated.
- **Reference / Pattern Question Paper** — provides the structure and format that the generated paper should follow.

<img width="300" height="500" alt="Choose Files" src="https://github.com/user-attachments/assets/1ee069de-7732-4dbd-9e10-264a8fe0fbcf" />

---

## 4. Processing Uploaded Files

After the documents are uploaded, the application processes and reads the files before starting the AI generation process.

This stage prepares the required information from the uploaded documents for the Generative AI workflow.

<img width="300" height="500" alt="Processing Uploaded Files" src="https://github.com/user-attachments/assets/52bb1a44-1d7c-4c07-b499-294052ca4b13" />

---

## 5. AI Question Generation

The application uses Generative AI to analyze the syllabus and create new questions according to the required paper structure.

The generation process includes:

- Analyzing syllabus content.
- Creating questions.
- Formatting the question paper.

<img width="300" height="500" alt="AI Question Generation" src="https://github.com/user-attachments/assets/800df23a-4731-4d2e-bea2-4672d5971e61" />

---

# 🤖 Generative AI Workflow

AutoExam uses **Google Gemini Generative AI** to generate new examination questions.

The generation process follows these major steps:

### 1. Collect Paper Details

The user first enters the required examination and paper information through a form.

### 2. Upload the Syllabus

The syllabus is uploaded as a PDF and provides the content from which new questions should be generated.

### 3. Upload a Reference / Pattern Paper

The user uploads a previous question paper that acts as a structural reference.

The application uses the reference paper to understand information such as:

- Number of questions.
- Sections.
- Marks.
- Question types.
- Attempt instructions.
- Question numbering.
- Sub-question formatting.

### 4. Process the Documents

The uploaded documents are processed before the AI generation stage.

PDF and text-processing components are used to extract and work with the required information.

### 5. Generate Questions

The application creates a structured prompt and sends the relevant information to Gemini.

The AI is instructed to:

- Use the syllabus as the content source.
- Follow the structure of the reference paper.
- Generate new questions.
- Avoid copying questions from the reference paper.
- Maintain the required formatting.

### 6. Display the Generated Paper

The generated question paper is returned to the Android application and can be viewed by the user.

---

# 🧠 Gemini AI Implementation

The project integrates the **Google Gemini API** for Generative AI-based question generation.

The application uses Gemini to process the supplied information and generate structured examination questions.

The generation process uses configuration parameters for controlling the AI response, including temperature and maximum output tokens.

The application creates a structured prompt containing the required examination information, reference-paper structure, and syllabus content.

The Gemini response is then processed and returned to the Android application.

---

# 🛠️ Technology Stack

## Android Development

- Java
- XML
- Android SDK
- Android Activities

## Artificial Intelligence

- Google Gemini API
- Generative AI
- Prompt Engineering
- AI-powered Question Generation

## Document Processing

- Apache PDFBox
- Google ML Kit Text Recognition
- PDF processing
- Text extraction

## Database

- Android Room Database
- SQLite

## Development Tools

- Android Studio
- Gradle
- Git
- GitHub

---

# 🗄️ Database

AutoExam uses **Room Database** for local data storage.

The project contains database components for managing application data, including users and generated question-paper/report information.

Important database components include:

```text
AppDatabase
UserEntity
UserDao
ReportEntity
ReportDao
```

Room provides an abstraction layer over SQLite and allows the application to manage structured local data.

---

# 📂 Project Structure

```text
AutoExam/
│
├── app/
│   └── src/
│       └── main/
│           │
│           ├── java/
│           │   └── com/fahim/autoexam/
│           │       │
│           │       ├── db/
│           │       │   ├── AppDatabase.java
│           │       │   ├── UserDao.java
│           │       │   ├── UserEntity.java
│           │       │   ├── ReportDao.java
│           │       │   └── ReportEntity.java
│           │       │
│           │       ├── LoginActivity.java
│           │       ├── RegisterActivity.java
│           │       ├── HomeActivity.java
│           │       ├── FormActivity.java
│           │       ├── ChooseSyllabusActivity.java
│           │       ├── UploadPDFActivity.java
│           │       ├── GeneratingActivity.java
│           │       ├── ViewPDFActivity.java
│           │       ├── GeminiAIHelper.java
│           │       └── ...
│           │
│           ├── res/
│           │   ├── drawable/
│           │   ├── layout/
│           │   ├── menu/
│           │   ├── mipmap/
│           │   ├── values/
│           │   └── xml/
│           │
│           └── AndroidManifest.xml
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

# 🔐 API Key Security

The Gemini API key is accessed through the application's build configuration.

The API key should never be hard-coded directly into the source code or committed to GitHub.

For production applications, API credentials should ideally be handled through a secure backend rather than being distributed directly with an Android application.

---

# ⚙️ Installation & Setup

## 1. Clone the Repository

```bash
git clone https://github.com/iRimshakhan/AutoExam.git
```

## 2. Open the Project

Open the project in **Android Studio**.

## 3. Configure the Gemini API Key

Configure the Gemini API key through the project's build configuration.

Do not commit your actual API key to GitHub.

## 4. Sync Gradle

Allow Android Studio to download and configure the required dependencies.

## 5. Build the Project

Build the application using Android Studio.

## 6. Run the Application

Run the application on:

- Android Emulator
- Compatible Android Device

---

# 🎓 Use Cases

AutoExam can be useful for:

- Schools.
- Colleges.
- Universities.
- Teachers.
- Educational institutions.
- Academic departments.
- Educational content creators.

---

# 🚀 Benefits

### Reduces Manual Work

Automates repetitive parts of question-paper preparation.

### Saves Time

Generates questions faster than creating an entire paper manually.

### Maintains Examination Structure

Uses a reference question paper to maintain the desired format, sections, marks, and question structure.

### Uses Generative AI

Demonstrates a practical application of Generative AI in an educational workflow.

### Centralized Paper Management

Generated papers can be stored and managed inside the application.

---

# 🔮 Future Improvements

Potential future improvements include:

- Better question-quality evaluation.
- Question difficulty controls.
- Improved syllabus-to-question mapping.
- Automated duplicate-question detection.
- Support for additional AI models.
- Secure backend-based Gemini API integration.
- Improved PDF generation and formatting.
- Export to additional document formats.
- Question-paper history and version management.
- Automated validation of generated questions.
- More advanced educational assessment features.

---

# 👩‍💻 Developer

## Rimsha Fatima Khan

**MSc IT — Artificial Intelligence**  
**Mumbai University**

### Interests

- Artificial Intelligence
- Machine Learning
- Generative AI
- Python
- Data Science
- Intelligent Automation
- AI-powered Applications
- Android Development

---

# 📌 Project Status

**Academic / Portfolio Project**

AutoExam is an AI-powered educational application developed to demonstrate the practical integration of **Generative AI with Android application development, document processing, and database management**.

---

# 🔗 Repository

**GitHub:**  
https://github.com/iRimshakhan/AutoExam

---

# 📄 License

This project is intended for educational and portfolio purposes.
