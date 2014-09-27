package com.example.yolo_fighter;

public class YoloPlayerInfo {
	private int ID;
	private String name;
	private String race;
	private int level;
	private int units;
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRace() {
		return race;
	}
	public void setRace(String race) {
		this.race = race;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getUnits() {
		return units;
	}
	public void setUnits(int units) {
		this.units = units;
	}
	
}
