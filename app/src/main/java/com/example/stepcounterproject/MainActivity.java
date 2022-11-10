package com.example.stepcounterproject;  // change this to your package

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.*;

// https://stackoverflow.com/questions/9276858/how-to-add-a-countup-timer-on-android
import android.os.CountDownTimer;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // experimental values for hi and lo magnitude limits
    private final double HI_STEP = 11.0;     // upper mag limit
    private final double LO_STEP = 8.0;      // lower mag limit
    boolean highLimit = false;      // detect high limit
    int counter = 0;                // step counter
    int stepPause =0;
    int currentTime =0;
    boolean pause = false;
    TextView tvMag, tvSteps;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    CountUpTimer timer;
    TextView tvCounter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tvCounter = findViewById(R.id.tvCount);
        timer = new CountUpTimer(300000) {  // should be high for the run (ms)
            public void onTick(int second) {
                tvCounter.setText(String.valueOf(second + currentTime));
                stepPause = second + currentTime;
            }
        };
       // tvMag = findViewById(R.id.tvMag);
        tvSteps = findViewById(R.id.tvSteps);

        // we are going to use the sensor service
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stopStepCounter();
        View b = findViewById(R.id.doReset);
        b.setVisibility(View.GONE);
        //mSensor = mSensorManager.unregisterListener();
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
        tvSteps.setText("0");
        counter=0;
        timer.start();
        startStepCounter();
        Toast.makeText(this, "Started counting", Toast.LENGTH_LONG).show();
    }

    public void doStop(View view) {
        View b = findViewById(R.id.doReset);
        b.setVisibility(View.VISIBLE);
        timer.cancel();
        currentTime = stepPause;
        stopStepCounter();
        Toast.makeText(this, "Stopped Run", Toast.LENGTH_LONG).show();
    }

    public void doReset(View view) {
        tvCounter.setText("0");
        Toast.makeText(this, "Reset", Toast.LENGTH_LONG).show();
        counter = 0;
        timer.cancel();
        stepPause=0;
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
        A2.putExtra("Seconds",stepPause);
        startActivity(A2);
    }
}