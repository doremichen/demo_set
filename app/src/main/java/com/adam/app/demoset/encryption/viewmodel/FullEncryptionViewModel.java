/*
 * Copyright (c) 2024 Adam Chen
 */

package com.adam.app.demoset.encryption.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.adam.app.demoset.encryption.data.model.FullEncryptionItem;
import com.adam.app.demoset.encryption.data.repository.FullEncryptionRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FullEncryptionViewModel extends AndroidViewModel {
    private final FullEncryptionRepository mRepository;
    private final LiveData<List<FullEncryptionItem>> mAllItems;

    // For Data Binding
    public final MutableLiveData<String> aliasInput = new MutableLiveData<>("");
    public final MutableLiveData<String> dataInput = new MutableLiveData<>("");

    public FullEncryptionViewModel(@NonNull Application application) {
        super(application);
        // In a real app, the passphrase should be securely derived, e.g., from Keystore
        byte[] passphrase = "super_secret_passphrase".getBytes(StandardCharsets.UTF_8);
        mRepository = new FullEncryptionRepository(application, passphrase);
        mAllItems = mRepository.getAllItems();
    }

    public LiveData<List<FullEncryptionItem>> getAllItems() { return mAllItems; }

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
        mRepository.insert(new FullEncryptionItem(alias, secretInfo));
    }

    public void deleteAll() { mRepository.deleteAll(); }
}
