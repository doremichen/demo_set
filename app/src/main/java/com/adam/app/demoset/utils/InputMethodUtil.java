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

/**
 * reference: http://www.ccbu.cc/index.php/framework/modify-default-inputmethod.html
 */
package com.adam.app.demoset.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

public final class InputMethodUtil {

    private InputMethodUtil() {
        throw new UnsupportedOperationException("This is utils, u can't instantiate me...");
    }

    /**
     * get system default input method
     * @param context
     * @return
     */
    public static String getDefaultInputMethod(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
    }

    /**
     * Set system default input method
     * @param context
     * @param inputMethod： input method
     * @return
     */
    public static boolean setDefaultInputMethod(Context context, String inputMethod) {
        return Settings.Secure.putString(context.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD,
                inputMethod);
    }

    /**
     * Gets the input method that the system has installed
     *
     * @param context
     * @return installed input method list
     */
    public static String[] getInputMethodIdList(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.getInputMethodList() != null) {
            String[] methodIds = new String[imm.getInputMethodList().size()];
            for (int i = 0; i < imm.getInputMethodList().size(); i++) {
                methodIds[i] = imm.getInputMethodList().get(i).getId();
            }
            return methodIds;
        }
        return new String[] {};
    }

    /**
     * update system input method with checking
     * @param context
     * @param inputMethod
     * @return
     */
    public static boolean updateDefaultInputMethod(Context context, String inputMethod) {
        if (context != null && !TextUtils.isEmpty(inputMethod)) {
            String current = getDefaultInputMethod(context);
            if (current != null && !current.equalsIgnoreCase(inputMethod)) {
                String packageName = inputMethod.substring(0, inputMethod.indexOf('/'));
                android.content.pm.ApplicationInfo info = null;
                try {
                    info = context.getPackageManager().getApplicationInfo(packageName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (info != null) {
                    return setDefaultInputMethod(context, inputMethod);
                }
            }
        }

        return false;
    }
}
