package com.masaworld.catmap.ui.activity;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.masaworld.catmap.R;
import com.masaworld.catmap.data.model.Cat;
import com.masaworld.catmap.service.CatPostService;
import com.masaworld.catmap.ui.fragment.LoginCheckDialogFragment;
import com.masaworld.catmap.viewmodel.CatMapViewModel;
import com.masaworld.catmap.viewmodel.ViewEvent;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, LoginCheckDialogFragment.LoginCheckCallback {

    private static final int PERMISSION_REQUEST_LOCATION =1458;
    private GoogleMap mMap;
    private FusedLocationProviderClient locationProviderClient;
    private CatMapViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setUpViewModel();
        setUpBroadcastReceiver();
        FloatingActionButton fab = findViewById(R.id.maps_location_button);
        fab.setOnClickListener(view -> viewModel.moveToCurrentLocation());
    }

    private void setUpViewModel() {
        viewModel = ViewModelProviders.of(this).get(CatMapViewModel.class);
        viewModel.getToastEvent().observe(this, this::handleToastEvent);
        viewModel.getLoginDialogEvent().observe(this, this::handleShowLoginDialogEvent);
        viewModel.getCats().observe(getLifecycle(), this::addCatMarker);
        viewModel.getNavigateToCatEvent().observe(this, this::handleNavigateToCatEvent);
        viewModel.getNavigateToAddCatEvent().observe(this, this::handleNavigateToAddCatEvent);
        viewModel.getReloadEvent().observe(this, this::handleReloadCatsEvent);
        viewModel.getCurrentLocationEvent().observe(this, this::handleCurrentLocationEvent);
        viewModel.moveToCurrentLocation();
    }

    private void setUpBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(CatPostService.CAT_POST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new CatPostBroadcastReceiver(), intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCats();
    }

    private void loadCats() {
        if (mMap != null) {
            LatLng target = mMap.getCameraPosition().target;
            viewModel.loadCats(target);
        }
    }

    private void addCatMarker(Cat cat) {
        LatLng latLng = new LatLng(cat.latitude, cat.longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cat_marker));
        mMap.addMarker(markerOptions).setTag(cat.id);
    }

    private void handleCurrentLocationEvent(ViewEvent e) {
        if (isEventExecutable(e)) {
            int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (result == PackageManager.PERMISSION_GRANTED) {
                moveCameraToCurrentLocation();
            } else if (result == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_LOCATION);
            }
            e.handled();
        }
    }

    private void moveCameraToCurrentLocation() {
        try {
            locationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                if (mMap != null) {
                    mMap.animateCamera(cameraUpdate);
                }
            });
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }

    private void handleShowLoginDialogEvent(ViewEvent e) {
        if (isEventExecutable(e)) {
            LoginCheckDialogFragment f = new LoginCheckDialogFragment();
            f.show(getSupportFragmentManager(), null);
            e.handled();
        }
    }

    private void handleNavigateToCatEvent(ViewEvent<Integer> e) {
        if (isEventExecutable(e)) {
            Intent intent = CatActivity.getIntent(e.getPayload(), this);
            startActivity(intent);
            e.handled();
        }
    }

    private void handleNavigateToAddCatEvent(ViewEvent<LatLng> e) {
        if (isEventExecutable(e)) {
            Intent intent = AddCatActivity.getIntent(e.getPayload(), this);
            startActivity(intent);
            e.handled();
        }
    }

    private void handleReloadCatsEvent(ViewEvent e) {
        if (isEventExecutable(e)) {
            viewModel.clear();
            loadCats();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (0 < grantResults.length && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                moveCameraToCurrentLocation();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        mMap.setMinZoomPreference(12);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        onCameraMove();
    }

    @Override
    public void onLoginAccepted() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        viewModel.addNewCat(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int id = (int) marker.getTag();
        viewModel.showCat(id);
        return true;
    }

    @Override
    public void onCameraMove() {
        loadCats();
    }

    private class CatPostBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean result = intent.getBooleanExtra(CatPostService.EXTRA_RESULT, false);
            viewModel.notifyCatPostResult(result);
        }

    }
}
