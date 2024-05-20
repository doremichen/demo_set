/**
 * UI
 */
package com.adam.app.demoset.wifi2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DemoWifiAct2 extends AppCompatActivity implements WifiController.WifiScanListener {

    public static final int REQUEST_WIFI_PERMISSION_CODE = 0x1357;
    private WifiBroadcastReceiver mWifiReceiv = new WifiBroadcastReceiver();
    private WifiController mWifiCtl;
    private ApListAdapter mAdapter;

    private static final String[] WIFI_PERMISSION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private boolean mPermissionGranted;

    private HandlerThread mScanHandlerThread;

    // scan wifi handler
    private Handler mHandler;

    private static final int SCAN_WIFI = 1;


    private WifiConnectDialog.DialogListener mListner = new WifiConnectDialog.DialogListener() {
        @Override
        public void onResult(WifiConnectData data) {

            DemoWifiAct2.this.mWifiCtl.connectWifiAfterQ(data.getSsid(), data.getPassword(), new WifiController.ConnectListener() {
                @Override
                public void onSuccess() {
                    Utils.showToast(DemoWifiAct2.this, "onSuccess");
                }

                @Override
                public void onFail(String msg) {
                    Utils.showToast(DemoWifiAct2.this, "onFail: " + msg);
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "onCreate");
        setContentView(R.layout.activity_demo_wifi_act2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Utils.askPermission(this, WIFI_PERMISSION, REQUEST_WIFI_PERMISSION_CODE)) {
            Utils.info(this, "permission granted!!!");
            this.mPermissionGranted = true;
            // initial wifi controller
            initScanWifiTask();

        }



        //initial list view
        RecyclerView recyclerView = findViewById(R.id.wifi_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // add divided line
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        this.mAdapter = new ApListAdapter(new ApListAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(ScanResult result) {
                Utils.showToast(DemoWifiAct2.this, "result: " + result.toString());
                // start edit dialog to get address
                WifiConnectDialog dialog = new WifiConnectDialog(DemoWifiAct2.this, result, DemoWifiAct2.this.mListner);
                dialog.create().show();
            }
        });
        recyclerView.setAdapter(this.mAdapter);


        Button scanWifibtn = findViewById(R.id.btn_scan_wifi);
        scanWifibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.info(this, "click scan wifi!!!");
                Utils.showToast(DemoWifiAct2.this, "click scan wifi!!!");
                DemoWifiAct2.this.mHandler.sendEmptyMessage(SCAN_WIFI);
            }
        });

        Button exitWifibtn = findViewById(R.id.btn_exit_wifi);
        exitWifibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // finish
                DemoWifiAct2.this.finish();
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // register
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            this.getApplicationContext().registerReceiver(this.mWifiReceiv, filter, RECEIVER_EXPORTED);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        Utils.info(this, "onResume");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Utils.info(this, "onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // quit
        this.mScanHandlerThread.quit();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.mWifiCtl.disconnectWifiAfterQ();
        } else {
            this.mWifiCtl.disconnectWifiBeforeQ();
            // unregister
            this.getApplicationContext().unregisterReceiver(this.mWifiReceiv);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WIFI_PERMISSION_CODE) {
            if (grantResults.length == WIFI_PERMISSION.length) {
                int count = 0;
                for (int result : grantResults) {
                    Utils.info(this, "result = " + result);
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Utils.showToast(this, "Camera permission is not granted");
                    } else {
                        count++;
                    }
                }
                if (count == grantResults.length) {
                    Utils.showToast(this, "Camera permission is granted");
                    this.mPermissionGranted = true;
                    initScanWifiTask();
                }
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onUpdateWifiList(List<ScanResult> list) {
        Utils.info(this, "onUpdateWifiList");
        // update list
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DemoWifiAct2.this.mAdapter.updateList(list);
            }
        });
    }




    /**
     * Receive wifi/network state
     */
    private class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        Utils.showToast(DemoWifiAct2.this, "wifi state disabled!!!");
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        Utils.showToast(DemoWifiAct2.this, "wifi state disabling!!!");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        Utils.showToast(DemoWifiAct2.this, "wifi state enabled!!!");
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        Utils.showToast(DemoWifiAct2.this, "wifi state enabling!!!");
                        break;
                }
            } else if ((WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()))) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                assert info != null;
                switch (info.getState()) {
                    case DISCONNECTED:
                        Utils.showToast(DemoWifiAct2.this, "network disconnected!!!");
                        break;
                    case CONNECTED:
                        Utils.showToast(DemoWifiAct2.this, "network connected!!!");
                        break;
                    case CONNECTING:
                        Utils.showToast(DemoWifiAct2.this, "network connecting!!!");
                        break;
                }
            }
        }
    }

    /**
     * Initial wifi module
     */
    private void initScanWifiTask() {
        this.mWifiCtl = new WifiController(this);

        this.mScanHandlerThread = new HandlerThread("scan_wifi_thread");
        // start
        this.mScanHandlerThread.start();
        this.mHandler = new Handler(this.mScanHandlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Utils.info(this, "start to scan@handleMessage");
                // start scan wifi
                assert DemoWifiAct2.this.mWifiCtl != null;
                DemoWifiAct2.this.mWifiCtl.wifiScan(DemoWifiAct2.this);
            }
        };
    }

}