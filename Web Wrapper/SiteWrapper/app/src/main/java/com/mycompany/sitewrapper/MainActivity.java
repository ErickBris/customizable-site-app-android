package com.mycompany.sitewrapper;

import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    //CHANGE TO YOUR URL



    String LOOKUP_URL = "http://www.codecanyon.net/";


    //You can use this, but if you create your own page and name it differently, put that here
    String OFFLINE_URL = "file:///android_asset/internal/nointernet.html";


    ////




    private static String TAG = MainActivity.class.getSimpleName();

    WebView myWebView;
    ProgressBar progressBar;
    boolean loadingFinished = true;
    boolean redirect = false;

    Button backButton, fwdButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        backButton = (Button) findViewById(R.id.action_back);
        fwdButton = (Button) findViewById(R.id.action_forward);

        setupWebView();

        loadHomePage();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void setupWebView() {

        myWebView = (WebView) findViewById(R.id.webview);
        final WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(false);


        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                if (!loadingFinished) {
                    redirect = true;
                }

                loadingFinished = false;
                myWebView.loadUrl(urlNewString);
                return true;
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadingFinished = false;
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished && !redirect) {
                    progressBar.setVisibility(View.INVISIBLE);

                } else {
                    redirect = false;
                }

            }
        });
    }

    private void setButtonStatus() {
        if (myWebView.canGoForward()) {
            fwdButton.setEnabled(true);
        } else {
            fwdButton.setEnabled(false);
        }

        if (myWebView.canGoBack()) {
            backButton.setEnabled(true);
        } else {
            backButton.setEnabled(false);
        }
    }

    private void loadHomePage() {
        // check if you are connected or not
        if (isConnected()) {
            Log.i(TAG, "Connected");
            navigateToURL(LOOKUP_URL);
        } else {
//            or you could show a toast message or both!
            Toast toast = Toast.makeText(getApplicationContext(), "No Internet Access", Toast.LENGTH_SHORT);
            toast.show();

            navigateToURL(OFFLINE_URL);

        }
    }

    private void navigateToURL(String url) {

        myWebView.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            // action with ID action_refresh was selected
            case R.id.action_refresh:
                myWebView.reload();
                break;

            case R.id.action_back:
                if (myWebView.canGoBack()) {
                    myWebView.goBack();
                }
                break;

            case R.id.action_forward:
                if (myWebView.canGoForward()) {
                    myWebView.goForward();
                }
                break;

            case R.id.action_home:
                loadHomePage();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


}
