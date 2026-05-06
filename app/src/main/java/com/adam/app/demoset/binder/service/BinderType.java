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

package com.adam.app.demoset.binder.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.adam.app.demoset.binder.IMyAidlCBInterface;
import com.adam.app.demoset.binder.IMyAidlInterface;
import com.adam.app.demoset.utils.Utils;

/**
 * Enum defining the types of Binder communication supported.
 * Encapsulates the connection logic and execution for each type.
 */
public enum BinderType {
    AIDL {
        private IMyAidlInterface mProxy;
        private final IMyAidlCBInterface mAidlCB = new AidlServiceCB();
        private Callback mCallback;

        @Override
        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        @Override
        public void execute(int a, int b) {
            if (mProxy == null) {
                logToUi("AIDL Proxy not connected");
                return;
            }

            logToUi("Executing AIDL call: add(" + a + ", " + b + ")");
            try {
                mProxy.add(a, b);
                mProxy.sendRequest(new MyBinderData("AIDL Request Data"));
            } catch (RemoteException e) {
                Utils.error(this, "AIDL execution failed: " + e.getMessage());
            }
        }

        @Override
        public ServiceConnection getConnect() {
            return new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    logToUi("AIDL Service Connected");
                    mProxy = IMyAidlInterface.Stub.asInterface(service);
                    try {
                        mProxy.registerServiceCB(mAidlCB);
                    } catch (RemoteException e) {
                        Utils.error(this, "Failed to register AIDL callback: " + e.getMessage());
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    logToUi("AIDL Service Disconnected");
                    mProxy = null;
                }
            };
        }

        private void logToUi(String msg) {
            if (mCallback != null) {
                mCallback.showLog(msg);
            }
        }

        private class AidlServiceCB extends IMyAidlCBInterface.Stub {
            @Override
            public void result(int c) {
                if (mCallback != null) {
                    mCallback.result(c);
                }
            }
        }
    },

    MESSENGER {
        private Messenger mProxy;
        private final Messenger mUIMessenger = new Messenger(new CallbackHandler());
        private Callback mCallback;

        @Override
        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        @Override
        public void execute(int a, int b) {
            if (mProxy == null) {
                logToUi("Messenger Proxy not connected");
                return;
            }

            logToUi("Executing Messenger call: add(" + a + ", " + b + ")");
            try {
                Message msg = Message.obtain(null, MyMessengerService.ACTION_ADD, a, b);
                msg.replyTo = mUIMessenger;
                mProxy.send(msg);
            } catch (RemoteException e) {
                Utils.error(this, "Messenger execution failed: " + e.getMessage());
            }
        }

        @Override
        public ServiceConnection getConnect() {
            return new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    logToUi("Messenger Service Connected");
                    mProxy = new Messenger(service);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    logToUi("Messenger Service Disconnected");
                    mProxy = null;
                }
            };
        }

        private void logToUi(String msg) {
            if (mCallback != null) {
                mCallback.showLog(msg);
            }
        }

        private class CallbackHandler extends Handler {
            CallbackHandler() {
                super(Looper.getMainLooper());
            }

            @Override
            public void handleMessage(@NonNull Message msg) {
                if (mCallback != null && msg.what == MyMessengerService.ACTION_REPLY_RESULT) {
                    mCallback.result(msg.arg1);
                }
            }
        }
    };

    /**
     * Interface for communicating results back to the caller.
     */
    public interface Callback {
        void result(int value);
        void showLog(String message);
    }

    public abstract void setCallback(Callback callback);
    public abstract void execute(int a, int b);
    public abstract ServiceConnection getConnect();
}
