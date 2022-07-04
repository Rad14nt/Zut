package com.example.zuut;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.example.zuut.apis.Api;
import com.example.zuut.databinding.ActivityMapsBinding;
import com.example.zuut.database.ZuutDatabase;
import com.example.zuut.model.Company;
import com.example.zuut.model.Scooter;
import com.example.zuut.model.ScooterRepository;

public final class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int VOI_MAX_BATTERY_LIFE = 40;
    private static final String KM = "km";
    private GoogleMap mMap;
    private LocationManager locationManager;
    private double userLat;
    private double userLong;
    private ZuutDatabase database;
    private static final int STANDARD_REFRESH_TIME = 5000;
    private ConstraintLayout scooter_info;
    List<Scooter> currentScooters = new ArrayList<>();
    private LocationCallback locationCallback;
    private List<Company> companies = new ArrayList<>();
    private List<Api> apis = new ArrayList<>();
    private final ScooterRepository scooterRepository = ScooterRepository.getInstance();
    private boolean sliderIsUp = false;
    private static final int  FROM_X_DELTA = 0;
    private static final int  FROM_Y_DELTA = 0;
    private static final int  TO_X_DELTA = 0;
    private static final int  TO_Y_DELTA = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = ZuutDatabase.getInstance(MapsActivity.this);

        com.example.zuut.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        scooter_info = findViewById(R.id.scooter_slider);
        scooter_info.setVisibility(View.INVISIBLE);

        ImageView settingsButton = findViewById(R.id.btn_settings);
        ImageView aboutButton = findViewById(R.id.btn_about);
        TextView title = findViewById(R.id.mapsToolbarTitle);

        title.setText(Constants.ZUUT);
        settingsButton.setOnClickListener(v -> openFilterActivity());
        aboutButton.setOnClickListener(v -> openAboutActivity());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        companies = database.getCompanyDao().findAllActivatedCompanies();
        if (companies.size() == 0) {
            openFilterActivity();
        }

        apis = new ArrayList<>();
        for (Company company : companies) {
            apis.add(Api.getInstanceOfApi(company));
        }
    }

    public void openFilterActivity() {
        Intent intent = new Intent(this, FilterActivity.class);
        startActivity(intent);
    }

    public void openAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {

        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        mMap = googleMap;
        mMap.setOnMarkerClickListener(marker1 -> showBottomSheetDialog(marker1));
        mMap.setOnMapClickListener(arg0 -> hideBottomSheetDialog());

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        client.getCurrentLocation(Constants.I, getCancellationToken()).addOnCompleteListener(this, task -> {

            Location initialLoc = task.getResult();
            if (initialLoc == null) {
                return;
            }
            Marker ourMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(initialLoc.getLatitude(), initialLoc.getLongitude())));
            assert ourMarker != null;
            setStandardUserView(ourMarker);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    userLat = initialLoc.getLatitude();
                    userLong = initialLoc.getLongitude();
                    ourMarker.setPosition(new LatLng(initialLoc.getLatitude(), initialLoc.getLongitude()));
                    mMap.isBuildingsEnabled();
                    fetchScootersFromAllApis(apis, companies);
                }

                @Override
                public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                    super.onLocationAvailability(locationAvailability);
                }
            };

            client.requestLocationUpdates(LocationRequest.create().setInterval(STANDARD_REFRESH_TIME), locationCallback, Looper.getMainLooper());

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, STANDARD_REFRESH_TIME, 5, location -> {
                userLat = location.getLatitude();
                userLong = location.getLongitude();
                ourMarker.setPosition(new LatLng(userLat, userLong));
                mMap.isBuildingsEnabled();
                client.removeLocationUpdates(locationCallback);
                fetchScootersFromAllApis(apis, companies);
            });
        });
    }

    private void fetchScootersFromAllApis(List<Api> apis, List<Company> companies) {
        for (Api api : apis) {
            Company cmp = null;
            for (Company company : companies) {
                if (company.equals(api.getCompany())) {
                    cmp = company;
                    break;
                }
            }
            api.newRetrieveScooters(MapsActivity.this, String.valueOf(userLat), String.valueOf(userLong), cmp, scooterRepository::addScooters);
        }
        setScooterMarkers(scooterRepository.getScooters());
        scooterRepository.clear();
    }


    @NotNull
    private CancellationToken getCancellationToken() {
        return new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                return false;
            }

            @NonNull
            @NotNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull @NotNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }
        };
    }


    private void setStandardUserView(Marker ourMarker) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ourMarker.getPosition(), Constants.ZOOM_VALUE));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ourMarker.getPosition())
                .zoom(Constants.ZOOM_VALUE)
                .bearing(Constants.BEARING_VALUE)
                .tilt(Constants.TILT_VALUE)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    private void setScooterMarkers(List<Scooter> scooters) {
        for (Scooter scooter : scooters) {

            if (!currentScooters.contains(scooter)) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(scooter.getLat(), scooter.getLong())));
                switch (scooter.getMake()) {
                    case Constants.BIRD:
                        assert marker != null;
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                        break;
                    case Constants.VOI:
                        assert marker != null;
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        break;

                }
                scooter.setMarker(marker);
                currentScooters.add(scooter);
            } else {
                Scooter currentScooter = currentScooters.get(currentScooters.indexOf(scooter));
                currentScooter.getMarker().setPosition(new LatLng(scooter.getLat(), scooter.getLong()));
            }
        }
        removeOldScootersFromList(scooters);
    }

    private void removeOldScootersFromList(List<Scooter> scooters) {
        Iterator<Scooter> currentIterator = currentScooters.iterator();
        while (currentIterator.hasNext()) {
            Scooter currentScooter = currentIterator.next();
            if (!scooters.contains(currentScooter)) {
                currentScooter.getMarker().remove();
                currentIterator.remove();
            }
        }

    }

    @SuppressLint("SetTextI18n")

    private boolean showBottomSheetDialog(Marker marker) {
        Optional<Scooter> optionalScooter = currentScooters.stream().filter(scoot -> scoot.getMarker().equals(marker)).findFirst();
        optionalScooter.ifPresent(scooter -> {
            TextView battery = scooter_info.findViewById(R.id.battery_level);
            TextView distance = scooter_info.findViewById(R.id.remaining_distance);
            ImageView logo = scooter_info.findViewById(R.id.company_logo);
            switch (scooter.getMake()) {
                case Constants.BIRD:
                    logo.setImageResource(R.drawable.bird_logo);
                    distance.setText(Math.round(scooter.getEstimatedRange() / Constants.ESTIMATED_RANGE_VALUE) + KM);
                    break;
                case Constants.VOI:
                    logo.setImageResource(R.drawable.voi_logo);
                    distance.setText(Math.round(VOI_MAX_BATTERY_LIFE * scooter.getBatteryLevel()) / Constants.BATTERY_MAX_LEVEL + KM);
                    break;
            }
            battery.setText((int) scooter.getBatteryLevel() + Constants.PERCENT);

            TranslateAnimation animate = new TranslateAnimation(
                    FROM_X_DELTA,
                    TO_X_DELTA,
                    scooter_info.getHeight(),
                    TO_Y_DELTA);
            animate.setDuration(Constants.DURATION_VALUE);
            animate.setFillAfter(true);
            scooter_info.startAnimation(animate);

            scooter_info.setVisibility(View.VISIBLE);
        });
        this.sliderIsUp = true;
        return false;
    }



    private void hideBottomSheetDialog() {
        if (sliderIsUp) {
            TranslateAnimation animate = new TranslateAnimation(
                    FROM_X_DELTA,
                    TO_X_DELTA,
                    FROM_Y_DELTA,
                    scooter_info.getHeight());
            animate.setDuration(Constants.DURATION_VALUE);
            animate.setFillAfter(true);
            scooter_info.startAnimation(animate);
            scooter_info.setVisibility(View.INVISIBLE);
        }
        this.sliderIsUp = false;
    }
}
