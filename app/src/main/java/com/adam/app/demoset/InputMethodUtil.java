/**
 * reference: http://www.ccbu.cc/index.php/framework/modify-default-inputmethod.html
 */
package com.adam.app.demoset;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

public abstract class InputMethodUtil {
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
