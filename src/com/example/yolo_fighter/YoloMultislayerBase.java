package com.example.yolo_fighter;

import java.nio.ByteBuffer;
import java.util.Random;

import android.app.Activity;
import android.database.CharArrayBuffer;
import android.net.Uri;
import android.os.Parcel;

import com.example.yolo_fighter.YoloEngine;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.ParticipantResult;

public abstract class YoloMultislayerBase { //extends Thread { // TODO is extending Thread a good idea?

	protected abstract void sendMessageToAllreliable(byte[] data);

	protected abstract void sendMessageToAll(byte[] data);
	
	
	// Sprawdza, czy aktywny jest właściwy typ multi
	public static void checkMultislayerInstance(Activity ac) {
		if (YoloEngine.mMultislayer == null) {
			if (YoloEngine.MULTI_MODE == YoloEngine.MULTI_GS)
				YoloEngine.mMultislayer = new YoloMultislayerGS(ac);
			else
				YoloEngine.mMultislayer = new YoloMultislayerBT();
		}
		
		if (YoloEngine.mMultislayer instanceof YoloMultislayerGS && YoloEngine.MULTI_MODE == YoloEngine.MULTI_BT)
			YoloEngine.mMultislayer = new YoloMultislayerBT();
		else if (YoloEngine.mMultislayer instanceof YoloMultislayerBT && YoloEngine.MULTI_MODE == YoloEngine.MULTI_GS) {
			((YoloMultislayerBT)YoloEngine.mMultislayer).koniec();
			YoloEngine.mMultislayer = new YoloMultislayerGS(ac);
		}
		
		YoloEngine.mMultislayer.setActivity(ac);
	}
	

	protected long sentAt;
	protected Activity mActivity = null;
    // private int sentPackageId = 1;
	// private int receivedPackageId = 0;

	
	protected void debugLog(final String text) {
		System.out.println(text);
		if(mActivity!= null)
			mActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {	
				if(YoloMainMenu.debug_textview != null)
					YoloMainMenu.debug_textview.setText(text);
			}
		});
		
	}	
	
	protected void processMessage(ByteBuffer rcvData) {
		char messageCode = rcvData.getChar();

		switch (messageCode) {
		case 'p':
			YoloEngine.mMultislayer.positionDataReceived(rcvData.getInt(), rcvData.getFloat(), rcvData.getFloat(), rcvData.get() == 1 ? true : false, rcvData.getFloat(), 0,rcvData.getInt(),rcvData.getInt(), rcvData.get() == 1 ? true : false);
			break;

		case 'l':
			int spriteLoad;
			for (int i = 0; i < 3; i++) {
				spriteLoad = rcvData.getInt();
				YoloEngine.sprite_load[spriteLoad < 45 ? spriteLoad : spriteLoad - 87] = true;
				if (spriteLoad == 14)
					YoloEngine.sprite_load[27] = true;
				if (spriteLoad == 36)
					YoloEngine.sprite_load[32] = true;
				if (spriteLoad == 37)
					YoloEngine.sprite_load[32] = true;
				if (spriteLoad == 38)
					YoloEngine.sprite_load[32] = true;
				if (spriteLoad == 43)
					YoloEngine.sprite_load[41] = true;
				if (spriteLoad == 120)
					YoloEngine.sprite_load[32] = true;
				if (spriteLoad == 121)
					YoloEngine.sprite_load[32] = true;
				if (spriteLoad == 122)
					YoloEngine.sprite_load[32] = true;
				if (spriteLoad == 123)
					YoloEngine.sprite_load[32] = true;
				if (spriteLoad == 124)
					YoloEngine.sprite_load[32] = true;
			}
			int index  = rcvData.getInt();
			YoloEngine.TeamAB[index].weapon = rcvData.getInt();
			switch(YoloEngine.TeamAB[index].weapon)
			{
			case 0:
				YoloEngine.TeamAB[index].weaponTextureX = .25f;
				YoloEngine.TeamAB[index].weaponTextureY = 0f;
				break;
			case 1:
				YoloEngine.TeamAB[index].weaponTextureX = .375f;
				YoloEngine.TeamAB[index].weaponTextureY = 0f;
				break;
			case 2:
				YoloEngine.TeamAB[index].weaponTextureX = .5f;
				YoloEngine.TeamAB[index].weaponTextureY = 0f;
				break;
			case 4:
				YoloEngine.TeamAB[index].weaponTextureX = .625f;
				YoloEngine.TeamAB[index].weaponTextureY = 0f;
				break;
			case 5:
				YoloEngine.TeamAB[index].weaponTextureX = .75f;
				YoloEngine.TeamAB[index].weaponTextureY = 0f;
				break;
			case 6:
				YoloEngine.TeamAB[index].weaponTextureX = .875f;
				YoloEngine.TeamAB[index].weaponTextureY = 0f;
				break;
			case 7:
				YoloEngine.TeamAB[index].weaponTextureX = .0f;
				YoloEngine.TeamAB[index].weaponTextureY = .125f;
				break;
			case 8:
				YoloEngine.TeamAB[index].weaponTextureX = .125f;
				YoloEngine.TeamAB[index].weaponTextureY = .125f;
				break;
			case 9:
				YoloEngine.TeamAB[index].weaponTextureX = .25f;
				YoloEngine.TeamAB[index].weaponTextureY = .125f;
				break;
			}
			YoloEngine.TeamAB[index].race = rcvData.getInt();
			System.out.println("otrzymane dane spriteload");
			break;

		case 't':
			String pattern = Integer.toBinaryString(rcvData.getInt()).substring(1);

			int i = 0,
			a = 0,
			b = YoloEngine.TeamSize;
			if (YoloEngine.MULTI_MODE == YoloEngine.MULTI_GS) {
				for (Participant p : YoloEngine.participants) {
					if (p.getStatus() == Participant.STATUS_JOINED) {

						if (pattern.charAt(i) == '0') {
							YoloEngine.TeamAB[a].playerTeam = YoloEngine.TeamA;
							YoloEngine.TeamAB[a].ParticipantId = p.getParticipantId();
							if (p.getParticipantId().equals(YoloEngine.playerParticipantID))
								YoloEngine.MyID = a;
							a++;
						}
						if (pattern.charAt(i) == '1') {
							YoloEngine.TeamAB[b].playerTeam = YoloEngine.TeamB;
							YoloEngine.TeamAB[b].ParticipantId = p.getParticipantId();
							if (p.getParticipantId().equals(YoloEngine.playerParticipantID))
								YoloEngine.MyID = b;
							b++;
						}
						i++;
					}
				}
			}
			else {	
				for (YoloPlayer p : YoloEngine.TeamAB)
					p = new YoloPlayer(1000f, 1000f, false, 666);	
				
				for (String p : YoloEngine.participantsBT) {					
						if (pattern.charAt(i) == '0') {
							YoloEngine.TeamAB[a].playerTeam = YoloEngine.TeamA;
							YoloEngine.TeamAB[a].ParticipantId = p;
							if (p.equals(YoloEngine.playerParticipantID))
								YoloEngine.MyID = a;
							a++;
						}
						if (pattern.charAt(i) == '1') {
							YoloEngine.TeamAB[b].playerTeam = YoloEngine.TeamB;
							YoloEngine.TeamAB[b].ParticipantId = p;
							if (p.equals(YoloEngine.playerParticipantID))
								YoloEngine.MyID = b;
							b++;
						}
						i++;					
				}							
			}
				
			YoloEngine.mMultislayer.sendMaxLife();
			YoloEngine.startTime = System.currentTimeMillis() + YoloEngine.countdownTime;
			break;

		case 'f':
			YoloGameRenderer.OpponentFire(rcvData.getFloat(), rcvData.getFloat(), rcvData.get() == 1 ? true : false, rcvData.get() == 1 ? true : false, rcvData.getInt(), rcvData.getInt(),
					rcvData.getFloat(), rcvData.get() == 1 ? true : false,rcvData.getInt(),rcvData.getFloat());
			break;

		case 'g':
			YoloGameRenderer.AIFire(rcvData.getFloat(), rcvData.getFloat(), rcvData.get() == 1 ? true : false, rcvData.getInt(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(),
					rcvData.get() == 1 ? true : false);
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
			 * 17 isBeingHealed 23 isPlayerFlying 24 isPlayerDef 25
			 * isPlayerInvincible 28 isPlayerDenialed 29 isHealing 36
			 * isPlayerBuff 37 isPlayerFireRateBuff 38 isPlayerMagReloadBuff
			 */
			break;

		case 'h':
			YoloGameRenderer.hitBoxs.add(new HitBox(rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getFloat(), rcvData.getInt(), rcvData
					.get() == 1 ? true : false, rcvData.get() == 1 ? true : false, rcvData.get() == 1 ? true : false, rcvData.getInt()));
			break;

		case 's':
			/*
			 * W imieniu towarzysza Iosif Wissarionowicz Dzugaszwili Stalina
			 * anektujemy ten teren ku lepszemu Zwiaskowi Socjalistycznemu
			 * Republik Radzieckich
			 */
			Skill tSkill = new Skill(rcvData.getFloat(), rcvData.getFloat(), rcvData.getInt(), rcvData.get() == 1 ? true : false, rcvData.getInt());
			if (tSkill.team == YoloEngine.TeamA)
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
		case 'r':
			boolean addAction = rcvData.get() == 1 ? true : false;
			if(addAction)
				YoloGameRenderer.PowerUPtab.add(new PowerUP(rcvData.getFloat(), rcvData.getFloat(), rcvData.getInt()));
			else
				YoloGameRenderer.PowerUPtab.removeElementAt((int)rcvData.getFloat());
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

	/**
	 * Wyznacza nowe przemieszczenia dla postaci przeciwnika
	 * 
	 * @param playerID
	 *            kogo dotyczą informacje
	 * @param x
	 * @param y
	 * @param isCrouch
	 * @param packageId
	 */
	protected void positionDataReceived(final int playerID, final float x, final float y, final boolean isCrouch, float life, int packageId,int aim,int act, boolean isLeft) {
		/* XXX
		 * if (packageId < receivedPackageId) { System.out.println("old data");
		 * return; } else receivedPackageId = packageId; // NIE DZIA�A, mo�e
		 * powinno?? (nie zwi�ksza sie zmienna)  dla > 2 graczy trzeba zrobi�
		 * array, Licznik oparty na INT, kiedy� // sko�czy si� zakres
		 */
		YoloEngine.TeamAB[playerID].isCrouch = isCrouch;

		YoloEngine.TeamAB[playerID].x_change = ((x - YoloEngine.TeamAB[playerID].x_last) / (float) YoloEngine.MULTI_STEPS);
		YoloEngine.TeamAB[playerID].y_change = ((y - YoloEngine.TeamAB[playerID].y_last) / (float) YoloEngine.MULTI_STEPS);

		YoloEngine.TeamAB[playerID].changesMade = 0;

		YoloEngine.TeamAB[playerID].x_last = x;
		YoloEngine.TeamAB[playerID].y_last = y;

		YoloEngine.TeamAB[playerID].PlayerLive = life;
		YoloEngine.TeamAB[playerID].aim = aim;
		YoloEngine.TeamAB[playerID].setAction(act);
		
		YoloEngine.TeamAB[playerID].isPlayerLeft = isLeft;

	}

	/**
	 * Wysy�a dane o pozycji gracza, w odst�pach YoloEngine.UPDATE_FREQ
	 * 
	 * @param x
	 * @param y
	 * @param isCrouched
	 */
	public void sendPlayerPosition(float x, float y, boolean isCrouched, boolean isLeft) {
		// int 4, float 4, char 2 --> bajty
		if (System.currentTimeMillis() - sentAt >= YoloEngine.UPDATE_FREQ) {
			sentAt = System.currentTimeMillis();

			ByteBuffer bbf = ByteBuffer.allocate(30);
			bbf.putChar('p');
			bbf.putInt(YoloEngine.MyID);
			bbf.putFloat(x);
			bbf.putFloat(y);
			if (isCrouched)
				bbf.put((byte) 1);
			else
				bbf.put((byte) 0);
			bbf.putFloat(YoloEngine.TeamAB[YoloEngine.MyID].PlayerLive);
			bbf.putInt(YoloEngine.TeamAB[YoloEngine.MyID].aim);
			bbf.putInt(YoloEngine.TeamAB[YoloEngine.MyID].act);
			if (isLeft)
				bbf.put((byte) 1);
			else
				bbf.put((byte) 0);
			sendMessageToAll(bbf.array());
		}

	}

	public byte[] sendSpriteLoad(int[] loadArray) {
		ByteBuffer bbf = ByteBuffer.allocate(14 + 4 * loadArray.length);
		bbf.putChar('l');
		for (int value : loadArray)
			bbf.putInt(value);
		bbf.putInt(YoloEngine.MyID);
		bbf.putInt(YoloEngine.currentPlayerInfo.getWEQ());
		bbf.putInt(YoloEngine.currentPlayerInfo.getRace());

		return bbf.array();
	}

	public void sendOpponentFire(float x, float y, boolean isLeft, boolean isCrouch, int sprite, int count, float damage, boolean team,int aim,float poiseDamge) {
		ByteBuffer bbf = ByteBuffer.allocate(40);
		bbf.putChar('f');
		bbf.putFloat(x);
		bbf.putFloat(y);
		if (isLeft)
			bbf.put((byte) 1);
		else
			bbf.put((byte) 0);
		if (isCrouch)
			bbf.put((byte) 1);
		else
			bbf.put((byte) 0);
		bbf.putInt(sprite);
		bbf.putInt(count);
		bbf.putFloat(damage);
		if (team)
			bbf.put((byte) 1);
		else
			bbf.put((byte) 0);
		bbf.putInt(aim);
		bbf.putFloat(poiseDamge);

		sendMessageToAll(bbf.array());
	}

	public void sendAIFire(float x, float y, boolean isLeft, int sprite, float x_texture, float y_texture, float damage, boolean team) {
		ByteBuffer bbf = ByteBuffer.allocate(30);
		bbf.putChar('g');
		bbf.putFloat(x);
		bbf.putFloat(y);
		if (isLeft)
			bbf.put((byte) 1);
		else
			bbf.put((byte) 0);
		bbf.putInt(sprite);
		bbf.putFloat(x_texture);
		bbf.putFloat(y_texture);
		bbf.putFloat(damage);
		if (team)
			bbf.put((byte) 1);
		else
			bbf.put((byte) 0);

		sendMessageToAll(bbf.array());
	}

	public void sendHitBox(float x, float y, float x_radius, float y_radius, float damage, float duration, int sprite, boolean isLeft, boolean efectOnMySkill, int ID) {
		// team te� jest przesy�any, tylko nie z arg a z YoloEngine
		ByteBuffer bbf = ByteBuffer.allocate(42);
		bbf.putChar('h');
		bbf.putFloat(x);
		bbf.putFloat(y);
		bbf.putFloat(x_radius);
		bbf.putFloat(y_radius);
		bbf.putFloat(damage);
		bbf.putFloat(duration);
		bbf.putInt(sprite);
		if (isLeft)
			bbf.put((byte) 1);
		else
			bbf.put((byte) 0);
		if (YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
			bbf.put((byte) 1);
		else
			bbf.put((byte) 0);
		if (efectOnMySkill)
			bbf.put((byte) 1);
		else
			bbf.put((byte) 0);
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
		 * 17 isBeingHealed 23 isPlayerFlying 24 isPlayerDef 25
		 * isPlayerInvincible 28 isPlayerDenialed 29 isHealing 36 isPlayerBuff
		 * 37 isPlayerFireRateBuff 38 isPlayerMagReloadBuff
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
	
	public void sendPowerUp(boolean actionAdd, float x, float y, int effect) {
		ByteBuffer bbf = ByteBuffer.allocate(16);	
		bbf.putChar('r');
		if(actionAdd)
			bbf.put((byte) 1);
		else
			bbf.put((byte) 0);
		bbf.putFloat(x);
		bbf.putFloat(y);
		bbf.putInt(effect);
		
		sendMessageToAllreliable(bbf.array());
	}

	public void setActivity(Activity xActivity) {
		this.mActivity = xActivity;
		System.out.println(mActivity.getTaskId()+" id z updateu");
	}

	
	static String assignTeams() {
		String teamAssignPattern = "1";

		// Team assignment dokÄ…d {0-teamA, 1-teamB}
		int a = 0, b = YoloEngine.TeamSize;
		// Przydzielamy nam
		if (new Random().nextBoolean()) {
			YoloEngine.TeamAB[a].playerTeam = YoloEngine.TeamA;
			YoloEngine.MyID = a;
			teamAssignPattern += "0";
			a++;
		} else {
			YoloEngine.TeamAB[b].playerTeam = YoloEngine.TeamB;
			YoloEngine.MyID = b;
			teamAssignPattern += "1";
			b++;
		}
		YoloEngine.TeamAB[YoloEngine.MyID].ParticipantId = YoloEngine.playerParticipantID;

		// Przydzielamy reszcie graczy
		for (Participant p : YoloEngine.participants) {
			if (!(YoloEngine.playerParticipantID.equals(p.getParticipantId()) || p.getStatus() != Participant.STATUS_JOINED)) {
				// nie jesteĹ›my to my, gracz nie naleĹĽy jeszcze do ĹĽadnego teamu
				// @TODO sprawdzenie, czy gracz nie ma już
				// przydzielonego teamu?
				if (a > (b - YoloEngine.TeamSize)) {
					YoloEngine.TeamAB[b].playerTeam = YoloEngine.TeamB;
					YoloEngine.TeamAB[b].ParticipantId = p.getParticipantId();
					teamAssignPattern += "1";
					b++;

				} else if (a < (b - YoloEngine.TeamSize)) {
					YoloEngine.TeamAB[a].playerTeam = YoloEngine.TeamA;
					YoloEngine.TeamAB[a].ParticipantId = p.getParticipantId();
					teamAssignPattern += "0";
					a++;

				} else {
					if (new Random().nextBoolean()) {
						YoloEngine.TeamAB[a].playerTeam = YoloEngine.TeamA;
						YoloEngine.TeamAB[a].ParticipantId = p.getParticipantId();
						teamAssignPattern += "0";
						a++;

					} else {
						YoloEngine.TeamAB[b].playerTeam = YoloEngine.TeamB;
						YoloEngine.TeamAB[b].ParticipantId = p.getParticipantId();
						teamAssignPattern += "1";
						b++;

					}
				}
			}
		}
		return teamAssignPattern;
	}
	
	static String assignTeamsXX() {
		String teamAssignPattern = "1";

		// Team assignment dokÄ…d {0-teamA, 1-teamB}
		int a = 0, b = YoloEngine.TeamSize;
		// Przydzielamy nam
		if (new Random().nextBoolean()) {
			YoloEngine.TeamAB[a].playerTeam = YoloEngine.TeamA;
			YoloEngine.MyID = a;
			teamAssignPattern += "0";
			a++;
		} else {
			YoloEngine.TeamAB[b].playerTeam = YoloEngine.TeamB;
			YoloEngine.MyID = b;
			teamAssignPattern += "1";
			b++;
		}
		YoloEngine.TeamAB[YoloEngine.MyID].ParticipantId = YoloEngine.playerParticipantID;

		// Przydzielamy reszcie graczy
		for (String p : YoloEngine.participantsBT) {
			if (!(YoloEngine.playerParticipantID.equals(p))) {
				// nie jesteĹ›my to my, 
				// @TODO sprawdzenie, czy gracz nie ma już
				// przydzielonego teamu?
				if (a > (b - YoloEngine.TeamSize)) {
					YoloEngine.TeamAB[b].playerTeam = YoloEngine.TeamB;
					YoloEngine.TeamAB[b].ParticipantId = p;
					teamAssignPattern += "1";
					b++;

				} else if (a < (b - YoloEngine.TeamSize)) {
					YoloEngine.TeamAB[a].playerTeam = YoloEngine.TeamA;
					YoloEngine.TeamAB[a].ParticipantId = p;
					teamAssignPattern += "0";
					a++;

				} else {
					if (new Random().nextBoolean()) {
						YoloEngine.TeamAB[a].playerTeam = YoloEngine.TeamA;
						YoloEngine.TeamAB[a].ParticipantId = p;
						teamAssignPattern += "0";
						a++;

					} else {
						YoloEngine.TeamAB[b].playerTeam = YoloEngine.TeamB;
						YoloEngine.TeamAB[b].ParticipantId = p;
						teamAssignPattern += "1";
						b++;

					}
				}
			}
		}
		return teamAssignPattern;
	}
	
	
}


