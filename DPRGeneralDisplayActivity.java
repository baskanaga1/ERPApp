package com.guruinfo.scm.DPR;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.guruinfo.scm.DPR.DPRDialog.DPR_MaterialRequest;
import com.guruinfo.scm.DPR.DPRDialog.DPR_MinDetails;
import com.guruinfo.scm.DPR.DPRDialog.DPR_Nmr;
import com.guruinfo.scm.DPR.DPRDialog.DPR_SubActivity;
import com.guruinfo.scm.R;
import com.guruinfo.scm.SCMDashboardActivityLatest;
import com.guruinfo.scm.common.BaseActivity;
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
public class DPRGeneralDisplayActivity extends BaseActivity {
    String TAG = "DPRGeneralDisplayAct";
    Context context;
    SessionManager session;
    String uid, Cre_Id;
    int beigeRowColor;
    int lightGreyRowColor;
    int lastAppliedColor;
    boolean isGeneralInformationExpanded = true;
    //General Information
    /*@Bind(R.id.general_information_layout)
    LinearLayout generalInformationLayout;
    @Bind(R.id.dpr_arrow_img)
    ImageView dprArrowIcon;
    @Bind(R.id.dpr_id_linear_layout)
    LinearLayout dprIdLinear;*/

    @Bind(R.id.work_lay)
    LinearLayout workOrderlay;
    @Bind(R.id.matrial_lay)
    LinearLayout matrialLay;
    @Bind(R.id.prim_lay)
    LinearLayout primLay;
    @Bind(R.id.sub_lay)
    LinearLayout subLay;
    @Bind(R.id.mis_lay)
    LinearLayout misLay;
    @Bind(R.id.nmr_lay)
    LinearLayout nmrLay;
    @Bind(R.id.min_lay)
    LinearLayout minLay;

    @Bind(R.id.enter_by_lay)
    LinearLayout enter_by_lay;
    @Bind(R.id.contractor_lay)
    LinearLayout contractor_lay;
    @Bind(R.id.pjt_lay)
    LinearLayout pjt_lay;
    @Bind(R.id.nature_lay)
    LinearLayout nature_lay;
    @Bind(R.id.pan_no_lay)
    LinearLayout pan_no_lay;
    @Bind(R.id.block_name_lay)
    LinearLayout block_name_lay;
    @Bind(R.id.description_lay)
    LinearLayout description_lay;

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
    @Bind(R.id.ic_home)
    ImageButton homeButton;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    String colorCode;
    String woId;
    String mBookId;
    JSONArray historyProcessJSONArray = new JSONArray();

    public static DPRGeneralDisplayFragment newInstance(Bundle bundle) {
        DPRGeneralDisplayFragment fragment = new DPRGeneralDisplayFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dpr_general_display_new);
        Log.d(TAG, "###onCreate");
        context = this;
        ButterKnife.bind(this);
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
        if (getIntent().getStringExtra("response") != null) {
            dprDisplay(getIntent().getStringExtra("response"));
        }
        if ((getIntent().getStringExtra("colorCode").equalsIgnoreCase("#fa4067")))
            homeButton.setVisibility(View.GONE);
        colorCode = getIntent().getStringExtra("colorCode");
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        appBarLayout.setVisibility(View.VISIBLE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Daily Progress Report");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorCode)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(darker(Color.parseColor(colorCode), 0.9f));
        }
        /*dprIdLinear.setOnClickListener(new View.OnClickListener() {
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
        });*/
        HistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("History", historyProcessJSONArray.toString());
                HistoryDialog(bundle);
            }
        });
        woRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (woId != null) {

                }
            }
        });
        matrialLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBookId != null) {
                    Intent intent = new Intent(DPRGeneralDisplayActivity.this, DPR_MaterialRequest.class);
                    intent.putExtra("id", mBookId);
                    intent.putExtra("type", "mr");
                    intent.putExtra("colorCode", "#0aa0dc");
                    intent.putExtra("title", "Material Request");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(DPRGeneralDisplayActivity.this, matrialLay, getString(R.string.transition_item));
                    ActivityCompat.startActivity(DPRGeneralDisplayActivity.this, intent, options.toBundle());
                }
            }
        });
        misLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBookId != null) {
                    Intent intent = new Intent(DPRGeneralDisplayActivity.this, DPR_MaterialRequest.class);
                    intent.putExtra("id", mBookId);
                    intent.putExtra("type", "misc");
                    intent.putExtra("colorCode", "#0aa0dc");
                    intent.putExtra("title", "Miscellaneous Work");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(DPRGeneralDisplayActivity.this, misLay, getString(R.string.transition_item));
                    ActivityCompat.startActivity(DPRGeneralDisplayActivity.this, intent, options.toBundle());
                }
            }
        });
        primLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBookId != null) {
                    Intent intent = new Intent(DPRGeneralDisplayActivity.this, DPR_MaterialRequest.class);
                    intent.putExtra("id", mBookId);
                    intent.putExtra("type", "others");
                    intent.putExtra("colorCode", "#0aa0dc");
                    intent.putExtra("title", "Primary Stage");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(DPRGeneralDisplayActivity.this, primLay, getString(R.string.transition_item));
                    ActivityCompat.startActivity(DPRGeneralDisplayActivity.this, intent, options.toBundle());
                }
            }
        });
        subLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBookId != null) {
                    Intent intent = new Intent(context, DPR_SubActivity.class);
                    intent.putExtra("MbookId", mBookId);
                    intent.putExtra("colorCode", "#0aa0dc");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(DPRGeneralDisplayActivity.this, subLay, getString(R.string.transition_item));
                    ActivityCompat.startActivity(DPRGeneralDisplayActivity.this, intent, options.toBundle());
                }
            }
        });
        nmrLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBookId != null) {
                    Intent intent = new Intent(context, DPR_Nmr.class);
                    intent.putExtra("MbookId", mBookId);
                    intent.putExtra("colorCode", "#0aa0dc");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(DPRGeneralDisplayActivity.this, nmrLay, getString(R.string.transition_item));
                    ActivityCompat.startActivity(DPRGeneralDisplayActivity.this, intent, options.toBundle());
                }
            }
        });
        minLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBookId != null) {
                    Intent intent = new Intent(context, DPR_MinDetails.class);
                    intent.putExtra("MbookId", mBookId);
                    intent.putExtra("colorCode", "#0aa0dc");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(DPRGeneralDisplayActivity.this, minLay, getString(R.string.transition_item));
                    ActivityCompat.startActivity(DPRGeneralDisplayActivity.this, intent, options.toBundle());
                }
            }
        });
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
            if (respObject.getString("showAllDetails").equalsIgnoreCase("false")) {
                matrialLay.setVisibility(View.GONE);
                primLay.setVisibility(View.GONE);
                misLay.setVisibility(View.GONE);
                nmrLay.setVisibility(View.GONE);
            }
            JSONObject generalInfoObject = respObject.getJSONObject("generalInformation");
            if(generalInfoObject.getString("projectName").equalsIgnoreCase(""))
                pjt_lay.setVisibility(View.GONE);
            if(generalInfoObject.getString("enteredBy").equalsIgnoreCase(""))
                enter_by_lay.setVisibility(View.GONE);
            if(generalInfoObject.getString("contractorName").equalsIgnoreCase(""))
                contractor_lay.setVisibility(View.GONE);
            if(generalInfoObject.getString("natureOfWork").equalsIgnoreCase(""))
                nature_lay.setVisibility(View.GONE);
            if(generalInfoObject.getString("panNo").equalsIgnoreCase(""))
                pan_no_lay.setVisibility(View.GONE);
            if(generalInfoObject.getString("description").equalsIgnoreCase(""))
                description_lay.setVisibility(View.GONE);
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
            mBookId = generalInfoObject.getString("MbookId");
            woId = generalInfoObject.getString("woId");
            JSONArray blocjArray = generalInfoObject.getJSONArray("BlockName");
            blockName.setText("");
            block_name_lay.setVisibility(View.GONE);
            for (int i = 0; i < blocjArray.length(); i++) {
                if (blocjArray.getJSONObject(i).getString("selected").equalsIgnoreCase("true")) {
                    blockName.setText(blocjArray.getJSONObject(i).getString("value"));
                    block_name_lay.setVisibility(View.VISIBLE);
                }
            }
            description.setText(generalInfoObject.getString("description"));
            // if (respObject.getString("showHistory").equalsIgnoreCase("true")) {
            historyProcessJSONArray = respObject.getJSONArray("History");
            if (historyProcessJSONArray.length() == 0)
                HistoryButton.setVisibility(View.GONE);
           /* }else
                HistoryButton.setVisibility(View.GONE);*/
            mrLabel.setText(respObject.getString("MRCount"));
            primaryLabel.setText(respObject.getString("PrimaryCount"));
            subActivityLabel.setText(respObject.getString("SubActivityCount"));
            miscLabel.setText(respObject.getString("MiscCount"));
            nmrLabel.setText(respObject.getString("NMRCount"));
            //  minLabel.setText("MIN Details ("+respObject.getString("MRCount")+")");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}