package com.example.bt_car_app.viewModel;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bt_car_app.data.Car;

public class CarViewModel extends ViewModel {
    private final Car car;
    private final MutableLiveData<Boolean> isMotorRunning;
    private final MutableLiveData<Double> speed;
    private final MutableLiveData<Double> distance;
    private Handler handler;
    private Runnable physicsUpdateTask;

    public CarViewModel() {
        car = new Car(false, 0);
        isMotorRunning = new MutableLiveData<>(car.isMotorRunning());
        speed = new MutableLiveData<>(car.getSpeed());
        distance = new MutableLiveData<>(car.getDistance());
        handler = new Handler();
        startPhysicsThread();
    }

    public LiveData<Boolean> getIsMotorRunning() {
        return isMotorRunning;
    }

    public LiveData<Double> getSpeed() {
        return speed;
    }

    public LiveData<Double> getDistance() {
        return distance;
    }

    public void startMotor() {
        car.startMotor();
        updateCarState();
    }

    public void stopMotor() {
        car.stopMotor();
        updateCarState();
    }

    private void startPhysicsThread() {
        physicsUpdateTask = new Runnable() {
            @Override
            public void run() {
                synchronized (car) {
                    car.updateSpeed(0.1);
                    speed.postValue(car.getSpeed());
                    distance.postValue(car.getDistance());
                }
                handler.postDelayed(this, 100);
            }
        };
        handler.post(physicsUpdateTask);
    }

    private void updateCarState() {
        isMotorRunning.setValue(car.isMotorRunning());
        speed.setValue(car.getSpeed());
        distance.setValue(car.getDistance());
    }
}
