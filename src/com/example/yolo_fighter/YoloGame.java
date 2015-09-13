package com.example.yolo_fighter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public class YoloGame extends Activity{

	public YoloGameView gameView;
	
	public static Context context ;
	
	public static float x=0,y=0, x_old=YoloEngine.MOVE_X,y_old,x_old3=YoloEngine.MOVE_X,y_old3,x2,y2,x3,y3,y_climb,vxbuff;
	private int c;
	private float degree=0;
	private float x_skill,y_skill;
    	private Skill newSkill;
    	public static int flying=2;
	private float buttonJumpX,buttonJumpY,buttonShotX,buttonShotY,buttonCrouchX,buttonCrouchY,buttonSkillX,buttonSkillY;
	private int pointerCount,pointer2=-1,pointer3=-1;
	private boolean dashHelp = true;
	public static int doubleTap = 0,lastMovePointer2 =-1,currentMovePointer2 =-1;
	private float xRadiusLadder = 2, yRadiusLadder = 2;
	
	private void detectClimb()
	{
		for(int q=0;q<YoloGameRenderer.LaddreTab.length;q++)
		{
			if(YoloEngine.TeamAB[YoloEngine.MyID].x + xRadiusLadder/2 + .5f> YoloGameRenderer.LaddreTab[q].x && YoloEngine.TeamAB[YoloEngine.MyID].x - xRadiusLadder/2 +.5f < YoloGameRenderer.LaddreTab[q].x + YoloGameRenderer.LaddreTab[q].dx)
			{
				if(YoloEngine.TeamAB[YoloEngine.MyID].y + yRadiusLadder/2 +.5f> YoloGameRenderer.LaddreTab[q].y && YoloEngine.TeamAB[YoloEngine.MyID].y - yRadiusLadder/2 + .5f < YoloGameRenderer.LaddreTab[q].y + YoloGameRenderer.LaddreTab[q].dy)
				{
					if(YoloEngine.TeamAB[YoloEngine.MyID].y > YoloGameRenderer.LaddreTab[q].y + YoloGameRenderer.LaddreTab[q].dy/2)
					{
						
						if(!YoloEngine.isClimbing)
						{	
							YoloEngine.isClimbing = true;
							YoloEngine.TeamAB[YoloEngine.MyID].isClimbingDown = true;
							YoloEngine.TeamAB[YoloEngine.MyID].x = YoloGameRenderer.LaddreTab[q].x + 0.01f;
							YoloEngine.isDoubleTaped = true;
						}
						break;
					}
					else
					{
						
						if(!YoloEngine.isClimbing)
						{	
							YoloEngine.isClimbing = true;
							YoloEngine.TeamAB[YoloEngine.MyID].isClimbingUp = true;
							YoloEngine.TeamAB[YoloEngine.MyID].x = YoloGameRenderer.LaddreTab[q].x + 0.01f;
							YoloEngine.isDoubleTaped = true;
						}	
						break;
					}
				}
			}
		}
	}
	
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
				if(!YoloEngine.TeamAB[YoloEngine.MyID].isJumping)
				{
					YoloEngine.TeamAB[YoloEngine.MyID].isJumping = true;
					YoloEngine.TeamAB[YoloEngine.MyID].setAction(6);
					YoloEngine.sp.play(YoloEngine.SoundInd[4], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
				}
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
			if(Math.abs(x_old - x2) < 40/YoloEngine.xdpi)//YoloEngine.MAX_VALUE_PLAYER_SPEED/2 )
			{
				YoloEngine.TeamAB[YoloEngine.MyID].vx = ((x2-x_old)*(x2-x_old)*Math.signum(x2-x_old))/60000;
			}
			else
			{
				YoloEngine.TeamAB[YoloEngine.MyID].vx = 0.08f*Math.signum(x2-x_old);
				if(Math.signum(x2-x_old)>0) x2 = x_old + 40/YoloEngine.xdpi;
				else x2 = x_old - 40/YoloEngine.xdpi;
			}
		}
		else
		{
			if(Math.abs(x_old - x2) < 40/YoloEngine.xdpi)//YoloEngine.MAX_VALUE_PLAYER_SPEED )
			{
				YoloEngine.TeamAB[YoloEngine.MyID].vx = ((x2-x_old)*(x2-x_old)*Math.signum(x2-x_old))/30000;
			}
			else
			{
				YoloEngine.TeamAB[YoloEngine.MyID].vx = 0.16f*Math.signum(x2-x_old);
				if(Math.signum(x2-x_old)>0) x2 = x_old + 40/YoloEngine.xdpi;
				else x2 = x_old - 40/YoloEngine.xdpi;
			}
		}
	}
	
	private void ActionClick(int id)
	{
		if(x > buttonSkillX - 150/YoloEngine.xdpi && x < buttonSkillX - 150/YoloEngine.xdpi + YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.xdpi &&
			y < buttonSkillY + 10/YoloEngine.xdpi && y > buttonSkillY - 50/YoloEngine.xdpi - YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.xdpi )
			{
				if(!YoloEngine.isSkillPressed && YoloEngine.TeamAB[YoloEngine.MyID].canSkill1)
				{
					//YoloEngine.canSkill1 = false;
					YoloEngine.isSkillPressed = true;
					YoloEngine.usedSkill = 0;
					if(YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill){YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill = false;YoloEngine.isSkillPressed = false;}
				}
			}
			else if(x > buttonSkillX - 50/YoloEngine.xdpi && x < buttonSkillX - 50/YoloEngine.xdpi + YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.xdpi &&
			y < buttonSkillY + 10/YoloEngine.xdpi && y > buttonSkillY - 50/YoloEngine.xdpi - YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.xdpi )
			{
				if(!YoloEngine.isSkillPressed && YoloEngine.TeamAB[YoloEngine.MyID].canSkill2)
				{
					//YoloEngine.canSkill2 = false;
					YoloEngine.isSkillPressed = true;
					YoloEngine.usedSkill = 1;
					if(YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill){YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill = false;YoloEngine.isSkillPressed = false;}
				}
			}
			else if(x > buttonSkillX + 50/YoloEngine.xdpi && x < buttonSkillX + 50/YoloEngine.xdpi + YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.xdpi &&
			y < buttonSkillY + 10/YoloEngine.xdpi && y > buttonSkillY - 50/YoloEngine.xdpi - YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.xdpi )
			{
				if(!YoloEngine.isSkillPressed && YoloEngine.TeamAB[YoloEngine.MyID].canSkill3)
				{
					//YoloEngine.canSkill3 = false;
					YoloEngine.isSkillPressed = true;
					YoloEngine.usedSkill = 2;
					if(YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill){YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill = false;YoloEngine.isSkillPressed = false;}
				}
			}	
			else if(x < YoloEngine.display_x/2)
			{
				if(pointer2 <= -1)
				{
					pointer2 = id;
				    x_old = x; y_old = YoloEngine.display_y-y;
				 	x2=YoloEngine.isClasic?x:0;
					y2=0;
					YoloEngine.TeamAB[YoloEngine.MyID].isMoving = true;
					if(doubleTap >= 1 && lastMovePointer2 ==-1)
						detectClimb();
					doubleTap++;
				}
			}
			else if(x > YoloEngine.display_x/2)
			{
				if(pointer3 <= -1)
				{
					pointer3 = id;
				 	x_old3 = x; y_old3 = YoloEngine.display_y-y;
				 	x3=YoloEngine.isClasic?x:0;
					y3=0;
					YoloEngine.TeamAB[YoloEngine.MyID].isShoting = true;
				}
			}
			
		 if(YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill) 
			{
			 x_skill = (x/YoloEngine.TX)/YoloEngine.LEVEL_scale ;
			 y_skill = ((YoloEngine.display_y-y)/YoloEngine.TY)/YoloEngine.LEVEL_scale;
			 
			 switch (YoloEngine.usedSkill)
				{
				case 0:
                    newSkill = new Skill(x_skill,y_skill,YoloEngine.SkillSprite1,YoloEngine.TeamAB[YoloEngine.MyID].playerTeam,-1);
                    if(YoloEngine.TeamAB[YoloEngine.MyID].playerTeam==YoloEngine.TeamA)
                    	YoloGameRenderer.skillTeamAVe.add(newSkill);
                    else
                    	YoloGameRenderer.skillTeamBVe.add(newSkill);
                    YoloEngine.TeamAB[YoloEngine.MyID].canSkill1 = false;
                    break;
				case 1:
					newSkill = new Skill(x_skill,y_skill,YoloEngine.SkillSprite2,YoloEngine.TeamAB[YoloEngine.MyID].playerTeam,-1);
                    if(YoloEngine.TeamAB[YoloEngine.MyID].playerTeam==YoloEngine.TeamA)
                    	YoloGameRenderer.skillTeamAVe.add(newSkill);
                    else
                    	YoloGameRenderer.skillTeamBVe.add(newSkill);
                    YoloEngine.TeamAB[YoloEngine.MyID].canSkill2 = false;
					break;
				case 2:
					newSkill = new Skill(x_skill,y_skill,YoloEngine.SkillSprite3,YoloEngine.TeamAB[YoloEngine.MyID].playerTeam,-1);
                    if(YoloEngine.TeamAB[YoloEngine.MyID].playerTeam==YoloEngine.TeamA)
                    	YoloGameRenderer.skillTeamAVe.add(newSkill);
                    else
                    	YoloGameRenderer.skillTeamBVe.add(newSkill);
                    YoloEngine.TeamAB[YoloEngine.MyID].canSkill3 = false;
					break;
				}
			 if(newSkill.sprite != 19 && newSkill.sprite != 26 && newSkill.sprite != 30)
				 YoloEngine.mMultislayer.sendMessageToAllreliable(newSkill.serializeSkillNew());
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
		    YoloEngine.xdpi = 210f/metrics.densityDpi;
		    YoloEngine.LEVEL_scale =(YoloEngine.ydpi/YoloEngine.xdpi);
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
		
		buttonShotX = YoloEngine.display_x-YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.xdpi-25/YoloEngine.xdpi;
		buttonShotY = YoloEngine.display_y - 25/YoloEngine.xdpi;

		buttonCrouchX = 2.75f*YoloEngine.BUTTON_JUMP_SIZE;
		buttonCrouchY = YoloEngine.display_y - .25f*(YoloEngine.BUTTON_JUMP_SIZE/YoloEngine.xdpi);
		
		buttonSkillX = (1/(YoloEngine.BUTTON_JUMP_SIZE*YoloEngine.xdpi/YoloEngine.display_x)/2)*(YoloEngine.BUTTON_JUMP_SIZE*YoloEngine.xdpi/YoloEngine.display_x)*YoloEngine.display_x;
		buttonSkillY = YoloEngine.display_y;
		
		if(YoloEngine.isClasic){y_old=YoloEngine.display_y-YoloEngine.MOVE_Y+YoloEngine.MOVE_SIZE_Y/2; x = x_old;}
		else{x2=-1000;y2 =-1000;x_old = -1000;y_old =-1000;x3=-1000;y3 =-1000;x_old3 = -1000;y_old3 =-1000;}
		
		if (YoloEngine.mGameProperties.gameType != GameProperties.OFFLINE) {
			final ProgressDialog mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setIcon(0);

			Thread t = new Thread() {
				public void run() {
					while (((YoloEngine.startTime - System.currentTimeMillis()) / 1000) > 0) {
						runOnUiThread(new Runnable() {
							public void run() {
								mProgressDialog.setMessage(Long.toString(((YoloEngine.startTime - System.currentTimeMillis()) / 1000)));
								if (!mProgressDialog.isShowing()) 
									mProgressDialog.show();							
							}
						});
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					mProgressDialog.dismiss();
				}

			};
			t.start();
		}
			

	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		
		c = event.getActionIndex();
		x = event.getX(c);
		y = event.getY(c);

	
		switch(event.getActionMasked())
		{
		
			case MotionEvent.ACTION_MOVE:
			{	
				
				pointerCount = event.getPointerCount();
				//if(YoloEngine.TeamAB[YoloEngine.MyID].isMoving || YoloEngine.TeamAB[YoloEngine.MyID].isShoting  )
		        for(int i = 0; i < pointerCount; ++i)
		        {
					x = event.getX(i);
					y = event.getY(i);
					if(x < YoloEngine.display_x/2 && i == pointer2)
					{		
						x2 = x;
						y2 = YoloEngine.display_y-y;
						
						if(YoloEngine.isClasic)
							ActionMoveX();
						else
						{
							x2 = x2-x_old;
							y2 = y2-y_old;
							if(x2*x2 + y2*y2 >1600/YoloEngine.xdpi/YoloEngine.xdpi)
							{
								x2 = (float) ((40/YoloEngine.xdpi*x2)/Math.sqrt(x2*x2+y2*y2));
								y2 = (float) ((40/YoloEngine.xdpi*y2)/Math.sqrt(x2*x2+y2*y2));
							}
							
							if(YoloEngine.TeamAB[YoloEngine.MyID].dashDuration <= 0)
								YoloEngine.TeamAB[YoloEngine.MyID].vx = (x2*x2*Math.signum(x2))/(YoloEngine.TeamAB[YoloEngine.MyID].isCrouch?(30000f/YoloEngine.xdpi/YoloEngine.xdpi):(15000f/YoloEngine.xdpi/YoloEngine.xdpi));
							
							if( y2 < -30/YoloEngine.xdpi)
								ActionDown();	
							else
							{
								YoloEngine.isCrouch_prest = false;
								if(YoloEngine.TeamAB[YoloEngine.MyID].canMove && !YoloEngine.isDoubleTaped)
									YoloEngine.TeamAB[YoloEngine.MyID].isClimbingDown = false;
								if( y2 > 30/YoloEngine.xdpi)
									ActionUp();
								else 
								{
									
									YoloEngine.TeamAB[YoloEngine.MyID].isJumping = false;
									YoloEngine.isClimbing = false;
									if(YoloEngine.TeamAB[YoloEngine.MyID].canMove && !YoloEngine.isDoubleTaped)
										YoloEngine.TeamAB[YoloEngine.MyID].isClimbingUp = false;
								}
								
								if(x2 > 30/YoloEngine.xdpi)
								{
									if(lastMovePointer2 == 0)
									{
										vxbuff = YoloEngine.TeamAB[YoloEngine.MyID].vx;
										YoloEngine.TeamAB[YoloEngine.MyID].vx = 0.32f;
										YoloEngine.TeamAB[YoloEngine.MyID].dashDuration = 10;
										YoloEngine.TeamAB[YoloEngine.MyID].isPlayerInvincible = true;
										YoloEngine.TeamAB[YoloEngine.MyID].invice = 10;
										dashHelp =false;
										lastMovePointer2 =-1;
									}
									else if(dashHelp)
										currentMovePointer2 = 0;
								}
								else if(x2 < -30/YoloEngine.xdpi)
								{
									if(lastMovePointer2 == 1)
									{
										vxbuff = YoloEngine.TeamAB[YoloEngine.MyID].vx;
										YoloEngine.TeamAB[YoloEngine.MyID].vx = -0.32f;
										YoloEngine.TeamAB[YoloEngine.MyID].dashDuration = 10;
										YoloEngine.TeamAB[YoloEngine.MyID].isPlayerInvincible = true;
										YoloEngine.TeamAB[YoloEngine.MyID].invice = 10;
										dashHelp =false;
										lastMovePointer2 =-1;
									}
									else if(dashHelp)
										currentMovePointer2 =1;
								}
								else
								{
									if(currentMovePointer2 >= 0)
										lastMovePointer2 = currentMovePointer2;
									currentMovePointer2 =-1;
									dashHelp = true;
								}
							}
							
							
						}
					}
					else if(i == pointer3)
					{
						x3 = x;
						y3 = YoloEngine.display_y-y;
						
						x3 = x3-x_old3;
						y3 = y3-y_old3;
						if(x3*x3 + y3*y3 >1600/YoloEngine.xdpi/YoloEngine.xdpi)
						{
							x3 = (float) ((40/YoloEngine.xdpi*x3)/Math.sqrt(x3*x3+y3*y3));
							y3 = (float) ((40/YoloEngine.xdpi*y3)/Math.sqrt(x3*x3+y3*y3));
						}
						if(x3 != 0 )
							degree = y3/x3;
						else
							degree = 3;
						
						if(y3!=0 && x3 != 0)
						if( y3 >= 0)
						{
							if(degree < -2.4142)
								YoloEngine.TeamAB[YoloEngine.MyID].aim = 2;
							else if(degree < -0.4142)
							{
								YoloEngine.TeamAB[YoloEngine.MyID].aim = 3;
								YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft = true;
							}
							else if(degree < -0)
							{
								YoloEngine.TeamAB[YoloEngine.MyID].aim = 4;
								YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft = true;
							}
							else if(degree < 0.4142)
							{
								YoloEngine.TeamAB[YoloEngine.MyID].aim = 0;
								YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft = false;
							}
							else if(degree < 2.4142)
							{
								YoloEngine.TeamAB[YoloEngine.MyID].aim = 1;
								YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft = false;
							}
							else
								YoloEngine.TeamAB[YoloEngine.MyID].aim = 2;
						}
						else
						{
							if(degree < -2.4142 && !YoloGameRenderer.onGround)
								YoloEngine.TeamAB[YoloEngine.MyID].aim = 6;
							else if(degree < -0.4142)
							{
								YoloEngine.TeamAB[YoloEngine.MyID].aim = 7;
								YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft = false;
							}
							else if(degree <= 0)
							{
								YoloEngine.TeamAB[YoloEngine.MyID].aim = 0;
								YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft = false;
							}
							else if(degree < 0.4142)
							{
								YoloEngine.TeamAB[YoloEngine.MyID].aim = 4;
								YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft = true;
							}
							else if(degree < 2.4142)
							{
								YoloEngine.TeamAB[YoloEngine.MyID].aim = 5;
								YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft = true;
							}
							else if(!YoloGameRenderer.onGround)
								YoloEngine.TeamAB[YoloEngine.MyID].aim = 6;
						}
						
						
					}
		        }
				
				
				if(YoloEngine.TeamAB[YoloEngine.MyID].vx < 0)
				{
					if(YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft)
						YoloEngine.TeamAB[YoloEngine.MyID].setAction(2);
					else 
						YoloEngine.TeamAB[YoloEngine.MyID].setAction(12);
					YoloEngine.TeamAB[YoloEngine.MyID].animation_slowdown = -(int)(2f/YoloEngine.TeamAB[YoloEngine.MyID].vx/6f);
					
				}
				if(YoloEngine.TeamAB[YoloEngine.MyID].vx > 0)
				{
					if(YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft)
						YoloEngine.TeamAB[YoloEngine.MyID].setAction(11);
					else 
						YoloEngine.TeamAB[YoloEngine.MyID].setAction(3);
					YoloEngine.TeamAB[YoloEngine.MyID].animation_slowdown = (int)(2f/YoloEngine.TeamAB[YoloEngine.MyID].vx/6f);
				}
				
				break;
			}
	
			case MotionEvent.ACTION_DOWN:
		    		ActionClick(event.getActionIndex());
				break;
					
			case MotionEvent.ACTION_POINTER_DOWN:
		    		ActionClick(event.getActionIndex());
				break;
					
			
			case MotionEvent.ACTION_UP:
				{

					if(event.getActionIndex() == pointer3)
					{
						pointer3 = -1;
						x3=-1000;
						y3=-1000;
						x_old3 = - 1000;
						y_old3 = - 1000;
						YoloEngine.TeamAB[YoloEngine.MyID].isShoting = false;
					}

					if(event.getActionIndex() == pointer2)
					{
						pointer2 = -1;
						x2=-1000;
						y2=-1000;
						x_old = - 1000;
						y_old = - 1000;
						YoloEngine.TeamAB[YoloEngine.MyID].isMoving = false;
						YoloEngine.TeamAB[YoloEngine.MyID].isJumping = false;
						YoloEngine.isCrouch_prest = false;
					//	lastMovePointer2 =-1;
						currentMovePointer2 = -1;
					}
					
					if(YoloEngine.isSkillPressed)
					{
						YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill = true;
						YoloEngine.isSkillPressed = false;
						
					}		
					break;
				}
			case MotionEvent.ACTION_POINTER_UP:
				{		
					
					if(event.getActionIndex() == pointer3)
					{
						pointer3 = -1;
						x3=-1000;
						y3=-1000;
						x_old3 = - 1000;
						y_old3 = - 1000;
						YoloEngine.TeamAB[YoloEngine.MyID].isShoting = false;
					}
					if(event.getActionIndex() == pointer2)
					{
						pointer2 = -1;
						x2=-1000;
						y2=-1000;
						x_old = - 1000;
						y_old = - 1000;
						YoloEngine.TeamAB[YoloEngine.MyID].isMoving = false;
						YoloEngine.TeamAB[YoloEngine.MyID].isJumping = false;
						YoloEngine.isCrouch_prest = false;
					//	lastMovePointer2 =-1;
						currentMovePointer2 =-1;
					}
					
					if(event.getActionIndex()<pointer2)
						pointer2--;
					if(event.getActionIndex()<pointer3)
						pointer3--;
					
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
		
		new AlertDialog.Builder(this)
		.setTitle(R.string.game_paused)		
		//.setMessage("")
		.setPositiveButton(R.string.return_main_menu, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				YoloEngine.mMultislayer.sendStateChangedMessage(3);
				YoloEngine.whichLayout=0;
				
				Intent mainMenu = new Intent(getApplicationContext(),YoloMainMenu.class);
				YoloGame.this.startActivity(mainMenu);
				YoloEngine.context = getApplicationContext();
				YoloGame.this.finish();
				YoloGameView.renderer.clear();
				//TODO MiHu path to variable YoloEngine.TeamAB[YoloEngine.MyID].coin TO JEST TYLKO ROZNICA NIE WARTOSC
				setContentView(R.layout.main_menu);				
			}
		}).setNegativeButton(R.string.resume_game, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
			}
		}).setIcon(R.drawable.ic_launcher).show();
		
		
			
			
	}
}
