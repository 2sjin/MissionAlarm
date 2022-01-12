package com.example.missionalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class copy extends AppCompatActivity {

    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    Ringtone ringtone = ((OnAlarmActivity)OnAlarmActivity.context).ringtone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy);

        controlDisplay();

        // 알람음 종료
        // 사자성어 사전
        String [] str = {"과유불급","문경지교","부화뇌동","망양지탄","누란지세","결자해지","은감불원","수기치인"
                ,"불광불급","수구초심"};
        SharedPreferences sharedPreferences= getSharedPreferences("test", MODE_PRIVATE);

        Random random = new Random();
        int firstNum = random.nextInt(1000)+109;
        int secondNum = random.nextInt(1000)+109;
        int randomMission = random.nextInt(2);
        int FourIndex = random.nextInt(str.length);
        Log.d("랜덤숫자", String.valueOf(randomMission));
        TextView textView = findViewById(R.id.textView);
        Button button1 = (Button) findViewById(R.id.button_1);
        EditText editText1 = (EditText) findViewById(R.id.editView);
        String s;
        String operation[] = {"+","-","*"};
        int randomOperation = random.nextInt(3 );
        if(randomMission==0){
            s = sharedPreferences.getString(str[FourIndex], "");
            textView.setText(s);
        }
        else{
            if (operation[randomOperation].equals("*")){
                firstNum = random.nextInt(31)+2;
                secondNum = random.nextInt(31)+2;
            }
            textView.setText(firstNum + operation[randomOperation]+secondNum);
        }
        int result = 0;
        if (operation[randomOperation].equals("+")){
            result = firstNum + secondNum;
        }
        else if(operation[randomOperation].equals("-")){
            result = firstNum - secondNum;
        }
        else if(operation[randomOperation].equals("*")){
            result = firstNum * secondNum;
        }
        int finalResult = result;
        View.OnClickListener listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                switch(v.getId()) {
                    case R.id.button_1:{
                        String s1 = editText1.getText().toString();
                        Log.d("문제발생",s1);
                        if(str[FourIndex].equals(s1)){
                            ringtoneRelease();
                            finish();
                        }
                        else if(finalResult == Integer.parseInt(s1)){
                            ringtoneRelease();
                            finish();
                        }
                        break;
                    }

                  /*  case R.id.button_4:{
                        String PhoneNumber = "+821025773617";
                        String sms = "깨워주세요!! 알람이 울렸지만 일어나지못했습니다!!";
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(PhoneNumber, null, sms, null, null);
                            Toast.makeText(getApplicationContext(), "전송 완료!", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "전송 오류!", Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();//오류 원인이 찍힌다.
                            e.printStackTrace();
                        }
                        break;
                    }*/
                }
            }
        };
        button1.setOnClickListener(listener);

    }
    public void ringtoneRelease() {
        if( ringtone != null ) {
            if( ringtone.isPlaying() ) {
                ringtone.stop();
                ringtone = null;
            }
        }
    }

    // 디스플레이 제어 관련 메소드
    public void controlDisplay() {
        // 전체 화면
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN );

        // 화면 자동꺼짐 방지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 꺼진 화면 깨우기
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE,"WAKE:LOCK");
        wakeLock.acquire(); // WakeLock 깨우기
    }
}
