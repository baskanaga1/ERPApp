package com.guruinfo.scm.Chat;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.github.clans.fab.FloatingActionButton;
import com.github.siyamed.shapeimageview.mask.PorterCircularImageView;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.HttpRequest;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.service.RestClientHelper;
import com.guruinfo.scm.common.utils.Sharedpref;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by Baskaran K on 12/26/2017.
 */
public class GroupEditAccess extends BaseActivity {
    String uID, credentialID;
    SessionManager session;
    ImageView back, chat_options;
    TextView action_name, exit_group;
    FloatingActionButton fab;
    BackgroundTask backgroundTask;
    Context context;
    public static String groupRoomId;
    ArrayList<HashMap<String, String>> tempPeoplesList = new ArrayList<>();
    ArrayList<HashMap<String, String>> valuearraylist = new ArrayList<>();
    private GroupEditAdapter listAdapter;
    String to_userid, to_username, from_userid, from_username, room_id;
    ListView chat_edit_listview;
    LinearLayout exit_lay;
    Handler mHandler;
    String memberDetails = "[]";
    Boolean rightAccess = false;
    String superAdmin = "";
    private PopupWindow mPopupWindow;
    int loader = R.drawable.empty_dp_large;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_edit);
        chat_edit_listview = (ListView) findViewById(R.id.chat_edit_listview);
        action_name = (TextView) findViewById(R.id.action_name);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        chat_options = (ImageView) findViewById(R.id.chat_options);
        chat_options.setVisibility(View.GONE);
        context = this;
        exit_lay = (LinearLayout) findViewById(R.id.exit_lay);
        session = new SessionManager(this);
        uID = session.getUserDetails().get(SessionManager.ID);
        credentialID = session.getUserDetails().get(SessionManager.CR_ID);
        to_userid = getIntent().getStringExtra("to_userid");
        to_username = getIntent().getStringExtra("to_username");
        from_userid = getIntent().getStringExtra("from_userid");
        from_username = getIntent().getStringExtra("from_username");
        room_id = getIntent().getStringExtra("roomId");
        groupRoomId = room_id;
        memberDetails = getIntent().getStringExtra("memberDetails");
        action_name.setText(to_username);
        String URL_FEED = HttpRequest.staticURL + "/?AId=EX_SERVICES&Action=MOBILE_CHAT_PROCESS&submode=GROUP_DETAILS&GroupId=" + to_userid + "&Cre_Id=" + credentialID + "&UID=" + uID + "";
        onListLoad(URL_FEED);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("TEMP: "+tempPeoplesList);
                groupDone();
            }
        });
        exit_group = (TextView) findViewById(R.id.exit_group);
        exit_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit_group.startAnimation(AnimationUtils.loadAnimation(GroupEditAccess.this, R.anim.image_click));
                com.guruinfo.scm.Chat.Easydialog.Confirm.using(GroupEditAccess.this).ask("EXIT")
                        .onPositive("Yes", new com.guruinfo.scm.Chat.Easydialog.Dialog.OnClickListener() {
                            @Override
                            public void onClick(com.guruinfo.scm.Chat.Easydialog.Dialog dialog, int which) {
                                dialog.dismiss();
                                exitDone();
                            }
                        }).onNegative("No", new com.guruinfo.scm.Chat.Easydialog.Dialog.OnClickListener() {
                    @Override
                    public void onClick(com.guruinfo.scm.Chat.Easydialog.Dialog dialog, int which) {
                        dialog.dismiss();
                    }
                }).build().show();
            }
        });
        back = (ImageView) findViewById(R.id.chat_actionbar).findViewById(R.id.chat_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishChat();
            }
        });
    }
    private void exitDone() {
        try {
            JSONObject extObj = new JSONObject();
            extObj.put("Exit_group", "allow_exit");
            extObj.put("UID", uID);
            extObj.put("GroupId", to_userid);
            extObj.put("submode", "GROUP_ADD_UPDATE");
            extObj.put("Action", "MOBILE_CHAT_PROCESS");
            extObj.put("Cre_Id", credentialID);
            String reqParam1 = extObj.toString();
            getLoad(reqParam1, "GROUPEXIT");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void groupDone() {
        int groupMemberList = 0;
        try {
            JSONObject reqObj = new JSONObject();
            String formName = "group";
            reqObj.put("formName", formName);
            reqObj.put("GroupId", to_userid);
            reqObj.put("submode", "GROUP_ADD_UPDATE");
            reqObj.put("Exit_group", "");
            reqObj.put("group_name", to_username);
            reqObj.put("super_admin", superAdmin);
            reqObj.put(formName + "_doc_groupicon", "0");
            reqObj.put("Action", "MOBILE_CHAT_PROCESS");
            reqObj.put("Cre_Id", credentialID);
            reqObj.put("UID", uID);
            String inputValue = "";
            for (int i = 0; i < tempPeoplesList.size(); i++) {
                groupMemberList++;
                reqObj.put("Grpchildid_" + tempPeoplesList.get(i).get("empCode"), tempPeoplesList.get(i).get("groupChildId"));
                reqObj.put("memername_" + tempPeoplesList.get(i).get("empCode"), tempPeoplesList.get(i).get("empName"));
                reqObj.put("empcode_" + tempPeoplesList.get(i).get("empCode"), tempPeoplesList.get(i).get("empCode"));
                if (tempPeoplesList.get(i).get("adminischecked").equalsIgnoreCase("true"))
                    reqObj.put("isadmin_" + tempPeoplesList.get(i).get("empCode"), "1");
                else if (tempPeoplesList.get(i).get("adminischecked").equalsIgnoreCase("null")) {
                    if (tempPeoplesList.get(i).get("isAdmin").equalsIgnoreCase("true")) {
                        reqObj.put("isadmin_" + tempPeoplesList.get(i).get("empCode"), "1");
                    } else {
                        reqObj.put("isadmin_" + tempPeoplesList.get(i).get("empCode"), "0");
                    }
                } else {
                    reqObj.put("isadmin_" + tempPeoplesList.get(i).get("empCode"), "0");
                }
                if (tempPeoplesList.get(i).get("exitischecked").equalsIgnoreCase("true"))
                    reqObj.put("isexit_" + tempPeoplesList.get(i).get("empCode"), "1");
                else if (tempPeoplesList.get(i).get("exitischecked").equalsIgnoreCase("null")) {
                    if (tempPeoplesList.get(i).get("isAllowExit").equalsIgnoreCase("true")) {
                        reqObj.put("isexit_" + tempPeoplesList.get(i).get("empCode"), "1");
                    } else {
                        reqObj.put("isexit_" + tempPeoplesList.get(i).get("empCode"), "0");
                    }
                } else
                    reqObj.put("isexit_" + tempPeoplesList.get(i).get("empCode"), "0");
                inputValue = inputValue.equalsIgnoreCase("") ? tempPeoplesList.get(i).get("empCode") : inputValue + "," + tempPeoplesList.get(i).get("empCode");
            }
            reqObj.put("inputvalue", inputValue);
            if (groupMemberList != 0) {
                String reqParam = reqObj.toString();
                getLoad(reqParam, "GROUP");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getLoad(String requestParameterValues, final String flag) {
        final String thisflag = flag;
        final String thisparameters = requestParameterValues;
        try {
            backgroundTask = new BackgroundTask(this, thisflag, new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(String values, String flagMsg) {
                    try {
                        if (flagMsg.equals("internet")) {
                            showInternetDialog(GroupEditAccess.this, values, thisparameters, thisflag);
                        } else {
                            try {
                                JSONObject respObj = new JSONObject(values);
                                parseJSONResponse(respObj, flag);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            backgroundTask.execute("", "", thisparameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showInternetDialog(Context activity, String err_msg, final String requestParameterValues, final String flag) {
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
                getLoad(requestParameterValues, flag);
            }
        });
        try {
            AlertDialog alert11 = builder1.create();
            alert11.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void parseJSONResponse(JSONObject responseJSONObject, String flag) {
        try {
            System.out.println("REG: " + responseJSONObject);
            if (responseJSONObject.getString("code").equalsIgnoreCase("1")) {
                if (flag.equals("GROUP")) {
                    setShortToast(responseJSONObject.getString("msg"));
                    finish();
                    Intent intent = new Intent(context, Chat_MainFragment.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
                } else if (flag.equals("GROUPEXIT")) {
                    setShortToast(responseJSONObject.getString("msg"));
                    finish();
                    Intent intent = new Intent(context, Chat_MainFragment.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
                }
            } else if (responseJSONObject.getString("code").equalsIgnoreCase("403")) {
                showSessionScreensDialog(this, responseJSONObject.getString("msg"));
            } else {
                setToast(responseJSONObject.getString("msg"));
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishChat();
    }
    public void finishChat() {
        hideSoftInputFromWindow();
        finish();
    }
    public void onListLoad(final String parameter) {
        RestClientHelper.getInstance().get(parameter, context, new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                listDisplay(response);
            }
            @Override
            public void onError(String error) {
                showInternetDialog1(context, error, parameter, "");
            }
        });
    }
    private void listDisplay(String values1) {
        try {
            JSONObject namObj = new JSONObject(values1);
            if (namObj.getString("code").equalsIgnoreCase("1")) {
                superAdmin = namObj.getString("SuperAdmin");
                JSONArray groupnamlist = namObj.getJSONArray("values");
                for (int i = 0; i < groupnamlist.length(); i++) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("position", "" + i);
                    hashMap.put("empName", groupnamlist.getJSONObject(i).getString("empName"));
                    hashMap.put("groupId", groupnamlist.getJSONObject(i).getString("groupId"));
                    hashMap.put("imageId", groupnamlist.getJSONObject(i).getString("imageId"));
                    hashMap.put("isAllowExit", groupnamlist.getJSONObject(i).getString("isAllowExit"));
                    hashMap.put("isAdmin", groupnamlist.getJSONObject(i).getString("isAdmin"));
                    hashMap.put("groupChildId", groupnamlist.getJSONObject(i).getString("groupChildId"));
                    hashMap.put("empCode", groupnamlist.getJSONObject(i).getString("empCode"));
                    hashMap.put("adminischecked", "null");
                    hashMap.put("exitischecked", "null");
                    valuearraylist.add(hashMap);
                    if ((groupnamlist.getJSONObject(i).getString("empCode")).equalsIgnoreCase(uID)) {
                        if (groupnamlist.getJSONObject(i).getString("isAdmin").equalsIgnoreCase("true")) {
                            rightAccess = true;
                            fab.setVisibility(View.VISIBLE);
                        } else {
                            fab.setVisibility(View.GONE);
                        }
                    } else if (rightAccess != true) {
                        fab.setVisibility(View.GONE);
                    }
                    if (!superAdmin.equals(uID)) {
                        if ((groupnamlist.getJSONObject(i).getString("empCode")).equalsIgnoreCase(uID)) {
                            if (groupnamlist.getJSONObject(i).getString("isAllowExit").equalsIgnoreCase("true")) {
                                exit_lay.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                tempPeoplesList = valuearraylist;
                if (valuearraylist.size() > 0) {
                    listAdapter = new GroupEditAdapter(context, R.layout.group_edit_list_row);
                    chat_edit_listview.setAdapter(listAdapter);
                    listAdapter.notifyDataSetChanged();
                }
            } else if (namObj.getString("code").equalsIgnoreCase("403")) {
                showSessionScreensDialog(this, namObj.getString("msg"));
            } else {
                finish();
                setToast(namObj.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void showInternetDialog1(Context activity, String err, final String requestParameterValues, final String flag) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setMessage(err);
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
                onListLoad(requestParameterValues);
            }
        });
        try {
            AlertDialog alert11 = builder1.create();
            alert11.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void hideSoftInputFromWindow() {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (this.getWindow().getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            this.getCurrentFocus().clearFocus();
        }
    }
    static class Holder {
        TextView user_name, user_id;
        TextView empCode;
        PorterCircularImageView userImage;
        CheckBox btn_ext, btn_admin;
        LinearLayout ad_lay;
    }
    private void addlist(int position, String peoplePos) {
        tempPeoplesList.get(position).put("adminischecked", "true");
        /*valuearraylist.get(Integer.parseInt(peoplePos)).put("adminischecked", "true");
        valuearraylist.get(Integer.parseInt(peoplePos)).put("exitischecked", "true");
        listAdapter.notifyDataSetChanged();*/
    }
    private void removelist(int position, String peoplePos) {
        tempPeoplesList.get(position).put("adminischecked", "false");
    }
    private void addlist1(int position, String peoplePos) {
        tempPeoplesList.get(position).put("exitischecked", "true");
    }
    private void removelist1(int position, String peoplePos) {
        tempPeoplesList.get(position).put("exitischecked", "false");
    }
    public class GroupEditAdapter extends ArrayAdapter<String> {
        public GroupEditAdapter(Context context, int resource) {
            super(context, resource);
        }
        @Override
        public int getCount() {
            return valuearraylist.size();
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            Holder holder = null;
            //final TextView user_name;
            //ImageView btn_ext, btn_admin;
            //PorterCircularImageView userImage;
            //CheckBox btn_ext, btn_admin;
            if (v == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = mInflater.inflate(R.layout.group_edit_list_row, null);
                holder = new Holder();
                holder.userImage = (PorterCircularImageView) v.findViewById(R.id.user_image);
                holder.user_name = (TextView) v.findViewById(R.id.user_name);
                holder.user_id = (TextView) v.findViewById(R.id.user_id);
                holder.btn_admin = (CheckBox) v.findViewById(R.id.btn_admin);
                holder.btn_ext = (CheckBox) v.findViewById(R.id.btn_ext);
                holder.ad_lay = (LinearLayout) v.findViewById(R.id.ad_lay);
                v.setTag(holder);
            } else {
                holder = (Holder) v.getTag();
            }
            if (valuearraylist.get(position).get("empCode").equalsIgnoreCase(superAdmin))
                v.setBackgroundColor(getContext().getResources().getColor(R.color.selected_color));
            else
                v.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            holder.user_name.setText(valuearraylist.get(position).get("empName"));
            holder.user_id.setText(valuearraylist.get(position).get("empCode"));
            imageLoad(valuearraylist.get(position).get("imageId"), holder.userImage);
            if ((valuearraylist.get(position).get("isAdmin").equalsIgnoreCase("true")) && (valuearraylist.get(position).get("isAllowExit").equalsIgnoreCase("true"))) {
                holder.btn_admin.setChecked(true);
                holder.btn_ext.setChecked(true);
            } else if ((valuearraylist.get(position).get("isAdmin").equalsIgnoreCase("true")) && (valuearraylist.get(position).get("isAllowExit").equalsIgnoreCase("false"))) {
                holder.btn_admin.setChecked(true);
                holder.btn_ext.setChecked(false);
            } else if ((valuearraylist.get(position).get("isAdmin").equalsIgnoreCase("false")) && (valuearraylist.get(position).get("isAllowExit").equalsIgnoreCase("true"))) {
                holder.btn_admin.setChecked(false);
                holder.btn_ext.setChecked(true);
            } else {
                holder.btn_admin.setChecked(false);
                holder.btn_ext.setChecked(false);
            }
            if ((valuearraylist.get(position).get("empCode")).equalsIgnoreCase(uID)) {
                if (valuearraylist.get(position).get("isAdmin").equalsIgnoreCase("false")) {
                    holder.ad_lay.setVisibility(View.GONE);
                }
            } else if (rightAccess == false) {
                holder.ad_lay.setVisibility(View.GONE);
            }
            if (valuearraylist.get(position).get("empCode").equalsIgnoreCase(superAdmin)) {
                holder.btn_admin.setClickable(false);
                holder.btn_ext.setClickable(false);
            } else {
                if (rightAccess == true) {
                    holder.btn_admin.setClickable(true);
                    holder.btn_ext.setClickable(true);
                } else {
                    holder.ad_lay.setVisibility(View.GONE);
                    //holder.btn_admin.setClickable(false);
                    //holder.btn_ext.setClickable(false);
                }
            }
            holder.btn_admin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        addlist(position, tempPeoplesList.get(position).get("position"));
                    } else {
                        removelist(position, tempPeoplesList.get(position).get("position"));
                    }
                }
            });
            holder.btn_ext.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        addlist1(position, tempPeoplesList.get(position).get("position"));
                    } else {
                        removelist1(position, tempPeoplesList.get(position).get("position"));
                    }
                }
            });
            Typeface myTypeface = Typeface.createFromAsset(
                    context.getAssets(),
                    "Roboto-Regular.ttf");
            return v;
        }
    }
    public void imageLoad(String imageId, PorterCircularImageView image) {
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
}
