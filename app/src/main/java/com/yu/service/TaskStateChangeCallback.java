package com.yu.service;

/**
 * Created by D22436 on 2017/8/3.
 * 任务状态改变时的回调
 */

public interface TaskStateChangeCallback<Progress, Result> {
    void onTaskProgressUpdate(Progress progress);

    void onTaskPostExecute(Result result);
}
