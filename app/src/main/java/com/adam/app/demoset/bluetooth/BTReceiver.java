package com.adam.app.demoset.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.adam.app.demoset.Utils;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class BTReceiver extends BroadcastReceiver {

    // Find bt device action
    public static final String ACTION_FOUND_BT_DEVICE = "find bt device";
    public static final String KEY_DEVICE_LIST = "device.list";

    // Update bt bound state
    public static final String ACTION_UPDATE_BT_BOUND_STATE = "bt bound state";
    public static final String KEY_BT_DEVICE = "bluetooth.device";
    public static final String KEY_BUNDLE_DEVICE = "bundle device";

    private WeakReference<DemoBTAct> mActRef;

    public BTReceiver(DemoBTAct act) {
        mActRef = new WeakReference<DemoBTAct>(act);
        BTController.INSTANCE.init(mActRef.get());
    }



    //Record the prev action in receiver
    private String preAction = "";

    /**
     * Bt action
     */
    private enum BTAction {

        STATE_CHANGED(BluetoothAdapter.ACTION_STATE_CHANGED) {
            @Override
            public void process(Context context, Intent intent) {
                Utils.info(this, "process");
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Utils.info(this, "state = " + state);

                // After on it enter to scan state
                if (state == BluetoothAdapter.STATE_ON) {
                    Utils.showToast(context, "bt state on");
                    BTController.INSTANCE.startDiscovery();
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    Utils.showToast(context, "bt state off");
                }
            }
        },
        DISCOVERY_STARTED(BluetoothAdapter.ACTION_DISCOVERY_STARTED) {
            @Override
            public void process(Context context, Intent intent) {
                Utils.info(this, "process");
                BTController.INSTANCE.showAlertDialog();
            }
        },
        DISCOVERY_FINISHED(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
            @Override
            public void process(Context context, Intent intent) {
                Utils.info(this, "process");
                BTController.INSTANCE.closeAlertDialog();

                // Send bt device list to UI receiver
                Intent it = new Intent();
                it.setAction(ACTION_FOUND_BT_DEVICE);
                it.putParcelableArrayListExtra(KEY_DEVICE_LIST, BTController.INSTANCE.getBluetoothDevices());
                context.sendBroadcast(it);
            }
        },
        FOUND(BluetoothDevice.ACTION_FOUND) {
            @Override
            public void process(Context context, Intent intent) {
                Utils.info(this, "process");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Bt device bound state
                int state = device.getBondState();
                if (state == BluetoothDevice.BOND_NONE) {
                    BTController.INSTANCE.getBluetoothDevices().add(device);
                }
            }
        },
        BOND_STATE_CHANGED(BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
            @Override
            public void process(Context context, Intent intent) {
                Utils.info(this, "process");
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Utils.info(this, "bound state: " + state);
                Utils.info(this, "bound prevstate: " + prevState);
                if (!Utils.areAllNotNull(device)) {
                    return;
                }

                Utils.info(this, "address: " + device.getAddress());

                // update bt information
                Intent it = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable(KEY_BT_DEVICE, device);
                it.putExtra(KEY_BUNDLE_DEVICE, bundle);
                it.setAction(ACTION_UPDATE_BT_BOUND_STATE);
                context.sendBroadcast(it);
            }
        };

        private String mKey;

        private BTAction(String key) {
            this.mKey = key;
        }

        public static BTAction by(String key) {
            return Arrays.stream(BTAction.values())
                    .filter(act -> act.mKey.equals(key))
                    .findFirst()
                    .orElse(null);
        }

        public abstract void process(Context context, Intent intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Utils.info(this, "[onReceive@BT receiver]");

        String action = intent.getAction();
        Utils.info(this, "BTReceiver action = " + action);

        BTAction btAction = BTAction.by(action);
        if (!Utils.areAllNotNull(btAction)) {
            return;
        }

        btAction.process(context, intent);

    }

}
