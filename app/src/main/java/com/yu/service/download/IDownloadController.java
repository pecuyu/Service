package com.yu.service.download;

/**
 * Created by D22436 on 2017/8/4.
 * 下载控制接口
 */

public interface IDownloadController {
    void startDownload(String downloadUrl);
    void pauseDownload();
    void cancelDownload();
}
