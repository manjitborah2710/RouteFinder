package com.task.routefinder.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
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
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.task.routefinder.R;
import com.task.routefinder.adapters.CustomSpinnerAdapter;
import com.task.routefinder.adapters.PopupAdapterMap;
import com.task.routefinder.algorithms.TSPGetAllPathClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener, LocationListener {

    final int SEARCH_CODE=1010;
    final int padding=250;

    private GoogleMap mMap;
    private TextView whereTo,userName;
    SearchView location;
    double slat,slang;
    double tlat,tlang;
    Map<String,LatLng> placesWithLatLng;
    RelativeLayout displayTotalDistance;
    TextView totalDistanceTV;
    ViewStub mapToolbarViewstub;

    String directionMode;
    Map<String,Address> allAddresses;
    final int markerDrawables[]=new int[]{R.drawable.marker1,R.drawable.marker2,R.drawable.marker3,R.drawable.marker4,R.drawable.marker5,R.drawable.marker6};

    List<String> validPlaces;
    List<String> placesList;
    List<Integer> invalidPlacesPositions;
    List<String> invalidPlacesNames;

    AlertDialog alertDialog;
    CameraUpdate cu;
    String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Id Linkage
        displayTotalDistance=findViewById(R.id.total_distance_display_relative_layout);
        displayTotalDistance.setVisibility(View.GONE);




        whereTo=findViewById(R.id.where_to_tv_MainActivity);


        userName=findViewById(R.id.user_name_tv_MainActivity);
        totalDistanceTV=findViewById(R.id.total_distance_tv);


        //setting click listeners
        whereTo.setOnClickListener(this);
        validPlaces=new ArrayList<>();
        invalidPlacesPositions=new ArrayList<>();
        invalidPlacesNames=new ArrayList<>();





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
    ImageButton saveSS;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mapToolbarViewstub=findViewById(R.id.map_toolbar_viewstub);
        mapToolbarViewstub.setLayoutResource(R.layout.map_toolbar);
        View view=mapToolbarViewstub.inflate();
        ImageButton clear=view.findViewById(R.id.clear_all_btn_map_toolbar);
        clear.setOnClickListener(this);
        ImageButton reposition=view.findViewById(R.id.reposition_btn_map_toolbar);
        reposition.setOnClickListener(this);
        ImageButton editSource=view.findViewById(R.id.change_source_btn_map_toolbar);
        editSource.setOnClickListener(this);
        saveSS=view.findViewById(R.id.save_screenshot);
        saveSS.setOnClickListener(this);



    }
    public void onClickActivity(){
        Intent intent=new Intent(MainActivity.this,SearchActivity.class);
        if(placesList!=null){
            intent.putExtra("locations",placesList.toArray(new String[placesList.size()]));
        }
        //Log.d("man",invalidPlacesPositions.size()+"");
        if(!invalidPlacesPositions.isEmpty()){
            int a[]=new int[invalidPlacesPositions.size()];
            for(int i=0;i<a.length;i++){
                a[i]=invalidPlacesPositions.get(i);
            }
            intent.putExtra("invalidLocs",a);
        }
        startActivityForResult(intent,SEARCH_CODE);
        overridePendingTransition(R.anim.enter_activity_transition,R.anim.exit_activity_transition);
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.where_to_tv_MainActivity:{
                onClickActivity();
                break;
            }
            case R.id.clear_all_btn_map_toolbar:{
                clearAll();
                break;
            }
            case R.id.reposition_btn_map_toolbar:{
                reposition();
                break;
            }
            case R.id.change_source_btn_map_toolbar:{
                changeSourceHelperMethod();
                break;
            }
            case R.id.save_screenshot:{
                if(isWriteStoragePermissionGranted()) {
                    saveScreenShot();
                }
                break;
            }
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> writePerm=new ArrayList<>();
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            else {
                writePerm.clear();
                writePerm.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                ActivityCompat.requestPermissions(this, writePerm.toArray(new String[writePerm.size()]), 1002);
                return false;
            }
        }
        else {
            return true;
        }
    }

    public void saveScreenShot() {
        if(totalDistanceTV.getText()==null) {
            Toast.makeText(getApplicationContext(),"Nothing to save",Toast.LENGTH_SHORT).show();
            return;
        }
        if(totalDistanceTV.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(),"Nothing to save",Toast.LENGTH_SHORT).show();
            return;
        }
        if(placesList==null) {
            Toast.makeText(getApplicationContext(),"Nothing to save",Toast.LENGTH_SHORT).show();
            return;
        }
        if(validPlaces.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Nothing to save",Toast.LENGTH_SHORT).show();
            return;
        }
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                writeToPdf(bitmap);
            }
        };
        mMap.snapshot(callback);

    }

    public void writeToPdf(Bitmap bitmap){

        Font font20=new Font(Font.FontFamily.TIMES_ROMAN,20);
        Font font24Bold=new Font(Font.FontFamily.TIMES_ROMAN,24,Font.BOLD);
        Font font18Bold=new Font(Font.FontFamily.TIMES_ROMAN,18,Font.BOLD);


        String order="";
        for(int i=0;i<nodesSequence.length;i++){
            int index=Integer.parseInt(nodesSequence[i]);
            order+=(i+1)+" -> "+placesList.get(index)+"\n";
        }

        Paragraph orderDisplayPara=new Paragraph("The order is -"+"\n"+order+"\n",font18Bold);


        String distance=totalDistanceTV.getText().toString();
        Paragraph distanceDisplayPara=new Paragraph("Total distance = "+distance+"\n\n",font18Bold);


        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-yyy E hh:mm:ss a \n(z)");

        String date=simpleDateFormat.format(calendar.getTime());

        //headings
        Paragraph heading1=new Paragraph("Route Finder\n",font24Bold);
        heading1.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph heading2=new Paragraph(date+"\n\n",font20);
        heading2.setAlignment(Paragraph.ALIGN_CENTER);

        Document document=new Document();



        File file = Environment.getExternalStorageDirectory();
        File newFile = new File(file.getAbsolutePath() + "/Route Finder");
        if (!newFile.exists()) {
            if (!newFile.mkdir()) {
                Toast.makeText(getApplicationContext(), "returned", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMM-yyyy");
        String now=dateFormat.format(Calendar.getInstance().getTime());
        String fileNameSuffix="Route_Finder"+"_"+now+"_"+Calendar.getInstance().getTimeInMillis()+".pdf";
        File filePath = new File(newFile + "/"+fileNameSuffix);
        try {

            FileOutputStream fout = new FileOutputStream(filePath);
            PdfWriter pdfWriter=PdfWriter.getInstance(document,fout);

            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
            Image image=Image.getInstance(byteArrayOutputStream.toByteArray());
            image.scaleToFit(0.95f*(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()),0.95f*(document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin()));
            image.setAlignment(Image.ALIGN_CENTER);




            document.open();

            document.add(heading1);
            document.add(heading2);



            document.add(image);
            document.newPage();
//            document.add(new Paragraph("\n\n\n"));

            document.add(orderDisplayPara);
            document.add(distanceDisplayPara);


            document.close();
            pdfWriter.close();




            Toast.makeText(getApplicationContext(), "PDF saved to /Route Finder", Toast.LENGTH_SHORT).show();
            fout.flush();
            fout.close();


        }
        catch (FileNotFoundException e) {

        }
        catch (IOException e) {

        }
        catch (DocumentException e){

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SEARCH_CODE){
            if(resultCode==RESULT_OK){
                String places[]=data.getExtras().getStringArray("result");
                mode=data.getExtras().getString("mode");
                placesList=new ArrayList<>();
                for(String i:places){
                    placesList.add(i);
                }
                try{
                    placesWithLatLng=new LinkedHashMap<>();
                    placesWithLatLng=getLocationOnMap(placesList);
                    //Toast.makeText(getApplicationContext(),""+invalidPlacesNames.isEmpty(),Toast.LENGTH_SHORT).show();
                    if(placesWithLatLng!=null){
                        getDistanceMatrix(placesWithLatLng,mode);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"None of the places you entered are valid",Toast.LENGTH_SHORT).show();
                    }
                    if (!invalidPlacesNames.isEmpty()) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setCancelable(false);
                        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.invalid_places_dialog_layout, null, false);

                        TextView names;
                        TextView ignore,reenter;
                        String nameString="";
                        names = v.findViewById(R.id.names_list_tv);
                        ignore=v.findViewById(R.id.ignore);
                        reenter=v.findViewById(R.id.reenter);
                        for (int i=0;i<invalidPlacesNames.size();i++){
                            nameString = nameString+invalidPlacesNames.get(i) + "\n";
                        }
                        names.setText(nameString.trim());
                        builder.setView(v);

                        ignore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                alertDialog.cancel();
                                placesList=new ArrayList<>(validPlaces);
                                invalidPlacesNames.clear();
                                invalidPlacesPositions.clear();
                            }
                        });
                        reenter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onClickActivity();
                                alertDialog.cancel();
                                alertDialog=null;
                            }
                        });

                        alertDialog = builder.create();
                        alertDialog.show();
                    }

                }
                catch (IOException e){
                    Log.d("mn",e.getStackTrace().toString());
                }
            }
        }
    }

    public Map<String,LatLng> getLocationOnMap(List<String> locations) throws IOException {
        mMap.clear();
        allAddresses=new LinkedHashMap<>();
        Map<String,LatLng> map=new LinkedHashMap<>();
        Geocoder geocoder=new Geocoder(this);
        LatLngBounds.Builder latLangBoundsBuilder = new LatLngBounds.Builder();
        List<Marker> markers=new ArrayList<>();

        validPlaces.clear();
        invalidPlacesNames.clear();
        invalidPlacesPositions.clear();

        for (int i = 0; i < locations.size(); i++) {
            List<Address> addressList= geocoder.getFromLocationName(locations.get(i), 1);
            if (addressList.isEmpty()) {
                invalidPlacesNames.add(locations.get(i));
                invalidPlacesPositions.add(i);
                continue;
            }

            validPlaces.add(locations.get(i));

            Address address = addressList.get(0);
            allAddresses.put(locations.get(i),address);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

            map.put(locations.get(i),latLng);



            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)).icon(BitmapDescriptorFactory.fromResource(R.drawable.default_marker)));
            markers.add(marker);

            mMap.setInfoWindowAdapter(new PopupAdapterMap(this));
        }
        if(!validPlaces.isEmpty()){
            for (Marker marker : markers) {
                latLangBoundsBuilder.include(marker.getPosition());
            }
            LatLngBounds bounds = latLangBoundsBuilder.build();

            cu=null;
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

//            mMap.moveCamera(cu);
            mMap.animateCamera(cu);
            return map;
        }
        return null;

    }


    public void getDistanceMatrix(Map<String,LatLng> map,final String mode) {
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

        final String url="https://maps.googleapis.com/maps/api/distancematrix/json?mode="+mode+"&units=metric&origins="+orgs+"&destinations="+dest+"&key="+getResources().getString(R.string.dm_api_key);
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
                            distanceMatrix[i][j]=distance.getDouble("value");
                        }
                    }
                    TSPGetAllPathClass getAllPathClass=new TSPGetAllPathClass(distanceMatrix);
                    String resultStr=getAllPathClass.getMinimizedPath(0);
                    //Toast.makeText(getApplicationContext(),resultStr,Toast.LENGTH_LONG).show();
                    Log.d("mn",resultStr);
                    drawPaths(resultStr,mode);


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

    String nodesSequence[];
    private void drawPaths(String result,String mode)
    {
        mMap.clear();

        String[] shortestPathList=result.trim().split(":");
        String distance=shortestPathList[1];
        String[] nodes=shortestPathList[0].split(" ");
        nodesSequence=nodes.clone();

        Log.d("mn","PathMin- "+distance);
        Log.d("mn",""+Arrays.toString(nodes));
        if(!validPlaces.isEmpty())
        {
            List<Marker> newMarkers=new ArrayList<>();
            LatLngBounds.Builder latLangBoundsBuilderNew = new LatLngBounds.Builder();
            for(int i=0;i<nodes.length;i++)
            {
                int index=Integer.parseInt(nodes[i]);
                Marker marker = mMap.addMarker(new MarkerOptions().position(placesWithLatLng.get(validPlaces.get(index))).title(allAddresses.get(validPlaces.get(index)).getAddressLine(0)).icon(BitmapDescriptorFactory.fromResource(markerDrawables[i])));
                newMarkers.add(marker);

                Log.d("mn","Node no - "+nodes[i] +"  PlaceName - "+validPlaces.get(index)+"  latlang - "+placesWithLatLng.get(validPlaces.get(index)));
            }
            mMap.setInfoWindowAdapter(new PopupAdapterMap(this));
            for (Marker marker : newMarkers) {
                latLangBoundsBuilderNew.include(marker.getPosition());
            }
            LatLngBounds bounds = latLangBoundsBuilderNew.build();
            cu=null;
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

//            mMap.moveCamera(cu);
            mMap.animateCamera(cu);
            for(int i=0;i<nodes.length - 1;i++)
            {
                int index=Integer.parseInt(nodes[i]);
                int indexTwo=Integer.parseInt(nodes[i+1]);
                Log.d("mn",validPlaces.get(index)+" "+validPlaces.get(indexTwo));
                apiCallDrawroutes(placesWithLatLng.get(validPlaces.get(index)),placesWithLatLng.get(validPlaces.get(indexTwo)),mode,distance);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Select Your locations",Toast.LENGTH_LONG).show();
        }

    }

    public void apiCallDrawroutes(LatLng source,LatLng dest,final String directionMode,final String distance)
    {


        String url="https://maps.googleapis.com/maps/api/directions/json?mode="+directionMode+"&origin="+source.latitude+","+source.longitude+"&destination="+dest.latitude+","+dest.longitude+"&key="+getResources().getString(R.string.dm_api_key);

        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                List<List<HashMap<String, String>>> routes = new ArrayList<>();

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray routesArr=jsonObject.getJSONArray("routes");
                    for(int i=0;i<routesArr.length();i++)
                    {
                        List path = new ArrayList<>();
                        jsonObject=routesArr.getJSONObject(i);
                        JSONArray legs=jsonObject.getJSONArray("legs");
                       // Log.d("mn",legs.toString());
                        for(int j=0;j<legs.length();j++)
                        {
                            JSONArray steps=legs.getJSONObject(j).getJSONArray("steps");
                            //Log.d("mn","Points "+steps.toString());
                            for(int k=0;k<steps.length();k++)
                            {
                                String object=steps.getJSONObject(k).getJSONObject("polyline").getString("points");
                                //Log.d("mn","Points "+object.toString());
                                List<LatLng> list = PolyUtil.decode(object);;

                                /** Traversing all points */
                                for (int l = 0; l < list.size(); l++) {
                                    HashMap<String, String> hm = new HashMap<>();
                                    hm.put("lat", Double.toString((list.get(l)).latitude));
                                    hm.put("lng", Double.toString((list.get(l)).longitude));
                                    path.add(hm);
                                }

                            }

                            routes.add(path);
                            //Log.d("mn",routes.size()+"");
                        }
                    }


                    ArrayList<LatLng> points;
                    PolylineOptions lineOptions = null;

                    for (int i = 0; i < routes.size(); i++) {
                        points = new ArrayList<>();
                        lineOptions = new PolylineOptions();
                        // Fetching i-th route
                        List<HashMap<String, String>> path = routes.get(i);
                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);
                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);
                            points.add(position);
                        }
                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);

                        if (directionMode.equalsIgnoreCase("walking")) {
                            lineOptions.width(10);
                            lineOptions.color(Color.BLUE);
                            List<PatternItem> patternItems=new ArrayList<>();
                            patternItems.add(new Dot());
                            lineOptions.pattern(patternItems);
                            mMap.addPolyline(lineOptions);
                        } else{
                            lineOptions.width(10);
                            lineOptions.color(Color.BLUE);
                            mMap.addPolyline(lineOptions);
                        }
                        totalDistanceTV.setText(distance+" km");
                        displayTotalDistance.setVisibility(View.VISIBLE);
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    void clearAll(){
        mMap.clear();
        placesList=null;
        invalidPlacesNames.clear();
        validPlaces.clear();
        invalidPlacesPositions.clear();
        totalDistanceTV.setText("");
        displayTotalDistance.setVisibility(View.GONE);
        cu=null;

    }
    void reposition(){
        if(cu!=null){
//            mMap.moveCamera(cu);
            mMap.animateCamera(cu);
        }
        else{
            getCurrentLocation();
        }

    }










    LocationManager locationManager;
    private void getCurrentLocation(){
        locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> permissions=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
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
        String currentLocString="";
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        Log.d("mn",latLng.toString());
        Geocoder geocoder=new Geocoder(this);
        try{
            List<Address> addresses=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            currentLocString=addresses.get(0).getAddressLine(0);

            mMap.addMarker(new MarkerOptions().position(latLng).title(currentLocString).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.setInfoWindowAdapter(new PopupAdapterMap(this));

//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,12.0f));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12.0f));


            locationManager.removeUpdates(this);
            locationManager=null;

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1001 && grantResults.length!=0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
        if(requestCode==1002 && grantResults.length!=0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                saveScreenShot();
            }
            else{
                Toast.makeText(getApplicationContext(),"Could not get the permissions required",Toast.LENGTH_SHORT).show();
            }
        }
    }

    int pos=0;
    void changeSourceHelperMethod(){
        if(placesList!=null){
            View view=LayoutInflater.from(this).inflate(R.layout.change_source_layout,null,false);
            Spinner spinner=view.findViewById(R.id.change_source_spinner);
            ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,placesList);
            spinner.setAdapter(adapter);
            AlertDialog.Builder builder=new AlertDialog.Builder(this).setCancelable(false)
                    .setView(view);
            pos=0;
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    pos=position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Collections.swap(placesList,0,pos);
                    placesWithLatLng=new LinkedHashMap<>();
                    try {

                        placesWithLatLng = getLocationOnMap(placesList);
                        //Toast.makeText(getApplicationContext(),""+invalidPlacesNames.isEmpty(),Toast.LENGTH_SHORT).show();
                        if (placesWithLatLng != null) {
                            getDistanceMatrix(placesWithLatLng, mode);
                        } else {
                            Toast.makeText(getApplicationContext(), "None of the places you entered are valid", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (IOException e){

                    }
                    dialog.dismiss();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog changeSourceDialog=builder.create();
            changeSourceDialog.show();
        }
        else{
            Toast.makeText(getApplicationContext(),"You have not entered any locations..click on where to?",Toast.LENGTH_SHORT).show();
        }
    }

}
