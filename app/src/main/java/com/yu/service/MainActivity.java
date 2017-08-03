package com.yu.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yu.service.async.DownloadAsyncTask;
import com.yu.service.async.TaskStateChangeCallback;
import com.yu.service.service.DownloadService;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextView tvProgress;
    ServiceConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.id_task_progressBar);
        tvProgress = (TextView) findViewById(R.id.id_textView_progress);
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
            DownloadService.TaskStateChangeControl control;

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                control = (DownloadService.TaskStateChangeControl) service;
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
        unbindService(conn);
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
}
