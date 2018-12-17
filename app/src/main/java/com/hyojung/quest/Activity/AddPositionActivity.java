package com.hyojung.quest.Activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hyojung.quest.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class AddPositionActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final int AREA_SELECTED = 20, BACK_PRESSED = 21;

    private GoogleMap googleMap;
    private Geocoder geocoder;
    private Button button;
    private EditText editText;
    double selectedLatitude = 0, selectedLongitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_position);
        editText = (EditText)findViewById(R.id.textbox_areaName);
        button = (Button)findViewById(R.id.button_searchArea);

        SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager()
                                                    .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.geocoder = new Geocoder(this);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(36.366920355, 127.344290167), 16));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
                selectedLatitude = latLng.latitude; // 위도
                selectedLongitude = latLng.longitude; // 경도
                Intent intent = new Intent();
                intent.putExtra("latitude", selectedLatitude);
                intent.putExtra("longitude", selectedLongitude);
                intent.putExtra("areaName", editText.getText());
                setResult(AREA_SELECTED, intent);
                finish();
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                googleMap.clear();
                selectedLatitude = marker.getPosition().latitude; // 위도
                selectedLongitude = marker.getPosition().longitude; // 경도
                Intent intent = new Intent();
                intent.putExtra("latitude", selectedLatitude);
                intent.putExtra("longitude", selectedLongitude);
                intent.putExtra("areaName", editText.getText());
                setResult(AREA_SELECTED, intent);
                finish();
                return true;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().replace(' ', '\0').isEmpty()) {
                    button.setEnabled(false);
                }
                else {
                    button.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().replace(' ', '\0').isEmpty()) {
                    button.setEnabled(false);
                }
                else {
                    button.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().replace(' ', '\0').isEmpty()) {
                    button.setEnabled(false);
                }
                else {
                    button.setEnabled(true);
                }
            }
        });

        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                googleMap.clear();
                String str = editText.getText().toString();
                List<Address> addressList = null;
                try {
                    // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                    addressList = geocoder.getFromLocationName(
                            str, // 주소
                            10); // 최대 검색 결과 개수
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                if (!addressList.isEmpty()) {
                    Address selectedAddress = addressList.get(0);
                    String madeString = selectedAddress.getAddressLine(
                            selectedAddress.getMaxAddressLineIndex()).replace(selectedAddress.getCountryName(), "").trim();

                    // 좌표(위도, 경도) 생성
                    LatLng point = new LatLng(selectedAddress.getLatitude(), selectedAddress.getLongitude());
                    // 마커 생성
                    MarkerOptions mOptions2 = new MarkerOptions();
                    mOptions2.title("검색 결과");
                    mOptions2.snippet(madeString);
                    mOptions2.position(point);
                    // 마커 추가
                    googleMap.addMarker(mOptions2);
                    // 해당 좌표로 화면 줌
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
                }
            }
        });
    }
}
