package com.example.yolo_fighter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;

import android.view.MotionEvent;

public class YoloGame extends Activity{

	public YoloGameView gameView;
	
	public static Context context ;
	
	public static float x=0,y=0, x_old=YoloEngine.MOVE_X,y_old,x2,y2,y_climb;
	private int c;
	private float x_skill,y_skill;

	private float buttonJumpX,buttonJumpY,buttonShotX,buttonShotY,buttonCrouchX,buttonCrouchY,buttonSkillX,buttonSkillY;
	
	private void ActionUp()
	{
		if(YoloEngine.canClimb)
		{
			if(!YoloEngine.isClimbing)
			{
				YoloEngine.canMove =  false;
				YoloEngine.isClimbingUp = true;
			}
			YoloEngine.isClimbing = true;
		}
		if(!YoloEngine.isJumping)
		{
			YoloEngine.Player_vy = 0.25f;
			YoloEngine.isJumping = true;
			
		}
		YoloEngine.isCrouch = false;
	}
	
	private void ActionDown()
	{
		if(YoloEngine.canClimb)
		{
			if(!YoloEngine.isClimbing)
			{
				YoloEngine.canMove =  false;
				YoloEngine.isClimbingDown = true;
			}
			YoloEngine.isClimbing = true;
		}
		else
		{
			if(!YoloEngine.isCrouch_prest)
				if(YoloEngine.isCrouch)
					YoloEngine.isCrouch = false;
				else
					YoloEngine.isCrouch = true;
			YoloEngine.isCrouch_prest = true;
		}
	}
	
	private void ActionMoveX()
	{
		if(YoloEngine.isCrouch)	
		{
			if(Math.abs(x_old - x2) < 40)//YoloEngine.MAX_VALUE_PLAYER_SPEED/2 )
			{
				YoloEngine.Player_vx = ((x2-x_old)*(x2-x_old)*Math.signum(x2-x_old))/60000;
			}
			else
			{
				YoloEngine.Player_vx = 0.08f*Math.signum(x2-x_old);
				if(Math.signum(x2-x_old)>0) x2 = x_old + 40;
				else x2 = x_old - 40;
			}
		}
		else
		{
			if(Math.abs(x_old - x2) < 40)//YoloEngine.MAX_VALUE_PLAYER_SPEED )
			{
				YoloEngine.Player_vx = ((x2-x_old)*(x2-x_old)*Math.signum(x2-x_old))/30000;
			}
			else
			{
				YoloEngine.Player_vx = 0.16f*Math.signum(x2-x_old);
				if(Math.signum(x2-x_old)>0) x2 = x_old + 40;
				else x2 = x_old - 40;
			}
		}
	}
	
	private void ActionClick()
	{
		 if(x < 275)
			{
				y2=0;
				YoloEngine.isMoving = true;
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
								if(YoloEngine.isCrouch)
									YoloEngine.isCrouch = false;
								else
									YoloEngine.isCrouch = true;
							}
					}
					if(x > buttonShotX - 50 && x < buttonShotX + 50 + YoloEngine.BUTTON_JUMP_SIZE )
						if(y < buttonShotY + 50 && y > buttonShotY - 50 - YoloEngine.BUTTON_JUMP_SIZE )
						{
							YoloEngine.isShoting = true;
						}
					if(x > buttonSkillX - 150 && x < buttonSkillX - 150 + YoloEngine.BUTTON_JUMP_SIZE )
						if(y < buttonSkillY + 50 && y > buttonSkillY - 50 - YoloEngine.BUTTON_JUMP_SIZE )
						{
							if(!YoloEngine.isSkillPressed)
							{
								YoloEngine.canSkill1 = false;
								YoloEngine.isSkillPressed = true;
								YoloEngine.usedSkill = 0;
								if(YoloEngine.isUsingSkill){YoloEngine.isUsingSkill = false;YoloEngine.isSkillPressed = false;}
							}
						}
					if(x > buttonSkillX - 50 && x < buttonSkillX - 50 + YoloEngine.BUTTON_JUMP_SIZE )
						if(y < buttonSkillY + 50 && y > buttonSkillY - 50 - YoloEngine.BUTTON_JUMP_SIZE )
						{
							if(!YoloEngine.isSkillPressed)
							{
								YoloEngine.canSkill2 = false;
								YoloEngine.isSkillPressed = true;
								YoloEngine.usedSkill = 1;
								if(YoloEngine.isUsingSkill){YoloEngine.isUsingSkill = false;YoloEngine.isSkillPressed = false;}
							}
						}
					if(x > buttonSkillX + 50 && x < buttonSkillX + 50 + YoloEngine.BUTTON_JUMP_SIZE )
						if(y < buttonSkillY + 50 && y > buttonSkillY - 50 - YoloEngine.BUTTON_JUMP_SIZE )
						{
							if(!YoloEngine.isSkillPressed)
							{
								YoloEngine.canSkill3 = false;
								YoloEngine.isSkillPressed = true;
								YoloEngine.usedSkill = 2;
								if(YoloEngine.isUsingSkill){YoloEngine.isUsingSkill = false;YoloEngine.isSkillPressed = false;}
							}
						}	
				}
		 if(YoloEngine.isUsingSkill) 
			{
			 x_skill = (x/YoloEngine.display_x)*YoloEngine.GAME_PROJECTION_X ;
			 y_skill = ((YoloEngine.display_y-y)/YoloEngine.display_y)*YoloEngine.GAME_PROJECTION_Y;
			 
			 switch (YoloEngine.usedSkill)
				{
				case 0:
					YoloGameRenderer.skillPlayerVe.add(new Skill(x_skill,y_skill,YoloEngine.SkillSprite1,0,.375f,.875f,20f,20f,57f));
					YoloEngine.mMultislayer.sendMessageToAllreliable((x_skill+"|"+y_skill+"|"+YoloEngine.SkillSprite1+"|"+0+"|"+.375f+"|"+.875f+"|"+5f+"|"+4f).getBytes());
					break;
				case 1:
					YoloGameRenderer.skillPlayerVe.add(new Skill(x_skill,y_skill,YoloEngine.SkillSprite2,YoloEngine.animationSlowdown2,.125f, .375f,3f,3f,YoloEngine.animationDuration2));
					YoloEngine.mMultislayer.sendMessageToAllreliable((x_skill+"|"+y_skill+"|"+YoloEngine.SkillSprite2+"|"+0+"|"+.125f+"|"+ .375f+"|"+3f+"|"+3f).getBytes());
					break;
				case 2:
					YoloGameRenderer.skillPlayerVe.add(new Skill(x_skill,y_skill,YoloEngine.SkillSprite3,YoloEngine.animationSlowdown3,0,0,20f,20f,YoloEngine.animationDuration3));		
					YoloEngine.mMultislayer.sendMessageToAllreliable((x_skill+"|"+y_skill+"|"+YoloEngine.SkillSprite3+"|"+0+"|"+.125f+"|"+ .375f+"|"+3f+"|"+3f).getBytes());
					break;
				}
				YoloEngine.isUsingSkill = false;			
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
		    YoloEngine.display_x = size.x;
		    YoloEngine.display_y = size.y;
		}
		else
		{
			YoloEngine.display_x = YoloEngine.display.getWidth();
			YoloEngine.display_y = YoloEngine.display.getHeight();
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
		
		if(YoloEngine.isClasic)	y_old=YoloEngine.display_y-YoloEngine.MOVE_Y+YoloEngine.MOVE_SIZE_Y/2;
		else y_old = YoloEngine.display_y - 105;
		
		x2=x_old;
		
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
				y2 = y;
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
					ActionMoveX();
					
					y2 = y_old-y2;
						
					if(y2 < 0)
					{
						if(y2 < - 40)//YoloEngine.MAX_VALUE_PLAYER_SPEED)
							if(Math.sqrt(1600-(x2-x_old)*(x2-x_old)) < -y2)
								y2 = -(float)Math.sqrt(1600-(x2-x_old)*(x2-x_old));
							
						if( y2 < -20)//YoloEngine.MAX_VALUE_PLAYER_SPEED /2)
							ActionDown();	
						else
						{
							YoloEngine.isCrouch_prest = false;
							YoloEngine.isClimbing = false;
						}
					}
					else
					{
						//System.out.println(x2);
						if(y2 > 40)//YoloEngine.MAX_VALUE_PLAYER_SPEED-80)
							if(Math.sqrt(1600-(x2-x_old)*(x2-x_old))<y2)
								y2 = (float)Math.sqrt(1600-(x2-x_old)*(x2-x_old));
							
						if( y2 > 20)//YoloEngine.MAX_VALUE_PLAYER_SPEED /2)
							ActionUp();
						else 
						{
							YoloEngine.isJumping = false;
							YoloEngine.isClimbing = false;
						}
					}
				}
	
				if(YoloEngine.Player_vx < 0) YoloEngine.isPlayerLeft = true;
				else YoloEngine.isPlayerLeft = false;
	
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
					YoloEngine.isMoving = false;
					YoloEngine.isJumping = false;
					YoloEngine.isCrouch_prest = false;
					if(x > 275)
						YoloEngine.isShoting = false;
					x2=x_old;
					y2=0;
					if(YoloEngine.isSkillPressed)
					{
						YoloEngine.isUsingSkill = true;
						YoloEngine.isSkillPressed = false;
						
					}		
					break;
				}
			case MotionEvent.ACTION_POINTER_UP:
				{					
					if(x < 275)
						YoloEngine.isMoving = false;
					else
						YoloEngine.isShoting = false;
					
					YoloEngine.isJumping = false;
					YoloEngine.isCrouch_prest= false;
					
					x2=x_old;
					y2=0;
					
					if(YoloEngine.isSkillPressed)
					{
						YoloEngine.isUsingSkill = true;
						YoloEngine.isSkillPressed = false;
					}
					break;
				}
		}
		return false;
	}
	
	@Override
	protected void onResume(){
		YoloEngine.Player_x = 0;
		YoloEngine.Player_y = 3f;
		super.onResume();
		gameView.onResume();
	}
	@Override
	protected void onPause()
	{
		YoloEngine.Player_x = 0;
		YoloEngine.Player_y = 3f;
		super.onPause();
		gameView.onPause();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}
