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
import com.adam.app.demoset.encryption.database.data.model.FullDbEncryptionItem;
import com.adam.app.demoset.encryption.database.data.repository.FullDbEncryptionRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FullDbEncryptionViewModel extends AndroidViewModel {
    private final FullDbEncryptionRepository mRepository;
    private final LiveData<List<FullDbEncryptionItem>> mAllItems;

    // For Data Binding
    public final MutableLiveData<String> aliasInput = new MutableLiveData<>("");
    public final MutableLiveData<String> dataInput = new MutableLiveData<>("");

    public FullDbEncryptionViewModel(@NonNull Application application) {
        super(application);
        // In a real app, the passphrase should be securely derived, e.g., from Keystore
        byte[] passphrase = "super_secret_passphrase".getBytes(StandardCharsets.UTF_8);
        mRepository = new FullDbEncryptionRepository(application, passphrase);
        mAllItems = mRepository.getAllItems();
    }

    public LiveData<List<FullDbEncryptionItem>> getAllItems() { return mAllItems; }

    public void onSaveClick() {
        String alias = aliasInput.getValue();
        String data = dataInput.getValue();
        if (alias != null && !alias.isEmpty() && data != null && !data.isEmpty()) {
            insert(alias, data);
            aliasInput.setValue("");
            dataInput.setValue("");
        }
    }

    private void insert(String alias, String secretInfo) {
        mRepository.insert(new FullDbEncryptionItem(alias, secretInfo));
    }

    public void deleteAll() { mRepository.deleteAll(); }
}


