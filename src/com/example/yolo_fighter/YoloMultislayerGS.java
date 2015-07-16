package com.example.yolo_fighter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
		if(YoloMainMenu.btn_invite != null && YoloMainMenu.btn_quick != null) {
			YoloMainMenu.btn_quick.setEnabled(false);
			YoloMainMenu.btn_invite.setEnabled(false);
			YoloMainMenu.btn_invite.setText("Invite");
			YoloMainMenu.btn_quick.setText("Quick");	
		}
		YoloEngine.timeOffset = 200;
		
		mHelper = new GameHelper(mActivity, GameHelper.CLIENT_GAMES);
		mHelper.enableDebugLog(true);

		GameHelperListener listener = new GameHelper.GameHelperListener() {
			@Override
			public void onSignInSucceeded() {

				// mHelper.beginUserInitiatedSignIn();
				debugLog("Signed in successfully");

				// POTENTIAL_PROBLEM generalnie brzydkie że static w
				// YoloMainMenu, ale buttony są tylko do testów
				YoloMainMenu.btn_quick.setEnabled(true);
				YoloMainMenu.btn_invite.setEnabled(true);

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

			}

			@Override
			public void onSignInFailed() {
				debugLog("Signing in failed :(" + mHelper.getSignInError());
			}

		};

		mHelper.setup(listener);
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
		if (!YoloEngine.MULTI_ACTIVE)
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
		if (!YoloEngine.MULTI_ACTIVE)
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
			for (YoloPlayer p : YoloEngine.TeamAB)
				p = new YoloPlayer(1000f, 1000f, false, 666);
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

			YoloEngine.MULTI_ACTIVE = true;

			mRoom = room;

			YoloEngine.mMultislayer.sendMessageToAllreliable(YoloEngine.mMultislayer.sendSpriteLoad(new int[] { YoloEngine.currentPlayerInfo.getSK1EQ(), YoloEngine.currentPlayerInfo.getSK2EQ(),
					YoloEngine.currentPlayerInfo.getSK3EQ() }));

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

			if (YoloEngine.playerParticipantID.equals(YoloEngine.participants.get(0).getParticipantId())) {				
				// My przydzielamy teamy
				System.out.println("przydzielam team");
				String teamAssignPattern = assignTeams();
				// @TODO czyszczenie listy teamAB, teamA, teamB ?				

				YoloEngine.mMultislayer.sendTeamAssignment(Integer.parseInt(teamAssignPattern, 2));
				YoloEngine.startTime = (System.currentTimeMillis() + YoloEngine.countdownTime + YoloEngine.timeOffset);
				YoloEngine.mMultislayer.sendMaxLife(); // TODO to powinno być później, żeby była pweność, że TeamAB jest dobrze usuzpłenione
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

	// GGGG
	public void signIn() {
		System.out.println("Signing in");
		mSignInprogress = true;
		mHelper.beginUserInitiatedSignIn();
	}

	public void signOut() {
		// całkowicie wylogowuje, żby np. zmienić konto
		System.out.println("Signing out");

		YoloMainMenu.btn_quick.setEnabled(false);
		YoloMainMenu.btn_invite.setEnabled(false);		
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
		leaveRoom();
		mProgressDialog = new ProgressDialog(mActivity);
		mProgressDialog.show();
		mProgressDialog.setMessage("Connecting...");
		mProgressDialog.setCancelable(true);

		Games.RealTimeMultiplayer.create(mHelper.getApiClient(), prepareGame());		
	}

	public void invite() {
		leaveRoom();
		
		// launch the player selection screen
		// minimum: 1 other player; maximum: 3 other players
		Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mHelper.getApiClient(), 1, 3);
		mActivity.startActivityForResult(intent, RC_SELECT_PLAYERS);

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

	public void cosCos(int request, int response, Intent data) {
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
			Bundle extras = data.getExtras();
			final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

			debugLog("invitation sent chyba");
			Toast toast = Toast.makeText(mActivity.getApplicationContext(), R.string.invitation_sent, Toast.LENGTH_SHORT);
			toast.show();

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

		}

	}

	

}
