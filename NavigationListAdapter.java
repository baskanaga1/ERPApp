package com.guruinfo.scm.tms;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.ui.SCMTextView;
import java.util.ArrayList;
/**
 * Created by Kannan G on 14/10/2016.
 */
class ViewHolder {
    ImageView imgIcon;
    SCMTextView txtTitle;
}
public class NavigationListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> navDrawerItems;
    Typeface arial_bold;
    public NavigationListAdapter(Context context, ArrayList<String> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }
    @Override
    public int getCount() {
        return navDrawerItems.size();
    }
    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final ViewHolder holder;
        if (v == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = mInflater.inflate(R.layout.drawer_list_item_layout, null);
            holder = new ViewHolder();
            holder.imgIcon = (ImageView) v.findViewById(R.id.icon);
            holder.txtTitle = (SCMTextView) v.findViewById(R.id.title);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.txtTitle.setTextColor(context.getResources().getColor(R.color.black));
        if (navDrawerItems.get(position).equalsIgnoreCase("Self Task")) {
           holder.imgIcon.setImageResource(R.drawable.tms_nav_selftask);
        } else if (navDrawerItems.get(position).equalsIgnoreCase("My Task")) {
            holder.imgIcon.setImageResource(R.drawable.tms_nav_mytask);
        } else if (navDrawerItems.get(position).equalsIgnoreCase("Assigned")) {
            holder.imgIcon.setImageResource(R.drawable.tms_nav_assign);
        }else if (navDrawerItems.get(position).equalsIgnoreCase("Group")) {
            holder.imgIcon.setImageResource(R.drawable.tms_nav_group);
        } else if (navDrawerItems.get(position).equalsIgnoreCase("Tagged")) {
            holder.imgIcon.setImageResource(R.drawable.tms_nav_tagged);
        }
       // holder.txtTitle.setText(navDrawerItems.get(position).toUpperCase());
        holder.txtTitle.setText(navDrawerItems.get(position));
        return v;
    }
}