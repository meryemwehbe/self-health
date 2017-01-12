package com.example.self_health.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.example.self_health.R;
import com.example.self_health.other.ButtonAdapter;
import com.example.self_health.other.ImageAdapter;

import org.w3c.dom.Text;

/**
 * Created by pc on 12/28/2016.
 */

public class FoodFragment extends Fragment {
    private Button br,lun,snac,din;
    private  String type;
    private String id;

public static FoodFragment newInstance(){
    return new FoodFragment();
}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food, container, false);
        id = getArguments().getString("ID");

        br =(Button)view.findViewById(R.id.btn_br);
        lun =(Button)view.findViewById(R.id.btn_lunch);
        din =(Button)view.findViewById(R.id.btn_dinner);
        snac =(Button)view.findViewById(R.id.btn_snack);
        br.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("Type", "Breakfast");
                bundle.putString("ID", id);
                Food fragment = new Food();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, (Fragment)fragment);
                fragmentTransaction.commitAllowingStateLoss();

            }
        });
        lun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("Type", "Lunch");
                Food fragment = new Food();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, (Fragment)fragment);
                fragmentTransaction.commitAllowingStateLoss();

            }
        });
        din.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("Type", "Dinner");
                Food fragment = new Food();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, (Fragment)fragment);
                fragmentTransaction.commitAllowingStateLoss();

            }
        });
        snac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("Type", "Snack");
                Food fragment = new Food();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, (Fragment)fragment);
                fragmentTransaction.commitAllowingStateLoss();

            }
        });
        return view;
    }





}
