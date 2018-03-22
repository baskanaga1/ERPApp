package com.guruinfo.scm;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.guruinfo.scm.Indent.Indent_listview;
import com.guruinfo.scm.MIN.MINListFragment;
import com.guruinfo.scm.MINack.MINACKListFragment;
import com.guruinfo.scm.MTN.MTNListFragment;
import com.guruinfo.scm.MTRN.MTRN_ListFragment;
import com.guruinfo.scm.MaterialRequest.MaterialRequest;
import com.guruinfo.scm.PO_Bill.PO_BillListFragment;
import com.guruinfo.scm.PoBillRecommendation.PoRecommendationList;
import com.guruinfo.scm.Ticketing.TicketingViewAllActivity;
import com.guruinfo.scm.Timesheet.TimeSheetAddAndViewActivity;
import com.guruinfo.scm.WO.WOListFragment;
import com.guruinfo.scm.WORecommendation.WoRecommendationList;
import com.guruinfo.scm.WO_Bill.WOBillListViewFragment;
import com.guruinfo.scm.bmrf.BMRFListFragment;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseFragment;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.notification.NotificationViewFragment;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.utils.Sharedpref;
import com.guruinfo.scm.grn.GRNListLatestFragment;
import com.guruinfo.scm.mr.MRListFragment;
import com.guruinfo.scm.mrir.MRIRListFragment;
import com.guruinfo.scm.po.POListFragment;
import com.guruinfo.scm.tms.TMSFragmentActivity;
import com.guruinfo.scm.vmf.VMFListFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import butterknife.Bind;
import butterknife.ButterKnife;
import static com.guruinfo.scm.SCMDashboardActivityLatest.NavigationFragmentManager;
import static com.guruinfo.scm.common.utils.BaseAppCompactFragmentActivity.context;
/**
 * Created by Kannan G on 05-Apr-16.
 */
public class SCMMenuFragment1 extends BaseFragment {
    public static String TAG = "SCMMenuFragment";
    Context context;
    SessionManager session;
    static String uid, Cre_Id;
    BackgroundTask backgroundTask;
    String requestParameter;
    String countnotification = "";
    String notificationCount = "0";
    String loadRequest = "";
    View clickView;
    String dashboard = "";
    String Timesheet_rights = "";
    String Ticketing_rights = "";
    int menuColorIndex = 0;
    @Bind(R.id.gridView)
    GridView gridview;
    @Bind(R.id.error)
    TextView errorTextView;
    TextView notifiCountText;
    ArrayList<HashMap<String, String>> scmMenuArrayList;
    HashMap<String, String> scmMenuHashmap;
    ArrayList<HashMap<String, String>> scmnotification;
    HashMap<String, String> hashmap;
    private boolean approvalRights;
    String mprcount = "0", mprrights = "", mrcount = "0", mrrights = "", vmfcount = "0", vmfrights = "", pocount = "0", porights = "", Indentcount = "0", Indentrights = "", mincount = "0",
            minrights = "", grncount = "0", grnrights = "", bmrfcount = "0", bmrfrights = "", mrircount = "0", mrirrights = "", minackcount = "0", minackrights = "",
            mtnrights = "", mtncount = "0", mtrncount = "0", mtrnrights = "", pobillcount = "0", pobillrights = "", porecommdcount = "0", porecommdrights = "", wocount = "0", worights = "", wobillcount = "0", wobillrights = "", woBillreccount = "0", woBillrecrights = "";
    public static SCMMenuFragment1 newInstance(Bundle bundle) {
        SCMMenuFragment1 fragment = new SCMMenuFragment1();
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scm_menu_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        final Bundle bundle = this.getArguments();
        menuColorIndex = 0;
        if (bundle != null) {
            loadRequest = bundle.getString("loadRequest");
            dashboard = bundle.getString("dashboard");
            Timesheet_rights = bundle.getString("Timesheet_rights");
            Ticketing_rights = bundle.getString("Ticketing_rights");
            System.out.println("Dashboard objects" + dashboard);
            countnotification = bundle.getString("countnotification");
            notificationCount = Sharedpref.GetPrefString(context,AppContants.Notification.SHARED_PREF_NOTIFICATION_COUNT);
            setHasOptionsMenu(true);
            try {
                parseJSONResponse(new JSONObject(loadRequest), "LOAD_SCM_MENU");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return view;
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
       /* inflater.inflate(R.menu.action_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);*/
        ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>SCM Dashboard</font>"));
        inflater.inflate(R.menu.action_bar, menu);
        if (Timesheet_rights.equalsIgnoreCase("true"))
            menu.findItem(R.id.time_sheet).setVisible(true);
        else
            menu.findItem(R.id.time_sheet).setVisible(false);
        final MenuItem myActionMenuItem = menu.findItem(R.id.notification);
        notifiCountText = (TextView) myActionMenuItem.getActionView().findViewById(R.id.tv_counter);
        notifiCountText.setText(notificationCount);
        final View noti = (myActionMenuItem).getActionView();
        noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickView = noti;
                notifiCountText.setText("0");
                NavigationFragmentManager(NotificationViewFragment.newInstance(null), "NOTIFICATION");
                // onLoadNotification();
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
        // return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.time_sheet:
                onLoadTimeSheet();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
   /* @Override
    public void onResume() {
        Log.e("Notification Count", "Changed");
        super.onResume();
        setNotificationCount();
    }
    @Override
    public void onPause() {
        Log.e("Notification Count", "Changed");
        super.onPause();
        setNotificationCount();
    }
    public void setNotificationCount() {
        if (Sharedpref.GetPrefString(getContext(), AppContants.Notification.SHARED_PREF_NOTIFICATION_COUNT) != null) {
            if (notifiCountText != null)
                notifiCountText.setText(Sharedpref.GetPrefString(getContext(), AppContants.Notification.SHARED_PREF_NOTIFICATION_COUNT));
        }
    }*/
    public void onLoadTimeSheet() {
        requestParameter = "{'Action':'TMS_MOBILE','submode':'TMS_SEARCH','date':'','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "'}";
        getData(requestParameter, "TIMESHEET");
        Log.d(TAG, "TIMESHEET--" + requestParameter);
    }
    public void getData(final String requestParameters, final String flag) {
        backgroundTask = new BackgroundTask(context, flag, new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String values, String flagMsg) {
                try {
                    if (flagMsg.equals("internet")) {
                        showInternetDialog(context, values, requestParameters, flag);
                    } else {
                        JSONObject jsonObject = new JSONObject(values);
                        parseJSONResponse(jsonObject, flag);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        backgroundTask.execute("", "", requestParameter);
    }
    private void onLoadNotification() {
        Intent intent = new Intent(context, NotificationViewFragment.class);
        intent.putExtra("colorCode", "#2d2e3a");
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), clickView, getString(R.string.transition_item));
        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
    }
    private void parseJSONResponse(JSONObject response, String flag) {
        try {
            if (flag.equals("LOAD_SCM_MENU")) {
                scmMenuArrayList = new ArrayList<>();
                JSONArray requestArray = response.getJSONArray("Load_Request");
                for (int i = 0; i < requestArray.length(); i++) {
                    JSONObject jsonObject = requestArray.getJSONObject(i);
                    scmMenuHashmap = new HashMap<>();
                    if (jsonObject.getString("Action").equalsIgnoreCase("SCM_MPR")) {
                        scmMenuHashmap.put("Action", "MPR");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("SCM_VMF")) {
                        scmMenuHashmap.put("Action", "VMF");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("SCM_MR")) {
                        scmMenuHashmap.put("Action", "MR");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("SCM_PO")) {
                        scmMenuHashmap.put("Action", "PO");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("SCM_INDENT")) {
                        scmMenuHashmap.put("Action", "INDENT");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("SCM_GRN")) {
                        scmMenuHashmap.put("Action", "GRN");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("SCM_MIN")) {
                        scmMenuHashmap.put("Action", "MIN");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("SCM_BMRF")) {
                        scmMenuHashmap.put("Action", "BMRF");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("SCM_MRIR")) {
                        scmMenuHashmap.put("Action", "MRIR");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("SCM_MIN_ACKNOWLEDGE")) {
                        scmMenuHashmap.put("Action", "MIN ACK");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("TMS")) {
                        scmMenuHashmap.put("Action", "TMS");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("SCM_MTN")) {
                        scmMenuHashmap.put("Action", "MTN");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("SCM_MTRN")) {
                        scmMenuHashmap.put("Action", "MTRN");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("SCM_POBILL_INVOICE")) {
                        scmMenuHashmap.put("Action", "PO BILL INVOICE");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("SCM_POBILL")) {
                        scmMenuHashmap.put("Action", "PO BILL RECOMM");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("SCM_WORK_ORDER")) {
                        scmMenuHashmap.put("Action", "WO");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("SCM_WORK_ORDER_INVOICE")) {
                        scmMenuHashmap.put("Action", "WO BILL INVOICE");
                    } else if (jsonObject.getString("Action").equalsIgnoreCase("SCM_WORK_ORDER_RECOMMENDATION")) {
                        scmMenuHashmap.put("Action", "WO BILL RECOMM");
                    }
                    //else if (param.equalsIgnoreCase("TICKETING")) {
                    if (scmMenuHashmap.size() > 0)
                        scmMenuArrayList.add(scmMenuHashmap);
                }
                if (Ticketing_rights.equalsIgnoreCase("true")) {
                    scmMenuHashmap = new HashMap<>();
                    scmMenuHashmap.put("Action", "TICKETING");
                    scmMenuArrayList.add(scmMenuHashmap);
                }
                // scmMenuArrayList = ApiCalls.getArraylistfromJson(response.getJSONArray("scmMenus").toString());
                if (scmMenuArrayList.size() == 0) {
                    gridview.setVisibility(View.GONE);
                    errorTextView.setVisibility(View.VISIBLE);
                } else {
                    errorTextView.setVisibility(View.GONE);
                    gridview.setVisibility(View.VISIBLE);
                    MenuAdapter adapter = new MenuAdapter(context, scmMenuArrayList);
                    gridview.setAdapter(adapter);
                }
        /*        parseJSONResponse(new JSONObject(countnotification), "COUNT_NOTIFICATION");
            }
            else if(flag.equalsIgnoreCase("COUNT_NOTIFICATION")) {*/
                scmnotification = new ArrayList<>();
                //JSONArray requestArray1 = response.getJSONArray("CountNotification");
                JSONObject countnotificationobject = new JSONObject(countnotification);
                JSONArray requestArray1 = countnotificationobject.getJSONArray("CountNotification");
                for (int i = 0; i < requestArray1.length(); i++) {
                    hashmap = new HashMap<>();
                    JSONObject innerobject = requestArray1.getJSONObject(i);
                    hashmap.put("Name", innerobject.getString("Name"));
                    // hashmap.put("notificationCount", innerobject.getString("notificationCount"));
                    JSONObject approvalRightsJSONObject = innerobject.getJSONObject("Approval_Rights");
                    String approvalvalue = "";
                    try {
                        approvalvalue = approvalRightsJSONObject.getString("0");
                        hashmap.put("approvalvalue", approvalvalue);
                        hashmap.put("notificationCount", "0");
                        // System.out.println(approvalvalue);
                    } catch (Exception e) {
                        approvalvalue = approvalRightsJSONObject.getString("1");
                        hashmap.put("approvalvalue", approvalvalue);
                        System.out.println(approvalvalue);
                        hashmap.put("notificationCount", innerobject.getString("notificationCount"));
                        String approvalcount = innerobject.getString("notificationCount");
                        //System.out.println(approvalcount);
                    }
                    // hashmap.put("")
                    scmnotification.add(hashmap);
                    String counts = innerobject.getString("Name");
                    //    System.out.println("COUNT: " + counts);
                }//for end
                for (int j = 0; j < scmnotification.size(); j++) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) scmnotification.get(j);
                    if (hashMap.get("Name").equalsIgnoreCase("MPR")) {
                        // Log.e("matched", hashMap.get("Name"));
                        mprcount = hashMap.get("notificationCount").toString();
                        mprrights = hashMap.get("approvalvalue").toString();
                    } else if (hashMap.get("Name").equalsIgnoreCase("VMF")) {
                        //Log.e("matched", hashMap.get("Name"));
                        vmfcount = hashMap.get("notificationCount").toString();
                        vmfrights = hashMap.get("approvalvalue").toString();
                    } else if (hashMap.get("Name").equalsIgnoreCase("MR")) {
                        //Log.e("matched", hashMap.get("Name"));
                        mrcount = hashMap.get("notificationCount").toString();
                        mrrights = hashMap.get("approvalvalue").toString();
                    } else if (hashMap.get("Name").equalsIgnoreCase("PO")) {
                        // Log.e("matched", hashMap.get("Name"));
                        pocount = hashMap.get("notificationCount").toString();
                        porights = hashMap.get("approvalvalue").toString();
                    } else if (hashMap.get("Name").equalsIgnoreCase("Indent")) {
                        Log.e("matched", hashMap.get("Name"));
                        Indentcount = hashMap.get("notificationCount").toString();
                        Indentrights = hashMap.get("approvalvalue").toString();
                    } else if (hashMap.get("Name").equalsIgnoreCase("MIN")) {
                        // Log.e("matched", hashMap.get("Name"));
                        mincount = hashMap.get("notificationCount").toString();
                        minrights = hashMap.get("approvalvalue").toString();
                    } else if (hashMap.get("Name").equalsIgnoreCase("GRN")) {
                        //Log.e("matched", hashMap.get("Name"));
                        grncount = hashMap.get("notificationCount").toString();
                        grnrights = hashMap.get("approvalvalue").toString();
                    } else if (hashMap.get("Name").equalsIgnoreCase("BMRF")) {
                        //Log.e("matched", hashMap.get("Name"));
                        bmrfcount = hashMap.get("notificationCount").toString();
                        bmrfrights = hashMap.get("approvalvalue").toString();
                    } else if (hashMap.get("Name").equalsIgnoreCase("MRIR")) {
                        //  Log.e("matched", hashMap.get("Name"));
                        mrircount = hashMap.get("notificationCount").toString();
                        mrirrights = hashMap.get("approvalvalue").toString();
                    } else if (hashMap.get("Name").equalsIgnoreCase("MIN_ACK")) {
                        // Log.e("matched", hashMap.get("Name"));
                        minackcount = hashMap.get("notificationCount").toString();
                        minackrights = hashMap.get("approvalvalue").toString();
                    } else if (hashMap.get("Name").equalsIgnoreCase("MTRN")) {
                        // Log.e("matched", hashMap.get("Name"));
                        mtrncount = hashMap.get("notificationCount").toString();
                        System.out.println("mtrncount:" + mtrncount);
                        mtrnrights = hashMap.get("approvalvalue").toString();
                    } else if (hashMap.get("Name").equalsIgnoreCase("MTN")) {
                        // Log.e("matched", hashMap.get("Name"));
                        mtncount = hashMap.get("notificationCount").toString();
                        mtnrights = hashMap.get("approvalvalue").toString();
                    } else if (hashMap.get("Name").equalsIgnoreCase("POBILL")) {
                        // Log.e("matched", hashMap.get("Name"));
                        pobillcount = hashMap.get("notificationCount").toString();
                        pobillrights = hashMap.get("approvalvalue").toString();
                    } else if (hashMap.get("Name").equalsIgnoreCase("MAT_POBILL_RECOMMEND")) {
                        porecommdcount = hashMap.get("notificationCount").toString();
                        porecommdrights = hashMap.get("approvalvalue").toString();
                    } else if (hashMap.get("Name").equalsIgnoreCase("WO")) {
                        wocount = hashMap.get("notificationCount").toString();
                        worights = hashMap.get("approvalvalue").toString();
                    } else if (hashMap.get("Name").equalsIgnoreCase("WOBILL")) {
                        wobillcount = hashMap.get("notificationCount").toString();
                        wobillrights = hashMap.get("approvalvalue").toString();
                    } else if (hashMap.get("Name").equalsIgnoreCase("WO_BILL_RECOMM_APPROVAL")) {
                        woBillreccount = hashMap.get("notificationCount").toString();
                        woBillrecrights = hashMap.get("approvalvalue").toString();
                    }
                }//innerfar end
/*
                JSONArray dashboardrequestArray = response.getJSONArray("Load_Request");
                for (int i = 0; i < requestArray.length(); i++) {
                    JSONObject jsonObject = requestArray.getJSONObject(i);*/
            } else if (flag.equalsIgnoreCase("TIMESHEET")) {
                Intent intent = new Intent(context, TimeSheetAddAndViewActivity.class);
                intent.putExtra("loadValues", response.toString());
                System.out.println("lOADVALUES" + response.toString());
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showInternetDialog(Context activity, String err_msg, final String req, final String flag) {
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
                //android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder1.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                getData(req, flag);
            }
        });
        try {
            final AlertDialog alert11 = builder1.create();
            alert11.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button btnPositive = alert11.getButton(Dialog.BUTTON_POSITIVE);
                    btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.Filter_textsize));
                    TextView textView = (TextView) alert11.findViewById(android.R.id.message);
                    if (context.getResources().getBoolean(R.bool.isTablet)) {
                        textView.setTextSize(25);
                    } else {
                        textView.setTextSize(16);
                    }
                }
            });
            alert11.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class ViewHolder {
        TextView labelTextView, count;
        RelativeLayout mainLayout;
        ImageView menuIcon;
    }
    //->BaseAdapter
    public class MenuAdapter extends BaseAdapter {
        Context context;
        ArrayList<HashMap<String, String>> arraylist;
        public MenuAdapter(Context context, ArrayList<HashMap<String, String>> arraylist) {
            this.context = context;
            this.arraylist = arraylist;
        }
        @Override
        public int getCount() {
            return arraylist.size();
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
            View row = convertView;
            final ViewHolder holder;
            if (row == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //row = mInflater.inflate(R.layout.dashboardgrid1, null);
                //row = mInflater.inflate(R.layout.dashboard_grid_item, null);
                row = mInflater.inflate(R.layout.dashboard_grid_item, null);
                holder = new ViewHolder();
                holder.labelTextView = (TextView) row.findViewById(R.id.main_btn_offers);
                holder.mainLayout = (RelativeLayout) row.findViewById(R.id.main);
                holder.menuIcon = (ImageView) row.findViewById(R.id.icon);
                holder.count = (TextView) row.findViewById(R.id.count);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }
            //USER_LOGGED_IN
          /*  Boolean staus = Sharedpref.getPrefBoolean(context, "USER_LOGGED_IN");
            System.out.println(staus.toString());*/
           /* for (int i = 0; i < scmnotification.size(); i++)
            {
                HashMap<String, String> hashMap = (HashMap<String, String>) scmnotification.get(i);
                if (hashMap.get("Name").equalsIgnoreCase("MPR")) {
                    Log.e("matched", hashMap.get("Name"));
                    mprcount = hashMap.get("notificationCount").toString();
                    mprrights = hashMap.get("approvalvalue").toString();
                } else if (hashMap.get("Name").equalsIgnoreCase("VMF")) {
                    Log.e("matched", hashMap.get("Name"));
                    vmfcount = hashMap.get("notificationCount").toString();
                    vmfrights = hashMap.get("approvalvalue").toString();
                } else if (hashMap.get("Name").equalsIgnoreCase("MR")) {
                    Log.e("matched", hashMap.get("Name"));
                    mrcount = hashMap.get("notificationCount").toString();
                    mrrights = hashMap.get("approvalvalue").toString();
                } else if (hashMap.get("Name").equalsIgnoreCase("PO")) {
                    Log.e("matched", hashMap.get("Name"));
                    pocount = hashMap.get("notificationCount").toString();
                    porights = hashMap.get("approvalvalue").toString();
                } else if (hashMap.get("Name").equalsIgnoreCase("Indent")) {
                    Log.e("matched", hashMap.get("Name"));
                    Indentcount = hashMap.get("notificationCount").toString();
                    Indentrights = hashMap.get("approvalvalue").toString();
                } else if (hashMap.get("Name").equalsIgnoreCase("MIN")) {
                    Log.e("matched", hashMap.get("Name"));
                    mincount = hashMap.get("notificationCount").toString();
                    minrights = hashMap.get("approvalvalue").toString();
                } else if (hashMap.get("Name").equalsIgnoreCase("GRN")) {
                    Log.e("matched", hashMap.get("Name"));
                    grncount = hashMap.get("notificationCount").toString();
                    grnrights = hashMap.get("approvalvalue").toString();
                } else if (hashMap.get("Name").equalsIgnoreCase("BMRF")) {
                    Log.e("matched", hashMap.get("Name"));
                    bmrfcount = hashMap.get("notificationCount").toString();
                    bmrfrights = hashMap.get("approvalvalue").toString();
                } else if (hashMap.get("Name").equalsIgnoreCase("MRIR")) {
                    Log.e("matched", hashMap.get("Name"));
                    mrircount = hashMap.get("notificationCount").toString();
                    mrirrights = hashMap.get("approvalvalue").toString();
                } else if (hashMap.get("Name").equalsIgnoreCase("MIN_ACK")) {
                    Log.e("matched", hashMap.get("Name"));
                    minackcount = hashMap.get("notificationCount").toString();
                    minackrights = hashMap.get("approvalvalue").toString();
                }
            }*/
            holder.labelTextView.setText(arraylist.get(position).get("Action"));
            if (arraylist.get(position).get("Action").equalsIgnoreCase("MPR")) {
                // holder.labelTextView.setText(remundscr(arraylist.get(position).get("Action").substring(0, 1).toUpperCase() + arraylist.get(position).get("Action").substring(1).toLowerCase()));
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (mprrights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(mprcount);
                }
                if (mprcount.equalsIgnoreCase("0")) {
                    //  holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (mprrights.equalsIgnoreCase("FALSE")) {
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("VMF")) {
                //  holder.labelTextView.setText(remundscr(arraylist.get(position).get("Action").substring(0, 1).toUpperCase() + arraylist.get(position).get("Action").substring(2).toLowerCase()));
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (vmfrights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(vmfcount);
                }
                if (vmfcount.equalsIgnoreCase("0")) {
                    // holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (vmfrights.equalsIgnoreCase("FALSE")) {
                    //holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("MR")) {
                //  holder.labelTextView.setText(remundscr(arraylist.get(position).get("Action").substring(0, 1).toUpperCase() + arraylist.get(position).get("Action").substring(2).toLowerCase()));
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (mrrights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(mrcount);
                }
                if (mrcount.equalsIgnoreCase("0")) {
                    // holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (mrrights.equalsIgnoreCase("FALSE")) {
                    // holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("PO")) {
                //  holder.labelTextView.setText(remundscr(arraylist.get(position).get("Action").substring(0, 1).toUpperCase() + arraylist.get(position).get("Action").substring(2).toLowerCase()));
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (porights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(pocount);
                }
                if (pocount.equalsIgnoreCase("0")) {
                    //   holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (porights.equalsIgnoreCase("FALSE")) {
                    //holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("GRN")) {
                //  holder.labelTextView.setText(remundscr(arraylist.get(position).get("Action").substring(0, 1).toUpperCase() + arraylist.get(position).get("Action").substring(2).toLowerCase()));
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (grnrights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(grncount);
                }
                if (grncount.equalsIgnoreCase("0")) {
                    //  holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (grnrights.equalsIgnoreCase("FALSE")) {
                    //holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("INDENT")) {
                //  holder.labelTextView.setText(remundscr(arraylist.get(position).get("Action").substring(0, 1).toUpperCase() + arraylist.get(position).get("Action").substring(2).toLowerCase()));
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (Indentrights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(Indentcount);
                }
                if (Indentcount.equalsIgnoreCase("0")) {
                    //   holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (Indentrights.equalsIgnoreCase("FALSE")) {
                    // holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("MIN")) {
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (minrights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(mincount);
                }
                if (mincount.equalsIgnoreCase("0")) {
                    //   holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (minrights.equalsIgnoreCase("FALSE")) {
                    // holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("BMRF")) {
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (bmrfrights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(bmrfcount);
                }
                if (bmrfcount.equalsIgnoreCase("0")) {
                    // holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (bmrfrights.equalsIgnoreCase("FALSE")) {
                    //holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("MRIR")) {
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (mrirrights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(mrircount);
                }
                if (mrircount.equalsIgnoreCase("0")) {
                    // holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (mrirrights.equalsIgnoreCase("FALSE")) {
                    // holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("MIN ACK")) {
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (minackrights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(minackcount);
                    //   holder.count.setText("999");
                }
                if (minackcount.equalsIgnoreCase("0")) {
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (minackrights.equalsIgnoreCase("FALSE")) {
                    //holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("MTN")) {
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (mtnrights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(mtncount);
                    //   holder.count.setText("999");
                }
                if (mtncount.equalsIgnoreCase("0")) {
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (mtnrights.equalsIgnoreCase("FALSE")) {
                    //holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("MTRN")) {
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (mtrnrights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(mtrncount);
                    //   holder.count.setText("999");
                }
                if (mtrncount.equalsIgnoreCase("0")) {
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (mtrnrights.equalsIgnoreCase("FALSE")) {
                    //holder.count.setVisibility(View.GONE);
                    holder.count.setVisibility(View.INVISIBLE);
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("PO BILL INVOICE")) {
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (pobillrights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(pobillcount);
                }
                if (pobillcount.equalsIgnoreCase("0")) {
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (pobillrights.equalsIgnoreCase("FALSE")) {
                    holder.count.setVisibility(View.INVISIBLE);
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("PO BILL RECOMM")) {
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (porecommdrights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(porecommdcount);
                }
                if (porecommdcount.equalsIgnoreCase("0")) {
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (porecommdrights.equalsIgnoreCase("FALSE")) {
                    holder.count.setVisibility(View.INVISIBLE);
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("WO")) {
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (worights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(wocount);
                }
                if (wocount.equalsIgnoreCase("0")) {
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (worights.equalsIgnoreCase("FALSE")) {
                    holder.count.setVisibility(View.INVISIBLE);
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("WO BILL INVOICE")) {
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (wobillrights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(wobillcount);
                }
                if (wobillcount.equalsIgnoreCase("0")) {
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (wobillrights.equalsIgnoreCase("FALSE")) {
                    holder.count.setVisibility(View.INVISIBLE);
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("WO BILL RECOMM")) {
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                if (woBillrecrights.equalsIgnoreCase("TRUE")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(woBillreccount);
                }
                if (woBillreccount.equalsIgnoreCase("0")) {
                    holder.count.setVisibility(View.INVISIBLE);
                } else if (woBillrecrights.equalsIgnoreCase("FALSE")) {
                    holder.count.setVisibility(View.INVISIBLE);
                }
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("TICKETING")) {
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                holder.count.setVisibility(View.INVISIBLE);
            } else if (arraylist.get(position).get("Action").equalsIgnoreCase("TMS")) {
                setImage(holder.menuIcon, holder.mainLayout, arraylist.get(position).get("Action"));
                holder.count.setVisibility(View.INVISIBLE);
            }
            if (menuColorIndex >= 11) {
                menuColorIndex = 0;
            } else {
                menuColorIndex = menuColorIndex + 1;
            }
            holder.mainLayout.setBackground(getResources().getDrawable(colorArray()[menuColorIndex]));
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    ClickLink(arraylist.get(position).get("Action"));
                }
            });
            return row;
        }
        public String remundscr(String res) {
            return res.replaceAll("_", " ");
        }
    }
    //Click Links for Dynamic View of Dashboard
    public void ClickLink(String param) {
        if (!((param.equalsIgnoreCase("TICKETING")) || (param.equalsIgnoreCase("TMS")))) {
        }
        if (param.equalsIgnoreCase("MPR")) {
            //navigateToNextFragment(MRListFragment.class.getName(), null);
            int count = (Integer.parseInt(mprcount));
            if (count > 0 && (mprrights.equalsIgnoreCase("true"))) {
                Bundle bundle = new Bundle();
                String req = "{'Action':'MRALL_PROCESS','submode':'MRALL_APPROVAL_SEARCH','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','search':'" + 0 + "','Mat_Req_ProjName':'','Mat_Req_Id':'','Mat_Req_RefNo':'','Mat_Req_StoreName':'','Mat_Req_Contractor':'','Mat_Req_ReqBy':'','Mat_Req_FromDate':'','Mat_Req_ToDate':'','Mat_Req_Status':'','Mat_Req_StageName':'','Mat_Req_IOW':'','Mat_Req_Material':'','page':' 1 '}";
                bundle.putString("load", req);
                bundle.putString("IsApproval", "true");
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>MPR APPROVAL</font>"));
                NavigationFragmentManager(MRListFragment.newInstance(bundle), "MPR APPROVAL");
            } else {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>MPR</font>"));
                NavigationFragmentManager(MRListFragment.newInstance(null), "MPR");
            }
        } else if (param.equalsIgnoreCase("VMF")) {
            int count = (Integer.parseInt(vmfcount));
            if (count > 0 && (vmfrights.equalsIgnoreCase("true"))) {
                Bundle bundle = new Bundle();
                String req = "{'Action':'VEHICLE_MOMENT','submode':'VEHICLE_MOMENT_APPROVAL_LIST','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','search':'1','veh_project':'','mode_of_transport':'','mode_of_move':'','trans_name':'','vechicle_no':'','drivers_name':'','security_ref_no':'','in_time':'','out_time':'','contact_no':'','vendor_name':'','is_vendor_type':'','page':' 1 '}";
                bundle.putString("load", req);
                bundle.putString("IsApproval", "true");
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>VMF APPROVAL</font>"));
                NavigationFragmentManager(VMFListFragment.newInstance(bundle), "VMF APPROVAL");
            } else {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>VMF</font>"));
                NavigationFragmentManager(VMFListFragment.newInstance(null), "VMF");
            }
        } else if (param.equalsIgnoreCase("MR")) {
            int count = (Integer.parseInt(mrcount));
            if (count > 0 && (mrrights.equalsIgnoreCase("true"))) {
                Bundle bundle = new Bundle();
                String req = "{'Action':'MR_PROCESS','submode':'MR_APPROVAL_SEARCH','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','search':'" + 0 + "','_Mat_Req_MIR_ProjName':'','_Mat_Req_MIR_MIRId':'','_Mat_Req_MIR_MRId':'','_Mat_Req_MIR_MRRefNo':'','_Mat_Req_MIR_MRFromDate':'','_Mat_Req_MIR_MRToDate':'','_Mat_Req_MIR_MIRRefNo':'','_Mat_Req_MIR_MIRFromDate':'','_Mat_Req_MIR_MIRToDate':'','_Mat_Req_MIR_StoreName':'','_Mat_Req_MIR_Contractor':'','_Mat_Req_MIR_MIRReqBy':'','_Mat_Req_MIR_StageName':'','_Mat_Req_MIR_IOW':'','_Mat_Req_MIR_Material':'','_Mat_Req_MIR_MIRStatus':'','page':' 1 '}";
                bundle.putString("load", req);
                bundle.putString("IsApproval", "true");
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>MR APPROVAL</font>"));
                NavigationFragmentManager(MaterialRequest.newInstance(bundle), "MR APPROVAL");
            } else {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>MR</font>"));
                NavigationFragmentManager(MaterialRequest.newInstance(null), "MR");
            }
        } else if (param.equalsIgnoreCase("PO")) {
            int count = (Integer.parseInt(pocount));
            if (count > 0 && (porights.equalsIgnoreCase("true"))) {
                Bundle bundle = new Bundle();
                String req = "{'Action':'PO_LIST_PROCESS','submode':'PO_APPROVAL_SEARCH','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','search':'" + 0 + "','_Mat_Req_MIR_ProjName':'','_Mat_Req_MIR_MIRId':'','_Mat_Req_MIR_MRId':'','_Mat_Req_MIR_MRRefNo':'','_Mat_Req_MIR_MRFromDate':'','_Mat_Req_MIR_MRToDate':'','_Mat_Req_MIR_MIRRefNo':'','_Mat_Req_MIR_MIRFromDate':'','_Mat_Req_MIR_MIRToDate':'','_Mat_Req_MIR_StoreName':'','_Mat_Req_MIR_Contractor':'','_Mat_Req_MIR_MIRReqBy':'','_Mat_Req_MIR_StageName':'','_Mat_Req_MIR_IOW':'','_Mat_Req_MIR_Material':'','_Mat_Req_MIR_MIRStatus':'','page':' 1 '}";
                bundle.putString("load", req);
                bundle.putString("IsApproval", "true");
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>PO</font>"));
                NavigationFragmentManager(POListFragment.newInstance(bundle), "PO");
            } else {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>PO APPROVAL</font>"));
                NavigationFragmentManager(POListFragment.newInstance(null), "PO APPROVAL");
            }
        } else if (param.equalsIgnoreCase("GRN")) {
            int count = (Integer.parseInt(grncount));
            if (count > 0 && (grnrights.equalsIgnoreCase("true"))) {
                Bundle approvalbundle = new Bundle();
                String req = "{'Action':'GRN_LIST_PROCESS','submode':'GRN_APPROVAL_SEARCH','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','search':'" + 0 + "','_Mat_Req_MIR_ProjName':'','_Mat_Req_MIR_MIRId':'','_Mat_Req_MIR_MRId':'','_Mat_Req_MIR_MRRefNo':'','_Mat_Req_MIR_MRFromDate':'','_Mat_Req_MIR_MRToDate':'','_Mat_Req_MIR_MIRRefNo':'','_Mat_Req_MIR_MIRFromDate':'','_Mat_Req_MIR_MIRToDate':'','_Mat_Req_MIR_StoreName':'','_Mat_Req_MIR_Contractor':'','_Mat_Req_MIR_MIRReqBy':'','_Mat_Req_MIR_StageName':'','_Mat_Req_MIR_IOW':'','_Mat_Req_MIR_Material':'','_Mat_Req_MIR_MIRStatus':'','page':' 1 ','mobileDevice':'mobile'}";
                approvalbundle.putString("load", req);
                approvalbundle.putString("IsApproval", "true");
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>GRN APPROVAL</font>"));
                NavigationFragmentManager(GRNListLatestFragment.newInstance(approvalbundle), "GRN APPROVAL");
            } else {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>GRN</font>"));
                NavigationFragmentManager(GRNListLatestFragment.newInstance(null), "GRN");
            }
        } else if (param.equalsIgnoreCase("INDENT")) {
            int count = (Integer.parseInt(Indentcount));
            if (count > 0 && (Indentrights.equalsIgnoreCase("true"))) {
                Bundle bundle = new Bundle();
                String req = "{'Action':'INDENT_LIST_PROCESS','submode':'INDENT_APPROVAL_SEARCH','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','search':'" + 0 + "','page':' 1 ','mobileDevice':'mobile'}";
                bundle.putString("load", req);
                bundle.putString("IsApproval", "true");
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>INDENT APPROVAL</font>"));
                NavigationFragmentManager(Indent_listview.newInstance(bundle), "INDENT APPROVAL");
            } else {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>INDENT</font>"));
                NavigationFragmentManager(Indent_listview.newInstance(null), "INDENT");
            }
        } else if (param.equalsIgnoreCase("MIN")) {
            int count = (Integer.parseInt(mincount));
            if (count > 0 && (minrights.equalsIgnoreCase("true"))) {
                Bundle bundle = new Bundle();
                String req = "{'Action':'MIN_LIST_PROCESS','submode':'MIN_APPROVAL_SEARCH','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','search':'" + 0 + "','page':' 1 ','mobileDevice':'mobile'}";
                bundle.putString("load", req);
                bundle.putString("IsApproval", "true");
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>MIN APPROVAL</font>"));
                NavigationFragmentManager(MINListFragment.newInstance(bundle), "MIN APPROVAL");
            } else {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>MIN</font>"));
                NavigationFragmentManager(MINListFragment.newInstance(null), "MIN");
            }
        } else if (param.equalsIgnoreCase("BMRF")) {
            int count = (Integer.parseInt(bmrfcount));
            if (count > 0 && (bmrfrights.equalsIgnoreCase("true"))) {
                Bundle bundle = new Bundle();
                String req = "{'Action':'BMRF_LIST_PROCESS','submode':'BMRF_APPROVAL_SEARCH','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','search':' 1','page':' 1 ','mobileDevice':'mobile'}";
                bundle.putString("load", req);
                bundle.putString("IsApproval", "true");
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>BMRF APPROVAL</font>"));
                NavigationFragmentManager(BMRFListFragment.newInstance(bundle), "BMRF APPROVAL");
            } else {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>BMRF</font>"));
                NavigationFragmentManager(BMRFListFragment.newInstance(null), "BMRF");
            }
        } else if (param.equalsIgnoreCase("MRIR")) {
            int count = (Integer.parseInt(mrircount));
            if (count > 0 && (mrirrights.equalsIgnoreCase("true"))) {
                Bundle bundle = new Bundle();
                String req = "{'Action':'MRIR_LIST_PROCESS','submode':'MRIR_APPROVAL_SEARCH','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','search':'" + 0 + "','_Mat_Req_MIR_ProjName':'','_Mat_Req_MIR_MIRId':'','_Mat_Req_MIR_MRId':'','_Mat_Req_MIR_MRRefNo':'','_Mat_Req_MIR_MRFromDate':'','_Mat_Req_MIR_MRToDate':'','_Mat_Req_MIR_MIRRefNo':'','_Mat_Req_MIR_MIRFromDate':'','_Mat_Req_MIR_MIRToDate':'','_Mat_Req_MIR_StoreName':'','_Mat_Req_MIR_Contractor':'','_Mat_Req_MIR_MIRReqBy':'','_Mat_Req_MIR_StageName':'','_Mat_Req_MIR_IOW':'','_Mat_Req_MIR_Material':'','_Mat_Req_MIR_MIRStatus':'','page':' 1 ','mobileDevice':'mobile'}";
                bundle.putString("load", req);
                bundle.putString("IsApproval", "true");
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>MRIR APPROVAL</font>"));
                NavigationFragmentManager(MRIRListFragment.newInstance(bundle), "MRIR APPROVAL");
            } else {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>MRIR</font>"));
                NavigationFragmentManager(MRIRListFragment.newInstance(null), "MRIR");
            }
        } else if (param.equalsIgnoreCase("MIN ACK")) {
            NavigationFragmentManager(MINACKListFragment.newInstance(null), "MIN ACK");
        } else if (param.equalsIgnoreCase("TICKETING")) {
            Intent intent = new Intent(getActivity(), TicketingViewAllActivity.class);
            startActivity(intent);
        } else if (param.equalsIgnoreCase("TMS")) {
            Intent intent = new Intent(getActivity(), TMSFragmentActivity.class);
            startActivity(intent);
        } else if (param.equalsIgnoreCase("MTN")) {
            int count = (Integer.parseInt(mtncount));
            if (count > 0 && (mtnrights.equalsIgnoreCase("true"))) {
                Bundle bundle = new Bundle();
                String req = "{'Action':'MATERIAL_TRANSFER_NOTE','submode':'MTN_SEARCH_APPROVAL','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','_search':'" + 0 + "','_Mat_Req_MIR_ProjName':'','_Mat_Req_MIR_MIRId':'','_Mat_Req_MIR_MRId':'','_Mat_Req_MIR_MRRefNo':'','_Mat_Req_MIR_MRFromDate':'','_Mat_Req_MIR_MRToDate':'','_Mat_Req_MIR_MIRRefNo':'','_Mat_Req_MIR_MIRFromDate':'','_Mat_Req_MIR_MIRToDate':'','_Mat_Req_MIR_StoreName':'','_Mat_Req_MIR_Contractor':'','_Mat_Req_MIR_MIRReqBy':'','_Mat_Req_MIR_StageName':'','_Mat_Req_MIR_IOW':'','_Mat_Req_MIR_Material':'','_Mat_Req_MIR_MIRStatus':'','page':'1'}";
                bundle.putString("load", req);
                bundle.putString("IsApproval", "true");
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>MTN APPROVAL</font>"));
                NavigationFragmentManager(MTNListFragment.newInstance(bundle), "MTN APPROVAL");
            } else {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>MTN</font>"));
                NavigationFragmentManager(MTNListFragment.newInstance(null), "MTN");
            }
        } else if (param.equalsIgnoreCase("MTRN")) {
            int count = (Integer.parseInt(mtrncount));
            if (count > 0 && (mtrnrights.equalsIgnoreCase("true"))) {
                Bundle bundle = new Bundle();
                String req = "{'Action':'MATERIAL_TRANSFER_REQUEST_NOTE','submode':'MTRN_SEARCH_APPROVAL','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','_search':'" + 0 + "','_Mat_Req_MIR_ProjName':'','_Mat_Req_MIR_MIRId':'','_Mat_Req_MIR_MRId':'','_Mat_Req_MIR_MRRefNo':'','_Mat_Req_MIR_MRFromDate':'','_Mat_Req_MIR_MRToDate':'','_Mat_Req_MIR_MIRRefNo':'','_Mat_Req_MIR_MIRFromDate':'','_Mat_Req_MIR_MIRToDate':'','_Mat_Req_MIR_StoreName':'','_Mat_Req_MIR_Contractor':'','_Mat_Req_MIR_MIRReqBy':'','_Mat_Req_MIR_StageName':'','_Mat_Req_MIR_IOW':'','_Mat_Req_MIR_Material':'','_Mat_Req_MIR_MIRStatus':'','page':'1','IsapprovalSearch':'true'}";
                bundle.putString("load", req);
                bundle.putString("IsApproval", "true");
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>MTRN APPROVAL</font>"));
                NavigationFragmentManager(MTRN_ListFragment.newInstance(bundle), "MTRN APPROVAL");
            } else {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>MTRN</font>"));
                NavigationFragmentManager(MTRN_ListFragment.newInstance(null), "MTRN");
            }
        } else if (param.equalsIgnoreCase("PO BILL INVOICE")) {
            int count = (Integer.parseInt(pobillcount));
            if (count > 0 && (pobillrights.equalsIgnoreCase("true"))) {
                Bundle bundle = new Bundle();
                String req = "{'Action':'PO_BILL_INVOICE','submode':'PO_BILL_APPROVAL_SEARCH','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','search':'" + 0 + "','RS':' 10 ','IsapprovalSearch':'true'}";
                bundle.putString("load", req);
                bundle.putString("IsApproval", "true");
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>PO BILL INVOICE APPROVAL</font>"));
                NavigationFragmentManager(PO_BillListFragment.newInstance(bundle), "PO BILL INVOICE APPROVAL");
            } else {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>PO BILL INVOICE</font>"));
                NavigationFragmentManager(PO_BillListFragment.newInstance(null), "PO BILL INVOICE");
            }
        } else if (param.equalsIgnoreCase("PO BILL RECOMM")) {
            int count = (Integer.parseInt(porecommdcount));
            if (count > 0 && (porecommdrights.equalsIgnoreCase("true"))) {
                Bundle bundle = new Bundle();
                String req = "{'Action':'PO_BILL_RECOMMENDATION','submode':'PO_BILL_APPROVAL_SEARCH','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','search':'" + 0 + "','RS':' 10 ','IsapprovalSearch':'true'}";
                bundle.putString("load", req);
                bundle.putString("IsApproval", "true");
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>PO BILL RECOMMENDATION APPROVAL</font>"));
                NavigationFragmentManager(PoRecommendationList.newInstance(bundle), "PO BILL RECOMMENDATION APPROVAL");
            } else {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>PO BILL RECOMMENDATION</font>"));
                NavigationFragmentManager(PoRecommendationList.newInstance(null), "PO BILL RECOMMENDATION");
            }
        } else if (param.equalsIgnoreCase("WO")) {
            int count = (Integer.parseInt(wocount));
            if (count > 0 && (worights.equalsIgnoreCase("true"))) {
                Bundle bundle = new Bundle();
                String req = "{'Action':'WORK_ORDER','submode':'WORK_ORDER_APPROVAL_SEARCH','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','search':'" + 0 + "','_Mat_Req_MIR_ProjName':'','_Mat_Req_MIR_MIRId':'','_Mat_Req_MIR_MRId':'','_Mat_Req_MIR_MRRefNo':'','_Mat_Req_MIR_MRFromDate':'','_Mat_Req_MIR_MRToDate':'','_Mat_Req_MIR_MIRRefNo':'','_Mat_Req_MIR_MIRFromDate':'','_Mat_Req_MIR_MIRToDate':'','_Mat_Req_MIR_StoreName':'','_Mat_Req_MIR_Contractor':'','_Mat_Req_MIR_MIRReqBy':'','_Mat_Req_MIR_StageName':'','_Mat_Req_MIR_IOW':'','_Mat_Req_MIR_Material':'','_Mat_Req_MIR_MIRStatus':'','page':' 1 ','FrmNameCtl':'WO','WO_search':'0','FrmNameCtl':'WO','WO_search':'0','RS':' 10 '}";
                bundle.putString("load", req);
                bundle.putString("IsApproval", "true");
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>WO</font>"));
                NavigationFragmentManager(WOListFragment.newInstance(bundle), "WO APPROVAL");
            } else {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>WO APPROVAL</font>"));
                NavigationFragmentManager(WOListFragment.newInstance(null), "WO");
            }
        } else if (param.equalsIgnoreCase("WO BILL INVOICE")) {
            int count = (Integer.parseInt(wobillcount));
            if (count > 0 && (wobillrights.equalsIgnoreCase("true"))) {
                Bundle bundle = new Bundle();
                String req = "{'Action':'WORK_ORFDER_INVOICE','submode':'WORK_ORDER_APPROVAL_SEARCH','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','search':'" + 0 + "','_Mat_Req_MIR_ProjName':'','_Mat_Req_MIR_MIRId':'','_Mat_Req_MIR_MRId':'','_Mat_Req_MIR_MRRefNo':'','_Mat_Req_MIR_MRFromDate':'','_Mat_Req_MIR_MRToDate':'','_Mat_Req_MIR_MIRRefNo':'','_Mat_Req_MIR_MIRFromDate':'','_Mat_Req_MIR_MIRToDate':'','_Mat_Req_MIR_StoreName':'','_Mat_Req_MIR_Contractor':'','_Mat_Req_MIR_MIRReqBy':'','_Mat_Req_MIR_StageName':'','_Mat_Req_MIR_IOW':'','_Mat_Req_MIR_Material':'','_Mat_Req_MIR_MIRStatus':'','page':' 1 ','FrmNameCtl':'WO','WO_search':'0','FrmNameCtl':'WO','WO_search':'0','RS':' 10 '}";
                bundle.putString("load", req);
                bundle.putString("IsApproval", "true");
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>WO BILL INVOICE</font>"));
                NavigationFragmentManager(WOBillListViewFragment.newInstance(bundle), "WO BILL INVOICE APPROVAL");
            } else {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>WO BILL INVOICE APPROVAL</font>"));
                NavigationFragmentManager(WOBillListViewFragment.newInstance(null), "WO BILL INVOICE");
            }
        } else if (param.equalsIgnoreCase("WO BILL RECOMM")) {
            System.out.println("wo recomm");
            int count = (Integer.parseInt(woBillreccount));
            if (count > 0 && (woBillrecrights.equalsIgnoreCase("true"))) {
                Bundle bundle = new Bundle();
                String req = "{'Action':'WORK_ORDER_RECOMMENDATION','submode':'WORK_ORDER_APPROVAL_SEARCH','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','search':'" + 0 + "','WO_search':'0','RS':' 10 '}";
                bundle.putString("load", req);
                bundle.putString("IsApproval", "true");
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>WO BILL RECOMMENDATION APPROVAL</font>"));
                NavigationFragmentManager(WoRecommendationList.newInstance(bundle), "WO BILL RECOMMENDATION APPROVAL");
            } else {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>WO BILL RECOMMENDATION</font>"));
                NavigationFragmentManager(WoRecommendationList.newInstance(null), "WO BILL RECOMMENDATION");
            }
        }
    }
    //Set Icon for Dynamic ListView in Dashboard:
    public void setImage(ImageView textView1, RelativeLayout image, String param) {
        if (param.equalsIgnoreCase("MPR")) {
            textView1.setImageResource(R.drawable.ic_mpr);
        } else if (param.equalsIgnoreCase("VMF")) {
            textView1.setImageResource(R.drawable.ic_menu_vmf);
        } else if (param.equalsIgnoreCase("MR")) {
            textView1.setImageResource(R.drawable.ic_menu_mr);
        } else if (param.equalsIgnoreCase("PO")) {
            textView1.setImageResource(R.drawable.ic_menu_po);
        } else if (param.equalsIgnoreCase("GRN")) {
            textView1.setImageResource(R.drawable.ic_grn);
        } else if (param.equalsIgnoreCase("INDENT")) {
            textView1.setImageResource(R.drawable.ic_indent);
        } else if (param.equalsIgnoreCase("MIN")) {
            textView1.setImageResource(R.drawable.ic_min);
        } else if (param.equalsIgnoreCase("BMRF")) {
            textView1.setImageResource(R.drawable.ic_bmrf);
        } else if (param.equalsIgnoreCase("MRIR")) {
            textView1.setImageResource(R.drawable.ic_mrir);
        } else if (param.equalsIgnoreCase("MIN ACK")) {
            textView1.setImageResource(R.drawable.ic_min_ack);
        } else if (param.equalsIgnoreCase("TICKETING")) {
            textView1.setImageResource(R.drawable.ic_ticket_icon);
        } else if (param.equalsIgnoreCase("TMS")) {
            textView1.setImageResource(R.drawable.ic_tms_icon);
        } else if (param.equalsIgnoreCase("MTN")) {
            textView1.setImageResource(R.drawable.mtn);
        } else if (param.equalsIgnoreCase("MTRN")) {
            textView1.setImageResource(R.drawable.mtrn);
        } else if (param.equalsIgnoreCase("PO BILL INVOICE")) {
            textView1.setImageResource(R.drawable.ic_icon_pobill);
        } else if (param.equalsIgnoreCase("PO BILL RECOMM")) {
            textView1.setImageResource(R.drawable.ic_icon_porecommendation);
        } else if (param.equalsIgnoreCase("WO")) {
            textView1.setImageResource(R.drawable.ic_icon_wo);
        } else if (param.equalsIgnoreCase("WO BILL INVOICE")) {
            textView1.setImageResource(R.drawable.ic_icon_wo_bill_invoice);
        } else if (param.equalsIgnoreCase("WO BILL RECOMM")) {
            textView1.setImageResource(R.drawable.wo_bill_recommendation);
        }
    }
}
