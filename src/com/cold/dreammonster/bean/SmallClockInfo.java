package com.cold.dreammonster.bean;

public class SmallClockInfo {
	public int hour;
	public int minute;
	public int alarmOn;
	public int weekNum;
	public int repeat;
	public String label;
	public int type;
	public String soundPath;
	public int id;
    
	
	public SmallClockInfo(int hour,int minute,int alarmOn,int weekNum,int repeat,String label,int type,String soundpath){
		this.hour=hour;
		this.minute=minute;
		this.alarmOn=alarmOn;
		this.weekNum=weekNum;
		this.repeat=repeat;
		this.label=label;
		this.type=type;
		this.soundPath=soundpath;
	}

}
