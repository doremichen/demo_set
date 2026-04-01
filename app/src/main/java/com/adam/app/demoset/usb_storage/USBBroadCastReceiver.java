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

package com.adam.app.demoset.usb_storage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import androidx.annotation.NonNull;

import com.adam.app.demoset.utils.Utils;

public class USBBroadCastReceiver extends BroadcastReceiver {

    private UsbListener mUsbListener;


    public static final String USB_PERMISSION = "com.demo.app.usb.permission";

    /**
     * Receive action
     */
    private enum ACTION {
        // member
        DEFAULT(""){
            @Override
            public void process(Context ctx, Intent intent, UsbListener listener) {
                throw new RuntimeException("Not usb action!!!");
            }
        },
        USB_DEVICE_ATTACH(UsbManager.ACTION_USB_DEVICE_ATTACHED) {
            @Override
            public void process(Context ctx, Intent intent, UsbListener listener) {
                Utils.info(ACTION.class, "process USB_DEVICE_ATTACH");
                // get new usb device
                UsbDevice newDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (newDevice == null) {
                    Utils.info(ACTION.class, "No new usb device");
                    return;
                }

                // callback
                listener.onInsert(newDevice);
            }
        },
        USB_DEVICE_DETACH(UsbManager.ACTION_USB_DEVICE_DETACHED) {
            @Override
            public void process(Context ctx, Intent intent, UsbListener listener) {
                Utils.info(ACTION.class, "process USB_DEVICE_DETACH");
                // remove the specified usb device
                UsbDevice removeDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (removeDevice == null) {
                    Utils.info(ACTION.class, "No usb device removed!!!");
                    return;
                }

                // callback
                listener.onRemove(removeDevice);
            }
        },
        USB_PERMISSION(USBBroadCastReceiver.USB_PERMISSION) {
            @Override
            public void process(Context ctx, Intent intent, UsbListener listener) {
                Utils.info(ACTION.class, "process USB_PERMISSION");
                // get usb device that need to graint permisison
                UsbDevice graintDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (graintDevice == null) {
                    Utils.info(ACTION.class, "No usb device need to graint permission!!!");
                    return;
                }

                boolean needGraint = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                if (needGraint) {
                    // callback
                    listener.onHavePermission(graintDevice);
                } else {
                    // callback
                    listener.onFail(graintDevice);
                }
            }
        };

        private String mAction;
        private ACTION(String action) {
            this.mAction = action;
        }

        /**
         * Obtain action handler by action event
         * @param action: event of receiver
         * @return
         */
        public static ACTION by(String action) {
            // find action handler
            for (ACTION member: ACTION.values()) {
                if (member.equals(action)) {
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
        Utils.info(this, "onReceive");
        String action = intent.getAction();
        Utils.info(this, "action: " + action);
        ACTION act = ACTION.by(action);
        // process action
        act.process(context, intent, this.mUsbListener);
    }
}
