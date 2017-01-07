package com.example.self_health.other;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.self_health.R;

import java.util.zip.Inflater;

/**
 * Created by pc on 11/20/2016.
 */

public class ImageAdapterMood extends ArrayAdapter<String> {
    private Context mContext;
    int[] imgs ={};
    String[] names = {};
    LayoutInflater inflater;
    public ImageAdapterMood(Context c,String[] names , int[] imgs)
    {
        super(c,R.layout.imagetext,names);
        mContext = c;
        this.names = names;
        this.imgs = imgs;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.imagetext,null);
        }
        final ViewHolder holder= new ViewHolder();

        holder.imageView = (ImageView) convertView.findViewById(R.id.myimage);
        holder.txtTitle = (TextView)convertView.findViewById(R.id.mytext);

        holder.imageView.setImageResource(imgs[position]);
        holder.txtTitle.setText(names[position]);
        return convertView;//super.getView(position,convertView,parent);
    }


}