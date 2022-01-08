package com.example.missionalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.*;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.time.LocalTime;

public class SetAlarmActivity extends AppCompatActivity {
    static final int REQUEST_CODE_RINGTONE = 10005;
    static final int MISSION_SIZE = 3;
    static final int PENALTY_SIZE = 2;

    TextView tvRingtone;
    TimePicker timePicker;
    Button buttonRemove;
    EditText etName;
    Switch switchVibration;
    ToggleButton [] tbWeek = new ToggleButton[7];
    CheckBox [] cbMission = new CheckBox[MISSION_SIZE];
    CheckBox [] cbPenalty = new CheckBox[PENALTY_SIZE];
    SeekBar volumeBar;

    Uri uri;
    Ringtone ringtone;
    AudioManager am;

    int hour, minute;
    long t1, t2;
    static boolean visibleRemoveButton = false;
    static AlarmItem alarm;


    // Activity 초기 실행
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        loadComponentId();
        setVisibleRemoveButton();
        timePicker.setIs24HourView(true);   // 시간 선택기를 24시간제로 설정
        resetComponent();

        // 이벤트: 시간 선택기의 값을 변경했을 때
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourChanged, int minuteChanged) {
                hour = hourChanged;
                minute = minuteChanged;
            }
        });

        // 이벤트: 시크바(볼륨바) 조작 시
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ringtone.play();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ringtone.setVolume((float)progress / 100.0f);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    // Activity 종료 시 알람음(미리 듣기) 종료
    @Override
    public void onDestroy() {
        super.onDestroy();
        if( ringtone != null ) {
            if( ringtone.isPlaying() ) {
                ringtone.stop();
                ringtone = null;
            }
        }
    }

    // 뒤로가기 버튼 두 번 누르면 애플리케이션 종료
    @Override
    public void onBackPressed() {
        t2 = System.currentTimeMillis();
        Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        if (t2 - t1 < 2000) {
            super.onBackPressed();
            moveTaskToBack(true);   // 태스크를 백그라운드로 이동
            finishAndRemoveTask();          // 액티비티 종료 + 태스크 리스트에서 지우기
        }
        t1 = System.currentTimeMillis();
    }

    // [취소] 버튼을 눌렀을 때
    public void clickedButtonCancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    // [저장] 버튼을 눌렀을 때
    public void clickedButtonAdd (View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("hour", hour);
        intent.putExtra("minute", minute);
        for(int i=0; i<7; i++)
            intent.putExtra("week_" + i, tbWeek[i].isChecked());
        intent.putExtra("name", etName.getText().toString());
        intent.putExtra("ringtoneName", uri.toString());
        intent.putExtra("ringtoneVolume", volumeBar.getProgress());
        intent.putExtra("vibration", switchVibration.isChecked());
        for(int i=0; i<MISSION_SIZE; i++)
            intent.putExtra("mission_" + i, cbMission[i].isChecked());
        for(int i=0; i<PENALTY_SIZE; i++)
            intent.putExtra("penalty_" + i, cbPenalty[i].isChecked());
        setResult(RESULT_OK, intent);
        finish();
    }

    // [삭제] 버튼을 눌렀을 때
    public void clickedRemoveCancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        setResult(-4, intent);
        finish();
    }

    // 레이아웃에서 컴포넌트 ID 가져오기
    public void loadComponentId() {
        tvRingtone = findViewById(R.id.tvRingtone);
        timePicker = findViewById(R.id.timePicker);
        buttonRemove = findViewById(R.id.buttonRemove);
        etName = findViewById(R.id.etName);
        switchVibration = findViewById(R.id.switchVibration);
        tbWeek[0] = findViewById(R.id.tbSunday);
        tbWeek[1] = findViewById(R.id.tbMonday);
        tbWeek[2] = findViewById(R.id.tbTuesday);
        tbWeek[3] = findViewById(R.id.tbWednesday);
        tbWeek[4] = findViewById(R.id.tbThrusday);
        tbWeek[5] = findViewById(R.id.tbFriday);
        tbWeek[6] = findViewById(R.id.tbSaturday);
        cbMission[0] = findViewById(R.id.cbMission1);
        cbMission[1] = findViewById(R.id.cbMission2);
        cbMission[2] = findViewById(R.id.cbMission3);
        cbPenalty[0] = findViewById(R.id.cbPanelty1);
        cbPenalty[1] = findViewById(R.id.cbPanelty2);
        volumeBar = findViewById(R.id.volumeBar);
    }

    // 컴포넌트 초기화
    public void resetComponent() {
        if(alarm == null) {     // 알람 추가: 현재 시각으로 초기화 및 시간 선택기에 출력
            LocalTime t = LocalTime.now();
            hour = t.getHour();
            minute = t.getMinute();
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
            tvRingtone.setText(getUriToString(uri));
        }
        else {    // 알람 수정: 알람 데이터를 가져와서 컴포넌트에 출력
            hour = alarm.hour;
            minute = alarm.minute;
            timePicker.setHour(alarm.hour);
            timePicker.setMinute(alarm.minute);
            etName.setText(alarm.name);
            switchVibration.setChecked(alarm.vibration);
            for(int i=0; i<7; i++)
                tbWeek[i].setChecked(alarm.week[i]);
            for(int i=0; i<MISSION_SIZE; i++)
                cbMission[i].setChecked(alarm.mission[i]);
            for(int i=0; i<PENALTY_SIZE; i++)
                cbPenalty[i].setChecked(alarm.penalty[i]);
            volumeBar.setProgress(alarm.ringtoneVolume);
            uri = alarm.ringtoneName;
            ringtone = RingtoneManager.getRingtone(this, uri);
            tvRingtone.setText(getUriToString(alarm.ringtoneName));
        }
    }

    // [삭제] 버튼의 표시/숨김 여부를 결정
    public void setVisibleRemoveButton() {
        if(visibleRemoveButton == true) // 알람 수정
            buttonRemove.setVisibility(View.VISIBLE);
        else    // 알람 추가
            buttonRemove.setVisibility(View.GONE);
    }

    // 삭제 버튼 활성화/비활성화 여부 설정
    public static void setVisibleRemoveButton(boolean b) {
        visibleRemoveButton = b;
    }

    // AlarmItem 객체 가져오기
    public static void setAlarmItem(AlarmItem a) {
        alarm = a;
    }

    // 알람 벨소리 선택
    public void setRingtone(View view) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER); // 암시적 Intent
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "알람 벨소리를  선택하세요");  // 제목을 넣는다.
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);  // 무음을 선택 리스트에서 제외
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true); // 기본 벨소리는 선택 리스트에 넣는다.
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        startActivityForResult(intent, REQUEST_CODE_RINGTONE); // 벨소리 선택 창을 안드로이드OS에 요청
    }

    // 알람 벨소리 선택 후 돌아왔을 때
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode) {
            case REQUEST_CODE_RINGTONE:
                if (resultCode == RESULT_OK) {
                    Uri uriTemp = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    if(uriTemp != null) {
                        uri = uriTemp;
                        tvRingtone.setText(getUriToString(uri));
                        ringtone = RingtoneManager.getRingtone(this, uri);
                    }
                }
        }
    }

    // URI를 문자열로 변환
    public String getUriToString(Uri uri) {
        return RingtoneManager.getRingtone(this, uri).getTitle(this);
    }

}