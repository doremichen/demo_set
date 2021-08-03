//
// This receiver must be registered dynamically
//
package com.adam.app.demoset.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.adam.app.demoset.Utils;

import java.util.HashMap;
import java.util.List;

public class WifiReceiver extends BroadcastReceiver {

    // Build action map
    static HashMap<String, WifiAction> mActMap = new HashMap<String, WifiAction>();

    static {
        mActMap.put(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION, new ScanAction());
        mActMap.put(WifiManager.NETWORK_STATE_CHANGED_ACTION, new NetWorkSateAction());
    }

    private Handler mUIHandler;

    public WifiReceiver(Handler handler) {
        mUIHandler = handler;

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.info(this, "[onReceive] enter action = " + intent.getAction());

        WifiAction action = mActMap.get(intent.getAction());
        if (action != null) {
            action.process(intent, mUIHandler);
        }
    }

    private abstract static class WifiAction {
        abstract void process(Intent intent, Handler handler);

        protected void sendInfoToUI(Handler handler, String key, String value, int result) {
            Utils.info(this, "[sendInfoToUI] enter");
            Bundle bundle = new Bundle();
            bundle.putString(key, value);
            Message msg = Message.obtain(null, result);
            msg.setData(bundle);
            // Send message to ui handler
            handler.sendMessage(msg);
        }
    }

    //
    // Used to handle scan result
    //
    private static class ScanAction extends WifiAction {

        @Override
        public void process(Intent intent, Handler handler) {
            Utils.info(this, "[process] enter");
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success == true) {
                scanSuccess(handler);
            } else {
                scanFail(handler);
            }
        }

        private void scanSuccess(Handler handler) {
            Utils.info(this, "[scanSuccess] enter");
            List<ScanResult> scanResults = WifiController.newInstance().getResult();
            for (ScanResult result : scanResults) {

                String scanResult = result.SSID;
                sendInfoToUI(handler, DemoWifiAct.KEY_SSID, scanResult, DemoWifiAct.SCAN_RESULT);
            }

        }

        private void scanFail(Handler handler) {
            Utils.info(this, "[scanFail] enter");
        }

    }

    //
    // Used to handle network state changed
    //
    private static class NetWorkSateAction extends WifiAction {

        @Override
        public void process(Intent intent, Handler handler) {
            Utils.info(this, "[process] enter");
            // NetworkInfo
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                Utils.info(this, "Network state is connected...");
                sendInfoToUI(handler, DemoWifiAct.KEY_INFO, info.toString(), DemoWifiAct.CONNECT_RESULT);
            }
        }
    }
}
