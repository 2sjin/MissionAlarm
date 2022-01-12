package com.example.missionalarm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

class MyTimer extends CountDownTimer {
    TextView textView;
    Context context;
    Button button;

    public MyTimer(long millisInFuture, long countDownInterval, TextView tv, Context context, Button btn) {
        super(millisInFuture, countDownInterval);
        this.textView = tv;
        this.context = context;
        this.button = btn;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        textView.setText(millisUntilFinished/1000 + " 초 후 문자 전송");
    }

    @Override
    public void onFinish() {
        textView.setText("문자메시지 전송");

        // 문자메시지 전송
        String PhoneNumber = "+821025773617";
        String sms = "깨워주세요!! 알람이 울렸지만 일어나지 못했습니다!!";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(PhoneNumber, null, sms, null, null);
            Toast.makeText(context, "전송 완료!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "전송 오류!", Toast.LENGTH_LONG).show();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();//오류 원인이 찍힌다.
            e.printStackTrace();
        }
        copy.failedMission = true;
        if(button != null)
            button.setText("알람 끄기");
    }
}