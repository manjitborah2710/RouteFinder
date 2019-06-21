package com.task.routefinder.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.task.routefinder.R;
import com.task.routefinder.adapters.AddLocationRecyclerViewAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements LocationListener{

    RecyclerView placesToBeSearched;
    AddLocationRecyclerViewAdapter adapter;
    FloatingActionButton addMoreLocationsBtn,doneBtn;
    AutoCompleteTextView sourceEt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        addMoreLocationsBtn=findViewById(R.id.add_location_fab_SearchActivity);
        doneBtn=findViewById(R.id.done_fab_SearchActivity);
        sourceEt=findViewById(R.id.source_et_SearchActivity);




        placesToBeSearched=findViewById(R.id.location_list_rv_SearchActivity);
        placesToBeSearched.setLayoutManager(new LinearLayoutManager(this));

        final ArrayList<String> list=new ArrayList<>();
        list.add("");





        adapter=new AddLocationRecyclerViewAdapter(this,list);

        placesToBeSearched.setAdapter(adapter);

        addMoreLocationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getItemCount()<5){
                    list.add("");
                    adapter.notifyItemInserted(adapter.getItemCount()-1);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Maximum 5 locations allowed",Toast.LENGTH_SHORT).show();
                }
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> allData = adapter.getAllData();
//                Log.d("mn",allData.toString()+"\n"+allData.isEmpty());
                if(allData.size()>=1) {
                    Intent intent = new Intent();
                    intent.putExtra("result", allData.toArray(new String[adapter.getAllData().size()]));
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Minimum 2 places required",Toast.LENGTH_SHORT).show();
                }
//                Log.d("mn",adapter.getAllData().toString());
            }
        });

        getCurrentLocation();



    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1001 && grantResults.length!=0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }

    private void getCurrentLocation(){
        List<String> permissions=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissions.clear();
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(!permissions.isEmpty()){
            ActivityCompat.requestPermissions(this,permissions.toArray(new String[permissions.size()]),1001);
        }
        else{
            LocationManager locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,100000000,0,this);
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("mn",location.toString());
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        Geocoder geocoder=new Geocoder(this);
        try{
            List<Address> addresses=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            sourceEt.setText(addresses.get(0).getAddressLine(0));
        }
        catch (IOException e){
            Log.d("mn","io "+e.getMessage());
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}