package com.guruinfo.scm.common.service;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.utils.AppLog;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.utils.FormValidation;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.utils.Sharedpref;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import static com.guruinfo.scm.common.AppContants.DASHBOARDOFFLINEMODE;
/**
 * Created by ERP on 10/4/2017.
 */
public class BackgroundServiceCall extends AsyncTask<String, String, String> {
    ProgressBar progress_bar;
    public static String respnse = "";
    public static String action = "";
    String httpresponse;
    private Context mContext;
    String mflag = "";
    boolean isOk = false;
    String error_mes;
    TextView textView1;
    boolean loadProgressBar = true;
    private OnTaskCompleted onTaskCompleted;
    public BackgroundServiceCall(Context context) {
        this.mContext = context;
    }
    public BackgroundServiceCall(Context context, String flag) {
        this.mContext = context;
        this.mflag = flag;
    }
    public BackgroundServiceCall(Context context, String flag, OnTaskCompleted onTaskCompleted) {
        this.onTaskCompleted = onTaskCompleted;
        this.mContext = context;
        this.mflag = flag;
    }
    public static boolean InEnum(String test) {
        for (httpAction c : httpAction.values()) {
            if (c.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
    protected void onPostExecute(String result) {
        AppLog.e("onPostExecute===>" + mflag, result);
        System.out.println("connection" + isOk);
        if (mContext != null) {
            if (!isOk) {
                if (onTaskCompleted != null)
                    onTaskCompleted.onTaskCompleted(error_mes, "internet");
                else
                    ((OnTaskCompleted) mContext).onTaskCompleted(error_mes, "internet");
            } else {
                respnse = result;
                super.onPostExecute(result);
                if (InEnum(action)) {
                    try {
                        handleResponse(action, result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (onTaskCompleted != null)
                        onTaskCompleted.onTaskCompleted(result, mflag);
                    else
                        ((OnTaskCompleted) mContext).onTaskCompleted(result, mflag);
                }
            }
        }
    }
    public Status statusReturn() {
        return this.getStatus();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @SuppressWarnings("static-access")
    @Override
    protected String doInBackground(String... params) {
        action = params[1];
        SessionManager session = new SessionManager(mContext);
        JsonFuntions client = new JsonFuntions();
        try {
           // if (!(Sharedpref.getPrefBoolean(mContext, DASHBOARDOFFLINEMODE))) {
                if (BaseActivity.isInternetAvailable()) {
                    httpresponse = new HttpRequest().postData(params[0], params[2], HttpRequest.staticURL, mContext);
                   if(httpresponse.equalsIgnoreCase("")){
                       isOk = new Boolean(false);
                       error_mes = BaseActivity.context.getString(R.string.no_server);
                   }else {
                       isOk = new Boolean(true);
                   }
                } else {
                    isOk = new Boolean(false);
                    error_mes = BaseActivity.context.getString(R.string.no_net);
                }
            /*} else {
                isOk = new Boolean(false);
                error_mes = "Offline Mode Enabled.";
            }*/
        } catch (HttpResponseException e) {
            httpresponse = "internet";
        } catch (UnknownHostException e) {
            httpresponse = "internet";
        } catch (ClientProtocolException e) {
            httpresponse = "internet";
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            httpresponse = "internet";
            e.printStackTrace();
        } catch (ConnectTimeoutException e) {
            httpresponse = "internet";
            e.printStackTrace();
        } catch (IOException e) {
            httpresponse = "internet";
            e.printStackTrace();
        } catch (Exception e) {
            httpresponse = "internet";
            e.printStackTrace();
        }
        return httpresponse;
    }
    @SuppressLint("CutPasteId")
    protected void handleResponse(String action, String Response) throws IOException {
        BackgroundServiceCall.httpAction act = BackgroundServiceCall.httpAction.valueOf(action);
        switch (act) {
            case logout:
                try {
                    JSONObject imeiObj = new JSONObject(Response);
                    if (imeiObj.getString("resCode").equals("1")) {
                        new SessionManager(mContext).logoutUserAct((Activity) mContext);
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
    private enum httpAction {
        logout
    }
}