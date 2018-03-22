package com.guruinfo.scm.Chat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.BaseActivity;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Created by Kannan G on 12/25/2017.
 */
public class IndivChatRowInfo extends BaseActivity {
    TextView readTime, deliveredTime, message, chat_right_time;
    LinearLayout deleteLay, searchLay;
    TextView action_name;
    ImageView back;
    ImageView singleTick, doubleTick, doubleTickRead;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indiv_chat_info);
        readTime = (TextView) findViewById(R.id.read_time);
        deliveredTime = (TextView) findViewById(R.id.delivered_time);
        message = (TextView) findViewById(R.id.chat_right_text);
        deleteLay = (LinearLayout) findViewById(R.id.delete_lay);
        searchLay = (LinearLayout) findViewById(R.id.search_lay);
        action_name = (TextView) findViewById(R.id.action_name);
        back = (ImageView) findViewById(R.id.chat_back);
        chat_right_time = (TextView) findViewById(R.id.chat_right_time);
        singleTick = (ImageView) findViewById(R.id.single_tick);
        doubleTick = (ImageView) findViewById(R.id.double_tick);
        doubleTickRead = (ImageView) findViewById(R.id.double_view_tick);
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
        deliveredTime.setTypeface(myTypeface);
        readTime.setTypeface(myTypeface);
       // action_name.setTypeface(myTypeface);
        message.setText(msg+Html.fromHtml("&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;" +
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
            JSONObject respObj=new JSONObject(response);
            JSONArray recArray=respObj.getJSONArray("receivedValue");
            JSONArray sendArray=respObj.getJSONArray("sendToList");
            JSONArray readArray=respObj.getJSONArray("readValue");
            if(readArray.length()>0){
                readTime.setText(readArray.getJSONObject(0).getString("Read"));
                deliveredTime.setText(readArray.getJSONObject(0).getString("Delivered"));
            }else if(recArray.length()>0){
                readTime.setText("-");
                deliveredTime.setText(recArray.getJSONObject(0).getString("Delivered"));
            }else {
                readTime.setText("-");
                deliveredTime.setText("-");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
