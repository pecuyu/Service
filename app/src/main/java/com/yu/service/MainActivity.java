package com.yu.service;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextView tvProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.id_task_progressBar);
        tvProgress = (TextView) findViewById(R.id.id_textView_progress);
    }

    public void startTask(View view) {
        new DownloadAsyncTask(new TaskStateChangeCallback<Integer, Integer>() {
            @Override
            public void onTaskProgressUpdate(Integer progress) {
                progressBar.setProgress(progress);
                tvProgress.setText(progress+"%");
            }

            @Override
            public void onTaskPostExecute(Integer result) {
                Toast.makeText(getApplicationContext(), "task finish,result=" + result,Toast.LENGTH_SHORT).show();
            }
        }).execute(1000);
    }
}
