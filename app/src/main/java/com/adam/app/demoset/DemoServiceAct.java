/**
 * The UI contains some service's demo
 * <p>
 * info:
 *
 * @author: AdamChen
 * @date: 2018/9/19
 */

package com.adam.app.demoset;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;
import java.util.Arrays;


public class DemoServiceAct extends AppCompatActivity {

    private ListView mListView;

    private RelativeLayout mLayout;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (Utils.ACTION_SHOW_SNACKBAR.equals(action)) {
                String message = intent.getStringExtra(Utils.KEY_MSG);

                setSnackMessage(message);
            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_svr);

        Item.setActivityContext(this);

        mListView = this.findViewById(R.id.list_action);
        mLayout = this.findViewById(R.id.content_view);

        // Covert arrayList to array string
        String[] items = Arrays.stream(Item.values())
                .map(Item::getType)
                .toArray(String[]::new);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                Utils.showToast(DemoServiceAct.this, "Item: " + item);
                Item executeItem = Item.getItemBy(item);
                if (Utils.areAllNotNull(executeItem)) {
                    executeItem.execute();
                }
            }
        });

        // register show snackbar receiver
        this.registerReceiver(this.mReceiver, new IntentFilter(Utils.ACTION_SHOW_SNACKBAR), RECEIVER_NOT_EXPORTED);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unregister show snackbar receiver
        this.unregisterReceiver(this.mReceiver);

        if (Utils.sIsBound) {
            this.unbindService(Utils.sConnection);
            Utils.sIsBound = false;
            if (Utils.sIsRemoteService) {
                Utils.sMessenger = null;
            } else {
                Utils.sLocalSvr = null;
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.local_svr:
                Utils.showToast(this, "Local service is configured.");
                Utils.sIsRemoteService = false;
                return true;
            case R.id.remote_svr:
                Utils.showToast(this, "Remote service is configured.");
                Utils.sIsRemoteService = true;
                return true;
            case R.id.exit:
                this.finish();
                return true;
        }

        return false;

    }

    private void setSnackMessage(String status) {

        Snackbar.make(this.mLayout, "Service status: " + status, Snackbar.LENGTH_SHORT).show();


    }

    private enum Item {
        START_SERVICE(Utils.ITEM_START_SERVICE) {
            @Override
            public void execute() {
                Intent it = new Intent();
                if (Utils.sIsRemoteService) {
                    it.setClassName(sActRef.get().getPackageName(), RemoteService.class.getName());
                } else {
                    it.setClassName(sActRef.get().getPackageName(), LocalService.class.getName());
                }
                sActRef.get().startService(it);

            }
        }, STOP_SERVICE(Utils.ITEM_STOP_SERVICE) {
            @Override
            public void execute() {
                Intent it = new Intent();
                if (Utils.sIsRemoteService) {
                    it.setClassName(sActRef.get().getPackageName(), RemoteService.class.getName());
                } else {
                    it.setClassName(sActRef.get().getPackageName(), LocalService.class.getName());
                }
                sActRef.get().stopService(it);
            }
        }, BIND_SERVICE(Utils.ITEM_BIND_SERVICE) {

            private LocalService mLocalService;
            private Messenger mService;

            private ServiceConnection mLocalConnection = new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {

                    mLocalService = ((LocalService.LocalBinder) service).getService();
                    Utils.sLocalSvr = mLocalService;
                    Utils.info(this, "service connect...");

                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                    mLocalService = null;
                    Utils.sLocalSvr = null;
                    Utils.info(this, "service disconnect...");

                }
            };

            private ServiceConnection mRemoteConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mService = new Messenger(service);
                    Utils.sMessenger = mService;
                    Utils.info(this, "service connect...");
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mService = null;
                    Utils.sMessenger = null;
                    Utils.info(this, "service disconnect...");
                }
            };

            @Override
            public void execute() {
                Intent it = new Intent();
                if (Utils.sIsRemoteService) {
                    it.setClassName(sActRef.get().getPackageName(), RemoteService.class.getName());
                    Utils.sConnection = mRemoteConnection;
                } else {
                    it.setClassName(sActRef.get().getPackageName(), LocalService.class.getName());
                    Utils.sConnection = mLocalConnection;
                }
                sActRef.get().bindService(it, Utils.sConnection, Context.BIND_AUTO_CREATE);
                Utils.sIsBound = true;
            }
        }, UNBIND_SERVICE(Utils.ITEM_UNBIND_SERVICE) {
            @Override
            public void execute() {
                if (Utils.sIsBound) {
                    sActRef.get().unbindService(Utils.sConnection);
                    Utils.sIsBound = false;
                    if (Utils.sIsRemoteService) {
                        Utils.sMessenger = null;
                    } else {
                        Utils.sLocalSvr = null;
                    }
                }
            }
        }, SERVICE_REQ(Utils.ITEM_SERVICE_REQUEST) {
            @Override
            public void execute() {
                if (Utils.sIsRemoteService) {
                    if (Utils.sMessenger != null) {
                        Message message = Message.obtain(null, RemoteService.ACTION_ONE);
                        try {
                            Utils.sMessenger.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (Utils.sLocalSvr != null) {
                        Utils.sLocalSvr.action1();
                    }
                }

            }
        };


        private static WeakReference<Activity> sActRef;
        private String mType;

        Item(String type) {
            mType = type;
        }

        public static void setActivityContext(Activity act) {
            sActRef = new WeakReference<Activity>(act);
        }

        public static Item getItemBy(String str) {

            return Arrays.stream(Item.values())
                    .filter(item -> item.getType().equals(str))
                    .findFirst()
                    .orElse(null);
        }

        public String getType() {
            return mType;
        }

        public abstract void execute();

    }

}
