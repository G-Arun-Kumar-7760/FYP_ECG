package com.experiment.chickenjohn.materialdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


public class bluetoothManager {
    private boolean CONNECT_STATE = false;
    private static BluetoothAdapter myBtAdapter = BluetoothAdapter.getDefaultAdapter();
    private static BluetoothDevice myBtDevice;
    private clientThread myBtClientThread;
    private BluetoothSocket myBtSocket;
    public final String btAddress = "00:18:E4:40:00:06";
    public bluetoothReceiver btReceiver = new bluetoothReceiver();
    private android.os.Handler uiRefreshHandler;
    private int receiveECGCounter = 0;
    private int receiveSPO2Counter = 0;
    private final int ECG_DATA = 0;
    private final int SPO2_DATA = 1;
    private int dataType = 0;
    private boolean receiveSpo2 = true;

    public bluetoothManager(android.os.Handler handler) {
        uiRefreshHandler = handler;
    }

    public void enableBluetooth(boolean startDiscovery) {
        if (!myBtAdapter.isEnabled()) {
            myBtAdapter.enable();
        }

        if (startDiscovery)
            myBtAdapter.startDiscovery();
    }

    public void disableBluetooth() {
        if (myBtAdapter.isEnabled()) {
            myBtAdapter.disable();
        }
    }

    public class bluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String targetName = "HC-05";
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice currentDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (currentDevice.getAddress().equalsIgnoreCase(btAddress)) {
                    myBtDevice = myBtAdapter.getRemoteDevice(btAddress);
                    Toast.makeText(context, "Device Found:" + myBtDevice.getName(), Toast.LENGTH_LONG).show();
                    CONNECT_STATE = true;
                    myBtClientThread = new clientThread();
                    myBtClientThread.start();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (!isConnected()) {
                    Toast.makeText(context, "Device Not Found", Toast.LENGTH_LONG).show();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(context, "Start to Search", Toast.LENGTH_LONG).show();
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                CONNECT_STATE = false;
                Toast.makeText(context, "Device Disconnected，Try to reconnect", Toast.LENGTH_LONG).show();
                Message uiRefreshMessage = Message.obtain();
                uiRefreshMessage.what = 1;
                uiRefreshHandler.sendMessage(uiRefreshMessage);
            }
        }
    }

    private class clientThread extends Thread {
        public void run() {
            try {
                myBtAdapter.cancelDiscovery();
                myBtSocket = myBtDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                myBtSocket.connect();
                new connectThread().start();
                Message uiRefreshMessage = Message.obtain();
                uiRefreshMessage.what = 0;
                uiRefreshHandler.sendMessage(uiRefreshMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class connectThread extends Thread {
        public void run() {
            InputStream mmInStream = null;
            try {
                mmInStream = myBtSocket.getInputStream();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            while (true) {
                try {
                    if ((mmInStream.available()) >= 2) {
                        //Log.v("data in stream",Integer.toString(bytes));
                        byte[] buf_data = new byte[2];
                        mmInStream.read(buf_data);
                        handleBtData(buf_data);
                    }
                } catch (IOException e) {
                    try {
                        mmInStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    //Handle received data here
    public void handleBtData(byte[] data) {
        int dataInInt;
        if (0x0 == (0x80 & data[1])) {
            dataInInt = ((0xff & ((int) data[1])) << 8) | (0xff & (int) data[0]);
        } else {
            dataInInt = ((0xff & (~data[1])) << 8) | (0xff & ((~data[0]) + 1));
            //dataInInt = -dataInInt;

        }
        dataInInt=dataInInt%1024;

        Message uiRefreshMessage = Message.obtain();
        switch (dataType) {
            case ECG_DATA:
                if (receiveSpo2) {
                    dataType = SPO2_DATA;
                }
                uiRefreshMessage.what = 2;
                uiRefreshMessage.arg1 = dataInInt;
                uiRefreshMessage.arg2 = receiveECGCounter;
                uiRefreshHandler.sendMessage(uiRefreshMessage);
                receiveECGCounter += 1;
                break;
            case SPO2_DATA:
                dataType = ECG_DATA;
                uiRefreshMessage.what = 4;
                uiRefreshMessage.arg1 = dataInInt;
                uiRefreshMessage.arg2 = receiveSPO2Counter;
                uiRefreshHandler.sendMessage(uiRefreshMessage);
                receiveSPO2Counter += 1;
                break;
            default:
                break;
        }

    }

    public void setSpo2Receiver (boolean receiveOrNot){
        receiveSpo2 = receiveOrNot;
        receiveSPO2Counter = receiveECGCounter-1;
    }

    //Registration of Broadcast Receiver
    public IntentFilter regBtReceiver() {
        IntentFilter bluetoothBroadcastFilter = new IntentFilter();
        bluetoothBroadcastFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        bluetoothBroadcastFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        bluetoothBroadcastFilter.addAction(BluetoothDevice.ACTION_FOUND);
        bluetoothBroadcastFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        return bluetoothBroadcastFilter;
    }

    public boolean isConnected() {
        return CONNECT_STATE;
    }
}
