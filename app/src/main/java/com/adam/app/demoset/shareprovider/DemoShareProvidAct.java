/*
 * Copyright (c) 2026 Adam Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.adam.app.demoset.shareprovider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoShareProvidBinding;
import com.adam.app.demoset.utils.UIUtils;

import java.util.List;

public class DemoShareProvidAct extends AppCompatActivity {

    private ShareActionProvider mShareAction;
    // view binding
    private ActivityDemoShareProvidBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // view binding
        mBinding = ActivityDemoShareProvidBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // hide system bar
        UIUtils.hideSystemBar(getWindow());

        // fit system windows
        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.appBarWrapper);

        // set shared button listener
        mBinding.btnShareAction.setOnClickListener(this::onSharedBtnClick);


    }

    private void onSharedBtnClick(View view) {
        triggerManualShare();
    }

    private void triggerManualShare() {
        Intent intent = createShareIntent();
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_menu_share, menu);

        // Set up SahreActionProvider
        MenuItem item_share = menu.findItem(R.id.menu_shared);
        mShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item_share);

        if (mShareAction != null) {
            mShareAction.setShareIntent(createShareIntent());
        }

        if (noSharedApp()) {
            menu.removeItem(R.id.menu_shared);
        }

        return true;
    }

    private boolean noSharedApp() {
        // query useful app
        PackageManager pm = getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(createShareIntent(), 0);
        return infos.isEmpty();
    }

    private Intent createShareIntent() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "來自 Abb 的分享：這是一個 Data Binding 與 ShareProvider 的 Demo 內容！");
        return sendIntent;
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
