package com.example.yolo_fighter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;


public class YoloPlayer extends YoloObject {
	
	private static FloatBuffer vertexBuffer;
	private static FloatBuffer textureBuffer;
	private static ByteBuffer indexBuffer;
	
	public boolean isPlayerPoisoned = false; //nie trzeba przesy³aæ 
	public boolean isPlayerSlowDown = false; //nie trzeba przesy³aæ 
	public boolean isPlayerFlying = false;
	public boolean isPlayerDenialed = false;
	public boolean isPlayerInvincible = false;
	public boolean isPlayerDef = false;
	public boolean isPlayerFrozen = false; //nie trzeba przesy³aæ 
	public boolean isPlayerBuff = false;
	public boolean isPlayerFireRateBuff = false;
	public boolean isPlayerMagReloadBuff = false;
	public boolean isBeingHealed = false;
	public boolean isHealing = false;
	
	public boolean isShoting = false; 
	public boolean isJumping = false;
	public boolean isPlayerLeft = false;
	public boolean isCrouch = false;
	public boolean isUsingSkill = false;
	public boolean canSkill1 = true;
	public boolean canSkill2 = true;
	public boolean canSkill3 = true;
	public boolean isClimbingUp = false;
	public boolean isClimbingDown = false;
	public boolean isMoving = false;
	public boolean onGround = false;
	public boolean canMove = true;
	private boolean a=true,b=true,c=true;
	private boolean revers = false;
	
	public boolean playerTeam = false; // 0 - teamA, 1 - teamB
	public int playerID;
	
	public int changesMade;
	public float x_change;
	public float y_change;
	public float x_last;
	public float y_last;
	public float x_lastX;
	public float y_lastX;
	
	int race;
	int weapon;
	float weaponTextureX,weaponTextureY;
	int aim;
	
	public String ParticipantId = "";
	public boolean isPlayerActive,isDead = false; // @TODO jak ktoœ siê roz³¹czy -> nie rysowaæ?
	
	//public float vy =0;
	public float vx = 0f;
	public float PlayerLive = 100;
	public final int PLAYER_BULLET_FREQUENCY = 10; 
	public float PLAYER_LIVE_MAX = 100;
	public float Player_Dmg_reduction = 1f,PlayerDmgBuff =1f;
	float x_texture=0.25f,y_texture=0,x_end=0.375f ,y_end=0,x_start=0,y_start=0,xTx[] = {0,0,0,0,0,0},yTx[] = {0,0,0,0,0,0};
	public int coin =0;
	int aniSlowCounter = 0, animation_slowdown = 0,iconcount=0,act=1,framecount=0;
	
	
	public int poisoned = 0,slowDowned=0,flying =0,defed =0,invice =0,deniled =YoloEngine.denialDuration,frozen =0,icice=0,thunder_h =0,healing =0,buffed =0,fireRated=0,reloadspeeded=0,dashDuration =0;
	public int fireSprite =0,fireCount = 0,firePause = 15,baseFirePause = firePause;
	public float fireDamage =1f,fireDamageBuff = fireDamage,poiseDamage=0.25f;
	public float PlayerMagCapasity = 30f,playerMag = PlayerMagCapasity;
	public int playerMagReloadTime = 100,reloading =0,baseMagReloadtime = playerMagReloadTime;
	public int healBuffer =0;
	
	private static float vertices[] = {
			0.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f
	};
	
	private static float texture[] = {
			0.0f, 0.0f,
			.125f, 0.0f,
			.125f, .125f,
			0.0f, .125f
	};
	
	private static byte indices[]={
		0, 1, 2,
		0, 2, 3
	};
	public YoloPlayer(float x,float y,boolean team,int playerID)
	{
		super(x,y);
		this.playerTeam = team;
		this.playerID = playerID;
		x_texture =0.25f;y_texture = 0f;
		x_start = 0.25f	;y_start =0f;
		x_end = 0.375f	; y_end = 0f;
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuf.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuf.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);
		
		indexBuffer = ByteBuffer.allocateDirect(indices.length);
		indexBuffer.put(indices);
		indexBuffer.position(0);
	}
	public void setAction(int action)
	{ 
		
		if(action != act)
		switch (action)
		{
		case 0://stand left
			x_texture =0.25f;
			y_texture = 0f;
			x_start = 0.25f	;
			y_start =0f;
			x_end = 0.375f	;
			y_end = 0f;
			revers = false;
			break;
		case 1://stand right
			x_texture =0f;
			y_texture = 0.125f;
			x_start = 0f;
			y_start =0.125f;
			x_end = 0.125f	;
			y_end = 0.125f;
			revers = false;
			break;
		case 2://walk left
			x_texture = y_texture = x_start = y_start =0f;
			x_end = 0.75f;
			y_end = 0f;
			framecount =0;
			revers = false;
			break;
		case 3://walk right
			x_texture = x_start = 0.75f;
			y_texture =y_start = 0f;
			x_end = 0.5f;
			y_end = 0.125f;
			framecount =0;
			revers = false;
			break;
		case 4:// climb up
			x_texture =0f;
			y_texture = 0.125f;
			x_start = 0f	;
			y_start =0.125f;
			x_end = 0.125f	;
			y_end = 0.125f;
			framecount =0;
			animation_slowdown =15;
			revers = false;
			break; 
		case 5:// climb down
			x_texture =0f;
			y_texture = 0.125f;
			x_start = 0f	;
			y_start =0.125f;
			x_end = 0.125f	;
			y_end = 0.125f;
			framecount =0;
			animation_slowdown =15;
			revers = false;
			break; 
		case 6: // jump
			break;
		case 7: // fall
			break;
		case 8: //hurt bullet
			break;
		case 9: //hurt arrow
			break;
		case 10: //hurt Skill/AI		
			break;
		case 11: //walk back left
			x_texture = x_start = 0.625f;
			y_texture = y_start = 0f;
			x_end = 0;
			y_end = 0;
			framecount =0;
			revers = true;
			break;
		case 12:// walk back right
			x_texture = x_start =  0.375f;
			y_texture =y_start = 0.125f;
			x_end = 0.75f;
			y_end = 0;
			framecount =0;
			revers = true;
			break;
		}
		
		act = action;
	}
	public void drawAlly(GL10 gl,boolean livebar)
	{
		if(icice > 0)
			icice--;
		else if(thunder_h > 0)
			thunder_h--;
		else
		{
			fireSprite =0;
			fireCount =0;
			fireDamage =1f;
		}
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
		gl.glTranslatef(x, y-YoloEngine.Y_DDROP, 0f);
		gl.glColor4f(1f,1f,1f,1f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glTranslatef(x_texture, y_texture, 0f);
		gl.glTranslatef(0f, 0f, 0f);
		draw(gl,YoloEngine.spriteSheets,2);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		
		if(aniSlowCounter++ >= animation_slowdown)
		{

			if(act == 2 || act == 3)
			{
				if(framecount == 0 )
					YoloEngine.sp.play(YoloEngine.SoundInd[1], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
				else if(framecount == 3)
					YoloEngine.sp.play(YoloEngine.SoundInd[2], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
			}
			else if(act == 4 || act == 5)
			{
				if(framecount == 0 )
					YoloEngine.sp.play(YoloEngine.SoundInd[3], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
			}
			
			framecount++;
			aniSlowCounter = 0;
			if(revers)
			{
				if(y_texture <= y_end && x_texture <= x_end)
				{
					framecount =0;
					y_texture = y_start;
					x_texture = x_start;
				}
				else if(x_texture>0 )x_texture-=0.125f;
				else{y_texture-=0.125f; x_texture=0.875f;}
				
				
			}
			else
			{
				if(x_texture<0.875f )x_texture+=0.125f;
				else
				{y_texture+=0.125f; x_texture=0f;}
				
				if(y_texture >= y_end && x_texture >= x_end)
				{
					framecount =0;
					y_texture = y_start;
					x_texture = x_start;
				}
			}
		}	
		
		if(vx==0)
		{
			if(act!=4||act != 5)
			{
				setAction(isPlayerLeft?0:1);
				animation_slowdown =0;
			}
		}
		
	
		if(livebar)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X*(PlayerLive/PLAYER_LIVE_MAX), YoloEngine.LIFE_BAR_Y, 1f);
			gl.glTranslatef(x/(PlayerLive/PLAYER_LIVE_MAX),y*10+8f, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glColor4f(1f,1f,1f,1f);
			gl.glTranslatef(0.875f, 0.125f, 0); 
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
     
		
		if(isPlayerPoisoned)
		{
			PlayerLive -= 0.1f;
			if(poisoned-- == 0)
				isPlayerPoisoned = false;	
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x + 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		if(isPlayerSlowDown)
		{
			if(slowDowned-- == 0)
				isPlayerSlowDown = false;
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.125f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		if(isPlayerFlying )
		{
			if(flying-- == 0)
			{
				isPlayerFlying = false;
				YoloGame.flying =2;
			}
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glColor4f(1f,1f,1f,1f);
			gl.glTranslatef(0.375f,0.875f, 0f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		if(isPlayerInvincible)
		{
			if(invice-- == 0)
				isPlayerInvincible = false;
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.25f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		
		if(isPlayerDef )
		{
			Player_Dmg_reduction = 0.5f;
			if(defed-- == 0)
			{
				isPlayerDef = false;
				Player_Dmg_reduction = 1;
			}
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.625f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		
		}
		if(isPlayerFrozen)
		{
			canMove = false;
			if(frozen-- == 0)
			{
				isPlayerFrozen = false;
				canMove = true;
			}
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.75f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		if(isPlayerDenialed)
		{
			if(deniled-- == 0)
				isPlayerDenialed = false;

			LinearDraw(gl,5, 0.5f, 0, 28,-0.5f,-0.5f,2f,2f);
		}
		if(isHealing)
		{
			if(healing-- == 0)
				isHealing = false;
			
			if(PlayerLive + 0.05f < PLAYER_LIVE_MAX)
				PlayerLive += 0.05f;
			else
				PlayerLive = PLAYER_LIVE_MAX;
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.5f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
			
			LinearDraw(gl,4, 0.5f, 0.75f, 29,0,0,1,1);
		}
		if(isPlayerBuff)
		{
			fireDamage = fireDamageBuff * PlayerDmgBuff;
			if(buffed--==0)
			{
				isPlayerBuff = false;
				fireDamage = fireDamageBuff;
			}
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.875f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
			
			if(c)c=LinearDraw(gl,3, 0.625f, 0.375f, 32,0,0,1,1);
		}
		if(isPlayerFireRateBuff)
		{
			firePause = baseFirePause/3;
			if(fireRated--==0)
			{
				isPlayerFireRateBuff = false;
				firePause = baseFirePause;
			}
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0f,0.125f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
			
			if(b)b=LinearDraw(gl,2, 0.625f, 0.375f, 32,0,0,1,1);
		}
		if(isPlayerMagReloadBuff)
		{
			playerMagReloadTime= baseMagReloadtime/3;
			if(reloadspeeded--==0)
			{
				isPlayerMagReloadBuff = false;
				 playerMagReloadTime= baseMagReloadtime;
			}
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.125f,0.125f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
			
			if(a)a=LinearDraw(gl,1, 0.625f, 0.375f, 32,0,0,1,1);
		}
		iconcount =0;
		if(isBeingHealed)
		{
			if(PlayerLive+healBuffer < PLAYER_LIVE_MAX)
			{
    			PlayerLive += healBuffer;
    			healBuffer =0;
			}
    		else
    			PlayerLive = PLAYER_LIVE_MAX;
			isBeingHealed =	LinearDraw(gl,0, 0.875f, 0.875f, 17,0,-YoloEngine.Y_DDROP,1,1);
		}
		if(dashDuration > 0)
		{
			if(--dashDuration ==0)
				vx = YoloGame.vxbuff;
			
			//TODO dash texture
		}
		if(PlayerLive <= 0 && ! isDead)
		{
			isDead = true;
			if(YoloEngine.TeamAB[YoloEngine.MyID].race == 0)
				YoloEngine.sp.play(YoloEngine.SoundInd[13], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
			else if(YoloEngine.TeamAB[YoloEngine.MyID].race == 1)
				YoloEngine.sp.play(YoloEngine.SoundInd[15], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
			else if(YoloEngine.TeamAB[YoloEngine.MyID].race == 2)
				YoloEngine.sp.play(YoloEngine.SoundInd[14], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
		}
	}
	public void drawOpponent(GL10 gl)
	{
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
		gl.glTranslatef(x, y-YoloEngine.Y_DDROP, 0f);
		gl.glColor4f(1f,1f,1f,1f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		if(isCrouch) gl.glTranslatef(0f, 0f, 0f);
		else gl.glTranslatef(0.125f, 0f, 0f);
		gl.glTranslatef(0f, 0f, 0f);
		draw(gl,YoloEngine.spriteSheets,3);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(YoloEngine.TEXTURE_SIZE_X*(PlayerLive/PLAYER_LIVE_MAX), YoloEngine.LIFE_BAR_Y, 1f);
		gl.glTranslatef(x/(PlayerLive/PLAYER_LIVE_MAX),y*10+8f, 0f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glColor4f(1f,1f,1f,1f);
		gl.glTranslatef(0.875f, 0.125f, 0); 
		draw(gl,YoloEngine.spriteSheets,1);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		

		if(isPlayerPoisoned)
		{
			PlayerLive -= 0.1f;
			if(poisoned-- == 0)
				isPlayerPoisoned = false;	
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		if(isPlayerSlowDown)
		{
			if(slowDowned-- == 0)
				isPlayerSlowDown = false;
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.125f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		if(isPlayerFlying )
		{
			if(flying-- == 0)
				isPlayerFlying = false;
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glColor4f(1f,1f,1f,1f);
			gl.glTranslatef(0.375f,0.875f, 0f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		if(isPlayerInvincible)
		{
			if(invice-- == 0)
				isPlayerInvincible = false;
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.25f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		
		if(isPlayerDef )
		{
			Player_Dmg_reduction = 0.5f;
			if(defed-- == 0)
				isPlayerDef = false;
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.625f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		
		}
		if(isPlayerFrozen)
		{
			canMove = false;
			if(frozen-- == 0)
			{
				isPlayerFrozen = false;
				canMove = true;
			}
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.75f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		if(isPlayerDenialed)
		{
			if(deniled-- == 0)
				isPlayerDenialed = false;
			
			LinearDraw(gl,5, 0.375f, 0f, 28,-0.5f,-0.5f,2f,2f);
		}
		if(healing>0)
		{
			healing--;
			if(PlayerLive + 0.05f < PLAYER_LIVE_MAX)
				PlayerLive += 0.05f;
			else
				PlayerLive = PLAYER_LIVE_MAX;
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.5f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();

    		LinearDraw(gl,4, 0.5f, 0.75f, 29,0,0,1,1);
		}
		if(isPlayerBuff)
		{
			fireDamage = fireDamageBuff * PlayerDmgBuff;
			if(buffed--==0)
			{
				isPlayerBuff = false;
				fireDamage = fireDamageBuff;
			}
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP+.25f, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.875f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
			
			if(c)c=LinearDraw(gl,3, 0.625f, 0.375f, 32,0,0,1,1);
		}
		if(isPlayerFireRateBuff)
		{
			if(fireRated--==0)
			{
				isPlayerFireRateBuff = false;
				firePause *=3f;
			}
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0f,0.125f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
			
			if(b)b=LinearDraw(gl,2, 0.625f, 0.375f, 32,0,0,1,1);
		}
		if(isPlayerMagReloadBuff)
		{
			if(reloadspeeded--==0)
			{
				isPlayerMagReloadBuff = false;
				 playerMagReloadTime*=3f;
			}
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x+ 0.25f*iconcount++, y-YoloEngine.Y_DDROP, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.125f,0.125f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
			
			if(a)a=LinearDraw(gl,1, 0.625f, 0.375f, 32,0,0,1,1);
		}
		iconcount =0;
		if(isBeingHealed)
		{
			if(PlayerLive+healBuffer < PLAYER_LIVE_MAX)
			{
    			PlayerLive += healBuffer;
    			healBuffer =0;
			}
    		else
    			PlayerLive = PLAYER_LIVE_MAX;
			
			isBeingHealed = LinearDraw(gl, 0, 0.875f, 0.875f, 17,0,-YoloEngine.Y_DDROP,1,1);
		}
	}
	
	public void draw (GL10 gl, int[] spriteSheet,int number)
	{
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, spriteSheet[number]);
		gl.glFrontFace(GL10.GL_CCW);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
	
	public boolean LinearDraw(GL10 gl,int i,float xEnd,float yEnd,int sprite,float xMove,float yMove,float xScale,float yScale)
	{
		if(xTx[i] >= xEnd && yTx[i] >= yEnd)
		{
			yTx[i]=0;xTx[i]=0;
			if(sprite != 28)return false;
		}
		else
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X*xScale, YoloEngine.TEXTURE_SIZE_Y*yScale, 1f);
			gl.glTranslatef((x + xMove)/xScale, (y + yMove)/yScale, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(xTx[i], yTx[i], 0f);
			draw(gl, YoloEngine.spriteSheets,sprite);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}

		if(xTx[i]<1f)xTx[i]+=0.125f;
		else{yTx[i]+=0.125f; xTx[i]=0f;}
		return true;
	}
	
	public void moveAway() {
		this.x = 1000f;
		this.y = 1000f;
		this.x_lastX = 1000f;
		this.y_lastX = 1000f;
		this.x_last = 1000f;
		this.y_last = 1000f;
		
	}

}
