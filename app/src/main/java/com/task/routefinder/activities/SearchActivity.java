package com.task.routefinder.activities;

import android.Manifest;
import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RadioButton;
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
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements LocationListener{

    RecyclerView placesToBeSearched;
    AddLocationRecyclerViewAdapter adapter;
    FloatingActionButton addMoreLocationsBtn,doneBtn;
    AutoCompleteTextView sourceEt;
    LocationManager locationManager;
    RadioButton drivingRB,walkingRB,cyclingRB;

    ImageButton chooseCurrentLocation,cancelSourceBtn;
    String prevLocs[];
    int invalidLocsPos[];
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        activity=this;


        addMoreLocationsBtn=findViewById(R.id.add_location_fab_SearchActivity);
        doneBtn=findViewById(R.id.done_fab_SearchActivity);
        sourceEt=findViewById(R.id.source_et_SearchActivity);
        chooseCurrentLocation=findViewById(R.id.currentlocation_source_btn_SearchActivity);
        cancelSourceBtn=findViewById(R.id.cancel_source_btn_SearchActivity);


        placesToBeSearched=findViewById(R.id.location_list_rv_SearchActivity);
        placesToBeSearched.setLayoutManager(new LinearLayoutManager(this));

        drivingRB=findViewById(R.id.driving_rb_SearchActivity);
        walkingRB=findViewById(R.id.walking_rb_SearchActivity);
        cyclingRB=findViewById(R.id.cycling_rb_SearchActivity);



        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            prevLocs=bundle.getStringArray("locations");
            invalidLocsPos=bundle.getIntArray("invalidLocs");
        }

        final ArrayList<String> list=new ArrayList<>();
        list.add("");
        if(prevLocs!=null){
            if(prevLocs.length>0) {
                list.clear();
                sourceEt.setText(prevLocs[0]);
                for (int i = 1; i < prevLocs.length; i++) {
                    list.add(prevLocs[i]);
                }
            }
        }
        if(invalidLocsPos!=null){
            for(int i=0;i<invalidLocsPos.length;i++){
                if(invalidLocsPos[i]==0){
                    sourceEt.setError("Invalid source");
                    break;
                }
            }
            for(int i=0;i<invalidLocsPos.length;i++){
                invalidLocsPos[i]--;
            }

        }

        adapter=new AddLocationRecyclerViewAdapter(this,list,invalidLocsPos);

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
                hideKeyboard(activity);

                List<String> allData = adapter.getAllData();
                if(sourceEt.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Enter a source",Toast.LENGTH_SHORT).show();
                    return;
                }
                allData.add(0,sourceEt.getText().toString());
//                Log.d("mn",allData.toString()+"\n"+allData.isEmpty());
                if(allData.size()>=2) {
                    Intent intent = new Intent();
                    intent.putExtra("result", allData.toArray(new String[adapter.getAllData().size()]));
                    String mode="";
                    if(drivingRB.isChecked()){
                        mode="driving";
                    }
                    else if(cyclingRB.isChecked()){
                        mode="bicycling";
                    }
                    else if(walkingRB.isChecked()){
                        mode="walking";
                    }

                    intent.putExtra("mode",mode);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Minimum 2 places required",Toast.LENGTH_SHORT).show();
                }
//                Log.d("mn",adapter.getAllData().toString());
            }
        });

        chooseCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
                getCurrentLocation();
//                if(!currentLocString.trim().equals("")){
//                    sourceEt.setText(currentLocString);
//                }
            }
        });

        cancelSourceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourceEt.setText("");
            }
        });





    }


    String currentLocString="";

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
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocString="";
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        Log.d("mn",latLng.toString());
        Geocoder geocoder=new Geocoder(this);
        try{
            List<Address> addresses=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            currentLocString=addresses.get(0).getAddressLine(0);
            sourceEt.setText(currentLocString);
            if(!sourceEt.getText().toString().trim().isEmpty())
            {
                locationManager.removeUpdates(this);
                locationManager=null;
            }

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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}