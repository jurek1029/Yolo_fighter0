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
	private static final String levelOptions = "INTEGER DEFAULT 1";
	private static final String keyXP = "XP";
	private static final String XPOptions = "INTEGER DEFAULT 0";
	private static final String keyCoins = "coins";
	private static final String coinsOptions = "INTEGER DEFAULT 0";
	private static final String keyUnits = "units";
	private static final String unitsOptions = "INTEGER DEFAULT 0";
	private static final String keyST1 = "ST1";
	private static final String ST1Options = "INTEGER DEFAULT 0";
	private static final String keyST2 = "ST2";
	private static final String ST2Options = "INTEGER DEFAULT 0";
	private static final String keyST3 = "ST3";
	private static final String ST3Options = "INTEGER DEFAULT 0";
	private static final String keyST4 = "ST4";
	private static final String ST4Options = "INTEGER DEFAULT 0";
	private static final String keySkill1 = "skill1";
	private static final String skill1Options = "INTEGER DEFAULT 1000000000";
	private static final String keySkill2 = "skill2";
	private static final String skill2Options = "INTEGER DEFAULT 1000000000";
	//private static final String keySkill3 = "skill3";
	//private static final String skill3Options = "INTEGER DEFAULT 1000000000";
	private static final String keyWeapon = "weapon";
	private static final String weaponOptions = "INTEGER DEFAULT 1000000000";
	private static final String keySK1EQ = "SK1EQ";
	private static final String SK1EQOptions = "INTEGER DEFAULT 0";
	private static final String keySK2EQ = "SK2EQ";
	private static final String SK2EQOptions = "INTEGER DEFAULT 0";
	private static final String keySK3EQ = "SK3EQ";
	private static final String SK3EQOptions = "INTEGER DEFAULT 0";
	private static final String keyWEQ = "WEQ";
	private static final String WEQOptions = "INTEGER DEFAULT 0";
	
	
	private static final String createTable =
			"CREATE TABLE " + tbName + " ( " +
	keyID + " " + IDOptions + ", " +
	keyName + " " + nameOptions + ", " +
	keyRace + " " + raceOptions + ", " +
	keyLevel + " " + levelOptions + ", " +
	keyXP + " " + XPOptions + ", " +
	keyCoins + " " + coinsOptions + ", " +
	keyUnits + " " + unitsOptions + ", " + 
	keyST1 + " " + ST1Options + ", " +
	keyST2 + " " + ST2Options + ", " +
	keyST3 + " " + ST3Options + ", " +
	keyST4 + " " + ST4Options + ", " +
	keySkill1 + " " + skill1Options + ", " +
	keySkill2 + " " + skill2Options + ", " +
	//keySkill3 + " " + skill3Options + ", " +
	keyWeapon + " " + weaponOptions + ", " +
	keySK1EQ + " " + SK1EQOptions + ", " +
	keySK2EQ + " " + SK2EQOptions + ", " +
	keySK3EQ + " " + SK3EQOptions + ", " +
	keyWEQ + " " + WEQOptions  +
	");";
	
	
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
		wartosciDoBazy.put(keyLevel, 1);
		wartosciDoBazy.put(keyXP, 0);
		wartosciDoBazy.put(keyCoins, 0);
		wartosciDoBazy.put(keyUnits, 0);
		wartosciDoBazy.put(keyST1, 0);
		wartosciDoBazy.put(keyST2, 0);
		wartosciDoBazy.put(keyST3, 0);
		wartosciDoBazy.put(keyST4, 0);
		wartosciDoBazy.put(keySkill1, 1000000000);
		wartosciDoBazy.put(keySkill2, 1000000000);
		//wartosciDoBazy.put(keySkill3, 1000000000);
		wartosciDoBazy.put(keyWeapon, 1000000000);
		wartosciDoBazy.put(keySK1EQ, 0);
		wartosciDoBazy.put(keySK2EQ, 0);
		wartosciDoBazy.put(keySK3EQ, 0);
		wartosciDoBazy.put(keyWEQ, 0);
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
		String[] kolumny={keyID, keyName, keyRace, keyLevel, keyXP, keyCoins, keyUnits, keyST1, keyST2, keyST3, keyST4, keySkill1, keySkill2, keyWeapon, keySK1EQ, keySK2EQ, keySK3EQ, keyWEQ};
		String args[]={ID+""};
		Cursor kursor = db.query(tbName, kolumny, " ID=?", args, null, null, null, null);
		if(kursor!=null){
			kursor.moveToFirst();
			playerInfo.setID(kursor.getInt(0));
			playerInfo.setName(kursor.getString(1));
			playerInfo.setRace(kursor.getString(2));
			playerInfo.setLevel(kursor.getInt(3));
			playerInfo.setXP(kursor.getInt(4));
			playerInfo.setCoins(kursor.getInt(5));
			playerInfo.setUnits(kursor.getInt(6));
			playerInfo.setST1(kursor.getInt(7));
			playerInfo.setST2(kursor.getInt(8));
			playerInfo.setST3(kursor.getInt(9));
			playerInfo.setST4(kursor.getInt(10));
			playerInfo.setSkill1(kursor.getInt(11));
			playerInfo.setSkill2(kursor.getInt(12));
			playerInfo.setWeapon(kursor.getInt(13));
			playerInfo.setSK1EQ(kursor.getInt(14));
			playerInfo.setSK2EQ(kursor.getInt(15));
			playerInfo.setSK3EQ(kursor.getInt(16));
			playerInfo.setWEQ(kursor.getInt(17));
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
		wartosciDoBazy.put(keyXP, playerInfo.getXP());
		wartosciDoBazy.put(keyCoins, playerInfo.getCoins());
		wartosciDoBazy.put(keyUnits, playerInfo.getUnits());
		wartosciDoBazy.put(keyST1, playerInfo.getST1());
		wartosciDoBazy.put(keyST2, playerInfo.getST2());
		wartosciDoBazy.put(keyST3, playerInfo.getST3());
		wartosciDoBazy.put(keyST4, playerInfo.getST4());
		wartosciDoBazy.put(keySkill1, playerInfo.getSkill1());
		wartosciDoBazy.put(keySkill2, playerInfo.getSkill2());
		//wartosciDoBazy.put(keySkill3, 1000000000);
		wartosciDoBazy.put(keyWeapon, playerInfo.getWeapon());
		wartosciDoBazy.put(keySK1EQ, playerInfo.getSK1EQ());
		wartosciDoBazy.put(keySK2EQ, playerInfo.getSK2EQ());
		wartosciDoBazy.put(keySK3EQ, playerInfo.getSK3EQ());
		wartosciDoBazy.put(keyWEQ, playerInfo.getWEQ());
		String args[]={playerInfo.getID()+""};
		db.update(tbName, wartosciDoBazy, "ID=?", args);
	}
	
	public List<YoloPlayerInfo> getAll() {
		
		List<YoloPlayerInfo> playerInfoList = new LinkedList<YoloPlayerInfo>();
		String[] kolumny={keyID, keyName, keyRace, keyLevel, keyXP, keyCoins, keyUnits, keyST1, keyST2, keyST3, keyST4, keySkill1, keySkill2, keyWeapon, keySK1EQ, keySK2EQ, keySK3EQ, keyWEQ};
		SQLiteDatabase db = getReadableDatabase();
		Cursor kursor = db.query(tbName, kolumny, null, null,null,null,null);
		while (kursor.moveToNext())
		{
			YoloPlayerInfo playerInfo = new YoloPlayerInfo();
			playerInfo.setID(kursor.getInt(0));
			playerInfo.setName(kursor.getString(1));
			playerInfo.setRace(kursor.getString(2));
			playerInfo.setLevel(kursor.getInt(3));
			playerInfo.setXP(kursor.getInt(4));
			playerInfo.setCoins(kursor.getInt(5));
			playerInfo.setUnits(kursor.getInt(6));
			playerInfo.setST1(kursor.getInt(7));
			playerInfo.setST2(kursor.getInt(8));
			playerInfo.setST3(kursor.getInt(9));
			playerInfo.setST4(kursor.getInt(10));
			playerInfo.setSkill1(kursor.getInt(11));
			playerInfo.setSkill2(kursor.getInt(12));
			playerInfo.setWeapon(kursor.getInt(13));
			playerInfo.setSK1EQ(kursor.getInt(14));
			playerInfo.setSK2EQ(kursor.getInt(15));
			playerInfo.setSK3EQ(kursor.getInt(16));
			playerInfo.setWEQ(kursor.getInt(17));
			playerInfoList.add(playerInfo);
		}
		kursor.close();
		db.close();
		return playerInfoList;

	}
	
	
}
