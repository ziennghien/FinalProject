package com.end.finalproject.customer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.end.finalproject.R;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION = 1001;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private CancellationTokenSource cancellationTokenSource;
    private Marker locationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Nút back
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Setup Map Fragment (null-check để tránh NPE)
        SupportMapFragment mapFrag =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFrag != null) {
            mapFrag.getMapAsync(this);
        } else {
            Toast.makeText(this, "MapFragment not found", Toast.LENGTH_SHORT).show();
        }

        // Init FusedLocationProvider và token
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        cancellationTokenSource = new CancellationTokenSource();

        // Tạo LocationRequest với API 35 + Priority constant
        locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 5_000L)
                .setMinUpdateIntervalMillis(3_000L)
                .build();

        // Callback nhận cập nhật vị trí
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                Location loc = result.getLastLocation();
                updateLocationOnMap(loc);
            }
        };
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Kiểm tra quyền vị trí
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableLocationFeatures();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }

    private void enableLocationFeatures() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Hiển thị nút My Location
        mMap.setMyLocationEnabled(true);

        // Lấy nhanh vị trí hiện tại 1 lần
        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.getToken()
        ).addOnSuccessListener(this, this::updateLocationOnMap);

        // Bắt đầu lắng nghe cập nhật vị trí
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );
    }

    private void updateLocationOnMap(Location location) {
        if (location == null || mMap == null) return;

        LatLng latLng = new LatLng(
                location.getLatitude(),
                location.getLongitude()
        );
        String title = getAddressFromLocation(location);

        if (locationMarker == null) {
            locationMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(title));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        } else {
            locationMarker.setPosition(latLng);
            locationMarker.setTitle(title);
            locationMarker.showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    private String getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );
            if (list != null && !list.isEmpty()) {
                Address addr = list.get(0);
                StringBuilder sb = new StringBuilder();
                if (addr.getThoroughfare() != null) {
                    sb.append(addr.getThoroughfare());
                }
                if (addr.getLocality() != null) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(addr.getLocality());
                }
                return sb.length() > 0 ? sb.toString() : "Current Location";
            }
        } catch (IOException ignored) { }
        return "Current Location";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableLocationFeatures();
        } else {
            Toast.makeText(this,
                    "Permission Denied",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tiếp tục cập nhật khi quay lại
        if (mMap != null
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
