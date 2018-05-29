package com.growatt.mybluetooth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnStart;
    private Button btnStop;
    private Button btnTuYa;
    private BluetoothClient mClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnStop = findViewById(R.id.btnTuYa);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        mClient = new BluetoothClient(this);
        //蓝牙状态监听
        mClient.registerBluetoothStateListener(mBluetoothStateListener);
    }
    private final BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
                if (!openOrClosed){
                    //蓝牙未开启，提示开启蓝牙
                    mClient.openBluetooth();
                }else {
                    //蓝牙已开启
                }
        }

    };
    @Override
    public void onClick(View view) {
        if (view == btnStart){
            //判断蓝牙是否打开
            mClient.isBluetoothOpened();
        }
        if (view == btnStop){
            mClient.stopSearch();
            mClient.closeBluetooth();
        }
        if (view == btnTuYa){
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mClient.unregisterBluetoothStateListener(mBluetoothStateListener);
    }
}
