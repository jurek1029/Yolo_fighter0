package com.example.yolo_fighter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

public class YoloMainMenu extends Activity 
{
	YoloDataBaseManager dbm = new YoloDataBaseManager(this);
	int spinnerPosition=0;
	List<YoloPlayerInfo> plInfoList = new LinkedList<YoloPlayerInfo>();
	List<String> plNames = new ArrayList<String>(plInfoList.size());
	ArrayList<Integer> plIDs = new ArrayList<Integer>(plInfoList.size());
	int newPlayerRace = 0;
	int currentSkill1Checked = 0;
	int currentSkill2Checked = 0;
	
// ------------------------- Multislayer BEGIN -----------------------
	
	private boolean mSignInprogress = false;
	private AlertDialog.Builder askInvitation; 
	private Invitation IncomingInvitation;
	private int RC_SELECT_PLAYERS;
	private int RC_SIGNIN = 9001;

	Button btn_quick;
	Button btn_invite;
	TextView debug_textview;
	

	
	RoomUpdateListener mRoomUpdateListener = new RoomUpdateListener() {
		// https://developer.android.com/reference/com/google/android/gms/games/multiplayer/realtime/RoomUpdateListener.html

		@Override
		public void onRoomCreated(int arg0, Room arg1) {
			YoloEngine.mRoom = arg1;
			System.out.println("Room created");
			debug_textview.setText("Room created");
		}

		@Override
		public void onRoomConnected(int statusCode, Room room) {

			System.out.println("Room connected");
			debug_textview.setText("Room connected");
			
			YoloEngine.mRoom = room;

			YoloEngine.mMultislayer.sendMessageToAllreliable((YoloEngine.SkillSprite1+"|"+YoloEngine.SkillSprite2+"|"+YoloEngine.SkillSprite3).getBytes());
			
			// byte[] ff = "test".getBytes();
		}

		@Override
		public void onLeftRoom(int arg0, String arg1) {

			System.out.println("Room left");
		}

		@Override
		public void onJoinedRoom(int arg0, Room arg1) {
			YoloEngine.mRoom = arg1;
			System.out.println("Room joined code: " + arg0);
			debug_textview.setText("Room joined code: " + arg0);
		}
	};
	
	RoomStatusUpdateListener mRoomStatusUpdateListener = new RoomStatusUpdateListener() {
		
		@Override
		public void onPeersDisconnected(Room arg0, List<String> arg1) {
			
			System.out.println("onPeersDisconnected");
			
		}
		
		@Override
		public void onPeersConnected(Room arg0, List<String> arg1) {
		
			System.out.println("onPeersConnected");	

		}

		@Override
		public void onConnectedToRoom(Room room) {

			
		}

		@Override
		public void onDisconnectedFromRoom(Room room) {

			
		}

		@Override
		public void onP2PConnected(String participantId) {

			
		}

		@Override
		public void onP2PDisconnected(String participantId) {

			
		}

		@Override
		public void onPeerDeclined(Room arg0, List<String> arg1) {

			
		}

		@Override
		public void onPeerInvitedToRoom(Room arg0, List<String> arg1) {

			
		}

		@Override
		public void onPeerJoined(Room arg0, List<String> arg1) {

			for(int i = 0; i < arg1.size(); i++){
				YoloEngine.opponents[i] = arg1.get(i);
			}
	
			System.out.println("onPeerJoined");
	
			
		}

		@Override
		public void onPeerLeft(Room arg0, List<String> arg1) {

			
		}

		@Override
		public void onRoomAutoMatching(Room room) {

			
		}

		@Override
		public void onRoomConnecting(Room room) {

			
		}
	};
	
	
	
// ------------------------- Multislayer END -------------------------
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 

	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		

		
// ------------------------- Multislayer BEGIN -----------------------

		btn_quick = (Button) findViewById(R.id.quick_button);
		btn_invite = (Button) findViewById(R.id.invite_button);
		debug_textview = (TextView) findViewById(R.id.textView1);
		btn_quick.setEnabled(false);
		btn_invite.setEnabled(false);
		
		askInvitation = new AlertDialog.Builder(YoloMainMenu.this).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				System.out.println("Invitation accepted");
				Games.RealTimeMultiplayer.join(YoloEngine.mHelper.getApiClient(), prepareGame(IncomingInvitation));
			}
		}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				System.out.println("Invitation rejected");
			}
		}).setIcon(android.R.drawable.ic_dialog_alert);
		
		YoloEngine.mHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		YoloEngine.mHelper.enableDebugLog(true);
		
		GameHelperListener listener = new GameHelper.GameHelperListener() {
			@Override
			public void onSignInSucceeded() {

				// mHelper.beginUserInitiatedSignIn();
				System.out.println("Signed in successfully");
				
				btn_quick.setEnabled(true);
				btn_invite.setEnabled(true);
				
				mSignInprogress = false;
				if (YoloEngine.mHelper.getInvitationId() != null) {
					// accept invitation

					Games.RealTimeMultiplayer.join(YoloEngine.mHelper.getApiClient(), prepareGame(YoloEngine.mHelper.getInvitation()));

					// prevent screen from sleeping during handshake
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

				}

				Games.Invitations.registerInvitationListener(YoloEngine.mHelper.getApiClient(), new OnInvitationReceivedListener() {

					@Override
					public void onInvitationRemoved(String invitationId) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onInvitationReceived(Invitation invitation) {
						// TODO Auto-generated method stub
						System.out.println("Invitation arrived, asking");
						IncomingInvitation = invitation;

						askInvitation.setTitle("New invitation");
						askInvitation.setMessage("Do u wanna accept, huh? from:" + invitation.getInviter().getDisplayName());
						askInvitation.show();

					}
				});

			}

			@Override
			public void onSignInFailed() {
				System.out.println("Signing in failed :(");
				System.out.println("ERROR: " + YoloEngine.mHelper.getSignInError());
				debug_textview.setText("sign in failed");
			}

		};

		YoloEngine.mHelper.setup(listener);
		
// ------------------------- Multislayer END -------------------------
		
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
		final Animation animMove2 = AnimationUtils.loadAnimation(this, R.layout.move);
		final Animation animMove3 = AnimationUtils.loadAnimation(this, R.layout.move);
		
		
		Button optionsBtn = (Button) findViewById(R.id.btnOptions);
		Button skillsBtn = (Button) findViewById(R.id.btnPM);
		Button playBtn = (Button) findViewById(R.id.btnPlay);
		
		
		
		animMove2.setStartOffset(500);
		animMove3.setStartOffset(1000);
		
		playBtn.startAnimation(animMove);
		skillsBtn.startAnimation(animMove2);
		optionsBtn.startAnimation(animMove3);
	}
	
	
//------------------------------------przyciski menu glowne----------------------
	public void optionsClick(View v)
	{
		YoloEngine.whichLayout = 1;
		/*Button isClasicBtn = (Button) findViewById(R.id.btnIsClasic);
		if(YoloEngine.isClasic==true) {
			isClasicBtn.setBackgroundResource(R.drawable.classiccontrolsoffbtn);
		}
		else {
			isClasicBtn.setBackgroundResource(R.drawable.classiccontrolsonbtn);
		}
		Button soundBtn = (Button) findViewById(R.id.btnSound);
		if(YoloEngine.enableSound==true) {
			soundBtn.setBackgroundResource(R.drawable.soundonbtn);
		}
		else {
			soundBtn.setBackgroundResource(R.drawable.soundoffbtn);
		}
		*/
		setContentView(R.layout.options_menu);
		
	}
	

	
	
	public void joinClick(View v)
	{
		
		// Te dwie instrukcje warto wrzuci� do jakiego� senwoengo eventu, �eby ci�gle tego nie od�wie�a� XXX
						
		
		
		
		Intent game = new Intent(getApplicationContext(),YoloGame.class);
		YoloMainMenu.this.startActivity(game);
		YoloEngine.context = getApplicationContext();
	}
	
	public void skillsClick(View v)
	{
		setContentView(R.layout.player_menu);
		YoloEngine.whichLayout = 1;
	//	List<YoloPlayerInfo> plInfoList = new LinkedList<YoloPlayerInfo>();
		plInfoList=dbm.getAll();
	//	List<String> plNames = new ArrayList<String>(plInfoList.size());
	//	ArrayList<Integer> plIDs = new ArrayList<Integer>(plInfoList.size());
		plNames.clear();
		plIDs.clear();
		for(int i=0;i<plInfoList.size(); i++)
		{
			YoloPlayerInfo playerInfo = plInfoList.get(i);
			plNames.add(playerInfo.getName());
			plIDs.add(playerInfo.getID());
		}
		
		
		Spinner spinner =  (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, plNames); 
	
		
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
		
		TextView txtViewStatCounter1 = (TextView) findViewById(R.id.stat1Counter);
		TextView txtViewStatCounter2 = (TextView) findViewById(R.id.stat2Counter);
		TextView txtViewStatCounter3 = (TextView) findViewById(R.id.stat3Counter);
		TextView txtViewStatCounter4 = (TextView) findViewById(R.id.stat4Counter);
		TextView txtViewCoinsCounter = (TextView) findViewById(R.id.coinsCounter);
		
		/*Integer st1 = YoloEngine.currentPlayerInfo.getST1();
		Integer st2 = YoloEngine.currentPlayerInfo.getST2();
		Integer st3 = YoloEngine.currentPlayerInfo.getST3();
		Integer st4 = YoloEngine.currentPlayerInfo.getST4();
		Integer coins = YoloEngine.currentPlayerInfo.getCoins();
		txtViewStatCounter1.setText(st1.toString());
		txtViewStatCounter2.setText(st2.toString());
		txtViewStatCounter3.setText(st3.toString());
		txtViewStatCounter4.setText(st4.toString());
		txtViewCoinsCounter.setText(coins.toString());*/
		
	}
//---------------------------------------------- przyciski player menu-------------------	
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
		switch(YoloEngine.currentPlayerInfo.getRace()) {
        case 0:
        	setContentView(R.layout.skill2angel_menu);
          break;
        case 1:
        	setContentView(R.layout.skill2devil_menu);
        	break;
        case 2:
        	setContentView(R.layout.skill2necromancer_menu);
        	break;
		}
	}
	
	
	public void addPlayerClick(View v)
	{
		setContentView(R.layout.addplayer_menu);
	}
	
	public void deletePlayerClick(View v)
	{
		dbm.deletePlayer(YoloEngine.currentPlayerInfo.getID());
		skillsClick(v);
	}
	
	//--------------------- add Player menu--------------------------
	public void angelClick(View v)
	{
		newPlayerRace = 0;
	}
	
	public void devilClick(View v)
	{
		newPlayerRace = 1;
	}
	
	public void necromancerClick(View v)
	{
		newPlayerRace = 2;
	}
	
	public void addPlayerinAddMenuClick(View v)
	{
		EditText newPlayerNameTxt = (EditText) findViewById(R.id.addPlTxtBox);
		YoloPlayerInfo newPlayerInfo = new YoloPlayerInfo();
		String plName = newPlayerNameTxt.getText().toString();
		newPlayerNameTxt.setText("");
		newPlayerInfo.setName(plName);
		newPlayerInfo.setRace(newPlayerRace);
		dbm.addPlayer(newPlayerInfo);
		
		skillsClick(v);
	}
	
	
	//-------------------options menu-----------------------------------------
	public void isClasicClick(View v)
	{
		Button isClasicBtn = (Button) findViewById(R.id.btnIsClasic);
		if(YoloEngine.isClasic==true) {
			YoloEngine.isClasic = false;
			isClasicBtn.setBackgroundResource(R.drawable.classiccontrolsbtnoff);
		}
		else {
			YoloEngine.isClasic = true;
			isClasicBtn.setBackgroundResource(R.drawable.classiccontrolsbtnon);
		}
	}
	
	public void soundClick(View v)
	{
		Button soundBtn = (Button) findViewById(R.id.btnSound);
		if(YoloEngine.enableSound==true) {
			YoloEngine.enableSound = false;
			soundBtn.setBackgroundResource(R.drawable.enablesoundbtnoff);
		}
		else {
			YoloEngine.enableSound = true;
			soundBtn.setBackgroundResource(R.drawable.enablesoundbtnon);
		}
	}
	
	public void backClick(View v)
	{
		YoloEngine.whichLayout = 0;
		setContentView(R.layout.main_menu);
	}
	
	
	@Override
	public void onResume()
	{
		super.onResume();
	}
//------------------------skill 1 menu-------------------------------------
	public void skills1BtnClick(View v) {
		
	      switch(v.getId()) {
	        case R.id.Skill1_1Btn:
	        	currentSkill1Checked = 1;
	          break;
	        case R.id.Skill1_2Btn:
	        	currentSkill1Checked = 2;
	          break;
	        case R.id.Skill1_3Btn:
	        	currentSkill1Checked = 3;
	          break;
	        case R.id.Skill1_4Btn:
	        	currentSkill1Checked = 4;
	          break;
	        case R.id.Skill1_5Btn:
	        	currentSkill1Checked = 5;
	          break;
	        case R.id.Skill1_6Btn:
	        	currentSkill1Checked = 6;
	          break;
	        case R.id.Skill1_7Btn:
	        	currentSkill1Checked = 7;
	          break;
	        case R.id.Skill1_8Btn:
	        	currentSkill1Checked = 8;
	          break;
	      }
	}
	
	public void skill1EqBtnClick(View v){
		Button currentSkill = (Button) findViewById(R.id.currentSkill1);
		String currentSkillTxt = Integer.toString(currentSkill1Checked);
		currentSkill.setText(currentSkillTxt);
		YoloEngine.SkillSprite1=currentSkill1Checked+3;
	}
	
//------------------------skill 2 menu---------------------------------
	public void skills2BtnClick(View v) {
		
	      switch(v.getId()) {
	        case R.id.Skill2_1Btn:
	        	currentSkill2Checked = 1;
	          break;
	      //  case R.id.Skill2necromancer_6Btn:
	        //	currentSkill2Checked = 6;
	          //break;
	        case R.id.Skill2_3Btn:
	        	currentSkill2Checked = 3;
	          break;
	        case R.id.Skill2_4Btn:
	        	currentSkill2Checked = 4;
	          break;
	        case R.id.Skill2_5Btn:
	        	currentSkill2Checked = 5;
	          break;
	        case R.id.Skill2_6Btn:
	        	currentSkill2Checked = 6;
	          break;
	        case R.id.Skill2_7Btn:
	        	currentSkill2Checked = 7;
	          break;
	        case R.id.Skill2_8Btn:
	        	currentSkill2Checked = 8;
	          break;
	      }
	}
	
	public void skills2necromancerBtnClick(View v) {
		
	      switch(v.getId()) {
	        case R.id.Skill2necromancer_handBtn:
	        	currentSkill2Checked = 9;
	          break;
	        case R.id.Skill2necromancer_archerBtn:
	        	currentSkill2Checked = 6;
	          break;
	        case R.id.Skill2necromancer_mummyBtn:
	        	currentSkill2Checked = 8;
	          break;
	        case R.id.Skill2necromancer_warriorBtn:
	        	currentSkill2Checked = 7;
	          break;
	        case R.id.Skill2necromancer_thunderBtn:
	        	currentSkill2Checked = 5;
	          break;
	        case R.id.Skill2necromancer_poisonBtn:
	        	currentSkill2Checked = 4;
	          break;
	        case R.id.Skill2_7Btn:
	        	currentSkill2Checked = 7;
	          break;
	        case R.id.Skill2_8Btn:
	        	currentSkill2Checked = 8;
	          break;
	      }
	}
	
	public void skill2necromancerEqBtnClick(View v){
		Button currentSkill = (Button) findViewById(R.id.currentSkillNecromancer2);
		switch(currentSkill2Checked) {
        case 9:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerhand1);
          break;
        case 6:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerarcher1);
          break;
        case 8:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancermummy1);
          break;
        case 7:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerwarrior1);
          break;
        case 5:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerthunder1);
          break;
        case 4:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerpoison1);
          break;
      }
		YoloEngine.SkillSprite2=currentSkill2Checked;
	}
	public void skill3necromancerEqBtnClick(View v){
		Button currentSkill = (Button) findViewById(R.id.currentSkillNecromancer3);
		switch(currentSkill2Checked) {
        case 9:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerhand1);
          break;
        case 6:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerarcher1);
          break;
        case 8:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancermummy1);
          break;
        case 7:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerwarrior1);
          break;
        case 5:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerthunder1);
          break;
        case 4:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerpoison1);
          break;
      }
		YoloEngine.SkillSprite3=currentSkill2Checked;
	}
	
	public void skill2EqBtnClick(View v){
		Button currentSkill = (Button) findViewById(R.id.currentSkill2);
		YoloEngine.SkillSprite2=currentSkill2Checked;
	}
	public void skill3EqBtnClick(View v){
		Button currentSkill = (Button) findViewById(R.id.currentSkill3);
		String currentSkillTxt = Integer.toString(currentSkill2Checked);
		currentSkill.setText(currentSkillTxt);
		YoloEngine.SkillSprite3=currentSkill2Checked;
	}
// ------------------------- Multislayer BEGIN -----------------------
	public void signIn(View v) {
		System.out.println("Signing in");
		mSignInprogress = true;
		YoloEngine.mHelper.beginUserInitiatedSignIn();
	}
	
	public void signOut(View v) {
		System.out.println("Signing out");
		
	//	btn_quick.setEnabled(false);
	//	btn_invite.setEnabled(false);
		
		if (YoloEngine.mHelper.getApiClient().isConnected()) {
			if (YoloEngine.mRoom != null)
				if (YoloEngine.mRoom.getStatus() != 6)
					Games.RealTimeMultiplayer.leave(YoloEngine.mHelper.getApiClient(), mRoomUpdateListener, YoloEngine.mRoom.getRoomId());

			//YoloEngine.mHelper.signOut();
			// mHelper.disconnect(); od��cza, nie wylogowuje
		}
		
	}
	
	
	public void startQuickGame(View v) {
		YoloEngine.MULTI_ACTIVE = true;
		
		Games.RealTimeMultiplayer.create(YoloEngine.mHelper.getApiClient(), prepareGame());

		// prevent screen from sleeping during handshake
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// go to game screen */
	}
	
	public void invite(View v) {
		YoloEngine.MULTI_ACTIVE = true;

		// request code for the "select players" UI
		// can be any number as long as it's unique
		RC_SELECT_PLAYERS = 10000;

		// launch the player selection screen
		// minimum: 1 other player; maximum: 3 other players
		Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(YoloEngine.mHelper.getApiClient(), 1, 3);
		startActivityForResult(intent, RC_SELECT_PLAYERS);
	}
	
	
	private RoomConfig prepareGame()
	{
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(mRoomUpdateListener);	
		roomConfigBuilder.setRoomStatusUpdateListener(mRoomStatusUpdateListener);
		roomConfigBuilder.setMessageReceivedListener(YoloEngine.mMultislayer.messageReceiver);
		
		// automatch criteria
		Bundle am = RoomConfig.createAutoMatchCriteria(1, 2, 0);
		roomConfigBuilder.setAutoMatchCriteria(am);
		
		RoomConfig roomConfig = roomConfigBuilder.build();	
		return roomConfig;
	}
	
	
	private RoomConfig prepareGame(Invitation invitation) {
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(mRoomUpdateListener);	
		roomConfigBuilder.setRoomStatusUpdateListener(mRoomStatusUpdateListener);
		roomConfigBuilder.setMessageReceivedListener(YoloEngine.mMultislayer.messageReceiver);
		
		roomConfigBuilder.setInvitationIdToAccept(invitation.getInvitationId());
		RoomConfig roomConfig = roomConfigBuilder.build();	
		return roomConfig;
	}
	
	
	@Override
	public void onActivityResult(int request, int response, Intent data) {
		
		if(request == RC_SIGNIN) { // Wracamy z powrotem do gameHelpera, kt�ry otworzy� okienko i chce wiedzie� co si� sta�o
			YoloEngine.mHelper.onActivityResult(request, response, data);
		}
		
		if (request == RC_SELECT_PLAYERS) {
			if (response != Activity.RESULT_OK) {
				// user canceled
				return;
			}

			// get the invitee list
			Bundle extras = data.getExtras();
			final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

			// get auto-match criteria
			Bundle autoMatchCriteria = null;
			int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
			int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

			if (minAutoMatchPlayers > 0) {
				autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0);
			} else {
				autoMatchCriteria = null;
			}

			// create the room and specify a variant if appropriate
			RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(mRoomUpdateListener);
			
			roomConfigBuilder.setMessageReceivedListener(YoloEngine.mMultislayer.messageReceiver);
			
			roomConfigBuilder.addPlayersToInvite(invitees);
			if (autoMatchCriteria != null) {
				roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
			}
			RoomConfig roomConfig = roomConfigBuilder.build();
			Games.RealTimeMultiplayer.create(YoloEngine.mHelper.getApiClient(), roomConfig);

			// prevent screen from sleeping during handshake
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}

	}
	
// ------------------------- Multislayer END -------------------------

public void onBackPressed() {
	if(YoloEngine.whichLayout==1)
	{
		YoloEngine.whichLayout=0;
		setContentView(R.layout.main_menu);
		
	}
	else {   
	Intent intent = new Intent(Intent.ACTION_MAIN);
	   intent.addCategory(Intent.CATEGORY_HOME);
	   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	   startActivity(intent);
	 }	
}
}

