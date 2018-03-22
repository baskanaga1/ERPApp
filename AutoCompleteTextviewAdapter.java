package com.guruinfo.scm.tms;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.guruinfo.scm.R;
import java.util.ArrayList;
import java.util.HashMap;
import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by Kannan G on 3/12/2016.
 */
public class AutoCompleteTextviewAdapter extends BaseAdapter implements
        Filterable {
    private ArrayList<HashMap<String,String>> fullList;
    private ArrayList<HashMap<String,String>> mOriginalValues;
    private ArrayFilter mFilter;
    Context context;
    public AutoCompleteTextviewAdapter(Context context, ArrayList<HashMap<String,String>> fullList) {
        this.fullList = fullList;
        mOriginalValues = new ArrayList<HashMap<String,String>>(fullList);
        this.context=context;
    }
    @Override
    public int getCount() {
        return fullList.size();
    }
    @Override
    public Object getItem(int position) {
        return fullList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final SearchViewHolder holder;
        if (v == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = mInflater.inflate(R.layout.searchlist_row, null);
            holder = new SearchViewHolder(v);
            v.setTag(holder);
        } else {
            holder = (SearchViewHolder) v.getTag();
        }
        holder.name.setText(fullList.get(position).get("value"));
        return v;
    }
    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }
    private class ArrayFilter extends Filter {
        private Object lock;
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (mOriginalValues == null) {
                synchronized (lock) {
                    mOriginalValues = new ArrayList<HashMap<String,String>>(fullList);
                }
            }
            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>(
                            mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                final String prefixString = prefix.toString().toLowerCase();
                ArrayList<HashMap<String,String>> values = mOriginalValues;
                int count = values.size();
                ArrayList<HashMap<String,String>> newValues = new ArrayList<HashMap<String,String>>(count);
                HashMap<String,String> mapValues=new HashMap<>();
                for (int i = 0; i < count; i++) {
                    String itemName = values.get(i).get("value");
                    String itemId= values.get(i).get("id");
                    if (itemName.toLowerCase().contains(prefixString)) {
                        mapValues.put("id",itemId);
                        mapValues.put("value",itemName);
                        newValues.add(new HashMap<String, String>(mapValues));
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            if (results.values != null) {
                fullList = (ArrayList<HashMap<String,String>>) results.values;
            } else {
                fullList = new ArrayList<HashMap<String,String>>();
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
class SearchViewHolder {
    @Bind(R.id.textView1)
    TextView name;
    public SearchViewHolder(View view) {
        ButterKnife.bind(this, view);
    }
}