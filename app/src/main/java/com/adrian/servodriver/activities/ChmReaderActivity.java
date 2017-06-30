package com.adrian.servodriver.activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adrian.servodriver.R;
import com.adrian.servodriver.utils.Constants;
import com.adrian.servodriver.utils.chm.Utils;
import com.ahmadnemati.clickablewebview.ClickableWebView;
import com.ahmadnemati.clickablewebview.listener.OnWebViewClicked;
import com.blankj.utilcode.util.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ChmReaderActivity extends BaseActivity implements View.OnClickListener {

    public static String chmFilePath = "", extractPath, md5File;
    private ImageButton mBackIB;
    private TextView mTitleTV;
    private WebView webview;
    private ProgressDialog progress;
    private ProgressBar progressLoadWeb;
    private ArrayList<String> listSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_chm_reader);
        StatusBarUtil.setTransparent(this);
        mBackIB = (ImageButton) findViewById(R.id.ib_back);
        mTitleTV = (TextView) findViewById(R.id.tv_title);
        Bundle bundle = getIntent().getExtras();
        mTitleTV.setText(bundle.getString("title"));
        mBackIB.setOnClickListener(this);

        switch (bundle.getInt("type")) {
            case 0:
                chmFilePath = Constants.ROOT_PATH + Constants.HELP + "test.chm";
                break;
            case 1:
                chmFilePath = Constants.ROOT_PATH + Constants.HELP + "PowerCollections.chm";
                break;
            case 2:
                chmFilePath = Constants.ROOT_PATH + Constants.HELP + "test.chm";
                break;
            case 3:
                chmFilePath = Constants.ROOT_PATH + Constants.HELP + "PowerCollections.chm";
                break;
        }
        Utils.chm = null;
        listSite = new ArrayList<>();

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressLoadWeb.setProgress(newProgress);
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!url.startsWith("http") && !url.endsWith(md5File)) {
                    String temp = url.substring("file://".length());
                    if (!temp.startsWith(extractPath)) {
                        url = "file://" + extractPath + temp;
                    }
                }

                super.onPageStarted(view, url, favicon);
                progressLoadWeb.setProgress(50);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressLoadWeb.setProgress(100);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                if (!url.startsWith("http") && !url.endsWith(md5File)) {
                    String temp = url.substring("file://".length());
                    if (!temp.startsWith(extractPath)) {
                        url = "file://" + extractPath + temp;
                    }
                }
                super.onLoadResource(view, url);
            }


            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (!url.startsWith("http") && !url.endsWith(md5File)) {
                    String temp = url.substring("file://".length());
                    String insideFileName;
                    if (!temp.startsWith(extractPath)) {
                        url = "file://" + extractPath + temp;
                        insideFileName = temp;
                    } else {
                        insideFileName = temp.substring(extractPath.length());
                    }
                    if (insideFileName.contains("#")) {
                        insideFileName = insideFileName.substring(0, insideFileName.indexOf("#"));
                    }
                    if (insideFileName.contains("?")) {
                        insideFileName = insideFileName.substring(0, insideFileName.indexOf("?"));
                    }
                    if (insideFileName.contains("%20")) {
                        insideFileName = insideFileName.replaceAll("%20", " ");
                    }
                    if (url.endsWith(".gif") || url.endsWith(".jpg") || url.endsWith(".png")) {
                        try {
                            return new WebResourceResponse("image/*", "", Utils.chm.getResourceAsStream(insideFileName));
                        } catch (IOException e) {
                            e.printStackTrace();
                            return super.shouldInterceptRequest(view, url);
                        }
                    } else if (url.endsWith(".css") || url.endsWith(".js")) {
                        try {
                            return new WebResourceResponse("", "", Utils.chm.getResourceAsStream(insideFileName));
                        } catch (IOException e) {
                            e.printStackTrace();
                            return super.shouldInterceptRequest(view, url);
                        }
                    } else {
                        Utils.extractSpecificFile(chmFilePath, extractPath + insideFileName, insideFileName);
                    }
                }
                Log.e("2, webviewrequest", url);
                return super.shouldInterceptRequest(view, url);
            }


            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return shouldInterceptRequest(view, request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.startsWith("http") && !url.endsWith(md5File)) {
                    String temp = url.substring("file://".length());
                    if (!temp.startsWith(extractPath)) {
                        url = "file://" + extractPath + temp;
                        view.loadUrl(url);
                        return true;
                    }
                }
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
                //return super.shouldOverrideUrlLoading(view, request);
            }
        });
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setLoadsImagesAutomatically(true);
        ((ClickableWebView) webview).setOnWebViewClickListener(new OnWebViewClicked() {
            @Override
            public void onClick(String url) {
//                Toast.makeText(MainActivity.this, url, Toast.LENGTH_SHORT).show();
                ToastUtils.showShortSafe(url);
            }
        });
        progressLoadWeb = (ProgressBar) findViewById(R.id.progressBar);
        progressLoadWeb.setMax(100);
    }

    @Override
    protected void loadData() {
        new AsyncTask<Void, Void, Void>() {
            int historyIndex = 1;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = new ProgressDialog(ChmReaderActivity.this);
                progress.setTitle("Waiting");
                progress.setMessage("Extracting...");
                progress.setCancelable(false);
                progress.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                md5File = Utils.checkSum(chmFilePath);
                extractPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/CHMReader/" + md5File;
                if (!(new File(extractPath).exists())) {
                    if (Utils.extract(chmFilePath, extractPath)) {
                        try {
                            listSite = Utils.domparse(chmFilePath, extractPath, md5File);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        (new File(extractPath)).delete();
                    }
                } else {
                    listSite = Utils.getListSite(extractPath, md5File);
                    historyIndex = Utils.getHistory(extractPath, md5File);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                webview.loadUrl("file://" + extractPath + "/" + listSite.get(historyIndex));
                if (progress != null) {
                    progress.dismiss();
                    progress = null;
                }
            }
        }.execute();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webview.canGoBack()) {
                        webview.goBack();
                    } else {
                        close();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                close();
                break;

        }
    }
}
