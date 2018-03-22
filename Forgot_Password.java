package com.guruinfo.scm;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;
import com.guruinfo.scm.common.service.HttpRequest;
/**
 * Created by ERP on 4/15/2017.
 */
public class Forgot_Password extends Activity {
    WebView web;
    ImageButton close_dialog_button;
    ProgressDialog pd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.webview);
        close_dialog_button = (ImageButton) findViewById(R.id.close_dialog_button);
        pd = ProgressDialog.show(this, "", "Loading...",true);
        close_dialog_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
            }
        });
        web = (WebView) findViewById(R.id.webview01);
        String url = HttpRequest.domainURL+"?AId=FORGOT_PASSWORD_LOGIN";
        web.setWebViewClient(new myWebClient());
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(url);
    }
    public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            Log.i("redirecturl", "shouldOverrideUrlLoading() URL : " + url);
            String redirecturl = HttpRequest.domainURL+"?AId=A1";
            if(redirecturl.equals(url))
            {
                finish();
            }
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            //super.onPageFinished(view, url);
            if(pd!=null && pd.isShowing())
            {
                pd.dismiss();
            }
        }
    }
    // To handle "Back" key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
            web.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
