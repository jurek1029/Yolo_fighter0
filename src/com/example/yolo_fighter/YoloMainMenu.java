package com.example.yolo_fighter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class YoloMainMenu extends Activity 
{
	YoloDataBaseManager dbm = new YoloDataBaseManager(this);
	int spinnerPosition=0;
	List<YoloPlayerInfo> plInfoList = new LinkedList<YoloPlayerInfo>();
	List<String> plNames = new ArrayList<String>(plInfoList.size());
	ArrayList<Integer> plIDs = new ArrayList<Integer>(plInfoList.size());
	
	@Override
	public void onCreate(Bundle savedInstanceState) 

	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
	
		
	//	dbm.close();
		
		
		/*YoloPlayerInfo playerInfo = new YoloPlayerInfo();
		playerInfo.setLevel(0);
		playerInfo.setRace("angel");
		playerInfo.setUnits(0);
		playerInfo.setName("YOLO FIGHTER");*/
		//dbm.addPlayer(playerInfo);
		
		/*for(int i=13; i<=19;i++){
			dbm.deletePlayer(i);
			Log.d("!!!", "usuwa");
		}*/
		
		
		/*List<YoloPlayerInfo> plInfoList = new LinkedList<YoloPlayerInfo>();
		plInfoList=dbm.getAll();
		for(int i=0;i<plInfoList.size(); i++)
		{
			YoloPlayerInfo playerInfo = plInfoList.get(i);
			System.out.println(playerInfo.getName());
			System.out.println(playerInfo.getID());
		}*/
		
		dbm.close();
		
		
		final Animation animMove = AnimationUtils.loadAnimation(this, R.layout.move);
		
		
		ImageButton create = (ImageButton) findViewById(R.id.btnCreate);
		ImageButton skills = (ImageButton) findViewById(R.id.btnSW);
		ImageButton join = (ImageButton) findViewById(R.id.btnJoin);
		
		create.getBackground().setAlpha(YoloEngine.MENU_BUTTON_ALPAH);
		skills.getBackground().setAlpha(YoloEngine.MENU_BUTTON_ALPAH);
		join.getBackground().setAlpha(YoloEngine.MENU_BUTTON_ALPAH);
		
	
		
		
	//	VideoView bgVideo = (VideoView)findViewById(R.id.bgVideo);
		
		
//        String uriPath = "android.resource://com.example.yolo_fighter/"+R.raw.blabla;
  //      Uri uri = Uri.parse(uriPath);
    //    bgVideo.setVideoURI(uri);
   //     bgVideo.requestFocus();
  //      bgVideo.start();
		
		
		join.startAnimation(animMove);
		skills.startAnimation(animMove);
		create.startAnimation(animMove);
	}
	
	public void createClick(View v)
	{
		//TODO tu co siê ma staæ po klikniêciu create'a
		Intent game = new Intent(getApplicationContext(),YoloGame.class);
		YoloMainMenu.this.startActivity(game);
		YoloEngine.context = getApplicationContext();
	}
	

	
	
	public void joinClick(View v)
	{
		//TODO tu to co siê ma staæ po klikniêciu join'a
		Intent game = new Intent(getApplicationContext(),YoloGame.class);
		YoloMainMenu.this.startActivity(game);
		YoloEngine.context = getApplicationContext();
	}
	
	public void skillsClick(View v)
	{
		setContentView(R.layout.player_menu);
		
	//	List<YoloPlayerInfo> plInfoList = new LinkedList<YoloPlayerInfo>();
		plInfoList=dbm.getAll();
	//	List<String> plNames = new ArrayList<String>(plInfoList.size());
	//	ArrayList<Integer> plIDs = new ArrayList<Integer>(plInfoList.size());
			
		for(int i=0;i<plInfoList.size(); i++)
		{
			YoloPlayerInfo playerInfo = plInfoList.get(i);
			plNames.add(playerInfo.getName());
			plIDs.add(playerInfo.getID());
		}
		
		
		Spinner spinner =  (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, plNames); 
		/*TextView txtViewStatCounter1 = (TextView) findViewById(R.id.stat1Counter);
		TextView txtViewStatCounter2 = (TextView) findViewById(R.id.stat2Counter);
		TextView txtViewStatCounter3 = (TextView) findViewById(R.id.stat3Counter);
		TextView txtViewStatCounter4 = (TextView) findViewById(R.id.stat4Counter);*/
		TextView txtViewUnitsCounter = (TextView) findViewById(R.id.unitsCounter);
		
		
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerArrayAdapter);
		
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

	        public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
	        	int currentID = plIDs.get(position);
	    		YoloEngine.currentPlayerInfo = dbm.getPlayerInfo(currentID);
	    		//txtViewUnitsCounter.setText(YoloEngine.currentPlayerInfo.getUnits()); 
	        }
	        public void onNothingSelected(AdapterView<?> arg0) { }
	    });
		
		
	}
	
	public void weaponClick(View v)
	{
		setContentView(R.layout.weapon_menu);
	}
	
	public void skill1Click(View v)
	{
		setContentView(R.layout.skill1_menu);
	}
	
	public void skill2Click(View v)
	{
		setContentView(R.layout.skill2_menu);
	}
	
	public void skill3Click(View v)
	{
		setContentView(R.layout.skill3_menu);
	}
	
	
	@Override
	public void onResume()
	{
		super.onResume();
	}
	
}
