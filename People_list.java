package com.guruinfo.scm.Chat;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by Kannan G on 12/5/2017.
 */
public class People_list extends Fragment {
    private static final String TAG = People_list.class.getSimpleName();
    private PeoplesAdapter listAdapter;
    //private List<LeadPortalFeedItemPOJOClass> feedItems;
    String from_username, from_userid;
    //  ArrayList<HashMap<String, String>> arrayList,peoples_arraylist;
    ArrayList<HashMap<String, String>> peoples_arraylist = new ArrayList<>();
    ActionBar bar;
    SwipeRefreshLayout swipeRefreshLayout;
    String URL_FEED;
    SessionManager session;
    ListView listView;
    String uid, Cre_Id;
    Context context;
    private TextView newsFeedEmptyListText;
    SCMApplication httpreadmanagement;
    String actionName;
    static String ACTION_LEAD_NEWS_FEED_LOAD = "action_lead_news_feed_load";
    ImageView chat_options;
    EditText search_value;
    Chat_MainFragment chat_mainFragment = new Chat_MainFragment();
    String search_show = "0";
    ArrayList<HashMap<String, String>> temp_arraylist;
    int loader = R.drawable.empty_dp_large;
    LinearLayout search_layout;
    FloatingActionButton fab;
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
        httpreadmanagement = new SCMApplication();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatlists, container, false);
        listView = (ListView) view.findViewById(R.id.group_list);
        newsFeedEmptyListText = (TextView) view.findViewById(R.id.group_error_msg);
        search_layout = (LinearLayout) view.findViewById(R.id.search_layout);
        search_value = (EditText) view.findViewById(R.id.search_value);
        search_value.setHint("Search");
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        chat_options = (ImageView) getActivity().findViewById(R.id.chat_actionbar).findViewById(R.id.chat_options);
        //URL_FEED = HttpRequest.chatURL + "?AId=CMN_CHAT_MASTER_AJAX&submode=HOME_PROCESS&UID=" + uid + "&Cre_Id=" + Cre_Id + "&app_type=2";
        //actionName = ACTION_LEAD_NEWS_FEED_LOAD;
        //serviceRequest(false);
        String empResponse = Sharedpref.GetPrefString(context, "People");
        parseJsonFeed(empResponse);
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
                        String Peoplelist = peoples_arraylist.get(i).get("empNameCode");
                        if (Peoplelist.toLowerCase().toString().trim().contains(s_value.toLowerCase().trim())) {
                            temp_arraylist.add(peoples_arraylist.get(i));
                        }
                    }
                    PeoplesAdapter1 listAdapter = new PeoplesAdapter1(context);
                    listView.setAdapter(listAdapter);
                } else {
                    PeoplesAdapter listAdapter = new PeoplesAdapter(context);
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
    /*public void serviceRequest(boolean isGetFromServer) {
        ServiceUtils.makeJsonObjectRequest(context, URL_FEED, actionName, isGetFromServer, new ServiceResponseListener() {
            @Override
            public void onError(String message) {
                Log.d(TAG, "Error Response message--" + message);
            }
            @Override
            public void onResponse(Object response) {
                Log.d(TAG, "Success Response--" + response.toString());
                parseJsonFeed((JSONObject) response);
            }
            @Override
            public void onRetry() {
                Log.d(TAG, "onRetry");
                serviceRequest(true);
            }
        });
    }*/
    private void parseJsonFeed(String response) {
        try {
            JSONArray peoplesNamelist = new JSONArray(response);
            from_username = Sharedpref.GetPrefString(context, "empName");
            from_userid = uid;
            if (peoplesNamelist.length() > 0) {
                listView.setVisibility(View.VISIBLE);
                newsFeedEmptyListText.setVisibility(View.GONE);
                ArrayList<HashMap<String, String>> inActiveList = new ArrayList<>();
                for (int i = 0; i < peoplesNamelist.length(); i++) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("empNameCode", peoplesNamelist.getJSONObject(i).getString("name")+" "+peoplesNamelist.getJSONObject(i).getString("empcode"));
                    hashMap.put("empname", peoplesNamelist.getJSONObject(i).getString("name"));
                    hashMap.put("empid", peoplesNamelist.getJSONObject(i).getString("empid"));
                    hashMap.put("docid", peoplesNamelist.getJSONObject(i).getString("docid"));
                    hashMap.put("name", peoplesNamelist.getJSONObject(i).getString("name"));
                    hashMap.put("empcode", peoplesNamelist.getJSONObject(i).getString("empcode"));
                    hashMap.put("designation", peoplesNamelist.getJSONObject(i).getString("designation"));
                    hashMap.put("isActive", peoplesNamelist.getJSONObject(i).getString("isActive"));
                    if (!peoplesNamelist.getJSONObject(i).getString("name").equalsIgnoreCase("")) {
                        if (peoplesNamelist.getJSONObject(i).getString("isActive").equalsIgnoreCase("true")) {
                            peoples_arraylist.add(hashMap);
                        } else {
                            inActiveList.add(hashMap);
                        }
                    }
                }
                peoples_arraylist.addAll(inActiveList);
                listAdapter = new PeoplesAdapter(context);
                listView.setAdapter(listAdapter);
            } else {
                listView.setVisibility(View.GONE);
                newsFeedEmptyListText.setVisibility(View.VISIBLE);
                newsFeedEmptyListText.setText("No Peoples Available");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public class PeoplesAdapter extends BaseAdapter {
        Context context;
        public PeoplesAdapter(Context context) {
            this.context = context;
        }
        @Override
        public int getCount() {
            return peoples_arraylist.size();
        }
        @Override
        public Object getItem(int position) {
            return position;
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
                v = mInflater.inflate(R.layout.peoples_list_item, null);
            }
            TextView groupname = (TextView) v.findViewById(R.id.groupname);
            PorterCircularImageView user_image = (PorterCircularImageView) v.findViewById(R.id.user_image);
            ImageView user_active = (ImageView) v.findViewById(R.id.active);
            TextView empCode = (TextView) v.findViewById(R.id.emp_code);
            empCode.setText(peoples_arraylist.get(position).get("empcode"));
            if (peoples_arraylist.get(position).get("isActive").equalsIgnoreCase("true"))
                user_active.setVisibility(View.VISIBLE);
            else
                user_active.setVisibility(View.GONE);
            groupname.setText(peoples_arraylist.get(position).get("name"));
            String userimage_id = peoples_arraylist.get(position).get("docid");
            imageLoag(userimage_id, user_image);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String to_userid = peoples_arraylist.get(position).get("empcode");
                    String to_username = peoples_arraylist.get(position).get("name");
                    Intent in = new Intent(getActivity(), Chatwindow.class);
                    in.putExtra("to_userid", to_userid);
                    in.putExtra("to_username", to_username);
                    in.putExtra("from_userid", uid);
                    in.putExtra("from_username", from_username);
                    in.putExtra("roomId", "");
                    startActivity(in);
                }
            });
            return v;
        }
    }
    public class PeoplesAdapter1 extends BaseAdapter {
        Context context;
        public PeoplesAdapter1(Context context) {
            this.context = context;
        }
        @Override
        public int getCount() {
            return temp_arraylist.size();
        }
        @Override
        public Object getItem(int position) {
            return position;
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
                v = mInflater.inflate(R.layout.peoples_list_item, null);
            }
            TextView groupname = (TextView) v.findViewById(R.id.groupname);
            TextView empCode = (TextView) v.findViewById(R.id.emp_code);
            empCode.setText(temp_arraylist.get(position).get("empcode"));
            PorterCircularImageView user_image = (PorterCircularImageView) v.findViewById(R.id.user_image);
            ImageView user_active = (ImageView) v.findViewById(R.id.active);
            if (temp_arraylist.get(position).get("isActive").equalsIgnoreCase("true"))
                user_active.setVisibility(View.VISIBLE);
            else
                user_active.setVisibility(View.GONE);
            groupname.setText(temp_arraylist.get(position).get("name"));
            String userimage_id = temp_arraylist.get(position).get("docid");
            imageLoag(userimage_id, user_image);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String to_userid = temp_arraylist.get(position).get("empcode");
                    String to_username = temp_arraylist.get(position).get("name");
                    Intent in = new Intent(getActivity(), Chatwindow.class);
                    in.putExtra("to_userid", to_userid);
                    in.putExtra("to_username", to_username);
                    in.putExtra("from_userid", uid);
                    in.putExtra("from_username", from_username);
                    in.putExtra("roomId", "");
                    startActivity(in);
                }
            });
            return v;
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
    class ViewHolder {
        TextView groupname;
    }
}