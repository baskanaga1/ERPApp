package com.guruinfo.scm.Equipment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.guruinfo.scm.ContractorsDao;
import com.guruinfo.scm.DaoMaster;
import com.guruinfo.scm.PjtStore;
import com.guruinfo.scm.PjtStoreDao;
import com.guruinfo.scm.R;
import com.guruinfo.scm.RequestedBy;
import com.guruinfo.scm.RequestedByDao;
import com.guruinfo.scm.SCMDashboardActivityLatest;
import com.guruinfo.scm.StatusDao;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseFragment;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.db.SCMDataBaseOpenHelper;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.ui.AutoCompleteTextviewAdapter;
import com.guruinfo.scm.common.ui.SCMEditText;
import com.guruinfo.scm.common.ui.SCMTextView;
import com.guruinfo.scm.common.utils.ApiCalls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.guruinfo.scm.SCMDashboardActivityLatest.NavigationFragmentManager;
import static com.guruinfo.scm.common.utils.BaseAppCompactFragmentActivity.loadSpinner;


public class EquipFilterFragment extends BaseFragment {
    String TAG = "EquipFilterFragment";
    @Bind(R.id.apply_filter)
    LinearLayout applyLinearLayout;
    @Bind(R.id.reset_filter)
    LinearLayout resetLinearLayout;
    @Bind(R.id.mrFilterMenu)
    ListView mrFilterMenu;
    @Bind(R.id.mrSelectedFilter)
    LinearLayout mrSelectedFilter;
    EditText FilterEditText;
    public static EditText FilterDate;
    public static EditText FilterTree;
    ProgressDialog mDialog;
    SessionManager session;
    String uid, Cre_Id;
    String requestParameter;
    BackgroundTask backgroundTask;
    String Submode;
    String listActionName;
    String listSubmodeName;
    String projectId = "";
    RequestedByDao requestedByDao;
    StatusDao statusDao;
    ContractorsDao contractorsDao;
    AutoCompleteTextView FilterAutocomplete;


    public static String stageId = "";
    String filterName = "";
    String filterType = "";
    String frmnameCTRL = "EQUIPMENT";
    boolean isView;
    int filterTextvalue = 0;

    ArrayList<HashMap<String, String>> FilterList = new ArrayList<>();
    HashMap<String, String> filterValue = new HashMap<>();
    HashMap<String, String> filterAutoCompleteId = new HashMap<>();


    ArrayList<HashMap<String, String>> EquipmentName = new ArrayList<>();
    ArrayList<HashMap<String, String>> RequestType = new ArrayList<>();
    ArrayList<HashMap<String, String>> RequestBy = new ArrayList<>();
    ArrayList<HashMap<String, String>> AssignTo = new ArrayList<>();
    ArrayList<HashMap<String, String>> Priority = new ArrayList<>();
    ArrayList<HashMap<String, String>> Status = new ArrayList<>();
    ArrayList<HashMap<String, String>> ApprovalStatus = new ArrayList<>();
    ArrayList<HashMap<String, String>> ClockSheetStatus = new ArrayList<>();
    ArrayList<HashMap<String, String>> project = new ArrayList<>();

    View previous;


    // TODO: Rename and change types and number of parameters
    public static EquipFilterFragment newInstance(Bundle bundle) {
        EquipFilterFragment fragment = new EquipFilterFragment();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.search:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        mDialog = new ProgressDialog(context);
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
        previous = new View(getActivity());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            listActionName = bundle.getString("LIST_ACTION");
            listSubmodeName = bundle.getString("LIST_SUBMODE");
            ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + bundle.getString("SEARCH_TYPE") + "</font>"));

        }

        /*SCMDataBaseOpenHelper SCMDatabaseOpenHelper = new SCMDataBaseOpenHelper(context, "SCM", null);
        SQLiteDatabase db = SCMDatabaseOpenHelper.getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();*/

        requestedByDao = daoSession.getRequestedByDao();
        statusDao = daoSession.getStatusDao();
        contractorsDao = daoSession.getContractorsDao();

        List<RequestedBy> requestedByLists = requestedByDao.queryBuilder().where(RequestedByDao.Properties.Status.eq("approved"), RequestedByDao.Properties.Display.eq("active")).list();
        Log.d(TAG, "requestedByLists Size" + requestedByLists.size());
        for (int i = 0; i < requestedByLists.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("id", requestedByLists.get(i).getLoad_id());
            hashMap.put("value", requestedByLists.get(i).getValue());
            RequestBy.add(hashMap);
            AssignTo.add(hashMap);
        }

        PjtStoreDao pjtStoreDao = daoSession.getPjtStoreDao();
        List<PjtStore> projectLists = pjtStoreDao.queryBuilder().where(PjtStoreDao.Properties.User_id.eq(uid)).list();
        if (projectLists.size() > 0)
            for (int i = 0; i < projectLists.size(); i++) {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", projectLists.get(i).getLoad_id());
                map.put("value", projectLists.get(i).getValue());
                project.add(map);
            }

        HashMap<String, String> listNameType = new HashMap<>();
        listNameType.put("Name", "Project Name");
        listNameType.put("Type", "Spinner");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "Equipment Name");
        listNameType.put("Type", "Spinner");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "Request Type");
        listNameType.put("Type", "Spinner");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "Request Date From");
        listNameType.put("Type", "DatePicker");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "Request Date To");
        listNameType.put("Type", "DatePicker");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "Equip Required Date From");
        listNameType.put("Type", "DatePicker");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "Equip Required Date To");
        listNameType.put("Type", "DatePicker");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "Request By");
        listNameType.put("Type", "AutoCompleteTextView");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "Assign To");
        listNameType.put("Type", "AutoCompleteTextView");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "Priority");
        listNameType.put("Type", "Spinner");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "Status");
        listNameType.put("Type", "Spinner");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "Approval Status");
        listNameType.put("Type", "Spinner");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "Clock Sheet Status");
        listNameType.put("Type", "Spinner");
        FilterList.add(new HashMap<String, String>(listNameType));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mr_filter_main, container, false);
        final Bundle bundle = this.getArguments();
        if (bundle != null) {

        }

        ButterKnife.bind(this, view);
        isView = true;
        filterName = "Project Name";
        stageId = "";
        dynamicLayout("Spinner");
        load(listActionName, "META_DATA", "", "");
        resetLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filterValue.size()>0) {
                    filterValue.clear();
                    stageId = "";
                    dynamicLayout(filterType);
                }
            }
        });
        applyLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String projectName ="", equipName = "", requestType = "", requestDateFrom = "", requestDateTo = "", equipRequiredDateFrom = "", equipRequiredDateTo = "",
                        requestBy = "", assignTo = "", priority = "", status = "", approvalStatus = "", clockSheetStatus = "";

                if (filterValue.containsKey("Request Date From"))
                    requestDateFrom = filterValue.get("Request Date From");
                if (filterValue.containsKey("Request Date To"))
                    requestDateTo = filterValue.get("Request Date To");
                if (filterValue.containsKey("Equip Required Date From"))
                    equipRequiredDateFrom = filterValue.get("Equip Required Date From");
                if (filterValue.containsKey("Equip Required Date To"))
                    equipRequiredDateTo = filterValue.get("Equip Required Date To");
                if (filterValue.containsKey("Request By"))
                    requestBy = filterAutoCompleteId.get("Request By");
                if (filterValue.containsKey("Assign To"))
                    assignTo = filterAutoCompleteId.get("Assign To");
                if (filterValue.containsKey("Project Name"))
                    projectName = project.get(Integer.parseInt(filterValue.get("Project Name")) - 1).get("id");
                if (filterValue.containsKey("Equipment Name"))
                    equipName = EquipmentName.get(Integer.parseInt(filterValue.get("Equipment Name")) - 1).get("id");
                if (filterValue.containsKey("Request Type"))
                    requestType = RequestType.get(Integer.parseInt(filterValue.get("Request Type")) - 1).get("id");
                if (filterValue.containsKey("Priority"))
                    priority = Priority.get(Integer.parseInt(filterValue.get("Priority")) - 1).get("id");
                if (filterValue.containsKey("Status"))
                    status = Status.get(Integer.parseInt(filterValue.get("Status")) - 1).get("id");
                if (filterValue.containsKey("Approval Status"))
                    approvalStatus = ApprovalStatus.get(Integer.parseInt(filterValue.get("Approval Status")) - 1).get("id");
                if (filterValue.containsKey("Clock Sheet Status"))
                    clockSheetStatus = ApprovalStatus.get(Integer.parseInt(filterValue.get("Clock Sheet Status")) - 1).get("id");

                if(projectName.equalsIgnoreCase("")){
                    setToast("Select The Project Name");
                }else if(equipRequiredDateFrom.equalsIgnoreCase("")){
                    setToast("Select The Equip Required Date From");
                }else if(equipRequiredDateTo.equalsIgnoreCase("")){
                    setToast("Select The Equip Required Date To");
                }else {
                    String listLoadValue = "{'Action':'" + listActionName + "','submode':'EQUIPMENT_LIST','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','frmnameCTRL':'"+frmnameCTRL+"','" + frmnameCTRL + "_ProjectName':'"+projectName+"','" + frmnameCTRL + "_EquipmentName':'"+equipName+"','" + frmnameCTRL + "_RequestType':'"+requestType+"','" + frmnameCTRL + "_RequestFromDate':'"+requestDateFrom+"','" + frmnameCTRL + "_RequestToDate':'"+requestDateTo+"','" + frmnameCTRL + "_EquipRequiredDateFrom':'"+equipRequiredDateFrom+"','" + frmnameCTRL + "_EquipRequiredDateTo':'"+equipRequiredDateTo+"','" + frmnameCTRL + "_RequestBy':'"+requestBy+"','" + frmnameCTRL + "_AssignTo':'" + assignTo + "','" + frmnameCTRL + "_Priority':'" + priority + "','" + frmnameCTRL + "_Status':'" + status + "','" + frmnameCTRL + "_ApprovalStatus':'" + approvalStatus + "','" + frmnameCTRL + "_ClocksheetStatus':'" + clockSheetStatus + "'}";
                    Bundle bundle = new Bundle();
                    bundle.putString("load", listLoadValue);
                    ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>EQUIPMENT</font>"));
                    NavigationFragmentManager(EquipListFragment.newInstance(bundle), "Equip");

                }

            }
        });

        return view;
    }



    public void dynamicLayout(String type) {
        mrSelectedFilter.removeAllViews();
        mrSelectedFilter.addView(addEditView(type));
    }
    public LinearLayout addEditView(String type) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        //layoutParams.setMargins(0, 20, 0, 0);
        linearLayout.setPadding(5, 5, 5, 5);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        if (type.equalsIgnoreCase("Spinner")) {
            if (filterName.equalsIgnoreCase("Project Name")) {
                linearLayout.addView(addSpinnerToTableRow(project, "Select Project", 3), 0);
            }else if (filterName.equalsIgnoreCase("Equipment Name")) {
                linearLayout.addView(addSpinnerToTableRow(EquipmentName, "Select Equipment Name", 3), 0);
            } else if (filterName.equalsIgnoreCase("Request Type")) {
                linearLayout.addView(addSpinnerToTableRow(RequestType, "Select Request Type", 3), 0);
            }else if (filterName.equalsIgnoreCase("Priority")) {
                linearLayout.addView(addSpinnerToTableRow(Priority, "Select Priority", 3), 0);
            }else if (filterName.equalsIgnoreCase("Status")) {
                linearLayout.addView(addSpinnerToTableRow(Status, "Select Status", 3), 0);
            }else if (filterName.equalsIgnoreCase("Approval Status")) {
                linearLayout.addView(addSpinnerToTableRow(ApprovalStatus, "Select Approval Status", 3), 0);
            }else if (filterName.equalsIgnoreCase("Clock Sheet Status")) {
                linearLayout.addView(addSpinnerToTableRow(ClockSheetStatus, "Select Clock Status", 3), 0);
            }

        }else if (type.equalsIgnoreCase("AutoCompleteTextView")) {
            FilterAutocomplete = autocomplete("", null, 3);
            linearLayout.addView(FilterAutocomplete, 0);
            if (filterName.equalsIgnoreCase("Request By")) {
                AutoCompleteTextviewAdapter requestByAdapter = new AutoCompleteTextviewAdapter(context, RequestBy);
                FilterAutocomplete.setAdapter(requestByAdapter);
            } else if (filterName.equalsIgnoreCase("Assign To")) {
                AutoCompleteTextviewAdapter AssignAdapter = new AutoCompleteTextviewAdapter(context, AssignTo);
                FilterAutocomplete.setAdapter(AssignAdapter);
            }

            if (filterValue.containsKey(filterName)) {
                FilterAutocomplete.setText(filterValue.get(filterName));
            }
            FilterAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                                      {
                                                          public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                                                              HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
                                                              filterValue.put(filterName, map.get("value"));
                                                              filterAutoCompleteId.put(filterName, map.get("id"));
                                                              FilterAutocomplete.setText(map.get("value"));
                                                              //TODO Do something with the selected text
                                                          }
                                                      }
            );
        } else if (type.equalsIgnoreCase("DatePicker")) {
            FilterDate = addEditTextDate("", null, 3);
            System.out.println("Date format" + FilterDate);
            linearLayout.addView(FilterDate, 0);
            if (filterValue.containsKey(filterName)) {
                FilterDate.setText(filterValue.get(filterName));
                System.out.println("Date format" + filterValue.get(filterName));
            }
        }
        return linearLayout;
    }


    public AutoCompleteTextView autocomplete(String value, Typeface typeface, int layoutWeight) {
        AutoCompleteTextView editText = new AutoCompleteTextView(context);
        editText.setBackgroundResource(R.drawable.scm_edit_text_bg);
        editText.setTextColor(getResources().getColor(R.color.result_color));
        if (typeface != null)
            editText.setTypeface(typeface);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        if(context.getResources().getBoolean(R.bool.isTablet)) {
            editText.setPadding(5, 5, 5, 5);
        } else {
            editText.setPadding(2, 2, 2, 2);
        }
        editText.setText(value);
        editText.setHint(filterName);
        editText.setThreshold(0);
        // editText.setLayoutParams(new TableRow.LayoutParams(0, 100, layoutWeight));
        return editText;
    }

    public SCMEditText addEditTextDate(String value, Typeface typeface, int layoutWeight) {
        final SCMEditText editText = new SCMEditText(context);
        editText.setBackgroundResource(R.drawable.scm_edit_text_bg);
        editText.setTextColor(getResources().getColor(R.color.result_color));
        if (typeface != null)
            editText.setTypeface(typeface);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        if(context.getResources().getBoolean(R.bool.isTablet)) {
            editText.setPadding(5, 5, 5, 5);
        } else {
            editText.setPadding(2, 2, 2, 2);
        }
        editText.setText(value);
        editText.setFocusable(false);
        editText.setClickable(false);
        //  editText.setHint(filterName);
        editText.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight));
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                //editText.setText(new StringBuilder().append().append("/").append(month).append("/").append(year));
                filterValue.put(filterName, s.toString());

                if(filterValue.containsKey("Request Date To")&& filterValue.containsKey("Request Date From")) {
                    Date convertedDateFrom = new Date();
                    Date convertedDateTo = new Date();
                    String format = "MM/dd/yyyy";
                    SimpleDateFormat formater = new SimpleDateFormat(format);
                    String fromDate = filterValue.get("Request Date From");
                    String toDate = filterValue.get("Request Date To");
                    if(fromDate!=null) {
                        System.out.println("FILTERNAME" + filterName);
                        try {
                            convertedDateFrom = formater.parse(fromDate);
                            convertedDateTo = formater.parse(toDate);
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (convertedDateFrom.compareTo(convertedDateTo) > 0) {
                            if(filterName.equalsIgnoreCase("Request Date To")) {
                                setToast("To Date Should be greate than From Date");
                                filterValue.remove("Request Date To");
                                dynamicLayout(filterType);
                            }else {
                                setToast("From Date Can't be greater than To Date");
                                filterValue.remove("Request Date From");
                                dynamicLayout(filterType);
                            }
                        } /*else {
                            filterValue.put(filterName, s.toString());

                        }*/
                    }
                }/*else {
                    filterValue.put(filterName, s.toString());
                }*/
                if(filterValue.containsKey("Equip Required Date To")&& filterValue.containsKey("Equip Required Date From")) {
                    Date convertedDateFrom = new Date();
                    Date convertedDateTo = new Date();
                    String format = "MM/dd/yyyy";
                    SimpleDateFormat formater = new SimpleDateFormat(format);
                    String fromDate = filterValue.get("Equip Required Date From");
                    String toDate = filterValue.get("Equip Required Date To");
                    if(fromDate!=null) {
                        System.out.println("FILTERNAME" + filterName);
                        try {
                            convertedDateFrom = formater.parse(fromDate);
                            convertedDateTo = formater.parse(toDate);
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (convertedDateFrom.compareTo(convertedDateTo) > 0) {
                            if(filterName.equalsIgnoreCase("Equip Required Date To")) {
                                setToast("To Date Should be greate than From Date");
                                filterValue.remove("Equip Required Date To");
                                dynamicLayout(filterType);
                            }else {
                                setToast("From Date Can't be greater than To Date");
                                filterValue.remove("Equip Required Date From");
                                dynamicLayout(filterType);
                            }
                        } /*else {
                            filterValue.put(filterName, s.toString());

                        }*/
                    }
                }/*else {
                    filterValue.put(filterName, s.toString());
                }*/


            }
        });
        return editText;
    }

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }
        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            /*Time chosenDate = new Time();
            chosenDate.set(dd, mm, yy);*/
            populateSetDate(yy, mm + 1, dd);
        }
        public void populateSetDate(int year, int month, int day) {
            FilterDate.setText(day + "/" + month + "/" + year);
        }
    }
    public Spinner addSpinnerToTableRow(ArrayList<HashMap<String, String>> valueList, String hintText, int layoutWeight) {
        Spinner spinner = new Spinner(context);
        spinner.setBackgroundResource(R.drawable.scm_dropdown_bg);
        // TableRow.LayoutParams spinnerLayoutParams = new TableRow.LayoutParams(0, 100, layoutWeight);
        // spinner.setLayoutParams(spinnerLayoutParams);
        loadSpinner(spinner, valueList, new String[]{"value"}, hintText);
        if (filterValue.containsKey(filterName)) {
            spinner.setSelection(Integer.parseInt(filterValue.get(filterName)));
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos > 0)
                    filterValue.put(filterName, "" + pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return spinner;
    }

    public void load(final String action, final String submode, String pjtId, String stgeId) {
        this.Submode = submode;
        this.projectId = pjtId;
        this.stageId = stgeId;
        requestParameter = "{'Action':'" + action + "','submode':'" + submode + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "'}";
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
    private void parseJSONResponse(JSONObject response, String flag) {

        try {

            if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_1)) {
                if (flag.equalsIgnoreCase("META_DATA")) {
                    JSONArray EquipmentNameArray = response.getJSONArray("equipmentName");
                    EquipmentName = ApiCalls.getArraylistfromJson(EquipmentNameArray.toString());

                    JSONArray RequestTypeArray = response.getJSONArray("requestType");
                    RequestType = ApiCalls.getArraylistfromJson(RequestTypeArray.toString());

                    JSONArray PriorityArray = response.getJSONArray("priority");
                    Priority = ApiCalls.getArraylistfromJson(PriorityArray.toString());

                    JSONArray StatusArray = response.getJSONArray("status");
                    Status = ApiCalls.getArraylistfromJson(StatusArray.toString());

                    JSONArray ApprovalStatusArray = response.getJSONArray("approvalStatus");
                    ApprovalStatus = ApiCalls.getArraylistfromJson(ApprovalStatusArray.toString());

                    JSONArray ClockSheetStatusArray = response.getJSONArray("clockSheetStatus");
                    ClockSheetStatus = ApiCalls.getArraylistfromJson(ClockSheetStatusArray.toString());


                    FilterListAdapter listadapter = new FilterListAdapter(context, FilterList);
                    mrFilterMenu.setAdapter(listadapter);
                }

            } else if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_403)) {
                showSessionScreensDialog(context, response.getString(AppContants.RESPONSE_MESSAGE));
            } else {
                setToast(response.getString(AppContants.RESPONSE_MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class FilterListAdapter extends BaseAdapter {
        private String TAG = "FilterListAdapter";
        Context context;
        ArrayList<HashMap<String, String>> filterList;
        public FilterListAdapter(Context context, ArrayList<HashMap<String, String>> filterList) {
            this.context = context;
            this.filterList = filterList;
        }
        @Override
        public int getCount() {
            return filterList.size();
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
            View v = convertView;
            final FilterViewHolder holder;
            if (v == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = mInflater.inflate(R.layout.mr_filter_list, null);
                holder = new FilterViewHolder(v);
                v.setTag(holder);
            } else {
                holder = (FilterViewHolder) v.getTag();
            }
            if (filterList.get(position).get("Name").equalsIgnoreCase("Project Name") && isView) {
                v.setBackgroundResource(R.color.white);
                holder.arrowView.setVisibility(View.VISIBLE);
                previous = v;
            } else {
                holder.arrowView.setVisibility(View.GONE);
                v.setBackgroundResource(0);
            }
        /*    if (filterTextvalue == 1) {
                v.setBackgroundResource(R.color.orange_bg);
            }*/
            holder.listItemName.setText(filterList.get(position).get("Name"));
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterName = filterList.get(position).get("Name");
                    filterType = filterList.get(position).get("Type");
                    dynamicLayout(filterType);
                    isView = false;
                    previous.setBackgroundResource(0);
                    ImageView arrow = (ImageView) previous.findViewById(R.id.mr_Filter_list_right_arrow);
                    arrow.setVisibility(View.GONE);
                    v.setBackgroundResource(R.color.white);
                    holder.arrowView.setVisibility(View.VISIBLE);
                    v.setBackgroundResource(R.color.white);
                    previous = v;
                }
            });
            return v;
        }
    }
    class FilterViewHolder {
        @Bind(R.id.name)
        SCMTextView listItemName;
        @Bind(R.id.mr_Filter_list_right_arrow)
        ImageView arrowView;
        public FilterViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }




}
