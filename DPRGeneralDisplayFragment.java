package com.guruinfo.scm.DPR;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.guruinfo.scm.R;
import com.guruinfo.scm.SCMDashboardActivityLatest;
import com.guruinfo.scm.common.BaseFragment;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.ui.SCMMandatoryEditText;
import com.guruinfo.scm.common.utils.ApiCalls;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by Kannan G on 11/14/2017.
 */
public class DPRGeneralDisplayFragment extends BaseFragment {
    String TAG = "DPRGeneralDisplayFrg";
    Context context;
    SessionManager session;
    String uid, Cre_Id;
    int beigeRowColor;
    int lightGreyRowColor;
    int lastAppliedColor;
    boolean isGeneralInformationExpanded = true;
    //General Information
    @Bind(R.id.general_information_layout)
    LinearLayout generalInformationLayout;
    @Bind(R.id.dpr_arrow_img)
    ImageView dprArrowIcon;
    @Bind(R.id.dpr_id_linear_layout)
    LinearLayout dprIdLinear;
    @Bind(R.id.dpr_project)
    TextView dprProject;
    @Bind(R.id.dpr_code_value)
    TextView dprCode;
    @Bind(R.id.dpr_status)
    TextView dprStatus;
    @Bind(R.id.enter_by)
    TextView enterBy;
    @Bind(R.id.contractor_name)
    TextView contractorName;
    @Bind(R.id.from_date)
    TextView fromDate;
    @Bind(R.id.to_date)
    TextView toDate;
    @Bind(R.id.nature_work)
    TextView natureOfWork;
    @Bind(R.id.wo_ref)
    TextView woRef;
    @Bind(R.id.pan_no)
    TextView panNo;
    @Bind(R.id.block_name)
    TextView blockName;
    @Bind(R.id.description)
    TextView description;
    //History
    @Bind(R.id.history_button)
    LinearLayout HistoryButton;
    //Tab
    @Bind(R.id.mr_text)
    TextView mrLabel;
    @Bind(R.id.primary_text)
    TextView primaryLabel;
    @Bind(R.id.sub_activity_text)
    TextView subActivityLabel;
    @Bind(R.id.misc_text)
    TextView miscLabel;
    @Bind(R.id.nmr_text)
    TextView nmrLabel;
    @Bind(R.id.min_text)
    TextView minLabel;
    JSONArray historyProcessJSONArray = new JSONArray();
    public static DPRGeneralDisplayFragment newInstance(Bundle bundle) {
        DPRGeneralDisplayFragment fragment = new DPRGeneralDisplayFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "###onCreate");
        context = getActivity();
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "###onCreateView");
        View view = inflater.inflate(R.layout.dpr_general_display, container, false);
        ButterKnife.bind(this, view);
        ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>Daily Progress Report</font>"));
        Bundle bundle = this.getArguments();
        if (!bundle.isEmpty()) {
            dprDisplay(bundle.getString("response"));
        }
        dprIdLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "generalInfoTitleTextView Clicked");
                if (!isGeneralInformationExpanded) {
                    isGeneralInformationExpanded = true;
                    dprArrowIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_collapse_new));
                    generalInformationLayout.setVisibility(View.VISIBLE);
                } else {
                    isGeneralInformationExpanded = false;
                    dprArrowIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_expand_new));
                    generalInformationLayout.setVisibility(View.GONE);
                }
            }
        });
        HistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("History", historyProcessJSONArray.toString());
                HistoryDialog(bundle);
            }
        });
        return view;
    }
    public void HistoryDialog(Bundle bundle) {
        JSONArray historyJsonArray = new JSONArray();
        ArrayList<HashMap<String, String>> historyArray = new ArrayList<>();
        String historyStringValue;
        int beigeRowColor;
        int lightGreyRowColor;
        int lastAppliedColor;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater2 = LayoutInflater.from(context);
        View convertView = inflater2.inflate(R.layout.po_history_dialog_layout, null);
        dialog.setContentView(convertView);
        ImageView close = (ImageView) convertView.findViewById(R.id.close_dialog_button);
        TableLayout historyTable = (TableLayout) convertView.findViewById(R.id.history_table_layout);
        beigeRowColor = getResources().getColor(R.color.unread_bg);
        historyStringValue = bundle.getString("History");
        try {
            historyJsonArray = new JSONArray(historyStringValue.toString());
            if (historyJsonArray.length() > 0) {
                historyArray = ApiCalls.getArraylistfromJson(historyJsonArray.toString());
                for (int i = 0; i < historyArray.size(); i++) {
                    historyTable.addView(addTableRowToTableLayout(historyArray.get(i), "History"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
    public TableRow addTableRowToTableLayout(HashMap<String, String> list, String type) {
        TableRow tableRow = new TableRow(context);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.FILL_PARENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        tableRow.setLayoutParams(layoutParams);
        tableRow.setOrientation(LinearLayout.HORIZONTAL);
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            tableRow.setPadding(10, 10, 10, 10);
        } else {
            tableRow.setPadding(5, 5, 5, 5);
        }
        applyAlternateRowColor(tableRow);
        if (type.equals("History")) {
            tableRow.addView(addTextViewToTableRow(list.get("Date"), null, 1), 0);
            tableRow.addView(addTextViewToTableRow(list.get("User"), null, 1), 1);
            tableRow.addView(addTextViewToTableRow(list.get("Pre Process Status"), null, 1), 2);
            tableRow.addView(addTextViewToTableRow(list.get("Current Process Status"), null, 1), 3);
            tableRow.addView(addTextViewToTableRow(list.get("Comments"), null, 1), 4);
        }
        return tableRow;
    }
    public void applyAlternateRowColor(TableRow datatable) {
        if (lastAppliedColor == 0) {
            datatable.setBackgroundColor(beigeRowColor);
            lastAppliedColor = beigeRowColor;
        } else if (lastAppliedColor == beigeRowColor) {
            datatable.setBackgroundColor(lightGreyRowColor);
            lastAppliedColor = lightGreyRowColor;
        } else if (lastAppliedColor == lightGreyRowColor) {
            datatable.setBackgroundColor(beigeRowColor);
            lastAppliedColor = beigeRowColor;
        }
    }
    public void dprDisplay(String response) {
        try {
            JSONObject respObject = new JSONObject(response);
            JSONObject generalInfoObject = respObject.getJSONObject("generalInformation");
            dprProject.setText(generalInfoObject.getString("projectName"));
            dprCode.setText(generalInfoObject.getString("DPRRefNo"));
            dprStatus.setText(generalInfoObject.getString("Status"));
            enterBy.setText(generalInfoObject.getString("enteredBy"));
            contractorName.setText(generalInfoObject.getString("contractorName"));
            fromDate.setText(generalInfoObject.getString("fromDate"));
            toDate.setText(generalInfoObject.getString("toDate"));
            natureOfWork.setText(generalInfoObject.getString("natureOfWork"));
            woRef.setText(generalInfoObject.getString("woRef"));
            panNo.setText(generalInfoObject.getString("panNo"));
            JSONArray blocjArray = generalInfoObject.getJSONArray("BlockName");
            for (int i = 0; i < blocjArray.length(); i++) {
                if (blocjArray.getJSONObject(i).getString("selected").equalsIgnoreCase("true"))
                    blockName.setText(blocjArray.getJSONObject(i).getString("value"));
            }
            description.setText(generalInfoObject.getString("description"));
           // if (respObject.getString("showHistory").equalsIgnoreCase("true")) {
                historyProcessJSONArray = respObject.getJSONArray("History");
                if (historyProcessJSONArray.length() == 0)
                    HistoryButton.setVisibility(View.GONE);
           /* }else
                HistoryButton.setVisibility(View.GONE);*/
            mrLabel.setText("MR (" + respObject.getString("MRCount") + ")");
            primaryLabel.setText("Primary Stage (" + respObject.getString("PrimaryCount") + ")");
            subActivityLabel.setText("Sub Activity (" + respObject.getString("SubActivityCount") + ")");
            miscLabel.setText("Miscallenous Work (" + respObject.getString("MiscCount") + ")");
            nmrLabel.setText("NMR (" + respObject.getString("NMRCount") + ")");
            //  minLabel.setText("MIN Details ("+respObject.getString("MRCount")+")");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
