package com.yu.service.download;

/**
 * Created by pecuyu on 2017/8/3.
 * 下载状态的监听器
 */

public interface DownloadStateListener {
    void onSuccess();   /*成功*/
    void onFailed();    /*失败*/
    void onPause();     /*暂停*/
    void onCancel();    /*取消*/
    void onProgressUpdate(int progress); /*更新进度*/
}




