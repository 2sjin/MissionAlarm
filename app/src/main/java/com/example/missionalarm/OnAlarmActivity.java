package com.example.missionalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;

public class OnAlarmActivity extends AppCompatActivity {
    Alarm alarm;
    Vibrator vibrator;
    long[] pattern = {100, 1000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_alarm);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 화면 자동꺼짐 방지
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);    // 진동 권한 획득

        Intent intent = new Intent();
        boolean b = intent.getBooleanExtra("AlarmForActivity", false);
//        alarm = (Alarm) intent.getSerializableExtra("AlarmObject");

        if(b == true)
//        if(alarm.vibration == true)
            vibrator.vibrate(pattern, 0);
        else
            vibrator.vibrate(pattern, -1);

    }

    public void clickedButtonOff(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        vibrator.cancel();
        finish();
    }
}