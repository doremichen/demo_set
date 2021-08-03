package com.adam.app.demoset;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class DemoShareProvidAct extends AppCompatActivity {

    private ShareActionProvider mShareAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_share_provid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_menu_share, menu);

        // Set up SahreActionProvider
        MenuItem item_share = menu.findItem(R.id.menu_shared);
        mShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item_share);

        if (!setShareIntent()) {
            menu.removeItem(R.id.menu_shared);
        }

        return true;
    }

    private boolean setShareIntent() {
        Utils.info(this, "setShareIntent enter");
        Utils.info(this, "mShareAction = " + mShareAction);
        boolean ret = false;

        if (mShareAction != null) {

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Demo text");

            // Query useful app
            PackageManager pm = getPackageManager();
            List<ResolveInfo> infos = pm.queryIntentActivities(sendIntent, 0);

            Utils.info(this, "infos.isEmpty = " + infos.isEmpty());

            if (!infos.isEmpty()) {
                mShareAction.setShareIntent(sendIntent);
                ret = true;
            }

        }

        return ret;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_shared:
                // do action
                return true;
            case R.id.demo_exit:
                this.finish();
                return true;
        }

        return false;
    }
}
