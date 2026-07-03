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

package com.adam.app.demoset.bluetooth.repository;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.adam.app.demoset.bluetooth.model.BtState;
import com.adam.app.demoset.utils.DemoAppConstants;
import com.adam.app.demoset.utils.Utils;

import java.util.Arrays;

import javax.inject.Inject;

/**
 * BroadcastReceiver for handling Bluetooth state and discovery events.
 */
public class BTReceiver extends BroadcastReceiver {

    public static final String ACTION_FOUND_BT_DEVICE = DemoAppConstants.ACTION_FOUND_BT_DEVICE;
    public static final String ACTION_UPDATE_BT_BOUND_STATE = DemoAppConstants.ACTION_UPDATE_BT_BOUND_STATE;
    public static final String KEY_BT_DEVICE = DemoAppConstants.KEY_BT_DEVICE;
    public static final String KEY_BUNDLE_DEVICE = DemoAppConstants.KEY_BUNDLE_DEVICE;

    private final BluetoothRepository mRepository;

    @Inject
    public BTReceiver(BluetoothRepository repository) {
        this.mRepository = repository;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BTAction btAction = BTAction.by(action);
        if (btAction != null) {
            btAction.process(context, intent, mRepository);
        }
    }

    /**
     * Enumeration of Bluetooth actions and their processing logic.
     */
    private enum BTAction {
        STATE_CHANGED(BluetoothAdapter.ACTION_STATE_CHANGED) {
            @Override
            public void process(Context context, Intent intent, BluetoothRepository repository) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    Utils.showToast(context, "Bluetooth state ON");
                    repository.setBtState(BtState.ON);
                    repository.startDiscovery();
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    Utils.showToast(context, "Bluetooth state OFF");
                    repository.setBtState(BtState.OFF);
                }
            }
        },
        DISCOVERY_STARTED(BluetoothAdapter.ACTION_DISCOVERY_STARTED) {
            @Override
            public void process(Context context, Intent intent, BluetoothRepository repository) {
                repository.setBtState(BtState.SCANNING);
                repository.clearDiscoveredDevices();
            }
        },
        DISCOVERY_FINISHED(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
            @Override
            public void process(Context context, Intent intent, BluetoothRepository repository) {
                repository.setBtState(BtState.IDLE);
                // Notify UI via local broadcast if still needed, but Repository's LiveData is preferred
                Intent it = new Intent(ACTION_FOUND_BT_DEVICE);
                context.sendBroadcast(it);
            }
        },
        FOUND(BluetoothDevice.ACTION_FOUND) {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void process(Context context, Intent intent, BluetoothRepository repository) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice.class);
                if (device != null) {
                    repository.addDiscoveredDevice(device);
                }
            }
        },
        BOND_STATE_CHANGED(BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void process(Context context, Intent intent, BluetoothRepository repository) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice.class);
                if (device == null) return;

                Intent it = new Intent(ACTION_UPDATE_BT_BOUND_STATE);
                Bundle bundle = new Bundle();
                bundle.putParcelable(KEY_BT_DEVICE, device);
                it.putExtra(KEY_BUNDLE_DEVICE, bundle);
                context.sendBroadcast(it);
            }
        };

        private final String mKey;

        BTAction(String key) {
            this.mKey = key;
        }

        public static BTAction by(String key) {
            return Arrays.stream(BTAction.values())
                    .filter(act -> act.mKey.equals(key))
                    .findFirst()
                    .orElse(null);
        }

        public abstract void process(Context context, Intent intent, BluetoothRepository repository);
    }
}
