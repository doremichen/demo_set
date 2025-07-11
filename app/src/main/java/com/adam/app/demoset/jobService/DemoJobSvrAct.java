/**
 * Demo job service activity
 */
package com.adam.app.demoset.jobService;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class DemoJobSvrAct extends AppCompatActivity {

    private static final String PACKAGE_NAME = "com.adam.app.demoset";
    private static final String CLASS_NAME = SecurJobService.class.getName();

    // Spinner item index
    private static final int SPINNER_SET_PERIODIC = 0;
    private static final int SPINNER_SET_OVERRIDE_DEADLINE = 1;
    private static final int SPINNER_SET_MINIMUMLATENCY = 2;

    private ListView mList;
    private TextView mShow;

    private Spinner mSpinner;
    private SeekBar mSeekBar;
    private TextView mIntervalVal;

    private Switch mSwitchIdle;
    private Switch mSwitchCharging;

    private RadioGroup mNetworkRequireOption;

    private boolean mCanSetTrigger;

    private static abstract class ConstraintSet {
        public static int sTriggerfunc = SPINNER_SET_PERIODIC;
        public static int sTriggervalue = 0;
        public static int sNetWorkType = JobInfo.NETWORK_TYPE_NONE;
    }

    /**
     * List item
     */
    private String[] mItems = {
            Utils.ITEM_START_SERVICE,
            Utils.ITEM_STOP_SERVICE,
            Utils.ITEM_EXIT
    };

    /**
     * Spinner item
     */
    private String[] mSpinnerItems = {
            SpinnerItem.getName(SPINNER_SET_PERIODIC),
            SpinnerItem.getName(SPINNER_SET_OVERRIDE_DEADLINE),
            SpinnerItem.getName(SPINNER_SET_MINIMUMLATENCY)
    };

    private HashMap<String, ItemType> mMap = new HashMap<String, ItemType>();


    // for job service id
    private int mJobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_job_svr);

        mList = this.findViewById(R.id.list_demo_permission);
        mShow = this.findViewById(R.id.welcome_demo_job_svr);
        mSpinner = this.findViewById(R.id.spinner_set_interval);
        mSeekBar = this.findViewById(R.id.seekbar_time_interval);
        mIntervalVal = this.findViewById(R.id.interval_unit);
        mNetworkRequireOption = this.findViewById(R.id.network_option);
        mSwitchIdle = this.findViewById(R.id.switch_idle);
        mSwitchCharging = this.findViewById(R.id.switch_charging);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (progress > 0) {
                    ConstraintSet.sTriggervalue = progress;
                    // use min when select periodic item
                    if (ConstraintSet.sTriggerfunc == SpinnerItem.SET_PERIODIC.getId()) {
                        mIntervalVal.setText(progress + " min");
                    } else {
                        mIntervalVal.setText(progress + " s");
                    }
                    mCanSetTrigger = true;
                } else {
                    mIntervalVal.setText(getString(R.string.label_interval_no_set));
                    mCanSetTrigger = false;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        buildSpinner();

        buildMainList();

    }

    private void buildSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, mSpinnerItems);

        mSpinner.setAdapter(adapter);


        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Utils.info(this, "onItemSelected");
                String name = SpinnerItem.getName(position);
                Utils.info(this, "set trigger type: " + name);
                ConstraintSet.sTriggerfunc = position;

                // show information when select the periodic item
                if (SpinnerItemName.Periodic.equals(name)) {
                    // position dialog button
                    Utils.DialogButton okButton = new Utils.DialogButton(
                            getResources().getString(R.string.label_ok_btn),
                            null);
                    Utils.showAlertDialog(DemoJobSvrAct.this,
                            R.string.label_dialog_info,
                            R.string.label_job_spinner_info,
                            okButton);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void buildMainList() {
        // build string map key -> value
        Map<String, String> stringMap = Utils.buildStringMap(this);

        mMap.put(stringMap.get(Utils.ITEM_START_SERVICE), new StartSvrItem());
        mMap.put(stringMap.get(Utils.ITEM_STOP_SERVICE), new StopSvrItem());
        mMap.put(stringMap.get(Utils.ITEM_EXIT), new ExitItem());

        // items array
        String[] itemValues = Arrays.stream(mItems)
                .map(key -> stringMap.getOrDefault(key, key))
                .toArray(String[]::new);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemValues);

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




    // Strategy Interface
    interface NetworkStrategy {
        int getNetworkType();
    }

    // Concrete Strategies
    class NoNetworkStrategy implements NetworkStrategy {
        @Override
        public int getNetworkType() {
            return JobInfo.NETWORK_TYPE_NONE;
        }
    }

    class AnyNetworkStrategy implements NetworkStrategy {
        @Override
        public int getNetworkType() {
            return JobInfo.NETWORK_TYPE_ANY;
        }
    }

    class WifiNetworkStrategy implements NetworkStrategy {
        @Override
        public int getNetworkType() {
            return JobInfo.NETWORK_TYPE_UNMETERED;
        }
    }

    private Map<Integer, NetworkStrategy> mNetworkStretagyMap = new HashMap<>() {
        {
            put(R.id.no_network_opt, new NoNetworkStrategy());
            put(R.id.any_network_opt, new AnyNetworkStrategy());
            put(R.id.wifi_network_opt, new WifiNetworkStrategy());
        }
    };

    /**
     * Network require option
     */
    private void setNetWorkRequire(JobInfo.Builder builder) {
        int checkedId = mNetworkRequireOption.getCheckedRadioButtonId();
        NetworkStrategy strategy = this.mNetworkStretagyMap.get(checkedId);
        if (!Utils.areAllNotNull(strategy)) {
            Utils.showToast(this, "No network strategy!!!");
            return;
        }

        Utils.info(this, "Network option selected: " + checkedId);
        builder.setRequiredNetworkType(strategy.getNetworkType());

    }


    private void setTriggerInterval(JobInfo.Builder builder) {
        Utils.info(this, "setTriggerInterval enter mCanSetTrigger = " + mCanSetTrigger);
        if (mCanSetTrigger) {
            long interval = ConstraintSet.sTriggervalue * 1000L;
            switch (ConstraintSet.sTriggerfunc) {
                case SPINNER_SET_PERIODIC:
                    Utils.info(this, "SPINNER_SET_PERIODIC");
                    // Use min when select the periodic item
                    if (ConstraintSet.sTriggerfunc == SpinnerItem.SET_PERIODIC.getId()) {
                        interval *= 60;
                    }
                    builder.setPeriodic(interval);
                    break;
                case SPINNER_SET_OVERRIDE_DEADLINE:
                    Utils.info(this, "SPINNER_SET_OVERRIDE_DEADLINE");
                    builder.setOverrideDeadline(interval);
                    break;
                case SPINNER_SET_MINIMUMLATENCY:
                    Utils.info(this, "SPINNER_SET_MINIMUMLATENCY");
                    builder.setMinimumLatency(interval);
                    break;
            }
        }
    }

    /**
     * As following items are the Spinner items
     */
    private abstract class SpinnerItemName {
        public static final String Periodic = "setPeriodic";
        public static final String OverrideDeadline = "setOverrideDeadline";
        public static final String MinimumLatency = "setMinimumLatency";
    }

    private enum SpinnerItem {

        SET_PERIODIC(SpinnerItemName.Periodic, SPINNER_SET_PERIODIC),
        SET_OVERRIDEDEADLINE(SpinnerItemName.OverrideDeadline, SPINNER_SET_OVERRIDE_DEADLINE),
        SET_MININUMLATENCY(SpinnerItemName.MinimumLatency, SPINNER_SET_MINIMUMLATENCY);

        private int mId;
        private String mName;

        SpinnerItem(String name, int id) {
            this.mName = name;
            this.mId = id;
        }

        public static String getName(int id) {
            return Arrays.stream(SpinnerItem.values())
                    .filter(item -> id == item.mId)
                    .findFirst()
                    .map(item -> item.mName)
                    .orElse(null);
        }

        public int getId() {
            return this.mId;
        }
    }


    /**
     * As following items are the list items
     */
    private interface ItemType {
        void execute();
    }

    private boolean shouldSetJobRequirements() {
        List<BooleanSupplier> conditions = Arrays.asList(
                () -> ConstraintSet.sNetWorkType != JobInfo.NETWORK_TYPE_NONE,
                () -> mSwitchIdle.isChecked(),
                () -> mSwitchCharging.isChecked(),
                () -> mCanSetTrigger
        );

        return conditions.stream().anyMatch(BooleanSupplier::getAsBoolean);
    }

    private class StartSvrItem implements ItemType {

        @Override
        public void execute() {
            Utils.info(this, "execute enter");

            //Prepare jobinfo
            JobInfo.Builder builder = new JobInfo.Builder(mJobId,
                    new ComponentName(PACKAGE_NAME, CLASS_NAME));

            setNetWorkRequire(builder);

            Utils.info(this, "mSwitchIdle = " + mSwitchIdle.isChecked());
            Utils.info(this, "mSwitchCharging = " + mSwitchCharging.isChecked());

            builder.setRequiresDeviceIdle(mSwitchIdle.isChecked());
            builder.setRequiresCharging(mSwitchCharging.isChecked());

            setTriggerInterval(builder);

            // Check job constrain set
            if (shouldSetJobRequirements()) {
                mJobId++;

                //Schedule job
                JobScheduler jobService = (JobScheduler) DemoJobSvrAct.this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobService.schedule(builder.build());
                Utils.info(this, "execute exit");

            } else {
                Utils.showToast(DemoJobSvrAct.this, "No Jobinfo ConstraintSet");
            }

//            //set latency time
//            builder.setMinimumLatency(1000L);

            //set dead line time
//            builder.setOverrideDeadline(100L);

//            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

//            //set required
//            builder.setRequiresDeviceIdle(false);
//
//            //set required
//            builder.setRequiresCharging(true);
//
//            //set persist
//            builder.setPersisted(true);


        }
    }


    private class StopSvrItem implements ItemType {

        @Override
        public void execute() {
            Utils.info(this, "execute enter");
            // Check job constrain set
            if (shouldSetJobRequirements()) {
                //cancel all job
                JobScheduler jobService = (JobScheduler) DemoJobSvrAct.this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobService.cancelAll();
                Utils.info(this, "execute exit");

            } else {
                Utils.showToast(DemoJobSvrAct.this, "No Jobinfo ConstraintSet");
            }


        }
    }

    private class ExitItem implements ItemType {

        @Override
        public void execute() {

            DemoJobSvrAct.this.finish();
        }
    }
}
