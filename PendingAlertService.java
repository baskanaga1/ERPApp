package com.guruinfo.scm.common.service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import com.guruinfo.scm.DaoSession;
import com.guruinfo.scm.PendingApproval;
import com.guruinfo.scm.PendingApprovalDao;
import com.guruinfo.scm.PendingRequest.PendingRequestDashboard;
import com.guruinfo.scm.PendingRequestList;
import com.guruinfo.scm.PendingRequestListDao;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.SCMApplication;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.utils.Sharedpref;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import static com.guruinfo.scm.common.AppContants.DASHBOARDOFFLINEMODE;
/**
 * Created by ERP on 9/25/2017.
 */
public class PendingAlertService extends Service {
    Thread backgroundThread;
    private boolean isRunning;
    private Context context;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }
    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }
    private Runnable myTask = new Runnable() {
        public void run() {
            boolean isUserLoggedIn = Sharedpref.getPrefBoolean(getApplicationContext(), "USER_LOGGED_IN");
            if ((!(Sharedpref.getPrefBoolean(context, DASHBOARDOFFLINEMODE))) && isUserLoggedIn) {
                if (isInternetAvailable()) {
                    PendingRequestListDao pendingRequestListDao;
                    PendingApprovalDao pendingApprovalDao;
                    List<PendingRequestList> pendingCountLists;
                    List<PendingApproval> pendingCountPOLists;
                    SessionManager session = new SessionManager(context);
                    String uid = session.getUserDetails().get(SessionManager.ID);
                    DaoSession daoSession = ((SCMApplication) context.getApplicationContext()).getDaoSession();
                    pendingRequestListDao = daoSession.getPendingRequestListDao();
                    pendingApprovalDao=daoSession.getPendingApprovalDao();
                    String curDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    pendingCountLists = pendingRequestListDao.queryBuilder().where(PendingRequestListDao.Properties.User_id.eq(uid), PendingRequestListDao.Properties.Status.eq("Pending"), PendingRequestListDao.Properties.Req_date.eq(curDate)).list();
                    pendingCountPOLists = pendingApprovalDao.queryBuilder().where(PendingApprovalDao.Properties.User_id.eq(uid), PendingApprovalDao.Properties.Status.eq("Pending")).list();
                   int pendingCount=pendingCountLists.size()+pendingCountPOLists.size();
                    if (pendingCount>0) {
                        Intent intent = new Intent(getApplicationContext(), PendingRequestDashboard.class);
                       /* if (PendingRequestDashboard.uid != null) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        }*/
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                                R.layout.alert_layout);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                .setContentTitle("Updating Pending Alert...")
                                .setContentText("MR,MPR,VMF Pending Request Update Pending.")
                                // Set Ticker Message
                                .setTicker("EAP Offline Alert...")
                                // Dismiss Notification
                                .setAutoCancel(true)
                                // Set PendingIntent into Notification
                                .setContentIntent(pendingIntent)
                                // .setStyle(bigPictureStyle);
                                // Set RemoteViews into Notification
                                .setContent(remoteViews);
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder.setSmallIcon(R.drawable.eapicon);
                        } else {
                            builder.setSmallIcon(R.drawable.ic_launcher);
                        }
                        remoteViews.setTextColor(R.id.title, getResources().getColor(R.color.black));
                        remoteViews.setTextColor(R.id.msg, getResources().getColor(R.color.dark_grey_bg));
                        remoteViews.setTextViewText(R.id.title, "EAP Offline Request Alert...");
                        remoteViews.setTextViewText(R.id.msg, "MR,MPR,VMF Pending Request Update Pending.");
                        remoteViews.setTextViewText(R.id.pending_count, "" + pendingCount);
                        if (Sharedpref.GetPrefString(context, uid + "_Name") != null) {
                            remoteViews.setTextViewText(R.id.msg, "Hi " + Sharedpref.GetPrefString(context, uid + "_Name") + ", You Have Pending Offline Records. Kindly Update...");
                            String projImg = Sharedpref.GetPrefString(context, uid + "_Photo");
                            if (!projImg.equalsIgnoreCase("0")) {
                                String projImgUrl = AppContants.largeThumbImageURL + projImg;
                                Bitmap bitmap = null;
                                try {
                                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(projImgUrl).getContent());
                                    if(bitmap !=null) {
                                        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                                        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                                        Paint paint = new Paint();
                                        paint.setShader(shader);
                                        paint.setAntiAlias(true);
                                        Canvas c = new Canvas(circleBitmap);
                                        c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
                                        remoteViews.setImageViewBitmap(R.id.userImage, circleBitmap);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        int defaults = 0;
                        defaults = defaults | Notification.DEFAULT_LIGHTS;
                       // defaults = defaults | Notification.DEFAULT_VIBRATE;
                        defaults = defaults | Notification.DEFAULT_SOUND;
                        builder.setDefaults(defaults);
                        if (android.os.Build.VERSION.SDK_INT >= 21) {
                            builder.setColor(getApplicationContext().getResources().getColor(R.color.yellow_bg))
                                    .setPriority(Notification.PRIORITY_HIGH)
                                    .setVisibility(Notification.VISIBILITY_PUBLIC);
                        }
                        // Create Notification Manager
                        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        // Build Notification with Notification Manager
                        Notification notif = builder.build();
                        notif.bigContentView = remoteViews;
                        notificationmanager.notify(10, notif);
                    }
                }
            } else {
                android.app.NotificationManager notificationManager = (android.app.NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(10);
            }
            stopSelf();
        }
    };
    @Override
    public void onDestroy() {
        this.isRunning = false;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }
}