package com.masaworld.catmap.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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

    private GoogleMap mMap;
    private CatMapViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setUpViewModel();
        setUpBroadcastReceiver();
    }

    private void setUpViewModel() {
        viewModel = ViewModelProviders.of(this).get(CatMapViewModel.class);
        viewModel.getToastEvent().observe(this, this::handleToastEvent);
        viewModel.getLoginDialogEvent().observe(this, this::handleShowLoginDialogEvent);
        viewModel.getCats().observe(getLifecycle(), this::addCatMarker);
        viewModel.getNavigateToCatEvent().observe(this, this::handleNavigateToCatEvent);
        viewModel.getNavigateToAddCatEvent().observe(this, this::handleNavigateToAddCatEvent);
        viewModel.getReloadEvent().observe(this, this::handleReloadCatsEvent);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        mMap.setMinZoomPreference(15);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        LatLng chiba = new LatLng(35.645089, 140.040902);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(chiba));
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
