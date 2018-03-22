package com.guruinfo.scm.DPR.DPRDialog;

/**
 * Created by ERP2 on 3/12/2018.
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

public class DPR_SubActivity extends BaseActivity {

    String TAG = "SubActivity";
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

    ArrayList<HashMap<String, String>> ParentArayList = new ArrayList<>();
    ArrayList<HashMap<String, String>> ChildArayList = new ArrayList<>();
    //ArrayList<ArrayList<HashMap<String, String>>> ChildArayList = new ArrayList<>();

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
        toolbar.setTitle("Sub Activity");
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

        listRequestParameter = "{'Action':'DAILY_PROGRESS_REPORT','submode':'ONCLICK_FUNCTIONALITY','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','table':'tabload','MbookId':'"+MbookId+"','tabname':'subactivity','isviewonly':'true'}";
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
                int k =1;
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    HashMap<String, String> hashMap = new HashMap<>();
                    if(!jsonObj.getString("stageName").equalsIgnoreCase("")){
                        hashMap.put("sno", ""+k);
                        k++;
                    }else
                        hashMap.put("sno", jsonObj.getString("sno"));

                    hashMap.put("stageName", jsonObj.getString("stageName"));
                    JSONArray inrjsonarr = jsonObj.getJSONArray("TableValues");
                    for (int j = 0; j < inrjsonarr.length(); j++) {
                        JSONObject inrjsonObj = inrjsonarr.getJSONObject(j);
                        hashMap.put("spec", inrjsonObj.getString("spec"));
                        hashMap.put("itemName", inrjsonObj.getString("itemName"));
                        hashMap.put("Formula", inrjsonObj.getString("Formula"));
                        hashMap.put("subActivity", inrjsonObj.getString("subActivity"));
                        hashMap.put("Csno", inrjsonObj.getString("sno"));
                        hashMap.put("stageNameIOW", inrjsonObj.getString("stageName"));
                        hashMap.put("deductQty", inrjsonObj.getString("deductQty"));
                        hashMap.put("grossQty", inrjsonObj.getString("grossQty"));
                        hashMap.put("Length", inrjsonObj.getString("Length"));
                        hashMap.put("UOM", inrjsonObj.getString("UOM"));
                        hashMap.put("Breadth", inrjsonObj.getString("Breadth"));
                        hashMap.put("Depth", inrjsonObj.getString("Depth"));
                        hashMap.put("netQty", inrjsonObj.getString("netQty"));
                        hashMap.put("Nos", inrjsonObj.getString("Nos"));
                        hashMap.put("type", inrjsonObj.getString("type"));
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
                        hashMap = new HashMap<>();
                    }

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
        ArrayList<HashMap<String, String>> ParentArayList1 = new ArrayList<>();



        public ParentListAdapter(Context context) {
            mContex = context;
            mInflater = LayoutInflater.from(context);
            mChInflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // return valuearraylist.size();
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
                v = mInflater.inflate(R.layout.dpr_subactivity_listrow, null);
                holder = new Holder();
                holder.parent_lay =(LinearLayout) v.findViewById(R.id.parent_lay);
                holder.stage_lay =(LinearLayout) v.findViewById(R.id.stage_lay);
                holder.sub_activity_lay =(LinearLayout) v.findViewById(R.id.sub_activity_lay);
                holder.spec_lay =(LinearLayout) v.findViewById(R.id.spec_lay);
                holder.uom_lay =(LinearLayout) v.findViewById(R.id.uom_lay);
                holder.formula_lay =(LinearLayout) v.findViewById(R.id.formula_lay);
                holder.length_lay =(LinearLayout) v.findViewById(R.id.length_lay);
                holder.breadh_lay =(LinearLayout) v.findViewById(R.id.breadh_lay);
                holder.depth_lay =(LinearLayout) v.findViewById(R.id.depth_lay);
                holder.nos_lay =(LinearLayout) v.findViewById(R.id.nos_lay);
                holder.gross_lay =(LinearLayout) v.findViewById(R.id.gross_lay);
                holder.deduct_lay =(LinearLayout) v.findViewById(R.id.deduct_lay);
                holder.net_lay =(LinearLayout) v.findViewById(R.id.net_lay);
                holder.item_lay =(LinearLayout) v.findViewById(R.id.item_lay);
                holder.ticket=(ImageView)v.findViewById(R.id.ticket);

                holder.parent_sno = (SCMTextView) v.findViewById(R.id.parent_sno);
                holder.child_sno = (SCMTextView) v.findViewById(R.id.child_sno);
                holder.parent_list = (SCMTextView) v.findViewById(R.id.parent_list);
                holder.stage_name = (SCMTextView) v.findViewById(R.id.stage_name);
                holder.sub_activity_name = (SCMTextView) v.findViewById(R.id.sub_activity_name);
                holder.item_name = (SCMTextView) v.findViewById(R.id.item_name);
                holder.spec = (SCMTextView) v.findViewById(R.id.spec);
                holder.uom = (SCMTextView) v.findViewById(R.id.uom);
                holder.formula = (SCMTextView) v.findViewById(R.id.formula);
                holder.length = (SCMTextView) v.findViewById(R.id.length);
                holder.breadth = (SCMTextView) v.findViewById(R.id.breadth);
                holder.depth = (SCMTextView) v.findViewById(R.id.depth);
                holder.nos = (SCMTextView) v.findViewById(R.id.nos);
                holder.gross_qty = (SCMTextView) v.findViewById(R.id.gross_qty);
                holder.deduct_qty = (SCMTextView) v.findViewById(R.id.deduct_qty);
                holder.net_qty = (SCMTextView) v.findViewById(R.id.net_qty);

                holder.split_list = (View) v.findViewById(R.id.split_list);
                holder.split_list1 = (View) v.findViewById(R.id.split_list1);


                v.setTag(holder);
            } else {
                holder = (Holder) v.getTag();
            }

            try{

                if(!ChildArayList.get(position).get("stageName").equalsIgnoreCase("")) {
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
                    String stageNameWithStyle=ChildArayList.get(position).get("stageName").replace("->"," <b>\u25CF</b> ");
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
                if(ChildArayList.get(position).get("stageNameIOW").equalsIgnoreCase(""))
                    holder.stage_lay.setVisibility(View.GONE);
                else
                    holder.stage_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("subActivity").equalsIgnoreCase(""))
                    holder.sub_activity_lay.setVisibility(View.GONE);
                else
                    holder.sub_activity_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("itemName").equalsIgnoreCase(""))
                    holder.item_lay.setVisibility(View.GONE);
                else
                    holder.item_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("spec").equalsIgnoreCase(""))
                    holder.spec_lay.setVisibility(View.GONE);
                else
                    holder.spec_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("UOM").equalsIgnoreCase(""))
                    holder.uom_lay.setVisibility(View.GONE);
                else
                    holder.uom_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("Formula").equalsIgnoreCase(""))
                    holder.formula_lay.setVisibility(View.GONE);
                else
                    holder.formula_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("Length").equalsIgnoreCase(""))
                    holder.length_lay.setVisibility(View.GONE);
                else
                    holder.length_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("Breadth").equalsIgnoreCase(""))
                    holder.breadh_lay.setVisibility(View.GONE);
                else
                    holder.breadh_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("Depth").equalsIgnoreCase(""))
                    holder.depth_lay.setVisibility(View.GONE);
                else
                    holder.depth_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("Nos").equalsIgnoreCase(""))
                    holder.nos_lay.setVisibility(View.GONE);
                else
                    holder.nos_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("grossQty").equalsIgnoreCase(""))
                    holder.gross_lay.setVisibility(View.GONE);
                else
                    holder.gross_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("deductQty").equalsIgnoreCase(""))
                    holder.deduct_lay.setVisibility(View.GONE);
                else
                    holder.deduct_lay.setVisibility(View.VISIBLE);
                if(ChildArayList.get(position).get("netQty").equalsIgnoreCase(""))
                    holder.net_lay.setVisibility(View.GONE);
                else
                    holder.net_lay.setVisibility(View.VISIBLE);

                holder.child_sno.setText(ChildArayList.get(position).get("Csno"));
                holder.stage_name.setText(ChildArayList.get(position).get("stageNameIOW"));
                holder.sub_activity_name.setText(ChildArayList.get(position).get("subActivity"));
                holder.item_name.setText(ChildArayList.get(position).get("itemName"));
                holder.spec.setText(ChildArayList.get(position).get("spec"));
                holder.uom.setText(ChildArayList.get(position).get("UOM"));
                holder.formula.setText(ChildArayList.get(position).get("Formula"));
                holder.length.setText(ChildArayList.get(position).get("Length"));
                holder.breadth.setText(ChildArayList.get(position).get("Breadth"));
                holder.depth.setText(ChildArayList.get(position).get("Depth"));
                holder.nos.setText(ChildArayList.get(position).get("Nos"));
                holder.gross_qty.setText(ChildArayList.get(position).get("grossQty"));
                holder.deduct_qty.setText(ChildArayList.get(position).get("deductQty"));
                holder.net_qty.setText(ChildArayList.get(position).get("netQty"));

            }catch (Exception e){
                e.printStackTrace();
            }
            holder.ticket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ChildArayList.get(position).get("isTicketAdd").equalsIgnoreCase("true")) {
                        Intent intent=new Intent(DPR_SubActivity.this, DPRBasedTickedAdd.class);
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
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(DPR_SubActivity.this, holder.ticket, getString(R.string.transition_item));
                        ActivityCompat.startActivity(DPR_SubActivity.this, intent, options.toBundle());
                        //} else if (ChildArayList.get(position).get("isTicketView").equalsIgnoreCase("true") || ChildArayList.get(position).get("isTicketUserView").equalsIgnoreCase("true")) {
                    } else if (ChildArayList.get(position).get("isTicketView").equalsIgnoreCase("true")) {
                        Intent intent=new Intent(DPR_SubActivity.this, DPRBasedTicketDisplay.class);
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
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(DPR_SubActivity.this, holder.ticket, getString(R.string.transition_item));
                        ActivityCompat.startActivity(DPR_SubActivity.this, intent, options.toBundle());
                    }
                }
            });




            return v;
        }
    }
    static class Holder {
        LinearLayout parent_lay, stage_lay, sub_activity_lay, item_lay, spec_lay, uom_lay, formula_lay, length_lay, breadh_lay, depth_lay, nos_lay, gross_lay, deduct_lay, net_lay;
        SCMTextView parent_sno, parent_list, child_sno, stage_name, sub_activity_name, item_name, spec, uom, formula, length, breadth, depth, nos, gross_qty, deduct_qty, net_qty;
        View split_list, split_list1;
        ImageView ticket;

    }
}

