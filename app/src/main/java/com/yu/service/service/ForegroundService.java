package com.yu.service.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.yu.service.MainActivity;
import com.yu.service.R;

/**
 * Created by D22436 on 2017/8/3.
 */

public class ForegroundService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent=new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        Notification noti = new NotificationCompat.Builder(this)
                .setContentTitle("this is title")
                .setContentText("this is text")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pi).build();
        startForeground(6,noti);
    }
}
