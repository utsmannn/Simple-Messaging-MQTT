package com.utsman.smm;

import android.support.annotation.Nullable;

import org.json.JSONObject;

public interface DataListener {
    void onListenData(@Nullable String senderId, JSONObject data);
}
