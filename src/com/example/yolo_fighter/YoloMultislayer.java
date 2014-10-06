package com.example.yolo_fighter;

import java.util.ArrayList;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;

public class YoloMultislayer {

	public float Opponents_x_last[] = new float[4];
	public float Opponents_y_last[] = new float[4];
	
	public float Opponents_x_lastX[] = new float[4];
	public float Opponents_y_lastX[] = new float[4];

	public float Opponents_x_change[] = new float[4];
	public float Opponents_y_change[] = new float[4];
	String mMyId;
	ArrayList<Participant> mParticipants;
	
	private long sentAt;

	private int sentPackageId = 1;
	private int receivedPackageId = 0;

	public void SendData(float x, float y, boolean isCrouch) {
		if (System.currentTimeMillis() - sentAt >= YoloEngine.UPDATE_FREQ) {
			// System.out.println(System.currentTimeMillis() - sentAt);
			sentAt = System.currentTimeMillis();
			
			sendMessageToAll((x + "|" + y + "|" + isCrouch + "|" + sentPackageId).toString().getBytes());

		}
	}


	public void DataReceived(final int playerID, final float x, final float y, final boolean isCrouch, int packageId) {

		if (packageId < receivedPackageId) {
			System.out.println("old data");
			return;
		} else
			receivedPackageId = packageId; // NIE DZIA£A, mo¿e powinno?? (nie zwiêksza sie zmienna) XXX dla > 2 graczy trzeba zrobiæ array, Licznik oparty na INT, kiedyœ
											// skoñczy siê zakres 

		YoloEngine.Opponent_isCrouched[playerID] = isCrouch;
		Opponents_x_change[playerID] = ((x - Opponents_x_last[playerID]) / (float) YoloEngine.MULTI_STEPS);
		Opponents_y_change[playerID] = ((y - Opponents_y_last[playerID]) / (float) YoloEngine.MULTI_STEPS);

		YoloEngine.changesMade[playerID] = 0;

		Opponents_x_last[playerID] = x;
		Opponents_y_last[playerID] = y;

	}
	
	
	public void sendMessageToAllreliable(byte[] data)
	{
		mMyId = YoloEngine.cRoom.getParticipantId(Games.Players.getCurrentPlayerId(YoloEngine.mHelper.getApiClient()));

		mParticipants = YoloEngine.cRoom.getParticipants();	
		
		for (Participant p : mParticipants) {
			if (p.getParticipantId().equals(mMyId))
				continue;
			if (p.getStatus() != Participant.STATUS_JOINED)
				continue;
			else {
				Games.RealTimeMultiplayer.sendReliableMessage(YoloEngine.mHelper.getApiClient(), null,  data, YoloEngine.cRoom.getRoomId().toString(), p.getParticipantId());

			}
		}

		
	}
	
	public void sendMessageToAll(byte[] data) {
		mMyId = YoloEngine.cRoom.getParticipantId(Games.Players.getCurrentPlayerId(YoloEngine.mHelper.getApiClient()));

		mParticipants = YoloEngine.cRoom.getParticipants();	
		
		for (Participant p : mParticipants) {
			if (p.getParticipantId().equals(mMyId))
				continue;
			if (p.getStatus() != Participant.STATUS_JOINED)
				continue;
			else {
				Games.RealTimeMultiplayer.sendUnreliableMessage(YoloEngine.mHelper.getApiClient(), data, YoloEngine.cRoom.getRoomId().toString(), p.getParticipantId());

			}
		}

	}
}
