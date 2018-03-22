package com.guruinfo.scm.Chat;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.clans.fab.FloatingActionButton;
import com.github.siyamed.shapeimageview.mask.PorterCircularImageView;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.SCMApplication;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.utils.Sharedpref;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by ERP on 12/5/2017.
 */
public class Chat_list extends Fragment {
    private static final String TAG = Chat_list.class.getSimpleName();
    public static PeoplesAdapter listAdapter;
    String from_username, from_userid;
    public static ArrayList<HashMap<String, String>> peoples_arraylist = new ArrayList<>();
    String URL_FEED;
    SessionManager session;
    static ListView listView;
    String uid, Cre_Id;
    Context context;
    public static Boolean isScreenOpen;
    static TextView newsFeedEmptyListText;
    SCMApplication httpreadmanagement;
    String actionName;
    ImageView chat_options;
    EditText search_value;
    String search_show = "0";
    ArrayList<HashMap<String, String>> temp_arraylist;
    int loader = R.drawable.empty_dp_large;
    LinearLayout search_layout;
    //REDIRECT TO peoplelist
    LinearLayout chatFeedsTabLayout;
    public LinearLayout groupsTabLayout;
    LinearLayout peoplesChatTabLayout;
    public View chatFeedsTabSelector;
    public View groupsTabSelector;
    public View peoplesTabSelector;
    FloatingActionButton fab;
    public TextView chat_text;
    public TextView group_text;
    public TextView people_text;
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        session = new SessionManager(context);
        HashMap<String, String> userdetails = session.getUserDetails();
        from_username = userdetails.get(SessionManager.NAME);
        uid = session.getUserDetails().get(SessionManager.ID);
        from_userid = uid;
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
        httpreadmanagement = new SCMApplication();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatlists, container, false);
        isScreenOpen = true;
        listView = (ListView) view.findViewById(R.id.group_list);
        newsFeedEmptyListText = (TextView) view.findViewById(R.id.group_error_msg);
        search_layout = (LinearLayout) view.findViewById(R.id.search_layout);
        search_value = (EditText) view.findViewById(R.id.search_value);
        chat_options = (ImageView) getActivity().findViewById(R.id.chat_actionbar).findViewById(R.id.chat_options);
        search_value.setHint("Search");
        android.app.NotificationManager notificationManager = (android.app.NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(23);
        //REDIRECT TO PEOPLESLIST
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        chat_options = (ImageView) getActivity().findViewById(R.id.chat_actionbar).findViewById(R.id.chat_options);
        chatFeedsTabLayout = (LinearLayout) getActivity().findViewById(R.id.lead_tab_layout).findViewById(R.id.chat_tab_layout);
        groupsTabLayout = (LinearLayout) getActivity().findViewById(R.id.lead_tab_layout).findViewById(R.id.groups_tab_layout);
        peoplesChatTabLayout = (LinearLayout) getActivity().findViewById(R.id.lead_tab_layout).findViewById(R.id.peoples_tab_layout);
        chatFeedsTabSelector = getActivity().findViewById(R.id.lead_tab_layout).findViewById(R.id.chat_tab_selector);
        groupsTabSelector = getActivity().findViewById(R.id.lead_tab_layout).findViewById(R.id.groups_tab_selector);
        peoplesTabSelector = getActivity().findViewById(R.id.lead_tab_layout).findViewById(R.id.peoples_tab_selector);
        chat_text = (TextView) getActivity().findViewById(R.id.lead_tab_layout).findViewById(R.id.chat_text);
        group_text = (TextView) getActivity().findViewById(R.id.lead_tab_layout).findViewById(R.id.group_text);
        people_text = (TextView) getActivity().findViewById(R.id.lead_tab_layout).findViewById(R.id.people_text);
        Typeface action_name_type = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Bold.ttf");
        chat_text.setTypeface(action_name_type);
        group_text.setTypeface(action_name_type);
        people_text.setTypeface(action_name_type);
        String empResponse = Sharedpref.GetPrefString(context, "People");
        try {
            JSONArray peopleArray = new JSONArray(empResponse);
            if (peopleArray.length() > 0) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                People_list newFragment = new People_list();
                Bundle args = new Bundle();
                newFragment.setArguments(args);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                YoYo.with(Techniques.SlideInLeft).duration(300).playOn(peoplesTabSelector);
                chatFeedsTabSelector.setVisibility(View.INVISIBLE);
                groupsTabSelector.setVisibility(View.INVISIBLE);
                peoplesTabSelector.setVisibility(View.VISIBLE);
                group_text.setTextColor(Color.parseColor("#9a9a9a"));
                people_text.setTextColor(Color.parseColor("#323232"));
                chat_text.setTextColor(Color.parseColor("#9a9a9a"));
                isScreenOpen = null;
            }
        });
        listAdapter = new PeoplesAdapter(context, R.layout.chat_list_row);
        if (!(Sharedpref.readgson(context, "ChatHistory") == null)) {
            if (!Sharedpref.readgson(context, "ChatHistory").isEmpty()) {
                peoples_arraylist = Sharedpref.readgson(context, "ChatHistory");
            }
        }
        //reLoad();
        listView.setAdapter(listAdapter);
        if (peoples_arraylist.size() == 0) {
            listView.setVisibility(View.GONE);
            newsFeedEmptyListText.setVisibility(View.VISIBLE);
            newsFeedEmptyListText.setText("No Chats Available");
        } else {
            listView.setVisibility(View.VISIBLE);
            newsFeedEmptyListText.setVisibility(View.GONE);
        }
        chat_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search_show.equalsIgnoreCase("0")) {
                    search_layout.setVisibility(View.VISIBLE);
                    search_show = "1";
                    search_value.requestFocus();
                    showSoftInputFromWindow();
                } else {
                    search_layout.setVisibility(View.GONE);
                    search_show = "0";
                    search_value.clearFocus();
                    hideSoftInputFromWindow();
                }
            }
        });
        search_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                temp_arraylist = new ArrayList<>();
                if (search_value.getText().toString().trim().length() > 0) {
                    String s_value = search_value.getText().toString();
                    for (int i = 0; i < peoples_arraylist.size(); i++) {
                        String Peoplelist = peoples_arraylist.get(i).get("name");
                        if (Peoplelist.toLowerCase().toString().trim().contains(s_value.toLowerCase().trim())) {
                            temp_arraylist.add(peoples_arraylist.get(i));
                        }
                    }
                    PeoplesAdapter1 listAdapter = new PeoplesAdapter1(context, R.layout.chat_list_row);
                    listView.setAdapter(listAdapter);
                } else {
                    PeoplesAdapter listAdapter = new PeoplesAdapter(context, R.layout.chat_list_row);
                    listView.setAdapter(listAdapter);
                }
            }
        });
        return view;
    }
    private void hideSoftInputFromWindow() {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (((Activity) context).getWindow().getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
            ((Activity) context).getCurrentFocus().clearFocus();
        }
    }
    private void showSoftInputFromWindow() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(search_value, InputMethodManager.SHOW_IMPLICIT);
    }
    @Override
    public void onResume() {
        isScreenOpen = true;
        super.onResume();
    }
    public static void listRefresh() {
        listAdapter.notifyDataSetChanged();
        if (peoples_arraylist.size() == 0) {
            listView.setVisibility(View.GONE);
            newsFeedEmptyListText.setVisibility(View.VISIBLE);
            newsFeedEmptyListText.setText("No Chats Available");
        } else {
            listView.setVisibility(View.VISIBLE);
            newsFeedEmptyListText.setVisibility(View.GONE);
        }
    }
   /* public void reLoad() {
        int TIME_OUT = 3000;
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    listAdapter.notifyDataSetChanged();
                    reLoad();
                } catch (Exception e) {
                }
            }
        }, TIME_OUT);
    }*/
    public class PeoplesAdapter extends ArrayAdapter<String> {
        public PeoplesAdapter(Context context, int resource) {
            super(context, resource);
        }
        @Override
        public int getCount() {
            return peoples_arraylist.size();
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = mInflater.inflate(R.layout.chat_list_row, null);
            }
            LinearLayout indLay = (LinearLayout) v.findViewById(R.id.indi_lay);
            LinearLayout groupLay = (LinearLayout) v.findViewById(R.id.group_Lay);
            PorterCircularImageView topLeftImg = (PorterCircularImageView) v.findViewById(R.id.top_left_img);
            PorterCircularImageView topRightImg = (PorterCircularImageView) v.findViewById(R.id.top_right_img);
            PorterCircularImageView bottomLeftImg = (PorterCircularImageView) v.findViewById(R.id.bottom_left_img);
            PorterCircularImageView bottomRightImg = (PorterCircularImageView) v.findViewById(R.id.bottom_right_img);
            TextView userName = (TextView) v.findViewById(R.id.user_name);
            TextView lastMsg = (TextView) v.findViewById(R.id.last_msg);
            final TextView unReadCount = (TextView) v.findViewById(R.id.unread_count);
            PorterCircularImageView user_image = (PorterCircularImageView) v.findViewById(R.id.user_image);
            userName.setText(peoples_arraylist.get(position).get("name"));
            String userimage_id = peoples_arraylist.get(position).get("docid");
            lastMsg.setText(peoples_arraylist.get(position).get("message"));
            if (peoples_arraylist.get(position).get("msgUnReadCount").equalsIgnoreCase("0") || peoples_arraylist.get(position).get("msgUnReadCount").equalsIgnoreCase("")) {
                unReadCount.setVisibility(View.GONE);
            } else {
                unReadCount.setVisibility(View.VISIBLE);
                unReadCount.setText(peoples_arraylist.get(position).get("msgUnReadCount"));
            }
            if (peoples_arraylist.get(position).get("chatType").equalsIgnoreCase("individual")) {
                indLay.setVisibility(View.VISIBLE);
                groupLay.setVisibility(View.GONE);
                try {
                    JSONArray indImageArray = new JSONArray(peoples_arraylist.get(position).get("memberDetails"));
                    for (int j = 0; j < indImageArray.length(); j++) {
                        if (!indImageArray.getJSONObject(j).getString("empCode").equalsIgnoreCase(uid)) {
                            imageLoad(indImageArray.getJSONObject(j).getString("empPhoto"), user_image);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                indLay.setVisibility(View.GONE);
                groupLay.setVisibility(View.VISIBLE);
                try {
                    JSONArray groupImageArray = new JSONArray(peoples_arraylist.get(position).get("memberDetails"));
                    if (groupImageArray.length() >= 4) {
                        topLeftImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(0).getString("empPhoto"), topLeftImg);
                        topRightImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(1).getString("empPhoto"), topRightImg);
                        bottomLeftImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(2).getString("empPhoto"), bottomLeftImg);
                        bottomRightImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(3).getString("empPhoto"), bottomRightImg);
                    } else if (groupImageArray.length() == 3) {
                        topLeftImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(0).getString("empPhoto"), topLeftImg);
                        topRightImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(1).getString("empPhoto"), topRightImg);
                        bottomLeftImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(2).getString("empPhoto"), bottomLeftImg);
                        bottomRightImg.setVisibility(View.GONE);
                    } else if (groupImageArray.length() == 2) {
                        topLeftImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(0).getString("empPhoto"), topLeftImg);
                        topRightImg.setVisibility(View.INVISIBLE);
                        bottomLeftImg.setVisibility(View.INVISIBLE);
                        bottomRightImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(2).getString("empPhoto"), bottomRightImg);
                    } else if (groupImageArray.length() == 1) {
                        topLeftImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(0).getString("empPhoto"), topLeftImg);
                        topRightImg.setVisibility(View.GONE);
                        bottomLeftImg.setVisibility(View.GONE);
                        bottomRightImg.setVisibility(View.GONE);
                    } else {
                        topLeftImg.setVisibility(View.VISIBLE);
                        topRightImg.setVisibility(View.GONE);
                        bottomLeftImg.setVisibility(View.GONE);
                        bottomRightImg.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isScreenOpen = null;
                    Intent in;
                    String to_userid = peoples_arraylist.get(position).get("empcode");
                    String to_username = peoples_arraylist.get(position).get("name");
                    if (peoples_arraylist.get(position).get("chatType").equalsIgnoreCase("individual")) {
                        in = new Intent(getActivity(), Chatwindow.class);
                        in.putExtra("to_userid", to_userid);
                    } else if (peoples_arraylist.get(position).get("chatType").equalsIgnoreCase("group")) {
                        in = new Intent(getActivity(), GroupChat.class);
                        in.putExtra("to_userid", peoples_arraylist.get(position).get("groupId"));
                        String groupResponse = Sharedpref.GetPrefString(context, "Group");
                        in.putExtra("isadmin", "0");
                        in.putExtra("isGroup", "false");
                        try {
                            JSONArray jsonArray = new JSONArray(groupResponse);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (jsonObject.getString("groupId").equalsIgnoreCase(peoples_arraylist.get(position).get("groupId"))) {
                                    in.putExtra("isadmin", jsonObject.getString("isadmin"));
                                    in.putExtra("isGroup", "true");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        in = new Intent(getActivity(), DynamicChat.class);
                        in.putExtra("to_userid", "0");
                        in.putExtra("isadmin", "1");
                    }
                    in.putExtra("to_username", to_username);
                    in.putExtra("from_userid", uid);
                    in.putExtra("from_username", from_username);
                    in.putExtra("roomId", peoples_arraylist.get(position).get("roomId"));
                    in.putExtra("memberDetails", peoples_arraylist.get(position).get("memberDetails"));
                    peoples_arraylist.get(position).put("msgUnReadCount", "0");
                    unReadCount.setVisibility(View.GONE);
                    if (!(Sharedpref.readgson(context, "ChatHistory") == null)) {
                        if (!Sharedpref.readgson(context, "ChatHistory").isEmpty()) {
                            Sharedpref.writegson(context, peoples_arraylist, "ChatHistory");
                        }
                    }
                    startActivity(in);
                }
            });
            return v;
        }
    }
    public class PeoplesAdapter1 extends ArrayAdapter<String> {
        public PeoplesAdapter1(Context context, int resource) {
            super(context, resource);
        }
        @Override
        public int getCount() {
            return temp_arraylist.size();
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = mInflater.inflate(R.layout.chat_list_row, null);
            }
            LinearLayout indLay = (LinearLayout) v.findViewById(R.id.indi_lay);
            LinearLayout groupLay = (LinearLayout) v.findViewById(R.id.group_Lay);
            PorterCircularImageView topLeftImg = (PorterCircularImageView) v.findViewById(R.id.top_left_img);
            PorterCircularImageView topRightImg = (PorterCircularImageView) v.findViewById(R.id.top_right_img);
            PorterCircularImageView bottomLeftImg = (PorterCircularImageView) v.findViewById(R.id.bottom_left_img);
            PorterCircularImageView bottomRightImg = (PorterCircularImageView) v.findViewById(R.id.bottom_right_img);
            PorterCircularImageView user_image = (PorterCircularImageView) v.findViewById(R.id.user_image);
            TextView userName = (TextView) v.findViewById(R.id.user_name);
            TextView lastMsg = (TextView) v.findViewById(R.id.last_msg);
            final TextView unReadCount = (TextView) v.findViewById(R.id.unread_count);
            userName.setText(temp_arraylist.get(position).get("name"));
            lastMsg.setText(temp_arraylist.get(position).get("message"));
            lastMsg.setVisibility(View.VISIBLE);
            indLay.setVisibility(View.GONE);
            groupLay.setVisibility(View.VISIBLE);
            if (temp_arraylist.get(position).get("msgUnReadCount").equalsIgnoreCase("0") || temp_arraylist.get(position).get("msgUnReadCount").equalsIgnoreCase("")) {
                unReadCount.setVisibility(View.GONE);
            } else {
                unReadCount.setVisibility(View.VISIBLE);
                unReadCount.setText(temp_arraylist.get(position).get("msgUnReadCount"));
            }
            if (temp_arraylist.get(position).get("chatType").equalsIgnoreCase("individual")) {
                indLay.setVisibility(View.VISIBLE);
                groupLay.setVisibility(View.GONE);
                try {
                    JSONArray indImageArray = new JSONArray(temp_arraylist.get(position).get("memberDetails"));
                    for (int j = 0; j < indImageArray.length(); j++) {
                        if (!indImageArray.getJSONObject(j).getString("empCode").equalsIgnoreCase(uid)) {
                            imageLoad(indImageArray.getJSONObject(j).getString("empPhoto"), user_image);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                indLay.setVisibility(View.GONE);
                groupLay.setVisibility(View.VISIBLE);
                try {
                    JSONArray groupImageArray = new JSONArray(temp_arraylist.get(position).get("memberDetails"));
                    if (groupImageArray.length() >= 4) {
                        topLeftImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(0).getString("empPhoto"), topLeftImg);
                        topRightImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(1).getString("empPhoto"), topRightImg);
                        bottomLeftImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(2).getString("empPhoto"), bottomLeftImg);
                        bottomRightImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(3).getString("empPhoto"), bottomRightImg);
                    } else if (groupImageArray.length() == 3) {
                        topLeftImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(0).getString("empPhoto"), topLeftImg);
                        topRightImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(1).getString("empPhoto"), topRightImg);
                        bottomLeftImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(2).getString("empPhoto"), bottomLeftImg);
                        bottomRightImg.setVisibility(View.GONE);
                    } else if (groupImageArray.length() == 2) {
                        topLeftImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(0).getString("empPhoto"), topLeftImg);
                        topRightImg.setVisibility(View.INVISIBLE);
                        bottomLeftImg.setVisibility(View.INVISIBLE);
                        bottomRightImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(2).getString("empPhoto"), bottomRightImg);
                    } else if (groupImageArray.length() == 1) {
                        topLeftImg.setVisibility(View.VISIBLE);
                        imageLoad(groupImageArray.getJSONObject(0).getString("empPhoto"), topLeftImg);
                        topRightImg.setVisibility(View.GONE);
                        bottomLeftImg.setVisibility(View.GONE);
                        bottomRightImg.setVisibility(View.GONE);
                    } else {
                        topLeftImg.setVisibility(View.VISIBLE);
                        topRightImg.setVisibility(View.GONE);
                        bottomLeftImg.setVisibility(View.GONE);
                        bottomRightImg.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isScreenOpen = null;
                    Intent in;
                    String to_userid = temp_arraylist.get(position).get("empcode");
                    String to_username = temp_arraylist.get(position).get("name");
                    if (temp_arraylist.get(position).get("chatType").equalsIgnoreCase("individual")) {
                        in = new Intent(getActivity(), Chatwindow.class);
                        in.putExtra("to_userid", to_userid);
                    } else if (temp_arraylist.get(position).get("chatType").equalsIgnoreCase("group")) {
                        in = new Intent(getActivity(), GroupChat.class);
                        in.putExtra("to_userid", temp_arraylist.get(position).get("groupId"));
                        String groupResponse = Sharedpref.GetPrefString(context, "Group");
                        in.putExtra("isadmin", "0");
                        in.putExtra("isGroup", "false");
                        try {
                            JSONArray jsonArray = new JSONArray(groupResponse);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (jsonObject.getString("groupId").equalsIgnoreCase(temp_arraylist.get(position).get("groupId"))) {
                                    in.putExtra("isadmin", jsonObject.getString("isadmin"));
                                    in.putExtra("isGroup", "true");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        in = new Intent(getActivity(), DynamicChat.class);
                        in.putExtra("to_userid", "0");
                        in.putExtra("isadmin", "1");
                    }
                    in.putExtra("to_username", to_username);
                    in.putExtra("from_userid", uid);
                    in.putExtra("from_username", from_username);
                    in.putExtra("roomId", temp_arraylist.get(position).get("roomId"));
                    in.putExtra("memberDetails", temp_arraylist.get(position).get("memberDetails"));
                    peoples_arraylist.get(Integer.parseInt(temp_arraylist.get(position).get("position"))).put("msgUnReadCount", "0");
                    unReadCount.setVisibility(View.GONE);
                    if (!(Sharedpref.readgson(context, "ChatHistory") == null)) {
                        if (!Sharedpref.readgson(context, "ChatHistory").isEmpty()) {
                            Sharedpref.writegson(context, peoples_arraylist, "ChatHistory");
                        }
                    }
                    startActivity(in);
                }
            });
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
    class ViewHolder {
        TextView groupname;
    }
}