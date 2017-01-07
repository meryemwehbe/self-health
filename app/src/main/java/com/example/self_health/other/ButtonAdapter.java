package com.example.self_health.other;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.self_health.R;
import com.example.self_health.fragment.Food;

/**
 * Created by pc on 11/20/2016.
 */

public class ButtonAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private LayoutInflater inflater;
    private int width;
    private int height;
    private int[] imgs = {};
    private String[] names ={};
    public ButtonAdapter(Context c , String[] names, int[] imgs,int width,int height) {
        super(c,R.layout.imagetext,names);

        mContext = c;
        this.names = names;
        this.imgs = imgs;
        this.width = width;
        this.height = height;
    }



    public long getItemId(int position) {
        return 0;
    }
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {


        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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
            convertView = inflater.inflate(R.layout.imagetext2,null);
        }
         ViewHolder holder =new ViewHolder();

        holder.imageView = (ImageView) convertView.findViewById(R.id.myimage);
        holder.txtTitle = (TextView)convertView.findViewById(R.id.mytext);


        holder.imageView .setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        holder.imageView .setPadding(8, 8, 8, 8);
        BitmapDrawable bdrawable = new BitmapDrawable(getContext().getResources(),decodeSampledBitmapFromResource(mContext.getResources(),imgs[position],50,50));
        holder.imageView .setBackgroundResource(R.drawable.buttonshape);
        //holder.imageView.setImageResource(imgs[position]);
        holder.txtTitle.setText(names[position]);
        holder.txtTitle.setTextColor(Color.WHITE);
        holder.txtTitle.setTextSize(25);
        holder.txtTitle.setShadowLayer(1,0,0,R.color.space);

        return  convertView;//super.getView(position,convertView,parent);
    }

}