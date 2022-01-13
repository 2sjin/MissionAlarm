package com.example.missionalarm;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

class MyTimer extends CountDownTimer {
    TextView textView;
    Context context;
    Button button;
    String phone;

    public MyTimer(long millisInFuture, long countDownInterval, TextView tv, Context context, Button btn, String phone) {
        super(millisInFuture, countDownInterval);
        this.textView = tv;
        this.context = context;
        this.button = btn;
        this.phone = phone;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        textView.setText(millisUntilFinished/1000 + " 초 후 문자 전송");
    }

    @Override
    public void onFinish() {
        // 미션 화면일 경우 버튼을 끄기 버튼으로 전환
        OnAlarmActivity.failedMission = true;
        copy.failedMission = true;
        if(button != null)
            button.setText("알람 끄기");

        // 문자메시지 전송
        textView.setText("문자메시지 전송");
        String PhoneNumber = phone;
        if(phone.substring(0, 3).equals("010"))
            PhoneNumber = "+8210" + phone.substring(3);
        String sms = "[APP 자동 발신]\n깨워주세요!! 알람이 울렸지만 일어나지 못하고 있습니다!!";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(PhoneNumber, null, sms, null, null);
            showDialog("문자메시지 전송 완료",
                    "주어진 시간 안에 알람을 끄지 못하여\n" + phone + " 연락처로 다음 메시지를 전송하였습니다.\n\n" + sms);
        } catch (Exception e) {
            showDialog("전송 오류", e.toString());
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();//오류 원인이 찍힌다.
            e.printStackTrace();
        }
    }

    public void showDialog(String title, String message) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show(); // 다이얼로그 출력

    }
}