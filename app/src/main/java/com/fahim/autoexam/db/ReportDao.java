package com.fahim.autoexam.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReportDao {
    @Insert
    void insert(ReportEntity report);

    @Query("SELECT * FROM reports ORDER BY id DESC")
    List<ReportEntity> getAllReports();

    @Delete
    void delete(ReportEntity report);
}
