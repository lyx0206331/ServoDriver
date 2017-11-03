package com.adrian.servodriver.views;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adrian.servodriver.R;
import com.adrian.servodriver.nohttp.CallServer;
import com.adrian.servodriver.utils.CommUtil;
import com.adrian.servodriver.utils.Constants;
import com.adrian.servodriver.utils.FileUtil;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.ServerError;
import com.yanzhenjie.nohttp.error.StorageReadWriteError;
import com.yanzhenjie.nohttp.error.StorageSpaceNotEnoughError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by ranqing on 2017/11/4.
 */

public class DownloadDialog extends BaseDialog implements View.OnClickListener {

    private ImageButton mCloseBtn;
    private TextView mTitleTV;
    private TextView mPercentTV;
    private Button mPauseBtn;
    private ProgressBar mProgressBar;

    private int percent;

    /**
     * 下载请求.
     */
    private DownloadRequest mDownloadRequest;

    public DownloadDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void initViews() {
        mCloseBtn = (ImageButton) findViewById(R.id.ib_close);
        mTitleTV = (TextView) findViewById(R.id.tv_title);
        mPercentTV = (TextView) findViewById(R.id.tv_percent);
        mPauseBtn = (Button) findViewById(R.id.btn_pause);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress);

        setCanceledOnTouchOutside(false);

        mPauseBtn.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);

//        setOnCancelListener(new OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                // 暂停下载
//                if (mDownloadRequest != null) {
////                    mDownloadRequest.cancel();
//                    mDownloadRequest.finish();
//                }
//            }
//        });
    }

    @Override
    protected void loadData() {
        download();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.layout_download_dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_close:
                if (mDownloadRequest != null) {
                    mDownloadRequest.finish();
                }
                cancel();
                break;
            case R.id.btn_pause:
                download();
                break;
        }
    }

    /**
     * 开始下载。
     */
    private void download() {
        // 开始下载了，但是任务没有完成，代表正在下载，那么暂停下载。
        if (mDownloadRequest != null && mDownloadRequest.isStarted() && !mDownloadRequest.isFinished()) {
            // 暂停下载。
            mDownloadRequest.cancel();
        } else if (mDownloadRequest == null || mDownloadRequest.isFinished()) {// 没有开始或者下载完成了，就重新下载。

            mDownloadRequest = new DownloadRequest(Constants.TEST_DOWNLOAD, RequestMethod.GET,
                    Constants.ROOT_PATH + Constants.DOWNLOAD_FIRMWARE,
                    true, true);

            // what 区分下载。
            // downloadRequest 下载请求对象。
            // downloadListener 下载监听。
            CallServer.getInstance().download(0, mDownloadRequest, downloadListener);

            // 添加到队列，在没响应的时候让按钮不可用。
            mPauseBtn.setEnabled(false);
        }
    }

    /**
     * 下载监听
     */
    private DownloadListener downloadListener = new DownloadListener() {

        @Override
        public void onStart(int what, boolean isResume, long beforeLength, Headers headers, long allCount) {
            int progress = percent;
            if (allCount != 0) {
                progress = (int) (beforeLength * 100 / allCount);
                mProgressBar.setProgress(progress);
            }
            updateProgress(progress, 0);

            mPauseBtn.setText(R.string.download_status_pause);
            mPauseBtn.setEnabled(true);
        }

        @Override
        public void onDownloadError(int what, Exception exception) {
            Logger.e(exception);
            mPauseBtn.setText(R.string.download_status_again_download);
            mPauseBtn.setEnabled(true);

            String message = context.getString(R.string.download_error);
            String messageContent;
            if (exception instanceof ServerError) {
                messageContent = context.getString(R.string.download_error_server);
            } else if (exception instanceof NetworkError) {
                messageContent = context.getString(R.string.download_error_network);
            } else if (exception instanceof StorageReadWriteError) {
                messageContent = context.getString(R.string.download_error_storage);
            } else if (exception instanceof StorageSpaceNotEnoughError) {
                messageContent = context.getString(R.string.download_error_space);
            } else if (exception instanceof TimeoutError) {
                messageContent = context.getString(R.string.download_error_timeout);
            } else if (exception instanceof UnKnownHostError) {
                messageContent = context.getString(R.string.download_error_un_know_host);
            } else if (exception instanceof URLError) {
                messageContent = context.getString(R.string.download_error_url);
            } else {
                messageContent = context.getString(R.string.download_error_un);
            }
            message = String.format(Locale.getDefault(), message, messageContent);
            mPercentTV.setText(message);
        }

        @Override
        public void onProgress(int what, int progress, long fileCount, long speed) {
            updateProgress(progress, speed);
            mProgressBar.setProgress(progress);
            percent = progress;
        }

        @Override
        public void onFinish(int what, String filePath) {
            Logger.d("Download finish, file path: " + filePath);
            CommUtil.showToast(R.string.download_status_finish);// 提示下载完成
            mPercentTV.setText(R.string.download_status_finish);

            mPauseBtn.setText(R.string.download_status_re_download);
            mPauseBtn.setEnabled(true);
        }

        @Override
        public void onCancel(int what) {
            mPercentTV.setText(R.string.download_status_be_pause);
            mPauseBtn.setText(R.string.download_status_resume);
            mPauseBtn.setEnabled(true);
        }

        private void updateProgress(int progress, long speed) {
            double newSpeed = speed / 1024D;
            DecimalFormat decimalFormat = new DecimalFormat("###0.00");
            String sProgress = context.getString(R.string.download_progress);
            sProgress = String.format(Locale.getDefault(), sProgress, progress, decimalFormat.format(newSpeed));
            mPercentTV.setText(sProgress);
        }
    };
}
