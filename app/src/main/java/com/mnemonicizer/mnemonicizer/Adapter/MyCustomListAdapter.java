package com.mnemonicizer.mnemonicizer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mnemonicizer.mnemonicizer.R;

import java.util.ArrayList;
import java.util.List;

public class MyCustomListAdapter extends BaseAdapter {

    List<String> aplabets = new ArrayList<>();
    ArrayList<String> listAddress;

    Context mContext;
    //constructor
    public MyCustomListAdapter(Context mContext, List<String> objects) {
        this.mContext = mContext;
        this.aplabets = objects;
    }


    public int getCount() {
        return aplabets.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View arg1, ViewGroup viewGroup)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.aplha_item, viewGroup, false);

        TextView alpha = (TextView) row.findViewById(R.id.alpha);

        alpha.setText(aplabets.get(position));



        return row;
    }
}
