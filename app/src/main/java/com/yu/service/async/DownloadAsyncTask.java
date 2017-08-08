package com.yu.service.async;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by D22436 on 2017/8/3.
 */

public class DownloadAsyncTask extends AsyncTask<Integer,Integer,Integer> {
    private static final String TAG = DownloadAsyncTask.class.toString();
    TaskStateChangeCallback<Integer,Integer> callback;

    public DownloadAsyncTask(TaskStateChangeCallback<Integer, Integer> callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.e(TAG, "prepare task：" + Thread.currentThread().getName());
        if (callback!=null) callback.onPreTaskExecute();
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        Log.e(TAG, "execute task：" + Thread.currentThread().getName());

        Integer max = params[0];
        Integer total = new Integer(0);
        for(int i=1;i<=max;i++) {
            total += i;
            publishProgress(i * 100 / max);  // 发布进度
            SystemClock.sleep(5);  // 模拟耗时
        }

        return total;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (callback != null) {
            callback.onTaskProgressUpdate(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        Log.e(TAG, "post task：" + Thread.currentThread().getName());

        super.onPostExecute(result);
        if (callback != null) {
            callback.onTaskPostExecute(result);
        }
    }
}
