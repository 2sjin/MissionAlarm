package com.example.missionalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class OnAlarmActivity extends AppCompatActivity {
    public static Object context;
    TextView tvOnAlarmName;
    TextView tvOnAlarmTime;
    Button buttonOff;

    Alarm alarm;
    static Alarm alarmObjectForMission;
    public static boolean failedMission = false;
    Ringtone ringtone;
    Vibrator vibrator;
    long[] pattern = {100, 1000};

    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    MyTimer timer;
    TextView textViewTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_alarm);
        context = this;

        // 진동 권한 획득 및 디스플레이 관련 제어
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        controlDisplay();
    }

    @Override
    public void onResume() {
        super.onResume();

        textViewTimer = findViewById(R.id.tvTimerOnAlarm);
        failedMission = false;

        // MainActivity에서 알람 객체 가져오기(테스트 중일 경우 SetAlarmActivity에서)
        if(SetAlarmActivity.alarmObjectForTest == null)
            alarm = MainActivity.alarmObjectForOnAlarm;
        else
            alarm = SetAlarmActivity.alarmObjectForTest;
        alarmObjectForMission = alarm;

        // 텍스트뷰 설정
        loadComponentId();
        tvOnAlarmName.setText(alarm.name);
        tvOnAlarmTime.setText(String.format("%02d:%02d", alarm.hour, alarm.minute));

        // 다른 알람음 및 진동 끄기
        vibrator.cancel();
        ringtoneRelease();

        // 알람음 재생
        ringtonePlay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            ringtone.setVolume((float) alarm.ringtoneVolume / 100.0f);

        // 진동 재생
        if (alarm.vibration == true)
            vibrator.vibrate(pattern, 0);

        // 버튼 텍스트 변경
        buttonOff.setText("미션 시작하기");
        if (alarm.mission[0] == false && alarm.mission[1] == false) {
            buttonOff.setText("알람 끄기");
        }

        // 벌칙 타이머 작동
        if (alarm.penalty[0] == true) {
            textViewTimer.setVisibility(View.VISIBLE);
            timer = new MyTimer(10*1000, 1*1000,
                    textViewTimer, this, buttonOff, alarm.phone);
            timer.start();
        }
        else
            textViewTimer.setVisibility(View.INVISIBLE);
    }

    // 미션 액티비티에서 복귀 후
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cancelTimer(false);
        finish();
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

    // [알람 끄기] 또는 [미션 시작하기] 버튼을 눌렀을 때
    public void clickedButtonOff(View view) {
        vibrator.cancel();
        if(failedMission == true) {
            cancelTimer(false);
            wakeLock.release();
            ringtoneRelease();
            finish();
        }
        else {
            cancelTimer(false);
            if (alarm.mission[0] == false && alarm.mission[1] == false) {
                ringtoneRelease();
                wakeLock.release();
                finish();
            } else {
                Intent intent = new Intent(this, copy.class); // copy class로 이동
                startActivity(intent);
            }
        }
    }

    // 알람음 재생
    public void ringtonePlay() {
        ringtone = RingtoneManager.getRingtone(this, alarm.ringtoneUri);
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build();
        ringtone.setAudioAttributes(audioAttributes);
        ringtone.play();
    }

    // 알람음 종료
    public void ringtoneRelease() {
        if( ringtone != null ) {
            if( ringtone.isPlaying() ) {
                ringtone.stop();
                ringtone = null;
            }
        }
    }

    // 레이아웃에서 컴포넌트 ID 가져오기
    public void loadComponentId() {
        tvOnAlarmName = findViewById(R.id.tvOnAlarmName);
        tvOnAlarmTime = findViewById(R.id.tvOnAlarmTime);
        buttonOff = findViewById(R.id.buttonOff);
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

    // 타이머 취소 및 재시작
    public void cancelTimer(boolean restart) {
        if(alarm.penalty[0] == true) {
            timer.cancel();
            if(restart)
                timer.start();
        }
    }
}