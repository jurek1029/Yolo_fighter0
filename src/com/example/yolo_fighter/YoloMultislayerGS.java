package com.example.yolo_fighter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

public class YoloMultislayerGS extends YoloMultislayerBase implements RealTimeMessageReceivedListener, RoomUpdateListener {

	private boolean mSignInprogress = false;
	private AlertDialog.Builder askInvitation;
	private Invitation IncomingInvitation;
	private int RC_SIGNIN = 9001;
	private int RC_SELECT_PLAYERS = 10000;
	
	private Room mRoom;
	private GameHelper mHelper;

	

	public YoloMultislayerGS(final Activity xActivity) {
		this.mActivity = xActivity;		
			
		debugLog("");	
		YoloEngine.timeOffset = 200;
		
		
		mHelper = new GameHelper(mActivity, GameHelper.CLIENT_GAMES);
		mHelper.enableDebugLog(true);

		GameHelperListener listener = new GameHelper.GameHelperListener() {
			@Override
			public void onSignInSucceeded() {

				// mHelper.beginUserInitiatedSignIn();
				debugLog("Signed in successfully");

				

				mSignInprogress = false;
				if (mHelper.getInvitationId() != null) {
					// accept invitation
					Games.RealTimeMultiplayer.join(mHelper.getApiClient(), prepareGame(mHelper.getInvitation()));
				}

				Games.Invitations.registerInvitationListener(mHelper.getApiClient(), new OnInvitationReceivedListener() {

					@Override
					public void onInvitationRemoved(String invitationId) {
						debugLog("Inv removed");
					}

					@Override
					public void onInvitationReceived(Invitation invitation) {
						debugLog("Invitation arrived, asking");
						IncomingInvitation = invitation;
						System.out.println(mActivity.getTaskId()+" id zapisanej");
						askInvitation = new AlertDialog.Builder(mActivity).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								cleanupBeforeNewPlay();
								System.out.println("Invitation accepted");
								Games.RealTimeMultiplayer.join(mHelper.getApiClient(), prepareGame(IncomingInvitation));
							}
						}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								System.out.println("Invitation rejected");
							}
						}).setIcon(android.R.drawable.ic_dialog_info);

						askInvitation.setTitle(R.string.new_invitation);
						askInvitation.setMessage(invitation.getInviter().getDisplayName() + " " + mActivity.getString(R.string.invitation_dialog));
						askInvitation.show();

					}
				});
				
				if(mRunQuick) {
					mRunQuick = false;
					startQuickGame();
				}

			}

			@Override
			public void onSignInFailed() {
				debugLog("Signing in failed :(" + mHelper.getSignInError());
				mRunQuick = false;
			}

		};
		mHelper.setup(listener);
		signIn();
	}

	

	RoomStatusUpdateListener mRoomStatusUpdateListener = new RoomStatusUpdateListener() {

		@Override
		public void onPeerJoined(Room arg0, List<String> arg1) {
			// ktoś dołączył (join)
			System.out.println("onPeerJoined");
		}

		@Override
		public void onPeersConnected(Room room, List<String> participantIds) {
			// ktoś podłączył się

			mRoom = room;
			System.out.println("onPeersConnected");
		}

		@Override
		public void onPeersDisconnected(Room room, List<String> participantIds) {
			mRoom = room;

			System.out.println("onPeersDisconnected");

			for (String p : participantIds) {
				YoloEngine.opponents.remove(p);
				for (int i = 0; i < YoloEngine.TeamSize * 2; i++) {
					if (YoloEngine.TeamAB[i].ParticipantId.equals(p))
						YoloEngine.TeamAB[i].moveAway();
				}
			}

			if (YoloEngine.opponents.size() == 0)
				debugLog("No other players left");
		}

		@Override
		public void onConnectedToRoom(Room room) {
			debugLog("Client connected to room");
		}

		@Override
		public void onDisconnectedFromRoom(Room room) {
			System.out.println("rosja");
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
			System.out.println("Invitation declined by smn");
			//decrease waiting for count?

		}

		@Override
		public void onPeerInvitedToRoom(Room arg0, List<String> arg1) {

		}

		@Override
		public void onPeerLeft(Room arg0, List<String> arg1) {
			System.out.println("niemcy");
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
	private boolean mRunQuick;
	

	@Override
	public void onRealTimeMessageReceived(RealTimeMessage message) {
		ByteBuffer rcvData = ByteBuffer.wrap(message.getMessageData());
		processMessage(rcvData);
	}

	/**
	 * Wysyła dane do wszystkich metoda reliable
	 * 
	 * @param data
	 *            Bytes array
	 */
	@Override
	protected void sendMessageToAllreliable(byte[] data) {
		if (YoloEngine.mGameProperties.gameType == GameProperties.OFFLINE)
			return;

		ArrayList<Participant> mParticipants = mRoom.getParticipants();

		for (Participant p : mParticipants) {
			if (p.getParticipantId().equals(YoloEngine.playerParticipantID))
				continue;
			if (p.getStatus() != Participant.STATUS_JOINED)
				continue;
			else {
				Games.RealTimeMultiplayer.sendReliableMessage(mHelper.getApiClient(), null, data, mRoom.getRoomId().toString(), p.getParticipantId());

			}
		}

	}

	/**
	 * Wysyła dane do wszystkich metoda unreliable
	 * 
	 * @param data
	 *            Bytes array
	 */
	@Override
	protected void sendMessageToAll(byte[] data) {
		if (YoloEngine.mGameProperties.gameType == GameProperties.OFFLINE)
			return;

		Games.RealTimeMultiplayer.sendUnreliableMessageToOthers(mHelper.getApiClient(), data, mRoom.getRoomId().toString());
	}

	@Override
	public void onJoinedRoom(int statusCode, Room room) {
		if (statusCode == GamesStatusCodes.STATUS_OK) {
			debugLog("Room joined");

			YoloEngine.opponents.clear();
			if (YoloEngine.participants != null)
				YoloEngine.participants.clear();

			mRoom = room;
			YoloEngine.playerParticipantID = mRoom.getParticipantId(Games.Players.getCurrentPlayerId(mHelper.getApiClient()));
		}
	}

	@Override
	public void onLeftRoom(int arg0, String arg1) {
		debugLog("Room left");

		YoloEngine.opponents.clear();
		if (YoloEngine.participants != null)
			YoloEngine.participants.clear();
	}

	@Override
	public void onRoomCreated(int statusCode, Room room) {
		if (statusCode == com.google.android.gms.games.GamesStatusCodes.STATUS_OK) {
			debugLog("Room created");

			mActivity.runOnUiThread(new Runnable() {
				public void run() {
					if (mProgressDialog != null)
						mProgressDialog.setMessage("Waiting for other players...");
				}
			});

			mRoom = room;
			YoloEngine.playerParticipantID = mRoom.getParticipantId(Games.Players.getCurrentPlayerId(mHelper.getApiClient()));

			YoloEngine.opponents.clear();
			if (YoloEngine.participants != null)
				YoloEngine.participants.clear();

		} else {

			if (mProgressDialog != null)
				mProgressDialog.dismiss();

			debugLog("Error " + statusCode);
		}
	}

	@Override
	public void onRoomConnected(int statusCode, Room room) {
		if (statusCode == GamesStatusCodes.STATUS_OK) {
			debugLog("All participants connected");
			mRoom = room;
						
			sendMessageToAllreliable(sendPreStartInfo(new int[] { YoloEngine.currentPlayerInfo.getSK1EQ(), YoloEngine.currentPlayerInfo.getSK2EQ(), YoloEngine.currentPlayerInfo.getSK3EQ() }));

			// Prepare participants and opponents list for further use
			YoloEngine.playerParticipantID = mRoom.getParticipantId(Games.Players.getCurrentPlayerId(mHelper.getApiClient()));
			YoloEngine.participants = mRoom.getParticipants();
			Collections.sort(YoloEngine.participants, new Comparator<Participant>() {
				@Override
				public int compare(Participant lhs, Participant rhs) {
					return lhs.getParticipantId().compareTo(rhs.getParticipantId());
				}
			});

			for (Participant p : YoloEngine.participants) {
				if (p.getStatus() == Participant.STATUS_JOINED && p.getParticipantId() != YoloEngine.playerParticipantID) {
					YoloEngine.opponents.add(p.getParticipantId());
				}
			}
			
			
			// If we are the server - assign teams
			if(isServer) { 				
				String teamAssignPattern = assignTeams();
				YoloEngine.TeamAB[YoloEngine.MyID].isServer = true;
				// @TODO czyszczenie listy teamAB, teamA, teamB ?				

				YoloEngine.mMultislayer.sendTeamAssignment(Integer.parseInt(teamAssignPattern, 2));
				YoloEngine.startTime = (System.currentTimeMillis() + YoloEngine.countdownTime + YoloEngine.timeOffset);
				YoloEngine.mMultislayer.sendMaxLife();				
									
				this.notreadyPlayersNumber = mRoom.getParticipantIds().size()-1;
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							sleep(300); // TODO po co ten sleep???
						} catch (InterruptedException e) {						
							e.printStackTrace();
						}
						sendGameProperties();
						
					}
				}).start();			
			}
			
			

			if (mProgressDialog != null) {
				mProgressDialog.setMessage("Starting the game");
				mProgressDialog.dismiss();
			}

		} else if (statusCode == GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED) {
			debugLog("Client reconnect required");
			if (mProgressDialog != null)
				mProgressDialog.dismiss();
		} else if (statusCode == GamesStatusCodes.STATUS_INTERNAL_ERROR) {
			debugLog("Error when connecting");
			if (mProgressDialog != null)
				mProgressDialog.dismiss();
		}
	}


	public void signIn() {
		System.out.println("Signing in");
		mSignInprogress = true;
		mHelper.beginUserInitiatedSignIn();
	}
	
	public void signInAndQuick() {
		System.out.println("Signing in");
		mSignInprogress = true;
		mRunQuick = true;
		mHelper.beginUserInitiatedSignIn();
	}

	public void signOut() {
		// całkowicie wylogowuje, żby np. zmienić konto
		System.out.println("Signing out");

	
		mHelper.signOut();
		// mHelper.disconnect(); odĹ‚Ä…cza, nie wylogowuje
	}

	public void leaveRoom() {
		System.out.println("Leaving the room");

		if (mHelper.getApiClient().isConnected()) {
			if (mRoom != null)
				if (mRoom.getStatus() != 6)
					Games.RealTimeMultiplayer.leave(mHelper.getApiClient(), this, mRoom.getRoomId());
		}
	}

	ProgressDialog mProgressDialog;

	public void startQuickGame( ) {
		// TODO check if signed in
		cleanupBeforeNewPlay();
		fromAutomatch = true;
		fromAutomatchNo = 0;
		
		
		mProgressDialog = new ProgressDialog(mActivity);
		mProgressDialog.show();
		mProgressDialog.setMessage("Connecting...");
		mProgressDialog.setCancelable(true);

		Games.RealTimeMultiplayer.create(mHelper.getApiClient(), prepareGame());		
	}

	@Override
	public void createGame(GameProperties gp) {
		// previously invite
		cleanupBeforeNewPlay();
		isServer = true;
		
		// launch the player selection screen
		Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mHelper.getApiClient(), gp.minPlayers-1, gp.maxPlayers-1, true);
		mActivity.startActivityForResult(intent, RC_SELECT_PLAYERS);
	}
	
	private void cleanupBeforeNewPlay() {
		leaveRoom();
		fromAutomatch = false;
		isServer = false;
		for (int i = 0; i < YoloEngine.TeamAB.length; i++) {			
			YoloEngine.TeamAB[i] =  new YoloPlayer(1000f, 1000f, false, 666);
		}
		if (YoloEngine.participants != null)
			YoloEngine.participants.clear();
		if (YoloEngine.opponents != null)
			YoloEngine.opponents.clear();		
	}

	private RoomConfig prepareGame() {
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
		roomConfigBuilder.setRoomStatusUpdateListener(mRoomStatusUpdateListener);
		roomConfigBuilder.setMessageReceivedListener((RealTimeMessageReceivedListener) YoloEngine.mMultislayer);

		// automatch criteria
		Bundle am = RoomConfig.createAutoMatchCriteria(1, 2, 0);
		roomConfigBuilder.setAutoMatchCriteria(am);

		RoomConfig roomConfig = roomConfigBuilder.build();
		return roomConfig;
	}

	private RoomConfig prepareGame(Invitation invitation) {
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
		roomConfigBuilder.setRoomStatusUpdateListener(mRoomStatusUpdateListener);
		roomConfigBuilder.setMessageReceivedListener((RealTimeMessageReceivedListener) YoloEngine.mMultislayer);

		roomConfigBuilder.setInvitationIdToAccept(invitation.getInvitationId());
		RoomConfig roomConfig = roomConfigBuilder.build();
		return roomConfig;
	}

	public void incomingAction(int request, int response, Intent data) {
		if (request == RC_SIGNIN) { // Wracamy z powrotem do gameHelpera, który otworzył‚ okienko i chce wiedzieć co się stało
			mHelper.onActivityResult(request, response, data);
		}

		if (request == RC_SELECT_PLAYERS) {
			if (response != Activity.RESULT_OK) {
				debugLog("result: bad");
				return;
				// user canceled 
			}
			

			// get the invitee list
			//Bundle extras = data.getExtras();
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
			RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);

			roomConfigBuilder.setMessageReceivedListener((RealTimeMessageReceivedListener) YoloEngine.mMultislayer);

			roomConfigBuilder.addPlayersToInvite(invitees);
			if (autoMatchCriteria != null) {
				roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
			}
			RoomConfig roomConfig = roomConfigBuilder.build();
			Games.RealTimeMultiplayer.create(mHelper.getApiClient(), roomConfig);
			
			debugLog("invitation sent");
			Toast toast = Toast.makeText(mActivity.getApplicationContext(), R.string.invitation_sent, Toast.LENGTH_LONG);
			toast.show();
		}

	}

	@Override
	protected void manuallyAssignTeams() {
		if (YoloEngine.playerParticipantID.equals(YoloEngine.participants.get(0).getParticipantId())) {
			isServer = false;
			YoloEngine.TeamAB[YoloEngine.MyID].isServer = true;
			String teamAssignPattern = assignTeams();
			YoloEngine.mMultislayer.sendTeamAssignment(Integer.parseInt(teamAssignPattern, 2));
			YoloEngine.startTime = (System.currentTimeMillis() + YoloEngine.countdownTime + YoloEngine.timeOffset);
			YoloEngine.mMultislayer.sendMaxLife();
			
			sendStateChangedMessage(1);
			startTheGame();
						
		}

	}
	

	

}
