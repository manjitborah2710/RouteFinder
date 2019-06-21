package com.task.routefinder.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.task.routefinder.R;
import com.task.routefinder.models.PlaceInfo;

import java.util.ArrayList;
import java.util.List;

public class AddLocationRecyclerViewAdapter extends RecyclerView.Adapter {
    Context context;
    List<String> data;
    List<AutoCompleteTextView> places;


    public AddLocationRecyclerViewAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
        places=new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.search_location_items_rv_search_activity,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder myViewHolder=(MyViewHolder)holder;
        myViewHolder.searchText.setHint("Enter a location");
        myViewHolder.searchText.setAdapter(new PlaceSearchResultsAdapter(context,new ArrayList<PlaceInfo>()));
        myViewHolder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.size()>1){
                    myViewHolder.searchText.setText("");
                    places.remove(myViewHolder.getAdapterPosition());
                    data.remove(myViewHolder.getAdapterPosition());
                    notifyItemRemoved(myViewHolder.getAdapterPosition());

                }
            }
        });

        places.add(myViewHolder.searchText);

        List<PlaceInfo> placeInfos=new ArrayList<>();
        placeInfos.add(new PlaceInfo("Guwahati","Assam, India"));
        placeInfos.add(new PlaceInfo("Silchar","Assam, India"));
        placeInfos.add(new PlaceInfo("Lumding","Assam, India"));
        placeInfos.add(new PlaceInfo("Haflong","Assam, India"));
        placeInfos.add(new PlaceInfo("Shillong","Assam, India"));
        PlaceSearchResultsAdapter adapter=new PlaceSearchResultsAdapter(context,placeInfos);
        myViewHolder.searchText.setAdapter(adapter);

//        List<PlaceInfo> placeInfos=new ArrayList<>();
//        placeInfos.add(new PlaceInfo("India","NIT"));
//        placeInfos.add(new PlaceInfo("Indonesia","Sydney"));
//        final PlaceSearchResultsAdapter placeSearchResultsAdapter=new PlaceSearchResultsAdapter(context,placeInfos);


        myViewHolder.searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Log.e("mn","before "+s+"-"+start+"-"+count+"-"+after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

//                getCompletions(s.toString(),myViewHolder.searchText);
//                Log.e("mn","on "+s+"-"+start+"-"+count);
            }

            @Override
            public void afterTextChanged(Editable s) {

//                getCompletions(s.toString(),myViewHolder.searchText);
//                ArrayAdapter arrayAdapter=new ArrayAdapter(context,android.R.layout.simple_list_item_1,new String[]{"Australia","New Zealand","Austria","India","Afghanisthan","Pakistan"});
//                myViewHolder.searchText.setAdapter(arrayAdapter);
//                Log.e("mn","after "+s);

            }
        });







    }

    PlacesClient placesClient;

    private void getCompletions(String s, final AutoCompleteTextView textView){
        if(s.equals("")){
            return;
        }
        Places.initialize(context.getApplicationContext(), context.getResources().getString(R.string.api_key));

        placesClient = Places.createClient(context);

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(23.63936, 68.14712),
                new LatLng(28.20453, 97.34466));

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                .setCountry("in")
                .setSessionToken(token)
                .setQuery(s)
                .build();

        final ArrayList<PlaceInfo> searchResults=new ArrayList<>();
        placesClient.findAutocompletePredictions(request).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onSuccess(FindAutocompletePredictionsResponse response) {
                searchResults.clear();
                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                    searchResults.add(new PlaceInfo(prediction.b(),prediction.c()));
                }
//                Log.d("mn",searchResults.toString());
//                PlaceSearchResultsAdapter placeSearchResultsAdapter=new PlaceSearchResultsAdapter(context,searchResults);
//                textView.setAdapter(placeSearchResultsAdapter);
                ((PlaceSearchResultsAdapter)textView.getAdapter()).setData(searchResults);
                ((PlaceSearchResultsAdapter)textView.getAdapter()).notifyDataSetChanged();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("mn","new error "+e.getLocalizedMessage());
            }
        });
//        List<PlaceInfo> placeInfos=new ArrayList<>();
//        placeInfos.add(new PlaceInfo("India","NIT"));
//        placeInfos.add(new PlaceInfo("Indonesia","Sydney"));
//        PlaceSearchResultsAdapter placeSearchResultsAdapter=new PlaceSearchResultsAdapter(context,placeInfos);
//        textView.setAdapter(placeSearchResultsAdapter);



    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<String> getAllData(){
        ArrayList<String> ps=new ArrayList<>();
        for(int i=0;i<data.size();i++){
            String temp = places.get(i).getText().toString();
            if(!temp.trim().equals("")) {
                ps.add(temp);
            }

        }
        return ps;
    }




}
class MyViewHolder extends RecyclerView.ViewHolder{
    public ImageButton cancelBtn;
    public AutoCompleteTextView searchText;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        cancelBtn=itemView.findViewById(R.id.cancel_location_btn_rv_layout);
        searchText=itemView.findViewById(R.id.location_et_rv_layout);

    }

}

