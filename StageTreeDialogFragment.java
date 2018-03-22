package com.guruinfo.scm.DPR;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.google.gson.Gson;
import com.guruinfo.scm.DaoSession;
import com.guruinfo.scm.R;
import com.guruinfo.scm.StageList;
import com.guruinfo.scm.StageListDao;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.SCMApplication;
import com.guruinfo.scm.common.service.StageService;
import com.guruinfo.scm.mr.MREditRequestActivity;
import com.guruinfo.scm.mr.MREditRequestFragment;
import com.guruinfo.scm.mr.MRFilterFragment;
import com.guruinfo.scm.mr.model.TreeModel;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Kannan G on 3/16/2016.
 */
public class StageTreeDialogFragment extends BaseActivity {
    String TAG = "StageTreeDialogFragment";
    AndroidTreeView tView;
    Context context;
    Gson gson = new Gson();
    String stageList;
    String Display;
    String StageValue = "";
    String userId;
    String pjtId;
    StageListDao stageListDao;
    protected DaoSession daoSession;
    TreeNode root = TreeNode.root();
    ViewGroup containerView;
    ProgressBar pb;
    ImageView close;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mr_stage_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        containerView = (ViewGroup) findViewById(R.id.container);
        pb = (ProgressBar) findViewById(R.id.pb);
        close = (ImageView) findViewById(R.id.close);
        context = this;
        daoSession = ((SCMApplication) context.getApplicationContext()).getDaoSession();
        stageListDao = daoSession.getStageListDao();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("datas");
        // stageList = bundle.getString("stage");
        Display = bundle.getString("Display");
        userId = bundle.getString("userId");
        pjtId = bundle.getString("pjtId");
        pb.setVisibility(View.VISIBLE);
        new stageDisplayProjectBasedTask().execute();
        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public class stageDisplayProjectBasedTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected String doInBackground(String... params) {
            List<StageList> stageLists = stageListDao.queryBuilder().where(StageListDao.Properties.User_id.eq(userId), StageListDao.Properties.Pjt_id.eq(pjtId)).list();
            String stageValues = "";
            for (int i = 0; i < stageLists.size(); i++) {
                stageValues += stageLists.get(i).getStage();
            }
            StageService parentNode1 = gson.fromJson(stageValues, StageService.class);
            if (parentNode1.getIOWSELECTVALUE().size() > 0) {
                TreeModel parentNode = (TreeModel) (((ArrayList) parentNode1.getIOWSELECTVALUE()).get(0));
                // TreeModel parentNode = gson.fromJson(stageList, TreeModel.class);
                TreeNode parentTreeNode = new TreeNode(new com.guruinfo.scm.MaterialRequest.TreeViewAdapter.TreeItem(parentNode, parentNode.getIsSelect(), parentNode.getstageName(), parentNode.getValue(), parentNode.getId(), parentNode.getstageId(), parentNode.getIsIOW(), true));
                if (parentTreeNode.getChildren().size() == 0)
                    if (parentNode.getIsIOW().equalsIgnoreCase("f"))
                        addChild(parentNode, parentTreeNode);
                parentTreeNode.setExpanded(true);
                root.addChild(parentTreeNode);
            }
            //addChild(parentNode,parentTreeNode);
            tView = new AndroidTreeView(context, root);
            tView.setDefaultAnimation(true);
            tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
            tView.setDefaultViewHolder(com.guruinfo.scm.MaterialRequest.TreeViewAdapter.class);
            tView.setDefaultNodeClickListener(nodeClickListener);
            tView.setDefaultNodeLongClickListener(nodeLongClickListener);
            return "";
        }
        @Override
        protected void onPostExecute(String updateDate) {
            pb.setVisibility(View.GONE);
            containerView.addView(tView.getView());
        }
    }
    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            com.guruinfo.scm.MaterialRequest.TreeViewAdapter.TreeItem item = (com.guruinfo.scm.MaterialRequest.TreeViewAdapter.TreeItem) value;
            TreeModel parentNode = item.parentNode;
            if (node.getChildren().size() == 0)
                if (parentNode.getIsIOW().equalsIgnoreCase("f"))
                    addChild(parentNode, node);
        }
    };
    public TreeNode addChild(TreeModel parentNode, TreeNode parentTreeNode) {
        for (TreeModel level1Tree : parentNode.getChildrens()) {
            TreeNode level1TreeNode = null;
            if (level1Tree.getChildrens() != null) {
                if (level1Tree.getChildrens().size() != 0) {
                    if ((level1Tree.getIsIOW().equalsIgnoreCase("f"))) {
                        level1TreeNode = new TreeNode(new com.guruinfo.scm.MaterialRequest.TreeViewAdapter.TreeItem(level1Tree, level1Tree.getIsSelect(), level1Tree.getstageName(), level1Tree.getValue(), level1Tree.getId(), level1Tree.getstageId(), level1Tree.getIsIOW(), true));
                    } else {
                        addChild(level1Tree, parentTreeNode);
                    }
                } else {
                    level1TreeNode = new TreeNode(new com.guruinfo.scm.MaterialRequest.TreeViewAdapter.TreeItem(level1Tree, level1Tree.getIsSelect(), level1Tree.getstageName(), level1Tree.getValue(), level1Tree.getId(), level1Tree.getstageId(), level1Tree.getIsIOW(), false));
                }
            } else {
                level1TreeNode = new TreeNode(new com.guruinfo.scm.MaterialRequest.TreeViewAdapter.TreeItem(level1Tree, level1Tree.getIsSelect(), level1Tree.getstageName(), level1Tree.getValue(), level1Tree.getId(), level1Tree.getstageId(), level1Tree.getIsIOW(), false));
            }
            if (level1TreeNode != null)
                parentTreeNode.addChild(level1TreeNode);
        }
        return parentTreeNode;
    }
    public String selectedIOWStageName(TreeNode node) {
        if (node.getParent().getLevel() > 0) {
            com.guruinfo.scm.MaterialRequest.TreeViewAdapter.TreeItem item = (com.guruinfo.scm.MaterialRequest.TreeViewAdapter.TreeItem) ((node.getParent()).getValue());
            StageValue = item.name + "->" + StageValue;
            selectedIOWStageName((node.getParent()));
        }
        return StageValue;
    }
    public String selectedStageName(TreeNode node) {
        if (node.getParent().getLevel() > 0) {
            com.guruinfo.scm.MaterialRequest.TreeViewAdapter.TreeItem item = (com.guruinfo.scm.MaterialRequest.TreeViewAdapter.TreeItem) ((node.getParent()).getValue());
            StageValue = item.name + "->" + StageValue;
            selectedStageName((node.getParent()));
        }
        return StageValue;
    }
    private TreeNode.TreeNodeLongClickListener nodeLongClickListener = new TreeNode.TreeNodeLongClickListener() {
        @Override
        public boolean onLongClick(TreeNode node, Object value) {
            com.guruinfo.scm.MaterialRequest.TreeViewAdapter.TreeItem item = (com.guruinfo.scm.MaterialRequest.TreeViewAdapter.TreeItem) value;
            // if (item.isSelect.equalsIgnoreCase("t")) {
            finish();
            if (Display.equalsIgnoreCase("IOW")) {
                //  StageValue = item.stageName;
                if (item.isIOW.equalsIgnoreCase("t")) {
                    String StageNameVaues = selectedIOWStageName(node);
                    MREditRequestFragment.stageName.setText(StageNameVaues);
                    MREditRequestFragment.IOWValue.setText(item.name);
                    MREditRequestFragment.iowIdText.setText(item.id);
                    MREditRequestFragment.iowId = item.id;
                } else {
                    String StageNameVaues = selectedStageName(node) + item.name + "->";
                    MREditRequestFragment.stageName.setText(StageNameVaues);
                    MREditRequestFragment.IOWValue.setText("");
                    MREditRequestFragment.iowIdText.setText("");
                    MREditRequestFragment.iowId = "";
                }
                MREditRequestFragment.stageIdText.setText(item.stageId);
                MREditRequestFragment.stageId = item.stageId;
                MREditRequestFragment.valueChange();
            }else if (Display.equalsIgnoreCase("D_IOW")) {
                if (item.isIOW.equalsIgnoreCase("t")) {
                    String StageNameVaues = selectedIOWStageName(node);
                    MREditRequestActivity.stageName.setText(StageNameVaues);
                    MREditRequestActivity.IOWValue.setText(item.name);
                    MREditRequestActivity.iowIdText.setText(item.id);
                    MREditRequestActivity.iowId = item.id;
                } else {
                    String StageNameVaues = selectedStageName(node) + item.name + "->";
                    MREditRequestActivity.stageName.setText(StageNameVaues);
                    MREditRequestActivity.IOWValue.setText("");
                    MREditRequestActivity.iowIdText.setText("");
                    MREditRequestActivity.iowId = "";
                }
                MREditRequestActivity.stageIdText.setText(item.stageId);
                MREditRequestActivity.stageId = item.stageId;
                MREditRequestActivity.valueChange();
            } else {
                DPRFilterFragment.stageId = item.id;
                DPRFilterFragment.FilterTree.setText(item.name);
            }
            return true;
        }
    };
}