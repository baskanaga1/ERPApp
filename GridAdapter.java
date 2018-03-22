package com.guruinfo.scm.common;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.guruinfo.scm.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
public class GridAdapter extends BaseAdapter {
    private Context mContext;
    int groupPosition;
    String comm_id, type, unit, category_name;
    private ArrayList<HashMap<String, String>> childtems;
    AnimImageLoader imageLoader;
    public GridAdapter(Context context, ArrayList<HashMap<String, String>> childern, String comm_id, String type, String unit, String category_name) {
        this.mContext = context;
        this.childtems = childern;
        this.comm_id = comm_id;
        this.type = type;
        this.unit = unit;
        this.category_name = category_name;
        imageLoader = new AnimImageLoader(context);
    }
    @Override
    public int getCount() {
        return childtems.size();
    }
    @Override
    public HashMap<String, String> getItem(int position) {
        return childtems.get(position);
    }
    @Override
    public long getItemId(int arg0) {
        return 0;
    }
    @Override
    public View getView(final int position, View v, final ViewGroup parent) {
        ViewHolder holder = null;
        if (v == null) {
			/*LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			//final View rowView = inflater.inflate(R.layout.offers_child, parent, false);
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, null);*/
			/*LayoutInflater vi;
			vi = LayoutInflater.from(mContext);
			v = vi.inflate(R.layout.grid_item, null);*/
			/*LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = mInflater.inflate(R.layout.grid_item, null);*/
            holder = new ViewHolder();
            v = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
            holder.name = (TextView) v.findViewById(R.id.label);
            holder.product_img = (ImageView) v.findViewById(R.id.img);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.name.setText(childtems.get(position).get("Sub_Category_Name"));
        final String image_url = AppContants.largeThumbImageURL + childtems.get(position).get("doc_id");
        System.out.println("image" + image_url);
        if (childtems.get(position).get("doc_id").equalsIgnoreCase("0"))
            imageLoader.DisplayImage(image_url, holder.product_img, 4);
        else
            Picasso.with(mContext)
                    .load(image_url)
                    .into(holder.product_img);
		/*v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				*//*Intent intent = new Intent(mContext, ExternalTicketingAddActivity.class);
				intent.putExtra("sub_cate", childtems.get(position).get("Sub_Category_Name"));
				intent.putExtra("id", childtems.get(position).get("Config_id"));
				intent.putExtra("subcatename", childtems.get(position).get("Category_Name"));
				intent.putExtra("category_name", category_name);
				intent.putExtra("doc_id", childtems.get(position).get("doc_id"));
				intent.putExtra("Config_id", childtems.get(position).get("Config_id"));
				intent.putExtra("comm_id", comm_id);
				intent.putExtra("type", type);
				intent.putExtra("unit", unit);
				mContext.startActivity(intent);
				Log.d("Total Child Count----", ""+parent.getChildCount());*//*
				Log.d("Click Position----","Group Position----"+groupPosition+" "+"Child Position-----"+position);
			}
		});*/
        return v;
    }
    class ViewHolder {
        TextView name;
        ImageView product_img;
    }
}