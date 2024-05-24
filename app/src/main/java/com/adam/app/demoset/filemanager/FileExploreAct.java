package com.adam.app.demoset.filemanager;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.adam.app.demoset.FileUtils;
import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.databinding.ActivityDemoDataBindingExBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileExploreAct extends AppCompatActivity {

    public static final int MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 1;
    public static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 2;

    private ActivityDemoDataBindingExBinding mBinding;

    private List<File> mFileList;
    private ArrayAdapter<String> mStrAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info("onCreate");

        this.mBinding = ActivityDemoDataBindingExBinding.inflate(getLayoutInflater());
        this.mBinding.toolbar.inflateMenu(R.menu.action_menu_file_manager);
        setContentView(this.mBinding.getRoot());

        setupUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        info("onResume");
        // check file explore permission
        boolean hasPermission = FileUtils.checkStoragePermission(this);
        info("hasPermission: " + String.valueOf(hasPermission));
        // show ui according to permission state
        if (!hasPermission) {
            this.mBinding.rationaleView.setVisibility(View.VISIBLE);
            this.mBinding.permissionButton.setVisibility(View.VISIBLE);
            this.mBinding.filesTreeView.setVisibility(View.GONE);
            return;
        }

        //check sdk version
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            if (!Environment.isExternalStorageLegacy()) {
                info("!Environment.isExternalStorageLegacy()");
                this.mBinding.rationaleView.setVisibility(View.GONE);
                this.mBinding.permissionButton.setVisibility(View.GONE);
                this.mBinding.legacyStorageView.setVisibility(View.VISIBLE);
                return;
            }
        }

        this.mBinding.rationaleView.setVisibility(View.GONE);
        this.mBinding.permissionButton.setVisibility(View.GONE);
        this.mBinding.filesTreeView.setVisibility(View.VISIBLE);
        // OPEN
        open(Environment.getExternalStorageDirectory());
    }

    private void setupUi() {
        info("setupUi");
        // set menu action
        this.mBinding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // start about activity
                Utils.showToast(FileExploreAct.this, "No implement yet!!!");
                return true;
            }
        });

        // set permission button
        this.mBinding.permissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtils.requestStoragePermission(FileExploreAct.this);
            }
        });

        // set list action
        this.mStrAdapter =new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        this.mBinding.filesTreeView.setAdapter(this.mStrAdapter);
        this.mBinding.filesTreeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File selectedItem = FileExploreAct.this.mFileList.get(position);
                open(selectedItem);
            }
        });
    }

    private void open(File selectedItem) {
        info("open");
        // check is file or not
        if (selectedItem.isFile()) {
            FileUtils.openFile(this, selectedItem);
            return;
        }

        // update list view
        boolean hasFile = true;
        try {
            this.mFileList = FileUtils.getFiles(selectedItem);
        } catch (FileNotFoundException e) {
            Utils.showToast(this, "No file!!!");
            hasFile = false;
        }

        if (!hasFile) {
            // no file so do not need to open!!!
            return;
        }

        // clear adapter
        this.mStrAdapter.clear();
        this.mStrAdapter.addAll(mapItems(selectedItem));

    }

    private List<String> mapItems(File seletctedFile) {
        info("mapItems");
        return this.mFileList.stream()
                .map(file -> file.getPath().equals(Objects.requireNonNull(seletctedFile.getParentFile()).getPath()) ? FileUtils.ParentLinkLabel(this) : FileUtils.ItemLabel(this, file))
                .collect(Collectors.toList());
    }

    private void info(String msg) {
        Utils.info(this, msg);
    }

}