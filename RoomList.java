package com.guruinfo.scm.Chat;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
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
 * Created by ERP on 12/8/2017.
 */
public class RoomList extends Fragment {
    private static final String TAG = RoomList.class.getSimpleName();
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
    int loader = R.drawable.empty_dp_large;
    private TextView newsFeedEmptyListText;
    SCMApplication httpreadmanagement;
    String actionName;
    static String ACTION_LEAD_NEWS_FEED_LOAD = "action_lead_news_feed_load";
    ImageView chat_options;
    EditText search_value;
    String search_show = "0";
    LinearLayout search_layout;
    FloatingActionButton fab;
    ArrayList<HashMap<String, String>> temp_arraylist;
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
        chat_options = (ImageView) getActivity().findViewById(R.id.chat_actionbar).findViewById(R.id.chat_options);
        search_layout = (LinearLayout) view.findViewById(R.id.search_layout);
        search_value = (EditText) view.findViewById(R.id.search_value);
        search_value.setHint("Search");
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        String groupResponse = Sharedpref.GetPrefString(context, "Group");
        parseJsonFeed(groupResponse);
        String empResponse = Sharedpref.GetPrefString(context, "People");
        try {
            JSONArray peoplesNamelist = new JSONArray(empResponse);
            if (peoplesNamelist.length() > 0)
                fab.setVisibility(View.VISIBLE);
            else
                fab.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), GroupAdd.class);
                startActivity(in);
            }
        });
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
                        String Peoplelist = peoples_arraylist.get(i).get("groupName");
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
    private void parseJsonFeed(String response) {
        try {
            JSONArray groupNameList = new JSONArray(response);
            if (groupNameList.length() > 0) {
                listView.setVisibility(View.VISIBLE);
                newsFeedEmptyListText.setVisibility(View.GONE);
                for (int i = 0; i < groupNameList.length(); i++) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("groupName", groupNameList.getJSONObject(i).getString("groupName"));
                    hashMap.put("groupId", groupNameList.getJSONObject(i).getString("groupId"));
                    hashMap.put("ImageId", groupNameList.getJSONObject(i).getString("ImageId"));
                    hashMap.put("isadmin", groupNameList.getJSONObject(i).getString("isadmin"));
                    hashMap.put("memberDetails", groupNameList.getJSONObject(i).getJSONArray("memberDetails").toString());
                    hashMap.put("roomId", groupNameList.getJSONObject(i).getString("groupId"));
                    peoples_arraylist.add(hashMap);
                }
                for (int i = 0; i < peoples_arraylist.size(); i++) {
                    System.out.println(i + ":" + peoples_arraylist.get(i));
                }
                listAdapter = new PeoplesAdapter(context);
                listView.setAdapter(listAdapter);
            } else {
                listView.setVisibility(View.GONE);
                newsFeedEmptyListText.setVisibility(View.VISIBLE);
                newsFeedEmptyListText.setText("No Groups Available");
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
            TextView unReadCount = (TextView) v.findViewById(R.id.unread_count);
            userName.setText(peoples_arraylist.get(position).get("groupName"));
            unReadCount.setVisibility(View.GONE);
            lastMsg.setVisibility(View.GONE);
            indLay.setVisibility(View.GONE);
            groupLay.setVisibility(View.VISIBLE);
            try {
                JSONArray groupImageArray = new JSONArray(peoples_arraylist.get(position).get("memberDetails"));
                if (groupImageArray.length() >= 4) {
                    topLeftImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(0).getString("empPhoto"), topLeftImg);
                    topRightImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(1).getString("empPhoto"), topRightImg);
                    bottomLeftImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(2).getString("empPhoto"), bottomLeftImg);
                    bottomRightImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(3).getString("empPhoto"), bottomRightImg);
                } else if (groupImageArray.length() == 3) {
                    topLeftImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(0).getString("empPhoto"), topLeftImg);
                    topRightImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(1).getString("empPhoto"), topRightImg);
                    bottomLeftImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(2).getString("empPhoto"), bottomLeftImg);
                    bottomRightImg.setVisibility(View.GONE);
                } else if (groupImageArray.length() == 2) {
                    topLeftImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(0).getString("empPhoto"), topLeftImg);
                    topRightImg.setVisibility(View.INVISIBLE);
                    bottomLeftImg.setVisibility(View.INVISIBLE);
                    bottomRightImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(2).getString("empPhoto"), bottomRightImg);
                } else if (groupImageArray.length() == 1) {
                    topLeftImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(0).getString("empPhoto"), topLeftImg);
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
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String to_userid = peoples_arraylist.get(position).get("groupId");
                    String to_username = peoples_arraylist.get(position).get("groupName");
                    Intent in = new Intent(getActivity(), GroupChat.class);
                    in.putExtra("to_userid", to_userid);
                    in.putExtra("to_username", to_username);
                    in.putExtra("from_userid", uid);
                    in.putExtra("isGroup", "true");
                    in.putExtra("from_username", from_username);
                    in.putExtra("isadmin", peoples_arraylist.get(position).get("isadmin"));
                    in.putExtra("roomId", peoples_arraylist.get(position).get("roomId"));
                    in.putExtra("memberDetails", peoples_arraylist.get(position).get("memberDetails"));
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
            TextView unReadCount = (TextView) v.findViewById(R.id.unread_count);
            userName.setText(temp_arraylist.get(position).get("groupName"));
            unReadCount.setVisibility(View.GONE);
            lastMsg.setVisibility(View.GONE);
            indLay.setVisibility(View.GONE);
            groupLay.setVisibility(View.VISIBLE);
            try {
                JSONArray groupImageArray = new JSONArray(temp_arraylist.get(position).get("memberDetails"));
                if (groupImageArray.length() >= 4) {
                    topLeftImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(0).getString("empPhoto"), topLeftImg);
                    topRightImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(1).getString("empPhoto"), topRightImg);
                    bottomLeftImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(2).getString("empPhoto"), bottomLeftImg);
                    bottomRightImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(3).getString("empPhoto"), bottomRightImg);
                } else if (groupImageArray.length() == 3) {
                    topLeftImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(0).getString("empPhoto"), topLeftImg);
                    topRightImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(1).getString("empPhoto"), topRightImg);
                    bottomLeftImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(2).getString("empPhoto"), bottomLeftImg);
                    bottomRightImg.setVisibility(View.GONE);
                } else if (groupImageArray.length() == 2) {
                    topLeftImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(0).getString("empPhoto"), topLeftImg);
                    topRightImg.setVisibility(View.INVISIBLE);
                    bottomLeftImg.setVisibility(View.INVISIBLE);
                    bottomRightImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(2).getString("empPhoto"), bottomRightImg);
                } else if (groupImageArray.length() == 1) {
                    topLeftImg.setVisibility(View.VISIBLE);
                    imageLoag(groupImageArray.getJSONObject(0).getString("empPhoto"), topLeftImg);
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
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String to_userid = temp_arraylist.get(position).get("groupId");
                    String to_username = temp_arraylist.get(position).get("groupName");
                    Intent in = new Intent(getActivity(), GroupChat.class);
                    in.putExtra("to_userid", to_userid);
                    in.putExtra("to_username", to_username);
                    in.putExtra("from_userid", uid);
                    in.putExtra("isGroup", "true");
                    in.putExtra("from_username", from_username);
                    in.putExtra("isadmin", temp_arraylist.get(position).get("isadmin"));
                    in.putExtra("roomId", temp_arraylist.get(position).get("roomId"));
                    in.putExtra("memberDetails", temp_arraylist.get(position).get("memberDetails"));
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