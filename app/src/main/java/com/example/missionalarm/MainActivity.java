package com.example.missionalarm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

        // 리스트의 항목 클릭 시(알람 수정)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedIndex = position;
                String data = (String) adapterView.getItemAtPosition(selectedIndex);
                setAlarm();
            }
        });

        // 리스트의 항목 길게 클릭 시(알람 삭제)
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedIndex = position;
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setTitle("알람 삭제");
                ad.setMessage("알람을 삭제하시겠습니까?");

                // 이벤트: [삭제] 버튼을 눌렀을 때
                ad.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listBack.remove(selectedIndex);
                        listFront.remove(selectedIndex);
                        updateList();
                        updateNextAlarm();
                        dialog.dismiss();
                    }
                });
                
                // 이벤트: [취소] 버튼을 눌렀을 때
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // 다이얼로그 출력
                ad.show();
                return true;
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
        Intent intent = new Intent(this, SetAlarmActivity.class);
        startActivityForResult(intent, 0);
    }

    // 알람 수정
    public void setAlarm() {
        SetAlarmActivity.setAlarmItem(listBack.get(selectedIndex));
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
        List<Integer> listTimeCode = new ArrayList<>();
        LocalDateTime tNow = LocalDateTime.now();
        int timeCode;
        int timeCodeNow = (tNow.getDayOfWeek().getValue() * 10000) + (tNow.getHour() * 100) + tNow.getMinute();

        // 활성화된 알람이 없을 경우
        if(listBack.size() == 0) {
            tvNextAlarm.setText(textNoAlarm);
            return;
        }

        // 월(1)...일(7) -> 일(1)...토(7)
        if(timeCodeNow / 10000 == 7)
            timeCodeNow -= 60000;
        else
            timeCodeNow += 10000;

        // ArrayList에 알람 추가
        for(int i=0; i<listBack.size(); i++) {
            for(int j=0; j<7; j++) {
                if(listBack.get(i).week[j] == true) {
                    timeCode = ((j+1) * 10000) + (listBack.get(i).hour * 100) + listBack.get(i).minute;
                    listTimeCode.add(timeCode);
                }
            }
        }
        Collections.sort(listTimeCode); // ArrayList 정렬

        // 현재를 기준으로 리스트 재정렬
        int indexOfSplit = 0;
        for(int i=0; i<listTimeCode.size(); i++) {
            if(listTimeCode.get(i) > timeCodeNow) {
                indexOfSplit = i;
                break;
            }
        }
        List<Integer> listTimeCodeSorted = new ArrayList<>();
        listTimeCodeSorted.addAll(listTimeCode.subList(indexOfSplit, listTimeCode.size()));
        listTimeCodeSorted.addAll(listTimeCode.subList(0, indexOfSplit));

        // 다음 알람까지 얼마나 남았는지 일(Day), 시간(Hour), 분(Minute) 단위로 계산
        int diffDay = (listTimeCodeSorted.get(0) / 10000) - (timeCodeNow / 10000);
        int diffHour = (listTimeCodeSorted.get(0) / 100 % 100) - (timeCodeNow / 100 % 100);
        int diffMinute = (listTimeCodeSorted.get(0) % 100) - (timeCodeNow % 100);
        if(diffMinute < 0) {
            diffMinute += 60;
            diffHour--;
        }
        if(diffHour < 0) {
            diffHour += 24;
            diffDay--;
        }
        if(diffDay < 0) {
            diffDay += 7;
        }

        // 다음 알람까지 남은 시간 출력
        String strResult = diffDay + "일 " + diffHour + "시간 " + diffMinute + "분 후에\n알람이 울립니다.";
        Toast.makeText(MainActivity.this, strResult, Toast.LENGTH_SHORT).show();
        tvNextAlarm.setText(strResult);

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