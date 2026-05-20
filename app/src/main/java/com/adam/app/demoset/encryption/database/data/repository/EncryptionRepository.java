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
import com.adam.app.demoset.encryption.database.data.dao.EncryptionDao;
import com.adam.app.demoset.encryption.database.data.database.EncryptionDatabase;
import com.adam.app.demoset.encryption.database.data.model.EncryptionItem;
import com.adam.app.demoset.encryption.database.utils.AppExecutors;

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
        AppExecutors.getInstance().diskIO().execute(() -> mEncryptionDao.insert(item));
    }

    public void deleteAll() {
        AppExecutors.getInstance().diskIO().execute(mEncryptionDao::deleteAll);
    }
}


