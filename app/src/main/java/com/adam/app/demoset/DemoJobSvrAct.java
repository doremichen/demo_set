package com.adam.app.demoset;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

public class DemoJobSvrAct extends AppCompatActivity {

    private ListView mList;
    private TextView mShow;

    private String[] mItems = {
            Utils.ITEM_START_SERVICE,
            Utils.ITEM_STOP_SERVICE,
            Utils.ITEM_EXIT
    };


    private HashMap<String, ItemType> mMap = new HashMap<String, ItemType>();


    // for job service id
    private int mJobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_job_svr);

        mList = (ListView) this.findViewById(R.id.list_demo_permission);
        mShow = (TextView) this.findViewById(R.id.show_permission_result);

        mMap.put(Utils.ITEM_START_SERVICE, new StartSvrItem());
        mMap.put(Utils.ITEM_STOP_SERVICE, new StopSvrItem());
        mMap.put(Utils.ITEM_EXIT, new ExitItem());


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mItems);

        mList.setAdapter(adapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemName = (String) parent.getItemAtPosition(position);
                ItemType item = mMap.get(itemName);
                if (item != null) {
                    item.execute();
                }

            }
        });

    }


    private interface ItemType {
        void execute();
    }

    private class StartSvrItem implements ItemType {

        @Override
        public void execute() {

            //Prepare jobinfo
            JobInfo.Builder builder = new JobInfo.Builder(mJobId++, new ComponentName(DemoJobSvrAct.this, SecurJobService.class));

            //set latency time
            builder.setMinimumLatency(10L);

            //set dead line time
            builder.setOverrideDeadline(100L);

//            //set required
//            builder.setRequiresDeviceIdle(false);
//
//            //set required
//            builder.setRequiresCharging(false);
//
//            //set persist
//            builder.setPersisted(true);

            //Schedule job
            JobScheduler jobService = (JobScheduler) DemoJobSvrAct.this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobService.schedule(builder.build());


        }
    }

    private class StopSvrItem implements ItemType {

        @Override
        public void execute() {
            //cancel all job
            JobScheduler jobService = (JobScheduler) DemoJobSvrAct.this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobService.cancelAll();


        }
    }

    private class ExitItem implements ItemType {

        @Override
        public void execute() {

            DemoJobSvrAct.this.finish();
        }
    }
}
