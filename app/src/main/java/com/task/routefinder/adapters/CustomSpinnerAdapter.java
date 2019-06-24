package com.task.routefinder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.task.routefinder.R;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    Context context;
    List<String> data;
    public CustomSpinnerAdapter(@NonNull Context context,List<String> data) {
        super(context, R.layout.spinner_dropdown_layout);
        this.context=context;
        this.data=data;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view= LayoutInflater.from(context).inflate(R.layout.spinner_dropdown_layout,null,false);
        TextView textView=view.findViewById(R.id.tv_inside_spinner);
        textView.setText(data.get(position));
        return view;
    }
}
