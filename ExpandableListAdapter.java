package com.guruinfo.scm.common;
import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.Gallery;
import com.guruinfo.scm.R;
import com.guruinfo.scm.Ticketing.TicketingGeneralAddActivity;
import java.util.ArrayList;
import java.util.HashMap;
public class ExpandableListAdapter extends BaseExpandableListAdapter {
	private Activity activity;
	private ArrayList<ArrayList<HashMap<String ,String>>> childtems;
	private LayoutInflater inflater;
	private ArrayList<String> parentItems;
	ArrayList<HashMap<String ,String>> child;
	private Activity mContex;
	private LayoutInflater mInflater;
	String comm_id,type,unit;
	String ticketType;
	String cat;
	public ExpandableListAdapter(Activity context, ArrayList<String> parents, ArrayList<ArrayList<HashMap<String, String>>> childern, String comm_id, String type, String unit, String ticketType) {
		mContex = context;
		this.parentItems = parents;
		this.childtems = childern;
		this.comm_id=comm_id;
		this.type=type;
		this.unit=unit;
		this.ticketType=ticketType;
		mInflater = LayoutInflater.from(context);
	}
	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		child =  childtems.get(groupPosition);
		View v = convertView;
		final ViewHolder holder;
		if (v == null) {
			//LayoutInflater mInflater = LayoutInflater.from(mContex);
			v = mInflater.inflate(R.layout.home_view,parent, false);
			holder = new ViewHolder();
			holder.gridView = (Gallery) v.findViewById(R.id.GridView_toolbar);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		DisplayMetrics metrics = new DisplayMetrics();
		mContex.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		// set gallery to left side
		ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) holder.gridView.getLayoutParams();
		mlp.setMargins(-(metrics.widthPixels / 2 + (120/2)), mlp.topMargin,
				mlp.rightMargin, mlp.bottomMargin);
		GridAdapter adapter = new GridAdapter(mContex, child,comm_id,type,unit,parentItems.get(groupPosition));
		holder.gridView.setAdapter(adapter);// Adapter
		/*ListAdapter myListAdapter1 = holder.gridView.getAdapter();
		int size1=myListAdapter1.getCount();
		//ListHeight.gridViewSetting(mContex, holder.gridView);
		holder.gridView.setNumColumns(size1);
		adapter.notifyDataSetChanged();*/
		holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
									int position, long id) {
				//Log.d("Group Position----"+groupPosition,"Child Position-----"+position);
	 if(ticketType.equalsIgnoreCase("General")){
					Intent intent = new Intent(mContex, TicketingGeneralAddActivity.class);
					intent.putExtra("sub_cate", childtems.get(groupPosition).get(position).get("Sub_Category_Name"));
					intent.putExtra("id", childtems.get(groupPosition).get(position).get("Config_id"));
					intent.putExtra("subcatename", childtems.get(groupPosition).get(position).get("Category_Name"));
					intent.putExtra("category_name", parentItems.get(groupPosition));
					intent.putExtra("doc_id", childtems.get(groupPosition).get(position).get("doc_id"));
					intent.putExtra("Config_id", childtems.get(groupPosition).get(position).get("Config_id"));
					intent.putExtra("comm_id", comm_id);
					intent.putExtra("type", type);
					intent.putExtra("unit", unit);
					mContex.startActivity(intent);
				}
			//	Log.d("Click Position----","Group Position----"+groupPosition+" "+"Child Position-----"+position);
			}
		});
		return v;
	}
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.expandablelist_group, null);
		}
		((CheckedTextView) convertView).setText(parentItems.get(groupPosition));
		((CheckedTextView) convertView).setChecked(isExpanded);
		return convertView;
	}
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}
	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}
	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}
	@Override
	public int getGroupCount() {
		return parentItems.size();
	}
	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}
	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}
	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}
	@Override
	public boolean hasStableIds() {
		return false;
	}
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
	class ViewHolder {
		Gallery gridView;
	}
}