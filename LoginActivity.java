package com.guruinfo.scm;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.LoginBackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.utils.ApiCalls;
import com.guruinfo.scm.common.utils.FormValidation;
import com.guruinfo.scm.common.utils.Sharedpref;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class LoginActivity extends BaseActivity implements OnTaskCompleted {
    String TAG = "LoginActivity";
    EditText userid, password;
    SessionManager session;
    TextView loginbutton, forgotpassword;
    Context context = this;
    String parameters, device, _MODEL, _MANUFACTURER, device_id, provider_name;
    static String uname;
    static String upass;
    ArrayList<HashMap<String, String>> arraylist, loadlist, processlist, approvallist;
    LoginBackgroundTask bt;
    static String registrationId;
    JSONObject loginObj, loginUdata;
    RelativeLayout button_lay1;
    RelativeLayout lay1;
    String userIDFromServer;
    String userTypeFromServer;
    UserTableDao userTableDao;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        registrationId = Sharedpref.GetPrefString(this, "DEVICE_REGISTRATION_ID");
        session = new SessionManager(this);
        TextView link = (TextView) findViewById(R.id.link);
        userid = (EditText) findViewById(R.id.userid);
        password = (EditText) findViewById(R.id.password);
        forgotpassword = (TextView) findViewById(R.id.forgotpassword);
        loginbutton = (TextView) findViewById(R.id.loginbutton);
        button_lay1 = (RelativeLayout) findViewById(R.id.button_lay1);
        lay1 = (RelativeLayout) findViewById(R.id.lay1);
        lay1.setBackgroundColor(Color.parseColor("#808080"));
        userTableDao = daoSession.getUserTableDao();
        android.app.NotificationManager notificationManager = (android.app.NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(10);
        notificationManager.cancel(0);
        notificationManager.cancel(75);
        notificationManager.cancelAll();
        forgotpassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog1(context);
            }
        });
        link.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.guruinfo.co.in/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        _MODEL = android.os.Build.MODEL;
        _MANUFACTURER = android.os.Build.MANUFACTURER;
        device_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        provider_name = getCarrierName(context);
        device = getIMEI(context);
    }
    public String getIMEI(Context context) {
        String imei = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null)
            try {
                imei = tm.getDeviceId();
            } catch (Exception e) {
                imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        return imei;
    }
    public String getCarrierName(Context context) {
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String carrierName = mngr.getNetworkOperatorName();
        return carrierName;
    }
    public void login(View v) {
        FormValidation fv = new FormValidation();
        if (fv.isRequired(userid)) {
            setError(userid, "Requser");
        } else if (fv.isRequired(password)) {
            setError(password, "Reqpwd");
        } else {
            Sharedpref.setPrefBoolean(context, "islogin", true);
            Sharedpref.SetPrefString(context, "type", "");
            Sharedpref.SetPrefString(context, "UserType", "");
            uname = userid.getText().toString();
            upass = password.getText().toString();
            try {
                if (isInternetAvailable1()) {
                    String versionName = getApplicationContext().getPackageManager().getPackageInfo("com.guruinfo.scm", 0).versionName;
                    parameters = "{'Action':'Login','Cre_Id':'', 'UID':'" + uname + "', 'PWD':'" + upass + "','Device':'" + device + "','Model':'" + _MODEL + "','Mac_Addresss':'" + device_id + "','Manufacturer':'" + _MANUFACTURER + "','mobile_reg_id':'" + registrationId + "','app_version':'" + versionName + "'}";
                    //Log.e("login url",parameters);
                    bt = new LoginBackgroundTask(this, "login");
                    bt.execute("", "", parameters);
                } else {
                    List<UserTable> loginList = userTableDao.queryBuilder().where(UserTableDao.Properties.Unique_id.eq(uname)).list();
                    if (loginList.size() > 0) {
                        if (!(loginList.get(0).getUnique_id().equals(uname))) {
                            setToast("Invalid Login Credential");
                        } else if (!(loginList.get(0).getPwd().equals(upass))) {
                            setToast("Invalid Password");
                        } else {
                            parseJson(loginList.get(0).getUser_id());
                        }
                    } else {
                        showDialog("Please Check Your Internet Connection...");
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @SuppressLint("InlinedApi")
    @Override
    public void onTaskCompleted(String values, String flag) {
        if (flag.equals("internet"))
            showInternetDialog(context, values);
        else if (flag.equals("login")) {
            parseJson(values);
        } else {
            parseSessionJosn(values);
        }
    }
    public void showDialog(String msg) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_dialog);
        TextView text = (TextView) dialog.findViewById(R.id.alert_msg);
        text.setText(msg);
        Button dialogButton = (Button) dialog.findViewById(R.id.ok_btn);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void showInternetDialog(Context activity, String err_msg) {
        final Dialog dialog = new Dialog(context, R.style.MaterialDialogSheet);
        dialog.setContentView(R.layout.offline_mode_alert); // your custom view.
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        TextView text = (TextView) dialog.findViewById(R.id.alert_msg);
        LinearLayout offLineLayout = (LinearLayout) dialog.findViewById(R.id.offline_lay);
        text.setText(err_msg);
        offLineLayout.setVisibility(View.GONE);
        Button offLineButton = (Button) dialog.findViewById(R.id.offline_btn);
        Button retryButton = (Button) dialog.findViewById(R.id.retry_btn);
        // Button exitButton = (Button) dialog.findViewById(R.id.exit_btn);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                bt = new LoginBackgroundTask(context, "login");
                bt.execute("", "", parameters);
            }
        });
    /*exitButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    });*/
        dialog.show();
    }
    public void showDialog1(final Context activity) {
        String msg = "Are You Employee?";
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(activity);
        View mView = layoutInflaterAndroid.inflate(R.layout.alertbuilder, null);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setView(mView);
        TextView message = (TextView) mView.findViewById(R.id.message);
        message.setText(msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton("Employee", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(LoginActivity.this, Forgot_Password.class);
                startActivity(intent);
            }
        });
        try {
            final AlertDialog alert11 = builder1.create();
            alert11.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button btnPositive = alert11.getButton(Dialog.BUTTON_POSITIVE);
                    btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.Filter_textsize));
                }
            });
            alert11.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void parseJson(String values) {
        try {
            loginObj = new JSONObject(values);
            if (loginObj.getString("code").equalsIgnoreCase("1")) {
                setToast(loginObj.getString("msg"));
                System.out.println("SUCESSMSG:" + loginObj.getString("msg"));
                loginUdata = loginObj.getJSONObject("resp");
                System.out.println("loginUdata" + loginUdata.toString());
                JSONArray dasharr = loginUdata.getJSONArray("Dash_Board");
                JSONArray processarr = loginUdata.getJSONArray("Process_Request");
                JSONArray loadarr = loginUdata.getJSONArray("Load_Request");
                JSONArray approvearr = loginUdata.getJSONArray("Approvals");
                arraylist = ApiCalls.getArraylistfromJson(dasharr.toString());
                processlist = ApiCalls.getArraylistfromJson(processarr.toString());
                loadlist = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> hash;
                for (int i = 0; i < loadarr.length(); i++) {
                    hash = new HashMap<String, String>();
                    JSONObject object = loadarr.getJSONObject(i);
                    String action = object.getString("Action");
                    action = action.replace("\r\n", "");
                    action = action.replace("\n", "");
                    action = action.replace("\r", "");
                    hash.put("Action", action);
                    loadlist.add(hash);
                }
                approvallist = ApiCalls.getArraylistfromJson(approvearr.toString());
                String name = "", approval_status;
                userIDFromServer = loginUdata.getString("uid");
                userTypeFromServer = loginUdata.getString("Login_Type");
                Sharedpref.SetPrefString(this, "USER_ID_FROM_SERVER", userIDFromServer);
                Sharedpref.SetPrefString(this, "USER_PWD_FROM_SERVER", upass);
                Sharedpref.SetPrefString(this, "USER_TYPE_FROM_SERVER", userTypeFromServer);
                Sharedpref.setPrefBoolean(this, "USER_LOGGED_IN", true);
                if (loginUdata.getString("uid").equalsIgnoreCase("admin")) {
                    name = "Admin";
                } else if (loginUdata.getString("uid").equalsIgnoreCase("erptest")) {
                    name = "Erptest";
                } else if (loginUdata.getString("uid").equalsIgnoreCase("erp")) {
                    name = "Erp";
                } else {
                    name = loginUdata.getString("name");
                }
                try {
                    loginUdata.getString("licenseKey");
                    new SessionManager(getApplicationContext()).createLoginSession(loginUdata.getString("uid"), name, loginUdata.getString("Approval"), loginUdata.getString("cr_id"), loginUdata.getString("name"), loginUdata.getString("email"), loginUdata.getString("licenseKey"), "true");
                } catch (Exception e) {
                    new SessionManager(getApplicationContext()).createLoginSession(loginUdata.getString("uid"), name, loginUdata.getString("Approval"), loginUdata.getString("cr_id"), "", "", "", "false");
                }
                userTableDao.insertOrReplace(new UserTable(loginUdata.getString("uid"), loginUdata.getString("Login_Type"), values, upass, loginUdata.getString("cr_id")));
                Sharedpref.SetPrefString(context, "username", loginUdata.getString("uid"));
                Sharedpref.writegson(this, arraylist, "dashboard");
                Sharedpref.writegson(this, processlist, "processrequest");
                Sharedpref.writegson(this, loadlist, "loadrequest");
                Sharedpref.writegson(this, approvallist, "approvallist");
                if (loginUdata.getString("Login_Type").equalsIgnoreCase("employee") || loginUdata.getString("Login_Type").equalsIgnoreCase("standard")) {
                    System.out.println("loginsuccesss");
                    if (getIntent().getBundleExtra("display") != null) {
                        if (getIntent().getStringExtra("loginId").equalsIgnoreCase(loginUdata.getString("uid"))) {
                            Bundle bundle = getIntent().getBundleExtra("display");
                            String className = bundle.getString("className");
                            String moduleId = bundle.getString("id");
                            String colorCode = bundle.getString("colorCode");
                            Class<?> classType = Class.forName(className);
                            Intent intent = new Intent(getApplicationContext(), classType);
                            intent.putExtra("id", moduleId);
                            intent.putExtra("colorCode", colorCode);
                            intent.putExtra("rights", "");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            finish();
                            traversToNextActivity(context, SCMDashboardActivityLatest.class);
                        }
                    } else {
                        finish();
                        traversToNextActivity(context, SCMDashboardActivityLatest.class);
                    }
                    Log.d(TAG, "parseJson - Setting UserType ERP****");
                    Sharedpref.SetPrefString(context, "type", "employee");
                    Sharedpref.SetPrefString(context, "UserType", "employee");
                    finish();
                }
            } else if (loginObj.getString("code").equalsIgnoreCase("0")) {
                setToast(loginObj.getString("msg"));
            } else if (loginObj.getString("code").equalsIgnoreCase("403")) {
                showSessionDialog(context, loginObj.getString("msg"));
            } else {
                setToast("Sorry, Something went wrong.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            setToast("Oops, something went wrong. Please try again later.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void parseSessionJosn(String values) {
        try {
            JSONObject loginObj = new JSONObject(values);
            if (loginObj.getString("code").equals("1")) {
                setToast(loginObj.getString("msg"));
            } else if (loginObj.getString("code").equals("403")) {
                showSessionScreensDialog(this, loginObj.getString("msg"));
            } else {
                setToast(loginObj.getString("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            setToast("Error");
        }
    }
    public static void showSessionDialog(final Context context, String message) {
        /*AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        TextView tv = new TextView(context);
        tv.setText("Activity Log");
        tv.setPadding(30, 30, 30, 30);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);
        builder1.setCustomTitle(tv);*/
        //builder1.setMessage(message);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        TextView tv = new TextView(context);
        TextView mv = new TextView(context);
        tv.setText("Activity Log");
        mv.setText(message);
        mv.setTextColor(getContext().getResources().getColor(R.color.black));
        tv.setGravity(Gravity.CENTER);
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            tv.setPadding(30, 30, 30, 30);
            mv.setPadding(30, 0, 30, 0);
            tv.setTextSize(35);
            mv.setTextSize(25);
        } else {
            tv.setPadding(20, 20, 20, 20);
            mv.setPadding(20, 0, 20, 0);
            tv.setTextSize(20);
            mv.setTextSize(16);
        }
        builder1.setCustomTitle(tv);
        builder1.setView(mv);
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Sharedpref.setPrefBoolean(context, "USER_LOGGED_IN", false);
                        Sharedpref.setPrefBoolean(context, "islogin", true);
                        String parameters = "{'Action':'LOGOUT','mode':'session','Cre_Id':'', 'UID':'" + uname + "', 'PWD':'" + upass + "','mobile_reg_id':'0'}";
                        LoginBackgroundTask bt = new LoginBackgroundTask(context, "session");
                        bt.execute("", "", parameters);
                    }
                });
        final AlertDialog alert11 = builder1.create();
        alert11.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alert11.getButton(Dialog.BUTTON_POSITIVE);
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.Filter_textsize));
            }
        });
        alert11.show();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
