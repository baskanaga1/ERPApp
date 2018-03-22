package com.guruinfo.scm.Equipment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.guruinfo.scm.Equipment.EquipSlotLip.DateInfo;
import com.guruinfo.scm.Equipment.EquipSlotLip.TimeInfo;
import com.guruinfo.scm.Equipment.EquipSlotLip.rowInfo;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.BaseFragment;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.service.RestClientHelper;
import com.guruinfo.scm.common.ui.LeadNothingSelectedSpinnerAdapter;
import com.guruinfo.scm.common.ui.SCMTextView;
import com.guruinfo.scm.common.utils.ApiCalls;
import com.guruinfo.scm.common.utils.Sharedpref;
import com.kelin.scrollablepanel.library.PanelAdapter;
import com.kelin.scrollablepanel.library.ScrollablePanel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.guruinfo.scm.Equipment.EquipRequestCreation.BookedList1;
import static com.guruinfo.scm.Equipment.EquipRequestCreation.bookEquipId;
import static com.guruinfo.scm.Equipment.EquipRequestCreation.equip_duration_time;
import static com.guruinfo.scm.Equipment.EquipRequestCreation.equip_start_date1;
import static com.guruinfo.scm.SCMDashboardActivityLatest.NavigationFragmentManager;


public class EquipSlotView extends BaseActivity {


    private static final String TAG = "EquipViewSlot";
    SessionManager session;
    static String uid, Cre_Id;
    BackgroundTask backgroundTask;
    String requestParameter;
    Context context;
    String listActionName = "MOBILE_EQUIPMENT_PROCESS";
    String Submode;
    public static String stageId = "";
    String projectId = "";
    String Equip_Req_Id = "";
    String Equip_Req_Id_Sel = "", EquipID = "", EquipName = "";
    private Calendar calendar;
    private int year, month, day;
    static EditText editDateTextView;
    String title;
    int timeSplitCount = 0;
    HashMap<String, HashMap<String, Integer>> continueSelectArray = new HashMap<>();
    LinearLayout first_lay;
    LinearLayout scrollPanel;
    TextView slot_incre_decre, slot_book;
    SCMTextView equip_name_slot;
    EditText equip_start_date;
    Spinner show_slot_spin, equip_id_spin;
    String SpinSelEquipId = "Equipment Id";
    String selDate;
    String selSplitTime;
    String isMultiBookable = "";

    String SplitCount, ScheduleDuration, MultiBookKey, saveDuration, saveTotalDuration;
    ArrayList<String> Equipment_dur_date;
    ArrayList<String> Equipment_start_date;
    ArrayList<String> Equipment_end_date;
    ArrayList<String> ScheduleDate;


    Boolean incre_decre = true;
    Calendar calander;
    int mYear, mMonth, mDay;
    String currentDate;
    ArrayList<HashMap<String, String>> EquipmentId = new ArrayList<>();

    HashMap<String, HashMap<String, String>> BookedList;


    public static Equip_Slot_View newInstance(Bundle bundle) {
        Equip_Slot_View fragment = new Equip_Slot_View();
        fragment.setArguments(bundle);
        return fragment;
    }

    public HashMap<String, HashMap<String, String>> getBookedListMap() {
        return BookedList;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equip_slot_view);
        context = getContext();
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Equip_Req_Id = extras.getString("EQUIPREQID");
            Equip_Req_Id_Sel = extras.getString("EQUIPREQID_SEL");
            EquipID = extras.getString("EQUIPID");
            EquipName = extras.getString("EQUIPNAME");
            if(!EquipID.equalsIgnoreCase(""))
                if (!Sharedpref.GetPrefString(context, "SlotType").equalsIgnoreCase("RESCHEDULE")&&!Sharedpref.GetPrefString(context, "SlotType").equalsIgnoreCase("SCHEDULE"))
                    SpinSelEquipId=EquipID;
                else
                    SpinSelEquipId = "Equipment Id";
            else
                SpinSelEquipId = "Equipment Id";
        }

        Equipment_start_date = new ArrayList<>();
        Equipment_dur_date = new ArrayList<>();
        Equipment_end_date = new ArrayList<>();
        ScheduleDate = new ArrayList<>();

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        equip_name_slot = (SCMTextView) findViewById(R.id.equip_name_slot);
        equip_start_date = (EditText) findViewById(R.id.equip_start_date1);
        show_slot_spin = (Spinner) findViewById(R.id.show_slot_spin);
        equip_id_spin = (Spinner) findViewById(R.id.equip_id_spin);

        slot_incre_decre = (TextView) findViewById(R.id.slot_ince_decre);
        slot_book = (TextView) findViewById(R.id.slot_book);

        scrollPanel = (LinearLayout) findViewById(R.id.scrollPanel);

        show_slot_spin.setSelection(1);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        date = calendar.getTime();
        SimpleDateFormat s;
        s = new SimpleDateFormat("dd/MM/yyyy");
        currentDate = s.format(date);


        slot_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("BookedList-->: " + BookedList.size());
                if (BookedList.size() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("SLOT_TYPE", "BOOKED");
                    if (SpinSelEquipId.equalsIgnoreCase("Equipment Id")) {
                        bundle.putString("EQUIPID", EquipID);
                    } else {
                        bundle.putString("EQUIPID", SpinSelEquipId);
                    }
                    bundle.putString("MULTIBOOK", isMultiBookable);
                    bundle.putString("EQUIPNAME", EquipName);
                    bundle.putString("EQUIPREQID", Equip_Req_Id);
                    bundle.putString("SPLITCOUNT", "" + timeSplitCount);
                    bundle.putSerializable("LIST_ITEM", (Serializable) BookedList);
                    if (Equip_Req_Id.equalsIgnoreCase("")) {
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("SLOT_TYPE", "BOOKED");
                        hashMap.put("MULTIBOOK", isMultiBookable);
                        hashMap.put("SPLITCOUNT", "" + timeSplitCount);
                        hashMap.put("EQUIPID", "" + SpinSelEquipId);
                        BookedList.put("Bundle", hashMap);
                        BookedList1 = BookedList;
                        if (!SpinSelEquipId.equalsIgnoreCase("Equipment Id"))
                            bookEquipId = SpinSelEquipId;
                        else
                            bookEquipId = "";

                        HashMap<String, String> Map = new HashMap<String, String>();
                        HashMap<String, String> Map1 = new HashMap<String, String>();
                        Iterator myVeryOwnIterator = BookedList.keySet().iterator();
                        while (myVeryOwnIterator.hasNext()) {
                            String key = (String) myVeryOwnIterator.next();
                            if (key.equalsIgnoreCase("Bundle")) {
                                Map1 = (HashMap<String, String>) BookedList.get(key);
                            } else {
                                Map = (HashMap<String, String>) BookedList.get(key);
                                //System.out.println("Key: "+key+" Value: "+Map);
                                String datesplit = Map.get("title") + "," + (Map.get("title")).replaceFirst("_(.*)", "") + "_" + Map.get("slotEndTime");
                                String startdatesplit = Map.get("title") + "#" + Map.get("equipUniq");
                                String enddatesplit = (Map.get("title")).replaceFirst("_(.*)", "") + "_" + Map.get("slotEndTime");
                                //System.out.println("@@@: "+datesplit +"=="+startdatesplit+"==="+enddatesplit);
                                ScheduleDate.add(key);
                                Equipment_dur_date.add(datesplit);
                                Equipment_start_date.add(startdatesplit);
                                Equipment_end_date.add(enddatesplit);
                            }

                        }


                        SplitCount = Map1.get("SPLITCOUNT");


                        if (SplitCount.equalsIgnoreCase("1")) {
                            saveDuration = "60";
                            saveTotalDuration = "" + (BookedList.size() - 1) * 60;
                        } else if (SplitCount.equalsIgnoreCase("2")) {
                            saveDuration = "30";
                            saveTotalDuration = "" + (BookedList.size() - 1) * 30;
                        } else if (SplitCount.equalsIgnoreCase("4")) {
                            saveDuration = "15";
                            saveTotalDuration = "" + (BookedList.size() - 1) * 15;
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
                        equip_start_date1.setText((ScheduleDate.get(0)).replace("_", " "));
                        equip_duration_time.setText(ScheduleDuration);
                        equip_duration_time.setEnabled(false);

                        finish();
                    } else {
                        NavigationFragmentManager(Equip_Slot_Schedule.newInstance(bundle), "Equip");
                        finish();
                    }

                    setToast("slot Booked!!!");

                } else {
                    setToast("Please choose slot to book!!!");
                }

            }
        });


        equip_start_date.setText(currentDate);
        selDate = equip_start_date.getText().toString();
        equip_start_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                selDate = equip_start_date.getText().toString();
                System.out.println("SELECTDATE" + selDate);
                load(listActionName, "VIEW_SLOT", selSplitTime, "");
                scrollPanel.removeAllViews();
                BookedList = new HashMap<String, HashMap<String, String>>();

            }
        });


        equip_start_date.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                editDateTextView = equip_start_date;
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getSupportFragmentManager(), "DatePicker");

            }
        });
        equip_id_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                    int selectSpinnerPosition = (equip_id_spin.getSelectedItemPosition()) - 1;
                    SpinSelEquipId = EquipmentId.get(selectSpinnerPosition).get("value");

                    System.out.println("SELECTID" + EquipmentId.get(selectSpinnerPosition).get("value"));
                    load(listActionName, "VIEW_SLOT", selSplitTime, "");
                    scrollPanel.removeAllViews();
                    BookedList = new HashMap<String, HashMap<String, String>>();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        show_slot_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub


                selSplitTime = show_slot_spin.getSelectedItem().toString();
                if (show_slot_spin.getSelectedItem().toString().trim().equals("30")) {
                    load(listActionName, "VIEW_SLOT", "30", "");
                    scrollPanel.removeAllViews();
                    BookedList = new HashMap<String, HashMap<String, String>>();
                } else if (show_slot_spin.getSelectedItem().toString().trim().equals("60")) {

                    load(listActionName, "VIEW_SLOT", "60", "");
                    scrollPanel.removeAllViews();
                    BookedList = new HashMap<String, HashMap<String, String>>();
                } else if (show_slot_spin.getSelectedItem().toString().trim().equals("15")) {
                    load(listActionName, "VIEW_SLOT", "15", "");
                    scrollPanel.removeAllViews();
                    BookedList = new HashMap<String, HashMap<String, String>>();
                }


            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        slot_incre_decre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


    }


    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();

            int yy = 2000, mm = 01, dd = 01;
            if (editDateTextView.getText().toString().trim().length() > 0) {
                String date[] = editDateTextView.getText().toString().split("/");
                dd = Integer.parseInt(date[0]);
                mm = (Integer.parseInt(date[1]) - 1);
                yy = Integer.parseInt(date[2]);
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
            view.setMinDate(System.currentTimeMillis() - 1000);
            populateSetDate(yy, mm + 1, dd);
        }

        public void populateSetDate(int year, int month, int day) {
            NumberFormat formatter = new DecimalFormat("00");
            editDateTextView.setText(formatter.format(day) + "/" + formatter.format(month) + "/" + year);
            editDateTextView.setError(null);

        }
    }


    public void load(final String action, final String submode, String pjtId, String stgeId) {
        this.Submode = submode;
        this.projectId = pjtId;
        this.stageId = stgeId;

        if (pjtId.equalsIgnoreCase("30")) {
            if (SpinSelEquipId.equalsIgnoreCase("Equipment Id")) {
                if (Sharedpref.GetPrefString(context, "SlotType").equalsIgnoreCase("SWAP")) {
                    requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'" + submode + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','equip_app_id':'" + Equip_Req_Id + "','start_datepicker':'" + selDate + "','SlotformName':'','slot_config_id':'','epuip_id':'" + Equip_Req_Id_Sel + "','unique_id':'" + EquipID + "','alreadychosenDuratin':'" + pjtId + "','alreadychosenTime':'','duration':''}";
                } else
                    requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'" + submode + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','equip_app_id':'" + Equip_Req_Id + "','start_datepicker':'" + selDate + "','SlotformName':'','slot_config_id':'','epuip_id':'" + Equip_Req_Id_Sel + "','unique_id':'','alreadychosenDuratin':'" + pjtId + "','alreadychosenTime':'','duration':''}";
            } else
                requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'" + submode + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','equip_app_id':'" + Equip_Req_Id + "','start_datepicker':'" + selDate + "','SlotformName':'','slot_config_id':'','epuip_id':'" + Equip_Req_Id_Sel + "','unique_id':'" + SpinSelEquipId + "','alreadychosenDuratin':'" + pjtId + "','alreadychosenTime':'','duration':''}";
        } else if (pjtId.equalsIgnoreCase("15")) {
            if (SpinSelEquipId.equalsIgnoreCase("Equipment Id")) {
                if (Sharedpref.GetPrefString(context, "SlotType").equalsIgnoreCase("SWAP")) {
                    requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'" + submode + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','equip_app_id':'" + Equip_Req_Id + "','start_datepicker':'" + selDate + "','SlotformName':'','slot_config_id':'','epuip_id':'" + Equip_Req_Id_Sel + "','unique_id':'" + EquipID + "','alreadychosenDuratin':'" + pjtId + "','alreadychosenTime':'','duration':''}";
                } else
                    requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'" + submode + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','equip_app_id':'" + Equip_Req_Id + "','start_datepicker':'" + selDate + "','SlotformName':'','slot_config_id':'','epuip_id':'" + Equip_Req_Id_Sel + "','unique_id':'','alreadychosenDuratin':'" + pjtId + "','alreadychosenTime':'','duration':''}";
            } else
                requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'" + submode + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','equip_app_id':'" + Equip_Req_Id + "','start_datepicker':'" + selDate + "','SlotformName':'','slot_config_id':'','epuip_id':'" + Equip_Req_Id_Sel + "','unique_id':'" + SpinSelEquipId + "','alreadychosenDuratin':'" + pjtId + "','alreadychosenTime':'','duration':''}";
        } else if (pjtId.equalsIgnoreCase("60")) {
            if (SpinSelEquipId.equalsIgnoreCase("Equipment Id")) {
                if (Sharedpref.GetPrefString(context, "SlotType").equalsIgnoreCase("SWAP")) {
                    requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'" + submode + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','equip_app_id':'" + Equip_Req_Id + "','start_datepicker':'" + selDate + "','SlotformName':'','slot_config_id':'','epuip_id':'" + Equip_Req_Id_Sel + "','unique_id':'" + EquipID + "','alreadychosenDuratin':'" + pjtId + "','alreadychosenTime':'','duration':''}";
                } else
                    requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'" + submode + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','equip_app_id':'" + Equip_Req_Id + "','start_datepicker':'" + selDate + "','SlotformName':'','slot_config_id':'','epuip_id':'" + Equip_Req_Id_Sel + "','unique_id':'','alreadychosenDuratin':'" + pjtId + "','alreadychosenTime':'','duration':''}";
            } else
                requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'" + submode + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','equip_app_id':'" + Equip_Req_Id + "','start_datepicker':'" + selDate + "','SlotformName':'','slot_config_id':'','epuip_id':'" + Equip_Req_Id_Sel + "','unique_id':'" + SpinSelEquipId + "','alreadychosenDuratin':'" + pjtId + "','alreadychosenTime':'','duration':''}";
        }
        Log.d(TAG, requestParameter);
        //onListLoad(requestParameter, submode);
        backgroundTask = new BackgroundTask(context, Submode, new OnTaskCompleted() {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        backgroundTask.execute("", "", requestParameter);
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
            if (flag.equals("VIEW_SLOT")) {
                JSONObject genObj = response.getJSONObject("generalInformation");
                equip_name_slot.setText(genObj.getString("equipmentName"));

                 isMultiBookable = response.getString("isMultiBookable");
                JSONObject EquipmentIdObj = genObj.getJSONObject("equipmentId");
                JSONArray EquipmentIdArray = EquipmentIdObj.getJSONArray("values");
                EquipmentId = ApiCalls.getArraylistfromJson(EquipmentIdArray.toString());
                if (Sharedpref.GetPrefString(context, "SlotType").equalsIgnoreCase("SWAP")) {
                    loadLeadStyleSpinner(equip_id_spin, EquipmentId, new String[]{"value"}, EquipID);
                } else {
                    loadLeadStyleSpinner(equip_id_spin, EquipmentId, new String[]{"value"}, SpinSelEquipId);
                }


                scrollPanel.addView(panel(response));
            }

            /*if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_1)) {

            } else if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_403)) {
                showSessionScreensDialog(context, response.getString(AppContants.RESPONSE_MESSAGE));
            } else {
                if (flag.equals("otpresponse")) {
                    // setmsgToast(dialog.getWindow().getDecorView(), response.getString("msg"));
                } else
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
            }*/
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

    public ScrollablePanel panel(JSONObject response) {
        ScrollablePanelAdapter1 scrollablePanelAdapter = new ScrollablePanelAdapter1();
        ScrollablePanel scrollablePanel = new ScrollablePanel(context, scrollablePanelAdapter);
        generateTestData(scrollablePanelAdapter, response);
        scrollablePanel.setPanelAdapter(scrollablePanelAdapter);
        return scrollablePanel;
    }

    private void generateTestData(ScrollablePanelAdapter1 scrollablePanelAdapter, JSONObject response) {
        List<TimeInfo> TimeInfoList = new ArrayList<>();
        List<DateInfo> dateInfoList = new ArrayList<>();
        List<List<rowInfo>> rowInfoList = new ArrayList<>();
        try {
            JSONObject resObject = response;

            JSONArray tableArray = resObject.getJSONArray("SlotValues");

            String times[] = {"00", "01", "02", "03", "04",
                    "05", "06", "07", "08", "09",
                    "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};

            //JSONArray jsonArray = resObject.getJSONArray("SlotValue");
            int numTimes = times.length;
            for (int s = 0; s < numTimes; s++) {

                TimeInfo roomInfo = new TimeInfo();
                String SlotTime = times[s];
                roomInfo.setRoomType(SlotTime);
                TimeInfoList.add(roomInfo);
            }
            title = "Slot";
            for (int i = 0; i < tableArray.length(); i++) {
                int m = 0;
                JSONObject rowObject = tableArray.getJSONObject(i);
                DateInfo dateInfo = new DateInfo();
                String verName = rowObject.getString("Date");
                dateInfo.setMonth(verName);
                dateInfoList.add(dateInfo);
                JSONArray timeSplitArray = rowObject.getJSONArray("TimeSplitVal");
                timeSplitCount = timeSplitArray.length();
                JSONArray slotTableValues = rowObject.getJSONArray("slotTableValues");
                List<rowInfo> rowInfoListRow = new ArrayList<>();
                for (int j = 0; j < slotTableValues.length(); j++) {
                    JSONObject valuesRow = slotTableValues.getJSONObject(j);
                    JSONArray valRowArr = valuesRow.getJSONArray("rowValues");
                    for (int k = 0; k < valRowArr.length(); k++) {
                        JSONObject valRowObj = valRowArr.getJSONObject(k);
                        rowInfo rowinfo = new rowInfo();
                        String avaiClass = valRowObj.getString("class");
                        String equipCount = valRowObj.getString("equipCount");
                        String title = valRowObj.getString("title");
                        String dateArray[] = title.split("_");
                        String equipUniq = valRowObj.getString("equipUniq");
                        String slotEndTime = valRowObj.getString("slotEndTime");
                        String slotStartTime = valRowObj.getString("slotStartTime");

                        String isSelected = "";
                        rowinfo.setAvailable(avaiClass);
                        rowinfo.setEquipCount(equipCount);
                        rowinfo.setComment(dateArray[0]);
                        rowinfo.setTitle(title);
                        rowinfo.setEquipUniq(equipUniq);
                        rowinfo.setslotEndTime(slotEndTime);
                        rowinfo.setslotStartTime(slotStartTime);
                        rowinfo.setSelected(isSelected);
                        if (avaiClass.equalsIgnoreCase("available") || avaiClass.equalsIgnoreCase("available multiBookable")) {
                            m++;
                            rowinfo.setInserted(m);
                        } else
                            rowinfo.setInserted(0);

                        rowInfoListRow.add(rowinfo);
                    }
                }
                rowInfoList.add(rowInfoListRow);
                System.out.println("rowInfoListRow-->" + rowInfoListRow.size());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        scrollablePanelAdapter.setDateInfoList(dateInfoList);
        scrollablePanelAdapter.setRoomInfoList(TimeInfoList);
        scrollablePanelAdapter.setrowList(rowInfoList);
        scrollablePanelAdapter.setTitle(title);
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

    public void loadLeadStyleSpinner(Spinner spin, ArrayList<HashMap<String, String>> resplist, String[] from, String txtStr) {
        SimpleAdapter listAdapter = new SimpleAdapter(BaseFragment.context, resplist, R.layout.lead_spinner_text, from, new int[]{android.R.id.text1});
        listAdapter.setDropDownViewResource(R.layout.lead_spinner_item_text);
        spin.setAdapter(listAdapter);
        spin.setSelection(-1);
        spin.getSelectedView();
        spin.setAdapter(new LeadNothingSelectedSpinnerAdapter(listAdapter, R.layout.lead_spinner_row_nothing_selected, BaseFragment.context, txtStr));
    }

    public class ScrollablePanelAdapter1 extends PanelAdapter {
        private static final int TITLE_TYPE = 4;
        //private static final int ROOM_TYPE = 0;
        private static final int DATE_TYPE = 0;
        private static final int TIME_TYPE = 1;
        private static final int ORDER_TYPE = 2;
        private List<TimeInfo> TimeInfoList;
        private List<DateInfo> dateInfoList;
        private List<List<rowInfo>> rowList;
        private String title;

        @Override
        public int getRowCount() {
            return TimeInfoList.size() + 1;
        }

        @Override
        public int getColumnCount() {
            return dateInfoList.size() + 1;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int row, int column) {
            int viewType = getItemViewType(row, column);
            switch (viewType) {
                case TIME_TYPE:
                    setTimeView(row, (TimeViewHolder) holder);
                    break;
                case DATE_TYPE:
                    setDateView(column, (DateViewHolder) holder);

                    break;
                case ORDER_TYPE:
                    setOrderView(row, column, (OrderViewHolder) holder);
                    break;
                case TITLE_TYPE:
                    setTitleView(row, column, (TitleViewHolder) holder);
                    break;
                default:
                    setOrderView(row, column, (OrderViewHolder) holder);
            }
        }

        public int getItemViewType(int row, int column) {
            if (column == 0 && row == 0) {
                return TITLE_TYPE;
            }
            if (column == 0) {
                return TIME_TYPE;
            }
            if (row == 0) {
                return DATE_TYPE;
            }
            return ORDER_TYPE;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case DATE_TYPE:
                    return new DateViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.slot_listitem_date_info, parent, false));
                case TIME_TYPE:
                    return new TimeViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.slot_listitem_time_info, parent, false));
                case ORDER_TYPE:
                    return new OrderViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.slot_list_item_info, parent, false));
                case TITLE_TYPE:
                    return new TitleViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.slot_listitem_title_info, parent, false));
                default:
                    break;
            }
            return new OrderViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.slot_list_item_info, parent, false));

        }

        private void setDateView(int pos, DateViewHolder viewHolder) {
            DateInfo dateInfo = dateInfoList.get(pos - 1);
            if (dateInfo != null && pos > 0) {
                viewHolder.dateTextView.setText(dateInfo.getMonth());

                if (timeSplitCount == 1) {
                    viewHolder.time0Text.setVisibility(View.GONE);
                    viewHolder.time15Text.setVisibility(View.GONE);
                    viewHolder.time30Text.setVisibility(View.GONE);
                    viewHolder.time45Text.setVisibility(View.GONE);
                    viewHolder.time60Text.setVisibility(View.VISIBLE);
                } else if (timeSplitCount == 2) {
                    viewHolder.time0Text.setVisibility(View.VISIBLE);
                    viewHolder.time15Text.setVisibility(View.GONE);
                    viewHolder.time30Text.setVisibility(View.VISIBLE);
                    viewHolder.time45Text.setVisibility(View.GONE);
                    viewHolder.time60Text.setVisibility(View.GONE);
                } else if (timeSplitCount == 4) {
                    viewHolder.time0Text.setVisibility(View.VISIBLE);
                    viewHolder.time15Text.setVisibility(View.VISIBLE);
                    viewHolder.time30Text.setVisibility(View.VISIBLE);
                    viewHolder.time45Text.setVisibility(View.VISIBLE);
                    viewHolder.time60Text.setVisibility(View.GONE);
                }
            }
        }

        private void setTimeView(int pos, TimeViewHolder viewHolder) {
            TimeInfo roomInfo = TimeInfoList.get(pos - 1);
            if (roomInfo != null && pos > 0) {
                viewHolder.roomTypeTextView.setText(roomInfo.getRoomType());

            }
        }

        private void setTitleView(final int row, final int column, TitleViewHolder holder) {
            // if (titleText == null) {
            holder.titleTextView.setText(title);
            //titleText = holder.titleTextView;
            // }
        }

        private void setOrderView(final int row, final int column, OrderViewHolder viewHolder) {

            final rowInfo rowInfo = rowList.get(column - 1).get(row - 1);
            if (rowInfo != null) {
                viewHolder.time0_lay.setClickable(false);
                viewHolder.time15_lay.setClickable(false);
                viewHolder.time30_lay.setClickable(false);
                viewHolder.time45_lay.setClickable(false);
                viewHolder.time60_lay.setClickable(false);
                if (timeSplitCount == 1) {
                    viewHolder.time0_lay.setVisibility(View.GONE);
                    viewHolder.time15_lay.setVisibility(View.GONE);
                    viewHolder.time30_lay.setVisibility(View.GONE);
                    viewHolder.time45_lay.setVisibility(View.GONE);
                    viewHolder.time60_lay.setVisibility(View.VISIBLE);
                } else if (timeSplitCount == 2) {
                    viewHolder.time0_lay.setVisibility(View.VISIBLE);
                    viewHolder.time15_lay.setVisibility(View.GONE);
                    viewHolder.time30_lay.setVisibility(View.VISIBLE);
                    viewHolder.time45_lay.setVisibility(View.GONE);
                    viewHolder.time60_lay.setVisibility(View.GONE);
                } else if (timeSplitCount == 4) {
                    viewHolder.time0_lay.setVisibility(View.VISIBLE);
                    viewHolder.time15_lay.setVisibility(View.VISIBLE);
                    viewHolder.time30_lay.setVisibility(View.VISIBLE);
                    viewHolder.time45_lay.setVisibility(View.VISIBLE);
                    viewHolder.time60_lay.setVisibility(View.GONE);
                }
                viewHolder.time0TextView.setText("");
                viewHolder.time15TextView.setText("");
                viewHolder.time30TextView.setText("");
                viewHolder.time45TextView.setText("");
                viewHolder.time60TextView.setText("");
                if (timeSplitCount == 2) {
                    final rowInfo rowInfo1 = rowList.get(column - 1).get((row * 2) - 2);
                    final rowInfo rowInfo2 = rowList.get(column - 1).get((row * 2) - 1);

                    if (rowInfo1.getAvailable().equalsIgnoreCase("available")) {

                        viewHolder.time0TextView.setText(rowInfo1.getEquipCount());
                        final GradientDrawable time0lay = (GradientDrawable) viewHolder.time0_lay.getBackground();
                        if (rowInfo1.getSelected().equalsIgnoreCase("true")) {
                            time0lay.setColor(getResources().getColor(R.color.bg9));
                        } else {
                            time0lay.setColor(getResources().getColor(R.color.bg2));
                        }
                        viewHolder.time0_lay.setClickable(true);
                        viewHolder.time0_lay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isMultiBookable.equalsIgnoreCase("true")) {
                                    continueAvailableSelection(rowInfo1, time0lay);
                                } else {
                                    HashMap<String, String> hashmap = new HashMap<String, String>();
                                    if (rowInfo1.getSelected().equalsIgnoreCase("")) {
                                        time0lay.setColor(getResources().getColor(R.color.bg9));
                                        rowInfo1.setSelected("true");
                                        System.out.println("START" + rowInfo1.getTitle());
                                        hashmap.put("equipUniq", rowInfo1.getEquipUniq());
                                        hashmap.put("title", rowInfo1.getTitle());
                                        hashmap.put("slotEndTime", rowInfo1.getslotEndTime());
                                        hashmap.put("class", rowInfo1.getAvailable());
                                        hashmap.put("slotStartTime", rowInfo1.getslotStartTime());
                                        hashmap.put("equipCount", rowInfo1.getEquipCount());
                                        BookedList.put(rowInfo1.getTitle(), hashmap);
                                    } else {
                                        time0lay.setColor(getResources().getColor(R.color.bg2));
                                        rowInfo1.setSelected("");
                                        if (BookedList.containsKey(rowInfo1.getTitle())) {
                                            BookedList.remove(rowInfo1.getTitle());
                                        }
                                    }
                                }
                            }
                        });

                    } else if (rowInfo1.getAvailable().equalsIgnoreCase("available multiBookable")) {
                        viewHolder.time0TextView.setText(rowInfo1.getEquipCount());
                        final GradientDrawable time0lay = (GradientDrawable) viewHolder.time0_lay.getBackground();
                        if (rowInfo1.getSelected().equalsIgnoreCase("true")) {
                            time0lay.setColor(getResources().getColor(R.color.bg9));
                        } else {
                            time0lay.setColor(getResources().getColor(R.color.bg5));
                        }
                        viewHolder.time0_lay.setClickable(true);
                        viewHolder.time0_lay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isMultiBookable.equalsIgnoreCase("true")) {
                                  continueMultiAvailableSelection(rowInfo1,time0lay);
                                } else {
                                    HashMap<String, String> hashmap = new HashMap<String, String>();
                                    if (rowInfo1.getSelected().equalsIgnoreCase("")) {
                                        time0lay.setColor(getResources().getColor(R.color.bg9));
                                        rowInfo1.setSelected("true");
                                        System.out.println("START" + rowInfo1.getTitle());
                                        hashmap.put("equipUniq", rowInfo1.getEquipUniq());
                                        hashmap.put("title", rowInfo1.getTitle());
                                        hashmap.put("slotEndTime", rowInfo1.getslotEndTime());
                                        hashmap.put("class", rowInfo1.getAvailable());
                                        hashmap.put("slotStartTime", rowInfo1.getslotStartTime());
                                        hashmap.put("equipCount", rowInfo1.getEquipCount());
                                        BookedList.put(rowInfo1.getTitle(), hashmap);
                                    } else {
                                        time0lay.setColor(getResources().getColor(R.color.bg5));
                                        rowInfo1.setSelected("");
                                        if (BookedList.containsKey(rowInfo1.getTitle())) {
                                            BookedList.remove(rowInfo1.getTitle());
                                        }
                                    }
                                }
                            }
                        });

                    } else if (rowInfo1.getAvailable().equalsIgnoreCase("booked")) {
                        viewHolder.time0TextView.setText(rowInfo1.getEquipCount());
                        final GradientDrawable time0lay = (GradientDrawable) viewHolder.time0_lay.getBackground();
                        time0lay.setColor(getResources().getColor(R.color.bg6));
                    } else {
                        final GradientDrawable time0lay = (GradientDrawable) viewHolder.time0_lay.getBackground();
                        time0lay.setColor(getResources().getColor(R.color.lightgray02));
                    }

                    if (rowInfo2.getAvailable().equalsIgnoreCase("available")) {
                        viewHolder.time30TextView.setText(rowInfo2.getEquipCount());
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time30_lay.getBackground();
                        if (rowInfo2.getSelected().equalsIgnoreCase("true")) {
                            time15lay.setColor(getResources().getColor(R.color.bg9));
                        } else {
                            time15lay.setColor(getResources().getColor(R.color.bg2));
                        }
                        viewHolder.time30_lay.setClickable(true);
                        viewHolder.time30_lay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isMultiBookable.equalsIgnoreCase("true")) {
                                    continueAvailableSelection(rowInfo2, time15lay);
                                } else {
                                    HashMap<String, String> hashmap = new HashMap<String, String>();
                                    if (rowInfo2.getSelected().equalsIgnoreCase("")) {
                                        time15lay.setColor(getResources().getColor(R.color.bg9));
                                        rowInfo2.setSelected("true");
                                        System.out.println("START" + rowInfo2.getTitle());

                                        hashmap.put("equipUniq", rowInfo2.getEquipUniq());
                                        hashmap.put("title", rowInfo2.getTitle());
                                        hashmap.put("slotEndTime", rowInfo2.getslotEndTime());
                                        hashmap.put("class", rowInfo2.getAvailable());
                                        hashmap.put("slotStartTime", rowInfo2.getslotStartTime());
                                        hashmap.put("equipCount", rowInfo2.getEquipCount());
                                        BookedList.put(rowInfo2.getTitle(), hashmap);
                                    } else {
                                        time15lay.setColor(getResources().getColor(R.color.bg2));
                                        rowInfo2.setSelected("");
                                        if (BookedList.containsKey(rowInfo2.getTitle())) {
                                            BookedList.remove(rowInfo2.getTitle());
                                        }
                                    }
                                }
                            }
                        });

                    } else if (rowInfo2.getAvailable().equalsIgnoreCase("available multiBookable")) {
                        viewHolder.time30TextView.setText(rowInfo2.getEquipCount());
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time30_lay.getBackground();
                        if (rowInfo2.getSelected().equalsIgnoreCase("true")) {
                            time15lay.setColor(getResources().getColor(R.color.bg9));
                        } else {
                            time15lay.setColor(getResources().getColor(R.color.bg5));
                        }
                        viewHolder.time30_lay.setClickable(true);
                        viewHolder.time30_lay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isMultiBookable.equalsIgnoreCase("true")) {
                                    continueMultiAvailableSelection(rowInfo2,time15lay);
                                } else {
                                    HashMap<String, String> hashmap = new HashMap<String, String>();
                                    if (rowInfo2.getSelected().equalsIgnoreCase("")) {
                                        time15lay.setColor(getResources().getColor(R.color.bg9));
                                        rowInfo2.setSelected("true");
                                        System.out.println("START" + rowInfo2.getTitle());

                                        hashmap.put("equipUniq", rowInfo2.getEquipUniq());
                                        hashmap.put("title", rowInfo2.getTitle());
                                        hashmap.put("slotEndTime", rowInfo2.getslotEndTime());
                                        hashmap.put("class", rowInfo2.getAvailable());
                                        hashmap.put("slotStartTime", rowInfo2.getslotStartTime());
                                        hashmap.put("equipCount", rowInfo2.getEquipCount());
                                        BookedList.put(rowInfo2.getTitle(), hashmap);
                                    } else {
                                        time15lay.setColor(getResources().getColor(R.color.bg5));
                                        rowInfo2.setSelected("");
                                        if (BookedList.containsKey(rowInfo2.getTitle())) {
                                            BookedList.remove(rowInfo2.getTitle());
                                        }
                                    }
                                }
                            }
                        });

                    } else if (rowInfo2.getAvailable().equalsIgnoreCase("booked")) {
                        viewHolder.time30TextView.setText(rowInfo2.getEquipCount());
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time30_lay.getBackground();
                        time15lay.setColor(getResources().getColor(R.color.bg6));

                    } else {
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time30_lay.getBackground();
                        time15lay.setColor(getResources().getColor(R.color.lightgray02));
                    }


                } else if (timeSplitCount == 4) {

                    final com.guruinfo.scm.Equipment.EquipSlotLip.rowInfo rowInfo1 = rowList.get(column - 1).get((row * 4) - 4);
                    final com.guruinfo.scm.Equipment.EquipSlotLip.rowInfo rowInfo2 = rowList.get(column - 1).get((row * 4) - 3);
                    final com.guruinfo.scm.Equipment.EquipSlotLip.rowInfo rowInfo3 = rowList.get(column - 1).get((row * 4) - 2);
                    final com.guruinfo.scm.Equipment.EquipSlotLip.rowInfo rowInfo4 = rowList.get(column - 1).get((row * 4) - 1);
                    if (rowInfo1.getAvailable().equalsIgnoreCase("available")) {
                        viewHolder.time0TextView.setText(rowInfo1.getEquipCount());
                        final GradientDrawable time0lay = (GradientDrawable) viewHolder.time0_lay.getBackground();
                        if (rowInfo1.getSelected().equalsIgnoreCase("true")) {
                            time0lay.setColor(getResources().getColor(R.color.bg9));
                        } else {
                            time0lay.setColor(getResources().getColor(R.color.bg2));
                        }
                        viewHolder.time0_lay.setClickable(true);
                        viewHolder.time0_lay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isMultiBookable.equalsIgnoreCase("true")) {
                                    continueAvailableSelection(rowInfo1, time0lay);
                                } else {
                                    if (rowInfo1.getSelected().equalsIgnoreCase("")) {
                                        time0lay.setColor(getResources().getColor(R.color.bg9));
                                        rowInfo1.setSelected("true");
                                        HashMap<String, String> hashmap = new HashMap<String, String>();
                                        hashmap.put("equipUniq", rowInfo1.getEquipUniq());
                                        hashmap.put("title", rowInfo1.getTitle());
                                        hashmap.put("slotEndTime", rowInfo1.getslotEndTime());
                                        hashmap.put("class", rowInfo1.getAvailable());
                                        hashmap.put("slotStartTime", rowInfo1.getslotStartTime());
                                        hashmap.put("equipCount", rowInfo1.getEquipCount());
                                        BookedList.put(rowInfo1.getTitle(), hashmap);

                                    } else {
                                        time0lay.setColor(getResources().getColor(R.color.bg2));
                                        rowInfo1.setSelected("");
                                        if (BookedList.containsKey(rowInfo1.getTitle())) {
                                            BookedList.remove(rowInfo1.getTitle());
                                        }
                                    }
                                }
                            }
                        });

                    } else if (rowInfo1.getAvailable().equalsIgnoreCase("available multiBookable")) {

                        viewHolder.time0TextView.setText(rowInfo1.getEquipCount());
                        final GradientDrawable time0lay = (GradientDrawable) viewHolder.time0_lay.getBackground();
                        if (rowInfo1.getSelected().equalsIgnoreCase("true")) {
                            time0lay.setColor(getResources().getColor(R.color.bg9));
                        } else {
                            time0lay.setColor(getResources().getColor(R.color.bg5));
                        }
                        viewHolder.time0_lay.setClickable(true);
                        viewHolder.time0_lay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isMultiBookable.equalsIgnoreCase("true")) {
                                    continueMultiAvailableSelection(rowInfo1,time0lay);
                                } else {
                                    if (rowInfo1.getSelected().equalsIgnoreCase("")) {
                                        time0lay.setColor(getResources().getColor(R.color.bg9));
                                        rowInfo1.setSelected("true");
                                        HashMap<String, String> hashmap = new HashMap<String, String>();
                                        hashmap.put("equipUniq", rowInfo1.getEquipUniq());
                                        hashmap.put("title", rowInfo1.getTitle());
                                        hashmap.put("slotEndTime", rowInfo1.getslotEndTime());
                                        hashmap.put("class", rowInfo1.getAvailable());
                                        hashmap.put("slotStartTime", rowInfo1.getslotStartTime());
                                        hashmap.put("equipCount", rowInfo1.getEquipCount());
                                        BookedList.put(rowInfo1.getTitle(), hashmap);

                                    } else {
                                        time0lay.setColor(getResources().getColor(R.color.bg5));
                                        rowInfo1.setSelected("");
                                        if (BookedList.containsKey(rowInfo1.getTitle())) {
                                            BookedList.remove(rowInfo1.getTitle());
                                        }
                                    }
                                }
                            }
                        });

                    } else if (rowInfo1.getAvailable().equalsIgnoreCase("booked")) {
                        viewHolder.time0TextView.setText(rowInfo1.getEquipCount());
                        final GradientDrawable time0lay = (GradientDrawable) viewHolder.time0_lay.getBackground();
                        time0lay.setColor(getResources().getColor(R.color.bg6));
                    } else {
                        final GradientDrawable time0lay = (GradientDrawable) viewHolder.time0_lay.getBackground();
                        time0lay.setColor(getResources().getColor(R.color.lightgray02));
                    }
                    if (rowInfo2.getAvailable().equalsIgnoreCase("available")) {
                        viewHolder.time15TextView.setText(rowInfo2.getEquipCount());
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time15_lay.getBackground();
                        if (rowInfo2.getSelected().equalsIgnoreCase("true")) {
                            time15lay.setColor(getResources().getColor(R.color.bg9));
                        } else {
                            time15lay.setColor(getResources().getColor(R.color.bg2));
                        }
                        viewHolder.time15_lay.setClickable(true);
                        viewHolder.time15_lay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isMultiBookable.equalsIgnoreCase("true")) {
                                    continueAvailableSelection(rowInfo2, time15lay);
                                } else {
                                    if (rowInfo2.getSelected().equalsIgnoreCase("")) {
                                        time15lay.setColor(getResources().getColor(R.color.bg9));
                                        rowInfo2.setSelected("true");
                                        HashMap<String, String> hashmap = new HashMap<String, String>();
                                        hashmap.put("equipUniq", rowInfo2.getEquipUniq());
                                        hashmap.put("title", rowInfo2.getTitle());
                                        hashmap.put("slotEndTime", rowInfo2.getslotEndTime());
                                        hashmap.put("class", rowInfo2.getAvailable());
                                        hashmap.put("slotStartTime", rowInfo2.getslotStartTime());
                                        hashmap.put("equipCount", rowInfo2.getEquipCount());
                                        BookedList.put(rowInfo2.getTitle(), hashmap);
                                    } else {
                                        time15lay.setColor(getResources().getColor(R.color.bg2));
                                        rowInfo2.setSelected("");
                                        if (BookedList.containsKey(rowInfo2.getTitle())) {
                                            BookedList.remove(rowInfo2.getTitle());
                                        }
                                    }
                                }
                            }
                        });

                    } else if (rowInfo2.getAvailable().equalsIgnoreCase("available multiBookable")) {
                        viewHolder.time15TextView.setText(rowInfo2.getEquipCount());
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time15_lay.getBackground();
                        if (rowInfo2.getSelected().equalsIgnoreCase("true")) {
                            time15lay.setColor(getResources().getColor(R.color.bg9));
                        } else {
                            time15lay.setColor(getResources().getColor(R.color.bg5));
                        }
                        viewHolder.time15_lay.setClickable(true);
                        viewHolder.time15_lay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isMultiBookable.equalsIgnoreCase("true")) {
                                    continueMultiAvailableSelection(rowInfo2,time15lay);
                                } else {
                                    if (rowInfo2.getSelected().equalsIgnoreCase("")) {
                                        time15lay.setColor(getResources().getColor(R.color.bg9));
                                        rowInfo2.setSelected("true");
                                        HashMap<String, String> hashmap = new HashMap<String, String>();
                                        hashmap.put("equipUniq", rowInfo2.getEquipUniq());
                                        hashmap.put("title", rowInfo2.getTitle());
                                        hashmap.put("slotEndTime", rowInfo2.getslotEndTime());
                                        hashmap.put("class", rowInfo2.getAvailable());
                                        hashmap.put("slotStartTime", rowInfo2.getslotStartTime());
                                        hashmap.put("equipCount", rowInfo2.getEquipCount());
                                        BookedList.put(rowInfo2.getTitle(), hashmap);
                                    } else {
                                        time15lay.setColor(getResources().getColor(R.color.bg5));
                                        rowInfo2.setSelected("");
                                        if (BookedList.containsKey(rowInfo2.getTitle())) {
                                            BookedList.remove(rowInfo2.getTitle());
                                        }
                                    }
                                }
                            }
                        });

                    } else if (rowInfo2.getAvailable().equalsIgnoreCase("booked")) {
                        viewHolder.time15TextView.setText(rowInfo2.getEquipCount());
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time15_lay.getBackground();
                        time15lay.setColor(getResources().getColor(R.color.bg6));

                    } else {
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time15_lay.getBackground();
                        time15lay.setColor(getResources().getColor(R.color.lightgray02));
                    }
                    if (rowInfo3.getAvailable().equalsIgnoreCase("available")) {
                        viewHolder.time30TextView.setText(rowInfo3.getEquipCount());
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time30_lay.getBackground();
                        if (rowInfo3.getSelected().equalsIgnoreCase("true")) {
                            time15lay.setColor(getResources().getColor(R.color.bg9));
                        } else {
                            time15lay.setColor(getResources().getColor(R.color.bg2));
                        }
                        viewHolder.time30_lay.setClickable(true);
                        viewHolder.time30_lay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isMultiBookable.equalsIgnoreCase("true")) {
                                    continueAvailableSelection(rowInfo3, time15lay);
                                } else {
                                    if (rowInfo3.getSelected().equalsIgnoreCase("")) {
                                        time15lay.setColor(getResources().getColor(R.color.bg9));
                                        rowInfo3.setSelected("true");
                                        HashMap<String, String> hashmap = new HashMap<String, String>();
                                        hashmap.put("equipUniq", rowInfo3.getEquipUniq());
                                        hashmap.put("title", rowInfo3.getTitle());
                                        hashmap.put("slotEndTime", rowInfo3.getslotEndTime());
                                        hashmap.put("class", rowInfo3.getAvailable());
                                        hashmap.put("slotStartTime", rowInfo3.getslotStartTime());
                                        hashmap.put("equipCount", rowInfo3.getEquipCount());
                                        BookedList.put(rowInfo3.getTitle(), hashmap);
                                    } else {
                                        time15lay.setColor(getResources().getColor(R.color.bg2));
                                        rowInfo3.setSelected("");
                                        if (BookedList.containsKey(rowInfo3.getTitle())) {
                                            BookedList.remove(rowInfo3.getTitle());
                                        }
                                    }
                                }
                            }
                        });

                    } else if (rowInfo3.getAvailable().equalsIgnoreCase("available multiBookable")) {

                        viewHolder.time30TextView.setText(rowInfo3.getEquipCount());
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time30_lay.getBackground();
                        if (rowInfo3.getSelected().equalsIgnoreCase("true")) {
                            time15lay.setColor(getResources().getColor(R.color.bg9));
                        } else {
                            time15lay.setColor(getResources().getColor(R.color.bg5));
                        }
                        viewHolder.time30_lay.setClickable(true);
                        viewHolder.time30_lay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isMultiBookable.equalsIgnoreCase("true")) {
                                    continueMultiAvailableSelection(rowInfo3,time15lay);
                                } else {
                                if (rowInfo3.getSelected().equalsIgnoreCase("")) {
                                    time15lay.setColor(getResources().getColor(R.color.bg9));
                                    rowInfo3.setSelected("true");
                                    HashMap<String, String> hashmap = new HashMap<String, String>();
                                    hashmap.put("equipUniq", rowInfo3.getEquipUniq());
                                    hashmap.put("title", rowInfo3.getTitle());
                                    hashmap.put("slotEndTime", rowInfo3.getslotEndTime());
                                    hashmap.put("class", rowInfo3.getAvailable());
                                    hashmap.put("slotStartTime", rowInfo3.getslotStartTime());
                                    hashmap.put("equipCount", rowInfo3.getEquipCount());
                                    BookedList.put(rowInfo3.getTitle(), hashmap);
                                } else {
                                    time15lay.setColor(getResources().getColor(R.color.bg5));
                                    rowInfo3.setSelected("");
                                    if (BookedList.containsKey(rowInfo3.getTitle())) {
                                        BookedList.remove(rowInfo3.getTitle());
                                    }
                                }
                                }
                            }
                        });


                    } else if (rowInfo3.getAvailable().equalsIgnoreCase("booked")) {
                        viewHolder.time30TextView.setText(rowInfo3.getEquipCount());
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time30_lay.getBackground();
                        time15lay.setColor(getResources().getColor(R.color.bg6));
                    } else {
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time30_lay.getBackground();
                        time15lay.setColor(getResources().getColor(R.color.lightgray02));
                    }
                    if (rowInfo4.getAvailable().equalsIgnoreCase("available")) {
                        viewHolder.time45TextView.setText(rowInfo4.getEquipCount());
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time45_lay.getBackground();
                        if (rowInfo4.getSelected().equalsIgnoreCase("true")) {
                            time15lay.setColor(getResources().getColor(R.color.bg9));
                        } else {
                            time15lay.setColor(getResources().getColor(R.color.bg2));
                        }
                        viewHolder.time45_lay.setClickable(true);
                        viewHolder.time45_lay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isMultiBookable.equalsIgnoreCase("true")) {
                                    continueAvailableSelection(rowInfo4, time15lay);
                                } else {
                                    if (rowInfo4.getSelected().equalsIgnoreCase("")) {
                                        time15lay.setColor(getResources().getColor(R.color.bg9));
                                        rowInfo4.setSelected("true");
                                        HashMap<String, String> hashmap = new HashMap<String, String>();
                                        hashmap.put("equipUniq", rowInfo4.getEquipUniq());
                                        hashmap.put("title", rowInfo4.getTitle());
                                        hashmap.put("slotEndTime", rowInfo4.getslotEndTime());
                                        hashmap.put("class", rowInfo4.getAvailable());
                                        hashmap.put("slotStartTime", rowInfo4.getslotStartTime());
                                        hashmap.put("equipCount", rowInfo4.getEquipCount());
                                        BookedList.put(rowInfo4.getTitle(), hashmap);
                                    } else {
                                        time15lay.setColor(getResources().getColor(R.color.bg2));
                                        rowInfo4.setSelected("");
                                        if (BookedList.containsKey(rowInfo4.getTitle())) {
                                            BookedList.remove(rowInfo4.getTitle());
                                        }
                                    }
                                }
                            }
                        });

                    } else if (rowInfo4.getAvailable().equalsIgnoreCase("available multiBookable")) {

                        viewHolder.time45TextView.setText(rowInfo4.getEquipCount());
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time45_lay.getBackground();
                        if (rowInfo4.getSelected().equalsIgnoreCase("true")) {
                            time15lay.setColor(getResources().getColor(R.color.bg9));
                        } else {
                            time15lay.setColor(getResources().getColor(R.color.bg5));
                        }
                        viewHolder.time45_lay.setClickable(true);
                        viewHolder.time45_lay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isMultiBookable.equalsIgnoreCase("true")) {
                                    continueMultiAvailableSelection(rowInfo4,time15lay);
                                } else {
                                    if (rowInfo4.getSelected().equalsIgnoreCase("")) {
                                        time15lay.setColor(getResources().getColor(R.color.bg9));
                                        rowInfo4.setSelected("true");
                                        HashMap<String, String> hashmap = new HashMap<String, String>();
                                        hashmap.put("equipUniq", rowInfo4.getEquipUniq());
                                        hashmap.put("title", rowInfo4.getTitle());
                                        hashmap.put("slotEndTime", rowInfo4.getslotEndTime());
                                        hashmap.put("class", rowInfo4.getAvailable());
                                        hashmap.put("slotStartTime", rowInfo4.getslotStartTime());
                                        hashmap.put("equipCount", rowInfo4.getEquipCount());
                                        BookedList.put(rowInfo4.getTitle(), hashmap);
                                    } else {
                                        time15lay.setColor(getResources().getColor(R.color.bg5));
                                        rowInfo4.setSelected("");
                                        if (BookedList.containsKey(rowInfo4.getTitle())) {
                                            BookedList.remove(rowInfo4.getTitle());
                                        }
                                    }
                                }
                            }
                        });

                    } else if (rowInfo4.getAvailable().equalsIgnoreCase("booked")) {
                        viewHolder.time45TextView.setText(rowInfo4.getEquipCount());
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time45_lay.getBackground();
                        time15lay.setColor(getResources().getColor(R.color.bg6));
                    } else {
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time45_lay.getBackground();
                        time15lay.setColor(getResources().getColor(R.color.lightgray02));
                    }
                } else {

                    if (rowInfo.getAvailable().equalsIgnoreCase("available")) {
                        viewHolder.time60TextView.setText(rowInfo.getEquipCount());
                        final GradientDrawable time0lay = (GradientDrawable) viewHolder.time0_lay.getBackground();
                        time0lay.setColor(getResources().getColor(R.color.bg2));
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time15_lay.getBackground();
                        time15lay.setColor(getResources().getColor(R.color.bg2));
                        final GradientDrawable time30lay = (GradientDrawable) viewHolder.time30_lay.getBackground();
                        time30lay.setColor(getResources().getColor(R.color.bg2));
                        final GradientDrawable time45lay = (GradientDrawable) viewHolder.time45_lay.getBackground();
                        time45lay.setColor(getResources().getColor(R.color.bg2));
                        final GradientDrawable time60lay = (GradientDrawable) viewHolder.time60_lay.getBackground();
                        if (rowInfo.getSelected().equalsIgnoreCase("true")) {
                            time60lay.setColor(getResources().getColor(R.color.bg9));
                        } else {
                            time60lay.setColor(getResources().getColor(R.color.bg2));
                        }
                        viewHolder.time60_lay.setClickable(true);
                        viewHolder.time60_lay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isMultiBookable.equalsIgnoreCase("true")) {
                                    continueAvailableSelection(rowInfo, time60lay);
                                } else {
                                    if (rowInfo.getSelected().equalsIgnoreCase("")) {
                                        time60lay.setColor(getResources().getColor(R.color.bg9));
                                        rowInfo.setSelected("true");
                                        HashMap<String, String> hashmap = new HashMap<String, String>();
                                        hashmap.put("equipUniq", rowInfo.getEquipUniq());
                                        hashmap.put("title", rowInfo.getTitle());
                                        hashmap.put("slotEndTime", rowInfo.getslotEndTime());
                                        hashmap.put("class", rowInfo.getAvailable());
                                        hashmap.put("slotStartTime", rowInfo.getslotStartTime());
                                        hashmap.put("equipCount", rowInfo.getEquipCount());
                                        BookedList.put(rowInfo.getTitle(), hashmap);
                                    } else {
                                        time60lay.setColor(getResources().getColor(R.color.bg2));
                                        rowInfo.setSelected("");
                                        if (BookedList.containsKey(rowInfo.getTitle())) {
                                            BookedList.remove(rowInfo.getTitle());
                                        }
                                    }
                                }
                            }
                        });


                        //viewHolder.time0TextView.setText(rowInfo.getEquipCount());

                    } else if (rowInfo.getAvailable().equalsIgnoreCase("available multiBookable")) {

                        viewHolder.time60TextView.setText(rowInfo.getEquipCount());
                        final GradientDrawable time0lay = (GradientDrawable) viewHolder.time0_lay.getBackground();
                        time0lay.setColor(getResources().getColor(R.color.bg2));
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time15_lay.getBackground();
                        time15lay.setColor(getResources().getColor(R.color.bg2));
                        final GradientDrawable time30lay = (GradientDrawable) viewHolder.time30_lay.getBackground();
                        time30lay.setColor(getResources().getColor(R.color.bg2));
                        final GradientDrawable time45lay = (GradientDrawable) viewHolder.time45_lay.getBackground();
                        time45lay.setColor(getResources().getColor(R.color.bg2));
                        final GradientDrawable time60lay = (GradientDrawable) viewHolder.time60_lay.getBackground();
                        if (rowInfo.getSelected().equalsIgnoreCase("true")) {
                            time60lay.setColor(getResources().getColor(R.color.bg9));
                        } else {
                            time60lay.setColor(getResources().getColor(R.color.bg5));
                        }
                        viewHolder.time60_lay.setClickable(true);
                        viewHolder.time60_lay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isMultiBookable.equalsIgnoreCase("true")) {
                                    continueMultiAvailableSelection(rowInfo,time60lay);
                                } else {
                                    if (rowInfo.getSelected().equalsIgnoreCase("")) {
                                        time60lay.setColor(getResources().getColor(R.color.bg9));
                                        rowInfo.setSelected("true");
                                        HashMap<String, String> hashmap = new HashMap<String, String>();
                                        hashmap.put("equipUniq", rowInfo.getEquipUniq());
                                        hashmap.put("title", rowInfo.getTitle());
                                        hashmap.put("slotEndTime", rowInfo.getslotEndTime());
                                        hashmap.put("class", rowInfo.getAvailable());
                                        hashmap.put("slotStartTime", rowInfo.getslotStartTime());
                                        hashmap.put("equipCount", rowInfo.getEquipCount());
                                        BookedList.put(rowInfo.getTitle(), hashmap);
                                    } else {
                                        time60lay.setColor(getResources().getColor(R.color.bg5));
                                        rowInfo.setSelected("");
                                        if (BookedList.containsKey(rowInfo.getTitle())) {
                                            BookedList.remove(rowInfo.getTitle());
                                        }
                                    }
                                }
                            }
                        });

                    } else if (rowInfo.getAvailable().equalsIgnoreCase("booked")) {
                        viewHolder.time60TextView.setText(rowInfo.getEquipCount());
                        final GradientDrawable time60lay = (GradientDrawable) viewHolder.time60_lay.getBackground();
                        time60lay.setColor(getResources().getColor(R.color.bg6));

                    } else {
                        final GradientDrawable time0lay = (GradientDrawable) viewHolder.time0_lay.getBackground();
                        time0lay.setColor(getResources().getColor(R.color.lightgray02));
                        final GradientDrawable time15lay = (GradientDrawable) viewHolder.time15_lay.getBackground();
                        time15lay.setColor(getResources().getColor(R.color.lightgray02));
                        final GradientDrawable time30lay = (GradientDrawable) viewHolder.time30_lay.getBackground();
                        time30lay.setColor(getResources().getColor(R.color.lightgray02));
                        final GradientDrawable time45lay = (GradientDrawable) viewHolder.time45_lay.getBackground();
                        time45lay.setColor(getResources().getColor(R.color.lightgray02));
                        final GradientDrawable time60lay = (GradientDrawable) viewHolder.time60_lay.getBackground();
                        time60lay.setColor(getResources().getColor(R.color.lightgray02));
                    }
                }

                /*final Boolean finalIsClickable = isClickable;
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (finalIsClickable) {
                        }
                    }
                });*/
            }
        }

        private class DateViewHolder extends RecyclerView.ViewHolder {
            public TextView dateTextView;
            public TextView time0Text;
            public TextView time15Text;
            public TextView time30Text;
            public TextView time45Text;
            public TextView time60Text;

            public DateViewHolder(View itemView) {
                super(itemView);
                this.dateTextView = (TextView) itemView.findViewById(R.id.date_type);
                this.time0Text = (TextView) itemView.findViewById(R.id.time0);
                this.time15Text = (TextView) itemView.findViewById(R.id.time15);
                this.time30Text = (TextView) itemView.findViewById(R.id.time30);
                this.time45Text = (TextView) itemView.findViewById(R.id.time45);
                this.time60Text = (TextView) itemView.findViewById(R.id.time60);


            }
        }

        private class TimeViewHolder extends RecyclerView.ViewHolder {
            public TextView roomTypeTextView;

            public TimeViewHolder(View view) {
                super(view);
                this.roomTypeTextView = (TextView) view.findViewById(R.id.time);

            }
        }

        private class OrderViewHolder extends RecyclerView.ViewHolder {
            public TextView time0TextView;
            public TextView time15TextView;
            public TextView time30TextView;
            public TextView time45TextView;
            public TextView time60TextView;
            public LinearLayout time0_lay, time15_lay, time30_lay, time45_lay, time60_lay;
            public View view;

            public OrderViewHolder(View view) {
                super(view);
                this.view = view;
                this.time0TextView = (TextView) view.findViewById(R.id.time0_text);
                this.time15TextView = (TextView) view.findViewById(R.id.time15_text);
                this.time30TextView = (TextView) view.findViewById(R.id.time30_text);
                this.time45TextView = (TextView) view.findViewById(R.id.time45_text);
                this.time60TextView = (TextView) view.findViewById(R.id.time60_text);

                this.time0_lay = (LinearLayout) view.findViewById(R.id.time0_lay);
                this.time15_lay = (LinearLayout) view.findViewById(R.id.time15_lay);
                this.time30_lay = (LinearLayout) view.findViewById(R.id.time30_lay);
                this.time45_lay = (LinearLayout) view.findViewById(R.id.time45_lay);
                this.time60_lay = (LinearLayout) view.findViewById(R.id.time60_lay);
            }
        }

        private class TitleViewHolder extends RecyclerView.ViewHolder {
            public TextView titleTextView;

            public TitleViewHolder(View view) {
                super(view);
                this.titleTextView = (TextView) view.findViewById(R.id.slot_title);
            }
        }

        public void setRoomInfoList(List<TimeInfo> TimeInfoList) {
            this.TimeInfoList = TimeInfoList;
        }

        public void setDateInfoList(List<DateInfo> dateInfoList) {
            this.dateInfoList = dateInfoList;
        }

        public void setrowList(List<List<rowInfo>> rowList) {
            this.rowList = rowList;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    private void continueMultiAvailableSelection(rowInfo rowInfo1, GradientDrawable time0lay) {
        if (!(rowInfo1.getSelected().equalsIgnoreCase(""))) {
            Boolean isOk = false;
            if (continueSelectArray.containsKey(rowInfo1.getComment())) {
                if (continueSelectArray.get(rowInfo1.getComment()).containsKey(rowInfo1.getComment() + "#" + (rowInfo1.getInserted() - 1)) && continueSelectArray.get(rowInfo1.getComment()).containsKey(rowInfo1.getComment() + "#" + (rowInfo1.getInserted() + 1))) {
                    isOk = true;
                }
            }
            if (!isOk) {
                continueSelectArray.get(rowInfo1.getComment()).remove(rowInfo1.getComment() + "#" + rowInfo1.getInserted());
                time0lay.setColor(getResources().getColor(R.color.bg2));
                rowInfo1.setSelected("");
                if (BookedList.containsKey(rowInfo1.getTitle())) {
                    BookedList.remove(rowInfo1.getTitle());
                }
            } else {
                setToast("Invalid");
            }
        } else {
            Boolean isAdd = false;
            if (continueSelectArray.size() == 0)
                isAdd = true;
            else if (continueSelectArray.containsKey(rowInfo1.getComment())) {
                if ((continueSelectArray.get(rowInfo1.getComment()).size() == 0 || continueSelectArray.get(rowInfo1.getComment()).containsKey(rowInfo1.getComment() + "#" + (rowInfo1.getInserted() - 1)) || continueSelectArray.get(rowInfo1.getComment()).containsKey(rowInfo1.getComment() + "#" + (rowInfo1.getInserted() + 1)))) {
                    isAdd = true;
                } else
                    isAdd = false;
            } else
                isAdd = true;
            if (isAdd) {
                if (continueSelectArray.containsKey(rowInfo1.getComment()))
                    continueSelectArray.get(rowInfo1.getComment()).put((rowInfo1.getComment() + "#" + rowInfo1.getInserted()), rowInfo1.getInserted());
                else {
                    HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
                    hashMap.put((rowInfo1.getComment() + "#" + rowInfo1.getInserted()), rowInfo1.getInserted());
                    continueSelectArray.put(rowInfo1.getComment(), hashMap);
                }
                HashMap<String, String> hashmap = new HashMap<String, String>();
                time0lay.setColor(getResources().getColor(R.color.bg9));
                rowInfo1.setSelected("true");
                System.out.println("START" + rowInfo1.getTitle());
                hashmap.put("equipUniq", rowInfo1.getEquipUniq());
                hashmap.put("title", rowInfo1.getTitle());
                hashmap.put("slotEndTime", rowInfo1.getslotEndTime());
                hashmap.put("class", rowInfo1.getAvailable());
                hashmap.put("slotStartTime", rowInfo1.getslotStartTime());
                hashmap.put("equipCount", rowInfo1.getEquipCount());
                BookedList.put(rowInfo1.getTitle(), hashmap);
            } else {
                setToast("Please Continue Book...");
            }
        }
    }

    public void continueAvailableSelection(rowInfo rowInfo1, GradientDrawable time0lay) {

        if (!(rowInfo1.getSelected().equalsIgnoreCase(""))) {
            Boolean isOk = false;
            if (continueSelectArray.containsKey(rowInfo1.getComment())) {
                if (continueSelectArray.get(rowInfo1.getComment()).containsKey(rowInfo1.getComment() + "#" + (rowInfo1.getInserted() - 1)) && continueSelectArray.get(rowInfo1.getComment()).containsKey(rowInfo1.getComment() + "#" + (rowInfo1.getInserted() + 1))) {
                    isOk = true;
                }
            }
            if (!isOk) {
                continueSelectArray.get(rowInfo1.getComment()).remove(rowInfo1.getComment() + "#" + rowInfo1.getInserted());
                time0lay.setColor(getResources().getColor(R.color.bg2));
                rowInfo1.setSelected("");
                if (BookedList.containsKey(rowInfo1.getTitle())) {
                    BookedList.remove(rowInfo1.getTitle());
                }
            } else {
                setToast("Invalid");
            }
        } else {
            Boolean isAdd = false;
            if (continueSelectArray.size() == 0)
                isAdd = true;
            else if (continueSelectArray.containsKey(rowInfo1.getComment())) {
                if ((continueSelectArray.get(rowInfo1.getComment()).size() == 0 || continueSelectArray.get(rowInfo1.getComment()).containsKey(rowInfo1.getComment() + "#" + (rowInfo1.getInserted() - 1)) || continueSelectArray.get(rowInfo1.getComment()).containsKey(rowInfo1.getComment() + "#" + (rowInfo1.getInserted() + 1)))) {
                    isAdd = true;
                } else
                    isAdd = false;
            } else
                isAdd = true;
            if (isAdd) {
                if (continueSelectArray.containsKey(rowInfo1.getComment()))
                    continueSelectArray.get(rowInfo1.getComment()).put((rowInfo1.getComment() + "#" + rowInfo1.getInserted()), rowInfo1.getInserted());
                else {
                    HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
                    hashMap.put((rowInfo1.getComment() + "#" + rowInfo1.getInserted()), rowInfo1.getInserted());
                    continueSelectArray.put(rowInfo1.getComment(), hashMap);
                }
                HashMap<String, String> hashmap = new HashMap<String, String>();
                time0lay.setColor(getResources().getColor(R.color.bg9));
                rowInfo1.setSelected("true");
                System.out.println("START" + rowInfo1.getTitle());
                hashmap.put("equipUniq", rowInfo1.getEquipUniq());
                hashmap.put("title", rowInfo1.getTitle());
                hashmap.put("slotEndTime", rowInfo1.getslotEndTime());
                hashmap.put("class", rowInfo1.getAvailable());
                hashmap.put("slotStartTime", rowInfo1.getslotStartTime());
                hashmap.put("equipCount", rowInfo1.getEquipCount());
                BookedList.put(rowInfo1.getTitle(), hashmap);
            } else {
                setToast("Please Continue Book...");
            }
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
        //Write your code here
        //Toast.makeText(getApplicationContext(), "Back press disabled!", Toast.LENGTH_SHORT).show();
    }

}
