package com.example.p09_gettingmylocations;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileWriter;

import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    private static final int request_user_location = 100;
    Button btnStart, btnStop, btnCheck;;
    TextView tv;
    FusedLocationProviderClient client;
    LocationCallback callback;
    String msg = "";
    String folderLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionCheck_Write = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck_Write != PermissionChecker.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "Permission is not granted!", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            finish();
        }
        tv = findViewById(R.id.tvLocation);
        btnStart = findViewById(R.id.buttonStart);
        btnStop = findViewById(R.id.buttonStop);
        btnCheck = findViewById(R.id.buttonCheck);

        folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder";
        File folder = new File(folderLocation);
        if (folder.exists() == true){
            boolean result = folder.mkdir();
            if (result == true){
                Log.d("File Read/Write", "Folder created");
                Toast.makeText(MainActivity.this, "Folder created", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, "Folder creation failed", Toast.LENGTH_SHORT).show();

            }
        }

        client = LocationServices.getFusedLocationProviderClient(this);

        if(checkUserPermission() == true){
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        msg = "Last known location when this Activity started: \nLatitude: " + location.getLatitude() + "\nLongtitude: " +location.getLongitude();
                        tv.setText(msg);
                    }else{
                        msg = "No Last known location found";
                        tv.setText(msg);
                    }
                }
            });

        }else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }




        callback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult != null){
                    Location data = locationResult.getLastLocation();
                    double lat  = data.getLatitude();
                    double lng = data.getLongitude();
                    msg = "Last known location when this Activity started: \nLatitude: " + lat + "\nLongtitude: " + lng;
                    tv.setText(msg);
                    try{
                        File file = new File(folderLocation, "data.txt");
                        FileWriter writer = new FileWriter(file, true);
                        writer.write("Latitude: " + lat + "\nLongitude: " + lng + "\n");
                        writer.flush();
                        writer.close();

                        Toast.makeText(MainActivity.this, "Write Successful", Toast.LENGTH_SHORT).show();

                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "Failed to write", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
            }
        };




        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                startService(i);

                if(checkUserPermission() == true){
                    LocationRequest request = LocationRequest.create();
                    request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    request.setInterval(10000);
                    request.setFastestInterval(5000);
                    request.setSmallestDisplacement(100);

                    client.requestLocationUpdates(request, callback, null);

                }else{
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                }


            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                stopService(i);
                client.removeLocationUpdates(callback);


            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    public  boolean checkUserPermission(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, request_user_location);
            }else{
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, request_user_location);

            }
            return  false;
        }else{
            return true;
        }
    }

}
