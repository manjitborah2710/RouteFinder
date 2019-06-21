package com.task.routefinder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.task.routefinder.R;

public class PopupAdapterMap implements GoogleMap.InfoWindowAdapter {
    Context context;

    public PopupAdapterMap(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View popup=LayoutInflater.from(context).inflate(R.layout.popup_layout,null,false);
        TextView firstLineTV=popup.findViewById(R.id.firstline_tv_popup_layout);

        firstLineTV.setText(marker.getTitle());

        return popup;
    }
}
