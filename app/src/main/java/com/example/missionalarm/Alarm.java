package com.example.missionalarm;

import android.net.Uri;

import java.io.Serializable;

public class Alarm implements Serializable {
    private static final long serialVersionUID = 1L;
    int hour, minute, ringtoneVolume, num;
    String name;
    Uri ringtoneName;
    boolean vibration;
    boolean [] week = new boolean[7];
    boolean [] mission = new boolean[3];
    boolean [] penalty = new boolean[2];

    // set 메소드
    public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }
    public void setRingtone(Uri uri, int volume) {
        this.ringtoneName = uri;
        this.ringtoneVolume = volume;
    }
    public void setName(String str) { this.name = str; }
    public void setVibration(boolean boo) { this.vibration = boo; }
    public void setWeek(int index, boolean boo) { this.week[index] = boo; }
    public void setMission(int index, boolean boo) { this.mission[index] = boo; }
    public void setPenalty(int index, boolean boo) { this.penalty[index] = boo; }

    // 알람 정보를 문자열로 리턴(문자열)
    public String getInfo() {
        int count = 0;
        String[] weekStr = {"일", "월", "화", "수", "목", "금", "토"};
        String returnStr = String.format("%s\n%02d:%02d        ", name, hour, minute);

        for(int i=0; i<7; i++) {
            if (week[i] == true)
                count++;
        }

        if(count == 7)
            returnStr = returnStr.concat("매일");
        else if(count == 0)
            returnStr = returnStr.concat("반복 없음");
        else {
            for(int i=0; i<7; i++) {
                if (week[i] == true)
                    returnStr = returnStr.concat(weekStr[i] + " ");
            }
        }

        returnStr = returnStr.concat("\n");
        count = 0;
        for(int i=0; i<3; i++) {
            if (mission[i] == true)
                count++;
        }
        if(count > 0)
            returnStr = returnStr.concat("[무작위 미션(" + count + ")]  ");
        if (penalty[0] == true)
            returnStr = returnStr.concat("[벌칙 1]  ");
        if (penalty[1] == true)
            returnStr = returnStr.concat("[벌칙 2]  ");

        return returnStr;
    }

}
