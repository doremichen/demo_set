package com.adam.app.demoset.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.handler.EventHandler;
import com.adam.app.demoset.databinding.model.User;

public class DemoDataBindingAct extends AppCompatActivity {

    private com.adam.app.demoset.databinding.ActivityDemoDataBindingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.
                setContentView(this, R.layout.activity_demo_data_binding);

        // default
        User user = new User("Adam", "Chen", 18);
        mBinding.setUser(user);
        mBinding.setHandler(new EventHandler(this, user));
    }
}