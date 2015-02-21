package com.example.yolo_fighter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.view.MotionEvent;

public class YoloGame extends Activity{

	public YoloGameView gameView;
	
	public static Context context ;
	
	public static float x=0,y=0, x_old=YoloEngine.MOVE_X,y_old,x2,y2,y_climb;
	private int c;
	private float x_skill,y_skill;
    private Skill newSkill;
    public static int flying=2;
	private float buttonJumpX,buttonJumpY,buttonShotX,buttonShotY,buttonCrouchX,buttonCrouchY,buttonSkillX,buttonSkillY;
	
	private void ActionUp()
	{
		if(!YoloEngine.isClimbing)
		{
			YoloEngine.TeamAB[YoloEngine.MyID].isClimbingUp = true;
			YoloEngine.isClimbing = true;
		}
		if(!YoloEngine.TeamAB[YoloEngine.MyID].isJumping)
		{
			if(flying-- > 0)
			{
				YoloEngine.TeamAB[YoloEngine.MyID].vy = 0.25f;
				YoloEngine.TeamAB[YoloEngine.MyID].isJumping = true;
			}		
		}
		YoloEngine.TeamAB[YoloEngine.MyID].isCrouch = false;
	}
	
	private void ActionDown()
	{

		if(!YoloEngine.isClimbing)
		{
			YoloEngine.TeamAB[YoloEngine.MyID].isClimbingDown = true;
			YoloEngine.isClimbing = true;
		}
		else	
		if(!YoloEngine.isCrouch_prest)
			if(YoloEngine.TeamAB[YoloEngine.MyID].isCrouch)
				YoloEngine.TeamAB[YoloEngine.MyID].isCrouch = false;
			else
				YoloEngine.TeamAB[YoloEngine.MyID].isCrouch = true;
		YoloEngine.isCrouch_prest = true;
	}
	
	private void ActionMoveX()
	{
		if(YoloEngine.TeamAB[YoloEngine.MyID].isCrouch)	
		{
			if(Math.abs(x_old - x2) < 40)//YoloEngine.MAX_VALUE_PLAYER_SPEED/2 )
			{
				YoloEngine.TeamAB[YoloEngine.MyID].vx = ((x2-x_old)*(x2-x_old)*Math.signum(x2-x_old))/60000;
			}
			else
			{
				YoloEngine.TeamAB[YoloEngine.MyID].vx = 0.08f*Math.signum(x2-x_old);
				if(Math.signum(x2-x_old)>0) x2 = x_old + 40;
				else x2 = x_old - 40;
			}
		}
		else
		{
			if(Math.abs(x_old - x2) < 40)//YoloEngine.MAX_VALUE_PLAYER_SPEED )
			{
				YoloEngine.TeamAB[YoloEngine.MyID].vx = ((x2-x_old)*(x2-x_old)*Math.signum(x2-x_old))/30000;
			}
			else
			{
				YoloEngine.TeamAB[YoloEngine.MyID].vx = 0.16f*Math.signum(x2-x_old);
				if(Math.signum(x2-x_old)>0) x2 = x_old + 40;
				else x2 = x_old - 40;
			}
		}
	}
	
	private void ActionClick()
	{
		 if(x < 275)
			{
			    x_old = x; y_old = YoloEngine.display_y-y;
			 	x2=YoloEngine.isClasic?x:0;
				y2=0;
				YoloEngine.TeamAB[YoloEngine.MyID].isMoving = true;
			}
			else
				{
					if(YoloEngine.isClasic)
					{
						if(x > buttonJumpX - 50 && x < buttonJumpX + 25 + YoloEngine.BUTTON_JUMP_SIZE )
							if(y < buttonJumpY + 50 && y > buttonJumpY - 50 - YoloEngine.BUTTON_JUMP_SIZE )
							{
								ActionUp();
							}
						
						if(x > buttonCrouchX && x < buttonCrouchX + YoloEngine.BUTTON_JUMP_SIZE )
							if(y < buttonCrouchY && y > buttonCrouchY - YoloEngine.BUTTON_JUMP_SIZE )
							{
								YoloEngine.isCrouch_prest = true;
								if(YoloEngine.TeamAB[YoloEngine.MyID].isCrouch)
									YoloEngine.TeamAB[YoloEngine.MyID].isCrouch = false;
								else
									YoloEngine.TeamAB[YoloEngine.MyID].isCrouch = true;
							}
					}
					if(x > buttonShotX - 50 && x < buttonShotX + 50 + YoloEngine.BUTTON_JUMP_SIZE )
						if(y < buttonShotY + 50 && y > buttonShotY - 50 - YoloEngine.BUTTON_JUMP_SIZE )
						{
							YoloEngine.TeamAB[YoloEngine.MyID].isShoting = true;
						}
					if(x > buttonSkillX - 150 && x < buttonSkillX - 150 + YoloEngine.BUTTON_JUMP_SIZE )
						if(y < buttonSkillY + 50 && y > buttonSkillY - 50 - YoloEngine.BUTTON_JUMP_SIZE )
						{
							if(!YoloEngine.isSkillPressed && YoloEngine.TeamAB[YoloEngine.MyID].canSkill1)
							{
								//YoloEngine.canSkill1 = false;
								YoloEngine.isSkillPressed = true;
								YoloEngine.usedSkill = 0;
								if(YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill){YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill = false;YoloEngine.isSkillPressed = false;}
							}
						}
					if(x > buttonSkillX - 50 && x < buttonSkillX - 50 + YoloEngine.BUTTON_JUMP_SIZE )
						if(y < buttonSkillY + 50 && y > buttonSkillY - 50 - YoloEngine.BUTTON_JUMP_SIZE )
						{
							if(!YoloEngine.isSkillPressed && YoloEngine.TeamAB[YoloEngine.MyID].canSkill2)
							{
								//YoloEngine.canSkill2 = false;
								YoloEngine.isSkillPressed = true;
								YoloEngine.usedSkill = 1;
								if(YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill){YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill = false;YoloEngine.isSkillPressed = false;}
							}
						}
					if(x > buttonSkillX + 50 && x < buttonSkillX + 50 + YoloEngine.BUTTON_JUMP_SIZE )
						if(y < buttonSkillY + 50 && y > buttonSkillY - 50 - YoloEngine.BUTTON_JUMP_SIZE )
						{
							if(!YoloEngine.isSkillPressed && YoloEngine.TeamAB[YoloEngine.MyID].canSkill3)
							{
								//YoloEngine.canSkill3 = false;
								YoloEngine.isSkillPressed = true;
								YoloEngine.usedSkill = 2;
								if(YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill){YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill = false;YoloEngine.isSkillPressed = false;}
							}
						}	
				}
		 if(YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill) 
			{
			 x_skill = (x/YoloEngine.TX)/YoloEngine.LEVEL_scale ;
			 y_skill = ((YoloEngine.display_y-y)/YoloEngine.TY)/YoloEngine.LEVEL_scale;
			 
			 switch (YoloEngine.usedSkill)
				{
				case 0:
                    newSkill = new Skill(x_skill,y_skill,YoloEngine.SkillSprite1,!YoloEngine.TeamAB[YoloEngine.MyID].playerTeam);
                    if(!YoloEngine.TeamAB[YoloEngine.MyID].playerTeam==YoloEngine.TeamA)
                    	YoloGameRenderer.skillTeamAVe.add(newSkill);
                    else
                    	YoloGameRenderer.skillTeamBVe.add(newSkill);
                    YoloEngine.TeamAB[YoloEngine.MyID].canSkill1 = false;
                    break;
				case 1:
                    newSkill = new Skill(x_skill,y_skill,YoloEngine.SkillSprite2,!YoloEngine.TeamAB[YoloEngine.MyID].playerTeam);
                    if(!YoloEngine.TeamAB[YoloEngine.MyID].playerTeam==YoloEngine.TeamA)//skill przeciwnika
                    	YoloGameRenderer.skillTeamAVe.add(newSkill);
                    else
                    	YoloGameRenderer.skillTeamBVe.add(newSkill);
                    YoloEngine.TeamAB[YoloEngine.MyID].canSkill2 = false;
					break;
				case 2:
                    newSkill = new Skill(x_skill,y_skill,YoloEngine.SkillSprite3,YoloEngine.TeamAB[YoloEngine.MyID].playerTeam);
                    if(YoloEngine.TeamAB[YoloEngine.MyID].playerTeam==YoloEngine.TeamA)
                    	YoloGameRenderer.skillTeamAVe.add(newSkill);
                    else
                    	YoloGameRenderer.skillTeamBVe.add(newSkill);
                    YoloEngine.TeamAB[YoloEngine.MyID].canSkill3 = false;
					break;
				}
			 YoloEngine.mMultislayer.sendMessageToAllreliable(newSkill.serializeSkill());
			 YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill = false;			
			}
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		if (android.os.Build.VERSION.SDK_INT >= 13)
		{
			Point size = new Point();
		    YoloEngine.display.getSize(size);
		    DisplayMetrics metrics = getResources().getDisplayMetrics();
		    YoloEngine.display_x = size.x;
		    YoloEngine.display_y = size.y;
		    YoloEngine.xdpi = 210f/metrics.xdpi;
		    YoloEngine.ydpi = 210f/metrics.ydpi;
		    YoloEngine.LEVEL_scale *=YoloEngine.xdpi;
		}
		else
		{
			YoloEngine.display_x = YoloEngine.display.getWidth();
			YoloEngine.display_y = YoloEngine.display.getHeight();
		}
		
		if(YoloEngine.test) {
			YoloEngine.TeamAB[0] = new YoloPlayer(3f,10f,YoloEngine.TeamAB[YoloEngine.MyID].playerTeam,0);
			YoloEngine.TeamAB[1] = new YoloPlayer(3f,10f,YoloEngine.TeamAB[YoloEngine.MyID].playerTeam,1);
			YoloEngine.TeamAB[2] = new YoloPlayer(10f,8f,!YoloEngine.TeamAB[YoloEngine.MyID].playerTeam,2);
			YoloEngine.TeamAB[3] = new YoloPlayer(9f,8f,!YoloEngine.TeamAB[YoloEngine.MyID].playerTeam,3);
		}
		super.onCreate(savedInstanceState);
		gameView = new YoloGameView(this);
		setContentView(gameView);
		
		context = getApplicationContext();
	
		YoloEngine.context = context;
		buttonJumpX = ((1/(YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.display_x))-1.5f)*(YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.display_x)*YoloEngine.display_x;
		buttonJumpY = YoloEngine.display_y - 1.5f*(YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.display_y)*YoloEngine.display_y;
		
		buttonShotX = ((1/(YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.display_x))-1.5f)*(YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.display_x)*YoloEngine.display_x;
		buttonShotY = YoloEngine.display_y - .25f*(YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.display_y)*YoloEngine.display_y;
		
		buttonCrouchX = (2.75f)*(YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.display_x)*YoloEngine.display_x;
		buttonCrouchY = YoloEngine.display_y - .25f*(YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.display_y)*YoloEngine.display_y;
		
		buttonSkillX = (1/(YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.display_x)/2)*(YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.display_x)*YoloEngine.display_x;
		buttonSkillY = YoloEngine.display_y;
		
		if(YoloEngine.isClasic){y_old=YoloEngine.display_y-YoloEngine.MOVE_Y+YoloEngine.MOVE_SIZE_Y/2; x = x_old;}
		else{x2=-1000;y2 =-1000;x_old = -1000;y_old =-1000;}
		
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		
		c = event.getActionIndex();
		x = event.getX(c);
		y = event.getY(c);

		if(x < 275)
			{
				x2 = x;
				y2 = YoloEngine.display_y-y;
			}
		
		switch(event.getActionMasked())
		{
		
			case MotionEvent.ACTION_MOVE:
			{
				if(x<275)
				if(YoloEngine.isClasic)
					ActionMoveX();
				else
				{
					x2 = x2-x_old;
					y2 = y2-y_old;
					if(x2*x2 + y2*y2 >1600)
					{
						x2 = (float) ((40*x2)/Math.sqrt(x2*x2+y2*y2));
						y2 = (float) ((40*y2)/Math.sqrt(x2*x2+y2*y2));
					}

					if( y2 < -30)
						ActionDown();	
					else
					{
						YoloEngine.isCrouch_prest = false;
						YoloEngine.isClimbing = false;
					}

					if( y2 > 30)
						ActionUp();
					else 
					{
						YoloEngine.TeamAB[YoloEngine.MyID].isJumping = false;
						YoloEngine.isClimbing = false;
					}
					YoloEngine.TeamAB[YoloEngine.MyID].vx = (x2*x2*Math.signum(x2))/(YoloEngine.TeamAB[YoloEngine.MyID].isCrouch?15000f:7500f);
					
				}
	
				if(YoloEngine.TeamAB[YoloEngine.MyID].vx < 0)
				{
					if(YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft == false)
					{
						YoloEngine.TeamAB[YoloEngine.MyID].x_texture = YoloEngine.TeamAB[YoloEngine.MyID].x_end;
						YoloEngine.TeamAB[YoloEngine.MyID].y_texture = YoloEngine.TeamAB[YoloEngine.MyID].y_end;
					}
					YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft = true;
					YoloEngine.TeamAB[YoloEngine.MyID].setAction(2);
					YoloEngine.TeamAB[YoloEngine.MyID].animation_slowdown = -(int)(2f/YoloEngine.TeamAB[YoloEngine.MyID].vx/6);
				}
				if(YoloEngine.TeamAB[YoloEngine.MyID].vx > 0)
				{
					if(YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft == true)
					{
						YoloEngine.TeamAB[YoloEngine.MyID].x_texture = YoloEngine.TeamAB[YoloEngine.MyID].x_end;
						YoloEngine.TeamAB[YoloEngine.MyID].y_texture = YoloEngine.TeamAB[YoloEngine.MyID].y_end;
					}
					YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft = false;
					YoloEngine.TeamAB[YoloEngine.MyID].setAction(3);
					YoloEngine.TeamAB[YoloEngine.MyID].animation_slowdown = (int)(2f/YoloEngine.TeamAB[YoloEngine.MyID].vx/6);
					
				}
	
				break;
			}
	
			case MotionEvent.ACTION_DOWN:
				ActionClick();
				break;
					
			case MotionEvent.ACTION_POINTER_DOWN:
				ActionClick();
				break;
					
			
			case MotionEvent.ACTION_UP:
				{
					YoloEngine.TeamAB[YoloEngine.MyID].isMoving = false;
					YoloEngine.TeamAB[YoloEngine.MyID].isJumping = false;
					YoloEngine.isCrouch_prest = false;
					if(x > 275)
						YoloEngine.TeamAB[YoloEngine.MyID].isShoting = false;
					x2=-1000;
					y2=-1000;
					x_old = - 1000;
					y_old = - 1000;
					if(YoloEngine.isSkillPressed)
					{
						YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill = true;
						YoloEngine.isSkillPressed = false;
						
					}		
					break;
				}
			case MotionEvent.ACTION_POINTER_UP:
				{					
					if(x < 275)
						YoloEngine.TeamAB[YoloEngine.MyID].isMoving = false;
					else
						YoloEngine.TeamAB[YoloEngine.MyID].isShoting = false;
					
					YoloEngine.TeamAB[YoloEngine.MyID].isJumping = false;
					YoloEngine.isCrouch_prest= false;
					
					x2=-1000;
					y2=-1000;
					x_old = - 1000;
					y_old = - 1000;
					if(YoloEngine.isSkillPressed)
					{
						YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill = true;
						YoloEngine.isSkillPressed = false;
					}
					break;
				}
		}
		return false;
	}
	
	@Override
	protected void onResume(){
		YoloEngine.TeamAB[YoloEngine.MyID].x = 3;
		YoloEngine.TeamAB[YoloEngine.MyID].y = 5f;
		YoloGameRenderer.first = false;
		YoloGameRenderer.toLoad = true;
		super.onResume();
		gameView.onResume();
	}
	@Override
	protected void onPause()
	{
		YoloEngine.TeamAB[YoloEngine.MyID].x = 3;
		YoloEngine.TeamAB[YoloEngine.MyID].y = 5f;
		YoloGameRenderer.first = false;
		YoloGameRenderer.toLoad = true;
		super.onPause();
		gameView.onPause();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		
			YoloEngine.whichLayout=0;
			YoloGameRenderer.skillTeamBVe.clear();
			YoloGameRenderer.skillTeamAVe.clear();
			Intent mainMenu = new Intent(getApplicationContext(),YoloMainMenu.class);
			YoloGame.this.startActivity(mainMenu);
			YoloEngine.context = getApplicationContext();
			YoloGame.this.finish();
			//TODO MiHu path to variable YoloEngine.TeamAB[YoloEngine.MyID].coin TO JEST TYLKO ROZNICA NIE WARTOSC
			setContentView(R.layout.main_menu);
	}
}
