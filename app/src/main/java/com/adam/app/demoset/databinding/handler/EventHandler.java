package com.adam.app.demoset.databinding.handler;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.adam.app.demoset.databinding.model.User;

public class EventHandler {
    private Context mContext;
    private User mUser;
    public EventHandler(Context context, User user) {
        mContext = context;
        this.mUser = user;
    }

    public void onClickFriend(View view) {
        Toast.makeText(mContext, "onClickFriend", Toast.LENGTH_LONG).show();
        this.mUser.updateInfo("Scott", "Chu");

    }
}
