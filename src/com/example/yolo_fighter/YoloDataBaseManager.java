package com.example.yolo_fighter;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class YoloDataBaseManager extends SQLiteOpenHelper {

	private static final String dbName = "yolodb.db";
	private static final String tbName = "playersinfo";
	private static final String keyID= "ID";
	private static final String IDOptions = "INTEGER PRIMARY KEY";
	private static final String keyName = "name";
	private static final String nameOptions = "TEXT NOT NULL";
	private static final String keyRace = "race";
	private static final String raceOptions = "TEXT NOT NULL";
	private static final String keyLevel = "level";
	private static final String levelOptions = " INTEGER DEFAULT 1";
	private static final String keyUnits = "units";
	private static final String unitsOptions = " INTEGER DEFAULT 0";
	 
	private static final String createTable =
			"CREATE TABLE " + tbName + "( " +
	keyID + " " + IDOptions + ", " +
	keyName + " " + nameOptions + ", " +
	keyRace + " " + raceOptions + ", " +
	keyLevel + " " + levelOptions + ", " +
	keyUnits + " " + unitsOptions + ");";
	
	
	public YoloDataBaseManager (Context context) {
		super(context, dbName , null, 1);
		//context.deleteDatabase(dbName);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public void addPlayer(YoloPlayerInfo playerInfo){
	SQLiteDatabase db = getWritableDatabase();
		ContentValues wartosciDoBazy = new ContentValues();
		wartosciDoBazy.put(keyName, playerInfo.getName());
		wartosciDoBazy.put(keyRace, playerInfo.getRace());
		wartosciDoBazy.put(keyLevel, playerInfo.getLevel());
		wartosciDoBazy.put(keyUnits, playerInfo.getUnits());
		db.insertOrThrow(tbName, null, wartosciDoBazy);
		db.close();
	}
	
	public void deletePlayer (int ID){
		SQLiteDatabase db = getWritableDatabase();
		String[] arguments={""+ID};
		db.delete(tbName, "ID=?", arguments);
		db.close();
	}
	
	public void closeDB() {
		SQLiteDatabase db = getReadableDatabase();
		db.close();
	}
	
	public YoloPlayerInfo getPlayerInfo(int ID){
		YoloPlayerInfo playerInfo = new YoloPlayerInfo();
		SQLiteDatabase db = getReadableDatabase();
		String[] kolumny={keyID, keyName, keyRace, keyLevel, keyUnits};
		String args[]={ID+""};
		Cursor kursor = db.query(tbName, kolumny, " ID=?", args, null, null, null, null);
		if(kursor!=null){
			kursor.moveToFirst();
			playerInfo.setID(kursor.getInt(0));
			playerInfo.setName(kursor.getString(1));
			playerInfo.setRace(kursor.getString(2));
			playerInfo.setLevel(kursor.getInt(3));
			playerInfo.setUnits(kursor.getInt(4));
		}
		kursor.close();
		db.close();
		return playerInfo;
	}
	
	public void updatePlayer(YoloPlayerInfo playerInfo)
	{
		SQLiteDatabase db = getReadableDatabase();
		ContentValues wartosciDoBazy = new ContentValues();
		wartosciDoBazy.put(keyName, playerInfo.getName());
		wartosciDoBazy.put(keyRace, playerInfo.getRace());
		wartosciDoBazy.put(keyLevel, playerInfo.getLevel());
		wartosciDoBazy.put(keyUnits, playerInfo.getUnits());
		String args[]={playerInfo.getID()+""};
		db.update(tbName, wartosciDoBazy, "ID=?", args);
	}
	
	public List<YoloPlayerInfo> getAll() {
		
		List<YoloPlayerInfo> playerInfoList = new LinkedList<YoloPlayerInfo>();
		String[] kolumny={keyID, keyName, keyRace, keyLevel, keyUnits};
		SQLiteDatabase db = getReadableDatabase();
		Cursor kursor = db.query(tbName, kolumny, null, null,null,null,null);
		while (kursor.moveToNext())
		{
			YoloPlayerInfo plInf = new YoloPlayerInfo();
			plInf.setID(kursor.getInt(0));
			plInf.setLevel(kursor.getInt(3));
			plInf.setName(kursor.getString(1));
			plInf.setRace(kursor.getString(2));
			plInf.setUnits(kursor.getInt(4));
			playerInfoList.add(plInf);
		}
		kursor.close();
		db.close();
		return playerInfoList;

	}
	
	
}
