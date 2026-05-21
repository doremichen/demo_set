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
package com.adam.app.demoset.encryption.sharedprefs.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.R;
import com.adam.app.demoset.encryption.sharedprefs.controller.EncryptedPrefsManager;

public class EncryptedPrefsViewModel extends AndroidViewModel {

    private EncryptedPrefsManager mManager;

    public final MutableLiveData<String> mKeyInput = new MutableLiveData<>("");
    public final MutableLiveData<String> mValueInput = new MutableLiveData<>("");
    
    public final MutableLiveData<String> mNormalValue = new MutableLiveData<>("");
    public final MutableLiveData<String> mDecryptedValue = new MutableLiveData<>("");
    public final MutableLiveData<String> mRawEncryptedValue = new MutableLiveData<>("");
    
    public final MutableLiveData<String> mLoadResultHeader = new MutableLiveData<>("");
    public final MutableLiveData<String> mStatusMessage = new MutableLiveData<>("");

    private final MutableLiveData<Boolean> mHideKeyboard = new MutableLiveData<>(false);

    public LiveData<Boolean> isHideKeyboard() {
        return mHideKeyboard;
    }

    public void ConsumeHideKeyboardEvent() {
        mHideKeyboard.setValue(false);
    }

    public EncryptedPrefsViewModel(@NonNull Application application) {
        super(application);
        try {
            mManager = new EncryptedPrefsManager(application);
        } catch (Exception e) {
            mStatusMessage.setValue(getApplication().getString(R.string.msg_prefs_init_error, e.getMessage()));
        }
    }

    public void onSaveClicked() {
        mHideKeyboard.setValue(true);
        String key = mKeyInput.getValue();
        String value = mValueInput.getValue();
        if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
            mStatusMessage.setValue(getApplication().getString(R.string.msg_prefs_enter_key_value));
            return;
        }

        mManager.saveToNormal(key, value);
        mManager.saveToEncrypted(key, value);
        
        mStatusMessage.setValue(getApplication().getString(R.string.msg_prefs_saved_success));
        refreshValues(key);
    }

    public void onLoadClicked() {
        mHideKeyboard.setValue(true);
        String key = mKeyInput.getValue();
        if (key == null || key.isEmpty()) {
            mStatusMessage.setValue(getApplication().getString(R.string.msg_prefs_enter_key_load));
            return;
        }
        refreshValues(key);
    }

    private void refreshValues(String key) {
        mLoadResultHeader.setValue(getApplication().getString(R.string.label_prefs_load_result_title, key));
        mNormalValue.setValue(mManager.getFromNormal(key));
        mDecryptedValue.setValue(mManager.getFromEncrypted(key));
        mRawEncryptedValue.setValue(mManager.getRawEncryptedString(key));
    }

    public void onClearClicked() {
        mHideKeyboard.setValue(true);
        mManager.clearAll();
        mNormalValue.setValue("");
        mDecryptedValue.setValue("");
        mRawEncryptedValue.setValue("");
        mLoadResultHeader.setValue("");
        mStatusMessage.setValue(getApplication().getString(R.string.msg_prefs_cleared_success));
    }
}
