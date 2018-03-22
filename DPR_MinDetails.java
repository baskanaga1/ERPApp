package com.guruinfo.scm.DPR.DPRDialog;

/**
 * Created by ERP2 on 3/13/2018.
 */

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.guruinfo.scm.R;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.ui.SCMTextView;
import com.guruinfo.scm.common.utils.ApiCalls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;



public class DPR_MinDetails extends BaseActivity {

    String TAG = "MIN Details";
    Context context;
    SessionManager session;
    String uid, Cre_Id;

    @Bind(R.id.sub_list_view)
    ListView subListView;
    @Bind(R.id.error_msg)
    SCMTextView errorMsg;
    @Bind(R.id.ic_home)
    ImageButton homeButton;

    String listRequestParameter;
    String requestParameter;
    String MbookId;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    String colorCode;
    BackgroundTask backgroundTask;

    ArrayList<HashMap<String, String>> ChildArayList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_activity_list);

        context = this;
        ButterKnife.bind(this);
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
        if (getIntent().getStringExtra("MbookId")!=null) {
            MbookId = getIntent().getStringExtra("MbookId");
        }
        if ((getIntent().getStringExtra("colorCode").equalsIgnoreCase("#fa4067")))
            homeButton.setVisibility(View.GONE);
        colorCode = getIntent().getStringExtra("colorCode");
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        appBarLayout.setVisibility(View.VISIBLE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("MIN Details");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorCode)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(darker(Color.parseColor(colorCode), 0.9f));
        }

        listRequestParameter = "{'Action':'DAILY_PROGRESS_REPORT','submode':'ONCLICK_FUNCTIONALITY','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','table':'tabload','MbookId':'"+MbookId+"','tabname':'mindetails','isviewonly':'true'}";
        onListLoad(listRequestParameter, "LOAD");

    }

    public void onListLoad(String req, final String flag) {
        requestParameter = req;
        Log.d(TAG, requestParameter);
        backgroundTask = new BackgroundTask(this, flag, new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String values, String flagMsg) {
                try {
                    if (flagMsg.equals("internet")) {
                        showInternetDialog1(context, values, requestParameter, flag);
                    } else {
                        JSONObject jsonObject = new JSONObject(values);
                        parseJSONResponse(jsonObject, flag);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        backgroundTask.execute("", "", requestParameter);
        /*RestClientHelper.getInstance().getURL(requestParameter, context, new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    System.out.println(TAG + " --> " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    parseJSONResponse(jsonObject, flag);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                showInternetDialog1(context, error, requestParameter, flag);
            }
        });*/
    }

    private void parseJSONResponse(JSONObject response, String flag) {
        try {

                JSONArray jsonArr = response.getJSONArray("values");
                ChildArayList = ApiCalls.getArraylistfromJson(jsonArr.toString());
                if(ChildArayList.size()==0){
                    errorMsg.setVisibility(View.VISIBLE);
                    subListView.setVisibility(View.GONE);
                }
                final ParentListAdapter parentListAdapter = new ParentListAdapter(context);
                subListView.setAdapter(parentListAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showInternetDialog1(Context activity, String err_msg, final String requestParameterValues, final String flag) {
        final Dialog dialog = new Dialog(context, R.style.MaterialDialogSheet);
        dialog.setContentView(R.layout.offline_mode_alert); // your custom view.
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        TextView text = (TextView) dialog.findViewById(R.id.alert_msg);
        LinearLayout offLineLayout = (LinearLayout) dialog.findViewById(R.id.offline_lay);
        text.setText(err_msg);
        offLineLayout.setVisibility(View.GONE);
        Button offLineButton = (Button) dialog.findViewById(R.id.offline_btn);
        Button retryButton = (Button) dialog.findViewById(R.id.retry_btn);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onListLoad(requestParameterValues, flag);
            }
        });

        dialog.show();

    }

    public class ParentListAdapter extends BaseAdapter {

        private Context mContex;
        private LayoutInflater mInflater;
        private LayoutInflater mChInflater;

        public ParentListAdapter(Context context) {
            mContex = context;
            mInflater = LayoutInflater.from(context);
            mChInflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return ChildArayList.size();
        }
        @Override
        public String getItem(int position) {
            return ChildArayList.get(position).toString();

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder;
            View v = convertView;

            if (v == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = mInflater.inflate(R.layout.dpr_mindetails_listrow, null);
                holder = new Holder();

                holder.spec_lay =(LinearLayout) v.findViewById(R.id.spec_lay);
                holder.item_lay =(LinearLayout) v.findViewById(R.id.item_lay);
                holder.min_qty_lay =(LinearLayout) v.findViewById(R.id.min_qty_lay);


                holder.child_sno = (SCMTextView) v.findViewById(R.id.child_sno);
                holder.item_name = (SCMTextView) v.findViewById(R.id.item_name);
                holder.spec = (SCMTextView) v.findViewById(R.id.spec);
                holder.min_qty = (SCMTextView) v.findViewById(R.id.min_qty);




                v.setTag(holder);
            } else {
                holder = (Holder) v.getTag();
            }

            try{

                if(ChildArayList.get(position).get("Item").equalsIgnoreCase(""))
                    holder.item_lay.setVisibility(View.GONE);
                else
                    holder.item_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("Spec").equalsIgnoreCase(""))
                    holder.spec_lay.setVisibility(View.GONE);
                else
                    holder.spec_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("MINQty").equalsIgnoreCase(""))
                    holder.min_qty_lay.setVisibility(View.GONE);
                else
                    holder.min_qty_lay.setVisibility(View.VISIBLE);

                holder.child_sno.setText(ChildArayList.get(position).get("S.No"));
                holder.item_name.setText(ChildArayList.get(position).get("Item"));
                holder.spec.setText(ChildArayList.get(position).get("Spec"));
                holder.min_qty.setText(ChildArayList.get(position).get("MINQty"));


            }catch (Exception e){
                e.printStackTrace();
            }




            return v;
        }
    }
    static class Holder {
        LinearLayout item_lay, spec_lay, min_qty_lay;
        SCMTextView child_sno, item_name, spec, min_qty;


    }
}


