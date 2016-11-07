package com.anuranbarman.gamedunia;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by anuran on 27/10/16.
 */

public class CustomCatAdapter extends BaseAdapter {
    private ArrayList<DataModel> data_list;
    private Activity activity;
    private static LayoutInflater inflater=null;
    public CustomCatAdapter(Activity activity,ArrayList<DataModel> data_list) {
        this.activity=activity;
        this.data_list = data_list;
        inflater=(LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data_list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ViewHolder holder;
        if(convertView==null){
            vi=inflater.inflate(R.layout.cat_list_row,null);
            holder=new ViewHolder();

            holder.catTitle=(TextView)vi.findViewById(R.id.catName);
            holder.arrow=(ImageView)vi.findViewById(R.id.arrowImage);

            vi.setTag(holder);
        }else
            holder=(ViewHolder)vi.getTag();

            holder.catTitle.setText(data_list.get(position).getCat_title());

        return vi;
    }

    public static class ViewHolder{
        TextView catTitle;
        ImageView arrow;
    }
}
