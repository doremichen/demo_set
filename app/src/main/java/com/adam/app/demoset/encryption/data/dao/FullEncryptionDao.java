/*
 * Copyright (c) 2024 Adam Chen
 */

package com.adam.app.demoset.encryption.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.adam.app.demoset.encryption.data.model.FullEncryptionItem;

import java.util.List;

@Dao
public interface FullEncryptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FullEncryptionItem item);

    @Query("SELECT * FROM full_encryption_items ORDER BY id DESC")
    LiveData<List<FullEncryptionItem>> getAllItems();

    @Query("DELETE FROM full_encryption_items")
    void deleteAll();
}
