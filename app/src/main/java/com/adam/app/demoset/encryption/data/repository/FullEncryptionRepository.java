/*
 * Copyright (c) 2024 Adam Chen
 */

package com.adam.app.demoset.encryption.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.adam.app.demoset.encryption.data.dao.FullEncryptionDao;
import com.adam.app.demoset.encryption.data.database.FullEncryptionDatabase;
import com.adam.app.demoset.encryption.data.model.FullEncryptionItem;
import java.util.List;

public class FullEncryptionRepository {
    private final FullEncryptionDao mDao;
    private final LiveData<List<FullEncryptionItem>> mAllItems;

    public FullEncryptionRepository(Application application, byte[] passphrase) {
        FullEncryptionDatabase db = FullEncryptionDatabase.getDatabase(application, passphrase);
        mDao = db.fullEncryptionDao();
        mAllItems = mDao.getAllItems();
    }

    public LiveData<List<FullEncryptionItem>> getAllItems() { return mAllItems; }

    public void insert(FullEncryptionItem item) {
        FullEncryptionDatabase.databaseWriteExecutor.execute(() -> mDao.insert(item));
    }

    public void deleteAll() {
        FullEncryptionDatabase.databaseWriteExecutor.execute(mDao::deleteAll);
    }
}
