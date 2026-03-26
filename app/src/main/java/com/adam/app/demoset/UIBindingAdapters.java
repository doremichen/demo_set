/**
 * Copyright (C) Adam demo app Project. All rights reserved.
 * <p>
 * Description: This class is the UI binding adapters.
 * </p>
 * <p>
 * Author: Adam Chen
 * Date: 2026/03/17
 */
package com.adam.app.demoset;

import android.view.View;

import androidx.databinding.BindingAdapter;

public class UIBindingAdapters {
    @BindingAdapter(value = {"android:onClick", "hideKeyboardOnClick"}, requireAll = false)
    public static void setHideKeyboardOnClick(View view, View.OnClickListener listener, boolean hide) {
        view.setOnClickListener(v -> {
            if (hide) {
                Utils.hideSoftKeyBoardFrom(view.getContext(), view);
            }

            if (listener != null) {
                listener.onClick(v);
            }
        });


    }
}
