package com.example.safedriving;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;


public class activity2 extends AppCompatActivity implements LocationListener {


    float nCurrentSpeed = 0;
    double maxSpeed = 0;
    double highestSpeed = 0;
    String strSpeedMessage;
    SwitchCompat sw_metric;
    TextView tv_speed;
    TextView tv_MaxSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);

        sw_metric = findViewById(R.id.sw_metric);
        tv_speed = findViewById(R.id.tv_speed);
        tv_MaxSpeed = findViewById(R.id.tv_MaxSpeed);
        Button stopbutton = (Button) findViewById(R.id.button2);
        stopbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMain();
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
            }else{
                doStuff();
            }
        }
        this.updateSpeed(null);
        highestSpeed = 0;

        sw_metric.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activity2.this.updateSpeed(null);
            }
        });
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location != null){
            CLocation myLocation = new CLocation(location, this.useMetricUnits());
            this.updateSpeed(myLocation);
        }

    }

    @SuppressLint("MissingPermission")
    private void doStuff(){
        LocationManager locationManager = (LocationManager)this.getSystemService(getApplicationContext().LOCATION_SERVICE);
        if(locationManager != null){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        //Toast.makeText(this, "waiting for GPS connection", Toast.LENGTH_SHORT).show();
    }

    private void updateSpeed(CLocation location) {


        if (location != null) {
            location.setUseMetricUnits(this.useMetricUnits());
            nCurrentSpeed = location.getSpeed();
            maxSpeed = MaxSpeed(nCurrentSpeed);
            Toast.makeText(this, speedChecker(maxSpeed), Toast.LENGTH_SHORT).show();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(" ", "0");

        Formatter format = new Formatter(new StringBuilder());
        format.format(Locale.US, "%5.1f", maxSpeed);
        String strMaxSpeed = format.toString();

        if (this.useMetricUnits()) {
            tv_MaxSpeed.setText("highest speed: " + strMaxSpeed + "km/h");
        } else {
            tv_MaxSpeed.setText("highest speed: " + strMaxSpeed + "mp/h");
        }

        if (this.useMetricUnits()) {
            tv_speed.setText(strCurrentSpeed + "km/h");
        } else {
            tv_speed.setText(strCurrentSpeed + "mp/h");
        }

    }

    private double MaxSpeed(double currentSpeed){

        if(highestSpeed < currentSpeed){
            highestSpeed = currentSpeed;
            Log.d("Speed", String.valueOf(highestSpeed));
        }
        return highestSpeed;
    }

    private String speedChecker(double highestSpeed){

        if(highestSpeed > 70){
            strSpeedMessage = "Naughty, you went over the legal speed limit.";
        }
        else{
            strSpeedMessage = "You are driving safely, thank you";
        }
        return strSpeedMessage;
    }

    private boolean useMetricUnits(){
        return sw_metric.isChecked();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1000){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                doStuff();

            }else{
                finish();
            }
        }
    }
    public void openMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
