package com.guruinfo.scm.DPR.DPRDialog;

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
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guruinfo.scm.DPR.Ticketing.DPRBasedTickedAdd;
import com.guruinfo.scm.DPR.Ticketing.DPRBasedTicketDisplay;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.NestedListView;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.ui.MyExpandableListView;
import com.guruinfo.scm.common.ui.SCMTextView;
import com.guruinfo.scm.common.utils.ApiCalls;

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

public class DPR_MaterialRequest extends BaseActivity {

    String TAG = "DPRGeneralDisplayAct";
    Context context;
    SessionManager session;
    String uid, Cre_Id;
    static BackgroundTask backgroundTask;
    @Bind(R.id.expandable_lv)
    MyExpandableListView mrListView;
    @Bind(R.id.labour_lv)
    NestedListView labourLv;
    @Bind(R.id.error_msg)
    SCMTextView errorMsg;
    @Bind(R.id.labour_datail_head)
    SCMTextView labour_datail_head;
    String requestParameter;
    String mBookId;
    String displayType;
    String title;
    private int lastExpandedPosition = -1;
    ArrayList<ArrayList<HashMap<String, String>>> childItems;
    ArrayList<HashMap<String, String>> mrParentList;
    HashMap<Integer, View> ChildView;
    ArrayList<HashMap<String, String>> labourListArray;

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    String colorCode;

    ArrayList<HashMap<String, String>> ParentArayList = new ArrayList<>();
    ArrayList<ArrayList<HashMap<String, String>>> ChildArayList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dpr_material_request);
        mrParentList = new ArrayList<>();
        childItems = new ArrayList<>();
        ChildView = new HashMap<Integer, View>();
        labourListArray = new ArrayList<>();
        context = this;
        ButterKnife.bind(this);
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
        mBookId = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        displayType = getIntent().getStringExtra("type");
        requestParameter = "{'Action':'DAILY_PROGRESS_REPORT','submode':'ONCLICK_FUNCTIONALITY','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','table':'tabload','isviewonly':'true','tabname':'" + displayType + "','MbookId':'" + mBookId + "'}";
        getList(requestParameter, "VIEW");
        colorCode = getIntent().getStringExtra("colorCode");
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        appBarLayout.setVisibility(View.VISIBLE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
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
    }

    public void getList(final String requestParameters, final String flag) {
        Log.d(TAG, requestParameter);
        Log.d(TAG, context.toString());
        backgroundTask = new BackgroundTask(context, flag, new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String values, String flagMsg) {
                try {
                    if (flagMsg.equals("internet")) {
                        showInternetDialog1(context, values, requestParameters, flag);
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
        backgroundTask.execute("", "", requestParameters);
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
                getList(requestParameterValues, flag);
            }
        });
        dialog.show();
    }

    private void parseJSONResponse(JSONObject responsejson, String flag) {
        try {
            if (responsejson.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_1)) {
                if (flag.equals("VIEW")) {
                    display(responsejson);
                }
            } else if (responsejson.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_403)) {
                showSessionScreensDialog(context, responsejson.getString(AppContants.RESPONSE_MESSAGE));
            } else {
                setToast(responsejson.getString(AppContants.RESPONSE_MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void display(JSONObject respObj) {
        try {
            JSONArray mainTableArray = respObj.getJSONArray("TableValues");
            int n = 0;
            for (int k = 0; k < mainTableArray.length(); k++) {
                HashMap<String, String> map = new HashMap<>();
                map.put("stageName", mainTableArray.getJSONObject(k).getString("stageName"));
                if (!(mainTableArray.getJSONObject(k).getString("stageName").equalsIgnoreCase(""))) {
                    n++;
                    map.put("stageSno", "" + n);
                }
                JSONArray tableArray = mainTableArray.getJSONObject(k).getJSONArray("TableValues");
                for (int i = 0; i < tableArray.length(); i++) {
                    map.put("iowLabourDisplayMisLabour", tableArray.getJSONObject(i).getJSONArray("iowLabourDisplayMisLabour").toString());
                    map.put("Material", tableArray.getJSONObject(i).getJSONArray("Material").toString());
                    map.put("iowLabourDisplayMain", tableArray.getJSONObject(i).getString("iowLabourDisplayMain").toString());
                    map.put("formula", tableArray.getJSONObject(i).getString("formula"));
                    map.put("parentSno", tableArray.getJSONObject(i).getString("sno"));
                    map.put("uom", tableArray.getJSONObject(i).getString("uom"));
                    map.put("grossQty", tableArray.getJSONObject(i).getString("grossQty"));
                    map.put("iowName", tableArray.getJSONObject(i).getString("iowName"));
                    map.put("netQty", tableArray.getJSONObject(i).getString("netQty"));
                    map.put("deductedQty", tableArray.getJSONObject(i).getString("deductedQty"));
                    map.put("length", tableArray.getJSONObject(i).getString("length"));
                    map.put("breadth", tableArray.getJSONObject(i).getString("breadth"));
                    map.put("depth", tableArray.getJSONObject(i).getString("depth"));
                    map.put("Nos", tableArray.getJSONObject(i).getString("Nos"));
                    map.put("ProcessType", tableArray.getJSONObject(i).getString("ProcessType"));
                    map.put("projId", tableArray.getJSONObject(i).getString("projId"));
                    map.put("mBookId", tableArray.getJSONObject(i).getString("mBookId"));
                    map.put("Ticketid", tableArray.getJSONObject(i).getString("Ticketid"));
                    map.put("childId", tableArray.getJSONObject(i).getString("childId"));
                    map.put("stageId", tableArray.getJSONObject(i).getString("stageId"));
                    map.put("iowId", tableArray.getJSONObject(i).getString("iowId"));
                    map.put("isTicketAdd", tableArray.getJSONObject(i).getString("isTicketAdd"));
                    map.put("isTicketView", tableArray.getJSONObject(i).getString("isTicketView"));
                    map.put("isTicketUserView", tableArray.getJSONObject(i).getString("isTicketUserView"));
                    map.put("WO", tableArray.getJSONObject(i).getJSONObject("otherDetails").getString("WO"));
                    map.put("dprReject", tableArray.getJSONObject(i).getJSONObject("otherDetails").getString("dprReject"));
                    map.put("dprRaised", tableArray.getJSONObject(i).getJSONObject("otherDetails").getString("dprRaised"));
                    map.put("EST", tableArray.getJSONObject(i).getJSONObject("otherDetails").getString("EST"));
                    map.put("Balance", tableArray.getJSONObject(i).getJSONObject("otherDetails").getString("Balance"));
                    JSONArray childArray = tableArray.getJSONObject(i).getJSONArray("childTable");
                    ArrayList<HashMap<String, String>> childList = new ArrayList<>();
                    for (int j = 0; j < childArray.length(); j++) {
                        HashMap<String, String> childMap = new HashMap<>();
                        childMap.put("Qty", childArray.getJSONObject(j).getString("Qty"));
                        childMap.put("Formula", childArray.getJSONObject(j).getString("Formula"));
                        childMap.put("childSno", childArray.getJSONObject(j).getString("sNo"));
                        childMap.put("gridName", childArray.getJSONObject(j).getString("gridName"));
                        childMap.put("gridDetail", childArray.getJSONObject(j).getString("gridDetail"));
                        childMap.put("Length", childArray.getJSONObject(j).getString("Length"));
                        childMap.put("Breadth", childArray.getJSONObject(j).getString("Breadth"));
                        childMap.put("Remarks", childArray.getJSONObject(j).getString("Remarks"));
                        childMap.put("Depth", childArray.getJSONObject(j).getString("Depth"));
                        childMap.put("jobName", childArray.getJSONObject(j).getString("jobName"));
                        childMap.put("Nos", childArray.getJSONObject(j).getString("Nos"));
                        childMap.put("DPRReject", childArray.getJSONObject(j).getJSONObject("Others").getString("DPRReject"));
                        childMap.put("DPRRaised", childArray.getJSONObject(j).getJSONObject("Others").getString("DPRRaised"));
                        childMap.put("EST", childArray.getJSONObject(j).getJSONObject("Others").getString("EST"));
                        childMap.put("Balance", childArray.getJSONObject(j).getJSONObject("Others").getString("Balance"));
                        childList.add(childMap);
                    }
                    childItems.add(new ArrayList<HashMap<String, String>>(childList));
                    mrParentList.add(map);
                    map = new HashMap<>();
                }
            }
            labourListArray = ApiCalls.getArraylistfromJson(respObj.getJSONArray("labourDetails").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        labour_datail_head.setVisibility(View.VISIBLE);
        if(labourListArray.size()==0 && mrParentList.size()==0){
            labour_datail_head.setVisibility(View.GONE);
            errorMsg.setVisibility(View.VISIBLE);
        }
        final ParentListExpandableadapter parentListAdapter = new ParentListExpandableadapter(context);
        mrListView.setAdapter(parentListAdapter);
        mrListView.setVisibility(View.VISIBLE);
        mrListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    mrListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
        mrListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                setListViewHeight(parent, groupPosition);
                return false;
            }
        });
        labourDetailsAdapter labourDetailsAdapter = new labourDetailsAdapter(context);
        labourLv.setAdapter(labourDetailsAdapter);
    }

    public void setListViewHeight(ExpandableListView listView,
                                  int group) {
        android.widget.ExpandableListAdapter listAdapter = (android.widget.ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupItem.getMeasuredHeight();
            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.AT_MOST);
                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    class ParentViewHolder {
        @Bind(R.id.pp_sno)
        SCMTextView pp_sno;
        @Bind(R.id.stage_name)
        SCMTextView stage_name;
        @Bind(R.id.iow)
        SCMTextView iow;
        @Bind(R.id.p_sno)
        SCMTextView p_sno;
        @Bind(R.id.uom)
        SCMTextView uom;
        @Bind(R.id.formula)
        SCMTextView formula;
        @Bind(R.id.length)
        SCMTextView length;
        @Bind(R.id.breadth)
        SCMTextView breadth;
        @Bind(R.id.depth)
        SCMTextView depth;
        @Bind(R.id.nos)
        SCMTextView nos;
        @Bind(R.id.gross_qty)
        SCMTextView gross_qty;
        @Bind(R.id.deduct_qty)
        SCMTextView deduct_qty;
        @Bind(R.id.net_qty)
        SCMTextView net_qty;
        @Bind(R.id.est_qty)
        SCMTextView est_qty;
        @Bind(R.id.wo_qty)
        SCMTextView wo_qty;
        @Bind(R.id.dpr_raised_qty)
        SCMTextView dpr_raised_qty;
        @Bind(R.id.dpr_rejected_qty)
        SCMTextView dpr_rejected_qty;
        @Bind(R.id.balance_qty)
        SCMTextView balance_qty;
        @Bind(R.id.ticket)
        ImageView ticket;
        @Bind(R.id.arrowbutton)
        ImageView arrowbutton;

        @Bind(R.id.parent_lay)
        LinearLayout parent_lay;

        public ParentViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class LabourViewHolder {
        @Bind(R.id.labour_type)
        SCMTextView labour_type;
        @Bind(R.id.nop)
        SCMTextView nop;
        @Bind(R.id.hours)
        SCMTextView hours;
        @Bind(R.id.remarks)
        SCMTextView remarks;


        public LabourViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class MaterialViewHolder {
        @Bind(R.id.item)
        SCMTextView item;
        @Bind(R.id.uom)
        SCMTextView uom;
        @Bind(R.id.est_qty)
        SCMTextView est_qty;
        @Bind(R.id.rec_qty)
        SCMTextView rec_qty;
        @Bind(R.id.balance_qty)
        SCMTextView balance_qty;


        public MaterialViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class MainLabourViewHolder {
        @Bind(R.id.labour)
        SCMTextView labour;
        @Bind(R.id.uom)
        SCMTextView uom;
        @Bind(R.id.est_qty)
        SCMTextView est_qty;
        @Bind(R.id.actual_qty)
        SCMTextView actual_qty;


        public MainLabourViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class MiscLabourViewHolder {
        @Bind(R.id.labour)
        SCMTextView labour;
        @Bind(R.id.uom)
        SCMTextView uom;
        @Bind(R.id.actual_qty)
        SCMTextView actual_qty;


        public MiscLabourViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public class ParentListExpandableadapter extends BaseExpandableListAdapter {
        private Context mContex;
        private LayoutInflater mInflater;
        private LayoutInflater mChInflater;

        public ParentListExpandableadapter(Context context) {
            mContex = context;
            mInflater = LayoutInflater.from(context);
            mChInflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public Object getGroup(int groupPosition) {
            return childItems.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return childItems.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
            final ParentViewHolder holder;
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContex
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = mInflater.inflate(R.layout.dpr_mr_parent_lv_row, null);
                holder = new ParentViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (ParentViewHolder) row.getTag();
            }
            if (!(mrParentList.get(groupPosition).get("stageName").equalsIgnoreCase(""))) {
                holder.parent_lay.setVisibility(View.VISIBLE);
                String stageNameWithStyle=mrParentList.get(groupPosition).get("stageName").replace("->"," <b>\u25CF</b> ");
                holder.stage_name.setText(Html.fromHtml(stageNameWithStyle));
                holder.p_sno.setText(mrParentList.get(groupPosition).get("stageSno"));
            } else {
                holder.parent_lay.setVisibility(View.GONE);
            }
            holder.ticket.setVisibility(View.VISIBLE);
            if (mrParentList.get(groupPosition).get("isTicketAdd").equalsIgnoreCase("true")) {
                holder.ticket.setImageDrawable(getResources().getDrawable(R.drawable.ticket_new));
            } else if (mrParentList.get(groupPosition).get("isTicketView").equalsIgnoreCase("true") || mrParentList.get(groupPosition).get("isTicketUserView").equalsIgnoreCase("true")) {
                holder.ticket.setImageDrawable(getResources().getDrawable(R.drawable.ticket_edit));
            } else {
                holder.ticket.setVisibility(View.GONE);
            }
            holder.pp_sno.setText(mrParentList.get(groupPosition).get("parentSno"));
            holder.iow.setText(mrParentList.get(groupPosition).get("iowName"));
            holder.formula.setText(mrParentList.get(groupPosition).get("formula"));
            holder.uom.setText(mrParentList.get(groupPosition).get("uom"));
            holder.length.setText(mrParentList.get(groupPosition).get("length"));
            holder.breadth.setText(mrParentList.get(groupPosition).get("breadth"));
            holder.depth.setText(mrParentList.get(groupPosition).get("depth"));
            holder.nos.setText(mrParentList.get(groupPosition).get("Nos"));
            holder.gross_qty.setText(mrParentList.get(groupPosition).get("grossQty"));
            holder.deduct_qty.setText(mrParentList.get(groupPosition).get("deductedQty"));
            holder.net_qty.setText(mrParentList.get(groupPosition).get("netQty"));
            holder.est_qty.setText(mrParentList.get(groupPosition).get("EST"));
            holder.wo_qty.setText(mrParentList.get(groupPosition).get("WO"));
            holder.dpr_raised_qty.setText(mrParentList.get(groupPosition).get("dprRaised"));
            holder.dpr_rejected_qty.setText(mrParentList.get(groupPosition).get("dprReject"));
            holder.balance_qty.setText(mrParentList.get(groupPosition).get("Balance"));
            holder.pp_sno.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDetailsDialog(mrParentList.get(groupPosition).get("Material"), mrParentList.get(groupPosition).get("iowLabourDisplayMain"), mrParentList.get(groupPosition).get("iowLabourDisplayMisLabour"));
                }
            });
            if (isExpanded) {
                holder.arrowbutton.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
            } else {
                holder.arrowbutton.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
            }
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isExpanded) {
                        ((ExpandableListView) parent).collapseGroup(groupPosition);
                    } else {
                        ((ExpandableListView) parent).expandGroup(groupPosition, true);
                        ChildView.clear();
                    }
                }
            });
            holder.ticket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mrParentList.get(groupPosition).get("isTicketAdd").equalsIgnoreCase("true")) {
                        Intent intent=new Intent(DPR_MaterialRequest.this, DPRBasedTickedAdd.class);
                        intent.putExtra("ticketId",mrParentList.get(groupPosition).get("Ticketid"));
                        intent.putExtra("reopenStatus","false");
                        intent.putExtra("reqStatus","Open");
                        intent.putExtra("projectId",mrParentList.get(groupPosition).get("projId"));
                        intent.putExtra("childId",mrParentList.get(groupPosition).get("childId"));
                        intent.putExtra("mBookId",mrParentList.get(groupPosition).get("mBookId"));
                        intent.putExtra("stageId",mrParentList.get(groupPosition).get("stageId"));
                        intent.putExtra("iowId",mrParentList.get(groupPosition).get("iowId"));
                        intent.putExtra("processType",mrParentList.get(groupPosition).get("ProcessType"));
                        intent.putExtra("uom",mrParentList.get(groupPosition).get("uom"));
                        intent.putExtra("colorCode", "#0aa0dc");
                        intent.putExtra("title", "MR");
                        CLASS_INTENT=getIntent();
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(DPR_MaterialRequest.this, holder.ticket, getString(R.string.transition_item));
                        ActivityCompat.startActivity(DPR_MaterialRequest.this, intent, options.toBundle());
                    //} else if (mrParentList.get(groupPosition).get("isTicketView").equalsIgnoreCase("true") || mrParentList.get(groupPosition).get("isTicketUserView").equalsIgnoreCase("true")) {
                    } else if (mrParentList.get(groupPosition).get("isTicketView").equalsIgnoreCase("true")) {
                        Intent intent=new Intent(DPR_MaterialRequest.this, DPRBasedTicketDisplay.class);
                        intent.putExtra("ticketId",mrParentList.get(groupPosition).get("Ticketid"));
                        intent.putExtra("reopenStatus","false");
                        intent.putExtra("reqStatus","Open");
                        intent.putExtra("projectId",mrParentList.get(groupPosition).get("projId"));
                        intent.putExtra("childId",mrParentList.get(groupPosition).get("childId"));
                        intent.putExtra("mBookId",mrParentList.get(groupPosition).get("mBookId"));
                        intent.putExtra("stageId",mrParentList.get(groupPosition).get("stageId"));
                        intent.putExtra("iowId",mrParentList.get(groupPosition).get("iowId"));
                        intent.putExtra("processType",mrParentList.get(groupPosition).get("ProcessType"));
                        intent.putExtra("uom",mrParentList.get(groupPosition).get("uom"));
                        intent.putExtra("colorCode", "#0aa0dc");
                        intent.putExtra("title", "MR");
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(DPR_MaterialRequest.this, holder.ticket, getString(R.string.transition_item));
                        ActivityCompat.startActivity(DPR_MaterialRequest.this, intent, options.toBundle());
                    }
                }
            });
            return row;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public Object getChild(int index, int stub) {
            return childItems.get(index);
        }

        @Override
        public long getChildId(int index, int stub) {
            return stub;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childItems.get(groupPosition).size();
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View row = convertView;
            row = mInflater.inflate(R.layout.dpr_mr_child_lv_row, null);
            LinearLayout paymentdays_layout = (LinearLayout) row.findViewById(R.id.paymentdays_layout);
            LinearLayout recommenddays_layout = (LinearLayout) row.findViewById(R.id.recommenddays_layout);
            SCMTextView job_name = (SCMTextView) row.findViewById(R.id.job_name);
            SCMTextView grid_name = (SCMTextView) row.findViewById(R.id.grid_name);
            SCMTextView grid_detail = (SCMTextView) row.findViewById(R.id.grid_detail);
            SCMTextView formula = (SCMTextView) row.findViewById(R.id.formula);
            SCMTextView length = (SCMTextView) row.findViewById(R.id.length);
            SCMTextView breadth = (SCMTextView) row.findViewById(R.id.breadth);
            SCMTextView depth = (SCMTextView) row.findViewById(R.id.depth);
            SCMTextView nos = (SCMTextView) row.findViewById(R.id.nos);
            SCMTextView est_qty = (SCMTextView) row.findViewById(R.id.est_qty);
            SCMTextView dpr_raised_qty = (SCMTextView) row.findViewById(R.id.dpr_raised_qty);
            SCMTextView dpr_rejected_qty = (SCMTextView) row.findViewById(R.id.dpr_rejected_qty);
            SCMTextView balance_qty = (SCMTextView) row.findViewById(R.id.balance_qty);
            SCMTextView qty = (SCMTextView) row.findViewById(R.id.qty);
            SCMTextView remarks = (SCMTextView) row.findViewById(R.id.remarks);
            SCMTextView c_sno = (SCMTextView) row.findViewById(R.id.c_sno);
            c_sno.setText(childItems.get(groupPosition).get(childPosition).get("childSno"));
            job_name.setText(childItems.get(groupPosition).get(childPosition).get("jobName"));
            grid_name.setText(childItems.get(groupPosition).get(childPosition).get("gridName"));
            grid_detail.setText(childItems.get(groupPosition).get(childPosition).get("gridDetail"));
            formula.setText(childItems.get(groupPosition).get(childPosition).get("Formula"));
            length.setText(childItems.get(groupPosition).get(childPosition).get("Length"));
            breadth.setText(childItems.get(groupPosition).get(childPosition).get("Breadth"));
            depth.setText(childItems.get(groupPosition).get(childPosition).get("Depth"));
            nos.setText(childItems.get(groupPosition).get(childPosition).get("Nos"));
            est_qty.setText(childItems.get(groupPosition).get(childPosition).get("EST"));
            dpr_raised_qty.setText(childItems.get(groupPosition).get(childPosition).get("DPRRaised"));
            dpr_rejected_qty.setText(childItems.get(groupPosition).get(childPosition).get("DPRReject"));
            balance_qty.setText(childItems.get(groupPosition).get(childPosition).get("Balance"));
            qty.setText(childItems.get(groupPosition).get(childPosition).get("Qty"));
            remarks.setText(childItems.get(groupPosition).get(childPosition).get("Remarks"));

            ChildView.put(childPosition, row);
            return row;
        }
    }

    public class labourDetailsAdapter extends BaseAdapter {
        private Context mContex;
        private LayoutInflater mInflater;

        public labourDetailsAdapter(Context context) {
            mContex = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return labourListArray.size();
        }

        @Override
        public Object getItem(int position) {
            return labourListArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final LabourViewHolder holder;
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContex
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = mInflater.inflate(R.layout.labour_details_row, null);
                holder = new LabourViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (LabourViewHolder) row.getTag();
            }
            holder.labour_type.setText(labourListArray.get(position).get("labourType"));
            holder.hours.setText(labourListArray.get(position).get("Hours"));
            holder.nop.setText(labourListArray.get(position).get("noOfPersons"));
            holder.remarks.setText(labourListArray.get(position).get("Remarks"));
            return row;
        }
    }

    public class MaterialAdapter extends BaseAdapter {
        private Context mContex;
        private LayoutInflater mInflater;
        ArrayList<HashMap<String, String>> materialArray = new ArrayList<>();

        public MaterialAdapter(Context context, ArrayList<HashMap<String, String>> materialArray) {
            mContex = context;
            mInflater = LayoutInflater.from(context);
            this.materialArray = materialArray;
        }

        @Override
        public int getCount() {
            return materialArray.size();
        }

        @Override
        public Object getItem(int position) {
            return materialArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MaterialViewHolder holder;
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContex
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = mInflater.inflate(R.layout.dpr_material_dialog_row, null);
                holder = new MaterialViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (MaterialViewHolder) row.getTag();
            }
            holder.item.setText(materialArray.get(position).get("Item"));
            holder.balance_qty.setText(materialArray.get(position).get("balanceQty"));
            holder.est_qty.setText(materialArray.get(position).get("estimatedQty"));
            holder.rec_qty.setText(materialArray.get(position).get("receivedQty"));
            holder.uom.setText(materialArray.get(position).get("UOM"));
            return row;
        }
    }

    public class MainLabourAdapter extends BaseAdapter {
        private Context mContex;
        private LayoutInflater mInflater;
        ArrayList<HashMap<String, String>> mainLabourArray = new ArrayList<>();

        public MainLabourAdapter(Context context, ArrayList<HashMap<String, String>> materialArray) {
            mContex = context;
            mInflater = LayoutInflater.from(context);
            this.mainLabourArray = materialArray;
        }

        @Override
        public int getCount() {
            return mainLabourArray.size();
        }

        @Override
        public Object getItem(int position) {
            return mainLabourArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MainLabourViewHolder holder;
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContex
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = mInflater.inflate(R.layout.dpr_labour_main_row, null);
                holder = new MainLabourViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (MainLabourViewHolder) row.getTag();
            }
            holder.labour.setText(mainLabourArray.get(position).get("labour"));
            holder.actual_qty.setText(mainLabourArray.get(position).get("actualQty"));
            holder.est_qty.setText(mainLabourArray.get(position).get("estQty"));
            holder.uom.setText(mainLabourArray.get(position).get("uom"));
            return row;
        }
    }

    public class MiscLabourAdapter extends BaseAdapter {
        private Context mContex;
        private LayoutInflater mInflater;
        ArrayList<HashMap<String, String>> miscLabourArray = new ArrayList<>();

        public MiscLabourAdapter(Context context, ArrayList<HashMap<String, String>> materialArray) {
            mContex = context;
            mInflater = LayoutInflater.from(context);
            this.miscLabourArray = materialArray;
        }

        @Override
        public int getCount() {
            return miscLabourArray.size();
        }

        @Override
        public Object getItem(int position) {
            return miscLabourArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MiscLabourViewHolder holder;
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContex
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = mInflater.inflate(R.layout.dpr_labour_misc_row, null);
                holder = new MiscLabourViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (MiscLabourViewHolder) row.getTag();
            }
            holder.labour.setText(miscLabourArray.get(position).get("labour"));
            holder.actual_qty.setText(miscLabourArray.get(position).get("actualQty"));
            holder.uom.setText(miscLabourArray.get(position).get("uom"));
            return row;
        }
    }

    public void materialDetailsDialog(String materialValue, String mainLabourValue, String miscLabourValue) {
        final LayoutInflater inflater2 = LayoutInflater.from(context);
        View convertView = inflater2.inflate(R.layout.dpr_material_dialog, null);
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(convertView);
        NestedListView materialList = (NestedListView) convertView.findViewById(R.id.material_lv);
        SCMTextView noMatrial = (SCMTextView) convertView.findViewById(R.id.no_material);
        NestedListView mainLabourList = (NestedListView) convertView.findViewById(R.id.main_labour_lv);
        SCMTextView noMainLabour = (SCMTextView) convertView.findViewById(R.id.no_main_labour);
        NestedListView miscLabourList = (NestedListView) convertView.findViewById(R.id.misc_labour_lv);
        SCMTextView noMiscLabour = (SCMTextView) convertView.findViewById(R.id.no_misc_labour);
        ImageView close = (ImageView) convertView.findViewById(R.id.close);
        ArrayList<HashMap<String, String>> materialArrayValue = new ArrayList<>();
        ArrayList<HashMap<String, String>> mainLabourArrayValue = new ArrayList<>();
        ArrayList<HashMap<String, String>> miscLabourArrayValue = new ArrayList<>();
        try {
            JSONArray materialArray = new JSONArray(materialValue);
            materialArrayValue = ApiCalls.getArraylistfromJson(materialArray.toString());
            MaterialAdapter materialAdapter = new MaterialAdapter(context, materialArrayValue);
            materialList.setAdapter(materialAdapter);

            JSONArray mainLabourArray = new JSONArray(mainLabourValue);
            mainLabourArrayValue = ApiCalls.getArraylistfromJson(mainLabourArray.toString());
            MainLabourAdapter mainLabourAdapter = new MainLabourAdapter(context, mainLabourArrayValue);
            mainLabourList.setAdapter(mainLabourAdapter);

            JSONArray miscLabourArray = new JSONArray(miscLabourValue);
            miscLabourArrayValue = ApiCalls.getArraylistfromJson(miscLabourArray.toString());
            MiscLabourAdapter miscLabourAdapter = new MiscLabourAdapter(context, miscLabourArrayValue);
            miscLabourList.setAdapter(miscLabourAdapter);

            if (materialArray.length() == 0)
                noMatrial.setVisibility(View.VISIBLE);
            if (mainLabourArray.length() == 0)
                noMainLabour.setVisibility(View.VISIBLE);
            if (miscLabourArray.length() == 0)
                noMiscLabour.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
