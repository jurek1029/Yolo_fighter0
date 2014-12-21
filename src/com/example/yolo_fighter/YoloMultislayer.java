package com.example.yolo_fighter;
import java.util.ArrayList;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;

public class YoloMultislayer {

	public float Opponents_x_last[] = { 1000f, 1000f, 1000f, 1000f };
	public float Opponents_y_last[] = { 1000f, 1000f, 1000f, 1000f };
	//new float[4];
	
	public float Opponents_x_lastX[] = new float[4];
	public float Opponents_y_lastX[] = new float[4];

	public float Opponents_x_change[] = new float[4];
	public float Opponents_y_change[] = new float[4];
	
	String mMyId;
	ArrayList<Participant> mParticipants;
	
	private long sentAt;

	private int sentPackageId = 1;
	private int receivedPackageId = 0;
	
	public RealTimeMessageReceivedListener messageReceiver;
	
	
	public YoloMultislayer() {

		YoloEngine.opponents[0] = "";
		YoloEngine.opponents[1] = "";
		YoloEngine.opponents[2] = "";
		YoloEngine.opponents[3] = "";

		messageReceiver = new RealTimeMessageReceivedListener() {

			@Override
			public void onRealTimeMessageReceived(RealTimeMessage message) {

				String[] MessString = new String(message.getMessageData()).split("\\|");
				if (MessString.length == 4) {

					int playerIDd = 0;
					for (int i = 0; i < 4; i++)
						if (YoloEngine.opponents[i].equals(message.getSenderParticipantId())) {
							playerIDd = i;
							break;
						}

					YoloEngine.mMultislayer.positionDataReceived(playerIDd, Float.parseFloat(MessString[0]), Float.parseFloat(MessString[1]), Boolean.parseBoolean(MessString[2]),
							Integer.parseInt(MessString[3]));
				} else if (MessString.length == 8)
					YoloGameRenderer.skillOponentVe.add(new Skill(Float.parseFloat(MessString[0]), Float.parseFloat(MessString[1]), Integer.parseInt(MessString[2]), Integer.parseInt(MessString[3]),
							Float.parseFloat(MessString[4]), Float.parseFloat(MessString[5]), Float.parseFloat(MessString[6]), Float.parseFloat(MessString[7]),Float.parseFloat(MessString[8])));
				else if (MessString.length == 3) {
					YoloEngine.sprite_load[Integer.parseInt(MessString[0])] = true;
					YoloEngine.sprite_load[Integer.parseInt(MessString[1])] = true;
					YoloEngine.sprite_load[Integer.parseInt(MessString[2])] = true;
				}
				else if (MessString.length == 6) {
					YoloGameRenderer.hitBoxs.add(new HitBox(Float.parseFloat(MessString[0]), Float.parseFloat(MessString[1]), Float.parseFloat(MessString[2]), Float.parseFloat(MessString[3]), Float.parseFloat(MessString[4]),Integer.parseInt(MessString[5])));//XXX Doda³em jeszcze jedn¹ zmienn¹
				}
				else {
					YoloGameRenderer.OpponentFire(Float.parseFloat(MessString[0]), Float.parseFloat(MessString[1]), Boolean.parseBoolean(MessString[2]), Boolean.parseBoolean(MessString[3]));
				}
				
				

			}
		};
	}

	/**
	 * Wysy³a dane o pozycji gracza, w odstêpach YoloEngine.UPDATE_FREQ
	 * 
	 * @param x
	 * @param y
	 * @param isCrouch
	 */
	public void SendData(float x, float y, boolean isCrouch) {
		if (System.currentTimeMillis() - sentAt >= YoloEngine.UPDATE_FREQ) {

			sentAt = System.currentTimeMillis();
			
			sendMessageToAll((x + "|" + y + "|" + isCrouch + "|" + sentPackageId).toString().getBytes());

		}
	}


	/**
	 * Wyznacza nowe przemieszczenia dla postaci przeciwnika
	 * 
	 * @param playerID	kogo dotycz¹ informacje
	 * @param x
	 * @param y
	 * @param isCrouch
	 * @param packageId
	 */
	public void positionDataReceived(final int playerID, final float x, final float y, final boolean isCrouch, int packageId) {
/*
		if (packageId < receivedPackageId) {
			System.out.println("old data");
			return;
		} else
			receivedPackageId = packageId; // NIE DZIA£A, mo¿e powinno?? (nie zwiêksza sie zmienna) XXX dla > 2 graczy trzeba zrobiæ array, Licznik oparty na INT, kiedyœ
											// skoñczy siê zakres 
*/
		YoloEngine.Opponent_isCrouched[playerID] = isCrouch;
		Opponents_x_change[playerID] = ((x - Opponents_x_last[playerID]) / (float) YoloEngine.MULTI_STEPS);
		Opponents_y_change[playerID] = ((y - Opponents_y_last[playerID]) / (float) YoloEngine.MULTI_STEPS);

		YoloEngine.changesMade[playerID] = 0;

		Opponents_x_last[playerID] = x;
		Opponents_y_last[playerID] = y;

	}
	
	
	/**
	 * Wysy³a dane do wszystkich
	 * metoda reliable
	 * 
	 * @param data	Bytes array
	 */
	public void sendMessageToAllreliable(byte[] data)
	{
		if(!YoloEngine.MULTI_ACTIVE)
			return;
		
		mMyId = YoloEngine.mRoom.getParticipantId(Games.Players.getCurrentPlayerId(YoloEngine.mHelper.getApiClient()));

		mParticipants = YoloEngine.mRoom.getParticipants();	
		
		for (Participant p : mParticipants) {
			if (p.getParticipantId().equals(mMyId))
				continue;
			if (p.getStatus() != Participant.STATUS_JOINED)
				continue;
			else {
				Games.RealTimeMultiplayer.sendReliableMessage(YoloEngine.mHelper.getApiClient(), null,  data, YoloEngine.mRoom.getRoomId().toString(), p.getParticipantId());

			}
		}

		
	}
	
	/**
	 * Wysy³a dane do wszystkich
	 * metoda unreliable
	 * 
	 * @param data	Bytes array
	 */
	public void sendMessageToAll(byte[] data) {
		mMyId = YoloEngine.mRoom.getParticipantId(Games.Players.getCurrentPlayerId(YoloEngine.mHelper.getApiClient()));

		mParticipants = YoloEngine.mRoom.getParticipants();	
		
		for (Participant p : mParticipants) {
			if (p.getParticipantId().equals(mMyId))
				continue;
			if (p.getStatus() != Participant.STATUS_JOINED)
				continue;
			else {
				Games.RealTimeMultiplayer.sendUnreliableMessage(YoloEngine.mHelper.getApiClient(), data, YoloEngine.mRoom.getRoomId().toString(), p.getParticipantId());

			}
		}
	}
	
	
	public byte[] generateMessageFromFloats(Float values[]) {

		String output = "";
		for(float value : values)
		{
			output += "|"+value;
		}
				
		return output.getBytes();
	}





}

