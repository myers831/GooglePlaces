package com.example.admin.googleplaces.mainactivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.googleplaces.R;
import com.example.admin.googleplaces.RecycleViewAdapter;
import com.example.admin.googleplaces.RetrofitHelper;
import com.example.admin.googleplaces.di.mainactivity.DaggerMainActivityComponent;
import com.example.admin.googleplaces.model.PlaceResults;
import com.example.admin.googleplaces.model.Result;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements MainActivityContractor.View {

    private static final String TAG = "Tag";
    int REQUEST_PLACE_PICKER = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 10;
    boolean permission = false;

    Location locationGPS = new Location("GPS");

    TextView tvName, tvAddress;
    RecyclerView rvPlaces;
    RecyclerView.LayoutManager layoutManager;
    RecycleViewAdapter adapter;

    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    PlaceAutocompleteFragment autocompleteFragment;

    Map<String, String> query = new ArrayMap<>();
    List<Result> resultList = new ArrayList<>();

    @Inject
    public MainActivityPresenter presenter;
    public LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        rvPlaces = findViewById(R.id.rvPlaces);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        DaggerMainActivityComponent.create().inject(this);

        presenter.addView(this);

        checkPermission();
        setRecycleView();


        Log.d(TAG, "onCreate: ");
    }

    public void setRecycleView() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "setRecycleView: Location Permission Error");
            return;
        }
        locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        query.put("key","AIzaSyBZjSvIVN5RRtPnASikeTi8lNUiG13u1OE");
        //query.put("types","food");
        query.put("radius","500");
        query.put("location",String.valueOf(locationGPS.getLatitude()) + "," + String.valueOf(locationGPS.getLongitude()));
        //query.put("name", "cruise");

        layoutManager = new LinearLayoutManager(this);
        adapter = new RecycleViewAdapter(resultList);

        rvPlaces.setLayoutManager(layoutManager);
        rvPlaces.setAdapter(adapter);

        RetrofitHelper.getCall(query).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PlaceResults>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: " + d.toString());
                    }

                    @Override
                    public void onNext(PlaceResults placeResults) {
                        for(Result r: placeResults.getResults()){
                            Log.d(TAG, "onNext: " + r.getName());
                            resultList.add(r);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode " + requestCode + " resultCode: " + resultCode + " Activity.RESULT_OK: " + Activity.RESULT_OK);
        onResults(requestCode, resultCode, data);

    }

    @Override
    public void showError(String s) {
        Log.d(TAG, "showError: " + s);
    }

    @Override
    public void setUpAutoComplete() {

        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }

    }

    @Override
    public void onResults(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());

                tvName.setText(place.getName());
                tvAddress.setText(place.getAddress());

                Log.d(TAG, "onActivityResult: ");
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void checkPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Does not have permission");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showRequestRationals();

            } else {
                requestContactsPermission();
            }
        } else {
            //presenter.getLocation();
            permission = true;
            Log.d(TAG, "onCreate: Permisson is already granted");

        }
    }

    private void showRequestRationals() {
        Log.d(TAG, "onCreate: Should show rationale");

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Explination")
                .setMessage("Please allow this Permission to read contacts")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestContactsPermission();
                    }
                }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Tink Again", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void requestContactsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_READ_LOCATION);
        Log.d(TAG, "onCreate: Requesting permission");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permission = true;
                    Log.d(TAG, "onRequestPermissionsResult: Permisson granted");

                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Permission denied");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void search(View view) {
        setUpAutoComplete();
    }
}
