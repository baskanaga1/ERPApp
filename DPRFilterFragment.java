package com.guruinfo.scm.DPR;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import com.guruinfo.scm.AdmEmpMaster;
import com.guruinfo.scm.AdmEmpMasterDao;
import com.guruinfo.scm.Contractors;
import com.guruinfo.scm.ContractorsDao;
import com.guruinfo.scm.DaoMaster;
import com.guruinfo.scm.MaterialsMaster;
import com.guruinfo.scm.MaterialsMasterDao;
import com.guruinfo.scm.PjtStore;
import com.guruinfo.scm.PjtStoreDao;
import com.guruinfo.scm.ProjUserMaterialListDao;
import com.guruinfo.scm.R;
import com.guruinfo.scm.RequestedBy;
import com.guruinfo.scm.RequestedByDao;
import com.guruinfo.scm.SCMDashboardActivityLatest;
import com.guruinfo.scm.Status;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import static com.guruinfo.scm.SCMDashboardActivityLatest.NavigationFragmentManager;
import static com.guruinfo.scm.common.utils.BaseAppCompactFragmentActivity.loadSpinner;
/**
 * Created by Kannan G on 28/10/2017.
 */
public class DPRFilterFragment extends BaseFragment {
    String TAG = "DPRFilterFragment";
    @Bind(R.id.apply_filter)
    LinearLayout applyLinearLayout;
    @Bind(R.id.reset_filter)
    LinearLayout resetLinearLayout;
    @Bind(R.id.mrFilterMenu)
    ListView mrFilterMenu;
    @Bind(R.id.mrSelectedFilter)
    LinearLayout mrSelectedFilter;
    ArrayList<HashMap<String, String>> FilterList = new ArrayList<>();
    HashMap<String, String> filterValue = new HashMap<>();
    HashMap<String, String> filterAutoCompleteId = new HashMap<>();
    EditText FilterEditText;
    AutoCompleteTextView FilterAutocomplete;
    public static EditText FilterDate;
    public static EditText FilterTree;
    public static String stageId = "";
    String filterName = "";
    String filterType = "";
    JSONArray projectStoreJsonArray;
    JSONArray projectContractorJsonArray;
    JSONArray projectRequestedByJsonArray;
    JSONArray projectNameJsonArray;
    JSONArray statusJsonArray;
    JSONArray materialJsonArray;
    JSONArray iowJsonArray;
    JSONArray closeRequestedByJsonArray;
    JSONArray closeStatusJsonArray;
    ArrayList<HashMap<String, String>> projectStore = new ArrayList<>();
    ArrayList<HashMap<String, String>> projectContractor = new ArrayList<>();
    ArrayList<HashMap<String, String>> projectRequestedBy = new ArrayList<>();
    ArrayList<HashMap<String, String>> statusArray = new ArrayList<>();
    ArrayList<HashMap<String, String>> materialArray = new ArrayList<>();
    ArrayList<HashMap<String, String>> iowArray = new ArrayList<>();
    ArrayList<HashMap<String, String>> projectName = new ArrayList<>();
    ArrayList<HashMap<String, String>> closeRequestedByList = new ArrayList<>();
    ArrayList<HashMap<String, String>> closeStatusList = new ArrayList<>();
    View previous;
    boolean isView;
    ProgressDialog mDialog;
    SessionManager session;
    String uid, Cre_Id;
    String requestParameter;
    BackgroundTask backgroundTask;
    String Submode;
    String listActionName;
    String listSubmodeName;
    String projectId = "";
    StatusDao statusDao;
    ContractorsDao contractorsDao;
    RequestedByDao requestedByDao;
    int filterTextvalue = 0;
    public static DPRFilterFragment newInstance(Bundle bundle) {
        DPRFilterFragment fragment = new DPRFilterFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        inflater.inflate(R.menu.list_action, menu);
        menu.findItem(R.id.new_request).setVisible(false);
        menu.findItem(R.id.favourite).setVisible(false);
        menu.findItem(R.id.filter).setVisible(false);
        menu.findItem(R.id.list).setVisible(false);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.approval_count).setVisible(false);
        menu.findItem(R.id.search).setVisible(false);
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
            ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>" + bundle.getString("SEARCH_TYPE") + "</font>"));
        }
        statusDao = daoSession.getStatusDao();
        contractorsDao = daoSession.getContractorsDao();
        requestedByDao = daoSession.getRequestedByDao();
        List<Contractors> contractorsLists = contractorsDao.queryBuilder().where(ContractorsDao.Properties.Status.eq("approved"), ContractorsDao.Properties.Display.eq("active")).list();
        Log.d(TAG, "contractorsLists Size" + contractorsLists.size());
        for (int i = 0; i < contractorsLists.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("id", contractorsLists.get(i).getLoad_id());
            hashMap.put("value", contractorsLists.get(i).getValue());
            projectContractor.add(hashMap);
        }
        PjtStoreDao pjtStoreDao = daoSession.getPjtStoreDao();
        List<PjtStore> projectLists = pjtStoreDao.queryBuilder().where(PjtStoreDao.Properties.User_id.eq(uid)).list();
        if (projectLists.size() > 0)
            for (int i = 0; i < projectLists.size(); i++) {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", projectLists.get(i).getLoad_id());
                map.put("value", projectLists.get(i).getValue());
                projectName.add(map);
            }
        List<Status> statusLists = statusDao.queryBuilder().where(StatusDao.Properties.Status.eq("approved")).list();
        Log.d(TAG, "statusLists Size" + statusLists.size());
        for (int i = 0; i < statusLists.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("id", statusLists.get(i).getLoad_id());
            hashMap.put("value", statusLists.get(i).getValue());
            statusArray.add(hashMap);
        }
        HashMap<String, String> listNameType = new HashMap<>();
        listNameType.put("Name", "Project Name");
        listNameType.put("Type", "Spinner");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "DPR ID");
        listNameType.put("Type", "EditText");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "DPR Reference No");
        listNameType.put("Type", "EditText");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "DPR From Date");
        listNameType.put("Type", "DatePicker");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "DPR To Date");
        listNameType.put("Type", "DatePicker");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "Contractor");
        listNameType.put("Type", "AutoCompleteTextView");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "Stage Name");
        listNameType.put("Type", "Tree");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "IOW");
        listNameType.put("Type", "AutoCompleteTextView");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "Status");
        listNameType.put("Type", "Spinner");
        FilterList.add(new HashMap<String, String>(listNameType));
        listNameType.put("Name", "WO ID");
        listNameType.put("Type", "EditText");
        FilterList.add(new HashMap<String, String>(listNameType));
        // All Search Filter
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mr_filter_main, container, false);
        ButterKnife.bind(this, view);
        isView = true;
        FilterListAdapter listadapter = new FilterListAdapter(context, FilterList);
        mrFilterMenu.setAdapter(listadapter);
        filterName = "Project Name";
        stageId = "";
        dynamicLayout("Spinner");
        resetLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterValue.clear();
                stageId = "";
                projectId = "";
                dynamicLayout(filterType);
            }
        });
        applyLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dprid = "", dprRef = "", woId = "", contractorNameValue = "",  dprFromDate = "",
                        dprToDate = "", statusValue = "", iowNameValue = "";
                if (filterValue.containsKey("DPR ID"))
                    dprid = filterValue.get("DPR ID");
                if (filterValue.containsKey("DPR Reference No"))
                    dprRef = filterValue.get("DPR Reference No");
                if (filterValue.containsKey("WO ID"))
                    woId = filterValue.get("WO ID");
                if (filterAutoCompleteId.containsKey("Contractor"))
                    contractorNameValue = filterAutoCompleteId.get("Contractor");
                if (filterValue.containsKey("DPR From Date"))
                    dprFromDate = filterValue.get("DPR From Date");
                if (filterValue.containsKey("DPR To Date"))
                    dprToDate = filterValue.get("DPR To Date");
                if (filterValue.containsKey("Status"))
                    statusValue = statusArray.get(Integer.parseInt(filterValue.get("Status")) - 1).get("id");
                if (filterAutoCompleteId.containsKey("IOW"))
                    iowNameValue = filterAutoCompleteId.get("IOW");
                if (filterValue.containsKey("Project Name"))
                    projectId = projectName.get(Integer.parseInt(filterValue.get("Project Name")) - 1).get("id");
                if (!projectId.equalsIgnoreCase("")) {
                    String listLoadValue = "{'Action':'" + listActionName + "','submode':'" + listSubmodeName + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','Mat_Req_search':'" + 1 + "','Mat_Req_MBook_ProjName':'" + projectId + "','Mat_Req_Mbook_Id':'" + dprid + "','Mat_Req_MBook_RefNo':'" + dprRef + "','Mat_Req_MBook_Contractor':'" + contractorNameValue + "','Mat_Req_MBook_FromDate':'" + dprFromDate + "','Mat_Req_MBook_ToDate':'" + dprToDate + "','Mat_Req_MBook_status':'" + statusValue + "','Mat_Req_Mat_Req_MIR_StageName':'" + stageId + "','Mat_Req_Mat_Req_MIR_IOW':'" + iowNameValue + "','Mat_Req_wo_Id':'" + woId + "','Mat_Req_BlockId':'','FrmNameCtl':'Mat_Req','page':' 1 '}";
                    Bundle bundle = new Bundle();
                    bundle.putString("load", listLoadValue);
                    if (listSubmodeName.equalsIgnoreCase("DPR_APPROVAL_SEARCH")) {
                        bundle.putString("IsApproval", "true");
                        ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>DPR APPROVAL</font>"));
                    } else
                        ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>DPR</font>"));
                    NavigationFragmentManager(DPRListFragment.newInstance(bundle), "DPR");
                } else
                    setToast("Select The Project Name");
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
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            linearLayout.setPadding(10, 10, 10, 10);
        } else {
            linearLayout.setPadding(5, 5, 5, 5);
        }
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        if (type.equalsIgnoreCase("EditText")) {
            FilterEditText = addEditText("", null, 2);
            linearLayout.addView(FilterEditText, 0);
            if (filterValue.containsKey(filterName)) {
                FilterEditText.setText(filterValue.get(filterName));
            }
            FilterEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    filterValue.put(filterName, s.toString());
                    filterTextvalue = 1;
                    Log.e("Filtervalue", Integer.toString(filterTextvalue));
                }
            });
        } else if (type.equalsIgnoreCase("Spinner")) {
            if (filterName.equalsIgnoreCase("Project Name")) {
                linearLayout.addView(addSpinnerToTableRow(projectName, "Select Project", 3), 0);
            } else if (filterName.equalsIgnoreCase("Status")) {
                linearLayout.addView(addSpinnerToTableRow(statusArray, "Select Status", 3), 0);
            } else if (filterName.equalsIgnoreCase("Store Name")) {
                linearLayout.addView(addSpinnerToTableRow(projectStore, "Select Store", 3), 0);
            }
        } else if (type.equalsIgnoreCase("AutoCompleteTextView")) {
            FilterAutocomplete = autocomplete("", null, 3);
            linearLayout.addView(FilterAutocomplete, 0);
            /*if (filterName.equalsIgnoreCase("Store Name")) {
                AutoCompleteTextviewAdapter storeNameAdapter = new AutoCompleteTextviewAdapter(context, projectStore);
                FilterAutocomplete.setAdapter(storeNameAdapter);
            } else*/
            if (filterName.equalsIgnoreCase("Contractor")) {
                AutoCompleteTextviewAdapter contractorAdapter = new AutoCompleteTextviewAdapter(context, projectContractor);
                FilterAutocomplete.setAdapter(contractorAdapter);
            } else if (filterName.equalsIgnoreCase("Material")) {
                AutoCompleteTextviewAdapter materialAdapter = new AutoCompleteTextviewAdapter(context, materialArray);
                FilterAutocomplete.setAdapter(materialAdapter);
            } else if (filterName.equalsIgnoreCase("Requested By")) {
                AutoCompleteTextviewAdapter requestedByAdapter = new AutoCompleteTextviewAdapter(context, projectRequestedBy);
                FilterAutocomplete.setAdapter(requestedByAdapter);
            } else if (filterName.equalsIgnoreCase("IOW")) {
                AutoCompleteTextviewAdapter iowAdapter = new AutoCompleteTextviewAdapter(context, iowArray);
                FilterAutocomplete.setAdapter(iowAdapter);
            }
            if (filterValue.containsKey(filterName)) {
                FilterAutocomplete.setText(filterValue.get(filterName));
            }
            FilterAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            linearLayout.addView(FilterDate, 0);
            if (filterValue.containsKey(filterName)) {
                FilterDate.setText(filterValue.get(filterName));
            }
        } else if (type.equalsIgnoreCase("Tree")) {
            FilterTree = addEditTextTree("", null, 3);
            if (filterValue.containsKey("Project Name")) {
                linearLayout.addView(FilterTree, 0);
            } else {
                linearLayout.addView(addTextViewToTableRow("First Select The Project", null, 3), 0);
                //  linearLayout.addView(FilterTree, 1);
            }
            if (filterValue.containsKey(filterName)) {
                FilterTree.setText(filterValue.get(filterName));
            }
            FilterTree.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    filterValue.put(filterName, s.toString());
                    load("MRALL_PROCESS", "MRALL_IOWCLICK_SELECT", projectId, stageId);
                }
            });
            FilterTree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    projectId = projectName.get(Integer.parseInt(filterValue.get("Project Name")) - 1).get("id");
                    Bundle bundle = new Bundle();
                    bundle.putString("stage", "");
                    bundle.putString("Display", "STAGE");
                    bundle.putString("userId", uid);
                    bundle.putString("pjtId", projectId);
                    Intent intent = new Intent(context, StageTreeDialogFragment.class);
                    intent.putExtra("datas", bundle);
                    startActivity(intent);
                }
            });
        }
        return linearLayout;
    }
    public SCMEditText addEditText(String value, Typeface typeface, int layoutWeight) {
        SCMEditText editText = new SCMEditText(context);
        editText.setBackgroundResource(R.drawable.scm_edit_text_bg);
        editText.setTextColor(getResources().getColor(R.color.result_color));
        if (typeface != null)
            editText.setTypeface(typeface);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            editText.setPadding(5, 5, 5, 5);
        } else {
            editText.setPadding(2, 2, 2, 2);
        }
        editText.setText(value);
        editText.setHint(filterName);
        //   editText.setLayoutParams(new TableRow.LayoutParams(0, 100, layoutWeight));
        return editText;
    }
    public SCMEditText addEditTextDate(String value, Typeface typeface, int layoutWeight) {
        final SCMEditText editText = new SCMEditText(context);
        editText.setBackgroundResource(R.drawable.scm_date_picker_bg);
        editText.setTextColor(getResources().getColor(R.color.result_color));
        if (typeface != null)
            editText.setTypeface(typeface);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            editText.setPadding(5, 5, 5, 5);
        } else {
            editText.setPadding(2, 2, 2, 2);
        }
        editText.setText(value);
        editText.setFocusable(false);
        editText.setClickable(false);
        editText.setHint(filterName);
        editText.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight));
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");*/
                Log.d("Date Picker", "Running");
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                Log.d("Date Picker", "Before Listener");
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, intimateOnDatePickerListener, yy, mm, dd);
                int date, month, year;
                if (editText.getHint().toString().equalsIgnoreCase("DPR From Date")) {
                    if (!(editText.getText().toString().equalsIgnoreCase(""))) {
                        String[] datevalue = editText.getText().toString().split("/");
                        datePickerDialog = new DatePickerDialog(context, intimateOnDatePickerListener, Integer.parseInt(datevalue[2]), (Integer.parseInt(datevalue[1]) - 1), Integer.parseInt(datevalue[0]));
                        if (editText.getHint().toString().equalsIgnoreCase("DPR From Date")) {
                            if (filterValue.containsKey("DPR To Date")) {
                                String woToDate = filterValue.get("DPR To Date");
                                String[] woToplit = woToDate.split("/");
                                date = Integer.parseInt(woToplit[0]);
                                month = (Integer.parseInt(woToplit[1]) - 1);
                                year = Integer.parseInt(woToplit[2]);
                                calendar.set(year, month, date);//Year,Mounth -1,Day
                                if (dd == date && mm == month && yy == year) {
                                    datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                                } else {
                                    calendar.set(year, month, date);//Year,Mounth -1,Day
                                    datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                                }
                            }
                        }
                    } else {
                        datePickerDialog = new DatePickerDialog(context, intimateOnDatePickerListener, yy, mm, dd);
                        if (editText.getHint().toString().equalsIgnoreCase("DPR From Date")) {
                            if (filterValue.containsKey("DPR To Date")) {
                                String woToDate = filterValue.get("DPR To Date");
                                String[] woToplit = woToDate.split("/");
                                date = Integer.parseInt(woToplit[0]);
                                month = (Integer.parseInt(woToplit[1]) - 1);
                                year = Integer.parseInt(woToplit[2]);
                                calendar.set(year, month, date);//Year,Mounth -1,Day
                                if (dd == date && mm == month && yy == year) {
                                    datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                                } else {
                                    calendar.set(year, month, date);//Year,Mounth -1,Day
                                    datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                                }
                            }
                        }
                    }
                } else if (editText.getHint().toString().equalsIgnoreCase("DPR To Date")) {
                    if (!(editText.getText().toString().equalsIgnoreCase(""))) {
                        String[] datevalue = editText.getText().toString().split("/");
                        datePickerDialog = new DatePickerDialog(context, intimateOnDatePickerListener, Integer.parseInt(datevalue[2]), (Integer.parseInt(datevalue[1]) - 1), Integer.parseInt(datevalue[0]));
                        if (editText.getHint().toString().equalsIgnoreCase("DPR To Date")) {
                            if (filterValue.containsKey("DPR From Date")) {
                                String woToDate = filterValue.get("DPR From Date");
                                String[] woToplit = woToDate.split("/");
                                date = Integer.parseInt(woToplit[0]);
                                month = (Integer.parseInt(woToplit[1]) - 1);
                                year = Integer.parseInt(woToplit[2]);
                                calendar.set(year, month, date);//Year,Mounth -1,Day
                                if (dd == date && mm == month && yy == year) {
                                    datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                                } else {
                                    calendar.set(year, month, date);//Year,Mounth -1,Day
                                    datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                                }
                            }
                        }
                    } else {
                        datePickerDialog = new DatePickerDialog(context, intimateOnDatePickerListener, yy, mm, dd);
                        if (editText.getHint().toString().equalsIgnoreCase("DPR To Date")) {
                            if (filterValue.containsKey("DPR From Date")) {
                                String woToDate = filterValue.get("DPR From Date");
                                String[] woToplit = woToDate.split("/");
                                date = Integer.parseInt(woToplit[0]);
                                month = (Integer.parseInt(woToplit[1]) - 1);
                                year = Integer.parseInt(woToplit[2]);
                                calendar.set(year, month, date);//Year,Mounth -1,Day
                                if (dd == date && mm == month && yy == year) {
                                    datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                                } else {
                                    calendar.set(year, month, date);//Year,Mounth -1,Day
                                    datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                                }
                            }
                        }
                    }
                }
                datePickerDialog.show();
            }
        });
        return editText;
    }
    public DatePickerDialog.OnDateSetListener intimateOnDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            Log.d("Date Picker", "After Listener");
            populateSetDate(yy, mm + 1, dd);
        }
        public void populateSetDate(int year, int month, int day) {
            NumberFormat formatter = new DecimalFormat("00");
            FilterDate.setText( formatter.format(day) + "/" + formatter.format(month) + "/" + formatter.format(year));
            filterValue.put(FilterDate.getHint().toString(), (formatter.format(day) + "/" + formatter.format(month) + "/" + formatter.format(year)));
        }
    };
    public SCMTextView addTextViewToTableRow(String value, Typeface typeface, int layoutWeight) {
        SCMTextView textView = new SCMTextView(context);
        textView.setTextColor(getResources().getColor(R.color.result_color));
        if (typeface != null)
            textView.setTypeface(typeface);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        textView.setGravity(Gravity.CENTER);
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            textView.setPadding(10, 10, 10, 10);
        } else {
            textView.setPadding(5, 5, 5, 5);
        }
        textView.setText(value);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight));
        return textView;
    }
    public SCMEditText addEditTextTree(String value, Typeface typeface, int layoutWeight) {
        SCMEditText editText = new SCMEditText(context);
        editText.setBackgroundResource(R.drawable.scm_edit_text_bg);
        editText.setTextColor(getResources().getColor(R.color.result_color));
        if (typeface != null)
            editText.setTypeface(typeface);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            editText.setPadding(5, 5, 5, 5);
        } else {
            editText.setPadding(2, 2, 2, 2);
        }
        editText.setText(value);
        editText.setFocusable(false);
        editText.setClickable(false);
        editText.setHint(filterName);
        //  editText.setLayoutParams(new TableRow.LayoutParams(0, 100, layoutWeight));
        return editText;
    }
    public AutoCompleteTextView autocomplete(String value, Typeface typeface, int layoutWeight) {
        AutoCompleteTextView editText = new AutoCompleteTextView(context);
        editText.setBackgroundResource(R.drawable.scm_edit_text_bg);
        editText.setTextColor(getResources().getColor(R.color.result_color));
        if (typeface != null)
            editText.setTypeface(typeface);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        if (context.getResources().getBoolean(R.bool.isTablet)) {
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
    public Spinner addSpinnerToTableRow(ArrayList<HashMap<String, String>> valueList, final String hintText, int layoutWeight) {
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
                if (pos > 0) {
                    filterValue.put(filterName, "" + pos);
                    if (!(projectId.equalsIgnoreCase(projectName.get(Integer.parseInt(filterValue.get("Project Name")) - 1).get("id"))))
                        if (hintText.equalsIgnoreCase("Select Project")) {
                            projectId = projectName.get(Integer.parseInt(filterValue.get("Project Name")) - 1).get("id");
                            PjtStoreDao pjtStoreDao = daoSession.getPjtStoreDao();
                            List<PjtStore> projectLists = pjtStoreDao.queryBuilder().where(PjtStoreDao.Properties.User_id.eq(uid)).list();
                            if (projectLists.size() > 0)
                                for (int i = 0; i < projectLists.size(); i++) {
                                    if (projectLists.get(i).getLoad_id().equalsIgnoreCase(projectId)) {
                                        try {
                                            JSONArray jsonArray = new JSONArray(projectLists.get(i).getStores().toString());
                                            projectStore = ApiCalls.getArraylistfromJson(jsonArray.toString());
                                            if (filterValue.containsKey("Store Name")) {
                                                filterValue.remove("Store Name");
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return spinner;
    }
    //Load and Search
    public void load(final String action, final String submode, String pjtId, String stgeId) {
        this.Submode = submode;
        this.projectId = pjtId;
        this.stageId = stgeId;
        requestParameter = "{'Action':'" + action + "','submode':'" + submode + "','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','proj_id':'" + projectId + "','stageId':'" + stageId + "'}";
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
                } catch (Exception e) {
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
                if (flag.equalsIgnoreCase("MRALL_IOWCLICK_SELECT")) {
                    iowJsonArray = response.getJSONArray("IOWSlectValues");
                    iowArray = ApiCalls.getArraylistfromJson(iowJsonArray.toString());
                }
            } else if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_403)) {
                showSessionScreensDialog(context, response.getString(AppContants.RESPONSE_MESSAGE));
            } else {
                setToast(response.getString(AppContants.RESPONSE_MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
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
            if (position == 0 && isView) {
                v.setBackgroundResource(R.color.white);
                holder.arrowView.setVisibility(View.VISIBLE);
                previous = v;
            } else {
                holder.arrowView.setVisibility(View.GONE);
                v.setBackgroundResource(0);
            }
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
