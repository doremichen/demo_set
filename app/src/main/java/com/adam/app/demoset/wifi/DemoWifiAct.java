package com.adam.app.demoset.wifi;

import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.util.ArrayList;

public class DemoWifiAct extends AppCompatActivity implements WifiController.CallBack,
        MyTouchItemListener.onItemClickListener {

    public static int SCAN_RESULT = 0;
    public static int CONNECT_RESULT = 1;

    public static String KEY_SSID = "key.ssid";
    public static String KEY_INFO = "key.network.info";


    private RecyclerView mListAp;
    private TextView mEmptyAp;
    private MyTouchItemListener mTouchListener;

    private ArrayList<String> mSSIDs;

    private ApListAdapter mListAdapter;

    private boolean mIsAllow;

    // Wifi permission
    private static final String[] WIFI_PERMISSION = {
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static final int WIFI_PERMISSION_RESULT_CODE = 0x5487;

    // Handle UI process
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Utils.inFo(this, "[handleMessage] msg.what = " + msg.what);
            if (msg.what == SCAN_RESULT) {
                String name = (String)msg.getData().get(KEY_SSID);

                //Update list view
                mSSIDs.add(name);
                mListAdapter.notifyDataSetChanged();

                showEmptyIfNoData();
            } else if (msg.what == CONNECT_RESULT) {
                String result = (String)msg.getData().get(KEY_INFO);

                // Show info
                Utils.showAlertDialog(DemoWifiAct.this, result, null);
            }
        }
    };

    // Wifi receiver
    private WifiReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo_wifi);

        // Check permission
        if (Utils.askPermission(this, WIFI_PERMISSION, WIFI_PERMISSION_RESULT_CODE)) {
            // Permission is granted
            mIsAllow = true;
        }

        initData();
        registerWifiAction();

    }



    @Override
    protected void onResume() {
        super.onResume();
        Utils.inFo(this, "onResume enter");
        if (mIsAllow) {
            showEmptyIfNoData();
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        Utils.inFo(this, "onPause enter");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.inFo(this, "onDestroy enter");
        // unregister receiver
        unregisterReceiver(mReceiver);

        mTouchListener.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_only_exit_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.demo_exit:
                this.finish();
                return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Utils.inFo(this, "onRequestPermissionsResult enter");
        if (requestCode == WIFI_PERMISSION_RESULT_CODE) {
            if (grantResults.length == WIFI_PERMISSION.length) {
                for (int result: grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Utils.inFo(this, "result is not PackageManager.PERMISSION_GRANTED");
                        mIsAllow = false;
                        // permission denied
                        this.finish();
                        break;
                    } else {
                        Utils.inFo(this, "result is PackageManager.PERMISSION_GRANTED");
                        mIsAllow = true;
                    }
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //
    // This method would be invoked when use press
    // the start scan button
    //
    public void startScanAction(View view) {
        Utils.showToast(this, "start scan....");
        WifiController.newInstance().startScan();
    }

    @Override
    public void onInfo(String msg) {
        Utils.showToast(this, msg);
    }


    @Override
    public void onLongClick(int position) {
        Utils.inFo(this, "[onLongClick] enter");
        // Show dialog to tell user connect information
        String info = mSSIDs.get(position);
        //
        mHandler.post(new ShowConnectInfoTask(info));

    }

    // ================================================
    // private subRoutine
    // ================================================

    class ShowConnectInfoTask implements Runnable {

        private String mInfo;

        ShowConnectInfoTask(String info) {
            mInfo = info;
        }

        @Override
        public void run() {
           Utils.inFo(this, "[run] enter");

            Utils.showAlertDialog(DemoWifiAct.this, "Connect to " + mInfo, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Utils.showToast(DemoWifiAct.this, "Click....");
                    // Connect to AP
                    WifiController.newInstance().connectToAP(mInfo, "pass");
                }
            });
        }
    }



    //
    // initialize data
    //
    private void initData() {
        mListAp = findViewById(R.id.list_ap);
        mEmptyAp = findViewById(R.id.empty_ap_view);

        mSSIDs = new ArrayList<>();

        // init WifiController
        WifiController.newInstance().init(getApplicationContext(), this);

        mReceiver = new WifiReceiver(mHandler);

        // List adapter
        mListAdapter = new ApListAdapter(this);
        mListAdapter.setData(mSSIDs);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mListAp.setLayoutManager(mLayoutManager);
        mListAp.setItemAnimator(new DefaultItemAnimator());
        mListAp.setAdapter(mListAdapter);

        mTouchListener = new MyTouchItemListener();
        mTouchListener.setonItemClickListener(this);
        mListAp.addOnItemTouchListener(mTouchListener);

    }

    //
    // register wifi action
    //
    private void registerWifiAction() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);

        // register receiver
        registerReceiver(mReceiver, intentFilter);
    }


    //
    // Show empty view when no ap data
    //
    private void showEmptyIfNoData() {
        Utils.inFo(this, "[showEmptyIfNoData] enter");
        if (mSSIDs.isEmpty()) {
            mEmptyAp.setVisibility(View.VISIBLE);
            mListAp.setVisibility(View.GONE);
        } else {
            mEmptyAp.setVisibility(View.GONE);
            mListAp.setVisibility(View.VISIBLE);
        }
        Utils.inFo(this, "[showEmptyIfNoData] exit");
    }



}
