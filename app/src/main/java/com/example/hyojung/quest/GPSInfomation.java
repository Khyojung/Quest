package com.example.hyojung.quest;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import static android.content.Context.LOCATION_SERVICE;

public class GPSInfomation implements LocationListener {

    private static final long MIN_DISTANCE_FOR_UPDATE = 5;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60;
    private final Context context;

    private boolean isGPSEnabled = false, isNetworkEnabled = false, isGetLocation = false;
    private Location userLocation = null;

    protected LocationManager locationManager;

    public GPSInfomation(Context context) {
        this.context = context;
    }

    private void getLocation() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.i("warnlog", "GPS 정보 불가");
            return;
        }
        try {
            locationManager = (LocationManager) context
                    .getSystemService(LOCATION_SERVICE);

            // GPS 정보 가져오기
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // 현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.i("warnlog", "GPS 정보 없음");
            }
            else {
                this.isGetLocation = true;
                // 네트워크 정보로 부터 위치값 가져오기
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_FOR_UPDATE,
                            MIN_DISTANCE_FOR_UPDATE, this);

                    if (locationManager != null) {
                        userLocation = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }

                if (isGPSEnabled) {
                    if (userLocation == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_FOR_UPDATE,
                                MIN_DISTANCE_FOR_UPDATE, this);
                        if (locationManager != null) {
                            userLocation = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            Log.i("enabled", "위치정보 갱신");
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Location getUserLocation() {
        this.getLocation();
        return userLocation;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
