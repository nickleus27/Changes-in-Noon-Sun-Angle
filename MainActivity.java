// Nick's version as of 05 03mar 2020 
package com.example.solarnoon;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;

//code for latitude location from https://subzdesigns.com/blog/how-to-get-latitude-and-longitude-in-android-studio

public class MainActivity extends AppCompatActivity implements LocationListener{
    private double latitude;
    private double declination;
    private double noonAngle;
    private double shadow;
    private double intensity;

    String locationLatitude = "";
    private int mInterval = 3000; // 3 seconds by default, can be changed later
    private Handler mHandler;

    private EditText yourLatitude;
    private EditText latitudeText;
    private EditText declinationText;
    private EditText angleAtNoon;
    private EditText shadowLength;
    private EditText intensityText;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        latitudeText = (EditText) findViewById(R.id.latitudeText);
        declinationText = (EditText) findViewById(R.id.declinationText);
        angleAtNoon = (EditText) findViewById(R.id.angleAtNoon);
        shadowLength = (EditText) findViewById(R.id.shadowLength);
        yourLatitude = (EditText) findViewById(R.id.yourLatitude);
        intensityText = (EditText) findViewById(R.id.intensityText);

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            public void run() {
                mHandler = new Handler();
                startRepeatingTask();
            }
        }, 5000);   //5 seconds



        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {

            try {
                getLocation(); //this function can change value of mInterval.
                yourLatitude.setText(""+getLocationLatitude());
            } finally {

                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, (LocationListener) this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    private double getLocationLatitude(){
        double thisLatitude;
        try {
            String text = locationLatitude;
            thisLatitude = Double.valueOf(text);
        }catch(Exception e) {
            thisLatitude = 0.0;
        }
        return thisLatitude;
    }

    @Override
    public void onLocationChanged(Location location) {
        locationLatitude = location.getLatitude() + "";
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Please Enable GPS", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonCalc(View v){
        latitude = getLatitude();
        declination = getDeclination();
        if(latitude<-90 || latitude > 90 || declination<-23.5 || declination>23.5) {
            latitudeText.setText("");
            latitudeText.setHint("-90 to 90");
            declinationText.setText("");
            declinationText.setHint("-23.5 to 23.5");
        }else {
            noonAngle = sunAngleAtNoon();
            shadow = shadowLength();
            intensity = calcIntensity();
            angleAtNoon.setText(noonAngle + Character.toString((char) 176));
            shadowLength.setText(shadow + " cm");//rounds to a tenth
            intensityText.setText(intensity + "%");
        }
    }

    public void graphClicked(View v){
        startActivity( new Intent(this, GraphActivity.class));
    }

//this helper function i used from https://www.baeldung.com/java-round-decimal-number
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    private double calcIntensity(){
        double radianNoonAngle = Math.toRadians(noonAngle);
        double intensity = 100*Math.sin(radianNoonAngle);
        intensity = round(intensity, 2);
        return intensity;
    }

    //this function is unfinished!!!
    private double shadowLength(){
        double radianNoonAngle = Math.toRadians(noonAngle);
        double s = 100/Math.tan(radianNoonAngle);
        s = round(s, 1);
        if(s<0)
            s = 0;

        return s;
    }

    private double sunAngleAtNoon(){
        double angleAtNoon = 90 - Math.abs(latitude - declination);
        if (angleAtNoon< 0)
            angleAtNoon = 0;

        return angleAtNoon;
    }

    public double getLatitude() {
        double latitude;
        try {
            String text = latitudeText.getText().toString();
            latitude = Double.valueOf(text);
        }catch(Exception e) {
            latitude = 0.0;
        }
        return latitude;
    }


//***added this function
    public double getNoonAngle() {
        return noonAngle;
    }

    public double getDeclination() {
        double declination;
        try {
            String text = declinationText.getText().toString();
            declination = Double.valueOf(text);
        }catch(Exception e) {
            declination = 0.0;
        }
        return declination;
    }

}

