package com.example.step_9;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private boolean signalMetod = false;
    private double magLast = 0;

    final String CHECK_STEP_KEY = "steps_key";
    final String TOTAL_CHECK_STEP_KEY = "total_steps_key";
    final String TARGET_KEY = "target_key";

    private int stepsCheck = 0;
    static int totalStepsL = 0;

    private int chTime = 0;
    private double minTime = 0;

    private int target = 7000;
    private TextView steps, targetView;
    private TextView timeView, distationView, caloriesView, textViewTime;
    private ProgressBar progressBar;

    int MinTime, ChTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        steps = findViewById(R.id.steps);
        targetView = findViewById(R.id.target);

        timeView = findViewById(R.id.timeView);
        distationView = findViewById(R.id.distationView);
        caloriesView = findViewById(R.id.caloriesView);

        progressBar = findViewById(R.id.progressBar);

        SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        stepsCheck = sPref.getInt(CHECK_STEP_KEY, 0);
        totalStepsL = sPref.getInt(TOTAL_CHECK_STEP_KEY, 0);
        //target = sPref.getInt(TARGET_KEY, 0);

        targetView.setText("Цель на день: " + target);
        progressBar.setMax( target);

        Date currentDate = new Date();

        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                steps.setText(Integer.toString(Math.max(stepsCheck, 0)));
                progressBar.setProgress(stepsCheck);

                double time = stepsCheck*0.01;
                while(true){
                    if(time<=58){
                        minTime = (float) (stepsCheck*0.01);
                        break;
                    }else {
                        time-=60;
                        chTime++;
                    }
                }
                timeView.setText(chTime + ":" + Math.round(minTime));
                distationView.setText(String.format("%.1f",stepsCheck*0.00075)+" км");
                caloriesView.setText(Math.round(stepsCheck*0.0562746201) + " кал");

                Thread t = new Thread(() -> {
                    DateFormat timeFormatMIN = new SimpleDateFormat("m", Locale.getDefault());
                    MinTime = Integer.parseInt(timeFormatMIN.format(currentDate));

                    DateFormat timeFormatCH = new SimpleDateFormat("H", Locale.getDefault());
                    ChTime = Integer.parseInt(timeFormatCH.format(currentDate));

                    if (ChTime == 0 && MinTime == 0){
                        stepsCheck = 0;
                        chTime = 0;
                        minTime = 0;
                    }

                    if(sensorEvent != null) {
                        double dx = sensorEvent.values[0];
                        double dy = sensorEvent.values[1];
                        double dz = sensorEvent.values[2];

                        double mag = Math.sqrt(dx*dx + dy*dy + dz*dz);

                        if(mag-magLast > 4) {
                            SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
                            SharedPreferences.Editor myEditor = sPref.edit();

                            myEditor.putInt(CHECK_STEP_KEY, stepsCheck);
                            myEditor.putInt(TOTAL_CHECK_STEP_KEY, totalStepsL);
                            myEditor.commit();


                            if (signalMetod){
                                stepsCheck++;
                                totalStepsL = stepsCheck;
                            }else{
                                signalMetod = true;
                            }

                            myEditor.putInt(CHECK_STEP_KEY, stepsCheck);
                            myEditor.putInt(TOTAL_CHECK_STEP_KEY, totalStepsL);
                            myEditor.commit();
                        }
                        magLast = mag;
                    }
                });
                t.start();
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        sensorManager.registerListener(stepDetector,sensor,sensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setDistance(String dis){
        progressBar.setMax(parseInt(dis));
    }

    public void clickShowDialog(View view){
        AlertDialog.Builder builder =  new AlertDialog.Builder(this );
        builder.setTitle(R.string.dialog_title);
        ConstraintLayout cl = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dialog_layout, null);

        builder.setPositiveButton(R.string.dialog_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog ad = (AlertDialog) dialog;
                EditText edTarget = ad.findViewById(R.id.edText);
                if(edTarget!= null){
                    if(!edTarget.getText().toString().equals("")){
                        SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putInt(TARGET_KEY, target);
                        ed.commit();

                        target = parseInt(edTarget.getText().toString());

                        ed.putInt(TARGET_KEY, target);
                        ed.commit();

                        setDistance(edTarget.getText().toString());
                        targetView.setText("Цель на день: " + edTarget.getText().toString());
                    }
                }
            }
        });
        builder.setView(cl);
        builder.show();
    }


    public void transitionLayout2(View view){
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }
}