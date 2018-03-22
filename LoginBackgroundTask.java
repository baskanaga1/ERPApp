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
@SuppressLint("NewApi")
@SuppressWarnings("unchecked")
public class LoginBackgroundTask extends AsyncTask<String, String, String> {
    ProgressBar progress_bar;
    public static String respnse = "";
    public static String action = "";
    String httpresponse;
    private Context mContext;
    private View mView;
    ProgressDialog mDialog;
    String mflag = "";
    boolean isOk = false;
    String error_mes;
    TextView textView1;
    boolean loadProgressBar = true;
    private OnTaskCompleted onTaskCompleted;
    public LoginBackgroundTask(Context context) {
        this.mContext = context;
        this.mDialog = new ProgressDialog(context);
    }
    public LoginBackgroundTask(Context context, String flag) {
        this.mContext = context;
        this.mflag = flag;
        this.mDialog = new ProgressDialog(context);
    }
    public LoginBackgroundTask(Context context, String flag, OnTaskCompleted onTaskCompleted) {
        this.onTaskCompleted = onTaskCompleted;
        this.mContext = context;
        this.mflag = flag;
        this.mDialog = new ProgressDialog(context);
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
        if (mDialog != null)
            if (mContext != null && mDialog.isShowing()) {
                if (!isOk) {
                    if (mDialog.isShowing())
                        mDialog.dismiss();
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
                        if (mDialog.isShowing())
                            mDialog.dismiss();
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
        if (loadProgressBar) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.cancel();
            }
            mDialog.setCancelable(true);
            mDialog.show();
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setContentView(R.layout.loader);
            mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            mDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    ((Activity) mContext).finish();
                }
            });
        }
    }
    @SuppressWarnings("static-access")
    @Override
    protected String doInBackground(String... params) {
        action = params[1];
        SessionManager session = new SessionManager(mContext);
        JsonFuntions client = new JsonFuntions();
        try {
            if (BaseActivity.isInternetAvailable()) {
                httpresponse = new HttpRequest().postData(params[0], params[2], HttpRequest.staticURL, mContext);
                isOk = new Boolean(true);
            } else {
                isOk = new Boolean(false);
                error_mes = BaseActivity.context.getString(R.string.no_net);
            }
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
        httpAction act = httpAction.valueOf(action);
        if (loadProgressBar)
            mDialog.dismiss();
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