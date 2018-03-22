package com.guruinfo.scm.Equipment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.guruinfo.scm.ContractorsDao;
import com.guruinfo.scm.DaoMaster;
import com.guruinfo.scm.R;
import com.guruinfo.scm.RequestedByDao;
import com.guruinfo.scm.SCMDashboardActivityLatest;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseFragment;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.db.SCMDataBaseOpenHelper;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.service.RestClientHelper;
import com.guruinfo.scm.common.ui.LeadNothingSelectedSpinnerAdapter;
import com.guruinfo.scm.common.ui.SCMTextView;
import com.guruinfo.scm.common.utils.ApiCalls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.guruinfo.scm.SCMDashboardActivityLatest.NavigationFragmentManager;

public class EquipRequestCreation extends BaseFragment {

    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day, hours, minutes;
    boolean is24hours = true;
    String RequestedBy="";
    static String uid, Cre_Id;
    SessionManager session;
    String Submode;
    public static String stageId = "";
    String projectId = "";
    String requestParameter;
    String saveRequestParameter;
    BackgroundTask backgroundTask;
    private static final String TAG = "EquipRequest";
    String listActionName = "MOBILE_EQUIPMENT_PROCESS";
    String Equip_Req_Id ="";
    String EquipId ="";
    RequestedByDao requestedByDao;
    ContractorsDao contractorsDao;
    static EditText editDateTextView;

    String equipNameSelect="";
    String equipsubWoSelect="";
    String equipsubSelect="";
    String equipReqTypeSelect="";
    String equipPrioritySelect="";
    String equipProjectSelect="";
    String equipRemainSelect="";
    String equipEquipIdSelect="";
    String equipEquipIdSelect_Id="";
    String equipReqType_Id="";
    String equipProject_Id="";
    String equipName_Id="";
    String equipRemain_Id="";
    String equipPriority_Id="";
    String equipSubCon_Id="";

    String equipNameValue ="";
    String equipName ="";
    String subConId ="";
    String subConName ="";
    String frmnameCTRL = "REQUEST_SAVE";

    private RadioButton radioButton;

    ArrayList<HashMap<String, String>> EquipmentId = new ArrayList<>();
    ArrayList<HashMap<String, String>> EquipmentName = new ArrayList<>();
    ArrayList<HashMap<String, String>> RequestType = new ArrayList<>();
    ArrayList<HashMap<String, String>> AssignTo = new ArrayList<>();
    ArrayList<HashMap<String, String>> Priority = new ArrayList<>();
    ArrayList<HashMap<String, String>> Project = new ArrayList<>();
    ArrayList<HashMap<String, String>> Remainder = new ArrayList<>();
    ArrayList<HashMap<String, String>> SubContract = new ArrayList<>();
    ArrayList<HashMap<String, String>> SubContractWo = new ArrayList<>();

    Context context;
    SCMTextView equip_slot_type, equip_regBy, notify_me, view_slot_id;
    RadioButton radio_reg, radio_allot;
    RadioGroup radio_group_id;
    Spinner equip_reg_type_spin, equip_project_spin, equip_name_spin, equip_id_spin, equip_priority_spin, equip_subcontract_spin, equip_subcon_wo_spin, equip_assignto_spin, equip_remainder_spin;
    public static  EditText equip_start_date1;
    EditText equip_description, equip_date;
    public static  TimeEditText equip_duration_time;
    CheckBox chk_sub_contract, email_chk, sms_chk, app_chk;
    TextView equip_save, equip_close;
    LinearLayout sub_contract_lay, reg_type_id, view_slot, close_lay, save_lay, date_lay, datetime_lay, split_lay;
    SCMTextView headerTitle;
    String ReqTypeValue="";

    boolean emailChk, smsChk, appChk;
    RadioButton rb;
    String EditView="";
    String SlotViewType="";
    String SplitCount="", ScheduleDuration="", MultiBookKey, saveDuration="", saveTotalDuration="";
    String Equip_Id = "";
    String Equip_Name = "";
    String Requested_by="";

    public static HashMap <String,HashMap<String, String>> BookedList1 = new HashMap <String,HashMap<String, String>>();
    ArrayList<String> Equipment_dur_date;
    ArrayList<String> Equipment_start_date;
    ArrayList<String> Equipment_end_date;
    ArrayList<String> ScheduleDate;
    public static String bookEquipId;






    public static EquipRequestCreation newInstance(Bundle bundle) {
        EquipRequestCreation fragment = new EquipRequestCreation();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub

        inflater.inflate(R.menu.list_action, menu);

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
        setHasOptionsMenu(true);

        Equipment_start_date = new ArrayList<>();
        Equipment_dur_date = new ArrayList<>();
        Equipment_end_date = new ArrayList<>();
        ScheduleDate = new ArrayList<>();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.equip_reg_creation, container, false);
        final Bundle bundle = this.getArguments();
        //setHasOptionsMenu(true);

        if (bundle != null) {


           // SlotView = bundle.getString("SLOT_TYPE");
            if(bundle.getString("SLOT_TYPE").equalsIgnoreCase("Edit")){
                EditView = bundle.getString("SLOT_TYPE");
                Equip_Req_Id = bundle.getString("EQUIPREGID");
                EquipId = bundle.getString("EQUIPID");
                load(listActionName, "EQUIPMENT_CREATION_AND_EDIT", "", "");
            }else if(bundle.getString("SLOT_TYPE").equalsIgnoreCase("BOOKED")){



            }else {
                Requested_by = bundle.getString("RequestedBy");
                load(listActionName, "EQUIPMENT_CREATION_AND_EDIT", "", "");
            }

        }
        ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Equipment Request Creation</font>"));
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);

        equip_slot_type = (SCMTextView)view.findViewById(R.id.equip_slot_type);
        equip_regBy = (SCMTextView)view.findViewById(R.id.equip_regBy);
        notify_me = (SCMTextView)view.findViewById(R.id.notify_me);
        view_slot_id = (SCMTextView)view.findViewById(R.id.view_slot_id);

        equip_reg_type_spin = (Spinner)view.findViewById(R.id.equip_reg_type_spin);
        equip_project_spin = (Spinner)view.findViewById(R.id.equip_project_spin);
        equip_name_spin = (Spinner)view.findViewById(R.id.equip_name_spin);
        equip_id_spin = (Spinner)view.findViewById(R.id.equip_id_spin);
        equip_priority_spin = (Spinner)view.findViewById(R.id.equip_priority_spin);
        equip_subcontract_spin = (Spinner)view.findViewById(R.id.equip_subcontract_spin);
        equip_subcon_wo_spin = (Spinner)view.findViewById(R.id.equip_subcon_wo_spin);
        equip_assignto_spin = (Spinner)view.findViewById(R.id.equip_assignto_spin);
        equip_remainder_spin = (Spinner)view.findViewById(R.id.equip_remainder_spin);

        radio_reg = (RadioButton)view.findViewById(R.id.radio_reg);
        radio_allot = (RadioButton)view.findViewById(R.id.radio_allot);
        radio_group_id = (RadioGroup)view.findViewById(R.id.radio_group_id);

        equip_start_date1 = (EditText)view.findViewById(R.id.equip_startdate);
        equip_date = (EditText)view.findViewById(R.id.equip_date);
        equip_duration_time = (TimeEditText)view.findViewById(R.id.equip_durationtime);

        equip_start_date1.setEnabled(false);
        equip_date.setEnabled(false);
        equip_name_spin.setEnabled(false);
        equip_duration_time.setEnabled(false);

        equip_description = (EditText)view.findViewById(R.id.equip_description);

        chk_sub_contract = (CheckBox)view.findViewById(R.id.chk_sub_contract);
        email_chk = (CheckBox)view.findViewById(R.id.email_chk);
        sms_chk = (CheckBox)view.findViewById(R.id.sms_chk);
        app_chk = (CheckBox)view.findViewById(R.id.app_chk);

        equip_close = (TextView) view.findViewById(R.id.equip_close);
        equip_save = (TextView) view.findViewById(R.id.equip_save);

        sub_contract_lay = (LinearLayout)view.findViewById(R.id.sub_contract_lay);
        reg_type_id = (LinearLayout)view.findViewById(R.id.reg_type_id);
        view_slot = (LinearLayout)view.findViewById(R.id.view_slot);
        save_lay = (LinearLayout)view.findViewById(R.id.save_lay);
        close_lay = (LinearLayout)view.findViewById(R.id.close_lay);
        date_lay = (LinearLayout)view.findViewById(R.id.date_lay);
        datetime_lay = (LinearLayout)view.findViewById(R.id.datetime_lay);
        split_lay = (LinearLayout)view.findViewById(R.id.split_lay);



        /*SCMDataBaseOpenHelper SCMDatabaseOpenHelper = new SCMDataBaseOpenHelper(context, "SCM", null);
        SQLiteDatabase db = SCMDatabaseOpenHelper.getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();*/

        requestedByDao = daoSession.getRequestedByDao();
        contractorsDao = daoSession.getContractorsDao();

        List<com.guruinfo.scm.RequestedBy> requestedByLists = requestedByDao.queryBuilder().where(RequestedByDao.Properties.Status.eq("approved"), RequestedByDao.Properties.Display.eq("active")).list();
        List<com.guruinfo.scm.Contractors> contractorsLists = contractorsDao.queryBuilder().where(ContractorsDao.Properties.Status.eq("approved"), ContractorsDao.Properties.Display.eq("active")).list();
        Log.d(TAG, "requestedByLists Size" + requestedByLists.size());
        for (int i = 0; i < requestedByLists.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("id", requestedByLists.get(i).getLoad_id());
            hashMap.put("value", requestedByLists.get(i).getValue());
            AssignTo.add(hashMap);
        }


        for (int i = 0; i < contractorsLists.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("id", contractorsLists.get(i).getLoad_id());
            hashMap.put("value", contractorsLists.get(i).getValue());
            SubContract.add(hashMap);
        }
        Log.d(TAG, "contractorsLists Size" + SubContract.size());

        save_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int errorCount = 0;
                JSONObject submitJson = new JSONObject();

                if(EditView.equalsIgnoreCase("Edit")){
                    if (radio_reg.isChecked()) {
                        if(equipReqTypeSelect.length()<0){
                            TextView errorvalue = (TextView) equip_reg_type_spin.getSelectedView();
                            errorvalue.setError("Field is Required");
                            errorCount++;
                        }else if(equipProjectSelect.length()<0){
                            TextView errorvalue = (TextView) equip_project_spin.getSelectedView();
                            errorvalue.setError("Field is Required");
                            errorCount++;
                        } else if (equipName.length() < 0) {
                            TextView errorvalue = (TextView) equip_name_spin.getSelectedView();
                            errorvalue.setError("Field is Required");
                            errorCount++;
                        } else if(ReqTypeValue.equalsIgnoreCase("scheduled")){
                            if (!(equip_start_date1.getText().toString().trim().length() > 0)) {
                                equip_start_date1.setError("Field is Required");

                                errorCount++;
                            }

                        }else if(ReqTypeValue.equalsIgnoreCase("unscheduled")){
                            if (!(equip_date.getText().toString().trim().length() > 0)) {
                                equip_date.setError("Field is Required");

                                errorCount++;
                            }

                        } else if ((equip_duration_time.getText().toString().trim()).equalsIgnoreCase("")) {
                            equip_duration_time.setError("Field is Required");
                            errorCount++;
                        }else if (chk_sub_contract.isChecked()) {

                            if (equipsubSelect.length() < 0) {
                                TextView errorvalue = (TextView) equip_subcontract_spin.getSelectedView();
                                errorvalue.setError("Field is Required");
                                errorCount++;
                            } else if (equipsubWoSelect.length()< 0) {
                                TextView errorvalue = (TextView) equip_subcon_wo_spin.getSelectedView();
                                errorvalue.setError("Field is Required");
                                errorCount++;
                            }

                        }

                    }else if (radio_allot.isChecked()) {{
                        if (equipProjectSelect.length() < 0) {
                            TextView errorvalue = (TextView) equip_project_spin.getSelectedView();
                            errorvalue.setError("Field is Required");
                            errorCount++;
                        } else if (equipName.length() < 0) {
                            TextView errorvalue = (TextView) equip_name_spin.getSelectedView();
                            errorvalue.setError("Field is Required");
                            errorCount++;
                        } else if(ReqTypeValue.equalsIgnoreCase("scheduled")){
                            if (!(equip_start_date1.getText().toString().trim().length() > 0)) {
                                equip_start_date1.setError("Field is Required");

                                errorCount++;
                            }

                        }else if(ReqTypeValue.equalsIgnoreCase("unscheduled")){
                            if (!(equip_date.getText().toString().trim().length() > 0)) {
                                equip_date.setError("Field is Required");

                                errorCount++;
                            }

                        } else if (!(equip_duration_time.getText().toString().trim().length() > 0)) {
                            equip_duration_time.setError("Field is Required");
                            errorCount++;
                        } else if ((equip_assignto_spin.getSelectedItemPosition()) - 1 < 0) {
                            TextView errorvalue = (TextView) equip_assignto_spin.getSelectedView();
                            errorvalue.setError("Field is Required");
                            errorCount++;
                        }else if (chk_sub_contract.isChecked()) {

                            if (equipsubSelect.length() < 0) {
                                TextView errorvalue = (TextView) equip_subcontract_spin.getSelectedView();
                                errorvalue.setError("Field is Required");
                                errorCount++;
                            } else if (equipsubWoSelect.length() < 0) {
                                TextView errorvalue = (TextView) equip_subcon_wo_spin.getSelectedView();
                                errorvalue.setError("Field is Required");
                                errorCount++;
                            }

                        }
                    }

                    }

                }else {
                    if (radio_reg.isChecked()) {
                        if ((equip_reg_type_spin.getSelectedItemPosition()) - 1 < 0) {
                            TextView errorvalue = (TextView) equip_reg_type_spin.getSelectedView();
                            errorvalue.setError("Field is Required");
                            errorCount++;
                        } else if ((equip_project_spin.getSelectedItemPosition()) - 1 < 0) {
                            TextView errorvalue = (TextView) equip_project_spin.getSelectedView();
                            errorvalue.setError("Field is Required");
                            errorCount++;
                        } else if ((equip_name_spin.getSelectedItemPosition()) - 1 < 0) {
                            TextView errorvalue = (TextView) equip_name_spin.getSelectedView();
                            errorvalue.setError("Field is Required");
                            errorCount++;
                        } else if(ReqTypeValue.equalsIgnoreCase("scheduled")){
                            if (!(equip_start_date1.getText().toString().trim().length() > 0)) {
                            equip_start_date1.setError("Field is Required");

                            errorCount++;
                            }

                        }else if(ReqTypeValue.equalsIgnoreCase("unscheduled")){
                            if (!(equip_date.getText().toString().trim().length() > 0)) {
                                equip_date.setError("Field is Required");

                                errorCount++;
                            }

                        } else if (!(equip_duration_time.getText().toString().trim().length() > 0)) {
                            equip_duration_time.setError("Field is Required");
                            errorCount++;
                        } else if (chk_sub_contract.isChecked()) {

                            if ((equip_subcontract_spin.getSelectedItemPosition()) - 1 < 0) {
                                TextView errorvalue = (TextView) equip_subcontract_spin.getSelectedView();
                                errorvalue.setError("Field is Required");
                                errorCount++;
                            } else if ((equip_subcon_wo_spin.getSelectedItemPosition()) - 1 < 0) {
                                TextView errorvalue = (TextView) equip_subcon_wo_spin.getSelectedView();
                                errorvalue.setError("Field is Required");
                                errorCount++;
                            }

                        }
                    } else if (radio_allot.isChecked()) {
                        if ((equip_project_spin.getSelectedItemPosition()) - 1 < 0) {
                            TextView errorvalue = (TextView) equip_project_spin.getSelectedView();
                            errorvalue.setError("Field is Required");
                            errorCount++;
                        } else if ((equip_name_spin.getSelectedItemPosition()) - 1 < 0) {
                            TextView errorvalue = (TextView) equip_name_spin.getSelectedView();
                            errorvalue.setError("Field is Required");
                            errorCount++;
                        } else if(ReqTypeValue.equalsIgnoreCase("scheduled")){
                            if (!(equip_start_date1.getText().toString().trim().length() > 0)) {
                                equip_start_date1.setError("Field is Required");

                                errorCount++;
                            }

                        }else if(ReqTypeValue.equalsIgnoreCase("unscheduled")){
                            if (!(equip_date.getText().toString().trim().length() > 0)) {
                                equip_date.setError("Field is Required");

                                errorCount++;
                            }

                        } else if (!(equip_duration_time.getText().toString().trim().length() > 0)) {
                            equip_duration_time.setError("Field is Required");
                            errorCount++;
                        } else if ((equip_assignto_spin.getSelectedItemPosition()) - 1 < 0) {
                            TextView errorvalue = (TextView) equip_assignto_spin.getSelectedView();
                            errorvalue.setError("Field is Required");
                            errorCount++;
                        }else if (chk_sub_contract.isChecked()) {

                            if ((equip_subcontract_spin.getSelectedItemPosition()) - 1 < 0) {
                                TextView errorvalue = (TextView) equip_subcontract_spin.getSelectedView();
                                errorvalue.setError("Field is Required");
                                errorCount++;
                            } else if ((equip_subcon_wo_spin.getSelectedItemPosition()) - 1 < 0) {
                                TextView errorvalue = (TextView) equip_subcon_wo_spin.getSelectedView();
                                errorvalue.setError("Field is Required");
                                errorCount++;
                            }

                        }else {
                            /*System.out.println("Date"+equip_start_date1.getText().toString());
                            System.out.println("Date"+equip_duration_time.getText().toString());*/
                        }
                    }


                }

                if (errorCount == 0) {
                    System.out.println("errorCount: " + errorCount);

                    if (BookedList1.size() > 0) {
                        HashMap<String, String> Map = new HashMap<String, String>();
                        HashMap<String, String> Map1 = new HashMap<String, String>();
                        Iterator myVeryOwnIterator = BookedList1.keySet().iterator();
                        while (myVeryOwnIterator.hasNext()) {
                            String key = (String) myVeryOwnIterator.next();
                            if (key.equalsIgnoreCase("Bundle")) {
                                Map1 = (HashMap<String, String>) BookedList1.get(key);
                            } else {
                                Map = (HashMap<String, String>) BookedList1.get(key);
                                //System.out.println("Key: "+key+" Value: "+Map);
                                String datesplit = Map.get("title") + "," + (Map.get("title")).replaceFirst("_(.*)", "") + "_" + Map.get("slotEndTime");
                                String startdatesplit = "";
                                if (!bookEquipId.equalsIgnoreCase("")) {
                                    startdatesplit = Map.get("title") + "#" + bookEquipId;
                                } else {
                                    startdatesplit = Map.get("title") + "#" + Map.get("equipUniq");
                                }
                                String enddatesplit = (Map.get("title")).replaceFirst("_(.*)", "") + "_" + Map.get("slotEndTime");
                                //System.out.println("@@@: "+datesplit +"=="+startdatesplit+"==="+enddatesplit);
                                ScheduleDate.add(key);
                                Equipment_dur_date.add(datesplit);
                                Equipment_start_date.add(startdatesplit);
                                Equipment_end_date.add(enddatesplit);
                            }

                        }

                        SlotViewType = Map1.get("SLOT_TYPE");
                        // Equip_Id = bundle.getString("EQUIPID");
                        SplitCount = Map1.get("SPLITCOUNT");
                        //Equip_Req_Id = bundle.getString("EQUIPREQID");
                        // Equip_Name = bundle.getString("EQUIPNAME");
                        MultiBookKey = Map1.get("MULTIBOOK");

                        if (SplitCount.equalsIgnoreCase("1")) {
                            saveDuration = "60";
                            saveTotalDuration = "" + (BookedList1.size() - 1) * 60;
                        } else if (SplitCount.equalsIgnoreCase("2")) {
                            saveDuration = "30";
                            saveTotalDuration = "" + (BookedList1.size() - 1) * 30;
                        } else if (SplitCount.equalsIgnoreCase("4")) {
                            saveDuration = "15";
                            saveTotalDuration = "" + (BookedList1.size() - 1) * 15;
                        }
                        int hours = Integer.parseInt(saveTotalDuration) / 60;
                        int minutes = Integer.parseInt(saveTotalDuration) % 60;
                        if (hours > 10) {
                            if (minutes > 0)
                                ScheduleDuration = "" + hours + ":" + minutes;
                            else
                                ScheduleDuration = "" + hours + ":0" + minutes;
                        } else {
                            if (minutes > 0)
                                ScheduleDuration = "0" + hours + ":" + minutes;
                            else
                                ScheduleDuration = "0" + hours + ":0" + minutes;
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
                    }

                    if (EditView.equalsIgnoreCase("Edit")) {
                        try {


                            JSONObject reqObj = new JSONObject();
                            String formName = "group";
                            reqObj.put("Action", listActionName);
                            reqObj.put("submode", "SLOT_SAVE_AND_UPDATE");
                            reqObj.put("Cre_Id", Cre_Id);
                            reqObj.put("UID", uid);
                            reqObj.put("equip_appointment_id", Equip_Req_Id);
                            reqObj.put("frmnameCTRL", frmnameCTRL);
                            if (radio_reg.isChecked()) {
                                reqObj.put(frmnameCTRL + "_EquipmentSlotType", "Request");
                                reqObj.put(frmnameCTRL + "_RequestType", RequestType.get((int) equip_reg_type_spin.getSelectedItemId()).get("id"));
                            } else {
                                reqObj.put(frmnameCTRL + "_EquipmentSlotType", "Allot");
                                reqObj.put(frmnameCTRL + "_RequestType", "");
                            }
                            try {
                                reqObj.put(frmnameCTRL + "_EquipmentId", EquipmentId.get((int) equip_id_spin.getSelectedItemId()).get("id"));
                            } catch (Exception e) {
                                e.printStackTrace();
                                reqObj.put(frmnameCTRL + "_EquipmentId", "");
                            }


                            reqObj.put(frmnameCTRL + "_Duration", equip_duration_time.getText().toString());
                            reqObj.put(frmnameCTRL + "_Description", equip_description.getText().toString());
                            reqObj.put(frmnameCTRL + "_Project", Project.get((int) equip_project_spin.getSelectedItemId()).get("id"));
                            if (equip_regBy.getText().toString().equalsIgnoreCase(""))
                                reqObj.put(frmnameCTRL + "_Requestedby", uid);
                            else
                                reqObj.put(frmnameCTRL + "_Requestedby", equip_regBy.getText().toString());
                            reqObj.put(frmnameCTRL + "_EquipmentNames", EquipmentName.get((int) equip_name_spin.getSelectedItemId()).get("id"));
                            reqObj.put(frmnameCTRL + "_total_duration", saveTotalDuration);
                            try {
                                reqObj.put(frmnameCTRL + "_Priority", Priority.get((int) equip_priority_spin.getSelectedItemId()).get("id"));
                                reqObj.put(frmnameCTRL + "_Reminder", Remainder.get((int) equip_remainder_spin.getSelectedItemId()).get("id"));
                            } catch (Exception e) {
                                e.printStackTrace();
                                reqObj.put(frmnameCTRL + "_Priority", "");
                                reqObj.put(frmnameCTRL + "_Reminder", "");
                            }

                            if (email_chk.isChecked())
                                reqObj.put(frmnameCTRL + "_emailNotifyMe", "1");
                            else
                                reqObj.put(frmnameCTRL + "_emailNotifyMe", "0");
                            if (sms_chk.isChecked())
                                reqObj.put(frmnameCTRL + "_smsNotifyMe", "1");
                            else
                                reqObj.put(frmnameCTRL + "_smsNotifyMe", "0");
                            if (app_chk.isChecked())
                                reqObj.put(frmnameCTRL + "_appNotifyMe", "1");
                            else
                                reqObj.put(frmnameCTRL + "_appNotifyMe", "0");
                            reqObj.put(frmnameCTRL + "_ReqeustTo", "");
                            reqObj.put(frmnameCTRL + "_SlotConfigId", "");
                            reqObj.put(frmnameCTRL + "_IsMultiBooked", MultiBookKey);
                            if (chk_sub_contract.isChecked()) {
                                reqObj.put(frmnameCTRL + "_isSubContractor", "1");
                                reqObj.put(frmnameCTRL + "_contractorName", SubContract.get((int) equip_subcontract_spin.getSelectedItemId()).get("id"));
                            } else {
                                reqObj.put(frmnameCTRL + "_isSubContractor", "0");
                                reqObj.put(frmnameCTRL + "_contractorName", "0");
                            }
                            reqObj.put(frmnameCTRL + "_workOrder", "");

                            if(RequestType.get((int) equip_reg_type_spin.getSelectedItemId()).get("id").equalsIgnoreCase("unscheduled")){
                                reqObj.put(frmnameCTRL + "_DateTime", equip_date.getText().toString());
                                reqObj.put(frmnameCTRL + "_pickupdate", equip_date.getText().toString());
                                reqObj.put(frmnameCTRL + "_equipment_dur_date", "");
                                reqObj.put(frmnameCTRL + "_equipment_start_date", "");
                                reqObj.put(frmnameCTRL + "_equipment_end_date", "");
                                reqObj.put(frmnameCTRL + "_equipment_duration", "");
                            }else {
                                reqObj.put(frmnameCTRL + "_DateTime", equip_start_date1.getText().toString());
                                reqObj.put(frmnameCTRL + "_pickupdate", equip_start_date1.getText().toString());
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

                                reqObj.put(frmnameCTRL + "_equipment_dur_date", equipment_dur_date);
                                reqObj.put(frmnameCTRL + "_equipment_start_date", equipment_start_date);
                                reqObj.put(frmnameCTRL + "_equipment_end_date", equipment_end_date);
                                reqObj.put(frmnameCTRL + "_equipment_duration", saveDuration);
                            }

                            String reqParam = reqObj.toString();
                            System.out.println("reqParam--->" + reqParam);
                            //onListLoad(reqParam, "");
                            onSaveList(reqParam, "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        try {


                            JSONObject reqObj = new JSONObject();
                            String formName = "group";
                            reqObj.put("Action", listActionName);
                            reqObj.put("submode", "SLOT_SAVE_AND_UPDATE");
                            reqObj.put("Cre_Id", Cre_Id);
                            reqObj.put("UID", uid);
                            reqObj.put("equip_appointment_id", "");
                            reqObj.put("frmnameCTRL", frmnameCTRL);
                            if (radio_reg.isChecked()) {
                                reqObj.put(frmnameCTRL + "_EquipmentSlotType", "Request");
                                reqObj.put(frmnameCTRL + "_RequestType", RequestType.get((int) equip_reg_type_spin.getSelectedItemId()).get("id"));
                            } else {
                                reqObj.put(frmnameCTRL + "_EquipmentSlotType", "Allot");
                                reqObj.put(frmnameCTRL + "_RequestType", "");
                            }
                            try {
                                reqObj.put(frmnameCTRL + "_EquipmentId", EquipmentId.get((int) equip_id_spin.getSelectedItemId()).get("id"));
                            } catch (Exception e) {
                                e.printStackTrace();
                                reqObj.put(frmnameCTRL + "_EquipmentId", "");
                            }


                            reqObj.put(frmnameCTRL + "_Duration", equip_duration_time.getText().toString());
                            reqObj.put(frmnameCTRL + "_Description", equip_description.getText().toString());
                            reqObj.put(frmnameCTRL + "_Project", Project.get((int) equip_project_spin.getSelectedItemId()).get("id"));
                            if (equip_regBy.getText().toString().equalsIgnoreCase(""))
                                reqObj.put(frmnameCTRL + "_Requestedby", uid);
                            else
                                reqObj.put(frmnameCTRL + "_Requestedby", equip_regBy.getText().toString());
                            reqObj.put(frmnameCTRL + "_EquipmentNames", EquipmentName.get((int) equip_name_spin.getSelectedItemId()).get("id"));
                            reqObj.put(frmnameCTRL + "_total_duration", saveTotalDuration);
                            try {
                                reqObj.put(frmnameCTRL + "_Priority", Priority.get((int) equip_priority_spin.getSelectedItemId()).get("id"));
                                reqObj.put(frmnameCTRL + "_Reminder", Remainder.get((int) equip_remainder_spin.getSelectedItemId()).get("id"));
                            } catch (Exception e) {
                                e.printStackTrace();
                                reqObj.put(frmnameCTRL + "_Priority", "");
                                reqObj.put(frmnameCTRL + "_Reminder", "");
                            }

                            if (email_chk.isChecked())
                                reqObj.put(frmnameCTRL + "_emailNotifyMe", "1");
                            else
                                reqObj.put(frmnameCTRL + "_emailNotifyMe", "0");
                            if (sms_chk.isChecked())
                                reqObj.put(frmnameCTRL + "_smsNotifyMe", "1");
                            else
                                reqObj.put(frmnameCTRL + "_smsNotifyMe", "0");
                            if (app_chk.isChecked())
                                reqObj.put(frmnameCTRL + "_appNotifyMe", "1");
                            else
                                reqObj.put(frmnameCTRL + "_appNotifyMe", "0");
                            reqObj.put(frmnameCTRL + "_ReqeustTo", "");
                            reqObj.put(frmnameCTRL + "_SlotConfigId", "");
                            reqObj.put(frmnameCTRL + "_IsMultiBooked", MultiBookKey);
                            if (chk_sub_contract.isChecked()) {
                                reqObj.put(frmnameCTRL + "_isSubContractor", "1");
                                reqObj.put(frmnameCTRL + "_contractorName", SubContract.get((int) equip_subcontract_spin.getSelectedItemId()).get("id"));
                            } else {
                                reqObj.put(frmnameCTRL + "_isSubContractor", "0");
                                reqObj.put(frmnameCTRL + "_contractorName", "0");
                            }
                            reqObj.put(frmnameCTRL + "_workOrder", "");
                            if(RequestType.get((int) equip_reg_type_spin.getSelectedItemId()).get("id").equalsIgnoreCase("unscheduled")){
                                reqObj.put(frmnameCTRL + "_DateTime", equip_date.getText().toString());
                                reqObj.put(frmnameCTRL + "_pickupdate",  equip_date.getText().toString());
                                reqObj.put(frmnameCTRL + "_equipment_dur_date", "");
                                reqObj.put(frmnameCTRL + "_equipment_start_date", "");
                                reqObj.put(frmnameCTRL + "_equipment_end_date", "");
                                reqObj.put(frmnameCTRL + "_equipment_duration", "");
                            }else {
                                reqObj.put(frmnameCTRL + "_DateTime", equip_start_date1.getText().toString());
                                reqObj.put(frmnameCTRL + "_pickupdate",  equip_start_date1.getText().toString());
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

                                reqObj.put(frmnameCTRL + "_equipment_dur_date", equipment_dur_date);
                                reqObj.put(frmnameCTRL + "_equipment_start_date", equipment_start_date);
                                reqObj.put(frmnameCTRL + "_equipment_end_date", equipment_end_date);
                                reqObj.put(frmnameCTRL + "_equipment_duration", saveDuration);
                            }

                            String reqParam = reqObj.toString();
                            System.out.println("reqParam--->" + reqParam);
                            //onListLoad(reqParam, "");
                            onSaveList(reqParam, "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
        });

        view_slot_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!equipName.equalsIgnoreCase("")){
                    /*((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#2d2e3a'>Equipment Slot View</font>"));
                    NavigationFragmentManager(Equip_Slot_View.newInstance(bundle1), "Equip");*/
                    Intent intent = new Intent(context, EquipSlotView.class);
                    Bundle extras = new Bundle();
                    extras.putString("EQUIPREQID", "");
                    extras.putString("EQUIPREQID_SEL", equipNameValue);
                    if(!equipEquipIdSelect.equalsIgnoreCase(""))
                        extras.putString("EQUIPID",equipEquipIdSelect);
                    else
                        extras.putString("EQUIPID","");
                    extras.putString("EQUIPNAME",equipName);
                    intent.putExtras(extras);
                    startActivity(intent);

                }else if(!equipNameValue.equalsIgnoreCase("")){
                    Intent intent = new Intent(context, EquipSlotView.class);
                    Bundle extras = new Bundle();
                    extras.putString("EQUIPREQID", "");
                    extras.putString("EQUIPREQID_SEL", equipNameValue);
                    if(!equipEquipIdSelect.equalsIgnoreCase(""))
                        extras.putString("EQUIPID",equipEquipIdSelect);
                    else
                        extras.putString("EQUIPID","");
                    extras.putString("EQUIPNAME",equipName);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            }
        });

        if(radio_reg.isChecked()) {
            reg_type_id.setVisibility(View.VISIBLE);
            equip_assignto_spin.setEnabled(false);
        }else {
            reg_type_id.setVisibility(View.GONE);



        }


        equip_start_date1.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                new TimePickerDialog(context, time, hours, minutes, is24hours).show();
                //new DatePickerDialog(context, date, year, month, day).show();
                editDateTextView = equip_start_date1;
                DialogFragment newFragment = new SelectDateFragment();

                newFragment.show(getFragmentManager(), "DatePicker");

                /*if(ReqTypeValue.equalsIgnoreCase("Unscheduled")) {

                }else{
                    equip_start_date1.setClickable(false);

                }*/
            }
        });

        equip_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDateTextView = equip_date;
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        equip_project_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (position > 0) {
                    int selectSpinnerPosition = (equip_project_spin.getSelectedItemPosition()) - 1;

                    equip_name_spin.setEnabled(true);


                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        equip_name_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (position > 0) {
                    int selectSpinnerPosition = (equip_name_spin.getSelectedItemPosition()) - 1;

                   equipNameValue = EquipmentName.get(selectSpinnerPosition).get("id");
                    equipName = EquipmentName.get(selectSpinnerPosition).get("value");
                    requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'AJAX_LOAD','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','actionKey':'EquipmentUnique','equip_id':'"+equipNameValue+"','proj_id':'','RequestCreation':'TRUE'}";
                    onListLoad(requestParameter,"AJAX_LOAD_EquipID");

                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        equip_id_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (position > 0) {
                    int selectSpinnerPosition = (equip_id_spin.getSelectedItemPosition()) - 1;


                    equipEquipIdSelect = EquipmentId.get(selectSpinnerPosition).get("value");


                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        equip_subcontract_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (position > 0) {
                    int selectSpinnerPosition = (equip_subcontract_spin.getSelectedItemPosition()) - 1;

                    subConId = SubContract.get(selectSpinnerPosition).get("id");
                    subConName = SubContract.get(selectSpinnerPosition).get("value");
                    requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'AJAX_LOAD','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','actionKey':'SubContractorWoLoads','proj_id':'','contr_id':'"+subConId+"','wo_id':''}";
                    onListLoad(requestParameter,"AJAX_LOAD");
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        equip_reg_type_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (position > 0) {
                    int selectSpinnerPosition = (equip_reg_type_spin.getSelectedItemPosition()) - 1;
                    ReqTypeValue = RequestType.get(selectSpinnerPosition).get("id");


                    if(RequestType .get(selectSpinnerPosition).get("id").equalsIgnoreCase("Unscheduled")){

                        equip_start_date1.setEnabled(true);
                        equip_duration_time.setEnabled(true);
                        equip_date.setEnabled(true);
                        equip_id_spin.setEnabled(false);
                        view_slot_id.setVisibility(View.GONE);
                        date_lay.setVisibility(View.VISIBLE);
                        datetime_lay.setVisibility(View.GONE);

                    }else {
                        equip_start_date1.setEnabled(false);
                        equip_duration_time.setEnabled(false);
                        equip_date.setEnabled(false);
                        equip_id_spin.setEnabled(true);
                        view_slot_id.setVisibility(View.VISIBLE);
                        datetime_lay.setVisibility(View.VISIBLE);
                        date_lay.setVisibility(View.GONE);
                    }

                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        close_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>EQUIPMENT</font>"));
                NavigationFragmentManager(EquipListFragment.newInstance(null), "Equip");
            }
        });


        radio_group_id.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                rb = (RadioButton) group.findViewById(checkedId);
                if(rb!=null && checkedId>-1){

                    if(rb.getText().equals("Request")){
                        loadLeadStyleSpinner(equip_reg_type_spin, RequestType , new String[]{"value"}, "Request Type");
                        loadLeadStyleSpinner(equip_assignto_spin, AssignTo , new String[]{"value"}, "Assign To");
                        reg_type_id.setVisibility(View.VISIBLE);
                        equip_assignto_spin.setEnabled(false);
                        view_slot.setVisibility(View.VISIBLE);
                    }else {

                        reg_type_id.setVisibility(View.GONE);
                        equip_assignto_spin.setEnabled(true);
                        equip_start_date1.setEnabled(false);
                        equip_duration_time.setEnabled(false);
                        view_slot.setVisibility(View.VISIBLE);


                    }
                }
            }
        });

        chk_sub_contract.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sub_contract_lay.setVisibility(View.VISIBLE);
                } else {
                    sub_contract_lay.setVisibility(View.GONE);
                }
            }
        });


        return view;

    }





    public void getLoadData(final String requestParameter, final String flag) {
        Log.d(TAG, requestParameter);
        backgroundTask = new BackgroundTask(context, flag, new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String values, String flagMsg) {
                try {
                    if (flagMsg.equals("internet")) {
                        //showInternetDialog(context, values, requestParameter, flag);
                    } else {
                        JSONObject jsonObject = new JSONObject(values);
                        //parseJSONResponse(jsonObject, flag);
                    }
                } catch (JSONException e) {
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


    public void load(final String action, final String submode, String pjtId, String stgeId) {
        this.Submode = submode;
        this.projectId = pjtId;
        this.stageId = stgeId;
        if(EditView.equalsIgnoreCase("Edit")){
            requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'" + submode + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','equip_appointment_id':'"+Equip_Req_Id+"'}";
        }else {
            requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'" + submode + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','equip_appointment_id':''}";
        }
        Log.d(TAG, requestParameter);
        onListLoad(requestParameter, submode);
        /*backgroundTask = new BackgroundTask(this.getActivity(), Submode, new OnTaskCompleted() {
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
        backgroundTask.execute("", "", requestParameter);*/
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
                if (flag.equals("EQUIPMENT_CREATION_AND_EDIT")) {
                    JSONObject genInforObj = response.getJSONObject("generalInformation");

                    if(!Requested_by.equalsIgnoreCase("")){
                        equip_regBy.setText(Requested_by);
                    }else {
                        equip_regBy.setText(genInforObj.getString("requestedBy"));
                    }
                    equip_description.setText(genInforObj.getString("description"));
                    if(!(genInforObj.getString("duration")).equalsIgnoreCase("00:00")){
                        equip_duration_time.setText(genInforObj.getString("duration"));
                    }

                    if(genInforObj.getString("equipmentSlotTypeRequest").equalsIgnoreCase("true")){
                        radio_reg.setChecked(true);
                    }
                    if(genInforObj.getString("equipmentSlotTypeAllot").equalsIgnoreCase("true")){
                        radio_allot.setChecked(true);
                    }



                    JSONObject notifyObj = genInforObj.getJSONObject("notifyMe");
                    if(notifyObj.getString("EMAIL").equalsIgnoreCase("true")){
                        email_chk.setChecked(true);
                    }
                    if(notifyObj.getString("APP").equalsIgnoreCase("true")){
                        app_chk.setChecked(true);
                    }
                    if(notifyObj.getString("SMS").equalsIgnoreCase("true")){
                        sms_chk.setChecked(true);
                    }


                    JSONArray EquipNameArray = genInforObj.getJSONArray("equipmentNames");
                    EquipmentName = ApiCalls.getArraylistfromJson(EquipNameArray.toString());


                    JSONObject EquipIdObj = genInforObj.getJSONObject("equipmentId");
                    JSONArray EquipIdArray = EquipIdObj.getJSONArray("values");
                    EquipmentId = ApiCalls.getArraylistfromJson(EquipIdArray.toString());
                    //System.out.println("Equipment: "+EquipIdObj);


                    JSONArray EquipRemainArray = genInforObj.getJSONArray("remainder");
                    Remainder = ApiCalls.getArraylistfromJson(EquipRemainArray.toString());


                    JSONArray EquipProjectArray = genInforObj.getJSONArray("project");
                    Project = ApiCalls.getArraylistfromJson(EquipProjectArray.toString());


                    JSONArray EquipPriorityArray = genInforObj.getJSONArray("priority");
                    Priority = ApiCalls.getArraylistfromJson(EquipPriorityArray.toString());


                    JSONArray EquipReqTypeArray = genInforObj.getJSONArray("requestType");
                    RequestType  = ApiCalls.getArraylistfromJson(EquipReqTypeArray.toString());

                   /* JSONArray EquipSubConArray = genInforObj.getJSONArray("subContractor");
                    SubContract = ApiCalls.getArraylistfromJson(EquipSubConArray.toString());

                    JSONArray EquipSubConWoArray = genInforObj.getJSONArray("subContractorWo");
                    SubContractWo = ApiCalls.getArraylistfromJson(EquipSubConWoArray.toString());


                    if(genInforObj.getString("isSubCont").equalsIgnoreCase("true")) {
                        chk_sub_contract.setChecked(true);
                        sub_contract_lay.setVisibility(View.VISIBLE);
                        for (int i = 0; i < EquipSubConArray.length(); i++) {
                            if (    EquipSubConArray.getJSONObject(i).getString("selected").equalsIgnoreCase("true")) {
                                equipsubSelect = EquipSubConArray.getJSONObject(i).getString("value");
                            }
                        }


                        for (int i = 0; i < EquipSubConWoArray.length(); i++) {
                            if (EquipSubConWoArray.getJSONObject(i).getString("selected").equalsIgnoreCase("true")) {
                                equipsubWoSelect = EquipSubConWoArray.getJSONObject(i).getString("value");
                            }
                        }
                    }*/


                    if(!Equip_Req_Id.equalsIgnoreCase("")) {
                        System.out.println("PRO: "+equipProjectSelect);
                        loadLeadStyleSpinner(equip_subcon_wo_spin, SubContractWo, new String[]{"value"}, "Sub-Contractor Wo");
                        loadLeadStyleSpinner(equip_subcontract_spin, SubContract, new String[]{"value"}, "Sub-Contractor");
                        loadLeadStyleSpinner(equip_reg_type_spin, RequestType , new String[]{"value"}, "Request Type");
                        loadLeadStyleSpinner(equip_priority_spin, Priority, new String[]{"value"}, "Priority");
                        loadLeadStyleSpinner(equip_project_spin, Project, new String[]{"value"}, "Project");
                        loadLeadStyleSpinner(equip_remainder_spin, Remainder, new String[]{"value"}, "Reminder");
                        loadLeadStyleSpinner(equip_id_spin, EquipmentId, new String[]{"value"}, "Equipment Id");
                        loadLeadStyleSpinner(equip_name_spin, EquipmentName, new String[]{"value"}, "Equipment Names");
                        loadLeadStyleSpinner(equip_assignto_spin, AssignTo , new String[]{"value"}, "Assign To");

                        for (int i=0; i<EquipNameArray.length(); i++){
                            if(EquipNameArray.getJSONObject(i).getString("selected").equalsIgnoreCase("true")){
                                equipName = EquipNameArray.getJSONObject(i).getString("value");
                                equipNameValue = EquipNameArray.getJSONObject(i).getString("id");
                                equip_name_spin.setSelection(i+1);
                            }
                        }
                        for (int i=0; i<EquipIdArray.length(); i++){
                            if(EquipIdArray.getJSONObject(i).getString("value").equalsIgnoreCase(EquipId)){
                                equipEquipIdSelect = EquipIdArray.getJSONObject(i).getString("value");
                                equipEquipIdSelect_Id = EquipIdArray.getJSONObject(i).getString("id");
                                equip_id_spin.setSelection(i+1);
                            }
                        }
                        for (int i=0; i<EquipRemainArray.length(); i++){
                            if(EquipRemainArray.getJSONObject(i).getString("selected").equalsIgnoreCase("true")){
                                equipRemainSelect = EquipRemainArray.getJSONObject(i).getString("value");
                                equipRemain_Id = EquipRemainArray.getJSONObject(i).getString("id");
                                equip_remainder_spin.setSelection(i+1);
                            }
                        }
                        for (int i=0; i<EquipProjectArray.length(); i++){
                            if(EquipProjectArray.getJSONObject(i).getString("selected").equalsIgnoreCase("true")){
                                equipProjectSelect = EquipProjectArray.getJSONObject(i).getString("value");
                                equipProject_Id = EquipProjectArray.getJSONObject(i).getString("id");
                                equip_project_spin.setSelection(i+1);
                            }
                        }
                        for (int i=0; i<EquipPriorityArray.length(); i++){
                            if(EquipPriorityArray.getJSONObject(i).getString("selected").equalsIgnoreCase("true")){
                                equipPrioritySelect = EquipPriorityArray.getJSONObject(i).getString("value");
                                equipPriority_Id = EquipPriorityArray.getJSONObject(i).getString("id");
                                equip_priority_spin.setSelection(i+1);
                            }
                        }
                        for (int i=0; i<EquipReqTypeArray.length(); i++){
                            if(EquipReqTypeArray.getJSONObject(i).getString("selected").equalsIgnoreCase("true")){
                                equipReqTypeSelect = EquipReqTypeArray.getJSONObject(i).getString("value");
                                equipReqType_Id = EquipReqTypeArray.getJSONObject(i).getString("id");
                                equip_reg_type_spin.setSelection(i+1);

                            }
                        }
                        if(equipReqTypeSelect.equalsIgnoreCase("scheduled")) {
                            equip_duration_time.setEnabled(false);
                            equip_start_date1.setText(genInforObj.getString("dateTime"));
                            equip_id_spin.setEnabled(true);
                        }
                        if(equipReqTypeSelect.equalsIgnoreCase("Unscheduled")) {
                            view_slot_id.setVisibility(View.GONE);
                            equip_date.setText(genInforObj.getString("dateTime"));
                            equip_id_spin.setEnabled(false);
                        }
                    }else {
                        loadLeadStyleSpinner(equip_subcon_wo_spin, SubContractWo, new String[]{"value"}, "Sub-Contractor Wo");
                        loadLeadStyleSpinner(equip_subcontract_spin, SubContract, new String[]{"value"}, "Sub-Contractor");
                        loadLeadStyleSpinner(equip_reg_type_spin, RequestType , new String[]{"value"}, "Request Type");
                        loadLeadStyleSpinner(equip_priority_spin, Priority, new String[]{"value"}, "Priority");
                        loadLeadStyleSpinner(equip_project_spin, Project, new String[]{"value"}, "Project");
                        loadLeadStyleSpinner(equip_remainder_spin, Remainder, new String[]{"value"}, "Reminder");
                        loadLeadStyleSpinner(equip_id_spin, EquipmentId, new String[]{"value"}, "Equipment Id");
                        loadLeadStyleSpinner(equip_name_spin, EquipmentName, new String[]{"value"}, "Equipment Names");
                        loadLeadStyleSpinner(equip_assignto_spin, AssignTo , new String[]{"value"}, "Assign To");

                        for (int i=0; i<EquipNameArray.length(); i++){
                            if(EquipNameArray.getJSONObject(i).getString("selected").equalsIgnoreCase("true")){
                                equipName = EquipNameArray.getJSONObject(i).getString("value");
                                equipNameValue = EquipNameArray.getJSONObject(i).getString("id");

                            }
                        }
                        for (int i=0; i<EquipIdArray.length(); i++){
                            if(EquipIdArray.getJSONObject(i).getString("value").equalsIgnoreCase(EquipId)){
                                equipEquipIdSelect = EquipIdArray.getJSONObject(i).getString("value");
                                equipEquipIdSelect_Id = EquipIdArray.getJSONObject(i).getString("id");

                            }
                        }
                        for (int i=0; i<EquipRemainArray.length(); i++){
                            if(EquipRemainArray.getJSONObject(i).getString("selected").equalsIgnoreCase("true")){
                                equipRemainSelect = EquipRemainArray.getJSONObject(i).getString("value");
                                equipRemain_Id = EquipRemainArray.getJSONObject(i).getString("id");

                            }
                        }
                        for (int i=0; i<EquipProjectArray.length(); i++){
                            if(EquipProjectArray.getJSONObject(i).getString("selected").equalsIgnoreCase("true")){
                                equipProjectSelect = EquipProjectArray.getJSONObject(i).getString("value");
                                equipProject_Id = EquipProjectArray.getJSONObject(i).getString("id");

                            }
                        }
                        for (int i=0; i<EquipPriorityArray.length(); i++){
                            if(EquipPriorityArray.getJSONObject(i).getString("selected").equalsIgnoreCase("true")){
                                equipPrioritySelect = EquipPriorityArray.getJSONObject(i).getString("value");
                                equipPriority_Id = EquipPriorityArray.getJSONObject(i).getString("id");

                            }
                        }
                        for (int i=0; i<EquipReqTypeArray.length(); i++){
                            if(EquipReqTypeArray.getJSONObject(i).getString("selected").equalsIgnoreCase("true")){
                                equipReqTypeSelect = EquipReqTypeArray.getJSONObject(i).getString("value");
                                equipReqType_Id = EquipReqTypeArray.getJSONObject(i).getString("id");


                            }
                        }
                        if(equipReqTypeSelect.equalsIgnoreCase("scheduled"))
                            equip_duration_time.setEnabled(false);
                        if(equipReqTypeSelect.equalsIgnoreCase("Unscheduled"))
                            view_slot_id.setVisibility(View.GONE);
                    }

                }else if (flag.equals("AJAX_LOAD")) {

                    JSONArray EquipSubConWoArray = response.getJSONArray("values");
                    SubContractWo = ApiCalls.getArraylistfromJson(EquipSubConWoArray.toString());

                        for (int i = 0; i < EquipSubConWoArray.length(); i++) {
                            if (EquipSubConWoArray.getJSONObject(i).getString("selected").equalsIgnoreCase("true")) {
                                equipsubWoSelect = EquipSubConWoArray.getJSONObject(i).getString("value");
                            }
                        }

                    if(!Equip_Req_Id.equalsIgnoreCase("")) {
                        loadLeadStyleSpinner(equip_subcon_wo_spin, SubContractWo, new String[]{"value"}, equipsubWoSelect);
                    }else {
                        loadLeadStyleSpinner(equip_subcon_wo_spin, SubContractWo, new String[]{"value"}, "Sub-Contractor Wo");
                    }

                }else if(flag.equals("AJAX_LOAD_EquipID")){
                    JSONArray EquipIdArray = response.getJSONArray("values");
                    EquipmentId = ApiCalls.getArraylistfromJson(EquipIdArray.toString());
                    //System.out.println("Equipment: "+EquipIdObj);
                    for (int i=0; i<EquipIdArray.length(); i++){
                        if(EquipIdArray.getJSONObject(i).getString("value").equalsIgnoreCase(EquipId)){
                            equipEquipIdSelect = EquipIdArray.getJSONObject(i).getString("value");
                            equipEquipIdSelect_Id = EquipIdArray.getJSONObject(i).getString("id");
                        }
                    }
                    if(!Equip_Req_Id.equalsIgnoreCase("")) {
                        loadLeadStyleSpinner(equip_id_spin, EquipmentId, new String[]{"value"}, equipEquipIdSelect);
                    }else {
                        loadLeadStyleSpinner(equip_id_spin, EquipmentId, new String[]{"value"}, "Equipment Id");
                    }
                }else {
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));

                    /*if(response.getString("msg").contains("Information updated")){
                        save_lay.setVisibility(View.GONE);
                        split_lay.setVisibility(View.GONE);
                    }else {
                        save_lay.setVisibility(View.VISIBLE);
                        split_lay.setVisibility(View.VISIBLE);
                    }*/
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
    public static void loadLeadStyleSpinner(Spinner spin, ArrayList<HashMap<String, String>> resplist, String[] from, String txtStr) {
        SimpleAdapter listAdapter = new SimpleAdapter(BaseFragment.context, resplist, R.layout.lead_spinner_text, from, new int[]{android.R.id.text1});
        listAdapter.setDropDownViewResource(R.layout.lead_spinner_item_text);
        spin.setAdapter(listAdapter);
        spin.setSelection(-1);
        spin.getSelectedView();
        spin.setAdapter(new LeadNothingSelectedSpinnerAdapter(listAdapter, R.layout.lead_spinner_row_nothing_selected, BaseFragment.context, txtStr));
    }

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = 2000, mm = 01, dd = 01;
            if (editDateTextView.getText().toString().trim().length() > 0) {
                String date[] = editDateTextView.getText().toString().split("/");
                String year_date = date[2].replaceFirst(" (.*)","");
                dd = Integer.parseInt(date[0]);
                mm = (Integer.parseInt(date[1])-1);
                yy = Integer.parseInt(year_date);
            } else {
                yy = calendar.get(Calendar.YEAR);
                mm = calendar.get(Calendar.MONTH);
                dd = calendar.get(Calendar.DAY_OF_MONTH);
            }
            DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, yy, mm, dd);
            DatePicker dp = dpd.getDatePicker();
            dp.setMinDate(calendar.getTimeInMillis());
            return dpd;
        }
        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            /*Time chosenDate = new Time();
            chosenDate.set(dd, mm, yy);*/
            populateSetDate(yy, mm + 1, dd);
        }
        public void populateSetDate(int year, int month, int day) {
            NumberFormat formatter = new DecimalFormat("00");
            editDateTextView.setText(formatter.format(day) + "/" + formatter.format(month) + "/" + year);
            editDateTextView.setError(null);
        }
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }


    };

    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            updateLabel();
        }
    };

    private void updateLabel() {
        String myFormat = "dd/MM/yy HH:mm:00"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        equip_start_date1.setText(sdf.format(calendar.getTime()));

    }


}
