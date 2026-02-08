package com.fahim.autoexam.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reports")
public class ReportEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String paperName;
    public String subjectName;
    public String filePath;
    public String date;
}
