package com.example.bt_car_app.data;

public class Car {
    private boolean isMotorRunning;
    private double speed;
    private double distance;
    private final double acceleration = 1;
    private final double loss = 0.1;

    public Car(boolean isMotoRunning, int initialSpeed) {
        this.isMotorRunning = isMotoRunning;
        speed = initialSpeed;
        distance = 0;
    }

    public boolean isMotorRunning() {
        return isMotorRunning;
    }

    public double getSpeed() {
        return speed;
    }

    public double getDistance() {
        return distance;
    }

    public void startMotor() {
        isMotorRunning = true;
    }

    public void stopMotor() {
        isMotorRunning = false;
    }

    public void updateSpeed(double deltaTime) {
        distance = speed * deltaTime + distance;
        if (isMotorRunning) {
            speed = acceleration * deltaTime + speed;
        } else {
            speed = Math.max(speed - loss * deltaTime, 0);
        }
    }
}
