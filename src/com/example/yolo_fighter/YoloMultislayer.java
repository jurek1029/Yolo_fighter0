package com.example.yolo_fighter;

import java.util.ArrayList;

import com.google.android.gms.common.data.f;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;

public class YoloMultislayer {

	public float Opponents_x_last[] = new float[4];
	public float Opponents_y_last[] = new float[4];
	
	public float Opponents_x_lastX[] = new float[4];
	public float Opponents_y_lastX[] = new float[4];


	public float Opponents_x_change[] = new float[4];
	public float Opponents_y_change[] = new float[4];

	private Boolean newPackage;
	private long sentAt;
	private long receivedAt;
	private int sentPackageId = 1;
	private int receivedPackageId = 0;

	public void SendData(float x, float y, boolean isCrouch) {
		if (System.currentTimeMillis() - sentAt >= YoloEngine.UPDATE_FREQ) {
			//System.out.println(System.currentTimeMillis() - sentAt);
			sentAt = System.currentTimeMillis();
			
			sendMessageToAll((x+"|"+y+"|"+isCrouch+"|"+sentPackageId).toString().getBytes());
			
		}
	}

	private void updateData(int playerID, float x, float y, boolean isCrouch) {

		
		YoloEngine.Opponent_isCrouched[playerID] = isCrouch;
		Opponents_x_change[playerID] = ((x - Opponents_x_last[playerID]) / (float) YoloEngine.MULTI_STEPS); // de facto trzerba sprawdziæ ile razy odpalany jest DrawOpponnent i jakoœ to powi¹zaæ
		Opponents_y_change[playerID] = ((y - Opponents_y_last[playerID]) / (float) YoloEngine.MULTI_STEPS);

		YoloEngine.changesMade = 0;
		
		
		Opponents_x_last[playerID] = x;
		Opponents_y_last[playerID] = y;

		receivedAt = System.currentTimeMillis();
		newPackage = false;
		
		
	}

	public void DataReceived(final int playerID, final float x, final float y, final boolean isCrouch, int packageId) {
		newPackage = true;
		//System.out.println(System.currentTimeMillis() - receivedAt);
		if(packageId < receivedPackageId) {System.out.println("old data"); return; }
		else
			receivedPackageId = packageId;
		
		
		//if (System.currentTimeMillis() - receivedAt >= YoloEngine.UPDATE_FREQ) {
			updateData(playerID, x, y, isCrouch);
		/*	
		} else { // nie lubimy zbyt czêstych updateów
			new Thread(new Runnable() {

				@Override
				public void run() {

					while (true) {
						if (newPackage) {
							System.out.println("breaking!"
									+ System.currentTimeMillis());
							newPackage = false;
							
							break;
						}

						if (System.currentTimeMillis() - receivedAt >= YoloEngine.UPDATE_FREQ) {
							updateData(playerID, x, y, isCrouch);
							break;
						}
						try {
							System.out.println("halt in thread");
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}).start();
			
		} */
	}
	
	
	public void sendMessageToAll(byte[] data) {
		String mMyId = YoloEngine.cRoom.getParticipantId(Games.Players.getCurrentPlayerId(YoloEngine.mHelper.getApiClient()));
	//	System.out.println(mMyId);

		ArrayList<Participant> mParticipants = YoloEngine.cRoom.getParticipants();
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
