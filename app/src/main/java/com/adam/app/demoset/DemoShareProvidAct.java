/**
 * Description: This class is activity of demo share action provider.
 *
 * Author: Adam Chen
 * Date: 2018/10/08
 */
package com.adam.app.demoset;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
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

        if (!Utils.areAllNotNull(this.mShareAction)) {
            Utils.showToast(this, "No share action!!!");
            return false;
        }

        // Create intent
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Demo text");

        // Query useful app
        PackageManager pm = getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(sendIntent, 0);

        // check infos data
        if (!infos.isEmpty()) {
            mShareAction.setShareIntent(sendIntent);
            return true;
        }

        return false;
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
