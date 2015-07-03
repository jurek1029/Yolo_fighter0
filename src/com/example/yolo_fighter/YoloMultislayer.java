package com.example.yolo_fighter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;

/* TODO
  - Test - life max czy wysyÅ‚a siÄ™ 
	
  
  
  
  • bardziej racjonalne wysy³anie rzeczy typu max life - moment wysy³ania
  • sprawdziæ dla wiêcej graczy
  
  
 */


public class YoloMultislayer {

	
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
						if(spriteLoad==14)YoloEngine.sprite_load[27]=true;
						if(spriteLoad==36)YoloEngine.sprite_load[32]=true;
						if(spriteLoad==37)YoloEngine.sprite_load[32]=true;
						if(spriteLoad==38)YoloEngine.sprite_load[32]=true;
						if(spriteLoad==43)YoloEngine.sprite_load[41]=true;
						if(spriteLoad==120)YoloEngine.sprite_load[32]=true;
						if(spriteLoad==121)YoloEngine.sprite_load[32]=true;
						if(spriteLoad==122)YoloEngine.sprite_load[32]=true;
						if(spriteLoad==123)YoloEngine.sprite_load[32]=true;
						if(spriteLoad==124)YoloEngine.sprite_load[32]=true;
					}
					System.out.println("otrzymane dane spriteload");
					break;

                case 't':
                    String pattern = Integer.toBinaryString(rcvData.getInt()).substring(1);

                    int i=0,a=0,b=YoloEngine.TeamSize;
                    for(Participant p : YoloEngine.participants) {
                        if(p.getStatus() == Participant.STATUS_JOINED) {
                        	
                            if (pattern.charAt(i) == '0') {                            	
                                YoloEngine.TeamAB[a].playerTeam = YoloEngine.TeamA;
                                YoloEngine.TeamAB[a].ParticipantId = p.getParticipantId();
                                if(p.getParticipantId().equals(YoloEngine.playerParticipantID))
                                	YoloEngine.MyID = a;
                                a++;
                            }
                            if (pattern.charAt(i) == '1') {								
								YoloEngine.TeamAB[b].playerTeam = YoloEngine.TeamB;
								YoloEngine.TeamAB[b].ParticipantId = p.getParticipantId();	
								if(p.getParticipantId().equals(YoloEngine.playerParticipantID)) 
									YoloEngine.MyID = b;
								b++;
							}
                            i++;
                        }
                    }
                                                                                   
                    YoloEngine.mMultislayer.sendMaxLife();
                    YoloEngine.startTime = System.currentTimeMillis()+YoloEngine.countdownTime;
                  //  YoloGameRenderer.givePlayerID();
                    break;

				case 'f':
					YoloGameRenderer.OpponentFire(rcvData.getFloat(), rcvData.getFloat(), rcvData.get() == 1 ? true : false, rcvData.get() == 1 ? true : false, rcvData.getInt(), rcvData.getInt(), rcvData.getFloat(), rcvData.get() == 1 ? true : false);
					break;

                case 'g':
                    YoloGameRenderer.AIFire(rcvData.getFloat(), rcvData.getFloat(), rcvData.get() == 1 ? true : false, rcvData.getInt(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.get() == 1 ? true : false);
                    break;
                    
                case 'b':
                	int id = rcvData.getInt();
                	int boolId = rcvData.getInt();
                	switch (boolId) {
					case 17:
						YoloEngine.TeamAB[id].isBeingHealed = true;
						break;
					case 23:
						YoloEngine.TeamAB[id].isPlayerFlying = true;
						break;
					case 24:
						YoloEngine.TeamAB[id].isPlayerDef = true;
						break;
					case 25:
						YoloEngine.TeamAB[id].isPlayerInvincible = true;
						break;
					case 28:
						YoloEngine.TeamAB[id].isPlayerDenialed = true;
						break;
					case 29:
						YoloEngine.TeamAB[id].isHealing = true;
						break;
					case 36:
						YoloEngine.TeamAB[id].isPlayerBuff = true;
						break;
					case 37:
						YoloEngine.TeamAB[id].isPlayerFireRateBuff = true;
						break;
					case 38:
						YoloEngine.TeamAB[id].isPlayerMagReloadBuff = true;
						break;						
					default:
						System.out.println("Unrecognized bool id");
						break;
					}                
                	/*
                	17 isBeingHealed 
                	23	isPlayerFlying
                	24	isPlayerDef
                	25 isPlayerInvincible
                	28 isPlayerDenialed
                	29 isHealing
                	36 isPlayerBuff
                	37 isPlayerFireRateBuff
                	38 isPlayerMagReloadBuff
                	*/
                	break;
                    
				case 'h':
					YoloGameRenderer.hitBoxs.add(new HitBox(rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getInt(), rcvData.get() == 1 ? true : false, rcvData.get() == 1 ? true : false, rcvData.get() == 1 ? true : false, rcvData.getInt()));
					break;

				case 's':
					/* W imieniu towarzysza Iosif Wissarionowicz Dzugaszwili Stalina anektujemy ten teren ku lepszemu Zwiaskowi Socjalistycznemu Republik Radzieckich 
					YoloGameRenderer.skillOponentVe.add(new Skill(new SkillData(rcvData.getFloat(), rcvData.getFloat(), rcvData.getInt(), rcvData.getInt(), rcvData.getFloat(), rcvData.getFloat(), rcvData
							.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.get() == 1 ? true : false,rcvData.getInt())));
							*/
					Skill tSkill = new Skill(rcvData.getFloat(), rcvData.getFloat(), rcvData.getInt(), rcvData.get() == 1 ? true : false, rcvData.getInt());
					if(tSkill.team == YoloEngine.TeamA)
						YoloGameRenderer.skillTeamAVe.add(tSkill);
					else
						YoloGameRenderer.skillTeamBVe.add(tSkill);
					
					break;
                case 'i':
                    YoloEngine.IDTracer = rcvData.getInt();
                    break;
                case 'm':
                	int ind = rcvData.getInt();
                    YoloEngine.TeamAB[ind].PLAYER_LIVE_MAX = rcvData.getFloat();
                    break;
                case 'q':
                	int k = rcvData.getInt();
                    YoloEngine.TeamAB[k].moveAway();
                    YoloEngine.opponents.remove(YoloEngine.TeamAB[k].ParticipantId);
                    System.out.println("Someone has just left");
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
	 * @param playerID	kogo dotyczÄ… informacje
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
			receivedPackageId = packageId; // NIE DZIAï¿½A, moï¿½e powinno?? (nie zwiï¿½ksza sie zmienna) XXX dla > 2 graczy trzeba zrobiï¿½ array, Licznik oparty na INT, kiedyï¿½
											// skoï¿½czy siï¿½ zakres 
*/
		YoloEngine.TeamAB[playerID].isCrouch = isCrouch;

		YoloEngine.TeamAB[playerID].x_change = ((x - YoloEngine.TeamAB[playerID].x_last) / (float) YoloEngine.MULTI_STEPS);
		YoloEngine.TeamAB[playerID].y_change = ((y - YoloEngine.TeamAB[playerID].y_last) / (float) YoloEngine.MULTI_STEPS);

		YoloEngine.TeamAB[playerID].changesMade = 0;

		YoloEngine.TeamAB[playerID].x_last = x;
		YoloEngine.TeamAB[playerID].y_last = y;

        YoloEngine.TeamAB[playerID].PlayerLive = life;

	}
	
	
	/**
	 * WysyÅ‚a dane do wszystkich
	 * metoda reliable
	 * 
	 * @param data	Bytes array
	 */
	public void sendMessageToAllreliable(byte[] data)
	{
		if(!YoloEngine.MULTI_ACTIVE)
			return;

		ArrayList<Participant> mParticipants = YoloEngine.mRoom.getParticipants();

		
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
	 * WysyÅ‚a dane do wszystkich
	 * metoda unreliable
	 * 
	 * @param data	Bytes array
	 */
	public void sendMessageToAll(byte[] data) {
        Games.RealTimeMultiplayer.sendUnreliableMessageToOthers(YoloEngine.mHelper.getApiClient(), data,YoloEngine.mRoom.getRoomId().toString());
	}

    /**
     * WysyÅ‚a dane o pozycji gracza, w odstÄ™pach YoloEngine.UPDATE_FREQ
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

    public void sendAIFire(float x, float y, boolean isLeft, int sprite, float x_texture, float y_texture, float damage, boolean team)
    {
        ByteBuffer bbf = ByteBuffer.allocate(30);
        bbf.putChar('g');
        bbf.putFloat(x);
        bbf.putFloat(y);
        if(isLeft)
            bbf.put((byte)1);
        else
            bbf.put((byte)0);
        bbf.putInt(sprite);
        bbf.putFloat(x_texture);
        bbf.putFloat(y_texture);
        bbf.putFloat(damage);
        if(team)
            bbf.put((byte)1);
        else
            bbf.put((byte)0);

        sendMessageToAll(bbf.array());
    }


    public void sendHitBox(float x, float y, float x_radius, float y_radius, float damage, float duration, int sprite, boolean isLeft, boolean efectOnMySkill, int ID)
    {
    	// team teÅ¼ jest przesyÅ‚any, tylko nie z arg a z YoloEngine
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
    
    public void SendSkillBool(int boolId) {
    	/*
    	17 isBeingHealed 
    	23	isPlayerFlying
    	24	isPlayerDef
    	25 isPlayerInvincible
    	28 isPlayerDenialed
    	29 isHealing
    	36 isPlayerBuff
    	37 isPlayerFireRateBuff
    	38 isPlayerMagReloadBuff
    	*/
    	ByteBuffer bbf = ByteBuffer.allocate(16);
    	bbf.putChar('b');
    	bbf.putInt(YoloEngine.MyID);
    	bbf.putInt(boolId);
    	
    	sendMessageToAllreliable(bbf.array());
    }
    
    public void sendMaxLife() {
    	ByteBuffer bbf = ByteBuffer.allocate(12);
    	bbf.putChar('m');
    	bbf.putInt(YoloEngine.MyID);
    	bbf.putFloat(YoloEngine.TeamAB[YoloEngine.MyID].PLAYER_LIVE_MAX);
    	
    	sendMessageToAllreliable(bbf.array());
    }

	public void sendQuitInfo(int myID) {
		ByteBuffer bbf = ByteBuffer.allocate(12);
    	bbf.putChar('q');
    	bbf.putInt(YoloEngine.MyID);
    	
    	sendMessageToAllreliable(bbf.array());		
	}
}

