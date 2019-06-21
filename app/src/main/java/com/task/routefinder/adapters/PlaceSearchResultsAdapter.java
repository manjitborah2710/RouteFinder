package com.task.routefinder.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.task.routefinder.R;
import com.task.routefinder.models.PlaceInfo;

import java.util.ArrayList;
import java.util.List;

public class PlaceSearchResultsAdapter extends ArrayAdapter<PlaceInfo> {

    Context context;
    List<PlaceInfo> data;
    private List<PlaceInfo> data_copy;

    public PlaceSearchResultsAdapter(@NonNull Context context, List<PlaceInfo> data) {
        super(context, R.layout.place_info_layout, data);
        this.context = context;
        this.data = data;
        data_copy = new ArrayList<>(data);
    }

    public void setData(List<PlaceInfo> data){
        this.data=data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.place_info_layout, parent, false);
        TextView fl, sl;
        fl = view.findViewById(R.id.firstline_tv_place_info_layout);
        sl = view.findViewById(R.id.secondline_tv_place_info_layout);
        fl.setText(data.get(position).getFirstLine());
        sl.setText(data.get(position).getSecondLine());
        return view;
    }


    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<PlaceInfo> placeSuggestions = new ArrayList<>();
                if (constraint != null) {
                    for (PlaceInfo placeInfo : data_copy) {
                        if (placeInfo.getFirstLine().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            placeSuggestions.add(placeInfo);
                        }
                    }
                    filterResults.values = placeSuggestions;
                    filterResults.count = placeSuggestions.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data.clear();
                if (results != null && results.count > 0) {
                    // avoids unchecked cast warning when using mDepartments.addAll((ArrayList<Department>) results.values);
                    for (Object object : (List<?>) results.values) {
                        if (object instanceof PlaceInfo) {
                            data.add((PlaceInfo) object);
                        }
                    }
                    notifyDataSetChanged();
                } else if (constraint == null) {
                    // no filter, add entire original list back in
                    data.addAll(data_copy);
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                PlaceInfo placeInfo = (PlaceInfo) resultValue;
                return placeInfo.getFirstLine();
            }
        };
        return filter;
    }
}
