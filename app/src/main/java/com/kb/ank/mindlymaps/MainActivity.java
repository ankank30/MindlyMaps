package com.kb.ank.mindlymaps;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.datatype.Duration;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "expectedError";

    GoogleMap googleMap;
    int count;
    String test_url="https://maps.googleapis.com/maps/api/directions/json?origin=22.250474,84.868225&destination=22.259334,84.886645&alternatives=true&key=%20AIzaSyAHsNPuWJgiQr0zG51gEaZV1fati3jiRAQ";

    Button fromButton, toButton;
    double left_turn = 0, right_turn = 0;
    double bias[];
    double prefBias=0;
    int prefRoute=0;
    int length=0;
    LocationListener locationListener;
    LocationManager locationManager;

    LottieAnimationView lottieAnimationView;

    Boolean switchFromTo = true;
    final Boolean FROM_SATE = true;
    final Boolean TO_STATE = false;

    LatLng fromLatLng, toLatLng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fetchRoutes();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET}, 10);
            }
            return;
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("It's Me!"));
                googleMap.clear();
                googleMap.addCircle(new CircleOptions().center(new LatLng(location.getLatitude(), location.getLongitude()))
                        .fillColor(Color.argb(150, 63, 81, 181))
                        .radius(location.getAccuracy())
                        .strokeColor(Color.argb(210, 63, 81, 181)));
                googleMap.addCircle(new CircleOptions().center(new LatLng(location.getLatitude(), location.getLongitude()))
                        .fillColor(Color.argb(255, 63, 81, 181))
                        .radius(10));
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
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET}, 10);
            }
            return;
        }
        locationManager.getLastKnownLocation("gps");
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

        lottieAnimationView = findViewById(R.id.animated_switch);
        lottieAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lottieAnimationView.getProgress() == 0) {
                    lottieAnimationView.playAnimation();
                } else {
                    lottieAnimationView.resumeAnimation();
                }
            }
        });

        lottieAnimationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (lottieAnimationView.getProgress() % 0.5 == 0) {
                    lottieAnimationView.pauseAnimation();
                    switchFromTo = !switchFromTo;
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.INTERNET}, 10);
                        }
                        return;
                    }
                    locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
                }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMaxZoomPreference(100);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET}, 10);
            }
            return;
        }
        googleMap.setMyLocationEnabled(true);
        setLocation(googleMap, new LatLng(22.251019, 84.903629));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.addMarker(new MarkerOptions().position(latLng).title("Point " + count));
                if(switchFromTo == FROM_SATE) {
                    fromLatLng = latLng;
                } else {
                    toLatLng = latLng;
                }
            }
        });
    }

    private void setLocation(GoogleMap googleMap, LatLng latLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.5f));
    }
    public void fetchRoutes()
    {
        String turn="";
        StringRequest request=new StringRequest(Request.Method.GET, test_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("routes");
                    bias=new double[array.length()];
                    length=bias.length;
                    for(int i=0;i<bias.length;i++)
                        bias[i]=0;
                    for (int i = 0; i < array.length(); i++)// Find the bias in this loop
                    {
                        JSONObject o = array.getJSONObject(i);
                        JSONArray legs = o.getJSONArray("legs");
                        for (int j = 0; j < legs.length(); j++) {
                            JSONObject steps = legs.getJSONObject(j);
                            JSONArray paths = steps.getJSONArray("steps");
                            for (int k = 0; k < paths.length(); k++) {
                                String turn = "";
                                JSONObject nodes = paths.getJSONObject(k);
                                if (nodes.has("maneuver")) {
                                    turn = nodes.getString("maneuver");
                                    int isLeft=turn.indexOf("left");
                                    int isRight=turn.indexOf("right");
                                    if(isRight!=-1)
                                        right_turn+=1;
                                    if(isLeft!=-1)
                                        left_turn+=1;
                                    isLeft=0;
                                    isRight=0;
                                }
                            }

                        }
                        if(left_turn==0)
                        bias[i]=0;
                        else bias[i]=right_turn/left_turn;
                        left_turn=0;
                        right_turn=0;
                    }
                    for(int i=0;i<length;i++)
                    {
                        if(bias[i]>prefBias)
                        {
                            prefBias=bias[i];
                            prefRoute=i;
                        }
                    }
                    Toast.makeText(getApplicationContext(),Integer.toString(prefRoute),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }
}
