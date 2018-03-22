package com.guruinfo.scm.Equipment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.guruinfo.scm.R;
import com.guruinfo.scm.SCMDashboardActivityLatest;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseFragment;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.service.RestClientHelper;
import com.guruinfo.scm.common.ui.LeadNothingSelectedSpinnerAdapter;
import com.guruinfo.scm.common.ui.SCMTextView;
import com.guruinfo.scm.common.utils.ApiCalls;
import com.guruinfo.scm.common.utils.Sharedpref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import static com.guruinfo.scm.SCMDashboardActivityLatest.NavigationFragmentManager;

public class Equip_Slot_Schedule extends BaseFragment {

    private static final String TAG = "EquipReSchedule";
    Context context;
    SessionManager session;
    static String uid, Cre_Id;
    BackgroundTask backgroundTask;
    String listRequestParameter;
    String requestParameter;
    String Slot_Type="";
    String SlotType="";
    String Equip_Id = "";
    String Equip_Req_By="";
    String Equip_Req_Id = "";
    String Equip_Name = "";
    SCMTextView slot_date_time, slot_project, slot_equipname, slot1, slot2;
    Spinner slot_equipid_spin;
    EditText slot_schedule_date, slot_duration;
    LinearLayout slot1_lay, slotshe_lay, slotpro_lay, save_lay, close_lay, split_lay;
    TextView equip_close, equip_save;
    String listActionName = "MOBILE_EQUIPMENT_PROCESS";
    String frmnameCTRL = "SLOT_SAVE";
    String Submode;
    public static String stageId = "";
    String projectId = "";
    String equipEquipIdSelect_Id, saveDuration, saveTotalDuration, MultiBookKey;

    ArrayList<String> Equipment_dur_date;
    ArrayList<String> Equipment_start_date;
    ArrayList<String> Equipment_end_date;
    ArrayList<String> ScheduleDate;

    String SplitCount, ScheduleDuration;
    //ArrayList<HashMap<String, String>> BookedList;
    HashMap <String,HashMap<String, String>> BookedList = new HashMap <String,HashMap<String, String>>();

    ArrayList<HashMap<String, String>> EquipmentId = new ArrayList<>();
    ArrayList<HashMap<String, String>> EquipName = new ArrayList<>();
    HashMap<String, String> hashMap1 = new HashMap<>();


    // TODO: Rename and change types and number of parameters
    public static Equip_Slot_Schedule newInstance(Bundle bundle) {
        Equip_Slot_Schedule fragment = new Equip_Slot_Schedule();
        Bundle args = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub

        inflater.inflate(R.menu.list_action, menu);

        menu.findItem(R.id.edit).setVisible(false);
        menu.findItem(R.id.new_request).setVisible(false);
        menu.findItem(R.id.filter).setVisible(false);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.favourite).setVisible(false);



        super.onCreateOptionsMenu(menu, inflater);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.equip_slot_schedule, container, false);



        Bundle bundle = this.getArguments();
        hashMap1 = new HashMap<>();
        Equipment_start_date = new ArrayList<>();
        Equipment_dur_date = new ArrayList<>();
        Equipment_end_date = new ArrayList<>();
        ScheduleDate = new ArrayList<>();
        EquipmentId = new ArrayList<>();
        EquipName = new ArrayList<>();

        if (bundle != null) {

            if(bundle.getString("SLOT_TYPE").equalsIgnoreCase("BOOKED")){

                SlotType = bundle.getString("SLOT_TYPE");
                Equip_Id = bundle.getString("EQUIPID");
                SplitCount = bundle.getString("SPLITCOUNT");
                Equip_Req_Id = bundle.getString("EQUIPREQID");
                Equip_Name = bundle.getString("EQUIPNAME");
                MultiBookKey = bundle.getString("MULTIBOOK");
                BookedList = (HashMap <String,HashMap<String, String>>) bundle.getSerializable("LIST_ITEM");
                System.out.println("BookedList"+BookedList.toString());
                if(Sharedpref.GetPrefString(context, "SlotType").equalsIgnoreCase("RESCHEDULE")) {
                    load(listActionName, "RESCHEDULE_ADD_UPDATE", "RESCHEDULE", "");
                }else if(Sharedpref.GetPrefString(context, "SlotType").equalsIgnoreCase("SCHEDULE")) {
                    load(listActionName, "RESCHEDULE_ADD_UPDATE", "SCHEDULE", "");
                }else if(Sharedpref.GetPrefString(context, "SlotType").equalsIgnoreCase("SWAP")) {
                    load(listActionName, "RESCHEDULE_ADD_UPDATE", "SWAP", "");
                }
                if(SplitCount.equalsIgnoreCase("1")){
                    saveDuration = "60";
                    saveTotalDuration = ""+BookedList.size()*60;
                }else if(SplitCount.equalsIgnoreCase("2")){
                    saveDuration = "30";
                    saveTotalDuration = ""+BookedList.size()*30;
                }else if(SplitCount.equalsIgnoreCase("4")){
                    saveDuration = "15";
                    saveTotalDuration = ""+BookedList.size()*15;
                }
                int hours = Integer.parseInt(saveTotalDuration)/60;
                int minutes = Integer.parseInt(saveTotalDuration)%60;
                if (hours > 10) {
                    if (minutes > 0)
                        ScheduleDuration = "" + hours + ":" + minutes;
                    else
                        ScheduleDuration = "" + hours + ":0" + minutes;
                }
                else {
                    if (minutes > 0)
                        ScheduleDuration = "0" + hours + ":" + minutes;
                    else
                        ScheduleDuration = "0" + hours + ":0" + minutes;
                }

                HashMap<String, String> Map=new HashMap<String, String>();
                Iterator myVeryOwnIterator = BookedList.keySet().iterator();
                while (myVeryOwnIterator.hasNext()) {
                    String key = (String) myVeryOwnIterator.next();
                    Map = (HashMap<String, String>) BookedList.get(key);
                    //System.out.println("Key: "+key+" Value: "+Map);
                    String datesplit = Map.get("title") + "," + (Map.get("title")).replaceFirst("_(.*)", "") + "_" + Map.get("slotEndTime");
                    String startdatesplit ="";
                    if(!Equip_Id.equalsIgnoreCase("Equipment Id")){
                        startdatesplit = Map.get("title") + "#" + Equip_Id;
                    }else {
                        startdatesplit = Map.get("title") + "#" + Map.get("equipUniq");
                    }

                    String enddatesplit = (Map.get("title")).replaceFirst("_(.*)", "") + "_" + Map.get("slotEndTime");
                    //System.out.println("@@@: "+datesplit +"=="+startdatesplit+"==="+enddatesplit);
                    ScheduleDate.add(key);
                    Equipment_dur_date.add(datesplit);
                    Equipment_start_date.add(startdatesplit);
                    Equipment_end_date.add(enddatesplit);

                }

                Collections.sort(ScheduleDate, new Comparator<String>() {
                    DateFormat f = new SimpleDateFormat("dd/MM/yyyy'_'hh:mm");
                    @Override
                    public int compare(String o1, String o2) {
                        try {
                            return f.parse(o1).compareTo(f.parse(o2));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });

                System.out.println(""+ScheduleDate.toString());

            }else {
                Slot_Type = bundle.getString("SLOT_TYPE");
                Equip_Id = bundle.getString("EQUIPID");
                Equip_Req_Id = bundle.getString("EQUIPREQID");
                Equip_Name = bundle.getString("EQUIPNAME");
                Equip_Req_By = bundle.getString("EQUIPREQBY");
                Sharedpref.SetPrefString(context, "SlotType", Slot_Type);
            }
        }

        slot_date_time = (SCMTextView)view.findViewById(R.id.slot_date_time);
        slot_project = (SCMTextView)view.findViewById(R.id.slot_project);
        slot_equipname = (SCMTextView)view.findViewById(R.id.slot_equipname);
        slot1 = (SCMTextView)view.findViewById(R.id.slot1);
        slot2 = (SCMTextView)view.findViewById(R.id.slot2);
        equip_close = (TextView) view.findViewById(R.id.equip_close);
        equip_save = (TextView) view.findViewById(R.id.equip_close);

        slot_equipid_spin = (Spinner)view.findViewById(R.id.slot_equipid_spin);

        slot_schedule_date = (EditText)view.findViewById(R.id.slot_schedule_date);
        slot_duration = (EditText)view.findViewById(R.id.slot_duration);

        slot1_lay = (LinearLayout)view.findViewById(R.id.slot1_lay);
        slotpro_lay = (LinearLayout)view.findViewById(R.id.slotpro_lay);
        slotshe_lay = (LinearLayout)view.findViewById(R.id.slotshe_lay);
        close_lay = (LinearLayout)view.findViewById(R.id.close_lay);
        save_lay = (LinearLayout)view.findViewById(R.id.save_lay);
        split_lay = (LinearLayout)view.findViewById(R.id.split_lay);

        slot_equipid_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    int selectSpinnerPosition = (slot_equipid_spin.getSelectedItemPosition()) - 1;
                    System.out.println(""+EquipmentId.get(selectSpinnerPosition).get("id"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        slot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EquipSlotView.class);
                Bundle extras = new Bundle();
                extras.putString("EQUIPREQID", Equip_Req_Id);
                extras.putString("EQUIPREQID_SEL", equipEquipIdSelect_Id);
                extras.putString("EQUIPID",Equip_Id);
                extras.putString("EQUIPNAME",Equip_Name);
                //((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#2d2e3a'>Equipment Slot View</font>"));
               // NavigationFragmentManager(Equip_Slot_View.newInstance(extras), "Equip");
                intent.putExtras(extras);
                startActivity(intent);

            }
        });
        slot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, EquipSlotView.class);
                Bundle extras = new Bundle();
                extras.putString("EQUIPREQID", Equip_Req_Id);
                extras.putString("EQUIPREQID_SEL", equipEquipIdSelect_Id);
                extras.putString("EQUIPID",Equip_Id);
                extras.putString("EQUIPNAME",Equip_Name);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });


            if(Slot_Type.equalsIgnoreCase("SCHEDULE")){
               /*listRequestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'RESCHEDULE_ADD_UPDATE','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','unique_id':'"+Equip_Req_Id+"','ScheduleType':'scheduled','processName':'EquipmentUnique','equip_id':'"+Equip_Id+"','RequestCreation':'true'}";
                onListLoad(listRequestParameter, "RESCHEDULE_ADD_UPDATE");*/
                load(listActionName, "RESCHEDULE_ADD_UPDATE", "SCHEDULE", "");
            }else if(Slot_Type.equalsIgnoreCase("RESCHEDULE")){
                //requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'RESCHEDULE_ADD_UPDATE','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','unique_id':'" + Equip_Req_Id + "','ScheduleType':'rescheduled','processName':'EquipmentUnique','equip_id':'" + Equip_Id + "','RequestCreation':'true'}";
                //onListLoad(listRequestParameter, "RESCHEDULE_ADD_UPDATE");
                load(listActionName, "RESCHEDULE_ADD_UPDATE", "RESCHEDULE", "");

            }else if(Slot_Type.equalsIgnoreCase("SWAP")){
                load(listActionName, "RESCHEDULE_ADD_UPDATE", "SWAP", "");
                slot1_lay.setVisibility(View.VISIBLE);
                slotpro_lay.setVisibility(View.GONE);
                slotshe_lay.setVisibility(View.GONE);
                slot1.setVisibility(View.GONE);

            }

        close_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>EQUIPMENT</font>"));
                NavigationFragmentManager(EquipListFragment.newInstance(null), "Equip");
            }
        });

        save_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Sharedpref.GetPrefString(context, "SlotType").equalsIgnoreCase("SCHEDULE")){
                    if(BookedList.size()>0) {

                        try {
                            JSONObject reqObj = new JSONObject();
                            String formName = "group";
                            reqObj.put("Action", listActionName);
                            reqObj.put("submode", "EQUIPMENT_SAVE_PROCESS");
                            reqObj.put("Cre_Id", Cre_Id);
                            reqObj.put("UID", uid);
                            reqObj.put("equip_appointment_id", hashMap1.get("equip_appointment_id"));
                            reqObj.put("frmnameCTRL", frmnameCTRL);
                            reqObj.put(frmnameCTRL + "_EquipmentSlotType", hashMap1.get("EquipmentSlotType"));
                            reqObj.put(frmnameCTRL + "_scheduleType", hashMap1.get("ScheduleType"));
                            reqObj.put(frmnameCTRL + "_EquipmentId", equipEquipIdSelect_Id);
                            reqObj.put(frmnameCTRL + "_pickupdate", "");
                            reqObj.put(frmnameCTRL + "_DateTime", slot_schedule_date.getText().toString());
                            reqObj.put(frmnameCTRL + "_Duration", slot_duration.getText().toString());
                            reqObj.put(frmnameCTRL + "_Description", hashMap1.get("description"));
                            reqObj.put(frmnameCTRL + "_Project", hashMap1.get("Project"));
                            reqObj.put(frmnameCTRL + "_req_by", uid);
                            for(int i=0; i < EquipName.size(); i++){
                                if(EquipName.get(i).get("value").equalsIgnoreCase(slot_equipname.getText().toString())){
                                    reqObj.put(frmnameCTRL + "_EquipmentNames", EquipName.get(i).get("id"));
                                }
                            }

                            reqObj.put(frmnameCTRL + "_verifyBy", uid);
                            reqObj.put(frmnameCTRL + "_total_duration", saveTotalDuration);
                            reqObj.put(frmnameCTRL + "_req_to", "");
                            reqObj.put(frmnameCTRL + "_equip_slot", hashMap1.get("equipSlot"));
                            reqObj.put(frmnameCTRL + "_equip_uniq_change", Equip_Id);
                            String equipment_dur_date = "";
                            for (int i = 0; i < Equipment_dur_date.size(); i++) {
                                if (i == 0) {
                                    equipment_dur_date = equipment_dur_date + "" + Equipment_dur_date.get(i);
                                } else
                                    equipment_dur_date = equipment_dur_date + "#" + Equipment_dur_date.get(i);
                            }
                            System.out.println("#####: " + equipment_dur_date);
                            String equipment_start_date = "";
                            for (int i = 0; i < Equipment_start_date.size(); i++) {
                                if (i == 0) {
                                    equipment_start_date = equipment_start_date + "" + Equipment_start_date.get(i);
                                } else
                                    equipment_start_date = equipment_start_date + "@@" + Equipment_start_date.get(i);
                            }
                            System.out.println("@@@@@: " + equipment_start_date);
                            String equipment_end_date = "";
                            for (int i = 0; i < Equipment_end_date.size(); i++) {
                                if (i == 0) {
                                    equipment_end_date = equipment_end_date + "" + Equipment_end_date.get(i);
                                } else
                                    equipment_end_date = equipment_end_date + "," + Equipment_end_date.get(i);
                            }
                            System.out.println("@@@@@: " + equipment_end_date);
                            reqObj.put(frmnameCTRL + "_equipment_dur_date", equipment_dur_date);
                            reqObj.put(frmnameCTRL + "_equipment_start_date", equipment_start_date);
                            reqObj.put(frmnameCTRL + "_equipment_end_date", equipment_end_date);
                            reqObj.put(frmnameCTRL + "_equipment_duration", saveDuration);

                            String reqParam = reqObj.toString();

                            // onListLoad(reqParam, "SLOT_SAVE_AND_UPDATE");
                            onSaveList(reqParam, "SLOT_SAVE_AND_UPDATE");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        setToast("Please book the slot");
                    }
                } else {


                    try {
                        JSONObject reqObj = new JSONObject();
                        String formName = "group";
                        reqObj.put("Action", listActionName);
                        reqObj.put("submode", "EQUIPMENT_SAVE_PROCESS");
                        reqObj.put("Cre_Id", Cre_Id);
                        reqObj.put("UID", uid);
                        reqObj.put("equip_appointment_id", hashMap1.get("equip_appointment_id"));
                        reqObj.put("frmnameCTRL", frmnameCTRL);
                        reqObj.put(frmnameCTRL + "_EquipmentSlotType", hashMap1.get("EquipmentSlotType"));
                        reqObj.put(frmnameCTRL + "_scheduleType", hashMap1.get("ScheduleType"));
                        reqObj.put(frmnameCTRL + "_EquipmentId", equipEquipIdSelect_Id);
                        reqObj.put(frmnameCTRL + "_pickupdate", "");
                        reqObj.put(frmnameCTRL + "_DateTime", slot_schedule_date.getText().toString());
                        reqObj.put(frmnameCTRL + "_Duration", slot_duration.getText().toString());
                        reqObj.put(frmnameCTRL + "_Description", hashMap1.get("description"));
                        reqObj.put(frmnameCTRL + "_Project", hashMap1.get("Project"));
                        reqObj.put(frmnameCTRL + "_req_by", uid);
                        for (int i = 0; i < EquipName.size(); i++) {
                            if (EquipName.get(i).get("value").equalsIgnoreCase(slot_equipname.getText().toString())) {
                                reqObj.put(frmnameCTRL + "_EquipmentNames", EquipName.get(i).get("id"));
                            }
                        }

                        reqObj.put(frmnameCTRL + "_verifyBy", uid);
                        reqObj.put(frmnameCTRL + "_total_duration", saveTotalDuration);
                        reqObj.put(frmnameCTRL + "_req_to", "");
                        reqObj.put(frmnameCTRL + "_equip_slot", hashMap1.get("equipSlot"));
                        reqObj.put(frmnameCTRL + "_equip_uniq_change", Equip_Id);
                        String equipment_dur_date = "";
                        for (int i = 0; i < Equipment_dur_date.size(); i++) {
                            if (i == 0) {
                                equipment_dur_date = equipment_dur_date + "" + Equipment_dur_date.get(i);
                            } else
                                equipment_dur_date = equipment_dur_date + "#" + Equipment_dur_date.get(i);
                        }
                        System.out.println("#####: " + equipment_dur_date);
                        String equipment_start_date = "";
                        for (int i = 0; i < Equipment_start_date.size(); i++) {
                            if (i == 0) {
                                equipment_start_date = equipment_start_date + "" + Equipment_start_date.get(i);
                            } else
                                equipment_start_date = equipment_start_date + "@@" + Equipment_start_date.get(i);
                        }
                        System.out.println("@@@@@: " + equipment_start_date);
                        String equipment_end_date = "";
                        for (int i = 0; i < Equipment_end_date.size(); i++) {
                            if (i == 0) {
                                equipment_end_date = equipment_end_date + "" + Equipment_end_date.get(i);
                            } else
                                equipment_end_date = equipment_end_date + "," + Equipment_end_date.get(i);
                        }
                        System.out.println("@@@@@: " + equipment_end_date);
                        reqObj.put(frmnameCTRL + "_equipment_dur_date", equipment_dur_date);
                        reqObj.put(frmnameCTRL + "_equipment_start_date", equipment_start_date);
                        reqObj.put(frmnameCTRL + "_equipment_end_date", equipment_end_date);
                        reqObj.put(frmnameCTRL + "_equipment_duration", saveDuration);

                        String reqParam = reqObj.toString();

                        // onListLoad(reqParam, "SLOT_SAVE_AND_UPDATE");
                        onSaveList(reqParam, "SLOT_SAVE_AND_UPDATE");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        return view;
    }

    public void load(final String action, final String submode, String pjtId, String stgeId) {
        this.Submode = submode;
        this.projectId = pjtId;
        this.stageId = stgeId;

        if (pjtId.equalsIgnoreCase("RESCHEDULE")) {
            requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'" + submode + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','unique_id':'" + Equip_Req_Id + "','ScheduleType':'rescheduled','processName':'EquipmentUnique','equip_id':'" + Equip_Id + "','RequestCreation':'true'}";
        }else if(pjtId.equalsIgnoreCase("SCHEDULE")) {
            requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'" + submode + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','unique_id':'" + Equip_Req_Id + "','ScheduleType':'scheduled','processName':'EquipmentUnique','equip_id':'" + Equip_Id + "','RequestCreation':'true'}";
        }else if(pjtId.equalsIgnoreCase("SWAP")) {
            requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'" + submode + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','unique_id':'" + Equip_Req_Id + "','ScheduleType':'swap','processName':'EquipmentUnique','equip_id':'" + Equip_Id + "','RequestCreation':'true'}";
        }
        Log.d(TAG, requestParameter);
        backgroundTask = new BackgroundTask(this.getActivity(), Submode, new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String values, String flagMsg) {
                try {
                    if (flagMsg.equals("internet")) {
                        showInternetDialog(context, values, action, submode, projectId, stageId);
                    } else {
                        JSONObject jsonObject = new JSONObject(values);
                        parseJSONResponse(jsonObject, submode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        backgroundTask.execute("", "", requestParameter);
    }

    public void onSaveList(final String requestParameters, final String flag) {
        Log.d(TAG, requestParameters);

        backgroundTask = new BackgroundTask(getActivity(), flag, new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String values, String flagMsg) {
                try {
                    if (flagMsg.equals("internet")) {
                        showInternetDialog(getActivity(), values, requestParameters, flag);
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

    public void showInternetDialog(Context activity, String err_msg, final String requestParameterValues, final String flag) {
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
        // Button exitButton = (Button) dialog.findViewById(R.id.exit_btn);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onSaveList(requestParameterValues, flag);
            }
        });
        dialog.show();

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
    public static void loadLeadStyleSpinner(Spinner spin, ArrayList<HashMap<String, String>> resplist, String[] from, String txtStr) {
        SimpleAdapter listAdapter = new SimpleAdapter(BaseFragment.context, resplist, R.layout.lead_spinner_text, from, new int[]{android.R.id.text1});
        listAdapter.setDropDownViewResource(R.layout.lead_spinner_item_text);
        spin.setAdapter(listAdapter);
        spin.setSelection(-1);
        spin.getSelectedView();
        spin.setAdapter(new LeadNothingSelectedSpinnerAdapter(listAdapter, R.layout.lead_spinner_row_nothing_selected, BaseFragment.context, txtStr));
    }

    private void parseJSONResponse(JSONObject response, String flag) {
        try {
            if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_1)) {
                if (flag.equals("RESCHEDULE_ADD_UPDATE")) {

                    JSONArray EquipmentNameArray = response.getJSONArray("equipmentList");
                    EquipName = ApiCalls.getArraylistfromJson(EquipmentNameArray.toString());
                    JSONObject EquipmentIdObj = response.getJSONObject("equipmentId");
                    JSONArray EquipmentIdArray = EquipmentIdObj.getJSONArray("values");
                    EquipmentId = ApiCalls.getArraylistfromJson(EquipmentIdArray.toString());
                    for (int i=0; i<EquipmentIdArray.length(); i++){
                        if(EquipmentIdArray.getJSONObject(i).getString("value").equalsIgnoreCase(Equip_Id)){
                            equipEquipIdSelect_Id = EquipmentIdArray.getJSONObject(i).getString("id");

                        }else if(Equip_Id.equalsIgnoreCase("")){
                            equipEquipIdSelect_Id = EquipmentIdArray.getJSONObject(i).getString("id");

                        }
                    }
                    loadLeadStyleSpinner(slot_equipid_spin, EquipmentId, new String[]{"value"}, Equip_Id);

                    if(SlotType.equalsIgnoreCase("BOOKED")){
                        slot_schedule_date.setText((ScheduleDate.get(0)).replace("_"," "));
                        slot_duration.setText(ScheduleDuration);
                        slot_date_time.setText(response.getString("dateAndTime"));
                        slot_project.setText(response.getString("project"));
                        slot_equipname.setText(Equip_Name);
                    }else {
                        slot_date_time.setText(response.getString("dateAndTime"));
                        slot_project.setText(response.getString("project"));
                        slot_equipname.setText(Equip_Name);
                        slot_schedule_date.setText(response.getString("scheduleDate"));
                        slot_duration.setText(response.getString("Duration"));
                    }

                    hashMap1.put("EquipmentSlotType", response.getString("EquipmentSlotType"));
                    hashMap1.put("project", response.getString("project"));
                    hashMap1.put("Project", response.getString("Project"));
                    hashMap1.put("description", response.getString("description"));
                    hashMap1.put("Duration", response.getString("Duration"));
                    hashMap1.put("totalDuration", response.getString("totalDuration"));
                    hashMap1.put("equip_appointment_id", response.getString("equip_appointment_id"));
                    hashMap1.put("equipUuniqChange", response.getString("equipUuniqChange"));
                    hashMap1.put("scheduleDate", response.getString("scheduleDate"));
                    hashMap1.put("dateAndTime", response.getString("dateAndTime"));
                    hashMap1.put("is_accept", response.getString("is_accept"));
                    hashMap1.put("ScheduleType", response.getString("ScheduleType"));

                }else if (flag.equals("SLOT_SAVE_AND_UPDATE")) {
                    System.out.println("RES-->"+response.toString());
                    setToast(response.getString("msg"));
                    if(response.getString("msg").equalsIgnoreCase("Information updated")){
                        save_lay.setVisibility(View.GONE);
                        split_lay.setVisibility(View.GONE);
                    }else {
                        save_lay.setVisibility(View.VISIBLE);
                        split_lay.setVisibility(View.VISIBLE);
                    }
                }else {
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
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

    /*public void listDisplay(JSONObject response) {
        try {

            JSONArray EQUIPListJSONArray = response.getJSONArray("TableValues");

            if (EQUIPListJSONArray.length() > 0) {


            }



        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
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
    public void showInternetDialog(Context activity, String err_msg, final String actionName, final String submodeName, final String pjtids, final String stId) {
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
                load(actionName, submodeName, pjtids, stId);
            }
        });

        dialog.show();

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


}
