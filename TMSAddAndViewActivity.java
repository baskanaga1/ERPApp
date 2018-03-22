package com.guruinfo.scm.tms;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.guruinfo.scm.CircleImageView;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseFragment;
import com.guruinfo.scm.common.FilePicker;
import com.guruinfo.scm.common.ImageLoader;
import com.guruinfo.scm.common.RestClient;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.HttpRequest;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.tree.model.TreeNode;
import com.guruinfo.scm.common.tree.view.AndroidTreeView;
import com.guruinfo.scm.common.utils.ApiCalls;
import com.guruinfo.scm.library.FilteredArrayAdapter;
import com.guruinfo.scm.library.TokenCompleteTextView;
import com.michael.easydialog.EasyDialog;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter;
import com.redmadrobot.inputmask.MaskedTextChangedListener;
import com.redmadrobot.inputmask.PolyMaskTextChangedListener;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import butterknife.Bind;
import butterknife.ButterKnife;
import static com.guruinfo.scm.common.BaseFragmentActivity.loadLeadStyleSpinner;
import static com.guruinfo.scm.common.BaseFragmentActivity.setToast;
/**
 * Created by Kannan G on 9/1/2016.
 */
public class TMSAddAndViewActivity extends BaseFragment implements TokenCompleteTextView.TokenListener<lists> {
    String TAG = "TMSAddAndViewActivity";
    SessionManager session;
    String res = "";
    BackgroundTask backgroundTask;
    String uid, Cre_Id, requestParameter;
    LinearLayout tableLayout;
    Context context;
    private Paint mPaint;
    InputMethodManager imm;
    int count;
    int colorCode;
    AutoCompleteTextView FilterAutocomplete;
    static EditText editDateTextView, editTimeText;
    CheckBox colorCheckBox;
    Dialog dialog, fileManagerAttachDialog;
    Spinner tms_userName_spinner;
    String pathAddress = "";
    ProgressDialog processDialog;
    String response, Camera_Capturepath, Gallery_path;
    EditText filePath;
    Spinner subTaskSpinner;
    TableLayout groupAttach;
    TableLayout generalAttach;
    TableLayout groupList;
    TableLayout generalList;
    EditText groupedit;
    EditText generaledit;
    JSONArray groupDiscussionConversation = new JSONArray();
    ArrayList<HashMap<String, String>> userNameArrayList;
    JSONArray generalDiscussionObj = new JSONArray();
    ArrayList<HashMap<String, String>> subTaskNameArrayList;
    ArrayList<HashMap<String, String>> taskNameArrayList;
    TextView editBtn, updateBtn, cancelBtn;
    TextView subTask, fileManager, discussion, timeSheet, ratting, history, timeExtension, taskStatus, negotiation;
    EasyDialog subTaskDialog, fileManagerDialog, discussionDialog, timeSheetDialog, rattingDialog, historyDialog, timeExtensionDialog, taskStatusDialog, negotiationDialog;
    JSONObject tabJsonObject;
    Dialog confirmationdialog, Pickerdialog, holdDialog, folderDialog;
    Button ok_button, cancel_button;
    Spinner departmentSpinner, wingSpinner, designationSpinner, individualSpinner, dependencyPreSpinner, companySpinner;
    Spinner dependencyTaskSpinner, folderSpinner;
    CheckBox selfAssignee;
    Boolean isViewDependency = false;
    int selectedPosition = 0;
    View errorFocusView;
    String parentId = "", taskType = "Task";
    HashMap<String, String> filterValue = new HashMap<>();
    HashMap<String, ArrayList<HashMap<String, String>>> loadHashValues = new HashMap<>();
    private Uri picUri;
    private File selectedFile;
    private static final int CAMERA_CAPTURE = 1;
    private static final int SELECT_PICTURE = 2;
    private static final int REQUEST_PICK_FILE = 3;
    ArrayList<String> doclist;
    Boolean month[];
    String color[];
    String taskId = "";
    int taskIdValue = 0;
    Boolean viewMode = false;
    Boolean editMode = false;
    JSONObject loadResponse = new JSONObject();
    TableLayout attachTableLayout;
    String formName = "TMSTaskUpdate";
    String listType = "";
    Boolean clone = false;
    String taskTypeClone = "";
    View view;
    Uri CAPTUREURI;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        dependencyTaskSpinner = new Spinner(context);
        attachTableLayout = new TableLayout(context);
        selfAssignee = new CheckBox(context);
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tms_add_view, container, false);
        ButterKnife.bind(context, view);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        TMSFragmentActivity.actionBarNewRequestBtn.setVisibility(View.GONE);
        TMSFragmentActivity.actionBarFilterBtn.setVisibility(View.GONE);
        TMSFragmentActivity.actionBarBookMarkBtn.setVisibility(View.GONE);
        TMSFragmentActivity.homeIcon.setVisibility(View.VISIBLE);
        taskId = getArguments().getString("TaskId");
        listType = getArguments().getString("listType");
        departmentSpinner = new Spinner(context);
        wingSpinner = new Spinner(context);
        designationSpinner = new Spinner(context);
        individualSpinner = new Spinner(context);
        dependencyPreSpinner = new Spinner(context);
        companySpinner = new Spinner(context);
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);
        }
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        }
        if (getArguments().getString("clone") != null) {
            clone = true;
            taskTypeClone = "clone";
        }
        mPaint = new Paint();
        doclist = new ArrayList<String>();
        tableLayout = (LinearLayout) view.findViewById(R.id.dynamic_lay);
        //Action Btn
        editBtn = (TextView) view.findViewById(R.id.edit_btn);
        updateBtn = (TextView) view.findViewById(R.id.update_btn);
        cancelBtn = (TextView) view.findViewById(R.id.cancel_btn);
        //Tab Btn
        subTask = (TextView) view.findViewById(R.id.subTask_btn);
        fileManager = (TextView) view.findViewById(R.id.fileManager_btn);
        discussion = (TextView) view.findViewById(R.id.discussion_btn);
        timeSheet = (TextView) view.findViewById(R.id.timeSheet_btn);
        ratting = (TextView) view.findViewById(R.id.ratting_btn);
        history = (TextView) view.findViewById(R.id.history_btn);
        timeExtension = (TextView) view.findViewById(R.id.timeExtension_btn);
        taskStatus = (TextView) view.findViewById(R.id.taskStatus_btn);
        negotiation = (TextView) view.findViewById(R.id.negotiation_btn);
        confirmationdialog = new Dialog(context);
        LayoutInflater inflater2 = getLayoutInflater(null);
        View convertView = inflater2.inflate(R.layout.image_conform, null);
        confirmationdialog.setContentView(convertView);
        ok_button = (Button) confirmationdialog.findViewById(R.id.ok);
        cancel_button = (Button) confirmationdialog.findViewById(R.id.cancel);
        confirmationdialog.setTitle("Confirmation Alert !");
        Pickerdialog = new Dialog(context);
        LayoutInflater inflater1 = getLayoutInflater(null);
        View convertView1 = inflater1.inflate(R.layout.attach_fragment, null);
        LinearLayout document = (LinearLayout) convertView1.findViewById(R.id.document);
        LinearLayout gallery = (LinearLayout) convertView1.findViewById(R.id.gallery);
        LinearLayout camera = (LinearLayout) convertView1.findViewById(R.id.camera);
        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDocument();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGallery();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCamera();
            }
        });
        Pickerdialog.setContentView(convertView1);
        Pickerdialog.setTitle("Attachments");
        if (getArguments().getString("Mode").equalsIgnoreCase("New"))
            requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_DISPLAY_TASK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + taskId + "', 'TaskType':'" + taskTypeClone + "', 'Editable':'true'}";
        else
            requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_DISPLAY_TASK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + taskId + "', 'TaskType':'', 'Editable':'false'}";
        System.out.println(requestParameter);
        getLoadData(requestParameter, "VIEW");
        subTask.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           hideKeyboard();
                                           LayoutInflater inflater2 = LayoutInflater.from(context);
                                           View subTaskView = inflater2.inflate(R.layout.tms_subtask_dialog, null);
                                           ViewGroup dynamicLayout = (ViewGroup) subTaskView.findViewById(R.id.subTask_dynamic_lay);
                                           Button dynCreateBtn = (Button) subTaskView.findViewById(R.id.subTask_create);
                                           AndroidTreeView tView;
                                           TreeNode root = TreeNode.root();
                                           subTaskDialog = new EasyDialog(context)
                                                   .setLayout(subTaskView)
                                                   .setBackgroundColor(context.getResources().getColor(R.color.white))
                                                   .setLocationByAttachedView(subTask)
                                                   .setGravity(EasyDialog.GRAVITY_TOP)
                                                   .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 300, 400, 0)
                                                   .setAnimationTranslationShow(EasyDialog.DIRECTION_Y, 300, 400, 0)
                                                   .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 300, 0, 400)
                                                   .setAnimationTranslationDismiss(EasyDialog.DIRECTION_Y, 300, 0, 400)
                                                   .setTouchOutsideDismiss(true)
                                                   .setMatchParent(false)
                                                   .setMarginLeftAndRight(10, 10)
                                                   .setMarginTopAndBottom(subTask.getHeight(), 0)
                                                   .setOutsideColor(context.getResources().getColor(R.color.transparent_black_bg))
                                                   .show();
                                           try {
                                               JSONObject tableValRow = tabJsonObject.getJSONObject("subTask");
                                               parentId = tableValRow.getString("parentId");
                                               taskType = tableValRow.getString("taskType");
                                               if (Boolean.parseBoolean(tableValRow.getString("createSubTask")))
                                                   dynCreateBtn.setVisibility(View.VISIBLE);
                                               else
                                                   dynCreateBtn.setVisibility(View.GONE);
                                               JSONArray childJsonArray = tableValRow.getJSONArray("ChildValues");
                                               JSONObject parentObject = tableValRow.getJSONObject("Value");
                                               String childTaskId = parentObject.getString("TaskId");
                                               Boolean isView = Boolean.parseBoolean(parentObject.getString("isView"));
                                               Boolean isDelete = Boolean.parseBoolean(parentObject.getString("isDelete"));
                                               JSONArray parentArray = parentObject.getJSONArray("Values");
                                               if (childJsonArray.length() != 0) {
                                                   TreeNode parentTreeNode = new TreeNode(new TMSSubTaskDialogAdapter.TreeItem(parentArray, new TMSListReload() {
                                                       @Override
                                                       public void onReloadList(String subTaskId) {
                                                           requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_DELETE_ALERT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + subTaskId + "'}";
                                                           System.out.println(requestParameter);
                                                           getLoadData(requestParameter, "DeleteAlert");
                                                       }
                                                       @Override
                                                       public void onMoveEditPage(Bundle bundle) {
                                                           loadTaskView(bundle);
                                                       }
                                                   }, true, isView, isDelete, childTaskId));
                                                   root.addChild(addChildTree(childJsonArray, parentTreeNode));
                                               } else {
                                                   TreeNode parentTreeNode = new TreeNode(new TMSSubTaskDialogAdapter.TreeItem(parentArray, new TMSListReload() {
                                                       @Override
                                                       public void onReloadList(String subTaskId) {
                                                           requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_DELETE_ALERT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + subTaskId + "'}";
                                                           System.out.println(requestParameter);
                                                           getLoadData(requestParameter, "DeleteAlert");
                                                       }
                                                       @Override
                                                       public void onMoveEditPage(Bundle bundle) {
                                                           loadTaskView(bundle);
                                                       }
                                                   }, false, isView, isDelete, childTaskId));
                                                   root.addChild(parentTreeNode);
                                               }
                                           } catch (JSONException e) {
                                               e.printStackTrace();
                                           }
                                           tView = new AndroidTreeView(getActivity(), root);
                                           tView.setDefaultAnimation(true);
                                           tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
                                           tView.setDefaultViewHolder(TMSSubTaskDialogAdapter.class);
                                           dynamicLayout.addView(tView.getView());
                                           if (savedInstanceState != null) {
                                               String state = savedInstanceState.getString("tState");
                                               if (!TextUtils.isEmpty(state)) {
                                                   tView.restoreState(state);
                                               }
                                           }
                                           dynCreateBtn.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_DISPLAY_TASK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + 0 + "', 'Editable':'true'}";
                                                   System.out.println(requestParameter);
                                                   getLoadData(requestParameter, "VIEW");
                                                   tableLayout.removeAllViews();
                                               }
                                           });
                                       }
                                   }
        );
        discussion.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              hideKeyboard();
                                              LayoutInflater inflater2 = LayoutInflater.from(context);
                                              View discussionView = inflater2.inflate(R.layout.tms_discussion_tabdialog, null);
                                              TabHost host = (TabHost) discussionView.findViewById(R.id.tabHost);
                                              groupAttach = (TableLayout) discussionView.findViewById(R.id.group_attach);
                                              generalAttach = (TableLayout) discussionView.findViewById(R.id.general_attach);
                                              groupList = (TableLayout) discussionView.findViewById(R.id.group_list);
                                              generalList = (TableLayout) discussionView.findViewById(R.id.general_list);
                                              final Spinner taskSpinner = (Spinner) discussionView.findViewById(R.id.tms_taskname_spinner);
                                              subTaskSpinner = (Spinner) discussionView.findViewById(R.id.tms_subtask_spinner);
                                              groupedit = (EditText) discussionView.findViewById(R.id.group_chat);
                                              generaledit = (EditText) discussionView.findViewById(R.id.general_chat);
                                              final Button groupSend = (Button) discussionView.findViewById(R.id.group_send_button);
                                              final Button generalSend = (Button) discussionView.findViewById(R.id.general_send_button);
                                              taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                  @Override
                                                  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                      int selectSpinnerPosition = (taskSpinner.getSelectedItemPosition()) - 1;
                                                      String tasId = taskNameArrayList.get(selectSpinnerPosition).get("id");
                                                      requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'DISCUSSION_SUBTASK_LOAD','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + tasId + "'}";
                                                      System.out.println(requestParameter);
                                                      getLoadData(requestParameter, "SUBTASKLIST");
                                                  }
                                                  @Override
                                                  public void onNothingSelected(AdapterView<?> parent) {
                                                  }
                                              });
                                              groupSend.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      try {
                                                          InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                                                          imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                                                      } catch (Exception e) {
                                                          // TODO: handle exception
                                                      }
                                                      if (groupedit.getText().toString().trim().length() > 0) {
                                                          String groupAttachValue = "";
                                                          for (int a = 0; a < groupAttach.getChildCount(); a++) {
                                                              TableRow attachRow = ((TableRow) groupAttach.getChildAt(a));
                                                              EditText editText = ((EditText) ((TableRow) attachRow.getChildAt(0)).getChildAt(0));
                                                              if (editText.getText().toString().trim().length() > 0) {
                                                                  if (groupAttachValue.equalsIgnoreCase(""))
                                                                      groupAttachValue = "" + editText.getId();
                                                                  else
                                                                      groupAttachValue = groupAttachValue + "," + editText.getId();
                                                              }
                                                          }
                                                          int taskSpinnerPosition = (taskSpinner.getSelectedItemPosition()) - 1;
                                                          String tasId = taskNameArrayList.get(taskSpinnerPosition).get("id");
                                                          requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'DISCUSSION_SAVE','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + tasId + "', 'attach':'" + groupAttachValue + "', 'comments':'" + groupedit.getText().toString().trim() + "','disType':'group','subTaskID':''}";
                                                          System.out.println(requestParameter);
                                                          getLoadData(requestParameter, "GROUPCHAT");
                                                      } else {
                                                          YoYo.with(Techniques.Bounce)
                                                                  .duration(700)
                                                                  .playOn(groupedit);
                                                          YoYo.with(Techniques.Shake)
                                                                  .duration(700)
                                                                  .playOn(groupSend);
                                                      }
                                                  }
                                              });
                                              generalSend.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      try {
                                                          InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                                                          imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                                                      } catch (Exception e) {
                                                          // TODO: handle exception
                                                      }
                                                      if (generaledit.getText().toString().trim().length() > 0) {
                                                          String generalAttachValue = "";
                                                          for (int a = 0; a < generalAttach.getChildCount(); a++) {
                                                              TableRow attachRow = ((TableRow) generalAttach.getChildAt(a));
                                                              EditText editText = ((EditText) ((TableRow) attachRow.getChildAt(0)).getChildAt(0));
                                                              if (editText.getText().toString().trim().length() > 0) {
                                                                  if (generalAttachValue.equalsIgnoreCase(""))
                                                                      generalAttachValue = "" + editText.getId();
                                                                  else
                                                                      generalAttachValue = generalAttachValue + "," + editText.getId();
                                                              }
                                                          }
                                                          int taskSpinnerPosition = (taskSpinner.getSelectedItemPosition()) - 1;
                                                          String tasId = taskNameArrayList.get(taskSpinnerPosition).get("id");
                                                          String subTasId = "0";
                                                          if (subTaskSpinner.getSelectedItemPosition() > 0) {
                                                              int subTaskSpinnerPosition = (subTaskSpinner.getSelectedItemPosition()) - 1;
                                                              subTasId = subTaskNameArrayList.get(subTaskSpinnerPosition).get("id");
                                                          }
                                                          requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'DISCUSSION_SAVE','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + tasId + "', 'attach':'" + generalAttachValue + "', 'comments':'" + generaledit.getText().toString().trim() + "','disType':'indiv','subTaskID':'" + subTasId + "'}";
                                                          System.out.println(requestParameter);
                                                          getLoadData(requestParameter, "GENERALCHAT");
                                                      } else {
                                                          YoYo.with(Techniques.Bounce)
                                                                  .duration(700)
                                                                  .playOn(generaledit);
                                                          YoYo.with(Techniques.Shake)
                                                                  .duration(700)
                                                                  .playOn(generalSend);
                                                      }
                                                  }
                                              });
                                              generalAttachDisplay();
                                              groupAttachDisplay();
                                              host.setup();
                                              try {
                                                  TabHost.TabSpec spec;
                                                  JSONObject DiscussionObject = tabJsonObject.getJSONObject("Discussion");
                                                  if (Boolean.parseBoolean(DiscussionObject.getString("groupDiscussion"))) {
                                                      spec = host.newTabSpec("Group Discussion");
                                                      spec.setContent(R.id.group_tab);
                                                      spec.setIndicator("Group Discussion");
                                                      host.addTab(spec);
                                                  } else {
                                                      discussionView.findViewById(R.id.group_tab).setVisibility(View.GONE);
                                                  }
                                                  if (Boolean.parseBoolean(DiscussionObject.getString("generalDiscussion"))) {
                                                      spec = host.newTabSpec("General Discussion");
                                                      spec.setContent(R.id.general_tab);
                                                      spec.setIndicator("General Discussion");
                                                      host.addTab(spec);
                                                  } else {
                                                      discussionView.findViewById(R.id.general_tab).setVisibility(View.GONE);
                                                  }
                                                  for (int i = 0; i < groupDiscussionConversation.length(); i++) {
                                                      groupList.addView(discussionChat(groupDiscussionConversation.getJSONObject(i), "group"));
                                                  }
                                                  JSONObject generalDiscussionConversation = DiscussionObject.getJSONObject("generalDiscussionConversation");
                                                  JSONArray taskNameJsonArray = generalDiscussionConversation.getJSONArray("TaskName");
                                                  taskNameArrayList = ApiCalls.getArraylistfromJson(taskNameJsonArray.toString());
                                                  loadLeadStyleSpinner(taskSpinner, taskNameArrayList, new String[]{"value"}, "Task");
                                                  for (int i = 0; i < taskNameArrayList.size(); i++) {
                                                      if (taskNameArrayList.get(i).get("selected").equalsIgnoreCase("true")) {
                                                          taskSpinner.setSelection(i + 1);
                                                      }
                                                  }
                                                  JSONObject subTaskNameJsonObject = generalDiscussionConversation.getJSONObject("SubTaskName");
                                                  JSONArray subTaskNameJsonArray = subTaskNameJsonObject.getJSONArray("Values");
                                                  subTaskNameArrayList = ApiCalls.getArraylistfromJson(subTaskNameJsonArray.toString());
                                                  loadLeadStyleSpinner(subTaskSpinner, subTaskNameArrayList, new String[]{"value"}, "Sub Task");
                                                  for (int i = 0; i < generalDiscussionObj.length(); i++) {
                                                      generalList.addView(discussionChat(generalDiscussionObj.getJSONObject(i), "indiv"));
                                                  }
                                              } catch (JSONException e) {
                                                  e.printStackTrace();
                                              }
                                              discussionDialog = new EasyDialog(context)
                                                      .setLayout(discussionView)
                                                      .setBackgroundColor(context.getResources().getColor(R.color.white))
                                                      .setLocationByAttachedView(discussion)
                                                      .setGravity(EasyDialog.GRAVITY_TOP)
                                                      .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 300, 400, 0)
                                                      .setAnimationTranslationShow(EasyDialog.DIRECTION_Y, 300, 400, 0)
                                                      .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 300, 0, 400)
                                                      .setAnimationTranslationDismiss(EasyDialog.DIRECTION_Y, 300, 0, 400)
                                                      .setTouchOutsideDismiss(true)
                                                      .setMatchParent(true)
                                                      .setMarginLeftAndRight(10, 10)
                                                      .setMarginTopAndBottom(discussion.getHeight(), 0)
                                                      .setOutsideColor(context.getResources().getColor(R.color.transparent_black_bg))
                                                      .show();
                                              discussionDialog.getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                                          }
                                      }
        );
        fileManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                LayoutInflater inflater2 = LayoutInflater.from(context);
                View fileManagerView = inflater2.inflate(R.layout.tms_filemanager_dialog, null);
                TableRow fileManagerUpload = (TableRow) fileManagerView.findViewById(R.id.file_upload);
                TableLayout fileManagerList = (TableLayout) fileManagerView.findViewById(R.id.file_manager_list);
                TextView noData = (TextView) fileManagerView.findViewById(R.id.no_data);
                try {
                    JSONObject jsonObject = tabJsonObject.getJSONObject("FileManager");
                    JSONArray fileArray = jsonObject.getJSONArray("TableValues");
                    if (Boolean.parseBoolean(jsonObject.getString("UploadFile")))
                        fileManagerUpload.setVisibility(View.VISIBLE);
                    else
                        fileManagerUpload.setVisibility(View.GONE);
                    if (fileArray.length() > 0) {
                        noData.setVisibility(View.GONE);
                        fileManagerList.setVisibility(View.VISIBLE);
                        for (int i = 0; i < fileArray.length(); i++) {
                            fileManagerList.addView(addFileManagerListTable(i, fileArray.getJSONObject(i)), i);
                        }
                    } else {
                        noData.setVisibility(View.VISIBLE);
                        fileManagerList.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                fileManagerUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'FILE_ADD','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + taskId + "'}";
                        System.out.println(requestParameter);
                        getLoadData(requestParameter, "FILEMANAGER");
                    }
                });
                fileManagerDialog = new EasyDialog(context)
                        .setLayout(fileManagerView)
                        .setBackgroundColor(context.getResources().getColor(R.color.white))
                        .setLocationByAttachedView(fileManager)
                        .setGravity(EasyDialog.GRAVITY_TOP)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 300, 400, 0)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_Y, 300, 400, 0)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 300, 0, 400)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_Y, 300, 0, 400)
                        .setTouchOutsideDismiss(true)
                        .setMatchParent(false)
                        .setMarginLeftAndRight(10, 10)
                        .setMarginTopAndBottom(fileManager.getHeight(), 0)
                        .setOutsideColor(context.getResources().getColor(R.color.transparent_black_bg))
                        .show();
            }
        });
        timeSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                LayoutInflater inflater2 = LayoutInflater.from(context);
                View timeSheetView = inflater2.inflate(R.layout.tms_timesheet_dialog, null);
                ListView timeSheetList = (ListView) timeSheetView.findViewById(R.id.time_sheet_list);
                TextView noData = (TextView) timeSheetView.findViewById(R.id.no_data);
                TextView grandTotal = (TextView) timeSheetView.findViewById(R.id.grandTotal);
                try {
                    JSONObject timeSheetJsonObject = tabJsonObject.getJSONObject("TimeList");
                    JSONArray timeSheetJsonArray = timeSheetJsonObject.getJSONArray("Values");
                    grandTotal.setText(timeSheetJsonObject.getString("GrandTotal"));
                    ArrayList<HashMap<String, String>> timeSheetArr = new ArrayList<HashMap<String, String>>();
                    for (int a = 0; a < timeSheetJsonArray.length(); a++) {
                        JSONObject object = timeSheetJsonArray.getJSONObject(a);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("Description", object.getString("Description"));
                        map.put("TotalHours", object.getString("TotalHours"));
                        map.put("OtherValues", object.getString("OtherValues"));
                        map.put("StartDateAndTime", object.getString("StartDateAndTime"));
                        map.put("EndDateAndTime", object.getString("EndDateAndTime"));
                        map.put("GrandTotal", object.getString("GrandTotal"));
                        timeSheetArr.add(map);
                    }
                    if (timeSheetArr.size() > 0) {
                        noData.setVisibility(View.GONE);
                        timeSheetList.setVisibility(View.VISIBLE);
                        TimeSheetListAdapter timeSheetListAdapter = new TimeSheetListAdapter(context, timeSheetArr);
                        timeSheetList.setAdapter(timeSheetListAdapter);
                    } else {
                        noData.setVisibility(View.VISIBLE);
                        timeSheetList.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                timeSheetDialog = new EasyDialog(context)
                        .setLayout(timeSheetView)
                        .setBackgroundColor(context.getResources().getColor(R.color.white))
                        .setLocationByAttachedView(timeSheet)
                        .setGravity(EasyDialog.GRAVITY_TOP)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 300, 400, 0)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_Y, 300, 400, 0)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 300, 0, 400)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_Y, 300, 0, 400)
                        .setTouchOutsideDismiss(true)
                        .setMatchParent(false)
                        .setMarginLeftAndRight(10, 10)
                        .setMarginTopAndBottom(timeSheet.getHeight(), 0)
                        .setOutsideColor(context.getResources().getColor(R.color.transparent_black_bg))
                        .show();
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                LayoutInflater inflater2 = LayoutInflater.from(context);
                View historyView = inflater2.inflate(R.layout.tms_history_dialog, null);
                ListView historyList = (ListView) historyView.findViewById(R.id.history_list);
                TextView historyHeading = (TextView) historyView.findViewById(R.id.history_heading);
                TextView noData = (TextView) historyView.findViewById(R.id.no_data);
                try {
                    JSONObject historyJsonObject = tabJsonObject.getJSONObject("History");
                    historyHeading.setText(historyJsonObject.getString("Header"));
                    JSONArray historyJsonArray = historyJsonObject.getJSONArray("Values");
                    ArrayList<HashMap<String, String>> historyArr = new ArrayList<HashMap<String, String>>();
                    for (int a = 0; a < historyJsonArray.length(); a++) {
                        JSONObject object = historyJsonArray.getJSONObject(a);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("UserNameId", object.getString("UserNameId"));
                        map.put("DateAndTime", object.getString("DateAndTime"));
                        map.put("Action", object.getString("Action"));
                        map.put("AssignTo", object.getString("AssignTo"));
                        map.put("AssignBy", object.getString("AssignBy"));
                        historyArr.add(map);
                    }
                    if (historyArr.size() > 0) {
                        noData.setVisibility(View.GONE);
                        historyList.setVisibility(View.VISIBLE);
                        HistoryListAdapter historyListAdapter = new HistoryListAdapter(context, historyArr);
                        historyList.setAdapter(historyListAdapter);
                    } else {
                        noData.setVisibility(View.VISIBLE);
                        historyList.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                historyDialog = new EasyDialog(context)
                        .setLayout(historyView)
                        .setBackgroundColor(context.getResources().getColor(R.color.white))
                        .setLocationByAttachedView(history)
                        .setGravity(EasyDialog.GRAVITY_TOP)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 300, 400, 0)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_Y, 300, 400, 0)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 300, 0, 400)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_Y, 300, 0, 400)
                        .setTouchOutsideDismiss(true)
                        .setMatchParent(false)
                        .setMarginLeftAndRight(10, 10)
                        .setMarginTopAndBottom(history.getHeight(), 0)
                        .setOutsideColor(context.getResources().getColor(R.color.transparent_black_bg))
                        .show();
            }
        });
        ratting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                LayoutInflater inflater2 = LayoutInflater.from(context);
                View rattingView = inflater2.inflate(R.layout.tms_ratting_dialog, null);
                TextView noData = (TextView) rattingView.findViewById(R.id.no_data);
                ViewGroup dynamicLayout = (ViewGroup) rattingView.findViewById(R.id.ratting_dynamic_lay);
                AndroidTreeView tView;
                TreeNode root = TreeNode.root();
                rattingDialog = new EasyDialog(context)
                        .setLayout(rattingView)
                        .setBackgroundColor(context.getResources().getColor(R.color.white))
                        .setLocationByAttachedView(ratting)
                        .setGravity(EasyDialog.GRAVITY_TOP)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 300, 400, 0)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_Y, 300, 400, 0)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 300, 0, 400)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_Y, 300, 0, 400)
                        .setTouchOutsideDismiss(true)
                        .setMatchParent(false)
                        .setMarginLeftAndRight(10, 10)
                        .setMarginTopAndBottom(ratting.getHeight(), 0)
                        .setOutsideColor(context.getResources().getColor(R.color.transparent_black_bg))
                        .show();
                try {
                    JSONObject tableValRow = tabJsonObject.getJSONObject("RatingList");
                    if (tableValRow.length() == 0) {
                        noData.setVisibility(View.VISIBLE);
                        dynamicLayout.setVisibility(View.GONE);
                    }
                    parentId = tableValRow.getString("parentId");
                    JSONArray childJsonArray = tableValRow.getJSONArray("ChildValues");
                    JSONObject parentObject = tableValRow.getJSONObject("Value");
                    String stars = parentObject.getString("Stars");
                    JSONArray parentArray = parentObject.getJSONArray("Values");
                    if (childJsonArray.length() != 0) {
                        TreeNode parentTreeNode = new TreeNode(new TMSRattingTreeAdapter.TreeItem(parentArray, true, stars));
                        root.addChild(addChildRattingTree(childJsonArray, parentTreeNode));
                    } else {
                        TreeNode parentTreeNode = new TreeNode(new TMSRattingTreeAdapter.TreeItem(parentArray, false, stars));
                        root.addChild(parentTreeNode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                tView = new AndroidTreeView(getActivity(), root);
                tView.setDefaultAnimation(true);
                tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
                tView.setDefaultViewHolder(TMSRattingTreeAdapter.class);
                dynamicLayout.addView(tView.getView());
                if (savedInstanceState != null) {
                    String state = savedInstanceState.getString("tState");
                    if (!TextUtils.isEmpty(state)) {
                        tView.restoreState(state);
                    }
                }
            }
        });
        timeExtension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                LayoutInflater inflater2 = LayoutInflater.from(context);
                View timeExtensionView = inflater2.inflate(R.layout.tms_timeextension_dialog, null);
                ListView timeExtensionList = (ListView) timeExtensionView.findViewById(R.id.timeExtension_list);
                TextView timeExtensionHeading = (TextView) timeExtensionView.findViewById(R.id.timeExtension_heading);
                TextView noData = (TextView) timeExtensionView.findViewById(R.id.no_data);
                try {
                    JSONObject historyJsonObject = tabJsonObject.getJSONObject("TimeExtension");
                    timeExtensionHeading.setText(historyJsonObject.getString("Header"));
                    JSONArray historyJsonArray = historyJsonObject.getJSONArray("Values");
                    ArrayList<HashMap<String, String>> timeExtensionArr = new ArrayList<HashMap<String, String>>();
                    for (int a = 0; a < historyJsonArray.length(); a++) {
                        JSONObject object = historyJsonArray.getJSONObject(a);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("Status", object.getString("Status"));
                        map.put("ActualPlannedDate", object.getString("ActualPlannedDate"));
                        map.put("ActualEndDate", object.getString("ActualEndDate"));
                        map.put("ActualStartDate", object.getString("ActualStartDate"));
                        map.put("DateAndTime2", object.getString("DateAndTime2"));
                        map.put("DateAndTime1", object.getString("DateAndTime1"));
                        map.put("NegotationBy", object.getString("NegotationBy"));
                        map.put("Reason", object.getString("Reason"));
                        map.put("AssignedBy", object.getString("AssignedBy"));
                        map.put("S.No", object.getString("S.No"));
                        timeExtensionArr.add(map);
                    }
                    if (timeExtensionArr.size() > 0) {
                        noData.setVisibility(View.GONE);
                        timeExtensionList.setVisibility(View.VISIBLE);
                        TimeExtensionAdapter timeExtensionAdapter = new TimeExtensionAdapter(context, timeExtensionArr);
                        final SwingRightInAnimationAdapter alphaInAnimationAdapter = new SwingRightInAnimationAdapter(timeExtensionAdapter);
                        alphaInAnimationAdapter.setAbsListView(timeExtensionList);
                        timeExtensionAdapter.setLimit(1);
                        timeExtensionList.setAdapter(alphaInAnimationAdapter);
                    } else {
                        noData.setVisibility(View.VISIBLE);
                        timeExtensionList.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                timeExtensionDialog = new EasyDialog(context)
                        .setLayout(timeExtensionView)
                        .setBackgroundColor(context.getResources().getColor(R.color.white))
                        .setLocationByAttachedView(timeExtension)
                        .setGravity(EasyDialog.GRAVITY_TOP)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 300, 400, 0)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_Y, 300, 400, 0)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 300, 0, 400)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_Y, 300, 0, 400)
                        .setTouchOutsideDismiss(true)
                        .setMatchParent(false)
                        .setMarginLeftAndRight(10, 10)
                        .setMarginTopAndBottom(timeExtension.getHeight(), 0)
                        .setOutsideColor(context.getResources().getColor(R.color.transparent_black_bg))
                        .show();
            }
        });
        taskStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                LayoutInflater inflater2 = LayoutInflater.from(context);
                View taskStatusView = inflater2.inflate(R.layout.tms_history_dialog, null);
                ListView statusList = (ListView) taskStatusView.findViewById(R.id.history_list);
                TextView statusHeading = (TextView) taskStatusView.findViewById(R.id.history_heading);
                TextView noData = (TextView) taskStatusView.findViewById(R.id.no_data);
                try {
                    JSONObject statusJsonObject = tabJsonObject.getJSONObject("TaskStatus");
                    statusHeading.setText(statusJsonObject.getString("Header"));
                    JSONArray statusJsonArray = statusJsonObject.getJSONArray("Values");
                    ArrayList<HashMap<String, String>> statusArr = new ArrayList<HashMap<String, String>>();
                    for (int a = 0; a < statusJsonArray.length(); a++) {
                        JSONObject object = statusJsonArray.getJSONObject(a);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("DateAndTime", object.getString("DateAndTime"));
                        map.put("UpdatedInformation", object.getString("UpdatedInformation"));
                        map.put("UpdatedBy", object.getString("UpdatedBy"));
                        map.put("TaskSubTaskName", object.getString("TaskSubTaskName"));
                        statusArr.add(map);
                    }
                    if (statusArr.size() > 0) {
                        noData.setVisibility(View.GONE);
                        statusList.setVisibility(View.VISIBLE);
                        TaskStatusListAdapter taskStatusListAdapter = new TaskStatusListAdapter(context, statusArr);
                        statusList.setAdapter(taskStatusListAdapter);
                    } else {
                        noData.setVisibility(View.VISIBLE);
                        statusList.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                taskStatusDialog = new EasyDialog(context)
                        .setLayout(taskStatusView)
                        .setBackgroundColor(context.getResources().getColor(R.color.white))
                        .setLocationByAttachedView(taskStatus)
                        .setGravity(EasyDialog.GRAVITY_TOP)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 300, 400, 0)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_Y, 300, 400, 0)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 300, 0, 400)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_Y, 300, 0, 400)
                        .setTouchOutsideDismiss(true)
                        .setMatchParent(false)
                        .setMarginLeftAndRight(10, 10)
                        .setMarginTopAndBottom(taskStatus.getHeight(), 0)
                        .setOutsideColor(context.getResources().getColor(R.color.transparent_black_bg))
                        .show();
            }
        });
        negotiation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                LayoutInflater inflater2 = LayoutInflater.from(context);
                View negotiationView = inflater2.inflate(R.layout.tms_timeextension_dialog, null);
                ListView timeExtensionList = (ListView) negotiationView.findViewById(R.id.timeExtension_list);
                TextView timeExtensionHeading = (TextView) negotiationView.findViewById(R.id.timeExtension_heading);
                TextView noData = (TextView) negotiationView.findViewById(R.id.no_data);
                try {
                    JSONObject historyJsonObject = tabJsonObject.getJSONObject("Negotation");
                    timeExtensionHeading.setText(historyJsonObject.getString("Header"));
                    JSONArray historyJsonArray = historyJsonObject.getJSONArray("Values");
                    ArrayList<HashMap<String, String>> timeExtensionArr = new ArrayList<HashMap<String, String>>();
                    for (int a = 0; a < historyJsonArray.length(); a++) {
                        JSONObject object = historyJsonArray.getJSONObject(a);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("Status", object.getString("Status"));
                        map.put("PlannedStartdate", object.getString("PlannedStartdate"));
                        map.put("PlannedEnddate", object.getString("PlannedEnddate"));
                        map.put("ActualEndDate", object.getString("ActualEndDate"));
                        map.put("ActualStartDate", object.getString("ActualStartDate"));
                        map.put("DateAndTime2", object.getString("DateAndTime2"));
                        map.put("DateAndTime1", object.getString("DateAndTime1"));
                        map.put("NegotationBy", object.getString("NegotationBy"));
                        map.put("Reason", object.getString("Reason"));
                        map.put("AssignedBy", object.getString("AssignedBy"));
                        map.put("S.No", object.getString("S.No"));
                        timeExtensionArr.add(map);
                    }
                    if (timeExtensionArr.size() > 0) {
                        noData.setVisibility(View.GONE);
                        timeExtensionList.setVisibility(View.VISIBLE);
                        NegotationAdapter negotationAdapter = new NegotationAdapter(context, timeExtensionArr);
                        final SwingRightInAnimationAdapter alphaInAnimationAdapter = new SwingRightInAnimationAdapter(negotationAdapter);
                        alphaInAnimationAdapter.setAbsListView(timeExtensionList);
                        negotationAdapter.setLimit(1);
                        timeExtensionList.setAdapter(alphaInAnimationAdapter);
                    } else {
                        noData.setVisibility(View.VISIBLE);
                        timeExtensionList.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                negotiationDialog = new EasyDialog(context)
                        .setLayout(negotiationView)
                        .setBackgroundColor(context.getResources().getColor(R.color.white))
                        .setLocationByAttachedView(negotiation)
                        .setGravity(EasyDialog.GRAVITY_TOP)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 300, 400, 0)
                        .setAnimationTranslationShow(EasyDialog.DIRECTION_Y, 300, 400, 0)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 300, 0, 400)
                        .setAnimationTranslationDismiss(EasyDialog.DIRECTION_Y, 300, 0, 400)
                        .setTouchOutsideDismiss(true)
                        .setMatchParent(false)
                        .setMarginLeftAndRight(10, 10)
                        .setMarginTopAndBottom(201, 0)
                        .setOutsideColor(context.getResources().getColor(R.color.transparent_black_bg))
                        .show();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener()
                                   {
                                       @Override
                                       public void onClick(View v) {
                                           requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_DISPLAY_TASK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + taskId + "', 'Editable':'true'}";
                                           System.out.println(requestParameter);
                                           getLoadData(requestParameter, "VIEW");
                                           tableLayout.removeAllViews();
                                       }
                                   }
        );
        cancelBtn.setOnClickListener(new View.OnClickListener()
                                     {
                                         @Override
                                         public void onClick(View v) {
                                             try {
                                                 InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                                                 imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                                             } catch (Exception e) {
                                                 // TODO: handle exception
                                             }
                                             Bundle bundle = new Bundle();
                                             bundle.putString("listType", listType);
                                             navigateToNextFragment(TMSListTreeActivity.class.getName(), bundle);
                                         }
                                     }
        );
        updateBtn.setOnClickListener(new View.OnClickListener()
                                     {
                                         @Override
                                         public void onClick(View v) {
                                             try {
                                                 try {
                                                     InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                                                     imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                                                 } catch (Exception e) {
                                                     // TODO: handle exception
                                                 }
                                                 JSONObject sendJsonObject = new JSONObject();
                                                 JSONArray jsonArray = loadResponse.getJSONArray("Values");
                                                 sendJsonObject.put("formName", formName);
                                                 sendJsonObject.put("quick_task_name", "");
                                                 sendJsonObject.put("TaskId", loadResponse.getString("Taskid"));
                                                 sendJsonObject.put("frmId", "1");
                                                 sendJsonObject.put("parentId", parentId);
                                                 sendJsonObject.put("taskType", taskType);
                                                 sendJsonObject.put(formName + "_isDownLine", "");
                                                 sendJsonObject.put(formName + "_isUpLine", "");
                                                 int errCount = 0;
                                                 String cateId = "";
                                                 for (int i = 0; i < jsonArray.length(); i++) {
                                                     JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                                     JSONObject ResultValues = jsonObject1.getJSONObject("ResultValues");
                                                     String head = jsonObject1.getString("Head");
                                                     if (!head.equalsIgnoreCase("Progress")) {
                                                         JSONArray jsonArray1 = ResultValues.getJSONArray("values");
                                                         int s = -1;
                                                         String categoryId = "";
                                                         String masterid = "";
                                                         String masId = "";
                                                         for (int j = 0; j < jsonArray1.length(); j++) {
                                                             JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                                                             String typeForm = jsonObject2.getString("DataType");
                                                             masterid = jsonObject2.getString("MasterId");
                                                             String masterType = jsonObject2.getString("MasterType");
                                                             Boolean isMandatory = Boolean.parseBoolean(jsonObject2.getString("IsMandatory"));
                                                             categoryId = jsonObject2.getString("categoryId");
                                                             JSONObject fieldSettings = jsonObject2.getJSONObject("fieldSettings");
                                                             String label = fieldSettings.getString("label");
                                                             String fieldMutiple = fieldSettings.getString("isMultiple");
                                                             int lineNo = Integer.parseInt(jsonObject2.getString("lineNo"));
                                                             sendJsonObject.put(formName + "_data_type_" + masterid, typeForm);
                                                             sendJsonObject.put(formName + "_" + masterid + "_1_lineNo", lineNo);
                                                             sendJsonObject.put(formName + "_" + masterid + "_1_deleted_lineNo", "");
                                                             sendJsonObject.put(formName + "_master_type_" + masterid, masterType);
                                                             if (masId.equalsIgnoreCase(""))
                                                                 masId = masterid;
                                                             else
                                                                 masId = masId + "," + masterid;
                                                             if (lineNo == 1) {
                                                                 s++;
                                                                 if (typeForm.equalsIgnoreCase("TEXTBOX") || typeForm.equalsIgnoreCase("TEXTAREA")) {
                                                                     Log.d("i Value :" + i, "j value :" + j);
                                                                     EditText editText = ((EditText) ((TableRow) ((TableRow) ((TableLayout) ((LinearLayout) ((LinearLayout) tableLayout.getChildAt(i)).getChildAt(1)).getChildAt(s)).getChildAt(0)).getChildAt(1)).getChildAt(0));
                                                                     editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.dynamic_header));
                                                                     if (isMandatory)
                                                                         if (editText.getText().toString().trim().length() > 0) {
                                                                         } else {
                                                                             editText.setError("Field is Required");
                                                                             if (errCount == 0)
                                                                                 errorFocusView = editText;
                                                                             errCount++;
                                                                         }
                                                                     sendJsonObject.put(formName + "_" + categoryId + "_" + label.replace(" ", "") + "_1_1", editText.getText().toString().trim());
                                                                     Log.d("Answer: " + label, editText.getText().toString());
                                                                 } else if (typeForm.equalsIgnoreCase("DATETIME")) {
                                                                     Log.d("i Value :" + i, "j value :" + j);
                                                                     EditText editTextDate = ((EditText) ((TableRow) ((TableRow) ((TableLayout) ((LinearLayout) ((LinearLayout) tableLayout.getChildAt(i)).getChildAt(1)).getChildAt(s)).getChildAt(0)).getChildAt(1)).getChildAt(0));
                                                                     EditText editTextTime = ((EditText) ((TableRow) ((TableRow) ((TableLayout) ((LinearLayout) ((LinearLayout) tableLayout.getChildAt(i)).getChildAt(1)).getChildAt(s)).getChildAt(0)).getChildAt(1)).getChildAt(1));
                                                                     if (isMandatory) {
                                                                         if (editTextDate.getText().toString().trim().length() > 0) {
                                                                         } else {
                                                                             editTextDate.setError("Field is Required");
                                                                             if (errCount == 0)
                                                                                 errorFocusView = editTextDate;
                                                                             errCount++;
                                                                         }
                                                                         if (editTextTime.getText().toString().trim().length() > 0) {
                                                                         } else {
                                                                             editTextTime.setError("Field is Required");
                                                                             if (errCount == 0)
                                                                                 errorFocusView = editTextDate;
                                                                             errCount++;
                                                                         }
                                                                     }
                                                                     sendJsonObject.put(formName + "_" + categoryId + "_" + label.replace(" ", "") + "_1_1", editTextDate.getText().toString().trim().replace("/", "-") + " " + editTextTime.getText().toString().trim());
                                                                     Log.d("Answer: " + label, editTextDate.getText().toString() + " " + editTextTime.getText().toString());
                                                                 } else if (typeForm.equalsIgnoreCase("DROPDOWN_S")) {
                                                                     Log.d("i Value :" + i, "j value :" + j);
                                                                     Spinner spinner = ((Spinner) ((TableRow) ((TableRow) ((TableLayout) ((LinearLayout) ((LinearLayout) tableLayout.getChildAt(i)).getChildAt(1)).getChildAt(s)).getChildAt(0)).getChildAt(1)).getChildAt(0));
                                                                     ArrayList<HashMap<String, String>> finalSpinnerResult = loadHashValues.get(label);
                                                                     int selectSpinnerPosition = (spinner.getSelectedItemPosition()) - 1;
                                                                     Log.d("Selected Position :", "" + selectSpinnerPosition);
                                                                     if (selectSpinnerPosition >= 0) {
                                                                         Log.d("Answer: " + label, finalSpinnerResult.get(selectSpinnerPosition).get("value"));
                                                                         sendJsonObject.put(formName + "_" + categoryId + "_" + label.replace(" ", "") + "_1_1", finalSpinnerResult.get(selectSpinnerPosition).get("id"));
                                                                     } else
                                                                         sendJsonObject.put(formName + "_" + categoryId + "_" + label.replace(" ", "") + "_1_1", "");
                                                                     if (isMandatory) {
                                                                         if (label.equalsIgnoreCase("Company") || label.equalsIgnoreCase("Department") || label.equalsIgnoreCase("Wing") || label.equalsIgnoreCase("Designation") || label.equalsIgnoreCase("Individual")) {
                                                                             if (!selfAssignee.isChecked())
                                                                                 if (selectSpinnerPosition < 0) {
                                                                                     TextView errorText = (TextView) spinner.getSelectedView();
                                                                                     errorText.setError("Field is Required");
                                                                                     if (errCount == 0)
                                                                                         errorFocusView = spinner;
                                                                                     errCount++;
                                                                                 }
                                                                         } else {
                                                                             if (selectSpinnerPosition < 0) {
                                                                                 TextView errorText = (TextView) spinner.getSelectedView();
                                                                                 errorText.setError("Field is Required");
                                                                                 if (errCount == 0)
                                                                                     errorFocusView = spinner;
                                                                                 errCount++;
                                                                                 errCount++;
                                                                             }
                                                                         }
                                                                     }
                                                                 } else if (typeForm.equalsIgnoreCase("CHECKBOX")) {
                                                                     Log.d("i Value :" + i, "j value :" + j);
                                                                     RadioGroup radioGroup = ((RadioGroup) ((TableRow) ((TableRow) ((TableLayout) ((LinearLayout) ((LinearLayout) tableLayout.getChildAt(i)).getChildAt(1)).getChildAt(s)).getChildAt(0)).getChildAt(1)).getChildAt(0));
                                                                     String checkValues = "";
                                                                     CheckBox checkBox = null;
                                                                     RadioButton radioButton = null;
                                                                     if (label.equalsIgnoreCase("Allow Sub Task")) {
                                                                         for (int a = 0; a < radioGroup.getChildCount(); a++) {
                                                                             radioButton = ((RadioButton) ((RadioGroup) ((TableRow) ((TableRow) ((TableLayout) ((LinearLayout) ((LinearLayout) tableLayout.getChildAt(i)).getChildAt(1)).getChildAt(s)).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(a));
                                                                             if (radioButton.isChecked())
                                                                                 if (checkValues.equalsIgnoreCase(""))
                                                                                     checkValues = "" + radioButton.getId();
                                                                                 else
                                                                                     checkValues = checkValues + "," + radioButton.getId();
                                                                             Log.d("Answer: " + label, checkValues);
                                                                         }
                                                                         if (isMandatory) {
                                                                             if (label.equalsIgnoreCase("Self Assignee")) {
                                                                                 if (!departmentSpinner.isEnabled()) {
                                                                                     if (checkValues.length() == 0) {
                                                                                         radioButton.setError("Field is Required");
                                                                                         if (errCount == 0)
                                                                                             errorFocusView = radioButton;
                                                                                         errCount++;
                                                                                     }
                                                                                 }
                                                                             } else if (checkValues.length() == 0) {
                                                                                 radioButton.setError("Field is Required");
                                                                                 if (errCount == 0)
                                                                                     errorFocusView = radioButton;
                                                                                 errCount++;
                                                                                 errCount++;
                                                                             }
                                                                         }
                                                                         sendJsonObject.put(formName + "_" + categoryId + "_" + label.replace(" ", "") + "_1_1", checkValues);
                                                                     } else {
                                                                         for (int a = 0; a < radioGroup.getChildCount(); a++) {
                                                                             checkBox = ((CheckBox) ((RadioGroup) ((TableRow) ((TableRow) ((TableLayout) ((LinearLayout) ((LinearLayout) tableLayout.getChildAt(i)).getChildAt(1)).getChildAt(s)).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(a));
                                                                             if (checkBox.isChecked())
                                                                                 if (checkValues.equalsIgnoreCase(""))
                                                                                     checkValues = "" + checkBox.getId();
                                                                                 else
                                                                                     checkValues = checkValues + "," + checkBox.getId();
                                                                             Log.d("Answer: " + label, checkValues);
                                                                         }
                                                                         if (isMandatory) {
                                                                             if (label.equalsIgnoreCase("Self Assignee")) {
                                                                                 if (!departmentSpinner.isEnabled()) {
                                                                                     if (checkValues.length() == 0) {
                                                                                         checkBox.setError("Field is Required");
                                                                                         if (errCount == 0)
                                                                                             errorFocusView = checkBox;
                                                                                         errCount++;
                                                                                     }
                                                                                 }
                                                                             } else if (checkValues.length() == 0) {
                                                                                 checkBox.setError("Field is Required");
                                                                                 if (errCount == 0)
                                                                                     errorFocusView = checkBox;
                                                                                 errCount++;
                                                                                 errCount++;
                                                                             }
                                                                         }
                                                                         sendJsonObject.put(formName + "_" + categoryId + "_" + label.replace(" ", "") + "_1_1", checkValues);
                                                                     }
                                                                 } else if (typeForm.equalsIgnoreCase("ATTACH")) {
                                                                     Log.d("i Value :" + i, "j value :" + j);
                                                                     EditText editText;
                                                                     for (int a = 0; a < attachTableLayout.getChildCount(); a++) {
                                                                         TableRow attachRow = ((TableRow) attachTableLayout.getChildAt(a));
                                                                         if (a == 0) {
                                                                             editText = ((EditText) ((TableRow) attachRow.getChildAt(1)).getChildAt(0));
                                                                             if (isMandatory)
                                                                                 if (editText.getText().toString().trim().length() == 0) {
                                                                                     if (errCount == 0)
                                                                                         errorFocusView = editText;
                                                                                     errCount++;
                                                                                     editText.setError("Field is Required");
                                                                                 }
                                                                         } else {
                                                                             editText = ((EditText) attachRow.getChildAt(0));
                                                                             if (isMandatory)
                                                                                 if (editText.getText().toString().trim().length() == 0) {
                                                                                     if (errCount == 0)
                                                                                         errorFocusView = editText;
                                                                                     errCount++;
                                                                                     editText.setError("Field is Required");
                                                                                 }
                                                                         }
                                                                         Log.d("Answer: " + label, "" + editText.getId());
                                                                         sendJsonObject.put(formName + "_" + categoryId + "_" + label.replace(" ", "") + "_1_" + (a + 1), editText.getId());
                                                                     }
                                                                 } else if (typeForm.equalsIgnoreCase("COL_PIC")) {
                                                                     Log.d("i Value :" + i, "j value :" + j);
                                                                     CheckBox checkBox = ((CheckBox) ((TableRow) ((TableRow) ((TableLayout) ((LinearLayout) ((LinearLayout) tableLayout.getChildAt(i)).getChildAt(1)).getChildAt(s)).getChildAt(0)).getChildAt(1)).getChildAt(0));
                                                                     String colorCode = "";
                                                                     if (filterValue.containsKey(label)) {
                                                                         colorCode = filterValue.get(label);
                                                                     }
                                                                     if (isMandatory) {
                                                                         if (colorCode.equalsIgnoreCase("")) {
                                                                             checkBox.setError("Field is Required");
                                                                             if (errCount == 0)
                                                                                 errorFocusView = checkBox;
                                                                             errCount++;
                                                                         }
                                                                     }
                                                                     Log.d("Answer: " + label, colorCode);
                                                                     sendJsonObject.put(formName + "_" + categoryId + "_" + label.replace(" ", "") + "_1_1", colorCode);
                                                                 }
                                                             }
                                                         }
                                                         sendJsonObject.put(categoryId + "_1_masterIds", masId);
                                                         sendJsonObject.put(formName + "_" + categoryId + "_categoryCnt", 1);
                                                         if (cateId.equalsIgnoreCase(""))
                                                             cateId = categoryId;
                                                         else
                                                             cateId = cateId + "," + categoryId;
                                                     }
                                                 }
                                                 if (filterValue.containsKey("Status")) {
                                                     sendJsonObject.put(formName + "_status", filterValue.get("Status"));
                                                 } else {
                                                     sendJsonObject.put(formName + "_status", "");
                                                 }
                                                 sendJsonObject.put("categoryIds", cateId);
                                                 sendJsonObject.put("Action", "TASK_MANAGEMENT_SYSTEM");
                                                 sendJsonObject.put("submode", "ADD_TASK_SUBMIT");
                                                 sendJsonObject.put("UID", uid);
                                                 sendJsonObject.put("Cre_Id", Cre_Id);
                                                 if (clone)
                                                     sendJsonObject.put("TaskId", "0");
                                                 requestParameter = sendJsonObject.toString();
                                                 Log.d("Submit Data :", requestParameter);
                                                 if (errCount == 0)
                                                     if (clone)
                                                         getLoadData(requestParameter, "CLONE");
                                                     else
                                                         getLoadData(requestParameter, "SUBMIT");
                                                 else {
                                                     errorFocusView.requestFocus(View.FOCUS_UP);
                                                     errorFocusView.requestFocusFromTouch();
                                                 }
                                             } catch (JSONException e) {
                                                 e.printStackTrace();
                                             }
                                         }
                                     }
        );
        return view;
    }
    private void groupAttachDisplay() {
        TableRow grouptableRow = new TableRow(context);
        TableLayout.LayoutParams layoutParams3 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        layoutParams3.gravity = Gravity.CENTER_VERTICAL;
        grouptableRow.setLayoutParams(layoutParams3);
        grouptableRow.setOrientation(TableLayout.HORIZONTAL);
        TableRow groupAnsRow = new TableRow(context);
        groupAnsRow.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
        groupAnsRow.setBackgroundColor(getResources().getColor(R.color.white));
        ArrayList<HashMap<String, String>> groupArrayListValueMulti = new ArrayList<HashMap<String, String>>();
        final EditText groupparentBrowseValue = addNonEditTextToTableRow(groupArrayListValueMulti, null, 6);
        groupAnsRow.addView(groupparentBrowseValue);
        ImageView groupbrowseParentButton = addButtonToTableRow("", null, 1);
        groupAnsRow.addView(groupbrowseParentButton);
        groupbrowseParentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filePath = groupparentBrowseValue;
                Pickerdialog.show();
            }
        });
        ImageView addImageView = addViewToTableRow(1);
        groupAnsRow.addView(addImageView);
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TableRow childTableRow = new TableRow(context);
                TableLayout.LayoutParams layoutParams3 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                layoutParams3.gravity = Gravity.CENTER_VERTICAL;
                layoutParams3.topMargin = 10;
                childTableRow.setLayoutParams(layoutParams3);
                childTableRow.setOrientation(TableLayout.HORIZONTAL);
                TableRow ansRow = new TableRow(context);
                ansRow.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
                ansRow.setBackgroundColor(getResources().getColor(R.color.white));
                ArrayList<HashMap<String, String>> arrayListValueMulti = new ArrayList<HashMap<String, String>>();
                final EditText groupparentBrowseValue = addNonEditTextToTableRow(arrayListValueMulti, null, 6);
                ansRow.addView(groupparentBrowseValue);
                ImageView browseParentButton = addButtonToTableRow("", null, 1);
                ansRow.addView(browseParentButton);
                ImageView imageView = addViewToTableRow(0);
                ansRow.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        groupAttach.removeView(childTableRow);
                    }
                });
                browseParentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filePath = groupparentBrowseValue;
                        Pickerdialog.show();
                    }
                });
                childTableRow.addView(ansRow);
                groupAttach.addView(childTableRow);
            }
        });
        grouptableRow.addView(groupAnsRow);
        groupAttach.addView(grouptableRow);
    }
    public TableRow addTableRowToTableLayout(String key, String value) {
        TableRow tableRow = new TableRow(context);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.FILL_PARENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        tableRow.setGravity(Gravity.CENTER_VERTICAL);
        tableRow.setLayoutParams(layoutParams);
        tableRow.setOrientation(LinearLayout.HORIZONTAL);
        tableRow.setWeightSum(4);
        //tableRow.setPadding(5, 5, 5, 5);
        int padding_Value =  (int) getResources().getDimension(R.dimen.dynamic_text_padding);
        tableRow.setPadding(padding_Value, padding_Value, padding_Value, padding_Value);
        tableRow.addView(addTextViewToTableRow(key, false, null, 2), 0);
        tableRow.addView(addTextViewToTableRow(value, false, null, 2), 1);
        return tableRow;
    }
    private void managerAttachDisplay(final TableLayout tableLayout) {
        TableRow generaltableRow = new TableRow(context);
        TableLayout.LayoutParams layoutParams2 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        layoutParams2.gravity = Gravity.CENTER_VERTICAL;
        generaltableRow.setLayoutParams(layoutParams2);
        generaltableRow.setOrientation(TableLayout.HORIZONTAL);
        TableRow ansRow = new TableRow(context);
        ansRow.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
        ansRow.setBackgroundColor(getResources().getColor(R.color.white));
        ArrayList<HashMap<String, String>> arrayListValueMulti = new ArrayList<HashMap<String, String>>();
        final EditText generalparentBrowseValue = addNonEditTextToTableRow(arrayListValueMulti, null, 6);
        ansRow.addView(generalparentBrowseValue);
        ImageView browseParentButton = addButtonToTableRow("", null, 1);
        ansRow.addView(browseParentButton);
        browseParentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generalparentBrowseValue.setError(null);
                filePath = generalparentBrowseValue;
                Pickerdialog.show();
            }
        });
        ImageView imageView = addViewToTableRow(1);
        ansRow.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TableRow childTableRow = new TableRow(context);
                TableLayout.LayoutParams layoutParams2 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                layoutParams2.gravity = Gravity.CENTER_VERTICAL;
                layoutParams2.topMargin = 10;
                childTableRow.setLayoutParams(layoutParams2);
                childTableRow.setOrientation(TableLayout.HORIZONTAL);
                TableRow ansRow = new TableRow(context);
                ansRow.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
                ansRow.setBackgroundColor(getResources().getColor(R.color.white));
                ArrayList<HashMap<String, String>> arrayListValueMulti = new ArrayList<HashMap<String, String>>();
                final EditText generalparentBrowseValue = addNonEditTextToTableRow(arrayListValueMulti, null, 6);
                ansRow.addView(generalparentBrowseValue);
                ImageView browseParentButton = addButtonToTableRow("", null, 1);
                ansRow.addView(browseParentButton);
                ImageView imageView = addViewToTableRow(0);
                ansRow.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tableLayout.removeView(childTableRow);
                    }
                });
                browseParentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filePath = generalparentBrowseValue;
                        Pickerdialog.show();
                    }
                });
                childTableRow.addView(ansRow);
                tableLayout.addView(childTableRow);
            }
        });
        generaltableRow.addView(ansRow);
        tableLayout.addView(generaltableRow);
    }
    private void generalAttachDisplay() {
        TableRow generaltableRow = new TableRow(context);
        TableLayout.LayoutParams layoutParams2 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        layoutParams2.gravity = Gravity.CENTER_VERTICAL;
        generaltableRow.setLayoutParams(layoutParams2);
        generaltableRow.setOrientation(TableLayout.HORIZONTAL);
        TableRow ansRow = new TableRow(context);
        ansRow.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
        ansRow.setBackgroundColor(getResources().getColor(R.color.white));
        ArrayList<HashMap<String, String>> arrayListValueMulti = new ArrayList<HashMap<String, String>>();
        final EditText generalparentBrowseValue = addNonEditTextToTableRow(arrayListValueMulti, null, 6);
        ansRow.addView(generalparentBrowseValue);
        ImageView browseParentButton = addButtonToTableRow("", null, 1);
        ansRow.addView(browseParentButton);
        browseParentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filePath = generalparentBrowseValue;
                Pickerdialog.show();
            }
        });
        ImageView imageView = addViewToTableRow(1);
        ansRow.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TableRow childTableRow = new TableRow(context);
                TableLayout.LayoutParams layoutParams2 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                layoutParams2.gravity = Gravity.CENTER_VERTICAL;
                layoutParams2.topMargin = 10;
                childTableRow.setLayoutParams(layoutParams2);
                childTableRow.setOrientation(TableLayout.HORIZONTAL);
                TableRow ansRow = new TableRow(context);
                ansRow.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
                ansRow.setBackgroundColor(getResources().getColor(R.color.white));
                ArrayList<HashMap<String, String>> arrayListValueMulti = new ArrayList<HashMap<String, String>>();
                final EditText generalparentBrowseValue = addNonEditTextToTableRow(arrayListValueMulti, null, 6);
                ansRow.addView(generalparentBrowseValue);
                ImageView browseParentButton = addButtonToTableRow("", null, 1);
                ansRow.addView(browseParentButton);
                ImageView imageView = addViewToTableRow(0);
                ansRow.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        generalAttach.removeView(childTableRow);
                    }
                });
                browseParentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filePath = generalparentBrowseValue;
                        Pickerdialog.show();
                    }
                });
                childTableRow.addView(ansRow);
                generalAttach.addView(childTableRow);
            }
        });
        generaltableRow.addView(ansRow);
        generalAttach.addView(generaltableRow);
    }
    public View addFileManagerListTable(int i, JSONObject managerTableRow) {
        LayoutInflater inflater2 = LayoutInflater.from(context);
        View view = inflater2.inflate(R.layout.tms_filemanager_listrow, null);
        LinearLayout listSNoBgLayout = (LinearLayout) view.findViewById(R.id.list_sno_bg_layout);
        TextView listSNoTextView = (TextView) view.findViewById(R.id.list_sno);
        TextView TitleTextView = (TextView) view.findViewById(R.id.title);
        TableLayout tablerow = (TableLayout) view.findViewById(R.id.file_manager_table);
        if (count < colorArray().length) {
            colorCode = colorArray()[count];
            count = count + 1;
        } else {
            count = 0;
        }
        listSNoBgLayout.setBackground(getResources().getDrawable(colorCode));
        String sNo = String.valueOf(i + 1);
        listSNoTextView.setText(sNo);
        try {
            JSONArray managerRow = managerTableRow.getJSONArray("Values");
            for (int j = 0; j < managerRow.length(); j++) {
                if (managerRow.getJSONObject(j).getString("displayName").equalsIgnoreCase("Task Name"))
                    TitleTextView.setText(managerRow.getJSONObject(j).getString("displayValue"));
                else
                    tablerow.addView(addTableRowToTableLayout(managerRow.getJSONObject(j).getString("displayName"), managerRow.getJSONObject(j).getString("displayValue")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
    public View discussionChat(final JSONObject chatRowObject, final String disType) {
        LayoutInflater inflater2 = LayoutInflater.from(context);
        View discussionRowView = inflater2.inflate(R.layout.tms_discuss_chatrow, null);
        TextView userName = (TextView) discussionRowView.findViewById(R.id.activity_conversation_username);
        TextView chatMessage = (TextView) discussionRowView.findViewById(R.id.activity_conversation);
        TextView dept = (TextView) discussionRowView.findViewById(R.id.activity_conversation_dept);
        TextView date = (TextView) discussionRowView.findViewById(R.id.activity_conversation_date);
        ImageView delete = (ImageView) discussionRowView.findViewById(R.id.imageView);
        CircleImageView userImage = (CircleImageView) discussionRowView.findViewById(R.id.user_image);
        TableLayout attach = (TableLayout) discussionRowView.findViewById(R.id.attach_table);
        int loader = R.drawable.empty_dp_large;
        ImageLoader imgLoader = new ImageLoader(context);
        try {
            userName.setText(chatRowObject.getString("EmpName"));
            chatMessage.setText(chatRowObject.getString("Discussion"));
            dept.setText(chatRowObject.getString("EmpDesignation"));
            date.setText(chatRowObject.getString("DateAndTime"));
            if (Boolean.parseBoolean(chatRowObject.getString("delecteDiscussion")))
                delete.setVisibility(View.VISIBLE);
            else
                delete.setVisibility(View.GONE);
            JSONArray array = chatRowObject.getJSONArray("img");
            for (int i = 0; i < array.length(); i++) {
                attach.addView(addAttachTable(array.getJSONObject(i).getString("imgId")));
            }
            String userimage_id = chatRowObject.getString("ImageId");
            if (!userimage_id.equals("")) {
                imgLoader.DisplayImage(AppContants.imageurl + userimage_id, loader, userImage);
            }
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tasDisId = "";
                    String taschId = "";
                    try {
                        tasDisId = chatRowObject.getString("DiscussionId");
                        taschId = chatRowObject.getString("TaskId");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'DISCUSSION_DELETE','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + taschId + "', 'taskChild':'" + tasDisId + "','type':'" + disType + "'}";
                    System.out.println(requestParameter);
                    if (disType.equalsIgnoreCase("group"))
                        getLoadData(requestParameter, "GROUPCHATDELETE");
                    else
                        getLoadData(requestParameter, "GENERALCHATDELETE");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return discussionRowView;
    }
    private TableRow addAttachTable(String attach) {
        TableRow tableRow = new TableRow(context);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.FILL_PARENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        tableRow.setGravity(Gravity.CENTER_VERTICAL);
        tableRow.setLayoutParams(layoutParams);
        tableRow.setOrientation(LinearLayout.HORIZONTAL);
        tableRow.setWeightSum(5);
        //tableRow.setPadding(5, 5, 5, 5);
        int padding_Value =  (int) getResources().getDimension(R.dimen.dynamic_text_padding);
        tableRow.setPadding(padding_Value, padding_Value, padding_Value, padding_Value);
        ImageView button = new ImageView(context);
        TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
        button.setLayoutParams(layoutParams1);
        button.setImageResource(R.drawable.tms_attachment);
        tableRow.addView(button, 0);
        tableRow.addView(addTextViewToTableRowWhite(attach, null, 4), 1);
        return tableRow;
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
    public void showPlayAlertDialog(String msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        /*TextView tv = new TextView(context);
        tv.setText("Status");
        tv.setPadding(30, 30, 30, 30);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);*/
        builder1.setTitle("Status");
        builder1.setMessage(msg);
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
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
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.Filter_textsize));
                //btnPositive.setTextSize(40);
                Button btnNegative = alert11.getButton(Dialog.BUTTON_NEGATIVE);
                btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.Filter_textsize));
                //btnNegative.setTextSize(40);
                Button btnNeutral = alert11.getButton(Dialog.BUTTON_NEUTRAL);
                btnNeutral.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.Filter_textsize));
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
    private void parseJSONResponse(JSONObject response, String flag) {
        try {
            if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_1)) {
                if (flag.equalsIgnoreCase("VIEW")) {
                    if (subTaskDialog != null)
                        if (subTaskDialog.getDialog().isShowing())
                            subTaskDialog.dismiss();
                    try {
                        loadResponse = response;
                        tabJsonObject = new JSONObject();
                        viewMode = Boolean.parseBoolean(response.getString("View"));
                        editMode = Boolean.parseBoolean(response.getString("editMode"));
                        taskIdValue = Integer.parseInt(response.getString("Taskid"));
                        TMSFragmentActivity.actionBarTitle.setText(response.getString("header"));
                        JSONObject downMenuObject = response.getJSONObject("DownMenus");
                        tabJsonObject = downMenuObject.getJSONObject("TaskOtherTableDetails");
                        if (Boolean.parseBoolean(downMenuObject.getString("subTask")))
                            subTask.setVisibility(View.VISIBLE);
                        else
                            subTask.setVisibility(View.GONE);
                        if (Boolean.parseBoolean(downMenuObject.getString("history")))
                            history.setVisibility(View.VISIBLE);
                        else
                            history.setVisibility(View.GONE);
                        if (Boolean.parseBoolean(downMenuObject.getString("fileManager")))
                            fileManager.setVisibility(View.VISIBLE);
                        else
                            fileManager.setVisibility(View.GONE);
                        if (Boolean.parseBoolean(downMenuObject.getString("discussion"))) {
                            discussion.setVisibility(View.VISIBLE);
                            JSONObject DiscussionObject = tabJsonObject.getJSONObject("Discussion");
                            groupDiscussionConversation = DiscussionObject.getJSONArray("groupDiscussionConversation");
                            JSONObject generalDiscussionConversation = DiscussionObject.getJSONObject("generalDiscussionConversation");
                            generalDiscussionObj = generalDiscussionConversation.getJSONArray("generalDiscussionObj");
                        } else
                            discussion.setVisibility(View.GONE);
                        if (Boolean.parseBoolean(downMenuObject.getString("timeExtension")))
                            timeExtension.setVisibility(View.VISIBLE);
                        else
                            timeExtension.setVisibility(View.GONE);
                        if (Boolean.parseBoolean(downMenuObject.getString("timeSheet")))
                            timeSheet.setVisibility(View.VISIBLE);
                        else
                            timeSheet.setVisibility(View.GONE);
                        if (Boolean.parseBoolean(downMenuObject.getString("rating")))
                            ratting.setVisibility(View.VISIBLE);
                        else
                            ratting.setVisibility(View.GONE);
                        if (Boolean.parseBoolean(downMenuObject.getString("taskStatus")))
                            taskStatus.setVisibility(View.VISIBLE);
                        else
                            taskStatus.setVisibility(View.GONE);
                        if (Boolean.parseBoolean(downMenuObject.getString("Negotiation")))
                            negotiation.setVisibility(View.VISIBLE);
                        else
                            negotiation.setVisibility(View.GONE);
                        if (viewMode && editMode) {
                            editBtn.setVisibility(View.VISIBLE);
                            updateBtn.setVisibility(View.GONE);
                        } else if (!viewMode && editMode) {
                            updateBtn.setText("Update");
                            updateBtn.setVisibility(View.VISIBLE);
                            editBtn.setVisibility(View.GONE);
                        } else if (!viewMode && !editMode) {
                            updateBtn.setVisibility(View.VISIBLE);
                            updateBtn.setText("Create");
                            editBtn.setVisibility(View.GONE);
                        } else {
                            updateBtn.setVisibility(View.GONE);
                            editBtn.setVisibility(View.GONE);
                        }
                        // Clone Display Button
                        if (clone) {
                            updateBtn.setVisibility(View.VISIBLE);
                            updateBtn.setText("Create");
                            editBtn.setVisibility(View.GONE);
                        }
                        JSONArray jsonArray = response.getJSONArray("Values");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final LinearLayout tableRow = new TableLayout(context);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
                            layoutParams.gravity = Gravity.CENTER_VERTICAL;
                            tableRow.setLayoutParams(layoutParams);
                            tableRow.setOrientation(LinearLayout.VERTICAL);
                            tableRow.setBackground(getResources().getDrawable(R.drawable.sublist_border_only));
                            final LinearLayout headtableRow = new TableLayout(context);
                            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
                            layoutParams1.gravity = Gravity.CENTER_VERTICAL;
                            layoutParams1.setMargins(10, 10, 10, 0);
                            headtableRow.setLayoutParams(layoutParams1);
                            headtableRow.setOrientation(LinearLayout.VERTICAL);
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            ArrayList<HashMap<String, String>> typeArrays = new ArrayList<>();
                            String head = jsonObject1.getString("Head");
                            TableLayout headLayout = new TableLayout(context);
                            headLayout = headdynamicField(head);
                            headLayout.setBackgroundColor(getResources().getColor(R.color.light_grey_bg));
                            headtableRow.addView(headLayout);
                            JSONObject ResultValues = jsonObject1.getJSONObject("ResultValues");
                            if (head.equalsIgnoreCase("Progress")) {
                                Boolean holdButton = Boolean.parseBoolean(ResultValues.getString("holdButton"));
                                Boolean completeButton = Boolean.parseBoolean(ResultValues.getString("completeButton"));
                                Boolean playButton = Boolean.parseBoolean(ResultValues.getString("playButton"));
                                tableRow.addView(dynamicProgressButton("Hold", holdButton, completeButton, playButton));
                            } else {
                                JSONArray jsonArray1 = ResultValues.getJSONArray("values");
                                for (int j = 0; j < jsonArray1.length(); j++) {
                                    ArrayList<HashMap<String, String>> typeArrayList = new ArrayList<>();
                                    JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                                    String typeForm = jsonObject2.getString("DataType");
                                    if (viewMode) {
                                        if (typeForm.equalsIgnoreCase("CHECKBOX"))
                                            typeForm = "CHECKBOX";
                                        else if (typeForm.equalsIgnoreCase("COL_PIC"))
                                            typeForm = "COL_PIC";
                                        else
                                            typeForm = "TEXTVIEW";
                                    }
                                    Boolean isMandatory = Boolean.parseBoolean(jsonObject2.getString("IsMandatory"));
                                    int lineNo = Integer.parseInt(jsonObject2.getString("lineNo"));
                                    JSONObject fieldSettings = jsonObject2.getJSONObject("fieldSettings");
                                    JSONArray valueLists = fieldSettings.getJSONArray("values");
                                    String label = fieldSettings.getString("label");
                                    String fieldMutiple = "false";
                                    if (!viewMode)
                                        fieldMutiple = fieldSettings.getString("isMultiple");
                                    for (int s = 0; s < valueLists.length(); s++) {
                                        JSONObject jsonObject = valueLists.getJSONObject(s);
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("id", jsonObject.getString("id"));
                                        hashMap.put("value", jsonObject.getString("value"));
                                        Log.d("Loading----", +i + "  " + j + "  " + s);
                                        hashMap.put("selected", jsonObject.getString("selected"));
                                        typeArrayList.add(new HashMap<String, String>(hashMap));
                                    }
                                    loadHashValues.put(label, typeArrayList);
                                    if (lineNo > 1 && !viewMode)
                                        addViewAttach(typeArrayList);
                                    else {
                                        tableRow.addView(dynamicField(typeForm, "", typeArrayList, fieldMutiple, label, isMandatory));
                                        if (head.equalsIgnoreCase("Task") && !viewMode && editMode && ((j + 1) == jsonArray1.length())) {
                                            String status = response.getString("status");
                                            JSONArray array = new JSONArray(status.toString());
                                            ArrayList<HashMap<String, String>> typeArrayList1 = new ArrayList<>();
                                            for (int s = 0; s < array.length(); s++) {
                                                JSONObject jsonObject = array.getJSONObject(s);
                                                HashMap<String, String> hashMap = new HashMap<>();
                                                hashMap.put("id", jsonObject.getString("id"));
                                                hashMap.put("value", jsonObject.getString("value"));
                                                Log.d("Loading----", +i + "  " + j + "  " + s);
                                                hashMap.put("selected", jsonObject.getString("selected"));
                                                typeArrayList1.add(new HashMap<String, String>(hashMap));
                                            }
                                            loadHashValues.put("Status", typeArrayList1);
                                            tableRow.addView(dynamicField("DROPDOWN_S", "", typeArrayList1, "false", "Status", isMandatory));
                                        } else if (head.equalsIgnoreCase("Task") && ((viewMode && editMode) || (viewMode && !editMode)) && ((j + 1) == jsonArray1.length())) {
                                            String status = response.getString("status");
                                            JSONArray array = new JSONArray(status.toString());
                                            ArrayList<HashMap<String, String>> typeArrayList1 = new ArrayList<>();
                                            for (int s = 0; s < array.length(); s++) {
                                                JSONObject jsonObject = array.getJSONObject(s);
                                                HashMap<String, String> hashMap = new HashMap<>();
                                                hashMap.put("id", jsonObject.getString("id"));
                                                hashMap.put("value", jsonObject.getString("value"));
                                                Log.d("Loading----", +i + "  " + j + "  " + s);
                                                hashMap.put("selected", jsonObject.getString("selected"));
                                                typeArrayList1.add(new HashMap<String, String>(hashMap));
                                            }
                                            loadHashValues.put("Status", typeArrayList1);
                                            tableRow.addView(dynamicField("TEXTVIEW", "", typeArrayList1, "false", "Status", isMandatory));
                                        }
                                    }
                                }
                            }
                            headtableRow.addView(tableRow);
                            tableLayout.addView(headtableRow);
                            headLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("onClick Position---", "" + headtableRow.getChildCount());
                                    ImageView arrow = (ImageView) ((TableRow) ((LinearLayout) headtableRow.getChildAt(0)).getChildAt(0)).getChildAt(1);
                                    if (headtableRow.getChildAt(headtableRow.getChildCount() - 1).getVisibility() == View.VISIBLE) {
                                        headtableRow.getChildAt(headtableRow.getChildCount() - 1).setVisibility(View.GONE);
                                        // EditText editText = (EditText) ((TableRow) ((TableLayout) tableLayout.getChildAt(i)).getChildAt(j)).getChildAt(0);
                                        arrow.setImageResource(R.drawable.plus_icon_gray);
                                    } else {
                                        headtableRow.getChildAt(headtableRow.getChildCount() - 1).setVisibility(View.VISIBLE);
                                        arrow.setImageResource(R.drawable.minus_icon_gray);
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (flag.equalsIgnoreCase("SUBTASKLIST")) {
                    JSONArray subTaskNameJsonArray = response.getJSONArray("Values");
                    subTaskNameArrayList = ApiCalls.getArraylistfromJson(subTaskNameJsonArray.toString());
                    loadLeadStyleSpinner(subTaskSpinner, subTaskNameArrayList, new String[]{"value"}, "Sub Task");
                } else if (flag.equalsIgnoreCase("GENERALCHAT")) {
                    generalList.removeAllViews();
                    generalAttach.removeAllViews();
                    generaledit.setText("");
                    generalDiscussionObj = response.getJSONArray("TableValues");
                    for (int i = 0; i < generalDiscussionObj.length(); i++) {
                        generalList.addView(discussionChat(generalDiscussionObj.getJSONObject(i), "indiv"));
                    }
                    generalAttachDisplay();
                } else if (flag.equalsIgnoreCase("GROUPCHAT")) {
                    groupList.removeAllViews();
                    groupAttach.removeAllViews();
                    groupedit.setText("");
                    groupDiscussionConversation = response.getJSONArray("TableValues");
                    for (int i = 0; i < groupDiscussionConversation.length(); i++) {
                        groupList.addView(discussionChat(groupDiscussionConversation.getJSONObject(i), "group"));
                    }
                    groupAttachDisplay();
                } else if (flag.equalsIgnoreCase("GENERALCHATDELETE")) {
                    generalList.removeAllViews();
                    generalDiscussionObj = response.getJSONArray("TableValues");
                    for (int i = 0; i < generalDiscussionObj.length(); i++) {
                        generalList.addView(discussionChat(generalDiscussionObj.getJSONObject(i), "indiv"));
                    }
                } else if (flag.equalsIgnoreCase("GROUPCHATDELETE")) {
                    groupList.removeAllViews();
                    groupDiscussionConversation = response.getJSONArray("TableValues");
                    for (int i = 0; i < groupDiscussionConversation.length(); i++) {
                        groupList.addView(discussionChat(groupDiscussionConversation.getJSONObject(i), "group"));
                    }
                } else if (flag.equalsIgnoreCase("USERNAME")) {
                    userNameArrayList = ApiCalls.getArraylistfromJson(response.getString("Values"));
                    loadLeadStyleSpinner(tms_userName_spinner, userNameArrayList, new String[]{"value"}, "Use Name");
                } else if (flag.equalsIgnoreCase("FILEMANAGER")) {
                    fileManagerAttachDialog = new Dialog(context);
                    LayoutInflater inflater2 = LayoutInflater.from(context);
                    View convertView = inflater2.inflate(R.layout.tms_filemanager_attach_dialog, null);
                    fileManagerAttachDialog.setContentView(convertView);
                    final Button submit_button = (Button) fileManagerAttachDialog.findViewById(R.id.manager_submit_button);
                    Button cancel_button = (Button) fileManagerAttachDialog.findViewById(R.id.manager_cancel_button);
                    final Spinner tms_subTask_spinner = (Spinner) fileManagerAttachDialog.findViewById(R.id.tms_subTask_spinner);
                    final Spinner tms_taskStage_spinner = (Spinner) fileManagerAttachDialog.findViewById(R.id.tms_taskStage_spinner);
                    tms_userName_spinner = (Spinner) fileManagerAttachDialog.findViewById(R.id.tms_userName_spinner);
                    TextView taskNameText = (TextView) fileManagerAttachDialog.findViewById(R.id.fileManager_taskName);
                    final TableLayout attachManager = (TableLayout) fileManagerAttachDialog.findViewById(R.id.tms_attach_table);
                    fileManagerAttachDialog.setTitle("File Manager Upload");
                    managerAttachDisplay(attachManager);
                    taskNameText.setText(response.getString("TaskName"));
                    final ArrayList<HashMap<String, String>> subTasktArrayList = ApiCalls.getArraylistfromJson(response.getString("SubTaskNameValue"));
                    loadLeadStyleSpinner(tms_subTask_spinner, subTasktArrayList, new String[]{"value"}, "Sub Task");
                    for (int i = 0; i < subTasktArrayList.size(); i++) {
                        if (subTasktArrayList.get(i).get("selected").equalsIgnoreCase("true"))
                            tms_subTask_spinner.setSelection(i + 1);
                    }
                    final ArrayList<HashMap<String, String>> taskStageArrayList = ApiCalls.getArraylistfromJson(response.getString("TaskStage"));
                    loadLeadStyleSpinner(tms_taskStage_spinner, taskStageArrayList, new String[]{"value"}, "Task Stage");
                    for (int i = 0; i < taskStageArrayList.size(); i++) {
                        if (taskStageArrayList.get(i).get("selected").equalsIgnoreCase("true"))
                            tms_taskStage_spinner.setSelection(i + 1);
                    }
                    userNameArrayList = ApiCalls.getArraylistfromJson(response.getString("UserName"));
                    loadLeadStyleSpinner(tms_userName_spinner, userNameArrayList, new String[]{"value"}, "User Name");
                    for (int i = 0; i < userNameArrayList.size(); i++) {
                        if (userNameArrayList.get(i).get("selected").equalsIgnoreCase("true"))
                            tms_userName_spinner.setSelection(i + 1);
                    }
                    tms_subTask_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position > 0) {
                                int selectSpinnerPosition = (tms_subTask_spinner.getSelectedItemPosition()) - 1;
                                String subTasId = subTasktArrayList.get(selectSpinnerPosition).get("id");
                                requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'FILE_LOAD_ON_PROCESS','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + taskId + "', 'subtaskid':'" + subTasId + "'}";
                                System.out.println(requestParameter);
                                getLoadData(requestParameter, "USERNAME");
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    submit_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (tms_taskStage_spinner.getSelectedItemPosition() > 0) {
                                if (tms_userName_spinner.getSelectedItemPosition() > 0) {
                                    String managerAttachValue = "";
                                    for (int a = 0; a < attachManager.getChildCount(); a++) {
                                        TableRow attachRow = ((TableRow) attachManager.getChildAt(a));
                                        EditText editText = ((EditText) ((TableRow) attachRow.getChildAt(0)).getChildAt(0));
                                        if (editText.getText().toString().trim().length() > 0) {
                                            if (managerAttachValue.equalsIgnoreCase(""))
                                                managerAttachValue = "" + editText.getId();
                                            else
                                                managerAttachValue = managerAttachValue + "," + editText.getId();
                                        }
                                    }
                                    if (!managerAttachValue.equalsIgnoreCase("")) {
                                        int taskStageSpinnerPosition = (tms_taskStage_spinner.getSelectedItemPosition()) - 1;
                                        String taskStageId = taskStageArrayList.get(taskStageSpinnerPosition).get("id");
                                        String subTasId = "0";
                                        if (tms_subTask_spinner.getSelectedItemPosition() > 0) {
                                            int subTaskSpinnerPosition = (tms_subTask_spinner.getSelectedItemPosition()) - 1;
                                            subTasId = subTasktArrayList.get(subTaskSpinnerPosition).get("id");
                                        }
                                        int userSpinnerPosition = (tms_userName_spinner.getSelectedItemPosition()) - 1;
                                        String userId = userNameArrayList.get(userSpinnerPosition).get("id");
                                        requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'SAVE_FILE_ADD','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + taskId + "', 'subtask_name':'" + subTasId + "', 'attachimage_image':'" + managerAttachValue + "', 'user_name':'" + userId + "', 'task_stage':'" + taskStageId + "', 'task_file_id':'" + 0 + "'}";
                                        System.out.println(requestParameter);
                                        getLoadData(requestParameter, "FILEMANAGER_SUBMIT");
                                    } else {
                                        EditText editText = ((EditText) ((TableRow) ((TableRow) attachManager.getChildAt(0)).getChildAt(0)).getChildAt(0));
                                        editText.setError("Field is Required");
                                        YoYo.with(Techniques.Bounce)
                                                .duration(700)
                                                .playOn(editText);
                                        YoYo.with(Techniques.Shake)
                                                .duration(700)
                                                .playOn(submit_button);
                                    }
                                } else {
                                    TextView errorText = (TextView) tms_userName_spinner.getSelectedView();
                                    errorText.setError("Field is Required");
                                    tms_userName_spinner.requestFocus(View.FOCUS_UP);
                                    tms_userName_spinner.requestFocusFromTouch();
                                    YoYo.with(Techniques.Bounce)
                                            .duration(700)
                                            .playOn(tms_userName_spinner);
                                    YoYo.with(Techniques.Shake)
                                            .duration(700)
                                            .playOn(submit_button);
                                }
                            } else {
                                TextView errorText = (TextView) tms_taskStage_spinner.getSelectedView();
                                errorText.setError("Field is Required");
                                tms_taskStage_spinner.requestFocus(View.FOCUS_UP);
                                tms_taskStage_spinner.requestFocusFromTouch();
                                YoYo.with(Techniques.Bounce)
                                        .duration(700)
                                        .playOn(tms_taskStage_spinner);
                                YoYo.with(Techniques.Shake)
                                        .duration(700)
                                        .playOn(submit_button);
                            }
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fileManagerAttachDialog.cancel();
                        }
                    });
                    fileManagerAttachDialog.show();
                } else if (flag.equalsIgnoreCase("FILEMANAGER_SUBMIT")) {
                    fileManagerAttachDialog.dismiss();
                    if (fileManagerDialog != null)
                        if (fileManagerDialog.getDialog().isShowing())
                            fileManagerDialog.dismiss();
                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_DISPLAY_TASK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + taskId + "', 'Editable':'false'}";
                    System.out.println(requestParameter);
                    getLoadData(requestParameter, "VIEW");
                    tableLayout.removeAllViews();
                } else if (flag.equalsIgnoreCase("Department")) {
                    ArrayList<HashMap<String, String>> optionsArrayList = new ArrayList<>();
                    optionsArrayList = ApiCalls.getArraylistfromJson(response.getString("Options"));
                    loadHashValues.put(flag, optionsArrayList);
                    loadLeadStyleSpinner(departmentSpinner, optionsArrayList, new String[]{"value"}, flag);
                    ArrayList<HashMap<String, String>> IndividualArrayList = new ArrayList<>();
                    IndividualArrayList = ApiCalls.getArraylistfromJson(response.getString("Individual"));
                    loadHashValues.put("Individual", IndividualArrayList);
                    loadLeadStyleSpinner(individualSpinner, IndividualArrayList, new String[]{"value"}, "Individual");
                    ArrayList<HashMap<String, String>> emptyArrayList = new ArrayList<>();
                    loadHashValues.put("Designation", emptyArrayList);
                    loadLeadStyleSpinner(designationSpinner, emptyArrayList, new String[]{"value"}, "Designation");
                    loadHashValues.put("Wing", emptyArrayList);
                    loadLeadStyleSpinner(wingSpinner, emptyArrayList, new String[]{"value"}, "Wing");
                } else if (flag.equalsIgnoreCase("Wing")) {
                    ArrayList<HashMap<String, String>> optionsArrayList = new ArrayList<>();
                    optionsArrayList = ApiCalls.getArraylistfromJson(response.getString("Options"));
                    ArrayList<HashMap<String, String>> IndividualArrayList = new ArrayList<>();
                    IndividualArrayList = ApiCalls.getArraylistfromJson(response.getString("Individual"));
                    loadHashValues.put(flag, optionsArrayList);
                    loadHashValues.put("Individual", IndividualArrayList);
                    loadLeadStyleSpinner(wingSpinner, optionsArrayList, new String[]{"value"}, flag);
                    loadLeadStyleSpinner(individualSpinner, IndividualArrayList, new String[]{"value"}, "Individual");
                    ArrayList<HashMap<String, String>> emptyArrayList = new ArrayList<>();
                    loadHashValues.put("Designation", emptyArrayList);
                    loadLeadStyleSpinner(designationSpinner, emptyArrayList, new String[]{"value"}, "Designation");
                } else if (flag.equalsIgnoreCase("Designation")) {
                    ArrayList<HashMap<String, String>> optionsArrayList = new ArrayList<>();
                    optionsArrayList = ApiCalls.getArraylistfromJson(response.getString("Options"));
                    ArrayList<HashMap<String, String>> IndividualArrayList = new ArrayList<>();
                    IndividualArrayList = ApiCalls.getArraylistfromJson(response.getString("Individual"));
                    loadHashValues.put(flag, optionsArrayList);
                    loadHashValues.put("Individual", IndividualArrayList);
                    loadLeadStyleSpinner(designationSpinner, optionsArrayList, new String[]{"value"}, flag);
                    loadLeadStyleSpinner(individualSpinner, IndividualArrayList, new String[]{"value"}, "Individual");
                } else if (flag.equalsIgnoreCase("Individual")) {
                    ArrayList<HashMap<String, String>> optionsArrayList = new ArrayList<>();
                    optionsArrayList = ApiCalls.getArraylistfromJson(response.getString("Options"));
                    ArrayList<HashMap<String, String>> IndividualArrayList = new ArrayList<>();
                    IndividualArrayList = ApiCalls.getArraylistfromJson(response.getString("Individual"));
                    loadHashValues.put(flag, optionsArrayList);
                    loadLeadStyleSpinner(individualSpinner, optionsArrayList, new String[]{"value"}, flag);
                } else if (flag.equalsIgnoreCase("SUBMIT")) {
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                    taskId = response.getString("TaskId");
                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_DISPLAY_TASK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + taskId + "', 'Editable':'true'}";
                    System.out.println(requestParameter);
                    getLoadData(requestParameter, "VIEW");
                    tableLayout.removeAllViews();
                } else if (flag.equalsIgnoreCase("CLONE")) {
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                    Bundle bundle = new Bundle();
                    bundle.putString("listType", listType);
                    navigateToNextFragment(TMSListTreeActivity.class.getName(), bundle);
                } else if (flag.equalsIgnoreCase("Hold") || (flag.equalsIgnoreCase("Play")) || (flag.equalsIgnoreCase("Completed"))) {
                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_DISPLAY_TASK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + taskId + "', 'Editable':'false'}";
                    System.out.println(requestParameter);
                    getLoadData(requestParameter, "VIEW");
                    tableLayout.removeAllViews();
                    if (holdDialog != null)
                        if (holdDialog.isShowing())
                            holdDialog.cancel();
                } else if (flag.equalsIgnoreCase("Folder")) {
                    ArrayList<HashMap<String, String>> parentArrayList = new ArrayList<>();
                    parentArrayList = ApiCalls.getArraylistfromJson(response.getString("ParentFolder"));
                    folderDialog = new Dialog(context);
                    LayoutInflater inflater2 = getLayoutInflater(null);
                    View convertView = inflater2.inflate(R.layout.tms_add_folder_dialog, null);
                    folderDialog.setContentView(convertView);
                    final Button yes_button = (Button) folderDialog.findViewById(R.id.folder_submit_button);
                    Button no_button = (Button) folderDialog.findViewById(R.id.folder_cancel_button);
                    final EditText folderText = (EditText) folderDialog.findViewById(R.id.tms_folder_edittext);
                    final Spinner parentSpinner = (Spinner) folderDialog.findViewById(R.id.tms_parent_spinner);
                    loadLeadStyleSpinner(parentSpinner, parentArrayList, new String[]{"value"}, "Parent");
                    folderDialog.setTitle("Add Folder");
                    final ArrayList<HashMap<String, String>> finalParentArrayList = parentArrayList;
                    yes_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String folderSpinnerValue = "0";
                            if ((parentSpinner.getSelectedItemPosition()) > 0) {
                                int selectSpinnerPosition = (parentSpinner.getSelectedItemPosition() - 1);
                                folderSpinnerValue = finalParentArrayList.get(selectSpinnerPosition).get("id");
                            }
                            if (folderText.getText().toString().trim().length() > 0) {
                                requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'FOLDER_SAVE_UPDATE','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + taskId + "', 'folder_name':'" + folderText.getText().toString().trim() + "', 'folder_id':'0', 'parent_folder':'" + folderSpinnerValue + "', 'actionModal':'', 'frm_config_id':'1','type':'task'}";
                                System.out.println(requestParameter);
                                getLoadData(requestParameter, "FolderSubmit");
                            } else {
                                YoYo.with(Techniques.Bounce)
                                        .duration(700)
                                        .playOn(folderText);
                                YoYo.with(Techniques.Shake)
                                        .duration(700)
                                        .playOn(yes_button);
                            }
                        }
                    });
                    no_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            folderDialog.cancel();
                        }
                    });
                    folderDialog.show();
                } else if (flag.equalsIgnoreCase("FolderSubmit")) {
                    folderDialog.cancel();
                    ArrayList<HashMap<String, String>> folderArrayList = new ArrayList<>();
                    folderArrayList = ApiCalls.getArraylistfromJson(response.getString("Folder"));
                    loadHashValues.put("Folder", folderArrayList);
                    loadLeadStyleSpinner(folderSpinner, folderArrayList, new String[]{"value"}, "Folder");
                    if (filterValue.containsKey("FolderPosition")) {
                        folderSpinner.setSelection(Integer.parseInt(filterValue.get("FolderPosition")));
                    }
                    folderSpinner.requestFocus(View.FOCUS_UP);
                    folderSpinner.requestFocusFromTouch();
                } else if (flag.equalsIgnoreCase("DeleteAlert")) {
                    showDeleteAlertDialog(response.getString(AppContants.RESPONSE_MESSAGE), response.getString("TaskId"), "1");
                } else if (flag.equalsIgnoreCase("Delete")) {
                    if (subTaskDialog != null)
                        if (subTaskDialog.getDialog().isShowing())
                            subTaskDialog.dismiss();
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                } else {
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                }
            } else if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase("0")) {
                if (flag.equalsIgnoreCase("DeleteAlert")) {
                    showDeleteAlertDialog(response.getString(AppContants.RESPONSE_MESSAGE), "0", "0");
                } else
                    showPlayAlertDialog(response.getString(AppContants.RESPONSE_MESSAGE));
            } else if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_403)) {
                showSessionScreensDialog(context, response.getString(AppContants.RESPONSE_MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public TreeNode addChildRattingTree(JSONArray parentNode, TreeNode parentTreeNode) {
        try {
            for (int p = 0; p < parentNode.length(); p++) {
                //JSONArray level1Tree = parentNode.getJSONObject(p).getJSONArray("ChildValues");
                JSONArray level1Tree = parentNode;
                //for (JSONArray level1Tree : parentNode.g()) {
                TreeNode level1TreeNode;
                JSONObject tableValRow = level1Tree.getJSONObject(p);
                JSONObject tableRow = tableValRow.getJSONObject("Value");
                String stars = tableRow.getString("Stars");
                JSONArray parentArray = tableRow.getJSONArray("Values");
                level1Tree = level1Tree.getJSONObject(p).getJSONArray("ChildValues");
                if (level1Tree.length() != 0) {
                    level1TreeNode = new TreeNode(new TMSRattingTreeAdapter.TreeItem(parentArray, true, stars));
                    addChildRattingTree(level1Tree, level1TreeNode);
                } else {
                    level1TreeNode = new TreeNode(new TMSRattingTreeAdapter.TreeItem(parentArray, false, stars));
                }
                parentTreeNode.addChild(level1TreeNode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parentTreeNode;
    }
    public TreeNode addChildTree(JSONArray parentNode, TreeNode parentTreeNode) {
        try {
            for (int p = 0; p < parentNode.length(); p++) {
                //JSONArray level1Tree = parentNode.getJSONObject(p).getJSONArray("ChildValues");
                JSONArray level1Tree = parentNode;
                //for (JSONArray level1Tree : parentNode.g()) {
                TreeNode level1TreeNode;
                JSONObject tableValRow = level1Tree.getJSONObject(p);
                JSONObject tableRow = tableValRow.getJSONObject("Value");
                String childTaskId = tableRow.getString("TaskId");
                Boolean isView = Boolean.parseBoolean(tableRow.getString("isView"));
                Boolean isDelete = Boolean.parseBoolean(tableRow.getString("isDelete"));
                JSONArray parentArray = tableRow.getJSONArray("Values");
                level1Tree = level1Tree.getJSONObject(p).getJSONArray("ChildValues");
                if (level1Tree.length() != 0) {
                    level1TreeNode = new TreeNode(new TMSSubTaskDialogAdapter.TreeItem(parentArray, new TMSListReload() {
                        @Override
                        public void onReloadList(String subTaskId) {
                            requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_DELETE_ALERT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + subTaskId + "'}";
                            System.out.println(requestParameter);
                            getLoadData(requestParameter, "DeleteAlert");
                        }
                        @Override
                        public void onMoveEditPage(Bundle bundle) {
                            loadTaskView(bundle);
                        }
                    }, true, isView, isDelete, childTaskId));
                    addChildTree(level1Tree, level1TreeNode);
                } else {
                    level1TreeNode = new TreeNode(new TMSSubTaskDialogAdapter.TreeItem(parentArray, new TMSListReload() {
                        @Override
                        public void onReloadList(String subTaskId) {
                            requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_DELETE_ALERT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + subTaskId + "'}";
                            System.out.println(requestParameter);
                            getLoadData(requestParameter, "DeleteAlert");
                        }
                        @Override
                        public void onMoveEditPage(Bundle bundle) {
                            loadTaskView(bundle);
                        }
                    }, false, isView, isDelete, childTaskId));
                }
                parentTreeNode.addChild(level1TreeNode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parentTreeNode;
    }
    public void loadTaskView(Bundle bundle) {
        if (subTaskDialog != null)
            if (subTaskDialog.getDialog().isShowing())
                subTaskDialog.dismiss();
        bundle.putString("listType", listType);
        navigateToNextFragment(TMSAddAndViewActivity.class.getName(), bundle);
    }
    public TableRow dynamicProgressButton(String displayName, Boolean holdButton, Boolean completeButton, Boolean playButton) {
        final TableRow tableRow = new TableRow(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        tableRow.setLayoutParams(layoutParams);
        tableRow.setOrientation(LinearLayout.VERTICAL);
        //tableRow.setPadding(5, 5, 5, 5);
        int padding_Value =  (int) getResources().getDimension(R.dimen.addNonEditTextToTableRow);
        tableRow.setPadding(padding_Value, padding_Value, padding_Value, padding_Value);
        if (holdButton) {
            ImageView holdImageButton = addHoldButton("Hold", 2);
            tableRow.addView(holdImageButton);
            holdImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holdDialog = new Dialog(context);
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
                                requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_PROGRESS_HOLD_SUBMIT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'taskId':'" + taskId + "', 'reason':'" + reasonText.getText().toString().trim() + "'}";
                                System.out.println(requestParameter);
                                getLoadData(requestParameter, "Hold");
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
        }
        if (completeButton) {
            ImageView completeImageButton = addCompleteButton("Completed", 2);
            tableRow.addView(completeImageButton);
            completeImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_PROGRESS_OTHER_SUBMIT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'taskId':'" + taskId + "', 'type':'Completed'}";
                    System.out.println(requestParameter);
                    getLoadData(requestParameter, "Completed");
                }
            });
        }
        if (playButton) {
            ImageView playImageButton = addPlayButton("Play", 2);
            tableRow.addView(playImageButton);
            playImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TASK_PROGRESS_OTHER_SUBMIT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'taskId':'" + taskId + "', 'type':'InProgress'}";
                    System.out.println(requestParameter);
                    getLoadData(requestParameter, "Play");
                }
            });
        }
        return tableRow;
    }
    public TableLayout headdynamicField(String headName) {
        final TableLayout tableRow = new TableLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        tableRow.setLayoutParams(layoutParams);
        tableRow.setOrientation(LinearLayout.VERTICAL);
        // tableRow.setWeightSum(sumWeight);
        //tableRow.setPadding(5, 5, 5, 5);
        int padding_Value =  (int) getResources().getDimension(R.dimen.dynamic_text_padding);
        tableRow.setPadding(padding_Value, padding_Value, padding_Value, padding_Value);
        final TableRow tableRow1 = new TableRow(context);
        TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
        tableRow1.setLayoutParams(layoutParams1);
        tableRow1.setOrientation(LinearLayout.HORIZONTAL);
        TextView textView = addTextViewToTableRow(headName, false, null, 5);
        textView.setTextColor(getResources().getColor(R.color.grey_text_tms));
        textView.setTypeface(Typeface.DEFAULT_BOLD);
       // textView.setTextSize(16);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.dynamic_header));
        tableRow1.addView(textView);
        ImageView imageView = addViewToTableRow(1);
        imageView.setImageResource(R.drawable.minus_icon_gray);
        tableRow1.addView(imageView, 1);
        tableRow.addView(tableRow1);
        return tableRow;
    }
    public TableLayout dynamicField(final String type, final String value, final ArrayList<HashMap<String, String>> arrayListValue, final String fieldMutiple, final String label, Boolean isMandatory) {
        final TableLayout tableRow = new TableLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        tableRow.setLayoutParams(layoutParams);
        tableRow.setOrientation(LinearLayout.VERTICAL);
        // tableRow.setWeightSum(sumWeight);
        //tableRow.setPadding(2, 2, 2, 2);
        int padding_Value =  (int) getResources().getDimension(R.dimen.dynamic_text_padding);
        tableRow.setPadding(padding_Value, padding_Value, padding_Value, padding_Value);
        if (type.equalsIgnoreCase("TEXTVIEW")) {
            final TableRow tableRow1 = new TableRow(context);
            TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER_VERTICAL;
            tableRow1.setLayoutParams(layoutParams1);
            tableRow1.setOrientation(LinearLayout.HORIZONTAL);
            String displayVal = "";
            for (int val = 0; val < arrayListValue.size(); val++) {
                if (arrayListValue.get(val).get("selected").equalsIgnoreCase("true"))
                    if (displayVal.equalsIgnoreCase(""))
                        displayVal = arrayListValue.get(val).get("value");
                    else
                        displayVal = displayVal + "," + arrayListValue.get(val).get("value");
            }
            if (label.equalsIgnoreCase("")) {//Nothing TO Show
            } else {
                TextView labelText = addTextViewToTableRow(label, false, null, 4);
                labelText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
                labelText.setTextColor(getResources().getColor(R.color.grey_text_tms));
                tableRow1.addView(labelText);
                // tableRow1.addView(addTextViewToTableRow(":", null, 1));
            }
            TextView textView = addTextViewToTableRow(displayVal, false, null, 6);
            textView.setBackgroundColor(getResources().getColor(R.color.white));
            tableRow1.addView(textView);
            tableRow.addView(tableRow1);
        } else if (type.equalsIgnoreCase("TEXTBOX")) {
            final TableRow tableRow1 = new TableRow(context);
            TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER_VERTICAL;
            tableRow1.setLayoutParams(layoutParams1);
            tableRow1.setOrientation(LinearLayout.HORIZONTAL);
            TableRow ansRow = new TableRow(context);
            ansRow.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
            ansRow.setBackgroundColor(getResources().getColor(R.color.white));
            String displayVal = "";
            for (int val = 0; val < arrayListValue.size(); val++) {
                if (arrayListValue.get(val).get("selected").equalsIgnoreCase("true"))
                    if (displayVal.equalsIgnoreCase(""))
                        displayVal = arrayListValue.get(val).get("value");
                    else
                        displayVal = displayVal + "," + arrayListValue.get(val).get("value");
            }
            if (label.equalsIgnoreCase("")) {//Nothing TO Show
            } else {
                TextView labelText = addTextViewToTableRow(label, isMandatory, null, 4);
                labelText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
                labelText.setTextColor(getResources().getColor(R.color.grey_text_tms));
                tableRow1.addView(labelText);
                // tableRow1.addView(addTextViewToTableRow(":", null, 1));
            }
            ansRow.addView(addEditTextToTableRow1(label, displayVal, null, 6));
            if (fieldMutiple.equalsIgnoreCase("true")) {
                ImageView imageView = addViewToTableRow(1);
                ansRow.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TableRow tableRow1 = new TableRow(context);
                        TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
                        tableRow1.setLayoutParams(layoutParams1);
                        tableRow1.setOrientation(LinearLayout.HORIZONTAL);
                        int cc = -1;
                        tableRow1.addView(addEditTextToTableRow1(label, value, null, 5), cc++);
                        ImageView imageView = addViewToTableRow(0);
                        tableRow1.addView(imageView, 1);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tableRow.removeView(tableRow1);
                            }
                        });
                        tableRow.addView(tableRow1);
                    }
                });
            }
            tableRow1.addView(ansRow);
            tableRow.addView(tableRow1);
        } else if (type.equalsIgnoreCase("TEXTAREA")) {
            final TableRow tableRow1 = new TableRow(context);
            TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER_VERTICAL;
            tableRow1.setLayoutParams(layoutParams1);
            tableRow1.setOrientation(LinearLayout.HORIZONTAL);
            TableRow ansRow = new TableRow(context);
            ansRow.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
            ansRow.setBackgroundColor(getResources().getColor(R.color.white));
            String displayVal = "";
            for (int val = 0; val < arrayListValue.size(); val++) {
                if (arrayListValue.get(val).get("selected").equalsIgnoreCase("true"))
                    if (displayVal.equalsIgnoreCase(""))
                        displayVal = arrayListValue.get(val).get("value");
                    else
                        displayVal = displayVal + "," + arrayListValue.get(val).get("value");
            }
            if (label.equalsIgnoreCase("")) {//Nothing TO Show
            } else {
                TextView labelText = addTextViewToTableRow(label, isMandatory, null, 4);
                labelText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
                labelText.setTextColor(getResources().getColor(R.color.grey_text_tms));
                tableRow1.addView(labelText);
                // tableRow1.addView(addTextViewToTableRow(":", null, 1));
            }
            ansRow.addView(addEditTextAreaToTableRow(label, displayVal, null, 6));
            if (fieldMutiple.equalsIgnoreCase("true")) {
                ImageView imageView = addViewToTableRow(1);
                ansRow.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TableRow tableRow1 = new TableRow(context);
                        TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
                        tableRow1.setLayoutParams(layoutParams1);
                        tableRow1.setOrientation(LinearLayout.HORIZONTAL);
                        int cc = -1;
                        tableRow1.addView(addEditTextAreaToTableRow(label, value, null, 5), cc++);
                        ImageView imageView = addViewToTableRow(0);
                        tableRow1.addView(imageView, 1);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tableRow.removeView(tableRow1);
                            }
                        });
                        tableRow.addView(tableRow1);
                    }
                });
            }
            tableRow1.addView(ansRow);
            tableRow.addView(tableRow1);
        } else if (type.equalsIgnoreCase("COL_PIC")) {
            final TableRow tableRow1 = new TableRow(context);
            TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER_VERTICAL;
            tableRow1.setLayoutParams(layoutParams1);
            tableRow1.setOrientation(LinearLayout.HORIZONTAL);
            //tableRow1.setBackgroundColor(getResources().getColor(R.color.white));
            if (label.equalsIgnoreCase("")) {//Nothing TO Show
            } else {
                TextView labelText = addTextViewToTableRow(label, isMandatory, null, 4);
                labelText.setTextColor(getResources().getColor(R.color.grey_text_tms));
                tableRow1.addView(labelText);
                // tableRow1.addView(addTextViewToTableRow(":", null, 1));
            }
            colorCheckBox = addColorCheckBox();
            for (int val = 0; val < arrayListValue.size(); val++) {
                if (arrayListValue.get(val).get("selected").equalsIgnoreCase("true")) {
                    filterValue.put(label, arrayListValue.get(val).get("id"));
                    selectedPosition = val;
                    colorCheckBox.setBackgroundColor(Color.parseColor(arrayListValue.get(val).get("value")));
                }
            }
            final TableRow tableColor = new TableRow(context);
            tableColor.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
            tableColor.addView(colorCheckBox);
            tableRow1.addView(tableColor);
            tableRow.addView(tableRow1);
            colorCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
// custom dialog
                    dialog = new Dialog(context);
                    dialog.setContentView(R.layout.color_dialog);
                    dialog.setTitle("Select Color");
                    if (filterValue.containsKey("selectedColorPosition")) {
                        selectedPosition = Integer.parseInt(filterValue.get("selectedColorPosition"));
                    }
                    GridView gridView = (GridView) dialog.findViewById(R.id.color_gridView);
                    ImageAdapter adapter = new ImageAdapter(context, arrayListValue, selectedPosition, label);
                    gridView.setAdapter(adapter);
                    gridView.setChoiceMode(gridView.CHOICE_MODE_SINGLE);
                    dialog.show();
                }
            });
        } else if (type.equalsIgnoreCase("CHECKBOX")) {
            final TableRow tableRow1 = new TableRow(context);
            TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER_VERTICAL;
            tableRow1.setLayoutParams(layoutParams1);
            tableRow1.setOrientation(LinearLayout.HORIZONTAL);
            final TableRow ansRow = new TableRow(context);
            ansRow.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
            ansRow.setBackgroundColor(getResources().getColor(R.color.white));
            RadioGroup parentRadioGroup;
            if (label.equalsIgnoreCase("Allow Sub Task")) {
                if (arrayListValue.size() > 0) {
                    if (arrayListValue.get(0).get("selected").equalsIgnoreCase("true")) {
                        ArrayList<HashMap<String, String>> DefaultListValue = new ArrayList<>();
                        HashMap<String, String> addRadioList = new HashMap<>();
                        addRadioList.put("id", "2148");
                        addRadioList.put("selected", "true");
                        addRadioList.put("value", "Yes");
                        DefaultListValue.add(addRadioList);
                        HashMap<String, String> addRadioList1 = new HashMap<>();
                        addRadioList1.put("id", "2149");
                        addRadioList1.put("selected", "false");
                        addRadioList1.put("value", "No");
                        DefaultListValue.add(addRadioList1);
                        parentRadioGroup = addRadioGroupToTableRow(DefaultListValue, 6);
                    } else {
                        ArrayList<HashMap<String, String>> DefaultListValue = new ArrayList<>();
                        HashMap<String, String> addRadioList = new HashMap<>();
                        addRadioList.put("id", "2148");
                        addRadioList.put("selected", "false");
                        addRadioList.put("value", "Yes");
                        DefaultListValue.add(addRadioList);
                        HashMap<String, String> addRadioList1 = new HashMap<>();
                        addRadioList1.put("id", "2149");
                        addRadioList1.put("selected", "true");
                        addRadioList1.put("value", "No");
                        DefaultListValue.add(addRadioList1);
                        parentRadioGroup = addRadioGroupToTableRow(DefaultListValue, 6);
                    }
                } else {
                    ArrayList<HashMap<String, String>> DefaultListValue = new ArrayList<>();
                    HashMap<String, String> addRadioList = new HashMap<>();
                    addRadioList.put("id", "2148");
                    addRadioList.put("selected", "false");
                    addRadioList.put("value", "Yes");
                    DefaultListValue.add(addRadioList);
                    HashMap<String, String> addRadioList1 = new HashMap<>();
                    addRadioList1.put("id", "2149");
                    addRadioList1.put("selected", "true");
                    addRadioList1.put("value", "No");
                    DefaultListValue.add(addRadioList1);
                    parentRadioGroup = addRadioGroupToTableRow(DefaultListValue, 6);
                }
            } else {
                parentRadioGroup = addCheckGroupToTableRow(label, arrayListValue, 6);
            }
            if (label.equalsIgnoreCase("")) {//Nothing TO Show
            } else {
                TextView labelText = addTextViewToTableRow(label, isMandatory, null, 4);
                labelText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
                labelText.setTextColor(getResources().getColor(R.color.grey_text_tms));
                tableRow1.addView(labelText);
                //tableRow1.addView(addTextViewToTableRow(":", null, 1));
            }
            ansRow.addView(parentRadioGroup);
            if (fieldMutiple.equalsIgnoreCase("true")) {
                ImageView imageView = addViewToTableRow(1);
                ansRow.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TableRow tableRow1 = new TableRow(context);
                        TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
                        tableRow1.setLayoutParams(layoutParams1);
                        tableRow1.setOrientation(LinearLayout.HORIZONTAL);
                        int cc = -1;
                        RadioGroup childRadioGroup = addCheckGroupToTableRow(label, arrayListValue, 5);
                        tableRow1.addView(childRadioGroup, cc++);
                        ImageView imageView = addViewToTableRow(0);
                        tableRow1.addView(imageView, 1);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tableRow.removeView(tableRow1);
                            }
                        });
                        childRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                // checkedId is the RadioButton selected
                                RadioButton radioButton = (RadioButton) group.getChildAt(group.getChildCount() - 2);
                                radioButton.setError(null);
                            }
                        });
                        tableRow.addView(tableRow1);
                    }
                });
            }
            tableRow1.addView(ansRow);
            tableRow.addView(tableRow1);
        } else if (type.equalsIgnoreCase("Radio")) {
            final TableRow tableRow1 = new TableRow(context);
            TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER_VERTICAL;
            tableRow1.setLayoutParams(layoutParams1);
            tableRow1.setOrientation(LinearLayout.HORIZONTAL);
            final TableRow ansRow = new TableRow(context);
            ansRow.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
            ansRow.setBackgroundColor(getResources().getColor(R.color.white));
            RadioGroup parentRadioGroup = addRadioGroupToTableRow(arrayListValue, 6);
            if (label.equalsIgnoreCase("")) {//Nothing TO Show
            } else {
                TextView labelText = addTextViewToTableRow(label, isMandatory, null, 4);
                labelText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
                labelText.setTextColor(getResources().getColor(R.color.grey_text_tms));
                tableRow1.addView(labelText);
                // tableRow1.addView(addTextViewToTableRow(":", null, 1));
            }
            ansRow.addView(parentRadioGroup);
            if (fieldMutiple.equalsIgnoreCase("true")) {
                ImageView imageView = addViewToTableRow(1);
                ansRow.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TableRow tableRow1 = new TableRow(context);
                        TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
                        tableRow1.setLayoutParams(layoutParams1);
                        tableRow1.setOrientation(LinearLayout.HORIZONTAL);
                        int cc = -1;
                        RadioGroup childRadioGroup = addRadioGroupToTableRow(arrayListValue, 5);
                        tableRow1.addView(childRadioGroup, cc++);
                        ImageView imageView = addViewToTableRow(0);
                        tableRow1.addView(imageView, 1);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tableRow.removeView(tableRow1);
                            }
                        });
                        childRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                // checkedId is the RadioButton selected
                                RadioButton radioButton = (RadioButton) group.getChildAt(group.getChildCount() - 2);
                                radioButton.setError(null);
                            }
                        });
                        tableRow1.addView(ansRow);
                        tableRow.addView(tableRow1);
                    }
                });
            }
            parentRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // checkedId is the RadioButton selected
                    RadioButton radioButton = (RadioButton) group.getChildAt(group.getChildCount() - 2);
                    radioButton.setError(null);
                }
            });
            tableRow.addView(tableRow1);
        } else if (type.equalsIgnoreCase("DROPDOWN_S")) {
            final TableRow tableRow1 = new TableRow(context);
            TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER_VERTICAL;
            tableRow1.setLayoutParams(layoutParams1);
            tableRow1.setOrientation(LinearLayout.HORIZONTAL);
            TableRow ansRow = new TableRow(context);
            ansRow.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
            ansRow.setBackgroundColor(getResources().getColor(R.color.white));
            if (label.equalsIgnoreCase("")) {//Nothing TO Show
            } else {
                TextView labelText = addTextViewToTableRow(label, isMandatory, null, 4);
                labelText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
                labelText.setTextColor(getResources().getColor(R.color.grey_text_tms));
                tableRow1.addView(labelText);
                //   tableRow1.addView(addTextViewToTableRow(":", null, 1));
            }
            Spinner spinner = addSpinnerToTableRow(arrayListValue, label, 6);
            for (int i = 0; i < arrayListValue.size(); i++) {
                if (arrayListValue.get(i).get("selected").equalsIgnoreCase("true")) {
                    filterValue.put(label, arrayListValue.get(i).get("id"));
                    filterValue.put(label + "position", "" + (i + 1));
                    spinner.setSelection(i + 1);
                }
            }
            if (!filterValue.containsKey(label + "position")) {
                filterValue.put(label + "position", "0");
                filterValue.put(label, "");
            }
            if (label.equalsIgnoreCase("Company"))
                companySpinner = spinner;
            else if (label.equalsIgnoreCase("Department"))
                departmentSpinner = spinner;
            else if (label.equalsIgnoreCase("Wing"))
                wingSpinner = spinner;
            else if (label.equalsIgnoreCase("Designation"))
                designationSpinner = spinner;
            else if (label.equalsIgnoreCase("Individual"))
                individualSpinner = spinner;
            else if (label.equalsIgnoreCase("Dependency Predecessor"))
                dependencyPreSpinner = spinner;
            else if (label.equalsIgnoreCase("Folder"))
                folderSpinner = spinner;
            else if (label.equalsIgnoreCase("Dependency Task")) {
                dependencyTaskSpinner = spinner;
                if (!isViewDependency)
                    dependencyTaskSpinner.setEnabled(false);
            }
            ansRow.addView(spinner);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (position > 0) {
                        if (filterValue.containsKey(label + "position")) {
                            if (Integer.parseInt(filterValue.get(label + "position")) != position) {
                                filterValue.put(label, loadHashValues.get(label).get(position - 1).get("id"));
                                String multiSelectValue = "";
                                if (filterValue.containsKey(label))
                                    multiSelectValue = filterValue.get(label);
                                if (label.equalsIgnoreCase("Company")) {
                                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'department','comp_id':'" + multiSelectValue + "','dep_id':'','wing_id':'','des_id':'','indiv_id':''}";
                                    System.out.println(requestParameter);
                                    getLoadData(requestParameter, "Department");
                                } else if (label.equalsIgnoreCase("Department")) {
                                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'wing','comp_id':'" + filterValue.get("Company") + "','dep_id':'" + multiSelectValue + "','wing_id':'','des_id':'','indiv_id':''}";
                                    System.out.println(requestParameter);
                                    getLoadData(requestParameter, "Wing");
                                } else if (label.equalsIgnoreCase("Wing")) {
                                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'designation','comp_id':'" + filterValue.get("Company") + "','dep_id':'" + filterValue.get("Department") + "','wing_id':'" + multiSelectValue + "','des_id':'','indiv_id':''}";
                                    System.out.println(requestParameter);
                                    getLoadData(requestParameter, "Designation");
                                } else if (label.equalsIgnoreCase("Designation")) {
                                    requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'TMS_ONCHOICE_CHANGE_CLICK','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','type':'individual','comp_id':'" + filterValue.get("Company") + "','dep_id':'" + filterValue.get("Department") + "','wing_id':'" + filterValue.get("Wing") + "','des_id':'" + multiSelectValue + "','indiv_id':''}";
                                    System.out.println(requestParameter);
                                    getLoadData(requestParameter, "Individual");
                                }
                            }
                        }
                        if (label.equalsIgnoreCase("Folder")) {
                            filterValue.put(label + "position", "" + (position + 1));
                        } else if (label.equalsIgnoreCase("Dependency Predecessor")) {
                            dependencyTaskSpinner.setEnabled(true);
                            isViewDependency = true;
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });
            if (fieldMutiple.equalsIgnoreCase("true")) {
                ImageView imageView = addViewToTableRow(1);
                ansRow.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (label.equalsIgnoreCase("Folder")) {
                            requestParameter = "{'Action':'TASK_MANAGEMENT_SYSTEM','submode':'FOLDER_ADD','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'TaskId':'" + taskId + "', 'folder_id':'0', 'actionModal':'', 'frm_config_id':'1', 'type':'task'}";
                            System.out.println(requestParameter);
                            getLoadData(requestParameter, "Folder");
                        } else {
                            final TableRow tableRow1 = new TableRow(context);
                            TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams1.gravity = Gravity.CENTER_VERTICAL;
                            tableRow1.setLayoutParams(layoutParams1);
                            tableRow1.setOrientation(LinearLayout.HORIZONTAL);
                            int cc = -1;
                            tableRow1.addView(addSpinnerToTableRow(arrayListValue, label, 5), cc++);
                            ImageView imageView = addViewToTableRow(0);
                            tableRow1.addView(imageView, 1);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    tableRow.removeView(tableRow1);
                                }
                            });
                            tableRow.addView(tableRow1);
                        }
                    }
                });
            }
            tableRow1.addView(ansRow);
            tableRow.addView(tableRow1);
        } else if (type.equalsIgnoreCase("DROPDOWN_M")) {
            final TableRow tableRow1 = new TableRow(context);
            TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER_VERTICAL;
            tableRow1.setLayoutParams(layoutParams1);
            tableRow1.setOrientation(LinearLayout.HORIZONTAL);
            TableRow ansRow = new TableRow(context);
            ansRow.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
            ansRow.setBackgroundColor(getResources().getColor(R.color.white));
            if (label.equalsIgnoreCase("")) {//Nothing TO Show
            } else {
                TextView labelText = addTextViewToTableRow(label, isMandatory, null, 4);
                labelText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
                labelText.setTextColor(getResources().getColor(R.color.grey_text_tms));
                tableRow1.addView(labelText);
                //  tableRow1.addView(addTextViewToTableRow(":", null, 1));
            }
            ansRow.addView(addMultiSpinnerToTableRow(arrayListValue, "Select", 6));
            if (fieldMutiple.equalsIgnoreCase("true")) {
                ImageView imageView = addViewToTableRow(1);
                ansRow.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TableRow tableRow1 = new TableRow(context);
                        TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
                        tableRow1.setLayoutParams(layoutParams1);
                        tableRow1.setOrientation(LinearLayout.HORIZONTAL);
                        int cc = -1;
                        tableRow1.addView(addMultiSpinnerToTableRow(arrayListValue, "Select", 5), cc++);
                        ImageView imageView = addViewToTableRow(0);
                        tableRow1.addView(imageView, 1);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tableRow.removeView(tableRow1);
                            }
                        });
                        tableRow.addView(tableRow1);
                    }
                });
            }
            tableRow1.addView(ansRow);
            tableRow.addView(tableRow1);
        } else if (type.equalsIgnoreCase("DATETIME")) {
            final TableRow tableRow1 = new TableRow(context);
            TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER_VERTICAL;
            tableRow1.setLayoutParams(layoutParams1);
            tableRow1.setOrientation(LinearLayout.HORIZONTAL);
            TableRow ansRow = new TableRow(context);
            ansRow.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
            ansRow.setBackgroundColor(getResources().getColor(R.color.white));
            String displayVal[] = new String[1];
            for (int val = 0; val < arrayListValue.size(); val++) {
                if (arrayListValue.get(val).get("selected").equalsIgnoreCase("true"))
                    if (arrayListValue.get(val).get("value").length() > 0)
                        displayVal = arrayListValue.get(val).get("value").split(" ");
            }
            final EditText editDateText = addEditTextDate("Date", displayVal[0], null, 3);
            //editDateText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
            if (label.equalsIgnoreCase("")) {//Nothing TO Show
            } else {
                TextView labelText = addTextViewToTableRow(label, isMandatory, null, 4);
                labelText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
                labelText.setTextColor(getResources().getColor(R.color.grey_text_tms));
                tableRow1.addView(labelText);
                // tableRow1.addView(addTextViewToTableRow(":", null, 1));
            }
            ansRow.addView(editDateText);
            final EditText parentTime = addEditTextTime("Time", displayVal[1], null, 3);
            parentTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
            ansRow.addView(parentTime);
            editDateText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editDateTextView = editDateText;
                    DialogFragment newFragment = new SelectDateFragment();
                    newFragment.show(getFragmentManager(), "DatePicker");
                }
            });
            parentTime.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  int mHour, mMinute;
                                                  editTimeText = parentTime;
                                                  final Calendar c = Calendar.getInstance();
                                                  if (!editTimeText.getText().toString().isEmpty() && !editTimeText.getText().toString().equalsIgnoreCase("-")) {
                                                      String timeHours[] = editTimeText.getText().toString().split(":");
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
                                                                  editTimeText.setText(f.format(hourOfDay) + ":" + f.format(minute) + ":00");
                                                                  editTimeText.setError(null);
                                                              }
                                                          }, mHour, mMinute, true);
                                                  timePickerDialog.show();
                                              }
                                          }
            );
            if (fieldMutiple.equalsIgnoreCase("true")) {
                ImageView imageView = addViewToTableRow(1);
                ansRow.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TableRow tableRow1 = new TableRow(context);
                        TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
                        tableRow1.setLayoutParams(layoutParams1);
                        tableRow1.setOrientation(LinearLayout.HORIZONTAL);
                        final EditText editDateText = addEditTextDate(label, "", null, 3);
                        tableRow1.addView(editDateText);
                        final EditText childTime = addEditTextTime("Time", "", null, 3);
                        tableRow1.addView(childTime);
                        ImageView imageView = addViewToTableRow(0);
                        tableRow1.addView(imageView);
                        editDateText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editDateTextView = editDateText;
                                DialogFragment newFragment = new SelectDateFragment();
                                newFragment.show(getFragmentManager(), "DatePicker");
                            }
                        });
                        childTime.setOnClickListener(new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View v) {
                                                             int mHour, mMinute;
                                                             final Calendar c = Calendar.getInstance();
                                                             editTimeText = childTime;
                                                             if (!editTimeText.getText().toString().isEmpty() && !editTimeText.getText().toString().equalsIgnoreCase("-")) {
                                                                 String timeHours[] = editTimeText.getText().toString().split(":");
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
                                                                             editTimeText.setText(f.format(hourOfDay) + ":" + f.format(minute));
                                                                             editTimeText.setError(null);
                                                                         }
                                                                     }, mHour, mMinute, true);
                                                             timePickerDialog.show();
                                                         }
                                                     }
                        );
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tableRow.removeView(tableRow1);
                            }
                        });
                        tableRow.addView(tableRow1);
                    }
                });
            }
            tableRow1.addView(ansRow);
            tableRow.addView(tableRow1);
        } /*else if (type.equalsIgnoreCase("TimePicker")) {
            final TableRow tableRow1 = new TableRow(context);
            TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER_VERTICAL;
            tableRow1.setLayoutParams(layoutParams1);
            tableRow1.setOrientation(LinearLayout.HORIZONTAL);
            tableRow1.addView(addEditTextTime("Time","", null, 5), 0);
            if (fieldMutiple.equalsIgnoreCase("true")) {
                ImageView imageView = addViewToTableRow(1);
                tableRow1.addView(imageView, 1);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TableRow tableRow1 = new TableRow(context);
                        TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
                        tableRow1.setLayoutParams(layoutParams1);
                        tableRow1.setOrientation(LinearLayout.HORIZONTAL);
                        int cc = -1;
                        tableRow1.addView(addEditTextTime("Time","", null, 5), cc++);
                        ImageView imageView = addViewToTableRow(0);
                        tableRow1.addView(imageView, 1);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tableRow.removeView(tableRow1);
                            }
                        });
                        tableRow.addView(tableRow1);
                    }
                });
            }
            tableRow.addView(tableRow1);
        }*/ else if (type.equalsIgnoreCase("AutoSearch")) {
            final TableRow tableRow1 = new TableRow(context);
            TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER_VERTICAL;
            tableRow1.setLayoutParams(layoutParams1);
            tableRow1.setOrientation(LinearLayout.HORIZONTAL);
            TableRow ansRow = new TableRow(context);
            ansRow.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
            ansRow.setBackgroundColor(getResources().getColor(R.color.white));
            if (label.equalsIgnoreCase("")) {//Nothing TO Show
            } else {
                TextView labelText = addTextViewToTableRow(label, isMandatory, null, 4);
                labelText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
                labelText.setTextColor(getResources().getColor(R.color.grey_text_tms));
                tableRow1.addView(labelText);
                //  tableRow1.addView(addTextViewToTableRow(":", null, 1));
            }
            String displayVal = "";
            for (int val = 0; val < arrayListValue.size(); val++) {
                if (arrayListValue.get(val).get("selected").equalsIgnoreCase("true"))
                    if (displayVal.equalsIgnoreCase(""))
                        displayVal = arrayListValue.get(val).get("value");
                    else
                        displayVal = displayVal + "," + arrayListValue.get(val).get("value");
            }
            FilterAutocomplete = autocomplete(label, displayVal, null, 5);
            ansRow.addView(FilterAutocomplete);
            AutoCompleteTextviewAdapter testAdapter = new AutoCompleteTextviewAdapter(context, arrayListValue);
            FilterAutocomplete.setAdapter(testAdapter);
            if (fieldMutiple.equalsIgnoreCase("true")) {
                ImageView imageView = addViewToTableRow(1);
                ansRow.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TableRow tableRow1 = new TableRow(context);
                        TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
                        tableRow1.setLayoutParams(layoutParams1);
                        tableRow1.setOrientation(LinearLayout.HORIZONTAL);
                        int cc = -1;
                        FilterAutocomplete = autocomplete(label, "", null, 5);
                        tableRow1.addView(FilterAutocomplete, cc++);
                        AutoCompleteTextviewAdapter testAdapter = new AutoCompleteTextviewAdapter(context, arrayListValue);
                        FilterAutocomplete.setAdapter(testAdapter);
                        ImageView imageView = addViewToTableRow(0);
                        tableRow1.addView(imageView, 1);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tableRow.removeView(tableRow1);
                            }
                        });
                        tableRow.addView(tableRow1);
                    }
                });
            }
            tableRow1.addView(ansRow);
            tableRow.addView(tableRow1);
        } else if (type.equalsIgnoreCase("ATTACH")) {
            TableRow tableRow1 = new TableRow(context);
            TableLayout.LayoutParams layoutParams2 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layoutParams2.gravity = Gravity.CENTER_VERTICAL;
            tableRow1.setLayoutParams(layoutParams2);
            tableRow1.setOrientation(TableLayout.HORIZONTAL);
            TableRow ansRow = new TableRow(context);
            ansRow.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
            ansRow.setBackgroundColor(getResources().getColor(R.color.white));
            if (label.equalsIgnoreCase("")) {//Nothing TO Show
            } else {
                TextView labelText = addTextViewToTableRow(label, isMandatory, null, 4);
                //label size
                labelText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
                labelText.setTextColor(getResources().getColor(R.color.grey_text_tms));
                tableRow1.addView(labelText);
                //  tableRow1.addView(addTextViewToTableRow(":", null, 1));
            }
            final EditText parentBrowseValue = addNonEditTextToTableRow(arrayListValue, null, 6);
            ansRow.addView(parentBrowseValue);
            ImageView browseParentButton = addButtonToTableRow(value, null, 1);
            ansRow.addView(browseParentButton);
            browseParentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filePath = parentBrowseValue;
                    Pickerdialog.show();
                }
            });
            if (fieldMutiple.equalsIgnoreCase("true")) {
                ImageView imageView = addViewToTableRow(1);
                ansRow.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TableRow tableRow12 = addViewAttach(value);
                        ArrayList<HashMap<String, String>> arrayListValueMulti = new ArrayList<HashMap<String, String>>();
                        addViewAttach(arrayListValueMulti);
                        // tableRow.addView(attachTableRow);
                    }
                });
            }
            tableRow1.addView(ansRow);
            tableRow.addView(tableRow1);
            attachTableLayout = tableRow;
        }
        return tableRow;
    }
    public TableRow addViewAttach(ArrayList<HashMap<String, String>> arrayValues) {
        final TableRow tableRow1 = new TableRow(context);
        TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
        layoutParams1.topMargin = 10;
        tableRow1.setLayoutParams(layoutParams1);
        tableRow1.setOrientation(LinearLayout.HORIZONTAL);
        tableRow1.setBackgroundColor(getResources().getColor(R.color.white));
        String value = "";
        if (arrayValues.size() > 0)
            value = arrayValues.get(0).get("value");
        final EditText childBrowseValue = addNonEditTextToTableRow(arrayValues, null, 6);
        tableRow1.addView(childBrowseValue);
        ImageView browseChildButton = addButtonToTableRow(value, null, 1);
        tableRow1.addView(browseChildButton);
        ImageView imageView = addViewToTableRow(0);
        tableRow1.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachTableLayout.removeView(tableRow1);
            }
        });
        browseChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filePath = childBrowseValue;
                Pickerdialog.show();
            }
        });
        attachTableLayout.addView(tableRow1);
        return tableRow1;
    }
    public TableRow addDiscussAttach(ArrayList<HashMap<String, String>> arrayValues) {
        final TableRow tableRow1 = new TableRow(context);
        TableLayout.LayoutParams layoutParams1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
        tableRow1.setLayoutParams(layoutParams1);
        tableRow1.setOrientation(LinearLayout.HORIZONTAL);
        tableRow1.setBackgroundColor(getResources().getColor(R.color.white));
        final EditText childBrowseValue = addNonEditTextToTableRow(arrayValues, null, 6);
        tableRow1.addView(childBrowseValue);
        ImageView browseChildButton = addButtonToTableRow("", null, 1);
        tableRow1.addView(browseChildButton);
        ImageView imageView = addViewToTableRow(0);
        tableRow1.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachTableLayout.removeView(tableRow1);
            }
        });
        browseChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filePath = childBrowseValue;
                Pickerdialog.show();
            }
        });
        attachTableLayout.addView(tableRow1);
        return tableRow1;
    }
    public ImageView addViewToTableRow(int addType) {
        ImageView imageView = new ImageView(context);
        TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
        int padding_Value =  (int) getResources().getDimension(R.dimen.padding_medium);
        layoutParams1.setMargins(padding_Value, 0, padding_Value, 0);
        imageView.setLayoutParams(layoutParams1);
        if (addType == 1)
            imageView.setImageResource(R.drawable.ic_tms_add);
        else if (addType == 0)
            imageView.setImageResource(R.drawable.ic_delete_dynamic);
        return imageView;
    }
    public ImageView addPlayButton(String value, int layoutWeight) {
        ImageView button = new ImageView(context);
        button.setImageResource(R.drawable.tms_play);
        //button.setPadding(5, 5, 5, 5);
        int padding_Value =  (int) getResources().getDimension(R.dimen.addNonEditTextToTableRow);
        button.setPadding(padding_Value, padding_Value, padding_Value, padding_Value);
        button.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight));
        return button;
    }
    public ImageView addCompleteButton(String value, int layoutWeight) {
        ImageView button = new ImageView(context);
        button.setImageResource(R.drawable.tms_completed);
        //button.setPadding(5, 5, 5, 5);
        int padding_Value =  (int) getResources().getDimension(R.dimen.addNonEditTextToTableRow);
        button.setPadding(padding_Value, padding_Value, padding_Value, padding_Value);
        button.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight));
        return button;
    }
    public ImageView addHoldButton(String value, int layoutWeight) {
        ImageView button = new ImageView(context);
        button.setImageResource(R.drawable.tms_hold);
        //button.setPadding(5, 5, 5, 5);
        int padding_Value =  (int) getResources().getDimension(R.dimen.addNonEditTextToTableRow);
        button.setPadding(padding_Value, padding_Value, padding_Value, padding_Value);
        button.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight));
        return button;
    }
    public TextView addTextViewToTableRow(String value, Boolean isMadatory, Typeface typeface, int layoutWeight) {
        TextView textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        textView.setTextColor(getResources().getColor(R.color.black));
        if (typeface != null)
            textView.setTypeface(typeface);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        //textView.setPadding(5, 5, 5, 5);
        int padding_Value =  (int) getResources().getDimension(R.dimen.addNonEditTextToTableRow);
        textView.setPadding(padding_Value, padding_Value, padding_Value, padding_Value);
        if (isMadatory) {
            if (viewMode) {
                textView.setText(value);
            } else {
                String manda = "<font color='#EE0000'> *</font>";
                textView.setText(Html.fromHtml(value + manda));
                // textView.setText(Html.fromHtml(value + " " + Html.fromHtml("<font color='#FF0000'>*</font>")));
            }
        } else
            textView.setText(value);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight));
        return textView;
    }
    public TextView addTextViewToTableRowWhite(String value, Typeface typeface, int layoutWeight) {
        TextView textView = new TextView(context);
        textView.setTextColor(getResources().getColor(R.color.white));
        if (typeface != null)
            textView.setTypeface(typeface);
        //textView.setPadding(5, 5, 5, 5);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        int padding_Value =  (int) getResources().getDimension(R.dimen.addNonEditTextToTableRow);
        textView.setPadding(padding_Value, padding_Value, padding_Value, padding_Value);
        textView.setText(value);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight));
        return textView;
    }
    public CheckBox addColorCheckBox() {
        CheckBox checkBox = new CheckBox(context);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        checkBox.setLayoutParams(layoutParams);
        checkBox.setButtonDrawable(R.drawable.color_view);
        return checkBox;
    }
    public EditText addEditTextToTableRow1(String hintText, String value, Typeface typeface, int layoutWeight) {
        final EditText editText = new EditText(context);
        editText.setBackgroundResource(R.drawable.edit_text_dynamic_bg);
        editText.setTextColor(getResources().getColor(R.color.black));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        if (typeface != null)
            editText.setTypeface(typeface);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        //editText.setPadding(5, 5, 5, 5);
        int padding_Value =  (int) getResources().getDimension(R.dimen.addNonEditTextToTableRow);
        editText.setPadding(padding_Value, padding_Value, padding_Value, padding_Value);
        editText.setText(value);
        editText.setHint(hintText);
        // if (hintText.equalsIgnoreCase("Estimate Duration") || hintText.equalsIgnoreCase("Update Frequency"))
        if (hintText.equalsIgnoreCase("Update Frequency"))
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        else
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight));
        if (hintText.equalsIgnoreCase("Estimate Duration")) {
            final List<String> affineFormats = new ArrayList<>();
            affineFormats.add("[000]:[00]");
            final MaskedTextChangedListener listener = new PolyMaskTextChangedListener(
                    "[000]:[00]",
                    affineFormats,
                    true,
                    editText,
                    null,
                    new MaskedTextChangedListener.ValueListener() {
                        @Override
                        public void onTextChanged(boolean maskFilled, @NonNull final String extractedValue) {
                            Log.d(TMSAddAndViewActivity.class.getSimpleName(), extractedValue);
                            Log.d(TMSAddAndViewActivity.class.getSimpleName(), String.valueOf(maskFilled));
                        }
                    }
            );
            editText.addTextChangedListener(listener);
            editText.setOnFocusChangeListener(listener);
            editText.setHint(listener.placeholder());
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        if (editText.length() != 6) {
                            editText.setText("");
                        }
                }
            });
        }
        return editText;
    }
    public EditText addEditTextAreaToTableRow(String hintText, String value, Typeface typeface, int layoutWeight) {
        final EditText editText = new EditText(context);
        editText.setBackgroundResource(R.drawable.edit_text_dynamic_bg);
        editText.setTextColor(getResources().getColor(R.color.black));
        if (typeface != null)
            editText.setTypeface(typeface);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        //editText.setPadding(5, 5, 5, 5);
        int padding_Value =  (int) getResources().getDimension(R.dimen.addNonEditTextToTableRow);
        editText.setPadding(padding_Value, padding_Value, padding_Value, padding_Value);
        editText.setText(value);
        editText.setHint(hintText);
        editText.setLayoutParams(new TableRow.LayoutParams(0, 200, layoutWeight));
        return editText;
    }
    public GridView addGridView() {
        GridView gridView = new GridView(context);
        gridView.setNumColumns(4);
        return gridView;
    }
    public RadioGroup addRadioGroupToTableRow(ArrayList<HashMap<String, String>> valueList, int layoutWeight) {
        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        TableRow.LayoutParams radioGroupLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight);
        radioGroupLayoutParams.gravity = Gravity.CENTER;
        radioGroup.setLayoutParams(radioGroupLayoutParams);
        for (int i = 0; i < valueList.size(); i++) {
            String labelText = valueList.get(i).get("value");
            int id = Integer.parseInt(valueList.get(i).get("id"));
            String isSelected = valueList.get(i).get("selected");
            addRadioButtonWithLabelToRadioGroup(labelText, id, radioGroup, isSelected);
        }
        return radioGroup;
    }
    public RadioGroup addCheckGroupToTableRow(String label, ArrayList<HashMap<String, String>> valueList, int layoutWeight) {
        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        TableRow.LayoutParams radioGroupLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight);
        radioGroupLayoutParams.gravity = Gravity.CENTER;
        radioGroup.setLayoutParams(radioGroupLayoutParams);
        for (int i = 0; i < valueList.size(); i++) {
            String labelText = valueList.get(i).get("value");
            int id = Integer.parseInt(valueList.get(i).get("id"));
            String selected = valueList.get(i).get("selected");
            addCheckButtonWithLabelToRadioGroup(label, labelText, id, selected, radioGroup);
        }
        return radioGroup;
    }
    public Spinner addSpinnerToTableRow(ArrayList<HashMap<String, String>> valueList, String hintText, int layoutWeight) {
        Spinner spinner = new Spinner(context);
        //  spinner.setBackgroundResource(R.drawable.lead_dropdown_bg);
        TableRow.LayoutParams spinnerLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight);
        spinner.setLayoutParams(spinnerLayoutParams);
        try {
            loadLeadStyleSpinner(spinner, valueList, new String[]{"value"}, hintText);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return spinner;
    }
    public ContactsCompletionView addMultiSpinnerToTableRow(ArrayList<HashMap<String, String>> valueList, String hintText, int layoutWeight) {
        final ContactsCompletionView spinner = new ContactsCompletionView(context);
        spinner.setBackgroundResource(R.drawable.lead_dropdown_bg);
        TableRow.LayoutParams spinnerLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight);
        spinner.setLayoutParams(spinnerLayoutParams);
        spinner.setCursorVisible(false);
        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(spinner.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        lists[] people = new lists[valueList.size()];
        ArrayAdapter<lists> adapter;
        try {
            for (int i = 0; i < valueList.size(); i++) {
                people[i] = new lists(valueList.get(i).get("value"), valueList.get(i).get("id"));
            }
            adapter = new FilteredArrayAdapter<lists>(context, R.layout.person_layout, people) {
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
            spinner.setAdapter(adapter);
            spinner.setTokenListener(this);
            spinner.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
            for (int i = 0; i < valueList.size(); i++) {
                if (valueList.get(i).get("selected").equalsIgnoreCase("true"))
                    spinner.addObject(people[i]);
            }
            spinner.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                    // TODO Auto-generated method stub
                    spinner.showDropDown();
                    // completionView.requestFocus();
                    return false;
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return spinner;
    }
    public EditText addEditTextDate(String label, String value, Typeface typeface, int layoutWeight) {
        EditText editDateText = new EditText(context);
        editDateText.setBackgroundResource(R.drawable.edit_text_dynamic_bg);
        editDateText.setTextColor(getResources().getColor(R.color.black));
        if (typeface != null)
            editDateText.setTypeface(typeface);
        editDateText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        //editDateText.setPadding(2, 2, 2, 2);
        int padding_Value =  (int) getResources().getDimension(R.dimen.addNonEditTextToTableRow);
        editDateText.setPadding(padding_Value, padding_Value, padding_Value, padding_Value);
        editDateText.setText(value);
        editDateText.setHint(label);
        editDateText.setFocusable(false);
        editDateText.setClickable(false);
        //  editText.setHint(filterName);
        editDateText.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight));
        return editDateText;
    }
   /* public CheckBox addCheckbox(String value, Typeface typeface, int layoutWeight) {
        colorCheckBox = new CheckBox(context);
        colorCheckBox.setButtonDrawable(R.drawable.color_view);
        colorCheckBox.setBackgroundColor(Color.parseColor(value));
        colorCheckBox.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight));
        colorCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// custom dialog
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.listview);
                dialog.setTitle("Select Color");
                GridView gridView = (GridView) dialog.findViewById(R.id.gridView);
                ImageAdapter adapter = new ImageAdapter(context, month, color);
                gridView.setAdapter(adapter);
                gridView.setChoiceMode(gridView.CHOICE_MODE_SINGLE);
               *//* // set the custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.text);
                text.setText("Android custom dialog example!");
                ImageView image = (ImageView) dialog.findViewById(R.id.image);
                image.setImageResource(R.drawable.i);
                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });*//*
                dialog.show();
            }
        });
        return colorCheckBox;
    }*/
    public EditText addEditTextTime(String label, String value, Typeface typeface, int layoutWeight) {
        editTimeText = new EditText(context);
        editTimeText.setBackgroundResource(R.drawable.edit_text_dynamic_bg);
        editTimeText.setTextColor(getResources().getColor(R.color.black));
        if (typeface != null)
            editTimeText.setTypeface(typeface);
        //editTimeText.setTextSize(14);
        editTimeText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        int padding_Value =  (int) getResources().getDimension(R.dimen.addNonEditTextToTableRow);
        editTimeText.setPadding(padding_Value, padding_Value, padding_Value, padding_Value);
        editTimeText.setText(value);
        editTimeText.setHint(label);
        editTimeText.setFocusable(false);
        editTimeText.setClickable(false);
        //  editText.setHint(filterName);
        editTimeText.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight));
        /*TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight);
        params.setMargins(2,0,0,0);
        editTimeText.setLayoutParams(params);
*/        return editTimeText;
    }
    public AutoCompleteTextView autocomplete(String label, String value, Typeface typeface, int layoutWeight) {
        AutoCompleteTextView editText = new AutoCompleteTextView(context);
        editText.setBackgroundResource(R.drawable.edit_text_dynamic_bg);
        editText.setTextColor(getResources().getColor(R.color.black));
        editText.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight));
        if (typeface != null)
            editText.setTypeface(typeface);
        //editText.setTextSize(14);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        //editText.setPadding(2, 2, 2, 2);
        int padding_Value =  (int) getResources().getDimension(R.dimen.dynamic_text_padding);
        editTimeText.setPadding(padding_Value, padding_Value, padding_Value, padding_Value);
        editText.setText(value);
        editText.setHint(label);
        editText.setThreshold(0);
        // editText.setLayoutParams(new TableRow.LayoutParams(0, 100, layoutWeight));
        return editText;
    }
    public EditText addNonEditTextToTableRow(ArrayList<HashMap<String, String>> arrayListValue, Typeface typeface, int layoutWeight) {
        EditText editText = new EditText(context);
        editText.setBackgroundResource(R.drawable.edit_text_dynamic_bg);
        editText.setTextColor(getResources().getColor(R.color.black));
        if (typeface != null)
            editText.setTypeface(typeface);
        //editText.setTextSize(14);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        editText.setEnabled(false);
        //editText.setPadding(5, 5, 5, 5);
        int padding_Value =  (int) getResources().getDimension(R.dimen.addNonEditTextToTableRow);
        editTimeText.setPadding(padding_Value, padding_Value, padding_Value, padding_Value);
        editText.setHint("No Directory");
        editText.setSingleLine();
        String displayVal = "";
        int displayId = 0;
        for (int val = 0; val < arrayListValue.size(); val++) {
            if (arrayListValue.get(val).get("selected").equalsIgnoreCase("true"))
                if (displayVal.equalsIgnoreCase("")) {
                    displayVal = arrayListValue.get(val).get("value");
                } else {
                    displayVal = displayVal + "," + arrayListValue.get(val).get("value");
                }
            displayId = Integer.parseInt(arrayListValue.get(0).get("id"));
        }
        editText.setText(displayVal);
        editText.setId(displayId);
        editText.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight));
        return editText;
    }
    public ImageView addButtonToTableRow(String value, Typeface typeface, int layoutWeight) {
        ImageView button = new ImageView(context);
        TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
        int padding_Value =  (int) getResources().getDimension(R.dimen.addNonEditTextToTableRow);
        layoutParams1.setMargins(padding_Value, 0, padding_Value, 0);
        button.setLayoutParams(layoutParams1);
        button.setImageResource(R.drawable.ic_attachment);
        return button;
    }
    private void addRadioButtonWithLabelToRadioGroup(String radioButtonLabel, int radioButtonID, RadioGroup radioGroup, String isSelected) {
      //  RadioButton radioButton = new RadioButton(context,null,R.attr.radioButtonStyle);
        RadioButton radioButton = new RadioButton(context);
 /*       if(context.getResources().getBoolean(R.bool.isTablet)==true)
        {
            radioButton.setButtonDrawable(R.drawable.radio_btn_holo_light);
        }*/
        radioButton.setId(radioButtonID);
        TableRow.LayoutParams radioButtonLayoutParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        radioButtonLayoutParams.gravity = Gravity.CENTER;
        radioButton.setLayoutParams(radioButtonLayoutParams);
        radioButton.setText(radioButtonLabel);
        radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        radioGroup.addView(radioButton);
        if (isSelected.equalsIgnoreCase("true"))
            radioButton.setChecked(true);
        else
            radioButton.setChecked(false);
        if (viewMode)
            radioButton.setEnabled(false);
        else
            radioButton.setEnabled(true);
    }
    public void hideKeyboard() {
        try {
            //  imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            InputMethodManager inputManager = (InputMethodManager) view
                    .getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            IBinder binder = view.getWindowToken();
            inputManager.hideSoftInputFromWindow(binder,
                    InputMethodManager.HIDE_NOT_ALWAYS);
            Log.d("hideKeyboard", "true");
            /*InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(((Activity) context).FOCUSABLE_VIEW.getWindowToken(), 0);
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            *//*InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);*/
        } catch (Exception e) {
            // TODO: handle exception
            Log.d("hideKeyboard", "false");
        }
    }
    private void addCheckButtonWithLabelToRadioGroup(final String label, String radioButtonLabel, int radioButtonID, String selected, RadioGroup radioGroup) {
      //  final AppCompatCheckBox checkButton = new AppCompatCheckBox(context);
        final CheckBox checkButton = new CheckBox(context);
        checkButton.setId(radioButtonID);
        /*if(context.getResources().getBoolean(R.bool.isTablet)==true)
        {
            checkButton.setScaleX(2.0f);
            checkButton.setScaleY(2.0f);
        }*/
       /* if(context.getResources().getBoolean(R.bool.isTablet))
        {
            TableRow.LayoutParams radioButtonLayoutParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,100);
            radioButtonLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
           // radioButtonLayoutParams.setMargins(20, 0, 0, 0);
            checkButton.setLayoutParams(radioButtonLayoutParams);
         *//*   checkButton.setScaleX(1.3f);
            checkButton.setScaleY(1.3f);*//*
            checkButton.setWidth(200);
            //checkButton.setHeight(100);
        }
        else
        {
            TableRow.LayoutParams radioButtonLayoutParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            radioButtonLayoutParams.gravity = Gravity.CENTER;
            checkButton.setLayoutParams(radioButtonLayoutParams);
            checkButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        }*/
        TableRow.LayoutParams radioButtonLayoutParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        radioButtonLayoutParams.gravity = Gravity.CENTER;
        checkButton.setLayoutParams(radioButtonLayoutParams);
        checkButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        if (radioButtonLabel.equalsIgnoreCase("1") || radioButtonLabel.equalsIgnoreCase("0"))
            checkButton.setText("");
        else
            checkButton.setText(radioButtonLabel);
        if (viewMode)
            checkButton.setEnabled(false);
        else
            checkButton.setEnabled(true);
        if (label.equalsIgnoreCase("Self Assignee")) {
            selfAssignee = checkButton;
        }
        if (selected.equalsIgnoreCase("true")) {
            checkButton.setChecked(true);
            if (label.equalsIgnoreCase("Self Assignee")) {
                if (!viewMode) {
                    companySpinner.setEnabled(false);
                    departmentSpinner.setEnabled(false);
                    wingSpinner.setEnabled(false);
                    designationSpinner.setEnabled(false);
                    individualSpinner.setEnabled(false);
                    companySpinner.setSelection(0);
                    individualSpinner.setSelection(0);
                    ArrayList<HashMap<String, String>> emptyArrayList = new ArrayList<HashMap<String, String>>();
                    loadHashValues.put("Department", emptyArrayList);
                    loadLeadStyleSpinner(departmentSpinner, emptyArrayList, new String[]{"value"}, "Department");
                    loadHashValues.put("Wing", emptyArrayList);
                    loadLeadStyleSpinner(wingSpinner, emptyArrayList, new String[]{"value"}, "Wing");
                    loadHashValues.put("Designation", emptyArrayList);
                    loadLeadStyleSpinner(designationSpinner, emptyArrayList, new String[]{"value"}, "Designation");
                    filterValue.put("Companyposition", "0");
                    filterValue.put("Departmentposition", "0");
                    filterValue.put("Wingposition", "0");
                    filterValue.put("Designationposition", "0");
                    filterValue.put("Individualposition", "0");
                }
            }
        } else
            checkButton.setChecked(false);
        checkButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkButton.setError(null);
                if (label.equalsIgnoreCase("Self Assignee")) {
                    if (isChecked) {
                        companySpinner.setEnabled(false);
                        departmentSpinner.setEnabled(false);
                        wingSpinner.setEnabled(false);
                        designationSpinner.setEnabled(false);
                        individualSpinner.setEnabled(false);
                        companySpinner.setSelection(0);
                        individualSpinner.setSelection(0);
                        TextView errorcompany = (TextView) companySpinner.getSelectedView();
                        errorcompany.setError(null);
                        TextView errorindividual = (TextView) individualSpinner.getSelectedView();
                        errorindividual.setError(null);
                        ArrayList<HashMap<String, String>> emptyArrayList = new ArrayList<HashMap<String, String>>();
                        loadHashValues.put("Department", emptyArrayList);
                        loadLeadStyleSpinner(departmentSpinner, emptyArrayList, new String[]{"value"}, "Department");
                        loadHashValues.put("Wing", emptyArrayList);
                        loadLeadStyleSpinner(wingSpinner, emptyArrayList, new String[]{"value"}, "Wing");
                        loadHashValues.put("Designation", emptyArrayList);
                        loadLeadStyleSpinner(designationSpinner, emptyArrayList, new String[]{"value"}, "Designation");
                        filterValue.put("Companyposition", "0");
                        filterValue.put("Departmentposition", "0");
                        filterValue.put("Wingposition", "0");
                        filterValue.put("Designationposition", "0");
                        filterValue.put("Individualposition", "0");
                    } else {
                        companySpinner.setEnabled(true);
                        departmentSpinner.setEnabled(true);
                        wingSpinner.setEnabled(true);
                        designationSpinner.setEnabled(true);
                        individualSpinner.setEnabled(true);
                    }
                }
            }
        });
        radioGroup.addView(checkButton);
       /* TextView radioButtonLabelTextView = new TextView(context);
        if (radioButtonLabel.equalsIgnoreCase("1") || radioButtonLabel.equalsIgnoreCase("0"))
            radioButtonLabel = "";
        radioButtonLabelTextView.setText(radioButtonLabel);
        TableRow.LayoutParams radioButtonLabelTextViewLayoutParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        radioButtonLabelTextViewLayoutParams.gravity = Gravity.CENTER;
        radioButtonLabelTextView.setLayoutParams(radioButtonLabelTextViewLayoutParams);
        radioButtonLabelTextView.setTextSize(12);
        radioGroup.addView(radioButtonLabelTextView);*/
    }
    @Override
    public void onTokenAdded(lists token) {
    }
    @Override
    public void onTokenRemoved(lists token) {
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
            editDateTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
            editDateTextView.setError(null);
        }
    }
    public class ImageAdapter extends BaseAdapter {
        public Context context;
        ArrayList<HashMap<String, String>> colors = new ArrayList<>();
        public LayoutInflater inflater;
        int selectedPosition;
        String label;
        public ImageAdapter(Context context, ArrayList<HashMap<String, String>> colors, int selectedPosition, String label) {
            super();
            this.context = context;
            this.colors = colors;
            this.label = label;
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
                    filterValue.put("selectedColorPosition", "" + position);
                    filterValue.put(label, colors.get(position).get("id"));
                    colorCheckBox.setBackgroundColor(Color.parseColor(colors.get(position).get("value")));
                    colorCheckBox.setError(null);
                    notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
            return convertView;
        }
    }
    public static class ViewHolder {
        CheckBox colorBox;
    }
    private class DoInBackground extends AsyncTask<Void, Void, Void> implements
            DialogInterface.OnCancelListener {
        protected void onPreExecute() {
            processDialog = new ProgressDialog(context);
            processDialog.setCancelable(true);
            processDialog.show();
            processDialog.setCanceledOnTouchOutside(false);
            processDialog.setContentView(R.layout.loader);
            processDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        protected Void doInBackground(Void... unused) {
            try {
                String timestamp = Long.toString(System.currentTimeMillis());
                RestClient client = new RestClient(HttpRequest.portalURL + "Mobile_Devices?Action=DOC_UPLOAD");
                MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                File upFile = null;
                upFile = new File(selectedFile.getPath());
                FileBody fBody = new FileBody(upFile);
                mpEntity.addPart("ImageController", fBody);
                client.AddHeader("Authorization", "");
                client.ExecuteMultipart(RestClient.RequestMethod.POSTMULTIPART, mpEntity);
                //client.Execute(RequestMethod.POST);
                response = client.getResponse();
                // String timestamp = Long.toString(System.currentTimeMillis());
                // RestClient client = new
                // RestClient(HttpRequest.portalURL+"?action=DP_UPLOAD&");
                // client.AddHeader("Authorization","");
                // client.ExecuteMultipart(RequestMethod.POSTMULTIPART,reqEntity);
                // client.Execute(RequestMethod.POST);
                // response = client.getResponse();
                System.out.println("response" + response);
            } catch (Exception e) {
                processDialog.dismiss();
                e.printStackTrace();
                setToast("UnSupported File");
            }
            return null;
        }
        protected void onPostExecute(Void unused) {
            processDialog.dismiss();
            try {
                JSONObject jsonObj = new JSONObject(response);
                if (jsonObj.getString("code").equalsIgnoreCase("1")) {
                    setToast(jsonObj.getString("msg"));
                    doclist.add(jsonObj.getString("docid"));
                    filePath.setText(pathAddress);
                    filePath.setId(Integer.parseInt(jsonObj.getString("docid")));
                } else if (jsonObj.getString("code").equalsIgnoreCase("0")) {
                    setToast(jsonObj.getString("msg"));
                } else if (jsonObj.getString("code").equalsIgnoreCase("403")) {
                    showSessionScreensDialog(context, jsonObj.getString("msg"));
                } else {
                    setToast(jsonObj.getString("msg"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void onCancel(DialogInterface dialog) {
            cancel(true);
            dialog.dismiss();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case CAMERA_CAPTURE:
                    //get the Uri for the captured image
                    //picUri = data.getData();
                    picUri = CAPTUREURI;
                    Camera_Capturepath = getPath(picUri);
                    selectedFile = new File(Camera_Capturepath);
                    //	filePath1.setText(Camera_Capturepath1);
                    confirmationdialog.show();
                    ok_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationdialog.cancel();
                            pathAddress = Camera_Capturepath;
                            System.out.println("camera" + selectedFile.toString());
                            new DoInBackground().execute();
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationdialog.cancel();
                        }
                    });
                    break;
                case SELECT_PICTURE:
                    Uri selectedImageUri = data.getData();
                    //Gallery_path = getPath(selectedImageUri);
                    Gallery_path = getPathFromUri(context,selectedImageUri);
                    selectedFile = new File(Gallery_path);
                    System.out.println("Filepath" + Gallery_path);
                    confirmationdialog.show();
                    ok_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationdialog.cancel();
                            pathAddress = Gallery_path;
                            new DoInBackground().execute();
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationdialog.cancel();
                        }
                    });
                    break;
                case REQUEST_PICK_FILE:
                    if (data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {
                        selectedFile = new File(
                                data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                        String posi = data.getStringExtra("position");
                        confirmationdialog.show();
                        ok_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmationdialog.cancel();
                                pathAddress = selectedFile.getPath();
                                new DoInBackground().execute();
                            }
                        });
                        cancel_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmationdialog.cancel();
                            }
                        });
                    }
                    break;
            }
        }
    }
    public String getPathFromUri(final Context context, final Uri uri) {
        boolean isAfterKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isAfterKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents".equals(
                    uri.getAuthority())) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    return "/stroage/" + type + "/" + split[1];
                }
            } else if ("com.android.providers.downloads.documents".equals(
                    uri.getAuthority())) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if ("com.android.providers.media.documents".equals(
                    uri.getAuthority())) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                contentUri = MediaStore.Files.getContentUri("external");
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {//MediaStore
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String[] projection = {
                MediaStore.Files.FileColumns.DATA
        };
        try {
            cursor = context.getContentResolver().query(
                    uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int cindex = cursor.getColumnIndexOrThrow(projection[0]);
                return cursor.getString(cindex);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public void onDocument() {
        Pickerdialog.cancel();
        Intent intent = new Intent(context, FilePicker.class);
        intent.putExtra("position", String.valueOf(0));
        startActivityForResult(intent, REQUEST_PICK_FILE);
    }
    public void onCamera() {
        Pickerdialog.cancel();
        try {
            //use standard intent to capture an image
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //we will handle the returned data in onActivityResult
            //Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File dir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fileName = "Image-" + n + ".JPEG";
            final String appDirectoryName = "AmarprakashEAP";
            final File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), appDirectoryName);
            imageRoot.mkdirs();
            imageRoot.mkdirs();
            final File file = new File(imageRoot, fileName);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "AmarprakashEAP");
            values.put(MediaStore.Images.Media.DESCRIPTION, "EAP Ticketing Image");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));
            values.put("_data", file.getAbsolutePath());
            ContentResolver cr = context.getContentResolver();
            CAPTUREURI = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support capturing images!";
            Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public void onGallery() {
        Pickerdialog.cancel();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }
    public class TimeSheetListAdapter extends BaseAdapter {
        private static final String TAG = "TimeSheetListAdapter";
        Context context;
        ArrayList<HashMap<String, String>> timeSheetArray;
        public TimeSheetListAdapter(Context context, ArrayList<HashMap<String, String>> timeSheetArray) {
            this.context = context;
            this.timeSheetArray = timeSheetArray;
        }
        @Override
        public int getCount() {
            return timeSheetArray.size();
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(final int i, View convertView, ViewGroup parent) {
            View row = convertView;
            final TitleViewHolder holder;
            if (row == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = mInflater.inflate(R.layout.tms_timesheet_listrow, null);
                //row = mInflater.inflate(R.layout.vmlistsample, null);
                holder = new TitleViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (TitleViewHolder) row.getTag();
            }
            if (count < colorArray().length) {
                colorCode = colorArray()[count];
                count = count + 1;
            } else {
                count = 0;
            }
            holder.listSNoBgLayout.setBackground(getResources().getDrawable(colorCode));
            String sNo = String.valueOf(i + 1);
            holder.listSNoTextView.setText(sNo);
            holder.startTime.setText(timeSheetArray.get(i).get("StartDateAndTime"));
            holder.endTime.setText(timeSheetArray.get(i).get("EndDateAndTime"));
            holder.totalHours.setText(timeSheetArray.get(i).get("TotalHours"));
            holder.description.setText(timeSheetArray.get(i).get("Description"));
            holder.projectNameTitleRow.setVisibility(View.GONE);
            holder.title.setVisibility(View.GONE);
            try {
                JSONArray array = new JSONArray(timeSheetArray.get(i).get("OtherValues"));
                for (int j = 0; j < array.length(); j++) {
                    if (array.getJSONObject(j).getString("displayName").equalsIgnoreCase("Task Name")) {
                        holder.title.setVisibility(View.VISIBLE);
                        holder.title.setText(array.getJSONObject(j).getString("displayValue"));
                    } else {
                        holder.projectNameTitleRow.setVisibility(View.VISIBLE);
                        holder.projectNameTitleLabel.setText(array.getJSONObject(j).getString("displayName"));
                        holder.projectNameTitleTextView.setText(array.getJSONObject(j).getString("displayValue"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return row;
        }
    }
    class TitleViewHolder {
        //Title Components
        @Bind(R.id.list_sno_bg_layout)
        LinearLayout listSNoBgLayout;
        @Bind(R.id.list_sno)
        TextView listSNoTextView;
        @Bind(R.id.project_value)
        TextView projectNameTitleTextView;
        @Bind(R.id.project_label)
        TextView projectNameTitleLabel;
        @Bind(R.id.project_row)
        TableRow projectNameTitleRow;
        @Bind(R.id.start_time)
        TextView startTime;
        @Bind(R.id.end_time)
        TextView endTime;
        @Bind(R.id.total_hours)
        TextView totalHours;
        @Bind(R.id.description)
        TextView description;
        @Bind(R.id.title)
        TextView title;
        public TitleViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    public class TaskStatusListAdapter extends BaseAdapter {
        private static final String TAG = "TaskStatusListAdapter";
        Context context;
        ArrayList<HashMap<String, String>> TaskStatusArray;
        public TaskStatusListAdapter(Context context, ArrayList<HashMap<String, String>> TaskStatusArray) {
            this.context = context;
            this.TaskStatusArray = TaskStatusArray;
        }
        @Override
        public int getCount() {
            return TaskStatusArray.size();
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(final int i, View convertView, ViewGroup parent) {
            View row = convertView;
            final TaskStatusViewHolder holder;
            if (row == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = mInflater.inflate(R.layout.tms_taskstatus_listrow, null);
                holder = new TaskStatusViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (TaskStatusViewHolder) row.getTag();
            }
            if (count < colorArray().length) {
                colorCode = colorArray()[count];
                count = count + 1;
            } else {
                count = 0;
            }
            holder.listSNoBgLayout.setBackground(getResources().getDrawable(colorCode));
            String sNo = String.valueOf(i + 1);
            holder.listSNoTextView.setText(sNo);
            holder.DateAndTime.setText(TaskStatusArray.get(i).get("DateAndTime"));
            holder.UpdatedInformation.setText(TaskStatusArray.get(i).get("UpdatedInformation"));
            holder.UpdatedBy.setText(TaskStatusArray.get(i).get("UpdatedBy"));
            holder.TaskSubTaskName.setText(TaskStatusArray.get(i).get("TaskSubTaskName"));
            return row;
        }
    }
    class TaskStatusViewHolder {
        //Title Components
        @Bind(R.id.list_sno_bg_layout)
        LinearLayout listSNoBgLayout;
        @Bind(R.id.list_sno)
        TextView listSNoTextView;
        @Bind(R.id.DateAndTime)
        TextView DateAndTime;
        @Bind(R.id.UpdatedInformation)
        TextView UpdatedInformation;
        @Bind(R.id.UpdatedBy)
        TextView UpdatedBy;
        @Bind(R.id.TaskSubTaskName)
        TextView TaskSubTaskName;
        public TaskStatusViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    public class TimeExtensionAdapter extends ExpandableListItemAdapter<Integer> {
        ArrayList<HashMap<String, String>> parentListValue = new ArrayList<HashMap<String, String>>();
        private Context mContex;
        private LayoutInflater mInflater;
        public TimeExtensionAdapter(Context context, ArrayList<HashMap<String, String>> parents) {
            super(context, R.layout.activity_expandablelistitem_card, R.id.activity_expandablelistitem_card_title, R.id.activity_expandablelistitem_card_content);
            mContex = context;
            this.parentListValue = parents;
            mInflater = LayoutInflater.from(context);
            for (int i = 0; i < parentListValue.size(); i++) {
                add(i);
            }
        }
        public View getContentView(final int i, View view, @NonNull ViewGroup viewGroup) {
            final TimeExtenChildViewHolder holder;
            View row = view;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContex
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = mInflater.inflate(R.layout.tms_timeextension_list_child, null);
                holder = new TimeExtenChildViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (TimeExtenChildViewHolder) row.getTag();
            }
            holder.ActualStartDate.setText(parentListValue.get(i).get("ActualStartDate"));
            holder.ActualEndDate.setText(parentListValue.get(i).get("ActualEndDate"));
            holder.ActualPlannedDate.setText(parentListValue.get(i).get("ActualPlannedDate"));
            holder.AssignedBy.setText(parentListValue.get(i).get("AssignedBy"));
            holder.DateAndTime2.setText(parentListValue.get(i).get("DateAndTime2"));
            holder.Status.setText(parentListValue.get(i).get("Status"));
            holder.Reason.setText(parentListValue.get(i).get("Reason"));
            return row;
        }
        public View getTitleView(int groupPosition, View convertView, ViewGroup viewGroup) {
            final TimeExtenTitleViewHolder holder;
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContex
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = mInflater.inflate(R.layout.tms_timeextension_list_title, null);
                holder = new TimeExtenTitleViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (TimeExtenTitleViewHolder) row.getTag();
            }
            if (count < colorArray().length) {
                colorCode = colorArray()[count];
                count = count + 1;
            } else {
                count = 0;
            }
            holder.listSNoTextView.setText(String.valueOf(groupPosition + 1));
            holder.listSNoBgLayout.setBackground(getResources().getDrawable(colorCode));
            holder.DateAndTime1.setText(parentListValue.get(groupPosition).get("DateAndTime1"));
            holder.NegotationBy.setText(parentListValue.get(groupPosition).get("NegotationBy"));
            return row;
        }
    }
    public class NegotationAdapter extends ExpandableListItemAdapter<Integer> {
        ArrayList<HashMap<String, String>> parentListValue = new ArrayList<HashMap<String, String>>();
        private Context mContex;
        private LayoutInflater mInflater;
        public NegotationAdapter(Context context, ArrayList<HashMap<String, String>> parents) {
            super(context, R.layout.activity_expandablelistitem_card, R.id.activity_expandablelistitem_card_title, R.id.activity_expandablelistitem_card_content);
            mContex = context;
            this.parentListValue = parents;
            mInflater = LayoutInflater.from(context);
            for (int i = 0; i < parentListValue.size(); i++) {
                add(i);
            }
        }
        public View getContentView(final int i, View view, @NonNull ViewGroup viewGroup) {
            final TimeExtenChildViewHolder holder;
            View row = view;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContex
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = mInflater.inflate(R.layout.tms_timeextension_list_child, null);
                holder = new TimeExtenChildViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (TimeExtenChildViewHolder) row.getTag();
            }
            holder.planedEndRow.setVisibility(View.VISIBLE);
            holder.planedStartDateLabel.setText("Planned Start date");
            holder.PlannedEndDate.setText(parentListValue.get(i).get("PlannedEnddate"));
            holder.ActualStartDate.setText(parentListValue.get(i).get("ActualStartDate"));
            holder.ActualEndDate.setText(parentListValue.get(i).get("ActualEndDate"));
            holder.ActualPlannedDate.setText(parentListValue.get(i).get("PlannedStartdate"));
            holder.AssignedBy.setText(parentListValue.get(i).get("AssignedBy"));
            holder.DateAndTime2.setText(parentListValue.get(i).get("DateAndTime2"));
            holder.Status.setText(parentListValue.get(i).get("Status"));
            holder.Reason.setText(parentListValue.get(i).get("Reason"));
            return row;
        }
        public View getTitleView(int groupPosition, View convertView, ViewGroup viewGroup) {
            final TimeExtenTitleViewHolder holder;
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContex
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = mInflater.inflate(R.layout.tms_timeextension_list_title, null);
                holder = new TimeExtenTitleViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (TimeExtenTitleViewHolder) row.getTag();
            }
            if (count < colorArray().length) {
                colorCode = colorArray()[count];
                count = count + 1;
            } else {
                count = 0;
            }
            holder.listSNoTextView.setText(String.valueOf(groupPosition + 1));
            holder.listSNoBgLayout.setBackground(getResources().getDrawable(colorCode));
            holder.DateAndTime1.setText(parentListValue.get(groupPosition).get("DateAndTime1"));
            holder.NegotationBy.setText(parentListValue.get(groupPosition).get("NegotationBy"));
            return row;
        }
    }
    class TimeExtenTitleViewHolder {
        //Title Components
        @Bind(R.id.list_sno_bg_layout)
        LinearLayout listSNoBgLayout;
        @Bind(R.id.list_sno)
        TextView listSNoTextView;
        @Bind(R.id.NegotationBy)
        TextView NegotationBy;
        @Bind(R.id.DateAndTime1)
        TextView DateAndTime1;
        public TimeExtenTitleViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    class TimeExtenChildViewHolder {
        @Bind(R.id.ActualStartDate)
        TextView ActualStartDate;
        @Bind(R.id.ActualEndDate)
        TextView ActualEndDate;
        @Bind(R.id.ActualPlannedDate)
        TextView ActualPlannedDate;
        @Bind(R.id.AssignedBy)
        TextView AssignedBy;
        @Bind(R.id.DateAndTime2)
        TextView DateAndTime2;
        @Bind(R.id.Status)
        TextView Status;
        @Bind(R.id.Reason)
        TextView Reason;
        @Bind(R.id.PlannedEndDate)
        TextView PlannedEndDate;
        @Bind(R.id.planedStartDateLabel)
        TextView planedStartDateLabel;
        @Bind(R.id.planedEndRow)
        TableRow planedEndRow;
        public TimeExtenChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    public class HistoryListAdapter extends BaseAdapter {
        private static final String TAG = "HistoryListAdapter";
        Context context;
        ArrayList<HashMap<String, String>> historyArray;
        public HistoryListAdapter(Context context, ArrayList<HashMap<String, String>> historyArray) {
            this.context = context;
            this.historyArray = historyArray;
        }
        @Override
        public int getCount() {
            return historyArray.size();
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(final int i, View convertView, ViewGroup parent) {
            View row = convertView;
            final HistoryTitleViewHolder holder;
            if (row == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = mInflater.inflate(R.layout.tms_history_listrow, null);
                holder = new HistoryTitleViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (HistoryTitleViewHolder) row.getTag();
            }
            if (count < colorArray().length) {
                colorCode = colorArray()[count];
                count = count + 1;
            } else {
                count = 0;
            }
            holder.listSNoBgLayout.setBackground(getResources().getDrawable(colorCode));
            String sNo = String.valueOf(i + 1);
            holder.listSNoTextView.setText(sNo);
            holder.assignTo.setText(historyArray.get(i).get("AssignTo"));
            holder.endTime.setText(historyArray.get(i).get("DateAndTime"));
            holder.assignBy.setText(historyArray.get(i).get("AssignBy"));
            holder.action.setText(historyArray.get(i).get("Action"));
            holder.userId.setText(historyArray.get(i).get("UserNameId"));
            return row;
        }
    }
    class HistoryTitleViewHolder {
        //Title Components
        @Bind(R.id.list_sno_bg_layout)
        LinearLayout listSNoBgLayout;
        @Bind(R.id.list_sno)
        TextView listSNoTextView;
        @Bind(R.id.assignTo_value)
        TextView assignTo;
        @Bind(R.id.assignBy_value)
        TextView assignBy;
        @Bind(R.id.end_time_value)
        TextView endTime;
        @Bind(R.id.action_value)
        TextView action;
        @Bind(R.id.userName_id)
        TextView userId;
        public HistoryTitleViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
