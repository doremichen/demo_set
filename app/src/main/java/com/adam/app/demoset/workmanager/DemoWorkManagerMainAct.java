package com.adam.app.demoset.workmanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoWorkManagerMainAct extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    public static final int REQUEST_WRITE_EXTERNAL_CODE = 0X1357;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_work_manager_main);

        // Write external permission request
        Utils.askPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                REQUEST_WRITE_EXTERNAL_CODE);

    }

    public void onSelectImg(View view) {
        Utils.inFo(this, "onSelectImg enter");
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.inFo(this, "onActivityResult enter");
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Utils.showToast(this, "Uri: " + uri.toString());
            Utils.inFo(this, "Uri: " + uri.toString());
            // Start execute picture activity
            Intent intent = new Intent(this, DemoExecuteTaskAct.class);
            intent.putExtra(Utils.THE_SELECTED_IMAGE, uri.toString());
            this.startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_WRITE_EXTERNAL_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Utils.showToast(this, "Camera permission is not granted");
                // Permission is not grained
                this.finish();
            } else {
                Utils.showToast(this, "Camera permission is granted");
            }
        }

    }
}
