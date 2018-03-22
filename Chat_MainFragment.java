package com.guruinfo.scm.Chat;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.guruinfo.scm.R;
import com.guruinfo.scm.SCMDashboardActivityLatest;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.service.RestClientHelper;
import com.guruinfo.scm.common.utils.Sharedpref;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import static com.guruinfo.scm.Chat.Chat_list.isScreenOpen;
import static com.guruinfo.scm.Chat.Chat_list.listRefresh;
import static com.guruinfo.scm.Chat.Chat_list.peoples_arraylist;
import static com.guruinfo.scm.common.service.HttpRequest.chatURL;
/**
 * Created by ERP on 12/5/2017.
 */
public class Chat_MainFragment extends BaseActivity implements OnTaskCompleted {
    private static final String TAG = "LeadDashBoardActivity";
    SessionManager session;
    String name, cr_id, uid;
    BackgroundTask bt;
    String parameter;
    static final int REFER_APP = 1;
    LinearLayout chatFeedsTabLayout, groupsTabLayout, peoplesChatTabLayout;
    View chatFeedsTabSelector, groupsTabSelector, peoplesTabSelector;
    String licenceKey;
    String userName;
    String userEmailID;
    LinearLayout userInfoMenuLayout;
    TextView userNameTextView, userMailIdTextView, chat_text, group_text, people_text;
    ImageView back;
    public ImageView chat_options;
    // private ChatService myService;
    private Intent serviceIntent;
    String current_Fragment = "";
    Boolean isPeopleTab = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_tab_dashboard);
        chatFeedsTabLayout = (LinearLayout) findViewById(R.id.lead_tab_layout).findViewById(R.id.chat_tab_layout);
        groupsTabLayout = (LinearLayout) findViewById(R.id.lead_tab_layout).findViewById(R.id.groups_tab_layout);
        peoplesChatTabLayout = (LinearLayout) findViewById(R.id.lead_tab_layout).findViewById(R.id.peoples_tab_layout);
        chatFeedsTabSelector = findViewById(R.id.lead_tab_layout).findViewById(R.id.chat_tab_selector);
        groupsTabSelector = findViewById(R.id.lead_tab_layout).findViewById(R.id.groups_tab_selector);
        peoplesTabSelector = findViewById(R.id.lead_tab_layout).findViewById(R.id.peoples_tab_selector);
        back = (ImageView) findViewById(R.id.chat_actionbar).findViewById(R.id.chat_back);
        chat_options = (ImageView) findViewById(R.id.chat_actionbar).findViewById(R.id.chat_options);
        chat_text = (TextView) findViewById(R.id.lead_tab_layout).findViewById(R.id.chat_text);
        group_text = (TextView) findViewById(R.id.lead_tab_layout).findViewById(R.id.group_text);
        people_text = (TextView) findViewById(R.id.lead_tab_layout).findViewById(R.id.people_text);
        Typeface action_name_type = Typeface.createFromAsset(this.getAssets(), "Roboto-Bold.ttf");
        chat_text.setTypeface(action_name_type);
        group_text.setTypeface(action_name_type);
        people_text.setTypeface(action_name_type);
        init();
        hideSoftInputFromWindow();
        chatFeedsTabLayout.setOnClickListener(chatTabLayoutClickListener);
        groupsTabLayout.setOnClickListener(groupsTabLayoutClickListener);
        peoplesChatTabLayout.setOnClickListener(peoplesTabLayoutClickListener);
        replaceFragment(new Chat_list());
        current_Fragment = "ChatList";
        // serviceIntent = new Intent(this, ChatService.class);
        String empResponse = Sharedpref.GetPrefString(context, "People");
        try {
            JSONArray peopleArray = new JSONArray(empResponse);
            if (peopleArray.length() > 0) {
                isPeopleTab = true;
                peoplesChatTabLayout.setVisibility(View.VISIBLE);
            } else {
                peoplesChatTabLayout.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadChatHistory();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isScreenOpen = null;
                finish();
                Intent intent = new Intent(context, SCMDashboardActivityLatest.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
            }
        });
        /*int TIME_OUT = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isLoading == null)
                        myService.doServiceStuff();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, TIME_OUT);*/
    }
    /* private ServiceConnection serviceConnection = new ServiceConnection() {
         @Override
         public void onServiceConnected(ComponentName name, IBinder service) {
             myService = ((ChatService.MyBinder) service).getService();
         }
         @Override
         public void onServiceDisconnected(ComponentName name) {
             myService = null;
             Log.d(TAG, "Service Disconnected");
         }
     };*/
    private void hideSoftInputFromWindow() {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (this.getWindow().getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            this.getCurrentFocus().clearFocus();
        }
    }
    public void loadChatHistory() {
        android.app.NotificationManager notificationManager = (android.app.NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(23);
        String req = "{'submode':'EMP_CHAT_DETAILS','UID':'" + uid + "'}";
        String flag = "ChatHistory";
        onLoad(req, flag);
    }
    @Override
    protected void onRestart() {
        loadChatHistory();
        super.onRestart();
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
    public void reLoad() {
        int TIME_OUT = 1000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    SessionManager session = new SessionManager(context);
                    String uid = session.getUserDetails().get(SessionManager.ID);
                    String req = "{'submode':'EMP_CHAT_DETAILS','UID':'" + uid + "'}";
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
    private void parseJSONResponse(JSONObject responseJSONObject) {
        try {
            if (responseJSONObject.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_1)) {
                String uid = responseJSONObject.getString("empcode");
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
                        hashMap.put("position", "" + i);
                        hashMap.put("msgUnReadCount", peoplesNamelist.getJSONObject(i).getString("msgUnReadCount"));
                        chatHistory.add(hashMap);
                    }
                    Sharedpref.writegson(context, chatHistory, "ChatHistory");
                    peoples_arraylist = chatHistory;
                    if (isScreenOpen != null) {
                        listRefresh();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    View.OnClickListener chatTabLayoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideSoftInputFromWindow();
            isScreenOpen = null;
            Log.d(TAG, "Called chatsTabLayoutClickListener");
            replaceFragment(new Chat_list());
            current_Fragment = "ChatList";
            YoYo.with(Techniques.SlideInLeft)
                    .duration(300)
                    .playOn(chatFeedsTabSelector);
            chatFeedsTabSelector.setVisibility(View.VISIBLE);
            groupsTabSelector.setVisibility(View.INVISIBLE);
            peoplesTabSelector.setVisibility(View.INVISIBLE);
            chat_text.setTextColor(Color.parseColor("#323232"));
            group_text.setTextColor(Color.parseColor("#9a9a9a"));
            people_text.setTextColor(Color.parseColor("#9a9a9a"));
        }
    };
    View.OnClickListener groupsTabLayoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideSoftInputFromWindow();
            isScreenOpen = null;
            Log.d(TAG, "Called groupTabLayoutClickListener");
            replaceFragment(new RoomList());
            current_Fragment = "GroupList";
            YoYo.with(Techniques.SlideInLeft)
                    .duration(300)
                    .playOn(groupsTabSelector);
            chatFeedsTabSelector.setVisibility(View.INVISIBLE);
            groupsTabSelector.setVisibility(View.VISIBLE);
            peoplesTabSelector.setVisibility(View.INVISIBLE);
            group_text.setTextColor(Color.parseColor("#323232"));
            people_text.setTextColor(Color.parseColor("#9a9a9a"));
            chat_text.setTextColor(Color.parseColor("#9a9a9a"));
        }
    };
    View.OnClickListener peoplesTabLayoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideSoftInputFromWindow();
            isScreenOpen = null;
            Log.d(TAG, "Called peopleTabLayoutClickListener");
            replaceFragment(new People_list());
            current_Fragment = "PeoplesList";
            YoYo.with(Techniques.SlideInLeft)
                    .duration(300)
                    .playOn(peoplesTabSelector);
            peoplesTabSelector.setVisibility(View.VISIBLE);
            chatFeedsTabSelector.setVisibility(View.INVISIBLE);
            groupsTabSelector.setVisibility(View.INVISIBLE);
            people_text.setTextColor(Color.parseColor("#323232"));
            group_text.setTextColor(Color.parseColor("#9a9a9a"));
            chat_text.setTextColor(Color.parseColor("#9a9a9a"));
        }
    };
    private void init() {
        // loadSideMenu();
        getFromSession();
        //loadActionBar();
    }
    private void loadActionBar() {
        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.custom_chat_actionbar, null);
        /*sideMenuIcon = (ImageView) v.findViewById(R.id.lead_menu_icon);
        leadActionBarTitle = (TextView) v.findViewById(R.id.lead_action_bar_title);*/
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(v);
        /*  sideMenuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RotateIn)
                        .duration(500)
                        .playOn(sideMenuIcon);
                if (!isdrawerOpen)
                    mDrawerLayout.openDrawer(menuLayout);
                else
                    mDrawerLayout.closeDrawer(menuLayout);
            }
        });
        String notifyCount = null;
        Log.d(TAG, "Notification Count--" + notifyCount);
        if (Integer.parseInt(notifyCount) > 0) {
            //setCount(notifyCount);
        }*/
    }
    private void getFromSession() {
        session = new SessionManager(getApplicationContext());
        uid = session.getUserDetails().get(SessionManager.ID);
        cr_id = session.getUserDetails().get(SessionManager.CR_ID);
        name = session.getUserDetails().get(SessionManager.NAME);
        //   Sharedpref.SetPrefString(context, "customername", uid);
        if (session.getUserDetails().get(SessionManager.FETCHED).equalsIgnoreCase("true")) {
            userName = session.getUserDetails().get(SessionManager.USER_NAME);
            userEmailID = session.getUserDetails().get(SessionManager.USER_EMAIL);
            licenceKey = session.getUserDetails().get(SessionManager.USER_CHATKEY);
            userNameTextView.setText(userName);
            userMailIdTextView.setText(userEmailID);
        } else {
            new getLiveChatConfigurationDetailsAsyncTask().execute();
        }
    }
    public void animateOnValidationError(View field, View button) {
        YoYo.with(Techniques.Bounce)
                .duration(700)
                .playOn(field);
        YoYo.with(Techniques.Shake)
                .duration(700)
                .playOn(button);
    }
    private String hardCodedJsonResponse() {
        String json = "{'TOTAL_PAGES':'3','Messages':[{'is_mob_read':'0','message':'Thank you for payment of Rs. 50000 towards  B210 in ROYAl CASTEL. Your Receipt:  RC3452 dated: 04.09.15 is generated- AMARPRAKASH ','id':'1','title':'Booking Approval','user_type':'CUSTOMER','ref_type':'RECEIPT','web_url':'','message_id':'3485','ref_id':'386','unit_id':'0','user_id':'APC00035','notification_date':'30/09/2015 17:39:21','hint_message':'Booking Approval'},{'is_mob_read':'0','message':'AMARPRAKASH warmly welcomes you to launch of its prestigious project TEMPLE WAVES. We believe your dream home search completes and happy journey begins ','id':'1','title':'Receipts','user_type':'CUSTOMER','ref_type':'GENERAL','web_url':'http://www.amarprakash.in/','message_id':'3486','ref_id':'386','unit_id':'0','user_id':'APC00035','notification_date':'30/03/2015 17:39:21','hint_message':'Receipts'},{'is_mob_read':'1','message':'Thank you for your interest towards PALM RIVIERA, Wish and believe your dream home search completes and happy journey begins here - AMARPRAKASH','id':'1','title':'Payment Request','user_type':'CUSTOMER','ref_type':'GENERAL','web_url':'','message_id':'3487','ref_id':'386','unit_id':'0','user_id':'APC00035','notification_date':'30/09/2014 17:39:21','hint_message':'Payment Request'},{'is_mob_read':'0','message':'Congrats!!! Customer Name, Welcome to AMARPRAKASH family. Your Proposal for Booking in PALM RIVIERA is forwarded for processing.','id':'1','title':'Payment Reminder','user_type':'CUSTOMER','ref_type':'PROJECT','web_url':'','message_id':'3488','ref_id':'386','unit_id':'0','user_id':'APC00035','notification_date':'05/11/2015 17:39:21','hint_message':'Payment Reminder'},{'is_mob_read':'1','message':'A Gentle Call for  payment of Rs. 500000- dated . Kindly make payment within 7 days to avoid delay payment,  Ignore if already paid - AMARPRAKASH','id':'1','title':'Payment Request','user_type':'CUSTOMER','ref_type':'DEMAND','web_url':'','message_id':'3489','ref_id':'386','unit_id':'0','user_id':'APC00035','notification_date':'04/11/2015 17:39:21','hint_message':'Payment Request'},{'is_mob_read':'1','message':'Dear Customer Happy Anniversary. AMARPRAKASH wishes happiness for both of you & may your lives be filled with love and Companionship forever.','id':'1','title':'Birthday Wishes','user_type':'CUSTOMER','ref_type':'Wishes','web_url':'','message_id':'3490','ref_id':'386','unit_id':'0','user_id':'APC00035','notification_date':'30/09/2015 17:39:21','hint_message':'Birthday Wishes'},{'is_mob_read':'0','message':'Interest Waiver','id':'1','title':'Interest Waiver','user_type':'CUSTOMER','ref_type':'EVENT','web_url':'','message_id':'3491','ref_id':'386','unit_id':'0','user_id':'APC00035','notification_date':'30/09/2015 17:39:21','hint_message':'Interest Waiver'},{'is_mob_read':'0','message':'receipt approved','id':'1','title':'Customization Commencement','user_type':'CUSTOMER','ref_type':'NOTICE','web_url':'','message_id':'3492','ref_id':'386','unit_id':'0','user_id':'APC00035','notification_date':'30/09/2015 17:39:21','hint_message':'Customization Commencement'},{'is_mob_read':'1','message':'receipt approved','id':'1','title':'Customization request Approval','user_type':'CUSTOMER','ref_type':'RECEIPT','web_url':'','message_id':'3493','ref_id':'386','unit_id':'0','user_id':'APC00035','notification_date':'30/09/2015 17:39:21','hint_message':'Customization request Approval'},{'is_mob_read':'0','message':'Anniversary Wishes','id':'1','title':'Anniversary Wishes','user_type':'CUSTOMER','ref_type':'RECEIPT','web_url':'','message_id':'3494','ref_id':'386','unit_id':'0','user_id':'APC00035','notification_date':'30/09/2015 17:39:21','hint_message':'Anniversary Wishes'}],'count':2,'code':'1','msg':'success'}";
        return json;
    }
    @Override
    public void onTaskCompleted(String values, String flag) {
        if (flag.equalsIgnoreCase("internet")) {
            showInternetDialog(context, values);
        } else if (flag.equalsIgnoreCase("LOGOUT")) {
            parseLogoutJson(values);
        } else if (flag.equalsIgnoreCase("liveChat")) {
            Log.d(TAG, "LiveChat Response---" + values);
            parseLiveChatJson(values);
        } else if (flag.equalsIgnoreCase("REFER")) {
            parseReferJson(values);
        } else if (flag.equalsIgnoreCase("fetchingNotificationList")) {
          /*  try {
                //parseNotificationJsonData(values);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }
    }
    private void parseReferJson(String values) {
        try {
            try {
                JSONObject response = new JSONObject(values);
                if (response.getString("code").equals("1")) {
                    // setToast(response.getString("msg"));
                    String subject = response.getString("subject");
                    String msgContent = response.getString("msgcontent");
                    removeDialog(REFER_APP);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, msgContent);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                } else if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_403)) {
                    showSessionScreensDialog(context, response.getString(AppContants.RESPONSE_MESSAGE));
                } else {
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void parseLiveChatJson(String values) {
        try {
            JSONObject liveChatObject = new JSONObject(values);
            if (liveChatObject.getString("code").equals("1")) {
                licenceKey = liveChatObject.getString("licenseKey");
                userName = liveChatObject.getString("name");
                userEmailID = liveChatObject.getString("emailId");
                if (!StringUtils.isBlank(userName)) {
                    userInfoMenuLayout.setVisibility(View.VISIBLE);
                    userNameTextView.setText(userName);
                    userMailIdTextView.setText(userEmailID);
                }
            } else if (liveChatObject.getString("code").equals("403")) {
                session.logoutUserAct(this);
                setToast(liveChatObject.getString("msg"));
            } else {
                setToast(liveChatObject.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void replaceFragment(Fragment newFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    /*private void replaceWithChatFragment(String licenceNo, String groupId, String name, String emailId) {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, ChatWindowSupportFragment.newInstance(licenceNo, groupId, name.replaceAll("\\s+", ""), emailId), "chat_fragment")
                    .addToBackStack("chat_fragment")
                    .commit();
        } catch (NullPointerException e) {
            setToast("Something went wrong, Live Chat can not be connected");
        }
    }*/
    private void parseLogoutJson(String values) {
        try {
            try {
                JSONObject imeiObj = new JSONObject(values);
                if (imeiObj.getString("code").equals("1")) {
                    setToast(imeiObj.getString("msg"));
                    session.logoutUserAct(this);
                } else if (imeiObj.getString("code").equals("403")) {
                    setToast(imeiObj.getString("msg"));
                    session.logoutUserAct(this);
                } else {
                    setToast(imeiObj.getString("msg"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder1.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                bt = new BackgroundTask(context);
                bt.execute("", "", parameter);
            }
        });
        try {
            AlertDialog alert11 = builder1.create();
            alert11.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getLiveChatConfigurationDetails() {
   /*     parameter = HttpRequest.portalURL + "Mobile_Devices?Action=HOME&SUBMODE=LIVE_CHAT&UID=" + uid + "&Cre_Id=" + cr_id;
        Log.d(TAG, "Fetching LIVE_CHAT-Parameter : " + parameter);
        bt = new BackgroundTask(this, "liveChat", false);
        bt.execute("", "", parameter);*/
    }
    public class getLiveChatConfigurationDetailsAsyncTask extends AsyncTask<Boolean, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... params) {
            Log.d(TAG, "Entered getLiveChatConfigurationDetailsAsyncTask class doInBackground");
            return null;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.d(TAG, "Entered getLiveChatConfigurationDetailsAsyncTask class onPostExecute");
            getLiveChatConfigurationDetails();
        }
    }
    @Override
    public void onBackPressed() {
        isScreenOpen = null;
        finish();
        Intent intent = new Intent(context, SCMDashboardActivityLatest.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }
    public void showNotificationAlert(final HashMap<String, String> notificationObject) {
        Log.d(TAG, "Entered showNotificationAlert");
    }
}