package com.example.yolo_fighter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

import static android.content.ContentResolver.*;

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
	
    private void debugLog(String text) {
        System.out.println(text);
        debug_textview.setText(text);
    }

	RoomUpdateListener mRoomUpdateListener = new RoomUpdateListener() {
		// https://developer.android.com/reference/com/google/android/gms/games/multiplayer/realtime/RoomUpdateListener.html

		@Override
		public void onRoomCreated(int statusCode, Room room) {
            if(statusCode == com.google.android.gms.games.GamesStatusCodes.STATUS_OK) {
                debugLog("Room created");
                YoloEngine.mRoom = room;
                YoloEngine.playerParticipantID = YoloEngine.mRoom.getParticipantId(Games.Players.getCurrentPlayerId(YoloEngine.mHelper.getApiClient()));

                YoloEngine.teamA.clear();
                YoloEngine.teamB.clear();
                YoloEngine.opponents.clear();
                if(YoloEngine.participants != null) YoloEngine.participants.clear();

                // @TODO temporarry
                YoloEngine.teamA.add(YoloEngine.playerParticipantID);
            }
            else
               debugLog("Error "+statusCode);
		}

		@Override
		public void onRoomConnected(int statusCode, Room room) {
            if(statusCode == GamesStatusCodes.STATUS_OK) {
                debugLog("All participants connected");

                YoloEngine.teamA.clear(); //@TODO temporarry

                YoloEngine.MULTI_ACTIVE = true;

                YoloEngine.mRoom = room;
                YoloEngine.mMultislayer.sendMessageToAllreliable(YoloEngine.mMultislayer.sendSpriteLoad(new int[]{YoloEngine.SkillSprite1, YoloEngine.SkillSprite2, YoloEngine.SkillSprite3}));

                YoloEngine.playerParticipantID = YoloEngine.mRoom.getParticipantId(Games.Players.getCurrentPlayerId(YoloEngine.mHelper.getApiClient()));
                YoloEngine.participants = YoloEngine.mRoom.getParticipants();
                Collections.sort(YoloEngine.participants, new Comparator<Participant>() {
                    @Override
                    public int compare(Participant lhs, Participant rhs) {
                        return lhs.getParticipantId().compareTo(rhs.getParticipantId());
                    }
                });

                for(Participant p : YoloEngine.participants) {
                    if(p.getStatus() == Participant.STATUS_JOINED && p.getParticipantId() != YoloEngine.playerParticipantID) {
                        YoloEngine.opponents.add(p.getParticipantId());
                    }
                }

                if(YoloEngine.playerParticipantID.equals(YoloEngine.participants.get(0).getParticipantId())) {
                    String teamAssignPattern = "1";

                    // Team assignment dokąd {0-teamA, 1-teamB}

                    // Przydzielamy nam
                    if (new Random().nextBoolean()) {
                        YoloEngine.teamA.add(YoloEngine.playerParticipantID);
                        YoloEngine.playerTeam = false;
                        teamAssignPattern += "0";
                    }
                    else {
                        YoloEngine.teamB.add(YoloEngine.playerParticipantID);
                        YoloEngine.playerTeam = true;
                        teamAssignPattern += "1";
                    }

                    // Przydzielamy reszcie graczy
                    for (Participant p : YoloEngine.participants) {
                        if (!(YoloEngine.playerParticipantID.equals(p.getParticipantId()) || p.getStatus() != Participant.STATUS_JOINED || YoloEngine.teamA.contains(p.getParticipantId()) || YoloEngine.teamB.contains(p.getParticipantId()))) { // nie jesteśmy to my, gracz nie należy jeszcze do żadnego teamu
                            if (YoloEngine.teamA.size() > YoloEngine.teamB.size()) {
                                YoloEngine.teamB.add(p.getParticipantId());
                                teamAssignPattern += "1";
                            } else if (YoloEngine.teamA.size() < YoloEngine.teamB.size()) {
                                YoloEngine.teamA.add(p.getParticipantId());
                                teamAssignPattern += "0";
                            } else {
                                if (new Random().nextBoolean()) {
                                    YoloEngine.teamA.add(p.getParticipantId());
                                    teamAssignPattern += "0";
                                } else {
                                    YoloEngine.teamB.add(p.getParticipantId());
                                    teamAssignPattern += "1";
                                }
                            }
                        }
                    }

                    YoloEngine.mMultislayer.sendTeamAssignment(Integer.parseInt(teamAssignPattern, 2));
                  //  YoloGameRenderer.givePlayerID();
                }
                
                YoloEngine.mMultislayer.sendMaxLife();


            }
			else if(statusCode == GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED)
                debugLog("Client reconnect required");
            else if(statusCode == GamesStatusCodes.STATUS_INTERNAL_ERROR)
                debugLog("Error when connecting");
		}

		@Override
		public void onLeftRoom(int arg0, String arg1) {
            debugLog("Room left");

            YoloEngine.teamA.clear();
            YoloEngine.teamB.clear();
            YoloEngine.opponents.clear();
            if(YoloEngine.participants != null) YoloEngine.participants.clear();
		}

		@Override
		public void onJoinedRoom(int statusCode, Room room) {
            if(statusCode == GamesStatusCodes.STATUS_OK) {
                debugLog("Room joined");

                YoloEngine.teamA.clear();
                YoloEngine.teamB.clear();
                YoloEngine.opponents.clear();
                if(YoloEngine.participants != null) YoloEngine.participants.clear();

                YoloEngine.mRoom = room;
                YoloEngine.playerParticipantID = YoloEngine.mRoom.getParticipantId(Games.Players.getCurrentPlayerId(YoloEngine.mHelper.getApiClient()));
            }
		}
	};

	RoomStatusUpdateListener mRoomStatusUpdateListener = new RoomStatusUpdateListener() {

        @Override
        public void onPeerJoined(Room arg0, List<String> arg1) {
            // ktoś dołączył (join)
             System.out.println("onPeerJoined");
        }

        @Override
        public void onPeersConnected(Room room, List<String> participantIds) {
            // ktoś podłaczył się

            YoloEngine.mRoom = room;
            System.out.println("onPeersConnected, ");
        }

        @Override
        public void onPeersDisconnected(Room room, List<String> participantIds) {
            YoloEngine.mRoom = room;

            System.out.println("onPeersDisconnected");

            for (String p : participantIds) {
                YoloEngine.teamA.remove(p);
                YoloEngine.teamB.remove(p);
                YoloEngine.opponents.remove(p);
            }

            if(YoloEngine.opponents.size() == 0)
                debugLog("No other players left");
        }

        @Override
		public void onConnectedToRoom(Room room) {
            debugLog("Client connected to room");
		}

		@Override
		public void onDisconnectedFromRoom(Room room) {

			
		}

		@Override
		public void onP2PConnected(String participantId) {
			// ignore
		}

		@Override
		public void onP2PDisconnected(String participantId) {
			// ignore
		}

		@Override
		public void onPeerDeclined(Room arg0, List<String> arg1) {

			
		}

		@Override
		public void onPeerInvitedToRoom(Room arg0, List<String> arg1) {

			
		}



		@Override
		public void onPeerLeft(Room arg0, List<String> arg1) {
            // Ktoś lub więcej ludzi uciekło
            // @TODO informacja w grze, że ktoś odłączył się, usunąć jego postać
		}

		@Override
		public void onRoomAutoMatching(Room room) {
            // STATUS: Automatching started
		}

		@Override
		public void onRoomConnecting(Room room) {
            // STATUS: Someone has joined and his conection is being established
		}
	};
	
	
	
// ------------------------- Multislayer END -------------------------
	

	@Override
	public void onCreate(Bundle savedInstanceState) 

	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);

        setMasterSyncAutomatically(true); // AUTO SYNC niezbędny dla wysyłania zaproszeń
		
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
				debugLog("Signed in successfully");
				
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


					}

					@Override
					public void onInvitationReceived(Invitation invitation) {
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
		plInfoList.clear();
		plInfoList=dbm.getAll();
		YoloEngine.whichLayout = 1;
		if (plInfoList.size()==0) setContentView(R.layout.addplayer_menu);
		else{
		YoloEngine.currentPlayerInfo = plInfoList.get(YoloEngine.currentPlayerInfoPosition);
		switch(YoloEngine.currentPlayerInfo.getRace()) {
        case 0:
        	setContentView(R.layout.skill2angel_menu);
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK2EQ();
        	v.setId(R.id.Skill2EqBtn);
        	System.out.println("powinien pierwszy");
        	skill2angelEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite2);
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK3EQ();
        	v.setId(R.id.Skill3EqBtn);
        	System.out.println("powinien drugi");
        	skill2angelEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite3);
        	setContentView(R.layout.main_menu);
          break;
        case 1:
        	break;
        case 2:
        	setContentView(R.layout.skill2necromancer_menu);
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK2EQ();
        	v.setId(R.id.Skill2EqBtn);
        	System.out.println("powinien pierwszy");
        	skill2necromancerEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite2);
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK3EQ();
        	v.setId(R.id.Skill3EqBtn);
        	System.out.println("powinien drugi");
        	skill2necromancerEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite3);
        	setContentView(R.layout.main_menu);
        	break;
		}
		YoloEngine.SkillSprite2 = YoloEngine.currentPlayerInfo.getSK2EQ();
		YoloEngine.SkillSprite3 = YoloEngine.currentPlayerInfo.getSK3EQ();
		Intent game = new Intent(getApplicationContext(),YoloGame.class);
		YoloMainMenu.this.startActivity(game);
		finish();
		YoloEngine.context = getApplicationContext();
		dbm.close();
		}
	}
	
	public void skillsClick(View v)
	{
		plInfoList=dbm.getAll();
		YoloEngine.whichLayout = 1;
		if (plInfoList.size()==0) setContentView(R.layout.addplayer_menu);
		else
		{
		setContentView(R.layout.player_menu);
	//	List<YoloPlayerInfo> plInfoList = new LinkedList<YoloPlayerInfo>();
		
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
		
		//int currentID = plIDs.get(YoloEngine.currentPlayerInfoPosition);
		//YoloEngine.currentPlayerInfo = dbm.getPlayerInfo(currentID);
		YoloEngine.currentPlayerInfo = plInfoList.get(YoloEngine.currentPlayerInfoPosition);
		//System.out.println(YoloEngine.currentPlayerInfoPosition+"lojo");
		
		Spinner spinner =  (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, plNames); 
	
		
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerArrayAdapter);
		spinner.setSelection(YoloEngine.currentPlayerInfoPosition);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

	        public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
	        	int currentID = plIDs.get(position);
	        	YoloEngine.currentPlayerInfoPosition = position;
	        	//System.out.println(position+"!!!!!!!!!!!!!!!!!!");
	    		YoloEngine.currentPlayerInfo = dbm.getPlayerInfo(currentID);
	    		//skillsClick(view);
	    		TextView txtViewStatCounter1 = (TextView) findViewById(R.id.stat1Counter);
	    		TextView txtViewStatCounter2 = (TextView) findViewById(R.id.stat2Counter);
	    		TextView txtViewStatCounter3 = (TextView) findViewById(R.id.stat3Counter);
	    		TextView txtViewStatCounter4 = (TextView) findViewById(R.id.stat4Counter);
	    		TextView txtViewCoinsCounter = (TextView) findViewById(R.id.coinsCounter);
	    		TextView txtViewCoinsST1Counter = (TextView) findViewById(R.id.stat1coinsTxt);
	    		TextView txtViewCoinsST2Counter = (TextView) findViewById(R.id.stat2coinsTxt);
	    		TextView txtViewCoinsST3Counter = (TextView) findViewById(R.id.stat3coinsTxt);
	    		TextView txtViewCoinsST4Counter = (TextView) findViewById(R.id.stat4coinsTxt);
	    		
	    		
	    		Integer st1 = YoloEngine.currentPlayerInfo.getST1();
	    		Integer st2 = YoloEngine.currentPlayerInfo.getST2();
	    		Integer st3 = YoloEngine.currentPlayerInfo.getST3();
	    		Integer st4 = YoloEngine.currentPlayerInfo.getST4();
	    		Integer coins = YoloEngine.currentPlayerInfo.getCoins();
	    		Integer st1Cost = YoloEngine.ST1Cost;
	    		Integer st2Cost = YoloEngine.ST2Cost;
	    		Integer st3Cost = YoloEngine.ST3Cost;
	    		Integer st4Cost = YoloEngine.ST4Cost;
	    		txtViewStatCounter1.setText(st1.toString());
	    		txtViewStatCounter2.setText(st2.toString());
	    		txtViewStatCounter3.setText(st3.toString());
	    		txtViewStatCounter4.setText(st4.toString());
	    		txtViewCoinsCounter.setText(coins.toString());
	    		txtViewCoinsST1Counter.setText(st1Cost.toString());
	    		txtViewCoinsST2Counter.setText(st2Cost.toString());
	    		txtViewCoinsST3Counter.setText(st3Cost.toString());
	    		txtViewCoinsST4Counter.setText(st4Cost.toString());
	    		//txtViewUnitsCounter.setText(YoloEngine.currentPlayerInfo.getUnits()); 
	        }
	        public void onNothingSelected(AdapterView<?> arg0) {
	        }
	    });
		
		
		}
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
        	setContentView(R.layout.skill2angel_menu);
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK2EQ();
        	v.setId(R.id.Skill2EqBtn);
        	System.out.println("powinien pierwszy");
        	skill2angelEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite2);
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK3EQ();
        	v.setId(R.id.Skill3EqBtn);
        	System.out.println("powinien drugi");
        	skill2angelEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite3);
          break;
        case 1:
        	setContentView(R.layout.skill2devil_menu);
        	break;
        case 2:
        	setContentView(R.layout.skill2necromancer_menu);
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK2EQ();
        	v.setId(R.id.Skill2EqBtn);
        	System.out.println("powinien pierwszy");
        	skill2necromancerEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite2);
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK3EQ();
        	v.setId(R.id.Skill3EqBtn);
        	System.out.println("powinien drugi");
        	skill2necromancerEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite3);
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
	//TODO do plus�w trzeba doda� zmian� STcost
	public void plusClick(View v)
	{
		switch(v.getId()) {
        case R.id.buttonplus1:
        	if(YoloEngine.ST1Cost<=YoloEngine.currentPlayerInfo.getCoins())
        		{
        		 YoloEngine.currentPlayerInfo.setCoins(YoloEngine.currentPlayerInfo.getCoins()-YoloEngine.ST1Cost);
        		 YoloEngine.currentPlayerInfo.setST1(YoloEngine.currentPlayerInfo.getST1()+1);
        		 dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        		}
          break;
        case R.id.buttonplus2:
        	if(YoloEngine.ST2Cost<=YoloEngine.currentPlayerInfo.getCoins())
        		{
        		 YoloEngine.currentPlayerInfo.setCoins(YoloEngine.currentPlayerInfo.getCoins()-YoloEngine.ST2Cost);
        		 YoloEngine.currentPlayerInfo.setST2(YoloEngine.currentPlayerInfo.getST2()+1);
        		 dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        		}
          break;
        case R.id.buttonplus3:
        	if(YoloEngine.ST3Cost<=YoloEngine.currentPlayerInfo.getCoins())
        		{
        		 YoloEngine.currentPlayerInfo.setCoins(YoloEngine.currentPlayerInfo.getCoins()-YoloEngine.ST3Cost);
        		 YoloEngine.currentPlayerInfo.setST3(YoloEngine.currentPlayerInfo.getST3()+1);
        		 dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        		}
          break;
        case R.id.buttonplus4:
        	if(YoloEngine.ST4Cost<=YoloEngine.currentPlayerInfo.getCoins())
        		{
        		 YoloEngine.currentPlayerInfo.setCoins(YoloEngine.currentPlayerInfo.getCoins()-YoloEngine.ST4Cost);
        		 YoloEngine.currentPlayerInfo.setST4(YoloEngine.currentPlayerInfo.getST4()+1);
        		 dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        		}
          break;
		}
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
		
		TextView description = (TextView) findViewById(R.id.skill2necromancerdescription);
		TextView lvl = (TextView) findViewById(R.id.skill2necromancerlvlneeded);
		TextView cost = (TextView) findViewById(R.id.skill2necromancercost);
		
	      switch(v.getId()) {
	        case R.id.Skill2necromancer_poisonBtn:
	        	currentSkill2Checked = 4;
	        	description.setText("Empoison your enemies with a cloud of toxic gas.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;
	        case R.id.Skill2necromancer_archerBtn:
	        	currentSkill2Checked = 6;
	        	description.setText("Summon a dangerous archer firing with a bow from a long distance.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;
	        case R.id.Skill2necromancer_warriorBtn:
	        	currentSkill2Checked = 7;
	        	description.setText("Warrior will pursue enemies and bereave them of any chance of survival.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;
	        case R.id.Skill2necromancer_mummyBtn:
	        	currentSkill2Checked = 8;
	        	description.setText("Dead mummy will be bane of your yet living enemies.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;
	        case R.id.Skill2necromancer_handBtn:
	        	currentSkill2Checked = 9;
	        	description.setText("Summon a rough hand striking out from a short distance.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;
	        case R.id.Skill2necromancer_barrelBtn:
	        	currentSkill2Checked = 10;
	        	description.setText("Throw a rolling barrel filled with explosive materials.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;
	        case R.id.Skill2necromancer_towerBtn:
	        	currentSkill2Checked = 11;
	        	description.setText("Tower absorbs bullets and fires with bolts.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;
	        case R.id.Skill2necromancer_wallBtn:
	        	currentSkill2Checked = 12;
	        	description.setText("Build a defensive wall which absorbs deadly bullets.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	        	break;
	        case R.id.Skill2necromancer_lifedrainBtn:
	        	currentSkill2Checked = 108;
	        	description.setText("Suck oomph of your enemies depriving them of grace of life.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	        	break;
	        case R.id.Skill2necromancer_resurrectionBtn:
	        	currentSkill2Checked = 109;
	        	description.setText("Raise your allies to reach ultimate success.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;
	        case R.id.Skill2necromancer_spikeBtn:
	        	currentSkill2Checked = 15;
	        	description.setText("Exsert lethal spikes from the ground.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;
	        case R.id.Skill2necromancer_slowdownBtn:
	        	currentSkill2Checked = 103;
	        	description.setText("Handicap your foes by inhibiting their movement.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;
	        
	      }
	}
	
	public void skill2necromancerEqBtnClick(View v){
		Button currentSkill;
		if (v.getId()==R.id.Skill2EqBtn)
		{
			currentSkill = (Button) findViewById(R.id.currentSkillNecromancer2);
		}
		else {
			currentSkill = (Button) findViewById(R.id.currentSkillNecromancer3);
		}
		int animationSlowdown = 0;
		float animationDuration = 0f;
		switch(currentSkill2Checked) {
		case 4:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerpoison1);
        	animationSlowdown = 0;
        	animationDuration = 57f;
          break;
		case 5:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerthunder1);
        	animationSlowdown = 0;
        	animationDuration = 57f;
          break;
		case 6:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerarcher1);
        	animationSlowdown = 10;
        	animationDuration = 0f;
          break;
        case 7:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerwarrior1);
        	animationSlowdown = 7;
        	animationDuration = 2f;
          break;
        case 8:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancermummy1);
        	animationSlowdown = 15;
        	animationDuration = 2f;
        break;
        case 9:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerhand1);
        	animationSlowdown = 10;
        	animationDuration = 2f;
          break;
        case 10:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerbarrel1);
        	animationSlowdown = 5;
        	animationDuration = 10f;
          break;
        case 11:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancertower1);
        	animationSlowdown = 10;
        	animationDuration = 0f;
        	break;
        case 12:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerwall1);
        	animationSlowdown = 10;
        	animationDuration = 0f;
          break;
        case 108:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerlifesuck1);
        	animationSlowdown = 0;
        	animationDuration = 0f;
          break;
        case 109:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerresurrection1);
        	animationSlowdown = 0;
        	animationDuration = 0f;
          break;
        case 15:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerspike1);
        	animationSlowdown = 0;
        	animationDuration = 20f;
          break;
        case 103:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerslowdown1);
        	animationSlowdown = 0;
        	animationDuration = 0f;
          break;
      }
		if (v.getId()==R.id.Skill2EqBtn)
		{
			YoloEngine.currentPlayerInfo.setSK2EQ(currentSkill2Checked);
			YoloEngine.animationSlowdown2=animationSlowdown;
			YoloEngine.animationDuration2=animationDuration;
			YoloEngine.SkillSprite2=currentSkill2Checked;
			System.out.println("pierwszy skill loaded "+YoloEngine.SkillSprite2);
		}
		else {
			YoloEngine.currentPlayerInfo.setSK3EQ(currentSkill2Checked);
			YoloEngine.animationSlowdown3=animationSlowdown;
			YoloEngine.animationDuration3=animationDuration;
			YoloEngine.SkillSprite3=currentSkill2Checked;
			System.out.println("drugi skill loaded "+YoloEngine.SkillSprite3);
		}
		dbm.updatePlayer(YoloEngine.currentPlayerInfo);
		
	}
	/*public void skill3necromancerEqBtnClick(View v){
		Button currentSkill = (Button) findViewById(R.id.currentSkillNecromancer3);
		switch(currentSkill2Checked) {
		case 4:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerpoison1);
        	YoloEngine.currentPlayerInfo.setSK3EQ(4);
        	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        	YoloEngine.animationSlowdown3 = 0;
        	YoloEngine.animationDuration3 = 57f;
          break;
		case 5:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerthunder1);
        	YoloEngine.currentPlayerInfo.setSK3EQ(5);
        	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        	YoloEngine.animationSlowdown3 = 0;
        	YoloEngine.animationDuration3 = 57f;
          break;
		case 6:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerarcher1);
        	YoloEngine.currentPlayerInfo.setSK3EQ(6);
        	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        	YoloEngine.animationSlowdown3 = 10;
        	YoloEngine.animationDuration3 = 0f;
          break;
        case 7:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerwarrior1);
        	YoloEngine.currentPlayerInfo.setSK3EQ(7);
        	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        	YoloEngine.animationSlowdown3 = 7;
        	YoloEngine.animationDuration3 = 2f;
          break;
        case 8:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancermummy1);
        	YoloEngine.currentPlayerInfo.setSK3EQ(8);
        	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        	YoloEngine.animationSlowdown3 = 15;
        	YoloEngine.animationDuration3 = 2f;
        break;
        case 9:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerhand1);
        	YoloEngine.currentPlayerInfo.setSK3EQ(9);
        	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        	YoloEngine.animationSlowdown3 = 10;
        	YoloEngine.animationDuration3 = 2f;
          break;
        case 10:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerbarrel1);
        	YoloEngine.currentPlayerInfo.setSK3EQ(10);
        	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        	YoloEngine.animationSlowdown3 = 5;
        	YoloEngine.animationDuration3 = 10f;
          break;
        case 11:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancertower1);
        	YoloEngine.currentPlayerInfo.setSK3EQ(11);
        	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        	YoloEngine.animationSlowdown3 = 10;
        	YoloEngine.animationDuration3 = 0f;
        	break;
        case 12:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerwall1);
        	YoloEngine.currentPlayerInfo.setSK3EQ(12);
        	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        	YoloEngine.animationSlowdown3 = 10;
        	YoloEngine.animationDuration3 = 0f;
          break;
        case 108:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerlifesuck1);
        	YoloEngine.currentPlayerInfo.setSK3EQ(108);
        	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        	YoloEngine.animationSlowdown3 = 0;
        	YoloEngine.animationDuration3 = 0f;
          break;
        case 109:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerresurrection1);
        	YoloEngine.currentPlayerInfo.setSK3EQ(109);
        	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        	YoloEngine.animationSlowdown3 = 0;
        	YoloEngine.animationDuration3 = 0f;
          break;
        case 15:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerspike1);
        	YoloEngine.currentPlayerInfo.setSK3EQ(15);
        	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        	YoloEngine.animationSlowdown3 = 0;
        	YoloEngine.animationDuration3 = 20f;
          break;
        case 103:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerslowdown1);
        	YoloEngine.currentPlayerInfo.setSK3EQ(103);
        	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
        	YoloEngine.animationSlowdown3 = 0;
        	YoloEngine.animationDuration3 = 0f;
          break;
      }
		YoloEngine.SkillSprite3=currentSkill2Checked;
	}
	*/
	
public void skills2angelBtnClick(View v) {
		
		TextView description = (TextView) findViewById(R.id.skill2angeldescription);
		TextView lvl = (TextView) findViewById(R.id.skill2angellvlneeded);
		TextView cost = (TextView) findViewById(R.id.skill2angelcost);
		
	      switch(v.getId()) {
	        case R.id.Skill2angel_icicleBtn:
	        	currentSkill2Checked = 19;
	        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;
	        case R.id.Skill2angel_thunderBtn:
	        	currentSkill2Checked = 5;
	        	description.setText("Any enemy hiding somewhere? Flush him out with your lightning.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;
	        case R.id.Skill2angel_piorunBtn:
	        	currentSkill2Checked = 26;
	        	description.setText("Magic bullets forged by no one else but Zeus dazzle your enemies.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;  
	        case R.id.Skill2angel_healBtn:
	        	currentSkill2Checked = 102;
	        	description.setText("Everybody make mistakes. Heal yourself and forget about it.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;
	        case R.id.Skill2angel_heal2Btn:
	        	currentSkill2Checked = 104;
	        	description.setText("Heal yourself and your friends.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;  
	        case R.id.Skill2angel_warmthBtn:
	        	currentSkill2Checked = 14;
	        	description.setText("Small aura following you heals you and your friends.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;  
	        case R.id.Skill2angel_potionBtn:
	        	currentSkill2Checked = 27;
	        	description.setText("Long lasting, but effective healing.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;    
	        case R.id.Skill2angel_shockwaveBtn:
	        	currentSkill2Checked = 18;
	        	description.setText("Freeze your enemies and leave them in the lurch. Or in the grace of your weapon.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;  
	        case R.id.Skill2angel_smokeBtn:
	        	currentSkill2Checked = 20;
	        	description.setText("Smoke denudes sight of the battlefield.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;  
	        case R.id.Skill2angel_trapBtn:
	        	currentSkill2Checked = 13;
	        	description.setText("Lay a trap for all these fo(ol)es and freeze them.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break; 
	        case R.id.Skill2angel_wingsBtn:
	        	currentSkill2Checked = 23;
	        	description.setText("What are these wings for? For flying of course.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;
	        case R.id.Skill2angel_denialBtn:
	        	currentSkill2Checked = 28;
	        	description.setText("Use their bullets against themselves. Return projectiles to sender.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;
	        case R.id.Skill2angel_defBtn:
	        	currentSkill2Checked = 24;
	        	description.setText("Decrease quantity of damage you receive by increasing your defence.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;  
	        case R.id.Skill2angel_infinityBtn:
	        	currentSkill2Checked = 25;
	        	description.setText("They will run a mile. Become immortal for the short period of time.");
	        	lvl.setText("10lvl");
	        	cost.setText("400");
	          break;
	      }
	}
	
public void skill2angelEqBtnClick(View v){
	Button currentSkill;
	if (v.getId()==R.id.Skill2EqBtn)
	{
		currentSkill = (Button) findViewById(R.id.currentSkillAngel2);
	}
	else {
		currentSkill = (Button) findViewById(R.id.currentSkillAngel3);
	}
	int animationSlowdown = 0;
	float animationDuration = 0f;
	switch(currentSkill2Checked) {
	case 18:
    	currentSkill.setBackgroundResource(R.drawable.skillangelshockwave1);
    	animationSlowdown = 0;
    	animationDuration = 0f;
      break;
	case 19:
    	currentSkill.setBackgroundResource(R.drawable.skillangelicicle1);
    	animationSlowdown = 0;
    	animationDuration = 0f;
      break;
	case 5:
    	currentSkill.setBackgroundResource(R.drawable.skillnecromancerthunder1);
    	animationSlowdown = 0;
    	animationDuration = 0f;
      break;
	case 26:
    	currentSkill.setBackgroundResource(R.drawable.skillangelpiorun1);
    	animationSlowdown = 0;
    	animationDuration = 0f;
      break;

	case 20:
    	currentSkill.setBackgroundResource(R.drawable.skillangelsmoke1);
    	animationSlowdown = 301;
    	animationDuration = 0f;
      break;
	case 102:
    	currentSkill.setBackgroundResource(R.drawable.skillangelheal1);
    	animationSlowdown = 0;
    	animationDuration = 0f;
      break;
	case 104:
    	currentSkill.setBackgroundResource(R.drawable.skillangelheal1);
    	animationSlowdown = 0;
    	animationDuration = 0f;
      break;
	case 14:
    	currentSkill.setBackgroundResource(R.drawable.skillangelwarmth1);
    	animationSlowdown = 6;
    	animationDuration = 1f;
      break;  
	case 27:
    	currentSkill.setBackgroundResource(R.drawable.skillangelpotion1);
    	animationSlowdown = 0;
    	animationDuration = 0f;
      break;
	case 13:
    	currentSkill.setBackgroundResource(R.drawable.skillangeltrap1);
    	animationSlowdown = 0;
    	animationDuration = 32f;
      break;  
	case 23:
    	currentSkill.setBackgroundResource(R.drawable.skillangelwings1);
    	animationSlowdown = 0;
    	animationDuration = 0f;
      break;
	case 25:
    	currentSkill.setBackgroundResource(R.drawable.skillangelinfinity1);
    	animationSlowdown = 0;
    	animationDuration = 0f;
      break; 
	case 28:
    	currentSkill.setBackgroundResource(R.drawable.skillangeldenial1);
    	animationSlowdown = 0;
    	animationDuration = 60f;
      break; 
	case 24:
    	currentSkill.setBackgroundResource(R.drawable.skillangeldef1);
    	animationSlowdown = 0;
    	animationDuration = 0f;
      break; 
  }
	if (v.getId()==R.id.Skill2EqBtn)
	{
		YoloEngine.currentPlayerInfo.setSK2EQ(currentSkill2Checked);
		YoloEngine.animationSlowdown2=animationSlowdown;
		YoloEngine.animationDuration2=animationDuration;
		YoloEngine.SkillSprite2=currentSkill2Checked;
		System.out.println("pierwszy skill loaded "+YoloEngine.SkillSprite2);
	}
	else {
		YoloEngine.currentPlayerInfo.setSK3EQ(currentSkill2Checked);
		YoloEngine.animationSlowdown3=animationSlowdown;
		YoloEngine.animationDuration3=animationDuration;
		YoloEngine.SkillSprite3=currentSkill2Checked;
		System.out.println("drugi skill loaded "+YoloEngine.SkillSprite3);
	}
	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
}

/*
public void skill3angelEqBtnClick(View v){
	Button currentSkill = (Button) findViewById(R.id.currentSkillAngel3);
	switch(currentSkill2Checked) {
	case 18:
    	currentSkill.setBackgroundResource(R.drawable.skillangelcloud1);
    	YoloEngine.currentPlayerInfo.setSK3EQ(18);
    	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
    	YoloEngine.animationSlowdown2 = 0;
    	//YoloEngine.animationDuration2 = 57f;
      break;
	case 19:
    	currentSkill.setBackgroundResource(R.drawable.skillangelicicle1);
    	YoloEngine.currentPlayerInfo.setSK3EQ(19);
    	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
    	YoloEngine.animationSlowdown2 = 0;
    	//YoloEngine.animationDuration2 = 57f;
      break;
	case 5:
    	currentSkill.setBackgroundResource(R.drawable.skillnecromancerthunder1);
    	YoloEngine.currentPlayerInfo.setSK3EQ(5);
    	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
    	YoloEngine.animationSlowdown2 = 0;
    	//YoloEngine.animationDuration2 = 57f;
      break;
	case 20:
    	currentSkill.setBackgroundResource(R.drawable.skillangelsmoke1);
    	YoloEngine.currentPlayerInfo.setSK3EQ(20);
    	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
    	YoloEngine.animationSlowdown2 = 301;
    	//YoloEngine.animationDuration2 = 57f;
      break;
	case 104:
    	currentSkill.setBackgroundResource(R.drawable.skillangelheal1);
    	YoloEngine.currentPlayerInfo.setSK3EQ(104);
    	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
    	YoloEngine.animationSlowdown2 = 0;
    	//YoloEngine.animationDuration2 = 57f;
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
	}*/
// ------------------------- Multislayer BEGIN -----------------------
	public void signIn(View v) {
		System.out.println("Signing in");
		mSignInprogress = true;
		YoloEngine.mHelper.beginUserInitiatedSignIn();
	}
	
	public void signOut(View v) {
		// całkowicie wylogowuje, żeby np. zmienić konto
        System.out.println("Signing out");
		
		btn_quick.setEnabled(false);
		btn_invite.setEnabled(false);

	    YoloEngine.mHelper.signOut();
		//mHelper.disconnect(); odłącza, nie wylogowuje
	}

    public void leaveRoom(View v) {
        System.out.println("Leaving the room");

        if (YoloEngine.mHelper.getApiClient().isConnected()) {
            if (YoloEngine.mRoom != null)
                if (YoloEngine.mRoom.getStatus() != 6)
                    Games.RealTimeMultiplayer.leave(YoloEngine.mHelper.getApiClient(), mRoomUpdateListener, YoloEngine.mRoom.getRoomId());
        }
    }
	
	
	public void startQuickGame(View v) {
		YoloEngine.MULTI_ACTIVE = true;
		
		Games.RealTimeMultiplayer.create(YoloEngine.mHelper.getApiClient(), prepareGame());

		// prevent screen from sleeping during handshake
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		//@TODO ekran oczekiwania na graczy
	}
	
	public void invite(View v) {
		YoloEngine.MULTI_ACTIVE = true;

        // prevent screen from sleeping during handshake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
		
		if(request == RC_SIGNIN) { // Wracamy z powrotem do gameHelpera, który otworzył okienko i chce wiedzie� co si� sta�o
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

            debugLog("invitation sent chyba");

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

@Override
public void onBackPressed() {
	if(YoloEngine.whichLayout==1)
	{
		YoloEngine.whichLayout=0;
		setContentView(R.layout.main_menu);
	}
	else {  
		YoloGameRenderer.skillTeamBVe.clear();
		YoloGameRenderer.skillTeamAVe.clear();
		finish();
	//Intent intent = new Intent(Intent.ACTION_MAIN);
	 //  intent.addCategory(Intent.CATEGORY_HOME);
	  // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	  // startActivity(intent);
	 }	
}
}

