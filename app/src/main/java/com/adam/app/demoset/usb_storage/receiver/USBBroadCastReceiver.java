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

package com.adam.app.demoset.usb_storage.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import androidx.annotation.NonNull;
import androidx.core.content.IntentCompat;

import com.adam.app.demoset.utils.Utils;

public class USBBroadCastReceiver extends BroadcastReceiver {

    private UsbListener mUsbListener;


    public static final String USB_PERMISSION = "com.demo.app.usb.permission";

    /**
     * Receive action
     */
    private enum UsbAction {
        // member
        DEFAULT(""){
            @Override
            public void process(Context ctx, Intent intent, UsbListener listener) {
                Utils.info(UsbAction.class, "Unknown usb action: " + intent.getAction());
            }
        },
        USB_DEVICE_ATTACH(UsbManager.ACTION_USB_DEVICE_ATTACHED) {
            @Override
            public void process(Context ctx, Intent intent, UsbListener listener) {
                Utils.info(UsbAction.class, "process USB_DEVICE_ATTACH");
                // get new usb device
                UsbDevice newDevice = IntentCompat.getParcelableExtra(intent, UsbManager.EXTRA_DEVICE, UsbDevice.class);
                if (newDevice == null) {
                    Utils.info(UsbAction.class, "No new usb device");
                    return;
                }

                // callback
                listener.onInsert(newDevice);
            }
        },
        USB_DEVICE_DETACH(UsbManager.ACTION_USB_DEVICE_DETACHED) {
            @Override
            public void process(Context ctx, Intent intent, UsbListener listener) {
                Utils.info(UsbAction.class, "process USB_DEVICE_DETACH");
                // remove the specified usb device
                UsbDevice removeDevice = IntentCompat.getParcelableExtra(intent, UsbManager.EXTRA_DEVICE, UsbDevice.class);
                if (removeDevice == null) {
                    Utils.info(UsbAction.class, "No usb device removed!!!");
                    return;
                }

                // callback
                listener.onRemove(removeDevice);
            }
        },
        USB_PERMISSION(USBBroadCastReceiver.USB_PERMISSION) {
            @Override
            public void process(Context ctx, Intent intent, UsbListener listener) {
                Utils.info(UsbAction.class, "process USB_PERMISSION");
                // get usb device that need to grant permission
                UsbDevice grantDevice = IntentCompat.getParcelableExtra(intent, UsbManager.EXTRA_DEVICE, UsbDevice.class);
                if (grantDevice == null) {
                    Utils.info(UsbAction.class, "No usb device need to grant permission!!!");
                    return;
                }

                boolean isGranted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                if (isGranted) {
                    // callback
                    listener.onHavePermission(grantDevice);
                } else {
                    // callback
                    listener.onFail(grantDevice);
                }
            }
        };

        private final String mAction;
        private UsbAction(String action) {
            this.mAction = action;
        }

        /**
         * Obtain action handler by action event
         * @param action: event of receiver
         * @return
         */
        public static UsbAction by(String action) {
            // find action handler
            for (UsbAction member: UsbAction.values()) {
                if (member.mAction.equals(action)) {
                    return member;
                }
            }
            return  DEFAULT;
        }


        public abstract void process(Context ctx, Intent intent, UsbListener listener);
    }

    /**
     * Usb device Listener
     */
    public interface UsbListener {
        // insert Usb
        void onInsert(UsbDevice device);
        // remove Usb
        void onRemove(UsbDevice device);
        // obtain usb permission
        void onHavePermission(UsbDevice device);
        // operator of Usb fail
        void onFail(UsbDevice device);
    }

    /**
     * Set usb listener
     * @param listener
     */
    public void setListener(@NonNull UsbListener listener) {
        this.mUsbListener = listener;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.info(this, "onReceive action: " + intent.getAction());
        String action = intent.getAction();
        UsbAction act = UsbAction.by(action);
        // process action
        act.process(context, intent, this.mUsbListener);
    }
}
