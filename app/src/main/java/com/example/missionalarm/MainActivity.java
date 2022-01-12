package com.example.missionalarm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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

        // 다른 앱 위에 표시 권한 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우 권한 처리
            Intent intentDraw;
            if (Settings.canDrawOverlays(getApplicationContext()) == false) {  // 다른 앱 위에 표시 권한이 없을 때
                intentDraw = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intentDraw, 456);
                if (Settings.canDrawOverlays(getApplicationContext()) == false) {   // 다른 앱 위에 표시 권한이 없을 때
                    AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                    ad.setTitle("다른 앱 위에 표시 권한");
                    ad.setMessage("'다른 앱 위에 표시' 권한 활성화 후 애플리케이션을 다시 시작해주세요.");
                    ad.setCancelable(false);    // 다이얼로그 바깥 영역 클릭 시 취소되지 않음
                    ad.setNegativeButton("종료", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAndRemoveTask();
                            dialog.dismiss();
                        }
                    });
                    ad.show(); // 다이얼로그 출력
                }
            }
        }

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
        final int SMS_RECEIVE_PERMISSON=1;

        //권한이 부여되어 있는지 확인
        int permissonCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if(permissonCheck == PackageManager.PERMISSION_GRANTED){
        }else{
            //권한설정 dialog에서 거부를 누르면
            //ActivityCompat.shouldShowRequestPermissionRationale 메소드의 반환값이 true가 된다.
            //단, 사용자가 "Don't ask again"을 체크한 경우
            //거부하더라도 false를 반환하여, 직접 사용자가 권한을 부여하지 않는 이상, 권한을 요청할 수 없게 된다.
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)){
                //이곳에 권한이 왜 필요한지 설명하는 Toast나 dialog를 띄워준 후, 다시 권한을 요청한다.
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.RECEIVE_SMS}, SMS_RECEIVE_PERMISSON);
            }else{
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.RECEIVE_SMS}, SMS_RECEIVE_PERMISSON);
            }
        }

        String [] str = {"과유불급","문경지교","부화뇌동","망양지탄","누란지세","결자해지","은감불원","수기치인"
                ,"불광불급","수구초심"};
        SharedPreferences prefs = getSharedPreferences("test", MODE_PRIVATE);
        SharedPreferences.Editor editor= prefs.edit();
        editor.putString(str[0],"정도를 지나침은 미치지 못한 것과 같음.");
        editor.putString(str[1],"목을 베어 줄 수 있을 정도로 절친한 사귐");
        editor.putString(str[2],"우레 소리에 맞춰서 함께 한다‘는 뜻으로, 자신의 뚜렷한 소신 없이 " +
                "그저 남이 하는 대로 따라하는 것을 의미");
        editor.putString(str[3]," 달아난 양을 찾다가 여러 갈래 길에 이르러 길을 잃었다는 뜻으로," +
                "학문의 길이 여러 갈래로 나뉘어져 있어 진리를 찾기 어려움");
        editor.putString(str[4],"포개어 놓은 알의 형세라는 뜻으로, 몹시 위험한 형세를 "+
                "비유적으로 이르는 말");
        editor.putString(str[5],"일을 맺은 사람이 풀어야 한다.");
        editor.putString(str[6],"은나라의 거울은 먼 데 있지 않다. 전대인 하나라에 있다‘\n" +
                "* 은나라와 하나라는 모두 망했다.\n" +
                "본받을 만한 좋은 전례는 가까운 곳에 있다.");
        editor.putString(str[7],"자신의 몸과 마음을 닦은 후에 남을 다스림");
        editor.putString(str[8],"미치지 않으면 미치지 못한다.");
        editor.putString(str[9],"‘여우는 죽을 때 구릉을 향해 머리를 두고 초심으로 돌아간다‘란 뜻으로\n" +
                "근본을 잊지 않는 마음을 뜻하거나\n" +
                "죽음을 앞두고 고향을 그리워하는 마음을 의미");
        editor.commit();

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
                        // unregistAlarm();
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

    // 다른 액티비티에서 복귀 후
    @Override
    protected void onResume() {
        super.onResume();
        updateNextAlarm();
    }

    // 알람매니저에 알람 등록
    public void registAlarm(int day, int hour, int minute) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("day", day);
        intent.putExtra("hour", hour);
        intent.putExtra("minute", minute);
        startService(intent);
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
            finish();
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
        int timeCode;
        int timeCodeNow = (tNow.getDayOfWeek().getValue() * 10000) + (tNow.getHour() * 100) + tNow.getMinute();

        // 추가한 알람이 없을 경우
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
            allWeekisDisabled = false;
            for(int j=0; j<7; j++) {
                if(listBack.get(i).week[j] == true) {
                    timeCode = ((j+1) * 10000) + (listBack.get(i).hour * 100) + listBack.get(i).minute;
                    hashMapAlarm.put(timeCode, listBack.get(i));
                }
            }
        }

        // 모든 알람이 바활성화(요일 미선택) 상태일 경우
        if(hashMapAlarm.size() == 0) {
            tvNextAlarm.setText(textNoAlarm);
            //unregistAlarm();
            return;
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
        alarmObjectForOnAlarm = hashMapAlarmSorted.get(codeIndexZero);

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
        for(int i=0; i<SetAlarmActivity.MISSION_SIZE; i++)
            alarmObject.setMission(i, data.getBooleanExtra("mission_" + i, false));
        for(int i=0; i<SetAlarmActivity.PENALTY_SIZE; i++)
            alarmObject.setPenalty(i, data.getBooleanExtra("penalty_" + i, false));
    }

}