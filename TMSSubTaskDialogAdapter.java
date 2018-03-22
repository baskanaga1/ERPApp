package com.guruinfo.scm.tms;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.tree.model.TreeNode;
import org.json.JSONArray;
import org.json.JSONException;
import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by Kannan G on 11/24/2016.
 */
public class TMSSubTaskDialogAdapter extends TreeNode.BaseNodeViewHolder<TMSSubTaskDialogAdapter.TreeItem> {
    String TAG = "TMSSubTaskDialogAdapter";
    String uid, Cre_Id;
    String requestParameter;
    BackgroundTask backgroundTask;
    SessionManager session;
    Typeface myTypeface;
    Typeface myTypefaceBold;
    int beigeRowColor;
    int lightGreyRowColor;
    int lastAppliedColor;
    TMSListReload viewReload;
    @Bind(R.id.task_Name)
    TextView tmsTaskName;
    @Bind(R.id.subTaskTable)
    TableLayout subTaskTable;
    @Bind(R.id.tms_tree)
    ImageView tmsTree;
    @Bind(R.id.tms_view)
    ImageView tmsView;
    @Bind(R.id.tms_delete)
    ImageView tmsDelete;
    JSONArray rowArray;
    Context context_;
    public TMSSubTaskDialogAdapter(Context context) {
        super(context);
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
        beigeRowColor = context.getResources().getColor(R.color.unread_bg);
        lightGreyRowColor = context.getResources().getColor(R.color.read_bg);
    }
    @Override
    public View createNodeView(final TreeNode node, final TreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.tms_subtask_dialog_row, null, false);
        ButterKnife.bind(this, view);
        myTypeface = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        myTypefaceBold = Typeface.createFromAsset(context.getAssets(), "Roboto-Bold.ttf");
        tmsTaskName.setTypeface(myTypefaceBold);
        viewReload = value.tmsViewReload;
        if (value.arrowView)
            tmsTree.setVisibility(View.VISIBLE);
        else
            tmsTree.setVisibility(View.GONE);
        if (value.isView)
            tmsView.setVisibility(View.VISIBLE);
        else
            tmsView.setVisibility(View.GONE);
        if (value.isDelete)
            tmsDelete.setVisibility(View.VISIBLE);
        else
            tmsDelete.setVisibility(View.GONE);
        rowArray = value.listValue;
        for (int i = 0; i < rowArray.length(); i++) {
            try {
                String displayName = rowArray.getJSONObject(i).getString("displayName");
                if (displayName.equalsIgnoreCase("Task Name")) {
                    tmsTaskName.setText(rowArray.getJSONObject(i).getString("displayValue"));
                } else {
                    subTaskTable.addView(addTableRowToTableLayout(rowArray.getJSONObject(i).getString("displayName"), rowArray.getJSONObject(i).getString("displayValue")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        tmsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("TaskId", value.taskId);
                bundle.putString("Mode", "display");
                viewReload.onMoveEditPage(bundle);
            }
        });
        tmsDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewReload.onReloadList(value.taskId);
            }
        });
        return view;
    }
    public TableRow addTableRowToTableLayout(String key, String value) {
        TableRow tableRow = new TableRow(context);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.FILL_PARENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        tableRow.setGravity(Gravity.CENTER_VERTICAL);
        tableRow.setLayoutParams(layoutParams);
        tableRow.setOrientation(LinearLayout.HORIZONTAL);
        tableRow.setWeightSum(4);
        tableRow.setPadding(5, 5, 5, 5);
        applyAlternateRowColor(tableRow);
        tableRow.addView(addTextViewToTableRow(key, myTypeface, 2), 0);
        tableRow.addView(addTextViewToTableRow(value, myTypefaceBold, 2), 1);
        return tableRow;
    }
    public void applyAlternateRowColor(LinearLayout dataTable) {
        if (lastAppliedColor == 0) {
            dataTable.setBackgroundColor(beigeRowColor);
            lastAppliedColor = beigeRowColor;
        } else if (lastAppliedColor == beigeRowColor) {
            dataTable.setBackgroundColor(lightGreyRowColor);
            lastAppliedColor = lightGreyRowColor;
        } else if (lastAppliedColor == lightGreyRowColor) {
            dataTable.setBackgroundColor(beigeRowColor);
            lastAppliedColor = beigeRowColor;
        }
    }
    public TextView addTextViewToTableRow(String value, Typeface typeface, int layoutWeight) {
        TextView textView = new TextView(context);
        textView.setTextColor(context.getResources().getColor(R.color.result_color));
        if (typeface != null)
            textView.setTypeface(typeface);
        if (context.getResources().getBoolean(R.bool.isTablet))
            textView.setTextSize(22);
        else
            textView.setTextSize(14);
        textView.setPadding(5, 5, 5, 5);
        textView.setText(value);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        textView.setLayoutParams(layoutParams);
        return textView;
    }
    public static class TreeItem {
        public JSONArray listValue;
        public boolean arrowView;
        public boolean isView;
        public boolean isDelete;
        public String taskId;
        public TMSListReload tmsViewReload;
        public TreeItem(JSONArray listValue, TMSListReload tmsViewReload, boolean arrowView, boolean isView, boolean isDelete, String taskId) {
            this.listValue = listValue;
            this.arrowView = arrowView;
            this.isView = isView;
            this.isDelete = isDelete;
            this.taskId = taskId;
            this.tmsViewReload = tmsViewReload;
        }
    }
}
