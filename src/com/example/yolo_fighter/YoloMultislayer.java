package com.example.yolo_fighter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;

/* TODO
  - Test - life max czy wysyła się 
  - Pozycje w osobnych
  - dodawanie skilli mojego teamu powinny byś dodawane do skillplayerve a przeciwnego do Opp
 */


public class YoloMultislayer {

	public float Opponents_x_last[] = { 1000f, 1000f, 1000f, 1000f };
	public float Opponents_y_last[] = { 1000f, 1000f, 1000f, 1000f };
	//new float[4];
	
	public float Opponents_x_lastX[] = { 1000f, 1000f, 1000f, 1000f };
	public float Opponents_y_lastX[] = { 1000f, 1000f, 1000f, 1000f };

	public float Opponents_x_change[] = new float[4];
	public float Opponents_y_change[] = new float[4];

	ArrayList<String> TeamAB_Participants;
	ArrayList<Participant> mParticipants;
	
	private long sentAt;

	private int sentPackageId = 1;
	private int receivedPackageId = 0;
	
	public RealTimeMessageReceivedListener messageReceiver;
	
	
	public YoloMultislayer() {


		messageReceiver = new RealTimeMessageReceivedListener() {

			@Override
			public void onRealTimeMessageReceived(RealTimeMessage message) {
				
				ByteBuffer rcvData = ByteBuffer.wrap(message.getMessageData());
				char messageCode = rcvData.getChar();

				switch (messageCode) {
				case 'p':
					YoloEngine.mMultislayer.positionDataReceived(rcvData.getInt(), rcvData.getFloat(), rcvData.getFloat(), rcvData.get() == 1 ? true : false, rcvData.getFloat(), 0);					
					break;

				case 'l':
					int spriteLoad;
					for(int i = 0; i < 3; i++) {
						spriteLoad = rcvData.getInt();
						YoloEngine.sprite_load[spriteLoad<45?spriteLoad : spriteLoad-87] = true;
					}
					break;

                case 't':
                    String pattern = Integer.toBinaryString(rcvData.getInt()).substring(1);

                    int i=0,a=0,b=YoloEngine.TeamSize;
                    for(Participant p : YoloEngine.participants) {
                        if(p.getStatus() == Participant.STATUS_JOINED) {
                        	
                            if (pattern.charAt(i) == '0') {                            	
                                YoloEngine.teamA.add(p.getParticipantId()); //@REMOVE
                                YoloEngine.TeamAB[a].playerTeam = YoloEngine.TeamA;
                                YoloEngine.TeamAB[a].ParticipantId = p.getParticipantId();
                                if(p.getParticipantId().equals(YoloEngine.playerParticipantID))
                                	YoloEngine.MyID = a;
                                a++;
                            }
                            if (pattern.charAt(i) == '1') {								
								YoloEngine.teamB.add(p.getParticipantId()); //@REMOVE
								YoloEngine.TeamAB[b].playerTeam = YoloEngine.TeamB;
								YoloEngine.TeamAB[b].ParticipantId = p.getParticipantId();	
								if(p.getParticipantId().equals(YoloEngine.playerParticipantID)) 
									YoloEngine.MyID = b;
								b++;
							}
                            i++;
                        }
                    }
                                                          
                    prepareMatchArray();
                               
                    YoloEngine.mMultislayer.sendMaxLife();
                  //  YoloGameRenderer.givePlayerID();
                    break;

				case 'f':
					YoloGameRenderer.OpponentFire(rcvData.getFloat(), rcvData.getFloat(), rcvData.get() == 1 ? true : false, rcvData.get() == 1 ? true : false, rcvData.getInt(), rcvData.getInt(), rcvData.getFloat(), rcvData.get() == 1 ? true : false);
					break;

                case 'g':
                    YoloGameRenderer.AIFire(rcvData.getFloat(), rcvData.getFloat(), rcvData.get() == 1 ? true : false, rcvData.getInt(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.get() == 1 ? true : false);
                    break;

				case 'h':
					YoloGameRenderer.hitBoxs.add(new HitBox(rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getInt(), rcvData.get() == 1 ? true : false, rcvData.get() == 1 ? true : false, rcvData.get() == 1 ? true : false, rcvData.getInt()));
					break;

				case 's':
					/* W imieniu towarzysza Iosif Wissarionowicz Dzugaszwili Stalina anektujemy ten teren ku lepszemu Zwiaskowi Socjalistycznemu Republik Radzieckich 
					YoloGameRenderer.skillOponentVe.add(new Skill(new SkillData(rcvData.getFloat(), rcvData.getFloat(), rcvData.getInt(), rcvData.getInt(), rcvData.getFloat(), rcvData.getFloat(), rcvData
							.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.get() == 1 ? true : false,rcvData.getInt())));
							*/
					break;
                case 'i':
                    YoloEngine.IDTracer = rcvData.getInt();
                    break;
                case 'm':
                	int ind = rcvData.getInt();
                    YoloEngine.TeamAB[ind].PLAYER_LIVE_MAX = rcvData.getFloat();
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
	public void positionDataReceived(final int playerID, final float x, final float y, final boolean isCrouch, float life, int packageId) {
/*
		if (packageId < receivedPackageId) {
			System.out.println("old data");
			return;
		} else
			receivedPackageId = packageId; // NIE DZIA�A, mo�e powinno?? (nie zwi�ksza sie zmienna) XXX dla > 2 graczy trzeba zrobi� array, Licznik oparty na INT, kiedy�
											// sko�czy si� zakres 
*/
		YoloEngine.TeamAB[playerID].isCrouch = isCrouch;
		Opponents_x_change[playerID] = ((x - Opponents_x_last[playerID]) / (float) YoloEngine.MULTI_STEPS);
		Opponents_y_change[playerID] = ((y - Opponents_y_last[playerID]) / (float) YoloEngine.MULTI_STEPS);

		YoloEngine.changesMade[playerID] = 0;

		Opponents_x_last[playerID] = x;
		Opponents_y_last[playerID] = y;

        YoloEngine.TeamAB[playerID].PlayerLive = life;

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

		mParticipants = YoloEngine.mRoom.getParticipants();

		
		for (Participant p : mParticipants) {
			if (p.getParticipantId().equals(YoloEngine.playerParticipantID))
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

            ByteBuffer bbf = ByteBuffer.allocate(20);
            bbf.putChar('p');
            bbf.putInt(YoloEngine.MyID);
            bbf.putFloat(x);
            bbf.putFloat(y);
            if(isCrouched)
                bbf.put((byte)1);
            else
                bbf.put((byte)0);
            bbf.putFloat(YoloEngine.TeamAB[YoloEngine.MyID].PlayerLive);

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
	
	public void sendOpponentFire(float x, float y, boolean isLeft, boolean isCrouch, int sprite,int count, float damage, boolean team)
	{
		ByteBuffer bbf = ByteBuffer.allocate(30);
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
        bbf.putInt(sprite);
        bbf.putInt(count);
        bbf.putFloat(damage);
        if(team)
            bbf.put((byte)1);
        else
            bbf.put((byte)0);

        sendMessageToAll(bbf.array());
	}

    public void sendAIFire(float x, float y, boolean isLeft, float x_texture, float y_texture, float damage, boolean team)
    {
        ByteBuffer bbf = ByteBuffer.allocate(20);
        bbf.putChar('g');
        bbf.putFloat(x);
        bbf.putFloat(y);
        if(isLeft)
            bbf.put((byte)1);
        else
            bbf.put((byte)0);
        bbf.putFloat(x_texture);
        bbf.putFloat(y_texture);

        sendMessageToAll(bbf.array());
    }


    public void sendHitBox(float x, float y, float x_radius, float y_radius, float damage, float duration, int sprite, boolean isLeft, boolean efectOnMySkill, int ID)
    {
    	// team też jest przesyłany, tylko nie z arg a z YoloEngine
    	ByteBuffer bbf = ByteBuffer.allocate(42);
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
        if(YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
            bbf.put((byte)1);
        else
            bbf.put((byte)0);
        if(efectOnMySkill)
            bbf.put((byte)1);
        else
            bbf.put((byte)0);
        bbf.putInt(ID);

        sendMessageToAllreliable(bbf.array());
    }


    public void sendTeamAssignment(int assignPattern) {
        ByteBuffer bbf = ByteBuffer.allocate(40);
        bbf.putChar('t');
        bbf.putInt(assignPattern);

        sendMessageToAllreliable(bbf.array());
    }


    public void sendTracerIncrease(int idTracer) {
        ByteBuffer bbf = ByteBuffer.allocate(40);
        bbf.putChar('i');
        bbf.putInt(idTracer);

        sendMessageToAllreliable(bbf.array());

    }
    
    public void sendMaxLife() {
    	ByteBuffer bbf = ByteBuffer.allocate(12);
    	bbf.putChar('m');
    	bbf.putInt(YoloEngine.MyID);
    	bbf.putFloat(YoloEngine.TeamAB[YoloEngine.MyID].PLAYER_LIVE_MAX);
    	
    	sendMessageToAllreliable(bbf.array());
    }
    
    public void prepareMatchArray() {
    //	for(YoloPlayer p : YoloEngine.TeamAB)
    //		TeamAB_Participants.add(p.ParticipantId);
    }
}

