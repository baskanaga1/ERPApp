package com.guruinfo.scm.Chat;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.siyamed.shapeimageview.mask.PorterCircularImageView;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.NestedListView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by Kannan G on 12/25/2017.
 */
public class GroupChatRowInfo extends BaseActivity {
    TextView message, chat_right_time;
    LinearLayout deleteLay, searchLay;
    TextView action_name;
    ImageView back;
    LinearLayout sendLay, deliveredLay, readLay;
    ImageView singleTick, doubleTick, doubleTickRead;
    NestedListView readList, recevList, sendList;
    ArrayList<HashMap<String, String>> sendArratList = new ArrayList<>();
    ArrayList<HashMap<String, String>> readArratList = new ArrayList<>();
    ArrayList<HashMap<String, String>> deliveredArratList = new ArrayList<>();
    int loader = R.drawable.empty_dp_large;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_chat_message_info);
        readList = (NestedListView) findViewById(R.id.read_list);
        recevList = (NestedListView) findViewById(R.id.delivered_list);
        sendList = (NestedListView) findViewById(R.id.send_list);
        message = (TextView) findViewById(R.id.chat_right_text);
        deleteLay = (LinearLayout) findViewById(R.id.delete_lay);
        searchLay = (LinearLayout) findViewById(R.id.search_lay);
        action_name = (TextView) findViewById(R.id.action_name);
        back = (ImageView) findViewById(R.id.chat_back);
        chat_right_time = (TextView) findViewById(R.id.chat_right_time);
        singleTick = (ImageView) findViewById(R.id.single_tick);
        doubleTick = (ImageView) findViewById(R.id.double_tick);
        doubleTickRead = (ImageView) findViewById(R.id.double_view_tick);
        sendLay = (LinearLayout) findViewById(R.id.send_lay);
        deliveredLay = (LinearLayout) findViewById(R.id.delivered_lay);
        readLay = (LinearLayout) findViewById(R.id.read_lay);
        deleteLay.setVisibility(View.INVISIBLE);
        searchLay.setVisibility(View.INVISIBLE);
        action_name.setText("Message Info");
        Bundle bundle = getIntent().getExtras();
        String response = bundle.getString("response");
        String cStatus = bundle.getString("cStatus");
        String time = bundle.getString("time");
        String msg = bundle.getString("msg");
        Typeface myTypeface = Typeface.createFromAsset(
                context.getAssets(),
                "Roboto-Regular.ttf");
        chat_right_time.setTypeface(myTypeface);
        chat_right_time.setText(time);
        message.setTypeface(myTypeface);
        // action_name.setTypeface(myTypeface);
        message.setText(msg+Html.fromHtml(" &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;" +
                "&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));
        if (cStatus.equalsIgnoreCase("1")) {
            singleTick.setVisibility(View.VISIBLE);
            doubleTick.setVisibility(View.GONE);
            doubleTickRead.setVisibility(View.GONE);
        } else if (cStatus.equalsIgnoreCase("2")) {
            singleTick.setVisibility(View.GONE);
            doubleTick.setVisibility(View.VISIBLE);
            doubleTickRead.setVisibility(View.GONE);
        } else if (cStatus.equalsIgnoreCase("3")) {
            singleTick.setVisibility(View.GONE);
            doubleTick.setVisibility(View.GONE);
            doubleTickRead.setVisibility(View.VISIBLE);
        }
        try {
            JSONObject respObj = new JSONObject(response);
            JSONArray recArray = respObj.getJSONArray("receivedValue");
            JSONArray sendArray = respObj.getJSONArray("sendToList");
            JSONArray readArray = respObj.getJSONArray("readValue");
            for (int i = 0; i < recArray.length(); i++) {
                HashMap<String, String> map = new HashMap<>();
                map.put("Name", recArray.getJSONObject(i).getString("Name"));
                map.put("imageId", recArray.getJSONObject(i).getString("imageId"));
                map.put("time", recArray.getJSONObject(i).getString("Delivered"));
                map.put("label", "Delivered");
                deliveredArratList.add(map);
            }
            for (int i = 0; i < sendArray.length(); i++) {
                HashMap<String, String> map = new HashMap<>();
                map.put("Name", sendArray.getJSONObject(i).getString("Name"));
                map.put("imageId", sendArray.getJSONObject(i).getString("imageId"));
                map.put("time", sendArray.getJSONObject(i).getString("Sent"));
                map.put("label", "Send");
                sendArratList.add(map);
            }
            for (int i = 0; i < readArray.length(); i++) {
                HashMap<String, String> map = new HashMap<>();
                map.put("Name", readArray.getJSONObject(i).getString("Name"));
                map.put("imageId", readArray.getJSONObject(i).getString("imageId"));
                map.put("time", readArray.getJSONObject(i).getString("Read"));
                map.put("Delivered", readArray.getJSONObject(i).getString("Delivered"));
                map.put("label", "Read");
                readArratList.add(map);
            }
            if (deliveredArratList.size() > 0) {
                MsgAdapter msgAdapter = new MsgAdapter(context, deliveredArratList);
                recevList.setAdapter(msgAdapter);
            } else {
                deliveredLay.setVisibility(View.GONE);
            }
            if (readArratList.size() > 0) {
                MsgAdapter msgAdapter = new MsgAdapter(context, readArratList);
                readList.setAdapter(msgAdapter);
            } else {
                readLay.setVisibility(View.GONE);
            }
            if (sendArratList.size() > 0) {
                MsgAdapter msgAdapter = new MsgAdapter(context, sendArratList);
                sendList.setAdapter(msgAdapter);
            } else {
                sendLay.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public class MsgAdapter extends BaseAdapter {
        Context context;
        ArrayList<HashMap<String, String>> temp_arraylist = new ArrayList<>();
        public MsgAdapter(Context context, ArrayList<HashMap<String, String>> listValue) {
            this.context = context;
            this.temp_arraylist = listValue;
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
                v = mInflater.inflate(R.layout.group_chat_msg_info_row, null);
            }
            LinearLayout second_lay = (LinearLayout) v.findViewById(R.id.second_lay);
            TextView userName = (TextView) v.findViewById(R.id.user_name);
            TextView firstLabel = (TextView) v.findViewById(R.id.first_label);
            TextView secondLabel = (TextView) v.findViewById(R.id.second_label);
            TextView first_time = (TextView) v.findViewById(R.id.first_time);
            TextView second_time = (TextView) v.findViewById(R.id.second_time);
            PorterCircularImageView user_image = (PorterCircularImageView) v.findViewById(R.id.user_image);
            second_lay.setVisibility(View.GONE);
            userName.setText(temp_arraylist.get(position).get("Name"));
            String userimage_id = temp_arraylist.get(position).get("imageId");
            imageLoad(userimage_id, user_image);
            if (temp_arraylist.get(position).get("label").equalsIgnoreCase("Read")) {
                second_lay.setVisibility(View.VISIBLE);
                secondLabel.setText("Delivered");
                second_time.setText(temp_arraylist.get(position).get("Delivered"));
            }
            firstLabel.setText(temp_arraylist.get(position).get("label"));
            first_time.setText(temp_arraylist.get(position).get("time"));
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
