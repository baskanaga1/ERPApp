package com.guruinfo.scm.Chat;/*
package com.guruinfo.ampemp.Chat;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import com.guruinfo.ampemp.Chat_MainFragment;
import com.guruinfo.ampemp.R;
import com.guruinfo.ampemp.common.AppContants;
import com.guruinfo.ampemp.common.SessionManager;
import com.guruinfo.ampemp.common.Sharedpref;
import com.guruinfo.others.service.RestClientHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import static com.guruinfo.ampemp.Chat.Chat_list.isScreenOpen;
import static com.guruinfo.ampemp.Chat.Chat_list.peoples_arraylist;
import static com.guruinfo.ampemp.Chat.GroupChat.groupRoomId;
import static com.guruinfo.ampemp.Chatwindow.indivRoomId;
import static com.guruinfo.ampemp.common.HttpRequest.chatURL;
*/
/**
 * Created by Kannan G on 7/14/2017.
 *//*
public class ChatService extends Service {
    String TAG = "ChatService";
    Context context = this;
    private final IBinder binder = new MyBinder();
    static String cr_id, uid;
    SessionManager session;
    Intent mainIntent;
    Boolean sessenExp = false;
    public static Boolean isLoading;
    public static String lastCID = "0";
    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }
    public void doServiceStuff() {
        // task.execute();
        onTaskRemoved(mainIntent);
        isLoading = true;
        String req = "{'submode':'EMP_CHAT_DETAILS','Cre_Id':'" + cr_id + "','UID':'" + uid + "'}";
        String flag = "ChatHistory";
        onLoad(req, flag);
    }
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        //start a separate thread and start listening to your network object
    }
    private void getFromSession() {
        session = new SessionManager(getApplicationContext());
        uid = session.getUserDetails().get(SessionManager.ID);
        cr_id = session.getUserDetails().get(SessionManager.CR_ID);
    }
    // create an inner Binder class
    public class MyBinder extends Binder {
        public ChatService getService() {
            getFromSession();
            return ChatService.this;
        }
    }
    public void onLoad(final String req, final String flag) {
        String requestParameter = req;
        Log.d(TAG, requestParameter);
        RestClientHelper.getInstance().getChatURL(requestParameter, context, new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    System.out.println(flag + " --> " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    parseJSONResponse(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(String error) {
                reLoad();
            }
        });
    }
    */
/*public void onLoad(final String req, final String flag) {
        String requestParameter = req;
        Log.d(TAG, requestParameter);
        BackgroundServiceCall backgroundTask = new BackgroundServiceCall(context, flag, new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String values, String flagMsg) {
                try {
                    if (flagMsg.equals("internet")) {
                    } else {
                        JSONObject jsonObject = new JSONObject(values);
                        parseJSONResponse(jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        backgroundTask.execute("", "", requestParameter);
    }*//*
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        session = new SessionManager(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        uid = session.getUserDetails().get(SessionManager.ID);
        if (session.isLoggedIn() || uid != null) {
            Intent restartService = new Intent(context, this.getClass());
            restartService.setPackage(getPackageName());
            startService(restartService);
        } else {
            isLoading = null;
            stopService(rootIntent);
        }
        super.onTaskRemoved(rootIntent);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Log.i("Service", "Service Created");
        mainIntent = intent;
        onTaskRemoved(intent);
        return Service.START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Service", "Service Destroy");
    }
    @Override
    public boolean stopService(Intent name) {
        Log.i("Service", "Service Stop");
        // TODO Auto-generated method stub
        return super.stopService(name);
    }
    private void parseJSONResponse(JSONObject responseJSONObject) {
        try {
            if (responseJSONObject.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_1)) {
                // new task().execute(responseJSONObject);
                JSONArray empNameArray = responseJSONObject.getJSONObject("HomeProcess").getJSONArray("empNameList");
                JSONArray groupNameArray = responseJSONObject.getJSONObject("HomeProcess").getJSONArray("groupNameList");
                Sharedpref.SetPrefString(context, "empName", responseJSONObject.getJSONObject("HomeProcess").getString("name"));
                Sharedpref.SetPrefString(context, "People", empNameArray.toString());
                Sharedpref.SetPrefString(context, "Group", groupNameArray.toString());
                if (responseJSONObject.getString("isAllowClearChat").equalsIgnoreCase("true"))
                    Sharedpref.setPrefBoolean(context, "isChatClear", true);
                else
                    Sharedpref.setPrefBoolean(context, "isChatClear", false);
                JSONArray peoplesNamelist = responseJSONObject.getJSONArray("values");
                ArrayList<HashMap<String, String>> chatHistory = new ArrayList<>();
                String displayValue = "";
                if (peoplesNamelist.length() > 0) {
                    for (int i = 0; i < peoplesNamelist.length(); i++) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("empid", uid);
                        hashMap.put("docid", "0");
                        JSONArray memberarray = peoplesNamelist.getJSONObject(i).getJSONArray("memberDetails");
                        if (memberarray.length() > 0) {
                            for (int j = 0; j < memberarray.length(); j++) {
                                if (memberarray.getJSONObject(j).getString("empCode").equalsIgnoreCase(peoplesNamelist.getJSONObject(i).getString("to_userid")))
                                    hashMap.put("docid", memberarray.getJSONObject(j).getString("empPhoto"));
                            }
                        }
                        hashMap.put("name", peoplesNamelist.getJSONObject(i).getString("room_name"));
                        hashMap.put("roomId", peoplesNamelist.getJSONObject(i).getString("room_id"));
                        hashMap.put("groupId", peoplesNamelist.getJSONObject(i).getString("groupId"));
                        hashMap.put("message", peoplesNamelist.getJSONObject(i).getString("message"));
                        hashMap.put("empcode", peoplesNamelist.getJSONObject(i).getString("to_userid"));
                        hashMap.put("memberDetails", memberarray.toString());
                        hashMap.put("chatType", peoplesNamelist.getJSONObject(i).getString("chat_type"));
                        hashMap.put("msgUnReadCount", peoplesNamelist.getJSONObject(i).getString("msgUnReadCount"));
                        chatHistory.add(hashMap);
                    }
                    Sharedpref.writegson(context, chatHistory, "ChatHistory");
                    if (peoples_arraylist != null) {
                        peoples_arraylist = chatHistory;
                    }
                    JSONArray unReadMsgArray = responseJSONObject.getJSONArray("valuesUnread");
                    for (int i = 0; i < unReadMsgArray.length(); i++) {
                        if (unReadMsgArray.getJSONObject(i).getString("chatType").equalsIgnoreCase("individual")) {
                            if (indivRoomId != null) {
                                if (!unReadMsgArray.getJSONObject(i).getString("roomId").equalsIgnoreCase(indivRoomId)) {
                                    if (displayValue.equalsIgnoreCase(""))
                                        displayValue = unReadMsgArray.getJSONObject(i).getString("groupName") + ": " + unReadMsgArray.getJSONObject(i).getString("Message");
                                    else
                                        displayValue = displayValue + "\n" + unReadMsgArray.getJSONObject(i).getString("groupName") + ": " + unReadMsgArray.getJSONObject(i).getString("Message");
                                }
                            } else {
                                if (displayValue.equalsIgnoreCase(""))
                                    displayValue = unReadMsgArray.getJSONObject(i).getString("groupName") + ": " + unReadMsgArray.getJSONObject(i).getString("Message");
                                else
                                    displayValue = displayValue + "\n" + unReadMsgArray.getJSONObject(i).getString("groupName") + ": " + unReadMsgArray.getJSONObject(i).getString("Message");
                            }
                        } else if (unReadMsgArray.getJSONObject(i).getString("chatType").equalsIgnoreCase("group")) {
                            if (groupRoomId != null) {
                                if (!unReadMsgArray.getJSONObject(i).getString("roomId").equalsIgnoreCase(groupRoomId)) {
                                    if (displayValue.equalsIgnoreCase(""))
                                        displayValue = unReadMsgArray.getJSONObject(i).getString("groupName") + ": " + unReadMsgArray.getJSONObject(i).getString("Message");
                                    else
                                        displayValue = displayValue + "\n" + unReadMsgArray.getJSONObject(i).getString("groupName") + ": " + unReadMsgArray.getJSONObject(i).getString("Message");
                                }
                            } else {
                                if (displayValue.equalsIgnoreCase(""))
                                    displayValue = unReadMsgArray.getJSONObject(i).getString("groupName") + ": " + unReadMsgArray.getJSONObject(i).getString("Message");
                                else
                                    displayValue = displayValue + "\n" + unReadMsgArray.getJSONObject(i).getString("groupName") + ": " + unReadMsgArray.getJSONObject(i).getString("Message");
                            }
                        }
                    }
                    if (isScreenOpen == null && !(lastCID.equalsIgnoreCase(responseJSONObject.getString("LastMsgId"))) && !(displayValue.equalsIgnoreCase(""))) {
                        lastCID = responseJSONObject.getString("LastMsgId");
                        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                                R.layout.customnotification);
                        String notificationTitleText = "Amarprakash CORP";
                        String message = "You Have New Message...";
                        Intent intent = new Intent(this, Chat_MainFragment.class);
                        intent.setAction(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent pIntent = PendingIntent.getActivity(this, 23, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                                .setContentTitle(notificationTitleText)
                                .setContentText(message)
                                // Set Ticker Message
                                .setTicker("Smart Chat Message")
                                // Dismiss Notification
                                .setAutoCancel(true)
                                // Set PendingIntent into Notification
                                .setContentIntent(pIntent)
                                // .setStyle(bigPictureStyle);
                                // Set RemoteViews into Notification
                                .setContent(remoteViews);
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder.setSmallIcon(R.drawable.ic_amarprakash_icon);
                        } else {
                            builder.setSmallIcon(R.drawable.ic_launcher);
                        }
                        try {
                            */
/*if (!(Sharedpref.GetPrefString(getApplicationContext(), "Notification_sound").equalsIgnoreCase(""))) {
                                Uri uri = Uri.parse(Sharedpref.GetPrefString(getApplicationContext(), "Notification_sound"));
                                builder.setSound(uri);
                            } else {*//*
                            int defaults = 0;
                            defaults = defaults | Notification.DEFAULT_LIGHTS;
                            defaults = defaults | Notification.DEFAULT_VIBRATE;
                            defaults = defaults | Notification.DEFAULT_SOUND;
                            builder.setDefaults(defaults);
                            //  }
                        } catch (Exception e) {
                            int defaults = 0;
                            defaults = defaults | Notification.DEFAULT_LIGHTS;
                            defaults = defaults | Notification.DEFAULT_VIBRATE;
                            defaults = defaults | Notification.DEFAULT_SOUND;
                            builder.setDefaults(defaults);
                            e.printStackTrace();
                        }
                        if (android.os.Build.VERSION.SDK_INT >= 21) {
                            builder.setColor(getApplicationContext().getResources().getColor(R.color.yellow_bg))
                                    .setPriority(Notification.PRIORITY_HIGH)
                                    .setVisibility(Notification.VISIBILITY_PUBLIC);
                        }
                        // Locate and set the Image into customnotificationtext.xml ImageViews
                        remoteViews.setImageViewResource(R.id.imagenotileft, R.drawable.ic_launcher);
                        // Locate and set the Text into customnotificationtext.xml TextViews
                        remoteViews.setTextViewText(R.id.title, notificationTitleText);
                        remoteViews.setTextViewText(R.id.text, displayValue);
                        // Create Notification Manager
                        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        // Build Notification with Notification Manager
                        Notification notif = builder.build();
                        notif.bigContentView = remoteViews;
                        notificationmanager.notify(23, notif);
                    }
                }
                reLoad();
            } else {
                reLoad();
            }
        } catch (JSONException e) {
            reLoad();
            e.printStackTrace();
        } catch (Exception e) {
            reLoad();
            e.printStackTrace();
        }
    }
   */
/* public class task extends AsyncTask<JSONObject, String, String> {
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected String doInBackground(JSONObject... params) {
            Log.d("yourTag", "long running service task");
            Log.d(TAG, "Service Data Received");
            JSONObject response = params[0];
            try {
                if (response.getString("code").equalsIgnoreCase("1")) {
                    JSONArray peoplesNamelist = response.getJSONArray("values");
                    ArrayList<HashMap<String, String>> chatHistory = new ArrayList<>();
                    if (peoplesNamelist.length() > 0) {
                        for (int i = 0; i < peoplesNamelist.length(); i++) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("empid", uid);
                            hashMap.put("docid", "0");
                            JSONArray memberarray = peoplesNamelist.getJSONObject(i).getJSONArray("memberDetails");
                            if (memberarray.length() > 0) {
                                for (int j = 0; j < memberarray.length(); j++) {
                                    if (memberarray.getJSONObject(j).getString("empCode").equalsIgnoreCase(peoplesNamelist.getJSONObject(i).getString("to_userid")))
                                        hashMap.put("docid", memberarray.getJSONObject(j).getString("empPhoto"));
                                }
                            }
                            hashMap.put("docid", "125");
                            hashMap.put("name", peoplesNamelist.getJSONObject(i).getString("room_name"));
                            hashMap.put("roomId", peoplesNamelist.getJSONObject(i).getString("room_id"));
                            hashMap.put("message", peoplesNamelist.getJSONObject(i).getString("message"));
                            hashMap.put("empcode", peoplesNamelist.getJSONObject(i).getString("to_userid"));
                            hashMap.put("memberDetails", memberarray.toString());
                            hashMap.put("chatType", peoplesNamelist.getJSONObject(i).getString("chat_type"));
                            hashMap.put("msgUnReadCount", peoplesNamelist.getJSONObject(i).getString("msgUnReadCount"));
                            chatHistory.add(hashMap);
                        }
                        Sharedpref.writegson(context, chatHistory, uid + "_ChatHistory");
                        if (peoples_arraylist != null) {
                            peoples_arraylist = chatHistory;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }
        @Override
        protected void onPostExecute(String updateDate) {
            reLoad();
        }
    }*//*
    public void reLoad() {
        int TIME_OUT = 4000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    session = new SessionManager(getApplicationContext());
                    uid = session.getUserDetails().get(SessionManager.ID);
                    String req = "{'submode':'EMP_CHAT_DETAILS','Cre_Id':'" + cr_id + "','UID':'" + uid + "'}";
                    String flag = "ChatHistory";
                    if (uid != null) {
                        onLoad(req, flag);
                        if (Sharedpref.GetPrefString(context, "chatServiceIP") != null)
                            if (!Sharedpref.GetPrefString(context, "chatServiceIP").equalsIgnoreCase(""))
                                chatURL = Sharedpref.GetPrefString(context, "chatServiceIP");
                    }
                } catch (Exception e) {
                }
            }
        }, TIME_OUT);
    }
    */
/*public static class NotifyServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            context.unregisterReceiver(this);
            doServiceStuff();
            Log.d("NotifyServiceReceiver", "Loading");
        }
    }*//*
}*/
