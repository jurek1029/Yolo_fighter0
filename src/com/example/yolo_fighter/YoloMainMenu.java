package com.example.yolo_fighter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

import android.app.Activity;
import android.app.AlertDialog;
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
	
	
// ------------------------- Multislayer BEGIN -----------------------
	
	private boolean mSignInprogress = false;
	private AlertDialog.Builder askInvitation; 
	private Invitation IncomingInvitation;
	private int RC_SELECT_PLAYERS;
	private int RC_SIGNIN = 9001;
	int playerIDd = 0;
	Button btn_quick;
	Button btn_invite;
	TextView debug_textview;
	
	private String[] MessString;
	
	RoomUpdateListener mRoomUpdateListener = new RoomUpdateListener() {
		// https://developer.android.com/reference/com/google/android/gms/games/multiplayer/realtime/RoomUpdateListener.html

		@Override
		public void onRoomCreated(int arg0, Room arg1) {
			YoloEngine.cRoom = arg1;
			System.out.println("Room created");
			debug_textview.setText("Room created");
		}

		@Override
		public void onRoomConnected(int statusCode, Room room) {

			System.out.println("Room connected");
			debug_textview.setText("Room connected");
			
			YoloEngine.cRoom = room;

			YoloEngine.mMultislayer.sendMessageToAllreliable((YoloEngine.SkillSprite1+"|"+YoloEngine.SkillSprite2+"|"+YoloEngine.SkillSprite3).getBytes());
			
			// byte[] ff = "test".getBytes();
		}

		@Override
		public void onLeftRoom(int arg0, String arg1) {

			System.out.println("Room left");
		}

		@Override
		public void onJoinedRoom(int arg0, Room arg1) {
			YoloEngine.cRoom = arg1;
			System.out.println("Room joined code: " + arg0);
			debug_textview.setText("Room joined code: " + arg0);
		}
	};
	
	RoomStatusUpdateListener mRoomStatusUpdateListener = new RoomStatusUpdateListener() {
		
		@Override
		public void onPeersDisconnected(Room arg0, List<String> arg1) {
			// TODO Auto-generated method stub
			
			System.out.println("onPeersDisconnected");
			

		}
		
		@Override
		public void onPeersConnected(Room arg0, List<String> arg1) {


			
			System.out.println("onPeersConnected");
			

			

		}

		@Override
		public void onConnectedToRoom(Room room) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDisconnectedFromRoom(Room room) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onP2PConnected(String participantId) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onP2PDisconnected(String participantId) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPeerDeclined(Room arg0, List<String> arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPeerInvitedToRoom(Room arg0, List<String> arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPeerJoined(Room arg0, List<String> arg1) {
			// TODO Auto-generated method stub
			for(int i = 0; i < arg1.size(); i++){
				YoloEngine.opponents[i] = arg1.get(i);
			}
	
			System.out.println("onPeerJoined");
	
			
		}

		@Override
		public void onPeerLeft(Room arg0, List<String> arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRoomAutoMatching(Room room) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRoomConnecting(Room room) {
			// TODO Auto-generated method stub
			
		}
	};
	
	RealTimeMessageReceivedListener mRTMreceiveList = new RealTimeMessageReceivedListener() {

		@Override
		public void onRealTimeMessageReceived(RealTimeMessage message) {
			
			playerIDd=0;
			String dd = new String(message.getMessageData());
			
			MessString = dd.split("\\|");
			if (MessString.length == 4) {

				for (int i = 0; i < 4; i++)
					if (YoloEngine.opponents[i].equals(message
							.getSenderParticipantId())) {
						playerIDd = i;
						break;
					}


				YoloEngine.mMultislayer.DataReceived(playerIDd,
						Float.parseFloat(dd.split("\\|")[0]),
						Float.parseFloat(dd.split("\\|")[1]),
						Boolean.parseBoolean(dd.split("\\|")[2]),
						Integer.parseInt(dd.split("\\|")[3]));
			}
			else if (MessString.length == 8)
				YoloGameRenderer.skillOponentVe.add(new Skill(Float.parseFloat(dd.split("\\|")[0]), Float.parseFloat(dd.split("\\|")[1]), Integer.parseInt(dd.split("\\|")[2]), Integer.parseInt(dd.split("\\|")[3]), Float.parseFloat(dd.split("\\|")[4]), Float.parseFloat(dd.split("\\|")[5]), Float.parseFloat(dd.split("\\|")[6]), Float.parseFloat(dd.split("\\|")[7])));
			else if (MessString.length == 3) {
				YoloEngine.sprite_load[ Integer.parseInt(dd.split("\\|")[0]) ] = true;
				YoloEngine.sprite_load[ Integer.parseInt(dd.split("\\|")[1]) ] = true;
				YoloEngine.sprite_load[ Integer.parseInt(dd.split("\\|")[2]) ] = true;
			}
			else {
				YoloGameRenderer.OpponentFire( Float.parseFloat(dd.split("\\|")[0]), Float.parseFloat(dd.split("\\|")[1]), Boolean.parseBoolean(dd.split("\\|")[2]),Boolean.parseBoolean(dd.split("\\|")[3]));
			}
			
			
				
		}
	};
	
// ------------------------- Multislayer END -------------------------
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 

	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		YoloEngine.opponents[0] = "";
		YoloEngine.opponents[1] = "";
		YoloEngine.opponents[2] = "";
		YoloEngine.opponents[3] = "";

		
// ------------------------- Multislayer BEGIN -----------------------

		btn_quick = (Button) findViewById(R.id.quick_button);
		btn_invite = (Button) findViewById(R.id.invite_button);
		debug_textview = (TextView) findViewById(R.id.textView1);
		btn_quick.setEnabled(false);
		btn_invite.setEnabled(false);
		
		askInvitation = new AlertDialog.Builder(YoloMainMenu.this).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				System.out.println("Invitation accepted");
				Games.RealTimeMultiplayer.join(YoloEngine.mHelper.getApiClient(), prepareGame(false, IncomingInvitation));
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

					Games.RealTimeMultiplayer.join(YoloEngine.mHelper.getApiClient(), prepareGame(false, YoloEngine.mHelper.getInvitation()));

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
		
		// Te dwie instrukcje warto wrzuciæ do jakiegoœ senwoengo eventu, ¿eby ci¹gle tego nie odœwie¿aæ XXX
						
		
		
		
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
			if (YoloEngine.cRoom != null)
				if (YoloEngine.cRoom.getStatus() != 6)
					Games.RealTimeMultiplayer.leave(YoloEngine.mHelper.getApiClient(), mRoomUpdateListener, YoloEngine.cRoom.getRoomId());

			//YoloEngine.mHelper.signOut();
			// mHelper.disconnect(); od³¹cza, nie wylogowuje
		}
		
	}
	
	
	public void startQuickGame(View v) {
		YoloEngine.multiActive = true;
		//YoloEngine.opponentsNo = 1; // TODO
		
		Games.RealTimeMultiplayer.create(YoloEngine.mHelper.getApiClient(), prepareGame(true, null));

		// prevent screen from sleeping during handshake
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// go to game screen */
	}
	
	public void invite(View v) {
		YoloEngine.multiActive = true;

		// request code for the "select players" UI
		// can be any number as long as it's unique
		RC_SELECT_PLAYERS = 10000;

		// launch the player selection screen
		// minimum: 1 other player; maximum: 3 other players
		Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(YoloEngine.mHelper.getApiClient(), 1, 3);
		startActivityForResult(intent, RC_SELECT_PLAYERS);
	}
	
	
	private RoomConfig prepareGame(boolean automatch, Invitation invitation)
	{
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(mRoomUpdateListener);	
		roomConfigBuilder.setRoomStatusUpdateListener(mRoomStatusUpdateListener);
		roomConfigBuilder.setMessageReceivedListener(mRTMreceiveList);
		
		if(automatch) {
			// automatch criteria
			Bundle am = RoomConfig.createAutoMatchCriteria(1, 2, 0);
			roomConfigBuilder.setAutoMatchCriteria(am);
		}
		if(!(invitation == null)) {
			roomConfigBuilder.setInvitationIdToAccept(invitation.getInvitationId());
		}
		RoomConfig roomConfig = roomConfigBuilder.build();	
		return roomConfig;
	}
	
	@Override
	public void onActivityResult(int request, int response, Intent data) {
		
		if(request == RC_SIGNIN) { // Wracamy z powrotem do gameHelpera, który otworzy³ okienko i chce wiedzieæ co siê sta³o
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
			
			roomConfigBuilder.setMessageReceivedListener(mRTMreceiveList);
			
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
	
}
