/*
 * Copyright (c) 2024 Adam Chen
 */

package com.adam.app.demoset.encryption.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.adam.app.demoset.encryption.data.dao.EncryptionDao;
import com.adam.app.demoset.encryption.data.database.EncryptionDatabase;
import com.adam.app.demoset.encryption.data.model.EncryptionItem;
import java.util.List;

public class EncryptionRepository {
    private final EncryptionDao mEncryptionDao;
    private final LiveData<List<EncryptionItem>> mAllItems;

    public EncryptionRepository(Application application) {
        EncryptionDatabase db = EncryptionDatabase.getDatabase(application);
        mEncryptionDao = db.encryptionDao();
        mAllItems = mEncryptionDao.getAllItems();
    }

    public LiveData<List<EncryptionItem>> getAllItems() { return mAllItems; }

    public void insert(EncryptionItem item) {
        EncryptionDatabase.databaseWriteExecutor.execute(() -> mEncryptionDao.insert(item));
    }

    public void deleteAll() {
        EncryptionDatabase.databaseWriteExecutor.execute(mEncryptionDao::deleteAll);
    }
}
