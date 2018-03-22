package com.guruinfo.scm.tms;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseFragment;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.utils.ApiCalls;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import butterknife.Bind;
import butterknife.ButterKnife;
import static com.guruinfo.scm.common.BaseFragmentActivity.loadLeadStyleSpinner;
import static com.guruinfo.scm.common.BaseFragmentActivity.setToast;
/**
 * Created by Kannan G on 1/4/2017.
 */
public class TMSQuickTaskActivity extends BaseFragment {
    String TAG = "TMSQuickTaskActivity";
    Context context;
    SessionManager session;
    BackgroundTask backgroundTask;
    String uid;
    String Cre_Id;
    String requestParameter;
    String listType = "";
    String frmId = "";
    ArrayList<HashMap<String, String>> companyArrayList = new ArrayList<>();
    ArrayList<HashMap<String, String>> wingArrayList = new ArrayList<>();
    ArrayList<HashMap<String, String>> departmentArrayList = new ArrayList<>();
    ArrayList<HashMap<String, String>> designationArrayList = new ArrayList<>();
    ArrayList<HashMap<String, String>> IndividualArrayList = new ArrayList<>();
    static EditText editDateTextView;
    @Bind(R.id.tms_taskName)
    EditText taskNameEdit;
    @Bind(R.id.tms_start_date)
    EditText startDate;
    @Bind(R.id.tms_start_time)
    EditText startTime;
    @Bind(R.id.tms_end_date)
    EditText endDate;
    @Bind(R.id.tms_end_time)
    EditText endTime;
    @Bind(R.id.tms_company_spinner)
    Spinner companySpinner;
    @Bind(R.id.tms_department_spinner)
    Spinner departmentSpinner;
    @Bind(R.id.tms_wing_spinner)
    Spinner wingSpinner;
    @Bind(R.id.tms_designation_spinner)
    Spinner designationSpinner;
    @Bind(R.id.tms_individual_spinner)
    Spinner individualSpinner;
    @Bind(R.id.tms_selfassign_checkbox)
    CheckBox selfAssignCheck;
    @Bind(R.id.tms_doTheyReassign_checkbox)
    CheckBox doTheyCheck;
    @Bind(R.id.tms_allowSubTask_checkbox)
    CheckBox allowSubTaskCheck;
    @Bind(R.id.quickTask_create_button)
    Button createButton;
    @Bind(R.id.quickTask_cancel_button)
    Button cancelButton;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tms_quick_task, container, false);
        ButterKnife.bind(this, view);
        TMSFragmentActivity.actionBarNewRequestBtn.setVisibility(View.GONE);
        TMSFragmentActivity.actionBarFilterBtn.setVisibility(View.GONE);
        TMSFragmentActivity.actionBarBookMarkBtn.setVisibility(View.GONE);
        TMSFragmentActivity.homeIcon.setVisibility(View.VISIBLE);
        TMSFragmentActivity.actionBarTitle.setText("Create Quick Task");
        listType = getArguments().getString("listType");
        requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'CREATE_QUICK_TASK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "'}";
        System.out.println(requestParameter);
        getLoadData(requestParameter, "QUICK_TASK");
        selfAssignCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    companySpinner.setEnabled(false);
                    departmentSpinner.setEnabled(false);
                    wingSpinner.setEnabled(false);
                    designationSpinner.setEnabled(false);
                    individualSpinner.setEnabled(false);
                    companySpinner.setSelection(0);
                    departmentSpinner.setSelection(0);
                    individualSpinner.setSelection(0);
                    TextView errorcompanyt = (TextView) companySpinner.getSelectedView();
                    errorcompanyt.setError(null);
                    TextView errordepartment = (TextView) departmentSpinner.getSelectedView();
                    errordepartment.setError(null);
                    TextView errorindividual = (TextView) individualSpinner.getSelectedView();
                    errorindividual.setError(null);
                    ArrayList<HashMap<String, String>> emptyArrayList = new ArrayList<HashMap<String, String>>();
                    loadLeadStyleSpinner(wingSpinner, emptyArrayList, new String[]{"value"}, "Wing");
                    loadLeadStyleSpinner(designationSpinner, emptyArrayList, new String[]{"value"}, "Designation");
                } else {
                    companySpinner.setEnabled(true);
                    departmentSpinner.setEnabled(true);
                    wingSpinner.setEnabled(true);
                    designationSpinner.setEnabled(true);
                    individualSpinner.setEnabled(true);
                }
            }
        });
        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    int selectSpinnerPosition = (companySpinner.getSelectedItemPosition()) - 1;
                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'department','comp_id':'" + companyArrayList.get(selectSpinnerPosition).get("id") + "','dep_id':'','wing_id':'','des_id':'','indiv_id':''}";
                    System.out.println(requestParameter);
                    getLoadData(requestParameter, "Department");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    int selectSpinnerPosition = (departmentSpinner.getSelectedItemPosition()) - 1;
                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'wing','comp_id':'" + companyArrayList.get(selectSpinnerPosition).get("id") + "','dep_id':'" + departmentArrayList.get(selectSpinnerPosition).get("id") + "','wing_id':'','des_id':'','indiv_id':''}";
                    System.out.println(requestParameter);
                    getLoadData(requestParameter, "Wing");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        wingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    int selectSpinnerPosition = (wingSpinner.getSelectedItemPosition()) - 1;
                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'designation','comp_id':'" + companyArrayList.get(selectSpinnerPosition).get("id") + "','dep_id':'" + departmentArrayList.get(departmentSpinner.getSelectedItemPosition() - 1).get("id") + "','wing_id':'" + wingArrayList.get(selectSpinnerPosition).get("id") + "','des_id':'','indiv_id':''}";
                    System.out.println(requestParameter);
                    getLoadData(requestParameter, "Designation");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        designationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    int selectSpinnerPosition = (wingSpinner.getSelectedItemPosition()) - 1;
                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'individual','comp_id':'" + companyArrayList.get(selectSpinnerPosition).get("id") + "','dep_id':'" + departmentArrayList.get(departmentSpinner.getSelectedItemPosition() - 1).get("id") + "','wing_id':'" + wingArrayList.get(wingSpinner.getSelectedItemPosition() - 1).get("id") + "','des_id':'" + designationArrayList.get(selectSpinnerPosition).get("id") + "','indiv_id':''}";
                    System.out.println(requestParameter);
                    getLoadData(requestParameter, "Individual");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDateTextView = startDate;
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDateTextView = endDate;
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mHour, mMinute;
                final Calendar c = Calendar.getInstance();
                if (!startTime.getText().toString().isEmpty() && !startTime.getText().toString().equalsIgnoreCase("-")) {
                    String timeHours[] = startTime.getText().toString().split(":");
                    mHour = Integer.parseInt(timeHours[0]);
                    mMinute = Integer.parseInt(timeHours[1]);
                } else {
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                view.setIs24HourView(true);
                                NumberFormat f = new DecimalFormat("00");
                                startTime.setText(f.format(hourOfDay) + ":" + f.format(minute) + ":00");
                                startTime.setError(null);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mHour, mMinute;
                final Calendar c = Calendar.getInstance();
                if (!endTime.getText().toString().isEmpty() && !endTime.getText().toString().equalsIgnoreCase("-")) {
                    String timeHours[] = endTime.getText().toString().split(":");
                    mHour = Integer.parseInt(timeHours[0]);
                    mMinute = Integer.parseInt(timeHours[1]);
                } else {
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                view.setIs24HourView(true);
                                NumberFormat f = new DecimalFormat("00");
                                endTime.setText(f.format(hourOfDay) + ":" + f.format(minute) + ":00");
                                endTime.setError(null);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int errorCount = 0;
                    String formName = "TMSQUICKTASK";
                    JSONObject submitJson = new JSONObject();
                    submitJson.put("formName", formName);
                    submitJson.put("quick_task_name", "CreateQuickTask");
                    if (!(taskNameEdit.getText().toString().trim().length() > 0)) {
                        taskNameEdit.setError("Field is Required");
                        errorCount++;
                    } else if (!(startDate.getText().toString().trim().length() > 0)) {
                        startDate.setError("Field is Required");
                        errorCount++;
                    } else if (!(startTime.getText().toString().trim().length() > 0)) {
                        startTime.setError("Field is Required");
                        errorCount++;
                    } else if (!(endDate.getText().toString().trim().length() > 0)) {
                        endDate.setError("Field is Required");
                        errorCount++;
                    } else if (!(endTime.getText().toString().trim().length() > 0)) {
                        endTime.setError("Field is Required");
                        errorCount++;
                    } else if (selfAssignCheck.isChecked()) {
                        submitJson.put(formName + "_2051_Company_1_1", "");
                        submitJson.put(formName + "_2051_Department_1_1", "");
                        submitJson.put(formName + "_2051_Wing_1_1", "");
                        submitJson.put(formName + "_2051_Designation_1_1", "");
                        submitJson.put(formName + "_2051_Individual_1_1", "");
                        submitJson.put(formName + "_2051_SelfAssignee_1_1", "1");
                    } else {
                        if ((companySpinner.getSelectedItemPosition() - 1) >= 0) {
                            submitJson.put(formName + "_2051_Company_1_1", companyArrayList.get(companySpinner.getSelectedItemPosition() - 1).get("id"));
                        } else {
                            TextView errorcompany = (TextView) companySpinner.getSelectedView();
                            errorcompany.setError("Field is Required");
                            errorCount++;
                        }
                        if ((departmentSpinner.getSelectedItemPosition() - 1) >= 0) {
                            submitJson.put(formName + "_2051_Department_1_1", departmentArrayList.get(departmentSpinner.getSelectedItemPosition() - 1).get("id"));
                        } else {
                            TextView errordepartment = (TextView) departmentSpinner.getSelectedView();
                            errordepartment.setError("Field is Required");
                            errorCount++;
                        }
                        if ((wingSpinner.getSelectedItemPosition() - 1) >= 0) {
                            submitJson.put(formName + "_2051_Wing_1_1", wingArrayList.get(wingSpinner.getSelectedItemPosition() - 1).get("id"));
                        } else {
                            TextView errordepartment = (TextView) wingSpinner.getSelectedView();
                            errordepartment.setError("Field is Required");
                            errorCount++;
                        }
                        if ((designationSpinner.getSelectedItemPosition() - 1) >= 0) {
                            submitJson.put(formName + "_2051_Designation_1_1", designationArrayList.get(designationSpinner.getSelectedItemPosition() - 1).get("id"));
                        } else {
                            TextView errordepartment = (TextView) designationSpinner.getSelectedView();
                            errordepartment.setError("Field is Required");
                            errorCount++;
                        }
                        if ((individualSpinner.getSelectedItemPosition() - 1) >= 0) {
                            submitJson.put(formName + "_2051_Individual_1_1", IndividualArrayList.get(individualSpinner.getSelectedItemPosition() - 1).get("id"));
                        } else {
                            TextView errordepartment = (TextView) individualSpinner.getSelectedView();
                            errordepartment.setError("Field is Required");
                            errorCount++;
                        }
                        submitJson.put(formName + "_2051_SelfAssignee_1_1", "0");
                    }
                    if (errorCount == 0) {
                        if (doTheyCheck.isChecked())
                            submitJson.put(formName + "_2051_DoTheyReassign_1_1", "1");
                        else
                            submitJson.put(formName + "_2051_DoTheyReassign_1_1", "0");
                        if (allowSubTaskCheck.isChecked())
                            submitJson.put(formName + "_2050_AllowSubTask_1_1", "1");
                        else
                            submitJson.put(formName + "_2050_AllowSubTask_1_1", "0");
                        submitJson.put(formName + "_2050_TaskName_1_1", taskNameEdit.getText().toString().trim());
                        submitJson.put(formName + "_2050_StartDate_1_1", startDate.getText().toString().trim().replace("/", "-") + " " + startTime.getText().toString().trim());
                        submitJson.put(formName + "_2050_EndDate_1_1", endDate.getText().toString().trim().replace("/", "-") + " " + endTime.getText().toString().trim());
                        submitJson.put("frmId", frmId);
                        submitJson.put("categoryIds", "2050,2051");
                        submitJson.put(formName + "_2050_categoryCnt", "1");
                        submitJson.put(formName + "_2051_categoryCnt", "1");
                        String msdId = "2060,2053,2054,2055";
                        submitJson.put("2050_1_masterIds", msdId);
                        String msId = "2168,2078,2079,2080,2081,2082,2083";
                        submitJson.put("2051_1_masterIds", msId);
                        submitJson.put("Action", "TASK_MANAGEMENT_SYSTEM");
                        submitJson.put("submode", "ADD_TASK_SUBMIT");
                        submitJson.put("TaskId", "0");
                        submitJson.put("UID", uid);
                        submitJson.put("Cre_Id", Cre_Id);
                        String msdIdArray[] = msdId.split(",");
                        String msIdArray[] = msId.split(",");
                        for (int i = 0; i < msdIdArray.length; i++) {
                            submitJson.put(formName + "_" + msdIdArray[i] + "_" + 1 + "_lineNo", 1);
                            submitJson.put(formName+"_master_type_"+msdIdArray[i],"header");
                        }
                        for (int i = 0; i < msIdArray.length; i++) {
                            submitJson.put(formName + "_" + msIdArray[i] + "_" + 1 + "_lineNo", 1);
                            submitJson.put(formName+"_master_type_"+msIdArray[i],"header");
                        }
                        submitJson.put(formName+"_data_type_2060","RADIO");
                        submitJson.put(formName+"_data_type_2053","TEXTBOX");
                        submitJson.put(formName+"_data_type_2054","DATETIME");
                        submitJson.put(formName+"_data_type_2055","DATETIME");
                        submitJson.put(formName+"_data_type_2168","DROPDOWN_S");
                        submitJson.put(formName+"_data_type_2078","DROPDOWN_S");
                        submitJson.put(formName+"_data_type_2079","DROPDOWN_S");
                        submitJson.put(formName+"_data_type_2080","DROPDOWN_S");
                        submitJson.put(formName+"_data_type_2081","DROPDOWN_S");
                        submitJson.put(formName+"_data_type_2082","CHECKBOX");
                        submitJson.put(formName+"_data_type_2083","CHECKBOX");
                       /* submitJson.put(formName+"_2050_TaskName_1_1","RADIO");
                        submitJson.put(formName+"_2050_EndDate_1_1","TEXTBOX");
                        submitJson.put(formName+"_2051_Company_1_1","DATETIME");
                        submitJson.put(formName+"_2051_Department_1_1","DATETIME");
                        submitJson.put(formName+"_2051_Wing_1_1","DROPDOWN_S");
                        submitJson.put(formName+"_2051_Designation_1_1","DROPDOWN_S");
                        submitJson.put(formName+"_2051_Individual_1_1","DROPDOWN_S");
                        submitJson.put(formName+"_2051_SelfAssignee_1_1","DROPDOWN_S");
                        submitJson.put(formName+"_2051_DoTheyReassign_1_1","DROPDOWN_S");
                        submitJson.put(formName+"_2050_AllowSubTask_1_1","CHECKBOX");
                        submitJson.put(formName+"_2050_StartDate_1_1","StartDate");
*/
                        requestParameter = submitJson.toString();
                        System.out.println(requestParameter);
                        getLoadData(requestParameter, "TASK_SUBMIT");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("listType", listType);
                navigateToNextFragment(TMSListTreeActivity.class.getName(), bundle);
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
                        showInternetDialog(context, values, requestParameter, flag);
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
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getLoadData(requestParameterValues, flag);
            }
        });
        dialog.show();
    }
    private void parseJSONResponse(JSONObject response, String flag) {
        try {
            if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_1)) {
                if (flag.equalsIgnoreCase("QUICK_TASK")) {
                    frmId = response.getString("frmConfigId");
                    String eDate[] = response.get("EndDate").toString().split(" ");
                    String sDate[] = response.get("StartDate").toString().split(" ");
                    startDate.setText(sDate[0]);
                    startTime.setText(sDate[1]);
                    endDate.setText(eDate[0]);
                    endTime.setText(eDate[1]);
                    companyArrayList = ApiCalls.getArraylistfromJson(response.getString("Company"));
                    loadLeadStyleSpinner(companySpinner, companyArrayList, new String[]{"value"}, "Company");
                    departmentArrayList = new ArrayList<>();
                    loadLeadStyleSpinner(departmentSpinner, departmentArrayList, new String[]{"value"}, "Department");
                    wingArrayList = new ArrayList<>();
                    loadLeadStyleSpinner(wingSpinner, wingArrayList, new String[]{"value"}, "Wing");
                    designationArrayList = new ArrayList<>();
                    loadLeadStyleSpinner(designationSpinner, designationArrayList, new String[]{"value"}, "Designation");
                    IndividualArrayList = new ArrayList<>();
                    loadLeadStyleSpinner(individualSpinner, IndividualArrayList, new String[]{"value"}, "Individual");
                } else if (flag.equalsIgnoreCase("Department")) {
                    departmentArrayList = ApiCalls.getArraylistfromJson(response.getString("Options"));
                    IndividualArrayList = ApiCalls.getArraylistfromJson(response.getString("Individual"));
                    loadLeadStyleSpinner(departmentSpinner, departmentArrayList, new String[]{"value"}, flag);
                    loadLeadStyleSpinner(individualSpinner, IndividualArrayList, new String[]{"value"}, "Individual");
                    ArrayList<HashMap<String, String>> emptyArrayList = new ArrayList<>();
                    loadLeadStyleSpinner(wingSpinner, emptyArrayList, new String[]{"value"}, "Wing");
                    loadLeadStyleSpinner(designationSpinner, emptyArrayList, new String[]{"value"}, "Designation");
                } else if (flag.equalsIgnoreCase("Wing")) {
                    wingArrayList = ApiCalls.getArraylistfromJson(response.getString("Options"));
                    IndividualArrayList = ApiCalls.getArraylistfromJson(response.getString("Individual"));
                    loadLeadStyleSpinner(wingSpinner, wingArrayList, new String[]{"value"}, flag);
                    loadLeadStyleSpinner(individualSpinner, IndividualArrayList, new String[]{"value"}, "Individual");
                    ArrayList<HashMap<String, String>> emptyArrayList = new ArrayList<>();
                    loadLeadStyleSpinner(designationSpinner, emptyArrayList, new String[]{"value"}, "Designation");
                } else if (flag.equalsIgnoreCase("Designation")) {
                    designationArrayList = ApiCalls.getArraylistfromJson(response.getString("Options"));
                    ArrayList<HashMap<String, String>> IndividualArrayList = new ArrayList<>();
                    IndividualArrayList = ApiCalls.getArraylistfromJson(response.getString("Individual"));
                    loadLeadStyleSpinner(designationSpinner, designationArrayList, new String[]{"value"}, flag);
                    loadLeadStyleSpinner(individualSpinner, IndividualArrayList, new String[]{"value"}, "Individual");
                } else if (flag.equalsIgnoreCase("Individual")) {
                    IndividualArrayList = ApiCalls.getArraylistfromJson(response.getString("Options"));
                    loadLeadStyleSpinner(individualSpinner, IndividualArrayList, new String[]{"value"}, flag);
                } else if (flag.equalsIgnoreCase("TASK_SUBMIT")) {
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                    Bundle bundle = new Bundle();
                    bundle.putString("listType", listType);
                    navigateToNextFragment(TMSListTreeActivity.class.getName(), bundle);
                } else {
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                }
            } else if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase("0")) {
                setToast(response.getString(AppContants.RESPONSE_MESSAGE));
            } else if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_403)) {
                showSessionScreensDialog(context, response.getString(AppContants.RESPONSE_MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = 2000, mm = 01, dd = 01;
            if (editDateTextView.getText().toString().trim().length() > 0) {
                String date[] = editDateTextView.getText().toString().split("/");
                dd = Integer.parseInt(date[0]);
                mm = (Integer.parseInt(date[1])-1);
                yy = Integer.parseInt(date[2]);
            } else {
                yy = calendar.get(Calendar.YEAR);
                mm = calendar.get(Calendar.MONTH);
                dd = calendar.get(Calendar.DAY_OF_MONTH);
            }
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
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
}