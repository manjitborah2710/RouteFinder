package com.task.routefinder.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.task.routefinder.R;
import com.task.routefinder.adapters.AddLocationRecyclerViewAdapter;
import com.task.routefinder.adapters.PlaceSearchResultsAdapter;
import com.task.routefinder.models.PlaceInfo;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity{

    RecyclerView placesToBeSearched;
    AddLocationRecyclerViewAdapter adapter;
    FloatingActionButton addMoreLocationsBtn,doneBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        addMoreLocationsBtn=findViewById(R.id.add_location_fab_SearchActivity);
        doneBtn=findViewById(R.id.done_fab_SearchActivity);




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
                if(allData.size()>=2) {
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






    }

    public boolean checkForPlaceAvailabiltiy(String place){
        if(place.equalsIgnoreCase("Silchar")){
            return true;
        }
        return false;
    }

}
