package com.example.yolo_fighter;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

class Skill 
{
	float x=0,y=0;
	float x_texture=0,y_texture=0,xEnd,yEnd,xStart,yStart;
	float SkillADDX=0,SkillADDY = 0;
	float x_radius,y_radius;
	float damage;
	float  vy = 0;
	
	//------------------------(X,Y) PRZECIWNIKA-------------------
	float x_oponnent, y_oponnent ;
	//-------------------------------------------------------------
	
	int sprite;
	int ret,j=0;
	int animation_slowdown;
	int aniSlowCounter = -1;
	int closest =0;
	
	boolean isLeft = false,onGround = false ,haveXY = false;
	boolean isUsed=false;
	
	public Skill(float x, float y,int sprite , int animation_slowdown,float xEnd,float yEnd,float x_radius,float y_radius) 
	{
		
		this.x = x;
		this.y = y;
		this.sprite = sprite;
		this.animation_slowdown = animation_slowdown;
		this.xEnd = xEnd;
		this.yEnd = yEnd;
		this.x_radius = x_radius;
		this.y_radius = y_radius;

		if(sprite == 6)
		{	
			if(YoloEngine.isPlayerLeft)
				x = (YoloEngine.Player_x - 2f)/4f;
			else
				x = (YoloEngine.Player_x + 2f)/4f;
			y = (YoloEngine.Player_y)/4f;
			
		}
	}

	public void setX()
	{
		if(YoloEngine.Player_x > YoloEngine.GAME_PROJECTION_X/2 +.5f && YoloEngine.Player_x < YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X - YoloEngine.GAME_PROJECTION_X/2 - .5f)
			SkillADDX = (((YoloEngine.Player_x-.5f)/YoloEngine.GAME_PROJECTION_X) - .5f)*YoloEngine.GAME_PROJECTION_X;
		else
		{
			SkillADDX = 0; 
			if(YoloEngine.Player_x > YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X - YoloEngine.GAME_PROJECTION_X/2 - .5f)
				x = YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X - (YoloEngine.GAME_PROJECTION_X - x);
		}
		
		x = x + SkillADDX;
	}
	public void setY()
	{
		if(YoloEngine.Player_y > YoloEngine.GAME_PROJECTION_Y/2 + .5f && YoloEngine.Player_y < YoloEngine.LEVEL_SIZE_Y*YoloEngine.GAME_PROJECTION_Y - YoloEngine.GAME_PROJECTION_Y/2 - .5f)
			SkillADDY = (((YoloEngine.Player_y-.5f)/YoloEngine.GAME_PROJECTION_Y) - .5f)*YoloEngine.GAME_PROJECTION_Y;
		else 
		{
			SkillADDY = 0;
			if(YoloEngine.Player_y > YoloEngine.LEVEL_SIZE_Y*YoloEngine.GAME_PROJECTION_Y - YoloEngine.GAME_PROJECTION_Y/2 - .5f)
				y = YoloEngine.LEVEL_SIZE_Y*YoloEngine.GAME_PROJECTION_Y - (YoloEngine.GAME_PROJECTION_Y - y);
		}
		
		y = y + SkillADDY;
	}
	
	public void move ()
	{
		for(int x = 0;x<YoloEngine.mMultislayer.Opponents_x_last.length-1;x++)
			if(Math.abs(x-YoloEngine.mMultislayer.Opponents_y_last[x]) < Math.abs(x-YoloEngine.mMultislayer.Opponents_y_last[x+1]))
					closest = x;
			
		x_oponnent = YoloEngine.mMultislayer.Opponents_x_last[closest]; 
		y_oponnent = YoloEngine.mMultislayer.Opponents_y_last[closest];
		 
		vy -= YoloEngine.GAME_ACCELERATION;
		y += vy;
		
		for(int i = j; i < YoloGameRenderer.ObjectTab.length; i++)
		{
			if(YoloGameRenderer.IsCollidedTop(YoloGameRenderer.ObjectTab[i],x,y,vy))
			{
					y = YoloGameRenderer.ObjectTab[i].max_y;
					vy = 0;
					onGround = true;
					j=i;
				break;
			}
			else onGround = false;
				
		}
		
		
		
		switch(sprite)
		{
		case 6:
			
			if(y_oponnent + 1 >y-y_radius/2 && y_oponnent < y + y_radius/2 && x_oponnent + 1  > x-x_radius/2 && x_oponnent < x+x_radius/2)
			{
				if(ret != YoloEngine.ARCHER_FIRE)
				if(x<x_oponnent)
				{
					isLeft = false;
					x_texture = xStart = 0;
					y_texture = yStart = 0.25f;
					xEnd = .875f;
					yEnd = 0.25f;
				}
				else
				{
					isLeft = true;
					x_texture = xStart = 0;
					y_texture = yStart = 0.125f;
					xEnd = .875f;
					yEnd = 0.125f;
				}
				ret = YoloEngine.ARCHER_FIRE;
			}
			else
			{
				if(!onGround)
				{
					if(ret != YoloEngine.ARCHER_STAND && ret != YoloEngine.ARCHER_FIRE)
					if(x>x_oponnent)
					{
						x_texture = xStart = xEnd = 0.75f;
						y_texture = yStart = yEnd = 0.375f;
					}
					else
					{
						x_texture = xStart = xEnd = 0.875f;
						y_texture = yStart = yEnd = 0.375f;
					}
					ret = YoloEngine.ARCHER_STAND;
				}	
				else
				{
					if(ret != YoloEngine.ARCHER_WALK && ret != YoloEngine.ARCHER_FIRE)
					if(x<x_oponnent)
					{
						//isLeft = true;
						if(x - YoloEngine.ARCHER_SPEED > YoloGameRenderer.ObjectTab[j].min_x)
						{
							x_texture = xStart = 0.5f;
							y_texture = yStart = 0f;
							xEnd = .875f;
							yEnd = 0f;
						}
						
					}
					else
					{
						//isLeft = false;
						if(x + YoloEngine.ARCHER_SPEED<YoloGameRenderer.ObjectTab[j].max_x)
						{
							x_texture = xStart = 0f;
							y_texture = yStart = 0f;
							xEnd = 0.375f;
							yEnd = 0f;
						}
						
					}
					else
					{
						if(Math.abs(x-x_oponnent)<YoloEngine.ARCHER_SPEED)
						{
							x_texture = xStart = xEnd = 0.875f;
							y_texture = yStart = yEnd = 0.375f;
							ret = YoloEngine.ARCHER_STAND;
						}
						else if(x>x_oponnent)
							if(x - YoloEngine.ARCHER_SPEED > YoloGameRenderer.ObjectTab[j].min_x)
								x -= YoloEngine.ARCHER_SPEED;
							else
							{
								x_texture = xStart = xEnd = 0.75f;
								y_texture = yStart = yEnd = 0.375f;
								ret = YoloEngine.ARCHER_STAND;
							}
						else
							if(x + YoloEngine.ARCHER_SPEED < YoloGameRenderer.ObjectTab[j].max_x)
								x += YoloEngine.ARCHER_SPEED;
							else
							{
								x_texture = xStart = xEnd = 0.875f;
								y_texture = yStart = yEnd = 0.375f;
								ret = YoloEngine.ARCHER_STAND;	
							}
						
					}
					ret = YoloEngine.ARCHER_WALK;
				}
				
			}
			
			break;
		}

	}
	
}

public class YoloGameRenderer implements Renderer {
	
	private YoloTexture TextureLoader ;
	private int[] spriteSheets = new int[7];
	private YoloBackground back= new YoloBackground(),load_back=new YoloBackground(),load_front = new YoloBackground();
	private YoloPlayer player = new YoloPlayer();
	private YoloBackground btn_mov = new YoloBackground(),btn_movball = new YoloBackground(); 
	private YoloBackground live_bar_1 = new YoloBackground(),live_bar_0 = new YoloBackground();
	private YoloWeapon btn = new YoloWeapon();
	
	private static Vector<YoloWeapon> Weapontab  = new Vector<YoloWeapon>();
	private static YoloWeapon bullet;
	
	public static Skill[] skilltab = new Skill[3];
	public static Vector<Skill> skillOponentVe = new Vector<Skill>();
	public static Vector<Skill> skillPlayerVe = new Vector<Skill>();

	
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
			jumpBtnX = 1/(MOVE_BALL_SIZE_X*2)-1.5f,shotBtnX = jumpBtnX,crouchBtnX = 2.75f,skillBtnX = 1/(MOVE_BALL_SIZE_X*2)/2,liveBarX_0 = (0.5f/(1f/LIVE_BAR_SIZE_Y))*(1/LIVE_BAR_SIZE_X_0);	
	private float joyBackTX = 0,joyBallTX = 0,BtnTX = 0,liveBarTX = 0,liveBarTX_1 = 0,XADD = 0; 

	private float cameraPosY,jumpBtnY = 1.5f,shotBtnY = .25f,crouchBtnY = .25f ,liveBarY = 1f/LIVE_BAR_SIZE_Y -1.75f;
	private float joyBackYT = 0,joyBallYT = 0,BtnYT = 0,liveBarYT = 0,YADD = 0;
	
//	private float SkillADDX = 0f,SkillADDY=0f;
	private float joyBallX1,joyBallY1;
	
	private boolean toLoad = true,first = false;
	private int loading_faze=0,loadingStepsCout =8;
	
	
	private int nextBullet = 0;
	private boolean onGround = true;
	private int ClimbingOn;
	private int S1cooldown = 0,S2cooldown = 0,S3cooldown = 0,poisoned = 0;
				
	public static YoloObject[] ObjectTab = new YoloObject[17];
	private YoloObject[] LaddreTab = new YoloObject[4];
	
	private long loopStart = 0;
	private long loopEnd = 0;
	private long loopRunTime = 0;

	@Override
	public void onDrawFrame(GL10 gl) {
		loopStart = System.currentTimeMillis();
		//====================================================LOADING===============================================================
		
		if(toLoad && first)
		{
			switch(loading_faze)
			{
			case 0:
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
				drawLoadingSrean(gl, 1f/loadingStepsCout);
				break;
			case 1:
				live_bar_1.loadTexture(gl, YoloEngine.LIVE_BAR_1, YoloEngine.context);
				live_bar_0.loadTexture(gl, YoloEngine.LIVE_BAR_0, YoloEngine.context);
				drawLoadingSrean(gl, 1f/loadingStepsCout);
				break;
			case 2:			
				spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.WEAPON_SPRITE, YoloEngine.context, 0);
				drawLoadingSrean(gl, 2f/loadingStepsCout);
				break;
			case 3:
				spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.BUTTON_TEXTURE, YoloEngine.context, 1);
				drawLoadingSrean(gl, 3f/loadingStepsCout);
				break;
			case 4:
				if(YoloEngine.sprite_load[loading_faze])
				spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.POISON_SKILL, YoloEngine.context, 4);
				drawLoadingSrean(gl, 4f/loadingStepsCout);
				break;
			case 5:
				if(YoloEngine.sprite_load[loading_faze])
				spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.THUNDER_SKILL, YoloEngine.context, 5);
				drawLoadingSrean(gl, 5f/loadingStepsCout);
				break;
			case 6:
				if(YoloEngine.sprite_load[loading_faze])
				spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.ARCHER_SPRITE, YoloEngine.context, 6);
				drawLoadingSrean(gl, 6f/loadingStepsCout);
				break;
				
				//TODO Loading texturek skilli
				
			case 7:
				spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.PLAYER_TEXTURE, YoloEngine.context, 2);
				drawLoadingSrean(gl, 7f/loadingStepsCout);
				break;
			case 8:
				spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.OPPONENT_TEXTURE, YoloEngine.context, 3); // Multislayer
				drawLoadingSrean(gl, 8f/loadingStepsCout);
				break;
			case 9:
				back.loadTexture(gl, YoloEngine.BACKGROUND, YoloEngine.context);
				toLoad = false;
				break;
			}
			loading_faze ++;
		}
		else
		{
			drawLoadingSrean(gl,0.1f);
		}
		first = true;
		if(!toLoad)
		{
			
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
			
	//--------------------------------------------------------PORUSZNIE KAMER¥---------------------------------------------------	
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
						//joyBallTX = XADD /MOVE_BALL_SIZE_X;
						//joyBackTX = XADD / MOVE_SIZE_X;
						liveBarTX = XADD / LIVE_BAR_SIZE_X_0;
						liveBarTX_1 = XADD / LIVE_BAR_SIZE_X_1;
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
	//-------------------------------------------------------SKILLS------------------------------------------------------------
			if(YoloEngine.canSkill1 == false)S1cooldown++;
			if(YoloEngine.canSkill2 == false)S2cooldown++;
			if(YoloEngine.canSkill3 == false)S3cooldown++;
			
			if(YoloEngine.SKILL1_COOLDOWN == S1cooldown){S1cooldown = 0; YoloEngine.canSkill1 = true;}
			if(YoloEngine.SKILL2_COOLDOWN == S2cooldown){S2cooldown = 0; YoloEngine.canSkill2 = true;}
			if(YoloEngine.SKILL3_COOLDOWN == S3cooldown){S3cooldown = 0; YoloEngine.canSkill3 = true;}
			
			if(YoloEngine.isPlayerPoisoned && poisoned != 0)
			{
				poisoned--;
				YoloEngine.PlayerLive -= 0.16f;
			}
			
				
			
	//----------------------------------------------------------------------------------------------------------------------------		
	
// ------------------------- Multislayer BEGIN -----------------------	

			if (YoloEngine.MULTI_ACTIVE) {
				
				YoloEngine.mMultislayer.SendData(YoloEngine.Player_x, YoloEngine.Player_y, YoloEngine.isCrouch);				
				YoloEngine.opponentsNo = YoloEngine.mRoom.getParticipantIds().size()-1;
				
			}
			
			
			for(int i = 0; i < YoloEngine.opponentsNo; i++) { 
				
				if(YoloEngine.changesMade[i] < YoloEngine.MULTI_STEPS) {
					if(YoloEngine.changesMade[i] == 0)
					{
						YoloEngine.Opponents_x[i] = YoloEngine.mMultislayer.Opponents_x_lastX[i];
						YoloEngine.Opponents_y[i] = YoloEngine.mMultislayer.Opponents_y_lastX[i];
						
						YoloEngine.mMultislayer.Opponents_x_lastX[i] = YoloEngine.mMultislayer.Opponents_x_last[i];
						YoloEngine.mMultislayer.Opponents_y_lastX[i] = YoloEngine.mMultislayer.Opponents_y_last[i];
					}
					YoloEngine.Opponents_x[i] += YoloEngine.mMultislayer.Opponents_x_change[i];
					YoloEngine.Opponents_y[i] += YoloEngine.mMultislayer.Opponents_y_change[i];
			
					YoloEngine.changesMade[i]++;
					//if(YoloEngine.changesMade == 5) System.out.println(YoloEngine.mMultislayer.Opponents_x_last[i] - YoloEngine.Opponents_x[i]); 
					//System.out.println(YoloEngine.changesMade);
					

				}
				else 
					;//System.out.println("no new data");
				


			} 
						
// ------------------------- Multislayer END -------------------------
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
			drawPlayerSkills(gl);	
			drawPlayer(gl);
			drawOponentSkills(gl);


			
			
// ------------------------- Multislayer BEGIN -----------------------
			
			for(int i = 0; i < YoloEngine.opponentsNo; i++) 
				drawOponnent(gl, YoloEngine.Opponents_x[i], YoloEngine.Opponents_y[i],YoloEngine.Opponent_isCrouched[i], 3);
		
// ------------------------- Multislayer END -------------------------
			
			
			if(YoloEngine.isShoting)playerFire(0.5f);
			else nextBullet = 0;
			moveBullets(gl);
			
			drawControls(gl);
			drawButtons(gl);
			
			

			
			
		}
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		loopEnd = System.currentTimeMillis();
		loopRunTime = (loopEnd - loopStart);
		
		
		
		
	}
	private boolean IsCollidedTop(YoloObject object)
	{
		if(YoloEngine.Player_x + 1  < object.min_x || YoloEngine.Player_x > object.max_x)return false;
	//	if(YoloEngine.Player_y < object.min_y || YoloEngine.Player_y > object.max_y)return false;
		if(YoloEngine.Player_y < object.max_y && YoloEngine.Player_y - YoloEngine.Player_vy < object.max_y)return false;
		if(YoloEngine.Player_y > object.max_y && YoloEngine.Player_y - YoloEngine.Player_vy > object.max_y)return false;
		return true;
	}
	public static boolean IsCollidedTop(YoloObject object,float x,float y, float vy)
	{
		if(x + 1  < object.min_x || x > object.max_x)return false;
		if(y < object.max_y && y - vy < object.max_y)return false;
		if(y > object.max_y && y - vy > object.max_y)return false;
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
	private boolean IsCollided(Skill skill)
	{
		if(YoloEngine.Player_x + 1  < skill.x*4 || YoloEngine.Player_x > skill.x*4 + skill.x_radius)return false;
		if(YoloEngine.Player_y + 1 < skill.y*4 || YoloEngine.Player_y > skill.y*4 + skill.y_radius) return false;
		
		return true;	
	}
	
	private void drawBullet(GL10 gl, YoloWeapon bullet)
	{
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(1/YoloEngine.GAME_PROJECTION_X*bullet.scale, 1/YoloEngine.GAME_PROJECTION_Y*bullet.scale, 1f);
		gl.glTranslatef(bullet.x/bullet.scale, bullet.y/bullet.scale, 0f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		if(bullet.isLeft)gl.glTranslatef(bullet.x_texture, bullet.y_texture, 0f);
		else gl.glTranslatef(bullet.x_texture + .125f, bullet.y_texture, 0f);
		gl.glColor4f(1f,1f,1f,1f);
		bullet.draw(gl,spriteSheets,bullet.sprite);
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
			
			if(Weapontab.get(i).x < 0)
			{
				Weapontab.remove(i);
				break;
			}
			else if(Weapontab.get(i).x > YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X)
			{
				Weapontab.remove(i);
				break;
			}
			else if(!Weapontab.get(i).isMy && IsCollided(Weapontab.get(i)))
			{
				YoloEngine.PlayerLive -= Weapontab.get(i).damage;
				Weapontab.remove(i);
				break;
			}
			else if(Weapontab.get(i).sprite != 6)
					for (int j = 0 ; j< ObjectTab.length ; j++)
						if (IsCollided(Weapontab.get(i), ObjectTab[j])) 
						{
							Weapontab.remove(i);
							break;
						}
			
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
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, 1f);
		gl.glTranslatef(skillBtnX + BtnTX-1.5f,BtnYT , 0f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		if(YoloEngine.canSkill1)gl.glTranslatef(0, .125f, 0);
		else gl.glTranslatef(.125f, .125f, 0);
		gl.glColor4f(.8f,.8f,.8f,0.5f);
		btn.draw(gl, spriteSheets,1);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, 1f);
		gl.glTranslatef(skillBtnX + BtnTX-.5f,BtnYT , 0f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		if(YoloEngine.canSkill2)gl.glTranslatef(0, .125f, 0);
		else gl.glTranslatef(.125f, .125f, 0);
		gl.glColor4f(.8f,.8f,.8f,0.5f);
		btn.draw(gl, spriteSheets,1);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, 1f);
		gl.glTranslatef(skillBtnX + BtnTX+.5f,BtnYT , 0f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		if(YoloEngine.canSkill3)gl.glTranslatef(0, .125f, 0);
		else gl.glTranslatef(.125f, .125f, 0);
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
	
	private void drawLoadingSrean( GL10 gl,float percet)
	{
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		back.draw(gl);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glTranslatef(1f/8f, 1f/12f, 0f);
		gl.glScalef(1f/1.3f,1f/6f,1f);
		load_back.draw(gl);
		if(percet != 0)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glPopMatrix();
			gl.glLoadIdentity();
			gl.glTranslatef(1f/8f, 1f/12f, 0f);
			gl.glScalef(percet*(1f/1.3f),1f/6f,1f);
			load_front.draw(gl);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
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
			bullet.isMy = true; 
			bullet.sprite = 0;
			bullet.x_texture = 0f;
			bullet.y_texture = 0f;
			bullet.x = YoloEngine.Player_x;
			if(!YoloEngine.isCrouch)	bullet.y = YoloEngine.Player_y + .5f; 
			else bullet.y = YoloEngine.Player_y + .025f; 
			bullet.size = 0.25f;
			bullet.isLeft = YoloEngine.isPlayerLeft;
			Weapontab.add(bullet);
			nextBullet = YoloEngine.PLAYER_BULLET_FREQUENCY;
			
			if(YoloEngine.MULTI_ACTIVE)
				YoloEngine.mMultislayer.sendMessageToAllreliable((YoloEngine.Player_x+"|"+YoloEngine.Player_y+"|"+YoloEngine.isPlayerLeft+"|"+YoloEngine.isCrouch+"|"+"l").getBytes());
		}
		nextBullet--;
		//TODO pociski przeciwnika
		
		
	}
	
	
	public static void OpponentFire(float x, float y, boolean isLeft, boolean isCrouch)
	{
		bullet = new YoloWeapon(0.2f);
		bullet.damage = 10f;
		bullet.isMy = false; 
		bullet.x = x;
		if(!isCrouch)	bullet.y = y + .5f; 
		else bullet.y = y + .025f; 
		bullet.sprite = 0;
		bullet.x_texture = 0f;
		bullet.y_texture = 0f;
		bullet.size = 0.25f;
		bullet.isLeft = isLeft;
		Weapontab.add(bullet);
	}

	
	private void AIFire(float x,float y,boolean isLeft)
	{
		bullet = new YoloWeapon(0.2f);
		bullet.damage = 10f;
		bullet.isMy = true; 
		if(isLeft)
			bullet.x = x-2f;
		else
			bullet.x = x;
		bullet.y = y; 
		bullet.sprite = 6;
		bullet.x_texture = 0f;
		bullet.y_texture = 0.5f;
		bullet.size = 0.25f;
		bullet.scale = 4f;
		bullet.isLeft = isLeft;
		Weapontab.add(bullet);
	}
	
	
	
	
	private void drawPlayerSkills(GL10 gl)
	{
		for(int i = 0; i < skillPlayerVe.size(); i++)
		{
			
			
			switch (skillPlayerVe.elementAt(i).sprite)
			{
			case 6:
				
				if(!skillPlayerVe.elementAt(i).haveXY)
				{
					if(YoloEngine.isPlayerLeft)
						skillPlayerVe.elementAt(i).x = YoloEngine.Player_x - 2f;
					else
						skillPlayerVe.elementAt(i).x = YoloEngine.Player_x + 2f;
			 		skillPlayerVe.elementAt(i).y = YoloEngine.Player_y + 2f;
			 		skillPlayerVe.elementAt(i).haveXY = true;
						
			 	}
				 	
				skillPlayerVe.elementAt(i).move();
					
				skillPlayerVe.elementAt(i).aniSlowCounter++;
				end :
				if(skillPlayerVe.elementAt(i).aniSlowCounter == skillPlayerVe.elementAt(i).animation_slowdown)
				{
					skillPlayerVe.elementAt(i).aniSlowCounter = -1;
					if(skillPlayerVe.elementAt(i).y_texture == skillPlayerVe.elementAt(i).yEnd && skillPlayerVe.elementAt(i).x_texture == skillPlayerVe.elementAt(i).xEnd)
					{
						skillPlayerVe.elementAt(i).x_texture = skillPlayerVe.elementAt(i).xStart;
						skillPlayerVe.elementAt(i).y_texture = skillPlayerVe.elementAt(i).yStart;
						
						if(skillPlayerVe.elementAt(i).ret == YoloEngine.ARCHER_FIRE)
						{
							AIFire(skillPlayerVe.elementAt(i).x, skillPlayerVe.elementAt(i).y, skillPlayerVe.elementAt(i).isLeft);
							skillPlayerVe.elementAt(i).ret = YoloEngine.ARCHER_NOPE;
						}
						
						break end;
					}
							
					skillPlayerVe.elementAt(i).x_texture+=0.125f;
					if(skillPlayerVe.elementAt(i).x_texture >= 1){skillPlayerVe.elementAt(i).y_texture+=0.125f; skillPlayerVe.elementAt(i).x_texture=0f;}
				}
								
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glLoadIdentity();
				gl.glPushMatrix();
				gl.glScalef(1/YoloEngine.GAME_PROJECTION_X*4, 1/YoloEngine.GAME_PROJECTION_Y*4, 1f);
				gl.glTranslatef(skillPlayerVe.elementAt(i).x/4f-.5f, skillPlayerVe.elementAt(i).y/4f-.25f, 0f);
				gl.glColor4f(1f,1f,1f,1f);
				gl.glMatrixMode(GL10.GL_TEXTURE);
				gl.glTranslatef(skillPlayerVe.elementAt(i).x_texture, skillPlayerVe.elementAt(i).y_texture, 0f);
				btn.draw(gl, spriteSheets,skillPlayerVe.elementAt(i).sprite);
				gl.glPopMatrix();
				gl.glLoadIdentity();	
				
				break;
					
			default : 
					
				if(skillPlayerVe.elementAt(i).x_texture==0 && skillPlayerVe.elementAt(i).y_texture==0)
				{
					skillPlayerVe.elementAt(i).setX();
					skillPlayerVe.elementAt(i).setY();
							
				}
		
				skillPlayerVe.elementAt(i).aniSlowCounter++;
				if(skillPlayerVe.elementAt(i).aniSlowCounter == skillPlayerVe.elementAt(i).animation_slowdown)
				{
					skillPlayerVe.elementAt(i).aniSlowCounter = -1;
					if(skillPlayerVe.elementAt(i).x_texture<1)skillPlayerVe.elementAt(i).x_texture+=0.125f;
					else{skillPlayerVe.elementAt(i).y_texture+=0.125f; skillPlayerVe.elementAt(i).x_texture=0f;}
						
					if(skillPlayerVe.elementAt(i).y_texture == skillPlayerVe.elementAt(i).yEnd && skillPlayerVe.elementAt(i).x_texture == skillPlayerVe.elementAt(i).xEnd)
					{
						skillPlayerVe.remove(i);
						i--;
					}
					else
					{
						gl.glMatrixMode(GL10.GL_MODELVIEW);
						gl.glLoadIdentity();
						gl.glPushMatrix();
						gl.glScalef(1/YoloEngine.GAME_PROJECTION_X*4, 1/YoloEngine.GAME_PROJECTION_Y*4, 1f);
						gl.glTranslatef(skillPlayerVe.elementAt(i).x/4f-.5f, skillPlayerVe.elementAt(i).y/4f-.25f, 0f);
						gl.glColor4f(1f,1f,1f,1f);
						gl.glMatrixMode(GL10.GL_TEXTURE);
						gl.glTranslatef(skillPlayerVe.elementAt(i).x_texture, skillPlayerVe.elementAt(i).y_texture, 0f);
						btn.draw(gl, spriteSheets,skillPlayerVe.elementAt(i).sprite);
						gl.glPopMatrix();
						gl.glLoadIdentity();
					}
				}	
				
			break;	
			}
			
			
		
		}
	}
	
	private void drawOponentSkills (GL10 gl)
	{		
		
		for (int i = 0;i<skillOponentVe.size();i++)
		{
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(1/YoloEngine.GAME_PROJECTION_X*4, 1/YoloEngine.GAME_PROJECTION_Y*4, 1f);
			gl.glTranslatef(skillOponentVe.elementAt(i).x/4f-.5f, skillOponentVe.elementAt(i).y/4f-.5f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(skillOponentVe.elementAt(i).x_texture, skillOponentVe.elementAt(i).y_texture, 0f);
			btn.draw(gl, spriteSheets,skillOponentVe.elementAt(i).sprite);
			gl.glPopMatrix();
			gl.glLoadIdentity();	
				
			if(skillOponentVe.elementAt(i).x_texture<1)skillOponentVe.elementAt(i).x_texture+=0.125f;
			else{skillOponentVe.elementAt(i).y_texture+=0.125f; skillOponentVe.elementAt(i).x_texture=0f;}
				
			if(skillOponentVe.elementAt(i).y_texture == skillOponentVe.elementAt(i).yEnd && skillOponentVe.elementAt(i).x_texture == skillOponentVe.elementAt(i).xEnd)
			{
				skillOponentVe.remove(i);
				i--;
			}
			else {

			
			switch (skillOponentVe.elementAt(i).sprite)
			{
			case 4:
				if(IsCollided(skillOponentVe.elementAt(i)))
				{
					poisoned = 300;
					YoloEngine.isPlayerPoisoned = true; 
				}
				break;
			case 5:
				if(IsCollided(skillOponentVe.elementAt(i)))
					YoloEngine.PlayerLive -= 30;
				break;
			}
			}
		}
	}
	
	// ------------------------- Multislayer BEGIN -----------------------
	private void drawOponnent(GL10 gl, float x, float y, boolean isCrouch, int sheetNo)
	{
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		//gl.glTranslatef(YoloEngine.Opponent_x, YoloEngine.Opponent_y, 0f);
		gl.glScalef(1/YoloEngine.GAME_PROJECTION_X, 1/YoloEngine.GAME_PROJECTION_Y, 1f);
		gl.glTranslatef(x, y, 0f);
		gl.glColor4f(1f,1f,1f,1f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		if(isCrouch) gl.glTranslatef(0f, 0f, 0f);
		else gl.glTranslatef(0.125f, 0f, 0f);
		gl.glTranslatef(0f, 0f, 0f);
		player.draw(gl,spriteSheets, sheetNo);
		gl.glPopMatrix();
		gl.glLoadIdentity();
	}
	
	// ------------------------- Multislayer END -------------------------
	
	
	
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
		
//----------------------------------------WCZYTYWANIE TEXTUREK----------------------------------------------
		TextureLoader = new YoloTexture(gl);
		back.loadTexture(gl, R.drawable.aniol_tlo_loading, YoloEngine.context);
		load_back.loadTexture(gl, R.drawable.pasek_back, YoloEngine.context);
		load_front.loadTexture(gl, R.drawable.pasek_wypelnienie, YoloEngine.context);
		
//------------------------------------------INICJOWANIE OBIEKTÓW FIZYCZNYCH----------------------------------		
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
		
		YoloEngine.sprite_load[0] = true;
		YoloEngine.sprite_load[1] = true;
		YoloEngine.sprite_load[2] = true;
		YoloEngine.sprite_load[3] = true;
		
		// Mulstislayer po otrzymaniu XXX
		YoloEngine.sprite_load[YoloEngine.SkillSprite1] = true;//Zale¿y od playera
		YoloEngine.sprite_load[YoloEngine.SkillSprite2] = true;//Zale¿y od playera
		YoloEngine.sprite_load[YoloEngine.SkillSprite3] = true;//Zale¿y od playera
		
		
		
//-----------------------------------------------------------------------------------------------------------		
		
	}

}
