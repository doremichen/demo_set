/*
 * Copyright (c) 2024 Adam Chen
 */

package com.adam.app.demoset.encryption.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.adam.app.demoset.encryption.data.model.EncryptionItem;

import java.util.List;

@Dao
public interface EncryptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EncryptionItem item);

    @Update
    void update(EncryptionItem item);

    @Delete
    void delete(EncryptionItem item);

    @Query("SELECT * FROM encryption_items ORDER BY id DESC")
    LiveData<List<EncryptionItem>> getAllItems();

    @Query("DELETE FROM encryption_items")
    void deleteAll();
}
