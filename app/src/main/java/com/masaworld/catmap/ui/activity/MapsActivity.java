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
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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
        GoogleMap.OnCameraMoveListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_REQUEST_LOCATION =1458;
    private GoogleMap mMap;
    private FusedLocationProviderClient locationProviderClient;
    private CatMapViewModel viewModel;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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
        setUpViews();
    }

    private void setUpViews() {
        Toolbar toolbar = findViewById(R.id.maps_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerLayout = findViewById(R.id.maps_drawer);
        navigationView = findViewById(R.id.maps_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        viewModel.getChangeNavigationMenuEvent().observe(this, this::handleChangeNavigationMenuEvent);
        viewModel.loadNavMenu();
        viewModel.moveToCurrentLocation();
    }

    private void setUpBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(CatPostService.CAT_POST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new CatPostBroadcastReceiver(), intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadNavMenu();
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

    private void handleChangeNavigationMenuEvent(ViewEvent<Integer> e) {
        if (isEventExecutable(e)) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(e.getPayload());
            e.handled();
        }
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
            showLoginDialog(R.string.login_check_title_add_cat, R.string.login_check_message_add_cat);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.menu_logout:
                viewModel.logout();
                break;
            case R.id.menu_oss_license:
                showToast("oss license");
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
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
