package com.example.missionalarm;

public class AlarmItem {
    int hour, minute;
    boolean [] week = new boolean[7];
    boolean vibration, ringtone;
    String name, mission, penalty;

    public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public void setWeek( int index, boolean boo) { this.week[index] = boo; }
    public void setName(String str) { this.name = str; }
    public void setVibration(boolean boo) { this.vibration = boo; }
    public void setRingtone(boolean boo) { this.ringtone = boo; }
    public void setMission(String str) { this.mission = str; }
    public void setPenalty(String str) { this.penalty = penalty; }

    public String getInfo() {
        int weekCount = 0;
        String[] weekStr = {"일", "월", "화", "수", "목", "금", "토"};
        String returnStr = String.format("%s\n%02d:%02d        ", name, hour, minute);
        for(int i=0; i<7; i++) {
            if (week[i] == true)
                weekCount++;
        }
        if(weekCount == 7)
            returnStr = returnStr.concat("매일");
        else {
            for(int i=0; i<7; i++) {
                if (week[i] == true)
                    returnStr = returnStr.concat(weekStr[i] + " ");
                else
                    returnStr = returnStr.concat("□ ");
            }
        }

        return returnStr;
    }

}
