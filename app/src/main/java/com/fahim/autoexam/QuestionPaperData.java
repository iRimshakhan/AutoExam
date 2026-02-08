package com.fahim.autoexam;

import java.io.Serializable;

public class QuestionPaperData implements Serializable {
    private String className;
    private String subjectName;
    private String questionPaperName;
    private String questionType;
    private String noOfQuestionsPerUnit;
    private String collegeName;
    private String date;
    private String marks;
    private String duration;
    private String pdfContent;
    private String pdfName;

    // Constructor
    public QuestionPaperData() {}

    // Getters and Setters
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getQuestionPaperName() { return questionPaperName; }
    public void setQuestionPaperName(String questionPaperName) { this.questionPaperName = questionPaperName; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public String getNoOfQuestionsPerUnit() { return noOfQuestionsPerUnit; }
    public void setNoOfQuestionsPerUnit(String noOfQuestionsPerUnit) { this.noOfQuestionsPerUnit = noOfQuestionsPerUnit; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getMarks() { return marks; }
    public void setMarks(String marks) { this.marks = marks; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getPdfContent() { return pdfContent; }
    public void setPdfContent(String pdfContent) { this.pdfContent = pdfContent; }

    public String getPdfName() { return pdfName; }
    public void setPdfName(String pdfName) { this.pdfName = pdfName; }
}