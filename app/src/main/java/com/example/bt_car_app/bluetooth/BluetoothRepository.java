package com.example.bt_car_app.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.UUID;

public class BluetoothRepository {
    private final Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGatt bluetoothGatt;
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.println(Log.ERROR, "err", "no permission");
            }
            if (device.getName() != null && device.getName().contains("BluetoothCar")) {
                bluetoothLeScanner.stopScan(this);
                connectToDevice(device);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("BluetoothScan", "Scan failed with error: " + errorCode);
        }
    };
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("BluetoothGattCallback", "Connected to GATT server");
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.println(Log.ERROR, "err", "no permission");
                }
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("BluetoothGattCallback", "Disconnected from GATT server");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(UUID.fromString("19B10010-E8F2-537E-4F6C-D104768A1214"));
                if (service != null) {
                    BluetoothGattCharacteristic characteristic =
                            service.getCharacteristic(UUID.fromString("19B10011-E8F2-537E-4F6C-D104768A1214"));
                    if (characteristic != null) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            Log.println(Log.ERROR, "err", "no permission");
                        }
                        gatt.readCharacteristic(characteristic);
                    }
                }
            } else {
                Log.w("BluetoothGattCallback", "Service discovery failed, status: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] value = characteristic.getValue();
                Log.i("BluetoothGattCallback", "Characteristic read: " + Arrays.toString(value));
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("BluetoothGattCallback", "Characteristic written successfully");
            }
        }
    };

    public BluetoothRepository(Context context) {
        this.context = context;
        initializeBluetooth();
        startScan(context);
    }

    public boolean isBluetoothSupported() {
        return bluetoothAdapter != null;
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public void initializeBluetooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    public void startScan(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.println(Log.ERROR, "err", "no permission");
        }
        bluetoothLeScanner.startScan(scanCallback);
    }

    public void connectToDevice(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.println(Log.ERROR, "err", "no permission");
        }
        bluetoothGatt = device.connectGatt(context, false, gattCallback);
    }

    public boolean writeToCharacteristic(String serviceUUID, String characteristicUUID, byte[] value) {
        BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
            if (characteristic != null) {
                characteristic.setValue(value);
                characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT); // Or WRITE_TYPE_NO_RESPONSE
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.println(Log.ERROR, "err", "no permission");
                }
                return bluetoothGatt.writeCharacteristic(characteristic);
            } else {
                Log.e("WriteError", "Characteristic not found");
            }
        } else {
            Log.e("WriteError", "Service not found");
        }
        return false;
    }

    public boolean readFromCharacteristic(String serviceUUID, String characteristicUUID) {
        BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
            if (characteristic != null) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.println(Log.ERROR, "err", "no permission");
                }
                return bluetoothGatt.readCharacteristic(characteristic);
            } else {
                Log.e("ReadError", "Characteristic not found");
            }
        } else {
            Log.e("ReadError", "Service not found");
        }
        return false;
    }


}
