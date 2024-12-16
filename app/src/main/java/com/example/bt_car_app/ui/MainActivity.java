package com.example.bt_car_app.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.bt_car_app.R;
import com.example.bt_car_app.viewModel.BluetoothViewModel;
import com.example.bt_car_app.viewModel.CarViewModel;

public class MainActivity extends AppCompatActivity {
    private BluetoothViewModel bluetoothViewModel;
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

        bluetoothViewModel = new BluetoothViewModel(this);
        bluetoothViewModel.checkBluetoothStatus();
        bluetoothViewModel.isBluetoothSupported().observe(this, supported -> {
            if (supported) {
                Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            }
        });

        bluetoothViewModel.getPower().observe(this, data -> {
            distanceText.setText(data);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
