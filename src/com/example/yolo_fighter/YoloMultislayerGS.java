package com.example.yolo_fighter;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.example.yolo_fighter.YoloEngine;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;

public class YoloMultislayerGS extends YoloMultislayerBase implements RealTimeMessageReceivedListener {
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

		ArrayList<Participant> mParticipants = YoloEngine.mRoom.getParticipants();

		for (Participant p : mParticipants) {
			if (p.getParticipantId().equals(YoloEngine.playerParticipantID))
				continue;
			if (p.getStatus() != Participant.STATUS_JOINED)
				continue;
			else {
				Games.RealTimeMultiplayer.sendReliableMessage(YoloEngine.mHelper.getApiClient(), null, data, YoloEngine.mRoom.getRoomId().toString(), p.getParticipantId());

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
		
		Games.RealTimeMultiplayer.sendUnreliableMessageToOthers(YoloEngine.mHelper.getApiClient(), data, YoloEngine.mRoom.getRoomId().toString());
	}

}
