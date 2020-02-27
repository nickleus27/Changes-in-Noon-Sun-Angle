package com.example.solarnoon;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {
    double latitude = 37.0;
    double declination = 0;
    double noonAngle = 0.0;
    double shadow = 0.0;
    EditText latitudeText;
    EditText declinationText;
    EditText angleAtNoon;
    EditText shadowLength;

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
            angleAtNoon.setText(noonAngle + Character.toString((char) 176));
            shadowLength.setText(shadow + " cm");//rounds to a tenth
        }
    }
//this helper function i used from https://www.baeldung.com/java-round-decimal-number
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    //this function is unfinished!!!
    private double shadowLength(){
        double radianNoonAngle = Math.toRadians(noonAngle);
        double s = 100/Math.tan(radianNoonAngle);
        s = round(shadow, 1);
        if(shadow<0)
            s = 0;

        return s;
    }

    private double sunAngleAtNoon(){
        double angleAtNoon = 90 - Math.abs(latitude - declination);
        if (angleAtNoon< 0)
            angleAtNoon = 0;

        return angleAtNoon;
    }

    private double getLatitude() {
        double latitude;
        try {
            String text = latitudeText.getText().toString();
            latitude = Double.valueOf(text);
        }catch(Exception e) {
            latitude = 0.0;
        }
        return latitude;
    }

    private double getDeclination() {
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

