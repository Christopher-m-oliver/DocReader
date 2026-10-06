package com.christopher.docreader;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecentDocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RecentDocument document);

    @Query(
            "SELECT * FROM recent_documents " +
                    "ORDER BY lastOpenedAt DESC"
    )
    List<RecentDocument> getAll();

    @Query("DELETE FROM recent_documents")
    void deleteAll();
}