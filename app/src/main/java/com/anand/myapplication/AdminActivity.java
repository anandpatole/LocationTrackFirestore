package com.anand.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminActivity extends AppCompatActivity implements OnMapReadyCallback
{
    RecyclerView recyclerView;
    FirebaseFirestore db;
    private GoogleMap mMap;
    ArrayList<HashMap<String,String>> maplist;
    Button refresh;
    Polyline line;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);
        //recyclerView=findViewById(R.id.recycler_view);
        db = FirebaseFirestore.getInstance();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        refresh=findViewById(R.id.refresh);
//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getData();
//            }
//        });
        getData();
    }

    public void getData() {


        db.collection("GPS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                           maplist =new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HashMap<String,String> map=new HashMap<>();
                                map.put("LAT",document.getData().get("LAT").toString());
                                map.put("LANG", document.getData().get("LANG").toString());

                                maplist.add(map);
                                //Log.d("data", document.getId() + " => " + document.getData());

                            }
                            Log.d("data",   maplist.toString());
                            drawOnMap();
                            continousFireStoreUpdate();
                          //  setRecyclerData(maplist);
                        } else {
                            Log.d("data", "Error getting documents: ", task.getException());
                        }
                    }
                });

       // DocumentReference user = db.collection("GPS").document("COORDINATES1");
//        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()){
//                    DocumentSnapshot doc = task.getResult();
//                    Log.d("data", String.valueOf(doc.getData()));
//                  //  TaskItem taskItem = DocumentSnapshot.toObject(TaskItem.class);
////                    for (TaskItem a : list) {
////
////                        list.add(taskItem);
////                    }
//
//
//
//                }
//            }
//        })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });


    }

    private void continousFireStoreUpdate()
    {
        db.collection("GPS")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w("YourTag", "Listen failed.", e);
                            return;
                        }
                        maplist =new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots)
                        {
                            if (doc.exists())
                            {
                                HashMap<String,String> map=new HashMap<>();
                                map.put("LAT",doc.getData().get("LAT").toString());
                                map.put("LANG", doc.getData().get("LANG").toString());

                                maplist.add(map);
                            }
                        }
                       drawOnMap();
                    }
                });
    }

//    private void setRecyclerData(ArrayList<HashMap<String,String>> list)
//
//    {
//        RecyclerAdpater adapter =new RecyclerAdpater(AdminActivity.this,list);
//        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(adapter);
//
//    }

    public void drawOnMap()
    {
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            public void onMapLoaded() {
                PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                mMap.clear();
                for (int z = 0; z < maplist.size(); z++)
                {

                    double latitude = Double.parseDouble(maplist.get(z).get("LAT"));
                    double longitude = Double.parseDouble(maplist.get(z).get("LANG"));
                    LatLng point =new LatLng(latitude,longitude);
                    if(z==0)
                    {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(point);
                        markerOptions.title("Start Point");
                        mMap.addMarker(markerOptions);
                    }
                    if(z==(maplist.size()-1))
                    {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(point);
                        markerOptions.title("End Point");
                        mMap.addMarker(markerOptions);
                    }
                    options.add(point);
                    if(z==0)
                    {
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(point, 35);
                        mMap.animateCamera(cameraUpdate);
                    }
                }

                if(line!=null)
                {
                    line.remove();
                }

               line = mMap.addPolyline(options);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


//        mMap.addMarker(new MarkerOptions().position(sydney).title("Kathmandu, Nepal"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
}
