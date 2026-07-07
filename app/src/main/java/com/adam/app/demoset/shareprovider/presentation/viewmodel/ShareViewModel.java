/*
 * Copyright (c) 2026 Adam Chen
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

package com.adam.app.demoset.shareprovider.presentation.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.adam.app.demoset.R;
import com.adam.app.demoset.shareprovider.domain.model.ShareContent;
import com.adam.app.demoset.shareprovider.domain.usecase.GetShareIntentUseCase;
import com.adam.app.demoset.utils.DemoAppConstants;
import com.adam.app.demoset.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for managing the state of the Share Provider demo.
 * It directly manages ShareActionProvider to encapsulate logic.
 */
@HiltViewModel
public class ShareViewModel extends AndroidViewModel {

    private final GetShareIntentUseCase mGetShareIntentUseCase;

    // Two-way binding for text input
    private final MutableLiveData<String> mShareText = new MutableLiveData<>("Check out this awesome Android Demo!");
    
    // UI state for content type
    private final MutableLiveData<Boolean> mIsImageType = new MutableLiveData<>(false);
    
    // Internal state storage for current Intent
    private Intent mCurrentIntent;

    // Event for manual sharing
    private final MutableLiveData<Intent> mManualShareEvent = new MutableLiveData<>();

    // Event for closing the demo
    private final MutableLiveData<Boolean> mExitEvent = new MutableLiveData<>();

    // ShareActionProvider managed by ViewModel
    private ShareActionProvider mShareActionProvider;

    // Observers for internal reactive logic
    private final Observer<String> mTextObserver = s -> updateIntent();
    private final Observer<Boolean> mTypeObserver = b -> updateIntent();

    /**
     * Constructor for ShareViewModel.
     * @param application application
     * @param getShareIntentUseCase getShareIntentUseCase
     */
    @Inject
    public ShareViewModel(@NonNull Application application, GetShareIntentUseCase getShareIntentUseCase) {
        super(application);
        this.mGetShareIntentUseCase = getShareIntentUseCase;
        
        // Setup internal observers to keep Intent in sync with UI state
        mShareText.observeForever(mTextObserver);
        mIsImageType.observeForever(mTypeObserver);
        
        updateIntent();
    }

    /**
     * Sets the ShareActionProvider and updates it with the current intent.
     * @param provider ShareActionProvider from the Activity's menu.
     */
    public void setShareActionProvider(ShareActionProvider provider) {
        this.mShareActionProvider = provider;
        if (mShareActionProvider != null && mCurrentIntent != null) {
            mShareActionProvider.setShareIntent(mCurrentIntent);
        }
    }

    public MutableLiveData<String> getShareText() {
        return mShareText;
    }

    public MutableLiveData<Boolean> getIsImageType() {
        return mIsImageType;
    }

    public LiveData<Intent> getManualShareEvent() {
        return mManualShareEvent;
    }

    public LiveData<Boolean> getExitEvent() {
        return mExitEvent;
    }

    /**
     * Synchronize the current UI state to generate a new Share Intent.
     */
    public void updateIntent() {
        ShareContent content;
        if (Boolean.TRUE.equals(mIsImageType.getValue())) {
            Uri imageUri = prepareSampleImage();
            content = (imageUri != null) ? new ShareContent(imageUri) : new ShareContent(mShareText.getValue());
        } else {
            content = new ShareContent(mShareText.getValue());
        }
        
        mCurrentIntent = mGetShareIntentUseCase.execute(content);
        
        // Dynamically update the provider if it exists
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(mCurrentIntent);
        }
    }

    /**
     * Prepares a sample image for sharing using FileProvider.
     * @return Content Uri of the image.
     */
    private Uri prepareSampleImage() {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.test);
            File cachePath = new File(getApplication().getCacheDir(), DemoAppConstants.SHARE_CACHE_DIR);
            if (!cachePath.exists() && !cachePath.mkdirs()) {
                Utils.info(this, "Failed to create cache directory");
                return null;
            }
            
            File file = new File(cachePath, DemoAppConstants.SHARE_FILE_NAME);
            try (FileOutputStream stream = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            }

            return FileProvider.getUriForFile(getApplication(), 
                DemoAppConstants.AUTHORITY_FILE_PROVIDER, file);
        } catch (IOException e) {
            Utils.info(this, "Error preparing sample image: " + e.getMessage());
            return null;
        }
    }

    public void onShareClicked() {
        updateIntent();
        mManualShareEvent.setValue(mCurrentIntent);
    }

    public void onExitClicked() {
        mExitEvent.setValue(true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Remove observers to prevent memory leaks
        mShareText.removeObserver(mTextObserver);
        mIsImageType.removeObserver(mTypeObserver);
    }
}
