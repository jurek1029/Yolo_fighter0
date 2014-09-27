package com.example.yolo_fighter;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

public class YoloGameRenderer implements Renderer {
	
	private YoloTexture TextureLoader ;
	private int[] spriteSheets = new int[4];
	private YoloBackground back = new YoloBackground();
	private YoloPlayer player = new YoloPlayer();
	private YoloBackground btn_mov = new YoloBackground(),btn_movball = new YoloBackground(); 
	private YoloBackground live_bar_1 = new YoloBackground(),live_bar_0 = new YoloBackground();
	private YoloWeapon btn = new YoloWeapon();
	
	private Vector<YoloWeapon> Weapontab  = new Vector<YoloWeapon>();
	private YoloWeapon bullet;

	
	private final float MOVE_SIZE_X = 2*YoloEngine.MAX_VALUE_PLAYER_SPEED/YoloEngine.display_x;
	private final float MOVE_SIZE_Y = YoloEngine.MOVE_SIZE_Y/YoloEngine.display_y;
	private final float MOVE_SIZE_X1 = 160f/YoloEngine.display_x;
	private final float MOVE_SIZE_Y1 = 160f/YoloEngine.display_y;
	private final float MOVE_BALL_SIZE_X = YoloEngine.MOVE_SIZE_Y/YoloEngine.display_x;
	private final float MOVE_POS_X = (YoloEngine.MOVE_X/YoloEngine.display_x - MOVE_SIZE_X/2)/MOVE_SIZE_X;
	private final float MOVE_POS_Y = (YoloEngine.display_y - YoloEngine.MOVE_Y)/YoloEngine.display_y + MOVE_SIZE_Y/2;
	private final float MOVE_POS_X1= (25f/YoloEngine.display_x)/MOVE_SIZE_X1 ;
	private final float MOVE_POS_Y1= (25f/YoloEngine.display_y)/MOVE_SIZE_Y1 ; 
	private final float LIVE_BAR_SIZE_X_0 = YoloEngine.LIVE_BAR_SIZE/YoloEngine.display_x;
	private float LIVE_BAR_SIZE_X_1 = LIVE_BAR_SIZE_X_0;
	private final float LIVE_BAR_SIZE_Y = 30f/YoloEngine.display_y;
	
	private float cameraPosX,joyBallX = (YoloGame.x2/YoloEngine.display_x - MOVE_BALL_SIZE_X/2)/MOVE_BALL_SIZE_X,
			jumpBtnX = 1/(MOVE_BALL_SIZE_X*2)-1.5f,shotBtnX = jumpBtnX,crouchBtnX = 2.75f,liveBarX_0 = (0.5f/(1f/LIVE_BAR_SIZE_Y))*(1/LIVE_BAR_SIZE_X_0);	
	private float joyBackTX = 0,joyBallTX = 0,BtnTX = 0,liveBarTX = 0,liveBarTX_1 = 0,XADD = 0; 

	private float cameraPosY,jumpBtnY = 1.5f,shotBtnY = .25f,crouchBtnY = .25f ,liveBarY = 1f/LIVE_BAR_SIZE_Y -1.75f;
	private float joyBackYT = 0,joyBallYT = 0,BtnYT = 0,liveBarYT = 0,YADD = 0;
	
	private float joyBallX1,joyBallY1;
	
	
	private int nextBullet = 0;
	private boolean onGround = true;
	private int ClimbingOn;
				
	private YoloObject[] ObjectTab = new YoloObject[17];
	private YoloObject[] LaddreTab = new YoloObject[4];
	
	private long loopStart = 0;
	private long loopEnd = 0;
	private long loopRunTime = 0;
	
	@Override
	public void onDrawFrame(GL10 gl) {
		loopStart = System.currentTimeMillis();
		
		//--------------------------------------------------GRAVITANCJA-------------------------------------------------------------		
		if(YoloEngine.canMove)
		{
			YoloEngine.Player_vy -= YoloEngine.GAME_ACCELERATION;
			
			for(int i = 0; i < ObjectTab.length; i++)
			{
				if(IsCollidedTop(ObjectTab[i]))
				{
					if(YoloEngine.Player_vy > 0)
						{
							//YoloEngine.Player_y = ObjectTab[i].min_y - YoloEngine.Player_y;
							onGround = false;
						}
					else
						{
							YoloEngine.Player_y = ObjectTab[i].max_y;
							YoloEngine.Player_vy = 0;
							onGround = true;
						}
					
						
					break;
				}
				onGround = false;
			}
		}
		YoloEngine.Player_y += YoloEngine.Player_vy;
		//-------------------------------------------TARCIE-----------------------------------------------------------------
		
		
		if(!YoloEngine.isMoving && YoloEngine.Player_vx != 0)
		{
			if(onGround)
			{
				if(YoloEngine.Player_vx>YoloEngine.GAME_GROUND_FRICTION *2) YoloEngine.Player_vx -= YoloEngine.GAME_GROUND_FRICTION;
				else if(YoloEngine.Player_vx<-YoloEngine.GAME_GROUND_FRICTION *2 )YoloEngine.Player_vx += YoloEngine.GAME_GROUND_FRICTION;
				else YoloEngine.Player_vx = 0;
			}
			else
			{
				if(YoloEngine.Player_vx>YoloEngine.GAME_AIR_FRICTION *2) YoloEngine.Player_vx -= YoloEngine.GAME_AIR_FRICTION;
				else if(YoloEngine.Player_vx<-YoloEngine.GAME_AIR_FRICTION *2 )YoloEngine.Player_vx += YoloEngine.GAME_AIR_FRICTION;
				else YoloEngine.Player_vx = 0;
			}
		}
		
//--------------------------------------------------------PORUSZNIE KAMER�---------------------------------------------------	
		if(YoloEngine.canMove)
		{
			YoloEngine.Player_x += YoloEngine.Player_vx;
			if(YoloEngine.Player_x < 0)
				{
					YoloEngine.Player_x = YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X-1;
					gl.glMatrixMode(GL10.GL_PROJECTION);
					gl.glLoadIdentity();
					gl.glOrthof(0f, 1f, 0f, 1f, -1f, 1f);
					
					XADD = 2f;
					cameraPosX = -XADD;
					BtnTX = XADD/ (MOVE_BALL_SIZE_X*2);
					joyBallTX = XADD /MOVE_BALL_SIZE_X;
					joyBackTX = XADD / MOVE_SIZE_X;
					liveBarTX = XADD / LIVE_BAR_SIZE_X_0;
					liveBarTX_1 = XADD / LIVE_BAR_SIZE_X_1;
					
					gl.glTranslatef(cameraPosX,cameraPosY,0f);
				}
			else if(YoloEngine.Player_x > YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X -1)
					{
						YoloEngine.Player_x = 0;
						gl.glMatrixMode(GL10.GL_PROJECTION);
						gl.glLoadIdentity();
						gl.glOrthof(0f, 1f, 0f, 1f, -1f, 1f);
						cameraPosX = 0;
						joyBackTX = 0;
						joyBallTX = 0;
						BtnTX = 0;
						liveBarTX = 0;
						liveBarTX_1 = 0;
						
						gl.glTranslatef(cameraPosX,cameraPosY,0f);
					}
		}
		
//------------------------------------------------------DARBINY---------------------------------------------------------------
		for (int i = 0;i < LaddreTab.length;i++)
		{
			if(IsCollided(LaddreTab[i]))
			{
				YoloEngine.canClimb = true;
				if(YoloEngine.isClimbingUp)
				{
					ClimbingOn = i;
					YoloEngine.Player_vy = YoloEngine.PLAYER_CLIMBING_SPEED;
				}
				else if(YoloEngine.isClimbingDown)
					{
						ClimbingOn = i;
						YoloEngine.Player_vy = -YoloEngine.PLAYER_CLIMBING_SPEED;
					}
				
				break;
			}
			YoloEngine.canClimb = false;
			
		}
		if(YoloEngine.isClimbingDown || YoloEngine.isClimbingUp)
		{
			if(!IsCollided(LaddreTab[ClimbingOn]))
			{
				YoloEngine.canMove = true;
				YoloEngine.isClimbingDown = false;
				YoloEngine.isClimbingUp = false;
				YoloEngine.Player_vy = 0;
			}
		}
//----------------------------------------------------------------------------------------------------------------------------		

		try
		{
			if(loopRunTime < YoloEngine.GAME_THREAD_FSP_SLEEP)
			{
			Thread.sleep(YoloEngine.GAME_THREAD_FSP_SLEEP - loopRunTime);
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		drawBackground(gl);
		drawPlayer(gl);
		drawControls(gl);
		drawButtons(gl);
		if(YoloEngine.isShoting)playerFire(0.5f);
		else nextBullet = 0;
		moveBullets(gl);

		
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		loopEnd = System.currentTimeMillis();
		loopRunTime = (loopEnd - loopStart);
		
		
	}
	private boolean IsCollidedTop(YoloObject object)
	{
		if(YoloEngine.Player_x + 1  < object.min_x || YoloEngine.Player_x > object.max_x)return false;
		if(YoloEngine.Player_y < object.min_y || YoloEngine.Player_y > object.max_y)return false;
		
		return true;
	}
	
	private boolean IsCollided(YoloObject object)
	{
		if(YoloEngine.Player_x + 1  < object.min_x || YoloEngine.Player_x > object.max_x)return false;
		if(YoloEngine.Player_y + 1 < object.min_y || YoloEngine.Player_y > object.max_y)return false;
		
		return true;
	}
	private boolean IsCollided(YoloWeapon object)
	{
		if(YoloEngine.Player_x + YoloEngine.PLAYER_SIZE < object.x || YoloEngine.Player_x > object.x + bullet.size)return false;
		if(YoloEngine.Player_y + YoloEngine.PLAYER_SIZE < object.y || YoloEngine.Player_y > object.y + bullet.size)return false;
		
		return true;
	}
	private boolean IsCollided(YoloWeapon bullet, YoloObject object)
	{
		if(object.max_x  < bullet.x || object.min_x > bullet.x + bullet.size)return false;
		if(object.max_y  < bullet.y || object.min_y > bullet.y + bullet.size)return false;
		
		return true;
	}
	
	private void drawBullet(GL10 gl, YoloWeapon bullet)
	{
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(1/YoloEngine.GAME_PROJECTION_X, 1/YoloEngine.GAME_PROJECTION_Y, 1f);
		gl.glTranslatef(bullet.x, bullet.y, 0f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		if(bullet.isLeft)gl.glTranslatef(0f, 0f, 0f);
		else gl.glTranslatef(.125f, 0f, 0f);
		gl.glColor4f(1f,1f,1f,1f);
		bullet.draw(gl,spriteSheets,0);
		gl.glPopMatrix();
		gl.glLoadIdentity();
	}
	
	private void moveBullets(GL10 gl)
	{
		for(int i = 0 ; i < Weapontab.size() ;i++)
		{
			if(Weapontab.get(i).isLeft) Weapontab.get(i).x -= Weapontab.get(i).bulletSpeed;
			else  Weapontab.get(i).x += Weapontab.get(i).bulletSpeed;

			drawBullet(gl, Weapontab.get(i));
			
			if(Weapontab.get(i).x < 0)Weapontab.remove(i);
			else if(Weapontab.get(i).x > YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X)Weapontab.remove(i);
			else if(!Weapontab.get(i).isMy && IsCollided(Weapontab.get(i)))
			{
				YoloEngine.PlayerLive -= Weapontab.get(i).damage;
				Weapontab.remove(i);
			}
			else for (int j = 0 ; j< ObjectTab.length ; j++)
					if (IsCollided(Weapontab.get(i), ObjectTab[j])) Weapontab.remove(i);
			
		}
	}
	
	private void drawControls(GL10 gl)
	{
		if(YoloEngine.isClasic)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(MOVE_SIZE_X, MOVE_SIZE_Y, 1f);
			gl.glTranslatef(MOVE_POS_X + joyBackTX,MOVE_POS_Y + joyBackYT, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glColor4f(0.6f,0.6f,0.6f,0.5f);
			btn_mov.draw(gl);
			gl.glPopMatrix();
			gl.glLoadIdentity();
			
			
			joyBallX = (YoloGame.x2/YoloEngine.display_x - MOVE_BALL_SIZE_X/2)/MOVE_BALL_SIZE_X;
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(MOVE_BALL_SIZE_X, MOVE_SIZE_Y, 1f);
			gl.glTranslatef(joyBallX + joyBallTX,MOVE_POS_Y + joyBallYT, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glColor4f(0.7f,0.7f,0.7f,0.5f);
			btn_movball.draw(gl);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		else
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(MOVE_SIZE_X1, MOVE_SIZE_Y1, 1f);
			gl.glTranslatef(MOVE_POS_X1 + joyBackTX,MOVE_POS_Y1 + joyBackYT, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glColor4f(0.7f,0.7f,0.7f,0.6f);
			btn_mov.draw(gl);
			gl.glPopMatrix();
			gl.glLoadIdentity();
			
			joyBallX1 = MOVE_POS_X1*2 + .5f - ((YoloGame.x_old-YoloGame.x2)/YoloEngine.display_x)/MOVE_SIZE_X1;
			joyBallY1 = MOVE_SIZE_Y1 +.5f + YoloGame.y2/YoloEngine.display_y/MOVE_SIZE_Y1;
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(MOVE_SIZE_X1/2, MOVE_SIZE_Y1/2, 1f);
			gl.glTranslatef(joyBallX1 + joyBackTX*2,joyBallY1 + joyBackYT*2, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glColor4f(0.7f,0.7f,0.7f,0.5f);
			btn_movball.draw(gl);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
			
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(LIVE_BAR_SIZE_X_0, LIVE_BAR_SIZE_Y, 1f);
		gl.glTranslatef( liveBarX_0 + liveBarTX,liveBarY +liveBarYT ,0f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glColor4f(1f,1f,1f,1f);
		live_bar_0.draw(gl);
		gl.glPopMatrix();
		gl.glLoadIdentity();
			
		liveBarTX_1 *= LIVE_BAR_SIZE_X_1 /( LIVE_BAR_SIZE_X_0*YoloEngine.PlayerLive/YoloEngine.PLAYER_LIVE_MAX);
		LIVE_BAR_SIZE_X_1 = LIVE_BAR_SIZE_X_0*YoloEngine.PlayerLive/YoloEngine.PLAYER_LIVE_MAX;
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(LIVE_BAR_SIZE_X_1, LIVE_BAR_SIZE_Y, 1f);
		gl.glTranslatef((0.5f/(1f/LIVE_BAR_SIZE_Y))*(1/LIVE_BAR_SIZE_X_1) + liveBarTX_1 ,liveBarY +liveBarYT,0f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glColor4f(1f,1f,1f,1f);
		live_bar_1.draw(gl);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		
	}
	
	private void drawButtons(GL10 gl)
	{
		if(YoloEngine.isClasic)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, 1f);
			gl.glTranslatef(jumpBtnX + BtnTX,jumpBtnY + BtnYT, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			if(YoloEngine.isJumping)gl.glTranslatef(0, 0, 0);
			else gl.glTranslatef(.125f, 0, 0);
			gl.glColor4f(.8f,.8f,.8f,0.5f);
			btn.draw(gl, spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, 1f);
			gl.glTranslatef(crouchBtnX + BtnTX,crouchBtnY + BtnYT , 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			if(YoloEngine.isCrouch)
				if(YoloEngine.isCrouch_prest)gl.glTranslatef(.75f, 0, 0);
				else gl.glTranslatef(.875f, 0, 0);
			else
				if(YoloEngine.isCrouch_prest)gl.glTranslatef(.5f, 0, 0);
				else gl.glTranslatef(.625f, 0, 0);
			
			gl.glColor4f(.8f,.8f,.8f,0.5f);
			btn.draw(gl, spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, 1f);
		if(YoloEngine.isClasic)	gl.glTranslatef(shotBtnX + BtnTX,shotBtnY + BtnYT , 0f);
		else gl.glTranslatef(shotBtnX + BtnTX,shotBtnY + BtnYT + .5f , 0f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		if(YoloEngine.isShoting)gl.glTranslatef(.25f, 0, 0);
		else gl.glTranslatef(.375f, 0, 0);
		gl.glColor4f(.8f,.8f,.8f,0.5f);
		btn.draw(gl, spriteSheets,1);
		gl.glPopMatrix();
		gl.glLoadIdentity();
	}
	
	private void drawBackground(GL10 gl)
	{
		if(YoloEngine.Player_x > YoloEngine.GAME_PROJECTION_X/2 +.5f && YoloEngine.Player_x < YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X - YoloEngine.GAME_PROJECTION_X/2 - .5f)
		{
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0f, 1f, 0f, 1f, -1f, 1f);
			XADD = ((YoloEngine.Player_x-.5f)/YoloEngine.GAME_PROJECTION_X) - .5f;
			cameraPosX = -XADD;
			BtnTX = XADD/ (MOVE_BALL_SIZE_X*2);
			if(YoloEngine.isClasic)
			{
				joyBallTX = XADD /MOVE_BALL_SIZE_X;
				joyBackTX = XADD / MOVE_SIZE_X;
			}
			else
			{
				joyBallTX = XADD /(MOVE_SIZE_X1*2f);
				joyBackTX = XADD / MOVE_SIZE_X1;
			}
			liveBarTX = XADD / LIVE_BAR_SIZE_X_0;
			liveBarTX_1 = XADD / LIVE_BAR_SIZE_X_1;
			
			gl.glTranslatef(cameraPosX,cameraPosY,0f);
			
		}
		if(YoloEngine.Player_y > YoloEngine.GAME_PROJECTION_Y/2 + .5f && YoloEngine.Player_y < YoloEngine.LEVEL_SIZE_Y*YoloEngine.GAME_PROJECTION_Y - YoloEngine.GAME_PROJECTION_Y/2 - .5f)
		{
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0f, 1f, 0f, 1f, -1f, 1f);
			YADD = ((YoloEngine.Player_y-.5f)/YoloEngine.GAME_PROJECTION_Y) - .5f;
			cameraPosY = -YADD ;
			BtnYT = YADD/ (MOVE_SIZE_Y*2);
			if(YoloEngine.isClasic)
			{
				joyBackYT = YADD / MOVE_SIZE_Y;
				joyBallYT = YADD / MOVE_SIZE_Y;
			}
			else
			{
				joyBackYT = YADD / MOVE_SIZE_Y1;
				joyBallYT = YADD / (MOVE_SIZE_Y1*2f);
			}
			liveBarYT = YADD / LIVE_BAR_SIZE_Y;
			
			gl.glTranslatef(cameraPosX,cameraPosY,0f);
			
			
		}
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(YoloEngine.LEVEL_SIZE_X,YoloEngine.LEVEL_SIZE_Y, 1f);
		gl.glTranslatef(0f, 0f, 0f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glColor4f(1f,1f,1f,1f);
		back.draw(gl);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
	}

	private void drawPlayer(GL10 gl)
	{
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(1/YoloEngine.GAME_PROJECTION_X, 1/YoloEngine.GAME_PROJECTION_Y, 1f);
		gl.glTranslatef(YoloEngine.Player_x, YoloEngine.Player_y, 0f);
		gl.glColor4f(1f,1f,1f,1f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		if(YoloEngine.isCrouch) gl.glTranslatef(0f, 0f, 0f);
		else gl.glTranslatef(0.125f, 0f, 0f);
		gl.glTranslatef(0f, 0f, 0f);
		player.draw(gl,spriteSheets,2);
		gl.glPopMatrix();
		gl.glLoadIdentity();
	}
	private void playerFire(float bulletSpeed)
	{
		if(nextBullet == 0)
		{
			bullet = new YoloWeapon(bulletSpeed);
			bullet.damage = 1f;
			bullet.isMy = false; // ------------------strzelarz nie swoimi pociskami tylko do sprawdzenia czy umierasz ----
			bullet.x = YoloEngine.Player_x;
			if(!YoloEngine.isCrouch)	bullet.y = YoloEngine.Player_y + .5f; 
			else bullet.y = YoloEngine.Player_y + .025f; 
			bullet.size = 0.25f;
			bullet.isLeft = YoloEngine.isPlayerLeft;
			Weapontab.add(bullet);
			nextBullet = YoloEngine.PLAYER_BULLET_FREQUENCY;
		}
		nextBullet--;
		//TODO pociski przeciwnika
	}
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0f, 1f, 0f, 1f, -1f, 1f);
		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
	//	gl.glEnable(GL10.GL_BLEND);
	//	gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE);
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		back.loadTexture(gl, YoloEngine.BACKGROUND, YoloEngine.context);
		if(YoloEngine.isClasic)
		{
			btn_mov.loadTexture(gl, YoloEngine.MOVE_TEXTURE, YoloEngine.context);
			btn_movball.loadTexture(gl, YoloEngine.MOVE_BALL_TEXTURE, YoloEngine.context);
		}
		else
		{
			btn_mov.loadTexture(gl, YoloEngine.MOVE_TEXTURE_1, YoloEngine.context);
			btn_movball.loadTexture(gl, YoloEngine.MOVE_BALL_TEXTURE_1, YoloEngine.context);
		}
		live_bar_1.loadTexture(gl, YoloEngine.LIVE_BAR_1, YoloEngine.context);
		live_bar_0.loadTexture(gl, YoloEngine.LIVE_BAR_0, YoloEngine.context);
		
		TextureLoader = new YoloTexture(gl);
		spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.WEAPON_SPRITE, YoloEngine.context, 0);
		spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.BUTTON_TEXTURE, YoloEngine.context, 1);
		spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.PLAYER_TEXTURE, YoloEngine.context, 2);
		
//------------------------------------------INICJOWANIE OBIEKT�W FIZYCZNYCH----------------------------------		
		YoloEngine.LEVEL_SIZE_X = YoloEngine.LEVEL_X/YoloEngine.display_x; 
		YoloEngine.LEVEL_SIZE_Y = YoloEngine.LEVEL_Y/YoloEngine.display_y; 
		YoloEngine.GAME_PROJECTION_X = YoloEngine.GAME_PROJECTION_Y*YoloEngine.display_x/YoloEngine.display_y;
		
		
		ObjectTab[0] = new YoloObject(0,1330,2400,110);
		ObjectTab[1] = new YoloObject(37, 671, 2301, 115);
		ObjectTab[2] = new YoloObject(209,495,383,53);
		ObjectTab[3] = new YoloObject(809,1175,383,53);
		ObjectTab[4] = new YoloObject(199,1185,130,43);
		ObjectTab[5] = new YoloObject(89, 889, 130, 43);
		ObjectTab[6] = new YoloObject(1394, 465, 130, 43);
		ObjectTab[7] = new YoloObject(1394, 220, 130, 43);
		ObjectTab[8] = new YoloObject(1705, 545, 130, 43);
		ObjectTab[9] = new YoloObject(1845, 95, 130, 43);
		ObjectTab[10] = new YoloObject(2000, 380, 130, 43);
		ObjectTab[11] = new YoloObject(2220, 270, 130, 43);
		ObjectTab[12] = new YoloObject(2260, 870, 130, 43);
		ObjectTab[13] = new YoloObject(210, 145 ,383, 53);
		ObjectTab[14] = new YoloObject(739, 319, 516, 96);
		ObjectTab[15] = new YoloObject(370, 990, 516, 96);
		ObjectTab[16] = new YoloObject(1113, 990, 1020, 122);
	
		LaddreTab[0]= new YoloObject(552, 659, 81, 316);
		LaddreTab[1]= new YoloObject(1388, 666, 85, 311);
		LaddreTab[2]= new YoloObject(1808, 988, 82, 321);
		LaddreTab[3]= new YoloObject(1876, 87, 81, 580);
		
		
		
//-----------------------------------------------------------------------------------------------------------		
	}

}
