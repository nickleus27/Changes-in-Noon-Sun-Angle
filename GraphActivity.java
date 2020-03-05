package com.example.solarnoon;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

public class GraphActivity extends AppCompatActivity {

    private LineGraphSeries<DataPoint> cosine;
    private MainActivity data;
    private double noonAngle, dec, t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        data = new MainActivity();


        //changed to getNoonAngle
        noonAngle = data.getNoonAngle();


        dec = data.getDeclination();
        t = calcT();

        double x, y, xrad;
        int dataPoints = 360;
        x = -180;
        GraphView graph = (GraphView) findViewById(R.id.graphView);
        cosine = new LineGraphSeries<>();

        for(int i = 0; i<dataPoints; i++){
            xrad=x*Math.PI/180;
            y = Math.cos(xrad);
            x = x + 1;
            cosine.appendData(new DataPoint(x,y), true, 100);
        }
        graph.addSeries(cosine);
    }

    public void backClick(View v){
        startActivity( new Intent(this, MainActivity.class));

    }

    public double calcT(){
        double t;
        double radianNoonAngle = Math.toRadians(noonAngle);
        double radianDec = Math.toRadians(dec);
        t = Math.tan(radianNoonAngle) * Math.tan(radianDec);
        if(t>1)
            t = 1;
        if(t<-1)
            t=-1;
        return t;
    }

}
