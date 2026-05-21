/*
 * Copyright (c) 2024 Adam Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.adam.app.demoset.encryption.database.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.adam.app.demoset.encryption.database.data.dao.FieldLevelEncryptionDao;
import com.adam.app.demoset.encryption.database.data.database.FieldLevelEncryptionDatabase;
import com.adam.app.demoset.encryption.database.data.model.FieldLevelEncryptionItem;
import java.util.List;

public class FieldLevelEncryptionRepository {
    private final FieldLevelEncryptionDao mFieldLevelEncryptionDao;
    private final LiveData<List<FieldLevelEncryptionItem>> mAllItems;

    public FieldLevelEncryptionRepository(Application application) {
        FieldLevelEncryptionDatabase db = FieldLevelEncryptionDatabase.getDatabase(application);
        mFieldLevelEncryptionDao = db.fieldLevelEncryptionDao();
        mAllItems = mFieldLevelEncryptionDao.getAllItems();
    }

    public LiveData<List<FieldLevelEncryptionItem>> getAllItems() { return mAllItems; }

    public void insert(FieldLevelEncryptionItem item) {
        FieldLevelEncryptionDatabase.databaseWriteExecutor.execute(() -> mFieldLevelEncryptionDao.insert(item));
    }

    public void deleteAll() {
        FieldLevelEncryptionDatabase.databaseWriteExecutor.execute(mFieldLevelEncryptionDao::deleteAll);
    }
}


