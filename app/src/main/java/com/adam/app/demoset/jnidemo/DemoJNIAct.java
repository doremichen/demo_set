package com.adam.app.demoset.jnidemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.adam.app.demoset.R;

public class DemoJNIAct extends AppCompatActivity {

    private TextView mShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_jni);

        mShow = (TextView)this.findViewById(R.id.tv_show_jni);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_only_exit_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.demo_bt_exit:
                this.finish();
                return true;
        }

        return false;
    }

    public void onInvokeJNI(View v) {
        String info = NativeUtils.sayHello();
        mShow.setText("JNI info: " + info);
    }
}
