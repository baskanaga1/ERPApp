package com.guruinfo.scm.tms;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.tree.model.TreeNode;
import com.guruinfo.scm.common.ui.SCMTextView;
import org.json.JSONArray;
import org.json.JSONException;
import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by Kannan G on 11/29/2016.
 */
public class TMSRattingTreeAdapter extends TreeNode.BaseNodeViewHolder<TMSRattingTreeAdapter.TreeItem> {
    String TAG = "TMSRattingTreeAdapter";
    String uid, Cre_Id;
    SessionManager session;
    Typeface myTypeface;
    Typeface myTypefaceBold;
    int beigeRowColor;
    int lightGreyRowColor;
    int lastAppliedColor;
    @Bind(R.id.task_Name)
    TextView tmsTaskName;
    @Bind(R.id.subRattingTable)
    TableLayout subRattingTable;
    @Bind(R.id.tms_tree)
    ImageView tmsTree;
    JSONArray rowArray;
    public TMSRattingTreeAdapter(Context context) {
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
        final View view = inflater.inflate(R.layout.tms_ratting_treerow, null, false);
        ButterKnife.bind(this, view);
        myTypeface = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        myTypefaceBold = Typeface.createFromAsset(context.getAssets(), "Roboto-Bold.ttf");
        tmsTaskName.setTypeface(myTypefaceBold);
        if (value.arrowView)
            tmsTree.setVisibility(View.VISIBLE);
        else
            tmsTree.setVisibility(View.GONE);
        rowArray = value.listValue;
        for (int i = 0; i < rowArray.length(); i++) {
            try {
                int scoreValue = 0;
                try {
                    scoreValue = Integer.parseInt(value.stars);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String displayName = rowArray.getJSONObject(i).getString("displayName");
                if (displayName.equalsIgnoreCase("Task Name")) {
                    tmsTaskName.setText(rowArray.getJSONObject(i).getString("displayValue"));
                } else {
                    subRattingTable.addView(addTableRowToTableLayout(rowArray.getJSONObject(i).getString("displayName"), rowArray.getJSONObject(i).getString("displayValue"), scoreValue));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return view;
    }
    public View addRattingBar(String value, int score) {
        LayoutInflater inflater2 = LayoutInflater.from(context);
        View rattedView = inflater2.inflate(R.layout.tms_rating_label, null);
        RatingBar tmmRatingBar = (RatingBar) rattedView.findViewById(R.id.tms_ratingBarSmall);
        SCMTextView tmsRatedText = (SCMTextView) rattedView.findViewById(R.id.tms_smallRatedText);
        tmmRatingBar.setRating(score);
        tmsRatedText.setText(value);
        return rattedView;
    }
    public TableRow addTableRowToTableLayout(String key, String value, int score) {
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
        if(key.equalsIgnoreCase("Rating")){
        if (score > 0) {
            TableRow ratingRow = new TableRow(context);
            TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2);
            ratingRow.setLayoutParams(layoutParams1);
            ratingRow.addView(addRattingBar(value, score));
            tableRow.addView(ratingRow,1);
        } else {
            tableRow.addView(addTextViewToTableRow(value, myTypefaceBold, 2), 1);
        }}else{
            tableRow.addView(addTextViewToTableRow(value, myTypefaceBold, 2), 1);
        }
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
        textView.setTextSize(14);
        // textView.setGravity(Gravity.CENTER);
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
        public String stars;
        public TreeItem(JSONArray listValue, boolean arrowView, String stars) {
            this.listValue = listValue;
            this.arrowView = arrowView;
            this.stars = stars;
        }
    }
}
