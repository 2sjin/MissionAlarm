package com.example.missionalarm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.time.LocalDateTime;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> listFront = new ArrayList<>();
    ArrayList<Alarm> listBack = new ArrayList<>();

    ListView listView;
    TextView tvNextAlarm;

    static Alarm alarmObjectForOnAlarm;
    Alarm alarmObject;
    AlarmManager alarmManager;
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
                        unregistAlarm();
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

    // 다른 액티비티에서 복귀 후
    @Override
    protected void onResume() {
        super.onResume();
        updateNextAlarm();
    }

    // 알람매니저에 알람 등록
    public void registAlarm(int week, int hour, int minute) {
        alarmManager = (AlarmManager)getSystemService (Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, week);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntent);
    }

    // 알람 매니저에서 알람 삭제
    public void unregistAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.cancel(pIntent);
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
                    alarmObject = new Alarm();
                    updateAlarmObject(data);
                    listBack.add(alarmObject);
                    listFront.add(alarmObject.getInfo());
                }
                break;
            case 1: // 알람 수정 화면에서 복귀 후
                if (resultCode == RESULT_OK) {    // 알람 수정
                    alarmObject = new Alarm();
                    updateAlarmObject(data);
                    listBack.set(selectedIndex, alarmObject);
                    listFront.set(selectedIndex, alarmObject.getInfo());
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
        HashMap<Integer, Alarm> hashMapAlarm = new HashMap<>();
        HashMap<Integer, Alarm> hashMapAlarmSorted = new HashMap<>();
        List<Integer> listKeys = new ArrayList<>();
        List<Integer> listKeysSorted = new ArrayList<>();
        LocalDateTime tNow = LocalDateTime.now();
        boolean allWeekisDisabled = false;
        int timeCode, weekCount;
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
            weekCount = 0;
            allWeekisDisabled = false;
            for(int j=0; j<7; j++) {
                if(listBack.get(i).week[j] == true) {
                    timeCode = ((j+1) * 10000) + (listBack.get(i).hour * 100) + listBack.get(i).minute;
                    hashMapAlarm.put(timeCode, listBack.get(i));
                    weekCount++;
                }
            }
            if(weekCount <= 0) {
                timeCode = ((timeCodeNow / 10000) * 10000) + (listBack.get(i).hour * 100) + listBack.get(i).minute;
                hashMapAlarm.put(timeCode, listBack.get(i));
                allWeekisDisabled = true;
            }
        }

        // 해시맵의 키 값을 ArrayList에 저장
        Set<Integer> setKeys = hashMapAlarm.keySet();
        listKeys = new ArrayList<>(setKeys);

        // ArrayList 정렬
        Collections.sort(listKeys);

        // 현재를 기준으로 ArrayList 재정렬
        int indexOfSplit = 0;
        for(int i=0; i<listKeys.size(); i++) {
            if(listKeys.get(i) > timeCodeNow) {
                indexOfSplit = i;
                break;
            }
        }

        listKeysSorted.addAll(listKeys.subList(indexOfSplit, listKeys.size()));
        listKeysSorted.addAll(listKeys.subList(0, indexOfSplit));

        // 정렬된 ArrayList를 기준으로 새로운 해시맵에 키-값 쌍 저장
        for (int i : listKeysSorted)
            hashMapAlarmSorted.put(i, hashMapAlarm.get(i));

        // 알람 매니저에 알람 등록
        int codeIndexZero = listKeysSorted.get(0);
        registAlarm(codeIndexZero / 10000, codeIndexZero / 100 % 100, codeIndexZero % 100);

        // onAlarmActivity에 데이터 전달을 위해 객체 저장
        alarmObjectForOnAlarm = alarmObject;

        // 다음 알람까지 얼마나 남았는지 일(Day), 시간(Hour), 분(Minute) 단위로 계산
        int diffDay = (codeIndexZero / 10000) - (timeCodeNow / 10000);
        int diffHour = (codeIndexZero / 100 % 100) - (timeCodeNow / 100 % 100);
        int diffMinute = (codeIndexZero % 100) - (timeCodeNow % 100);
        if(diffMinute < 0) {
            diffMinute += 60;
            diffHour--;
        }
        if(diffHour < 0) {
            diffHour += 24;
            diffDay--;
        }
        if(diffDay < 0) {
            if(allWeekisDisabled == true)
                diffDay += 1;
            else
                diffDay += 7;
        }

        // 다음 알람까지 남은 시간 출력
        String strResult = diffDay + "일 " + diffHour + "시간 " + diffMinute + "분 후에\n다음 알람이 울립니다.";
        Toast.makeText(MainActivity.this, strResult, Toast.LENGTH_SHORT).show();
        tvNextAlarm.setText(strResult);

    }

    // 알람 객체 새로고침
    public void updateAlarmObject (Intent data) {
        alarmObject.setTime(data.getIntExtra("hour", 0), data.getIntExtra("minute", 0));
        for(int i=0; i<7; i++)
            alarmObject.setWeek(i, data.getBooleanExtra("week_" + i, false));
        alarmObject.setName(data.getStringExtra("name"));
        alarmObject.setRingtone(Uri.parse(data.getStringExtra("ringtoneName")), data.getIntExtra("ringtoneVolume", 50));
        alarmObject.setVibration(data.getBooleanExtra("vibration", data.getBooleanExtra("vibration", false)));
        for(int i=0; i<3; i++)
            alarmObject.setMission(i, data.getBooleanExtra("mission_" + i, false));
        for(int i=0; i<2; i++)
            alarmObject.setPenalty(i, data.getBooleanExtra("penalty_" + i, false));
    }

}