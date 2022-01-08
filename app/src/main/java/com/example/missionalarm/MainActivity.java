package com.example.missionalarm;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> listFront = new ArrayList<>();
    ArrayList<AlarmItem> listBack = new ArrayList<>();

    ListView listView;
    TextView tv;

    int selectedIndex;
    long t1, t2;

    // Activity 초기 실행
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadComponentId();
        updateList();

        // 리스트의 항목 클릭 시
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedIndex = position;
                String data = (String) adapterView.getItemAtPosition(selectedIndex);
                tv.setText(data);
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
        AlarmItem alarmTemp = new AlarmItem();
        alarmTemp.setTime(data.getIntExtra("hour", 0), data.getIntExtra("minute", 0));
        for(int i=0; i<7; i++)
            alarmTemp.setWeek(i, data.getBooleanExtra("week_" + i, false));
        alarmTemp.setName(data.getStringExtra("name"));
        alarmTemp.setVibration(data.getBooleanExtra("vibration", data.getBooleanExtra("vibration", false)));
        alarmTemp.setRingtone(data.getBooleanExtra("ringtone", true));
        alarmTemp.setMission(data.getStringExtra("mission"));
        alarmTemp.setPenalty(data.getStringExtra("penalty"));
        switch(requestCode) {
            case 0: // 알람 추가 화면에서 복귀 후
                if (resultCode == RESULT_OK) {
                    listBack.add(alarmTemp);
                    listFront.add(alarmTemp.getInfo());
                }
                break;
            case 1: // 알람 수정 화면에서 복귀 후
                if (resultCode == RESULT_OK) {    // 알람 수정
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
    }

    // 레이아웃에서 컴포넌트 ID 가져오기
    public void loadComponentId() {
        tv = findViewById(R.id.textViewAlarm);
        listView = findViewById(R.id.listView);
    }

    // 리스트 새로고침
    public void updateList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listFront);
        listView.setAdapter(adapter);
    }



}