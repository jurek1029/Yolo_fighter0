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
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.internal.lw;
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
	int skillPointer = 0;
	int unitsNeeded = 0;
	int lvlNeeded = 0;
	boolean buying = false;
	private SharedPreferences preferences;
	
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
                for(YoloPlayer p : YoloEngine.TeamAB) p = new YoloPlayer(1000f, 1000f, false, 666);
                if(YoloEngine.participants != null) YoloEngine.participants.clear();

                // @TODO temporarry
                //YoloEngine.teamA.add(YoloEngine.playerParticipantID);
            }
            else
               debugLog("Error "+statusCode);
		}

		@Override
		public void onRoomConnected(int statusCode, Room room) {
            if(statusCode == GamesStatusCodes.STATUS_OK) {
                debugLog("All participants connected");

                //YoloEngine.teamA.clear(); //@TODO temporarry

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
                	YoloEngine.TeamAB[YoloEngine.MyID].ParticipantId = YoloEngine.playerParticipantID;
                    String teamAssignPattern = "1";

                    // Team assignment dokąd {0-teamA, 1-teamB}

                    // Przydzielamy nam
                    if (new Random().nextBoolean()) {
                        YoloEngine.teamA.add(YoloEngine.playerParticipantID); //@REMOVE
                        YoloEngine.TeamAB[0].playerTeam = YoloEngine.TeamA;
                        YoloEngine.MyID = 0;
                        teamAssignPattern += "0";
                    }
                    else {
                        YoloEngine.teamB.add(YoloEngine.playerParticipantID); //@REMOVE
                        YoloEngine.TeamAB[0].playerTeam = YoloEngine.TeamB;
                        YoloEngine.MyID = 2;
                        teamAssignPattern += "1";
                    }
                    int i = 1,a=0,b=YoloEngine.TeamSize;
                    // Przydzielamy reszcie graczy
                    for (Participant p : YoloEngine.participants) {
                        if (!(YoloEngine.playerParticipantID.equals(p.getParticipantId()) || p.getStatus() != Participant.STATUS_JOINED || YoloEngine.teamA.contains(p.getParticipantId()) || YoloEngine.teamB.contains(p.getParticipantId()))) { // nie jesteśmy to my, gracz nie należy jeszcze do żadnego teamu
                            // @TODO sprawdzenie, czy gracz nie ma ju� przydzielonego teamu?
                        	if (YoloEngine.teamA.size() > YoloEngine.teamB.size()) {
                                YoloEngine.teamB.add(p.getParticipantId()); //@REMOVE
                                YoloEngine.TeamAB[b].playerTeam = YoloEngine.TeamB;
                                YoloEngine.TeamAB[b].ParticipantId = p.getParticipantId();
                                teamAssignPattern += "1";
                                b++;
                                i++;
                            } else if (YoloEngine.teamA.size() < YoloEngine.teamB.size()) {
                                YoloEngine.teamA.add(p.getParticipantId()); //@REMOVE
                                YoloEngine.TeamAB[a].playerTeam = YoloEngine.TeamA;
                                YoloEngine.TeamAB[a].ParticipantId = p.getParticipantId();
                                teamAssignPattern += "0";
                                a++;
                                i++;
                            } else {
                                if (new Random().nextBoolean()) {
                                    YoloEngine.teamA.add(p.getParticipantId()); //@REMOVE
                                    YoloEngine.TeamAB[a].playerTeam = YoloEngine.TeamA;
                                    YoloEngine.TeamAB[a].ParticipantId = p.getParticipantId();
                                    teamAssignPattern += "0";
                                    a++;
                                    i++;
                                } else {
                                    YoloEngine.teamB.add(p.getParticipantId()); //@REMOVE
                                    YoloEngine.TeamAB[b].playerTeam = YoloEngine.TeamB;
                                    YoloEngine.TeamAB[b].ParticipantId = p.getParticipantId();
                                    teamAssignPattern += "1";
                                    b++;
                                    i++;
                                }
                            }
                        }
                    }

                    YoloEngine.mMultislayer.sendTeamAssignment(Integer.parseInt(teamAssignPattern, 2));
                  //  YoloGameRenderer.givePlayerID();
                    YoloEngine.mMultislayer.prepareMatchArray();
                    YoloEngine.mMultislayer.sendMaxLife(); //@TODO to powinno by� p�niej, �eby by�a pweno��, czy TeamAB jest dobrze usuzp�enione
                }
                
                


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
                try {
                	YoloEngine.TeamAB[Collections.binarySearch(YoloEngine.mMultislayer.TeamAB_Participants, p)].moveAway();
				} catch (Exception e) {
					System.out.println("exception w usuwaniu gracza");
				}
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
		preferences=getSharedPreferences("daneGra", Activity.MODE_PRIVATE);
		
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
		
		/*
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
		optionsBtn.startAnimation(animMove3);*/
	}
	
	
//------------------------------------przyciski menu glowne----------------------
	public void optionsClick(View v)
	{
		YoloEngine.whichLayout = 1;
		setContentView(R.layout.options_menu);
		Button isClasicBtn = (Button) findViewById(R.id.btnIsClasic);
		boolean isClasic =preferences.getBoolean("isClasic", false);
		if(isClasic==true) {
			isClasicBtn.setBackgroundResource(R.drawable.classiccontrolsbtnon);
		}
		else {
			isClasicBtn.setBackgroundResource(R.drawable.classiccontrolsbtnoff);
		}
		Button soundBtn = (Button) findViewById(R.id.btnSound);
		boolean enableSound =preferences.getBoolean("enableSound", true);
		if(enableSound==true) {
			soundBtn.setBackgroundResource(R.drawable.enablesoundbtnon);
		}
		else {
			soundBtn.setBackgroundResource(R.drawable.enablesoundbtnoff);
		}
		
		
		
	}
	

	
	
	public void joinClick(View v)
	{
		
		// Te dwie instrukcje warto wrzuci� do jakiego� senwoengo eventu, �eby ci�gle tego nie od�wie�a� XXX
		plInfoList.clear();
		plInfoList=dbm.getAll();
		YoloEngine.whichLayout = 1;
		if (plInfoList.size()==0) {setContentView(R.layout.addplayer_menu); newPlayerRace=0;}
		else{
		int currentPlayerInfoPosition =preferences.getInt("currentPlInfPos", 0);
		YoloEngine.currentPlayerInfo = plInfoList.get(currentPlayerInfoPosition);
		buying = false;
		switch(YoloEngine.currentPlayerInfo.getRace()) {
        case 0:
        	setContentView(R.layout.skill2angel_menu);
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK1EQ();
        	v.setId(R.id.Skill1EqBtn);
        	System.out.println("powinien pierwszy");
        	skill2angelEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite2);
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
        	setContentView(R.layout.skill2devil_menu);
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK1EQ();
        	v.setId(R.id.Skill1EqBtn);
        	System.out.println("powinien pierwszy");
        	skill2devilEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite2);
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK2EQ();
        	v.setId(R.id.Skill2EqBtn);
        	System.out.println("powinien pierwszy");
        	skill2devilEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite2);
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK3EQ();
        	v.setId(R.id.Skill3EqBtn);
        	System.out.println("powinien drugi");
        	skill2devilEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite3);
        	setContentView(R.layout.main_menu);
        	break;
        case 2:
        	setContentView(R.layout.skill2necromancer_menu);
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK1EQ();
        	v.setId(R.id.Skill1EqBtn);
        	System.out.println("powinien pierwszy");
        	skill2necromancerEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite2);
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
		buying = true;
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
		if (plInfoList.size()==0) {setContentView(R.layout.addplayer_menu); newPlayerRace=0;}
		else
		{
		setContentView(R.layout.player_menu);
	//	List<YoloPlayerInfo> plInfoList = new LinkedList<YoloPlayerInfo>();
		
	//	List<String> plNames = new ArrayList<String>(plInfoList.size());
	//	ArrayList<Integer> plIDs = new ArrayList<Integer>(plInfoList.size());
		//XXX
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
		int currentPlayerInfoPosition =preferences.getInt("currentPlInfPos", 0);
		YoloEngine.currentPlayerInfo = plInfoList.get(currentPlayerInfoPosition);
		//System.out.println(YoloEngine.currentPlayerInfoPosition+"lojo");
		
		Spinner spinner =  (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, plNames); 
	
		
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerArrayAdapter);
		spinner.setSelection(currentPlayerInfoPosition);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

	        public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
	        	int currentID = plIDs.get(position);
	        	SharedPreferences.Editor preferencesEditor = preferences.edit();
	        	int currentPlayerInfoPosition = position;
	        	preferencesEditor.putInt("currentPlInfPos", currentPlayerInfoPosition);
	    		preferencesEditor.commit();
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
	    		TextView txtViewLevelCounter = (TextView) findViewById(R.id.levelCounter);
	    		TextView txtViewUnitsCounter = (TextView) findViewById(R.id.unitsCounter);
	    		ImageView playerImage = (ImageView) findViewById(R.id.playerImage);
	    		
	    		Integer st1 = YoloEngine.currentPlayerInfo.getST1();
	    		Integer st2 = YoloEngine.currentPlayerInfo.getST2();
	    		Integer st3 = YoloEngine.currentPlayerInfo.getST3();
	    		Integer st4 = YoloEngine.currentPlayerInfo.getST4();
	    		Integer coins = YoloEngine.currentPlayerInfo.getCoins();
	    		Integer units = YoloEngine.currentPlayerInfo.getUnits();
	    		Integer level = YoloEngine.currentPlayerInfo.getLevel();
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
	    		txtViewUnitsCounter.setText(units.toString());
	    		txtViewLevelCounter.setText(level.toString()); 
	    		switch(YoloEngine.currentPlayerInfo.getRace()){
	    		case 0: playerImage.setBackgroundResource(R.drawable.addangel);
	    			break;
	    		case 1: playerImage.setBackgroundResource(R.drawable.adddevil);
    			break;
	    		case 2: playerImage.setBackgroundResource(R.drawable.addnecromancer);
    			break;
	    		}
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
		buying = false;
		switch(YoloEngine.currentPlayerInfo.getRace()) {
        case 0:
        	setContentView(R.layout.skill2angel_menu);
        	skills2angelTreeDraw();
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK1EQ();
        	v.setId(R.id.Skill1EqBtn);
        	System.out.println("powinien pierwszy");
        	skill2angelEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite1);
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
        	skills2devilTreeDraw();
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK1EQ();
        	v.setId(R.id.Skill1EqBtn);
        	System.out.println("powinien pierwszy");
        	skill2devilEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite2);
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK2EQ();
        	v.setId(R.id.Skill2EqBtn);
        	System.out.println("powinien pierwszy");
        	skill2devilEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite2);
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK3EQ();
        	v.setId(R.id.Skill3EqBtn);
        	System.out.println("powinien drugi");
        	skill2devilEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite3);
        	break;
        case 2:
        	setContentView(R.layout.skill2necromancer_menu);
        	skills2necromancerTreeDraw();
        	currentSkill2Checked=YoloEngine.currentPlayerInfo.getSK1EQ();
        	v.setId(R.id.Skill1EqBtn);
        	System.out.println("powinien pierwszy");
        	skill2necromancerEqBtnClick(v);
        	System.out.println(YoloEngine.SkillSprite2);
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
		buying=true;
	}
	
	
	public void addPlayerClick(View v)
	{
		setContentView(R.layout.addplayer_menu);
		newPlayerRace=0;
	}
	
	public void deletePlayerClick(View v)
	{
		dbm.deletePlayer(YoloEngine.currentPlayerInfo.getID());
		SharedPreferences.Editor preferencesEditor = preferences.edit();
    	int currentPlayerInfoPosition = 0;
    	preferencesEditor.putInt("currentPlInfPos", currentPlayerInfoPosition);
		preferencesEditor.commit();
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
		Button buttonAng = (Button) findViewById(R.id.addAngelBtn);
		Button buttonDev = (Button) findViewById(R.id.addDevilBtn);
		Button buttonNecr = (Button) findViewById(R.id.addNecrBtn);
		buttonAng.setBackgroundResource(R.drawable.angelbtn1);
		buttonDev.setBackgroundResource(R.drawable.devilbtn2);
		buttonNecr.setBackgroundResource(R.drawable.necromancerbtn2);
		newPlayerRace = 0;
	}
	
	public void devilClick(View v)
	{
		Button buttonAng = (Button) findViewById(R.id.addAngelBtn);
		Button buttonDev = (Button) findViewById(R.id.addDevilBtn);
		Button buttonNecr = (Button) findViewById(R.id.addNecrBtn);
		buttonAng.setBackgroundResource(R.drawable.angelbtn2);
		buttonDev.setBackgroundResource(R.drawable.devilbtn1);
		buttonNecr.setBackgroundResource(R.drawable.necromancerbtn2);
		newPlayerRace = 1;
	}
	
	public void necromancerClick(View v)
	{
		Button buttonAng = (Button) findViewById(R.id.addAngelBtn);
		Button buttonDev = (Button) findViewById(R.id.addDevilBtn);
		Button buttonNecr = (Button) findViewById(R.id.addNecrBtn);
		buttonAng.setBackgroundResource(R.drawable.angelbtn2);
		buttonDev.setBackgroundResource(R.drawable.devilbtn2);
		buttonNecr.setBackgroundResource(R.drawable.necromancerbtn1);
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
		switch(newPlayerRace){
		case 0:
		newPlayerInfo.setSkill1("10000000000000");
		break;
		case 1:
		newPlayerInfo.setSkill1("100000000000000");	
		break;
		case 2:
		newPlayerInfo.setSkill1("100000000000");
		break;
		}
		dbm.addPlayer(newPlayerInfo);
		//TODO
		plInfoList=dbm.getAll();
		SharedPreferences.Editor preferencesEditor = preferences.edit();
    	int currentPlayerInfoPosition = plInfoList.size()-1;
    	preferencesEditor.putInt("currentPlInfPos", currentPlayerInfoPosition);
		preferencesEditor.commit();
		skillsClick(v);
	}
	
	
	//-------------------options menu-----------------------------------------
	public void isClasicClick(View v)
	{
		SharedPreferences.Editor preferencesEditor = preferences.edit();
		boolean isClasic =preferences.getBoolean("isClasic", false);
		Button isClasicBtn = (Button) findViewById(R.id.btnIsClasic);
		if(isClasic==true) {
			isClasic = false;
			isClasicBtn.setBackgroundResource(R.drawable.classiccontrolsbtnoff);
		}
		else {
			isClasic = true;
			isClasicBtn.setBackgroundResource(R.drawable.classiccontrolsbtnon);
		}
		preferencesEditor.putBoolean("isClasic", isClasic);
		preferencesEditor.commit();
	}
	
	public void soundClick(View v)
	{
		SharedPreferences.Editor preferencesEditor = preferences.edit();
		boolean enableSound =preferences.getBoolean("enableSound", true);
		Button soundBtn = (Button) findViewById(R.id.btnSound);
		if(enableSound==true) {
			enableSound = false;
			soundBtn.setBackgroundResource(R.drawable.enablesoundbtnoff);
		}
		else {
			enableSound = true;
			soundBtn.setBackgroundResource(R.drawable.enablesoundbtnon);
		}
		preferencesEditor.putBoolean("enableSound", enableSound);
		preferencesEditor.commit();
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
	public void skills2necromancerTreeDraw()
	{
		String skillTab = YoloEngine.currentPlayerInfo.getSkill1();
		ImageView branch2a = (ImageView) findViewById(R.id.branch2a);
		ImageView branch2b = (ImageView) findViewById(R.id.branch2b);
		ImageView branch3a = (ImageView) findViewById(R.id.branch3a);
		ImageView branch3b = (ImageView) findViewById(R.id.branch3b);
		ImageView branch4a = (ImageView) findViewById(R.id.branch4a);
		ImageView branch4b = (ImageView) findViewById(R.id.branch4b);
		ImageView branch5a = (ImageView) findViewById(R.id.branch5a);
		ImageView branch5b = (ImageView) findViewById(R.id.branch5b);
		ImageView branch6a = (ImageView) findViewById(R.id.branch6a);
		ImageView branch6aa = (ImageView) findViewById(R.id.branch6aa);
		ImageView branch6b = (ImageView) findViewById(R.id.branch6b);
		ImageView branch6bb = (ImageView) findViewById(R.id.branch6bb);
		ImageView branch7a = (ImageView) findViewById(R.id.branch7a);
		Button skillHand = (Button) findViewById(R.id.Skill2necromancer_handBtn);
		Button skillSpike = (Button) findViewById(R.id.Skill2necromancer_spikeBtn);
		Button skillMummy = (Button) findViewById(R.id.Skill2necromancer_mummyBtn);
		Button skillSlowdown = (Button) findViewById(R.id.Skill2necromancer_slowdownBtn);
		Button skillBarrel = (Button) findViewById(R.id.Skill2necromancer_barrelBtn);
		Button skillWarrior = (Button) findViewById(R.id.Skill2necromancer_warriorBtn);
		Button skillWall = (Button) findViewById(R.id.Skill2necromancer_wallBtn);
		Button skillArcher = (Button) findViewById(R.id.Skill2necromancer_archerBtn);
		Button skillLifeDrain = (Button) findViewById(R.id.Skill2necromancer_lifedrainBtn);
		Button skillTower = (Button) findViewById(R.id.Skill2necromancer_towerBtn);
		Button skillResurrection = (Button) findViewById(R.id.Skill2necromancer_resurrectionBtn);
		if (skillTab.charAt(1)=='1')
		{
			branch2a.setBackgroundResource(R.drawable.branch_norm1);
			skillHand.setBackgroundResource(R.drawable.skillnecromancerhand1);
		}
		if (skillTab.charAt(2)=='1')
		{
			branch2b.setBackgroundResource(R.drawable.branch_norm1);
			skillSpike.setBackgroundResource(R.drawable.skillnecromancerspike1);
		}
		if (skillTab.charAt(3)=='1')
		{
			branch4a.setBackgroundResource(R.drawable.branch_norm1);
			branch3a.setBackgroundResource(R.drawable.branch_poz1);
			skillMummy.setBackgroundResource(R.drawable.skillnecromancermummy1);
		}
		if (skillTab.charAt(4)=='1')
		{
			branch3a.setBackgroundResource(R.drawable.branch_poz1);
			branch3b.setBackgroundResource(R.drawable.branch_poz1);
			skillSlowdown.setBackgroundResource(R.drawable.skillnecromancerslowdown1);
		}
		if (skillTab.charAt(5)=='1')
		{
			branch3b.setBackgroundResource(R.drawable.branch_poz1);
			branch4b.setBackgroundResource(R.drawable.branch_norm1);
			skillBarrel.setBackgroundResource(R.drawable.skillnecromancerbarrel1);
		}
		if (skillTab.charAt(6)=='1')
		{
			branch5a.setBackgroundResource(R.drawable.branch_norm1);
			skillWarrior.setBackgroundResource(R.drawable.skillnecromancerwarrior1);
		}
		if (skillTab.charAt(7)=='1')
		{
			branch5b.setBackgroundResource(R.drawable.branch_norm1);
			skillWall.setBackgroundResource(R.drawable.skillnecromancerwall1);
		}
		if (skillTab.charAt(8)=='1')
		{
			branch6a.setBackgroundResource(R.drawable.branch_poz1);
			branch6aa.setBackgroundResource(R.drawable.branch_lwg1);
			skillArcher.setBackgroundResource(R.drawable.skillnecromancerarcher1);
		}
		if (skillTab.charAt(9)=='1')
		{
			branch7a.setBackgroundResource(R.drawable.branch_norm1);
			skillLifeDrain.setBackgroundResource(R.drawable.skillnecromancerlifesuck1);
		}
		if (skillTab.charAt(10)=='1')
		{
			branch6b.setBackgroundResource(R.drawable.branch_poz1);
			branch6bb.setBackgroundResource(R.drawable.branch_pwg1);
			skillTower.setBackgroundResource(R.drawable.skillnecromancertower1);
		}
		if (skillTab.charAt(11)=='1')
		{
			skillResurrection.setBackgroundResource(R.drawable.skillnecromancerresurrection1);
		}
	}
	
	public void skills2necromancerBtnClick(View v) {
		
		TextView description = (TextView) findViewById(R.id.skill2necromancerdescription);
		TextView lvl = (TextView) findViewById(R.id.skill2necromancerlvlneeded);
		TextView cost = (TextView) findViewById(R.id.skill2necromancercost);
		TextView units = (TextView) findViewById(R.id.skill2necromancerunits);
	      switch(v.getId()) {
	        case R.id.Skill2necromancer_poisonBtn:
	        	currentSkill2Checked = 4;
	        	description.setText("Empoison your enemies with a cloud of toxic gas.");
	        	lvlNeeded=1;
	        	unitsNeeded=1;
	        	skillPointer = 0;
	          break;
	        case R.id.Skill2necromancer_archerBtn:
	        	currentSkill2Checked = 6;
	        	description.setText("Summon a dangerous archer firing with a bow from a long distance.");
	        	lvlNeeded=1;
	        	unitsNeeded=1;
	        	skillPointer = 8;
	          break;
	        case R.id.Skill2necromancer_warriorBtn:
	        	currentSkill2Checked = 7;
	        	description.setText("Warrior will pursue enemies and bereave them of any chance of survival.");
	        	lvlNeeded=1;
	        	unitsNeeded=1;
	        	skillPointer = 6;
	          break;
	        case R.id.Skill2necromancer_mummyBtn:
	        	currentSkill2Checked = 8;
	        	description.setText("Dead mummy will be bane of your yet living enemies.");
	        	lvlNeeded=1;
	        	unitsNeeded=1;
	        	skillPointer = 3;
	          break;
	        case R.id.Skill2necromancer_handBtn:
	        	currentSkill2Checked = 9;
	        	description.setText("Summon a rough hand striking out from a short distance.");
	        	lvlNeeded=1;
	        	unitsNeeded=1;
	        	skillPointer = 1;
	          break;
	        case R.id.Skill2necromancer_barrelBtn:
	        	currentSkill2Checked = 10;
	        	description.setText("Throw a rolling barrel filled with explosive materials.");
	        	lvlNeeded=1;
	        	unitsNeeded=1;
	        	skillPointer = 5;
	          break;
	        case R.id.Skill2necromancer_towerBtn:
	        	currentSkill2Checked = 11;
	        	description.setText("Tower absorbs bullets and fires with bolts.");
	        	lvlNeeded=1;
	        	unitsNeeded=1;
	        	skillPointer = 10;
	          break;
	        case R.id.Skill2necromancer_wallBtn:
	        	currentSkill2Checked = 12;
	        	description.setText("Build a defensive wall which absorbs deadly bullets.");
	        	lvlNeeded=1;
	        	unitsNeeded=1;
	        	skillPointer = 7;
	        	break;
	        case R.id.Skill2necromancer_lifedrainBtn:
	        	currentSkill2Checked = 108;
	        	description.setText("Suck oomph of your enemies depriving them of grace of life.");
	        	lvlNeeded=1;
	        	unitsNeeded=1;
	        	skillPointer = 9;
	        	break;
	        case R.id.Skill2necromancer_resurrectionBtn:
	        	currentSkill2Checked = 109;
	        	description.setText("Raise your allies to reach ultimate success.");
	        	lvlNeeded=1;
	        	unitsNeeded=1;
	        	skillPointer = 11;
	          break;
	        case R.id.Skill2necromancer_spikeBtn:
	        	currentSkill2Checked = 15;
	        	description.setText("Exsert lethal spikes from the ground.");
	        	lvlNeeded=1;
	        	unitsNeeded=1;
	        	skillPointer = 2;
	          break;
	        case R.id.Skill2necromancer_slowdownBtn:
	        	currentSkill2Checked = 103;
	        	description.setText("Handicap your foes by inhibiting their movement.");
	        	lvlNeeded=1;
	        	unitsNeeded=1;
	        	skillPointer = 4;
	          break;
	        
	      }
	    lvl.setText(lvlNeeded+" lvl");
      	cost.setText(unitsNeeded+" units");
      	units.setText("your units: "+YoloEngine.currentPlayerInfo.getUnits());
	}
	
	public void skill2necromancerEqBtnClick(View v){
		String skillTab = YoloEngine.currentPlayerInfo.getSkill1();
		String newSkillTab;
		boolean bought= false;
		Button currentSkill;
		if (v.getId()==R.id.Skill2EqBtn)
		{
			currentSkill = (Button) findViewById(R.id.currentSkillNecromancer2);
		}
		else if (v.getId()==R.id.Skill3EqBtn){
			currentSkill = (Button) findViewById(R.id.currentSkillNecromancer3);
		}
		else currentSkill = (Button) findViewById(R.id.currentSkillNecromancer1);
		switch(currentSkill2Checked) {
		case 4:
			if (skillTab.charAt(skillPointer)=='1')
			{
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerpoison1);
			bought = true;
			}
			else if (YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
			{
				currentSkill.setBackgroundResource(R.drawable.skillnecromancerpoison1);
				YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
				newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
				YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
				bought=true;
			}
        	break;
		case 5:
        	currentSkill.setBackgroundResource(R.drawable.skillnecromancerthunder1);
          break;
		case 6:
        	if (skillTab.charAt(skillPointer)=='1')
			{
        		currentSkill.setBackgroundResource(R.drawable.skillnecromancerarcher1);
			bought = true;
			}
			else if (skillTab.charAt(6)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
			{
				currentSkill.setBackgroundResource(R.drawable.skillnecromancerarcher1);
				YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
				newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
				YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
				bought=true;
			}
          break;
        case 7:
        	if (skillTab.charAt(skillPointer)=='1')
			{
        		currentSkill.setBackgroundResource(R.drawable.skillnecromancerwarrior1);
			bought = true;
			}
			else if (skillTab.charAt(3)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
			{
				currentSkill.setBackgroundResource(R.drawable.skillnecromancerwarrior1);
				YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
				newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
				YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
				bought=true;
			}
          break;
        case 8:
        	if (skillTab.charAt(skillPointer)=='1')
			{
        		currentSkill.setBackgroundResource(R.drawable.skillnecromancermummy1);
			bought = true;
			}
			else if ((skillTab.charAt(1)=='1' || skillTab.charAt(4)=='1') && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
			{
				currentSkill.setBackgroundResource(R.drawable.skillnecromancermummy1);
				YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
				newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
				YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
				bought=true;
			}
        break;
        case 9:
        	if (skillTab.charAt(skillPointer)=='1')
			{
        		currentSkill.setBackgroundResource(R.drawable.skillnecromancerhand1);
			bought = true;
			}
			else if (skillTab.charAt(0)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
			{
				currentSkill.setBackgroundResource(R.drawable.skillnecromancerhand1);
				YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
				newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
				YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
				bought=true;
			}
          break;
        case 10:
        	if (skillTab.charAt(skillPointer)=='1')
			{
        		currentSkill.setBackgroundResource(R.drawable.skillnecromancerbarrel1);
			bought = true;
			}
			else if ((skillTab.charAt(2)=='1' || skillTab.charAt(4)=='1') && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
			{
				currentSkill.setBackgroundResource(R.drawable.skillnecromancerbarrel1);
				YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
				newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
				YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
				bought=true;
			}
          break;
        case 11:
        	if (skillTab.charAt(skillPointer)=='1')
			{
        		currentSkill.setBackgroundResource(R.drawable.skillnecromancertower1);
			bought = true;
			}
			else if (skillTab.charAt(7)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
			{
				currentSkill.setBackgroundResource(R.drawable.skillnecromancertower1);
				YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
				newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
				YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
				bought=true;
			}
        	break;
        case 12:
        	if (skillTab.charAt(skillPointer)=='1')
			{
        		currentSkill.setBackgroundResource(R.drawable.skillnecromancerwall1);
			bought = true;
			}
			else if (skillTab.charAt(5)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
			{
				currentSkill.setBackgroundResource(R.drawable.skillnecromancerwall1);
				YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
				newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
				YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
				bought=true;
			}
          break;
        case 108:
        	if (skillTab.charAt(skillPointer)=='1')
			{
        		currentSkill.setBackgroundResource(R.drawable.skillnecromancerlifesuck1);
			bought = true;
			}
			else if ((skillTab.charAt(8)=='1' || skillTab.charAt(10)=='1') && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
			{
				currentSkill.setBackgroundResource(R.drawable.skillnecromancerlifesuck1);
				YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
				newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
				YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
				bought=true;
			}
          break;
        case 109:
        	if (skillTab.charAt(skillPointer)=='1')
			{
        		currentSkill.setBackgroundResource(R.drawable.skillnecromancerresurrection1);
			bought = true;
			}
			else if (skillTab.charAt(9)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
			{
				currentSkill.setBackgroundResource(R.drawable.skillnecromancerresurrection1);
				YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
				newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
				YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
				bought=true;
			}
          break;
        case 15:
        	if (skillTab.charAt(skillPointer)=='1')
			{
        		currentSkill.setBackgroundResource(R.drawable.skillnecromancerspike1);
			bought = true;
			}
			else if (skillTab.charAt(0)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
			{
				currentSkill.setBackgroundResource(R.drawable.skillnecromancerspike1);
				YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
				newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
				YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
				bought=true;
			}
          break;
        case 103:
        	if (skillTab.charAt(skillPointer)=='1')
			{
        		currentSkill.setBackgroundResource(R.drawable.skillnecromancerslowdown1);
			bought = true;
			}
			else if ((skillTab.charAt(3)=='1' || skillTab.charAt(5)=='1')&& YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
			{
				currentSkill.setBackgroundResource(R.drawable.skillnecromancerslowdown1);
				YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
				newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
				YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
				bought=true;
			}
          break;
      }
		if (bought==true){
		if (v.getId()==R.id.Skill2EqBtn)
		{
			YoloEngine.currentPlayerInfo.setSK2EQ(currentSkill2Checked);
			YoloEngine.SkillSprite2=currentSkill2Checked;
			System.out.println("pierwszy skill loaded "+YoloEngine.SkillSprite2);
		}
		else if (v.getId()==R.id.Skill3EqBtn){
			YoloEngine.currentPlayerInfo.setSK3EQ(currentSkill2Checked);
			YoloEngine.SkillSprite3=currentSkill2Checked;
			System.out.println("drugi skill loaded "+YoloEngine.SkillSprite3);
		}
		else {
			YoloEngine.currentPlayerInfo.setSK1EQ(currentSkill2Checked);
			YoloEngine.SkillSprite1=currentSkill2Checked;
			System.out.println("drugi skill loaded "+YoloEngine.SkillSprite3);
		}
		skills2necromancerTreeDraw();
		TextView units = (TextView) findViewById(R.id.skill2necromancerunits);
		units.setText("your units: "+YoloEngine.currentPlayerInfo.getUnits());
		}
		else { if (buying == true)
			 Toast.makeText(getApplicationContext(), 
                    "Too low level, not enough units or unlock earlier skills", Toast.LENGTH_LONG).show();
		}
		dbm.updatePlayer(YoloEngine.currentPlayerInfo);
		
	}

public void skills2angelTreeDraw()
{
	String skillTab = YoloEngine.currentPlayerInfo.getSkill1();
	ImageView branch2a = (ImageView) findViewById(R.id.branch2a);
	ImageView branch2b = (ImageView) findViewById(R.id.branch2b);
	ImageView branch2c = (ImageView) findViewById(R.id.branch2c);
	ImageView branch2d = (ImageView) findViewById(R.id.branch2d);
	ImageView branch3a = (ImageView) findViewById(R.id.branch3a);
	ImageView branch3b = (ImageView) findViewById(R.id.branch3b);
	ImageView branch4a = (ImageView) findViewById(R.id.branch4a);
	ImageView branch4b = (ImageView) findViewById(R.id.branch4b);
	ImageView branch5a = (ImageView) findViewById(R.id.branch5a);
	ImageView branch5b = (ImageView) findViewById(R.id.branch5b);
	ImageView branch6a = (ImageView) findViewById(R.id.branch6a);
	ImageView branch6b = (ImageView) findViewById(R.id.branch6b);
	ImageView branch7a = (ImageView) findViewById(R.id.branch7a);
	ImageView branch7b = (ImageView) findViewById(R.id.branch7b);
	ImageView branch7c = (ImageView) findViewById(R.id.branch7c);
	ImageView branch8a = (ImageView) findViewById(R.id.branch8a);
	ImageView branch8b = (ImageView) findViewById(R.id.branch8b);
	ImageView branch8c = (ImageView) findViewById(R.id.branch8c);
	ImageView branch9a = (ImageView) findViewById(R.id.branch9a);
	ImageView branch9b = (ImageView) findViewById(R.id.branch9b);
	Button skillWings = (Button) findViewById(R.id.Skill2angel_wingsBtn);
	Button skillShockwave = (Button) findViewById(R.id.Skill2angel_shockwaveBtn);
	Button skillHeal1 = (Button) findViewById(R.id.Skill2angel_healBtn);
	Button skillTrap = (Button) findViewById(R.id.Skill2angel_trapBtn);
	Button skillHeal2 = (Button) findViewById(R.id.Skill2angel_heal2Btn);
	Button skillDef = (Button) findViewById(R.id.Skill2angel_defBtn);
	Button skillPotion = (Button) findViewById(R.id.Skill2angel_potionBtn);
	Button skillThunder = (Button) findViewById(R.id.Skill2angel_thunderBtn);
	Button skillPiorun = (Button) findViewById(R.id.Skill2angel_piorunBtn);
	Button skillIcicle= (Button) findViewById(R.id.Skill2angel_icicleBtn);
	Button skillWarmth = (Button) findViewById(R.id.Skill2angel_warmthBtn);
	Button skillDenial = (Button) findViewById(R.id.Skill2angel_denialBtn);
	Button skillInfinity = (Button) findViewById(R.id.Skill2angel_infinityBtn);
	if (skillTab.charAt(1)=='1')
	{
		branch2a.setBackgroundResource(R.drawable.branch_lwd1);
		branch2b.setBackgroundResource(R.drawable.branch_poz1);
		branch2c.setBackgroundResource(R.drawable.branch_poz1);
		branch2d.setBackgroundResource(R.drawable.branch_pwd1);
		skillWings.setBackgroundResource(R.drawable.skillangelwings1);
	}
	if (skillTab.charAt(2)=='1')
	{
		branch3a.setBackgroundResource(R.drawable.branch_norm1);
		skillShockwave.setBackgroundResource(R.drawable.skillangelshockwave1);
	}
	if (skillTab.charAt(3)=='1')
	{
		branch3b.setBackgroundResource(R.drawable.branch_norm1);
		skillHeal1.setBackgroundResource(R.drawable.skillangelheal1);
	}
	if (skillTab.charAt(3)=='1')
	{
		branch3b.setBackgroundResource(R.drawable.branch_norm1);
		skillHeal1.setBackgroundResource(R.drawable.skillangelheal1);
	}
	if (skillTab.charAt(4)=='1')
	{
		branch4a.setBackgroundResource(R.drawable.branch_poz1);
		branch5a.setBackgroundResource(R.drawable.branch_norm1);
		skillTrap.setBackgroundResource(R.drawable.skillangeltrap1);
	}
	if (skillTab.charAt(5)=='1')
	{
		branch4a.setBackgroundResource(R.drawable.branch_poz1);
		branch4b.setBackgroundResource(R.drawable.branch_poz1);
		skillDef.setBackgroundResource(R.drawable.skillangeldef1);
	}
	if (skillTab.charAt(6)=='1')
	{
		branch4b.setBackgroundResource(R.drawable.branch_poz1);
		branch5b.setBackgroundResource(R.drawable.branch_norm1);
		skillPotion.setBackgroundResource(R.drawable.skillangelpotion1);
	}
	if (skillTab.charAt(7)=='1')
	{
		branch6a.setBackgroundResource(R.drawable.branch_poz1);
		branch6b.setBackgroundResource(R.drawable.branch_pwd1);
		branch7a.setBackgroundResource(R.drawable.branch_norm1);
		branch7b.setBackgroundResource(R.drawable.branch_norm1);
		skillThunder.setBackgroundResource(R.drawable.skillnecromancerthunder1);
	}
	if (skillTab.charAt(8)=='1')
	{
		branch7c.setBackgroundResource(R.drawable.branch_norm1);
		skillHeal2.setBackgroundResource(R.drawable.skillangelheal1);
	}
	if (skillTab.charAt(9)=='1')
	{
		branch8a.setBackgroundResource(R.drawable.branch_norm1);
		skillPiorun.setBackgroundResource(R.drawable.skillangelpiorun1);
	}
	if (skillTab.charAt(10)=='1')
	{
		branch8b.setBackgroundResource(R.drawable.branch_norm1);
		branch9a.setBackgroundResource(R.drawable.branch_poz1);
		branch9b.setBackgroundResource(R.drawable.branch_pwg1);
		skillIcicle.setBackgroundResource(R.drawable.skillangelicicle1);
	}
	if (skillTab.charAt(11)=='1')
	{
		branch8c.setBackgroundResource(R.drawable.branch_norm1);
		skillWarmth.setBackgroundResource(R.drawable.skillangelwarmth1);
	}
	if (skillTab.charAt(12)=='1')
	{
		skillDenial.setBackgroundResource(R.drawable.skillangeldenial1);
	}
	if (skillTab.charAt(13)=='1')
	{
		skillInfinity.setBackgroundResource(R.drawable.skillangelinfinity1);
	}
}
	
	
public void skills2angelBtnClick(View v) {
		
		TextView description = (TextView) findViewById(R.id.skill2angeldescription);
		TextView lvl = (TextView) findViewById(R.id.skill2angellvlneeded);
		TextView cost = (TextView) findViewById(R.id.skill2angelcost);
		TextView units = (TextView) findViewById(R.id.skill2angelunits);
		
	      switch(v.getId()) {
	        case R.id.Skill2angel_icicleBtn:
	        	currentSkill2Checked = 19;
	        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
	        	lvlNeeded = 1;
	        	unitsNeeded = 1;
	        	skillPointer = 10;
	          break;
	        case R.id.Skill2angel_thunderBtn:
	        	currentSkill2Checked = 5;
	        	description.setText("Any enemy hiding somewhere? Flush him out with your lightning.");
	        	lvlNeeded = 1;
	        	unitsNeeded = 1;
	        	skillPointer = 7;
	        	break;
	        case R.id.Skill2angel_piorunBtn:
	        	currentSkill2Checked = 26;
	        	description.setText("Magic bullets forged by no one else but Zeus dazzle your enemies.");
	        	lvlNeeded = 1;
	        	unitsNeeded = 1;
	        	skillPointer = 9;
	        	break;  
	        case R.id.Skill2angel_healBtn:
	        	currentSkill2Checked = 17;
	        	description.setText("Everybody make mistakes. Heal yourself and forget about it.");
	        	lvlNeeded = 1;
	        	unitsNeeded = 1;
	        	skillPointer = 3;
	        	break;
	        case R.id.Skill2angel_heal2Btn:
	        	currentSkill2Checked = 104;
	        	description.setText("Heal yourself and your friends.");
	        	lvlNeeded = 1;
	        	unitsNeeded = 1;
	        	skillPointer = 8;
	        	break;  
	        case R.id.Skill2angel_warmthBtn:
	        	currentSkill2Checked = 14;
	        	description.setText("Small aura following you heals you and your friends.");
	        	lvlNeeded = 1;
	        	unitsNeeded = 1;
	        	skillPointer = 11;
	        	break;  
	        case R.id.Skill2angel_potionBtn:
	        	currentSkill2Checked = 29;
	        	description.setText("Long lasting, but effective healing.");
	        	lvlNeeded = 1;
	        	unitsNeeded = 1;
	        	skillPointer = 6;
	        	break;    
	        case R.id.Skill2angel_shockwaveBtn:
	        	currentSkill2Checked = 18;
	        	description.setText("Freeze your enemies and leave them in the lurch. Or in the grace of your weapon.");
	        	lvlNeeded = 1;
	        	unitsNeeded = 1;
	        	skillPointer = 2;
	        	break;  
	        case R.id.Skill2angel_smokeBtn:
	        	currentSkill2Checked = 20;
	        	description.setText("Smoke denudes sight of the battlefield.");
	        	lvlNeeded = 1;
	        	unitsNeeded = 1;
	        	skillPointer = 0;
	        	break;  
	        case R.id.Skill2angel_trapBtn:
	        	currentSkill2Checked = 13;
	        	description.setText("Lay a trap for all these fo(ol)es and freeze them.");
	        	lvlNeeded = 1;
	        	unitsNeeded = 1;
	        	skillPointer = 4;
	        	break; 
	        case R.id.Skill2angel_wingsBtn:
	        	currentSkill2Checked = 23;
	        	description.setText("What are these wings for? For flying of course.");
	        	lvlNeeded = 1;
	        	unitsNeeded = 1;
	        	skillPointer = 1;
	        	break;
	        case R.id.Skill2angel_denialBtn:
	        	currentSkill2Checked = 28;
	        	description.setText("Use their bullets against themselves. Return projectiles to sender.");
	        	lvlNeeded = 1;
	        	unitsNeeded = 1;
	        	skillPointer = 12;
	        	break;
	        case R.id.Skill2angel_defBtn:
	        	currentSkill2Checked = 24;
	        	description.setText("Decrease quantity of damage you receive by increasing your defence.");
	        	lvlNeeded = 1;
	        	unitsNeeded = 1;
	        	skillPointer = 5;
	        	break;  
	        case R.id.Skill2angel_infinityBtn:
	        	currentSkill2Checked = 25;
	        	description.setText("They will run a mile. Become immortal for the short period of time.");
	        	lvlNeeded = 1;
	        	unitsNeeded = 1;
	        	skillPointer = 13;
	        	break;
	      }
	      lvl.setText(lvlNeeded+" lvl");
	      cost.setText(unitsNeeded+" units");
	      units.setText("your units: "+YoloEngine.currentPlayerInfo.getUnits());
	}
	
public void skill2angelEqBtnClick(View v){
	ImageView currentSkill;
	String skillTab = YoloEngine.currentPlayerInfo.getSkill1();
	String newSkillTab;
	boolean bought = false;
	if (v.getId()==R.id.Skill2EqBtn)
	{
		currentSkill = (ImageView) findViewById(R.id.currentSkillAngel2);
	}
	else if (v.getId()==R.id.Skill3EqBtn) {
		currentSkill = (ImageView) findViewById(R.id.currentSkillAngel3);
	}
	else currentSkill = (ImageView) findViewById(R.id.currentSkillAngel1);
	switch(currentSkill2Checked) {
	case 18:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skillangelshockwave1);
    		bought = true;
		}
		else if (skillTab.charAt(1)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skillangelshockwave1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 19:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skillangelicicle1);
    		bought = true;
		}
		else if (skillTab.charAt(7)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skillangelicicle1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 5:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skillnecromancerthunder1);
    		bought = true;
		}
		else if (skillTab.charAt(4)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skillnecromancerthunder1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
    	break;
	case 26:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skillangelpiorun1);
    		bought = true;
		}
		else if (skillTab.charAt(7)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skillangelpiorun1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 20:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skillangelsmoke1);
    		bought = true;
		}
		else if (skillTab.charAt(0)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skillangelsmoke1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 17:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skillangelheal1);
    		bought = true;
		}
		else if (skillTab.charAt(1)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skillangelheal1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 104:
		if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skillangelheal1);
    		bought = true;
		}
		else if (skillTab.charAt(6)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skillangelheal1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 14:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skillangelwarmth1);
    		bought = true;
		}
		else if (skillTab.charAt(8)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skillangelwarmth1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;  
	case 29:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skillangelpotion1);
    		bought = true;
		}
		else if ((skillTab.charAt(3)=='1' || skillTab.charAt(5)=='1') && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skillangelpotion1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 13:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skillangeltrap1);
    		bought = true;
		}
		else if ((skillTab.charAt(2)=='1' || skillTab.charAt(5)=='1') && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skillangeltrap1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;  
	case 23:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skillangelwings1);
    		bought = true;
		}
		else if (skillTab.charAt(0)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skillangelwings1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 25:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skillangelinfinity1);
    		bought = true;
		}
		else if (skillTab.charAt(11)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skillangelinfinity1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break; 
	case 28:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skillangeldenial1);
    		bought = true;
		}
		else if ((skillTab.charAt(9)=='1' || skillTab.charAt(10)=='1') && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skillangeldenial1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break; 
	case 24:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skillangeldef1);
    		bought = true;
		}
		else if ((skillTab.charAt(6)=='1' || skillTab.charAt(4)=='1') && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skillangeldef1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break; 
  }
	if (bought == true)
	{
	if (v.getId()==R.id.Skill2EqBtn)
	{
		YoloEngine.currentPlayerInfo.setSK2EQ(currentSkill2Checked);
		YoloEngine.SkillSprite2=currentSkill2Checked;
		System.out.println("pierwszy skill loaded "+YoloEngine.SkillSprite2);
	}
	else if (v.getId()==R.id.Skill3EqBtn){
		YoloEngine.currentPlayerInfo.setSK3EQ(currentSkill2Checked);
		YoloEngine.SkillSprite3=currentSkill2Checked;
		System.out.println("drugi skill loaded "+YoloEngine.SkillSprite3);
	}
	else {
		YoloEngine.currentPlayerInfo.setSK1EQ(currentSkill2Checked);
		YoloEngine.SkillSprite1=currentSkill2Checked;
		System.out.println("drugi skill loaded "+YoloEngine.SkillSprite3);
	}
	skills2angelTreeDraw();
	TextView units = (TextView) findViewById(R.id.skill2angelunits);
	units.setText("your units: "+YoloEngine.currentPlayerInfo.getUnits());
	}
	else {if (buying == true)
		 Toast.makeText(getApplicationContext(), 
                "Too low level, not enough units or unlock earlier skills", Toast.LENGTH_LONG).show();
	}
	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
}

public void skills2devilTreeDraw()
{
	String skillTab = YoloEngine.currentPlayerInfo.getSkill1();
	ImageView branch2a = (ImageView) findViewById(R.id.branch2a);
	ImageView branch2b = (ImageView) findViewById(R.id.branch2b);
	ImageView branch2c = (ImageView) findViewById(R.id.branch2c);
	ImageView branch2d = (ImageView) findViewById(R.id.branch2d);
	ImageView branch3a = (ImageView) findViewById(R.id.branch3a);
	ImageView branch3b = (ImageView) findViewById(R.id.branch3b);
	ImageView branch3c = (ImageView) findViewById(R.id.branch3c);
	ImageView branch4a = (ImageView) findViewById(R.id.branch4a);
	ImageView branch4b = (ImageView) findViewById(R.id.branch4b);
	ImageView branch4c = (ImageView) findViewById(R.id.branch4c);
	ImageView branch5a = (ImageView) findViewById(R.id.branch5a);
	ImageView branch5b = (ImageView) findViewById(R.id.branch5b);
	ImageView branch5c = (ImageView) findViewById(R.id.branch5c);
	ImageView branch6a = (ImageView) findViewById(R.id.branch6a);
	ImageView branch6b = (ImageView) findViewById(R.id.branch6b);
	ImageView branch6c = (ImageView) findViewById(R.id.branch6c);
	ImageView branch7a = (ImageView) findViewById(R.id.branch7a);
	ImageView branch7b = (ImageView) findViewById(R.id.branch7b);
	ImageView branch8a = (ImageView) findViewById(R.id.branch8a);
	ImageView branch8b = (ImageView) findViewById(R.id.branch8b);
	Button skillShockwave = (Button) findViewById(R.id.Skill2devil_schockwaveBtn);
	Button skillDmgSelf = (Button) findViewById(R.id.Skill2devil_dmgSelfBtn);
	Button skillDmgRadius = (Button) findViewById(R.id.Skill2devil_dmgRadiusBtn);
	Button skillDmgTeam = (Button) findViewById(R.id.Skill2devil_dmgTeamBtn);
	Button skillRlSelf = (Button) findViewById(R.id.Skill2devil_rlSelfBtn);
	Button skillRlRadius = (Button) findViewById(R.id.Skill2devil_rlRadiusBtn);
	Button skillRlTeam = (Button) findViewById(R.id.Skill2devil_rlTeamBtn);
	Button skillfrSelf = (Button) findViewById(R.id.Skill2devil_frSelfBtn);
	Button skillfrRadius = (Button) findViewById(R.id.Skill2devil_frRadiusBtn);
	Button skillfrTeam = (Button) findViewById(R.id.Skill2devil_frTeamBtn);
	Button skillLava = (Button) findViewById(R.id.Skill2devil_lavaBtn);
	Button skillThief = (Button) findViewById(R.id.Skill2devil_thiefBtn);
	Button skillFireBall = (Button) findViewById(R.id.Skill2devil_fireBallBtn);
	Button skillStamina = (Button) findViewById(R.id.Skill2devil_staminaBtn);
	if (skillTab.charAt(1)=='1')
	{
		branch2a.setBackgroundResource(R.drawable.branch_lwd1);
		branch2b.setBackgroundResource(R.drawable.branch_poz1);
		branch2c.setBackgroundResource(R.drawable.branch_poz1);
		branch2d.setBackgroundResource(R.drawable.branch_pwd1);
		branch3a.setBackgroundResource(R.drawable.branch_norm1);
		branch3b.setBackgroundResource(R.drawable.branch_norm1);
		branch3c.setBackgroundResource(R.drawable.branch_norm1);
		skillShockwave.setBackgroundResource(R.drawable.skilldevilschockwave1);
	}
	if (skillTab.charAt(2)=='1')
	{
		branch4a.setBackgroundResource(R.drawable.branch_norm1);
		skillDmgSelf.setBackgroundResource(R.drawable.skilldevildmgself1);
	}
	if (skillTab.charAt(5)=='1')
	{
		branch5a.setBackgroundResource(R.drawable.branch_norm1);
		skillDmgRadius.setBackgroundResource(R.drawable.skilldevildmgradius1);
	}
	if (skillTab.charAt(8)=='1')
	{
		branch6a.setBackgroundResource(R.drawable.branch_norm1);
		skillDmgTeam.setBackgroundResource(R.drawable.skilldevildmgteam1);
	}
	if (skillTab.charAt(3)=='1')
	{
		branch4b.setBackgroundResource(R.drawable.branch_norm1);
		skillfrSelf.setBackgroundResource(R.drawable.skilldevilfrself1);
	}
	if (skillTab.charAt(6)=='1')
	{
		branch5b.setBackgroundResource(R.drawable.branch_norm1);
		skillfrRadius.setBackgroundResource(R.drawable.skilldevilfrradius1);
	}
	if (skillTab.charAt(9)=='1')
	{
		branch6b.setBackgroundResource(R.drawable.branch_norm1);
		branch7a.setBackgroundResource(R.drawable.branch_poz1);
		branch7b.setBackgroundResource(R.drawable.branch_pwg1);
		skillfrTeam.setBackgroundResource(R.drawable.skilldevilfrteam1);
	}
	if (skillTab.charAt(4)=='1')
	{
		branch4c.setBackgroundResource(R.drawable.branch_norm1);
		skillRlSelf.setBackgroundResource(R.drawable.skilldevilrl1);
	}
	if (skillTab.charAt(7)=='1')
	{
		branch5c.setBackgroundResource(R.drawable.branch_norm1);
		skillRlRadius.setBackgroundResource(R.drawable.skilldevilrlradius1);
	}
	if (skillTab.charAt(10)=='1')
	{
		branch6c.setBackgroundResource(R.drawable.branch_norm1);
		skillRlTeam.setBackgroundResource(R.drawable.skilldevilrlteam1);
	}
	if (skillTab.charAt(11)=='1')
	{
		branch8a.setBackgroundResource(R.drawable.branch_norm1);
		skillLava.setBackgroundResource(R.drawable.skilldevillava1);
	}
	if (skillTab.charAt(12)=='1')
	{
		branch8b.setBackgroundResource(R.drawable.branch_norm1);
		skillThief.setBackgroundResource(R.drawable.skilldevilthief1);
	}
	if (skillTab.charAt(13)=='1')
	{
		skillFireBall.setBackgroundResource(R.drawable.skilldevilfireball1);
	}
	if (skillTab.charAt(14)=='1')
	{
		skillStamina.setBackgroundResource(R.drawable.skilldevilstamina1);
	}
}

public void skills2devilBtnClick(View v) {
	
	TextView description = (TextView) findViewById(R.id.skill2devildescription);
	TextView lvl = (TextView) findViewById(R.id.skill2devillvlneeded);
	TextView cost = (TextView) findViewById(R.id.skill2devilcost);
	TextView units = (TextView) findViewById(R.id.skill2devilunits);
	
      switch(v.getId()) {
        case R.id.Skill2devil_schockwaveBtn:
        	currentSkill2Checked = 43;
        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
        	lvlNeeded = 1;
        	unitsNeeded = 1;
        	skillPointer = 1;
          break;
        case R.id.Skill2devil_dmgSelfBtn:
        	currentSkill2Checked = 36;
        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
        	lvlNeeded = 1;
        	unitsNeeded = 1;
        	skillPointer = 2;
        	break;
        case R.id.Skill2devil_dmgRadiusBtn:
        	currentSkill2Checked = 119;
        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
        	lvlNeeded = 1;
        	unitsNeeded = 1;
        	skillPointer = 5;
          break;
        case R.id.Skill2devil_dmgTeamBtn:
        	currentSkill2Checked = 120;
        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
        	lvlNeeded = 1;
        	unitsNeeded = 1;
        	skillPointer = 8;
          break;
        case R.id.Skill2devil_lavaBtn:
        	currentSkill2Checked = 33;
        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
        	lvlNeeded = 1;
        	unitsNeeded = 1;
        	skillPointer = 11;
          break;
        case R.id.Skill2devil_thiefBtn:
        	currentSkill2Checked = 126;
        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
        	lvlNeeded = 1;
        	unitsNeeded = 1;
        	skillPointer = 12;
          break;
        case R.id.Skill2devil_fireBallBtn:
        	currentSkill2Checked = 30;
        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
        	lvlNeeded = 1;
        	unitsNeeded = 1;
        	skillPointer = 13;
          break;
        case R.id.Skill2devil_teleportBtn:
        	currentSkill2Checked = 31;
        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
        	lvlNeeded = 1;
        	unitsNeeded = 1;
        	skillPointer = 0;
          break;
        case R.id.Skill2devil_staminaBtn:
        	currentSkill2Checked = 127;
        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
        	lvlNeeded = 1;
        	unitsNeeded = 1;
        	skillPointer = 14;
          break;
        case R.id.Skill2devil_frSelfBtn:
        	currentSkill2Checked = 37;
        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
        	lvlNeeded = 1;
        	unitsNeeded = 1;
        	skillPointer = 3;
          break;
        case R.id.Skill2devil_frRadiusBtn:
        	currentSkill2Checked = 121;
        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
        	lvlNeeded = 1;
        	unitsNeeded = 1;
        	skillPointer = 6;
          break;
        case R.id.Skill2devil_frTeamBtn:
        	currentSkill2Checked = 123;
        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
        	lvlNeeded = 1;
        	unitsNeeded = 1;
        	skillPointer = 9;
          break;
        case R.id.Skill2devil_rlSelfBtn:
        	currentSkill2Checked = 38;
        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
        	lvlNeeded = 1;
        	unitsNeeded = 1;
        	skillPointer = 4;
          break;
        case R.id.Skill2devil_rlRadiusBtn:
        	currentSkill2Checked = 122;
        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
        	lvlNeeded = 1;
        	unitsNeeded = 1;
        	skillPointer = 7;
          break;
        case R.id.Skill2devil_rlTeamBtn:
        	currentSkill2Checked = 124;
        	description.setText("Turn your normal bullets into icicles and freeze your enemies.");
        	lvlNeeded = 1;
        	unitsNeeded = 1;
        	skillPointer = 10;
          break;}
      lvl.setText(lvlNeeded+" lvl");
      cost.setText(unitsNeeded+" units");
      units.setText("your units: "+YoloEngine.currentPlayerInfo.getUnits());
}


public void skill2devilEqBtnClick(View v){
	Button currentSkill;
	String skillTab = YoloEngine.currentPlayerInfo.getSkill1();
	String newSkillTab;
	boolean bought = false;
	if (v.getId()==R.id.Skill2EqBtn)
	{
		currentSkill = (Button) findViewById(R.id.currentSkillDevil2);
	}
	else if (v.getId()==R.id.Skill3EqBtn){
		currentSkill = (Button) findViewById(R.id.currentSkillDevil3);
	}
	else currentSkill = (Button) findViewById(R.id.currentSkillDevil1);
	switch(currentSkill2Checked) {
	case 43:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skilldevilschockwave1);
    		bought = true;
		}
		else if (skillTab.charAt(0)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skilldevilschockwave1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 36:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skilldevildmgself1);
    		bought = true;
		}
		else if (skillTab.charAt(1)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skilldevildmgself1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 119:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skilldevildmgradius1);
    		bought = true;
		}
		else if (skillTab.charAt(2)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skilldevildmgradius1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 120:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skilldevildmgteam1);
    		bought = true;
		}
		else if (skillTab.charAt(5)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skilldevildmgteam1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 33:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skilldevillava1);
    		bought = true;
		}
		else if ((skillTab.charAt(9)=='1' || skillTab.charAt(8)=='1') && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skilldevillava1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 126:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skilldevilthief1);
    		bought = true;
		}
		else if (skillTab.charAt(10)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skilldevilthief1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 30:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skilldevilfireball1);
    		bought = true;
		}
		else if (skillTab.charAt(11)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skilldevilfireball1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 31:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skilldevilteleport1);
    		bought = true;
		}
		else if (skillTab.charAt(0)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skilldevilteleport1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 127:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skilldevilstamina1);
    		bought = true;
		}
		else if (skillTab.charAt(12)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skilldevilstamina1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 37:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skilldevilfrself1);
    		bought = true;
		}
		else if (skillTab.charAt(1)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skilldevilfrself1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 121:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skilldevilfrradius1);
    		bought = true;
		}
		else if (skillTab.charAt(3)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skilldevilfrradius1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 123:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skilldevilfrteam1);
    		bought = true;
		}
		else if (skillTab.charAt(6)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skilldevilfrteam1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 38:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skilldevilrl1);
    		bought = true;
		}
		else if (skillTab.charAt(1)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skilldevilrl1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 122:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skilldevilrlradius1);
    		bought = true;
		}
		else if (skillTab.charAt(4)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skilldevilrlradius1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
	case 124:
    	if (skillTab.charAt(skillPointer)=='1')
		{
    		currentSkill.setBackgroundResource(R.drawable.skilldevilrlteam1);
    		bought = true;
		}
		else if (skillTab.charAt(7)=='1' && YoloEngine.currentPlayerInfo.getUnits()>=unitsNeeded && YoloEngine.currentPlayerInfo.getLevel()>=lvlNeeded)
		{
			currentSkill.setBackgroundResource(R.drawable.skilldevilrlteam1);
			YoloEngine.currentPlayerInfo.setUnits(YoloEngine.currentPlayerInfo.getUnits()-unitsNeeded);
			newSkillTab = skillTab.substring(0, skillPointer)+"1"+skillTab.substring(skillPointer+1);
			YoloEngine.currentPlayerInfo.setSkill1(newSkillTab);
			bought=true;
		}
      break;
  }
	if (bought == true)
	{
	if (v.getId()==R.id.Skill2EqBtn)
	{
		YoloEngine.currentPlayerInfo.setSK2EQ(currentSkill2Checked);
		YoloEngine.SkillSprite2=currentSkill2Checked;
		System.out.println("pierwszy skill loaded "+YoloEngine.SkillSprite2);
	}
	else if (v.getId()==R.id.Skill3EqBtn){
		YoloEngine.currentPlayerInfo.setSK3EQ(currentSkill2Checked);
		YoloEngine.SkillSprite3=currentSkill2Checked;
		System.out.println("drugi skill loaded "+YoloEngine.SkillSprite3);
	}
	else {
		YoloEngine.currentPlayerInfo.setSK1EQ(currentSkill2Checked);
		YoloEngine.SkillSprite1=currentSkill2Checked;
		System.out.println("drugi skill loaded "+YoloEngine.SkillSprite3);
	}
	skills2devilTreeDraw();
	TextView units = (TextView) findViewById(R.id.skill2devilunits);
	units.setText("your units: "+YoloEngine.currentPlayerInfo.getUnits());
	}
	else {if (buying == true)
		 Toast.makeText(getApplicationContext(), 
                "Too low level, not enough units or unlock earlier skills", Toast.LENGTH_LONG).show();
	}
	dbm.updatePlayer(YoloEngine.currentPlayerInfo);
}


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

