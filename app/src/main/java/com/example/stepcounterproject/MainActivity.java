package com.example.stepcounterproject;  // change this to your package

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

// https://stackoverflow.com/questions/9276858/how-to-add-a-countup-timer-on-android
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    String string;
    boolean aBoolean;
    int anInt;
    // experimental values for hi and lo magnitude limits
    private final double HI_STEP = 11.0;     // upper mag limit
    private final double LO_STEP = 8.0;      // lower mag limit
    boolean highLimit = false;      // detect high limit
    int counter = 0;                // step counter
    int stopWatch =0;
    boolean pause = false;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    CountUpTimer timer;
    TextView tvStopWatch, tvSteps;
    Boolean ifStop=false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tvStopWatch = findViewById(R.id.tvCount);
        timer = new CountUpTimer(300000) {  // should be high for the run (ms)
            public void onTick(int second) {
                tvStopWatch.setText(String.valueOf(stopWatch));
                stopWatch = stopWatch +1;

            }

        };
        tvSteps = findViewById(R.id.tvSteps);

        // we are going to use the sensor service
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //stopStepCounter();
        View b = findViewById(R.id.doReset);
        b.setVisibility(View.GONE);
        //mSensor = mSensorManager.unregisterListener();


        if(savedInstanceState != null)
        {
            counter = savedInstanceState.getInt("counter");
            stopWatch = savedInstanceState.getInt("stopWatch");
            ifStop=savedInstanceState.getBoolean("ifStop");

            tvStopWatch.setText(Integer.toString(stopWatch));
            tvSteps.setText(Integer.toString(counter));

            if(ifStop==false)
            {
                timer.start();
                startStepCounter();
            }
        }
    }


    /*
     * When the app is brought to the foreground - using app on screen
     */
    protected void onResume() {
        super.onResume();

        // turn on the sensor

    }

    public void startStepCounter()
    {
        super.onResume();
        // turn on the sensor
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopStepCounter()
    {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    /*
     * App running but not on screen - in the background
     */
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);    // turn off listener to save power
    }

    public void doStart(View view) {

        View b = findViewById(R.id.doReset);
        b.setVisibility(View.GONE);
        tvSteps.setText(Integer.toString(counter));
        timer.start();
        ifStop=false;
        startStepCounter();
        Toast.makeText(this, "Started counting", Toast.LENGTH_LONG).show();

    }

    public void doStop(View view) {
        View b = findViewById(R.id.doReset);
        b.setVisibility(View.VISIBLE);
        ifStop=true;
        timer.cancel();
        stopStepCounter();

        Toast.makeText(this, "Stopped Run", Toast.LENGTH_LONG).show();
    }

    public void doReset(View view) {
        Toast.makeText(this, "Reset", Toast.LENGTH_LONG).show();
        counter = 0;
        timer.onFinish();
        timer.cancel();
        tvStopWatch.setText("0");
        stopWatch =0;
        tvSteps.setText(String.valueOf(counter));

    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // get a magnitude number using Pythagorus's Theorem
        double mag = round(Math.sqrt((x*x) + (y*y) + (z*z)), 2);
        //tvMag.setText(String.valueOf(mag));

        // for me! if msg > 11 and then drops below 9, we have a step
        // you need to do your own mag calculating
        if ((mag > HI_STEP) && (highLimit == false)) {
            highLimit = true;
        }
        if ((mag < LO_STEP) && (highLimit == true)) {
            // we have a step
            counter++;
            tvSteps.setText(String.valueOf(counter));
            highLimit = false;
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not used
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void changeScreen(View view)
    {
        Intent A2 = new Intent (view.getContext(), ShowRunDetails.class);
        A2.putExtra("Steps",counter);
        A2.putExtra("Seconds", stopWatch);
        startActivity(A2);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("ifStop",ifStop);
        // Put int value
        outState.putInt("stopWatch",stopWatch);
        outState.putInt("counter",counter);


        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // get values from saved state

        stopWatch=savedInstanceState.getInt("stopWatch");
        counter=savedInstanceState.getInt("counter");
        ifStop=savedInstanceState.getBoolean("ifStop");
        // display toast
        //Toast.makeText(getApplicationContext(),string+" - "+ aBoolean+" - "+anInt, Toast.LENGTH_SHORT).show();
        super.onRestoreInstanceState(savedInstanceState);
    }
}