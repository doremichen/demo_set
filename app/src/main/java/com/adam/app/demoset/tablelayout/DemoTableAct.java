/**
 * This class is the main activity of demo table view
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2025-11-11
 */
package com.adam.app.demoset.tablelayout;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.databinding.ActivityDemoTableBinding;
import com.adam.app.demoset.tablelayout.viewmodel.TicTacToeViewModel;

public class DemoTableAct extends AppCompatActivity {

    // button array size 9
    private final Button[] mButtons = new Button[9];

    // view biding
    private ActivityDemoTableBinding mBinding;
    // view model
    private TicTacToeViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // data binding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_demo_table);

        // init view model
        mViewModel = new ViewModelProvider(this).get(TicTacToeViewModel.class);
        // data binding view model and activity
        mBinding.setViewModel(mViewModel);
        mBinding.setActivity(this);
        // data binding lifecycle owner
        mBinding.setLifecycleOwner(this);

        // update message
        mViewModel.getMessageLiveData().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Utils.showToast(this, message);
            }
        });

    }
}
