package com.example.bt_car_app.viewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bt_car_app.bluetooth.BluetoothDeviceModel;
import com.example.bt_car_app.bluetooth.BluetoothRepository;

import java.util.List;

public class BluetoothViewModel {
    private final BluetoothRepository bluetoothRepository;
    private final MutableLiveData<Boolean> bluetoothSupportedLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> bluetoothEnabledLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> bluetoothPowerLiveData = new MutableLiveData<>();

    public BluetoothViewModel(Context context) {
        this.bluetoothRepository = new BluetoothRepository(context);
    }

    public void checkBluetoothStatus() {
        bluetoothSupportedLiveData.setValue(bluetoothRepository.isBluetoothSupported());
        bluetoothEnabledLiveData.setValue(bluetoothRepository.isBluetoothEnabled());
    }

    public void updatePowerData(byte[] value) {
        bluetoothPowerLiveData.setValue(String.valueOf(bluetoothRepository.writeToCharacteristic("19B10010-E8F2-537E-4F6C-D104768A1214", "19B10011-E8F2-537E-4F6C-D104768A1214", value)));
    }

    public LiveData<Boolean> isBluetoothSupported() {
        return bluetoothSupportedLiveData;
    }

    public LiveData<Boolean> isBluetoothEnabled() {
        return bluetoothEnabledLiveData;
    }

    public LiveData<String> getPower() {
        return bluetoothPowerLiveData;
    }
}
