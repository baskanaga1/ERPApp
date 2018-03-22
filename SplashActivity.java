package com.guruinfo.scm;
import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.BaseLoginActivity;
import com.guruinfo.scm.common.SCMApplication;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.db.SCMDataBaseOpenHelper;
import com.guruinfo.scm.common.notification.NotificationManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.utils.ApiCalls;
import com.guruinfo.scm.common.utils.FormValidation;
import com.guruinfo.scm.common.utils.Sharedpref;
import org.greenrobot.greendao.database.Database;
import org.jsoup.Jsoup;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static com.guruinfo.scm.common.AppContants.DASHBOARDOFFLINEMODE;
import static com.guruinfo.scm.common.AppContants.DB_PWD;
import static com.guruinfo.scm.common.SCMApplication.daoSession;
import static com.guruinfo.scm.common.SCMApplication.db;
import static com.guruinfo.scm.common.SCMApplication.helper;
public class SplashActivity extends BaseActivity implements OnTaskCompleted {
    public static String TAG = "SplashActivity";
    Context context = this;
    SessionManager session;
    ProgressDialog mDialog;
    String error_mes;
    static boolean retry = false;
    static boolean isserver = false;
    TextView version;
    String userType;
    String uid, Cre_Id;
    String registrationId;
    boolean isRegIDSent;
    BackgroundTask bt;
    String parameters;
    Activity activity = this;
    Boolean isActive = false;
    int count = 0;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);
        userType = Sharedpref.GetPrefString(context, "type");
        if (Sharedpref.GetPrefString(activity, "DEVICE_REGISTRATION_ID").length() <= 0)
            NotificationManager.getInstance().registerDevice(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(tp);
        }
        version = (TextView) findViewById(R.id.version);
        session = new SessionManager(this);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
        String version_name = null;
        try {
            version_name = getApplicationContext().getPackageManager().getPackageInfo("com.guruinfo.scm", 0).versionName;
            version.setText("Version " + version_name);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        getData();
    }
    public Boolean permissionHandle() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int loc = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        /*if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }*/
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            if (!isActive) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                        (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                isActive = true;
            }
            return false;
        } else {
            try {
                setupDatabase();
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        }
        return true;
    }
    public void setupDatabase() {
        try {
            if(SCMApplication.daoSession==null) {
                File path = new File(Environment.getExternalStorageDirectory(), "EAP/data/SCM");
                path.getParentFile().mkdirs();
                // SCMDataBaseOpenHelper SCMDatabaseOpenHelper = new SCMDataBaseOpenHelper(this, path.getAbsolutePath(), null);
                // db = SCMDatabaseOpenHelper.getWritableDatabase();
                DaoMaster.DevOpenHelper helper1 = new DaoMaster.DevOpenHelper(this, path.getAbsolutePath(), null);
                db = helper1.getEncryptedWritableDb(DB_PWD);
                DaoMaster daoMaster = new DaoMaster(db);
                SCMApplication.daoSession = daoMaster.newSession();
                helper = helper1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean web_update() {
        boolean isVersion = false;
        if (!isInternetAvailable() || (Sharedpref.getPrefBoolean(context, DASHBOARDOFFLINEMODE))) {
            isVersion = true;
        } else {
            try {
                String curVersion = getApplicationContext().getPackageManager().getPackageInfo("com.guruinfo.scm", 0).versionName;
                System.out.println("versioncode" + curVersion);
                String newVersion;
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.guruinfo.scm&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get().select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                System.out.println("newVersion" + newVersion);
                isVersion = newVersion.equals(curVersion);
//		        return (value(curVersion) < value(newVersion)) ? true : false;
            } catch (Exception e) {
                isVersion = true;
                e.printStackTrace();
            }
        }
        return isVersion;
    }
    private long value(String string) {
        string = string.trim();
        if (string.contains(".")) {
            final int index = string.lastIndexOf(".");
            return value(string.substring(0, index)) * 100 + value(string.substring(index + 1));
        } else {
            return Long.valueOf(string);
        }
    }
    public void getData() {
        Log.e("", "true");
        int SPLASH_TIME_OUT = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (permissionHandle()) {
                       /* if (Sharedpref.GetPrefString(activity, "DEVICE_REGISTRATION_ID").length() <= 0)
                            getData();
                        else*/
                            loadData();
                    } else {
                        getData();
                    }
                } catch (Exception e) {
                  //  if (Sharedpref.GetPrefString(activity, "DEVICE_REGISTRATION_ID").length() <= 0)
                        getData();
                   /* else
                        loadData();*/
                }
            }
        }, SPLASH_TIME_OUT);
    }
    public void loadData() {
        //Local
        doHome();
        //Live
       // doHome1();
    }
    private void doHome() {
        try {
            Log.d(TAG, "USER TYPE in doHome is -- " + Sharedpref.GetPrefString(context, "type"));
            registrationId = Sharedpref.GetPrefString(activity, "DEVICE_REGISTRATION_ID");
            isRegIDSent = Sharedpref.getPrefBoolean(activity, "IS_REG_ID_SENT_TO_SERVER");
          //  DaoSession daoSession = ((SCMApplication) context.getApplicationContext()).getDaoSession();
            if (session.isLoggedIn()) {
                if (Sharedpref.GetPrefString(context, "type").equalsIgnoreCase("employee") || Sharedpref.GetPrefString(context, "UserType").equalsIgnoreCase("employee")) {
                    try {
                        // ProjMrMasterDao projMrMasterDao = daoSession.getProjMrMasterDao();
                        // List<ProjMrMaster> mrList = projMrMasterDao.queryBuilder().list();
                        finish();
                        traversToNextActivity(context, SCMDashboardActivityLatest.class);
                    } catch (Exception e) {
                        // forceUpdateDialog();
                    }
                }
            } else {
                try {
                    // ProjMrMasterDao projMrMasterDao = daoSession.getProjMrMasterDao();
                    // List<ProjMrMaster> mrList = projMrMasterDao.queryBuilder().list();
                    finish();
                    traversToNextActivity(context, LoginActivity.class);
                } catch (Exception e) {
                    // forceUpdateDialog();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void doHome1() {
        try {
            Log.d(TAG, "USER TYPE in doHome1 is -- " + Sharedpref.GetPrefString(context, "type"));
            registrationId = Sharedpref.GetPrefString(activity, "DEVICE_REGISTRATION_ID");
            isRegIDSent = Sharedpref.getPrefBoolean(activity, "IS_REG_ID_SENT_TO_SERVER");
           // DaoSession daoSession = ((SCMApplication) context.getApplicationContext()).getDaoSession();
           /* if (registrationId.length() > 0 && !isRegIDSent) {
                sendRegIdToServer();
            }*/
            if (session.isLoggedIn()) {
                if (web_update()) {
                    if (Sharedpref.GetPrefString(context, "type").equalsIgnoreCase("employee") || Sharedpref.GetPrefString(context, "UserType").equalsIgnoreCase("employee")) {
                        try {
                           // ProjMrMasterDao projMrMasterDao = daoSession.getProjMrMasterDao();
                           // List<ProjMrMaster> mrList = projMrMasterDao.queryBuilder().list();
                            finish();
                            traversToNextActivity(context, SCMDashboardActivityLatest.class);
                        } catch (Exception e) {
                            // forceUpdateDialog();
                        }
                    }
                } else {
                    showVersion(this);
                }
            } else {
                if (web_update()) {
                    try {
                        //ProjMrMasterDao projMrMasterDao = daoSession.getProjMrMasterDao();
                       // List<ProjMrMaster> mrList = projMrMasterDao.queryBuilder().list();
                        finish();
                        traversToNextActivity(context, LoginActivity.class);
                       // ApiCalls.doThread(this, LoginActivity.class, "");
                    } catch (Exception e) {
                        // forceUpdateDialog();
                    }
                } else {
                    showVersion(this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void forceUpdateDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        TextView tv = new TextView(context);
        TextView mv = new TextView(context);
        tv.setText("Alert");
        mv.setText("Please Clear your Local Data.");
        mv.setTextColor(context.getResources().getColor(R.color.black));
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
                        /*finish();
                        Uri packageURI = Uri.parse("package:" + SplashActivity.class.getPackage().getName());
                        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                        startActivity(uninstallIntent);*/
                        ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
                        finish();
                        startActivity(getIntent());
                    }
                });
        final AlertDialog alert11 = builder1.create();
        alert11.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alert11.getButton(Dialog.BUTTON_POSITIVE);
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.Filter_textsize));
            }
        });
        alert11.show();
    }
    public void showInternetDialog(Context activity, String err_msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setMessage(err_msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                //android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder1.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                getData();
            }
        });
        try {
            final AlertDialog alert11 = builder1.create();
            alert11.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button btnPositive = alert11.getButton(Dialog.BUTTON_POSITIVE);
                    btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.Filter_textsize));
                    //btnPositive.setTextSize(40);
                    Button btnNegative = alert11.getButton(Dialog.BUTTON_NEGATIVE);
                    btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.Filter_textsize));
                    //btnNegative.setTextSize(40);
                    Button btnNeutral = alert11.getButton(Dialog.BUTTON_NEUTRAL);
                    btnNeutral.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.Filter_textsize));
                    TextView textView = (TextView) alert11.findViewById(android.R.id.message);
                    if (context.getResources().getBoolean(R.bool.isTablet)) {
                        textView.setTextSize(25);
                    } else {
                        textView.setTextSize(16);
                    }
                }
            });
            alert11.show();
            //alert11.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void showVersion(final Activity activity) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(activity);
        View mView = layoutInflaterAndroid.inflate(R.layout.alertbuilder, null);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setView(mView);
        TextView message = (TextView) mView.findViewById(R.id.message);
        message.setText("A New Version is Available.Please Update");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                activity.finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                // android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder1.setNegativeButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.cancel();
                        activity.finish();
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.guruinfo.scm&hl=en")));
                    }
                });
            }
        });
        final AlertDialog alert11 = builder1.create();
        alert11.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alert11.getButton(Dialog.BUTTON_POSITIVE);
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.Filter_textsize));
                Button btnNegative = alert11.getButton(Dialog.BUTTON_NEGATIVE);
                btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.Filter_textsize));
                Button btnNeutral = alert11.getButton(Dialog.BUTTON_NEUTRAL);
                btnNeutral.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.Filter_textsize));
            }
        });
        alert11.show();
    }
    private void sendRegIdToServer() {
          /*Setting this two values to call the API before login process*/
        Sharedpref.setPrefBoolean(context, "islogin", true);
        Sharedpref.SetPrefString(context, "type", "");
        Log.d(TAG, "sendRegIdToServer--SETTING USER TYPE TO NONE --****");
        parameters = "{'Action':'REG_DEVICE','mobile_reg_id':'" + registrationId + "'}";
        bt = new BackgroundTask(context, "REG_DEVICE_ID");
        bt.execute("", "", parameters);
    }
    @Override
    public void onTaskCompleted(String values, String flag) {
        if (flag.equals("REG_DEVICE_ID")) {
            System.out.println("Sent to server successfully");
            Sharedpref.setPrefBoolean(activity, "IS_REG_ID_SENT_TO_SERVER", true);
            Sharedpref.SetPrefString(context, "type", userType);
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
       /* progressBar.setVisibility(View.VISIBLE);
        ((SplashActivity) context).getData();
        userType = Sharedpref.GetPrefString(context, "type");
        Log.d(TAG, "onRestart-- Setting userType--****" + userType);*/
    }
}