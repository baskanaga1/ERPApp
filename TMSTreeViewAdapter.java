package com.guruinfo.scm.tms;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseFragment;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.tree.model.TreeNode;
import com.guruinfo.scm.common.ui.SCMTextView;
import com.guruinfo.scm.common.utils.ApiCalls;
import com.michael.easydialog.EasyDialog;
import org.json.JSONArray;
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
import static com.guruinfo.scm.tms.TMSListTreeActivity.collapse;
import static com.guruinfo.scm.tms.TMSListTreeActivity.expand;
/**
 * Created by ERP on 11/16/2016.
 */
public class TMSTreeViewAdapter extends TreeNode.BaseNodeViewHolder<TMSTreeViewAdapter.TreeItem> {
    String TAG = "TMSTreeViewAdapter";
    String uid, Cre_Id;
    String requestParameter;
    BackgroundTask backgroundTask;
    SessionManager session;
    Dialog reassignDialog;
    Dialog statusUpdateDialog;
    Dialog ratingRequestDialog;
    Dialog ratedDialog;
    Dialog holdDialog;
    Dialog declineSelectDialog;
    Dialog timeExtensionDialog;
    Dialog timeExtensionApprovedDialog;
    Spinner companySpinner,departmentSpinner, wingSpinner, designationSpinner, individualSpinner;
    CheckBox selfAssignCheck;
    static EditText editDateTextView;
    String bookMarkStatusValue;
    private LinearLayout parentLay, childLay;
    ImageView arrow;
    HashMap<String, String> mapValues;
    TMSListReload listReload;
    ImageView actionClicked;
    EasyDialog actionDialog;
    ArrayList<HashMap<String, String>> companyArrayList = new ArrayList<>();
    ArrayList<HashMap<String, String>> wingArrayList = new ArrayList<>();
    ArrayList<HashMap<String, String>> departmentArrayList = new ArrayList<>();
    ArrayList<HashMap<String, String>> designationArrayList = new ArrayList<>();
    ArrayList<HashMap<String, String>> IndividualArrayList = new ArrayList<>();
    LinearLayout childColor;
    TextView tmsPriority;
    TextView tmsTaskName;
    TextView tmsEndDate;
    ImageView tmsBooking;
    ImageView tmsPlay;
    ImageView tmsHold;
    ImageView tmsComplete;
    ImageView tmsLitigation;
    ImageView tmsDeclineRed;
    CheckBox tmsMultiSelect;
    CheckBox tmsMutiDecline;
    TableRow tmsRatingReqButtonRow;
    ImageView tmsRatingReqButton;
    //Content Components
    @Bind(R.id.tms_taskId)
    TextView tmsTaskId;
    @Bind(R.id.tms_project)
    TextView tmsProject;
    @Bind(R.id.tms_assignedBy)
    TextView tmsAssignedBy;
    @Bind(R.id.tms_assignedTO)
    TextView tmsAssignedTo;
    @Bind(R.id.tms_startDate)
    TextView tmsStartDate;
    @Bind(R.id.tms_status)
    TextView tmsStatus;
    @Bind(R.id.tms_statusReq)
    TextView tmsStatusReq;
    @Bind(R.id.tms_action)
    ImageView tmsActionDialog;
    @Bind(R.id.tms_overdue)
    ImageView tmsOverdue;
    @Bind(R.id.assByRow)
    LinearLayout assByRow;
    @Bind(R.id.assToRow)
    LinearLayout assToRow;
    Typeface myTypeface;
    Typeface myTypefaceBold;
    @Bind(R.id.tms_taskId_label)
    TextView tmsTaskIdLabel;
    @Bind(R.id.tms_project_label)
    TextView tmsProjectLabel;
    @Bind(R.id.tms_assignedBy_label)
    TextView tmsAssignedByLabel;
    @Bind(R.id.tms_assignedTO_label)
    TextView tmsAssignedToLabel;
    @Bind(R.id.tms_startDate_label)
    TextView tmsStartDateLabel;
    @Bind(R.id.tms_status_label)
    TextView tmsStatusLabel;
    public TMSTreeViewAdapter(Context context) {
        super(context);
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
    }
    @Override
    public View createNodeView(final TreeNode node, TreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.tms_list_parent, null, false);
        ButterKnife.bind(this, view);
        myTypeface = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        myTypefaceBold = Typeface.createFromAsset(context.getAssets(), "Roboto-Bold.ttf");
        parentLay = (LinearLayout) view.findViewById(R.id.parent_lay);
        childLay = (LinearLayout) view.findViewById(R.id.child_lay);
        arrow = (ImageView) view.findViewById(R.id.arrow);
        tmsTaskName = (TextView) view.findViewById(R.id.task_Name);
        tmsTaskName.setTypeface(myTypefaceBold);
        tmsEndDate = (TextView) view.findViewById(R.id.tms_endDate);
        tmsEndDate.setTypeface(myTypeface);
        tmsPriority = (TextView) view.findViewById(R.id.tms_priority);
        tmsPriority.setTypeface(myTypeface);
        tmsEndDate = (TextView) view.findViewById(R.id.tms_endDate);
        tmsEndDate.setTypeface(myTypeface);
        tmsBooking = (ImageView) view.findViewById(R.id.tms_Bookmark);
        tmsPlay = (ImageView) view.findViewById(R.id.tms_play);
        tmsHold = (ImageView) view.findViewById(R.id.tms_hold);
        tmsComplete = (ImageView) view.findViewById(R.id.tms_complete);
        tmsLitigation = (ImageView) view.findViewById(R.id.tms_litigation);
        tmsDeclineRed = (ImageView) view.findViewById(R.id.select1_checkbox);
        tmsMultiSelect = (CheckBox) view.findViewById(R.id.select_checkbox);
        tmsMutiDecline = (CheckBox) view.findViewById(R.id.decline_button);
        childColor = (LinearLayout) view.findViewById(R.id.color_lay);
        listReload = value.tmsListReload;
        mapValues = value.listValue;
        childLay.setVisibility(View.GONE);
        assByRow.setVisibility(View.GONE);
        assToRow.setVisibility(View.GONE);
        if (node.getLevel() == 1)
            childColor.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        else if (node.getLevel() == 2)
            childColor.setBackgroundColor(context.getResources().getColor(R.color.first_child));
        else if (node.getLevel() == 3)
            childColor.setBackgroundColor(context.getResources().getColor(R.color.second_child));
        else if (node.getLevel() == 4)
            childColor.setBackgroundColor(context.getResources().getColor(R.color.third_child));
        else if (node.getLevel() == 5)
            childColor.setBackgroundColor(context.getResources().getColor(R.color.fourth_child));
        else if (node.getLevel() == 6)
            childColor.setBackgroundColor(context.getResources().getColor(R.color.fifth_child));
        else
            childColor.setBackgroundColor(context.getResources().getColor(R.color.default_child));
        tmsPriority.setText(mapValues.get("Priority"));
        if (mapValues.get("isCheckbox").equalsIgnoreCase("true"))
            tmsMultiSelect.setVisibility(View.VISIBLE);
        else
            tmsMultiSelect.setVisibility(View.GONE);
        if (mapValues.get("isDeclinebox").equalsIgnoreCase("true")) {
            tmsMutiDecline.setChecked(false);
            if (mapValues.get("isDeclineRedbox").equalsIgnoreCase("true")) {
                tmsMutiDecline.setVisibility(View.GONE);
                tmsDeclineRed.setVisibility(View.VISIBLE);
                tmsMultiSelect.setEnabled(false);
            } else {
                tmsMutiDecline.setVisibility(View.VISIBLE);
                tmsDeclineRed.setVisibility(View.GONE);
                tmsMultiSelect.setEnabled(true);
            }
        } else {
            tmsMutiDecline.setChecked(false);
            tmsMutiDecline.setVisibility(View.GONE);
            tmsDeclineRed.setVisibility(View.GONE);
            tmsMultiSelect.setEnabled(true);
        }
        try {
            JSONArray headerJsonArray = new JSONArray(mapValues.get("displayValues"));
            for (int a = 0; a < headerJsonArray.length(); a++) {
                JSONObject jsonObject = headerJsonArray.getJSONObject(a);
                if (jsonObject.getString("Label").equalsIgnoreCase("Task Name"))
                    tmsTaskName.setText(jsonObject.getString("Value"));
                else if (jsonObject.getString("Label").equalsIgnoreCase("End Date"))
                    tmsEndDate.setText(jsonObject.getString("Value"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mapValues.get("Priority").equalsIgnoreCase("High")) {
            tmsPriority.setBackgroundResource(R.drawable.tms_priority_border_red);
        } else if (mapValues.get("Priority").equalsIgnoreCase("Normal")) {
            tmsPriority.setBackgroundResource(R.drawable.tms_priority_border_gray);
        } else if (mapValues.get("Priority").equalsIgnoreCase("Low")) {
            tmsPriority.setBackgroundResource(R.drawable.tms_priority_border_green);
        } else if (mapValues.get("Priority").equalsIgnoreCase("Flagged")) {
            tmsPriority.setBackgroundResource(R.drawable.tms_priority_border_yellow);
        } else if (mapValues.get("Priority").equalsIgnoreCase("--") || mapValues.get("Priority").equalsIgnoreCase("-")) {
            tmsPriority.setVisibility(View.INVISIBLE);
        } else {
            tmsPriority.setBackgroundResource(R.drawable.tms_priority_border_black);
        }
        if (mapValues.get("BookMarked").equalsIgnoreCase("true")) {
            tmsBooking.setImageResource(R.drawable.bookmark_select);
            tmsBooking.setTag("true");
        } else {
            tmsBooking.setImageResource(R.drawable.bookmark_unselect);
            tmsBooking.setTag("false");
        }
        if (mapValues.get("Litegation").equalsIgnoreCase("true"))
            tmsLitigation.setVisibility(View.VISIBLE);
        else
            tmsLitigation.setVisibility(View.GONE);
        if (mapValues.get("holdButton").equalsIgnoreCase("true"))
            tmsHold.setVisibility(View.VISIBLE);
        else
            tmsHold.setVisibility(View.GONE);
        if (mapValues.get("completeButton").equalsIgnoreCase("true"))
            tmsComplete.setVisibility(View.VISIBLE);
        else
            tmsComplete.setVisibility(View.GONE);
        if (mapValues.get("playButton").equalsIgnoreCase("true"))
            tmsPlay.setVisibility(View.VISIBLE);
        else
            tmsPlay.setVisibility(View.GONE);
        if (value.arrowView)
            arrow.setVisibility(View.VISIBLE);
        else
            arrow.setVisibility(View.GONE);
        if (mapValues.get("overdue").equalsIgnoreCase("1"))
            tmsOverdue.setVisibility(View.VISIBLE);
        else
            tmsOverdue.setVisibility(View.GONE);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (node.isExpanded())
                    tView.expandNode(node);
                else
                    tView.collapseNode(node);*/
                tView.toggleNode(node);
            }
        });
        try {
            JSONArray headerJsonArray = new JSONArray(mapValues.get("displayValues"));
            for (int a = 0; a < headerJsonArray.length(); a++) {
                JSONObject jsonObject = headerJsonArray.getJSONObject(a);
                if (jsonObject.getString("Label").equalsIgnoreCase("Project LocationVerticals")) {
                    tmsProject.setText(jsonObject.getString("Value"));
                    tmsProject.setTypeface(myTypeface);
                    tmsProjectLabel.setTypeface(myTypeface);
                } else if (jsonObject.getString("Label").equalsIgnoreCase("Assigned To")) {
                    assToRow.setVisibility(View.VISIBLE);
                    tmsAssignedTo.setText(jsonObject.getString("Value"));
                    tmsAssignedTo.setTypeface(myTypeface);
                    tmsAssignedToLabel.setTypeface(myTypeface);
                } else if (jsonObject.getString("Label").equalsIgnoreCase("Assigned By")) {
                    assByRow.setVisibility(View.VISIBLE);
                    tmsAssignedBy.setText(jsonObject.getString("Value"));
                    tmsAssignedBy.setTypeface(myTypeface);
                    tmsAssignedByLabel.setTypeface(myTypeface);
                } else if (jsonObject.getString("Label").equalsIgnoreCase("Start Date")) {
                    tmsStartDate.setText(jsonObject.getString("Value"));
                    tmsStartDate.setTypeface(myTypeface);
                    tmsStartDateLabel.setTypeface(myTypeface);
                } else if (jsonObject.getString("Label").equalsIgnoreCase("Status")) {
                    tmsStatus.setText(jsonObject.getString("Value"));
                    tmsStatus.setTypeface(myTypeface);
                    tmsStatusLabel.setTypeface(myTypeface);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tmsTaskId.setText(mapValues.get("TaskId"));
        tmsTaskId.setTypeface(myTypefaceBold);
        tmsTaskId.setTextColor(Color.parseColor("#f58205"));
        tmsTaskId.setPaintFlags(tmsTaskId.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tmsTaskIdLabel.setTypeface(myTypeface);
        tmsStatusReq.setText(mapValues.get("StatusKey"));
        tmsStatusReq.setTypeface(myTypeface);
        parentLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (childLay.getVisibility() == View.VISIBLE) {
                    collapse(childLay);
                    //childLay.setVisibility(View.GONE);
                    TMSListTreeActivity.dummyLay = null;
                } else {
                    //childLay.setVisibility(View.VISIBLE);
                    expand(childLay);
                    if (TMSListTreeActivity.dummyLay != null)
                        collapse(TMSListTreeActivity.dummyLay);
                    // dummyLay.setVisibility(View.GONE);
                    TMSListTreeActivity.dummyLay = childLay;
                }
            }
        });
        tmsTaskId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("TaskId", mapValues.get("TaskId"));
                bundle.putString("Mode", "display");
                listReload.onMoveEditPage(bundle);
            }
        });
        tmsLitigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'LITEGATION_DISPLAY','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + mapValues.get("TaskId") + "'}";
                System.out.println(requestParameter);
                getLoadData(requestParameter, "LITIGATION");
            }
        });
        tmsActionDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionClicked = tmsActionDialog;
                actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_select));
                LayoutInflater inflater2 = LayoutInflater.from(context);
                View convertView = inflater2.inflate(R.layout.tms_action_dialog, null);
                TableRow tmsHoldChatRow = (TableRow) convertView.findViewById(R.id.tms_holdchat_row);
                TableRow tmsTimeReqExtensionRow = (TableRow) convertView.findViewById(R.id.tms_time_request_extension_row);
                TableRow tmsTimeApprovalExtensionRow = (TableRow) convertView.findViewById(R.id.tms_time_approval_extension_row);
                TableRow tmsReAssignButtonRow = (TableRow) convertView.findViewById(R.id.tms_reAssign_row);
                TableRow tmsReSendButtonRow = (TableRow) convertView.findViewById(R.id.tms_resend_row);
                TableRow tmsCloneButtonRow = (TableRow) convertView.findViewById(R.id.tms_clone_row);
                TableRow tmsStatusUpdateButtonRow = (TableRow) convertView.findViewById(R.id.tms_statusUpdate_row);
                TableRow tmsTaskDisConButtonRow = (TableRow) convertView.findViewById(R.id.tms_taskDiscontinue_row);
                TableRow tmsReOpenButtonRow = (TableRow) convertView.findViewById(R.id.tms_reOpen_row);
                tmsRatingReqButtonRow = (TableRow) convertView.findViewById(R.id.tms_ratingRequest_row);
                TableRow tmsDeleteButtonRow = (TableRow) convertView.findViewById(R.id.tms_delete_row);
                tmsRatingReqButton = (ImageView) convertView.findViewById(R.id.tms_ratingRequest);
                TextView tmsRatingText = (TextView) convertView.findViewById(R.id.tms_ratingRequest_text);
                if (mapValues.get("ResendIcon").equalsIgnoreCase("true"))
                    tmsReSendButtonRow.setVisibility(View.VISIBLE);
                else
                    tmsReSendButtonRow.setVisibility(View.GONE);
                if (mapValues.get("taskRatingButton").equalsIgnoreCase("Red")) {
                    tmsRatingReqButtonRow.setVisibility(View.VISIBLE);
                    tmsRatingReqButton.setImageResource(R.drawable.tms_ratingreq_red);
                    tmsRatingText.setText("Rating Requested");
                } else if (mapValues.get("taskRatingButton").equalsIgnoreCase("Green")) {
                    tmsRatingReqButtonRow.setVisibility(View.VISIBLE);
                    tmsRatingReqButton.setImageResource(R.drawable.tms_ratingreq_green);
                    tmsRatingText.setText("Rating");
                } else if (mapValues.get("taskRatingButton").equalsIgnoreCase("Rated")) {
                    tmsRatingReqButtonRow.setVisibility(View.VISIBLE);
                    tmsRatingReqButton.setImageResource(R.drawable.tms_rating);
                    tmsRatingText.setText("Rated");
                } else if (mapValues.get("taskRatingButton").equalsIgnoreCase("Orange")) {
                    tmsRatingReqButtonRow.setVisibility(View.VISIBLE);
                    tmsRatingReqButton.setImageResource(R.drawable.tms_ratingreq_orange);
                    tmsRatingText.setText("Rating Request");
                } else {
                    tmsRatingReqButtonRow.setVisibility(View.GONE);
                }
                if (Boolean.parseBoolean(mapValues.get("taskReassignButton")))
                    tmsReAssignButtonRow.setVisibility(View.VISIBLE);
                else
                    tmsReAssignButtonRow.setVisibility(View.GONE);
                if (Boolean.parseBoolean(mapValues.get("taskCloneButton")))
                    tmsCloneButtonRow.setVisibility(View.VISIBLE);
                else
                    tmsCloneButtonRow.setVisibility(View.GONE);
                if (Boolean.parseBoolean(mapValues.get("taskDiscontinueButton")))
                    tmsTaskDisConButtonRow.setVisibility(View.VISIBLE);
                else
                    tmsTaskDisConButtonRow.setVisibility(View.GONE);
                if (Boolean.parseBoolean(mapValues.get("taskStatusAddUpdate")))
                    tmsStatusUpdateButtonRow.setVisibility(View.VISIBLE);
                else
                    tmsStatusUpdateButtonRow.setVisibility(View.GONE);
                if (Boolean.parseBoolean(mapValues.get("taskDeleteButton")))
                    tmsDeleteButtonRow.setVisibility(View.VISIBLE);
                else
                    tmsDeleteButtonRow.setVisibility(View.GONE);
                if (Boolean.parseBoolean(mapValues.get("taskReopenButton")))
                    tmsReOpenButtonRow.setVisibility(View.VISIBLE);
                else
                    tmsReOpenButtonRow.setVisibility(View.GONE);
                if (mapValues.get("TimeExtension").equalsIgnoreCase("true")) {
                    if (mapValues.get("timeExtensionDisplay").equalsIgnoreCase("true")) {
                        tmsTimeApprovalExtensionRow.setVisibility(View.VISIBLE);
                        tmsTimeReqExtensionRow.setVisibility(View.GONE);
                    } else {
                        tmsTimeApprovalExtensionRow.setVisibility(View.GONE);
                        tmsTimeReqExtensionRow.setVisibility(View.VISIBLE);
                    }
                } else {
                    tmsTimeReqExtensionRow.setVisibility(View.GONE);
                    tmsTimeApprovalExtensionRow.setVisibility(View.GONE);
                }
                if (tmsStatus.getText().toString().equalsIgnoreCase("Hold"))
                    tmsHoldChatRow.setVisibility(View.VISIBLE);
                else
                    tmsHoldChatRow.setVisibility(View.GONE);
                actionDialog = new EasyDialog(context)
                        .setLayout(convertView)
                        .setBackgroundColor(context.getResources().getColor(R.color.white))
                        .setLocationByAttachedView(tmsActionDialog)
                        .setGravity(EasyDialog.GRAVITY_LEFT)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 1000, -600, 100, -50, 50, 0)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 350, 0, -800)
                        .setTouchOutsideDismiss(true)
                        .setMatchParent(false)
                        .setOutsideColor(context.getResources().getColor(R.color.transparent_black_bg))
                        .show();
                actionDialog.setOnEasyDialogDismissed(new EasyDialog.OnEasyDialogDismissed() {
                    @Override
                    public void onDismissed() {
                        actionDialog.dismiss();
                        actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                    }
                });
                tmsCloneButtonRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (actionDialog.getDialog().isShowing()) {
                            actionDialog.dismiss();
                            actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("TaskId", mapValues.get("TaskId"));
                        bundle.putString("Mode", "New");
                        bundle.putString("clone", "true");
                        listReload.onMoveEditPage(bundle);
                    }
                });
                tmsTimeApprovalExtensionRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Action Dialog Closed
                        if (actionDialog.getDialog().isShowing()) {
                            actionDialog.dismiss();
                            actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                        }
                        try {
                            JSONObject timeExtensionJson = new JSONObject(mapValues.get("TimeExtensionValues"));
                            timeExtensionApprovedDialog = new Dialog(context);
                            LayoutInflater inflater2 = LayoutInflater.from(context);
                            View convertView = inflater2.inflate(R.layout.tms_time_extension_approval_dialog, null);
                            timeExtensionApprovedDialog.setContentView(convertView);
                            timeExtensionApprovedDialog.setTitle("Time Extension");
                            final Button submit_button = (Button) timeExtensionApprovedDialog.findViewById(R.id.time_extension_submit_button);
                            Button cancel_button = (Button) timeExtensionApprovedDialog.findViewById(R.id.time_extension_cancel_button);
                            final SCMTextView actualStartDateText = (SCMTextView) timeExtensionApprovedDialog.findViewById(R.id.tms_actualStart_time);
                            final SCMTextView actualEndDateText = (SCMTextView) timeExtensionApprovedDialog.findViewById(R.id.tms_actualEnd_time);
                            final EditText commentText = (EditText) timeExtensionApprovedDialog.findViewById(R.id.tms_status_dialog_editText);
                            final EditText timeExtensionDateText = (EditText) timeExtensionApprovedDialog.findViewById(R.id.tms_timeExtension_date);
                            final EditText timeExtensionTimeText = (EditText) timeExtensionApprovedDialog.findViewById(R.id.tms_timeExtension_time);
                            final RadioGroup timeExtensionRadioGroup = (RadioGroup) timeExtensionApprovedDialog.findViewById(R.id.time_extension_radiogroup);
                            final RadioButton acceptedRadioButton = (RadioButton) timeExtensionApprovedDialog.findViewById(R.id.time_extension_accepted);
                            final RadioButton rejectedRadioButton = (RadioButton) timeExtensionApprovedDialog.findViewById(R.id.time_extension_rejected);
                            actualStartDateText.setText(timeExtensionJson.getString("ActualStartDate"));
                            actualEndDateText.setText(timeExtensionJson.getString("ActualEndDate"));
                            commentText.setText(timeExtensionJson.getString("Reason"));
                            if (timeExtensionJson.getString("StatusReject").equalsIgnoreCase("true")) {
                                rejectedRadioButton.setChecked(true);
                            } else if (timeExtensionJson.getString("StatusAccept").equalsIgnoreCase("true")) {
                                acceptedRadioButton.setChecked(true);
                            }
                            if (timeExtensionJson.getString("TimeExtension").length() > 0) {
                                String displayVal[] = timeExtensionJson.getString("TimeExtension").split(" ");
                                timeExtensionDateText.setText(displayVal[0]);
                                timeExtensionTimeText.setText(displayVal[1]);
                            }
                            if (timeExtensionJson.getString("Display").equalsIgnoreCase("false")) {
                                submit_button.setVisibility(View.VISIBLE);
                            } else
                                submit_button.setVisibility(View.GONE);
                            timeExtensionDateText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    editDateTextView = timeExtensionDateText;
                                    Log.d("Date Picker", "Running");
                                    final Calendar calendar = Calendar.getInstance();
                                    int yy = calendar.get(Calendar.YEAR);
                                    int mm = calendar.get(Calendar.MONTH);
                                    int dd = calendar.get(Calendar.DAY_OF_MONTH);
                                    Log.d("Date Picker", "Before Listener");
                                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, intimateOnDatePickerListener, yy, mm, dd);
                                    datePickerDialog.show();
                                    // new DatePickerDialog(context, intimateOnDatePickerListener, yy, mm, dd).show();
                                }
                            });
                            /*timeExtensionDateText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    *//*editDateTextView = timeExtensionDateText;
                                    DialogFragment newFragment = new SelectDateFragment();
                                    newFragment.show(cogetFragmentManager(), "DatePicker");*//*
                                }
                            });*/
                            timeExtensionTimeText.setOnClickListener(new View.OnClickListener() {
                                                                         @Override
                                                                         public void onClick(View v) {
                                                                             int mHour, mMinute;
                                                                             final Calendar c = Calendar.getInstance();
                                                                             if (!timeExtensionTimeText.getText().toString().isEmpty() && !timeExtensionTimeText.getText().toString().equalsIgnoreCase("-")) {
                                                                                 String timeHours[] = timeExtensionTimeText.getText().toString().split(":");
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
                                                                                             timeExtensionTimeText.setText(f.format(hourOfDay) + ":" + f.format(minute) + ":00");
                                                                                             timeExtensionTimeText.setError(null);
                                                                                         }
                                                                                     }, mHour, mMinute, true);
                                                                             timePickerDialog.show();
                                                                         }
                                                                     }
                            );
                            submit_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    hideKeyboard();
                                    int selectedId = timeExtensionRadioGroup.getCheckedRadioButtonId();
                                    if (timeExtensionDateText.getText().toString().trim().length() == 0) {
                                        YoYo.with(Techniques.Bounce)
                                                .duration(700)
                                                .playOn(timeExtensionDateText);
                                        YoYo.with(Techniques.Shake)
                                                .duration(700)
                                                .playOn(submit_button);
                                    } else if (timeExtensionTimeText.getText().toString().trim().length() == 0) {
                                        YoYo.with(Techniques.Bounce)
                                                .duration(700)
                                                .playOn(timeExtensionTimeText);
                                        YoYo.with(Techniques.Shake)
                                                .duration(700)
                                                .playOn(submit_button);
                                    } else if (selectedId == 0) {
                                        YoYo.with(Techniques.Bounce)
                                                .duration(700)
                                                .playOn(timeExtensionRadioGroup);
                                        YoYo.with(Techniques.Shake)
                                                .duration(700)
                                                .playOn(submit_button);
                                    } else if (commentText.getText().toString().trim().length() == 0) {
                                        YoYo.with(Techniques.Bounce)
                                                .duration(700)
                                                .playOn(commentText);
                                        YoYo.with(Techniques.Shake)
                                                .duration(700)
                                                .playOn(submit_button);
                                    } else {
                                        RadioButton radioButton = (RadioButton) timeExtensionApprovedDialog.findViewById(selectedId);
                                        String status = radioButton.getText().toString();
                                        requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TIME_EXTENSION_UPDATE','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + mapValues.get("TaskId") + "', 'reason':'" + commentText.getText().toString().trim() + "', 'timeextentionId':'" + mapValues.get("TimeExtensionId") + "', 'taskApprovarId':'" + mapValues.get("Owner") + "', 'taskactualstartdate':'" + actualStartDateText.getText().toString() + "', 'taskactualenddate':'" + actualEndDateText.getText().toString() + "', 'timeextension_date':'" + timeExtensionDateText.getText().toString() + " " + timeExtensionTimeText.getText().toString() + "', 'status':'" + status.toLowerCase() + "'}";
                                        System.out.println(requestParameter);
                                        getLoadData(requestParameter, "TimeExtensionApprovalSubmit");
                                    }
                                }
                            });
                            cancel_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    hideKeyboard();
                                    timeExtensionApprovedDialog.cancel();
                                }
                            });
                            timeExtensionApprovedDialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                tmsTimeReqExtensionRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Action Dialog Closed
                        if (actionDialog.getDialog().isShowing()) {
                            actionDialog.dismiss();
                            actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                        }
                        requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TIME_EXTENSION','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + mapValues.get("TaskId") + "', 'timeextentionId':'" + mapValues.get("TimeExtensionId") + "'}";
                        System.out.println(requestParameter);
                        getLoadData(requestParameter, "TimeExtension");
                    }
                });
                tmsReAssignButtonRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Action Dialog Closed
                        if (actionDialog.getDialog().isShowing()) {
                            actionDialog.dismiss();
                            actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                        }
                        requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_REASSIGN','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + mapValues.get("TaskId") + "', 'key':'reAssign'}";
                        System.out.println(requestParameter);
                        getLoadData(requestParameter, "ReAssign");
                    }
                });
                tmsDeleteButtonRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Action Dialog Closed
                        if (actionDialog.getDialog().isShowing()) {
                            actionDialog.dismiss();
                            actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                        }
                        requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_DELETE_ALERT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + mapValues.get("TaskId") + "'}";
                        System.out.println(requestParameter);
                        getLoadData(requestParameter, "DeleteAlert");
                    }
                });
                tmsStatusUpdateButtonRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Action Dialog Closed
                        if (actionDialog.getDialog().isShowing()) {
                            actionDialog.dismiss();
                            actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                        }
                        requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_STATUS','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + mapValues.get("TaskId") + "'}";
                        System.out.println(requestParameter);
                        getLoadData(requestParameter, "StatusUpdate");
                    }
                });
                tmsTaskDisConButtonRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Action Dialog Closed
                        if (actionDialog.getDialog().isShowing()) {
                            actionDialog.dismiss();
                            actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                        }
                        requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_DISCONTINUE','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + mapValues.get("TaskId") + "', 'key':'TaskDiscontinue'}";
                        System.out.println(requestParameter);
                        getLoadData(requestParameter, "TaskDiscontinue");
                    }
                });
                tmsReSendButtonRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Action Dialog Closed
                        if (actionDialog.getDialog().isShowing()) {
                            actionDialog.dismiss();
                            actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                        }
                        requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'RATING_ALERT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + mapValues.get("TaskId") + "', 'table':'progressChange', 'status':'AssignResend'}";
                        System.out.println(requestParameter);
                        getLoadData(requestParameter, "RATING_ALERT");
                    }
                });
                tmsRatingReqButtonRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Action Dialog Closed
                        if (actionDialog.getDialog().isShowing()) {
                            actionDialog.dismiss();
                            actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                        }
                        if (mapValues.get("taskRatingButton").equalsIgnoreCase("Orange")) {
                            requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'RATING_ALERT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + mapValues.get("TaskId") + "', 'table':'progressChange', 'status':'ReqRating'}";
                            System.out.println(requestParameter);
                            getLoadData(requestParameter, "RATING_ALERT");
                        } else if (mapValues.get("taskRatingButton").equalsIgnoreCase("Rated")) {
                      /*  ratedDialog = new Dialog(context);
                        LayoutInflater inflater2 = getLayoutInflater(null);
                        View convertView = inflater2.inflate(R.layout.tms_rated_dialog, null);
                        ratedDialog.setContentView(convertView);
                        ratedDialog.setTitle("Task Rated");
                        RatingBar ratingBar = (RatingBar) ratedDialog.findViewById(R.id.tms_ratingBar);
                        TextView displayText = (TextView) ratedDialog.findViewById(R.id.tms_ratedText);
                        ratingBar.setRating(Integer.parseInt(mapValues.get("Ratescore")));
                        //  ratingBar.setProgressDrawable(getResources().getDrawable(R.drawable.color_border));
                        displayText.setText(mapValues.get("Ratewords"));
                        ratedDialog.show();*/
                            LayoutInflater inflater2 = LayoutInflater.from(context);
                            View convertView = inflater2.inflate(R.layout.tms_rated_dialog, null);
                            RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.tms_ratingBar);
                            TextView displayText = (TextView) convertView.findViewById(R.id.tms_ratedText);
                            ratingBar.setRating(Integer.parseInt(mapValues.get("Ratescore")));
                            displayText.setText(mapValues.get("Ratewords"));
                            new EasyDialog(context)
//                        .setLayoutResourceId(R.layout.layout_tip_content_horizontal)//layout resource id
                                    .setLayout(convertView)
                                    .setBackgroundColor(context.getResources().getColor(R.color.white))
//                        .setLocation(new location[])//point in screen
                                    .setLocationByAttachedView(tmsRatingReqButtonRow)
                                    .setGravity(EasyDialog.GRAVITY_TOP)
                                    .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 300, 400, 0)
                                    .setAnimationTranslationShow(EasyDialog.DIRECTION_Y, 300, 400, 0)
                                    .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 300, 0, 400)
                                    .setAnimationTranslationDismiss(EasyDialog.DIRECTION_Y, 300, 0, 400)
                                    .setTouchOutsideDismiss(true)
                                    .setMatchParent(false)
                                    .setMarginLeftAndRight(24, 24)
                                    .setOutsideColor(context.getResources().getColor(R.color.transparent_black_bg))
                                    .show();
                        } else if (mapValues.get("taskRatingButton").equalsIgnoreCase("Green")) {
                            ratingRequestDialog = new Dialog(context);
                            LayoutInflater inflater2 = LayoutInflater.from(context);
                            View convertView = inflater2.inflate(R.layout.tms_rating_dialog, null);
                            ratingRequestDialog.setContentView(convertView);
                            ratingRequestDialog.setCancelable(false);
                            ratingRequestDialog.setTitle("Task Rating");
                            final Button yes_button = (Button) ratingRequestDialog.findViewById(R.id.rating_yes_button);
                            Button no_button = (Button) ratingRequestDialog.findViewById(R.id.rating_no_button);
                            final RatingBar ratingBar = (RatingBar) ratingRequestDialog.findViewById(R.id.tms_ratingBar);
                            final EditText commentText = (EditText) ratingRequestDialog.findViewById(R.id.tms_rating_dialog_editText);
                            yes_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    hideKeyboard();
                                    int ratingValue = (int) (ratingBar.getRating());
                                    if (ratingValue > 0) {
                                        requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'RATING_SUBMIT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + mapValues.get("TaskId") + "', 'tmsfeedBack':'" + commentText.getText().toString().trim() + "','themeRating':'" + ratingValue + "','actionKey':'rating','submit_btn':''}";
                                        System.out.println(requestParameter);
                                        getLoadData(requestParameter, "TaskRatingGreenSubmit");
                                    } else {
                                        YoYo.with(Techniques.Bounce)
                                                .duration(700)
                                                .playOn(ratingBar);
                                        YoYo.with(Techniques.Shake)
                                                .duration(700)
                                                .playOn(yes_button);
                                    }
                                }
                            });
                            no_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    hideKeyboard();
                                    ratingRequestDialog.cancel();
                                }
                            });
                            ratingRequestDialog.show();
                        }
                    }
                });
                tmsReOpenButtonRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Action Dialog Closed
                        if (actionDialog.getDialog().isShowing()) {
                            actionDialog.dismiss();
                            actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                        }
                        requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'RATING_ALERT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + mapValues.get("TaskId") + "', 'table':'progressChange', 'status':'TaskReopen'}";
                        System.out.println(requestParameter);
                        getLoadData(requestParameter, "RATING_ALERT");
                    }
                });
            }
        });
        tmsDeclineRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                declineSelectDialog = new Dialog(context);
                LayoutInflater inflater2 = LayoutInflater.from(context);
                View convertView = inflater2.inflate(R.layout.tms_decline_assigned, null);
                declineSelectDialog.setContentView(convertView);
                declineSelectDialog.setCancelable(false);
                final Button submit_button = (Button) declineSelectDialog.findViewById(R.id.decline_save_button);
                Button cancel_button = (Button) declineSelectDialog.findViewById(R.id.decline_cancel_button);
                final EditText reasonText = (EditText) declineSelectDialog.findViewById(R.id.tms_reason_dialog_editText);
                reasonText.setText(mapValues.get("declineReason"));
                declineSelectDialog.setTitle("Assigned By Decline");
                submit_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyboard();
                        if (reasonText.getText().toString().trim().length() > 0) {
                            mapValues.put("declineReason", reasonText.getText().toString().trim());
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("action", "0");
                            map.put("reason", reasonText.getText().toString().trim());
                            tmsMultiSelect.setChecked(false);
                            mapValues.put("isCheckboxSelected", "false");
                            tmsMultiSelect.setEnabled(false);
                            mapValues.put("isDeclineRedbox", "true");
                            tmsDeclineRed.setVisibility(View.VISIBLE);
                            tmsMutiDecline.setVisibility(View.GONE);
                            TMSListTreeActivity.selectedCheckTaskIdWithReason.put(mapValues.get("TaskId"), map);
                            declineSelectDialog.cancel();
                            isSubmitDisplay();
                        } else {
                            YoYo.with(Techniques.Bounce)
                                    .duration(700)
                                    .playOn(reasonText);
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .playOn(submit_button);
                        }
                    }
                });
                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyboard();
                        declineSelectDialog.cancel();
                        TMSListTreeActivity.selectedCheckTaskIdWithReason.remove(mapValues.get("TaskId"));
                        mapValues.put("declineReason", "");
                        tmsMutiDecline.setChecked(false);
                        tmsMultiSelect.setEnabled(true);
                        tmsDeclineRed.setVisibility(View.GONE);
                        mapValues.put("isDeclineRedbox", "false");
                        tmsMutiDecline.setVisibility(View.VISIBLE);
                        isSubmitDisplay();
                    }
                });
                declineSelectDialog.show();
            }
        });
        tmsMutiDecline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if (check) {
                    declineSelectDialog = new Dialog(context);
                    LayoutInflater inflater2 = LayoutInflater.from(context);
                    View convertView = inflater2.inflate(R.layout.tms_decline_assigned, null);
                    declineSelectDialog.setContentView(convertView);
                    declineSelectDialog.setCancelable(false);
                    final Button submit_button = (Button) declineSelectDialog.findViewById(R.id.decline_save_button);
                    Button cancel_button = (Button) declineSelectDialog.findViewById(R.id.decline_cancel_button);
                    final EditText reasonText = (EditText) declineSelectDialog.findViewById(R.id.tms_reason_dialog_editText);
                    reasonText.setText(mapValues.get("declineReason"));
                    declineSelectDialog.setTitle("Assigned By Decline");
                    submit_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideKeyboard();
                            if (reasonText.getText().toString().trim().length() > 0) {
                                mapValues.put("declineReason", reasonText.getText().toString().trim());
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("action", "0");
                                map.put("reason", reasonText.getText().toString().trim());
                                tmsMutiDecline.setChecked(true);
                                mapValues.put("isCheckboxSelected", "false");
                                tmsMultiSelect.setChecked(false);
                                tmsMultiSelect.setEnabled(false);
                                tmsDeclineRed.setVisibility(View.VISIBLE);
                                mapValues.put("isDeclineRedbox", "true");
                                tmsMutiDecline.setVisibility(View.GONE);
                                TMSListTreeActivity.selectedCheckTaskIdWithReason.put(mapValues.get("TaskId"), map);
                                declineSelectDialog.cancel();
                                isSubmitDisplay();
                            } else {
                                YoYo.with(Techniques.Bounce)
                                        .duration(700)
                                        .playOn(reasonText);
                                YoYo.with(Techniques.Shake)
                                        .duration(700)
                                        .playOn(submit_button);
                            }
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideKeyboard();
                            declineSelectDialog.cancel();
                            tmsMutiDecline.setChecked(false);
                            tmsMultiSelect.setEnabled(true);
                            mapValues.put("declineReason", "");
                            mapValues.put("isDeclineRedbox", "false");
                            tmsMutiDecline.setChecked(false);
                            tmsMultiSelect.setEnabled(true);
                            isSubmitDisplay();
                        }
                    });
                    declineSelectDialog.show();
                } else {
                    tmsMultiSelect.setEnabled(true);
                    isSubmitDisplay();
                }
            }
        });
        tmsMultiSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if (check) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("action", "1");
                    map.put("reason", "");
                    TMSListTreeActivity.selectedCheckTaskIdWithReason.put(mapValues.get("TaskId"), map);
                    mapValues.put("isCheckboxSelected", "true");
                    isSubmitDisplay();
                } else {
                    TMSListTreeActivity.selectedCheckTaskIdWithReason.remove(mapValues.get("TaskId"));
                    mapValues.put("isCheckboxSelected", "false");
                    isSubmitDisplay();
                }
            }
        });
        tmsBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bookMarkStatus;
                if (tmsBooking.getTag().equals("true")) {
                    bookMarkStatus = 1;
                    bookMarkStatusValue = "false";
                } else {
                    bookMarkStatus = 0;
                    bookMarkStatusValue = "true";
                }
                    /*if (mapValues.get("BookMarked").equalsIgnoreCase("true")) {
                        bookMarkStatus = 1;
                        bookMarkStatusValue = "false";
                    } else {
                        bookMarkStatus = 0;
                        bookMarkStatusValue = "true";
                    }*/
                requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'BOOKMARK_PROCESS','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'taskStatus':'" + bookMarkStatus + "', 'taskId':'" + mapValues.get("TaskId") + "'}";
                System.out.println(requestParameter);
                getLoadData(requestParameter, "BookMark");
            }
        });
        tmsHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holdDialog = new Dialog(context);
                holdDialog.getWindow().getAttributes().windowAnimations = R.style.in_out_animationdialog;
                LayoutInflater inflater2 = LayoutInflater.from(context);
                View convertView = inflater2.inflate(R.layout.tms_hold_dialog, null);
                holdDialog.setContentView(convertView);
                final Button yes_button = (Button) holdDialog.findViewById(R.id.hold_yes_button);
                Button no_button = (Button) holdDialog.findViewById(R.id.hold_no_button);
                final EditText reasonText = (EditText) holdDialog.findViewById(R.id.tms_reason_dialog_editText);
                holdDialog.setTitle("Hold Reason");
                yes_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyboard();
                        if (reasonText.getText().toString().trim().length() > 0) {
                            requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_PROGRESS_HOLD_SUBMIT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'taskId':'" + mapValues.get("TaskId") + "', 'reason':'" + reasonText.getText().toString().trim() + "'}";
                            System.out.println(requestParameter);
                            getLoadData(requestParameter, "ListReLoad");
                            holdDialog.cancel();
                        } else {
                            YoYo.with(Techniques.Bounce)
                                    .duration(700)
                                    .playOn(reasonText);
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .playOn(yes_button);
                        }
                    }
                });
                no_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyboard();
                        holdDialog.cancel();
                    }
                });
                holdDialog.show();
            }
        });
        tmsComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_PROGRESS_OTHER_SUBMIT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'taskId':'" + mapValues.get("TaskId") + "', 'type':'Completed'}";
                System.out.println(requestParameter);
                getLoadData(requestParameter, "ListReLoad");
            }
        });
        tmsPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_PROGRESS_OTHER_SUBMIT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'taskId':'" + mapValues.get("TaskId") + "', 'type':'InProgress'}";
                System.out.println(requestParameter);
                getLoadData(requestParameter, "ListReLoad");
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
                if (flag.equalsIgnoreCase("BookMark")) {
                    tmsBooking.setTag(bookMarkStatusValue);
                    if (bookMarkStatusValue.equalsIgnoreCase("true"))
                        tmsBooking.setImageResource(R.drawable.bookmark_select);
                    else
                        tmsBooking.setImageResource(R.drawable.bookmark_unselect);
                } else if (flag.equalsIgnoreCase("ReAssign")) {
                    final Boolean[] isCompanyLoad = {false};
                    String selfCheck = response.getString("SelfAssignee");
                    String taskId = response.getString("TaskId");
                    reassignDialog = new Dialog(context);
                    LayoutInflater inflater2 = LayoutInflater.from(context);
                    View convertView = inflater2.inflate(R.layout.tms_reassign_dialog, null);
                    reassignDialog.setContentView(convertView);
                    Button submit_button = (Button) reassignDialog.findViewById(R.id.reassign_submit_button);
                    Button cancel_button = (Button) reassignDialog.findViewById(R.id.reassign_cancel_button);
                    companySpinner = (Spinner) reassignDialog.findViewById(R.id.tms_company_spinner);
                    departmentSpinner = (Spinner) reassignDialog.findViewById(R.id.tms_department_spinner);
                    wingSpinner = (Spinner) reassignDialog.findViewById(R.id.tms_wing_spinner);
                    designationSpinner = (Spinner) reassignDialog.findViewById(R.id.tms_designation_spinner);
                    individualSpinner = (Spinner) reassignDialog.findViewById(R.id.tms_individual_spinner);
                    selfAssignCheck = (CheckBox) reassignDialog.findViewById(R.id.tms_selfassign_checkbox);
                    reassignDialog.setTitle("Reassign To");
                    companyArrayList = ApiCalls.getArraylistfromJson(response.getString("Company"));
                    loadLeadStyleSpinner(companySpinner, companyArrayList, new String[]{"value"}, "Company");
                    for (int i = 0; i < companyArrayList.size(); i++) {
                        if (companyArrayList.get(i).get("selected").equalsIgnoreCase("true"))
                            companySpinner.setSelection(i + 1);
                    }
                    departmentArrayList = ApiCalls.getArraylistfromJson(response.getString("Department"));
                    loadLeadStyleSpinner(departmentSpinner, departmentArrayList, new String[]{"value"}, "Department");
                    for (int i = 0; i < departmentArrayList.size(); i++) {
                        if (departmentArrayList.get(i).get("selected").equalsIgnoreCase("true"))
                            departmentSpinner.setSelection(i + 1);
                    }
                    wingArrayList = ApiCalls.getArraylistfromJson(response.getString("Wing"));
                    loadLeadStyleSpinner(wingSpinner, wingArrayList, new String[]{"value"}, "Wing");
                    for (int i = 0; i < wingArrayList.size(); i++) {
                        if (wingArrayList.get(i).get("selected").equalsIgnoreCase("true"))
                            wingSpinner.setSelection(i + 1);
                    }
                    designationArrayList = ApiCalls.getArraylistfromJson(response.getString("Designation"));
                    loadLeadStyleSpinner(designationSpinner, designationArrayList, new String[]{"value"}, "Designation");
                    for (int i = 0; i < designationArrayList.size(); i++) {
                        if (designationArrayList.get(i).get("selected").equalsIgnoreCase("true"))
                            designationSpinner.setSelection(i + 1);
                    }
                    IndividualArrayList = ApiCalls.getArraylistfromJson(response.getString("Individual"));
                    loadLeadStyleSpinner(individualSpinner, IndividualArrayList, new String[]{"value"}, "Individual");
                    for (int i = 0; i < IndividualArrayList.size(); i++) {
                        if (IndividualArrayList.get(i).get("selected").equalsIgnoreCase("true"))
                            individualSpinner.setSelection(i + 1);
                    }
                    if (selfCheck.equalsIgnoreCase("1")) {
                        selfAssignCheck.setChecked(true);
                        companySpinner.setEnabled(false);
                        departmentSpinner.setEnabled(false);
                        wingSpinner.setEnabled(false);
                        designationSpinner.setEnabled(false);
                        individualSpinner.setEnabled(false);
                        companySpinner.setSelection(0);
                        departmentSpinner.setSelection(0);
                        individualSpinner.setSelection(0);
                        /*TextView errordepartment = (TextView) departmentSpinner.getSelectedView();
                        errordepartment.setError(null);
                        TextView errorindividual = (TextView) individualSpinner.getSelectedView();
                        errorindividual.setError(null);*/
                        ArrayList<HashMap<String, String>> emptyArrayList = new ArrayList<HashMap<String, String>>();
                        loadLeadStyleSpinner(wingSpinner, emptyArrayList, new String[]{"value"}, "Wing");
                        loadLeadStyleSpinner(designationSpinner, emptyArrayList, new String[]{"value"}, "Designation");
                    } else {
                        selfAssignCheck.setChecked(false);
                    }
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
                                isCompanyLoad[0] =true;
                            }
                        }
                    });
                    companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position > 0) {
                                if(isCompanyLoad[0]) {
                                    int selectSpinnerPosition = (companySpinner.getSelectedItemPosition()) - 1;
                                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'department','comp_id':'" + companyArrayList.get(selectSpinnerPosition).get("id") + "','dep_id':'','wing_id':'','des_id':'','indiv_id':''}";
                                    System.out.println(requestParameter);
                                    getLoadData(requestParameter, "Department");
                                }
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
                                if(isCompanyLoad[0]) {
                                    int selectSpinnerPosition = (departmentSpinner.getSelectedItemPosition()) - 1;
                                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'wing','comp_id':'" + companyArrayList.get(selectSpinnerPosition).get("id") + "','dep_id':'" + departmentArrayList.get(selectSpinnerPosition).get("id") + "','wing_id':'','des_id':'','indiv_id':''}";
                                    System.out.println(requestParameter);
                                    getLoadData(requestParameter, "Wing");
                                }
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
                                if(isCompanyLoad[0]) {
                                    int selectSpinnerPosition = (wingSpinner.getSelectedItemPosition()) - 1;
                                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'designation','comp_id':'" + companyArrayList.get(selectSpinnerPosition).get("id") + "','dep_id':'" + departmentArrayList.get(departmentSpinner.getSelectedItemPosition() - 1).get("id") + "','wing_id':'" + wingArrayList.get(selectSpinnerPosition).get("id") + "','des_id':'','indiv_id':''}";
                                    System.out.println(requestParameter);
                                    getLoadData(requestParameter, "Designation");
                                }
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
                                if(isCompanyLoad[0]) {
                                    int selectSpinnerPosition = (wingSpinner.getSelectedItemPosition()) - 1;
                                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'individual','comp_id':'" + companyArrayList.get(selectSpinnerPosition).get("id") + "','dep_id':'" + departmentArrayList.get(departmentSpinner.getSelectedItemPosition() - 1).get("id") + "','wing_id':'" + wingArrayList.get(wingSpinner.getSelectedItemPosition() - 1).get("id") + "','des_id':'" + designationArrayList.get(selectSpinnerPosition).get("id") + "','indiv_id':''}";
                                    System.out.println(requestParameter);
                                    getLoadData(requestParameter, "Individual");
                                }
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    individualSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position > 0) {
                                isCompanyLoad[0] =true;
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    final String finalTaskId = taskId;
                    submit_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideKeyboard();
                            try {
                                int errorCount = 0;
                                JSONObject submitReassign = new JSONObject();
                                submitReassign.put("key", "");
                                if (selfAssignCheck.isChecked()) {
                                    submitReassign.put("company", "");
                                    submitReassign.put("department", "");
                                    submitReassign.put("wing", "");
                                    submitReassign.put("designation", "");
                                    submitReassign.put("individual", "");
                                    submitReassign.put("selfAssignee", "1");
                                } else {
                                    if ((companySpinner.getSelectedItemPosition() - 1) >= 0) {
                                        submitReassign.put("company", companyArrayList.get(companySpinner.getSelectedItemPosition() - 1).get("id"));
                                    } else {
                                        TextView errorcompany = (TextView) companySpinner.getSelectedView();
                                        errorcompany.setError("Field is Required");
                                        errorCount++;
                                    }
                                    if ((departmentSpinner.getSelectedItemPosition() - 1) >= 0) {
                                        submitReassign.put("department", departmentArrayList.get(departmentSpinner.getSelectedItemPosition() - 1).get("id"));
                                    } else {
                                        TextView errordepartment = (TextView) departmentSpinner.getSelectedView();
                                        errordepartment.setError("Field is Required");
                                        errorCount++;
                                    }
                                    if ((wingSpinner.getSelectedItemPosition() - 1) >= 0) {
                                        submitReassign.put("wing", wingArrayList.get(wingSpinner.getSelectedItemPosition() - 1).get("id"));
                                    } else {
                                        TextView errordepartment = (TextView) wingSpinner.getSelectedView();
                                        errordepartment.setError("Field is Required");
                                        errorCount++;
                                    }
                                    if ((designationSpinner.getSelectedItemPosition() - 1) >= 0) {
                                        submitReassign.put("designation", designationArrayList.get(designationSpinner.getSelectedItemPosition() - 1).get("id"));
                                    } else {
                                        TextView errordepartment = (TextView) designationSpinner.getSelectedView();
                                        errordepartment.setError("Field is Required");
                                        errorCount++;
                                    }
                                    if ((individualSpinner.getSelectedItemPosition() - 1) >= 0) {
                                        submitReassign.put("individual", IndividualArrayList.get(individualSpinner.getSelectedItemPosition() - 1).get("id"));
                                    } else {
                                        TextView errordepartment = (TextView) individualSpinner.getSelectedView();
                                        errordepartment.setError("Field is Required");
                                        errorCount++;
                                    }
                                    submitReassign.put("selfAssignee", "0");
                                }
                                if (errorCount == 0) {
                                    submitReassign.put("Action", "TASK_MANAGEMENT_SYSTEM");
                                    submitReassign.put("submode", "TASK_REASSIGN");
                                    submitReassign.put("TaskId", finalTaskId);
                                    submitReassign.put("UID", uid);
                                    submitReassign.put("Cre_Id", Cre_Id);
                                    requestParameter = submitReassign.toString();
                                    System.out.println(requestParameter);
                                    getLoadData(requestParameter, "ReAssignSubmit");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideKeyboard();
                            reassignDialog.cancel();
                        }
                    });
                    reassignDialog.show();
                } else if (flag.equalsIgnoreCase("StatusUpdate")) {
                    String taskNameValue = response.getString("TaskName");
                    String tastCodeValue = response.getString("TaskCode");
                    final String taskId = response.getString("TaskId");
                    statusUpdateDialog = new Dialog(context);
                    LayoutInflater inflater2 = LayoutInflater.from(context);
                    View convertView = inflater2.inflate(R.layout.tms_status_update_dialog, null);
                    statusUpdateDialog.setContentView(convertView);
                    statusUpdateDialog.setTitle("Status Update");
                    final Button yes_button = (Button) statusUpdateDialog.findViewById(R.id.status_yes_button);
                    Button no_button = (Button) statusUpdateDialog.findViewById(R.id.status_no_button);
                    SCMTextView taskNameText = (SCMTextView) statusUpdateDialog.findViewById(R.id.tms_task_name);
                    SCMTextView taskCodeText = (SCMTextView) statusUpdateDialog.findViewById(R.id.tms_task_code);
                    final EditText commentText = (EditText) statusUpdateDialog.findViewById(R.id.tms_status_dialog_editText);
                    taskNameText.setText(taskNameValue);
                    taskCodeText.setText(tastCodeValue);
                    yes_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideKeyboard();
                            if (commentText.getText().toString().trim().length() > 0) {
                                requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_STATUS_UPDATE','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + taskId + "', 'Comments':'" + commentText.getText().toString().trim() + "'}";
                                System.out.println(requestParameter);
                                getLoadData(requestParameter, "StatusSubmit");
                            } else {
                                YoYo.with(Techniques.Bounce)
                                        .duration(700)
                                        .playOn(commentText);
                                YoYo.with(Techniques.Shake)
                                        .duration(700)
                                        .playOn(yes_button);
                            }
                        }
                    });
                    no_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideKeyboard();
                            statusUpdateDialog.cancel();
                        }
                    });
                    statusUpdateDialog.show();
                } else if (flag.equalsIgnoreCase("TimeExtension")) {
                    String taskNameValue = response.getString("TaskName");
                    String tastCodeValue = response.getString("TaskCode");
                    final String actualStartDate = response.getString("ActualStartDate");
                    final String actualEndDate = response.getString("ActualEndDate");
                    String condition = response.getString("condtion");
                    String alert = response.getString("msg");
                    String TimeExtension = response.getString("TimeExtension");
                    String Reason = response.getString("Reason");
                    final String taskId = response.getString("TaskId");
                    final String TaskApprovedId = response.getString("TaskApprovedId");
                    final String TimeextentionId = response.getString("TimeextentionId");
                    timeExtensionDialog = new Dialog(context);
                    LayoutInflater inflater2 = LayoutInflater.from(context);
                    View convertView = inflater2.inflate(R.layout.tms_time_extension_dialog, null);
                    timeExtensionDialog.setContentView(convertView);
                    timeExtensionDialog.setTitle("Time Extension");
                    final Button submit_button = (Button) timeExtensionDialog.findViewById(R.id.time_extension_submit_button);
                    Button cancel_button = (Button) timeExtensionDialog.findViewById(R.id.time_extension_cancel_button);
                    SCMTextView taskNameText = (SCMTextView) timeExtensionDialog.findViewById(R.id.tms_task_name);
                    SCMTextView taskCodeText = (SCMTextView) timeExtensionDialog.findViewById(R.id.tms_task_code);
                    SCMTextView actualStartDateText = (SCMTextView) timeExtensionDialog.findViewById(R.id.tms_actualStart_time);
                    SCMTextView actualEndDateText = (SCMTextView) timeExtensionDialog.findViewById(R.id.tms_actualEnd_time);
                    SCMTextView alertText = (SCMTextView) timeExtensionDialog.findViewById(R.id.alert);
                    final EditText commentText = (EditText) timeExtensionDialog.findViewById(R.id.tms_status_dialog_editText);
                    final EditText timeExtensionDateText = (EditText) timeExtensionDialog.findViewById(R.id.tms_timeExtension_date);
                    final EditText timeExtensionTimeText = (EditText) timeExtensionDialog.findViewById(R.id.tms_timeExtension_time);
                    taskNameText.setText(taskNameValue);
                    taskCodeText.setText(tastCodeValue);
                    actualStartDateText.setText(actualStartDate);
                    actualEndDateText.setText(actualEndDate);
                    commentText.setText(Reason);
                    if (TimeExtension.length() > 0) {
                        String displayVal[] = TimeExtension.split(" ");
                        timeExtensionDateText.setText(displayVal[0]);
                        timeExtensionTimeText.setText(displayVal[1]);
                    }
                    if (alert.length() > 0) {
                        alertText.setText(alert);
                        alertText.setVisibility(View.VISIBLE);
                    } else
                        alertText.setVisibility(View.GONE);
                    if (condition.equalsIgnoreCase("")) {
                        submit_button.setVisibility(View.VISIBLE);
                    } else
                        submit_button.setVisibility(View.GONE);
                    timeExtensionDateText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editDateTextView = timeExtensionDateText;
                            Log.d("Date Picker", "Running");
                            final Calendar calendar = Calendar.getInstance();
                            int yy = calendar.get(Calendar.YEAR);
                            int mm = calendar.get(Calendar.MONTH);
                            int dd = calendar.get(Calendar.DAY_OF_MONTH);
                            Log.d("Date Picker", "Before Listener");
                            DatePickerDialog datePickerDialog = new DatePickerDialog(context, intimateOnDatePickerListener, yy, mm, dd);
                            datePickerDialog.show();
                            // new DatePickerDialog(context, intimateOnDatePickerListener, yy, mm, dd).show();
                        }
                    });
                    timeExtensionTimeText.setOnClickListener(new View.OnClickListener() {
                                                                 @Override
                                                                 public void onClick(View v) {
                                                                     int mHour, mMinute;
                                                                     final Calendar c = Calendar.getInstance();
                                                                     if (!timeExtensionTimeText.getText().toString().isEmpty() && !timeExtensionTimeText.getText().toString().equalsIgnoreCase("-")) {
                                                                         String timeHours[] = timeExtensionTimeText.getText().toString().split(":");
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
                                                                                     timeExtensionTimeText.setText(f.format(hourOfDay) + ":" + f.format(minute) + ":00");
                                                                                     timeExtensionTimeText.setError(null);
                                                                                 }
                                                                             }, mHour, mMinute, true);
                                                                     timePickerDialog.show();
                                                                 }
                                                             }
                    );
                    submit_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideKeyboard();
                            if (timeExtensionDateText.getText().toString().trim().length() == 0) {
                                YoYo.with(Techniques.Bounce)
                                        .duration(700)
                                        .playOn(timeExtensionDateText);
                                YoYo.with(Techniques.Shake)
                                        .duration(700)
                                        .playOn(submit_button);
                            } else if (timeExtensionTimeText.getText().toString().trim().length() == 0) {
                                YoYo.with(Techniques.Bounce)
                                        .duration(700)
                                        .playOn(timeExtensionTimeText);
                                YoYo.with(Techniques.Shake)
                                        .duration(700)
                                        .playOn(submit_button);
                            } else if (commentText.getText().toString().trim().length() == 0) {
                                YoYo.with(Techniques.Bounce)
                                        .duration(700)
                                        .playOn(commentText);
                                YoYo.with(Techniques.Shake)
                                        .duration(700)
                                        .playOn(submit_button);
                            } else {
                                requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TIME_EXTENSION_UPDATE','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + taskId + "', 'reason':'" + commentText.getText().toString().trim() + "', 'timeextentionId':'" + TimeextentionId + "', 'taskApprovarId':'" + TaskApprovedId + "', 'taskactualstartdate':'" + actualStartDate + "', 'taskactualenddate':'" + actualEndDate + "', 'timeextension_date':'" + timeExtensionDateText.getText().toString() + " " + timeExtensionTimeText.getText().toString() + "'}";
                                System.out.println(requestParameter);
                                getLoadData(requestParameter, "TimeExtensionSubmit");
                            }
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideKeyboard();
                            timeExtensionDialog.cancel();
                        }
                    });
                    timeExtensionDialog.show();
                } else if (flag.equalsIgnoreCase("ASSIGN")) {
                    if (actionDialog != null)
                        if (actionDialog.getDialog().isShowing()) {
                            actionDialog.dismiss();
                            actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                        }
                    listReload.onReloadList("List Reload");
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                } else if (flag.equalsIgnoreCase("TimeExtensionSubmit")) {
                    timeExtensionDialog.cancel();
                    if (actionDialog != null)
                        if (actionDialog.getDialog().isShowing()) {
                            actionDialog.dismiss();
                            actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                        }
                    listReload.onReloadList("List Reload");
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                } else if (flag.equalsIgnoreCase("TimeExtensionApprovalSubmit")) {
                    if (actionDialog != null)
                        if (actionDialog.getDialog().isShowing()) {
                            actionDialog.dismiss();
                            actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                        }
                    listReload.onReloadList("List Reload");
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                } else if (flag.equalsIgnoreCase("TaskRatingGreenSubmit")) {
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                    ratingRequestDialog.cancel();
                    if (actionDialog != null)
                        if (actionDialog.getDialog().isShowing()) {
                            actionDialog.dismiss();
                            actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                        }
                    listReload.onReloadList("List Reload");
                } else if (flag.equalsIgnoreCase("StatusSubmit")) {
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                    statusUpdateDialog.cancel();
                } else if (flag.equalsIgnoreCase("TaskDiscontinue")) {
                    showTaskDisAlertDialog(response.getString(AppContants.RESPONSE_MESSAGE), response.getString("TaskId"));
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
                } else if (flag.equalsIgnoreCase("ReAssignSubmit")) {
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                    reassignDialog.cancel();
                } else if (flag.equalsIgnoreCase("DeleteAlert")) {
                    showDeleteAlertDialog(response.getString(AppContants.RESPONSE_MESSAGE), response.getString("TaskId"), "1");
                } else if (flag.equalsIgnoreCase("RATING_ALERT")) {
                    showRatingRequestAlertDialog("Status", response.getString(AppContants.RESPONSE_MESSAGE));
                } else if (flag.equalsIgnoreCase("ListReLoad")) {
                    listReload.onReloadList(response.getString(AppContants.RESPONSE_MESSAGE));
                    if (actionDialog != null)
                        if (actionDialog.getDialog().isShowing()) {
                            actionDialog.dismiss();
                            actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                        }
                } else if (flag.equalsIgnoreCase("LITIGATION")) {
                    Intent intent = new Intent(context, TMSLitigationTab.class);
                    intent.putExtra("response", response.toString());
                    context.startActivity(intent);
                } else {
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                }
            } else if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase("0")) {
                if (flag.equalsIgnoreCase("DeleteAlert")) {
                    showDeleteAlertDialog(response.getString(AppContants.RESPONSE_MESSAGE), "0", "0");
                } else
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
            } else if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_403)) {
                BaseFragment.showSessionScreensDialog(context, response.getString(AppContants.RESPONSE_MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void isSubmitDisplay() {
        if (TMSListTreeActivity.selectedCheckTaskIdWithReason.size() > 0)
            TMSListTreeActivity.tmsSubmit.setVisibility(View.VISIBLE);
        else
            TMSListTreeActivity.tmsSubmit.setVisibility(View.GONE);
    }
    public void showTaskDisAlertDialog(String msg, final String taskid) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        /*TextView tv = new TextView(context);
        tv.setText("Task Discontinued");
        tv.setPadding(30, 30, 30, 30);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);*/
        builder1.setTitle("Task Discontinued");
        builder1.setMessage(msg);
        builder1.setCancelable(false);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_DISCONTINUE','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + taskid + "', 'key':''}";
                        System.out.println(requestParameter);
                        if (actionDialog.getDialog().isShowing()) {
                            actionDialog.dismiss();
                            actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                        }
                        getLoadData(requestParameter, "ListReLoad");
                    }
                });
        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alert11 = builder1.create();
        alert11.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alert11.getButton(Dialog.BUTTON_POSITIVE);
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.Filter_textsize));
                //btnPositive.setTextSize(40);
                Button btnNegative = alert11.getButton(Dialog.BUTTON_NEGATIVE);
                btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.Filter_textsize));
                //btnNegative.setTextSize(40);
                Button btnNeutral = alert11.getButton(Dialog.BUTTON_NEUTRAL);
                btnNeutral.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.Filter_textsize));
                TextView textView = (TextView) alert11.findViewById(android.R.id.message);
                if(context.getResources().getBoolean(R.bool.isTablet)) {
                    textView.setTextSize(25);
                } else {
                    textView.setTextSize(16);
                }
            }
        });
        alert11.show();
    }
    public void showDeleteAlertDialog(String msg, final String taskid, String code) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
       /* TextView tv = new TextView(context);
        tv.setText("Task Delete");
        tv.setPadding(30, 30, 30, 30);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);*/
        builder1.setTitle("Task Delete");
        builder1.setMessage(msg);
        builder1.setCancelable(false);
        if (code.equalsIgnoreCase("1")) {
            builder1.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_DELETE_PROCESS','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + taskid + "'}";
                            System.out.println(requestParameter);
                            getLoadData(requestParameter, "Delete");
                        }
                    });
            builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        } else {
            builder1.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        final AlertDialog alert11 = builder1.create();
        alert11.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alert11.getButton(Dialog.BUTTON_POSITIVE);
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.Filter_textsize));
                //btnPositive.setTextSize(40);
                Button btnNegative = alert11.getButton(Dialog.BUTTON_NEGATIVE);
                btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.Filter_textsize));
                //btnNegative.setTextSize(40);
                Button btnNeutral = alert11.getButton(Dialog.BUTTON_NEUTRAL);
                btnNeutral.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.Filter_textsize));
                TextView textView = (TextView) alert11.findViewById(android.R.id.message);
                if(context.getResources().getBoolean(R.bool.isTablet)) {
                    textView.setTextSize(25);
                } else {
                    textView.setTextSize(16);
                }
            }
        });
        alert11.show();
    }
    public DatePickerDialog.OnDateSetListener intimateOnDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            Log.d("Date Picker", "After Listener");
            populateSetDate(yy, mm + 1, dd);
        }
        public void populateSetDate(int year, int month, int day) {
            editDateTextView.setText(day + "/" + month + "/" + year);
        }
    };
    public void showRatingRequestAlertDialog(String title, String msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                /*TextView tv = new TextView(context);
        tv.setText(title);
        tv.setPadding(30, 30, 30, 30);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);*/
        builder1.setTitle(title);
        builder1.setMessage(msg);
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        listReload.onReloadList("List Reload");
                        if (actionDialog != null)
                            if (actionDialog.getDialog().isShowing()) {
                                actionDialog.dismiss();
                                actionClicked.setImageDrawable(context.getResources().getDrawable(R.drawable.tms_action_unselect));
                            }
                    }
                });
        final AlertDialog alert11 = builder1.create();
        alert11.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alert11.getButton(Dialog.BUTTON_POSITIVE);
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.Filter_textsize));
                //btnPositive.setTextSize(40);
                Button btnNegative = alert11.getButton(Dialog.BUTTON_NEGATIVE);
                btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.Filter_textsize));
                //btnNegative.setTextSize(40);
                Button btnNeutral = alert11.getButton(Dialog.BUTTON_NEUTRAL);
                btnNeutral.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.Filter_textsize));
                TextView textView = (TextView) alert11.findViewById(android.R.id.message);
                if(context.getResources().getBoolean(R.bool.isTablet)) {
                    textView.setTextSize(25);
                } else {
                    textView.setTextSize(16);
                }
            }
        });
        alert11.show();
    }
    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public static class TreeItem {
        public HashMap<String, String> listValue;
        public boolean arrowView;
        public TMSListReload tmsListReload;
        public TreeItem(HashMap<String, String> listValue, TMSListReload tmsListReload, boolean arrowView) {
            this.listValue = listValue;
            this.arrowView = arrowView;
            this.tmsListReload = tmsListReload;
        }
    }
}