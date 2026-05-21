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
package com.adam.app.demoset.encryption.database.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.adam.app.demoset.encryption.database.controller.CryptoManager;
import com.adam.app.demoset.encryption.database.data.model.FieldLevelEncryptionItem;
import com.adam.app.demoset.encryption.database.data.repository.FieldLevelEncryptionRepository;
import java.util.List;

public class FieldLevelEncryptionViewModel extends AndroidViewModel {
    private final FieldLevelEncryptionRepository mRepository;
    private final CryptoManager mCryptoManager;
    private final LiveData<List<FieldLevelEncryptionItem>> mAllItems;

    // For Data Binding
    public final MutableLiveData<String> aliasInput = new MutableLiveData<>("");
    public final MutableLiveData<String> dataInput = new MutableLiveData<>("");

    public FieldLevelEncryptionViewModel(@NonNull Application application) {
        super(application);
        mRepository = new FieldLevelEncryptionRepository(application);
        mCryptoManager = new CryptoManager();
        mAllItems = Transformations.map(mRepository.getAllItems(), items -> {
            for (FieldLevelEncryptionItem item : items) {
                item.setDecryptedData(mCryptoManager.decrypt(item.getEncryptedData(), item.getIv()));
            }
            return items;
        });
    }

    public LiveData<List<FieldLevelEncryptionItem>> getAllItems() { return mAllItems; }

    public void onSaveClick() {
        String alias = aliasInput.getValue();
        String data = dataInput.getValue();
        if (alias != null && !alias.isEmpty() && data != null && !data.isEmpty()) {
            encryptAndInsert(alias, data);
            aliasInput.setValue("");
            dataInput.setValue("");
        }
    }

    private void encryptAndInsert(String alias, String plainText) {
        CryptoManager.EncryptedData result = mCryptoManager.encrypt(plainText);
        if (result != null) {
            FieldLevelEncryptionItem newItem = new FieldLevelEncryptionItem(alias, result.data, result.iv);
            mRepository.insert(newItem);
        }
    }

    public void deleteAll() { mRepository.deleteAll(); }
}


