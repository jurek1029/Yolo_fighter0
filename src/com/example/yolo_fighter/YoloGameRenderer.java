package com.example.yolo_fighter;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

class HitBox 
{
	float x,y,x_radius,y_radius,damage,duration,sprite;
	int counter =0;
	boolean isLeft;
	Vector<Integer> hitAIs = new Vector<Integer>();
	HitBox(float x ,float y, float x_radius ,float y_radius ,float damage,float duration,float sprite,float isLeft)//TODO x,y skill zmiemiæ
	{
		this.x =x;
		this.y =y;
		this.x_radius = x_radius;
		this.y_radius = y_radius;
		this.damage = damage;
		this.duration = duration;
		this.isLeft = isLeft==1?true:false;
		this.sprite = sprite;
		
	}
}

class Skill 
{
	float x=0,y=0;
	float x_texture=0,y_texture=0,xEnd,yEnd,xStart,yStart;
	float SkillADDX=0,SkillADDY = 0;
	float x_radius,y_radius;
	int frameCounter =0;
	float frameDuration;
	float damage,life=100f,damage_buffor =0f;
	float  vy = 0;
	
	//------------------------(X,Y) PRZECIWNIKA-------------------
	float x_oponnent, y_oponnent ;
	//-------------------------------------------------------------
	
	int sprite,id;
	int ret=100,j=0;
	int animation_slowdown;
	int aniSlowCounter = -1;
	int closest =0;
	int poison_duration =0,slowDown_duration =0;;
	int fire_rate = 10,fireCounter =0;
	
	boolean isLeft = false,onGround = false ,haveXY = false;
	boolean isUsed=false;
	boolean isPoisoned = false,isSlowDown = false;
	
	public Skill(float x, float y,int sprite , int animation_slowdown,float xEnd,float yEnd,float x_radius,float y_radius,float frameDuration,float life) 
	{
		
		this.x = x;
		this.y = y;
		this.sprite = sprite;
		this.animation_slowdown = animation_slowdown;
		this.xEnd = xEnd;
		this.yEnd = yEnd;
		this.x_radius = x_radius;
		this.y_radius = y_radius;
		this.frameDuration = frameDuration;
		this.life = life;

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
//-------------------------------------------------------------------Szukanie najbli¿szego------------------------------		
		if(ret == 100)
		{
			float minLenght = 10000f,temp;
			
			for(int i=0;i<YoloEngine.mMultislayer.Opponents_x_last.length;i++)
			{
				temp = Math.abs(x-YoloEngine.mMultislayer.Opponents_x_last[i])*Math.abs(x-YoloEngine.mMultislayer.Opponents_x_last[i]) +
						Math.abs(y-YoloEngine.mMultislayer.Opponents_y_last[i])*Math.abs(y-YoloEngine.mMultislayer.Opponents_y_last[i]);
				if(temp<minLenght)
				{
					minLenght = temp;
					closest = i;
				}
			}
			/*
			for(int x = 0;x<YoloEngine.mMultislayer.Opponents_x_last.length-1;x++)
				if(Math.abs(this.x-YoloEngine.mMultislayer.Opponents_y_last[x]) < Math.abs(this.x-YoloEngine.mMultislayer.Opponents_y_last[x+1]))
						closestP = x;
				
			for(int z = 0;z<YoloGameRenderer.skillOponentVe.size()-1;z++)
				if(YoloGameRenderer.skillOponentVe.elementAt(z).sprite >= 6 && YoloGameRenderer.skillOponentVe.elementAt(z).sprite <= 9)
					if(Math.abs(this.x - YoloGameRenderer.skillOponentVe.elementAt(z).x)<Math.abs(this.x - YoloGameRenderer.skillOponentVe.elementAt(z+1).x))
							closestS = z;
			
			if(closestS > YoloGameRenderer.skillOponentVe.size())
				if(Math.abs(this.x - YoloGameRenderer.skillOponentVe.elementAt(closestS).x)<Math.abs(this.x-YoloEngine.mMultislayer.Opponents_y_last[closestP]))
					closest = closestS;
				else
					closest = closestP;
			*/
			x_oponnent = YoloEngine.mMultislayer.Opponents_x_last[closest]; 
			y_oponnent = YoloEngine.mMultislayer.Opponents_y_last[closest];
		 
		}
//--------------------------------------------------------------------GRAWITANCJA-------------------------------------------	
		vy -= YoloEngine.GAME_ACCELERATION;
		y += vy;
		if(sprite!=10)
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
		else
			for(int i = 0; i < YoloGameRenderer.ObjectTab.length; i++)
			{
				if(YoloGameRenderer.IsCollidedTop(YoloGameRenderer.ObjectTab[i],x-1.5f,y,vy))
				{
						y = YoloGameRenderer.ObjectTab[i].max_y;
						vy = 0;
						onGround = true;
					break;
				}
				else onGround = false;	
			}
//--------------------------------------------------------------------------------------------------------------------------
		switch(sprite)
		{
		case 6:
			if(poison_duration > 0)
			{
				life -= 0.16f*YoloEngine.GAME_SKIPED_FRAMES;
				poison_duration-=YoloEngine.GAME_SKIPED_FRAMES;
			}	
			if(slowDown_duration >0)
				slowDown_duration--;
			else
				isSlowDown = false;
			if(ret==YoloEngine.ARCHER_HURT)
			{
				if(isLeft)
				{
					x_texture = xStart = xEnd = 0.125f;
					y_texture = yStart = yEnd = 0.375f;
				}
				else
				{
					x_texture = xStart = xEnd = 0.5f;
					y_texture = yStart = yEnd = 0.375f;
				}
				
				life -= damage_buffor;
				damage_buffor = 0f;
				ret = YoloEngine.ARCHER_NULL;
				
				if(life<0)
				{
					ret=YoloEngine.ARCHER_DYING;
					if(isLeft)
					{
						x_texture = xStart = xEnd = 0.25f;
						y_texture = yStart = yEnd = 0.375f;
					}
					else
					{
						x_texture = xStart = xEnd = 0.625f;
						y_texture = yStart = yEnd = 0.375f;
					}
				}
			}
			else if(y_oponnent + 1 >y-y_radius/2 && y_oponnent < y + y_radius/2 && x_oponnent + 1  > x-x_radius/2 && x_oponnent < x+x_radius/2)
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
						isLeft = true;
						x_texture = xStart = xEnd = 0.75f;
						y_texture = yStart = yEnd = 0.375f;
					}
					else
					{
						isLeft = false;
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
						isLeft = false;
						if(x - YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES > YoloGameRenderer.ObjectTab[j].min_x)
						{
							x_texture = xStart = 0.5f;
							y_texture = yStart = 0f;
							xEnd = .875f;
							yEnd = 0f;
						}
						
					}
					else
					{
						isLeft = true;
						if(x + YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES<YoloGameRenderer.ObjectTab[j].max_x)
						{
							x_texture = xStart = 0f;
							y_texture = yStart = 0f;
							xEnd = 0.375f;
							yEnd = 0f;
						}
						
					}
					else
					{
						if(Math.abs(x-x_oponnent)<YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES)
						{
							x_texture = xStart = xEnd = 0.875f;
							y_texture = yStart = yEnd = 0.375f;
							ret = YoloEngine.ARCHER_STAND;
						}
						else if(x>x_oponnent)
							if(x - YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES > YoloGameRenderer.ObjectTab[j].min_x)
								if(isSlowDown)
									x -= YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x -= YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x = YoloGameRenderer.ObjectTab[j].min_x;
								x_texture = xStart = xEnd = 0.75f;
								y_texture = yStart = yEnd = 0.375f;
								ret = YoloEngine.ARCHER_STAND;
							}
						else
							if(x + YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES < YoloGameRenderer.ObjectTab[j].max_x)
								if(isSlowDown)
									x += YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x += YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x = YoloGameRenderer.ObjectTab[j].max_x-1f;
								x_texture = xStart = xEnd = 0.875f;
								y_texture = yStart = yEnd = 0.375f;
								ret = YoloEngine.ARCHER_STAND;	
							}
						
					}
					ret = YoloEngine.ARCHER_WALK;
				}
				
			}
			
			break;
		case 7:
			if(poison_duration > 0)
			{
				life -= 0.16f*YoloEngine.GAME_SKIPED_FRAMES;
				poison_duration-= YoloEngine.GAME_SKIPED_FRAMES;
			}
			if(slowDown_duration >0)
				slowDown_duration--;
			else
				isSlowDown = false;
			
			if(ret==YoloEngine.WARRIOR_HURT)
			{
				if(isLeft)
				{
					x_texture = xStart = 0.5f;
					y_texture = yStart = yEnd = 0.25f;
					xEnd = 0.625f;
				}
				else
				{
					
					x_texture = xStart = 0f;
					y_texture = yStart = yEnd = 0.375f;
					xEnd = 0.125f;
				}
				
				life -= damage_buffor;
				damage_buffor = 0f;
				ret = YoloEngine.WARRIOR_NULL;
				
				if(life<0)
				{
					ret=YoloEngine.WARRIOR_DYING;
					if(isLeft)
					{
						x_texture = xStart = xEnd = 0.5f;
						y_texture = yStart = yEnd = 0.25f;
						xEnd = 0.75f;
						
					}
					else
					{
						x_texture = xStart = 0f;
						y_texture = yStart = yEnd = 0.375f;
						xEnd = 0.25f;
					}
					
				}
			}
			else if(y_oponnent + 1 >y-y_radius/2 && y_oponnent < y + y_radius/2 && x_oponnent + 1  > x-x_radius/2 && x_oponnent < x+x_radius/2)
			{
				if(ret != YoloEngine.WARRIOR_ATTACK)
				if(x<x_oponnent)
				{
					isLeft = false;
					x_texture = xStart = 0;
					y_texture = yStart = 0.25f;
					xEnd = .375f;
					yEnd = 0.25f;
				}
				else
				{
					isLeft = true;
					x_texture = xStart = 0.5f;
					y_texture = yStart = 0.125f;
					xEnd = .875f;
					yEnd = 0.125f;
			
				}
				ret = YoloEngine.WARRIOR_ATTACK;
			}
			else
			{
				if(!onGround)
				{
					if(ret != YoloEngine.WARRIOR_STAND && ret != YoloEngine.WARRIOR_ATTACK)
					if(x>x_oponnent)
					{
						isLeft = true;
						x_texture = xStart = xEnd = 0.875f;
						y_texture = yStart = yEnd = 0.25f;
					}
					else
					{
						isLeft = false;
						x_texture = xStart = xEnd = 0.375f;
						y_texture = yStart = yEnd = 0.375f;
					}
					ret = YoloEngine.WARRIOR_STAND;
				}	
				else
				{
					if(ret != YoloEngine.WARRIOR_WALK && ret != YoloEngine.WARRIOR_ATTACK)
					if(x<x_oponnent)
					{
						isLeft = false;
						if(x - YoloEngine.WARRIOR_SPEED *YoloEngine.GAME_SKIPED_FRAMES > YoloGameRenderer.ObjectTab[j].min_x)
						{
						
							x_texture = xStart = 0.75f;
							y_texture = yStart = 0f;
							xEnd = 0.375f;
							yEnd = 0.125f;
						}
						
					}
					else
					{
						isLeft = true;
						if(x + YoloEngine.WARRIOR_SPEED *YoloEngine.GAME_SKIPED_FRAMES<YoloGameRenderer.ObjectTab[j].max_x)
						{
							x_texture = xStart = 0f;
							y_texture = yStart = 0f;
							xEnd = .625f;
							yEnd = 0f;
						}
						
					}
					else
					{
						if(Math.abs(x-x_oponnent)<YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES)
						{
							x_texture = xStart = xEnd = 0.375f;
							y_texture = yStart = yEnd = 0.375f;
							ret = YoloEngine.WARRIOR_STAND;
						}
						else if(x>x_oponnent)
							if(x - YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES > YoloGameRenderer.ObjectTab[j].min_x)
								if(isSlowDown)
									x -= YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x -= YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x = YoloGameRenderer.ObjectTab[j].min_x;
								x_texture = xStart = xEnd = 0.375f;
								y_texture = yStart = yEnd = 0.375f;
								ret = YoloEngine.WARRIOR_STAND;
							}
						else
							if(x + YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES +1f < YoloGameRenderer.ObjectTab[j].max_x)
								if(isSlowDown)
									x += YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x += YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x= YoloGameRenderer.ObjectTab[j].max_x-1f;
								x_texture = xStart = xEnd = 0.875f;
								y_texture = yStart = yEnd = 0.25f;
								ret = YoloEngine.WARRIOR_STAND;	
							}
						
					}
					ret = YoloEngine.WARRIOR_WALK;
				}
				
			}
			break;
		case 8:
			if(poison_duration > 0)
			{
				life -= 0.16f*YoloEngine.GAME_SKIPED_FRAMES;
				poison_duration-=YoloEngine.GAME_SKIPED_FRAMES;
			}
			if(slowDown_duration >0)
				slowDown_duration--;
			else
				isSlowDown = false;
			
			if(ret==YoloEngine.MUMMY_HURT)
			{
				if(isLeft)
				{
					x_texture = xStart = xEnd =  0f;
					y_texture = yStart = yEnd = 0.5f;
				}
				else
				{
					x_texture = xStart = xEnd = 0.25f;
					y_texture = yStart = yEnd = 0.5f;
				}
				
				life -= damage_buffor;
				damage_buffor = 0f;
				ret = YoloEngine.MUMMY_NULL;
				
				if(life<0)
				{
					ret=YoloEngine.MUMMY_DYING;
					if(isLeft)
					{
						x_texture = xStart = 0f;
						y_texture = yStart = yEnd = 0.5f;
						xEnd = 0.125f;
					}
					else
					{
						x_texture = xStart = xEnd = 0.25f;
						y_texture = yStart = yEnd = 0.5f;
						xEnd = 0.375f;
					}
					
				}
			}
			else if(y_oponnent + 1 >y-y_radius/2 && y_oponnent < y + y_radius/2 && x_oponnent + 1  > x-x_radius/2 && x_oponnent < x+x_radius/2)
			{
				if(ret != YoloEngine.MUMMY_ATTACK)
				if(x<x_oponnent)
				{
					isLeft = false;
					x_texture = xStart = 0f;
					y_texture = yStart = 0.375f;
					xEnd = .75f;
					yEnd = 0.375f;
				}
				else
				{
					isLeft = true;
					x_texture = xStart = 0;
					y_texture = yStart = 0.25f;
					xEnd = .75f;
					yEnd = 0.25f;
				}
				ret = YoloEngine.MUMMY_ATTACK;
			}
			else
			{
				if(!onGround)
				{
					if(ret != YoloEngine.MUMMY_STAND && ret != YoloEngine.MUMMY_ATTACK)
					if(x>x_oponnent)
					{
						isLeft = true;
						x_texture = xStart = xEnd = 0.875f;
						y_texture = yStart = yEnd = 0.25f;
					}
					else
					{
						isLeft = false;
						x_texture = xStart = xEnd = 0.875f;
						y_texture = yStart = yEnd = 0.375f;
					}
					ret = YoloEngine.MUMMY_STAND;
				}	
				else
				{
					if(ret != YoloEngine.MUMMY_WALK && ret != YoloEngine.MUMMY_ATTACK)
					if(x<x_oponnent)
					{
						isLeft = false;
						if(x - YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES > YoloGameRenderer.ObjectTab[j].min_x)
						{
							x_texture = xStart = 0f;
							y_texture = yStart = 0.125f;
							xEnd = .875f;
							yEnd = 0.125f;
						}
						
					}
					else
					{
						isLeft = true;
						if(x + YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES<YoloGameRenderer.ObjectTab[j].max_x)
						{
							x_texture = xStart = 0f;
							y_texture = yStart = 0f;
							xEnd = 0.875f;
							yEnd = 0f;
						}
						
					}
					else
					{
						if(Math.abs(x-x_oponnent)<YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES)
						{
							x_texture = xStart = xEnd = 0.875f;
							y_texture = yStart = yEnd = 0.375f;
							ret = YoloEngine.MUMMY_STAND;
						}
						else if(x>x_oponnent)
							if(x - YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES> YoloGameRenderer.ObjectTab[j].min_x)
								if(isSlowDown)
									x -= YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x -= YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x = YoloGameRenderer.ObjectTab[j].min_x;
								x_texture = xStart = xEnd = 0.875f;
								y_texture = yStart = yEnd = 0.25f;
								ret = YoloEngine.MUMMY_STAND;
							}
						else
							if(x + YoloEngine.MUMMY_SPEED*YoloEngine.GAME_SKIPED_FRAMES + 1f< YoloGameRenderer.ObjectTab[j].max_x)
								if(isSlowDown)
									x += YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x += YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x = YoloGameRenderer.ObjectTab[j].max_x-1f;
								x_texture = xStart = xEnd = 0.875f;
								y_texture = yStart = yEnd = 0.375f;
								ret = YoloEngine.MUMMY_STAND;	
							}
						
					}
					ret = YoloEngine.MUMMY_WALK;
				}
				
			}
			break;
		case 9:
			if(poison_duration > 0)
			{
				life -= 0.16f*YoloEngine.GAME_SKIPED_FRAMES;
				poison_duration-=YoloEngine.GAME_SKIPED_FRAMES;
			}
			if(ret==YoloEngine.HAND_HURT)
			{
				if(isLeft)
				{
					x_texture = xStart = xEnd = 0f;
					y_texture = yStart = yEnd = 0.25f;
				}
				else
				{
					x_texture = xStart = xEnd = 0.5f;
					y_texture = yStart = yEnd = 0.25f;
				}
				
				life -= damage;
				ret = YoloEngine.HAND_NULL;
				
				if(life<0)
				{
					ret=YoloEngine.HAND_DYING;
					if(isLeft)
					{
						x_texture = xStart = 0f;
						y_texture = yStart = yEnd = 0.25f;
						xEnd = 0.375f;
					}
					else
					{
						x_texture = xStart = xEnd = 0.5f;
						y_texture = yStart = yEnd = 0.375f;
						xEnd = 0.875f;
					}
					
				}
			}
			else if(y_oponnent + 1 >y-y_radius/2 && y_oponnent < y + y_radius/2 && x_oponnent + 1  > x-x_radius/2 && x_oponnent < x+x_radius/2)
			{
				if(ret != YoloEngine.HAND_ATTACK)
				if(x<x_oponnent)
				{
					isLeft = false;
					x_texture = xStart = 0;
					y_texture = yStart = 0.125f;
					xEnd = 0.375f;
					yEnd = 0.125f;
				}
				else
				{
					isLeft = true;
					x_texture = xStart = 0.5f;
					y_texture = yStart = 0.125f;
					xEnd = 0.875f;
					yEnd = 0.125f;
				}
				ret = YoloEngine.HAND_ATTACK;
			}
			else
			{
				if(ret != YoloEngine.HAND_STAND && ret != YoloEngine.HAND_ATTACK)
				if(x>x_oponnent)
				{
					isLeft = true;
					x_texture = xStart = 0f;
					y_texture = yStart = yEnd = 0f;
					xEnd = 0.375f;
				}
				else
				{
					isLeft = false;
					x_texture = xStart = 0.5f;
					y_texture = yStart = yEnd = 0f;
					xEnd = 0.875f;
				}
				ret = YoloEngine.HAND_STAND;
					
			}
			break;
		case 10:
			life -= damage_buffor;
			damage_buffor = 0f;
			if(life<0)
			{
				if(ret != YoloEngine.BARREL_ATTACK)
				{
					x_texture = xStart = 0;
					y_texture = yStart = 0.25f;
					xEnd = .875f;
					yEnd = 0.5f;
					ret = YoloEngine.BARREL_ATTACK;
				}
			}
			else if(y_oponnent + 1 >y-y_radius/2 && y_oponnent < y + y_radius/2 && x_oponnent + 1  > x-x_radius/2 && x_oponnent < x+x_radius/2)//czy dobre?
			{
				if(ret != YoloEngine.BARREL_ATTACK)
				{
					x_texture = xStart = 0;
					y_texture = yStart = 0.25f;
					xEnd = .875f;
					yEnd = 0.5f;	
					ret = YoloEngine.BARREL_ATTACK;
				}
			}
			else
			{
				if(!onGround)
				{
					if(ret != YoloEngine.BARREL_ATTACK)
					{
						x_texture = xStart = xEnd = 0f;
						y_texture = yStart = yEnd = 0f;
						ret = YoloEngine.BARREL_STAND;
					}
				}	
				else
				{
					if(isLeft)
					{
						if(ret!=YoloEngine.BARREL_WALK && ret != YoloEngine.BARREL_ATTACK)
						{
							if(x - YoloEngine.BARREL_SPEED*YoloEngine.GAME_SKIPED_FRAMES -1f > 0)
							{
								x_texture = xStart = 0f;
								y_texture = yStart = 0.125f;
								xEnd = .875f;
								yEnd = 0.125f;
							}
							ret = YoloEngine.ARCHER_WALK;
						}
						else
						{
							if(x - YoloEngine.BARREL_SPEED*YoloEngine.GAME_SKIPED_FRAMES -1f< 0)
							{
								if(ret != YoloEngine.BARREL_ATTACK)
								{
									x_texture = xStart = 0;
									y_texture = yStart = 0.25f;
									xEnd = .875f;
									yEnd = 0.5f;
									ret = YoloEngine.BARREL_ATTACK;
								}
							}
							else
								x -= YoloEngine.BARREL_SPEED*YoloEngine.GAME_SKIPED_FRAMES; 
						}
					}
					else 
					{
						if(ret!=YoloEngine.BARREL_WALK && ret != YoloEngine.BARREL_ATTACK)
						{
							if(x + YoloEngine.BARREL_SPEED*YoloEngine.GAME_SKIPED_FRAMES + 1f < YoloEngine.LEVEL_SIZE_X * YoloEngine.GAME_PROJECTION_X )
							{
								x_texture = xStart = 0f;
								y_texture = yStart = 0f;
								xEnd = 0.875f;
								yEnd = 0f;
							}
							ret = YoloEngine.ARCHER_WALK;
						}
						else
						{
							if(x + YoloEngine.BARREL_SPEED*YoloEngine.GAME_SKIPED_FRAMES + 1f > YoloEngine.LEVEL_SIZE_X * YoloEngine.GAME_PROJECTION_X )
							{
								if(ret != YoloEngine.BARREL_ATTACK)
								{
									x_texture = xStart = 0;
									y_texture = yStart = 0.25f;
									xEnd = .875f;
									yEnd = 0.5f;
									ret = YoloEngine.BARREL_ATTACK;
								}
							}
							else
								x += YoloEngine.BARREL_SPEED*YoloEngine.GAME_SKIPED_FRAMES; 
						}
					}
				}
				
			}
			
			break;
		case 11:
			life -= damage_buffor;
			damage_buffor = 0f;
			if(life<0)
			{
				x_texture = xStart = xEnd = 0.875f;
				y_texture = yStart = yEnd = 0.125f;
				ret = YoloEngine.TOWER_DYING;
			}
			else 
			{
				if(ret == 100)
				{
					x_texture = xStart = 0f;
					y_texture = yStart = yEnd = 0f;
					xEnd = 0.875f;
					ret = YoloEngine.TOWER_NEW;
				}
				if(ret != YoloEngine.TOWER_NEW)
				if(y_oponnent + 1 >y-y_radius/2 && y_oponnent < y + y_radius/2 && x_oponnent + 1  > x-x_radius/2 && x_oponnent < x+x_radius/2)
				{
					if(ret != YoloEngine.TOWER_FIRE)
					if(x<x_oponnent)
						isLeft = false;
					else
						isLeft = true;
					x_texture = xStart = xEnd = 0.875f;
					y_texture = yStart = yEnd = 0f;
					ret = YoloEngine.TOWER_FIRE;
				}
				else
				{
					ret = YoloEngine.TOWER_STAND;
					x_texture = xStart = xEnd = 0.875f;
					y_texture = yStart = yEnd = 0f;
				}
			}
			break;
		case 12:
			life -= damage_buffor;
			damage_buffor = 0f;
			
			if(ret != YoloEngine.WALL_NEW && ret != YoloEngine.WALL_STAND)
			{
				x_texture = xStart = 0f;
				y_texture = yStart = yEnd = 0f;
				xEnd = 0.875f;
				ret = YoloEngine.WALL_NEW;
			}
			
			if(ret == YoloEngine.WALL_STAND)
			if(life>4*life/5)
			{
				x_texture = xStart = xEnd = 0.875f;
				y_texture = yStart = yEnd = 0f;
			}
			else if(life>3*life/5)
			{
				x_texture = xStart = xEnd = 0f;
				y_texture = yStart = yEnd = 0.125f;
			}
			else if(life>2*life/5)
			{
				x_texture = xStart = xEnd = 0.125f;
				y_texture = yStart = yEnd = 0.125f;
			}
			else if(life>life/5)
			{
				x_texture = xStart = xEnd = 0.25f;
				y_texture = yStart = yEnd = 0.125f;
			}
			else if(life>0)
			{
				x_texture = xStart = xEnd = 0.375f;
				y_texture = yStart = yEnd = 0.125f;
			}
			else
			{
				x_texture = xStart = xEnd = 0.5f;
				y_texture = yStart = yEnd = 0.125f;
				ret = 4;
			}	
			break;
		}
	}	
}

public class YoloGameRenderer implements Renderer {
	
	private YoloTexture TextureLoader ;
	private int[] spriteSheets = new int[21];
	private YoloBackground back= new YoloBackground(),load_back=new YoloBackground(),load_front = new YoloBackground();
	private YoloPlayer player = new YoloPlayer();
	//private YoloBackground btn_mov = new YoloBackground(),btn_movball = new YoloBackground(); 
	//private YoloBackground live_bar_1 = new YoloBackground(),live_bar_0 = new YoloBackground();
	private YoloWeapon btn = new YoloWeapon();
	
	public static YoloObject[] ObjectTab = new YoloObject[17];
	private YoloObject[] LaddreTab = new YoloObject[4];
	
	private static Vector<YoloWeapon> Weapontab  = new Vector<YoloWeapon>();
	private static YoloWeapon bullet;
	
	public static Skill[] skilltab = new Skill[3];
	public static Vector<Skill> skillOponentVe = new Vector<Skill>();
	public static Vector<Skill> skillPlayerVe = new Vector<Skill>();
	public static Vector<HitBox> hitBoxs = new Vector<HitBox>();
	
	private final float MOVE_SIZE_X = 2*YoloEngine.MAX_VALUE_PLAYER_SPEED/YoloEngine.display_x; // 200/display_x
	private final float MOVE_SIZE_Y = YoloEngine.MOVE_SIZE_Y/YoloEngine.display_y; // 50/display_y
	private final float MOVE_SIZE_X1 = 160f/YoloEngine.display_x;
	private final float MOVE_SIZE_Y1 = 160f/YoloEngine.display_y;
	private final float MOVE_BALL_SIZE_X = YoloEngine.MOVE_SIZE_Y/YoloEngine.display_x; // 50/display_x
	private final float MOVE_POS_X = 25f/YoloEngine.display_x;//(YoloEngine.MOVE_X/YoloEngine.display_x - MOVE_SIZE_X/2);// /MOVE_SIZE_X;  (125-100)/display_x
	private final float MOVE_POS_Y = 50f/YoloEngine.display_y; //(YoloEngine.display_y - YoloEngine.MOVE_Y)/YoloEngine.display_y + MOVE_SIZE_Y/2; // 25/display_y == move_y/2/display_y
	private final float MOVE_POS_X1= (25f/YoloEngine.display_x);// /MOVE_SIZE_X1 ;
	private final float MOVE_POS_Y1= (25f/YoloEngine.display_y);// /MOVE_SIZE_Y1 ; 
	private final float LIVE_BAR_SIZE_X_0 = YoloEngine.LIVE_BAR_SIZE/YoloEngine.display_x;
	private float LIVE_BAR_SIZE_X_1 = LIVE_BAR_SIZE_X_0;
	private final float LIVE_BAR_SIZE_Y = 30f/YoloEngine.display_y;
	
	private float cameraPosX,joyBallX =(YoloGame.x2-25f)/YoloEngine.display_x //(YoloGame.x2/YoloEngine.display_x - MOVE_BALL_SIZE_X/2)// /MOVE_BALL_SIZE_X, (x2-25)dis_x
			,jumpBtnX = 1-125f/YoloEngine.display_x // 1/(MOVE_BALL_SIZE_X*2)-1.5f
			,shotBtnX = jumpBtnX
			,crouchBtnX = 250f/YoloEngine.display_x //2.75f
			,skillBtnX = .5f - 50f/YoloEngine.display_x //  1/(MOVE_BALL_SIZE_X*2)/2
			,liveBarX_0 = 25f/YoloEngine.display_x //(0.5f/(1f/LIVE_BAR_SIZE_Y))*(1/LIVE_BAR_SIZE_X_0);	
			,joyBallX1
			,XADD = 0; 

	private float cameraPosY
			,jumpBtnY = 150f/YoloEngine.display_y
			,shotBtnY = 25f/YoloEngine.display_y
			,crouchBtnY = 25f/YoloEngine.display_y
			,liveBarY = 1-55f/YoloEngine.display_y // 1f/LIVE_BAR_SIZE_Y -1.75f;// 1-(25+30)/dis_x
			,joyBallY1
			,YADD = 0; 
	
	private boolean toLoad = true,first = false;
	private int loading_faze=0,loadingStepsCout = 20;
	
	
	private int nextBullet = 0;
	private boolean onGround = true;
	private int ClimbingOn;
	private int S1cooldown = 0,S2cooldown = 0,S3cooldown = 0,poisoned = 0,slowDowned=0;;
				
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
				spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.PLAYER_TEXTURE, YoloEngine.context, 2);
				drawLoadingSrean(gl, 0f/loadingStepsCout);
				break;
			case 1:
				spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.OPPONENT_TEXTURE, YoloEngine.context, 3); // Multislayer
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
			case 7:
				if(YoloEngine.sprite_load[loading_faze])
					spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.WARRIOR_SPRITE, YoloEngine.context, 7);
					drawLoadingSrean(gl, 7f/loadingStepsCout);
				break;
			case 8:
				if(YoloEngine.sprite_load[loading_faze])
					spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.MUMMY_SPRITE, YoloEngine.context, 8);
					drawLoadingSrean(gl, 8f/loadingStepsCout);
				break;
			case 9:
				if(YoloEngine.sprite_load[loading_faze])
					spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.HAND_SPRITE, YoloEngine.context, 9);
					drawLoadingSrean(gl, 9f/loadingStepsCout);
				break;
			case 10:
				if(YoloEngine.sprite_load[loading_faze])
					spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.BARREL_SPRITE, YoloEngine.context, 10);
					drawLoadingSrean(gl, 10f/loadingStepsCout);
				break;
			case 11:
				if(YoloEngine.sprite_load[loading_faze])
					spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.TOWER_SPRITE, YoloEngine.context, 11);
					drawLoadingSrean(gl, 11f/loadingStepsCout);
				break;
			case 12:
				if(YoloEngine.sprite_load[loading_faze])
					spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.WALL_SPRITE, YoloEngine.context, 12);
					drawLoadingSrean(gl, 12f/loadingStepsCout);
				break;
			case 13:
				if(YoloEngine.sprite_load[loading_faze])
					spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.LIVE_DRAIN_SPRITE, YoloEngine.context, 13);
					drawLoadingSrean(gl, 13f/loadingStepsCout);
				break;
			case 14:
				if(YoloEngine.sprite_load[loading_faze])
					spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.RESURECTION_SPRITE, YoloEngine.context, 14);
					drawLoadingSrean(gl, 14f/loadingStepsCout);
				break;
			case 15:
				if(YoloEngine.sprite_load[loading_faze])
					spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.SPIKES_SPRITE, YoloEngine.context, 15);
					drawLoadingSrean(gl, 15f/loadingStepsCout);
				break;
			case 16:
				if(YoloEngine.sprite_load[loading_faze])
					spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.SLOW_DOWN_SPRITE, YoloEngine.context, 16);
					drawLoadingSrean(gl, 16f/loadingStepsCout);
				break;
			case 17:
				if(YoloEngine.sprite_load[loading_faze])
					spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.HEAL_SPRITE, YoloEngine.context, 17);
					drawLoadingSrean(gl, 17f/loadingStepsCout);
				break;
			case 18:
				if(YoloEngine.sprite_load[loading_faze])
					spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.SHOCK_WAVE_SPRITE, YoloEngine.context, 18);
					drawLoadingSrean(gl, 18f/loadingStepsCout);
				break;
			case 19:
				if(YoloEngine.sprite_load[loading_faze])
					spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.ICICLE_SPRITE, YoloEngine.context, 19);
					drawLoadingSrean(gl, 19f/loadingStepsCout);
				break;
			case 20:
				if(YoloEngine.sprite_load[loading_faze])
					spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.FOG_SPRITE, YoloEngine.context, 20);
					drawLoadingSrean(gl, 20f/loadingStepsCout);
				break;
				//TODO Loading texturek skilli
				
			case 21:
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
			
			if(YoloEngine.canMove)
				if(YoloEngine.isPlayerSlowDown && slowDowned != 0)
					YoloEngine.Player_x += YoloEngine.Player_vx/2f;
				else
					YoloEngine.Player_x += YoloEngine.Player_vx;
			
			if(slowDowned != 0)slowDowned--;
	//------------------------------------------------------DARBINY---------------------------------------------------------------TODO Drabiny
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
			YoloEngine.GAME_SKIPED_FRAMES = (int) (loopRunTime/YoloEngine.GAME_THREAD_FSP_SLEEP + 1);
			
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
			hitBox();
			
			
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
		
		if(object.max_x  <= bullet.x || object.min_x >= bullet.x + bullet.size)return false;
		if(object.max_y  <= bullet.y || object.min_y >= bullet.y + bullet.size)return false;

		return true;
	}
	private boolean IsCollided(Skill skill)
	{
		if(YoloEngine.Player_x + 1  < skill.x*4 || YoloEngine.Player_x > skill.x*4 + skill.x_radius)return false;
		if(YoloEngine.Player_y + 1 < skill.y*4 || YoloEngine.Player_y > skill.y*4 + skill.y_radius) return false;
		
		return true;	
	}
	private boolean IsCollided(Skill skill ,Skill skill2)
	{
		if(skill2.x + 1  < skill.x || skill2.x > skill.x + skill.x_radius)return false;
		if(skill2.y + 1 < skill.y || skill2.y > skill.y + skill.y_radius) return false;
		
		return true;	
	}
	
	private boolean IsCollided(YoloWeapon bullet , Skill skill)
	{
		if(bullet.x + bullet.size <= skill.x*4 || bullet.x >= skill.x*4 + 1f)return false;
		if(bullet.y + bullet.size <= skill.y*4 || bullet.y >= skill.y*4 + 2f) return false;
		
		return true;
	}
	private boolean IsCollided(float x ,float y ,float x_radius ,float y_radius)
	{
		if(YoloEngine.Player_x + 1 < x || YoloEngine.Player_x > x + x_radius)return false;
		if(YoloEngine.Player_y + 1 < y || YoloEngine.Player_y > y + y_radius) return false;
		
		return true;	
	}
	private boolean IsCollided(HitBox hitbox , Skill skill)
	{
		if(hitbox.x + hitbox.x_radius < skill.x || hitbox.x > skill.x + 1f)return false;
		if(hitbox.y + hitbox.y_radius < skill.y || hitbox.y >= skill.y + 2f) return false;
		
		return true;
	}
	private void drawBullet(GL10 gl, YoloWeapon bullet)
	{
		if(bullet.count ==0)
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
		else
			
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(1/YoloEngine.GAME_PROJECTION_X*bullet.scale, 1/YoloEngine.GAME_PROJECTION_Y*bullet.scale, 1f);
			gl.glTranslatef(bullet.x/bullet.scale, bullet.y/bullet.scale, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			if(bullet.isLeft)gl.glTranslatef(bullet.x_texture, bullet.y_texture, 0f);
			else gl.glTranslatef(bullet.x_texture, bullet.y_texture+.125f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			bullet.draw(gl,spriteSheets,bullet.sprite);
			gl.glPopMatrix();
			gl.glLoadIdentity();
			
			if(bullet.couter++!=bullet.count)
				if(bullet.x_texture<1)bullet.x_texture+=0.125f;
				else{bullet.y_texture+=0.125f; bullet.x_texture=0f;}
			else
			{
				bullet.couter=0;
				bullet.x_texture =0;
				bullet.y_texture =0;
			}
		}
	}
	
	private void moveBullets(GL10 gl)
	{
		out:
		for(int i = 0 ; i < Weapontab.size() ;i++)
		{
			if(Weapontab.get(i).isLeft) Weapontab.get(i).x -= Weapontab.get(i).bulletSpeed;
			else  Weapontab.get(i).x += Weapontab.get(i).bulletSpeed;

			drawBullet(gl, Weapontab.get(i));
			
			if(Weapontab.get(i).x + 1f < 0)
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
			else 
			{
				for (int j = 0 ; j< ObjectTab.length ; j++)
					if (IsCollided(Weapontab.get(i), ObjectTab[j])) 
					{
						Weapontab.remove(i);
						break out;
					}
				if(!Weapontab.get(i).isMy)
				for (int x =0 ;x < skillPlayerVe.size();x++)
					if(skillPlayerVe.elementAt(x).sprite>=6 && skillPlayerVe.elementAt(x).sprite <= 9 )
						if(IsCollided(Weapontab.get(i), skillPlayerVe.elementAt(x)))
						{
							Weapontab.remove(i);
							skillPlayerVe.elementAt(x).damage_buffor += Weapontab.get(i).damage;
							break;
						}
				
			}
			
		}
	}
	
	void drawSt(GL10 gl,float x,float y,float scaleX,float scaleY,float xT,float yT,boolean isAlfa)
	{
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glTranslatef(x,y, 0f);
		gl.glScalef(scaleX, scaleY, 1f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		if(isAlfa)gl.glColor4f(0.6f,0.6f,0.6f,0.5f);
		else gl.glColor4f(1f,1f,1f,1f);
		gl.glTranslatef(xT, yT, 0);
		btn.draw(gl,spriteSheets,1);
		gl.glPopMatrix();
		gl.glLoadIdentity();
	}
	
	private void drawControls(GL10 gl)
	{
		if(YoloEngine.isClasic)
		{
			joyBallX =(YoloGame.x2-25f)/YoloEngine.display_x; // (YoloGame.x2/YoloEngine.display_x - MOVE_BALL_SIZE_X/2)/MOVE_BALL_SIZE_X;// (x2-25)/dis_x
			drawSt(gl, MOVE_POS_X + XADD, MOVE_POS_Y + YADD, MOVE_SIZE_X,  MOVE_SIZE_Y, .5f, .125f,true);
			drawSt(gl, joyBallX + XADD, MOVE_POS_Y + YADD, MOVE_BALL_SIZE_X, MOVE_SIZE_Y, .625f, .125f,true);	
		}
		else
		{
			joyBallX1 = (YoloGame.x2-60f)/YoloEngine.display_x; // MOVE_POS_X1*2 + .5f - ((YoloGame.x_old-YoloGame.x2)/YoloEngine.display_x)/MOVE_SIZE_X1;//(x2+25-80)/dis_x
			joyBallY1 = (YoloGame.y2+65f)/YoloEngine.display_y; // MOVE_SIZE_Y1 +.5f + YoloGame.y2/YoloEngine.display_y/MOVE_SIZE_Y1;// (y2+25+40)/dis_y
			drawSt(gl, MOVE_POS_X1 + XADD, MOVE_POS_Y1 + YADD, MOVE_SIZE_X1, MOVE_SIZE_Y1, .25f, .125f,true);
			drawSt(gl, joyBallX1 + XADD, joyBallY1 + YADD, MOVE_SIZE_X1/2, MOVE_SIZE_Y1/2, .375f, .125f,true);
		}
		
		LIVE_BAR_SIZE_X_1 = LIVE_BAR_SIZE_X_0*YoloEngine.PlayerLive/YoloEngine.PLAYER_LIVE_MAX; // dobrze;
		drawSt(gl, liveBarX_0 + XADD, liveBarY +YADD, LIVE_BAR_SIZE_X_0, LIVE_BAR_SIZE_Y, .75f, .125f,true);
		drawSt(gl, liveBarX_0 + XADD, liveBarY +YADD, LIVE_BAR_SIZE_X_1,LIVE_BAR_SIZE_Y, .875f, .125f,false);
	}
	
	private void drawButtons(GL10 gl)
	{
		if(YoloEngine.isClasic)
		{
			drawSt(gl, jumpBtnX + XADD, jumpBtnY + YADD, MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, YoloEngine.isJumping? 0 : .125f, 0, true);
			if(YoloEngine.isCrouch)drawSt(gl, crouchBtnX + XADD, crouchBtnY + YADD,MOVE_BALL_SIZE_X*2,  MOVE_SIZE_Y*2, YoloEngine.isCrouch_prest? .75f : .875f, 0, true);
			else drawSt(gl, crouchBtnX + XADD, crouchBtnY + YADD,MOVE_BALL_SIZE_X*2,  MOVE_SIZE_Y*2, YoloEngine.isCrouch_prest? .5f : .625f, 0, true);
		}
		drawSt(gl, shotBtnX + XADD, shotBtnY + YADD, MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, YoloEngine.isShoting? .25f : .375f , 0, true);

		drawSt(gl, skillBtnX + XADD - 100f/YoloEngine.display_x, YADD, MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, YoloEngine.canSkill1? 0 : .125f, .125f, true);
		drawSt(gl, skillBtnX + XADD, YADD, MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, YoloEngine.canSkill2? 0 : .125f, .125f, true);
		drawSt(gl, skillBtnX + XADD + 100f/YoloEngine.display_x, YADD, MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, YoloEngine.canSkill3? 0 : .125f, .125f, true);
	}
	
	private void drawBackground(GL10 gl)
	{
		if(YoloEngine.Player_x > YoloEngine.GAME_PROJECTION_X/2 +.5f && YoloEngine.Player_x < YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X - YoloEngine.GAME_PROJECTION_X/2 - .5f)
		{
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0f, 1f, 0f, 1f, -1f, 1f);
			XADD = ((YoloEngine.Player_x-.5f)/YoloEngine.GAME_PROJECTION_X) - .5f;//taka sta³a
			cameraPosX = -XADD;

			gl.glTranslatef(cameraPosX,cameraPosY,0f);
			
		}
		if(YoloEngine.Player_y > YoloEngine.GAME_PROJECTION_Y/2 + .5f && YoloEngine.Player_y < YoloEngine.LEVEL_SIZE_Y*YoloEngine.GAME_PROJECTION_Y - YoloEngine.GAME_PROJECTION_Y/2 - .5f)
		{
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0f, 1f, 0f, 1f, -1f, 1f);
			YADD = ((YoloEngine.Player_y-.5f)/YoloEngine.GAME_PROJECTION_Y) - .5f;
			cameraPosY = -YADD ;

			gl.glTranslatef(cameraPosX,cameraPosY,0f);
		}
		if(YoloEngine.Player_x < 0)
		{
			YoloEngine.Player_x = YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X-1;
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0f, 1f, 0f, 1f, -1f, 1f);
			
			XADD = ((YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X)/YoloEngine.GAME_PROJECTION_X)-1f;
			cameraPosX = -XADD;

			gl.glTranslatef(cameraPosX,cameraPosY,0f);
		}
		else if(YoloEngine.Player_x > YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X -1)
			{
				YoloEngine.Player_x = 0;
				gl.glMatrixMode(GL10.GL_PROJECTION);
				gl.glLoadIdentity();
				gl.glOrthof(0f, 1f, 0f, 1f, -1f, 1f);
				XADD = 0;
				cameraPosX = 0;

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
	}
	private void playerFire(float bulletSpeed,int sprite,int count,float damage,boolean isleft)
	{
		if(nextBullet == 0)
		{
			bullet = new YoloWeapon(bulletSpeed);
			bullet.damage = 1f;
			bullet.isMy = true; 
			bullet.sprite = sprite;
			bullet.x_texture = 0f;
			bullet.y_texture = 0f;
			bullet.x = YoloEngine.Player_x;
			if(!YoloEngine.isCrouch)	bullet.y = YoloEngine.Player_y + .5f; 
			else bullet.y = YoloEngine.Player_y + .025f; 
			bullet.size = 0.25f;
			bullet.isLeft = isleft;
			Weapontab.add(bullet);
			
			if(YoloEngine.MULTI_ACTIVE)
				YoloEngine.mMultislayer.sendMessageToAllreliable((YoloEngine.Player_x+"|"+YoloEngine.Player_y+"|"+YoloEngine.isPlayerLeft+"|"+YoloEngine.isCrouch+"|"+"l").getBytes());
		}
		nextBullet--;
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
	
	
	private void AIFire(float x,float y,boolean isLeft,int sprite,float x_texture,float y_texture)
	{
		bullet = new YoloWeapon(0.2f);
		bullet.damage = 10f;
		bullet.isMy = true; 
		if(isLeft)
			bullet.x = x-2f;
		else
			bullet.x = x;
		bullet.y = y; 
		bullet.sprite = sprite;
		bullet.x_texture = x_texture ;
		bullet.y_texture = y_texture;
		bullet.size = 0.1f;
		bullet.scale = 4f;
		bullet.isLeft = isLeft;
		Weapontab.add(bullet);
		if(YoloEngine.MULTI_ACTIVE)
			YoloEngine.mMultislayer.sendMessageToAllreliable((x+"|"+y+"|"+isLeft+"|"+false+"|"+"l").getBytes());
	}
	
	private boolean AIDraw(GL10 gl,int i,boolean isMy,int sprite)
	{
		Vector<Skill> Ve;
		if(isMy)
		{
			Ve = skillPlayerVe;
			if(!Ve.elementAt(i).haveXY)
			{
				if(YoloEngine.isPlayerLeft)
					Ve.elementAt(i).x = YoloEngine.Player_x ;
				else
					Ve.elementAt(i).x = YoloEngine.Player_x + 1f;
				if(sprite == 11 || sprite == 12)
				{
					float maxy=0;
					for(int q=0;q<YoloGameRenderer.ObjectTab.length;q++)
					{
						if(YoloEngine.Player_x>YoloGameRenderer.ObjectTab[q].min_x && YoloEngine.Player_x<YoloGameRenderer.ObjectTab[q].max_x)
						{
							if(YoloEngine.Player_y>=YoloGameRenderer.ObjectTab[q].max_y)
								if(YoloGameRenderer.ObjectTab[q].max_y>maxy)
									maxy = YoloGameRenderer.ObjectTab[q].max_y;
						}
					}
					Ve.elementAt(i).y = maxy;
				}
				else Ve.elementAt(i).y = YoloEngine.Player_y + 2f;
				
				if(sprite == 10)
				{
					Ve.elementAt(i).isLeft = YoloEngine.isPlayerLeft;
					Ve.elementAt(i).x++;
				}
				
		 		Ve.elementAt(i).haveXY = true;	
		 	}
		}
		else Ve = skillOponentVe;
			
		Ve.elementAt(i).move();
		
		Ve.elementAt(i).aniSlowCounter++;
		end :
		if(Ve.elementAt(i).aniSlowCounter == Ve.elementAt(i).animation_slowdown)
		{
			Ve.elementAt(i).aniSlowCounter = -1;
			
			if(Ve.elementAt(i).y_texture == Ve.elementAt(i).yEnd && Ve.elementAt(i).x_texture == Ve.elementAt(i).xEnd)
			{
				if(Ve.elementAt(i).ret==4)
				{ 
					Ve.remove(i);
					return true;
				}		
				
				Ve.elementAt(i).x_texture = Ve.elementAt(i).xStart;
				Ve.elementAt(i).y_texture = Ve.elementAt(i).yStart;
				
				if(isMy)
				{
//-------------------------------------------------Tworzenie HitBoxów----------------------------------------------------------------------------
					Ve.elementAt(i).frameCounter=0;
					switch (sprite)
					{
					case 6:
						if(Ve.elementAt(i).ret == YoloEngine.ARCHER_FIRE)
						{
							AIFire(Ve.elementAt(i).x, Ve.elementAt(i).y, Ve.elementAt(i).isLeft,6,0f,.5f);
							Ve.elementAt(i).ret = YoloEngine.ARCHER_NULL;
						}
						break;
					case 10:
						if(Ve.elementAt(i).ret == YoloEngine.BARREL_ATTACK)
						{
							Ve.remove(i);
							return true;
						}
						Ve.elementAt(i).ret = YoloEngine.BARREL_STAND;
						break;
					case 11:
						if(Ve.elementAt(i).ret == YoloEngine.TOWER_FIRE)
						{
							Ve.elementAt(i).fireCounter+=YoloEngine.GAME_SKIPED_FRAMES;
							if(Ve.elementAt(i).fireCounter >= Ve.elementAt(i).fire_rate)
							{
								AIFire(Ve.elementAt(i).x, Ve.elementAt(i).y, Ve.elementAt(i).isLeft,11,0f,.125f);
								Ve.elementAt(i).ret = YoloEngine.TOWER_STAND;
								Ve.elementAt(i).fireCounter =0;
							}
						}
						else if(Ve.elementAt(i).ret == YoloEngine.TOWER_NEW)
						{
							Ve.elementAt(i).ret = YoloEngine.TOWER_STAND;
							Ve.elementAt(i).x_texture = Ve.elementAt(i).xEnd;
							Ve.elementAt(i).y_texture = Ve.elementAt(i).yEnd;
						}
						break;
					case 12:
						Ve.elementAt(i).ret = YoloEngine.WALL_STAND;
						Ve.elementAt(i).x_texture = Ve.elementAt(i).xEnd;
						Ve.elementAt(i).y_texture = Ve.elementAt(i).yEnd;
						break;
					}
				}
				
				break end;
			}
			if(isMy)
			if(sprite == 7)
			{
				if(Ve.elementAt(i).frameCounter==2)
					if(Ve.elementAt(i).ret == YoloEngine.WARRIOR_ATTACK)
					{
						YoloEngine.mMultislayer.sendMessageToAllreliable(YoloEngine.mMultislayer.generateMessageFromFloats(new Float[]{Ve.elementAt(i).x,
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								(float)Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft?1f:0f}));
						Ve.elementAt(i).ret = YoloEngine.WARRIOR_NULL;
					}
			}
			else if(sprite == 9)
			{
				if(Ve.elementAt(i).frameCounter==2)
					if(Ve.elementAt(i).ret == YoloEngine.HAND_ATTACK)
					{
						YoloEngine.mMultislayer.sendMessageToAllreliable(YoloEngine.mMultislayer.generateMessageFromFloats(new Float[]{Ve.elementAt(i).x,
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								(float)Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft?1f:0f}));
						Ve.elementAt(i).ret = YoloEngine.HAND_NULL;
					}
			}
			else if(sprite == 15)
			{
				if(Ve.elementAt(i).frameCounter==3||Ve.elementAt(i).frameCounter == 6)
					if(Ve.elementAt(i).ret == YoloEngine.MUMMY_ATTACK)
					{
						YoloEngine.mMultislayer.sendMessageToAllreliable(YoloEngine.mMultislayer.generateMessageFromFloats(new Float[]{Ve.elementAt(i).x,
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								(float)Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft?1f:0f}));
						Ve.elementAt(i).ret = YoloEngine.MUMMY_NULL;
					}
			}
			else if(sprite == 10)
				if(Ve.elementAt(i).frameCounter==0)
					if(Ve.elementAt(i).ret == YoloEngine.BARREL_ATTACK)
					{
						YoloEngine.mMultislayer.sendMessageToAllreliable(YoloEngine.mMultislayer.generateMessageFromFloats(new Float[]{Ve.elementAt(i).x,
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								(float)Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft?1f:0f}));
					}
//--------------------------------------------------------------------------------------------------------------------------------------------		
		
			Ve.elementAt(i).frameCounter++;
			Ve.elementAt(i).x_texture+=0.125f; //kolejna klatka texturki;	
			if(Ve.elementAt(i).x_texture >= 1){Ve.elementAt(i).y_texture+=0.125f; Ve.elementAt(i).x_texture=0f;}
			
		}
						
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(1/YoloEngine.GAME_PROJECTION_X*4, 1/YoloEngine.GAME_PROJECTION_Y*4, 1f);
		gl.glTranslatef(Ve.elementAt(i).x/4f-.5f, Ve.elementAt(i).y/4f-.25f, 0f);
		gl.glColor4f(1f,1f,1f,1f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glTranslatef(Ve.elementAt(i).x_texture, Ve.elementAt(i).y_texture, 0f);
		btn.draw(gl, spriteSheets,Ve.elementAt(i).sprite);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		if(Ve.elementAt(i).isPoisoned)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(1/YoloEngine.GAME_PROJECTION_X*4, 1/YoloEngine.GAME_PROJECTION_Y*4, 1f);
			gl.glTranslatef(Ve.elementAt(i).x/4f-.5f, Ve.elementAt(i).y/4f-.25f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0f, 0.875f, 0f);
			btn.draw(gl, spriteSheets,Ve.elementAt(i).sprite);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		if(Ve.elementAt(i).isSlowDown)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(1/YoloEngine.GAME_PROJECTION_X*4, 1/YoloEngine.GAME_PROJECTION_Y*4, 1f);
			gl.glTranslatef(Ve.elementAt(i).x/4f-.5f, Ve.elementAt(i).y/4f-.25f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.125f, 0.875f, 0f);
			btn.draw(gl, spriteSheets,Ve.elementAt(i).sprite);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		return false;
	}
	
	private boolean LinearSkillDraw(GL10 gl,Skill skill)
	{
		float scale = skill.sprite == 15?1.5f:4f,scaleX = skill.sprite==20?35f:4f;
		if(skill.sprite ==20)scale = 8f;
		
		if(skill.y_texture == skill.yEnd && skill.x_texture == skill.xEnd)
			return true;
		else
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(1/YoloEngine.GAME_PROJECTION_X*scaleX, 1/YoloEngine.GAME_PROJECTION_Y*scale, 1f);
			gl.glTranslatef(skill.x/scaleX-.5f, skill.y/scale - (skill.sprite==20?0.5f:0.25f), 0f);
			gl.glColor4f(1f,1f,1f,1f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(skill.x_texture, skill.y_texture, 0f);
			btn.draw(gl, spriteSheets,skill.sprite);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		
		if(skill.aniSlowCounter++ == skill.animation_slowdown)
		{
			skill.aniSlowCounter = 0;
			if(skill.x_texture<1)skill.x_texture+=0.125f;
			else{skill.y_texture+=0.125f; skill.x_texture=0f;}
		}	
		return false;
	}
	
	private void drawPlayerSkills(GL10 gl)
	{
		int sprite;
		for(int i = 0; i < skillPlayerVe.size(); i++)
		{
			sprite = skillPlayerVe.elementAt(i).sprite;
			if(sprite>5 && sprite<13)
			{
				if(AIDraw(gl, i, true, skillPlayerVe.elementAt(i).sprite))//Rysuje kolejne AI
					{
						i--;continue;
					}
			}
			else//Pozosta³e skille
			{		
				if(skillPlayerVe.elementAt(i).x_texture==0 && skillPlayerVe.elementAt(i).y_texture==0)
				{
					if(sprite == 100||sprite == 101||sprite == 103)
					{
						skillPlayerVe.elementAt(i).x = YoloEngine.Player_x;
						skillPlayerVe.elementAt(i).y = YoloEngine.Player_y;
						
						for(int j =0;j<YoloEngine.mMultislayer.Opponents_x_last.length;j++)
						{
							if(Math.abs(YoloEngine.mMultislayer.Opponents_x_last[j]-skillPlayerVe.elementAt(i).x)<skillPlayerVe.elementAt(i).x_radius)			//TODO jeœli jest przeciwnikiem
								if(Math.abs(YoloEngine.mMultislayer.Opponents_y_last[j]-skillPlayerVe.elementAt(i).y)<skillPlayerVe.elementAt(i).y_radius)
								{
									int slowD =0;																												
									if(sprite==101)slowD = 2;
									Skill skill = new Skill(0,0,sprite-87,slowD,0.875f,0.375f,0,0,0,0);
									skill.x = YoloEngine.mMultislayer.Opponents_x_last[j];
									skill.y = YoloEngine.mMultislayer.Opponents_y_last[j];
									skillPlayerVe.add(skill);
								}
						}
						for(int j=0;j<skillOponentVe.size();j++)
						{
							if(Math.abs(skillOponentVe.elementAt(j).x-skillPlayerVe.elementAt(i).x)<skillPlayerVe.elementAt(i).x_radius)			
								if(Math.abs(skillOponentVe.elementAt(j).y-skillPlayerVe.elementAt(i).y)<skillPlayerVe.elementAt(i).y_radius)
								{
									int slowD =0;																												
									if(sprite==101)slowD = 2;
									Skill skill = new Skill(0,0,sprite-87,slowD,0.875f,0.375f,0,0,0,0);
									skill.x = skillPlayerVe.elementAt(j).x;
									skill.y = skillPlayerVe.elementAt(j).y;
									skillPlayerVe.add(skill);
								}
						}
						
						YoloEngine.mMultislayer.sendMessageToAllreliable(YoloEngine.mMultislayer.generateMessageFromFloats(new Float[]{skillPlayerVe.elementAt(i).x,
								skillPlayerVe.elementAt(i).y, skillPlayerVe.elementAt(i).x_radius, skillPlayerVe.elementAt(i).y_radius, skillPlayerVe.elementAt(i).damage, 
								skillPlayerVe.elementAt(i).frameDuration,(float)skillPlayerVe.elementAt(i).sprite,skillPlayerVe.elementAt(i).isLeft?1f:0f}));
						
						skillPlayerVe.remove(i--);
						continue;
					}
					else if(sprite == 104)
					{
						skillPlayerVe.elementAt(i).x = YoloEngine.Player_x;
						skillPlayerVe.elementAt(i).y = YoloEngine.Player_y;
						
						for(int j =0;j<YoloEngine.mMultislayer.Opponents_x_last.length;j++)
						{
							if(Math.abs(YoloEngine.mMultislayer.Opponents_x_last[j]-skillPlayerVe.elementAt(i).x)<skillPlayerVe.elementAt(i).x_radius)			//TODO jeœli nasz
								if(Math.abs(YoloEngine.mMultislayer.Opponents_y_last[j]-skillPlayerVe.elementAt(i).y)<skillPlayerVe.elementAt(i).y_radius)
								{
									Skill skill = new Skill(0,0,sprite-87,0,0.875f,0.375f,0,0,0,0);
									skill.x = YoloEngine.mMultislayer.Opponents_x_last[j];
									skill.y = YoloEngine.mMultislayer.Opponents_y_last[j];
									skillPlayerVe.add(skill);
									//TODO dodanie ¿ycia graczowi
								}
						}
						int k = skillPlayerVe.size();
						for(int j=0;j<k;j++)
						{	
							if(j!=i)
							if(Math.abs(skillPlayerVe.elementAt(j).x-skillPlayerVe.elementAt(i).x)<skillPlayerVe.elementAt(i).x_radius)			
								if(Math.abs(skillPlayerVe.elementAt(j).y-skillPlayerVe.elementAt(i).y)<skillPlayerVe.elementAt(i).y_radius)
								{
									Skill skill = new Skill(0,0,sprite-87,0,0.875f,0.375f,0,0,0,0);
									skill.x = skillPlayerVe.elementAt(j).x;
									skill.y = skillPlayerVe.elementAt(j).y;
									skillPlayerVe.add(skill);
									skillPlayerVe.elementAt(j).life+=skillPlayerVe.elementAt(j).damage; // TODO sprawdziæ max ¿ycie
								}
						}
						
						YoloEngine.mMultislayer.sendMessageToAllreliable(YoloEngine.mMultislayer.generateMessageFromFloats(new Float[]{skillPlayerVe.elementAt(i).x,
								skillPlayerVe.elementAt(i).y, skillPlayerVe.elementAt(i).x_radius, skillPlayerVe.elementAt(i).y_radius, skillPlayerVe.elementAt(i).damage, 
								skillPlayerVe.elementAt(i).frameDuration,(float)skillPlayerVe.elementAt(i).sprite,skillPlayerVe.elementAt(i).isLeft?1f:0f}));
						
						skillPlayerVe.remove(i--);
						continue;
					}
					else
					{
						if(!skillPlayerVe.elementAt(i).haveXY)
						{
							if(sprite  != 13 && sprite != 14 && sprite != 16 && sprite !=17)
							{
								skillPlayerVe.elementAt(i).setX();
								skillPlayerVe.elementAt(i).setY();
							}
							
							if(sprite==15)
							{
								float maxy=0;
								for(int q=0;q<YoloGameRenderer.ObjectTab.length;q++)
								{
									if(skillPlayerVe.elementAt(i).x>YoloGameRenderer.ObjectTab[q].min_x && skillPlayerVe.elementAt(i).x<YoloGameRenderer.ObjectTab[q].max_x)
									{
										if(skillPlayerVe.elementAt(i).y>=YoloGameRenderer.ObjectTab[q].max_y)
											if(YoloGameRenderer.ObjectTab[q].max_y>maxy)
												maxy = YoloGameRenderer.ObjectTab[q].max_y;
									}
								}
								skillPlayerVe.elementAt(i).y = maxy;
							}
							else if(sprite==102)
							{
								if(YoloEngine.PlayerLive+skillPlayerVe.elementAt(i).damage<YoloEngine.PLAYER_LIVE_MAX)
									YoloEngine.PlayerLive+=skillPlayerVe.elementAt(i).damage;
								skillPlayerVe.elementAt(i).sprite = 17;
							}
							else if(sprite==19)
							{
								playerFire(0.5f, 19, 8, skillPlayerVe.elementAt(i).damage, skillPlayerVe.elementAt(i).isLeft);
								skillPlayerVe.remove(i);
								continue;
							}

							skillPlayerVe.elementAt(i).haveXY = true;
						}
						
						YoloEngine.mMultislayer.sendMessageToAllreliable(YoloEngine.mMultislayer.generateMessageFromFloats(new Float[]{skillPlayerVe.elementAt(i).x,
								skillPlayerVe.elementAt(i).y, skillPlayerVe.elementAt(i).x_radius, skillPlayerVe.elementAt(i).y_radius, skillPlayerVe.elementAt(i).damage, 
								skillPlayerVe.elementAt(i).frameDuration,(float)skillPlayerVe.elementAt(i).sprite,skillPlayerVe.elementAt(i).isLeft?1f:0f}));
					}
				}
				
				if(LinearSkillDraw(gl, skillPlayerVe.elementAt(i)))
				{
					skillPlayerVe.remove(i--);
				}
			}
		}
	}
	
	
	
	private void drawOponentSkills (GL10 gl)
	{		
		int sprite;
		for(int i = 0; i < skillOponentVe.size(); i++)
		{
			sprite = skillOponentVe.elementAt(i).sprite;
			if(sprite>5&&sprite<13)
			{
				if(AIDraw(gl, i, false, skillOponentVe.elementAt(i).sprite)) //Rysuje kolejne AI
				{
					i--;continue;
				}
			}
			else//Pozosta³e skille
			{	
				if(skillOponentVe.elementAt(i).x_texture==0 && skillOponentVe.elementAt(i).y_texture==0) //XXX TODO WHAT???
				{
					skillOponentVe.elementAt(i).setX();
					skillOponentVe.elementAt(i).setY();
							
				}
		
				skillOponentVe.elementAt(i).aniSlowCounter++;
				if(skillOponentVe.elementAt(i).aniSlowCounter == skillOponentVe.elementAt(i).animation_slowdown)
				{
					skillOponentVe.elementAt(i).aniSlowCounter = -1;
					if(skillOponentVe.elementAt(i).x_texture<1)skillOponentVe.elementAt(i).x_texture+=0.125f;
					else{skillOponentVe.elementAt(i).y_texture+=0.125f; skillOponentVe.elementAt(i).x_texture=0f;}
						
					if(skillOponentVe.elementAt(i).y_texture == skillOponentVe.elementAt(i).yEnd && skillOponentVe.elementAt(i).x_texture == skillOponentVe.elementAt(i).xEnd)
					{
						skillOponentVe.remove(i);
						i--;
					}
					else
					{
						gl.glMatrixMode(GL10.GL_MODELVIEW);
						gl.glLoadIdentity();
						gl.glPushMatrix();
						gl.glScalef(1/YoloEngine.GAME_PROJECTION_X*4, 1/YoloEngine.GAME_PROJECTION_Y*4, 1f);
						gl.glTranslatef(skillOponentVe.elementAt(i).x/4f-.5f, skillOponentVe.elementAt(i).y/4f-.25f, 0f);
						gl.glColor4f(1f,1f,1f,1f);
						gl.glMatrixMode(GL10.GL_TEXTURE);
						gl.glTranslatef(skillOponentVe.elementAt(i).x_texture, skillOponentVe.elementAt(i).y_texture, 0f);
						btn.draw(gl, spriteSheets,skillOponentVe.elementAt(i).sprite);
						gl.glPopMatrix();
						gl.glLoadIdentity();
						
						switch (skillOponentVe.elementAt(i).sprite)
						{
						case 4:
							if(IsCollided(skillOponentVe.elementAt(i)))
							{
								poisoned = 300;
								YoloEngine.isPlayerPoisoned = true; 
							}
							else for(int j = 0;j<skillPlayerVe.size();j++)
									if(IsCollided(skillOponentVe.elementAt(i), skillPlayerVe.elementAt(j)))
									{
										skillPlayerVe.elementAt(j).isPoisoned = true;
										skillPlayerVe.elementAt(j).poison_duration = 300;
									}
							
							break;
						case 5:
							if(IsCollided(skillOponentVe.elementAt(i)))
								YoloEngine.PlayerLive -= 30;
							else for(int j = 0;j<skillPlayerVe.size();j++)
								if(IsCollided(skillOponentVe.elementAt(i), skillPlayerVe.elementAt(j)))
									skillPlayerVe.elementAt(j).life -= 30;
							
							break;
						}
					}
				}	
			}
		}
	}
	
	
	private void hitBox ()
	{
		for(int i = 0;i<hitBoxs.size();i++)
		{
		
			if(hitBoxs.elementAt(i).counter++ < hitBoxs.elementAt(i).duration)
			{
				if(IsCollided(hitBoxs.elementAt(i).x, hitBoxs.elementAt(i).y, hitBoxs.elementAt(i).x_radius, hitBoxs.elementAt(i).y_radius))
				{
					switch((int)hitBoxs.elementAt(i).sprite)
					{
					case 4:
						poisoned = 300;
						YoloEngine.isPlayerPoisoned = true;
						break;
					case 101:
						if(YoloEngine.PlayerLive<=0)
							YoloEngine.PlayerLive = YoloEngine.PLAYER_LIVE_MAX/2;
						break;
					case 103:
						slowDowned = 300;
						YoloEngine.isPlayerSlowDown = true;
						break;
						
					default:
						YoloEngine.PlayerLive -= hitBoxs.elementAt(i).damage;
					}
				}
					
				
				for(int j = 0; j<skillPlayerVe.size();j++)
					if(skillPlayerVe.elementAt(j).sprite >= 6 && skillPlayerVe.elementAt(j).sprite <= 12 && skillPlayerVe.elementAt(j).sprite !=10 ) 
						if(IsCollided(hitBoxs.elementAt(i),skillPlayerVe.elementAt(j)))
						{
							if(!hitBoxs.elementAt(i).hitAIs.contains(j))
							{
								switch((int)hitBoxs.elementAt(i).sprite)
								{
								case 4:
									skillPlayerVe.elementAt(j).poison_duration = 300;
									skillPlayerVe.elementAt(j).isPoisoned =true;
									break;
								case 101://jak trzymac max ¿ycie dla skilli;
									break;
								case 103:
									skillPlayerVe.elementAt(j).slowDown_duration = 300;
									skillPlayerVe.elementAt(j).isSlowDown = true;
									break;
								default:
									skillPlayerVe.elementAt(j).damage_buffor -= hitBoxs.elementAt(i).damage;
								}
								hitBoxs.elementAt(i).hitAIs.add(j);
							}
						}
			}
			else hitBoxs.remove(i);
		}
	}
	
	private int findID(int id)
	{
		int i =skillOponentVe.size()/2,p=0,k=skillOponentVe.size();
		while(skillOponentVe.elementAt(i).id != id)
		{
			if(skillOponentVe.elementAt(i).id>id)
			{
				k=i;
				i=(p+k)/2;
			}
			else
			{
				p=i;
				i=(p+k)/2;
			}
		}	
		return i;
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
		TextureLoader = new YoloTexture(gl,21);
		back.loadTexture(gl, R.drawable.aniol_tlo_loading, YoloEngine.context);
		load_back.loadTexture(gl, R.drawable.pasek_back, YoloEngine.context);
		load_front.loadTexture(gl, R.drawable.pasek_wypelnienie, YoloEngine.context);
		
//------------------------------------------INICJOWANIE OBIEKTÓW FIZYCZNYCH----------------------------------		
		YoloEngine.LEVEL_SIZE_X = YoloEngine.LEVEL_X/YoloEngine.display_x; 
		YoloEngine.LEVEL_SIZE_Y = YoloEngine.LEVEL_Y/YoloEngine.display_y; 
		YoloEngine.GAME_PROJECTION_X = YoloEngine.GAME_PROJECTION_Y*15/9;
		
		
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
		YoloEngine.sprite_load[YoloEngine.SkillSprite1<30?YoloEngine.SkillSprite1 : YoloEngine.SkillSprite1-87] = true;//Zale¿y od playera
		YoloEngine.sprite_load[YoloEngine.SkillSprite2<30?YoloEngine.SkillSprite2 : YoloEngine.SkillSprite2-87] = true;//Zale¿y od playera
		YoloEngine.sprite_load[YoloEngine.SkillSprite3<30?YoloEngine.SkillSprite3 : YoloEngine.SkillSprite3-87] = true;//Zale¿y od playera
		
		YoloEngine.mMultislayer.Opponents_x_last[0]=15f;
		YoloEngine.mMultislayer.Opponents_y_last[0]=3f;
		YoloEngine.mMultislayer.Opponents_x_last[1]=10f;
		YoloEngine.mMultislayer.Opponents_y_last[1]=5f;
		YoloEngine.mMultislayer.Opponents_x_last[2]=20f;
		YoloEngine.mMultislayer.Opponents_y_last[2]=3f;
		
		
//-----------------------------------------------------------------------------------------------------------		
		
	}

}
