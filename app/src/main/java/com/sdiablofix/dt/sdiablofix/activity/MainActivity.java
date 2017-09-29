package com.sdiablofix.dt.sdiablofix.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sdiablofix.dt.sdiablofix.R;

public class MainActivity extends AppCompatActivity {

    // 定义控件
    // EditText etbarcode;

    /** 定义扫描头接口 **/
//    ScannerInerface Controll = new ScannerInerface(this);
//    IntentFilter intentFilter;
//    BroadcastReceiver scanReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        // 找到定义的EditText控件
//        etbarcode = (EditText) this.findViewById(R.id.etbarcode);
//
//        /** 以下是开始扫描，获取条码内容* */
//
//        Controll.open();// 打开扫描头，开始扫描
//        Controll.setOutputMode(1);// 使用广播模式
//        intentFilter = new IntentFilter("android.intent.action.SCANRESULT");
//
//        scanReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                // 此处获取扫描结果信息
//                final String scanResult = intent.getStringExtra("value");
//                // 给文本接收框赋值
//                etbarcode.setText(scanResult);
//                etbarcode.invalidate();
//            }
//        };

    }

    @Override
    protected void onResume() {
        super.onResume();

        /** 注册广播接收 **/
        // registerReceiver(scanReceiver, intentFilter);

    }

    @Override
    protected void onPause() {

        /** 注销广播 **/
        // this.unregisterReceiver(scanReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        scanReceiver = null;
//        intentFilter = null;
        super.onDestroy();
    }
}
