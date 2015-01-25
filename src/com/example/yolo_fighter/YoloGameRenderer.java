package com.example.yolo_fighter;

import java.nio.ByteBuffer;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.google.android.gms.internal.in;

import android.opengl.GLSurfaceView.Renderer;

class HitBox extends YoloObject
{
	float damage,duration;
	int counter =0, sprite,ID;
	boolean isLeft, team,efectOnMySkill; //teamA -> 0, teamB ->1
	Vector<Integer> hitAIs = new Vector<Integer>();
	 HitBox(float x ,float y, float dx ,float dy, float damage, float duration, int sprite, boolean isLeft, boolean team, boolean efectOnMySkill, int ID)
	{
		super(x, y,(int)dx,(int)dy);
		this.damage = damage;
		this.duration = duration;
		this.isLeft = isLeft;
		this.sprite = sprite;
        this.team = team;
		this.efectOnMySkill = efectOnMySkill;
		this.ID = ID;
	}

}

class Skill extends YoloObject
{
	float x_texture=0,y_texture=0,xEnd,yEnd,xStart,yStart;
	float SkillADDX=0,SkillADDY = 0;
	float x_radius,y_radius;
	int frameCounter =0;
	float frameDuration;
	float damage,life,MAXlife,damage_buffor =0f;
	float x_oponnent, y_oponnent ;
	
	int sprite,id;
	int ret=100,j=0;
	int animation_slowdown;
	int aniSlowCounter = -1;
	int closest =0;
	int poison_duration =0,slowDown_duration =0;;
	int fire_rate = 10,fireCounter =0;
	int resurestion_count =0;
	
	boolean isLeft = false,onGround = false ,haveXY = false;
	boolean isUsed=false;
	boolean isPoisoned = false,isSlowDown = false;
    boolean team;//,effectOnMyskill; //może tu od razu = YoloEngine.playerTeam? a dla odbieranych będą przydzielane odpowiednie w konstruktorze?

    public Skill(float x, float y,int sprite , int animation_slowdown,float xEnd,float yEnd,float x_radius,float y_radius,float frameDuration,float life, boolean team)
    {
    	super(x,y);
        this.sprite = sprite;
        this.animation_slowdown = animation_slowdown;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
        this.x_radius = x_radius;
        this.y_radius = y_radius;
        this.frameDuration = frameDuration;
        this.life = this.MAXlife = life;
        this.team = team;

        id = giveID();

        if(sprite == 6)
        {
            if(YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft)
                x = (YoloEngine.TeamAB[YoloEngine.MyID].x - 2f)/4f;
            else
                x = (YoloEngine.TeamAB[YoloEngine.MyID].x + 2f)/4f;
            y = (YoloEngine.TeamAB[YoloEngine.MyID].y)/4f;

        }
    }

	public void setX()
	{
		if(YoloEngine.TeamAB[YoloEngine.MyID].x > YoloEngine.GAME_PROJECTION_X/2 +.5f && YoloEngine.TeamAB[YoloEngine.MyID].x < YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X - YoloEngine.GAME_PROJECTION_X/2 - .5f)
			SkillADDX = (((YoloEngine.TeamAB[YoloEngine.MyID].x-.5f)/YoloEngine.GAME_PROJECTION_X) - .5f)*YoloEngine.GAME_PROJECTION_X;
		else
		{
			SkillADDX = 0; 
			if(YoloEngine.TeamAB[YoloEngine.MyID].x > YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X - YoloEngine.GAME_PROJECTION_X/2 - .5f)
				x = YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X - (YoloEngine.GAME_PROJECTION_X - x);
		}
		
		x = x + SkillADDX;
	}
	public void setY()
	{
		if(YoloEngine.TeamAB[YoloEngine.MyID].y > YoloEngine.GAME_PROJECTION_Y/2 + .5f && YoloEngine.TeamAB[YoloEngine.MyID].y < YoloEngine.LEVEL_SIZE_Y*YoloEngine.GAME_PROJECTION_Y - YoloEngine.GAME_PROJECTION_Y/2 - .5f)
			SkillADDY = (((YoloEngine.TeamAB[YoloEngine.MyID].y-.5f)/YoloEngine.GAME_PROJECTION_Y) - .5f)*YoloEngine.GAME_PROJECTION_Y;
		else 
		{
			SkillADDY = 0;
			if(YoloEngine.TeamAB[YoloEngine.MyID].y > YoloEngine.LEVEL_SIZE_Y*YoloEngine.GAME_PROJECTION_Y - YoloEngine.GAME_PROJECTION_Y/2 - .5f)
				y = YoloEngine.LEVEL_SIZE_Y*YoloEngine.GAME_PROJECTION_Y - (YoloEngine.GAME_PROJECTION_Y - y);
		}
		
		y = y + SkillADDY;
	}
	
	public void move ()
	{
//-------------------------------------------------------------------Szukanie najbli�szego------------------------------		
		if(ret == 100)
		{ 
			float minLenght = 10000f,temp;
			if(team == YoloEngine.TeamB)
				for(int i=0;i<YoloEngine.TeamSize;i++)
				{
					temp = Math.abs(x-YoloEngine.TeamAB[i].x)*Math.abs(x-YoloEngine.TeamAB[i].x) +
							Math.abs(y-YoloEngine.TeamAB[i].y)*Math.abs(y-YoloEngine.TeamAB[i].y);
					if(temp<minLenght)
					{
						minLenght = temp;
						closest = i;
					}
				}
			else
				for(int i=2;i<YoloEngine.TeamAB.length;i++)
				{
					temp = Math.abs(x-YoloEngine.TeamAB[i].x)*Math.abs(x-YoloEngine.TeamAB[i].x) +
							Math.abs(y-YoloEngine.TeamAB[i].y)*Math.abs(y-YoloEngine.TeamAB[i].y);
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
			x_oponnent = YoloEngine.TeamAB[closest].x; 
			y_oponnent = YoloEngine.TeamAB[closest].y;
		 
		}
//--------------------------------------------------------------------GRAWITANCJA-------------------------------------------	
		vy -= YoloEngine.GAME_ACCELERATION;
		y += vy;
		if(sprite!=10&&sprite!=13&&sprite!=14)
		for(int i = j; i < YoloGameRenderer.ObjectTab.length; i++)
		{
			if(YoloGameRenderer.IsCollidedTop(this,YoloGameRenderer.ObjectTab[i]))
			{
					y = YoloGameRenderer.ObjectTab[i].y + YoloGameRenderer.ObjectTab[i].dy;
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
				if(YoloGameRenderer.IsCollidedTop(this,YoloGameRenderer.ObjectTab[i]))//x-1.5f,y,vy))
				{
						y = YoloGameRenderer.ObjectTab[i].y + YoloGameRenderer.ObjectTab[i].dy;
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
			else
				isPoisoned = false;
			if(slowDown_duration >0)
				slowDown_duration--;
			else
				isSlowDown = false;
			
			if(life<0)
			{
				ret=YoloEngine.ARCHER_DYING;
				if(!isLeft)
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
			else
			if(ret==YoloEngine.ARCHER_HURT)
			{
				if(!isLeft)
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
						if(x - YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES > YoloGameRenderer.ObjectTab[j].x)
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
						if(x + YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES<YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx)
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
							if(x - YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES > YoloGameRenderer.ObjectTab[j].x)
								if(isSlowDown)
									x -= YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x -= YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x = YoloGameRenderer.ObjectTab[j].x;
								x_texture = xStart = xEnd = 0.75f;
								y_texture = yStart = yEnd = 0.375f;
								ret = YoloEngine.ARCHER_STAND;
							}
						else
							if(x + YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES < YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx)
								if(isSlowDown)
									x += YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x += YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x = YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx-1f;
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
			else
				isPoisoned = false;
			if(slowDown_duration >0)
				slowDown_duration--;
			else
				isSlowDown = false;
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
			else
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
						if(x - YoloEngine.WARRIOR_SPEED *YoloEngine.GAME_SKIPED_FRAMES > YoloGameRenderer.ObjectTab[j].x)
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
						if(x + YoloEngine.WARRIOR_SPEED *YoloEngine.GAME_SKIPED_FRAMES<YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx)
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
							if(x - YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES > YoloGameRenderer.ObjectTab[j].x)
								if(isSlowDown)
									x -= YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x -= YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x = YoloGameRenderer.ObjectTab[j].x;
								x_texture = xStart = xEnd = 0.375f;
								y_texture = yStart = yEnd = 0.375f;
								ret = YoloEngine.WARRIOR_STAND;
							}
						else
							if(x + YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES +1f < YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx)
								if(isSlowDown)
									x += YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x += YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x= YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx-1f;
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
			else
				isPoisoned = false;
			if(slowDown_duration >0)
				slowDown_duration--;
			else
				isSlowDown = false;
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
			else
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
						if(x - YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES > YoloGameRenderer.ObjectTab[j].x)
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
						if(x + YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES<YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx)
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
							if(x - YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES> YoloGameRenderer.ObjectTab[j].x)
								if(isSlowDown)
									x -= YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x -= YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x = YoloGameRenderer.ObjectTab[j].x;
								x_texture = xStart = xEnd = 0.875f;
								y_texture = yStart = yEnd = 0.25f;
								ret = YoloEngine.MUMMY_STAND;
							}
						else
							if(x + YoloEngine.MUMMY_SPEED*YoloEngine.GAME_SKIPED_FRAMES + 1f< YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx)
								if(isSlowDown)
									x += YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x += YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x = YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx-1f;
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
			else
				isPoisoned = false;
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
			else
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
			
			if(ret ==100)
			{
				x_texture = xStart = 0f;
				y_texture = yStart = yEnd = 0f;
				xEnd = 0.875f;
				ret = YoloEngine.WALL_NEW;
			}
			if(ret != YoloEngine.WALL_NEW)
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
		case 13:
			if(life<0)
			{
				x_texture = xStart =0; xEnd = 0.875f;
				y_texture = yStart = yEnd = 0f;
				ret = YoloEngine.TRAP_ATTACK;
			}
			else
			if(ret!=YoloEngine.TRAP_ATTACK)
			if(y_oponnent + 1 >y-y_radius/2 && y_oponnent < y + y_radius/2 && x_oponnent + 1  > x-x_radius/2 && x_oponnent < x+x_radius/2)
			{
				x_texture = xStart =0; xEnd = 0.875f;
				y_texture = yStart = yEnd = 0f;
				ret = YoloEngine.TRAP_ATTACK;
			}
			else if(ret!=YoloEngine.TRAP_STAND)
			{
				x_texture = xStart = 0; xEnd = 0.875f;
				y_texture = yStart = 0; yEnd = 0.875f;
				ret = YoloEngine.TRAP_STAND;
			}
			break;
		case 14:
			if(life<0)
				ret = YoloEngine.WARMTH_DYING;
			else
			if(ret!=YoloEngine.WARMTH_ATACK)
				if(y_oponnent + 1 >y-y_radius/2 && y_oponnent < y + y_radius/2 && x_oponnent + 1  > x-x_radius/2 && x_oponnent < x+x_radius/2)
				{
					x_texture = xStart =0; xEnd = 0.875f;
					y_texture = yStart = yEnd = 0f;
					ret = YoloEngine.WARMTH_ATACK;
				}
				else if(ret!=YoloEngine.WARMTH_STAND)
				{
					x_texture = xStart =0; xEnd = 0.875f;
					y_texture = yStart = yEnd = 0f;
					ret = YoloEngine.WARMTH_STAND;
				}
			break;
		}
	}
	
	private int giveID()
	{
		int ID = YoloEngine.IDTracer + YoloEngine.playerID;
		YoloEngine.IDTracer+=YoloEngine.opponents.size()+1;
		
        YoloEngine.mMultislayer.sendTracerIncrease(YoloEngine.IDTracer);//info o zwiekszniu
		return ID;
	}

    public byte[] serializeSkill()
    {
        ByteBuffer bbf = ByteBuffer.allocate(50);
        bbf.putChar('s');
        bbf.putFloat(x);
        bbf.putFloat(y);
        bbf.putInt(sprite);
        bbf.putInt(animation_slowdown);
        bbf.putFloat(xEnd);
        bbf.putFloat(yEnd);
        bbf.putFloat(x_radius);
        bbf.putFloat(y_radius);
        bbf.putFloat(frameDuration);
        bbf.putFloat(life);
        if(team)
            bbf.put((byte)1);
        else
            bbf.put((byte)0);

        return bbf.array();
    }
}

public class YoloGameRenderer implements Renderer {
	
	private YoloTexture TextureLoader ;
	private YoloBackground back= new YoloBackground(),load_back=new YoloBackground(),load_front = new YoloBackground();
	private YoloWeapon btn = new YoloWeapon(0,0);
	
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
    float half_fx,half_bx,half_fy,half_by ;
	
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
	
	public static boolean toLoad = true,first = false;
	private int loading_faze=0,loadingStepsCout = 30;
	
	
	private int nextBullet = 0;
	private boolean onGround = true;
	private int ClimbingOn;
	private int S1cooldown = 0,S2cooldown = 0,S3cooldown = 0;
				
	private long loopStart = 0;
	private long loopEnd = 0;
	private long loopRunTime = 0;

	@Override
	public void onDrawFrame(GL10 gl) {
		loopStart = System.currentTimeMillis();
		//====================================================LOADING===============================================================
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		if(toLoad && first)
			Load(gl);
		else
			drawLoadingSrean(gl,0.1f);
		first = true;
		if(!toLoad)
		{
			//--------------------------------------------------GRAVITANCJA-------------------------------------------------------------
			YoloEngine.TeamAB[YoloEngine.MyID].y += YoloEngine.TeamAB[YoloEngine.MyID].vy;
			if(YoloEngine.TeamAB[YoloEngine.MyID].canMove)
			{
				YoloEngine.TeamAB[YoloEngine.MyID].vy -= YoloEngine.GAME_ACCELERATION;
				
				for(int i = 0; i < ObjectTab.length; i++)
				{
					if(IsCollidedTop(YoloEngine.TeamAB[YoloEngine.MyID],ObjectTab[i]))
					{
						if(YoloEngine.TeamAB[YoloEngine.MyID].vy > 0)
							{
								onGround = false;
							}
						else
							{
								YoloGame.flying = YoloEngine.TeamAB[YoloEngine.MyID].isPlayerFlying?10:2;
								YoloEngine.TeamAB[YoloEngine.MyID].y = ObjectTab[i].y + ObjectTab[i].dy;
								YoloEngine.TeamAB[YoloEngine.MyID].vy = 0;
								onGround = true;
							}
						break;
					}
					onGround = false;
				}
			}
			
			
			//-------------------------------------------TARCIE-----------------------------------------------------------------
			
			
			if(!YoloEngine.TeamAB[YoloEngine.MyID].isMoving && YoloEngine.TeamAB[YoloEngine.MyID].vx != 0)
			{
				if(onGround)
				{
					if(YoloEngine.TeamAB[YoloEngine.MyID].vx>YoloEngine.GAME_GROUND_FRICTION *2) YoloEngine.TeamAB[YoloEngine.MyID].vx -= YoloEngine.GAME_GROUND_FRICTION;
					else if(YoloEngine.TeamAB[YoloEngine.MyID].vx<-YoloEngine.GAME_GROUND_FRICTION *2 )YoloEngine.TeamAB[YoloEngine.MyID].vx += YoloEngine.GAME_GROUND_FRICTION;
					else YoloEngine.TeamAB[YoloEngine.MyID].vx = 0;
				}
				else
				{
					if(YoloEngine.TeamAB[YoloEngine.MyID].vx>YoloEngine.GAME_AIR_FRICTION *2) YoloEngine.TeamAB[YoloEngine.MyID].vx -= YoloEngine.GAME_AIR_FRICTION;
					else if(YoloEngine.TeamAB[YoloEngine.MyID].vx<-YoloEngine.GAME_AIR_FRICTION *2 )YoloEngine.TeamAB[YoloEngine.MyID].vx += YoloEngine.GAME_AIR_FRICTION;
					else YoloEngine.TeamAB[YoloEngine.MyID].vx = 0;
				}
			}
			
			if(YoloEngine.TeamAB[YoloEngine.MyID].canMove)
				if(YoloEngine.TeamAB[YoloEngine.MyID].isPlayerSlowDown && YoloEngine.TeamAB[YoloEngine.MyID].slowDowned != 0)
					YoloEngine.TeamAB[YoloEngine.MyID].x += YoloEngine.TeamAB[YoloEngine.MyID].vx/2f;
				else
					YoloEngine.TeamAB[YoloEngine.MyID].x += YoloEngine.TeamAB[YoloEngine.MyID].vx;
			
			
	//------------------------------------------------------DARBINY---------------------------------------------------------------FIXME Drabiny
			for (int i = 0;i < LaddreTab.length;i++)
			{
				if(IsCollided(YoloEngine.TeamAB[YoloEngine.MyID],LaddreTab[i]))
				{
					YoloEngine.canClimb = true;
					if(YoloEngine.TeamAB[YoloEngine.MyID].isClimbingUp)
					{
						ClimbingOn = i;
						YoloEngine.TeamAB[YoloEngine.MyID].vy = YoloEngine.PLAYER_CLIMBING_SPEED;
					}
					else if(YoloEngine.TeamAB[YoloEngine.MyID].isClimbingDown)
						{
							ClimbingOn = i;
							YoloEngine.TeamAB[YoloEngine.MyID].vy = -YoloEngine.PLAYER_CLIMBING_SPEED;
						}
					
					break;
				}
				YoloEngine.canClimb = false;
				
			}
			if(YoloEngine.TeamAB[YoloEngine.MyID].isClimbingDown || YoloEngine.TeamAB[YoloEngine.MyID].isClimbingUp)
			{
				if(!IsCollided(YoloEngine.TeamAB[YoloEngine.MyID],LaddreTab[ClimbingOn]))
				{
					YoloEngine.TeamAB[YoloEngine.MyID].canMove = true;
					YoloEngine.TeamAB[YoloEngine.MyID].isClimbingDown = false;
					YoloEngine.TeamAB[YoloEngine.MyID].isClimbingUp = false;
					YoloEngine.TeamAB[YoloEngine.MyID].vy = 0;
				}
			}
	
// ------------------------- Multislayer BEGIN -----------------------	

			if (YoloEngine.MULTI_ACTIVE) {
                YoloEngine.mMultislayer.sendPlayerPosition(YoloEngine.TeamAB[YoloEngine.MyID].x, YoloEngine.TeamAB[YoloEngine.MyID].y, YoloEngine.TeamAB[YoloEngine.MyID].isCrouch);
			}	
			for(int i = 0; i < YoloEngine.opponents.size(); i++) {

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

                    if(YoloEngine.teamA.contains(YoloEngine.opponents.get(i))) {
                        YoloEngine.TeamA_x[YoloEngine.teamA.indexOf(YoloEngine.opponents.get(i))] = YoloEngine.Opponents_x[i];
                        YoloEngine.TeamA_y[YoloEngine.teamA.indexOf(YoloEngine.opponents.get(i))] = YoloEngine.Opponents_y[i];
                        YoloEngine.TeamA_isCrouched[YoloEngine.teamA.indexOf(YoloEngine.opponents.get(i))] = YoloEngine.Opponent_isCrouched[i];
                    }
                    else {
                        YoloEngine.TeamB_x[YoloEngine.teamB.indexOf(YoloEngine.opponents.get(i))] = YoloEngine.Opponents_x[i];
                        YoloEngine.TeamB_y[YoloEngine.teamB.indexOf(YoloEngine.opponents.get(i))] = YoloEngine.Opponents_y[i];
                        YoloEngine.TeamB_isCrouched[YoloEngine.teamB.indexOf(YoloEngine.opponents.get(i))] = YoloEngine.Opponent_isCrouched[i];
                    }

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
			
			//-------------------------------------------------------SKILLS------------------------------------------------------------
			YoloEngine.GAME_SKIPED_FRAMES = (int) (loopRunTime/YoloEngine.GAME_THREAD_FSP_SLEEP + 1);
			
			if(YoloEngine.canSkill1 == false)S1cooldown++;
			if(YoloEngine.canSkill2 == false)S2cooldown++;
			if(YoloEngine.canSkill3 == false)S3cooldown++;
			
			if(YoloEngine.SKILL1_COOLDOWN == S1cooldown){S1cooldown = 0; YoloEngine.canSkill1 = true;}
			if(YoloEngine.SKILL2_COOLDOWN == S2cooldown){S2cooldown = 0; YoloEngine.canSkill2 = true;}
			if(YoloEngine.SKILL3_COOLDOWN == S3cooldown){S3cooldown = 0; YoloEngine.canSkill3 = true;}
		
			//--------------------------------------------------------------------------------------------------------------------------	
			
			drawBackground(gl);
			drawPlayerSkills(gl);	
			moveBullets(gl);
			if(YoloEngine.playerTeam == YoloEngine.TeamA) 
			{
				YoloEngine.TeamAB[0].drawAlly(gl,YoloEngine.MyID==0?false:true);
				YoloEngine.TeamAB[1].drawAlly(gl,YoloEngine.MyID==1?false:true);
			}
			else
			{
				YoloEngine.TeamAB[2].drawAlly(gl,YoloEngine.MyID==2?false:true);
				YoloEngine.TeamAB[3].drawAlly(gl,YoloEngine.MyID==3?false:true);
			}
			drawOponentSkills(gl);
			hitBox();
			if(YoloEngine.playerTeam == YoloEngine.TeamA)
			{
				YoloEngine.TeamAB[2].drawOpponent(gl);
				YoloEngine.TeamAB[3].drawOpponent(gl);
			}
			else
			{
				YoloEngine.TeamAB[0].drawOpponent(gl);
				YoloEngine.TeamAB[1].drawOpponent(gl);
			}
			if(YoloEngine.TeamAB[YoloEngine.MyID].isShoting)playerFire(0.5f);
			else nextBullet = 0;
			drawControls(gl);
			drawButtons(gl);
		}

		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		loopEnd = System.currentTimeMillis();
		loopRunTime = (loopEnd - loopStart);

	}
	public static boolean IsCollidedTop(YoloObject object1,YoloObject object2)
	{
		if(object1.x + object1.dx < object2.x || object1.x > object2.x + object2.dx)return false;
		if(object1.y < object2.y + object2.dy && object1.y - object1.vy < object2.y + object2.dy)return false;
		if(object1.y > object2.y + object2.dy && object1.y - object1.vy > object2.y + object2.dy)return false;
		return true;
	}
	private boolean IsCollided(YoloObject object1, YoloObject object2)
	{
		if(object1.x + object1.px + object1.dx < object2.x || object1.x + object1.px > object2.x + object2.dx)return false;
		if(object1.y + object1.px + object1.dx < object2.y || object1.y + object1.px > object2.y + object2.dy)return false;
		
		return true;
	}
	private void drawBullet(GL10 gl, YoloWeapon bullet)
	{
		if(bullet.count ==0)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(bullet.x, bullet.y, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			if(bullet.isLeft)gl.glTranslatef(bullet.x_texture, bullet.y_texture, 0f);
			else gl.glTranslatef(bullet.x_texture + .125f, bullet.y_texture, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			bullet.draw(gl,YoloEngine.spriteSheets,bullet.sprite);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		else
			
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(bullet.x, bullet.y, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			if(bullet.isLeft)gl.glTranslatef(bullet.x_texture, bullet.y_texture, 0f);
			else gl.glTranslatef(bullet.x_texture, bullet.y_texture+.125f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			bullet.draw(gl,YoloEngine.spriteSheets,bullet.sprite);
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
			
//---------------------------------------Bullet Collision--------------------------------------------------------------
			if(Weapontab.get(i).x + 1f < 0)
			{
				Weapontab.remove(i--);
				continue;
			}
			else if(Weapontab.get(i).x > YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X)
			{
				Weapontab.remove(i--);
				continue;
			}
			else 
			{
				for(int j=0;j<YoloEngine.TeamAB.length;j++)
				{
					if(Weapontab.get(i).team != YoloEngine.TeamAB[j].playerTeam)
					{
						if(IsCollided(Weapontab.get(i),YoloEngine.TeamAB[j]))
						{
							if(!YoloEngine.TeamAB[j].isPlayerInvincible)
							{
								if(YoloEngine.TeamAB[j].isPlayerDenialed)
								{
									Weapontab.get(i).isLeft = !Weapontab.get(i).isLeft;
									Weapontab.get(i).team = !Weapontab.get(i).team;
								}
								else
								{
									YoloEngine.TeamAB[j].PlayerLive -= Weapontab.get(i).damage*YoloEngine.TeamAB[j].Player_Dmg_reduction;
									Weapontab.remove(i--);
									continue out;
								}
							}
							else
							{
								Weapontab.remove(i--);
								continue out;
							}						
						}
					}
				}
				
				for (int j = 0 ; j< ObjectTab.length ; j++)
					if (IsCollided(Weapontab.get(i), ObjectTab[j])) 
					{
						Weapontab.remove(i--);
						continue out;
					}
				
				if(Weapontab.get(i).team == YoloEngine.playerTeam)
				{
					for (int x =0 ;x < skillPlayerVe.size();x++)
						if(skillPlayerVe.elementAt(x).sprite>=6 && skillPlayerVe.elementAt(x).sprite <= 12 )
							if(IsCollided(Weapontab.get(i), skillPlayerVe.elementAt(x)))
							{
								
								skillPlayerVe.elementAt(x).damage_buffor += Weapontab.get(i).damage;
								skillPlayerVe.elementAt(x).ret = 3;
								Weapontab.remove(i--);
								continue out;
							}
				}
				else
				{
					for (int x =0 ;x < skillOponentVe.size();x++)
						if(skillOponentVe.elementAt(x).sprite>=6 && skillOponentVe.elementAt(x).sprite <= 12 )
							if(IsCollided(Weapontab.get(i), skillOponentVe.elementAt(x)))
							{
								
								skillOponentVe.elementAt(x).damage_buffor += Weapontab.get(i).damage;
								skillOponentVe.elementAt(x).ret = 3;
								Weapontab.remove(i--);
								continue out;
							}
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
		btn.draw(gl,YoloEngine.spriteSheets,1);
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

		LIVE_BAR_SIZE_X_1 = LIVE_BAR_SIZE_X_0*YoloEngine.TeamAB[YoloEngine.MyID].PlayerLive/YoloEngine.TeamAB[YoloEngine.MyID].PLAYER_LIVE_MAX; // dobrze;
		drawSt(gl, liveBarX_0 + XADD, liveBarY +YADD, LIVE_BAR_SIZE_X_0, LIVE_BAR_SIZE_Y, .75f, .125f,true);
		drawSt(gl, liveBarX_0 + XADD, liveBarY +YADD, LIVE_BAR_SIZE_X_1,LIVE_BAR_SIZE_Y, .875f, .125f,false);
	}
	
	private void drawButtons(GL10 gl)
	{
		if(YoloEngine.isClasic)
		{
			drawSt(gl, jumpBtnX + XADD, jumpBtnY + YADD, MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, YoloEngine.TeamAB[YoloEngine.MyID].isJumping? 0 : .125f, 0, true);
			if(YoloEngine.TeamAB[YoloEngine.MyID].isCrouch)drawSt(gl, crouchBtnX + XADD, crouchBtnY + YADD,MOVE_BALL_SIZE_X*2,  MOVE_SIZE_Y*2, YoloEngine.isCrouch_prest? .75f : .875f, 0, true);
			else drawSt(gl, crouchBtnX + XADD, crouchBtnY + YADD,MOVE_BALL_SIZE_X*2,  MOVE_SIZE_Y*2, YoloEngine.isCrouch_prest? .5f : .625f, 0, true);
		}
		drawSt(gl, shotBtnX + XADD, shotBtnY + YADD, MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, YoloEngine.TeamAB[YoloEngine.MyID].isShoting? .25f : .375f , 0, true);

		drawSt(gl, skillBtnX + XADD - 100f/YoloEngine.display_x, YADD, MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, YoloEngine.canSkill1? 0 : .125f, .125f, true);
		drawSt(gl, skillBtnX + XADD, YADD, MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, YoloEngine.canSkill2? 0 : .125f, .125f, true);
		drawSt(gl, skillBtnX + XADD + 100f/YoloEngine.display_x, YADD, MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, YoloEngine.canSkill3? 0 : .125f, .125f, true);
	}
	
	private void drawBackground(GL10 gl)
	{
		if(YoloEngine.TeamAB[YoloEngine.MyID].x > half_fx && YoloEngine.TeamAB[YoloEngine.MyID].x < YoloEngine.LEVEL_X/YoloEngine.TX -  half_bx  )
		{
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0f, 1f, 0f, 1f, -1f, 1f);
			XADD = ((YoloEngine.TeamAB[YoloEngine.MyID].x-half_fx)*YoloEngine.TEXTURE_SIZE_X);
			cameraPosX = -XADD;

			gl.glTranslatef(cameraPosX,cameraPosY,0f);
			
		}
		if(YoloEngine.TeamAB[YoloEngine.MyID].y > half_fy && YoloEngine.TeamAB[YoloEngine.MyID].y < YoloEngine.LEVEL_Y/YoloEngine.TY -  half_by)
		{
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0f, 1f, 0f, 1f, -1f, 1f);
			YADD = ((YoloEngine.TeamAB[YoloEngine.MyID].y-half_fy)*YoloEngine.TEXTURE_SIZE_Y);
			cameraPosY = -YADD ;

			gl.glTranslatef(cameraPosX,cameraPosY,0f);
		}
		if(YoloEngine.TeamAB[YoloEngine.MyID].x < -0.5f)
		{
			YoloEngine.TeamAB[YoloEngine.MyID].x = YoloEngine.LEVEL_X/YoloEngine.TX-0.5f;
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0f, 1f, 0f, 1f, -1f, 1f);
			
			XADD = (YoloEngine.LEVEL_X/YoloEngine.TX-half_bx+0.5f)*YoloEngine.TEXTURE_SIZE_X- 0.5f;
			cameraPosX = -XADD;

			gl.glTranslatef(cameraPosX,cameraPosY,0f);
		}
		else if(YoloEngine.TeamAB[YoloEngine.MyID].x > YoloEngine.LEVEL_X/YoloEngine.TX-0.5f)
			{
			YoloEngine.TeamAB[YoloEngine.MyID].x = -0.5f;
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
		gl.glLoadIdentity();
		gl.glPushMatrix();
		back.draw(gl);
		gl.glPopMatrix();
		gl.glLoadIdentity();
	
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glTranslatef(1f/8f, 1f/12f, 0f);
		gl.glScalef(1f/1.3f,1f/6f,1f);
		load_back.draw(gl);
		gl.glPopMatrix();
		if(percet != 0)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glTranslatef(1f/8f, 1f/12f, 0f);
			gl.glScalef(percet*(1f/1.3f),1f/6f,1f);
			load_front.draw(gl);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
	}
	private void Load(GL10 gl)
	{
		switch(loading_faze)
		{
		case 0:
			YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.PLAYER_TEXTURE, YoloEngine.context, 2);
			drawLoadingSrean(gl, 0f/loadingStepsCout);
			break;
		case 1:
			YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.OPPONENT_TEXTURE, YoloEngine.context, 3); // Multislayer
			drawLoadingSrean(gl, 1f/loadingStepsCout);
			break;
		case 2:			
			YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.WEAPON_SPRITE, YoloEngine.context, 0);
			drawLoadingSrean(gl, 2f/loadingStepsCout);
			break;
		case 3:
			YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.BUTTON_TEXTURE, YoloEngine.context, 1);
			drawLoadingSrean(gl, 3f/loadingStepsCout);
			break;
		case 4:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.POISON_SKILL, YoloEngine.context, 4);
			drawLoadingSrean(gl, 4f/loadingStepsCout);
			break;
		case 5:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.THUNDER_V_SKILL, YoloEngine.context, 5);
			drawLoadingSrean(gl, 5f/loadingStepsCout);
			break;
		case 6:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.ARCHER_SPRITE, YoloEngine.context, 6);
			drawLoadingSrean(gl, 6f/loadingStepsCout);
			break;
		case 7:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.WARRIOR_SPRITE, YoloEngine.context, 7);
				drawLoadingSrean(gl, 7f/loadingStepsCout);
			break;
		case 8:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.MUMMY_SPRITE, YoloEngine.context, 8);
				drawLoadingSrean(gl, 8f/loadingStepsCout);
			break;
		case 9:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.HAND_SPRITE, YoloEngine.context, 9);
				drawLoadingSrean(gl, 9f/loadingStepsCout);
			break;
		case 10:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.BARREL_SPRITE, YoloEngine.context, 10);
				drawLoadingSrean(gl, 10f/loadingStepsCout);
			break;
		case 11:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.TOWER_SPRITE, YoloEngine.context, 11);
				drawLoadingSrean(gl, 11f/loadingStepsCout);
			break;
		case 12:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.WALL_SPRITE, YoloEngine.context, 12);
				drawLoadingSrean(gl, 12f/loadingStepsCout);
			break;
		case 13:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.TRAP_SPRITE, YoloEngine.context, 13);
				drawLoadingSrean(gl, 13f/loadingStepsCout);
			break;
		case 14:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.HEAL_LONG_RAD_SPRITE, YoloEngine.context, 14);
				drawLoadingSrean(gl, 14f/loadingStepsCout);
			break;
		case 15:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.SPIKES_SPRITE, YoloEngine.context, 15);
				drawLoadingSrean(gl, 15f/loadingStepsCout);
			break;
		case 16:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.SLOW_DOWN_SPRITE, YoloEngine.context, 16);
				drawLoadingSrean(gl, 16f/loadingStepsCout);
			break;
		case 17:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.HEAL_SPRITE, YoloEngine.context, 17);
				drawLoadingSrean(gl, 17f/loadingStepsCout);
			break;
		case 18:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.SHOCK_WAVE_SPRITE, YoloEngine.context, 18);
				drawLoadingSrean(gl, 18f/loadingStepsCout);
			break;
		case 19:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.ICICLE_SPRITE, YoloEngine.context, 19);
				drawLoadingSrean(gl, 19f/loadingStepsCout);
			break;
		case 20:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.FOG_SPRITE, YoloEngine.context, 20);
				drawLoadingSrean(gl, 20f/loadingStepsCout);
			break;
		case 21:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.LIVE_DRAIN_SPRITE, YoloEngine.context, 21);
				drawLoadingSrean(gl, 21f/loadingStepsCout);
			break;
		case 22:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.RESURECTION_SPRITE, YoloEngine.context, 22);
				drawLoadingSrean(gl, 22f/loadingStepsCout);
			break;
		case 26:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.THUNDER_H_SPRITE, YoloEngine.context, 26);
				drawLoadingSrean(gl, 26f/loadingStepsCout);
			break;
		case 27:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.HEAL_LONG_SPRITE, YoloEngine.context, 27);
				drawLoadingSrean(gl, 27f/loadingStepsCout);
			break;
		case 28:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.DENIAL_SPRITE, YoloEngine.context, 28);
				drawLoadingSrean(gl, 28f/loadingStepsCout);
			break;
		case 29:
			if(YoloEngine.sprite_load[loading_faze])
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.STAMINA_THIEF_SPRITE, YoloEngine.context, 29);
				drawLoadingSrean(gl, 29f/loadingStepsCout);
			break;
			//TODO Loading texturek skilli
			
		case 30:
			back.loadTexture(gl, YoloEngine.BACKGROUND, YoloEngine.context);
			toLoad = false;
			break;
		}
		loading_faze ++;
	}
	
	private void playerFire(float bulletSpeed)
	{
		if(nextBullet == 0)
		{
			bullet = new YoloWeapon(YoloEngine.TeamAB[YoloEngine.MyID].x,
					!YoloEngine.TeamAB[YoloEngine.MyID].isCrouch?YoloEngine.TeamAB[YoloEngine.MyID].y+0.2f:YoloEngine.TeamAB[YoloEngine.MyID].y - .1f,bulletSpeed);
			bullet.damage = 1f;
			bullet.team = YoloEngine.playerTeam; 
			bullet.sprite = 0;
			bullet.x_texture = 0f;
			bullet.y_texture = 0f;
			//bullet.size = 0.25f;
			bullet.isLeft = YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft;
			Weapontab.add(bullet);
			nextBullet = YoloEngine.TeamAB[YoloEngine.MyID].PLAYER_BULLET_FREQUENCY;

            if(YoloEngine.MULTI_ACTIVE)
                YoloEngine.mMultislayer.sendOpponentFire(bullet.x, bullet.y, YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft, YoloEngine.TeamAB[YoloEngine.MyID].isCrouch, bullet.sprite, bullet.count, bullet.damage, YoloEngine.TeamAB[YoloEngine.MyID].playerTeam);
		}
		nextBullet--;
	}
	private void playerFire(float bulletSpeed,int sprite,int count,float damage)
	{
		bullet = new YoloWeapon(YoloEngine.TeamAB[YoloEngine.MyID].x,
				!YoloEngine.TeamAB[YoloEngine.MyID].isCrouch?YoloEngine.TeamAB[YoloEngine.MyID].y+0.2f:YoloEngine.TeamAB[YoloEngine.MyID].y - .1f,bulletSpeed);
			bullet.damage = 1f;
			bullet.team = YoloEngine.playerTeam; 
			bullet.sprite = sprite;
			bullet.x_texture = 0f;
			bullet.y_texture = 0f;
		//	bullet.size = 0.25f;
			bullet.isLeft = YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft;
			Weapontab.add(bullet);
			
			if(YoloEngine.MULTI_ACTIVE)
				YoloEngine.mMultislayer.sendOpponentFire(bullet.x, bullet.y, YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft, YoloEngine.TeamAB[YoloEngine.MyID].isCrouch, sprite, count, damage, YoloEngine.TeamAB[YoloEngine.MyID].playerTeam);
	}
	
	
	public static void OpponentFire(float x, float y, boolean isLeft, boolean isCrouch,int sprite,int count,float damage, boolean team) //XXX oppfire nie potrzebuje isCrouch
	{
		bullet = new YoloWeapon(x,y,0.2f);
		bullet.damage = damage;
		bullet.count = count;
		bullet.team = team;
		bullet.sprite = sprite;
		bullet.x_texture = 0f;
		bullet.y_texture = 0f;
		//bullet.size = 0.25f;
		bullet.isLeft = isLeft;
		Weapontab.add(bullet);
	}
	
	
	public static void AIFire(float x,float y,boolean isLeft,int sprite,float x_texture,float y_texture, float damage, boolean team)
	{
		bullet = new YoloWeapon(x,y,0.2f);//FIXME AI bullet speed
		bullet.damage = damage;
		bullet.team = team;
		bullet.sprite = sprite;
		bullet.x_texture = x_texture ;
		bullet.y_texture = y_texture;
	//	bullet.size = 0.1f;
//		bullet.scale = 4f;
		bullet.isLeft = isLeft;
		Weapontab.add(bullet);
		if(YoloEngine.MULTI_ACTIVE)
            YoloEngine.mMultislayer.sendAIFire(x, y, isLeft, x_texture, y_texture, damage, team);
	}
	
	private boolean AIDraw(GL10 gl,int i,boolean isMy,int sprite)
	{
		Vector<Skill> Ve;
		if(isMy)
		{
			Ve = skillPlayerVe;
			if(!Ve.elementAt(i).haveXY)
			{
				if(YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft)
					Ve.elementAt(i).x = YoloEngine.TeamAB[YoloEngine.MyID].x ;
				else
					Ve.elementAt(i).x = YoloEngine.TeamAB[YoloEngine.MyID].x + 1f;
				if(sprite == 11 || sprite == 12)
				{
					float maxy=0;
					for(int q=0;q<ObjectTab.length;q++)
					{
						if(YoloEngine.TeamAB[YoloEngine.MyID].x>ObjectTab[q].x && YoloEngine.TeamAB[YoloEngine.MyID].x<ObjectTab[q].x + ObjectTab[q].dx)
						{
							if(YoloEngine.TeamAB[YoloEngine.MyID].y>=ObjectTab[q].y + ObjectTab[i].dy)
								if(ObjectTab[q].y + ObjectTab[i].dy>maxy)
									maxy = ObjectTab[q].y + ObjectTab[i].dy;
						}
					}
					Ve.elementAt(i).y = maxy;
				}
				else Ve.elementAt(i).y = YoloEngine.TeamAB[YoloEngine.MyID].y + 2f;
				
				if(sprite == 10)
				{
					Ve.elementAt(i).isLeft = YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft;
					Ve.elementAt(i).x++;
				}
				
				YoloEngine.mMultislayer.sendMessageToAllreliable(skillPlayerVe.elementAt(i).serializeSkill());
				
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
					if(Ve.elementAt(i).resurestion_count++ > 100)
						Ve.remove(i);
					return true;
				}		
				
				Ve.elementAt(i).x_texture = Ve.elementAt(i).xStart;
				Ve.elementAt(i).y_texture = Ve.elementAt(i).yStart;
				
				if(isMy)
				{
//-------------------------------------------------Tworzenie HitBox�w----------------------------------------------------------------------------
					Ve.elementAt(i).frameCounter=0;
					switch (sprite)
					{
					case 6:
						if(Ve.elementAt(i).ret == YoloEngine.ARCHER_FIRE)
						{
							AIFire(Ve.elementAt(i).x, Ve.elementAt(i).y, Ve.elementAt(i).isLeft,6,0f,.5f,Ve.elementAt(i).damage,YoloEngine.TeamAB[YoloEngine.MyID].playerTeam);
							Ve.elementAt(i).ret = YoloEngine.ARCHER_NULL;
						}
						break;
					/*case 10:
						if(Ve.elementAt(i).ret == YoloEngine.BARREL_ATTACK)
						{
							Ve.remove(i);
							return true;
						}
						Ve.elementAt(i).ret = YoloEngine.BARREL_STAND;
						break;*/
					case 11:
						if(Ve.elementAt(i).ret == YoloEngine.TOWER_FIRE)
						{
							Ve.elementAt(i).fireCounter+=YoloEngine.GAME_SKIPED_FRAMES;
							if(Ve.elementAt(i).fireCounter >= Ve.elementAt(i).fire_rate)
							{
								AIFire(Ve.elementAt(i).x, Ve.elementAt(i).y, Ve.elementAt(i).isLeft,11,0f,.125f, Ve.elementAt(i).damage,YoloEngine.TeamAB[YoloEngine.MyID].playerTeam);
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
						Ve.elementAt(i).x_texture = Ve.elementAt(i).xEnd;
						Ve.elementAt(i).y_texture = Ve.elementAt(i).yEnd;
						Ve.elementAt(i).ret = YoloEngine.WALL_STAND;
						break;
					case 13:
						Ve.elementAt(i).life--;
						break;
					case 14:
						Ve.elementAt(i).life--;
						hitBoxs.add(new HitBox(Ve.elementAt(i).x,
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, 1f,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,YoloEngine.TeamAB[YoloEngine.MyID].playerTeam,true, Ve.elementAt(i).id));
						YoloEngine.mMultislayer.sendHitBox(Ve.elementAt(i).x,
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, 1f,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,true, Ve.elementAt(i).id);
						break;
					}
				}
				
				break end;
			}
			if(isMy)
			if(sprite == 7) //WYDAJNO��??
			{
				if(Ve.elementAt(i).frameCounter==2)
					if(Ve.elementAt(i).ret == YoloEngine.WARRIOR_ATTACK)
					{
						hitBoxs.add(new HitBox(Ve.elementAt(i).x,
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,Ve.elementAt(i).team,false, Ve.elementAt(i).id));
						YoloEngine.mMultislayer.sendHitBox(Ve.elementAt(i).x,
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,false, Ve.elementAt(i).id);
						Ve.elementAt(i).ret = YoloEngine.WARRIOR_NULL;
					}
			}
			else if(sprite == 9)
			{
				if(Ve.elementAt(i).frameCounter==2)
					if(Ve.elementAt(i).ret == YoloEngine.HAND_ATTACK)
					{
						hitBoxs.add(new HitBox(Ve.elementAt(i).x,
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,Ve.elementAt(i).team,false, Ve.elementAt(i).id));
						YoloEngine.mMultislayer.sendHitBox(Ve.elementAt(i).x,
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,false, Ve.elementAt(i).id);
						Ve.elementAt(i).ret = YoloEngine.HAND_NULL;
					}
			}
			else if(sprite == 8)
			{
				if(Ve.elementAt(i).frameCounter==3||Ve.elementAt(i).frameCounter == 6)
					if(Ve.elementAt(i).ret == YoloEngine.MUMMY_ATTACK)
					{
						hitBoxs.add(new HitBox(Ve.elementAt(i).x,
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,Ve.elementAt(i).team,false, Ve.elementAt(i).id));
						YoloEngine.mMultislayer.sendHitBox(Ve.elementAt(i).x,
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,false, Ve.elementAt(i).id);
						Ve.elementAt(i).ret = YoloEngine.MUMMY_NULL;
					}
			}
			else if(sprite == 10)
			{
				if(Ve.elementAt(i).frameCounter==0)
					if(Ve.elementAt(i).ret == YoloEngine.BARREL_ATTACK)
					{
						hitBoxs.add(new HitBox(Ve.elementAt(i).x,
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,Ve.elementAt(i).team,false, Ve.elementAt(i).id));
						YoloEngine.mMultislayer.sendHitBox(Ve.elementAt(i).x,
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,false, Ve.elementAt(i).id);
					}
			}
			else if(sprite == 13)
			{
				if(Ve.elementAt(i).frameCounter==8)
					if(Ve.elementAt(i).ret == YoloEngine.TRAP_ATTACK)
					{
						hitBoxs.add(new HitBox(Ve.elementAt(i).x,
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,Ve.elementAt(i).team,false, Ve.elementAt(i).id));
						YoloEngine.mMultislayer.sendHitBox(Ve.elementAt(i).x,
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,false, Ve.elementAt(i).id);
					}
			}
//--------------------------------------------------------------------------------------------------------------------------------------------		
		
			Ve.elementAt(i).frameCounter++;
			Ve.elementAt(i).x_texture+=0.125f; //kolejna klatka texturki;	
			if(Ve.elementAt(i).x_texture >= 1){Ve.elementAt(i).y_texture+=0.125f; Ve.elementAt(i).x_texture=0f;}
			
		}
						
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
		gl.glTranslatef(Ve.elementAt(i).x -.5f, Ve.elementAt(i).y -.25f, 0f); 
		gl.glColor4f(1f,1f,1f,1f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glTranslatef(Ve.elementAt(i).x_texture, Ve.elementAt(i).y_texture, 0f);
		btn.draw(gl, YoloEngine.spriteSheets,Ve.elementAt(i).sprite);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(YoloEngine.TEXTURE_SIZE_X*(Ve.elementAt(i).life/Ve.elementAt(i).MAXlife), YoloEngine.LIFE_BAR_Y, 1f);
		gl.glTranslatef(Ve.elementAt(i).x/(Ve.elementAt(i).life/Ve.elementAt(i).MAXlife),Ve.elementAt(i).y*10+8f, 0f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glColor4f(1f,1f,1f,1f);
		gl.glTranslatef(0.875f, 0.125f, 0); 
		btn.draw(gl,YoloEngine.spriteSheets,1);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		if(Ve.elementAt(i).isPoisoned)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(Ve.elementAt(i).x -.5f, Ve.elementAt(i).y -.25f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0f, 0.875f, 0f);
			btn.draw(gl, YoloEngine.spriteSheets,Ve.elementAt(i).sprite);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		if(Ve.elementAt(i).isSlowDown)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(Ve.elementAt(i).x -.5f, Ve.elementAt(i).y -.25f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.125f, 0.875f, 0f);
			btn.draw(gl, YoloEngine.spriteSheets,Ve.elementAt(i).sprite);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		return false;
	}
	
	private boolean LinearSkillDraw(GL10 gl,Skill skill)
	{
		float scale = skill.sprite == 15?0.375f:1f,scaleX = skill.sprite==20?8.75f:1f;
		if(skill.sprite ==20)scale = 8f;
		
		if(skill.y_texture == skill.yEnd && skill.x_texture == skill.xEnd)
			return true;
		else
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X*scaleX, YoloEngine.TEXTURE_SIZE_Y*scale, 1f);
			gl.glTranslatef(skill.x/scaleX-.5f, skill.y/scale - (skill.sprite==20?0.5f:0.25f), 0f);
			gl.glColor4f(1f,1f,1f,1f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(skill.x_texture, skill.y_texture, 0f);
			btn.draw(gl, YoloEngine.spriteSheets,skill.sprite);
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
	private void LoopSkillDraw(GL10 gl,Skill skill)
	{
		float scale = 2f,scaleX = 2f;
		
		if(skill.y_texture == skill.yEnd && skill.x_texture == skill.xEnd)
		{
			skill.y_texture = 0;
			skill.x_texture = 0;
		}
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(YoloEngine.TEXTURE_SIZE_X*scaleX, YoloEngine.TEXTURE_SIZE_Y*scale, 1f);
		gl.glTranslatef((YoloEngine.TeamAB[YoloEngine.MyID].x-.5f)/scaleX, (YoloEngine.TeamAB[YoloEngine.MyID].y-.5f)/scale, 0f);
		gl.glColor4f(1f,1f,1f,1f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glTranslatef(skill.x_texture, skill.y_texture, 0f);
		btn.draw(gl, YoloEngine.spriteSheets,skill.sprite);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		if(skill.aniSlowCounter++ == skill.animation_slowdown)
		{
			skill.aniSlowCounter = 0;
			if(skill.x_texture<1)skill.x_texture+=0.125f;
			else{skill.y_texture+=0.125f; skill.x_texture=0f;}
		}	
	}
	private void findingSkillMy(int i,boolean isMy)
	{
		Vector<Skill> Ve;
		int p =0,q=YoloEngine.TeamSize;
		if(isMy) Ve = skillPlayerVe;
		else Ve = skillOponentVe;
		if(isMy?YoloEngine.playerTeam:!YoloEngine.playerTeam == YoloEngine.TeamA)
		{
			p=YoloEngine.TeamSize;
			q*=2;
		}

		int sprite =Ve.elementAt(i).sprite;
		
		for(int j =p;j<q;j++)
		{
			if(Math.abs(YoloEngine.TeamAB[j].x-Ve.elementAt(i).x)<Ve.elementAt(i).x_radius)		
				if(Math.abs(YoloEngine.TeamAB[j].y-Ve.elementAt(i).y)<Ve.elementAt(i).y_radius) 
				{
					if(sprite==109)
					{
						if(YoloEngine.TeamAB[j].PlayerLive<=0)
						{
							Skill skill = new Skill(0,0,sprite-87,0,0.875f,0.375f,0,0,0,0,Ve.elementAt(i).team);
							skill.x = YoloEngine.TeamAB[j].x;
							skill.y = YoloEngine.TeamAB[j].y;
							if(isMy)
								skillPlayerVe.add(skill);
							else
								skillOponentVe.add(skill);
						}
					}
					else
					{
						int slowD =0;																												
						if(sprite==103)slowD = 2;
						Skill skill = new Skill(0,0,sprite-87,slowD,0.875f,0.375f,0,0,0,0,Ve.elementAt(i).team);
						skill.x = YoloEngine.TeamAB[j].x;
						skill.y = YoloEngine.TeamAB[j].y;
						if(isMy)
							skillPlayerVe.add(skill);
						else
							skillOponentVe.add(skill);
					}
				}
		}
		int k = Ve.size();
		for(int j=0;j<k;j++)
		{
			if(!isMy && j!=i)
			if(Math.abs(skillOponentVe.elementAt(j).x-Ve.elementAt(i).x)<Ve.elementAt(i).x_radius)			
				if(Math.abs(skillOponentVe.elementAt(j).y-Ve.elementAt(i).y)<Ve.elementAt(i).y_radius)
				{
					if(sprite==109)
					{
						if(skillOponentVe.elementAt(j).life<=0)
						{
							Skill skill = new Skill(0,0,sprite-87,0,0.875f,0.375f,0,0,0,0,Ve.elementAt(i).team);
							skill.x = Ve.elementAt(j).x;
							skill.y = Ve.elementAt(j).y;
							if(isMy)
								skillPlayerVe.add(skill);
							else
								skillOponentVe.add(skill);
							
						}
					}
					else
					{
						int slowD =0;																												
						if(sprite==103)slowD = 2;
						Skill skill = new Skill(0,0,sprite-87,slowD,0.875f,0.375f,0,0,0,0,Ve.elementAt(i).team);
						skill.x = Ve.elementAt(j).x;
						skill.y = Ve.elementAt(j).y;
						if(isMy)
							skillPlayerVe.add(skill);
						else
							skillOponentVe.add(skill);
					}
				}
		}
	}
	
	private void findingSkillOpp(int i,boolean isMy)
	{
		Vector<Skill> Ve;
		int p=0,q=YoloEngine.TeamSize;
		if(isMy) Ve = skillPlayerVe;
		else Ve = skillOponentVe;
		if(isMy?YoloEngine.playerTeam:!YoloEngine.playerTeam == YoloEngine.TeamB)
		{
			p=YoloEngine.TeamSize;
			q*=2;
		}
		
		int sprite =Ve.elementAt(i).sprite;
		
		for(int j =p;j<q;j++)
		{
			if(Math.abs(YoloEngine.TeamAB[j].x-Ve.elementAt(i).x)<Ve.elementAt(i).x_radius)			
				if(Math.abs(YoloEngine.TeamAB[j].y-Ve.elementAt(i).y)<Ve.elementAt(i).y_radius) // rozr�nianie
				{
					if(sprite==109)
					{
						if(YoloEngine.TeamAB[j].PlayerLive<=0)
						{
							Skill skill = new Skill(0,0,sprite-87,0,0.875f,0.375f,0,0,0,0,Ve.elementAt(i).team);
							skill.x = YoloEngine.TeamAB[j].x;
							skill.y = YoloEngine.TeamAB[j].y;
							if(isMy)
								skillPlayerVe.add(skill);
							else
								skillOponentVe.add(skill);
						}
					}
					else
					{
						Skill skill = new Skill(0,0,sprite-87,0,0.875f,0.375f,0,0,0,0,Ve.elementAt(i).team);
						skill.x = YoloEngine.TeamAB[j].x;
						skill.y = YoloEngine.TeamAB[j].y;
						if(isMy)
							skillPlayerVe.add(skill);
						else
							skillOponentVe.add(skill);
					}
				}
		}
		int k = Ve.size();
		for(int j=0;j<k;j++)
		{	
			if(isMy &&j!=i)
			if(Math.abs(skillPlayerVe.elementAt(j).x-Ve.elementAt(i).x)<Ve.elementAt(i).x_radius)			
				if(Math.abs(skillPlayerVe.elementAt(j).y-Ve.elementAt(i).y)<Ve.elementAt(i).y_radius)
				{
					if(sprite==109)
					{
						if(skillPlayerVe.elementAt(j).life<=0)
						{
							Skill skill = new Skill(0,0,sprite-87,0,0.875f,0.375f,0,0,0,0,Ve.elementAt(i).team);
							skill.x = Ve.elementAt(j).x;
							skill.y = Ve.elementAt(j).y;
							if(isMy)
								skillPlayerVe.add(skill);
							else
								skillOponentVe.add(skill);
							
						}
					}
					else
					{
						Skill skill = new Skill(0,0,sprite-87,0,0.875f,0.375f,0,0,0,0,Ve.elementAt(i).team);
						skill.x = Ve.elementAt(j).x;
						skill.y = Ve.elementAt(j).y;
						if(isMy)
							skillPlayerVe.add(skill);
						else
							skillOponentVe.add(skill);
					}
				}
		}
	}
	
	private void drawPlayerSkills(GL10 gl)
	{
		int sprite;
		for(int i = 0; i < skillPlayerVe.size(); i++)
		{
			sprite = skillPlayerVe.elementAt(i).sprite;
			if(sprite>5 && sprite<15)
			{
				if(AIDraw(gl, i, true, skillPlayerVe.elementAt(i).sprite))//Rysuje kolejne AI
					{
						i--;continue;
					}
			}
			else if(sprite == 28)
			{
				YoloEngine.mMultislayer.sendMessageToAllreliable(skillPlayerVe.elementAt(i).serializeSkill());
				
				if(!YoloEngine.TeamAB[YoloEngine.MyID].isPlayerDenialed)
					YoloEngine.TeamAB[YoloEngine.MyID].deniled = YoloEngine.denialDuration;
				
				YoloEngine.TeamAB[YoloEngine.MyID].isPlayerDenialed = true;
				LoopSkillDraw(gl, skillPlayerVe.elementAt(i));
				
				if(YoloEngine.TeamAB[YoloEngine.MyID].deniled-- == 0)
				{
					YoloEngine.TeamAB[YoloEngine.MyID].isPlayerDenialed = false;
					skillPlayerVe.remove(i--);
					continue;
				}			
			}
			else
			{		
				if(skillPlayerVe.elementAt(i).x_texture==0 && skillPlayerVe.elementAt(i).y_texture==0)
				{
					if(sprite == 108||sprite == 103)
					{
						skillPlayerVe.elementAt(i).x = YoloEngine.TeamAB[YoloEngine.MyID].x;
						skillPlayerVe.elementAt(i).y = YoloEngine.TeamAB[YoloEngine.MyID].y;
						
						findingSkillMy(i,true);
						
						YoloEngine.mMultislayer.sendMessageToAllreliable(skillPlayerVe.elementAt(i).serializeSkill());
						
						hitBoxs.add(new HitBox(skillPlayerVe.elementAt(i).x,
								skillPlayerVe.elementAt(i).y, skillPlayerVe.elementAt(i).x_radius, skillPlayerVe.elementAt(i).y_radius, skillPlayerVe.elementAt(i).damage, 
								skillPlayerVe.elementAt(i).frameDuration,skillPlayerVe.elementAt(i).sprite,skillPlayerVe.elementAt(i).isLeft,skillPlayerVe.elementAt(i).team,false, skillPlayerVe.elementAt(i).id));
						
						YoloEngine.mMultislayer.sendHitBox(skillPlayerVe.elementAt(i).x,
								skillPlayerVe.elementAt(i).y, skillPlayerVe.elementAt(i).x_radius, skillPlayerVe.elementAt(i).y_radius, skillPlayerVe.elementAt(i).damage, 
								skillPlayerVe.elementAt(i).frameDuration,skillPlayerVe.elementAt(i).sprite,skillPlayerVe.elementAt(i).isLeft,false, skillPlayerVe.elementAt(i).id);
						
						skillPlayerVe.remove(i--);
						continue;
					}
					else if(sprite == 104||sprite == 109)
					{
						skillPlayerVe.elementAt(i).x = YoloEngine.TeamAB[YoloEngine.MyID].x;
						skillPlayerVe.elementAt(i).y = YoloEngine.TeamAB[YoloEngine.MyID].y;
						
						findingSkillOpp(i,true);
						
						YoloEngine.mMultislayer.sendMessageToAllreliable(skillPlayerVe.elementAt(i).serializeSkill());
						
						hitBoxs.add(new HitBox(skillPlayerVe.elementAt(i).x,
								skillPlayerVe.elementAt(i).y, skillPlayerVe.elementAt(i).x_radius, skillPlayerVe.elementAt(i).y_radius, skillPlayerVe.elementAt(i).damage, 
								skillPlayerVe.elementAt(i).frameDuration,skillPlayerVe.elementAt(i).sprite,skillPlayerVe.elementAt(i).isLeft,skillPlayerVe.elementAt(i).team,true, skillPlayerVe.elementAt(i).id));
						
						YoloEngine.mMultislayer.sendHitBox(skillPlayerVe.elementAt(i).x,
								skillPlayerVe.elementAt(i).y, skillPlayerVe.elementAt(i).x_radius, skillPlayerVe.elementAt(i).y_radius, skillPlayerVe.elementAt(i).damage, 
								skillPlayerVe.elementAt(i).frameDuration,skillPlayerVe.elementAt(i).sprite,skillPlayerVe.elementAt(i).isLeft,true, skillPlayerVe.elementAt(i).id);
						
						skillPlayerVe.remove(i--);
						continue;
					}
					else
					{
						if(!skillPlayerVe.elementAt(i).haveXY)
						{
							if(sprite  != 21 && sprite != 22 && sprite != 16 && sprite !=17)
							{
								skillPlayerVe.elementAt(i).setX();
								skillPlayerVe.elementAt(i).setY();
							}
							
							if(sprite==15)
							{
								float maxy=0;
								for(int q=0;q<ObjectTab.length;q++)
								{
									if(skillPlayerVe.elementAt(i).x>ObjectTab[q].x && skillPlayerVe.elementAt(i).x<ObjectTab[q].x + ObjectTab[q].dx)
									{
										if(skillPlayerVe.elementAt(i).y>=ObjectTab[q].y + ObjectTab[i].dy)
											if(ObjectTab[q].y + ObjectTab[i].dy>maxy)
												maxy = ObjectTab[q].y + ObjectTab[i].dy;
									}
								}
								skillPlayerVe.elementAt(i).y = maxy;
								
								hitBoxs.add(new HitBox(skillPlayerVe.elementAt(i).x,
										skillPlayerVe.elementAt(i).y, skillPlayerVe.elementAt(i).x_radius, skillPlayerVe.elementAt(i).y_radius, skillPlayerVe.elementAt(i).damage, 
										skillPlayerVe.elementAt(i).frameDuration,skillPlayerVe.elementAt(i).sprite,skillPlayerVe.elementAt(i).isLeft,skillPlayerVe.elementAt(i).team,false, skillPlayerVe.elementAt(i).id));
							}
							else if(sprite==102)
							{
								if(YoloEngine.TeamAB[YoloEngine.MyID].PlayerLive+skillPlayerVe.elementAt(i).damage<YoloEngine.TeamAB[YoloEngine.MyID].PLAYER_LIVE_MAX)
									YoloEngine.TeamAB[YoloEngine.MyID].PlayerLive+=skillPlayerVe.elementAt(i).damage;
								skillPlayerVe.elementAt(i).sprite = 17;
							}
							else if(sprite==19)
							{
								playerFire(0.5f, sprite, 8, skillPlayerVe.elementAt(i).damage);
								skillPlayerVe.remove(i);
								continue;
							}
							else if(sprite == 26)
							{
								playerFire(0.5f, sprite, 4, skillPlayerVe.elementAt(i).damage);
								skillPlayerVe.remove(i);
								continue;
							}
							else if(sprite == 23)
							{
								if(!YoloEngine.TeamAB[YoloEngine.MyID].isPlayerFlying)
								{
									YoloGame.flying = 10;
									YoloEngine.TeamAB[YoloEngine.MyID].flying = YoloEngine.flyingDuration;
								}
								YoloEngine.TeamAB[YoloEngine.MyID].isPlayerFlying = true;
								
								if(YoloEngine.TeamAB[YoloEngine.MyID].flying-- == 0)
								{
									YoloEngine.TeamAB[YoloEngine.MyID].isPlayerFlying = false;
									skillPlayerVe.remove(i);
									continue;
								}
								
							}
							else if(sprite == 24)
							{
								if(!YoloEngine.TeamAB[YoloEngine.MyID].isPlayerDef)
									YoloEngine.TeamAB[YoloEngine.MyID].defed = YoloEngine.defDuration;
								
								YoloEngine.TeamAB[YoloEngine.MyID].isPlayerDef = true;
								
								if(YoloEngine.TeamAB[YoloEngine.MyID].defed-- == 0)
								{
									YoloEngine.TeamAB[YoloEngine.MyID].isPlayerDef = false;
									skillPlayerVe.remove(i);
									continue;
								}
							}
							else if(sprite == 25)
							{
								if(!YoloEngine.TeamAB[YoloEngine.MyID].isPlayerInvincible)
									YoloEngine.TeamAB[YoloEngine.MyID].invice = YoloEngine.InvincibleDuration;
								
								YoloEngine.TeamAB[YoloEngine.MyID].isPlayerInvincible = true;
								
								if(YoloEngine.TeamAB[YoloEngine.MyID].invice-- == 0)
								{
									YoloEngine.TeamAB[YoloEngine.MyID].isPlayerInvincible = false;
									skillPlayerVe.remove(i);
									continue;
								}
							}
							else
							{
								hitBoxs.add(new HitBox(skillPlayerVe.elementAt(i).x,
										skillPlayerVe.elementAt(i).y, skillPlayerVe.elementAt(i).x_radius, skillPlayerVe.elementAt(i).y_radius, skillPlayerVe.elementAt(i).damage, 
										skillPlayerVe.elementAt(i).frameDuration,skillPlayerVe.elementAt(i).sprite,skillPlayerVe.elementAt(i).isLeft,skillPlayerVe.elementAt(i).team,false, skillPlayerVe.elementAt(i).id));
							}

							skillPlayerVe.elementAt(i).haveXY = true;
						}
						YoloEngine.mMultislayer.sendMessageToAllreliable(skillPlayerVe.elementAt(i).serializeSkill());
						
						YoloEngine.mMultislayer.sendHitBox(skillPlayerVe.elementAt(i).x,
								skillPlayerVe.elementAt(i).y, skillPlayerVe.elementAt(i).x_radius, skillPlayerVe.elementAt(i).y_radius, skillPlayerVe.elementAt(i).damage, 
								skillPlayerVe.elementAt(i).frameDuration,skillPlayerVe.elementAt(i).sprite,skillPlayerVe.elementAt(i).isLeft,false, skillPlayerVe.elementAt(i).id);
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
			if(sprite>5&&sprite<15)//Dla AI
			{
				if(AIDraw(gl, i, false, skillOponentVe.elementAt(i).sprite)) 
				{
					i--;continue;
				}
			}
			else// Dla nie AI
			{	
				if(skillOponentVe.elementAt(i).x_texture==0 && skillOponentVe.elementAt(i).y_texture==0)
					if(sprite == 108||sprite == 103)
					{
						findingSkillOpp(i,false);
						hitBoxs.add(new HitBox(skillOponentVe.elementAt(i).x,
								skillOponentVe.elementAt(i).y, skillOponentVe.elementAt(i).x_radius, skillOponentVe.elementAt(i).y_radius, skillOponentVe.elementAt(i).damage, 
								skillOponentVe.elementAt(i).frameDuration,skillOponentVe.elementAt(i).sprite,skillOponentVe.elementAt(i).isLeft,skillOponentVe.elementAt(i).team,true, skillOponentVe.elementAt(i).id));
						skillOponentVe.remove(i);
						continue;
					}
					else if(sprite == 104||sprite == 109)
					{
						findingSkillMy(i,false);
						hitBoxs.add(new HitBox(skillOponentVe.elementAt(i).x,
								skillOponentVe.elementAt(i).y, skillOponentVe.elementAt(i).x_radius, skillOponentVe.elementAt(i).y_radius, skillOponentVe.elementAt(i).damage, 
								skillOponentVe.elementAt(i).frameDuration,skillOponentVe.elementAt(i).sprite,skillOponentVe.elementAt(i).isLeft,skillOponentVe.elementAt(i).team,false, skillOponentVe.elementAt(i).id));
						skillOponentVe.remove(i);
						continue;
					}
				
				if(LinearSkillDraw(gl, skillOponentVe.elementAt(i)))
				{
					skillOponentVe.remove(i--);
				}
			}
		}
	}
	
	private int findIDMyTeam(int id)
	{
		int i =skillPlayerVe.size()/2,p=0,k=skillPlayerVe.size();
		while(skillPlayerVe.elementAt(i).id != id)
		{
			if(skillPlayerVe.elementAt(i).id>id)
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
	
	private int findIDOppTeam(int id)
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
	
	private void hitBox ()
	{
		for(int i = 0;i<hitBoxs.size();i++)
		{
			
			if(hitBoxs.elementAt(i).counter++ < hitBoxs.elementAt(i).duration)
			{
				int p = (hitBoxs.elementAt(i).efectOnMySkill?YoloEngine.playerTeam:!YoloEngine.playerTeam == YoloEngine.TeamA)?0:YoloEngine.TeamSize,
						k=YoloEngine.TeamSize+p;
				
				for(int j = p;j<k;j++)
					if(IsCollided(hitBoxs.elementAt(i),YoloEngine.TeamAB[j])) 
						if(!hitBoxs.elementAt(i).hitAIs.contains(YoloEngine.playerID))
						{
							switch((int)hitBoxs.elementAt(i).sprite)
							{
							case 4:
								YoloEngine.TeamAB[j].poisoned = 300;
								YoloEngine.TeamAB[j].isPlayerPoisoned = true;
								break;
							case 14:
								YoloEngine.TeamAB[j].PlayerLive+=hitBoxs.elementAt(i).damage;
							case 18:
								//TODO zamra�anie
								break;
							case 109:
								if(YoloEngine.TeamAB[j].PlayerLive<=0)
									YoloEngine.TeamAB[j].PlayerLive = YoloEngine.TeamAB[j].PLAYER_LIVE_MAX/2;
								break;
							case 103:
								YoloEngine.TeamAB[j].slowDowned = 300;
								YoloEngine.TeamAB[j].isPlayerSlowDown = true;
								break;
							case 104:
								YoloEngine.TeamAB[j].PlayerLive += hitBoxs.elementAt(i).damage;
								break;
								
							default:
								if(!YoloEngine.TeamAB[j].isPlayerInvincible)
									YoloEngine.TeamAB[j].PlayerLive -= hitBoxs.elementAt(i).damage*YoloEngine.TeamAB[j].Player_Dmg_reduction;
							}
							hitBoxs.elementAt(i).hitAIs.add(YoloEngine.playerID);
						}

				if(hitBoxs.elementAt(i).team == YoloEngine.TeamAB[YoloEngine.MyID].playerTeam) //AI
				{
					if(hitBoxs.elementAt(i).efectOnMySkill == true)
					{
						for(int j = 0; j<skillPlayerVe.size();j++) // moje skile
							if(skillPlayerVe.elementAt(j).sprite >= 6 && skillPlayerVe.elementAt(j).sprite <= 12 && skillPlayerVe.elementAt(j).sprite !=10 ) 
								if(IsCollided(hitBoxs.elementAt(i),skillPlayerVe.elementAt(j)))
								{
									if(!hitBoxs.elementAt(i).hitAIs.contains(skillPlayerVe.elementAt(j).id))
									{
										switch((int)hitBoxs.elementAt(i).sprite)
										{
										case 14:
											if(skillPlayerVe.elementAt(j).life +hitBoxs.elementAt(i).damage < skillPlayerVe.elementAt(j).MAXlife)
												skillPlayerVe.elementAt(j).life +=hitBoxs.elementAt(i).damage;
											else
												skillPlayerVe.elementAt(j).life=skillPlayerVe.elementAt(j).MAXlife;
										case 104:
											if(skillPlayerVe.elementAt(j).life +hitBoxs.elementAt(i).damage < skillPlayerVe.elementAt(j).MAXlife && skillPlayerVe.elementAt(j).life>0)
												skillPlayerVe.elementAt(j).life +=hitBoxs.elementAt(i).damage;
											else
												skillPlayerVe.elementAt(j).life=skillPlayerVe.elementAt(j).MAXlife;
											break;
										case 109:
											if(skillPlayerVe.elementAt(j).life<=0)
												skillPlayerVe.elementAt(j).life = skillPlayerVe.elementAt(j).MAXlife/2;
											break;
										}
										hitBoxs.elementAt(i).hitAIs.add(skillPlayerVe.elementAt(j).id);
									}
								}
					}
					else
					{
						for(int j = 0; j<skillOponentVe.size();j++) // skile przeciwnik�w
							if(skillOponentVe.elementAt(j).sprite >= 6 && skillOponentVe.elementAt(j).sprite <= 12 && skillOponentVe.elementAt(j).sprite !=10 ) 
								if(IsCollided(hitBoxs.elementAt(i),skillOponentVe.elementAt(j)))
								{
									if(!hitBoxs.elementAt(i).hitAIs.contains(skillOponentVe.elementAt(j).id))
									{
										switch((int)hitBoxs.elementAt(i).sprite)
										{
										case 4:
											skillOponentVe.elementAt(j).poison_duration = 300;
											skillOponentVe.elementAt(j).isPoisoned =true;
											break;
										case 18:
											//TODO zamra�anie
											break;
										case 103:
											skillOponentVe.elementAt(j).slowDown_duration = 300;
											skillOponentVe.elementAt(j).isSlowDown = true;
											break;
										default:
											skillOponentVe.elementAt(j).damage_buffor -= hitBoxs.elementAt(i).damage;
										}
										hitBoxs.elementAt(i).hitAIs.add(skillOponentVe.elementAt(j).id);
									}
								}
					}
				}
				else
				{
					if(hitBoxs.elementAt(i).efectOnMySkill == true)
					{
						for(int j = 0; j<skillPlayerVe.size();j++) // moje skile
							if(skillPlayerVe.elementAt(j).sprite >= 6 && skillPlayerVe.elementAt(j).sprite <= 12 && skillPlayerVe.elementAt(j).sprite !=10 ) 
								if(IsCollided(hitBoxs.elementAt(i),skillPlayerVe.elementAt(j)))
								{
									if(!hitBoxs.elementAt(i).hitAIs.contains(skillPlayerVe.elementAt(j).id))
									{
										switch((int)hitBoxs.elementAt(i).sprite)
										{
										case 4:
											skillPlayerVe.elementAt(j).poison_duration = 300;
											skillPlayerVe.elementAt(j).isPoisoned =true;
											break;
										case 18:
											//TODO zamra�anie
											break;
										case 103:
											skillPlayerVe.elementAt(j).slowDown_duration = 300;
											skillPlayerVe.elementAt(j).isSlowDown = true;
											break;
										default:
											skillPlayerVe.elementAt(j).damage_buffor -= hitBoxs.elementAt(i).damage;
										}
										hitBoxs.elementAt(i).hitAIs.add(skillPlayerVe.elementAt(j).id);
									}
								}
					}
					else
					{
						for(int j = 0; j<skillOponentVe.size();j++) // skile przeciwnik�w
							if(skillOponentVe.elementAt(j).sprite >= 6 && skillOponentVe.elementAt(j).sprite <= 12 && skillOponentVe.elementAt(j).sprite !=10 ) 
								if(IsCollided(hitBoxs.elementAt(i),skillOponentVe.elementAt(j)))
								{
									if(!hitBoxs.elementAt(i).hitAIs.contains(skillOponentVe.elementAt(j).id))
									{
										switch((int)hitBoxs.elementAt(i).sprite)
										{
										case 14:
											if(skillOponentVe.elementAt(j).life +hitBoxs.elementAt(i).damage < skillOponentVe.elementAt(j).MAXlife)
												skillOponentVe.elementAt(j).life +=hitBoxs.elementAt(i).damage;
											else
												skillOponentVe.elementAt(j).life=skillOponentVe.elementAt(j).MAXlife;
										case 104:
											if(skillOponentVe.elementAt(j).life +hitBoxs.elementAt(i).damage < skillOponentVe.elementAt(j).MAXlife && skillOponentVe.elementAt(j).life>0)
												skillOponentVe.elementAt(j).life +=hitBoxs.elementAt(i).damage;
											else
												skillOponentVe.elementAt(j).life=skillOponentVe.elementAt(j).MAXlife;
											break;
										case 109:
											if(skillOponentVe.elementAt(j).life<=0)
												skillOponentVe.elementAt(j).life = skillOponentVe.elementAt(j).MAXlife/2f;
											break;
										}
										hitBoxs.elementAt(i).hitAIs.add(skillOponentVe.elementAt(j).id);
									}
								}
					}
				}
			}
			else 
			{
				hitBoxs.remove(i);
				continue;
			}
			
			if(hitBoxs.elementAt(i).sprite >5 && hitBoxs.elementAt(i).sprite <11)//je�eli AI
				if(hitBoxs.elementAt(i).team == YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
				{
					int id = findIDMyTeam(hitBoxs.elementAt(i).ID);
					skillPlayerVe.elementAt(id).x = hitBoxs.elementAt(i).x;
					skillPlayerVe.elementAt(id).y = hitBoxs.elementAt(i).y;
					skillPlayerVe.elementAt(id).isLeft = hitBoxs.elementAt(i).isLeft;
					
					switch(skillPlayerVe.elementAt(id).sprite)
					{
					case 6:
						if(!skillPlayerVe.elementAt(id).isLeft)
						{
							skillPlayerVe.elementAt(id).x_texture = skillPlayerVe.elementAt(id).xStart = 0;
							skillPlayerVe.elementAt(id).y_texture = skillPlayerVe.elementAt(id).yStart = 0.25f;
							skillPlayerVe.elementAt(id).xEnd = .875f;
							skillPlayerVe.elementAt(id).yEnd = 0.25f;
						}
						else
						{
							skillPlayerVe.elementAt(id).x_texture = skillPlayerVe.elementAt(id).xStart = 0;
							skillPlayerVe.elementAt(id).y_texture = skillPlayerVe.elementAt(id).yStart = 0.125f;
							skillPlayerVe.elementAt(id).xEnd = .875f;
							skillPlayerVe.elementAt(id).yEnd = 0.125f;
						}
						skillPlayerVe.elementAt(id).ret = YoloEngine.ARCHER_FIRE;
						break;
					case 7:
						if(!skillPlayerVe.elementAt(id).isLeft)
						{
							skillPlayerVe.elementAt(id).x_texture = skillPlayerVe.elementAt(id).xStart = 0;
							skillPlayerVe.elementAt(id).y_texture = skillPlayerVe.elementAt(id).yStart = 0.25f;
							skillPlayerVe.elementAt(id).xEnd = .375f;
							skillPlayerVe.elementAt(id).yEnd = 0.25f;
						}
						else
						{
							skillPlayerVe.elementAt(id).x_texture = skillPlayerVe.elementAt(id).xStart = 0.5f;
							skillPlayerVe.elementAt(id).y_texture = skillPlayerVe.elementAt(id).yStart = 0.125f;
							skillPlayerVe.elementAt(id).xEnd = .875f;
							skillPlayerVe.elementAt(id).yEnd = 0.125f;
						}
						skillPlayerVe.elementAt(id).ret = YoloEngine.WARRIOR_ATTACK;
						break;
					case 8:
						if(!skillPlayerVe.elementAt(id).isLeft)
						{
							skillPlayerVe.elementAt(id).x_texture = skillPlayerVe.elementAt(id).xStart = 0f;
							skillPlayerVe.elementAt(id).y_texture = skillPlayerVe.elementAt(id).yStart = 0.375f;
							skillPlayerVe.elementAt(id).xEnd = .75f;
							skillPlayerVe.elementAt(id).yEnd = 0.375f;
						}
						else
						{
							skillPlayerVe.elementAt(id).x_texture = skillPlayerVe.elementAt(id).xStart = 0;
							skillPlayerVe.elementAt(id).y_texture = skillPlayerVe.elementAt(id).yStart = 0.25f;
							skillPlayerVe.elementAt(id).xEnd = .75f;
							skillPlayerVe.elementAt(id).yEnd = 0.25f;
						}
						skillPlayerVe.elementAt(id).ret = YoloEngine.MUMMY_ATTACK;
						break;
					case 10:
						skillPlayerVe.elementAt(id).x_texture = skillPlayerVe.elementAt(id).xStart = 0;
						skillPlayerVe.elementAt(id).y_texture = skillPlayerVe.elementAt(id).yStart = 0.25f;
						skillPlayerVe.elementAt(id).xEnd = .875f;
						skillPlayerVe.elementAt(id).yEnd = 0.5f;
						skillPlayerVe.elementAt(id).ret = YoloEngine.BARREL_ATTACK;
						break;
					}
				}
				else
				{
					int id = findIDOppTeam(hitBoxs.elementAt(i).ID);
					skillOponentVe.elementAt(id).x = hitBoxs.elementAt(i).x;
					skillOponentVe.elementAt(id).y = hitBoxs.elementAt(i).y;
					skillOponentVe.elementAt(id).isLeft = hitBoxs.elementAt(i).isLeft;
					
					switch(skillOponentVe.elementAt(id).sprite)
					{
					case 6:
						if(!skillOponentVe.elementAt(id).isLeft)
						{
							skillOponentVe.elementAt(id).x_texture = skillOponentVe.elementAt(id).xStart = 0;
							skillOponentVe.elementAt(id).y_texture = skillOponentVe.elementAt(id).yStart = 0.25f;
							skillOponentVe.elementAt(id).xEnd = .875f;
							skillOponentVe.elementAt(id).yEnd = 0.25f;
						}
						else
						{
							skillOponentVe.elementAt(id).x_texture = skillOponentVe.elementAt(id).xStart = 0;
							skillOponentVe.elementAt(id).y_texture = skillOponentVe.elementAt(id).yStart = 0.125f;
							skillOponentVe.elementAt(id).xEnd = .875f;
							skillOponentVe.elementAt(id).yEnd = 0.125f;
						}
						skillOponentVe.elementAt(id).ret = YoloEngine.ARCHER_FIRE;
						break;
					case 7:
						if(!skillOponentVe.elementAt(id).isLeft)
						{
							skillOponentVe.elementAt(id).x_texture = skillOponentVe.elementAt(id).xStart = 0;
							skillOponentVe.elementAt(id).y_texture = skillOponentVe.elementAt(id).yStart = 0.25f;
							skillOponentVe.elementAt(id).xEnd = .375f;
							skillOponentVe.elementAt(id).yEnd = 0.25f;
						}
						else
						{
							skillOponentVe.elementAt(id).x_texture = skillOponentVe.elementAt(id).xStart = 0.5f;
							skillOponentVe.elementAt(id).y_texture = skillOponentVe.elementAt(id).yStart = 0.125f;
							skillOponentVe.elementAt(id).xEnd = .875f;
							skillOponentVe.elementAt(id).yEnd = 0.125f;
						}
						skillOponentVe.elementAt(id).ret = YoloEngine.WARRIOR_ATTACK;
						break;
					case 8:
						if(!skillOponentVe.elementAt(id).isLeft)
						{
							skillOponentVe.elementAt(id).x_texture = skillOponentVe.elementAt(id).xStart = 0f;
							skillOponentVe.elementAt(id).y_texture = skillOponentVe.elementAt(id).yStart = 0.375f;
							skillOponentVe.elementAt(id).xEnd = .75f;
							skillOponentVe.elementAt(id).yEnd = 0.375f;
						}
						else
						{
							skillOponentVe.elementAt(id).x_texture = skillOponentVe.elementAt(id).xStart = 0;
							skillOponentVe.elementAt(id).y_texture = skillOponentVe.elementAt(id).yStart = 0.25f;
							skillOponentVe.elementAt(id).xEnd = .75f;
							skillOponentVe.elementAt(id).yEnd = 0.25f;
						}
						skillOponentVe.elementAt(id).ret = YoloEngine.MUMMY_ATTACK;
						break;
					case 10:
						skillOponentVe.elementAt(id).x_texture = skillOponentVe.elementAt(id).xStart = 0;
						skillOponentVe.elementAt(id).y_texture = skillOponentVe.elementAt(id).yStart = 0.25f;
						skillOponentVe.elementAt(id).xEnd = .875f;
						skillOponentVe.elementAt(id).yEnd = 0.5f;
						skillOponentVe.elementAt(id).ret = YoloEngine.BARREL_ATTACK;
						break;
					}
				}
			
		}
	}
	
	public static void givePlayerID()//wykonywane przy do�aczeniu gracza
	{
		try
		{
			if(YoloEngine.teamA.contains(YoloEngine.playerParticipantID))
				YoloEngine.playerID = YoloEngine.teamA.indexOf(YoloEngine.playerParticipantID);
			else
				YoloEngine.playerID = YoloEngine.teamB.indexOf(YoloEngine.playerParticipantID)+YoloEngine.teamB.size();
			YoloEngine.IDTracer = YoloEngine.opponents.size()+1;
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
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
		
//-------------------------------------------WCZYTYWANIE TEXTUREK----------------------------------------------
		TextureLoader = new YoloTexture(gl,30);
		back.loadTexture(gl, R.drawable.aniol_tlo_loading, YoloEngine.context);
		load_back.loadTexture(gl, R.drawable.pasek_back, YoloEngine.context);
		load_front.loadTexture(gl, R.drawable.pasek_wypelnienie, YoloEngine.context);
		
//------------------------------------------INICJOWANIE OBIEKT�W FIZYCZNYCH----------------------------------		
		YoloEngine.LEVEL_SIZE_X = YoloEngine.LEVEL_X/YoloEngine.display_x*YoloEngine.LEVEL_scale; 
		YoloEngine.LEVEL_SIZE_Y = YoloEngine.LEVEL_Y/YoloEngine.display_y*YoloEngine.LEVEL_scale; 
		YoloEngine.GAME_PROJECTION_X = YoloEngine.display_x/YoloEngine.LEVEL_SIZE_X;
		YoloEngine.GAME_PROJECTION_Y = YoloEngine.display_y/YoloEngine.LEVEL_SIZE_Y;
		YoloEngine.TEXTURE_SIZE_X /=YoloEngine.display_x/YoloEngine.LEVEL_scale;
		YoloEngine.TEXTURE_SIZE_Y /=YoloEngine.display_y/YoloEngine.LEVEL_scale;
		YoloEngine.LIFE_BAR_Y/=YoloEngine.display_y/YoloEngine.LEVEL_scale;
		half_fx = (1f/YoloEngine.TEXTURE_SIZE_X/2f -.5f);
		half_bx = (1f/YoloEngine.TEXTURE_SIZE_X/2f +.5f);
		half_fy = (1f/YoloEngine.TEXTURE_SIZE_Y/2f -.5f);
		half_by = (1f/YoloEngine.TEXTURE_SIZE_Y/2f +.5f);
		givePlayerID();
		
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
		YoloEngine.sprite_load[YoloEngine.SkillSprite1<30?YoloEngine.SkillSprite1 : YoloEngine.SkillSprite1-87] = true;//Zale�y od playera
		YoloEngine.sprite_load[YoloEngine.SkillSprite2<30?YoloEngine.SkillSprite2 : YoloEngine.SkillSprite2-87] = true;//Zale�y od playera
		YoloEngine.sprite_load[YoloEngine.SkillSprite3<30?YoloEngine.SkillSprite3 : YoloEngine.SkillSprite3-87] = true;//Zale�y od playera
		
//-----------------------------------------------------------------------------------------------------------		
	}
}
