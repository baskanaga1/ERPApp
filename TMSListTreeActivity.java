package com.guruinfo.scm.tms;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.guruinfo.scm.R;
import com.guruinfo.scm.Timesheet.TimeSheetAddAndViewActivity;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseFragment;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.tree.model.TreeNode;
import com.guruinfo.scm.common.tree.view.AndroidTreeView;
import com.michael.easydialog.EasyDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import butterknife.Bind;
import butterknife.ButterKnife;
import static com.guruinfo.scm.common.BaseFragmentActivity.setToast;
/**
 * Created by Kannan G on 11/14/2016.
 */
public class TMSListTreeActivity extends BaseFragment {
    String TAG = "TMSListTreeActivity";
    private AndroidTreeView tView;
    TreeNode root = TreeNode.root();
    ViewGroup containerView;
    Context context;
    SessionManager session;
    String res = "";
    String firstLoadList = "";
    BackgroundTask backgroundTask;
    String uid;
    String Cre_Id;
    String requestParameter;
    @Bind(R.id.tms_scrollview)
    ScrollView tmsScrollview;
    @Bind(R.id.error_msg)
    TextView emptyListMsgTextView;
    static Button tmsSubmit;
    String listType = "";
    int pos;
    static LinearLayout dummyLay;
    int pageNo = 0;
    int totalPage = 0;
    static HashMap<String, HashMap<String, String>> selectedCheckTaskIdWithReason = new HashMap<>();
    ArrayList<HashMap<String, String>> tmsListValues;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tms_list_display, container, false);
        containerView = (ViewGroup) view.findViewById(R.id.container);
        ButterKnife.bind(this, view);
        tmsSubmit = (Button) view.findViewById(R.id.tms_submit);
        TMSFragmentActivity.actionBarFilterBtn.setVisibility(View.VISIBLE);
        // TMSFragmentActivity.actionBarBookMarkBtn.setVisibility(View.VISIBLE);
        TMSFragmentActivity.homeIcon.setVisibility(View.VISIBLE);
        TMSFragmentActivity.actionBarTitle.setText("Task Listing");
        tmsListValues = new ArrayList<HashMap<String, String>>();
        pageNo = 0;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            listType = bundle.getString("listType");
            TMSFragmentActivity.actionBarNewRequestBtn.setVisibility(View.VISIBLE);
            if (bundle.getString("response") != null) {
                res = bundle.getString("response");
                firstLoadList = requestParameter = bundle.getString("request");
                listDisplay(res);
            } else {
                firstLoadList = requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_SEARCH','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'metaData':'false', 'searchName':'" + listType + "', 'page':'" + pageNo + "'}";
                System.out.println(requestParameter);
                getLoadData(requestParameter, "LIST");
            }
        }
        TMSFragmentActivity.actionBarFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle filterBundle = new Bundle();
                filterBundle.putString("listType", listType);
                navigateToNextFragment(TMSsearchActivity.class.getName(), filterBundle);
            }
        });
        TMSFragmentActivity.actionBarNewRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LayoutInflater inflater2 = LayoutInflater.from(context);
                View convertView = inflater2.inflate(R.layout.tms_newrequest_dialog, null);
                Button QuickTask = (Button) convertView.findViewById(R.id.QuickTask);
                Button CreateTask = (Button) convertView.findViewById(R.id.CreateTask);
                final EasyDialog  newRequestDialog = new EasyDialog(context)
                        .setLayout(convertView)
                        .setBackgroundColor(context.getResources().getColor(R.color.white))
                        .setLocationByAttachedView(TMSFragmentActivity.actionBarNewRequestBtn)
                        .setGravity(EasyDialog.GRAVITY_BOTTOM)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 1000, -600, 100, -50, 50, 0)
                        .setAnimationAlphaShow(1000, 0.3f, 1.0f)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 500, -50, 800)
                        .setAnimationAlphaDismiss(500, 1.0f, 0.0f)
                        .setTouchOutsideDismiss(true)
                        .setMatchParent(false)
                        .setOutsideColor(context.getResources().getColor(R.color.transparent_black_bg))
                        .show();
                QuickTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("listType", listType);
                        navigateToNextFragment(TMSQuickTaskActivity.class.getName(), bundle);
                        newRequestDialog.dismiss();
                    }
                });
                CreateTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("TaskId", "0");
                        bundle.putString("Mode", "New");
                        bundle.putString("listType", listType);
                        navigateToNextFragment(TMSAddAndViewActivity.class.getName(), bundle);
                        newRequestDialog.dismiss();
                    }
                });
            }
        });
        tmsScrollview.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                // We take the last son in the scrollview
                View view = (View) tmsScrollview.getChildAt(tmsScrollview.getChildCount() - 1);
                int diff = (view.getBottom() - (tmsScrollview.getHeight() + tmsScrollview.getScrollY()));
                // if diff is zero, then the bottom has been reached
                if (diff == 0 && pageNo < totalPage) {
                    Log.i(TAG, "loading more data");
                    pageNo++;
                    Log.d(TAG, "Page Count--While Scrolling down" + pageNo);
                    try {
                        JSONObject jsonObject1 = new JSONObject(requestParameter);
                        jsonObject1.put("page", pageNo);
                        getLoadData(jsonObject1.toString(), "PAGELIST");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        tmsSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject multiselectSubmit = new JSONObject();
                    String taskIdValues = "";
                    String frmName = "TMSAssigned";
                    multiselectSubmit.put("process", "Request");
                    multiselectSubmit.put("FrmNameCtl", frmName);
                    for (Map.Entry<String, HashMap<String, String>> val : selectedCheckTaskIdWithReason.entrySet()) {
                        String keyname = val.getKey();
                        multiselectSubmit.put(frmName + "_" + keyname + "_reason", selectedCheckTaskIdWithReason.get(keyname).get("reason"));
                        multiselectSubmit.put(frmName + "_" + keyname + "_action", selectedCheckTaskIdWithReason.get(keyname).get("action"));
                        if (taskIdValues.equalsIgnoreCase("")) {
                            taskIdValues = keyname;
                        } else {
                            taskIdValues = taskIdValues + "," + keyname;
                        }
                    }
                    multiselectSubmit.put(frmName + "_taskassign", taskIdValues);
                    multiselectSubmit.put("Action", "TASK_MANAGEMENT_SYSTEM");
                    multiselectSubmit.put("submode", "RATING_ALERT");
                    multiselectSubmit.put("UID", uid);
                    multiselectSubmit.put("Cre_Id", Cre_Id);
                    requestParameter = multiselectSubmit.toString();
                    Log.d("Submit--->", requestParameter);
                    getLoadData(requestParameter, "ASSIGN");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        root = TreeNode.root();
        tView = new AndroidTreeView(getActivity(), root);
        tView.setDefaultAnimation(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(TMSTreeViewAdapter.class);
        containerView.addView(tView.getView());
        // tView.expandAll();
        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }
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
    public void pageListDisplay(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray tableValues = jsonObject.getJSONArray("TableValues");
            //totalPage = jsonObject.getInt("pageCount");
            for (int i = 0; i < tableValues.length(); i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                JSONObject tableValRow = tableValues.getJSONObject(i);
                JSONObject tableRow = tableValRow.getJSONObject("Values");
                //  hashMap.put("Task Name", tableRow.getString("Task Name"));
                hashMap.put("Owner", tableRow.getString("Owner"));
                hashMap.put("displayValues", tableRow.getString("LabelValues"));
                //  hashMap.put("End Date", tableRow.getString("End Date"));
                hashMap.put("ResendIcon", tableRow.getString("ResendIcon"));
                hashMap.put("Color", tableRow.getString("Color"));
                hashMap.put("Priority", tableRow.getString("Priority"));
                hashMap.put("isCheckbox", tableRow.getString("taskActive"));
                hashMap.put("isDeclinebox", tableRow.getString("taskDecline"));
                hashMap.put("isDeclineRedbox", "false");
                hashMap.put("isCheckboxSelected", "false");
                hashMap.put("BookMarked", tableRow.getString("BookMarked"));
                hashMap.put("overdue", tableRow.getString("overdue"));
                hashMap.put("TimeExtension", tableRow.getString("TimeExtension"));
                hashMap.put("TimeExtensionId", tableRow.getString("TimeExtensionId"));
                hashMap.put("timeExtensionDisplay", tableRow.getString("timeExtensionDisplay"));
                if (tableRow.getString("timeExtensionDisplay").equalsIgnoreCase("true")) {
                    hashMap.put("TimeExtensionValues", tableRow.getString("TimeExtensionValues"));
                }
                hashMap.put("StatusKey", tableRow.getString("StatusKey"));
                // hashMap.put("Task Code", tableRow.getString("Task Code"));
                hashMap.put("TaskId", tableRow.getString("TaskId"));
                /*if (listType.equalsIgnoreCase("selfAssigned")) {
                    //dont Get Assigned Data
                } else if (listType.equalsIgnoreCase("assignedToMe")) {
                    hashMap.put("Assigned By", tableRow.getString("Assigned By"));
                } else if (listType.equalsIgnoreCase("assignedByMe")) {
                    hashMap.put("Assigned To", tableRow.getString("Assigned To"));
                } else {
                    hashMap.put("Assigned To", tableRow.getString("Assigned To"));
                    hashMap.put("Assigned By", tableRow.getString("Assigned By"));
                }*/
                hashMap.put("Litegation", tableRow.getString("Litegation"));
                // hashMap.put("ProjectLocationVerticals", tableRow.getString("Project LocationVerticals"));
                // hashMap.put("Start Date", tableRow.getString("Start Date"));
                hashMap.put("statusHoldButton", tableRow.getString("statusHoldButton"));
                hashMap.put("completedPercentage", tableRow.getString("completedPercentage"));
                JSONObject progressStatusObject = tableRow.getJSONObject("ProgressStatus");
                hashMap.put("holdButton", progressStatusObject.getString("holdButton"));
                hashMap.put("completeButton", progressStatusObject.getString("completeButton"));
                hashMap.put("playButton", progressStatusObject.getString("playButton"));
                JSONObject ActionObj = tableRow.getJSONObject("ActionObj");
                hashMap.put("taskRatingButton", ActionObj.getString("RatingButton"));
                if (ActionObj.getString("RatingButton").equalsIgnoreCase("Rated")) {
                    JSONObject ratedObject = ActionObj.optJSONObject("taskRated");
                    hashMap.put("Ratescore", ratedObject.getString("Ratescore"));
                    hashMap.put("Ratewords", ratedObject.getString("Ratewords"));
                }
                hashMap.put("taskReassignButton", ActionObj.getString("taskReassignButton"));
                hashMap.put("taskCloneButton", ActionObj.getString("taskCloneButton"));
                hashMap.put("declineReason", "");
                hashMap.put("taskDiscontinueButton", ActionObj.getString("taskDiscontinueButton"));
                hashMap.put("taskViewButton", ActionObj.getString("taskViewButton"));
                hashMap.put("taskStatusAddUpdate", ActionObj.getString("taskStatusAddUpdate"));
                hashMap.put("taskDeleteButton", ActionObj.getString("taskDeleteButton"));
                hashMap.put("taskReopenButton", ActionObj.getString("taskReopenButton"));
                // tmsListValues.add(hashMap);
                JSONArray childJsonArray = tableValRow.getJSONArray("ChildValues");
                if (childJsonArray.length() != 0) {
                    TreeNode parentTreeNode = new TreeNode(new TMSTreeViewAdapter.TreeItem(hashMap, new TMSListReload() {
                        @Override
                        public void onReloadList(String msg) {
                            ReLoadList();
                        }
                        @Override
                        public void onMoveEditPage(Bundle bundle) {
                            MoveNextPage(bundle);
                        }
                    }, true));
                    tView.setDefaultViewHolder(TMSTreeViewAdapter.class);
                    tView.addNode(root, addChild(childJsonArray, parentTreeNode));
                } else {
                    TreeNode parentTreeNode = new TreeNode(new TMSTreeViewAdapter.TreeItem(hashMap, new TMSListReload() {
                        @Override
                        public void onReloadList(String msg) {
                            ReLoadList();
                        }
                        @Override
                        public void onMoveEditPage(Bundle bundle) {
                            MoveNextPage(bundle);
                        }
                    }, false));
                    tView.setDefaultViewHolder(TMSTreeViewAdapter.class);
                    tView.addNode(root, parentTreeNode);
                }
            }
            tView.setDefaultViewHolder(TMSTreeViewAdapter.class);
            /*tView.addNode(root, parentTreeNode);
            tView = new AndroidTreeView(getActivity(), root);
            tView.setDefaultAnimation(true);
            tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
            tView.setDefaultViewHolder(TMSTreeViewAdapter.class);
            containerView.removeAllViews();
            containerView.addView(tView.getView());*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void MoveNextPage(Bundle bundle) {
        bundle.putString("listType", listType);
        navigateToNextFragment(TMSAddAndViewActivity.class.getName(), bundle);
    }
    public void listDisplay(String jsonResponse) {
        try {
            root = TreeNode.root();
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray tableValues = jsonObject.getJSONArray("TableValues");
            // totalPage = jsonObject.getInt("pageCount");
            for (int i = 0; i < tableValues.length(); i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                JSONObject tableValRow = tableValues.getJSONObject(i);
                JSONObject tableRow = tableValRow.getJSONObject("Values");
                //  hashMap.put("Task Name", tableRow.getString("Task Name"));
                hashMap.put("Owner", tableRow.getString("Owner"));
                hashMap.put("displayValues", tableRow.getString("LabelValues"));
                //  hashMap.put("End Date", tableRow.getString("End Date"));
                //  hashMap.put("Task Update", tableRow.getString("Task Update"));
                hashMap.put("ResendIcon", tableRow.getString("ResendIcon"));
                hashMap.put("Color", tableRow.getString("Color"));
                hashMap.put("Priority", tableRow.getString("Priority"));
                hashMap.put("isCheckbox", tableRow.getString("taskActive"));
                hashMap.put("isDeclinebox", tableRow.getString("taskDecline"));
                hashMap.put("isDeclineRedbox", "false");
                hashMap.put("isCheckboxSelected", "false");
                hashMap.put("BookMarked", tableRow.getString("BookMarked"));
                hashMap.put("overdue", tableRow.getString("overdue"));
                hashMap.put("TimeExtension", tableRow.getString("TimeExtension"));
                hashMap.put("TimeExtensionId", tableRow.getString("TimeExtensionId"));
                hashMap.put("timeExtensionDisplay", tableRow.getString("timeExtensionDisplay"));
                if (tableRow.getString("timeExtensionDisplay").equalsIgnoreCase("true")) {
                    hashMap.put("TimeExtensionValues", tableRow.getString("TimeExtensionValues"));
                }
                hashMap.put("StatusKey", tableRow.getString("StatusKey"));
                // hashMap.put("Task Code", tableRow.getString("Task Code"));
                hashMap.put("TaskId", tableRow.getString("TaskId"));
                /*if (listType.equalsIgnoreCase("selfAssigned")) {
                    //dont Get Assigned Data
                } else if (listType.equalsIgnoreCase("assignedToMe")) {
                    hashMap.put("Assigned By", tableRow.getString("Assigned By"));
                } else if (listType.equalsIgnoreCase("assignedByMe")) {
                    hashMap.put("Assigned To", tableRow.getString("Assigned To"));
                } else {
                    hashMap.put("Assigned To", tableRow.getString("Assigned To"));
                    hashMap.put("Assigned By", tableRow.getString("Assigned By"));
                }*/
                hashMap.put("Litegation", tableRow.getString("Litegation"));
                // hashMap.put("ProjectLocationVerticals", tableRow.getString("Project LocationVerticals"));
                // hashMap.put("Start Date", tableRow.getString("Start Date"));
                hashMap.put("statusHoldButton", tableRow.getString("statusHoldButton"));
                hashMap.put("completedPercentage", tableRow.getString("completedPercentage"));
                JSONObject progressStatusObject = tableRow.getJSONObject("ProgressStatus");
                hashMap.put("holdButton", progressStatusObject.getString("holdButton"));
                hashMap.put("completeButton", progressStatusObject.getString("completeButton"));
                hashMap.put("playButton", progressStatusObject.getString("playButton"));
                JSONObject ActionObj = tableRow.getJSONObject("ActionObj");
                hashMap.put("taskRatingButton", ActionObj.getString("RatingButton"));
                if (ActionObj.getString("RatingButton").equalsIgnoreCase("Rated")) {
                    JSONObject ratedObject = ActionObj.optJSONObject("taskRated");
                    hashMap.put("Ratescore", ratedObject.getString("Ratescore"));
                    hashMap.put("Ratewords", ratedObject.getString("Ratewords"));
                }
                hashMap.put("taskReassignButton", ActionObj.getString("taskReassignButton"));
                hashMap.put("taskCloneButton", ActionObj.getString("taskCloneButton"));
                hashMap.put("declineReason", "");
                hashMap.put("taskDiscontinueButton", ActionObj.getString("taskDiscontinueButton"));
                hashMap.put("taskViewButton", ActionObj.getString("taskViewButton"));
                hashMap.put("taskStatusAddUpdate", ActionObj.getString("taskStatusAddUpdate"));
                hashMap.put("taskDeleteButton", ActionObj.getString("taskDeleteButton"));
                hashMap.put("taskReopenButton", ActionObj.getString("taskReopenButton"));
                // tmsListValues.add(hashMap);
                JSONArray childJsonArray = tableValRow.getJSONArray("ChildValues");
                if (childJsonArray.length() != 0) {
                    TreeNode parentTreeNode = new TreeNode(new TMSTreeViewAdapter.TreeItem(hashMap, new TMSListReload() {
                        @Override
                        public void onReloadList(String msg) {
                            ReLoadList();
                        }
                        @Override
                        public void onMoveEditPage(Bundle bundle) {
                            MoveNextPage(bundle);
                        }
                    }, true));
                    root.addChild(addChild(childJsonArray, parentTreeNode));
                } else {
                    TreeNode parentTreeNode = new TreeNode(new TMSTreeViewAdapter.TreeItem(hashMap, new TMSListReload() {
                        @Override
                        public void onReloadList(String msg) {
                            ReLoadList();
                        }
                        @Override
                        public void onMoveEditPage(Bundle bundle) {
                            MoveNextPage(bundle);
                        }
                    }, false));
                    root.addChild(parentTreeNode);
                }
            }
            tView = new AndroidTreeView(getActivity(), root);
            tView.setDefaultAnimation(true);
            tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
            tView.setDefaultViewHolder(TMSTreeViewAdapter.class);
            containerView.removeAllViews();
            containerView.addView(tView.getView());
            if (tableValues.length() == 0) {
                tmsScrollview.setVisibility(View.GONE);
                tmsSubmit.setVisibility(View.GONE);
                emptyListMsgTextView.setVisibility(View.VISIBLE);
            } else {
                if (selectedCheckTaskIdWithReason.size() == 0)
                    tmsSubmit.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void ReLoadList() {
        requestParameter = firstLoadList;
        System.out.println(requestParameter);
        tmsListValues.clear();
        pageNo = 1;
        getLoadData(requestParameter, "LIST");
    }
    private void parseJSONResponse(JSONObject response, String flag) {
        try {
            if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_1)) {
                if (flag.equalsIgnoreCase("LIST")) {
                    listDisplay(response.toString());
                } else if (flag.equalsIgnoreCase("PAGELIST")) {
                    pageListDisplay(response.toString());
                } else if (flag.equalsIgnoreCase("TimeSheet")) {
                    Intent intent = new Intent(context, TimeSheetAddAndViewActivity.class);
                    intent.putExtra("loadValues", response.toString());
                    startActivity(intent);
                } else if (flag.equalsIgnoreCase("ASSIGN")) {
                    TMSListTreeActivity.selectedCheckTaskIdWithReason.clear();
                    requestParameter = firstLoadList;
                    System.out.println(requestParameter);
                    tmsListValues.clear();
                    pageNo = 1;
                    getLoadData(requestParameter, "LIST");
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                } else if (flag.equalsIgnoreCase("ListReLoad")) {
                    requestParameter = firstLoadList;
                    System.out.println(requestParameter);
                    tmsListValues.clear();
                    pageNo = 1;
                    getLoadData(requestParameter, "LIST");
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
    public TreeNode addChild(JSONArray parentNode, TreeNode parentTreeNode) {
        try {
            for (int p = 0; p < parentNode.length(); p++) {
                //JSONArray level1Tree = parentNode.getJSONObject(p).getJSONArray("ChildValues");
                JSONArray level1Tree = parentNode;
                //for (JSONArray level1Tree : parentNode.g()) {
                TreeNode level1TreeNode;
                HashMap<String, String> hashMap = new HashMap<>();
                JSONObject tableValRow = level1Tree.getJSONObject(p);
                JSONObject tableRow = tableValRow.getJSONObject("Values");
                //  hashMap.put("Task Name", tableRow.getString("Task Name"));
                hashMap.put("Owner", tableRow.getString("Owner"));
                hashMap.put("displayValues", tableRow.getString("LabelValues"));
                //  hashMap.put("End Date", tableRow.getString("End Date"));
                //  hashMap.put("Task Update", tableRow.getString("Task Update"));
                hashMap.put("ResendIcon", tableRow.getString("ResendIcon"));
                hashMap.put("Color", tableRow.getString("Color"));
                hashMap.put("Priority", tableRow.getString("Priority"));
                hashMap.put("isCheckbox", tableRow.getString("taskActive"));
                hashMap.put("isDeclinebox", tableRow.getString("taskDecline"));
                hashMap.put("isDeclineRedbox", "false");
                hashMap.put("isCheckboxSelected", "false");
                hashMap.put("BookMarked", tableRow.getString("BookMarked"));
                hashMap.put("overdue", tableRow.getString("overdue"));
                hashMap.put("TimeExtension", tableRow.getString("TimeExtension"));
                hashMap.put("TimeExtensionId", tableRow.getString("TimeExtensionId"));
                hashMap.put("timeExtensionDisplay", tableRow.getString("timeExtensionDisplay"));
                if (tableRow.getString("timeExtensionDisplay").equalsIgnoreCase("true")) {
                    hashMap.put("TimeExtensionValues", tableRow.getString("TimeExtensionValues"));
                }
                hashMap.put("StatusKey", tableRow.getString("StatusKey"));
                // hashMap.put("Task Code", tableRow.getString("Task Code"));
                hashMap.put("TaskId", tableRow.getString("TaskId"));
                /*if (listType.equalsIgnoreCase("selfAssigned")) {
                    //dont Get Assigned Data
                } else if (listType.equalsIgnoreCase("assignedToMe")) {
                    hashMap.put("Assigned By", tableRow.getString("Assigned By"));
                } else if (listType.equalsIgnoreCase("assignedByMe")) {
                    hashMap.put("Assigned To", tableRow.getString("Assigned To"));
                } else {
                    hashMap.put("Assigned To", tableRow.getString("Assigned To"));
                    hashMap.put("Assigned By", tableRow.getString("Assigned By"));
                }*/
                hashMap.put("Litegation", tableRow.getString("Litegation"));
                // hashMap.put("ProjectLocationVerticals", tableRow.getString("Project LocationVerticals"));
                // hashMap.put("Start Date", tableRow.getString("Start Date"));
                hashMap.put("statusHoldButton", tableRow.getString("statusHoldButton"));
                hashMap.put("completedPercentage", tableRow.getString("completedPercentage"));
                JSONObject progressStatusObject = tableRow.getJSONObject("ProgressStatus");
                hashMap.put("holdButton", progressStatusObject.getString("holdButton"));
                hashMap.put("completeButton", progressStatusObject.getString("completeButton"));
                hashMap.put("playButton", progressStatusObject.getString("playButton"));
                JSONObject ActionObj = tableRow.getJSONObject("ActionObj");
                hashMap.put("taskRatingButton", ActionObj.getString("RatingButton"));
                if (ActionObj.getString("RatingButton").equalsIgnoreCase("Rated")) {
                    JSONObject ratedObject = ActionObj.optJSONObject("taskRated");
                    hashMap.put("Ratescore", ratedObject.getString("Ratescore"));
                    hashMap.put("Ratewords", ratedObject.getString("Ratewords"));
                }
                hashMap.put("taskReassignButton", ActionObj.getString("taskReassignButton"));
                hashMap.put("taskCloneButton", ActionObj.getString("taskCloneButton"));
                hashMap.put("declineReason", "");
                hashMap.put("taskDiscontinueButton", ActionObj.getString("taskDiscontinueButton"));
                hashMap.put("taskViewButton", ActionObj.getString("taskViewButton"));
                hashMap.put("taskStatusAddUpdate", ActionObj.getString("taskStatusAddUpdate"));
                hashMap.put("taskDeleteButton", ActionObj.getString("taskDeleteButton"));
                hashMap.put("taskReopenButton", ActionObj.getString("taskReopenButton"));
                level1Tree = level1Tree.getJSONObject(p).getJSONArray("ChildValues");
                if (level1Tree.length() != 0) {
                    level1TreeNode = new TreeNode(new TMSTreeViewAdapter.TreeItem(hashMap, new TMSListReload() {
                        @Override
                        public void onReloadList(String msg) {
                            ReLoadList();
                        }
                        @Override
                        public void onMoveEditPage(Bundle bundle) {
                            MoveNextPage(bundle);
                        }
                    }, true));
                    addChild(level1Tree, level1TreeNode);
                } else {
                    level1TreeNode = new TreeNode(new TMSTreeViewAdapter.TreeItem(hashMap, new TMSListReload() {
                        @Override
                        public void onReloadList(String msg) {
                            ReLoadList();
                        }
                        @Override
                        public void onMoveEditPage(Bundle bundle) {
                            MoveNextPage(bundle);
                        }
                    }, false));
                }
                parentTreeNode.addChild(level1TreeNode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parentTreeNode;
    }
    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }
            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
    public static void collapse(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = targetHeight;
        v.setVisibility(View.GONE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * (1 - interpolatedTime));
                v.requestLayout();
            }
            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
    /*public static class TreeViewAdapter extends TreeNode.BaseNodeViewHolder<TreeViewAdapter.TreeItem> {
        private LinearLayout parentLay, childLay;
        ImageView arrow;
        HashMap<String, String> mapValues;
        LinearLayout childColor;
        TextView tmsPriority;
        TextView tmsTaskName;
        TextView tmsEndDate;
        ImageView tmsBooking;
        ImageView tmsPlay;
        ImageView tmsHold;
        ImageView tmsComplete;
        ImageView tmsDeclineRed;
        CheckBox tmsMultiSelect;
        CheckBox tmsMutiDecline;
        @Bind(R.id.tms_assignedByLabel)
        TextView tmsAssignedByLabel;
        @Bind(R.id.tms_assignedTOLabel)
        TextView tmsAssignedToLabel;
        //Content Components
        @Bind(R.id.tms_taskId)
        TextViewPlus tmsTaskId;
        @Bind(R.id.tms_project)
        TextViewPlus tmsProject;
        @Bind(R.id.tms_assignedBy)
        TextViewPlus tmsAssignedBy;
        @Bind(R.id.tms_assignedTO)
        TextViewPlus tmsAssignedTo;
        @Bind(R.id.tms_startDate)
        TextViewPlus tmsStartDate;
        @Bind(R.id.tms_status)
        TextViewPlus tmsStatus;
        @Bind(R.id.tms_statusReq)
        TextViewPlus tmsStatusReq;
        @Bind(R.id.tms_holdchat)
        ImageView tmsHoldChat;
        @Bind(R.id.tms_overdue)
        ImageView tmsOverDue;
        @Bind(R.id.tms_time_req_extension)
        ImageView tmsTimeReqExtension;
        @Bind(R.id.tms_time_approval_extension)
        ImageView tmsTimeApprovalExtension;
        @Bind(R.id.tms_view)
        ImageView tmsViewButton;
        @Bind(R.id.tms_reAssign)
        ImageView tmsReAssignButton;
        @Bind(R.id.tms_clone)
        ImageView tmsCloneButton;
        @Bind(R.id.tms_statusUpdate)
        ImageView tmsStatusUpdateButton;
        @Bind(R.id.tms_taskDiscontinue)
        ImageView tmsTaskDisConButton;
        @Bind(R.id.tms_reOpen)
        ImageView tmsReOpenButton;
        @Bind(R.id.tms_ratingRequest)
        ImageView tmsRatingReqButton;
        @Bind(R.id.tms_delete)
        ImageView tmsDeleteButton;
        public TreeViewAdapter(Context context) {
            super(context);
        }
        @Override
        public View createNodeView(final TreeNode node, TreeItem value) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.tms_list_parent, null, false);
            ButterKnife.bind(this, view);
            parentLay = (LinearLayout) view.findViewById(R.id.parent_lay);
            childLay = (LinearLayout) view.findViewById(R.id.child_lay);
            arrow = (ImageView) view.findViewById(R.id.arrow);
            tmsTaskName = (TextView) view.findViewById(R.id.task_Name);
            tmsEndDate = (TextView) view.findViewById(R.id.tms_endDate);
            tmsPriority = (TextView) view.findViewById(R.id.tms_priority);
            tmsTaskName = (TextView) view.findViewById(R.id.task_Name);
            tmsEndDate = (TextView) view.findViewById(R.id.tms_endDate);
            tmsBooking = (ImageView) view.findViewById(R.id.tms_Bookmark);
            tmsPlay = (ImageView) view.findViewById(R.id.tms_play);
            tmsHold = (ImageView) view.findViewById(R.id.tms_hold);
            tmsComplete = (ImageView) view.findViewById(R.id.tms_complete);
            tmsDeclineRed = (ImageView) view.findViewById(R.id.select1_checkbox);
            tmsMultiSelect = (CheckBox) view.findViewById(R.id.select_checkbox);
            tmsMutiDecline = (CheckBox) view.findViewById(R.id.decline_button);
            childColor = (LinearLayout) view.findViewById(R.id.color_lay);
            mapValues = value.listValue;
            childLay.setVisibility(View.GONE);
            if (value.childColorCode == 0)
                childColor.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            else if (value.childColorCode == 1)
                childColor.setBackgroundColor(context.getResources().getColor(R.color.first_child));
            else if (value.childColorCode == 2)
                childColor.setBackgroundColor(context.getResources().getColor(R.color.second_child));
            else if (value.childColorCode == 3)
                childColor.setBackgroundColor(context.getResources().getColor(R.color.third_child));
            else if (value.childColorCode == 4)
                childColor.setBackgroundColor(context.getResources().getColor(R.color.fourth_child));
            else if (value.childColorCode == 5)
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
            Drawable buttonBackground = ContextCompat.getDrawable(context, R.drawable.tms_priority_border_red);
            if (mapValues.get("Priority").equalsIgnoreCase("High")) {
                buttonBackground.setColorFilter(ContextCompat.getColor(context, R.color
                        .red), PorterDuff.Mode.ADD);
                tmsPriority.setBackground(buttonBackground);
            } else if (mapValues.get("Priority").equalsIgnoreCase("Normal")) {
                buttonBackground.setColorFilter(ContextCompat.getColor(context, R.color
                        .gray), PorterDuff.Mode.ADD);
                tmsPriority.setBackground(buttonBackground);
            } else if (mapValues.get("Priority").equalsIgnoreCase("Low")) {
                buttonBackground.setColorFilter(ContextCompat.getColor(context, R.color
                        .dark_green), PorterDuff.Mode.ADD);
                tmsPriority.setBackground(buttonBackground);
            } else if (mapValues.get("Priority").equalsIgnoreCase("Flagged")) {
                buttonBackground.setColorFilter(ContextCompat.getColor(context, R.color
                        .yellow_bg), PorterDuff.Mode.ADD);
                tmsPriority.setBackground(buttonBackground);
            } else if (mapValues.get("Priority").equalsIgnoreCase("--") || mapValues.get("Priority").equalsIgnoreCase("-")) {
                tmsPriority.setVisibility(View.INVISIBLE);
            } else {
                buttonBackground.setColorFilter(ContextCompat.getColor(context, R.color
                        .black), PorterDuff.Mode.ADD);
                tmsPriority.setBackground(buttonBackground);
            }
            if (mapValues.get("BookMarked").equalsIgnoreCase("true"))
                tmsBooking.setImageResource(R.drawable.bookmark_select);
            else
                tmsBooking.setImageResource(R.drawable.bookmark_unselect);
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
           *//* arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (node.isExpanded())
                        node.setExpanded(false);
                    else
                        node.setExpanded(true);
                }
            });*//*
            tmsTaskName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (childLay.getVisibility() == View.VISIBLE) {
                        collapse(childLay);
                        //childLay.setVisibility(View.GONE);
                        dummyLay = null;
                    } else {
                        //childLay.setVisibility(View.VISIBLE);
                        expand(childLay);
                        if (dummyLay != null)
                            collapse(dummyLay);
                        // dummyLay.setVisibility(View.GONE);
                        dummyLay = childLay;
                    }
                }
            });
            *//*tmsDeclineRed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    declineSelectDialog = new Dialog(context);
                    LayoutInflater inflater2 = getLayoutInflater(null);
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
                                selectedCheckTaskIdWithReason.put(mapValues.get("TaskId"), map);
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
                            declineSelectDialog.cancel();
                            selectedCheckTaskIdWithReason.remove(mapValues.get("TaskId"));
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
                        LayoutInflater inflater2 = getLayoutInflater(null);
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
                                    selectedCheckTaskIdWithReason.put(mapValues.get("TaskId"), map);
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
                                declineSelectDialog.cancel();
                                tmsMutiDecline.setChecked(false);
                                tmsMultiSelect.setEnabled(true);
                                *//**//*mapValues.put("declineReason", "");
                                mapValues.put("isDeclineRedbox", "false");
                                tmsMutiDecline.setChecked(false);
                                tmsMultiSelect.setEnabled(true);
                                isSubmitDisplay();*//**//*
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
                        selectedCheckTaskIdWithReason.put(mapValues.get("TaskId"), map);
                        mapValues.put("isCheckboxSelected", "true");
                        isSubmitDisplay();
                    } else {
                        selectedCheckTaskIdWithReason.remove(mapValues.get("TaskId"));
                        mapValues.put("isCheckboxSelected", "false");
                        isSubmitDisplay();
                    }
                }
            });
            tmsBooking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // pos = i;
                    clickView = tmsBooking;
                    int bookMarkStatus;
                    if (mapValues.get("BookMarked").equalsIgnoreCase("true")) {
                        bookMarkStatus = 1;
                        bookMarkStatusValue = "false";
                    } else {
                        bookMarkStatus = 0;
                        bookMarkStatusValue = "true";
                    }
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
                    // holdDialog.getWindow().peekDecorView().animate().x(50);//.setExitTransition().getDecorView().getResources().getAnimation(R.anim.dialog_in).setPivotX(50f);
                    // holdDialog.getWindow().peekDecorView().animate().y(50);
                    // holdDialog.getWindow().getDecorView().setPivotY(50f);
                    Animation animShow = AnimationUtils.loadAnimation(context, R.anim.dialog_in);
                    Animation animHide = AnimationUtils.loadAnimation(context, R.anim.dialog_out);
                    LayoutInflater inflater2 = getLayoutInflater(null);
                    View convertView = inflater2.inflate(R.layout.tms_hold_dialog, null);
                    holdDialog.setContentView(convertView);
                    final Button yes_button = (Button) holdDialog.findViewById(R.id.hold_yes_button);
                    Button no_button = (Button) holdDialog.findViewById(R.id.hold_no_button);
                    final EditText reasonText = (EditText) holdDialog.findViewById(R.id.tms_reason_dialog_editText);
                    holdDialog.setTitle("Hold Reason");
                    yes_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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
            });*//*
            return view;
        }
        @Override
        public void toggle(boolean active) {
            arrow.setImageDrawable(context.getResources().getDrawable(active ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down));
        }
        public static class TreeItem {
            public HashMap<String, String> listValue;
            public boolean arrowView;
            public int childColorCode;
            public TreeItem(HashMap<String, String> listValue, boolean arrowView, int childColorCode) {
                this.listValue = listValue;
                this.arrowView = arrowView;
                this.childColorCode = childColorCode;
            }
        }
    }*/
}