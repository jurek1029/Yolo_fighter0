package com.example.yolo_fighter;
import java.nio.ByteBuffer;
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
				
				ByteBuffer rcvData = ByteBuffer.wrap(message.getMessageData());
				char messageCode = rcvData.getChar();
				
				switch (messageCode) {
				case 'p':
					int playerIDd = 0;
					for (int i = 0; i < 4; i++)
						if (YoloEngine.opponents[i].equals(message.getSenderParticipantId())) {
							playerIDd = i;
							break;
						}

					YoloEngine.mMultislayer.positionDataReceived(playerIDd, rcvData.getFloat(), rcvData.getFloat(), rcvData.get() == 1 ? true : false, 0);
					break;

				case 'l':
					YoloEngine.sprite_load[rcvData.getInt()] = true;
					YoloEngine.sprite_load[rcvData.getInt()] = true;
					YoloEngine.sprite_load[rcvData.getInt()] = true;
					break;

				case 'f':
			//		YoloGameRenderer.OpponentFire(rcvData.getFloat(), rcvData.getFloat(), rcvData.get() == 1 ? true : false, rcvData.get() == 1 ? true : false);
					break;
                case 'd':
                    //@TODO damage (dodać na końcu rcvData.getFloat())
             //       YoloGameRenderer.OpponentFire(rcvData.getFloat(), rcvData.getFloat(), rcvData.get() == 1 ? true : false, rcvData.get() == 1 ? true : false);
                    break;

				case 'h':
					YoloGameRenderer.hitBoxs.add(new HitBox(rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getInt(), rcvData.get() == 1 ? true : false));
					break;

				case 's':
					YoloGameRenderer.skillOponentVe.add(new Skill(rcvData.getFloat(), rcvData.getFloat(), rcvData.getInt(), rcvData.getInt(), rcvData.getFloat(), rcvData.getFloat(), rcvData
							.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat()));
					break;

				default:
					System.out.println("message not recognized");
					break;
				}
			}
		};
	}




	/**
	 * Wyznacza nowe przemieszczenia dla postaci przeciwnika
	 * 
	 * @param playerID	kogo dotyczą informacje
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
			receivedPackageId = packageId; // NIE DZIA�A, mo�e powinno?? (nie zwi�ksza sie zmienna) XXX dla > 2 graczy trzeba zrobi� array, Licznik oparty na INT, kiedy�
											// sko�czy si� zakres 
*/
		YoloEngine.Opponent_isCrouched[playerID] = isCrouch;
		Opponents_x_change[playerID] = ((x - Opponents_x_last[playerID]) / (float) YoloEngine.MULTI_STEPS);
		Opponents_y_change[playerID] = ((y - Opponents_y_last[playerID]) / (float) YoloEngine.MULTI_STEPS);

		YoloEngine.changesMade[playerID] = 0;

		Opponents_x_last[playerID] = x;
		Opponents_y_last[playerID] = y;

	}
	
	
	/**
	 * Wysyła dane do wszystkich
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
	 * Wysyła dane do wszystkich
	 * metoda unreliable
	 * 
	 * @param data	Bytes array
	 */
	public void sendMessageToAll(byte[] data) {
        /*
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
		*/
        Games.RealTimeMultiplayer.sendUnreliableMessageToOthers(YoloEngine.mHelper.getApiClient(), data,YoloEngine.mRoom.getRoomId().toString());
	}

    /**
     * Wysyła dane o pozycji gracza, w odstępach YoloEngine.UPDATE_FREQ
     *
     * @param x
     * @param y
     * @param isCrouched
     */
	public void sendPlayerPosition(float x, float y, boolean isCrouched) {
		// int 4, float 4, char 2 --> bajty
        if (System.currentTimeMillis() - sentAt >= YoloEngine.UPDATE_FREQ) {
            sentAt = System.currentTimeMillis();

            ByteBuffer bbf = ByteBuffer.allocate(12);
            bbf.putChar('p');
            bbf.putFloat(x);
            bbf.putFloat(y);
            if(isCrouched)
                bbf.put((byte)1);
            else
                bbf.put((byte)0);

            sendMessageToAll(bbf.array());
        }
		
	}
	
	public byte[] sendSpriteLoad(int[] loadArray)
	{
		ByteBuffer bbf = ByteBuffer.allocate(2 + 4 * loadArray.length);
		bbf.putChar('l');
		for(int value : loadArray)
			bbf.putInt(value);
		
		return bbf.array();
	}
	
	public void sendOpponentFire(float x, float y, boolean isLeft, boolean isCrouch)
	{
		ByteBuffer bbf = ByteBuffer.allocate(20);
		bbf.putChar('f');
		bbf.putFloat(x);
		bbf.putFloat(y);
		if(isLeft)
			bbf.put((byte)1);
		else
			bbf.put((byte)0);
		if(isCrouch)
			bbf.put((byte)1);
		else
			bbf.put((byte)0);

        sendMessageToAll(bbf.array());
	}

    public void sendOpponentFire(float x, float y, boolean isLeft, boolean isCrouch, float damage)
    {
        // z damage
        ByteBuffer bbf = ByteBuffer.allocate(25);
        bbf.putChar('d');
        bbf.putFloat(x);
        bbf.putFloat(y);
        if(isLeft)
            bbf.put((byte)1);
        else
            bbf.put((byte)0);
        if(isCrouch)
            bbf.put((byte)1);
        else
            bbf.put((byte)0);
        bbf.putFloat(damage);

        sendMessageToAll(bbf.array());
    }

    public void sendAIFire(float x, float y, boolean isLeft)
    {
        ByteBuffer bbf = ByteBuffer.allocate(20);
        bbf.putChar('f');
        bbf.putFloat(x);
        bbf.putFloat(y);
        if(isLeft)
            bbf.put((byte)1);
        else
            bbf.put((byte)0);
        sendMessageToAll(bbf.array());
    }


    public void sendHitBox(float x, float y, float x_radius, float y_radius, float damage, float duration, int sprite, boolean isLeft)
    {
        ByteBuffer bbf = ByteBuffer.allocate(40);
        bbf.putChar('h');
        bbf.putFloat(x);
        bbf.putFloat(y);
        bbf.putFloat(x_radius);
        bbf.putFloat(y_radius);
        bbf.putFloat(damage);
        bbf.putFloat(duration);
        bbf.putInt(sprite);
        if(isLeft)
            bbf.put((byte)1);
        else
            bbf.put((byte)0);

        sendMessageToAllreliable(bbf.array());
    }
	






}

