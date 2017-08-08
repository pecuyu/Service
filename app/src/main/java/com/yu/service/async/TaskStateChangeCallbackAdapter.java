package com.yu.service.async;

/**
 * Created by D22436 on 2017/8/8.
 *  TaskStateChangeCallback适配器，选择性实现回调方法
 */

public class TaskStateChangeCallbackAdapter<Progress, Result> implements  TaskStateChangeCallback<Progress, Result> {

    @Override
    public void onPreTaskExecute() {

    }

    @Override
    public void onTaskProgressUpdate(Progress progress) {

    }

    @Override
    public void onTaskPostExecute(Result result) {

    }
}
