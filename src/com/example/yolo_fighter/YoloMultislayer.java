package com.example.yolo_fighter;

import com.google.android.gms.common.data.f;
import com.google.android.gms.games.Games;

public class YoloMultislayer {

	public float Opponents_x_last[] = new float[4];
	public float Opponents_y_last[] = new float[4];
	



	public float Opponents_x_change[] = new float[4];
	public float Opponents_y_change[] = new float[4];

	private Boolean newPackage;
	private long sentAt;
	private long receivedAt;
	private boolean firstrr = true;

	public void SendData(float x, float y, boolean isCrouch) {
		if (System.currentTimeMillis() - sentAt >= YoloEngine.UPDATE_FREQ) {
			// System.out.println("x: "+x+" y: "+y);
			sentAt = System.currentTimeMillis();
			
			sendMessageToAll((x+"|"+y+"|"+isCrouch).toString().getBytes());
			
		}
	}

	private void updateData(int playerID, float x, float y, boolean isCrouch) {

		YoloEngine.Opponent_isCrouched[playerID] = isCrouch;
		Opponents_x_change[playerID] = ((x - Opponents_x_last[playerID]) / (float) 10); // de facto trzerba sprawdziæ ile razy odpalany jest DrawOpponnent i jakoœ to powi¹zaæ
		Opponents_y_change[playerID] = ((y - Opponents_y_last[playerID]) / (float) 10);

		YoloEngine.changesMade = 0;
		
		
		Opponents_x_last[playerID] = x;
		Opponents_y_last[playerID] = y;
		
		if(firstrr){
			/*
			YoloEngine.Opponents_x[0] = 3f;
			YoloEngine.Opponents_y[0] = 5f;
			Opponents_x_last[0] = 3f;
			Opponents_y_last[0] = 5f;
			Opponents_x_change[playerID] = 0;
			Opponents_y_change[playerID] = 0;
			*/
			firstrr = false;
			
		}

		receivedAt = System.currentTimeMillis();
		newPackage = false;
		
	}

	public void DataReceived(final int playerID, final float x, final float y, final boolean isCrouch) {
		newPackage = true;
		if (System.currentTimeMillis() - receivedAt >= YoloEngine.UPDATE_FREQ) {
			updateData(playerID, x, y, isCrouch);
		} else { // nie lubimy zbyt czêstych updateów
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
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
		}
	}
	
	
	public void sendMessageToAll(byte[] data) {
		for (int i = 0; i < YoloEngine.cRoom.getParticipants().size(); i++) {
			if (YoloEngine.cRoom.getParticipantIds().get(i) != Games.Players.getCurrentPlayerId(YoloEngine.mHelper.getApiClient())) {
			//	Games.RealTimeMultiplayer.sendReliableMessage(YoloEngine.mHelper.getApiClient(), null, data, YoloEngine.cRoom.getRoomId().toString(), YoloEngine.cRoom.getParticipantIds().get(i));
				Games.RealTimeMultiplayer.sendUnreliableMessage(YoloEngine.mHelper.getApiClient(), data, YoloEngine.cRoom.getRoomId().toString(), YoloEngine.cRoom.getParticipantIds().get(i));
			}
		}

	}
}
