package com.yu.service;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yu.service.async.DownloadAsyncTask;
import com.yu.service.async.TaskStateChangeCallback;
import com.yu.service.download.IDownloadController;
import com.yu.service.service.DownloadService;
import com.yu.service.service.ForegroundService;
import com.yu.service.service.TaskStateChangeControl;

public class MainActivity extends AppCompatActivity {
    static ProgressBar progressBar;
    static TextView tvProgress;
    ServiceConnection conn;
    private String downloadUrl="http://gdown.baidu.com/data/wisegame/74ac7c397e120549/QQ_708.apk";
    /*来自download service的消息*/
    public static final int MSG_PROGRESS_UPDATE_FOME_DOWNLOAD_SERVICE = 0x66;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.id_task_progressBar);
        tvProgress = (TextView) findViewById(R.id.id_textView_progress);

        // 绑定服务
        dlConn = new DownloadServiceConnection();
        Intent intent = new Intent(this, com.yu.service.download.DownloadService.class);
        startService(intent);
        bindService(intent, dlConn, Context.BIND_AUTO_CREATE);

        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    /*  服务 */
    public void statService(View view) {
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);

    }
    public void stopService(View view) {
        Intent intent = new Intent(this, DownloadService.class);
        stopService(intent);
    }
    public void bindService(View view) {
        Intent intent = new Intent(this, DownloadService.class);
        conn = new ServiceConnection() {
            TaskStateChangeControl control;

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                control = (TaskStateChangeControl) service;
                control.startTask();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                control.stopTask();
            }
        };

        bindService(intent,conn , BIND_AUTO_CREATE);
    }
    public void unbindService(View view) {
        Intent intent = new Intent(this, DownloadService.class);
        if (conn != null) {
            unbindService(conn);
            conn = null;
        }
    }

    public void startForegroundService(View view) {
        Intent intent = new Intent(this, ForegroundService.class);
        startService(intent);
    }

    /**
     * 启动异步任务
     * @param view
     */
    public void startTask(View view) {
        final long time = System.currentTimeMillis();
        new DownloadAsyncTask(new TaskStateChangeCallback<Integer, Integer>() {
            @Override
            public void onTaskProgressUpdate(Integer progress) {
                progressBar.setProgress(progress);
                tvProgress.setText(progress + "%");
            }

            @Override
            public void onTaskPostExecute(Integer result) {
                long cost = System.currentTimeMillis() - time;  // 计时
                Toast.makeText(getApplicationContext(),
                        String.format("task finished in %d second,result=%d", cost / 1000, result), Toast.LENGTH_SHORT).show();
            }
        }).execute(1000);
    }

    /* 下载 */
    IDownloadController controller;
    DownloadServiceConnection dlConn;

    public void startDownload(View view) {

        if (controller!=null) controller.startDownload(downloadUrl);
    }
    public void pauseDownload(View view) {
        if (controller!=null){
            controller.pauseDownload();
        }
    }
    public void cancelDownload(View view) {
        if (controller!=null) controller.cancelDownload();
    }

    class DownloadServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            controller = (IDownloadController) service;
            Log.e("TAG", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dlConn !=null) unbindService(dlConn);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "granted permission", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "you denied permission", Toast.LENGTH_SHORT).show();
                    finish();
                }

                break;

        }
    }


    /**
     * 主线程Handler，处理各种 线程消息
     */
    public static Handler sMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case   MSG_PROGRESS_UPDATE_FOME_DOWNLOAD_SERVICE:
                    int progress = msg.arg1;
                    progressBar.setProgress(progress);
                    tvProgress.setText(progress+"%");
                    break;
            }
        }
    };
}


