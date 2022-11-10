package com.example.stepcounterproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ShowRunDetails extends AppCompatActivity {

    TextView tvDateOfRun, tvNumberMeters, tvCalories, tvTimeTaken;
    public Calendar calendar;
    public SimpleDateFormat dateFormat;
    public String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_run_details);


        //Assigning TextViews and getting data from other screen
        tvDateOfRun = (TextView) findViewById(R.id.dateTv);
        tvTimeTaken = (TextView) findViewById(R.id.TimeTakenTv);
        tvCalories = (TextView) findViewById(R.id.caloriesTv);
        tvNumberMeters =(TextView) findViewById(R.id.NumberMeterTv);
        int steps = getIntent().getIntExtra("Steps",-1);
        int timeTaken = getIntent().getIntExtra("Seconds",-1);

        //Formulas
        double calories = steps*0.04;
        double meters = steps*0.8;
        //Log.i("test",Double.toString(meters));
       // Getting the Current Date
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        date = SimpleDateFormat.getDateInstance().format(calendar.getTime());   //format(calendar.getTime());
        tvDateOfRun.setText(date);



        displayRunDetails(meters,timeTaken,calories);
    }

    public void displayRunDetails(double meters, int timeTaken, double calories)
    {
        tvTimeTaken.setText(Integer.toString(timeTaken)+" Seconds");
        tvCalories.setText(Double.toString(calories));
        tvNumberMeters.setText(Double.toString(meters)+ " M");
    }

    public void returnBtn()
    {
        finish();
    }
}