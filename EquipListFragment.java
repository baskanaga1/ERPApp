package com.guruinfo.scm.Equipment;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bowyer.app.fabtransitionlayout.BottomSheetLayout;
import com.guruinfo.scm.R;
import com.guruinfo.scm.SCMDashboardActivityLatest;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseFragment;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.RestClientHelper;
import com.guruinfo.scm.common.ui.SCMTextView;
import com.guruinfo.scm.common.utils.Sharedpref;
import com.michael.easydialog.EasyDialog;
import com.paging.listview.PagingBaseAdapter;
import com.paging.listview.PagingExpandableBaseAdapter;
import com.paging.listview.PagingExpandableListView;
import com.paging.listview.PagingListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.guruinfo.scm.SCMDashboardActivityLatest.NavigationFragmentManager;


public class EquipListFragment extends BaseFragment {

    private static final String TAG = "EquipListFragment";
    Context context;
    SessionManager session;
    static String uid, Cre_Id;
    BackgroundTask backgroundTask;
    String requestParameter;
    String listRequestParameter;
    boolean isIcon = true;
    Boolean newRequestBtn = false;
    Boolean approvalBtn = false;
    ArrayList<String> selectedMRItemsArrayList;
    String listType = "";
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    ParentListAdapter adapter;

    @Bind(R.id.error_msg)
    SCMTextView emptyMsgTextView;
    /*@Bind(R.id.equip_list)
    PagingListView equipList;*/
    @Bind(R.id.vmf_list_view)
    PagingListView vmf_list_view;
    @Bind(R.id.mr_list_view)
    PagingExpandableListView mrListView;
    @Bind(R.id.total_layout)
    LinearLayout total_layout;
    @Bind(R.id.footer_layout)
    LinearLayout footer_layout;
    @Bind(R.id.bottom_sheet)
    BottomSheetLayout bottom_sheet;
    ArrayList<HashMap<String, String>> itemValuearraylist = new ArrayList<>();

    HashMap<String, String> listItemValue = new HashMap<>();
    static String EQUIPMENT = "EQUIPMENT";
    static EditText requestReturnable;
    static String request = null;
    int pageNo = 0;
    int totalPage = 1;
    static int parentColorIndex = 0;
    EasyDialog newRequestDialog;
    MenuInflater inflater1;


    public static EquipListFragment newInstance(Bundle bundle) {
        EquipListFragment fragment = new EquipListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        inflater1 = inflater;
        inflater.inflate(R.menu.list_action, menu);

        menu.findItem(R.id.new_request).setVisible(true);
        menu.findItem(R.id.filter).setVisible(true);
        menu.findItem(R.id.list).setVisible(false);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.favourite).setVisible(false);



        super.onCreateOptionsMenu(menu, inflater);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.new_request:

                Bundle bundle = new Bundle();
                bundle.putString("HEADER", "Equipment Request Creation");
                bundle.putString("SLOT_TYPE", "Create");
                Sharedpref.SetPrefString(context, "SlotType", "Create");
                bundle.putString("RequestedBy",uid);
                NavigationFragmentManager(EquipRequestCreation.newInstance(bundle), "EQUIP");

                /*LayoutInflater inflater2 = LayoutInflater.from(context);
                View convertView = inflater2.inflate(R.layout.equip_newrequest_dialog, null);
                Button SlotSetting = (Button) convertView.findViewById(R.id.equip_slot_setting);
                Button EquipRequest = (Button) convertView.findViewById(R.id.equip_reg);

                 newRequestDialog = new EasyDialog(context)
                        .setLayout(convertView)
                        .setBackgroundColor(context.getResources().getColor(R.color.white))
                        .setGravity(EasyDialog.GRAVITY_BOTTOM)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 1000, -600, 100, -50, 50, 0)
                        .setAnimationAlphaShow(1000, 0.3f, 1.0f)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 500, -50, 800)
                        .setAnimationAlphaDismiss(500, 1.0f, 0.0f)
                        .setTouchOutsideDismiss(true)
                        .setMatchParent(true)
                        .setOutsideColor(context.getResources().getColor(R.color.transparent_black_bg))
                        .show();


                EquipRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("HEADER", "Equipment Request Creation");
                        //navigateToNextFragment(EquipRequestCreation.class.getName(), bundle);

                        NavigationFragmentManager(EquipRequestCreation.newInstance(bundle), "EQUIP");
                        newRequestDialog.dismiss();
                    }
                });
                SlotSetting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        //navigateToNextFragment(EquipmentSlotSettings.class.getName(), bundle);
                        ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#2d2e3a'>Add Equipment Slot Setting</font>"));
                        NavigationFragmentManager(EquipmentSlotSettings.newInstance(null), "");
                        newRequestDialog.dismiss();
                    }
                });*/


                return true;
            case R.id.favourite:

                return true;
            case R.id.filter:
                Bundle bundle1 = new Bundle();
                bundle1.putString("LIST_ACTION", "MOBILE_EQUIPMENT_PROCESS");
                bundle1.putString("LIST_SUBMODE", "EQUIPMENT_LIST");
                bundle1.putString("SEARCH_TYPE", "EQUIPMENT REQUEST LISTING");
                NavigationFragmentManager(EquipFilterFragment.newInstance(bundle1), "EQUIP");
                return true;
            case R.id.list:
                /*((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#2d2e3a'>EQUIPMENT</font>"));
                NavigationFragmentManager(EquipListFragment.newInstance(null), "EQUIP");*/
                return true;
            default:
                searchView.setOnQueryTextListener(queryTextListener);
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
        selectedMRItemsArrayList = new ArrayList<>();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment vmf_list_view
        //View view = inflater.inflate(R.layout.equipment_fragment_list, container, false);
        View view = inflater.inflate(R.layout.paging_expandable_list, container, false);
        itemValuearraylist = new ArrayList<>();

        ButterKnife.bind(this, view);

        requestReturnable = (EditText) view.findViewById(R.id.newRequestReturnable);

        mrListView.setVisibility(View.GONE);
        total_layout.setVisibility(View.GONE);
        footer_layout.setVisibility(View.GONE);
        bottom_sheet.setVisibility(View.GONE);
        vmf_list_view.setVisibility(View.VISIBLE);
        final Bundle bundle = this.getArguments();
        //setHasOptionsMenu(true);

        if (bundle != null) {
            listRequestParameter = bundle.getString("load");

        }else {
            //((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#2d2e3a'>EQUIPMENT</font>"));
            listRequestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'EQUIPMENT_LIST','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','RPN':' 1 '}";
        }
        totalPage = 1;
        pageNo = 0;



        adapter = new ParentListAdapter(context);
        vmf_list_view.setAdapter(adapter);
        vmf_list_view.setHasMoreItems(true);
        vmf_list_view.setPagingableListener(new PagingListView.Pagingable() {
            @Override
            public void onLoadMoreItems() {
                if (pageNo < totalPage) {
                    try {
                        pageNo++;
                        JSONObject jsonObject = new JSONObject(listRequestParameter.toString());
                        jsonObject.put("RPN", pageNo);
                        jsonObject.put("RS", 10);
                        System.out.println("Equip_Request" + jsonObject.toString());
                        listRequestParameter = jsonObject.toString();
                        onListLoad(listRequestParameter, "LOAD");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    vmf_list_view.onFinishLoading(false, null, totalPage);
                }
            }
        });
        requestReturnable.addTextChangedListener(new
                                                         TextWatcher() {
                                                             @Override
                                                             public void beforeTextChanged(CharSequence s, int start, int count,
                                                                                           int after) {
                                                                 // TODO Auto-generated method stub
                                                             }

                                                             @Override
                                                             public void onTextChanged(CharSequence s, int start, int before,
                                                                                       int count) {
                                                                 // TODO Auto-generated method stub
                                                             }

                                                             @Override
                                                             public void afterTextChanged(Editable s) {
                                                                 //if (request != null)
                                                                     //getPOList(request, flagName);
                                                             }
                                                         }
        );





        return view;
    }

    public void onListLoad(String req, final String flag) {
        requestParameter = req;
        Log.d(TAG, requestParameter);
        RestClientHelper.getInstance().getURL(requestParameter, context, new RestClientHelper.RestClientListener() {
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
        });
    }

    private void parseJSONResponse(JSONObject response, String flag) {
        try {
            if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_1)) {
                if (flag.equals("LOAD")) {
                    listDisplay(response);
                }
            } else if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_403)) {
                showSessionScreensDialog(context, response.getString(AppContants.RESPONSE_MESSAGE));
            } else {
                if (flag.equals("otpresponse")) {
                   // setmsgToast(dialog.getWindow().getDecorView(), response.getString("msg"));
                } else
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void listDisplay(JSONObject response) {
        try {

            JSONArray EQUIPListJSONArray = response.getJSONArray("TableValues");

            if (EQUIPListJSONArray.length() > 0) {
                System.out.println("EQUIPLISTJSONArray has some values");
                //final TreeNode root = TreeNode.root();
                int parentColorIndex = 0;
                for (int i = 0; i < EQUIPListJSONArray.length(); i++) {

                    HashMap<String, String> hashMap = new HashMap<>();
                    if (parentColorIndex > 11) {
                        parentColorIndex = 0;
                        hashMap.put("color", "" + colorArray()[parentColorIndex]);
                    } else {
                        hashMap.put("color", "" + colorArray()[parentColorIndex]);
                        parentColorIndex = parentColorIndex + 1;
                    }
                    hashMap.put("expand", "");
                    hashMap.put("assignTo", EQUIPListJSONArray.getJSONObject(i).getString("assignTo"));
                    hashMap.put("equipName", EQUIPListJSONArray.getJSONObject(i).getString("equipName"));
                    hashMap.put("equipIdNum", EQUIPListJSONArray.getJSONObject(i).getString("equipIdNum"));
                    hashMap.put("sNo", EQUIPListJSONArray.getJSONObject(i).getString("sNo"));
                    hashMap.put("status", EQUIPListJSONArray.getJSONObject(i).getString("status"));
                    hashMap.put("clockSheetStatus", EQUIPListJSONArray.getJSONObject(i).getString("clockSheetStatus"));
                    hashMap.put("date", EQUIPListJSONArray.getJSONObject(i).getString("date"));
                    hashMap.put("startDateandTime", EQUIPListJSONArray.getJSONObject(i).getString("startDateandTime"));
                    hashMap.put("equipId", EQUIPListJSONArray.getJSONObject(i).getString("equipId"));
                    hashMap.put("duration", EQUIPListJSONArray.getJSONObject(i).getString("duration"));
                    hashMap.put("viewerType", EQUIPListJSONArray.getJSONObject(i).getString("viewerType"));
                    hashMap.put("priority", EQUIPListJSONArray.getJSONObject(i).getString("priority"));
                    hashMap.put("approvalStatus", EQUIPListJSONArray.getJSONObject(i).getString("approvalStatus"));
                    JSONObject editableJsonObject = EQUIPListJSONArray.getJSONObject(i).getJSONObject("action");
                    hashMap.put("scheduleType", editableJsonObject.getString("scheduleType"));
                    hashMap.put("history", editableJsonObject.getString("history"));
                    hashMap.put("slotSchedule", editableJsonObject.getString("slotSchedule"));
                    hashMap.put("swap", editableJsonObject.getString("swap"));
                    hashMap.put("clockAddUpdate", editableJsonObject.getString("clockAddUpdate"));
                    hashMap.put("reqType", EQUIPListJSONArray.getJSONObject(i).getString("reqType"));
                    hashMap.put("requestedBy", EQUIPListJSONArray.getJSONObject(i).getString("requestedBy"));
                    totalPage = Integer.parseInt(response.getString("pageCount"));

                    itemValuearraylist.add(hashMap);

                }

            }
            if (EQUIPListJSONArray.length() > 0) {
                emptyMsgTextView.setVisibility(View.GONE);
                mrListView.setVisibility(View.GONE);
                total_layout.setVisibility(View.GONE);
                footer_layout.setVisibility(View.GONE);
                bottom_sheet.setVisibility(View.GONE);
                vmf_list_view.setVisibility(View.VISIBLE);
                vmf_list_view.onFinishLoading(true, itemValuearraylist, totalPage);
            } else {
                emptyMsgTextView.setVisibility(View.VISIBLE);
                vmf_list_view.setVisibility(View.GONE);
                mrListView.setVisibility(View.GONE);
                total_layout.setVisibility(View.GONE);
                footer_layout.setVisibility(View.GONE);
                bottom_sheet.setVisibility(View.GONE);

            }

            setHasOptionsMenu(true);
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

    static class Holder {
        SCMTextView equip_list_sno_bg_text, equip_list_date_content,equip_reg_type, equip_reg_id_content, equip_name, equip_no, equip_priority, equip_status, equip_approval;
        ImageView slot_schedule, slot_reschedule, slot_swap, history;
        SCMTextView equip_reg_by, equip_clock_status, equip_assignedTO, equip_startDate, equip_duration;
        LinearLayout equip_parent_lay, equip_child_lay, sno_bg_lay;


    }

    public class ParentListAdapter extends PagingBaseAdapter {

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
           // return valuearraylist.size();
            return itemValuearraylist.size();
        }
        @Override
        public String getItem(int position) {
           return itemValuearraylist.get(position).toString();

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder;
            View v = convertView;

            //final TextView user_name;
            //ImageView btn_ext, btn_admin;
            //PorterCircularImageView userImage;
            //CheckBox btn_ext, btn_admin;

            if (v == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = mInflater.inflate(R.layout.equip_parent_list, null);
                holder = new Holder();


                holder.equip_list_sno_bg_text = (SCMTextView)v.findViewById(R.id.equip_list_sno_bg_text);
                holder.equip_list_date_content = (SCMTextView)v.findViewById(R.id.equip_list_date_content);
                holder.equip_reg_type = (SCMTextView)v.findViewById(R.id.equip_reg_type);
                holder.equip_reg_id_content = (SCMTextView)v.findViewById(R.id.equip_reg_id_content);
                holder.equip_name = (SCMTextView)v.findViewById(R.id.equip_name);
                holder.equip_no = (SCMTextView)v.findViewById(R.id.equip_no);
                holder.equip_priority = (SCMTextView)v.findViewById(R.id.equip_priority);
                holder.equip_status = (SCMTextView)v.findViewById(R.id.equip_status);
                holder.equip_approval = (SCMTextView)v.findViewById(R.id.equip_approval);

                holder.slot_schedule = (ImageView)v.findViewById(R.id.slot_schedule);
                holder.slot_reschedule = (ImageView)v.findViewById(R.id.slot_reschedule);
                holder.slot_swap = (ImageView)v.findViewById(R.id.slot_swap);
                holder.history = (ImageView)v.findViewById(R.id.history);

                holder.equip_reg_by = (SCMTextView)v.findViewById(R.id.equip_reg_by);
                holder.equip_clock_status = (SCMTextView)v.findViewById(R.id.equip_clock_status);
                holder.equip_assignedTO = (SCMTextView)v.findViewById(R.id.equip_assignedTO);
                holder.equip_startDate = (SCMTextView)v.findViewById(R.id.equip_startDate);
                holder.equip_duration = (SCMTextView)v.findViewById(R.id.equip_duration);

                holder.equip_parent_lay = (LinearLayout)v.findViewById(R.id.equip_parent_lay);
                holder.equip_child_lay = (LinearLayout)v.findViewById(R.id.equip_child_lay);
                holder.sno_bg_lay = (LinearLayout)v.findViewById(R.id.sno_bg_lay);
                holder.equip_child_lay.setVisibility(View.GONE);
                v.setTag(holder);
            } else {
                holder = (Holder) v.getTag();
            }

            holder.equip_list_sno_bg_text.setText(itemValuearraylist.get(position).get("sNo"));
            holder.equip_list_date_content.setText(itemValuearraylist.get(position).get("date"));
            holder.equip_reg_type.setText(itemValuearraylist.get(position).get("reqType"));
            holder.equip_reg_id_content.setText(itemValuearraylist.get(position).get("equipId"));
            holder.equip_no.setText(itemValuearraylist.get(position).get("equipIdNum"));
            holder.equip_name.setText(itemValuearraylist.get(position).get("equipName"));
            holder.equip_priority.setText(itemValuearraylist.get(position).get("priority"));
            holder.equip_status.setText(itemValuearraylist.get(position).get("status"));
            holder.equip_approval.setText(itemValuearraylist.get(position).get("approvalStatus"));
            holder.equip_reg_by.setText(itemValuearraylist.get(position).get("requestedBy"));
            holder.equip_clock_status.setText(itemValuearraylist.get(position).get("clockSheetStatus"));
            holder.equip_assignedTO.setText(itemValuearraylist.get(position).get("assignTo"));
            holder.equip_startDate.setText(itemValuearraylist.get(position).get("startDateandTime"));
            holder.equip_duration.setText(itemValuearraylist.get(position).get("duration"));

            holder.sno_bg_lay.setBackground(getResources().getDrawable(Integer.parseInt(itemValuearraylist.get(position).get("color"))));

            if(itemValuearraylist.get(position).get("scheduleType").equalsIgnoreCase("rescheduled")){

                holder.slot_reschedule.setVisibility(View.VISIBLE);
                holder.slot_schedule.setVisibility(View.GONE);
            }else if(itemValuearraylist.get(position).get("scheduleType").equalsIgnoreCase("scheduled")){
                holder.slot_schedule.setVisibility(View.VISIBLE);
                holder.slot_reschedule.setVisibility(View.GONE);
            }else {
                holder.slot_schedule.setVisibility(View.GONE);
                holder.slot_reschedule.setVisibility(View.GONE);
            }

            if(itemValuearraylist.get(position).get("swap").equalsIgnoreCase("true"))
                holder.slot_swap.setVisibility(View.VISIBLE);
            else
                holder.slot_swap.setVisibility(View.GONE);

            if(itemValuearraylist.get(position).get("history").equalsIgnoreCase("true"))
                holder.history.setVisibility(View.VISIBLE);
            else
                holder.history.setVisibility(View.GONE);



            holder.slot_schedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString("SLOT_TYPE","SCHEDULE");
                    bundle.putString("EQUIPID",itemValuearraylist.get(position).get("equipIdNum"));
                    bundle.putString("EQUIPREQID",itemValuearraylist.get(position).get("equipId"));
                    bundle.putString("EQUIPNAME",itemValuearraylist.get(position).get("equipName"));
                    bundle.putString("EQUIPREQBY",itemValuearraylist.get(position).get("requestedBy"));
                    ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Equipment Slot Schedule</font>"));
                    NavigationFragmentManager(Equip_Slot_Schedule.newInstance(bundle), "EQUIP");
                }
            });
            holder.slot_reschedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("SLOT_TYPE","RESCHEDULE");
                    bundle.putString("EQUIPID",itemValuearraylist.get(position).get("equipIdNum"));
                    bundle.putString("EQUIPREQID",itemValuearraylist.get(position).get("equipId"));
                    bundle.putString("EQUIPNAME",itemValuearraylist.get(position).get("equipName"));
                    bundle.putString("EQUIPREQBY",itemValuearraylist.get(position).get("requestedBy"));
                    ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Equipment Slot Schedule</font>"));
                    NavigationFragmentManager(Equip_Slot_Schedule.newInstance(bundle), "EQUIP");
                }
            });
            holder.slot_swap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("SLOT_TYPE","SWAP");
                    bundle.putString("EQUIPID",itemValuearraylist.get(position).get("equipIdNum"));
                    bundle.putString("EQUIPREQID",itemValuearraylist.get(position).get("equipId"));
                    bundle.putString("EQUIPNAME",itemValuearraylist.get(position).get("equipName"));
                    bundle.putString("EQUIPREQBY",itemValuearraylist.get(position).get("requestedBy"));
                    ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Equipment Swapping</font>"));
                    NavigationFragmentManager(Equip_Slot_Schedule.newInstance(bundle), "EQUIP");
                }
            });
            holder.history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("EQUIPREQID",itemValuearraylist.get(position).get("equipId"));
                    ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Equipment History</font>"));
                    NavigationFragmentManager(Equip_History.newInstance(bundle), "EQUIP");
                }
            });

            holder.equip_reg_id_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Bundle bundle = new Bundle();
                    //bundle.putSerializable("LIST_ITEM",(Serializable)itemValuearraylist);
                    bundle.putString("viewerType",itemValuearraylist.get(position).get("viewerType"));
                    bundle.putString("EQUIPREQID",itemValuearraylist.get(position).get("equipId"));
                    ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Equipment Request View</font>"));
                    NavigationFragmentManager(EquipRequestView.newInstance(bundle), "EQUIP");

                }
            });

            if(!itemValuearraylist.get(position).get("expand").equalsIgnoreCase("")){
                expand(holder.equip_child_lay);

            }else {
                collapse(holder.equip_child_lay);
            }

            holder.equip_parent_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!itemValuearraylist.get(position).get("expand").equalsIgnoreCase("")){
                        itemValuearraylist.get(position).put("expand", "");
                        notifyDataSetChanged();
                    }else {
                        itemValuearraylist.get(position).put("expand", "true");
                        notifyDataSetChanged();
                    }

                    /*if (holder.equip_child_lay.getVisibility() == View.VISIBLE) {
                        collapse(holder.equip_child_lay);
                        //childLay.setVisibility(View.GONE);

                    } else {
                        //childLay.setVisibility(View.VISIBLE);
                        System.out.println("clicked");
                        expand(holder.equip_child_lay);

                    }*/
                }
            });


            return v;
        }
    }

   /* public class ParentListAdapter1 extends PagingExpandableBaseAdapter {
        private Context mContex;
        private LayoutInflater mInflater;
        private LayoutInflater mChInflater;
        public ParentListAdapter1(Context context) {
            this.mContex = context;
            mInflater = LayoutInflater.from(context);
            mChInflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public Object getGroup(int groupPosition) {
            return childItems.get(groupPosition);
        }
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childItems.get(groupPosition);
        }
        @Override
        public int getGroupCount() {
            return childItems.size();
        }
        @Override
        public int getChildrenCount(int groupPosition) {
            return childItems.get(groupPosition).size();
        }
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }
        @Override
        public boolean hasStableIds() {
            return true;
        }
        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContex
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = mInflater.inflate(R.layout.min_parent_list_items, null);
                holder = new ViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            return row;
        }
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View row = convertView;
            row = mInflater.inflate(R.layout.bmrf_child_list_item, null);

            return row;
        }
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
        class ViewHolder {
            @Bind(R.id.min_parent_list_id_content)
            SCMTextView BmrfIdContent;
            @Bind(R.id.MIN_parent_list_mpr_date_content)
            SCMTextView BmrfDateContent;
            @Bind(R.id.MIN_parent_list_project_content)
            SCMTextView projectContent;
            @Bind(R.id.min_parent_list_store_name_content)
            SCMTextView storeNameContent;
            @Bind(R.id.min_parent_list_status_content)
            SCMTextView statusContent;
            @Bind(R.id.min_parent_list_sno_bg_text)
            SCMTextView sno;
            @Bind(R.id.min_type)
            SCMTextView Po_ID;
            @Bind(R.id.favorite)
            ImageView favorite;
            @Bind(R.id.min_middle_layout)
            LinearLayout parentLinearLayout;
            @Bind(R.id.arrow_icon)
            ImageView arrowView;
            @Bind(R.id.min_vendor_name)
            SCMTextView Vendor_name;
            @Bind(R.id.BMRF_Requestedby)
            SCMTextView Requested_by;
            @Bind(R.id.transactionSNoBGLayout)
            LinearLayout transactionSNoBGLayout;
            @Bind(R.id.isfreeze)
            LinearLayout isfreeze;
            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }*/


    public void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }



    public static void collapse(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = targetHeight;
        v.setVisibility(View.GONE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * (1 - interpolatedTime));
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
       // mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
