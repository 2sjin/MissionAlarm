package com.example.missionalarm;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.time.LocalDateTime;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> listFront = new ArrayList<>();
    ArrayList<AlarmItem> listBack = new ArrayList<>();

    ListView listView;
    TextView tvNextAlarm;

    AlarmItem alarmTemp;
    int selectedIndex;
    long t1, t2;
    String textNoAlarm = "활성화된 알람이\n없습니다.";

    // Activity 초기 실행
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadComponentId();
        updateList();
        updateNextAlarm();

        // 리스트의 항목 클릭 시
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedIndex = position;
                String data = (String) adapterView.getItemAtPosition(selectedIndex);
                setAlarm();
            }
        });
    }

    // 뒤로가기 버튼 두 번 누르면 애플리케이션 종료
    @Override
    public void onBackPressed() {
        t2 = System.currentTimeMillis();
        Toast.makeText(MainActivity.this, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        if (t2 - t1 < 2000) {
            super.onBackPressed();
            moveTaskToBack(true);   // 태스크를 백그라운드로 이동
            finishAndRemoveTask();          // 액티비티 종료 + 태스크 리스트에서 지우기
        }
        t1 = System.currentTimeMillis();
    }

    // 알람 추가 버튼 클릭 시
    public void addAlarm(View view) {
        SetAlarmActivity.setAlarmItem(null);
        SetAlarmActivity.setVisibleRemoveButton(false);
        Intent intent = new Intent(this, SetAlarmActivity.class);
        startActivityForResult(intent, 0);
    }

    // 알람 수정
    public void setAlarm() {
        SetAlarmActivity.setAlarmItem(listBack.get(selectedIndex));
        SetAlarmActivity.setVisibleRemoveButton(true);
        Intent intent = new Intent(this, SetAlarmActivity.class);
        startActivityForResult(intent, 1);
    }

    // startActivityForResult 이후 메인 화면으로 돌아올 때
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0: // 알람 추가 화면에서 복귀 후
                if (resultCode == RESULT_OK) {
                    alarmTemp = new AlarmItem();
                    updateAlarmTemp(data);
                    listBack.add(alarmTemp);
                    listFront.add(alarmTemp.getInfo());
                }
                break;
            case 1: // 알람 수정 화면에서 복귀 후
                if (resultCode == RESULT_OK) {    // 알람 수정
                    alarmTemp = new AlarmItem();
                    updateAlarmTemp(data);
                    listBack.set(selectedIndex, alarmTemp);
                    listFront.set(selectedIndex, alarmTemp.getInfo());
                }
                else if(resultCode == -4) {   // 알람 삭제
                    listBack.remove(selectedIndex);
                    listFront.remove(selectedIndex);
                }
                break;
        }
        updateList();
        updateNextAlarm();
    }

    // 레이아웃에서 컴포넌트 ID 가져오기
    public void loadComponentId() {
        tvNextAlarm = findViewById(R.id.tvNextAlarm);
        listView = findViewById(R.id.listView);
    }

    // 리스트 새로고침
    public void updateList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listFront);
        listView.setAdapter(adapter);
    }

    // 다음 알람 새로고침
    public void updateNextAlarm() {
        int codeNowWeek, codeNowTime, codeTempWeek, codeTempTime, codeNextAlarm;
        ArrayList<Integer> listTemp = new ArrayList<>();
        LocalDateTime t = LocalDateTime.now();

        // 현재 요일과 시간 구하기, 일요일 시작으로 변경 (1=일, ..., 7=토)
        codeNowWeek = t.getDayOfWeek().getValue();
        if(codeNowWeek == 7)
            codeNowWeek = 1;
        else
            codeNowWeek += 1;
        codeNowTime = (t.getHour() * 100) + t.getMinute();

        // 활성화된 알람이 없을 경우
        if(listBack.size() == 0) {
            tvNextAlarm.setText(textNoAlarm);
            return;
        }

        // 활성화된 알람을 리스트에 추가
        listTemp.clear();
        codeTempWeek = 0;
        for(int i=0; i<listBack.size(); i++) {
            for(int cntWeek=0; cntWeek<7; cntWeek++) {
                if(listBack.get(i).week[cntWeek] == true) {
                    codeTempWeek = cntWeek + 1;
                    break;
                }
            }
            codeTempTime = (listBack.get(i).hour * 100) + listBack.get(i).minute;
            if(codeTempWeek <= 0)
                codeTempWeek = -99;
            else if(codeTempTime <= codeTempTime)
                codeTempWeek += 7;
            listTemp.add((codeTempWeek * 10000) + codeTempTime);
        }

        // 리스트에서 최소값 구하기
        codeNextAlarm = Collections.min(listTemp);

        // 최대값 보정
        if(codeNextAlarm >= 90000) {
            codeNextAlarm -= 70000;
        }

        // 알람이 얼마 후에 울리는지 계산
        int diffDay = (codeNextAlarm / 10000) - codeNowWeek;
        int diffHour = (codeNextAlarm % 10000 / 100 ) - (codeNowTime / 100);
        int diffMinute = (codeNextAlarm % 100) - (codeNowTime % 100);
        if(diffMinute < 0) {
            diffMinute += 60;
            diffHour--;
        }
        if(diffHour < 0) {
            diffHour += 24;
            diffDay--;
        }

        // 알람이 얼마 후에 올리는지 표시
        if(diffDay >= 0)
            tvNextAlarm.setText(diffDay + "일 " + diffHour + "시간 " + diffMinute + "분 후에\n알람이 울립니다.");
        else
            tvNextAlarm.setText(textNoAlarm);
    }

    // 알람 객체 새로고침
    public void updateAlarmTemp (Intent data) {
        alarmTemp.setTime(data.getIntExtra("hour", 0), data.getIntExtra("minute", 0));
        for(int i=0; i<7; i++)
            alarmTemp.setWeek(i, data.getBooleanExtra("week_" + i, false));
        alarmTemp.setName(data.getStringExtra("name"));
        alarmTemp.setRingtone(Uri.parse(data.getStringExtra("ringtoneName")), data.getIntExtra("ringtoneVolume", 50));
        alarmTemp.setVibration(data.getBooleanExtra("vibration", data.getBooleanExtra("vibration", false)));
        for(int i=0; i<3; i++)
            alarmTemp.setMission(i, data.getBooleanExtra("mission_" + i, false));
        for(int i=0; i<2; i++)
            alarmTemp.setPenalty(i, data.getBooleanExtra("penalty_" + i, false));
    }

}