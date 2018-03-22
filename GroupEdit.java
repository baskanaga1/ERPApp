package com.guruinfo.scm.Chat;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.github.clans.fab.FloatingActionButton;
import com.github.siyamed.shapeimageview.mask.PorterCircularImageView;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.service.RestClientHelper;
import com.guruinfo.scm.common.utils.Sharedpref;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;
/**
 * Created by ERP on 12/14/2017.
 */
public class GroupEdit extends BaseActivity {
    String uID, credentialID;
    SessionManager session;
    BackgroundTask backgroundTask;
    ListView peoples_list;
    ImageView back;
    TextView action_name;
    ListAdapterHolder rechold1;
    PeoplesAdapter people_list_adapter;
    ArrayList<HashMap<String, String>> peoples_arraylist = new ArrayList<>();
    ArrayList<HashMap<String, String>> tempPeoplesList = new ArrayList<>();
    ArrayList<HashMap<String, String>> recycler_arraylist = new ArrayList<>();
    HashMap<String, HashMap<String, String>> userList = new HashMap<>();
    EditText search_value;
    EditText groupName;
    String searchValueName = "";
    int loader = R.drawable.empty_dp_large;
    FloatingActionButton fab;
    RecyclerView recycler_view;
    String superAdmin;
    String groupId;
    String isAllowExit;
    String imageId;
    LinearLayout deleteLay, searchLay;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupadd);
        session = new SessionManager(this);
        uID = session.getUserDetails().get(SessionManager.ID);
        credentialID = session.getUserDetails().get(SessionManager.CR_ID);
        peoples_list = (ListView) findViewById(R.id.peopleslists);
        search_value = (EditText) findViewById(R.id.search_value);
        back = (ImageView) findViewById(R.id.chat_back);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        action_name = (TextView) findViewById(R.id.action_name);
        groupName = (EditText) findViewById(R.id.group_name);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        deleteLay = (LinearLayout) findViewById(R.id.delete_lay);
        searchLay = (LinearLayout) findViewById(R.id.search_lay);
        recycler_view.setHasFixedSize(true);
        deleteLay.setVisibility(View.INVISIBLE);
        searchLay.setVisibility(View.INVISIBLE);
        Typeface action_name_type = Typeface.createFromAsset(this.getAssets(), "Roboto-Regular.ttf");
        search_value.setTypeface(action_name_type);
        rechold1 = new ListAdapterHolder();
        recycler_view.setAdapter(rechold1);
        recycler_view.setHasFixedSize(true);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext(), HORIZONTAL, false);
        recycler_view.setLayoutManager(layoutManager1);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        Bundle bundle = getIntent().getExtras();
        String respString = bundle.getString("response");
        action_name.setText("Edit Group");
        try {
            JSONObject respObj = new JSONObject(respString);
            superAdmin = respObj.getString("SuperAdmin");
            groupName.setText(respObj.getString("groupName"));
            groupName.setSelection(groupName.getText().length());
            imageId = respObj.getString("imageId");
            JSONArray valueList = respObj.getJSONArray("values");
            for (int i = 0; i < valueList.length(); i++) {
                HashMap<String, String> map = new HashMap<>();
                map.put("empNameCode", valueList.getJSONObject(i).getString("empName") + " " + valueList.getJSONObject(i).getString("empCode"));
                map.put("empName", valueList.getJSONObject(i).getString("empName"));
                map.put("groupId", valueList.getJSONObject(i).getString("groupId"));
                map.put("isAllowExit", valueList.getJSONObject(i).getString("isAllowExit"));
                map.put("isAdmin", valueList.getJSONObject(i).getString("isAdmin"));
                map.put("imageId", valueList.getJSONObject(i).getString("imageId"));
                map.put("groupChildId", valueList.getJSONObject(i).getString("groupChildId"));
                map.put("empCode", valueList.getJSONObject(i).getString("empCode"));
                userList.put(valueList.getJSONObject(i).getString("empCode"), map);
                groupId = valueList.getJSONObject(i).getString("groupId");
                if (uID.equalsIgnoreCase(valueList.getJSONObject(i).getString("empCode")))
                    isAllowExit = valueList.getJSONObject(i).getString("isAllowExit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String empResponse = Sharedpref.GetPrefString(context, "People");
        listDisplay(empResponse);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!groupName.getText().toString().trim().equalsIgnoreCase("")) {
                    groupDone();
                } else {
                    groupName.setError("Room Name is Required");
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void groupDone() {
        int groupMemberList = 0;
        try {
            JSONObject reqObj = new JSONObject();
            String formName = "group";
            reqObj.put("formName", formName);
            reqObj.put("GroupId", groupId);
            reqObj.put("submode", "GROUP_ADD_UPDATE");
            reqObj.put("group_name", groupName.getText().toString().trim());
            reqObj.put("super_admin", superAdmin);
            reqObj.put(formName + "_doc_groupicon", imageId);
            reqObj.put("Action", "MOBILE_CHAT_PROCESS");
            reqObj.put("Cre_Id", credentialID);
            reqObj.put("UID", uID);
            String inputValue = "";
            for (int i = 0; i < peoples_arraylist.size(); i++) {
                if (peoples_arraylist.get(i).get("ischecked").equalsIgnoreCase("true")) {
                    groupMemberList++;
                    reqObj.put("Grpchildid_" + peoples_arraylist.get(i).get("empcode"), peoples_arraylist.get(i).get("grpChildId"));
                    reqObj.put("memername_" + peoples_arraylist.get(i).get("empcode"), peoples_arraylist.get(i).get("empname"));
                    reqObj.put("empcode_" + peoples_arraylist.get(i).get("empcode"), peoples_arraylist.get(i).get("empcode"));
                    if (peoples_arraylist.get(i).get("isadmin").equalsIgnoreCase("true"))
                        reqObj.put("isadmin_" + peoples_arraylist.get(i).get("empcode"), "1");
                    else
                        reqObj.put("isadmin_" + peoples_arraylist.get(i).get("empcode"), "0");
                    if (peoples_arraylist.get(i).get("isexit").equalsIgnoreCase("true"))
                        reqObj.put("isexit_" + peoples_arraylist.get(i).get("empcode"), "1");
                    else
                        reqObj.put("isexit_" + peoples_arraylist.get(i).get("empcode"), "0");
                    inputValue = inputValue.equalsIgnoreCase("") ? peoples_arraylist.get(i).get("empcode") : inputValue + "," + peoples_arraylist.get(i).get("empcode");
                }
            }
            /*reqObj.put("Grpchildid_" + uID, peoples_arraylist.get(i).get("grpChildId"));
            reqObj.put("memername_" + uID, peoples_arraylist.get(i).get("empname"));
            reqObj.put("empcode_" + uID, peoples_arraylist.get(i).get("empcode"));
            if (peoples_arraylist.get(i).get("isadmin").equalsIgnoreCase("true"))
                reqObj.put("isadmin_" + uID, "1");
            else
                reqObj.put("isadmin_" + uID, "0");
            if (peoples_arraylist.get(i).get("isexit").equalsIgnoreCase("true"))
                reqObj.put("isexit_" +uID, "1");
            else
                reqObj.put("isexit_" + uID, "0");*/
            reqObj.put("Exit_group", isAllowExit);
            reqObj.put("inputvalue", inputValue);
            if (groupMemberList != 0) {
                String reqParam = reqObj.toString();
                getLoad(reqParam, "GROUP");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onListLoad(final String parameter) {
        System.out.println("request" + parameter);
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
    private void getLoad(String requestParameterValues, final String flag) {
        final String thisflag = flag;
        final String thisparameters = requestParameterValues;
        try {
            backgroundTask = new BackgroundTask(this, thisflag, new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(String values, String flagMsg) {
                    try {
                        if (flagMsg.equals("internet")) {
                            showInternetDialog(GroupEdit.this, values, thisparameters, thisflag);
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
    private void parseJSONResponse(JSONObject responseJSONObject, String flag) {
        try {
            if (responseJSONObject.getString("code").equalsIgnoreCase("1")) {
                if (flag.equals("GROUP")) {
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
                setShortToast(responseJSONObject.getString("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void listDisplay(String values) {
        try {
            JSONArray peoplesNamelist = new JSONArray(values);
            HashMap<String, HashMap<String, String>> tempNameList = new HashMap<>();
            tempNameList.putAll(userList);
            int inc = 0;
            if ((userList.containsKey(superAdmin))) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("position", "" + inc);
                hashMap.put("empNameCode", userList.get(superAdmin).get("empName") + " " + userList.get(superAdmin).get("empCode"));
                hashMap.put("empname", userList.get(superAdmin).get("empName"));
                hashMap.put("empid", userList.get(superAdmin).get("empCode"));
                hashMap.put("docid", userList.get(superAdmin).get("imageId"));
                hashMap.put("name", userList.get(superAdmin).get("empName"));
                hashMap.put("empcode", userList.get(superAdmin).get("empCode"));
                hashMap.put("designation", "");
                hashMap.put("ischecked", "true");
                hashMap.put("grpChildId", userList.get(superAdmin).get("groupChildId"));
                hashMap.put("isadmin", userList.get(superAdmin).get("isAdmin"));
                hashMap.put("isexit", userList.get(superAdmin).get("isAllowExit"));
                inc++;
                recycler_arraylist.add(hashMap);
                peoples_arraylist.add(hashMap);
                userList.remove(superAdmin);
            }
            Iterator myVeryOwnIterator = userList.keySet().iterator();
            while (myVeryOwnIterator.hasNext()) {
                String key = (String) myVeryOwnIterator.next();
                HashMap<String, String> value = (HashMap<String, String>) userList.get(key);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("position", "" + inc);
                hashMap.put("empNameCode", value.get("empName") + " " + value.get("empCode"));
                hashMap.put("empname", value.get("empName"));
                hashMap.put("empid", value.get("empCode"));
                hashMap.put("docid", value.get("imageId"));
                hashMap.put("name", value.get("empName"));
                hashMap.put("empcode", value.get("empCode"));
                hashMap.put("designation", "");
                hashMap.put("ischecked", "true");
                hashMap.put("grpChildId", value.get("groupChildId"));
                hashMap.put("isadmin", value.get("isAdmin"));
                hashMap.put("isexit", value.get("isAllowExit"));
                inc++;
                recycler_arraylist.add(hashMap);
                peoples_arraylist.add(hashMap);
            }
          /*  for (int i = 0; i < peoplesNamelist.length(); i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                if (userList.containsKey(peoplesNamelist.getJSONObject(i).getString("empcode"))) {
                    hashMap.put("position", "" + inc);
                    hashMap.put("empname", peoplesNamelist.getJSONObject(i).getString("name"));
                    hashMap.put("empid", peoplesNamelist.getJSONObject(i).getString("empid"));
                    hashMap.put("docid", peoplesNamelist.getJSONObject(i).getString("docid"));
                    hashMap.put("name", peoplesNamelist.getJSONObject(i).getString("name"));
                    hashMap.put("empcode", peoplesNamelist.getJSONObject(i).getString("empcode"));
                    hashMap.put("designation", peoplesNamelist.getJSONObject(i).getString("designation"));
                    hashMap.put("ischecked", "true");
                    hashMap.put("grpChildId", userList.get(peoplesNamelist.getJSONObject(i).getString("empcode")).get("groupChildId"));
                    hashMap.put("isadmin", userList.get(peoplesNamelist.getJSONObject(i).getString("empcode")).get("isAdmin"));
                    hashMap.put("isexit", userList.get(peoplesNamelist.getJSONObject(i).getString("empcode")).get("isAllowExit"));
                    recycler_arraylist.add(hashMap);
                    userList.remove(peoplesNamelist.getJSONObject(i).getString("empcode"));
                    inc++;
                    peoples_arraylist.add(hashMap);
                }
            }*/
            for (int i = 0; i < peoplesNamelist.length(); i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                if (!(tempNameList.containsKey(peoplesNamelist.getJSONObject(i).getString("empcode")))) {
                    hashMap.put("position", "" + inc);
                    hashMap.put("empNameCode", peoplesNamelist.getJSONObject(i).getString("name") + " " + peoplesNamelist.getJSONObject(i).getString("empcode"));
                    hashMap.put("empname", peoplesNamelist.getJSONObject(i).getString("name"));
                    hashMap.put("empid", peoplesNamelist.getJSONObject(i).getString("empid"));
                    hashMap.put("docid", peoplesNamelist.getJSONObject(i).getString("docid"));
                    hashMap.put("name", peoplesNamelist.getJSONObject(i).getString("name"));
                    hashMap.put("empcode", peoplesNamelist.getJSONObject(i).getString("empcode"));
                    hashMap.put("designation", peoplesNamelist.getJSONObject(i).getString("designation"));
                    hashMap.put("ischecked", "false");
                    hashMap.put("grpChildId", "0");
                    hashMap.put("isadmin", "false");
                    hashMap.put("isexit", "false");
                    inc++;
                    peoples_arraylist.add(hashMap);
                }
            }
            people_list_adapter = new PeoplesAdapter(context, R.layout.group_people_list_add);
            peoples_list.setAdapter(people_list_adapter);
            rechold1.notifyDataSetChanged();
            tempPeoplesList = peoples_arraylist;
        } catch (JSONException e) {
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
    public class PeoplesAdapter extends ArrayAdapter<String> {
        public PeoplesAdapter(Context context, int resource) {
            super(context, resource);
        }
        @Override
        public int getCount() {
            return tempPeoplesList.size();
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            Holder holder = null;
            if (v == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = mInflater.inflate(R.layout.group_people_list_add, null);
                holder = new Holder();
                holder.empName = (TextView) v.findViewById(R.id.groupname);
                holder.empCode = (TextView) v.findViewById(R.id.emp_code);
                holder.user_image = (PorterCircularImageView) v.findViewById(R.id.user_image);
                holder.add_member = (CheckBox) v.findViewById(R.id.add_member);
                v.setTag(holder);
            } else {
                holder = (Holder) v.getTag();
                holder.add_member.setOnCheckedChangeListener(null);
            }
            holder.empName.setText(tempPeoplesList.get(position).get("name"));
            holder.empCode.setText(tempPeoplesList.get(position).get("empcode"));
            String userimage_id = tempPeoplesList.get(position).get("docid");
            if (tempPeoplesList.get(position).get("ischecked").equalsIgnoreCase("false")) {
                holder.add_member.setChecked(false);
            } else {
                holder.add_member.setChecked(true);
            }
            imageLoad(userimage_id, holder.user_image);
            if ((tempPeoplesList.get(position).get("empcode").equalsIgnoreCase(superAdmin)) || (tempPeoplesList.get(position).get("empcode").equalsIgnoreCase(uID))) {
                v.setBackgroundColor(getResources().getColor(R.color.selected_color));
                holder.add_member.setVisibility(View.INVISIBLE);
            } else {
                holder.add_member.setVisibility(View.VISIBLE);
                v.setBackgroundColor(getResources().getColor(R.color.white_bg));
            }
            holder.add_member.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        addlist(position, tempPeoplesList.get(position).get("position"));
                    } else {
                        removelist(position, tempPeoplesList.get(position).get("position"));
                    }
                }
            });
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(tempPeoplesList.get(position).get("empcode").equalsIgnoreCase(superAdmin))) {
                        if (tempPeoplesList.get(position).get("ischecked").equalsIgnoreCase("true"))
                            detailsDisplay(position);
                        else
                            setShortToast("First add to Group");
                    }
                }
            });
            return v;
        }
    }
    public void detailsDisplay(final int pos) {
        final Dialog detailsdialog = new Dialog(this);
        detailsdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = getLayoutInflater();
        detailsdialog.setCanceledOnTouchOutside(true);
        View convertView = inflater.inflate(R.layout.group_add_popup, null);
        detailsdialog.setContentView(convertView);
        LinearLayout ok_button = (LinearLayout) detailsdialog.findViewById(R.id.done_lay);
        LinearLayout cancel_button = (LinearLayout) detailsdialog.findViewById(R.id.cancel_lay);
        LinearLayout admin_button = (LinearLayout) detailsdialog.findViewById(R.id.admin_lay);
        LinearLayout exit_button = (LinearLayout) detailsdialog.findViewById(R.id.exit_lay);
        TextView empName = (TextView) detailsdialog.findViewById(R.id.emp_name);
        TextView empCode = (TextView) detailsdialog.findViewById(R.id.emp_code);
        final CheckBox isAdmin = (CheckBox) detailsdialog.findViewById(R.id.is_admin);
        final CheckBox isExit = (CheckBox) detailsdialog.findViewById(R.id.is_exit);
        empName.setText(tempPeoplesList.get(pos).get("name"));
        empCode.setText(tempPeoplesList.get(pos).get("empcode"));
        /*isAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    tempPeoplesList.get(pos).put("isadmin","false");
                    peoples_arraylist.get(Integer.parseInt(tempPeoplesList.get(pos).get("position"))).put("isadmin","false");
                }else {
                    tempPeoplesList.get(pos).put("isadmin","true");
                    peoples_arraylist.get(Integer.parseInt(tempPeoplesList.get(pos).get("position"))).put("isadmin","true");
                }
            }
        });
        isExit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    tempPeoplesList.get(pos).put("isexit","false");
                    peoples_arraylist.get(Integer.parseInt(tempPeoplesList.get(pos).get("position"))).put("isexit","false");
                }else {
                    tempPeoplesList.get(pos).put("isexit","true");
                    peoples_arraylist.get(Integer.parseInt(tempPeoplesList.get(pos).get("position"))).put("isexit","true");
                }
            }
        });*/
        if (tempPeoplesList.get(pos).get("isadmin").equalsIgnoreCase("true"))
            isAdmin.setChecked(true);
        else
            isAdmin.setChecked(false);
        if (tempPeoplesList.get(pos).get("isexit").equalsIgnoreCase("true"))
            isExit.setChecked(true);
        else
            isExit.setChecked(false);
        detailsdialog.show();
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsdialog.dismiss();
            }
        });
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAdmin.isChecked()) {
                    tempPeoplesList.get(pos).put("isadmin", "false");
                    peoples_arraylist.get(Integer.parseInt(tempPeoplesList.get(pos).get("position"))).put("isadmin", "false");
                } else {
                    tempPeoplesList.get(pos).put("isadmin", "true");
                    peoples_arraylist.get(Integer.parseInt(tempPeoplesList.get(pos).get("position"))).put("isadmin", "true");
                }
                if (!isExit.isChecked()) {
                    tempPeoplesList.get(pos).put("isexit", "false");
                    peoples_arraylist.get(Integer.parseInt(tempPeoplesList.get(pos).get("position"))).put("isexit", "false");
                } else {
                    tempPeoplesList.get(pos).put("isexit", "true");
                    peoples_arraylist.get(Integer.parseInt(tempPeoplesList.get(pos).get("position"))).put("isexit", "true");
                }
                detailsdialog.dismiss();
            }
        });
        admin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin.isChecked()) {
                    isAdmin.setChecked(false);
                } else {
                    isAdmin.setChecked(true);
                }
            }
        });
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExit.isChecked()) {
                    isExit.setChecked(false);
                } else {
                    isExit.setChecked(true);
                }
            }
        });
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
    private void removelist(int position, String peoplePos) {
        String temp_delete_Emp = tempPeoplesList.get(position).get("empcode");
        tempPeoplesList.get(position).put("ischecked", "false");
        peoples_arraylist.get(Integer.parseInt(peoplePos)).put("ischecked", "false");
        peoples_arraylist.get(Integer.parseInt(peoplePos)).put("isadmin", "false");
        peoples_arraylist.get(Integer.parseInt(peoplePos)).put("isexit", "false");
        for (int i = 0; i < recycler_arraylist.size(); i++) {
            String current_empcode = recycler_arraylist.get(i).get("empcode");
            if (temp_delete_Emp.equalsIgnoreCase(current_empcode)) {
                recycler_arraylist.remove(i);
            }
        }
        people_list_adapter.notifyDataSetChanged();
        rechold1.notifyDataSetChanged();
    }
    private void addlist(int position, String peoplePos) {
        tempPeoplesList.get(position).put("ischecked", "true");
        peoples_arraylist.get(Integer.parseInt(peoplePos)).put("ischecked", "true");
        people_list_adapter.notifyDataSetChanged();
        recycler_arraylist.add(tempPeoplesList.get(position));
        rechold1.notifyDataSetChanged();
    }
    static class Holder {
        TextView empName;
        TextView empCode;
        PorterCircularImageView user_image;
        CheckBox add_member;
    }
    public class ListAdapterHolder extends RecyclerView.Adapter<ListAdapterHolder.ViewHolder> {
        public ListAdapterHolder() {
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
            final View sView = mInflater.inflate(R.layout.add_group_list, parent, false);
            return new ViewHolder(sView);
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            if ((position) == recycler_arraylist.size()) {
                holder.search_lay.setVisibility(View.GONE);
                holder.search_value.setVisibility(View.VISIBLE);
                holder.search_value.requestFocus();
                holder.search_value.setText(searchValueName);
                holder.search_value.setSelection(holder.search_value.getText().length());
                holder.search_value.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        tempPeoplesList = new ArrayList<>();
                        searchValueName = holder.search_value.getText().toString();
                        if (holder.search_value.getText().toString().trim().length() > 0) {
                            String s_value = holder.search_value.getText().toString();
                            for (int i = 0; i < peoples_arraylist.size(); i++) {
                                String Peoplelist = peoples_arraylist.get(i).get("empNameCode");
                                if (Peoplelist.toLowerCase().toString().trim().contains(s_value.toLowerCase().trim())) {
                                    tempPeoplesList.add(peoples_arraylist.get(i));
                                }
                            }
                            people_list_adapter.notifyDataSetChanged();
                        } else {
                            tempPeoplesList = peoples_arraylist;
                            people_list_adapter.notifyDataSetChanged();
                        }
                    }
                });
            } else {
                holder.search_lay.setVisibility(View.VISIBLE);
                String userimage_id = recycler_arraylist.get(position).get("docid");
                holder.user_name.setText(recycler_arraylist.get(position).get("empname"));
                holder.search_value.setVisibility(View.GONE);
                imageLoad(userimage_id, holder.user_image);
                if ((recycler_arraylist.get(position).get("empcode").equalsIgnoreCase(superAdmin) || (recycler_arraylist.get(position).get("empcode").equalsIgnoreCase(uID))))
                    holder.delete.setVisibility(View.INVISIBLE);
                else
                    holder.delete.setVisibility(View.VISIBLE);
            }
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Recycler position:" + position);
                    removelist1(position);
                }
            });
        }
        @Override
        public int getItemCount() {
            return recycler_arraylist.size() + 1;
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            PorterCircularImageView user_image;
            TextView user_name;
            ImageView delete;
            EditText search_value;
            LinearLayout search_lay;
            public ViewHolder(View view) {
                super(view);
                user_name = (TextView) view.findViewById(R.id.user_name);
                delete = (ImageView) view.findViewById(R.id.delete);
                user_image = (PorterCircularImageView) view.findViewById(R.id.user_image);
                search_value = (EditText) view.findViewById(R.id.search_value);
                search_lay = (LinearLayout) view.findViewById(R.id.search_lay);
            }
        }
    }
    private void removelist1(int position) {
        String temp_delete_Emp = recycler_arraylist.get(position).get("empcode");
        recycler_arraylist.remove(position);
        for (int i = 0; i < peoples_arraylist.size(); i++) {
            String current_empcode = peoples_arraylist.get(i).get("empcode");
            if (temp_delete_Emp.equalsIgnoreCase(current_empcode)) {
                peoples_arraylist.get(i).put("ischecked", "false");
            }
        }
        people_list_adapter.notifyDataSetChanged();
        rechold1.notifyDataSetChanged();
    }
}
