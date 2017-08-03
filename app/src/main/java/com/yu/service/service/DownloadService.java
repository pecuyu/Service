package com.yu.service.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by D22436 on 2017/8/3.
 */

public class DownloadService extends Service {
    public static final String TAG = DownloadService.class.getName();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return new Proxy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");

        super.onDestroy();
    }

    class Proxy extends Binder implements TaskStateChangeControl {

        @Override
        public void startTask() {
            Log.e(TAG, "startTask");
        }

        @Override
        public void getTaskProgress() {
            Log.e(TAG, "getTaskProgress");
        }

        @Override
        public void stopTask() {
            Log.e(TAG, "stopTask");
        }
    }

}
