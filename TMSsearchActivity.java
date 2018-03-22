package com.guruinfo.scm.tms;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseFragment;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.ui.SCMTextView;
import com.guruinfo.scm.common.utils.ApiCalls;
import com.guruinfo.scm.library.FilteredArrayAdapter;
import com.guruinfo.scm.library.TokenCompleteTextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import butterknife.Bind;
import butterknife.ButterKnife;
import static com.guruinfo.scm.common.BaseFragmentActivity.loadLeadStyleSpinner;
import static com.guruinfo.scm.common.BaseFragmentActivity.setToast;
/**
 * Created by Kannan G on 9/1/2016.
 */
public class TMSsearchActivity extends BaseFragment implements TokenCompleteTextView.TokenListener<lists> {
    String TAG = "TMSsearchActivity";
    Context context;
    SessionManager session;
    BackgroundTask backgroundTask;
    String uid, Cre_Id, requestParameter;
    boolean isView;
    int colorPosition = 0;
    @Bind(R.id.FilterMenu)
    ListView FilterMenu;
    @Bind(R.id.SelectedFilter)
    LinearLayout SelectedFilter;
    @Bind(R.id.apply_filter)
    LinearLayout applyFilter;
    @Bind(R.id.reset_filter)
    LinearLayout resetFilter;
    ContactsCompletionView MultiSearchView;
    String hintTextValue;
    ArrayList<HashMap<String, String>> colors = new ArrayList<>();
    ArrayList<HashMap<String, String>> FilterList = new ArrayList<>();
    HashMap<String, String> filterAutoCompleteId = new HashMap<>();
    HashMap<String, String> filterValue = new HashMap<>();
    EditText FilterEditText;
    AutoCompleteTextView FilterAutocomplete;
    public static EditText FilterDate;
    String filterName = "";
    String filterType = "";
    int pos = -1;
    JSONArray valuesArrayList = new JSONArray();
    static EditText editDateTextView, editTimeText;
    CheckBox checkBox;
    Dialog dialog;
    String listType;
    ArrayAdapter<lists> multiSelectAdapter;
    lists[] people;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tms_searchview, container, false);
        ButterKnife.bind(this, view);
        isView = true;
        TMSFragmentActivity.actionBarNewRequestBtn.setVisibility(View.GONE);
        TMSFragmentActivity.actionBarFilterBtn.setVisibility(View.GONE);
        TMSFragmentActivity.actionBarBookMarkBtn.setVisibility(View.GONE);
       // TMSFragmentActivity.homeIcon.setVisibility(View.GONE);
       // TMSFragmentActivity.dashBoard.setVisibility(View.GONE);
        TMSFragmentActivity.actionBarTitle.setText("Task Searching");
        listType = getArguments().getString("listType");
        requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_SEARCH','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'metaData':'true'}";
        System.out.println(requestParameter);
        getLoadData(requestParameter, "LOAD");
        applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                int errCount = 0;
                try {
                    JSONObject submitJson = new JSONObject();
                    for (int i = 0; i < FilterList.size(); i++) {
                        if (FilterList.get(i).get("IsMandatory").equalsIgnoreCase("true")) {
                            if (filterValue.containsKey(FilterList.get(i).get("Name"))) {
                                if (filterValue.get(FilterList.get(i).get("Name")).length() > 0) {
                                    submitJson.put("display_Name_" + FilterList.get(i).get("MasterId"), FilterList.get(i).get("Name").replace(" ", ""));
                                    submitJson.put(FilterList.get(i).get("FrmNameCtl") + "_" + FilterList.get(i).get("Name").replace(" ", ""), filterValue.get(FilterList.get(i).get("Name")));
                                } else
                                    errCount++;
                            } else {
                                errCount++;
                                submitJson.put("display_Name_" + FilterList.get(i).get("MasterId"), FilterList.get(i).get("Name").replace(" ", ""));
                                submitJson.put(FilterList.get(i).get("FrmNameCtl") + "_" + FilterList.get(i).get("Name").replace(" ", ""), "");
                            }
                        } else {
                            if (filterValue.containsKey(FilterList.get(i).get("Name"))) {
                                submitJson.put("display_Name_" + FilterList.get(i).get("MasterId"), FilterList.get(i).get("Name").replace(" ", ""));
                                submitJson.put(FilterList.get(i).get("FrmNameCtl") + "_" + FilterList.get(i).get("Name").replace(" ", ""), filterValue.get(FilterList.get(i).get("Name")));
                            } else {
                                submitJson.put("display_Name_" + FilterList.get(i).get("MasterId"), FilterList.get(i).get("Name").replace(" ", ""));
                                submitJson.put(FilterList.get(i).get("FrmNameCtl") + "_" + FilterList.get(i).get("Name").replace(" ", ""), "");
                            }
                        }
                        submitJson.put("data_Type_" + FilterList.get(i).get("MasterId"), FilterList.get(i).get("Type"));
                        if (FilterList.get(i).get("Type").equalsIgnoreCase("DROPDOWN_M"))
                            submitJson.put("data_Input_Type_" + FilterList.get(i).get("MasterId"), "multi");
                        else
                            submitJson.put("data_Input_Type_" + FilterList.get(i).get("MasterId"), "single");
                        submitJson.put("FrmNameCtl", FilterList.get(i).get("FrmNameCtl"));
                        submitJson.put(FilterList.get(i).get("FrmNameCtl") + "_" + (i + 1), FilterList.get(i).get("MasterId"));
                    }
                    submitJson.put("headerSize", valuesArrayList.length());
                    submitJson.put("search", "yes");
                    submitJson.put("Action", "TASK_MANAGEMENT_SYSTEM");
                    submitJson.put("submode", "TMS_SEARCH");
                    submitJson.put("metaData", "false");
                    submitJson.put("search", true);
                    submitJson.put("searchName", listType);
                    submitJson.put("Cre_Id", Cre_Id);
                    submitJson.put("UID", uid);
                    submitJson.put("page", 0);
                    if (errCount == 0) {
                        Log.d("Submit Value------", submitJson.toString());
                        requestParameter = submitJson.toString();
                        System.out.println(requestParameter);
                        getLoadData(requestParameter, "SEARCH");
                    } else
                        setToast("Fill The Mandatory Fields");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        resetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                filterValue.clear();
                filterName = FilterList.get(pos).get("Name");
                filterType = FilterList.get(pos).get("Type");
                for (int i = 0; i < FilterList.size(); i++) {
                    if (FilterList.get(i).get("Name").equalsIgnoreCase("Department")) {
                        FilterList.get(i).put("fieldSettings", "[]");
                    }
                    if (FilterList.get(i).get("Name").equalsIgnoreCase("Individual")) {
                        FilterList.get(i).put("fieldSettings", "[]");
                    }
                    if (FilterList.get(i).get("Name").equalsIgnoreCase("Wing")) {
                        FilterList.get(i).put("fieldSettings", "[]");
                    }
                    if (FilterList.get(i).get("Name").equalsIgnoreCase("Designation")) {
                        FilterList.get(i).put("fieldSettings", "[]");
                    }
                }
                dynamicLayout(filterType, FilterList.get(pos).get("fieldSettings"), filterName);
            }
        });
        return view;
    }
    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
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
                if (flag.equalsIgnoreCase("LOAD")) {
                    try {
                        //  JSONObject responseObject = new JSONObject(res);
                        valuesArrayList = response.getJSONArray("Values");
                        for (int i = 0; i < valuesArrayList.length(); i++) {
                            JSONObject objects = valuesArrayList.getJSONObject(i);
                            JSONObject LabelValues = objects.getJSONObject("LabelValues");
                            HashMap<String, String> listNameType = new HashMap<>();
                            listNameType.put("Name", LabelValues.getString("displayName"));
                            listNameType.put("Type", LabelValues.getString("DataType"));
                            listNameType.put("MasterId", LabelValues.getString("MasterId"));
                            listNameType.put("IsMandatory", LabelValues.getString("IsMandatory"));
                            listNameType.put("FrmNameCtl", LabelValues.getString("FrmNameCtl"));
                            listNameType.put("fieldSettings", LabelValues.getJSONObject("fieldSettings").getJSONObject("Options").getString("Options"));
                            FilterList.add(new HashMap<String, String>(listNameType));
                        }
                        FilterListAdapter listadapter = new FilterListAdapter(context, FilterList);
                        FilterMenu.setAdapter(listadapter);
                        if (FilterList.size() > 0) {
                            filterName = FilterList.get(0).get("Name");
                            filterType = FilterList.get(0).get("Type");
                            dynamicLayout(filterType, FilterList.get(0).get("fieldSettings"), filterName);
                        }
                        FilterMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                for (int j = 0; j < adapterView.getChildCount(); j++)
                                    adapterView.getChildAt(j).setBackgroundColor(0);
                                // change the background color of the selected element
                                colorPosition = i;
                                view.setBackgroundResource(R.color.white);
                                filterName = FilterList.get(i).get("Name");
                                filterType = FilterList.get(i).get("Type");
                                colorPosition = pos = i;
                                dynamicLayout(filterType, FilterList.get(i).get("fieldSettings"), filterName);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (flag.equalsIgnoreCase("WING")) {
                    for (int i = 0; i < FilterList.size(); i++) {
                        if (FilterList.get(i).get("Name").equalsIgnoreCase("Wing")) {
                            FilterList.get(i).put("fieldSettings", response.getString("Options"));
                        }
                        if (FilterList.get(i).get("Name").equalsIgnoreCase("Individual")) {
                            FilterList.get(i).put("fieldSettings", response.getString("Individual"));
                        }
                    }
                } else if (flag.equalsIgnoreCase("DES")) {
                    for (int i = 0; i < FilterList.size(); i++) {
                        if (FilterList.get(i).get("Name").equalsIgnoreCase("Designation")) {
                            FilterList.get(i).put("fieldSettings", response.getString("Options"));
                        }
                        if (FilterList.get(i).get("Name").equalsIgnoreCase("Individual")) {
                            FilterList.get(i).put("fieldSettings", response.getString("Individual"));
                        }
                    }
                } else if (flag.equalsIgnoreCase("DEPARTMENT")) {
                    for (int i = 0; i < FilterList.size(); i++) {
                        if (FilterList.get(i).get("Name").equalsIgnoreCase("Department")) {
                            FilterList.get(i).put("fieldSettings", response.getString("Options"));
                        }
                        if (FilterList.get(i).get("Name").equalsIgnoreCase("Individual")) {
                            FilterList.get(i).put("fieldSettings", response.getString("Individual"));
                        }
                    }
                } else if (flag.equalsIgnoreCase("INDI")) {
                    for (int i = 0; i < FilterList.size(); i++) {
                        if (FilterList.get(i).get("Name").equalsIgnoreCase("Individual")) {
                            FilterList.get(i).put("fieldSettings", response.getString("Options"));
                        }
                    }
                } else if (flag.equalsIgnoreCase("SEARCH")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("response", response.toString());
                    bundle.putString("listType", listType);
                    bundle.putString("request", requestParameter);
                    navigateToNextFragment(TMSListTreeActivity.class.getName(), bundle);
                }
            } else if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_403)) {
                showSessionScreensDialog(context, response.getString(AppContants.RESPONSE_MESSAGE));
            } else {
                setToast(response.getString(AppContants.RESPONSE_MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void dynamicLayout(String type, String values, String hint) {
        SelectedFilter.removeAllViews();
        SelectedFilter.addView(addEditView(type, values, hint));
    }
    public LinearLayout addEditView(String type, String values, String hint) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        //layoutParams.setMargins(0, 20, 0, 0);
        linearLayout.setPadding(5, 5, 5, 5);
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
                }
            });
        } else if (type.equalsIgnoreCase("DROPDOWN_S")) {
            try {
                JSONArray jsonArray = new JSONArray(values);
                ArrayList<HashMap<String, String>> optionValues = ApiCalls.getArraylistfromJson(jsonArray.toString());
                //   if (filterName.equalsIgnoreCase("Project Name")) {
                linearLayout.addView(addSpinnerToTableRow(optionValues, "Select " + hint, 3), 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("DROPDOWN_M")) {
            try {
                JSONArray jsonArray = new JSONArray(values);
                ArrayList<HashMap<String, String>> optionValues = ApiCalls.getArraylistfromJson(jsonArray.toString());
                hintTextValue = hint;
                FilterAutocomplete = addMultiSpinnerToTableRow(optionValues, hint, 3);
                linearLayout.addView(FilterAutocomplete, 0);
               /* AutoCompleteTextviewAdapter autoCompleteTextviewAdapter = new AutoCompleteTextviewAdapter(context, optionValues);
                FilterAutocomplete.setAdapter(autoCompleteTextviewAdapter);
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
                );*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("DatePicker")) {
            FilterDate = addEditTextDate("", null, 3);
            linearLayout.addView(FilterDate, 0);
            if (filterValue.containsKey(filterName)) {
                FilterDate.setText(filterValue.get(filterName));
            }
        } else if (type.equalsIgnoreCase("COL_PIC")) {
            GridView gridColor = addGridView();
            linearLayout.addView(gridColor, 0);
            int selectedPosition = 0;
            if (filterValue.containsKey("selectedColorPosition")) {
                selectedPosition = Integer.parseInt(filterValue.get("selectedColorPosition"));
            }
            try {
                JSONArray jsonArray = new JSONArray(values);
                if (colors.size() == 0)
                    colors = ApiCalls.getArraylistfromJson(jsonArray.toString());
                ImageAdapter adapter = new ImageAdapter(context, filterName, selectedPosition);
                gridColor.setAdapter(adapter);
                gridColor.setChoiceMode(gridColor.CHOICE_MODE_SINGLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return linearLayout;
    }
    public GridView addGridView() {
        GridView gridView = new GridView(context);
        gridView.setNumColumns(4);
        return gridView;
    }
    public EditText addEditTextDate(String value, Typeface typeface, int layoutWeight) {
        EditText editText = new EditText(context);
        editText.setBackgroundResource(R.drawable.lead_edit_text_bg);
        editText.setTextColor(getResources().getColor(R.color.result_color));
        if (typeface != null)
            editText.setTypeface(typeface);
        editText.setTextSize(14);
        editText.setPadding(2, 2, 2, 2);
        editText.setText(value);
        editText.setFocusable(false);
        editText.setClickable(false);
        //  editText.setHint(filterName);
        editText.setLayoutParams(new TableRow.LayoutParams(0, 100, layoutWeight));
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
                filterValue.put(filterName, s.toString());
            }
        });
        return editText;
    }
    public EditText addEditText(String value, Typeface typeface, int layoutWeight) {
        EditText editText = new EditText(context);
        editText.setBackgroundResource(R.drawable.lead_edit_text_bg);
        editText.setTextColor(getResources().getColor(R.color.result_color));
        if (typeface != null)
            editText.setTypeface(typeface);
        editText.setTextSize(14);
        editText.setPadding(2, 2, 2, 2);
        editText.setText(value);
        editText.setFocusable(false);
        editText.setClickable(false);
        editText.setHint(filterName);
        //  editText.setLayoutParams(new TableRow.LayoutParams(0, 100, layoutWeight));
        return editText;
    }
    public ContactsCompletionView addMultiSpinnerToTableRow(ArrayList<HashMap<String, String>> valueList, final String hintText, int layoutWeight) {
        MultiSearchView = new ContactsCompletionView(context);
        MultiSearchView.setBackgroundResource(R.drawable.lead_dropdown_bg);
        TableRow.LayoutParams spinnerLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight);
        MultiSearchView.setLayoutParams(spinnerLayoutParams);
        MultiSearchView.setCursorVisible(false);
        MultiSearchView.setHint(hintText);
        InputMethodManager in = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(MultiSearchView.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
       people = new lists[valueList.size()];
        try {
            for (int i = 0; i < valueList.size(); i++) {
                people[i] = new lists(valueList.get(i).get("value"), valueList.get(i).get("id"));
            }
            if (filterValue.containsKey(hintText)) {
                String selectMultiSelectValues[] = filterValue.get(hintText).split(",");
                for (int sel = 0; sel < selectMultiSelectValues.length; sel++) {
                    for (int i = 0; i < valueList.size(); i++) {
                        if (selectMultiSelectValues[sel].equalsIgnoreCase(valueList.get(i).get("id")))
                            MultiSearchView.addObject(people[i]);
                    }
                }
            }
            multiSelectAdapter = new FilteredArrayAdapter<lists>(context, R.layout.person_layout, people) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                        convertView = l.inflate(R.layout.person_layout, parent, false);
                    }
                    lists p = getItem(position);
                    ((TextView) convertView.findViewById(R.id.name)).setText(p.getValue());
                    return convertView;
                }
                @Override
                protected boolean keepObject(lists lists, String mask) {
                    mask = mask.toLowerCase();
                    return lists.getValue().toLowerCase().startsWith(mask) || lists.getId().toLowerCase().startsWith(mask);
                }
            };
            MultiSearchView.setAdapter(multiSelectAdapter);
            MultiSearchView.setTokenListener(this);
            MultiSearchView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
            MultiSearchView.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                    // TODO Auto-generated method stub
                    MultiSearchView.showDropDown();
                    return false;
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return MultiSearchView;
    }
    public AutoCompleteTextView autocomplete(String value, Typeface typeface, int layoutWeight) {
        AutoCompleteTextView editText = new AutoCompleteTextView(context);
        editText.setBackgroundResource(R.drawable.lead_edit_text_bg);
        editText.setTextColor(getResources().getColor(R.color.result_color));
        if (typeface != null)
            editText.setTypeface(typeface);
        editText.setTextSize(14);
        editText.setPadding(2, 2, 2, 2);
        editText.setText(value);
        editText.setHint(filterName);
        editText.setThreshold(0);
        return editText;
    }
    public Spinner addSpinnerToTableRow(final ArrayList<HashMap<String, String>> valueList, String hintText, int layoutWeight) {
        Spinner spinner = new Spinner(context);
        spinner.setBackgroundResource(R.drawable.lead_dropdown_bg);
        loadLeadStyleSpinner(spinner, valueList, new String[]{"value"}, hintText);
        if (filterValue.containsKey(filterName + "pos")) {
            spinner.setSelection(Integer.parseInt(filterValue.get(filterName + "pos")));
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos > 0) {
                    filterValue.put(filterName + "pos", "" + pos);
                    filterValue.put(filterName, "" + valueList.get(pos - 1).get("id"));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return spinner;
    }
    @Override
    public void onTokenAdded(lists token) {
        hideKeyboard();
        String multiSelectValue = "";
        for (int i = 0; i < MultiSearchView.getObjects().size(); i++) {
            if (multiSelectValue.equalsIgnoreCase(""))
                multiSelectValue = MultiSearchView.getObjects().get(i).getId();
            else
                multiSelectValue = multiSelectValue + "," + MultiSearchView.getObjects().get(i).getId();
        }
        System.out.println("Token Value--" + multiSelectValue);
        filterValue.put(hintTextValue, multiSelectValue);
        if (filterName.equalsIgnoreCase("Company")) {
            requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'department','comp_id':'" + multiSelectValue + "','dep_id':'','wing_id':'','des_id':'','indiv_id':''}";
            System.out.println(requestParameter);
            getLoadData(requestParameter, "DEPARTMENT");
        } else if (filterName.equalsIgnoreCase("Department")) {
            requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'wing','comp_id':'" + filterValue.get("Company") + "','dep_id':'" + multiSelectValue + "','wing_id':'','des_id':'','indiv_id':''}";
            System.out.println(requestParameter);
            getLoadData(requestParameter, "WING");
        } else if (filterName.equalsIgnoreCase("Wing")) {
            requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'designation','comp_id':'" + filterValue.get("Company") + "','dep_id':'" + filterValue.get("Department") + "','wing_id':'" + multiSelectValue + "','des_id':'','indiv_id':''}";
            System.out.println(requestParameter);
            getLoadData(requestParameter, "DES");
        } else if (filterName.equalsIgnoreCase("Designation")) {
            requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'individual','comp_id':'" + filterValue.get("Company") + "','dep_id':'" + filterValue.get("Department") + "','wing_id':'" + filterValue.get("Wing") + "','des_id':'" + multiSelectValue + "','indiv_id':''}";
            System.out.println(requestParameter);
            getLoadData(requestParameter, "INDI");
        }
    }
    @Override
    public void onTokenRemoved(lists token) {
        String multiSelectValue = "";
        for (int i = 0; i < MultiSearchView.getObjects().size(); i++) {
            if (multiSelectValue.equalsIgnoreCase(""))
                multiSelectValue = MultiSearchView.getObjects().get(i).getId();
            else
                multiSelectValue = multiSelectValue + "," + MultiSearchView.getObjects().get(i).getId();
        }
        System.out.println("Token Value--" + multiSelectValue);
        filterValue.put(hintTextValue, multiSelectValue);
        if (filterName.equalsIgnoreCase("Company")) {
            if (filterValue.containsKey("Department")) {
                filterValue.remove("Department");
            }
            if (filterValue.containsKey("Wing")) {
                filterValue.remove("Wing");
            }
            if (filterValue.containsKey("Designation")) {
                filterValue.remove("Designation");
            }
            if (filterValue.containsKey("Individual")) {
                filterValue.remove("Individual");
            }
            requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'department','comp_id':'" + multiSelectValue + "','dep_id':'','wing_id':'','des_id':'','indiv_id':''}";
            System.out.println(requestParameter);
            getLoadData(requestParameter, "DEPARTMENT");
        } else if (filterName.equalsIgnoreCase("Department")) {
            if (filterValue.containsKey("Wing")) {
                filterValue.remove("Wing");
            }
            if (filterValue.containsKey("Designation")) {
                filterValue.remove("Designation");
            }
            if (filterValue.containsKey("Individual")) {
                filterValue.remove("Individual");
            }
            requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'wing','comp_id':'" + filterValue.get("Company") + "','dep_id':'" + multiSelectValue + "','wing_id':'','des_id':'','indiv_id':''}";
            System.out.println(requestParameter);
            getLoadData(requestParameter, "WING");
        } else if (filterName.equalsIgnoreCase("Wing")) {
            if (filterValue.containsKey("Designation")) {
                filterValue.remove("Designation");
            }
            if (filterValue.containsKey("Individual")) {
                filterValue.remove("Individual");
            }
            requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'designation','comp_id':'" + filterValue.get("Company") + "','dep_id':'" + filterValue.get("Department") + "','wing_id':'" + multiSelectValue + "','des_id':'','indiv_id':''}";
            System.out.println(requestParameter);
            getLoadData(requestParameter, "DES");
        } else if (filterName.equalsIgnoreCase("Designation")) {
            if (filterValue.containsKey("Individual")) {
                filterValue.remove("Individual");
            }
            requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'individual','comp_id':'" + filterValue.get("Company") + "','dep_id':'" + filterValue.get("Department") + "','wing_id':'" + filterValue.get("Wing") + "','des_id':'" + multiSelectValue + "','indiv_id':''}";
            System.out.println(requestParameter);
            getLoadData(requestParameter, "INDI");
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
                v = mInflater.inflate(R.layout.tms_filter, null);
                holder = new FilterViewHolder(v);
                v.setTag(holder);
            } else {
                holder = (FilterViewHolder) v.getTag();
            }
            if (position == colorPosition && isView) {
                pos = position;
                v.setBackgroundResource(R.color.white);
            } else {
                v.setBackgroundResource(0);
            }
            if (filterList.get(position).get("IsMandatory").equalsIgnoreCase("true")) {
                String manda = "<font color='#EE0000'> *</font>";
                holder.listItemName.setText(Html.fromHtml(filterList.get(position).get("Name") + manda));
                //holder.listItemName.setText(Html.fromHtml(filterList.get(position).get("Name") + " " + Html.fromHtml("<font color='#FF0000'>*</font>")));
            } else
                holder.listItemName.setText(filterList.get(position).get("Name"));
           /* v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterName = filterList.get(position).get("Name");
                    filterType = filterList.get(position).get("Type");
                    colorPosition=pos = position;
                    dynamicLayout(filterType, filterList.get(position).get("fieldSettings"), filterName);
                    previous.setBackgroundResource(0);
                    ImageView arrow = (ImageView) previous.findViewById(R.id.Filter_list_right_arrow);
                    arrow.setVisibility(View.GONE);
                    v.setBackgroundResource(R.color.white);
                    holder.arrowView.setVisibility(View.VISIBLE);
                    v.setBackgroundResource(R.color.white);
                    previous = v;
                }
            });*/
            return v;
        }
    }
    class FilterViewHolder {
        @Bind(R.id.name)
        SCMTextView listItemName;
        @Bind(R.id.Filter_list_right_arrow)
        ImageView arrowView;
        public FilterViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
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
            editDateTextView.setText(day + "/" + month + "/" + year);
            editDateTextView.setError(null);
        }
    }
    public class ImageAdapter extends BaseAdapter {
        public String selectColor;
        public int selectedPosition;
        public Context context;
        public LayoutInflater inflater;
        public ImageAdapter(Context context, String selectColor, int selectedPosition) {
            super();
            this.context = context;
            this.selectColor = selectColor;
            this.selectedPosition = selectedPosition;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return colors.size();
        }
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.color_list_row, null);
                holder.colorBox = (CheckBox) convertView.findViewById(R.id.box);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();
            // holder.colorBox.setText(title[position]);
            if (colors.get(position).get("id").length() > 0)
                holder.colorBox.setBackgroundColor(Color.parseColor(colors.get(position).get("value")));
            else
                holder.colorBox.setBackgroundColor(Color.parseColor("#00000000"));
            if (selectedPosition == position)
                holder.colorBox.setChecked(true);
            else
                holder.colorBox.setChecked(false);
            holder.colorBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = position;
                    filterValue.put(selectColor, colors.get(position).get("id"));
                    filterValue.put("selectedColorPosition", "" + position);
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }
    public static class ViewHolder {
        CheckBox colorBox;
    }
}
