/*
 * Copyright (c) 2024 Adam Chen
 */

package com.adam.app.demoset.encryption.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.adam.app.demoset.encryption.controller.CryptoManager;
import com.adam.app.demoset.encryption.data.model.EncryptionItem;
import com.adam.app.demoset.encryption.data.repository.EncryptionRepository;
import java.util.List;

public class EncryptionViewModel extends AndroidViewModel {
    private final EncryptionRepository mRepository;
    private final CryptoManager mCryptoManager;
    private final LiveData<List<EncryptionItem>> mAllItems;

    // For Data Binding
    public final MutableLiveData<String> aliasInput = new MutableLiveData<>("");
    public final MutableLiveData<String> dataInput = new MutableLiveData<>("");

    public EncryptionViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EncryptionRepository(application);
        mCryptoManager = new CryptoManager();
        mAllItems = Transformations.map(mRepository.getAllItems(), items -> {
            for (EncryptionItem item : items) {
                item.setDecryptedData(mCryptoManager.decrypt(item.getEncryptedData(), item.getIv()));
            }
            return items;
        });
    }

    public LiveData<List<EncryptionItem>> getAllItems() { return mAllItems; }

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
            EncryptionItem newItem = new EncryptionItem(alias, result.data, result.iv);
            mRepository.insert(newItem);
        }
    }

    public void deleteAll() { mRepository.deleteAll(); }
}
