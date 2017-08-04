package com.yu.service.download;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by D22436 on 2017/8/4.
 */

public class Utils {

    /**
     * 获取下载文件名
     *
     * @param path
     * @return
     */
    public static String getFileName(String path) {
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }

    /**
     * 获取文件长度
     *
     * @param downloadUrl
     * @return
     * @throws IOException
     */
    static long getDownloadFileLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        Response response = client.newCall(request).execute();
        long contentLength = 0;
        if (response != null && response.isSuccessful()) {
            contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return contentLength;
    }

    /**
     * 获取外部下载目录
     * @return
     */
    public static File getExternalDownloadDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }
}
