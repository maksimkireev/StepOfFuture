package com.example.step_9;

import static com.example.step_9.MainActivity.totalStepsL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
public class UserActivity extends AppCompatActivity {

    private TextView totalSteps, totalTime, totalDistation, totalCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        totalSteps = findViewById(R.id.totalStep);
        totalTime = findViewById(R.id.timeViewTotal);
        totalDistation = findViewById(R.id.totalDistationView);
        totalCalories = findViewById(R.id.totalCaloriesView);


        totalSteps.setText(String.valueOf(totalStepsL));

        double time = totalStepsL*0.01;
        float minTime = 0;
        int chTime = 0;
        while(true){
            if(time<=58){
                minTime = (float) (totalStepsL*0.01);
                break;
            }else {
                time-=60;
                chTime++;
            }
        }
        totalTime.setText(chTime + ":" + Math.round(minTime));

        totalDistation.setText(String.format("%.1f",totalStepsL*0.00075)+" км");

        totalCalories.setText(Math.round(totalStepsL*0.0562746201) + " кал");
    }

    public void transitionLayout1(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}