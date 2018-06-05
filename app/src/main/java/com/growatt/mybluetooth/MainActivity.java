package com.growatt.mybluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.growatt.mybluetooth.bluetooth.activity.BluetoothActivity;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnStart;
    private Button btnStop;
    private Button btnBle;
    private Button btnTuYa;
    private BluetoothClient mClient;
    private String MAC = "20:18:05:24:09:30";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnBle = findViewById(R.id.btnStop);
        btnTuYa = findViewById(R.id.btnTuYa);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnBle.setOnClickListener(this);
        btnTuYa.setOnClickListener(this);
        mClient = new BluetoothClient(this);
        //蓝牙状态监听
        mClient.registerBluetoothStateListener(mBluetoothStateListener);
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
                    SearchRequest request = new SearchRequest.Builder()
                            .searchBluetoothLeDevice(3000, 3)   // 先扫BLE设备3次，每次3s
                            .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
                            .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                            .build();

                    mClient.search(request, new SearchResponse() {
                        @Override
                        public void onSearchStarted() {

                        }

                        @Override
                        public void onDeviceFounded(SearchResult device) {
                            Beacon beacon = new Beacon(device.scanRecord);
                            BluetoothLog.v(String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
                        }

                        @Override
                        public void onSearchStopped() {

                        }

                        @Override
                        public void onSearchCanceled() {

                        }
                    });
                }
        }

    };
    BleConnectOptions options = new BleConnectOptions.Builder()
            .setConnectRetry(3)   // 连接如果失败重试3次
            .setConnectTimeout(30000)   // 连接超时30s
            .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
            .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
            .build();
    @Override
    public void onClick(View view) {
        if (view == btnStart){
            //判断蓝牙是否打开
//            mClient.isBluetoothOpened();
            mClient.connect(MAC,options, new BleConnectResponse() {
                @Override
                public void onResponse(int code, BleGattProfile profile) {
                    if (code == Constants.REQUEST_SUCCESS) {
                        BleGattService service = profile.getServices().get(0);
                        mClient.read(MAC, service.getUUID(), service.getCharacters().get(0).getUuid(), new BleReadResponse() {
                            @Override
                            public void onResponse(int code, byte[] data) {
                                if (code == Constants.REQUEST_SUCCESS) {

                                }
                            }
                        });
                    }
                }
            });
        }
        if (view == btnStop){
            mClient.stopSearch();
            mClient.closeBluetooth();
        }
        if (view == btnTuYa){
//            startActivity(new Intent(this, TuYaActivity.class));
            byte[] sendjiami = new byte[1];
            sendjiami[0] = 0x01;
            decodenewKey(sendjiami);

        }
        if (view == btnBle){
            startActivity(new Intent(this, BluetoothActivity.class));
        }
        if (view == btnStop){

        }
    }
        private static byte[] newkeys = {1,2,3,4};
        public static byte[] decodenewKey(byte[] src) {
            if (src == null) return src;
            for (int j = 0; j < src.length; j++)    // Payload数据做掩码处理
            {
                src[j] = (byte) (src[j] ^ newkeys[j % 4]);
            }
            return src;
        }
    @Override
    protected void onStop() {
        super.onStop();
        mClient.unregisterBluetoothStateListener(mBluetoothStateListener);
    }
}
