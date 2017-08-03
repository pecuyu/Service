package com.yu.service.download;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by pecuyu on 2017/8/3.
 */

public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    /* 状态类型 */
    public static final int TYPE_FAILED = 0;
    public static final int TYPE_SUCCESS = 1;
    public static final int TYPE_PAUSE = 2;
    public static final int TYPE_CANCELED = 3;

    /*是否暂停任务*/
    private boolean isPause = false;
    /*是否取消了任务*/
    private boolean isCanceled = false;

    private DownloadStateListener mListener;

    private int lastProgress = 0;

    public DownloadTask(DownloadStateListener listener) {
        this.mListener = listener;
    }

    @Override
    protected Integer doInBackground(String... params) {
        long downloadLength = 0; /*已经下载的长度*/
        String downloadUrl = params[0];  /*下载链接*/
        String fileName = getFileName(downloadUrl); /*文件名*/
        InputStream is = null;
        RandomAccessFile savedFile = null;  /*将要下载并保存的文件*/
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (file.exists()) {    /*如果文件已经存在*/
            downloadLength = file.length();
        }
        try {
            long contentLength = getDownloadFileLength(downloadUrl);
            if (contentLength == 0) {  /*获取下载文件大小失败*/
                return TYPE_FAILED;
            }
            if (contentLength == downloadLength) {  /*已经存在下载完成的文件*/
                return TYPE_SUCCESS;
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().
                    url(downloadUrl)
                    .addHeader("RANGE", "bytes=" + downloadUrl + "-") /*支持断点下载，指定从哪个字节开始下载*/
                    .build();
            Response response = client.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadLength);  /*跳过已经下载的部分*/
                byte[] buf = new byte[1024];
                int len = -1;
                int total = 0;  /*本次写入的总长度*/
                int progress;
                while ((len = is.read(buf)) != -1) {
                    if (isPause) {
                        return TYPE_PAUSE;
                    } else if (isCanceled) {
                        return TYPE_CANCELED;
                    } else {
                        total += len;
                        savedFile.write(buf, 0, len); /*写入*/
                        progress = (int) ((total + downloadLength) * 100 / contentLength); /*计算进度*/
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }

                if (savedFile != null) {
                    savedFile.close();
                }

                if (isCanceled && file != null) {
                    file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int progress = values[0];
        if (progress > lastProgress && mListener != null) {
            mListener.onProgressUpdate(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer type) {
        super.onPostExecute(type);
        switch (type) {
            case TYPE_FAILED:
                if (mListener!=null) mListener.onFailed();
                break;
            case TYPE_SUCCESS:
                if (mListener!=null) mListener.onSuccess();
                break;
            case TYPE_PAUSE:
                if (mListener!=null) mListener.onPause();
                break;
            case TYPE_CANCELED:
                if (mListener!=null) mListener.onCancel();
                break;
        }
    }

    /**
     * 暂停任务
     */
    public void pauseTask() {
        isPause = true;
    }

    /**
     * 取消任务
     */
    public void cancelTask() {
        isCanceled = true;
    }

    /**
     * 获取文件长度
     *
     * @param downloadUrl
     * @return
     * @throws IOException
     */
    private long getDownloadFileLength(String downloadUrl) throws IOException {
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
     * 获取文件名
     *
     * @param path
     * @return
     */
    public String getFileName(String path) {
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }
}
