package com.guruinfo.scm.DPR;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.bowyer.app.fabtransitionlayout.BottomSheetLayout;
import com.guruinfo.scm.ApprovalStatus;
import com.guruinfo.scm.ApprovalStatusDao;
import com.guruinfo.scm.CommMasterDetails;
import com.guruinfo.scm.CommMasterDetailsDao;
import com.guruinfo.scm.Contractors;
import com.guruinfo.scm.ContractorsDao;
import com.guruinfo.scm.DPR.bean.DateInfo;
import com.guruinfo.scm.DPR.bean.RoomInfo;
import com.guruinfo.scm.DPR.bean.rowInfo;
import com.guruinfo.scm.DaoMaster;
import com.guruinfo.scm.MaterialsMaster;
import com.guruinfo.scm.MaterialsMasterDao;
import com.guruinfo.scm.PendingApproval;
import com.guruinfo.scm.PendingApprovalDao;
import com.guruinfo.scm.PendingRequestListDao;
import com.guruinfo.scm.PjtStore;
import com.guruinfo.scm.PjtStoreDao;
import com.guruinfo.scm.ProjContractorMasterView;
import com.guruinfo.scm.ProjContractorMasterViewDao;
import com.guruinfo.scm.ProjFormulaMaster;
import com.guruinfo.scm.ProjFormulaMasterDao;
import com.guruinfo.scm.ProjIowMasterDao;
import com.guruinfo.scm.ProjJobStageGridIowDao;
import com.guruinfo.scm.ProjJobStageGridMasterDao;
import com.guruinfo.scm.ProjLabourMaster;
import com.guruinfo.scm.ProjLabourMasterDao;
import com.guruinfo.scm.ProjMbookIowGridChildDao;
import com.guruinfo.scm.ProjMbookIowNmrChildDao;
import com.guruinfo.scm.ProjMbookMasterDao;
import com.guruinfo.scm.ProjMbookSubActivityChildDao;
import com.guruinfo.scm.ProjMbookiowChildDao;
import com.guruinfo.scm.ProjProjectMasterDao;
import com.guruinfo.scm.ProjStageChild;
import com.guruinfo.scm.ProjStageChildDao;
import com.guruinfo.scm.ProjWoMaster;
import com.guruinfo.scm.ProjWoMasterDao;
import com.guruinfo.scm.R;
import com.guruinfo.scm.RightsTable;
import com.guruinfo.scm.RightsTableDao;
import com.guruinfo.scm.SCMDashboardActivityLatest;
import com.guruinfo.scm.common.BaseFragment;
import com.guruinfo.scm.common.SCMApplication;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.db.SCMDataBaseOpenHelper;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.ui.SCMMandatoryEditText;
import com.guruinfo.scm.common.ui.SCMTextView;
import com.guruinfo.scm.common.utils.ApiCalls;
import com.guruinfo.scm.common.utils.FormValidation;
import com.guruinfo.scm.common.utils.Sharedpref;
import com.kelin.scrollablepanel.library.PanelAdapter;
import com.kelin.scrollablepanel.library.ScrollablePanel;
import com.paging.listview.PagingBaseAdapter;
import com.paging.listview.PagingListView;
import com.ramotion.foldingcell.FoldingCell;
import org.greenrobot.greendao.database.Database;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static com.guruinfo.scm.SCMDashboardActivityLatest.NavigationFragmentManager;
import static com.guruinfo.scm.common.AppContants.DASHBOARDOFFLINEMODE;
public class DPROfflineListFragment extends BaseFragment {
    private static final String TAG = "DPROfflineListFragment";
    Context context;
    SessionManager session;
    static String uid, Cre_Id;
    BackgroundTask backgroundTask;
    String requestParameter;
    String listRequestParameter;
    boolean isIcon = true;
    String notificationCount = "";
    @Bind(R.id.total_layout)
    LinearLayout totalLayout;
    String listType = "";
    static String request = null;
    ArrayList<String> selectedMRItemsArrayList;
    JSONArray approvalJsonArray = new JSONArray();
    private boolean approvalRights;
    Boolean updateButton = false, addNew = false, rightsApproval = false;
    private int lastExpandedPosition = -1;
    ArrayList<HashMap<String, String>> DPRArrayList;
    HashMap<Integer, View> ChildView;
    @Bind(R.id.error_msg)
    SCMTextView emptyMsgTextView;
    @Bind(R.id.footer)
    LinearLayout footerLayout;
    View newRequestView;
    int pageNo = 0;
    int totalPage = 1;
    static String flagName = "";
    TextView footerButton1;
    TextView footerButton3;
    TextView footerButton4;
    TextView footerButton2;
    TextView footerButton5;
    DPRListAdapter adapter;
    Boolean filterBtn = true;
    @Bind(R.id.vmf_list_view)
    PagingListView dprListView;
    LinearLayout footer_fifth_button_layout;
    String approvalStatusValue, descriptionValue;
    String footerButton1Value, footerButton2Value, footerButton3Value, footerButton4Value, footerButton5Value;
    static EditText requestReturnable;
    Boolean approvalscreen = false;
    HashMap<Integer, String> childDetails;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    String colorCode;
    View clickView;
    Boolean newRequestBtn = false;
    Boolean approvalBtn = false;
    PendingRequestListDao pendingRequestListDao;
    ArrayList<String> projectNameList = new ArrayList<>();
    ArrayList<String> projectIdList = new ArrayList<>();
    @Bind(R.id.bottom_sheet)
    BottomSheetLayout mBottomSheetLayout;
    FoldingCell childFoldingCell;
    Animation animation;
    int childClickPos;
    @Bind(R.id.list_menu)
    ListView mMenuList;
    ContractorsDao contractorsDao;
    ProjMbookMasterDao projMbookMasterDao;
    ProjContractorMasterViewDao projContractorMasterViewDao;
    ProjProjectMasterDao projProjectMasterDao;
    ProjMbookSubActivityChildDao projMbookSubActivityChildDao;
    ProjMbookIowNmrChildDao projMbookIowNmrChildDao;
    ProjMbookiowChildDao projMbookiowChildDao;
    ProjStageChildDao projStageChildDao;
    ProjLabourMasterDao projLabourMasterDao;
    ProjFormulaMasterDao projFormulaMasterDao;
    ProjMbookIowGridChildDao projMbookIowGridChildDao;
    ProjJobStageGridIowDao projJobStageGridIowDao;
    ProjJobStageGridMasterDao projJobStageGridMasterDao;
    ProjIowMasterDao projIowMasterDao;
    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    HashMap<Integer, String> contractorFreeze = new HashMap<>();
    String StageValue = "";
    @Bind(R.id.add_new)
    ImageView addNewImage;
    ProgressDialog mDialog;
    public static DPROfflineListFragment newInstance(Bundle bundle) {
        DPROfflineListFragment fragment = new DPROfflineListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        inflater.inflate(R.menu.list_action, menu);
        menu.findItem(R.id.new_request).setVisible(false);
        if (isIcon) {
            if (newRequestBtn) {
                mFab.setVisibility(View.VISIBLE);
            } else {
                mFab.setVisibility(View.GONE);
            }
            if (filterBtn)
                menu.findItem(R.id.filter).setVisible(true);
            else
                menu.findItem(R.id.filter).setVisible(false);
            if (approvalBtn) {
                menu.findItem(R.id.list).setVisible(true);
                menu.findItem(R.id.approval_count).setVisible(false);
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>DPR APPROVAL</font>"));
            } else {
                menu.findItem(R.id.list).setVisible(false);
                if ((!approvalBtn) && approvalRights) {
                    ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>DPR</font>"));
                    menu.findItem(R.id.approval_count).setVisible(true);
                    final MenuItem myActionMenuItem = menu.findItem(R.id.approval_count);
                    TextView notiCount = (TextView) myActionMenuItem.getActionView().findViewById(R.id.tv_counter);
                    notiCount.setText(notificationCount);
                    final View noti = (myActionMenuItem).getActionView();
                    noti.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>DPR OFFLINE APPROVAL</font>"));
                            Bundle bundle = new Bundle();
                            String req = "{'Action':'DAILY_PROGRESS_REPORT','submode':'DPR_APPROVAL_SEARCH','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','Mat_Req_search':'" + 1 + "','Mat_Req_MBook_ProjName':'','Mat_Req_Mbook_Id':'','Mat_Req_MBook_RefNo':'','Mat_Req_MBook_Contractor':'','Mat_Req_MBook_FromDate':'','Mat_Req_MBook_ToDate':'','Mat_Req_MBook_status':'','Mat_Req_Mat_Req_MIR_StageName':'','Mat_Req_Mat_Req_MIR_IOW':'','Mat_Req_wo_Id':'','Mat_Req_BlockId':'','FrmNameCtl':'Mat_Req','RS':' 10 '}";
                            bundle.putString("load", req);
                            bundle.putString("IsApproval", "true");
                            NavigationFragmentManager(DPROfflineListFragment.newInstance(bundle), "DPR APPROVAL");
                        }
                    });
                } else
                    menu.findItem(R.id.approval_count).setVisible(false);
            }
            menu.findItem(R.id.search).setVisible(true);
            menu.findItem(R.id.favourite).setVisible(false);
            try {
                // Associate searchable configuration with the SearchView
                SearchManager searchManager =
                        (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
                SearchView searchView =
                        (SearchView) menu.findItem(R.id.search).getActionView();
                searchView.setSearchableInfo(
                        searchManager.getSearchableInfo(getActivity().getComponentName()));
                EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
                searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
                searchEditText.setTextColor(getResources().getColor(R.color.black));
                ImageView searchCloseIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
                searchCloseIcon.setColorFilter(getResources().getColor(R.color.black));
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        // do your search
                        Log.d(TAG, "Search Clicked");
                        Bundle bundle = new Bundle();
                        String keyWord = s.toString();
                        String req = "{'Action':'DAILY_PROGRESS_REPORT','submode':'DPR_ALL_SEARCH','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','Mat_Req_search':'" + 1 + "','purpose':'searchList','word':'" + keyWord + "','Mat_Req_MBook_ProjName':'','Mat_Req_Mbook_Id':'','Mat_Req_MBook_RefNo':'','Mat_Req_MBook_Contractor':'','Mat_Req_MBook_FromDate':'','Mat_Req_MBook_ToDate':'','Mat_Req_MBook_status':'','Mat_Req_Mat_Req_MIR_StageName':'','Mat_Req_Mat_Req_MIR_IOW':'','Mat_Req_wo_Id':'','Mat_Req_BlockId':'','FrmNameCtl':'Mat_Req','RS':' 10 '}";
                        bundle.putString("load", req);
                        ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>DPR</font>"));
                        NavigationFragmentManager(DPROfflineListFragment.newInstance(bundle), "DPR");
                        return true;
                    }
                    @Override
                    public boolean onQueryTextChange(String s) {
                        // do your search on change or save the last string or...
                        return false;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            menu.findItem(R.id.new_request).setVisible(false);
            menu.findItem(R.id.favourite).setVisible(false);
            menu.findItem(R.id.filter).setVisible(false);
            menu.findItem(R.id.list).setVisible(false);
            menu.findItem(R.id.search).setVisible(false);
            menu.findItem(R.id.approval_count).setVisible(false);
        }
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                menu.findItem(R.id.new_request).setVisible(false);
                menu.findItem(R.id.favourite).setVisible(false);
                menu.findItem(R.id.filter).setVisible(false);
                menu.findItem(R.id.list).setVisible(false);
                menu.findItem(R.id.approval_count).setVisible(false);
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (Build.VERSION.SDK_INT >= 11) {
                    getActivity().invalidateOptionsMenu();
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.filter:
                if (!(Sharedpref.getPrefBoolean(context, DASHBOARDOFFLINEMODE))) {
                    Bundle bundle = new Bundle();
                    if (listType.equalsIgnoreCase("DPR_ALL_SEARCH")) {
                        bundle.putString("LIST_ACTION", "DAILY_PROGRESS_REPORT");
                        bundle.putString("LIST_SUBMODE", "DPR_ALL_SEARCH");
                        bundle.putString("KEY", "PROJ_DPR_ALL");
                        bundle.putString("SEARCH_TYPE", "DPR SEARCH");
                    } else if (listType.equalsIgnoreCase("DPR_HIERARCHY_SEARCH")) {
                        bundle.putString("LIST_ACTION", "DAILY_PROGRESS_REPORT");
                        bundle.putString("LIST_SUBMODE", "DPR_HIERARCHY_SEARCH");
                        bundle.putString("KEY", "PROJ_DPR_HIERARCHY");
                        bundle.putString("SEARCH_TYPE", "DPR SEARCH");
                    } else if (listType.equalsIgnoreCase("DPR_APPROVAL_SEARCH")) {
                        bundle.putString("LIST_ACTION", "DAILY_PROGRESS_REPORT");
                        bundle.putString("LIST_SUBMODE", "DPR_APPROVAL_SEARCH");
                        bundle.putString("KEY", "PROJ_DPR_APPROVAL");
                        bundle.putString("SEARCH_TYPE", "DPR APPROVAL SEARCH");
                    }
                    NavigationFragmentManager(DPRFilterFragment.newInstance(bundle), "DPR");
                } else
                    setToast("Offline Mode Enabled");
                return true;
            case R.id.list:
                if (!(Sharedpref.getPrefBoolean(context, DASHBOARDOFFLINEMODE))) {
                    ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>DPR</font>"));
                    NavigationFragmentManager(DPROfflineListFragment.newInstance(null), "DPR");
                } else
                    setToast("Offline Mode Enabled");
                return true;
            case R.id.search:
                return true;
            default:
                searchView.setOnQueryTextListener(queryTextListener);
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
        selectedMRItemsArrayList = new ArrayList<>();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.paging_expandable_list, container, false);
        ButterKnife.bind(this, view);
        mDialog = new ProgressDialog(context);
        contractorFreeze = new HashMap<>();
        pendingRequestListDao = daoSession.getPendingRequestListDao();
        projMbookMasterDao = daoSession.getProjMbookMasterDao();
        projContractorMasterViewDao = daoSession.getProjContractorMasterViewDao();
        projProjectMasterDao = daoSession.getProjProjectMasterDao();
        projMbookSubActivityChildDao = daoSession.getProjMbookSubActivityChildDao();
        projMbookIowNmrChildDao = daoSession.getProjMbookIowNmrChildDao();
        projMbookiowChildDao = daoSession.getProjMbookiowChildDao();
        projStageChildDao = daoSession.getProjStageChildDao();
        projLabourMasterDao = daoSession.getProjLabourMasterDao();
        projFormulaMasterDao = daoSession.getProjFormulaMasterDao();
        projMbookIowGridChildDao = daoSession.getProjMbookIowGridChildDao();
        projJobStageGridIowDao = daoSession.getProjJobStageGridIowDao();
        projJobStageGridMasterDao = daoSession.getProjJobStageGridMasterDao();
        projIowMasterDao = daoSession.getProjIowMasterDao();
        contractorsDao = daoSession.getContractorsDao();
        requestReturnable = (EditText) view.findViewById(R.id.newRequestReturnable);
        dprListView.setVisibility(View.VISIBLE);
        totalLayout.setVisibility(View.GONE);
        DPRArrayList = new ArrayList<HashMap<String, String>>();
        childDetails = new HashMap<>();
        ChildView = new HashMap<Integer, View>();
        totalPage = 1;
        pageNo = 0;
        initializeFooter();
        initListMenu();
        mBottomSheetLayout.setFab(mFab);
        List<Contractors> contractorList = contractorsDao.queryBuilder().list();
        for (int i = 0; i < contractorList.size(); i++) {
            contractorFreeze.put(Integer.parseInt(contractorList.get(i).getLoad_id()), contractorList.get(i).getFreeze());
        }
        ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>DPR OFFLINE APPROVAL</font>"));
       /* if (Sharedpref.getPrefBoolean(context, DASHBOARDOFFLINEMODE)) {
            ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.actionbar_title)+"'>DPR</font>"));
            offlineRights();
            filterBtn = false;
            updateButton = false;
            setHasOptionsMenu(true);
            adapter = new DPRListAdapter(context);
            dprListView.setAdapter(adapter);
            dprListView.setHasMoreItems(true);
            offlineLoadDisplay();
        } else {*/
        onLoad();
        // }
        addNewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetLayout.contractFab();
            }
        });
        animation = AnimationUtils.loadAnimation(context,
                R.anim.anim_slide_out_right);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                adapter.notifyDataSetChanged();
            }
        });
        return view;
    }
    private void initListMenu() {
        projectNameList = new ArrayList<>();
        projectIdList = new ArrayList<>();
        PjtStoreDao pjtStoreDao = daoSession.getPjtStoreDao();
        List<PjtStore> projectLists = pjtStoreDao.queryBuilder().where(PjtStoreDao.Properties.User_id.eq(uid)).list();
        if (projectLists.size() > 0)
            for (int i = 0; i < projectLists.size(); i++) {
                projectNameList.add(projectLists.get(i).getValue());
                projectIdList.add(projectLists.get(i).getLoad_id());
            }
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.mr_new_request_listrow, projectNameList);
        mMenuList.setAdapter(adapter);
        mMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
                mBottomSheetLayout.contractFab();
                int TIME_OUT = 200;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                           /* String proName = projectNameList.get(position);
                            String proId = projectIdList.get(position);
                            Intent intent = new Intent(context, VMFNewAndEditApprovalFragment.class);
                            intent.putExtra("response", "");
                            intent.putExtra("colorCode", "#34495e");
                            intent.putExtra("responseType", "newRequest");
                            intent.putExtra("projectName", proName);
                            intent.putExtra("projectId", proId);
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), mFab, getString(R.string.transition_item));
                            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());*/
                        } catch (Exception e) {
                        }
                    }
                }, TIME_OUT);
            }
        });
    }
    @OnClick(R.id.fab)
    void onFabClick() {
        if (projectNameList.size() > 0) {
            mBottomSheetLayout.expandFab();
        } else
            setToast("Your Projects List is Empty. Please Sync Your Local Database.");
    }
    public void offlineRights() {
        RightsTableDao rightsTableDao = daoSession.getRightsTableDao();
        List<RightsTable> rightsTables = rightsTableDao.queryBuilder().where(RightsTableDao.Properties.User_id.eq(uid)).list();
        if (rightsTables.size() > 0) {
            try {
                JSONObject response = new JSONObject(rightsTables.get(0).getDash_board());
                JSONObject rightsApprovalObject = response.getJSONObject("Rights");
                JSONArray requestArray = rightsApprovalObject.getJSONArray("Load_Request");
                for (int i = 0; i < requestArray.length(); i++) {
                    JSONObject rightsObject = requestArray.getJSONObject(i);
                    if (rightsObject.getString("Action").equalsIgnoreCase("SCM_DPR_ADD")) {
                        newRequestBtn = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            newRequestBtn = false;
        }
    }
    public void onLoad() {
        adapter = new DPRListAdapter(context);
        dprListView.setAdapter(adapter);
        dprListView.setHasMoreItems(true);
        dprListView.setPagingableListener(new PagingListView.Pagingable() {
            @Override
            public void onLoadMoreItems() {
                if (pageNo < totalPage) {
                    pageNo++;
                    new listLoad().execute(pageNo);
                } else {
                    dprListView.onFinishLoading(false, null, totalPage);
                }
            }
        });
    }
    public String selectedBlockName(String stageId) {
        String blockName = "";
        if (!(stageId.equalsIgnoreCase("0"))) {
            List<ProjStageChild> stageList = projStageChildDao.queryBuilder().where(ProjStageChildDao.Properties.Stage_id.eq(stageId)).list();
            if (stageList.size() > 0) {
                blockName = stageList.get(0).getStage_name();
            }
        }
        return blockName;
    }
    public String selectedStageName(String stageId) {
        if (!(stageId.equalsIgnoreCase("0"))) {
            List<ProjStageChild> stageList = projStageChildDao.queryBuilder().where(ProjStageChildDao.Properties.Stage_id.eq(stageId)).list();
            if (stageList.size() > 0) {
                if (StageValue.equalsIgnoreCase(""))
                    StageValue = stageList.get(0).getStage_name();
                else
                    StageValue = stageList.get(0).getStage_name() + " -> " + StageValue;
                selectedStageName(stageList.get(0).getStage_parent_id());
            }
        }
        return StageValue;
    }
    public class listLoad extends AsyncTask<Integer, String, String> {
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected String doInBackground(Integer... params) {
            try {
                listType = "DPR_APPROVAL_SEARCH";
                System.out.println("DPR LIST TYPE" + listType);
                //Approval Status
                ApprovalStatusDao approvalStatusDao = daoSession.getApprovalStatusDao();
                List<ApprovalStatus> approvalList = approvalStatusDao.queryBuilder().where(ApprovalStatusDao.Properties.User_id.eq(uid), ApprovalStatusDao.Properties.Process_name.eq("PROJ_MBOOK_REQCONFIG")).list();
                if (approvalList.size() > 0) {
                    String approvalStat = approvalList.get(0).getApproval_status();
                    JSONObject approvalRightsJSONObject = new JSONObject(approvalList.get(0).getApproval_rights());
                    try {
                        approvalRights = Boolean.parseBoolean(approvalRightsJSONObject.getString("0"));
                    } catch (Exception e) {
                        approvalRights = Boolean.parseBoolean(approvalRightsJSONObject.getString("1"));
                    }
                    if (approvalRights) {
                        String subQry = "";
                        ArrayList<String> approvalPendingList = new ArrayList<>();
                        HashMap<String, String> pjtList = new HashMap<>();
                        PjtStoreDao pjtStoreDao = daoSession.getPjtStoreDao();
                        List<PjtStore> projectLists = pjtStoreDao.queryBuilder().where(PjtStoreDao.Properties.User_id.eq(uid)).list();
                        if (projectLists.size() > 0)
                            for (int i = 0; i < projectLists.size(); i++) {
                                pjtList.put(projectLists.get(i).getLoad_id(), projectLists.get(i).getValue());
                            }
                        PendingApprovalDao pendingApprovalDao = daoSession.getPendingApprovalDao();
                        List<PendingApproval> pendingApprovalList = pendingApprovalDao.queryBuilder().where(PendingApprovalDao.Properties.User_id.eq(uid), PendingApprovalDao.Properties.Process_name.eq("DPR_APPROVAL")).list();
                        if (pendingApprovalList.size() > 0)
                            for (int i = 0; i < pendingApprovalList.size(); i++) {
                                approvalPendingList.add(pendingApprovalList.get(i).getProcess_id());
                            }
                        if (approvalList.size() > 0) {
                            subQry = approvalList.get(0).getValidation_qry();
                            subQry = subQry.replace("req_status ", "a.req_status ");
                            subQry = subQry.replace("req_process_status ", "a.req_process_status ");
                        }
                        //total Count
                        String totalqry = "SELECT a.mbook_id ,a.mbook_code ,a.from_date ,e.contractor_name AS contractor_name ,a.to_date ,f.project_name AS proj_name ,a.req_type ,a.req_status ,a.req_process_status ,a.req_by , a.req_date ,a.approved_by ,a.approval_date ,a.req_description ,a.req_history ,a.req_comments ,a.proj_id, IFNULL(b.others_iow,0) AS others_iow,IFNULL(c.nmr,0) AS nmr,IFNULL(d.sub_activity,0) AS sub_activity,a.contractor_id  ,subact_val_exception,nmr_val_exception ,a.block_id  \n" +
                                "FROM (" + projMbookMasterDao.getTablename() + " AS a," + projContractorMasterViewDao.getTablename() + " AS e," + projProjectMasterDao.getTablename() + " AS f) " +
                                "LEFT JOIN (SELECT mbook_id,COUNT(*) AS others_iow FROM " + projMbookiowChildDao.getTablename() + " GROUP BY mbook_id) AS b ON a.mbook_id = b.mbook_id " +
                                "LEFT JOIN (SELECT mbook_id,COUNT(*) AS nmr FROM " + projMbookIowNmrChildDao.getTablename() + " GROUP BY mbook_id) AS c ON a.mbook_id = c.mbook_id " +
                                "LEFT JOIN (SELECT mbook_id,COUNT(*) AS sub_activity " +
                                "FROM " + projMbookSubActivityChildDao.getTablename() + " GROUP BY mbook_id) AS d ON a.mbook_id = d.mbook_id " +
                                "WHERE 0=0 AND a.contractor_id = e.contractor_id AND a.proj_id=f.project_id  AND 0=0  AND a.req_status IN('pending','rejected') " +
                                "AND " + subQry + " ORDER BY a.mbook_id DESC";
                        Database db1 = ((SCMApplication) context.getApplicationContext()).getreadableDb();
                        Cursor c2 = db1.rawQuery(totalqry, null);
                        int page = c2.getCount() / 10;
                        int addPage = c2.getCount() % 10;
                        if (addPage > 0) {
                            page = page + 1;
                        }
                        totalPage = page;
                        c2.close();
                        int pageStart = 0;
                        if (pageNo == 1) {
                            pageStart = 0;
                        } else {
                            pageStart = (pageNo - 1) * 10;
                        }
                        String qry = "SELECT a.mbook_id ,a.mbook_code ,a.from_date ,e.contractor_name AS contractor_name ,a.to_date ,f.project_name AS proj_name ,a.req_type ,a.req_status ,a.req_process_status ,a.req_by , a.req_date ,a.approved_by ,a.approval_date ,a.req_description ,a.req_history ,a.req_comments , a.proj_id , IFNULL(b.others_iow,0) AS others_iow,IFNULL(c.nmr,0) AS nmr,IFNULL(d.sub_activity,0) AS sub_activity,a.contractor_id  ,subact_val_exception,nmr_val_exception ,a.block_id , a.pending_with , a.wo_id " +
                                "FROM (" + projMbookMasterDao.getTablename() + " AS a," + projContractorMasterViewDao.getTablename() + " AS e," + projProjectMasterDao.getTablename() + " AS f) " +
                                "LEFT JOIN (SELECT mbook_id,COUNT(*) AS others_iow FROM " + projMbookiowChildDao.getTablename() + " GROUP BY mbook_id) AS b ON a.mbook_id = b.mbook_id " +
                                "LEFT JOIN (SELECT mbook_id,COUNT(*) AS nmr FROM " + projMbookIowNmrChildDao.getTablename() + " GROUP BY mbook_id) AS c ON a.mbook_id = c.mbook_id " +
                                "LEFT JOIN (SELECT mbook_id,COUNT(*) AS sub_activity " +
                                "FROM " + projMbookSubActivityChildDao.getTablename() + " GROUP BY mbook_id) AS d ON a.mbook_id = d.mbook_id " +
                                "WHERE 0=0 AND a.contractor_id = e.contractor_id AND a.proj_id=f.project_id  " +
                                "AND " + subQry + " ORDER BY a.mbook_id DESC LIMIT " + pageStart + ",10";
                        Database db = ((SCMApplication) context.getApplicationContext()).getreadableDb();
                        Cursor c = db.rawQuery(qry, null);
                        c.moveToFirst();
                        int parentColorIndex = 0;
                        while (c.isAfterLast() == false) {
                            try {
                                if (!(approvalPendingList.contains(c.getString(0)))) {
                                    StageValue = "";
                                    String mBookId = c.getString(0);
                                    HashMap<String, String> hashMap = new HashMap<String, String>();
                                    if (parentColorIndex > 11) {
                                        parentColorIndex = 0;
                                        hashMap.put("color", "" + colorArray()[parentColorIndex]);
                                    } else {
                                        hashMap.put("color", "" + colorArray()[parentColorIndex]);
                                        parentColorIndex = parentColorIndex + 1;
                                    }
                                    hashMap.put("mbookID", mBookId);
                                    String dprcode = c.getString(0) + "-" + c.getString(1);
                                    hashMap.put("dprRefNo", dprcode);
                                    if (contractorFreeze.containsKey(Integer.parseInt(c.getString(20))))
                                        hashMap.put("isFreez", contractorFreeze.get(Integer.parseInt(c.getString(20))));
                                    else
                                        hashMap.put("isFreez", "false");
                                    String to_date = nullCheck(c.getString(4));
                                    to_date = to_date.contains("1000") ? "-" : DBDateToAppDate(to_date, false, true);
                                    String from_date = nullCheck(c.getString(2));
                                    from_date = from_date.contains("1000") ? "-" : DBDateToAppDate(from_date, false, true);
                                    hashMap.put("isEdit", c.getString(3));
                                    String statusName = c.getString(8);
                                    String s2 = statusName.substring(0, 1).toUpperCase();
                                    String typeCapitalized1 = s2 + statusName.substring(1);
                                    hashMap.put("status", typeCapitalized1);
                                    hashMap.put("blockName", selectedBlockName(c.getString(23)));
                                    hashMap.put("block", selectedStageName(c.getString(23)));
                                    hashMap.put("projectId", c.getString(16));
                                    hashMap.put("history", c.getString(14));
                                    hashMap.put("description", c.getString(13));
                                    hashMap.put("toDate", to_date);
                                    hashMap.put("contractorName", c.getString(3));
                                    hashMap.put("contractorId", c.getString(20));
                                    hashMap.put("preparedBy", empNameFormat(c.getString(9)));
                                    hashMap.put("pendingWith", empNameFormat(c.getString(24)));
                                    hashMap.put("woId", c.getString(25));
                                    hashMap.put("approvedBy", empNameFormat(c.getString(11)));
                                    hashMap.put("project", c.getString(5));
                                    hashMap.put("fromDate", from_date);
                                    hashMap.put("subactValException", "false");
                                    hashMap.put("isOffline", "true");
                                    hashMap.put("isSelected", "false");
                                    if ((Integer.parseInt(c.getString(17)) > 0) || (Integer.parseInt(c.getString(18)) > 0) || (Integer.parseInt(c.getString(19))) > 0) {
                                        hashMap.put("childTable", "true");
                                    } else
                                        hashMap.put("childTable", "false");
                                    DPRArrayList.add(hashMap);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            c.moveToNext();
                        }
                        c.close();
                        RightsTableDao rightsTableDao = daoSession.getRightsTableDao();
                        List<RightsTable> dashboardRights = rightsTableDao.queryBuilder().where(RightsTableDao.Properties.User_id.eq(uid)).list();
                        if (dashboardRights.size() > 0)
                            try {
                                JSONObject rightsObj = new JSONObject(dashboardRights.get(0).getDash_board());
                                JSONObject rightsApprovalObject = rightsObj.getJSONObject("Rights");
                                JSONArray requestArray = rightsApprovalObject.getJSONArray("Load_Request");
                                for (int i = 0; i < requestArray.length(); i++) {
                                    JSONObject rightsObject = requestArray.getJSONObject(i);
                                    if (rightsObject.getString("Action").equalsIgnoreCase("SCM_PO_APPROVAL")) {
                                        rightsApproval = true;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                    approvalJsonArray = new JSONArray(approvalStat);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String load) {
            if (!approvalRights) {
                dprListView.setVisibility(View.GONE);
                emptyMsgTextView.setVisibility(View.VISIBLE);
                emptyMsgTextView.setText("You Dont Have an Approval Rights");
            }
            if (approvalJsonArray.length() > 0) {
                ArrayList<HashMap<String, String>> approvalStatusArrayList = ApiCalls.getArraylistfromJson(approvalJsonArray.toString());
                int approveButtonNumbers = approvalStatusArrayList.size();
                if (approveButtonNumbers == 1) {
                    footerButton1.setVisibility(View.VISIBLE);
                    footerButton2.setVisibility(View.GONE);
                    footerButton3.setVisibility(View.GONE);
                    footerButton4.setVisibility(View.GONE);
                    footerButton5.setVisibility(View.GONE);
                    footerButton1.setText(approvalStatusArrayList.get(0).get("displayName"));
                    footerButton1Value = approvalStatusArrayList.get(0).get("Value");
                } else if (approveButtonNumbers == 2) {
                    footerButton1.setVisibility(View.VISIBLE);
                    footerButton2.setVisibility(View.VISIBLE);
                    footerButton3.setVisibility(View.GONE);
                    footerButton4.setVisibility(View.GONE);
                    footerButton5.setVisibility(View.GONE);
                    footerButton1.setText(approvalStatusArrayList.get(0).get("displayName"));
                    footerButton1Value = approvalStatusArrayList.get(0).get("Value");
                    footerButton2.setText(approvalStatusArrayList.get(1).get("displayName"));
                    footerButton2Value = approvalStatusArrayList.get(1).get("Value");
                } else if (approveButtonNumbers == 3) {
                    footerButton1.setVisibility(View.VISIBLE);
                    footerButton2.setVisibility(View.VISIBLE);
                    footerButton3.setVisibility(View.VISIBLE);
                    footerButton4.setVisibility(View.GONE);
                    footerButton5.setVisibility(View.GONE);
                    footerButton1.setText(approvalStatusArrayList.get(0).get("displayName"));
                    footerButton1Value = approvalStatusArrayList.get(0).get("Value");
                    footerButton2.setText(approvalStatusArrayList.get(1).get("displayName"));
                    footerButton2Value = approvalStatusArrayList.get(1).get("Value");
                    footerButton3.setText(approvalStatusArrayList.get(2).get("displayName"));
                    footerButton3Value = approvalStatusArrayList.get(2).get("Value");
                } else if (approveButtonNumbers == 4) {
                    footerButton1.setVisibility(View.VISIBLE);
                    footerButton2.setVisibility(View.VISIBLE);
                    footerButton3.setVisibility(View.VISIBLE);
                    footerButton4.setVisibility(View.VISIBLE);
                    footerButton5.setVisibility(View.GONE);
                    footerButton1.setText(approvalStatusArrayList.get(0).get("displayName"));
                    footerButton1Value = approvalStatusArrayList.get(0).get("Value");
                    footerButton2.setText(approvalStatusArrayList.get(1).get("displayName"));
                    footerButton2Value = approvalStatusArrayList.get(1).get("Value");
                    footerButton3.setText(approvalStatusArrayList.get(2).get("displayName"));
                    footerButton3Value = approvalStatusArrayList.get(2).get("Value");
                    footerButton4.setText(approvalStatusArrayList.get(3).get("displayName"));
                    footerButton4Value = approvalStatusArrayList.get(3).get("Value");
                } else if (approveButtonNumbers == 5) {
                    footerButton1.setVisibility(View.VISIBLE);
                    footerButton2.setVisibility(View.VISIBLE);
                    footerButton3.setVisibility(View.VISIBLE);
                    footerButton4.setVisibility(View.VISIBLE);
                    footerButton5.setVisibility(View.VISIBLE);
                    footer_fifth_button_layout.setVisibility(View.VISIBLE);
                    footerButton1.setText(approvalStatusArrayList.get(0).get("displayName"));
                    footerButton1Value = approvalStatusArrayList.get(0).get("Value");
                    footerButton2.setText(approvalStatusArrayList.get(1).get("displayName"));
                    footerButton2Value = approvalStatusArrayList.get(1).get("Value");
                    footerButton3.setText(approvalStatusArrayList.get(2).get("displayName"));
                    footerButton3Value = approvalStatusArrayList.get(2).get("Value");
                    footerButton4.setText(approvalStatusArrayList.get(3).get("displayName"));
                    footerButton4Value = approvalStatusArrayList.get(3).get("Value");
                    footerButton5.setText(approvalStatusArrayList.get(4).get("displayName"));
                    footerButton5Value = approvalStatusArrayList.get(4).get("Value");
                }
                for (int i = 0; i < approveButtonNumbers; i++) {
                    approvalStatusArrayList.get(i).get("id");
                }
            }
            if (DPRArrayList.size() > 0 && approvalRights) {
                emptyMsgTextView.setVisibility(View.GONE);
                dprListView.setVisibility(View.VISIBLE);
                dprListView.onFinishLoading(true, DPRArrayList, totalPage);
            } else {
                emptyMsgTextView.setVisibility(View.VISIBLE);
                dprListView.setVisibility(View.GONE);
            }
        }
    }
    public class childListLoad extends AsyncTask<Integer, String, String> {
        @Override
        protected void onPreExecute() {
            loadProgressBar();
        }
        @Override
        protected String doInBackground(Integer... params) {
            int mbookId = params[0];
            JSONObject childResObj = new JSONObject();
            JSONArray childResArray = new JSONArray();
            JSONArray mrJsonArray = new JSONArray();
            JSONArray primaryJsonArray = new JSONArray();
            JSONArray miscJsonArray = new JSONArray();
            JSONArray nmrJsonArray = new JSONArray();
            JSONArray subActivityJsonArray = new JSONArray();
            JSONObject mrObj = new JSONObject();
            JSONObject primaryObj = new JSONObject();
            JSONObject miscObj = new JSONObject();
            JSONObject nmrObj = new JSONObject();
            JSONObject subActivityObj = new JSONObject();
            HashMap<String, String> itemValueList = new HashMap<>();
            MaterialsMasterDao materialsMasterDao = daoSession.getMaterialsMasterDao();
            List<MaterialsMaster> materialList = materialsMasterDao.queryBuilder().list();
            for (int i = 0; i < materialList.size(); i++) {
                itemValueList.put(materialList.get(i).getItem_id(), materialList.get(i).getItem_name());
            }
            HashMap<String, String> comnValueList = new HashMap<>();
            CommMasterDetailsDao commMasterDetailsDao = daoSession.getCommMasterDetailsDao();
            List<CommMasterDetails> cmnMasterList = commMasterDetailsDao.queryBuilder().list();
            for (int i = 0; i < cmnMasterList.size(); i++) {
                comnValueList.put(cmnMasterList.get(i).getMaster_id(), cmnMasterList.get(i).getDisp_value());
            }
            HashMap<String, String> formulaValueList = new HashMap<>();
            ProjFormulaMasterDao projFormulaMasterDao = daoSession.getProjFormulaMasterDao();
            List<ProjFormulaMaster> formulaMasters = projFormulaMasterDao.queryBuilder().list();
            for (int i = 0; i < formulaMasters.size(); i++) {
                formulaValueList.put(formulaMasters.get(i).getFormula_id(), formulaMasters.get(i).getFormula_name() + "(" + formulaMasters.get(i).getFormula() + ")");
            }
            try {
                mrObj.put("Header", "MR");
                primaryObj.put("Header", "Primary");
                miscObj.put("Header", "Misc");
                nmrObj.put("Header", "NMR");
                subActivityObj.put("Header", "SubActivity");
                //MR
                String childMRQry = "SELECT a.mbook_child_id , a.mbook_id , a.mbook_type , a.stage_id , a.iow_id , a.iow_uom , a.formula_id , a.length , a.breadth , a.depth , a.volume , a.iow_nos , a.gross_qty , a.deduct_qty , a.net_qty , a.basic_iow_qty , a.remarks ,b.iow_name , b.iow_code " +
                        "FROM " + projMbookiowChildDao.getTablename() + " AS a " +
                        "LEFT JOIN " + projIowMasterDao.getTablename() + " AS b ON a.iow_id=b.iow_id " +
                        "WHERE a.mbook_id='" + mbookId + "'  AND a.mbook_type='mr'  ORDER BY a.stage_id,b.iow_name";
                Database db = ((SCMApplication) context.getApplicationContext()).getreadableDb();
                Cursor c = db.rawQuery(childMRQry, null);
                int inc = 0;
                c.moveToFirst();
                while (c.isAfterLast() == false) {
                    try {
                        StageValue = "";
                        inc++;
                        JSONObject object = new JSONObject();
                        object.put("deducyQty", FormValidation.qtyFormat("0.0000", c.getString(13)));
                        object.put("nos", FormValidation.integerCheck(c.getString(11)));
                        object.put("breadth", FormValidation.qtyFormat("0.0000", c.getString(8)));
                        object.put("stageName", selectedStageName(c.getString(3)));
                        if (comnValueList.containsKey(c.getString(5)))
                            object.put("uom", comnValueList.get(c.getString(5)));
                        else
                            object.put("uom", "");
                        object.put("grossQty", FormValidation.qtyFormat("0.0000", c.getString(12)));
                        object.put("length", FormValidation.qtyFormat("0.0000", c.getString(7)));
                        object.put("iow", c.getString(17) + "(" + c.getString(18) + ")");
                        object.put("netQty", FormValidation.qtyFormat("0.0000", c.getString(14)));
                        object.put("depth", FormValidation.qtyFormat("0.0000", c.getString(9)));
                        if (formulaValueList.containsKey(c.getString(6)))
                            object.put("formula", formulaValueList.get(c.getString(6)));
                        else
                            object.put("formula", "");
                        object.put("sNo", inc);
                        mrJsonArray.put(object);
                        c.moveToNext();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                c.close();
                mrObj.put("values", mrJsonArray);
                //Primary
                String childPrimaryQry = "SELECT a.mbook_child_id , a.mbook_id , a.mbook_type , a.stage_id , a.iow_id , a.iow_uom , a.formula_id , a.length , a.breadth , a.depth , a.volume , a.iow_nos , a.gross_qty , a.deduct_qty , a.net_qty , a.basic_iow_qty , a.remarks ,b.iow_name , b.iow_code " +
                        "FROM " + projMbookiowChildDao.getTablename() + " AS a " +
                        "LEFT JOIN " + projIowMasterDao.getTablename() + " AS b ON a.iow_id=b.iow_id " +
                        "WHERE a.mbook_id='" + mbookId + "'  AND a.mbook_type='others'  ORDER BY a.stage_id,b.iow_name";
                Database db1 = ((SCMApplication) context.getApplicationContext()).getreadableDb();
                 Cursor c1 = db1.rawQuery(childPrimaryQry, null);
                int inc1 = 0;
                c1.moveToFirst();
                while (c1.isAfterLast() == false) {
                    try {
                        StageValue = "";
                        inc1++;
                        JSONObject object = new JSONObject();
                        object.put("deducyQty", FormValidation.qtyFormat("0.0000", c1.getString(13)));
                        object.put("nos", FormValidation.integerCheck(c1.getString(11)));
                        object.put("breadth", FormValidation.qtyFormat("0.0000", c1.getString(8)));
                        object.put("stageName", selectedStageName(c1.getString(3)));
                        if (comnValueList.containsKey(c1.getString(5)))
                            object.put("uom", comnValueList.get(c1.getString(5)));
                        else
                            object.put("uom", "");
                        object.put("grossQty", FormValidation.qtyFormat("0.0000", c1.getString(12)));
                        object.put("length", FormValidation.qtyFormat("0.0000", c1.getString(7)));
                        object.put("iow", c1.getString(17) + "(" + c1.getString(18) + ")");
                        object.put("netQty", FormValidation.qtyFormat("0.0000", c1.getString(14)));
                        object.put("depth", FormValidation.qtyFormat("0.0000", c1.getString(9)));
                        if (formulaValueList.containsKey(c1.getString(6)))
                            object.put("formula", formulaValueList.get(c1.getString(6)));
                        else
                            object.put("formula", "");
                        object.put("sNo", inc1);
                        primaryJsonArray.put(object);
                        c1.moveToNext();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                c1.close();
                primaryObj.put("values", primaryJsonArray);
                //Misc
                String childmiscQry = "SELECT a.mbook_child_id , a.mbook_id , a.mbook_type , a.stage_id , a.iow_id , a.iow_uom , a.formula_id , a.length , a.breadth , a.depth , a.volume , a.iow_nos , a.gross_qty , a.deduct_qty , a.net_qty , a.basic_iow_qty , a.remarks , b.iow_name , b.iow_code " +
                        "FROM " + projMbookiowChildDao.getTablename() + " AS a " +
                        "LEFT JOIN " + projIowMasterDao.getTablename() + " AS b ON a.iow_id=b.iow_id " +
                        "WHERE a.mbook_id='" + mbookId + "'  AND a.mbook_type='misc'  ORDER BY a.stage_id,b.iow_name";
                Database db2 = ((SCMApplication) context.getApplicationContext()).getreadableDb();
                Cursor c2 = db2.rawQuery(childmiscQry, null);
                int inc2 = 0;
                c2.moveToFirst();
                while (c2.isAfterLast() == false) {
                    try {
                        StageValue = "";
                        inc2++;
                        JSONObject object = new JSONObject();
                        object.put("deducyQty", FormValidation.qtyFormat("0.0000", c2.getString(13)));
                        object.put("nos", FormValidation.integerCheck(c2.getString(11)));
                        object.put("breadth", FormValidation.qtyFormat("0.0000", c2.getString(8)));
                        object.put("stageName", selectedStageName(c2.getString(3)));
                        if (comnValueList.containsKey(c2.getString(5)))
                            object.put("uom", comnValueList.get(c2.getString(5)));
                        else
                            object.put("uom", "");
                        object.put("grossQty", FormValidation.qtyFormat("0.0000", c2.getString(12)));
                        object.put("length", FormValidation.qtyFormat("0.0000", c2.getString(7)));
                        object.put("iow", c2.getString(17) + "(" + c2.getString(18) + ")");
                        object.put("netQty", FormValidation.qtyFormat("0.0000", c2.getString(14)));
                        object.put("depth", FormValidation.qtyFormat("0.0000", c2.getString(9)));
                        if (formulaValueList.containsKey(c2.getString(6)))
                            object.put("formula", formulaValueList.get(c2.getString(6)));
                        else
                            object.put("formula", "");
                        object.put("sNo", inc2);
                        miscJsonArray.put(object);
                        c2.moveToNext();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                c2.close();
                miscObj.put("values", miscJsonArray);
                //NMR
                String childNMRQry = "SELECT a.mbook_nmr_child_id , a.mbook_id , a.mbook_type , a.stage_id , a.iow_id , a.labour_id , a.uom , a.reg_no , a.reg_in_time , a.reg_out_time , a.reg_tot_mins , a.ot_no , a.ot_type , a.ot_value , a.ot_mins , a.ot_tot_mins , a.remarks , b.iow_name ,b.iow_code " +
                        "FROM " + projMbookIowNmrChildDao.getTablename() + " AS a " +
                        "LEFT JOIN " + projIowMasterDao.getTablename() + " AS b ON a.iow_id=b.iow_id  WHERE a.mbook_id='" + mbookId + "' ORDER BY a.stage_id,b.iow_name ";
                Database db3 = ((SCMApplication) context.getApplicationContext()).getreadableDb();
                Cursor c3 = db3.rawQuery(childNMRQry, null);
                int inc3 = 0;
                c3.moveToFirst();
                while (c3.isAfterLast() == false) {
                    try {
                        StageValue = "";
                        inc3++;
                        JSONObject object = new JSONObject();
                        object.put("total", ConvertMinstoHrs(Integer.parseInt(c3.getString(15)) + Integer.parseInt(c3.getString(10))));
                        object.put("stageName", selectedStageName(c3.getString(3)));
                        if (comnValueList.containsKey(c3.getString(6)))
                            object.put("uom", comnValueList.get(c3.getString(6)));
                        else
                            object.put("uom", "");
                        object.put("description", c3.getString(16));
                        object.put("iow", c3.getString(17) + "(" + c3.getString(18) + ")");
                        List<ProjLabourMaster> labourList = projLabourMasterDao.queryBuilder().where(ProjLabourMasterDao.Properties.Labour_id.eq(c3.getString(5))).list();
                        if (labourList.size() > 0)
                            object.put("labour", labourList.get(0).getLabour_name());
                        else
                            object.put("labour", "");
                        object.put("sNo", inc3);
                        JSONObject otObj = new JSONObject();
                        JSONObject regularObj = new JSONObject();
                        //OT Value
                        otObj.put("nos", c3.getString(11));
                        otObj.put("total(HH:MM)", ConvertMinstoHrs(Integer.parseInt(c3.getString(15))));
                        if (c3.getString(12).equalsIgnoreCase("hours"))
                            otObj.put("value", ConvertMinstoHrs((int) Double.parseDouble(c3.getString(13))));
                        else
                            otObj.put("value", c3.getString(13));
                        otObj.put("type", c3.getString(12).toUpperCase());
                        object.put("ot", otObj);
                        //Regular Value
                        regularObj.put("nos", c3.getString(7));
                        regularObj.put("total(HH:MM)", ConvertMinstoHrs(Integer.parseInt(c3.getString(10))));
                        regularObj.put("out(HH:MM)", c3.getString(9));
                        regularObj.put("in(HH:MM)", c3.getString(8));
                        object.put("regular", regularObj);
                        nmrJsonArray.put(object);
                        c3.moveToNext();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                c3.close();
                nmrObj.put("values", nmrJsonArray);
                //Sub Activity
                String subActivityQry = "SELECT a.sub_activity_child_id , a.mbook_id , a.sub_activity_id ,a.item_id , a.spec_id , a.stage_id , a.iow_id , a.uom_id , a.formula_id , a.length , a.breadth , a.depth , a.volume , a.iow_nos , a.gross_qty , a.deduct_qty , a.net_qty , a.basic_iow_qty , a.remarks ,b.iow_name , b.iow_code " +
                        "FROM " + projMbookSubActivityChildDao.getTablename() + " AS a LEFT JOIN " + projIowMasterDao.getTablename() + " AS b ON a.iow_id=b.iow_id  WHERE a.mbook_id='" + mbookId + "' ORDER BY a.stage_id,b.iow_name";
                Database db4 = ((SCMApplication) context.getApplicationContext()).getreadableDb();
                Cursor c4 = db4.rawQuery(subActivityQry, null);
                int inc4 = 0;
                c4.moveToFirst();
                while (c4.isAfterLast() == false) {
                    try {
                        StageValue = "";
                        inc4++;
                        JSONObject object = new JSONObject();
                        object.put("sNo", inc4);
                        //object.put("itemName", c4.getString(3));
                        if (itemValueList.containsKey(c4.getString(3)))
                            object.put("itemName", itemValueList.get(c4.getString(3)));
                        else
                            object.put("itemName", "");
                        object.put("breadth", FormValidation.qtyFormat("0.0000", c4.getString(10)));
                        object.put("stageName", selectedStageName(c4.getString(5)));
                        // object.put("spce", c4.getString(4));
                        if (comnValueList.containsKey(c4.getString(4)))
                            object.put("spce", comnValueList.get(c4.getString(4)));
                        else
                            object.put("spce", "");
                        object.put("depth", FormValidation.qtyFormat("0.0000", c4.getString(11)));
                        object.put("nos", c4.getString(13));
                        // object.put("subActivity", c4.getString(0));
                        if (comnValueList.containsKey(c4.getString(2)))
                            object.put("subActivity", comnValueList.get(c4.getString(2)));
                        else
                            object.put("subActivity", "");
                        if (comnValueList.containsKey(c4.getString(7)))
                            object.put("uom", comnValueList.get(c4.getString(7)));
                        else
                            object.put("uom", "");
                        object.put("grossQty", FormValidation.qtyFormat("0.0000", c4.getString(14)));
                        object.put("iow", c4.getString(19) + "(" + c4.getString(20) + ")");
                        object.put("length", FormValidation.qtyFormat("0.0000", c4.getString(9)));
                        object.put("activityName", "");
                        object.put("deductedQty", FormValidation.qtyFormat("0.0000", c4.getString(15)));
                        if (formulaValueList.containsKey(c4.getString(8)))
                            object.put("formula", formulaValueList.get(c4.getString(8)));
                        else
                            object.put("formula", "");
                        object.put("netQty", FormValidation.qtyFormat("0.0000", c4.getString(16)));
                        subActivityJsonArray.put(object);
                        c4.moveToNext();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                c4.close();
                subActivityObj.put("values", subActivityJsonArray);
                //All Datas
                childResArray.put(mrObj);
                childResArray.put(primaryObj);
                childResArray.put(miscObj);
                childResArray.put(nmrObj);
                childResArray.put(subActivityObj);
                childResObj.put("values", childResArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return childResObj.toString();
        }
        @Override
        protected void onPostExecute(String response) {
            if (mDialog.isShowing())
                mDialog.dismiss();
            try {
                childView(new JSONObject(response));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public class displayLoad extends AsyncTask<Integer, String, String> {
        @Override
        protected void onPreExecute() {
            loadProgressBar();
        }
        @Override
        protected String doInBackground(Integer... params) {
            int mbookId = params[0];
            JSONObject dispResObj = new JSONObject();
            try {
                //MR
                String childMRQry = "SELECT a.mbook_child_id , a.mbook_id , a.mbook_type , a.stage_id , a.iow_id , a.iow_uom , a.formula_id , a.length , a.breadth , a.depth , a.volume , a.iow_nos , a.gross_qty , a.deduct_qty , a.net_qty , a.basic_iow_qty , a.remarks ,b.iow_name , b.iow_code " +
                        "FROM " + projMbookiowChildDao.getTablename() + " AS a " +
                        "LEFT JOIN " + projIowMasterDao.getTablename() + " AS b ON a.iow_id=b.iow_id " +
                        "WHERE a.mbook_id='" + mbookId + "'  AND a.mbook_type='mr'  ORDER BY a.stage_id,b.iow_name";
                Database db = ((SCMApplication) context.getApplicationContext()).getreadableDb();
                Cursor c = db.rawQuery(childMRQry, null);
                dispResObj.put("MRCount", c.getCount());
                c.close();
                //Primary
                String childPrimaryQry = "SELECT a.mbook_child_id , a.mbook_id , a.mbook_type , a.stage_id , a.iow_id , a.iow_uom , a.formula_id , a.length , a.breadth , a.depth , a.volume , a.iow_nos , a.gross_qty , a.deduct_qty , a.net_qty , a.basic_iow_qty , a.remarks ,b.iow_name , b.iow_code " +
                        "FROM " + projMbookiowChildDao.getTablename() + " AS a " +
                        "LEFT JOIN " + projIowMasterDao.getTablename() + " AS b ON a.iow_id=b.iow_id " +
                        "WHERE a.mbook_id='" + mbookId + "'  AND a.mbook_type='others'  ORDER BY a.stage_id,b.iow_name";
                Database db1 = ((SCMApplication) context.getApplicationContext()).getreadableDb();
                Cursor c1 = db1.rawQuery(childPrimaryQry, null);
                dispResObj.put("PrimaryCount", c1.getCount());
                c1.close();
                //Misc
                String childmiscQry = "SELECT a.mbook_child_id , a.mbook_id , a.mbook_type , a.stage_id , a.iow_id , a.iow_uom , a.formula_id , a.length , a.breadth , a.depth , a.volume , a.iow_nos , a.gross_qty , a.deduct_qty , a.net_qty , a.basic_iow_qty , a.remarks , b.iow_name , b.iow_code " +
                        "FROM " + projMbookiowChildDao.getTablename() + " AS a " +
                        "LEFT JOIN " + projIowMasterDao.getTablename() + " AS b ON a.iow_id=b.iow_id " +
                        "WHERE a.mbook_id='" + mbookId + "'  AND a.mbook_type='misc'  ORDER BY a.stage_id,b.iow_name";
                Database db2 = ((SCMApplication) context.getApplicationContext()).getreadableDb();
                Cursor c2 = db2.rawQuery(childmiscQry, null);
                dispResObj.put("MiscCount", c2.getCount());
                c2.close();
                //NMR
                String childNMRQry = "SELECT a.mbook_nmr_child_id , a.mbook_id , a.mbook_type , a.stage_id , a.iow_id , a.labour_id , a.uom , a.reg_no , a.reg_in_time , a.reg_out_time , a.reg_tot_mins , a.ot_no , a.ot_type , a.ot_value , a.ot_mins , a.ot_tot_mins , a.remarks , b.iow_name ,b.iow_code " +
                        "FROM " + projMbookIowNmrChildDao.getTablename() + " AS a " +
                        "LEFT JOIN " + projIowMasterDao.getTablename() + " AS b ON a.iow_id=b.iow_id  WHERE a.mbook_id='" + mbookId + "' ORDER BY a.stage_id,b.iow_name ";
                Database db3 = ((SCMApplication) context.getApplicationContext()).getreadableDb();
                Cursor c3 = db3.rawQuery(childNMRQry, null);
                dispResObj.put("NMRCount", c3.getCount());
                c3.close();
                //Sub Activity
                String subActivityQry = "SELECT a.sub_activity_child_id , a.mbook_id , a.sub_activity_id ,a.item_id , a.spec_id , a.stage_id , a.iow_id , a.uom_id , a.formula_id , a.length , a.breadth , a.depth , a.volume , a.iow_nos , a.gross_qty , a.deduct_qty , a.net_qty , a.basic_iow_qty , a.remarks ,b.iow_name , b.iow_code " +
                        "FROM " + projMbookSubActivityChildDao.getTablename() + " AS a LEFT JOIN " + projIowMasterDao.getTablename() + " AS b ON a.iow_id=b.iow_id  WHERE a.mbook_id='" + mbookId + "' ORDER BY a.stage_id,b.iow_name";
                Database db4 =((SCMApplication) context.getApplicationContext()).getreadableDb();
                Cursor c4 = db4.rawQuery(subActivityQry, null);
                dispResObj.put("SubActivityCount", c4.getCount());
                c4.close();
                //All Datas
                dispResObj.put("History", formHistoryTable(DPRArrayList.get(childClickPos).get("history"), false));
                dispResObj.put("GeneralDocument", false);
                JSONObject generalInfObj = new JSONObject();
                generalInfObj.put("Status", DPRArrayList.get(childClickPos).get("status"));
                generalInfObj.put("fromDate", DPRArrayList.get(childClickPos).get("fromDate"));
                generalInfObj.put("enteredBy", DPRArrayList.get(childClickPos).get("preparedBy"));
                generalInfObj.put("DPRRefNo", DPRArrayList.get(childClickPos).get("dprRefNo"));
                generalInfObj.put("description", DPRArrayList.get(childClickPos).get("description"));
                generalInfObj.put("toDate", DPRArrayList.get(childClickPos).get("toDate"));
                generalInfObj.put("contractorName", DPRArrayList.get(childClickPos).get("contractorName"));
                generalInfObj.put("projectName", DPRArrayList.get(childClickPos).get("project"));
                ProjWoMasterDao projWoMasterDao = daoSession.getProjWoMasterDao();
                List<ProjWoMaster> woList = projWoMasterDao.queryBuilder().where(ProjWoMasterDao.Properties.Wo_id.eq(DPRArrayList.get(childClickPos).get("woId"))).list();
                if (woList.size() > 0)
                    generalInfObj.put("woRef", woList.get(0).getWo_id() + "-" + woList.get(0).getWo_code());
                else
                    generalInfObj.put("woRef", "");
                List<ProjContractorMasterView> contractorMasterViewList = projContractorMasterViewDao.queryBuilder().where(ProjContractorMasterViewDao.Properties.Contractor_id.eq(DPRArrayList.get(childClickPos).get("contractorId"))).list();
                if (contractorMasterViewList.size() > 0) {
                    generalInfObj.put("panNo", contractorMasterViewList.get(0).getPAN());
                    generalInfObj.put("natureOfWork", contractorMasterViewList.get(0).getNature_business());
                } else {
                    generalInfObj.put("panNo", "");
                    generalInfObj.put("natureOfWork", "");
                }
                JSONArray blockJsonArray = new JSONArray();
                JSONObject blockObj = new JSONObject();
                blockObj.put("id", "0");
                blockObj.put("selected", "true");
                blockObj.put("value", DPRArrayList.get(childClickPos).get("blockName"));
                blockJsonArray.put(blockObj);
                generalInfObj.put("BlockName", blockJsonArray);
                dispResObj.put("generalInformation", generalInfObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dispResObj.toString();
        }
        @Override
        protected void onPostExecute(String response) {
            if (mDialog.isShowing())
                mDialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putString("response", response);
            NavigationFragmentManager(DPRGeneralDisplayFragment.newInstance(bundle), "Daily Progress Report");
        }
    }
    private void loadProgressBar() {
        Log.d(TAG, "Initiating ProgressBar for DB Sync");
        mDialog.show();
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setContentView(R.layout.loader);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView textView = (TextView) mDialog.findViewById(R.id.loader_msg_text_view);
        //  textView.setText("Updating Database,Please wait this might take a several minutes");
        textView.setText("Loading...");
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                ((Activity) context).finish();
            }
        });
    }
    public void showDialog(String msg) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_dialog);
        TextView text = (TextView) dialog.findViewById(R.id.alert_msg);
        text.setText(msg);
        Button dialogButton = (Button) dialog.findViewById(R.id.ok_btn);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                /*Intent intent = new Intent(context, DBUpdateService.class);
                startActivity(intent);*/
            }
        });
        dialog.show();
    }
   /* public void offlineLoadDisplay() {
        try {
            String curDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            List<PendingRequestList> pendingVMFLists = pendingRequestListDao.queryBuilder().where(PendingRequestListDao.Properties.User_id.eq(uid), PendingRequestListDao.Properties.Process_name.eq("DPR"), PendingRequestListDao.Properties.Status.eq("Pending"), PendingRequestListDao.Properties.Req_date.eq(curDate)).list();
            DPRArrayList = new ArrayList<>();
            int parentColorIndex = 0;
            for (int i = 0; i < pendingVMFLists.size(); i++) {
                JSONObject resRowObject = new JSONObject(pendingVMFLists.get(i).getDisplay_response());
                JSONObject generalInformObject = resRowObject.getJSONObject("generalInformation");
                HashMap<String, String> map = new HashMap<>();
                if (parentColorIndex > 11) {
                    parentColorIndex = 0;
                    map.put("color", "" + colorArray()[parentColorIndex]);
                } else {
                    map.put("color", "" + colorArray()[parentColorIndex]);
                    parentColorIndex = parentColorIndex + 1;
                }
                map.put("isfreeze", "false");
                map.put("Project Name", generalInformObject.getString("ProjectNameSelectedValue"));
                map.put("Security Ref No", pendingVMFLists.get(i).getUnique_id());
                if (generalInformObject.getString("Vendor From").equalsIgnoreCase("Vendors")) {
                    map.put("Vendor Name", generalInformObject.getJSONArray("VendorNameSelectedValue").getJSONObject(0).getString("value"));
                } else {
                    map.put("Vendor Name", "");
                }
                map.put("Vehicle No", generalInformObject.getString("VehicleNumber"));
                map.put("Status", generalInformObject.getString("Status"));
                map.put("In Time", generalInformObject.getString("In Time"));
                map.put("Out Time", generalInformObject.getString("Out Time"));
                map.put("isSelected", "false");
                map.put("request", pendingVMFLists.get(i).getSend_request());
                map.put("response", pendingVMFLists.get(i).getDisplay_response());
                map.put("isOffline", "true");
                DPRArrayList.add(map);
            }
            if (DPRArrayList.size() > 0) {
                emptyMsgTextView.setVisibility(View.GONE);
                dprListView.setVisibility(View.VISIBLE);
                dprListView.onFinishLoading(true, DPRArrayList, totalPage);
            } else {
                emptyMsgTextView.setText("No Offline Record(s)");
                emptyMsgTextView.setVisibility(View.VISIBLE);
                dprListView.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    public void listDisplay(JSONObject response) {
        try {
            JSONObject rightsApprovalObject = response.getJSONObject("Rights");
            JSONArray requestArray = rightsApprovalObject.getJSONArray("Load_Request");
            totalPage = Integer.parseInt(response.getString("pageCount"));
            for (int i = 0; i < requestArray.length(); i++) {
                JSONObject rightsObject = requestArray.getJSONObject(i);
                if (rightsObject.getString("Action").equalsIgnoreCase("SCM_DPR_ADD")) {
                    addNew = true;
                } else if (rightsObject.getString("Action").equalsIgnoreCase("SCM_DPR_UPDATE")) {
                    updateButton = true;
                } else if (rightsObject.getString("Action").equalsIgnoreCase("SCM_DPR_APPROVAL")) {
                    rightsApproval = true;
                }
            }
            JSONObject approvalRightsJSONObject = response.getJSONObject("Approval_Rights");
            try {
                approvalRights = Boolean.parseBoolean(approvalRightsJSONObject.getString("0"));
            } catch (Exception e) {
                approvalRights = Boolean.parseBoolean(approvalRightsJSONObject.getString("1"));
            }
       /* if (approvalscreen == true) {
            SCMDashBoardFragmentActivity.actionBarAllbutton.setVisibility(View.VISIBLE);
            SCMDashBoardFragmentActivity.actionBarApprovalsIconLayout.setVisibility(View.GONE);
            SCMDashBoardFragmentActivity.actionBarAllbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToNextFragment(DPROfflineListFragment.class.getName(), null);
                }
            });
        } else {
            SCMDashBoardFragmentActivity.actionBarAllbutton.setVisibility(View.GONE);
            SCMDashBoardFragmentActivity.actionBarApprovalsIconLayout.setVisibility(View.VISIBLE);
        }*/
            listType = response.getString("Header");
            System.out.println("DPR LIST TYPE" + listType);
            if (addNew)
                newRequestBtn = true;
            else
                newRequestBtn = false;
            JSONArray dprJSONArray = response.getJSONArray("TableValues");
            ArrayList<HashMap<String, String>> DPRArrayPageList = new ArrayList<>();
            DPRArrayPageList = ApiCalls.getArraylistfromJson(dprJSONArray.toString());
            notificationCount = response.getString("notificationCount");
            if (DPRArrayPageList.size() > 0) {
                int parentColorIndex = 0;
                for (int i = 0; i < DPRArrayPageList.size(); i++) {
                    DPRArrayPageList.get(i).put("isOffline", "false");
                    DPRArrayPageList.get(i).put("isSelected", "false");
                    if (parentColorIndex > 11) {
                        parentColorIndex = 0;
                        DPRArrayPageList.get(i).put("color", "" + colorArray()[parentColorIndex]);
                    } else {
                        DPRArrayPageList.get(i).put("color", "" + colorArray()[parentColorIndex]);
                        parentColorIndex = parentColorIndex + 1;
                    }
                    DPRArrayList.add(DPRArrayPageList.get(i));
                }
                //Approval Status
                approvalJsonArray = response.optJSONArray("Approval_Status");
                if (approvalJsonArray.length() > 0) {
                    ArrayList<HashMap<String, String>> approvalStatusArrayList = ApiCalls.getArraylistfromJson(approvalJsonArray.toString());
                    int approveButtonNumbers = approvalStatusArrayList.size();
                    if (approveButtonNumbers == 1) {
                        footerButton1.setVisibility(View.VISIBLE);
                        footerButton2.setVisibility(View.GONE);
                        footerButton3.setVisibility(View.GONE);
                        footerButton4.setVisibility(View.GONE);
                        footerButton5.setVisibility(View.GONE);
                        footerButton1.setText(approvalStatusArrayList.get(0).get("displayName"));
                        footerButton1Value = approvalStatusArrayList.get(0).get("Value");
                    } else if (approveButtonNumbers == 2) {
                        footerButton1.setVisibility(View.VISIBLE);
                        footerButton2.setVisibility(View.VISIBLE);
                        footerButton3.setVisibility(View.GONE);
                        footerButton4.setVisibility(View.GONE);
                        footerButton5.setVisibility(View.GONE);
                        footerButton1.setText(approvalStatusArrayList.get(0).get("displayName"));
                        footerButton1Value = approvalStatusArrayList.get(0).get("Value");
                        footerButton2.setText(approvalStatusArrayList.get(1).get("displayName"));
                        footerButton2Value = approvalStatusArrayList.get(1).get("Value");
                    } else if (approveButtonNumbers == 3) {
                        footerButton1.setVisibility(View.VISIBLE);
                        footerButton2.setVisibility(View.VISIBLE);
                        footerButton3.setVisibility(View.VISIBLE);
                        footerButton4.setVisibility(View.GONE);
                        footerButton5.setVisibility(View.GONE);
                        footerButton1.setText(approvalStatusArrayList.get(0).get("displayName"));
                        footerButton1Value = approvalStatusArrayList.get(0).get("Value");
                        footerButton2.setText(approvalStatusArrayList.get(1).get("displayName"));
                        footerButton2Value = approvalStatusArrayList.get(1).get("Value");
                        footerButton3.setText(approvalStatusArrayList.get(2).get("displayName"));
                        footerButton3Value = approvalStatusArrayList.get(2).get("Value");
                    } else if (approveButtonNumbers == 4) {
                        footerButton1.setVisibility(View.VISIBLE);
                        footerButton2.setVisibility(View.VISIBLE);
                        footerButton3.setVisibility(View.VISIBLE);
                        footerButton4.setVisibility(View.VISIBLE);
                        footerButton5.setVisibility(View.GONE);
                        footerButton1.setText(approvalStatusArrayList.get(0).get("displayName"));
                        footerButton1Value = approvalStatusArrayList.get(0).get("Value");
                        footerButton2.setText(approvalStatusArrayList.get(1).get("displayName"));
                        footerButton2Value = approvalStatusArrayList.get(1).get("Value");
                        footerButton3.setText(approvalStatusArrayList.get(2).get("displayName"));
                        footerButton3Value = approvalStatusArrayList.get(2).get("Value");
                        footerButton4.setText(approvalStatusArrayList.get(3).get("displayName"));
                        footerButton4Value = approvalStatusArrayList.get(3).get("Value");
                    } else if (approveButtonNumbers == 5) {
                        footerButton1.setVisibility(View.VISIBLE);
                        footerButton2.setVisibility(View.VISIBLE);
                        footerButton3.setVisibility(View.VISIBLE);
                        footerButton4.setVisibility(View.VISIBLE);
                        footerButton5.setVisibility(View.VISIBLE);
                        footer_fifth_button_layout.setVisibility(View.VISIBLE);
                        footerButton1.setText(approvalStatusArrayList.get(0).get("displayName"));
                        footerButton1Value = approvalStatusArrayList.get(0).get("Value");
                        footerButton2.setText(approvalStatusArrayList.get(1).get("displayName"));
                        footerButton2Value = approvalStatusArrayList.get(1).get("Value");
                        footerButton3.setText(approvalStatusArrayList.get(2).get("displayName"));
                        footerButton3Value = approvalStatusArrayList.get(2).get("Value");
                        footerButton4.setText(approvalStatusArrayList.get(3).get("displayName"));
                        footerButton4Value = approvalStatusArrayList.get(3).get("Value");
                        footerButton5.setText(approvalStatusArrayList.get(4).get("displayName"));
                        footerButton5Value = approvalStatusArrayList.get(4).get("Value");
                    }
                    for (int i = 0; i < approveButtonNumbers; i++) {
                        approvalStatusArrayList.get(i).get("id");
                    }
                }
            }
            if (DPRArrayPageList.size() > 0) {
                emptyMsgTextView.setVisibility(View.GONE);
                dprListView.setVisibility(View.VISIBLE);
                dprListView.onFinishLoading(true, DPRArrayList, totalPage);
            } else {
                emptyMsgTextView.setVisibility(View.VISIBLE);
                dprListView.setVisibility(View.GONE);
            }
            setHasOptionsMenu(true);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void childView(JSONObject response) {
        try {
            childDetails.put(childClickPos, response.toString());
            int flipCount = 1;
            JSONArray childListArray = response.getJSONArray("values");
            if (childListArray.length() > 0) {
                LinearLayout mrLay = (LinearLayout) childFoldingCell.findViewById(R.id.mr_lay);
                LinearLayout primaryLay = (LinearLayout) childFoldingCell.findViewById(R.id.primary_lay);
                LinearLayout miscLay = (LinearLayout) childFoldingCell.findViewById(R.id.misc_lay);
                LinearLayout nmrLay = (LinearLayout) childFoldingCell.findViewById(R.id.nmr_lay);
                LinearLayout subActivityLay = (LinearLayout) childFoldingCell.findViewById(R.id.sub_activity_lay);
                LinearLayout mrPanel = (LinearLayout) childFoldingCell.findViewById(R.id.mr_panel);
                LinearLayout primaryPanel = (LinearLayout) childFoldingCell.findViewById(R.id.primary_panel);
                LinearLayout miscPanel = (LinearLayout) childFoldingCell.findViewById(R.id.misc_panel);
                LinearLayout nmrPanel = (LinearLayout) childFoldingCell.findViewById(R.id.nmr_panel);
                LinearLayout subActivityPanel = (LinearLayout) childFoldingCell.findViewById(R.id.sub_activity_panel);
                //MR
                JSONArray mrChildArray = childListArray.getJSONObject(0).getJSONArray("values");
                if (mrChildArray.length() > 0) {
                    flipCount = flipCount + 2;
                    mrLay.setVisibility(View.VISIBLE);
                } else mrLay.setVisibility(View.GONE);
                //Primary
                JSONArray primaryChildArray = childListArray.getJSONObject(1).getJSONArray("values");
                if (primaryChildArray.length() > 0) {
                    flipCount = flipCount + 2;
                    primaryLay.setVisibility(View.VISIBLE);
                } else primaryLay.setVisibility(View.GONE);
                //Misc
                JSONArray miscChildArray = childListArray.getJSONObject(2).getJSONArray("values");
                if (miscChildArray.length() > 0) {
                    flipCount = flipCount + 2;
                    miscLay.setVisibility(View.VISIBLE);
                } else miscLay.setVisibility(View.GONE);
                //NMR
                JSONArray nmrChildArray = childListArray.getJSONObject(3).getJSONArray("values");
                if (nmrChildArray.length() > 0) {
                    flipCount = flipCount + 2;
                    nmrLay.setVisibility(View.VISIBLE);
                } else nmrLay.setVisibility(View.GONE);
                //SubActivity
                JSONArray subActivityChildArray = childListArray.getJSONObject(4).getJSONArray("values");
                if (subActivityChildArray.length() > 0) {
                    flipCount = flipCount + 2;
                    subActivityLay.setVisibility(View.VISIBLE);
                } else subActivityLay.setVisibility(View.GONE);
                // childFoldingCell.initialize(30, 1000, R.color.bgBackSideColor, flipCount);
                childFoldingCell.toggle(true);
                adapter.registerToggle(childClickPos);
                //MR
                if (mrChildArray.length() > 0) {
                    mrLay.setVisibility(View.VISIBLE);
                    mrPanel.removeAllViews();
                    ScrollablePanel panel = mrListPanel(mrChildArray);
                    mrPanel.addView(panel);
                } else {
                    mrLay.setVisibility(View.GONE);
                }
                //Primary
                if (primaryChildArray.length() > 0) {
                    primaryLay.setVisibility(View.VISIBLE);
                    primaryPanel.removeAllViews();
                    ScrollablePanel pane2 = mrListPanel(primaryChildArray);
                    primaryPanel.addView(pane2);
                } else {
                    primaryLay.setVisibility(View.GONE);
                }
                //Misc
                if (miscChildArray.length() > 0) {
                    miscLay.setVisibility(View.VISIBLE);
                    miscPanel.removeAllViews();
                    ScrollablePanel pane3 = mrListPanel(miscChildArray);
                    miscPanel.addView(pane3);
                } else {
                    miscLay.setVisibility(View.GONE);
                }
                //NMR
                if (nmrChildArray.length() > 0) {
                    nmrLay.setVisibility(View.VISIBLE);
                    nmrPanel.removeAllViews();
                    ScrollablePanel pane4 = nmrListPanel(nmrChildArray);
                    nmrPanel.addView(pane4);
                } else {
                    nmrLay.setVisibility(View.GONE);
                }
                //SubActivity
                if (subActivityChildArray.length() > 0) {
                    subActivityLay.setVisibility(View.VISIBLE);
                    subActivityPanel.removeAllViews();
                    ScrollablePanel pane5 = subActivityListPanel(subActivityChildArray);
                    subActivityPanel.addView(pane5);
                } else {
                    subActivityLay.setVisibility(View.GONE);
                }
            } else {
                setToast("No Child List");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String selectedmrId(ArrayList<String> mrIdList) {
        String ids = "";
        for (int i = 0; i < mrIdList.size(); i++) {
            if (ids.equalsIgnoreCase("")) {
                ids = mrIdList.get(i);
            } else {
                ids = ids + "," + mrIdList.get(i);
            }
        }
        return ids;
    }
    private void initializeFooter() {
        footerButton1 = (TextView) footerLayout.findViewById(R.id.footerButton1);
        footerButton2 = (TextView) footerLayout.findViewById(R.id.footerButton2);
        footerButton3 = (TextView) footerLayout.findViewById(R.id.footerButton3);
        footerButton4 = (TextView) footerLayout.findViewById(R.id.footerButton4);
        footerButton5 = (TextView) footerLayout.findViewById(R.id.footerButton5);
        footer_fifth_button_layout = (LinearLayout) footerLayout.findViewById(R.id.fifth_button_layout);
        footerButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approvalStatusValue = footerButton1Value;
                dprSubmit(approvalStatusValue);
            }
        });
        footerButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approvalStatusValue = footerButton2Value;
                dprSubmit(approvalStatusValue);
            }
        });
        footerButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approvalStatusValue = footerButton3Value;
                dprSubmit(approvalStatusValue);
            }
        });
        footerButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approvalStatusValue = footerButton4Value;
                dprSubmit(approvalStatusValue);
            }
        });
        footerButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approvalStatusValue = footerButton5Value;
                dprSubmit(approvalStatusValue);
            }
        });
        footerLayout.setVisibility(View.GONE);
    }
    public void dprSubmit(final String Status) {
        final LayoutInflater inflater2 = LayoutInflater.from(context);
        View convertView = inflater2.inflate(R.layout.mr_submit_dialog_fragment_layout, null);
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(convertView);
        Button submit = (Button) convertView.findViewById(R.id.mr_submit_dialog_submit_button);
        final SCMMandatoryEditText descriptionEditText = (SCMMandatoryEditText) convertView.findViewById(R.id.mr_submit_dialog_description_edit_text);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PendingApprovalDao pendingApprovalDao = daoSession.getPendingApprovalDao();
                for (int i = 0; i < selectedMRItemsArrayList.size(); i++) {
                    int pos = Integer.parseInt(selectedMRItemsArrayList.get(i));
                    String reqID = "1";
                    String offLineId = "";
                    try {
                        List<PendingApproval> pendingLists = pendingApprovalDao.queryBuilder().where(PendingApprovalDao.Properties.User_id.eq(uid), PendingApprovalDao.Properties.Process_name.eq("DPR_APPROVAL")).list();
                        if (pendingLists.size() > 0)
                            reqID = "" + (Integer.parseInt(pendingLists.get(pendingLists.size() - 1).getRequest_id()) + 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date todayDate = new Date();
                    String curDate = currentDate.format(todayDate);
                    String curDateValue = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    String requestId = reqID + "-DPR-" + uid + "-" + curDate;
                    offLineId = uid + "-" + curDate + "-DPR-" + reqID;
                    String Ids = DPRArrayList.get(pos).get("mbookID");
                    String projectName = DPRArrayList.get(pos).get("project");
                    String processLink = DPRArrayList.get(pos).get("dprRefNo");
                    String processDate = DPRArrayList.get(pos).get("toDate");
                    try {
                        JSONObject reqObj = new JSONObject();
                        reqObj.put("req_comments", descriptionEditText.getText().toString());
                        reqObj.put("req_process_status", Status);
                        reqObj.put("MBookID", Ids);
                        pendingApprovalDao.insertOrReplace(new PendingApproval(requestId, uid, reqID, "DPR_APPROVAL", curDateValue, reqObj.toString(), curDate, projectName, offLineId, "Pending", "Update Pending", "1", descriptionEditText.getText().toString(), Ids, Status, processLink, processDate));
                        Log.d(TAG, "PendingApprovalDao Updated");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                setToast(Status + " Successfully...");
                dialog.dismiss();
                hideSoftInputFromWindow();
                Bundle bundle = new Bundle();
                String req = "{'Action':'DAILY_PROGRESS_REPORT','submode':'DPR_APPROVAL_SEARCH','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','Mat_Req_search':'" + 1 + "','Mat_Req_MBook_ProjName':'','Mat_Req_Mbook_Id':'','Mat_Req_MBook_RefNo':'','Mat_Req_MBook_Contractor':'','Mat_Req_MBook_FromDate':'','Mat_Req_MBook_ToDate':'','Mat_Req_MBook_status':'','Mat_Req_Mat_Req_MIR_StageName':'','Mat_Req_Mat_Req_MIR_IOW':'','Mat_Req_wo_Id':'','Mat_Req_BlockId':'','FrmNameCtl':'Mat_Req','RS':' 10 '}";
                bundle.putString("load", req);
                bundle.putString("IsApproval", "true");
                NavigationFragmentManager(DPROfflineListFragment.newInstance(bundle), "DPR APPROVAL");
            }
        });
        dialog.show();
    }
    private void hideSoftInputFromWindow() {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getWindow().getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            getActivity().getCurrentFocus().clearFocus();
        }
    }
    public static class PendingDialogFragment extends DialogFragment {
        @Bind(R.id.pending)
        SCMTextView values;
        @Bind(R.id.close_dialog_button)
        ImageButton close;
        @Bind(R.id.title)
        SCMTextView title;
        String pendingWithValue;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.mr_openwith_dialog, container);
            //   View view = inflater.inflate(R.layout.dialog, container);
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            ButterKnife.bind(this, view);
            final Bundle bundle = this.getArguments();
            if (bundle != null) {
                if (bundle.getString("pendingWith") != null) {
                    pendingWithValue = bundle.getString("pendingWith");
                    values.setText(pendingWithValue);
                    title.setText(bundle.getString("title"));
                }
            }
            return view;
        }
        @OnClick(R.id.close_dialog_button)
        public void onClose() {
            Log.d(TAG, "onClose");
            dismiss();
        }
    }
    public class DPRListAdapter extends PagingBaseAdapter {
        private static final String TAG = "DPRListAdapter";
        Context context;
        public DPRListAdapter(Context context) {
            this.context = context;
        }
        @Override
        public int getCount() {
            return DPRArrayList.size();
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
            final TitleViewHolder holder;
            FoldingCell row = (FoldingCell) convertView;
            if (row == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = (FoldingCell) mInflater.inflate(R.layout.dpr_list_row, parent, false);
                holder = new TitleViewHolder(row);
                row.setTag(holder);
            } else {
               /* if (unfoldedIndexes.contains(i)) {
                    row.unfold(true);
                } else {*/
                row.fold(true);
                // }
                holder = (TitleViewHolder) row.getTag();
            }
            holder.listSNoBgLayout.setBackground(getResources().getDrawable(Integer.parseInt(DPRArrayList.get(i).get("color"))));
            String sNo = String.valueOf(i + 1);
            holder.listSNoTextView.setText(sNo);
            if (DPRArrayList.get(i).get("isSelected").equalsIgnoreCase("true"))
                holder.ParentLayout.setBackgroundColor(Color.parseColor("#fef2d2"));
            else
                holder.ParentLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            //TODO: change the Key Values
            holder.projectNameTitleTextView.setText(DPRArrayList.get(i).get("project"));
            holder.dprCodeValue.setText(DPRArrayList.get(i).get("dprRefNo"));
            holder.childDprId.setText(DPRArrayList.get(i).get("dprRefNo"));
            holder.vendorValue.setText(DPRArrayList.get(i).get("contractorName"));
            // if (DPRArrayList.get(i).get("block").equalsIgnoreCase(""))
            //     holder.vehicleno.setVisibility(View.GONE);
            // else
            holder.vehicleno.setVisibility(View.VISIBLE);
            holder.vehicleno.setText(DPRArrayList.get(i).get("block"));
            holder.preparedBy.setText(DPRArrayList.get(i).get("preparedBy"));
            holder.dprCodeValue.setTextColor(Color.parseColor("#0B0080"));
            holder.dprCodeValue.setPaintFlags(holder.dprCodeValue.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.statusContent.setText(DPRArrayList.get(i).get("status"));
            if (DPRArrayList.get(i).get("isFreez").equalsIgnoreCase("true")) {
                holder.isfreeze.setVisibility(View.VISIBLE);
            } else {
                holder.isfreeze.setVisibility(View.GONE);
            }
            if (DPRArrayList.get(i).get("status").contains("Approved"))
                holder.statusContent.setBackgroundColor(Color.parseColor("#17C967"));
            else if (DPRArrayList.get(i).get("status").equalsIgnoreCase("Forward"))
                holder.statusContent.setBackgroundColor(Color.parseColor("#2E2EFE"));
            else if (DPRArrayList.get(i).get("status").equalsIgnoreCase("Pending"))
                holder.statusContent.setBackgroundColor(Color.parseColor("#FE2E2E"));
            else
                holder.statusContent.setBackgroundColor(Color.parseColor("#FE2E2E"));
            holder.fromDate.setText(DPRArrayList.get(i).get("fromDate"));
            holder.toDate.setText(DPRArrayList.get(i).get("toDate"));
            holder.deleteBtn.setVisibility(View.GONE);
            holder.editBtn.setVisibility(View.GONE);
            if (DPRArrayList.get(i).get("childTable").equalsIgnoreCase("true")) {
                holder.arrow.setVisibility(View.VISIBLE);
            } else {
                holder.arrow.setVisibility(View.GONE);
            }
            holder.dprCodeValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    childClickPos = i;
                    new displayLoad().execute(Integer.parseInt(DPRArrayList.get(i).get("mbookID")));
                }
            });
            holder.statusContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    if (DPRArrayList.get(i).get("status").equalsIgnoreCase("approved")) {
                        bundle.putString("title", "Approved By");
                        bundle.putString("pendingWith", DPRArrayList.get(i).get("approvedBy"));
                    } else {
                        bundle.putString("title", "Pending With");
                        bundle.putString("pendingWith", DPRArrayList.get(i).get("pendingWith"));
                    }
                    showAsDialog(PendingDialogFragment.class, bundle, context);
                }
            });
            holder.listSNoBgLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //is chkIos checked?
                    if (DPRArrayList.get(i).get("childTable").equalsIgnoreCase("true"))
                        if (listType.equalsIgnoreCase("DPR_APPROVAL_SEARCH") && approvalRights && rightsApproval) {
                            if (DPRArrayList.get(i).get("isFreez").equalsIgnoreCase("false")) {
                                if (!(selectedMRItemsArrayList.contains("" + i))) {
                                    DPRArrayList.get(i).put("isSelected", "true");
                                    holder.ParentLayout.setBackgroundColor(Color.parseColor("#fef2d2"));
                                    selectedMRItemsArrayList.add("" + i);
                                } else {
                                    selectedMRItemsArrayList.remove("" + i);
                                    holder.ParentLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                                    DPRArrayList.get(i).put("isSelected", "false");
                                }
                                if (selectedMRItemsArrayList.size() > 0)
                                    footerLayout.setVisibility(View.VISIBLE);
                                else
                                    footerLayout.setVisibility(View.GONE);
                            }
                        }
                }
            });
            final FoldingCell finalRow1 = row;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (DPRArrayList.get(i).get("childTable").equalsIgnoreCase("true")) {
                        childFoldingCell = finalRow1;
                        childClickPos = i;
                        if (childDetails.containsKey(i)) {
                            try {
                                JSONObject object = new JSONObject(childDetails.get(i));
                                childView(object);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            new childListLoad().execute(Integer.parseInt(DPRArrayList.get(i).get("mbookID")));
                        }
                    }
                }
            });
            holder.childCloseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalRow1.toggle(true);
                    registerToggle(i);
                }
            });
            return row;
        }
        // simple methods for register cell state changes
        public void registerToggle(int position) {
            if (unfoldedIndexes.contains(position))
                registerFold(position);
            else
                registerUnfold(position);
        }
        public void registerFold(int position) {
            unfoldedIndexes.remove(position);
        }
        public void registerUnfold(int position) {
            unfoldedIndexes.add(position);
        }
    }
    class TitleViewHolder {
        //Title Components
        @Bind(R.id.ParentLayout)
        LinearLayout ParentLayout;
        @Bind(R.id.vmf_list_sno_bg_layout)
        LinearLayout listSNoBgLayout;
        @Bind(R.id.vmf_list_sno)
        SCMTextView listSNoTextView;
        @Bind(R.id.vmf_projectName)
        SCMTextView projectNameTitleTextView;
        @Bind(R.id.status)
        SCMTextView statusContent;
        @Bind(R.id.arrow)
        ImageView arrow;
        @Bind(R.id.edit)
        ImageView editBtn;
        @Bind(R.id.delete)
        ImageView deleteBtn;
        @Bind(R.id.isfreeze)
        LinearLayout isfreeze;
        @Bind(R.id.first_row_value)
        SCMTextView dprCodeValue;
        @Bind(R.id.second_row_value)
        SCMTextView vendorValue;
        @Bind(R.id.vehicle_no)
        SCMTextView vehicleno;
        @Bind(R.id.prepared_by)
        SCMTextView preparedBy;
        @Bind(R.id.from_date)
        TextView fromDate;
        @Bind(R.id.to_date)
        TextView toDate;
        @Bind(R.id.child_dpr_id)
        TextView childDprId;
        @Bind(R.id.child_close)
        ImageView childCloseBtn;
        public TitleViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    public ScrollablePanel mrListPanel(JSONArray tableArray) {
        ScrollablePanelAdapter scrollablePanelAdapter = new ScrollablePanelAdapter();
        ScrollablePanel scrollablePanel = new ScrollablePanel(context, scrollablePanelAdapter);
        generateMRData(scrollablePanelAdapter, tableArray);
        scrollablePanel.setPanelAdapter(scrollablePanelAdapter);
        return scrollablePanel;
    }
    public ScrollablePanel nmrListPanel(JSONArray tableArray) {
        ScrollablePanelAdapter scrollablePanelAdapter = new ScrollablePanelAdapter();
        ScrollablePanel scrollablePanel = new ScrollablePanel(context, scrollablePanelAdapter);
        generateNMRData(scrollablePanelAdapter, tableArray);
        scrollablePanel.setPanelAdapter(scrollablePanelAdapter);
        return scrollablePanel;
    }
    public ScrollablePanel subActivityListPanel(JSONArray tableArray) {
        ScrollablePanelAdapter scrollablePanelAdapter = new ScrollablePanelAdapter();
        ScrollablePanel scrollablePanel = new ScrollablePanel(context, scrollablePanelAdapter);
        generateSubActivityData(scrollablePanelAdapter, tableArray);
        scrollablePanel.setPanelAdapter(scrollablePanelAdapter);
        return scrollablePanel;
    }
    private void generateMRData(ScrollablePanelAdapter scrollablePanelAdapter, JSONArray tableArray) {
        List<RoomInfo> roomInfoList = new ArrayList<>();
        List<DateInfo> dateInfoList = new ArrayList<>();
        List<List<rowInfo>> rowInfoList = new ArrayList<>();
        try {
            RoomInfo roomInfo = new RoomInfo();
            roomInfo.setRoomName("Stage Name");
            roomInfoList.add(roomInfo);
            RoomInfo roomInfo1 = new RoomInfo();
            roomInfo1.setRoomName("IOW");
            roomInfoList.add(roomInfo1);
            RoomInfo roomInfo2 = new RoomInfo();
            roomInfo2.setRoomName("UOM");
            roomInfoList.add(roomInfo2);
            RoomInfo roomInfo3 = new RoomInfo();
            roomInfo3.setRoomName("Formula");
            roomInfoList.add(roomInfo3);
            RoomInfo roomInfo4 = new RoomInfo();
            roomInfo4.setRoomName("Length");
            roomInfoList.add(roomInfo4);
            RoomInfo roomInfo5 = new RoomInfo();
            roomInfo5.setRoomName("Breadth");
            roomInfoList.add(roomInfo5);
            RoomInfo roomInfo6 = new RoomInfo();
            roomInfo6.setRoomName("Depth");
            roomInfoList.add(roomInfo6);
            RoomInfo roomInfo7 = new RoomInfo();
            roomInfo7.setRoomName("Nos");
            roomInfoList.add(roomInfo7);
            RoomInfo roomInfo8 = new RoomInfo();
            roomInfo8.setRoomName("Gross Qty");
            roomInfoList.add(roomInfo8);
            RoomInfo roomInfo9 = new RoomInfo();
            roomInfo9.setRoomName("Deduct Qty");
            roomInfoList.add(roomInfo9);
            RoomInfo roomInfo10 = new RoomInfo();
            roomInfo10.setRoomName("Net Qty");
            roomInfoList.add(roomInfo10);
            for (int i = 0; i < tableArray.length(); i++) {
                JSONObject rowObject = tableArray.getJSONObject(i);
                DateInfo dateInfo = new DateInfo();
                dateInfo.setMonth(i + 1);
                dateInfo.setCheck(false);
                dateInfo.setWeek("");
                dateInfoList.add(dateInfo);
                List<rowInfo> rowInfoListRow = new ArrayList<>();
                rowInfo rowinfo = new rowInfo();
                rowinfo.setComment(rowObject.getString("stageName"));
                rowinfo.setSlotId("");
                rowinfo.setIsHoliday(false);
                rowinfo.setIsQty(false);
                rowInfoListRow.add(rowinfo);
                rowInfo rowinfo1 = new rowInfo();
                rowinfo1.setComment(rowObject.getString("iow"));
                rowinfo1.setIsHoliday(false);
                rowinfo1.setIsQty(false);
                rowInfoListRow.add(rowinfo1);
                rowInfo rowinfo2 = new rowInfo();
                rowinfo2.setComment(rowObject.getString("uom"));
                rowinfo2.setIsHoliday(false);
                rowinfo2.setIsQty(false);
                rowInfoListRow.add(rowinfo2);
                rowInfo rowinfo3 = new rowInfo();
                rowinfo3.setComment(rowObject.getString("formula"));
                rowinfo3.setIsHoliday(false);
                rowinfo3.setIsQty(false);
                rowInfoListRow.add(rowinfo3);
                rowInfo rowinfo4 = new rowInfo();
                rowinfo4.setComment(rowObject.getString("length"));
                rowinfo4.setIsHoliday(false);
                rowinfo4.setIsQty(false);
                rowInfoListRow.add(rowinfo4);
                rowInfo rowinfo5 = new rowInfo();
                rowinfo5.setComment(rowObject.getString("breadth"));
                rowinfo5.setIsHoliday(false);
                rowinfo5.setIsQty(false);
                rowInfoListRow.add(rowinfo5);
                rowInfo rowinfo6 = new rowInfo();
                rowinfo6.setComment(rowObject.getString("depth"));
                rowinfo6.setIsHoliday(false);
                rowinfo6.setIsQty(false);
                rowInfoListRow.add(rowinfo6);
                rowInfo rowinfo7 = new rowInfo();
                rowinfo7.setComment(rowObject.getString("nos"));
                rowinfo7.setIsHoliday(false);
                rowinfo7.setIsQty(false);
                rowInfoListRow.add(rowinfo7);
                rowInfo rowinfo8 = new rowInfo();
                rowinfo8.setComment(rowObject.getString("grossQty"));
                rowinfo8.setIsHoliday(false);
                rowinfo8.setIsQty(true);
                rowInfoListRow.add(rowinfo8);
                rowInfo rowinfo9 = new rowInfo();
                rowinfo9.setComment(rowObject.getString("deducyQty"));
                rowinfo9.setIsHoliday(false);
                rowinfo9.setIsQty(false);
                rowInfoListRow.add(rowinfo9);
                rowInfo rowinfo10 = new rowInfo();
                rowinfo10.setComment(rowObject.getString("netQty"));
                rowinfo10.setIsHoliday(false);
                rowinfo10.setIsQty(true);
                rowInfoListRow.add(rowinfo10);
                rowInfoList.add(rowInfoListRow);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        scrollablePanelAdapter.setDateInfoList(dateInfoList);
        scrollablePanelAdapter.setRoomInfoList(roomInfoList);
        scrollablePanelAdapter.setrowList(rowInfoList);
        scrollablePanelAdapter.setTitle("S.No");
    }
    private void generateNMRData(ScrollablePanelAdapter scrollablePanelAdapter, JSONArray tableArray) {
        List<RoomInfo> roomInfoList = new ArrayList<>();
        List<DateInfo> dateInfoList = new ArrayList<>();
        List<List<rowInfo>> rowInfoList = new ArrayList<>();
        try {
            RoomInfo roomInfo = new RoomInfo();
            roomInfo.setRoomName("Stage Name");
            roomInfoList.add(roomInfo);
            RoomInfo roomInfo1 = new RoomInfo();
            roomInfo1.setRoomName("IOW");
            roomInfoList.add(roomInfo1);
            RoomInfo roomInfo2 = new RoomInfo();
            roomInfo2.setRoomName("Labour");
            roomInfoList.add(roomInfo2);
            RoomInfo roomInfo3 = new RoomInfo();
            roomInfo3.setRoomName("Description");
            roomInfoList.add(roomInfo3);
            RoomInfo roomInfo4 = new RoomInfo();
            roomInfo4.setRoomName("UOM");
            roomInfoList.add(roomInfo4);
            RoomInfo roomInfo5 = new RoomInfo();
            roomInfo5.setRoomName("Nos");
            roomInfoList.add(roomInfo5);
            RoomInfo roomInfo6 = new RoomInfo();
            roomInfo6.setRoomName("In(HH:MM)");
            roomInfoList.add(roomInfo6);
            RoomInfo roomInfo7 = new RoomInfo();
            roomInfo7.setRoomName("Out(HH:MM)");
            roomInfoList.add(roomInfo7);
            RoomInfo roomInfo8 = new RoomInfo();
            roomInfo8.setRoomName("Total(HH:MM)");
            roomInfoList.add(roomInfo8);
            RoomInfo roomInfo9 = new RoomInfo();
            roomInfo9.setRoomName("Nos");
            roomInfoList.add(roomInfo9);
            RoomInfo roomInfo10 = new RoomInfo();
            roomInfo10.setRoomName("Type");
            roomInfoList.add(roomInfo10);
            RoomInfo roomInfo11 = new RoomInfo();
            roomInfo11.setRoomName("Value");
            roomInfoList.add(roomInfo11);
            RoomInfo roomInfo12 = new RoomInfo();
            roomInfo12.setRoomName("Total(HH:MM)");
            roomInfoList.add(roomInfo12);
            RoomInfo roomInfo13 = new RoomInfo();
            roomInfo13.setRoomName("Total");
            roomInfoList.add(roomInfo13);
            for (int i = 0; i < tableArray.length(); i++) {
                JSONObject rowObject = tableArray.getJSONObject(i);
                DateInfo dateInfo = new DateInfo();
                dateInfo.setMonth(i + 1);
                dateInfo.setCheck(false);
                dateInfo.setWeek("");
                dateInfoList.add(dateInfo);
                List<rowInfo> rowInfoListRow = new ArrayList<>();
                rowInfo rowinfo = new rowInfo();
                rowinfo.setComment(rowObject.getString("stageName"));
                rowinfo.setSlotId("");
                rowinfo.setIsHoliday(false);
                rowinfo.setIsQty(false);
                rowInfoListRow.add(rowinfo);
                rowInfo rowinfo1 = new rowInfo();
                rowinfo1.setComment(rowObject.getString("iow"));
                rowinfo1.setIsHoliday(false);
                rowinfo1.setIsQty(false);
                rowInfoListRow.add(rowinfo1);
                rowInfo rowinfo2 = new rowInfo();
                rowinfo2.setComment(rowObject.getString("labour"));
                rowinfo2.setIsHoliday(false);
                rowinfo2.setIsQty(false);
                rowInfoListRow.add(rowinfo2);
                rowInfo rowinfo3 = new rowInfo();
                rowinfo3.setComment(rowObject.getString("description"));
                rowinfo3.setIsHoliday(false);
                rowinfo3.setIsQty(false);
                rowInfoListRow.add(rowinfo3);
                rowInfo rowinfo4 = new rowInfo();
                rowinfo4.setComment(rowObject.getString("uom"));
                rowinfo4.setIsHoliday(false);
                rowinfo4.setIsQty(false);
                rowInfoListRow.add(rowinfo4);
                rowInfo rowinfo5 = new rowInfo();
                rowinfo5.setComment(rowObject.getJSONObject("regular").getString("nos"));
                rowinfo5.setIsHoliday(false);
                rowinfo5.setIsQty(false);
                rowInfoListRow.add(rowinfo5);
                rowInfo rowinfo6 = new rowInfo();
                rowinfo6.setComment(rowObject.getJSONObject("regular").getString("in(HH:MM)"));
                rowinfo6.setIsHoliday(false);
                rowinfo6.setIsQty(false);
                rowInfoListRow.add(rowinfo6);
                rowInfo rowinfo7 = new rowInfo();
                rowinfo7.setComment(rowObject.getJSONObject("regular").getString("out(HH:MM)"));
                rowinfo7.setIsHoliday(false);
                rowinfo7.setIsQty(false);
                rowInfoListRow.add(rowinfo7);
                rowInfo rowinfo8 = new rowInfo();
                rowinfo8.setComment(rowObject.getJSONObject("regular").getString("total(HH:MM)"));
                rowinfo8.setIsHoliday(false);
                rowinfo8.setIsQty(false);
                rowInfoListRow.add(rowinfo8);
                rowInfo rowinfo9 = new rowInfo();
                rowinfo9.setComment(rowObject.getJSONObject("ot").getString("nos"));
                rowinfo9.setIsHoliday(false);
                rowinfo9.setIsQty(false);
                rowInfoListRow.add(rowinfo9);
                rowInfo rowinfo10 = new rowInfo();
                rowinfo10.setComment(rowObject.getJSONObject("ot").getString("type"));
                rowinfo10.setIsHoliday(false);
                rowinfo10.setIsQty(false);
                rowInfoListRow.add(rowinfo10);
                rowInfo rowinfo11 = new rowInfo();
                rowinfo11.setComment(rowObject.getJSONObject("ot").getString("value"));
                rowinfo11.setIsHoliday(false);
                rowinfo11.setIsQty(false);
                rowInfoListRow.add(rowinfo11);
                rowInfo rowinfo12 = new rowInfo();
                rowinfo12.setComment(rowObject.getJSONObject("ot").getString("total(HH:MM)"));
                rowinfo12.setIsHoliday(false);
                rowinfo12.setIsQty(false);
                rowInfoListRow.add(rowinfo12);
                rowInfo rowinfo13 = new rowInfo();
                rowinfo13.setComment(rowObject.getString("total"));
                rowinfo13.setIsHoliday(false);
                rowinfo13.setIsQty(false);
                rowInfoListRow.add(rowinfo13);
                rowInfoList.add(rowInfoListRow);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        scrollablePanelAdapter.setDateInfoList(dateInfoList);
        scrollablePanelAdapter.setRoomInfoList(roomInfoList);
        scrollablePanelAdapter.setrowList(rowInfoList);
        scrollablePanelAdapter.setTitle("S.No");
    }
    private void generateSubActivityData(ScrollablePanelAdapter scrollablePanelAdapter, JSONArray tableArray) {
        List<RoomInfo> roomInfoList = new ArrayList<>();
        List<DateInfo> dateInfoList = new ArrayList<>();
        List<List<rowInfo>> rowInfoList = new ArrayList<>();
        try {
            RoomInfo roomInfo = new RoomInfo();
            roomInfo.setRoomName("Stage Name");
            roomInfoList.add(roomInfo);
            RoomInfo roomInfo1 = new RoomInfo();
            roomInfo1.setRoomName("IOW");
            roomInfoList.add(roomInfo1);
            RoomInfo roomInfo2 = new RoomInfo();
            roomInfo2.setRoomName("Sub Activity");
            roomInfoList.add(roomInfo2);
            RoomInfo roomInfo3 = new RoomInfo();
            roomInfo3.setRoomName("Item Name");
            roomInfoList.add(roomInfo3);
            RoomInfo roomInfo4 = new RoomInfo();
            roomInfo4.setRoomName("Spec");
            roomInfoList.add(roomInfo4);
            RoomInfo roomInfo5 = new RoomInfo();
            roomInfo5.setRoomName("UOM");
            roomInfoList.add(roomInfo5);
            RoomInfo roomInfo6 = new RoomInfo();
            roomInfo6.setRoomName("Formula");
            roomInfoList.add(roomInfo6);
            RoomInfo roomInfo7 = new RoomInfo();
            roomInfo7.setRoomName("Length");
            roomInfoList.add(roomInfo7);
            RoomInfo roomInfo8 = new RoomInfo();
            roomInfo8.setRoomName("Breadth");
            roomInfoList.add(roomInfo8);
            RoomInfo roomInfo9 = new RoomInfo();
            roomInfo9.setRoomName("Depth");
            roomInfoList.add(roomInfo9);
            RoomInfo roomInfo10 = new RoomInfo();
            roomInfo10.setRoomName("Nos");
            roomInfoList.add(roomInfo10);
            RoomInfo roomInfo11 = new RoomInfo();
            roomInfo11.setRoomName("Gross Qty");
            roomInfoList.add(roomInfo11);
            RoomInfo roomInfo12 = new RoomInfo();
            roomInfo12.setRoomName("Deduct Qty");
            roomInfoList.add(roomInfo12);
            RoomInfo roomInfo13 = new RoomInfo();
            roomInfo13.setRoomName("Net Qty");
            roomInfoList.add(roomInfo13);
            for (int i = 0; i < tableArray.length(); i++) {
                JSONObject rowObject = tableArray.getJSONObject(i);
                DateInfo dateInfo = new DateInfo();
                dateInfo.setMonth(i + 1);
                dateInfo.setCheck(false);
                dateInfo.setWeek("");
                dateInfoList.add(dateInfo);
                List<rowInfo> rowInfoListRow = new ArrayList<>();
                rowInfo rowinfo = new rowInfo();
                rowinfo.setComment(rowObject.getString("stageName"));
                rowinfo.setSlotId("");
                rowinfo.setIsHoliday(false);
                rowinfo.setIsQty(false);
                rowInfoListRow.add(rowinfo);
                rowInfo rowinfo1 = new rowInfo();
                rowinfo1.setComment(rowObject.getString("iow"));
                rowinfo1.setIsHoliday(false);
                rowinfo1.setIsQty(false);
                rowInfoListRow.add(rowinfo1);
                rowInfo rowinfo2 = new rowInfo();
                rowinfo2.setComment(rowObject.getString("subActivity"));
                rowinfo2.setIsHoliday(false);
                rowinfo2.setIsQty(false);
                rowInfoListRow.add(rowinfo2);
                rowInfo rowinfo3 = new rowInfo();
                rowinfo3.setComment(rowObject.getString("itemName"));
                rowinfo3.setIsHoliday(false);
                rowinfo3.setIsQty(false);
                rowInfoListRow.add(rowinfo3);
                rowInfo rowinfo4 = new rowInfo();
                rowinfo4.setComment(rowObject.getString("spce"));
                rowinfo4.setIsHoliday(false);
                rowinfo4.setIsQty(false);
                rowInfoListRow.add(rowinfo4);
                rowInfo rowinfo5 = new rowInfo();
                rowinfo5.setComment(rowObject.getString("uom"));
                rowinfo5.setIsHoliday(false);
                rowinfo5.setIsQty(false);
                rowInfoListRow.add(rowinfo5);
                rowInfo rowinfo6 = new rowInfo();
                rowinfo6.setComment(rowObject.getString("formula"));
                rowinfo6.setIsHoliday(false);
                rowinfo6.setIsQty(false);
                rowInfoListRow.add(rowinfo6);
                rowInfo rowinfo7 = new rowInfo();
                rowinfo7.setComment(rowObject.getString("length"));
                rowinfo7.setIsHoliday(false);
                rowinfo7.setIsQty(false);
                rowInfoListRow.add(rowinfo7);
                rowInfo rowinfo8 = new rowInfo();
                rowinfo8.setComment(rowObject.getString("breadth"));
                rowinfo8.setIsHoliday(false);
                rowinfo8.setIsQty(false);
                rowInfoListRow.add(rowinfo8);
                rowInfo rowinfo9 = new rowInfo();
                rowinfo9.setComment(rowObject.getString("depth"));
                rowinfo9.setIsHoliday(false);
                rowinfo9.setIsQty(false);
                rowInfoListRow.add(rowinfo9);
                rowInfo rowinfo10 = new rowInfo();
                rowinfo10.setComment(rowObject.getString("nos"));
                rowinfo10.setIsHoliday(false);
                rowinfo10.setIsQty(false);
                rowInfoListRow.add(rowinfo10);
                rowInfo rowinfo11 = new rowInfo();
                rowinfo11.setComment(rowObject.getString("grossQty"));
                rowinfo11.setIsHoliday(false);
                rowinfo11.setIsQty(true);
                rowInfoListRow.add(rowinfo11);
                rowInfo rowinfo12 = new rowInfo();
                rowinfo12.setComment(rowObject.getString("deductedQty"));
                rowinfo12.setIsHoliday(false);
                rowinfo12.setIsQty(true);
                rowInfoListRow.add(rowinfo12);
                rowInfo rowinfo13 = new rowInfo();
                rowinfo13.setComment(rowObject.getString("netQty"));
                rowinfo13.setIsHoliday(false);
                rowinfo13.setIsQty(true);
                rowInfoListRow.add(rowinfo13);
                rowInfoList.add(rowInfoListRow);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        scrollablePanelAdapter.setDateInfoList(dateInfoList);
        scrollablePanelAdapter.setRoomInfoList(roomInfoList);
        scrollablePanelAdapter.setrowList(rowInfoList);
        scrollablePanelAdapter.setTitle("S.No");
    }
    public class ScrollablePanelAdapter extends PanelAdapter {
        private static final int TITLE_TYPE = 4;
        private static final int ROOM_TYPE = 0;
        private static final int DATE_TYPE = 1;
        private static final int ORDER_TYPE = 2;
        private List<RoomInfo> roomInfoList;
        private List<DateInfo> dateInfoList;
        private List<List<rowInfo>> rowList;
        private String title;
        @Override
        public int getRowCount() {
            return dateInfoList.size() + 1;
        }
        @Override
        public int getColumnCount() {
            return roomInfoList.size() + 1;
        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int row, int column) {
            int viewType = getItemViewType(row, column);
            switch (viewType) {
                case DATE_TYPE:
                    setDateView(row, column, (DateViewHolder) holder);
                    break;
                case ROOM_TYPE:
                    setRoomView(column, row, (RoomViewHolder) holder);
                    break;
                case ORDER_TYPE:
                    setOrderView(column, row, (OrderViewHolder) holder);
                    break;
                case TITLE_TYPE:
                    setTitleView(column, row, (TitleViewHolder) holder);
                    break;
                default:
                    setOrderView(column, row, (OrderViewHolder) holder);
            }
        }
        public int getItemViewType(int row, int column) {
            if (column == 0 && row == 0) {
                return TITLE_TYPE;
            }
            if (column == 0) {
                return DATE_TYPE;
            }
            if (row == 0) {
                return ROOM_TYPE;
            }
            return ORDER_TYPE;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case DATE_TYPE:
                    return new DateViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.dpr_listitem_date_info, parent, false));
                case ROOM_TYPE:
                    return new RoomViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.dpr_listitem_room_info, parent, false));
                case ORDER_TYPE:
                    return new OrderViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.dpr_listitem_order_info, parent, false));
                case TITLE_TYPE:
                    return new TitleViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.dpr_listitem_title, parent, false));
                default:
                    break;
            }
            return new OrderViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dpr_listitem_order_info, parent, false));
        }
        private void setDateView(final int pos, int column, final DateViewHolder viewHolder) {
            final DateInfo dateInfo = dateInfoList.get(pos - 1);
            if (dateInfo != null && pos > 0) {
                if (pos % 2 == 0) {
                    viewHolder.view.setBackgroundResource(R.drawable.bg_white_item);
                } else {
                    viewHolder.view.setBackgroundResource(R.drawable.bg_lightpink_item);
                }
                viewHolder.dateTextView.setText("" + dateInfo.getMonth());
            }
        }
        private void setRoomView(int pos, int column, RoomViewHolder viewHolder) {
            RoomInfo roomInfo = roomInfoList.get(pos - 1);
            if (roomInfo != null && pos > 0) {
                if (pos == 1) {
                    viewHolder.stageLay.setVisibility(View.VISIBLE);
                    viewHolder.baseLay.setVisibility(View.GONE);
                    viewHolder.roomTypeTextView.setText(roomInfo.getRoomType());
                    viewHolder.roomNameTextView.setText(roomInfo.getRoomName());
                    viewHolder.roomTypeTextView1.setText(roomInfo.getRoomType());
                    viewHolder.roomNameTextView1.setText(roomInfo.getRoomName());
                } else {
                    viewHolder.stageLay.setVisibility(View.GONE);
                    viewHolder.baseLay.setVisibility(View.VISIBLE);
                    viewHolder.roomTypeTextView.setText(roomInfo.getRoomType());
                    viewHolder.roomNameTextView.setText(roomInfo.getRoomName());
                    viewHolder.roomTypeTextView1.setText(roomInfo.getRoomType());
                    viewHolder.roomNameTextView1.setText(roomInfo.getRoomName());
                }
            }
        }
        private void setTitleView(final int row, final int column, final TitleViewHolder holder) {
            holder.titleTextView.setText(title);
        }
        private void setOrderView(final int row, final int column, final OrderViewHolder viewHolder) {
            final rowInfo rowInfo = rowList.get(column - 1).get(row - 1);
            if (rowInfo != null && column > 0) {
                if (column % 2 == 0) {
                    viewHolder.view.setBackgroundResource(R.drawable.bg_white_item);
                } else {
                    viewHolder.view.setBackgroundResource(R.drawable.bg_lightpink_item);
                }
                if (rowInfo.getIsHoliday()) {
                    viewHolder.statusTextView.setVisibility(View.GONE);
                    viewHolder.qtyTextView.setVisibility(View.GONE);
                    viewHolder.nameTextView.setVisibility(View.VISIBLE);
                    viewHolder.nameTextView.setText(rowInfo.getComment());
                    viewHolder.nameTextView.setTextColor(getResources().getColor(R.color.app_theme_gray));
                } else if (rowInfo.getIsQty()) {
                    viewHolder.statusTextView.setVisibility(View.GONE);
                    viewHolder.nameTextView.setVisibility(View.GONE);
                    viewHolder.qtyTextView.setVisibility(View.VISIBLE);
                    viewHolder.qtyTextView.setText(rowInfo.getComment());
                    viewHolder.qtyTextView.setTextColor(getResources().getColor(R.color.app_theme_gray));
                } else {
                    viewHolder.statusTextView.setVisibility(View.VISIBLE);
                    viewHolder.nameTextView.setVisibility(View.GONE);
                    viewHolder.qtyTextView.setVisibility(View.GONE);
                    viewHolder.statusTextView.setText(rowInfo.getComment());
                    viewHolder.statusTextView.setTextColor(getResources().getColor(R.color.app_theme_gray));
                }
                if (row == 1) {
                    viewHolder.stageLay.setVisibility(View.VISIBLE);
                    viewHolder.baseLay.setVisibility(View.GONE);
                    viewHolder.stageTextView.setText(rowInfo.getComment());
                    viewHolder.stageTextView.setTextColor(getResources().getColor(R.color.app_theme_gray));
                } else {
                    viewHolder.stageLay.setVisibility(View.GONE);
                    viewHolder.baseLay.setVisibility(View.VISIBLE);
                }
            }
        }
        private class DateViewHolder extends RecyclerView.ViewHolder {
            public TextView dateTextView;
            public View view;
            public DateViewHolder(View view) {
                super(view);
                this.view = view;
                this.dateTextView = (TextView) view.findViewById(R.id.date);
            }
        }
        private class RoomViewHolder extends RecyclerView.ViewHolder {
            public TextView roomTypeTextView;
            public TextView roomNameTextView;
            public TextView roomTypeTextView1;
            public TextView roomNameTextView1;
            public LinearLayout baseLay;
            public LinearLayout stageLay;
            public RoomViewHolder(View view) {
                super(view);
                this.roomTypeTextView = (TextView) view.findViewById(R.id.room_type);
                this.roomNameTextView = (TextView) view.findViewById(R.id.room_name);
                this.roomTypeTextView1 = (TextView) view.findViewById(R.id.room_type1);
                this.roomNameTextView1 = (TextView) view.findViewById(R.id.room_name1);
                this.baseLay = (LinearLayout) view.findViewById(R.id.base_lay);
                this.stageLay = (LinearLayout) view.findViewById(R.id.stage_lay);
            }
        }
        private class OrderViewHolder extends RecyclerView.ViewHolder {
            public TextView nameTextView;
            public TextView statusTextView;
            public TextView qtyTextView;
            public TextView stageTextView;
            public LinearLayout baseLay;
            public LinearLayout stageLay;
            public View view;
            public OrderViewHolder(View view) {
                super(view);
                this.view = view;
                this.statusTextView = (TextView) view.findViewById(R.id.status);
                this.nameTextView = (TextView) view.findViewById(R.id.code);
                this.qtyTextView = (TextView) view.findViewById(R.id.qty);
                this.stageTextView = (TextView) view.findViewById(R.id.status1);
                this.baseLay = (LinearLayout) view.findViewById(R.id.guest_layout);
                this.stageLay = (LinearLayout) view.findViewById(R.id.guest_layout1);
            }
        }
        private class TitleViewHolder extends RecyclerView.ViewHolder {
            public TextView titleTextView;
            public TitleViewHolder(View view) {
                super(view);
                this.titleTextView = (TextView) view.findViewById(R.id.title);
            }
        }
        public void setRoomInfoList(List<RoomInfo> roomInfoList) {
            this.roomInfoList = roomInfoList;
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
}