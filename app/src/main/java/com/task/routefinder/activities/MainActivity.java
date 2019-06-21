package com.task.routefinder.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonParser;
import com.google.maps.android.SphericalUtil;
import com.task.routefinder.R;
import com.task.routefinder.adapters.PopupAdapterMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener {

    final int SEARCH_CODE=1010;

    private GoogleMap mMap;
    private TextView whereTo,userName;
    SearchView location;
    double slat,slang;
    double tlat,tlang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Id Linkage
        whereTo=findViewById(R.id.where_to_tv_MainActivity);

        userName=findViewById(R.id.user_name_tv_MainActivity);


        //setting click listeners
        whereTo.setOnClickListener(this);


//        location=findViewById(R.id.searchview);
//        location.setVisibility(View.INVISIBLE);
//        location.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                getLocationOnMap(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment_MainActivity);
        mapFragment.getMapAsync(this);

//        double ans = getDistance(24.75,92.789,24.833,92.778);
//        ans = ans/1000;
       // Toast.makeText(MapsActivity.this,""+ans,Toast.LENGTH_LONG).show();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        //get  current location and mark

    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.where_to_tv_MainActivity:{
                Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                startActivityForResult(intent,SEARCH_CODE);
                overridePendingTransition(R.anim.enter_activity_transition,R.anim.exit_activity_transition);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SEARCH_CODE){
            if(resultCode==RESULT_OK){
                String places[]=data.getExtras().getStringArray("result");
                ArrayList<String> placesList=new ArrayList<>();
                for(String i:places){
                    placesList.add(i);
                }
                try{
                    getDistanceMatrix(getLocationOnMap(placesList));
                }
                catch (IOException e){
                    Log.d("mn",e.getStackTrace().toString());
                }
            }
        }
    }

    public Map<String,LatLng> getLocationOnMap(List<String> locations) throws IOException
    {
        mMap.clear();
        Map<String,LatLng> map=new LinkedHashMap<>();
        Geocoder geocoder=new Geocoder(this);
        LatLngBounds.Builder latLangBoundsBuilder = new LatLngBounds.Builder();
        List<Marker> markers=new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            List<Address> addressList= geocoder.getFromLocationName(locations.get(i), 1);
            if (addressList.isEmpty()) continue;
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

            map.put(locations.get(i),latLng);


            String locality="",state="",country="";

//            locality=(address.getLocality()!=null)?address.getLocality()+", ":"";
//            state=(address.getSubLocality()!=null)?address.getSubLocality()+", ":"";
//            country=(address.getSubAdminArea()!=null)?address.getSubAdminArea():"";

            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)).snippet("Hello world").icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            markers.add(marker);

            mMap.setInfoWindowAdapter(new PopupAdapterMap(this));

//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
//        }
        for (Marker marker : markers) {
            latLangBoundsBuilder.include(marker.getPosition());
        }
        LatLngBounds bounds = latLangBoundsBuilder.build();
        int padding=300;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.moveCamera(cu);
        mMap.animateCamera(cu);
        return map;
    }


    public void getDistanceMatrix(Map<String,LatLng> map) {
        String placeNames[]=new String[map.size()];

        String orgs="",dest="";
        Set<Map.Entry<String,LatLng>> set=map.entrySet();
        int setIndex=0;
        for(Map.Entry<String,LatLng> e:set){
            placeNames[setIndex]=e.getKey();
            orgs+=e.getValue().latitude+","+e.getValue().longitude+"|";
            setIndex++;
        }
        orgs=orgs.substring(0,orgs.length()-1);
        dest=new String(orgs);

        //Calculating the distance in meters

        final String url="https://maps.googleapis.com/maps/api/distancematrix/json?mode=driving&units=metric&origins="+orgs+"&destinations="+dest+"&key="+getResources().getString(R.string.dm_api_key);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    List<String> addresses=new ArrayList<>();
                    JSONObject jsonObject=new JSONObject(response);

                    JSONArray addressesJSONArr=jsonObject.getJSONArray("destination_addresses");
                    for(int i=0;i<addressesJSONArr.length();i++){
                        addresses.add(addressesJSONArr.getString(i));
                    }
                    int size=addresses.size();
                    double distanceMatrix[][]=new double[size][size];



                    JSONArray jsonArray=jsonObject.getJSONArray("rows");

                    for(int i=0;i<jsonArray.length();i++){
                        JSONArray array=jsonArray.getJSONObject(i).getJSONArray("elements");
                        for(int j=0;j<array.length();j++){
                            JSONObject distance=array.getJSONObject(j).getJSONObject("distance");
                            JSONObject duration=array.getJSONObject(j).getJSONObject("duration");
//                            Log.d("mn",addresses.get(i)+"->"+addresses.get(j)+" : "+distance.getString("text"));
                            distanceMatrix[i][j]=distance.getDouble("value");
                        }
                    }
//                    for(int i=0;i<size;i++)
//                    {
//                        for(int j=0;j<size;j++)
//                        {
//                            Log.d("mn",distanceMatrix[i][j]+"");
//                        }
//                    }




//                    Log.d("mn",url+"\n"+response);

                } catch (JSONException e) {
                    Log.d("mn","error"+url+"\n"+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("mn",error.toString());
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }


}
