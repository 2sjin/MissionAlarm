package com.example.missionalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.InputMismatchException;
import java.util.Random;

public class copy extends AppCompatActivity {
    Alarm alarmObject;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    MyTimer timer;
    TextView textViewTimer;

    Ringtone ringtone = ((OnAlarmActivity)OnAlarmActivity.context).ringtone;

    public static boolean failedMission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy);

        failedMission = false;
        alarmObject = OnAlarmActivity.alarmObjectForMission;
        controlDisplay();

        // 사자성어 답안
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
        TextView textViewTitle = findViewById(R.id.textViewTitle);
        TextView textViewTimer = findViewById(R.id.tvTimerMission);
        Button button1 = (Button) findViewById(R.id.button_1);
        Button buttonExitTest = findViewById(R.id.buttonExitTest);
        EditText editText1 = (EditText) findViewById(R.id.editView);

        String s;
        String operation[] = {"+", "-", "*"};
        int randomOperation = random.nextInt(3 );

        // 미리보기 종료 버튼 출력 여부 결정
        if(SetAlarmActivity.alarmObjectForTest == null)
            buttonExitTest.setVisibility(View.INVISIBLE);
        else
            buttonExitTest.setVisibility(View.VISIBLE);

        // 벌칙 타이머 작동
        if (alarmObject.penalty[0] == true) {
            textViewTimer.setVisibility(View.VISIBLE);
            timer = new MyTimer(10*1000, 1*1000, textViewTimer, this, button1);
            timer.start();
        }
        else
            textViewTimer.setVisibility(View.INVISIBLE);

        // 미션별 체크박스 체크 여부 확인
        if(randomMission == 0 && alarmObject.mission[0] == false)
            randomMission = 1;
        else if(randomMission == 1 && alarmObject.mission[1] == false)
            randomMission = 0;

        if(randomMission == 0) {
            s = sharedPreferences.getString(str[FourIndex], "");
            textView.setText(s);
            textViewTitle.setText("[미션]\n사자성어 퀴즈의\n정답을 입력하세요");
        }
        else {
            if (operation[randomOperation].equals("*")){
                firstNum = random.nextInt(31)+2;
                secondNum = random.nextInt(31)+2;
            }
            textView.setText(firstNum + operation[randomOperation]+secondNum);
            textViewTitle.setText("[미션]\n수학(사칙연산)퀴즈의\n정답을 입력하세요");
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
        int finalRandomMission = randomMission;
        View.OnClickListener listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                if(failedMission == true) {
                    cancelTimer(false);
                    ringtoneRelease();
                    finish();
                }
                else {
                    switch (v.getId()) {
                        case R.id.button_1: {
                            try {
                                String s1 = editText1.getText().toString();
                                Log.d("문제발생", s1);
                                if (str[FourIndex].equals(s1)) {
                                    cancelTimer(false);
                                    ringtoneRelease();
                                    finish();
                                } else if (finalResult == Integer.parseInt(s1)) {
                                    cancelTimer(false);
                                    ringtoneRelease();
                                    finish();
                                } else {
                                    if (alarmObject.penalty[0] == true) {
                                        cancelTimer(true); // 타이머 취소 후 재시작
                                    }
                                    Toast.makeText(getApplicationContext(), "틀렸습니다!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            } catch (NumberFormatException e) {
                                cancelTimer(true); // 타이머 취소 후 재시작
                                if (finalRandomMission == 1)
                                    Toast.makeText(getApplicationContext(), "정수를 입력해주세요.", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getApplicationContext(), "틀렸습니다!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        };
        button1.setOnClickListener(listener);

    }

    // 특수 키 입력 방지
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                setVolumeControlStream(AudioManager.ERROR);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override public void onBackPressed() { return; }

    // 알람음 정지
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

    // 테스트(미리보기) 종료
    public void exitTest(View view) {
        cancelTimer(false);
        ringtoneRelease();
        finish();
    }

    // 컴포넌트를 눌렀을 때 타이머 재시작
    public void onClickForTimerRestart(View view) {
        cancelTimer(true);
    }

    // 타이머 취소 및 재시작
    public void cancelTimer(boolean restart) {
        if(alarmObject.penalty[0] == true) {
            timer.cancel();
            if(restart)
                timer.start();
        }
    }



}
