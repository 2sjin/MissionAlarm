package com.example.missionalarm;

public class AlarmItem {
    int hour;
    int minute;
    String name;
    boolean vibration;
    boolean bell;
    String mission;
    String penalty;

    public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public void setName(String str) { this.name = str; }
    public void setVibration(boolean boo) { this.vibration = boo; }
    public void setBell(boolean boo) { this.bell = boo; }
    public void setMission(String str) { this.mission = str; }
    public void setPenalty(String str) { this.penalty = penalty; }

    public String getInfo() {
        return String.format("%02d:%02d\n%s", hour, minute, name);
    }

}
