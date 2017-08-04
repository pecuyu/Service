package com.yu.service.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.yu.service.MainActivity;
import com.yu.service.R;

import java.io.File;

/**
 * Created by D22436 on 2017/8/4.
 */

public class DownloadService extends Service {
    private DownloadTask mTask;
    private MyDownloadStateListener mListener;
    private String mDownloadUrl;

    @Override

    public void onCreate() {
        super.onCreate();

    }

    class MyDownloadStateListener implements DownloadStateListener {

        @Override
        public void onSuccess() {
            mTask = null;
            /*下载成功则结束前台*/
            stopForeground(true);
            getNotificationManager().notify(1, getNotification(-1, "download success"));
            Toast.makeText(DownloadService.this, "download success", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onFailed() {
            mTask = null;
            /*下载失败则结束前台*/
            stopForeground(true);
            getNotificationManager().notify(1, getNotification(-1, "download failed"));
            Toast.makeText(DownloadService.this, "download failed", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onPause() {
            mTask = null;
            Toast.makeText(DownloadService.this, "download paused", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCancel() {
            mTask = null;
            Toast.makeText(DownloadService.this, "download canceled", Toast.LENGTH_SHORT).show();
            stopForeground(true);

        }

        @Override
        public void onProgressUpdate(int progress) {
            getNotificationManager().notify(1, getNotification(progress, String.format("%s downlaoding",Utils.getFileName(mDownloadUrl))));
            if (MainActivity.sMainHandler != null) {
                Message msg = MainActivity.sMainHandler.obtainMessage();
                msg.what = MainActivity.MSG_PROGRESS_UPDATE_FOME_DOWNLOAD_SERVICE;
                msg.arg1 = progress;
                msg.sendToTarget();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new DownloadController();
    }

    class DownloadController extends Binder implements IDownloadController {
        @Override
        public void startDownload(String downloadUrl) {
            if (downloadUrl == null) {
                return;
            }
            mDownloadUrl = downloadUrl;
            if (mTask == null) {
                mListener = new MyDownloadStateListener();
                mTask = new DownloadTask(mListener);
                mTask.execute(downloadUrl);
                startForeground(1, getNotification(0,String.format("%s downlaoding",Utils.getFileName(mDownloadUrl))));
            }
            Log.e("TAG", "startDownload");
        }

        @Override
        public void pauseDownload() {
            if (mTask != null) {
                mTask.pauseTask();
            }
            Log.e("TAG", "pauseDownload");
        }

        @Override
        public void cancelDownload() {
            if (mTask != null) {
                mTask.cancelTask();
            } else {
                if (mDownloadUrl != null) {  // 如果连接存在，则删除文件
                    String fileName = Utils.getFileName(mDownloadUrl);
                    File file = new File(Utils.getExternalDownloadDir(), fileName);
                    if (file != null && file.exists()) {
                        file.delete();
                    }
                }
            }
            Log.e("TAG", "cancelDownload");

            // 取消通知
            getNotificationManager().cancel(1);
            stopForeground(true);
            Toast.makeText(DownloadService.this, "cancelDownload", Toast.LENGTH_SHORT).show();
        }
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private Notification getNotification(int progress, String title) {
        Intent intent = new Intent(DownloadService.this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(DownloadService.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(DownloadService.this)
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pi);
        if (progress >= 0) {   // 进度不小于0才显示
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }

        return builder.build();
    }
}
