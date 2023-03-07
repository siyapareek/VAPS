package com.app.VAPS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;

public class MapsHome extends AppCompatActivity implements OnMapReadyCallback {

    TextInputLayout fromLoc, toLoc;
    GoogleMap map;
    double gpsLatitude = 0.0, gpsLongitude = 0.0;
    String apiKey;
    int fromAUTOCOMPLETE_REQUEST_CODE = 1,toAUTOCOMPLETE_REQUEST_CODE = 2,toIndex = 0,fromIndex = 0,selectedType = 0;;
    RadioButton VitType,InterCityType;
    RadioGroup Type;

    double AB = 0, UB = 0.2, MPH =0.2, ME = 0.15, GH = 0.8, BH1 = 1.4, BH2 = 0.4, BH3 = 0.05; //In Km

    double kmArray[] = {0,0.05,0.15,0.15,0.7,0.7,0.1,0.05};



    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_home);

        fromLoc = findViewById(R.id.from);
        toLoc = findViewById(R.id.to);
        VitType = findViewById(R.id.VitRadio);
        InterCityType = findViewById(R.id.InterCityRadio);
        Type = findViewById(R.id.radioGroup);
        fromLoc.getEditText().setFocusable(false);
        toLoc.getEditText().setFocusable(false);
        VitType.toggle();
        Type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = Type.findViewById(checkedId);
                int index = Type.indexOfChild(radioButton);
                fromLoc.getEditText().setText("");
                toLoc.getEditText().setText("");
                switch (index) {
                    case 0:
                        selectedType = 0;
                        break;
                    case 1:
                        selectedType = 1;
                        break;
                }
            }
        });

        apiKey = "AIzaSyAq-yGPuqrF_Xbmv_NhzDvSnf9HGlUEHnA";

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        fromLoc.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedType == 1) {
                    List<Place.Field> field = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS);
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
                            .build(MapsHome.this);
                    startActivityForResult(intent, fromAUTOCOMPLETE_REQUEST_CODE);
                } else {
                    PopupMenu popup = new PopupMenu(MapsHome.this, fromLoc.getEditText());

                    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(item -> {
                        fromLoc.getEditText().setText(item.getTitle());
                        switch (item.getItemId()) {
                            case R.id.AB:
                                fromIndex= 0;
                                break;
                            case R.id.UB:
                                fromIndex= 1;
                                break;
                            case R.id.MPH:
                                fromIndex= 2;
                                break;
                            case R.id.ME:
                                fromIndex= 3;
                                break;
                            case R.id.GH:
                                fromIndex= 4;
                                break;
                            case R.id.BH1:
                                fromIndex= 5;
                                break;
                            case R.id.BH2:
                                fromIndex= 6;
                                break;
                            case R.id.BH3:
                                fromIndex= 7;
                                break;

                        }
                        return true;
                    });

                }
            }
        });
        toLoc.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedType == 1)
                {
                    List<Place.Field> field = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS);
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
                            .build(MapsHome.this);
                    startActivityForResult(intent, toAUTOCOMPLETE_REQUEST_CODE);
                }
                else
                {
                    PopupMenu popup = new PopupMenu(MapsHome.this, fromLoc.getEditText());

                    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            toLoc.getEditText().setText(item.getTitle());

                            switch (item.getItemId()) {
                                case R.id.AB:
                                    toIndex= 0;
                                    break;
                                case R.id.UB:
                                    toIndex= 1;
                                    break;
                                case R.id.MPH:
                                    toIndex= 2;
                                    break;
                                case R.id.ME:
                                    toIndex= 3;
                                    break;
                                case R.id.GH:
                                    toIndex= 4;
                                    break;
                                case R.id.BH1:
                                    toIndex= 5;
                                    break;
                                case R.id.BH2:
                                    toIndex= 6;
                                    break;
                                case R.id.BH3:
                                    toIndex= 7;

                                    break;
                            }

                            return true;

                        }
                    });
                }

            }
        });

        if (ActivityCompat.checkSelfPermission(MapsHome.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsHome.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsHome.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        else{
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
            mapFragment.getMapAsync(this);
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        LatLng origin = new LatLng(30.7333, 76.7794);
        LatLng dest = new LatLng(30.7333, 76.7794);
        map.addMarker(new MarkerOptions().position(origin).title("From"));
        map.addMarker(new MarkerOptions().position(dest).title("To"));


        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                gpsLatitude = location.getLatitude();
                gpsLongitude = location.getLongitude();
                int i = 0;
                if(i==0) {
                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                    map.moveCamera(center);
                    map.animateCamera(zoom);
                    i++;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == fromAUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                fromLoc.getEditText().setText(place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                System.out.println(status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("User Canceled");
            }
        }

        if (requestCode == toAUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                toLoc.getEditText().setText(place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                System.out.println(status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("User Canceled");
            }
        }
    }

    public void BookButton(View v)
    {
        if(toLoc.getEditText().getText().toString().equals(fromLoc.getEditText().getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"To and From Location Cannot Be Same.",Toast.LENGTH_SHORT).show();
            return;
        }
        //double kmArray[] = {0,0.2,0.2,0.15,0.8,1.4,0.4,0.05};
        double totalKM = 0.0;
        if (fromIndex<=toIndex)
        {
            System.out.println("fromIndex is "+fromIndex);
            System.out.println("toIndex is "+toIndex);
            System.out.println("From Less");
            for (int i = fromIndex+1; i<=toIndex; i++)
            {
                System.out.println("i is "+i);
                totalKM = totalKM + kmArray[i];
            }
        }
        else
        {
            System.out.println("fromIndex is "+fromIndex);
            System.out.println("toIndex is "+toIndex);
            System.out.println("To Less");
            for (int i = fromIndex; i>toIndex; i--)
            {
                System.out.println("i is = "+i);
                totalKM = totalKM + kmArray[i];
            }
        }


        System.out.println("Price is");
        System.out.println(totalKM*15);

        Toast.makeText(getApplicationContext(),"Price is Rs : " + totalKM*15,Toast.LENGTH_SHORT).show();
    }

}