package com.guruinfo.scm.DPR.DPRDialog;

/**
 * Created by ERP2 on 3/13/2018.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.guruinfo.scm.DPR.Ticketing.DPRBasedTickedAdd;
import com.guruinfo.scm.DPR.Ticketing.DPRBasedTicketDisplay;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.ui.SCMTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.guruinfo.scm.common.AppContants.CLASS_INTENT;

/**
 * Created by ERP2 on 3/9/2018.
 */

public class DPR_Nmr extends BaseActivity {

    String TAG = "NMR";
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
        toolbar.setTitle("NMR");
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

        listRequestParameter = "{'Action':'DAILY_PROGRESS_REPORT','submode':'ONCLICK_FUNCTIONALITY','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','table':'tabload','MbookId':'"+MbookId+"','tabname':'nmr','isviewonly':'true'}";
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
            if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_1)) {
                JSONArray jsonArr = response.getJSONArray("values");
                int j=1;
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("StageName", jsonObj.getString("StageName"));
                    if(!jsonObj.getString("StageName").equalsIgnoreCase("")){
                        hashMap.put("sno", ""+j);
                        j++;
                    }else
                        hashMap.put("sno", jsonObj.getString("sno"));

                    JSONObject inrjsonObj = jsonObj.getJSONObject("TableValues");

                    hashMap.put("Csno", inrjsonObj.getString("sno"));
                    hashMap.put("labour", inrjsonObj.getString("labour"));
                    hashMap.put("RegIn", inrjsonObj.getString("RegIn"));
                    hashMap.put("OtType", inrjsonObj.getString("OtType"));
                    hashMap.put("uom", inrjsonObj.getString("uom"));
                    hashMap.put("otMins", inrjsonObj.getString("otMins"));
                    hashMap.put("description", inrjsonObj.getString("description"));
                    hashMap.put("RegOut", inrjsonObj.getString("RegOut"));
                    hashMap.put("RegTotal", inrjsonObj.getString("RegTotal"));
                    hashMap.put("OtNo", inrjsonObj.getString("OtNo"));
                    hashMap.put("OtValue", inrjsonObj.getString("OtValue"));
                    hashMap.put("StageNameIOW", inrjsonObj.getString("StageNameIOW"));
                    hashMap.put("RegNo", inrjsonObj.getString("RegNo"));
                    hashMap.put("Total", inrjsonObj.getString("Total"));
                    hashMap.put("ProcessType", inrjsonObj.getString("ProcessType"));
                    hashMap.put("projId", inrjsonObj.getString("projId"));
                    hashMap.put("mBookId", inrjsonObj.getString("mBookId"));
                    hashMap.put("Ticketid", inrjsonObj.getString("Ticketid"));
                    hashMap.put("childId", inrjsonObj.getString("childId"));
                    hashMap.put("stageId", inrjsonObj.getString("stageId"));
                    hashMap.put("iowId", inrjsonObj.getString("iowId"));
                    hashMap.put("isTicketAdd", inrjsonObj.getString("isTicketAdd"));
                    hashMap.put("isTicketView", inrjsonObj.getString("isTicketView"));
                    hashMap.put("isTicketUserView", inrjsonObj.getString("isTicketUserView"));
                    ChildArayList.add(hashMap);

                }
                if(ChildArayList.size()==0){
                    errorMsg.setVisibility(View.VISIBLE);
                    subListView.setVisibility(View.GONE);

                }
                final ParentListAdapter parentListAdapter = new ParentListAdapter(context);
                subListView.setAdapter(parentListAdapter);
            }

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
                v = mInflater.inflate(R.layout.dpr_nmr_listrow, null);
                holder = new Holder();
                holder.parent_lay =(LinearLayout) v.findViewById(R.id.parent_lay);
                holder.stage_lay =(LinearLayout) v.findViewById(R.id.stage_lay);
                holder.labour_lay =(LinearLayout) v.findViewById(R.id.labour_lay);
                holder.descrip_lay =(LinearLayout) v.findViewById(R.id.descrip_lay);
                holder.uom_lay =(LinearLayout) v.findViewById(R.id.uom_lay);
                holder.regular_lay =(LinearLayout) v.findViewById(R.id.regular_lay);
                holder.nos_lay =(LinearLayout) v.findViewById(R.id.nos_lay);
                holder.inhour_lay =(LinearLayout) v.findViewById(R.id.inhour_lay);
                holder.outhour_lay =(LinearLayout) v.findViewById(R.id.outhour_lay);
                holder.total_hr_lay =(LinearLayout) v.findViewById(R.id.total_hr_lay);
                holder.ot_lay =(LinearLayout) v.findViewById(R.id.ot_lay);
                holder.ot_nos_lay =(LinearLayout) v.findViewById(R.id.ot_nos_lay);
                holder.type_lay =(LinearLayout) v.findViewById(R.id.type_lay);
                holder.value_lay =(LinearLayout) v.findViewById(R.id.value_lay);
                holder.ot_total_lay =(LinearLayout) v.findViewById(R.id.ot_total_lay);
                holder.total_lay =(LinearLayout) v.findViewById(R.id.total_lay);
                holder.ticket=(ImageView)v.findViewById(R.id.ticket); 


                holder.parent_sno = (SCMTextView) v.findViewById(R.id.parent_sno);
                holder.child_sno = (SCMTextView) v.findViewById(R.id.child_sno);
                holder.parent_list = (SCMTextView) v.findViewById(R.id.parent_list);
                holder.stage_name = (SCMTextView) v.findViewById(R.id.stage_name);
                holder.labour = (SCMTextView) v.findViewById(R.id.labour);
                holder.description = (SCMTextView) v.findViewById(R.id.description);
                holder.uom = (SCMTextView) v.findViewById(R.id.uom);
                holder.nos = (SCMTextView) v.findViewById(R.id.nos);
                holder.in_hour = (SCMTextView) v.findViewById(R.id.in_hour);
                holder.out_hour = (SCMTextView) v.findViewById(R.id.out_hour);
                holder.total_hour = (SCMTextView) v.findViewById(R.id.total_hour);
                holder.ot_nos = (SCMTextView) v.findViewById(R.id.ot_nos);
                holder.nos = (SCMTextView) v.findViewById(R.id.nos);
                holder.type = (SCMTextView) v.findViewById(R.id.type);
                holder.value = (SCMTextView) v.findViewById(R.id.value);
                holder.ot_total = (SCMTextView) v.findViewById(R.id.ot_total);
                holder.total = (SCMTextView) v.findViewById(R.id.total);

                holder.split_view = (View) v.findViewById(R.id.split_view);
                holder.split_list = (View) v.findViewById(R.id.split_list);
                holder.split_list1 = (View) v.findViewById(R.id.split_list1);


                v.setTag(holder);
            } else {
                holder = (Holder) v.getTag();
            }

            try{



                if(!ChildArayList.get(position).get("StageName").equalsIgnoreCase("")) {
                    if(position==0){
                        holder.split_list1.setVisibility(View.GONE);
                    }else {
                        holder.split_list1.setVisibility(View.VISIBLE);
                    }
                    if(position==ChildArayList.size()-1){
                        holder.split_list.setVisibility(View.VISIBLE);
                    }else {
                        holder.split_list.setVisibility(View.GONE);
                    }

                    holder.parent_lay.setVisibility(View.VISIBLE);
                    holder.parent_sno.setText(ChildArayList.get(position).get("sno"));
                    String stageNameWithStyle=ChildArayList.get(position).get("StageName").replace("->"," <b>\u25CF</b> ");
                    holder.parent_list.setText(Html.fromHtml(stageNameWithStyle));
                }else {
                    holder.split_list1.setVisibility(View.GONE);
                    holder.parent_lay.setVisibility(View.GONE);
                    if(position==ChildArayList.size()-1){
                        holder.split_list.setVisibility(View.VISIBLE);
                    }else {
                        holder.split_list.setVisibility(View.GONE);
                    }
                }
                holder.ticket.setVisibility(View.VISIBLE);
                if (ChildArayList.get(position).get("isTicketAdd").equalsIgnoreCase("true")) {
                    holder.ticket.setImageDrawable(getResources().getDrawable(R.drawable.ticket_new));
                } else if (ChildArayList.get(position).get("isTicketView").equalsIgnoreCase("true") || ChildArayList.get(position).get("isTicketUserView").equalsIgnoreCase("true")) {
                    holder.ticket.setImageDrawable(getResources().getDrawable(R.drawable.ticket_edit));
                } else {
                    holder.ticket.setVisibility(View.GONE);
                }

                if(ChildArayList.get(position).get("StageNameIOW").equalsIgnoreCase(""))
                    holder.stage_lay.setVisibility(View.GONE);
                else
                    holder.stage_lay.setVisibility(View.VISIBLE);

                if(ChildArayList.get(position).get("labour").equalsIgnoreCase(""))
                    holder.labour_lay.setVisibility(View.GONE);
                else
                    holder.labour_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("description").equalsIgnoreCase(""))
                    holder.descrip_lay.setVisibility(View.GONE);
                else
                    holder.descrip_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("uom").equalsIgnoreCase(""))
                    holder.uom_lay.setVisibility(View.GONE);
                else
                    holder.uom_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("Total").equalsIgnoreCase(""))
                    holder.total_lay.setVisibility(View.GONE);
                else
                    holder.total_lay.setVisibility(View.VISIBLE);

                holder.child_sno.setText(ChildArayList.get(position).get("Csno"));
                holder.stage_name.setText(ChildArayList.get(position).get("StageNameIOW"));
                holder.labour.setText(ChildArayList.get(position).get("labour"));
                holder.description.setText(ChildArayList.get(position).get("description"));
                holder.uom.setText(ChildArayList.get(position).get("uom"));
                holder.nos.setText(ChildArayList.get(position).get("RegNo"));
                holder.in_hour.setText(ChildArayList.get(position).get("RegIn"));
                holder.out_hour.setText(ChildArayList.get(position).get("RegOut"));
                holder.total_hour.setText(ChildArayList.get(position).get("RegTotal"));
                holder.ot_nos.setText(ChildArayList.get(position).get("OtNo"));
                holder.type.setText(ChildArayList.get(position).get("OtType"));
                holder.value.setText(ChildArayList.get(position).get("OtValue"));
                holder.ot_total.setText(ChildArayList.get(position).get("otMins"));
                holder.total.setText(ChildArayList.get(position).get("Total"));

                holder.ticket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ChildArayList.get(position).get("isTicketAdd").equalsIgnoreCase("true")) {
                            Intent intent=new Intent(DPR_Nmr.this, DPRBasedTickedAdd.class);
                            intent.putExtra("ticketId",ChildArayList.get(position).get("Ticketid"));
                            intent.putExtra("reopenStatus","false");
                            intent.putExtra("reqStatus","Open");
                            intent.putExtra("projectId",ChildArayList.get(position).get("projId"));
                            intent.putExtra("childId",ChildArayList.get(position).get("childId"));
                            intent.putExtra("mBookId",ChildArayList.get(position).get("mBookId"));
                            intent.putExtra("stageId",ChildArayList.get(position).get("stageId"));
                            intent.putExtra("iowId",ChildArayList.get(position).get("iowId"));
                            intent.putExtra("processType",ChildArayList.get(position).get("ProcessType"));
                            intent.putExtra("uom",ChildArayList.get(position).get("uom"));
                            intent.putExtra("colorCode", "#0aa0dc");
                            intent.putExtra("title", "MR");
                            CLASS_INTENT=getIntent();
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(DPR_Nmr.this, holder.ticket, getString(R.string.transition_item));
                            ActivityCompat.startActivity(DPR_Nmr.this, intent, options.toBundle());
                            //} else if (ChildArayList.get(position).get("isTicketView").equalsIgnoreCase("true") || ChildArayList.get(position).get("isTicketUserView").equalsIgnoreCase("true")) {
                        } else if (ChildArayList.get(position).get("isTicketView").equalsIgnoreCase("true")) {
                            Intent intent=new Intent(DPR_Nmr.this, DPRBasedTicketDisplay.class);
                            intent.putExtra("ticketId",ChildArayList.get(position).get("Ticketid"));
                            intent.putExtra("reopenStatus","false");
                            intent.putExtra("reqStatus","Open");
                            intent.putExtra("projectId",ChildArayList.get(position).get("projId"));
                            intent.putExtra("childId",ChildArayList.get(position).get("childId"));
                            intent.putExtra("mBookId",ChildArayList.get(position).get("mBookId"));
                            intent.putExtra("stageId",ChildArayList.get(position).get("stageId"));
                            intent.putExtra("iowId",ChildArayList.get(position).get("iowId"));
                            intent.putExtra("processType",ChildArayList.get(position).get("ProcessType"));
                            intent.putExtra("uom",ChildArayList.get(position).get("uom"));
                            intent.putExtra("colorCode", "#0aa0dc");
                            intent.putExtra("title", "MR");
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(DPR_Nmr.this, holder.ticket, getString(R.string.transition_item));
                            ActivityCompat.startActivity(DPR_Nmr.this, intent, options.toBundle());
                        }
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }




            return v;
        }
    }
    static class Holder {
        LinearLayout parent_lay,child_lay, stage_lay, labour_lay,descrip_lay,uom_lay,regular_lay,nos_lay,inhour_lay,outhour_lay,total_hr_lay,ot_lay,ot_nos_lay,type_lay,value_lay,ot_total_lay,total_lay;
        SCMTextView parent_sno, parent_list, child_sno, stage_name, labour,description,uom,nos,in_hour,out_hour,total_hour,ot_nos,type,value,ot_total,total;
        View split_view, split_list, split_list1;
        ImageView ticket;

    }
}


