package com.example.bt_car_app.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.bt_car_app.R;
import com.example.bt_car_app.viewModel.CarViewModel;

public class MainActivity extends AppCompatActivity {
    private CarViewModel carViewModel;
    private Button button;
    private TextView isRunningText;
    private TextView speedText;
    private TextView distanceText;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.main_activity);

        isRunningText = findViewById(R.id.text_is_running);
        speedText = findViewById(R.id.text_speed);
        distanceText = findViewById(R.id.text_distance);
        button = findViewById(R.id.button_gas);

        carViewModel = new CarViewModel();

        carViewModel.getIsMotorRunning().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isRunning) {
                isRunningText.setText(isRunning ? "Motor running" : "Motor sill");
            }
        });

        carViewModel.getSpeed().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double speed) {
                speedText.setText("Speed: " + String.valueOf(speed));
            }
        });

        carViewModel.getDistance().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double distance) {
                distanceText.setText("Distance: " + String.valueOf(distance));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunningText.getText() == "Motor running") {
                    carViewModel.stopMotor();
                } else {
                    carViewModel.startMotor();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
