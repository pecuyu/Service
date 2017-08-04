package com.yu.service.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by D22436 on 2017/8/3.
 */

public class AsyncIntentService  extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AsyncIntentService(String name) {
        super("AsyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
