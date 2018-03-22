package com.guruinfo.scm.Chat;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.github.siyamed.shapeimageview.mask.PorterCircularImageView;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.RestClientHelper;
import com.guruinfo.scm.common.utils.Sharedpref;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
/**
 * Created by ERP on 12/5/2017.
 */
public class Chatwindow extends BaseActivity {
    String uID, credentialID;
    SessionManager session;
    public static String indivRoomId;
    BackgroundTask backgroundTask;
    ImageView chat_options, back, deletechat, isActiveView;
    LinearLayout deleteLay, searchLay;
    TextView action_name;
    EditText chat_message;
    Button send, adduser;
    ProgressDialog mDialog;
    ArrayList<HashMap<String, String>> chatarraylist = new ArrayList<>();
    private ChatAdapter listAdapter;
    String to_userid, to_username, from_userid, from_username, room_id;
    StickyListHeadersListView chat_listview;
    Handler mHandler;
    int arraylist_size = 0;
    int loader = R.drawable.empty_dp_large;
    HashMap<String, String> cStatusMap = new HashMap<>();
    String thisDate;
    String clickCStatus = "1";
    String clickMessage = "Testing";
    String clickTime = "19:25";
    MediaPlayer mp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_window);
        chat_listview = (StickyListHeadersListView) findViewById(R.id.chat_listview);
        // chat_listview.setOnScrollListener(new ListViewOnScrollListener());
        action_name = (TextView) findViewById(R.id.action_name);
        mDialog = new ProgressDialog(context);
        chat_message = (EditText) findViewById(R.id.chat_message);
        deletechat = (ImageView) findViewById(R.id.deletechat);
        isActiveView = (ImageView) findViewById(R.id.active);
        deleteLay = (LinearLayout) findViewById(R.id.delete_lay);
        searchLay = (LinearLayout) findViewById(R.id.search_lay);
        session = new SessionManager(this);
        uID = session.getUserDetails().get(SessionManager.ID);
        credentialID = session.getUserDetails().get(SessionManager.CR_ID);
        send = (Button) findViewById(R.id.send);
        adduser = (Button) findViewById(R.id.adduser);
        chat_options = (ImageView) findViewById(R.id.chat_options);
        chat_options.setImageDrawable(getResources().getDrawable(R.drawable.option));
        to_userid = getIntent().getStringExtra("to_userid");
        to_username = getIntent().getStringExtra("to_username");
        from_userid = getIntent().getStringExtra("from_userid");
        from_username = getIntent().getStringExtra("from_username");
        room_id = getIntent().getStringExtra("roomId");
        chat_options.setVisibility(View.GONE);
        deleteLay.setVisibility(View.VISIBLE);
        indivRoomId = room_id;
        action_name.setText(to_username);
        isActiveView.setVisibility(View.GONE);
        if (Sharedpref.getPrefBoolean(context, "isChatClear"))
            deleteLay.setVisibility(View.VISIBLE);
        else
            deleteLay.setVisibility(View.INVISIBLE);
        try {
            String empNameList = Sharedpref.GetPrefString(context, "People");
            JSONArray empNameArray = new JSONArray(empNameList);
            for (int i = 0; i < empNameArray.length(); i++) {
                if (empNameArray.getJSONObject(i).getString("isActive").equalsIgnoreCase("true") && to_userid.equalsIgnoreCase(empNameArray.getJSONObject(i).getString("empcode")))
                    isActiveView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        Date todayDate = new Date();
        thisDate = currentDate.format(todayDate);
        listAdapter = new ChatAdapter(context, R.layout.chat_right);
        back = (ImageView) findViewById(R.id.chat_actionbar).findViewById(R.id.chat_back);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send.startAnimation(AnimationUtils.loadAnimation(Chatwindow.this, R.anim.image_click));
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                if (chat_message.getText().toString().trim().equalsIgnoreCase("")) {
                    setToast("Please Enter message");
                } else if (room_id.equalsIgnoreCase("")) {
                    setToast("Please Wait...");
                } else {
                    String encodedURL = chat_message.getText().toString().trim();
                    try {
                        encodedURL = URLEncoder.encode(chat_message.getText().toString().trim(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String sendparamsss = "{'action':'sendchat','chat_type':'individual','UID':'" + uID + "','Cre_Id':'" + credentialID + "','from_userid':'" + uID + "','to_userid':'" + room_id + "','room_id':'" + room_id + "','message':'" + encodedURL + "'}";
                    System.out.println("sendparamsss:" + sendparamsss);
                    getLoad(sendparamsss, "SEND", chatarraylist.size());
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("cstatus", "");
                    hashMap.put("datemonthyear", "");
                    hashMap.put("DateWithTime", thisDate);
                    hashMap.put("date_and_time", currentDateTimeString);
                    hashMap.put("cid", "");
                    hashMap.put("to_username", to_username);
                    hashMap.put("message", chat_message.getText().toString().trim());
                    hashMap.put("sent", "");
                    hashMap.put("from_userid", from_userid);
                    hashMap.put("notify", "");
                    hashMap.put("imageId", "");
                    hashMap.put("room_name", "");
                    hashMap.put("room_id", "");
                    hashMap.put("from_username", from_username);
                    hashMap.put("chat_type", "individual");
                    hashMap.put("isRecSend", "");
                    hashMap.put("status", "pending");
                    chatarraylist.add(hashMap);
                    if (chatarraylist.size() == 1)
                        chat_listview.setAdapter(listAdapter);
                    listAdapter.notifyDataSetChanged();
                    chat_message.setText("");
                    chat_listview.setSelection(listAdapter.getCount() - 1);
                }
            }
        });
        deletechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletechat.startAnimation(AnimationUtils.loadAnimation(Chatwindow.this, R.anim.image_click));
                String sendparamsss = "{'action':'clearchat','chat_type':'individual','UID':'" + uID + "','Cre_Id':'" + credentialID + "','from_userid':'" + uID + "','room_id':'" + room_id + "'}";
                System.out.println("sendparamsss:" + sendparamsss);
                if (room_id.equalsIgnoreCase("")) {
                    setToast("Please Wait...");
                } else {
                    com.guruinfo.scm.Chat.Easydialog.Confirm.using(Chatwindow.this).ask("Are you sure want to clear the chat history?")
                            .onPositive("Yes", new com.guruinfo.scm.Chat.Easydialog.Dialog.OnClickListener() {
                                @Override
                                public void onClick(com.guruinfo.scm.Chat.Easydialog.Dialog dialog, int which) {
                                    dialog.dismiss();
                                    String sendparamsss = "{'action':'clearchat','chat_type':'individual','UID':'" + uID + "','Cre_Id':'" + credentialID + "','from_userid':'" + uID + "','room_id':'" + room_id + "'}";
                                    System.out.println("sendparamsss:" + sendparamsss);
                                    getLoad(sendparamsss, "Clear", 0);
                                }
                            }).onNegative("No", new com.guruinfo.scm.Chat.Easydialog.Dialog.OnClickListener() {
                        @Override
                        public void onClick(com.guruinfo.scm.Chat.Easydialog.Dialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).build().show();
                }
            }
        });
        adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adduser.startAnimation(AnimationUtils.loadAnimation(Chatwindow.this, R.anim.image_click));
            }
        });
        String requestParameters = "{'action':'indivchat','chat_type':'individual','Cre_Id':'" + credentialID + "','to_userid':'" + to_userid + "','to_username':'" + to_username + "','from_userid':'" + from_userid + "','from_username':'" + from_username + "','UID':'" + uID + "','mobType':'true'}";
        System.out.println("requestParameters:" + requestParameters);
        getLoad(requestParameters, "IMCHAT", 0);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishChat();
            }
        });
        chat_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  if (dialogoption == 0) {
                    paymentDialog.show();
                    dialogoption = 1;
                } else {`
                    paymentDialog.dismiss();
                    dialogoption = 0;
                }*/
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishChat();
    }
    @Override
    public void onRestart() {
        super.onRestart();
        m_Runnable.run();
    }
    public void finishChat() {
        hideSoftInputFromWindow();
        indivRoomId = null;
        if (mHandler != null)
            mHandler.removeCallbacks(m_Runnable);
        if (mp != null)
            mp.release();
        finish();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        indivRoomId = null;
        if (mHandler != null)
            mHandler.removeCallbacks(m_Runnable);
    }
    private final Runnable m_Runnable = new Runnable() {
        public void run() {
            String requestParameters = "{'action':'chatHeartbeat','Cre_Id':'" + credentialID + "','to_userid':'" + uID + "','RoomIds':'" + room_id + "','unreadMessage':'true','from_userid':'" + uID + "'}";
            System.out.println("requestParameters:" + requestParameters);
            getLoad(requestParameters, "UnRead_CHAT", 0);
            Chatwindow.this.mHandler.postDelayed(m_Runnable, 5000);
        }
    };
    public void getLoad(final String req, final String flag, final int pos) {
        final String requestParameter = req;
        Log.d("Individual Chat", requestParameter);
        RestClientHelper.getInstance().getChatURL(requestParameter, context, new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                parseJSONResponse(response, flag, pos);
            }
            @Override
            public void onError(String error) {
                if (flag.equalsIgnoreCase("SEND")) {
                    chatarraylist.get(pos).put("isRecSend", "false");
                    chatarraylist.get(pos).put("status", "pending");
                    listAdapter.notifyDataSetChanged();
                } else if (flag.equalsIgnoreCase("IMCHAT")) {
                    getLoad(requestParameter, flag, pos);
                } else if (!(flag.equalsIgnoreCase("UnRead_CHAT"))) {
                    showInternetDialog(Chatwindow.this, error, requestParameter, flag, pos);
                }
            }
        });
    }
    private void hideSoftInputFromWindow() {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (this.getWindow().getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            this.getCurrentFocus().clearFocus();
        }
    }
    public class ChatAdapter extends ArrayAdapter<String> implements StickyListHeadersAdapter {
        private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        private SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm a");
        private SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
        private SimpleDateFormat headerdateformat = new SimpleDateFormat("dd MMMM yyyy");
        private Date date = null;
        private String msgDate = "";
        private Date headerDate = null;
        private String msgHeader = "";
        private String msg = "";
        private String mName = "";
        private int day = 0;
        public ChatAdapter(Context context, int resource) {
            super(context, resource);
        }
        @Override
        public int getCount() {
            return chatarraylist.size();
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.chat_right, null);
                holder = new ViewHolder();
                holder.singleTick = (ImageView) convertView.findViewById(R.id.single_tick);
                holder.doubleTick = (ImageView) convertView.findViewById(R.id.double_tick);
                holder.doubleTickRead = (ImageView) convertView.findViewById(R.id.double_view_tick);
                holder.userImage = (PorterCircularImageView) convertView.findViewById(R.id.user_image);
                holder.chat_left_layout = (LinearLayout) convertView.findViewById(R.id.chat_left_layout);
                holder.chat_right_layout = (LinearLayout) convertView.findViewById(R.id.chat_right_layout);
                holder.chat_right_text = (TextView) convertView.findViewById(R.id.chat_right_text);
                holder.chat_right_time = (TextView) convertView.findViewById(R.id.chat_right_time);
                holder.chat_left_message = (TextView) convertView.findViewById(R.id.chat_left_message);
                holder.chat_left_time = (TextView) convertView.findViewById(R.id.chat_left_time);
                holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_row);
                holder.retry = (ImageView) convertView.findViewById(R.id.retry_row);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Typeface myTypeface = Typeface.createFromAsset(
                    context.getAssets(),
                    "Roboto-Regular.ttf");
            holder.chat_left_time.setTypeface(myTypeface);
            holder.chat_right_time.setTypeface(myTypeface);
            holder.chat_left_message.setTypeface(myTypeface);
            holder.chat_right_text.setTypeface(myTypeface);
            if (chatarraylist.get(position).get("from_userid").equalsIgnoreCase(from_userid)) {
                holder.singleTick.setVisibility(View.GONE);
                holder.doubleTick.setVisibility(View.GONE);
                holder.doubleTickRead.setVisibility(View.GONE);
                holder.chat_left_layout.setVisibility(View.GONE);
                holder.chat_right_layout.setVisibility(View.VISIBLE);
                holder.chat_right_text.setText(chatarraylist.get(position).get("message")+
                        Html.fromHtml( "&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;" +
                                "&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));
                holder.chat_right_time.setText(chatarraylist.get(position).get("date_and_time"));
                Typeface action_name_type = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
                holder.chat_right_text.setTypeface(action_name_type);
                holder.chat_right_time.setTypeface(action_name_type);
                if (chatarraylist.get(position).get("status").equalsIgnoreCase("pending")) {
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.chat_right_time.setVisibility(View.GONE);
                    if (chatarraylist.get(position).get("isRecSend").equalsIgnoreCase("false")) {
                        holder.pb.setVisibility(View.GONE);
                        holder.retry.setVisibility(View.VISIBLE);
                    } else {
                        holder.retry.setVisibility(View.GONE);
                    }
                } else {
                    holder.pb.setVisibility(View.GONE);
                    holder.chat_right_time.setVisibility(View.VISIBLE);
                    if (cStatusMap.containsKey(chatarraylist.get(position).get("cid"))) {
                        if (cStatusMap.get(chatarraylist.get(position).get("cid")).equalsIgnoreCase("1")) {
                            holder.singleTick.setVisibility(View.VISIBLE);
                            holder.doubleTick.setVisibility(View.GONE);
                            holder.doubleTickRead.setVisibility(View.GONE);
                        } else if (cStatusMap.get(chatarraylist.get(position).get("cid")).equalsIgnoreCase("2")) {
                            holder.singleTick.setVisibility(View.GONE);
                            holder.doubleTick.setVisibility(View.VISIBLE);
                            holder.doubleTickRead.setVisibility(View.GONE);
                        } else if (cStatusMap.get(chatarraylist.get(position).get("cid")).equalsIgnoreCase("3")) {
                            holder.singleTick.setVisibility(View.GONE);
                            holder.doubleTick.setVisibility(View.GONE);
                            holder.doubleTickRead.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } else {
                holder.pb.setVisibility(View.GONE);
                holder.retry.setVisibility(View.GONE);
                holder.chat_left_layout.setVisibility(View.VISIBLE);
                holder.chat_right_layout.setVisibility(View.GONE);
                holder.chat_left_message.setText(chatarraylist.get(position).get("message")+
                        Html.fromHtml("&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;" +
                                "&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));
                holder.chat_left_time.setText(chatarraylist.get(position).get("date_and_time"));
                Typeface action_name_type = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
                holder.chat_left_message.setTypeface(action_name_type);
                holder.chat_left_time.setTypeface(action_name_type);
                imageLoag(chatarraylist.get(position).get("imageId"), holder.userImage);
            }
            holder.retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String encodedURL = holder.chat_right_text.getText().toString().trim();
                    try {
                        encodedURL = URLEncoder.encode(chatarraylist.get(position).get("message"), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String requestParameters = "{'action':'sendchat','chat_type':'individual','UID':'" + uID + "','Cre_Id':'" + credentialID + "','from_userid':'" + uID + "','to_userid':'" + room_id + "','room_id':'" + room_id + "','message':'" + encodedURL + "'}";
                    chatarraylist.get(position).put("date_and_time", "");
                    chatarraylist.get(position).put("cid", "");
                    chatarraylist.get(position).put("message", chatarraylist.get(position).get("message"));
                    chatarraylist.get(position).put("isRecSend", "");
                    chatarraylist.get(position).put("status", "pending");
                    listAdapter.notifyDataSetChanged();
                    getLoad(requestParameters, "SEND", position);
                }
            });
           /* chat_right_layout.setOnTouchListener(new View.OnTouchListener() {
                GestureDetector gestureDetector = new GestureDetector(new MyGestureDetector(context, chatarraylist.get(position).get("cid")));
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    clickCStatus = cStatusMap.get(chatarraylist.get(position).get("cid"));
                    clickMessage = chatarraylist.get(position).get("message");
                    clickTime = chatarraylist.get(position).get("date_and_time");
                    return gestureDetector.onTouchEvent(event);
                }
            });*/
            holder.chat_right_layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!chatarraylist.get(position).get("cid").equalsIgnoreCase("")) {
                        clickCStatus = cStatusMap.get(chatarraylist.get(position).get("cid"));
                        clickMessage = chatarraylist.get(position).get("message");
                        clickTime = chatarraylist.get(position).get("date_and_time");
                        String req = "{'Action':'MOBILE_CHAT_PROCESS','submode':'MESSAGE_DETAILS','Cre_Id':'" + credentialID + "','UID':'" + uID + "','chat_id':'" + chatarraylist.get(position).get("cid") + "'}";
                        onMessageLoad(req, "MESSAGE_DETAILS");
                    }
                    return false;
                }
            });
            /*chat_right_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!chatarraylist.get(position).get("cid").equalsIgnoreCase("")) {
                        clickCStatus = cStatusMap.get(chatarraylist.get(position).get("cid"));
                        clickMessage = chatarraylist.get(position).get("message");
                        clickTime = chatarraylist.get(position).get("date_and_time");
                        String req = "{'Action':'MOBILE_CHAT_PROCESS','submode':'MESSAGE_DETAILS','Cre_Id':'" + credentialID + "','UID':'" + uID + "','chat_id':'" + chatarraylist.get(position).get("cid") + "'}";
                        onMessageLoad(req, "MESSAGE_DETAILS");
                    }
                }
            });*/
            return convertView;
        }
        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            final HeaderViewHolder holder;
            LayoutInflater inflater = LayoutInflater.from(context);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.chat_header_row, parent, false);
                holder = new HeaderViewHolder();
                holder.headerDate = (TextView) convertView.findViewById(R.id.header_date);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }
            try {
                if (chatarraylist.get(position).get("DateWithTime") != null && !chatarraylist.get(position).get("DateWithTime").equalsIgnoreCase("")) {
                    headerDate = format1.parse(chatarraylist.get(position).get("DateWithTime"));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (headerDate != null) {
                msgHeader = headerdateformat.format(headerDate);
            }
            if (show(msgHeader) == 0) {
                holder.headerDate.setText("TODAY");
            } else if (show(msgHeader) == -1) {
                holder.headerDate.setText("YESTERDAY");
            } else {
                holder.headerDate.setText(msgHeader);
            }
            return convertView;
        }
        private class HeaderViewHolder {
            private TextView headerDate;
        }
        @Override
        public long getHeaderId(int position) {
            int mDatePos = 0;
            String mDate = chatarraylist.get(position).get("DateWithTime");
            DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = inputFormat.parse(mDate);
                mDatePos = date.getDate();
            } catch (ParseException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            return mDatePos;
        }
        private class ViewHolder {
            TextView chat_right_text, chat_right_time, chat_left_message, chat_left_time;
            LinearLayout chat_left_layout, chat_right_layout;
            ProgressBar pb;
            PorterCircularImageView userImage;
            ImageView retry, singleTick, doubleTick, doubleTickRead;
        }
        private int show(String time) {
            try {
                String outputPattern = "dd MMMM yyyy";
                SimpleDateFormat format = new SimpleDateFormat(outputPattern);
                Date Date1 = format.parse(getdate());
                Date Date2 = format.parse(time);
                long mills = Date2.getTime() - Date1.getTime();
                long Day1 = mills / (1000 * 60 * 60);
                day = (int) Day1 / 24;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return day;
        }
        private String getdate() {
            String time = "";
            try {
                String outputPattern = "dd MMMM yyyy";
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat(outputPattern);
                time = df.format(c.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return time;
        }
    }
    private void loadProgressBar() {
        mDialog.show();
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setContentView(R.layout.loader);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                ((Activity) context).finish();
            }
        });
    }
    public void onMessageLoad(final String req, final String flag) {
        loadProgressBar();
        final String requestParameter = req;
        RestClientHelper.getInstance().getURL(requestParameter, context, new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    mDialog.dismiss();
                    System.out.println(flag + " --> " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    parseJSONResponse1(jsonObject, flag);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(String error) {
                mDialog.dismiss();
                chatShowInternetDialog(context, error, req, flag);
            }
        });
    }
    private void parseJSONResponse1(JSONObject responseJSONObject, String flag) {
        try {
            if (responseJSONObject.getString("code").equalsIgnoreCase("1")) {
                if (flag.equalsIgnoreCase("MESSAGE_DETAILS")) {
                    hideSoftInputFromWindow();
                    indivRoomId = null;
                    if (mHandler != null)
                        mHandler.removeCallbacks(m_Runnable);
                    if (mp != null)
                        mp.release();
                    Bundle bundle = new Bundle();
                    bundle.putString("response", responseJSONObject.toString());
                    bundle.putString("msg", clickMessage);
                    bundle.putString("cStatus", clickCStatus);
                    bundle.putString("time", clickTime);
                    traversToNextActivity(context, IndivChatRowInfo.class, bundle);
                }
            } else if (responseJSONObject.getString("code").equalsIgnoreCase("403")) {
                showSessionScreensDialog(this, responseJSONObject.getString("msg"));
            } else {
                setToast(responseJSONObject.getString("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void chatShowInternetDialog(Context activity, String err, final String requestParameterValues, final String flag) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setMessage(err);
        builder1.setCancelable(true);
        builder1.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder1.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onMessageLoad(requestParameterValues, flag);
            }
        });
        try {
            AlertDialog alert11 = builder1.create();
            alert11.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void imageLoag(String imageId, PorterCircularImageView image) {
        if (imageId != null) {
       /* if (!imageId.equals("")) {
            Picasso.with(context)
                    .load(loader)
                    .into(image);
        } else */
            if (imageId.equals("0") || imageId.equals("")) {
                Picasso.with(context).load(loader).into(image);
            } else {
                Picasso.with(context)
                        .load(AppContants.imageurl + imageId)
                        .into(image);
            }
        }
    }
    public void showInternetDialog(Context activity, String err_msg, final String requestParameterValues, final String flag, final int pos) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setMessage(err_msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder1.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getLoad(requestParameterValues, flag, pos);
            }
        });
        try {
            AlertDialog alert11 = builder1.create();
            alert11.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void parseJSONResponse(String response, String flag, int pos) {
        try {
            JSONArray responseArray = new JSONArray(response);
            if (flag.equals("IMCHAT")) {
                this.mHandler = new Handler();
                m_Runnable.run();
                ArrayList<HashMap<String, String>> tempChatlist = new ArrayList<>();
                if (chatarraylist.size() > 0) {
                    tempChatlist = chatarraylist;
                }
                chatarraylist = new ArrayList<>();
                JSONArray chatValues = responseArray.getJSONObject(0).getJSONArray("acitivitychat");
                room_id = responseArray.getJSONObject(0).getJSONArray("roomdetjson").getJSONObject(0).getString("room_id");
                indivRoomId = room_id;
                if (chatValues.length() > 0) {
                    for (int i = 0; i < chatValues.length(); i++) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("cstatus", chatValues.getJSONObject(i).getString("cstatus"));
                        hashMap.put("datemonthyear", chatValues.getJSONObject(i).getString("datemonthyear"));
                        hashMap.put("DateWithTime", chatValues.getJSONObject(i).getString("DateWithTime"));
                        hashMap.put("date_and_time", chatValues.getJSONObject(i).getString("date_and_time"));
                        hashMap.put("cid", chatValues.getJSONObject(i).getString("cid"));
                        hashMap.put("to_username", chatValues.getJSONObject(i).getString("to_username"));
                        hashMap.put("message", chatValues.getJSONObject(i).getString("message"));
                        hashMap.put("sent", chatValues.getJSONObject(i).getString("sent"));
                        hashMap.put("from_userid", chatValues.getJSONObject(i).getString("from_userid"));
                        hashMap.put("notify", chatValues.getJSONObject(i).getString("notify"));
                        hashMap.put("room_name", chatValues.getJSONObject(i).getString("room_name"));
                        hashMap.put("room_id", chatValues.getJSONObject(i).getString("room_id"));
                        hashMap.put("imageId", chatValues.getJSONObject(i).getString("imageId"));
                        hashMap.put("from_username", chatValues.getJSONObject(i).getString("from_username"));
                        hashMap.put("chat_type", chatValues.getJSONObject(i).getString("chat_type"));
                        hashMap.put("isRecSend", "true");
                        hashMap.put("status", "completed");
                        if (chatValues.getJSONObject(i).getString("from_userid").equalsIgnoreCase(uID))
                            cStatusMap.put(chatValues.getJSONObject(i).getString("cid"), chatValues.getJSONObject(i).getString("cstatus"));
                        chatarraylist.add(hashMap);
                    }
                    for (int i = 0; i < tempChatlist.size(); i++) {
                        chatarraylist.add(tempChatlist.get(i));
                    }
                    room_id = chatarraylist.get(0).get("room_id");
                    listAdapter = new ChatAdapter(context, R.layout.chat_right);
                    chat_listview.setAdapter(listAdapter);
                    System.out.println("arraylist_size:" + arraylist_size);
                    chat_listview.setSelection(listAdapter.getCount() - 1);
                    chat_listview.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                        }
                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            System.out.println("visibleItemCount:" + visibleItemCount);
                            System.out.println("firstVisibleItem:" + firstVisibleItem);
                            System.out.println("totalItemCount:" + totalItemCount);
                        }
                    });
                } else {
                    chat_listview.setVisibility(View.VISIBLE);
                }
            } else if (flag.equalsIgnoreCase("Clear")) {
                if (responseArray.length() > 0) {
                    JSONObject statusObj = responseArray.getJSONObject(0);
                    if (statusObj.getString("status").equalsIgnoreCase("true")) {
                        chatarraylist.clear();
                        listAdapter.notifyDataSetChanged();
                    }
                }
            } else if (flag.equals("SEND")) {
                cStatusMap.put(responseArray.getJSONObject(0).getString("cid"), "1");
                chatarraylist.get(pos).put("date_and_time", responseArray.getJSONObject(0).getString("date_and_time"));
                chatarraylist.get(pos).put("DateWithTime", responseArray.getJSONObject(0).getString("DateWithTime"));
                chatarraylist.get(pos).put("cid", responseArray.getJSONObject(0).getString("cid"));
                chatarraylist.get(pos).put("message", responseArray.getJSONObject(0).getString("message"));
                chatarraylist.get(pos).put("isRecSend", "true");
                chatarraylist.get(pos).put("status", "completed");
                listAdapter.notifyDataSetChanged();
            } else if (flag.equalsIgnoreCase("UnRead_CHAT")) {
                JSONArray chatValues = responseArray.getJSONObject(0).getJSONArray("acitivitychat");
                JSONArray cStatusArray = responseArray.getJSONObject(0).getJSONArray("chatStatus");
                if (chatValues.length() > 0) {
                    for (int i = 0; i < chatValues.length(); i++) {
                        Log.d("unRead", "success");
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("cstatus", chatValues.getJSONObject(i).getString("cstatus"));
                        hashMap.put("datemonthyear", chatValues.getJSONObject(i).getString("datemonthyear"));
                        hashMap.put("DateWithTime", chatValues.getJSONObject(i).getString("DateWithTime"));
                        hashMap.put("date_and_time", chatValues.getJSONObject(i).getString("date_and_time"));
                        hashMap.put("cid", chatValues.getJSONObject(i).getString("cid"));
                        hashMap.put("to_username", chatValues.getJSONObject(i).getString("to_username"));
                        hashMap.put("message", chatValues.getJSONObject(i).getString("message"));
                        hashMap.put("sent", chatValues.getJSONObject(i).getString("sent"));
                        hashMap.put("from_userid", chatValues.getJSONObject(i).getString("from_userid"));
                        hashMap.put("notify", chatValues.getJSONObject(i).getString("notify"));
                        hashMap.put("room_name", chatValues.getJSONObject(i).getString("room_name"));
                        hashMap.put("room_id", chatValues.getJSONObject(i).getString("room_id"));
                        hashMap.put("imageId", chatValues.getJSONObject(i).getString("imageId"));
                        hashMap.put("from_username", chatValues.getJSONObject(i).getString("from_username"));
                        hashMap.put("chat_type", chatValues.getJSONObject(i).getString("chat_type"));
                        hashMap.put("isRecSend", "true");
                        hashMap.put("status", "completed");
                        chatarraylist.add(hashMap);
                    }
                }
                if (cStatusArray.length() > 0) {
                    for (int i = 0; i < cStatusArray.length(); i++) {
                        cStatusMap.put(cStatusArray.getJSONObject(i).getString("cid"), cStatusArray.getJSONObject(i).getString("cstatus"));
                    }
                }
                listAdapter.notifyDataSetChanged();
                if (chatValues.length() > 0) {
                    chat_listview.setSelection(listAdapter.getCount() - 1);
                    if (mp != null)
                        mp.release();
                    mp.create(this, R.raw.ring1).start();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
