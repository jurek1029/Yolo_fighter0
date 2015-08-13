package com.example.yolo_fighter;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView.Renderer;


class PowerUP extends YoloObject
{
	int effect;
	float x_texture,y_texture;
	PowerUP()
	{	
		super(0,0);
		Random rng = new Random();
		x=(YoloEngine.LEVEL_X/YoloEngine.TX)* rng.nextFloat();
		y=(YoloEngine.LEVEL_Y/YoloEngine.TY)* rng.nextFloat();
		effect = rng.nextInt(8);
		switch(effect)
		{
		case 0:
			x_texture = 0f;
			y_texture = 0.375f;
			break;
		case 1:
			x_texture = 0.125f;
			y_texture = 0.375f;
			break;
		case 2:
			x_texture = 0.25f;
			y_texture = 0.375f;
			break;
		case 3:
			x_texture = 0.375f;
			y_texture = 0.375f;
			break;
		case 4:
			x_texture = 0.5f;
			y_texture = 0.375f;
			break;
		case 5:
			x_texture = 0.625f;
			y_texture = 0.375f;
			break;
		case 6:
			x_texture = 0.75f;
			y_texture = 0.375f;
			break;
		case 7:
			x_texture = 0.75f;
			y_texture = 0.375f;
			break;
			
		}
		
	}
	
	public void Activate()
	{
		Random rng = new Random();
		switch(effect)
		{
		case 0:
			int a = rng.nextInt(50);
			if(YoloEngine.TeamAB[YoloEngine.MyID].PlayerLive + a < YoloEngine.TeamAB[YoloEngine.MyID].PLAYER_LIVE_MAX )
				YoloEngine.TeamAB[YoloEngine.MyID].PlayerLive += a;
			else
				YoloEngine.TeamAB[YoloEngine.MyID].PlayerLive = YoloEngine.TeamAB[YoloEngine.MyID].PLAYER_LIVE_MAX ;
			break;
		case 1:
			YoloEngine.TeamAB[YoloEngine.MyID].fireDamage ++;
			break;
		case 2:
			if(YoloEngine.TeamAB[YoloEngine.MyID].firePause-2 > 1)
				YoloEngine.TeamAB[YoloEngine.MyID].firePause-=2;
			break;
		case 3:
			YoloEngine.TeamAB[YoloEngine.MyID].poiseDamage++;
			break;
		case 4:
			YoloEngine.TeamAB[YoloEngine.MyID].playerMag = YoloEngine.TeamAB[YoloEngine.MyID].PlayerMagCapasity;
			break;
		case 5:
			if(YoloEngine.TeamAB[YoloEngine.MyID].playerMagReloadTime - 10 > 10)
				YoloEngine.TeamAB[YoloEngine.MyID].playerMagReloadTime -= 10;
			break;
		case 6:
			YoloEngine.TeamAB[YoloEngine.MyID].coin += rng.nextInt(50);
			break;
		case 7:
			int b = rng.nextInt(10);
			if(b == 3) b += rng.nextBoolean()?-1 :1 ;
			YoloEngine.TeamAB[YoloEngine.MyID].weapon = b;
		    YoloGameRenderer.weaponSelect();
			break;
		}
	}
	
}

class HitBox extends YoloObject
{
	float damage,duration;
	int counter =0, sprite,ID;
	boolean isLeft, team,efectOnMySkill; //teamA -> 0, teamB ->1
	Vector<Integer> hitAIs = new Vector<Integer>();
	 HitBox(float x ,float y, float dx ,float dy, float damage, float duration, int sprite, boolean isLeft, boolean team, boolean efectOnMySkill, int ID)
	{
		super(x, y,dx,dy);
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
	float x_radius,y_radius;//hitbox
	float damage,life,MAXlife,damage_buffor =0f,poiseDamageBuffor =0;
	float frameDuration=1;//hitbox
	float x_oponnent, y_oponnent ;
	int closest =0;
	int frameCounter =0;//hitbox
	float VolumeScale =1f;
	int sprite,id;
	int ret=100,j=0;
	int animation_slowdown,aniSlowCounter = -1;
	float scale_x=1f, scale_y=1f;
	int poison_duration =0,slowDown_duration =0,frozen_duration =0,lava_duration =0;
	int fire_rate = 10,fireCounter =0;
	int resurestion_count =0;
	
	boolean isLeft = false,onGround = false ,haveXY = false,team,canMove = true;
	boolean isUsed=false;
	boolean isPoisoned = false,isSlowDown = false,isFrozen = false;

    public Skill(float x,float y,int sprite, boolean team, int ID)
    {
    	super(x,y);
    	this.team = team;
    	this.sprite = sprite;
    	float lx,ly;
    	
    	switch(sprite)
    	{
    	case 4://Poison
    		xEnd = 0.125f;
    		yEnd = 0.875f;
    		x_radius = 6f;
    		y_radius = 4f;
    		frameDuration = 57;
    		life = 0;MAXlife = life;
    		animation_slowdown = 0;
    		resurestion_count = 0;
    		scale_x = 6f;scale_y = 4f;
    		if(ID < 0)
    		{
    			setX();setY();
    			this.x -=(x_radius/2);
    		}
    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
    			VolumeScale =0;
    		else
    		{
    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
    		}
    		YoloEngine.sp.play(YoloEngine.SoundInd[16], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
    		YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage,frameDuration,sprite,isLeft,team,false,id));
    		break;
    	case 5://Thunder_v
    		xEnd = 0.25f;
    		yEnd = 0.375f;
    		x_radius = 3f;
    		y_radius = 3f;
    		frameDuration = 26;
    		life = 0;MAXlife = life;
    		animation_slowdown = 0;
    		resurestion_count = 0;
    		scale_x = 3f;scale_y = 7f;
    		damage = 25f;
    		
    		if(ID < 0)
    		{
    			setX();setY();
    			this.x -= .5f;
    		}
    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
    			VolumeScale =0;
    		else
    		{
    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
    		}
    		YoloEngine.sp.play(YoloEngine.SoundInd[52], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
    		YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage,frameDuration,sprite,isLeft,team,false,id));
    		break;
    	case 6://Archer
    		xEnd = 0f;
    		yEnd = 0f;
    		x_radius = 5f;
    		y_radius = 5f;
    		frameDuration = 0;
    		life = 30;MAXlife = life;
    		animation_slowdown = 10;
    		resurestion_count = 300;
    		damage = 3f;
    		 if(ID < 0)
    			 setAIXY();
    		break;
    	case 7://Warrior
    		xEnd = 0f;
    		yEnd = 0f;
    		x_radius = 0.5f;
    		y_radius = 1.5f;
    		frameDuration = 2;
    		life = 50;MAXlife = life;
    		animation_slowdown = 7;
    		resurestion_count = 300;
    		damage = 5f;
    		 if(ID < 0)
    			 setAIXY();
    		break;
    	case 8://Mummy
    		xEnd = 0f;
    		yEnd = 0f;
    		x_radius = 0.5f;
    		y_radius = 1.5f;
    		frameDuration = 2;
    		life = 30;MAXlife = life;
    		animation_slowdown = 15;
    		resurestion_count = 300;
    		damage = 1.5f;
    		 if(ID < 0)
    			 setAIXY();
    		break;
    	case 9://Hand
    		xEnd = 0f;
    		yEnd = 0f;
    		x_radius = 1.5f;
    		y_radius = 1.5f;
    		frameDuration = 2;
    		life = 30;MAXlife = life;
    		animation_slowdown = 10;
    		resurestion_count = 300;
    		damage = 5f;
    		 if(ID < 0)
    			 setAIXY();
    		break;
    	case 10://Barrel
    		xEnd = 0f;
    		yEnd = 0f;
    		x_radius = 2f;
    		y_radius = 2f;
    		frameDuration = 10;
    		life = 10;MAXlife = life;
    		animation_slowdown = 10;
    		resurestion_count = 0;
    		damage = 20f;
    		 if(ID < 0)
    			 setAIXY();
    		isLeft = YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft; // TODO przes³aæ isLeft
			this.x++;
    		break;
    	case 11://Tower
    		{
    		xEnd = 0f;
    		yEnd = 0f;
    		x_radius = 5f;
    		y_radius = 5f;
    		frameDuration = 0;
    		life = 60;MAXlife = life;
    		animation_slowdown = 10;
    		resurestion_count = 0;
    		damage = 5f;
    		if(ID < 0)
    			setXYFloor();
    		//
    		break;
    		}
    	case 12://Wall
    		xEnd = 0f;
    		yEnd = 0f;
    		x_radius = 0f;
    		y_radius = 0f;
    		frameDuration = 0;
    		life = 80;MAXlife = life;
    		animation_slowdown = 10;
    		resurestion_count = 0;
    		damage = 0f;
    		if(ID < 0)
    			setXYFloor();
    		break;
    	case 13://Trap
    		xEnd = 0f;
    		yEnd = 0f;
    		x_radius = 3f;
    		y_radius = 3f;
    		frameDuration = 32;
    		life = 16;MAXlife = life;
    		animation_slowdown = 0;
    		resurestion_count = 0;
    		damage = 15f;
    		 if(ID < 0)
    			 setAIXY();
    		 lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
     		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
     		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
     			VolumeScale =0;
     		else
     		{
     			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
     		}
     		YoloEngine.sp.play(YoloEngine.SoundInd[55], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
    		break;
    	case 14://Warmth
    		xEnd = 0f;
    		yEnd = 0f;
    		x_radius = 3f;
    		y_radius = 3f;
    		frameDuration = 1;
    		life = 20;MAXlife = life;
    		animation_slowdown = 6;
    		resurestion_count = 0;
    		damage = 2f;
    		 if(ID < 0)
    			 setAIXY();
    		 lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
     		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
     		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
     			VolumeScale =0;
     		else
     		{
     			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
     		}
     		YoloEngine.sp.play(YoloEngine.SoundInd[55], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
    		break;
    	case 15://Spikes
    	{
    		xEnd = 0f;
    		yEnd = 0.5f;
    		x_radius = 1f;
    		y_radius = 1f;
    		frameDuration = 20;
    		life = 0;MAXlife = life;
    		animation_slowdown = 0;
    		resurestion_count = 0;
    		damage = 15f;
    		scale_y = 0.375f;
    		if(ID < 0)
    		{
	    		setX();setY();
	    		float maxy=0;
				for(int q=0;q<YoloGameRenderer.ObjectTab.length;q++)
				{
					if(this.x+1f>YoloGameRenderer.ObjectTab[q].x && this.x<YoloGameRenderer.ObjectTab[q].x + YoloGameRenderer.ObjectTab[q].dx)
					{
						if(this.y>=YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy)
							if(YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy>maxy)
								maxy = YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy;
					}
				}
				this.y = maxy;
    		}
    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
    			VolumeScale =0;
    		else
    		{
    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
    		}
    		YoloEngine.sp.play(YoloEngine.SoundInd[41], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage,frameDuration,sprite,isLeft,team,false,id));
    		break;
    	}
    	case 16:
    		xEnd = 0.5f;
    		yEnd = 0.375f;
    		break;
    	case 17:
	    	{
	    		boolean My = false;
	    		if(team == YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
	    		{
	    			if(YoloEngine.SkillSprite1 == 17 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill1)
	    				My = true;
	    			else if(YoloEngine.SkillSprite2 == 17 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill2)
	    				My = true;
	    			else if(YoloEngine.SkillSprite3 == 17 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill3)
	    				My = true;
	    		}
	    		if(My)
	    		{
	    			YoloEngine.TeamAB[YoloEngine.MyID].healBuffer = 20;
		    		YoloEngine.TeamAB[YoloEngine.MyID].isBeingHealed = true;
		    		YoloEngine.mMultislayer.SendSkillBool(17);
	    		}
	    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
	    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
	    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
	    			VolumeScale =0;
	    		else
	    		{
	    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
	    		}
	    		YoloEngine.sp.play(YoloEngine.SoundInd[56], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
	    	}
    		break;
    	case 18://zamra¿anie 
    		xEnd = 0.875f;
    		yEnd = 0.875f;
    		x_radius = 2.5f;
    		y_radius = 2.5f;
    		frameDuration = 32;
    		life = 0;MAXlife = life;
    		animation_slowdown = 0;
    		resurestion_count = 0;
    		damage = 0f;
    		 if(ID < 0)
    			 setAIXY();
    	//	this.x -=.5f;this.y -=.5f;
    		 lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
     		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
     		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
     			VolumeScale =0;
     		else
     		{
     			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
     		}
     		YoloEngine.sp.play(YoloEngine.SoundInd[53], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
    		YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage,frameDuration,sprite,isLeft,team,false,id));
    		break;
    	case 19://Icicle
     		xEnd = 0.875f;
    		yEnd = 0f;
    		x_radius = 1f;
    		y_radius = 1f;
    		frameDuration = 0;
    		life = 0;MAXlife = life;
    		animation_slowdown = 0;
    		resurestion_count = 0;
    		damage = 3f;
    		setX();setY();
     		YoloEngine.TeamAB[YoloEngine.MyID].fireSprite = sprite;
    		YoloEngine.TeamAB[YoloEngine.MyID].fireDamage = damage;
    		YoloEngine.TeamAB[YoloEngine.MyID].fireCount = 8;
    		YoloEngine.TeamAB[YoloEngine.MyID].icice = YoloEngine.icicleDuration;
    		break;
    	case 20://Smoke weed everyday
    		xEnd = 0.125f;
    		yEnd = 0f;
    		x_radius = 0f;
    		y_radius = 0f;
    		frameDuration = 0;
    		life = 0;MAXlife = life;
    		animation_slowdown = 300;
    		resurestion_count = 0;
    		damage = 5f;
    		scale_x = 15f;scale_y = 6f;
    		if(ID < 0)
    		{
    			setX();setY();	
    			this.x -=7f;this.y -= 1.25f;
    		}
    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
    			VolumeScale =0;
    		else
    		{
    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
    		}
    		YoloEngine.sp.play(YoloEngine.SoundInd[58], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
    		
    		break;
    	case 21:
     		xEnd = 0.5f;
    		yEnd = 0.375f;
    		damage = 8f;
    		break;
    	case 22:
    		xEnd = 0.5f;
    		yEnd = 0.375f;
    		break;
    	case 23:
	    	{
	    		boolean My = false;
	    		if(team == YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
	    		{
	    			if(YoloEngine.SkillSprite1 == 23 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill1)
	    				My = true;
	    			else if(YoloEngine.SkillSprite2 == 23 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill2)
	    				My = true;
	    			else if(YoloEngine.SkillSprite3 == 23 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill3)
	    				My = true;
	    		}
	    		if(My)
	    		{
	    			YoloGame.flying = 10;
					YoloEngine.TeamAB[YoloEngine.MyID].flying = YoloEngine.flyingDuration;
					YoloEngine.TeamAB[YoloEngine.MyID].isPlayerFlying = true;
					YoloEngine.mMultislayer.SendSkillBool(23);
	    		}
	    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
	    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
	    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
	    			VolumeScale =0;
	    		else
	    		{
	    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
	    		}
	    		YoloEngine.sp.play(YoloEngine.SoundInd[59], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
	    	}
			break;
    	case 24:
	    	{
	    		boolean My = false;
	    		if(team == YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
	    		{
	    			if(YoloEngine.SkillSprite1 == 24 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill1)
	    				My = true;
	    			else if(YoloEngine.SkillSprite2 == 24 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill2)
	    				My = true;
	    			else if(YoloEngine.SkillSprite3 == 24 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill3)
	    				My = true;
	    		}
	    		if(My)
	    		{
	    			YoloEngine.TeamAB[YoloEngine.MyID].defed = YoloEngine.defDuration;
					YoloEngine.TeamAB[YoloEngine.MyID].isPlayerDef = true;
					YoloEngine.mMultislayer.SendSkillBool(24);
	    		}
	    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
	    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
	    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
	    			VolumeScale =0;
	    		else
	    		{
	    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
	    		}
	    		YoloEngine.sp.play(YoloEngine.SoundInd[59], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
	    	}
			break;
    	case 25:
	    	{
	    		boolean My = false;
	    		if(team == YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
	    		{
	    			if(YoloEngine.SkillSprite1 == 25 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill1)
	    				My = true;
	    			else if(YoloEngine.SkillSprite2 == 25 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill2)
	    				My = true;
	    			else if(YoloEngine.SkillSprite3 == 25 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill3)
	    				My = true;
	    		}
	    		if(My)
	    		{
	    			YoloEngine.TeamAB[YoloEngine.MyID].invice = YoloEngine.InvincibleDuration;	
					YoloEngine.TeamAB[YoloEngine.MyID].isPlayerInvincible = true;
					YoloEngine.mMultislayer.SendSkillBool(25);
	    		}
	    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
	    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
	    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
	    			VolumeScale =0;
	    		else
	    		{
	    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
	    		}
	    		YoloEngine.sp.play(YoloEngine.SoundInd[55], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
	    	}
    		break;
    	case 26://Thunder_h
    		xEnd = 0.5f;
    		yEnd = 0f;
    		x_radius = 1f;
    		y_radius = 1f;
    		frameDuration = 0;
    		life = 0;MAXlife = life;
    		animation_slowdown = 0;
    		resurestion_count = 0;
    		damage = 5f;
    		YoloEngine.TeamAB[YoloEngine.MyID].fireSprite = sprite;
    		YoloEngine.TeamAB[YoloEngine.MyID].fireDamage = damage;
    		YoloEngine.TeamAB[YoloEngine.MyID].fireCount = 4;
    		YoloEngine.TeamAB[YoloEngine.MyID].thunder_h = YoloEngine.thunderDuration;
    		setX();setY();
    		break;
    	case 27://Heal_long
       		xEnd = 0.5f;
    		yEnd = 0.125f;
    		x_radius = 0f;
    		y_radius = 0f;
    		frameDuration = 0;
    		life = 0;MAXlife = life;
    		animation_slowdown = 0;
    		resurestion_count = 0;
    		damage = 3f;
    		if(ID<0)
    			setAIXY();
    		break;
    	case 28://Denail 
	    	{
	    		boolean My = false;
	    		if(team == YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
	    		{
	    			if(YoloEngine.SkillSprite1 == 28 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill1)
	    				My = true;
	    			else if(YoloEngine.SkillSprite2 == 28 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill2)
	    				My = true;
	    			else if(YoloEngine.SkillSprite3 == 28 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill3)
	    				My = true;
	    		}
	    		if(My)
	    		{
	    			YoloEngine.TeamAB[YoloEngine.MyID].deniled = YoloEngine.denialDuration;
	        		YoloEngine.TeamAB[YoloEngine.MyID].isPlayerDenialed = true;
	        		YoloEngine.mMultislayer.SendSkillBool(28);
	    		}
	    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
	    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
	    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
	    			VolumeScale =0;
	    		else
	    		{
	    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
	    		}
	    		YoloEngine.sp.play(YoloEngine.SoundInd[61], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
	    	}
    		break;
    	case 29://HealLong 
	    	{
	    		boolean My = false;
	    		if(team == YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
	    		{
	    			if(YoloEngine.SkillSprite1 == 29 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill1)
	    				My = true;
	    			else if(YoloEngine.SkillSprite2 == 29 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill2)
	    				My = true;
	    			else if(YoloEngine.SkillSprite3 == 29 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill3)
	    				My = true;
	    		}
	    		if(My)
	    		{
	    			YoloEngine.TeamAB[YoloEngine.MyID].healing = YoloEngine.healingDuration; 
	    			YoloEngine.TeamAB[YoloEngine.MyID].isHealing = true;
	    			YoloEngine.mMultislayer.SendSkillBool(29);
	    		}
	    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
	    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
	    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
	    			VolumeScale =0;
	    		else
	    		{
	    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
	    		}
	    		YoloEngine.sp.play(YoloEngine.SoundInd[54], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
	    	}
    		break;
    	case 30://Fire_ball
    		xEnd = 0.875f;
    		yEnd = 0f;
    		x_radius = 1f;
    		y_radius = 1f;
    		frameDuration = 0;
    		life = 0;MAXlife = life;
    		animation_slowdown = 0;
    		resurestion_count = 0;
    		damage = 5f;
    		YoloEngine.TeamAB[YoloEngine.MyID].fireSprite = sprite;
    		YoloEngine.TeamAB[YoloEngine.MyID].fireDamage = damage;
    		YoloEngine.TeamAB[YoloEngine.MyID].fireCount = 8;
    		YoloEngine.TeamAB[YoloEngine.MyID].thunder_h = YoloEngine.thunderDuration;
    		setX();setY();
    		break;
    	case 31://Teleportacion
    	{
    		xEnd = 0.375f;
    		yEnd = 0.625f;
    		boolean My = false;
    		if(team == YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
    		{
    			if(YoloEngine.SkillSprite1 == 31 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill1)
    				My = true;
    			else if(YoloEngine.SkillSprite2 == 31 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill2)
    				My = true;
    			else if(YoloEngine.SkillSprite3 == 31 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill3)
    				My = true;
    		}
    		if(My)
    		{
    			setX();setY();
    			YoloEngine.TeamAB[YoloEngine.MyID].x = this.x;
	    		YoloEngine.TeamAB[YoloEngine.MyID].y = this.y;
    		}
    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
    			VolumeScale =0;
    		else
    		{
    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
    		}
    		YoloEngine.sp.play(YoloEngine.SoundInd[46], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
    		break;
    	}
    	case 32://Buff
    		xEnd = 0.625f;
    		yEnd = 0.375f;
    		x_radius = 0.5f;
    		y_radius = 0.5f;
    		frameDuration = 0;
    		life = 0;MAXlife = life;
    		animation_slowdown = 0;
    		resurestion_count = 0;
    		damage = 3f;
    		this.x = x;this.y =y;
    	//	YoloGameRenderer.hitBoxs.add(new HitBox(this.x,this.y, x_radius, y_radius, damage,1,sprite,isLeft,team,true,id));
    		
    		break;
    	case 33://Lava weed everyday
    	{
    		xEnd = 0.125f;
    		yEnd = 0.375f;
    		x_radius = 10f;
    		y_radius = 0.1f;
    		frameDuration = lava_duration = 300;
    		life = 0;MAXlife = life;
    		animation_slowdown = 0;
    		resurestion_count = 0;
    		damage = 0.03f;
    		scale_x = 15f;scale_y = 1f;
    		if(ID<0)
    		{
	    		setX();setY();
	    		float maxy=0;
				for(int q=0;q<YoloGameRenderer.ObjectTab.length;q++)
				{
					if(this.x+1f>YoloGameRenderer.ObjectTab[q].x && this.x<YoloGameRenderer.ObjectTab[q].x + YoloGameRenderer.ObjectTab[q].dx)
					{
						if(this.y>=YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy)
							if(YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy>maxy)
								maxy = YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy;
					}
				}
				this.y = maxy;
				this.x -= 7f;
    		}
    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
    			VolumeScale =0;
    		else
    		{
    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
    		}
    		YoloEngine.sp.play(YoloEngine.SoundInd[47], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage,frameDuration,sprite,isLeft,team,false,id));
    		break; 
    	}
    	case 34://Buff fireRate
    		xEnd = 0.625f;
    		yEnd = 0.375f;
    		x_radius = 0.5f;
    		y_radius = 0.5f;
    		frameDuration = 0;
    		life = 0;MAXlife = life;
    		animation_slowdown = 0;
    		resurestion_count = 0;
    		damage = 3f;
    		this.sprite = 32;
    		this.x = x;this.y =y;
    	//	YoloGameRenderer.hitBoxs.add(new HitBox(this.x,this.y, x_radius, y_radius, damage,1,34,isLeft,team,true,id));
    		
    		break;
    	case 35://Buff magReload
    		xEnd = 0.625f;
    		yEnd = 0.375f;
    		x_radius = 0.5f;
    		y_radius = 0.5f;
    		frameDuration = 0;
    		life = 0;MAXlife = life;
    		animation_slowdown = 0;
    		resurestion_count = 0;
    		damage = 3f;
    		this.sprite = 32;
    		this.x = x;this.y =y;
    	//	YoloGameRenderer.hitBoxs.add(new HitBox(this.x,this.y, x_radius, y_radius, damage,1,35,isLeft,team,true,id));
    		
    		break;
    	case 36://Buff siebie
	    	{
	    		damage = 3f;
	    		boolean My = false;
	    		if(team == YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
	    		{
	    			if(YoloEngine.SkillSprite1 == 36 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill1)
	    				My = true;
	    			else if(YoloEngine.SkillSprite2 == 36 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill2)
	    				My = true;
	    			else if(YoloEngine.SkillSprite3 == 36 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill3)
	    				My = true;
	    		}
	    		if(My)
	    		{
	    			YoloEngine.TeamAB[YoloEngine.MyID].buffed = YoloEngine.buffDuration;
					YoloEngine.TeamAB[YoloEngine.MyID].PlayerDmgBuff = damage;
					YoloEngine.TeamAB[YoloEngine.MyID].isPlayerBuff = true;
					YoloEngine.mMultislayer.SendSkillBool(36);
	    		}
	    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
	    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
	    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
	    			VolumeScale =0;
	    		else
	    		{
	    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
	    		}
	    		YoloEngine.sp.play(YoloEngine.SoundInd[48], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
	    	}
    		break;
    	case 37://Buff fireRate siebie
	    	{
	    		damage = 3f;
	    		boolean My = false;
	    		if(team == YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
	    		{
	    			if(YoloEngine.SkillSprite1 == 37 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill1)
	    				My = true;
	    			else if(YoloEngine.SkillSprite2 == 37 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill2)
	    				My = true;
	    			else if(YoloEngine.SkillSprite3 == 37 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill3)
	    				My = true;
	    		}
	    		if(My)
	    		{
	    			YoloEngine.TeamAB[YoloEngine.MyID].fireRated = YoloEngine.buffDuration;
					YoloEngine.TeamAB[YoloEngine.MyID].isPlayerFireRateBuff = true;
					YoloEngine.mMultislayer.SendSkillBool(37);
	    		}
	    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
	    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
	    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
	    			VolumeScale =0;
	    		else
	    		{
	    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
	    		}
	    		YoloEngine.sp.play(YoloEngine.SoundInd[48], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
	    	}
    		break;
    	case 38://Buff magReload siebie
	    	{
	    		damage = 3f;
	    		boolean My = false;
	    		if(team == YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
	    		{
	    			if(YoloEngine.SkillSprite1 == 37 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill1)
	    				My = true;
	    			else if(YoloEngine.SkillSprite2 == 37 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill2)
	    				My = true;
	    			else if(YoloEngine.SkillSprite3 == 37 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill3)
	    				My = true;
	    		}
	    		if(My)
	    		{
	    			YoloEngine.TeamAB[YoloEngine.MyID].reloadspeeded = YoloEngine.buffDuration;
					YoloEngine.TeamAB[YoloEngine.MyID].isPlayerMagReloadBuff = true;
					YoloEngine.mMultislayer.SendSkillBool(38);
	    		}
	    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
	    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
	    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
	    			VolumeScale =0;
	    		else
	    		{
	    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
	    		}
	    		YoloEngine.sp.play(YoloEngine.SoundInd[48], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
	    	}
    		break;
    	case 39://thief draw
    		xEnd = 0.875f;
    		yEnd = 0.25f;
    		x_radius = 0f;
    		y_radius = 0f;
    		frameDuration = 0;
    		life = 0;MAXlife = life;
    		animation_slowdown = 5;
    		resurestion_count = 0;
    		damage = 3f;
    		this.x = x;this.y =y;
    		break;
    	case 40://stamina draw
    		xEnd = 0.875f;
    		yEnd = 0.25f;
    		x_radius = 0f;
    		y_radius = 0f;
    		frameDuration = 0;
    		life = 0;MAXlife = life;
    		animation_slowdown = 0;
    		resurestion_count = 0;
    		damage = 3f;
    		this.x = x;this.y =y;
    		break;
    	case 41://EarthWave right
    		xEnd = 0.625f;
    		yEnd = 0f;
    		x_radius = 0f;
    		y_radius = 0f;
    		frameDuration = 0;
    		life = 0;MAXlife = life;
    		animation_slowdown = 2;
    		resurestion_count = 0;
    		this.x = x;this.y =y+0.2f;
    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
    			VolumeScale =0;
    		else
    		{
    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
    		}
    		YoloEngine.sp.play(YoloEngine.SoundInd[51], YoloEngine.Volume*4*VolumeScale, YoloEngine.Volume*4*VolumeScale, 1, 0, 1f);
    		break;
    	case 42://EarthWave left
    		y_texture = 0.125f;
    		xEnd = 0.625f;
    		yEnd = 0.125f;
    		x_radius = 0f;
    		y_radius = 0f;
    		frameDuration = 0;
    		life = 0;MAXlife = life;
    		animation_slowdown = 2;
    		resurestion_count = 0;
    		this.x = x;this.y =y+0.2f;
    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
    			VolumeScale =0;
    		else
    		{
    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
    		}
    		YoloEngine.sp.play(YoloEngine.SoundInd[51], YoloEngine.Volume*4*VolumeScale, YoloEngine.Volume*4*VolumeScale, 1, 0, 1f);
    		break;
    	case 43://EarthWave
    	{
    		x_radius = 3f;
    		y_radius = 0.5f;
    		damage = 15f;
    		if(ID<0)
    		{
	    		setX();setY();
	    		float maxy=0;
				for(int q=0;q<YoloGameRenderer.ObjectTab.length;q++)
				{
					if(this.x+1f>YoloGameRenderer.ObjectTab[q].x && this.x<YoloGameRenderer.ObjectTab[q].x + YoloGameRenderer.ObjectTab[q].dx)
					{
						if(this.y>=YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy)
							if(YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy>maxy)
								maxy = YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy;
					}
				}
				this.y = maxy;
    		}
    		
    		
    		if(team == YoloEngine.TeamA)
    		{
				YoloGameRenderer.skillTeamAVe.add(new Skill(this.x, this.y, 41, team,-1));
				YoloGameRenderer.skillTeamAVe.add(new Skill(this.x, this.y, 42, team,-1));
    		}
			else
			{
				YoloGameRenderer.skillTeamBVe.add(new Skill(this.x, this.y, 41, team,-1));
				YoloGameRenderer.skillTeamBVe.add(new Skill(this.x, this.y, 42, team,-1));
			}
    		YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage, frameDuration,sprite,isLeft,team,false, id));
    		break;
    	}
    	case 102://Heal
       		xEnd = 0.875f;
    		yEnd = 0.875f;
    		x_radius = 0f;
    		y_radius = 0f;
    		frameDuration = 0;
    		life = 0;MAXlife = life;
    		animation_slowdown = 0;
    		resurestion_count = 0;
    		damage = 20f;
    		if(YoloEngine.TeamAB[YoloEngine.MyID].PlayerLive+damage<YoloEngine.TeamAB[YoloEngine.MyID].PLAYER_LIVE_MAX)
				YoloEngine.TeamAB[YoloEngine.MyID].PlayerLive+=damage;
			sprite = 17;
    		break;
    	case 103://SlowDown
    		x_radius = 4f;
    		y_radius = 4f;
    		if(ID<0)
    			setX();setY();
    			lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
        		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
        	if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
        		VolumeScale =0;
        	else
        	{
        		VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
        	}
        	YoloEngine.sp.play(YoloEngine.SoundInd[42], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage, frameDuration,sprite,isLeft,team,false, id));
    		break;
    	case 104://Heal_rad
    		x_radius = 4f;
    		y_radius = 4f;
    		damage = 15f;
    		if(ID<0)
    			setX();setY();
    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
        	ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
        	if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
        		VolumeScale =0;
        	else
        	{
        		VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
        	}
        	YoloEngine.sp.play(YoloEngine.SoundInd[62], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage, frameDuration,sprite,isLeft,team,true, id));
    		break;
    	case 108://Life_drain
    		x_radius = 4f;
    		y_radius = 4f;
    		damage = 8f;
    		if(ID<0)
    			setX();setY();
    			lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
        		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
        	if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
        		VolumeScale =0;
        	else
        	{
        		VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
        	}
        	YoloEngine.sp.play(YoloEngine.SoundInd[43], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage, frameDuration,sprite,isLeft,team,false, id));
    		break;
    	case 109://Resurection
    		x_radius = 3f;
    		y_radius = 3f;
    		if(ID<0)
    			setX();setY();
    			lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
        		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
        	if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
        		VolumeScale =0;
        	else
        	{
        		VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
        	}
        	YoloEngine.sp.play(YoloEngine.SoundInd[44], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage, frameDuration,sprite,isLeft,team,true, id));
    		break;
    	case 119://buff_rad
    		x_radius = 3f;
    		y_radius = 3f;
    		if(ID<0)
    			setX();setY();
    			lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
        		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
	    	if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
	    		VolumeScale =0;
	    	else
	    	{
	    		VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
	    	}
	    	YoloEngine.sp.play(YoloEngine.SoundInd[48], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage, frameDuration,sprite,isLeft,team,true, id));
    		break;
    	case 120://buff_team
	    	{
	    		x_radius = 0.5f;
	    		y_radius = 0.5f;
	    		int p =(team == YoloEngine.TeamA?0:YoloEngine.TeamSize),k=YoloEngine.TeamSize + p;
				for(int j =p;j<k;j++)
				{
					Skill skill = new Skill(YoloEngine.TeamAB[j].x,YoloEngine.TeamAB[j].y+0.25f,32,team,-1);
					skill.x = YoloEngine.TeamAB[j].x;
					skill.y = YoloEngine.TeamAB[j].y+0.25f;
					YoloGameRenderer.hitBoxs.add(new HitBox(skill.x - x_radius/2 +.5f,skill.y - y_radius/2 +.5f, x_radius, y_radius, damage,1,32,isLeft,team,true,id));
					if(team == YoloEngine.TeamA)
						YoloGameRenderer.skillTeamAVe.add(skill);
					else
						YoloGameRenderer.skillTeamBVe.add(skill);
				}
				lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
	    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
	    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
	    			VolumeScale =0;
	    		else
	    		{
	    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
	    		}
	    		YoloEngine.sp.play(YoloEngine.SoundInd[48], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
	    		break;
	    	}
    	case 121://buff_rad fireRate
    		x_radius = 3f;
    		y_radius = 3f;
    		if(ID<0)
    			setX();setY();
    			lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
        		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
	    	if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
	    		VolumeScale =0;
	    	else
	    	{
	    		VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
	    	}
	    	YoloEngine.sp.play(YoloEngine.SoundInd[48], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage, frameDuration,sprite,isLeft,team,true, id));
    		break;
    	case 122://buff_rad magRealoa
    		x_radius = 3f;
    		y_radius = 3f;
    		if(ID<0)
    			setX();setY();
    			lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
        		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
	    	if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
	    		VolumeScale =0;
	    	else
	    	{
	    		VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
	    	}
	    	YoloEngine.sp.play(YoloEngine.SoundInd[48], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage, frameDuration,sprite,isLeft,team,true, id));
    		break;
    	case 123://buff_team fireRate
	    	{
	    		x_radius = 0.5f;
	    		y_radius = 0.5f;
	    		int p =(team == YoloEngine.TeamA?0:YoloEngine.TeamSize),k=YoloEngine.TeamSize + p;
				for(int j =p;j<k;j++)
				{
					Skill skill = new Skill(YoloEngine.TeamAB[j].x,YoloEngine.TeamAB[j].y+0.25f,34,team,-1);
					skill.x = YoloEngine.TeamAB[j].x;
					skill.y = YoloEngine.TeamAB[j].y+0.25f;
					YoloGameRenderer.hitBoxs.add(new HitBox(skill.x - x_radius/2 +.5f,skill.y - y_radius/2 +.5f, x_radius, y_radius, damage,1,34,isLeft,team,true,id));
					if(team == YoloEngine.TeamA)
						YoloGameRenderer.skillTeamAVe.add(skill);
					else
						YoloGameRenderer.skillTeamBVe.add(skill);
				}
				lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
	    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
	    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
	    			VolumeScale =0;
	    		else
	    		{
	    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
	    		}
	    		YoloEngine.sp.play(YoloEngine.SoundInd[48], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
	    		break;
	    	}
    	
    	case 124://buff_team magReload
    		x_radius = 0.5f;
    		y_radius = 0.5f;
    		int p =(team == YoloEngine.TeamA?0:YoloEngine.TeamSize),k=YoloEngine.TeamSize + p;
			for(int j =p;j<k;j++)
			{
				Skill skill = new Skill(YoloEngine.TeamAB[j].x,YoloEngine.TeamAB[j].y+0.25f,35,team,-1);
				skill.x = YoloEngine.TeamAB[j].x;
				skill.y = YoloEngine.TeamAB[j].y+0.25f;
				YoloGameRenderer.hitBoxs.add(new HitBox(skill.x - x_radius/2 +.5f,skill.y - y_radius/2 +.5f, x_radius, y_radius, damage,1,35,isLeft,team,true,id));
				if(team == YoloEngine.TeamA)
					YoloGameRenderer.skillTeamAVe.add(skill);
				else
					YoloGameRenderer.skillTeamBVe.add(skill);
			}
			lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
    		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
    			VolumeScale =0;
    		else
    		{
    			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
    		}
    		YoloEngine.sp.play(YoloEngine.SoundInd[48], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
    		break;
    	case 126://Thief
    		x_radius = 3f;
    		y_radius = 3f;
    		damage = 8f;
    		if(ID<0)
    			setX();setY();
    			lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
        		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
	    	if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
	    		VolumeScale =0;
	    	else
	    	{
	    		VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
	    	}
	    	YoloEngine.sp.play(YoloEngine.SoundInd[49], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
    		if(team != YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
    			YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage, frameDuration,sprite,isLeft,team,false, id));
    		else if(YoloEngine.SkillSprite1 == 126 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill1)
    			YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage, frameDuration,sprite,isLeft,team,false, id));
    		else if(YoloEngine.SkillSprite2 == 126 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill2)
    			YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage, frameDuration,sprite,isLeft,team,false, id));
    		else if(YoloEngine.SkillSprite3 == 126 && YoloEngine.TeamAB[YoloEngine.MyID].canSkill3)
    			YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage, frameDuration,sprite,isLeft,team,false, id));
    		break;
    	case 127://Stamina
    		x_radius = 3f;
    		y_radius = 3f;
    		if(ID<0)
    			setX();setY();
    			lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
        		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
	    	if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
	    		VolumeScale =0;
	    	else
	    	{
	    		VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
	    	}
	    	YoloEngine.sp.play(YoloEngine.SoundInd[50], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			YoloGameRenderer.hitBoxs.add(new HitBox(this.x - x_radius/2 +.5f,this.y - y_radius/2 +.5f, x_radius, y_radius, damage, frameDuration,sprite,isLeft,team,false, id));
    		break;
    	}
    	if(ID < 0)
    		id = giveID();
    	else
    		id = ID;
    	//YoloEngine.mMultislayer.sendMessageToAllreliable(this.serializeSkill());
    }

	public void setX()
	{
		if(YoloEngine.TeamAB[YoloEngine.MyID].x > YoloGameRenderer.half_fx && YoloEngine.TeamAB[YoloEngine.MyID].x < YoloEngine.LEVEL_X/YoloEngine.TX -  YoloGameRenderer.half_bx)
			x = YoloEngine.TeamAB[YoloEngine.MyID].x + (x-(((YoloEngine.display_x/2)/YoloEngine.TX)/YoloEngine.LEVEL_scale));	
		else
			if(YoloEngine.TeamAB[YoloEngine.MyID].x > YoloEngine.LEVEL_X/YoloEngine.TX -  YoloGameRenderer.half_bx)
				x = YoloEngine.LEVEL_X/YoloEngine.TX - (YoloEngine.display_x/YoloEngine.TX/YoloEngine.LEVEL_scale - x);
		x-=.5f;
	}
	public void setY()
	{
		if(YoloEngine.TeamAB[YoloEngine.MyID].y > YoloGameRenderer.half_fy && YoloEngine.TeamAB[YoloEngine.MyID].y < YoloEngine.LEVEL_Y/YoloEngine.TY -  YoloGameRenderer.half_by)
			y = YoloEngine.TeamAB[YoloEngine.MyID].y +.5f+ (y-(((YoloEngine.display_y/2)/YoloEngine.TY)/YoloEngine.LEVEL_scale));
		else 
			if(YoloEngine.TeamAB[YoloEngine.MyID].y > YoloEngine.LEVEL_Y/YoloEngine.TY -  YoloGameRenderer.half_by)
				y = YoloEngine.LEVEL_Y/YoloEngine.TY - (YoloEngine.display_y/YoloEngine.TY/YoloEngine.LEVEL_scale - y);
		y-=.5f;
	}
	
	public void setAIXY()
	{
		if(YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft)
			x = YoloEngine.TeamAB[YoloEngine.MyID].x - 0.5f ;
		else
			x = YoloEngine.TeamAB[YoloEngine.MyID].x + 0.5f;
		y = YoloEngine.TeamAB[YoloEngine.MyID].y+ 0.1f;
	}
	public void setXYFloor()
	{
		if(YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft)
			x = YoloEngine.TeamAB[YoloEngine.MyID].x - 0.5f ;
		else
			x = YoloEngine.TeamAB[YoloEngine.MyID].x + 0.5f;
		
		float maxy=0;
		for(int q=0;q<YoloGameRenderer.ObjectTab.length;q++)
		{
			
			if(YoloEngine.TeamAB[YoloEngine.MyID].x+1f>YoloGameRenderer.ObjectTab[q].x && 
					YoloEngine.TeamAB[YoloEngine.MyID].x<YoloGameRenderer.ObjectTab[q].x + YoloGameRenderer.ObjectTab[q].dx)
			{
				if(YoloEngine.TeamAB[YoloEngine.MyID].y>=YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy)
					if(YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy>maxy)
						maxy = YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy;
			}
		}
		y = maxy;
	}
	
	public void move ()
	{
//-------------------------------------------------------------------Szukanie najbliï¿½szego------------------------------		
		if(ret == 100)
		{ 
			float minLenght = 10000f,temp;
			boolean a = true;
			if(team == YoloEngine.TeamB)
			{
				for(int i=0;i<YoloEngine.TeamSize;i++)
				{
					temp = Math.abs(x-YoloEngine.TeamAB[i].x)*Math.abs(x-YoloEngine.TeamAB[i].x) +
							Math.abs(y-YoloEngine.TeamAB[i].y)*Math.abs(y-YoloEngine.TeamAB[i].y);
					if(temp<minLenght)
					{
						minLenght = temp;
						closest = i;
						a = true;
					}
				}
				for(int i=0;i<YoloGameRenderer.skillTeamAVe.size();i++)
				{	
					if(YoloGameRenderer.skillTeamAVe.elementAt(i).sprite >=6 && YoloGameRenderer.skillTeamAVe.elementAt(i).sprite <=12)
					if(YoloGameRenderer.skillTeamAVe.elementAt(i).ret !=4)
					{
						temp = Math.abs(x-YoloGameRenderer.skillTeamAVe.elementAt(i).x)*Math.abs(x-YoloGameRenderer.skillTeamAVe.elementAt(i).x) +
								Math.abs(y-YoloGameRenderer.skillTeamAVe.elementAt(i).y)*Math.abs(y-YoloGameRenderer.skillTeamAVe.elementAt(i).y);
						if(temp*0.75f<minLenght)
						{
							minLenght = temp;
							closest = i;
							a = false;
						}
					}
				}
			}
			else
			{
				for(int i=2;i<YoloEngine.TeamAB.length;i++)
				{
					temp = Math.abs(x-YoloEngine.TeamAB[i].x)*Math.abs(x-YoloEngine.TeamAB[i].x) +
							Math.abs(y-YoloEngine.TeamAB[i].y)*Math.abs(y-YoloEngine.TeamAB[i].y);
					if(temp<minLenght)
					{
						minLenght = temp;
						closest = i;
						a = true;
					}
				}
				for(int i=0;i<YoloGameRenderer.skillTeamBVe.size();i++)
				{
					if(YoloGameRenderer.skillTeamBVe.elementAt(i).sprite >=6 && YoloGameRenderer.skillTeamBVe.elementAt(i).sprite <=12)
					if(YoloGameRenderer.skillTeamBVe.elementAt(i).ret !=4)
					{
						temp = Math.abs(x-YoloGameRenderer.skillTeamBVe.elementAt(i).x)*Math.abs(x-YoloGameRenderer.skillTeamBVe.elementAt(i).x) +
								Math.abs(y-YoloGameRenderer.skillTeamBVe.elementAt(i).y)*Math.abs(y-YoloGameRenderer.skillTeamBVe.elementAt(i).y);
						if(temp*0.75f<minLenght)
						{
							minLenght = temp;
							closest = i;
							a = false;
						}
					}
				}
			}
			
			
			if(a)
			{
				x_oponnent = YoloEngine.TeamAB[closest].x; 
				y_oponnent = YoloEngine.TeamAB[closest].y;
			}
			else if(team == YoloEngine.TeamB)
			{
				x_oponnent = YoloGameRenderer.skillTeamAVe.elementAt(closest).x; 
				y_oponnent = YoloGameRenderer.skillTeamAVe.elementAt(closest).y;
			}
			else
			{
				x_oponnent = YoloGameRenderer.skillTeamBVe.elementAt(closest).x; 
				y_oponnent = YoloGameRenderer.skillTeamBVe.elementAt(closest).y;
			}
		 
		}
//--------------------------------------------------------------------GRAWITANCJA-------------------------------------------	
		if(sprite<11 || sprite>14)
		{
			vy -= YoloEngine.GAME_ACCELERATION;
			y += vy;
		}
		if(sprite!=10)
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
//----------------------------------------------------VolumeScale------------------------------------------------------
		float lx = Math.abs(x-YoloEngine.TeamAB[YoloEngine.MyID].x),ly =Math.abs(y-YoloEngine.TeamAB[YoloEngine.MyID].y);
		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
			VolumeScale =0;
		else
		{
			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
		}
//---------------------------------------------------------------------------------------------------------------------		
		
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
				if(ret !=YoloEngine.ARCHER_DYING)
					YoloEngine.sp.play(YoloEngine.SoundInd[20], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
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
				YoloEngine.sp.play(YoloEngine.SoundInd[19], YoloEngine.Volume/4f*VolumeScale, YoloEngine.Volume/4f*VolumeScale, 1, 0, 1f);
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
			else if(y_oponnent >y-y_radius/2f && y_oponnent - 1 < y + y_radius/2f && x_oponnent > x-x_radius/2f && x_oponnent - 1< x+x_radius/2f )
			{
				if(ret != YoloEngine.ARCHER_FIRE)
				{
					YoloEngine.sp.play(YoloEngine.SoundInd[18], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
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
							if(x - YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES +.5f > YoloGameRenderer.ObjectTab[j].x)
								if(isSlowDown)
									x -= YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x -= YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x = YoloGameRenderer.ObjectTab[j].x - .5f;
								x_texture = xStart = xEnd = 0.75f;
								y_texture = yStart = yEnd = 0.375f;
								ret = YoloEngine.ARCHER_STAND;
							}
						else
							if(x + YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES +.5f< YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx)
								if(isSlowDown)
									x += YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x += YoloEngine.ARCHER_SPEED*YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x = YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx-.5f;
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
				if(ret != YoloEngine.WARRIOR_DYING)
					YoloEngine.sp.play(YoloEngine.SoundInd[25], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
				ret=YoloEngine.WARRIOR_DYING;
				if(isLeft)
				{
					x_texture = xStart = xEnd = 0.75f;
					y_texture = yStart = yEnd = 0.25f;
					
				}
				else
				{
					x_texture = xStart = xEnd = 0.25f;
					y_texture = yStart = yEnd = 0.375f;
				}
				
			}
			else
			if(ret==YoloEngine.WARRIOR_HURT)
			{
				YoloEngine.sp.play(YoloEngine.SoundInd[24], YoloEngine.Volume/4f*VolumeScale, YoloEngine.Volume/4f*VolumeScale, 1, 0, 1f);
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
			else if(y_oponnent >y-y_radius/2f && y_oponnent - 1 < y + y_radius/2f && x_oponnent > x-x_radius/2f && x_oponnent - 1< x+x_radius/2f )
			{
				if(ret != YoloEngine.WARRIOR_ATTACK)
				{
					YoloEngine.sp.play(YoloEngine.SoundInd[23], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
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
						if(x + YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES +.5f < YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx)
						{
						
							x_texture = xStart = 0.75f;
							y_texture = yStart = 0f;
							xEnd = 0.375f;
							yEnd = 0.125f;
						}
						else
						{
							x= YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx-.5f;
							x_texture = xStart = xEnd = 0.375f;
							y_texture = yStart = yEnd = 0.375f;
							ret = YoloEngine.WARRIOR_STAND;
						}
						
					}
					else
					{
						isLeft = true;
						if(x - YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES +.5f > YoloGameRenderer.ObjectTab[j].x)
						{
							x_texture = xStart = 0f;
							y_texture = yStart = 0f;
							xEnd = .625f;
							yEnd = 0f;
						}
						else
						{
							x = YoloGameRenderer.ObjectTab[j].x - .5f;
							x_texture = xStart = xEnd = 0.875f;
							y_texture = yStart = yEnd = 0.25f;
							ret = YoloEngine.WARRIOR_STAND;
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
							if(x - YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES +.5f > YoloGameRenderer.ObjectTab[j].x)
								if(isSlowDown)
									x -= YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x -= YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x = YoloGameRenderer.ObjectTab[j].x - .5f;
								x_texture = xStart = xEnd = 0.875f;
								y_texture = yStart = yEnd = 0.25f;
								ret = YoloEngine.WARRIOR_STAND;
							}
						else
							if(x + YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES +.5f < YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx)
								if(isSlowDown)
									x += YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x += YoloEngine.WARRIOR_SPEED*YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x= YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx-.5f;
								x_texture = xStart = xEnd = 0.375f;
								y_texture = yStart = yEnd = 0.375f;
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
				if(ret != YoloEngine.MUMMY_DYING)
					YoloEngine.sp.play(YoloEngine.SoundInd[30], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
				ret=YoloEngine.MUMMY_DYING;
				if(isLeft)
				{
					x_texture = xStart = xEnd = 0.125f;
					y_texture = yStart = yEnd = 0.5f;
				}
				else
				{
					x_texture = xStart = xEnd = 0.375f;
					y_texture = yStart = yEnd = 0.5f;
				}
				
			}
			else
			if(ret==YoloEngine.MUMMY_HURT)
			{
				YoloEngine.sp.play(YoloEngine.SoundInd[29], YoloEngine.Volume/4f*VolumeScale, YoloEngine.Volume/4f*VolumeScale, 1, 0, 1f);
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
			else if(y_oponnent >y-y_radius/2f && y_oponnent - 1 < y + y_radius/2f && x_oponnent > x-x_radius/2f && x_oponnent - 1< x+x_radius/2f )
			{
				if(ret != YoloEngine.MUMMY_ATTACK)
				{
					YoloEngine.sp.play(YoloEngine.SoundInd[28], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
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
						if(x + YoloEngine.MUMMY_SPEED*YoloEngine.GAME_SKIPED_FRAMES +.5f< YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx)
						{
							x_texture = xStart = 0f;
							y_texture = yStart = 0.125f;
							xEnd = .875f;
							yEnd = 0.125f;
						}
						else
						{
							x = YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx-.5f;
							x_texture = xStart = xEnd = 0.875f;
							y_texture = yStart = yEnd = 0.375f;
							ret = YoloEngine.MUMMY_STAND;
						}
						
					}
					else
					{
						isLeft = true;
						if(x - YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES +.5f> YoloGameRenderer.ObjectTab[j].x)
						{
							x_texture = xStart = 0f;
							y_texture = yStart = 0f;
							xEnd = 0.875f;
							yEnd = 0f;
						}
						else
						{
							x = YoloGameRenderer.ObjectTab[j].x -.5f;
							x_texture = xStart = xEnd = 0.875f;
							y_texture = yStart = yEnd = 0.25f;
							ret = YoloEngine.MUMMY_STAND;
							break;
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
							if(x - YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES +.5f> YoloGameRenderer.ObjectTab[j].x)
								if(isSlowDown)
									x -= YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x -= YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x = YoloGameRenderer.ObjectTab[j].x -.5f;
								x_texture = xStart = xEnd = 0.875f;
								y_texture = yStart = yEnd = 0.25f;
								ret = YoloEngine.MUMMY_STAND;
							}
						else
							if(x + YoloEngine.MUMMY_SPEED*YoloEngine.GAME_SKIPED_FRAMES +.5f< YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx)
								if(isSlowDown)
									x += YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES/2f;
								else
									x += YoloEngine.MUMMY_SPEED *YoloEngine.GAME_SKIPED_FRAMES;
							else
							{
								x = YoloGameRenderer.ObjectTab[j].x + YoloGameRenderer.ObjectTab[j].dx-.5f;
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
				if(ret != YoloEngine.HAND_DYING )
					YoloEngine.sp.play(YoloEngine.SoundInd[33], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
				ret=YoloEngine.HAND_DYING;
				if(isLeft)
				{
					x_texture = xStart = xEnd = 0.375f;
					y_texture = yStart = yEnd = 0.25f;
				}
				else
				{
					x_texture = xStart = xEnd = 0.875f;
					y_texture = yStart = yEnd = 0.375f;
				}
				
			}
			else
			if(ret==YoloEngine.HAND_HURT)
			{
				YoloEngine.sp.play(YoloEngine.SoundInd[32], YoloEngine.Volume/4f*VolumeScale, YoloEngine.Volume/4f*VolumeScale, 1, 0, 1f);
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
				
				life -= damage_buffor;
				damage_buffor = 0f;
				ret = YoloEngine.HAND_NULL;
				
				
			}
			else if(y_oponnent >y-y_radius/2f && y_oponnent - 1 < y + y_radius/2f && x_oponnent > x-x_radius/2f && x_oponnent - 1< x+x_radius/2f )
			{
				if(ret != YoloEngine.HAND_ATTACK)
				{
					YoloEngine.sp.play(YoloEngine.SoundInd[31], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
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
					YoloEngine.sp.play(YoloEngine.SoundInd[35], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
					x_texture = xStart = 0;
					y_texture = yStart = 0.25f;
					xEnd = .875f;
					yEnd = 0.5f;
					frameCounter =0;
					ret = YoloEngine.BARREL_ATTACK;	
				}
			}
			else if(y_oponnent >y-y_radius/2f && y_oponnent - 1 < y + y_radius/2f && x_oponnent > x-x_radius/2f && x_oponnent - 1< x+x_radius/2f )
			{
				if(ret != YoloEngine.BARREL_ATTACK)
				{
					YoloEngine.sp.play(YoloEngine.SoundInd[35], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
					x_texture = xStart = 0;
					y_texture = yStart = 0.25f;
					xEnd = .875f;
					yEnd = 0.5f;	
					frameCounter =0;
					ret = YoloEngine.BARREL_ATTACK;
					life =0;
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
							if(x - YoloEngine.BARREL_SPEED*YoloEngine.GAME_SKIPED_FRAMES  > 0)
							{
								x_texture = xStart = 0f;
								y_texture = yStart = 0.125f;
								xEnd = .875f;
								yEnd = 0.125f;
								//YoloEngine.sp.play(YoloEngine.SoundInd[34], YoloEngine.Volume/4f*VolumeScale, YoloEngine.Volume/4f*VolumeScale, 1, 0, 1f);
							}
							ret = YoloEngine.ARCHER_WALK;
						}
						else
						{
							if(x - YoloEngine.BARREL_SPEED*YoloEngine.GAME_SKIPED_FRAMES < 0)
							{
								if(ret != YoloEngine.BARREL_ATTACK)
								{
									YoloEngine.sp.play(YoloEngine.SoundInd[35], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
									x_texture = xStart = 0;
									y_texture = yStart = 0.25f;
									xEnd = .875f;
									yEnd = 0.5f;
									frameCounter =0;
									ret = YoloEngine.BARREL_ATTACK;
									life =0;
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
							if(x + YoloEngine.BARREL_SPEED*YoloEngine.GAME_SKIPED_FRAMES + 1f < YoloEngine.LEVEL_X/YoloEngine.TX)
							{
								x_texture = xStart = 0f;
								y_texture = yStart = 0f;
								xEnd = 0.875f;
								yEnd = 0f;
								//YoloEngine.sp.play(YoloEngine.SoundInd[34], YoloEngine.Volume/4f*VolumeScale, YoloEngine.Volume/4f*VolumeScale, 1, 0, 1f);
							}
							ret = YoloEngine.ARCHER_WALK;
						}
						else
						{
							if(x + YoloEngine.BARREL_SPEED*YoloEngine.GAME_SKIPED_FRAMES + 1f > YoloEngine.LEVEL_X/YoloEngine.TX )
							{
								if(ret != YoloEngine.BARREL_ATTACK)
								{
									YoloEngine.sp.play(YoloEngine.SoundInd[35], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
									x_texture = xStart = 0;
									y_texture = yStart = 0.25f;
									xEnd = .875f;
									yEnd = 0.5f;
									frameCounter =0;
									ret = YoloEngine.BARREL_ATTACK;
									life =0;
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
			if(damage_buffor != 0)
				YoloEngine.sp.play(YoloEngine.SoundInd[38], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
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
					if(y_oponnent >y-y_radius/2f && y_oponnent - 1 < y + y_radius/2f && x_oponnent > x-x_radius/2f && x_oponnent - 1< x+x_radius/2f )
				{
					if(ret != YoloEngine.TOWER_FIRE)
					{
						YoloEngine.sp.play(YoloEngine.SoundInd[37], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
						if(x<x_oponnent)
							isLeft = false;
						else
							isLeft = true;
						x_texture = xStart = xEnd = 0.875f;
						y_texture = yStart = yEnd = 0f;
					}
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
			if(damage_buffor != 0)
				YoloEngine.sp.play(YoloEngine.SoundInd[40], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
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
			if(life<0)
			{
				x_texture = xStart = xEnd = 0.5f;
				y_texture = yStart = yEnd = 0.125f;
				ret = 4;
			}
			else if(life<MAXlife/5f)
			{
				x_texture = xStart = xEnd = 0.375f;
				y_texture = yStart = yEnd = 0.125f;
			}
			else if(life<2*MAXlife/5f)
			{
				x_texture = xStart = xEnd = 0.25f;
				y_texture = yStart = yEnd = 0.125f;
			}
			else if(life<3*MAXlife/5f)
			{
				x_texture = xStart = xEnd = 0.125f;
				y_texture = yStart = yEnd = 0.125f;
			}
			else if(life<4*MAXlife/5f)
			{
				x_texture = xStart = xEnd = 0f;
				y_texture = yStart = yEnd = 0.125f;
			}
			else
			{
				x_texture = xStart = xEnd = 0.875f;
				y_texture = yStart = yEnd = 0f;
			}	
			break;
		case 13:
			if(life<0)
			{
				if(ret != YoloEngine.TRAP_ATTACK)
					YoloEngine.sp.play(YoloEngine.SoundInd[53], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
				x_texture = xStart = 0; xEnd = 0.875f;
				y_texture = yStart = .125f; yEnd = 0.875f;
				ret = YoloEngine.TRAP_ATTACK;
				life=0;
				ret =4;
			}
			else	
			if(ret!=YoloEngine.TRAP_ATTACK)
			{
				if(y_oponnent >y-y_radius/2f && y_oponnent - 1 < y + y_radius/2f && x_oponnent > x-x_radius/2f && x_oponnent - 1< x+x_radius/2f )
				{
		    		YoloEngine.sp.play(YoloEngine.SoundInd[53], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
					x_texture = xStart = 0; xEnd = 0.875f;
					y_texture = yStart = 0; yEnd = 0.875f;
					ret = YoloEngine.TRAP_ATTACK;
					life=0;
					ret =4;
				}
				else if(ret!=YoloEngine.TRAP_STAND)
				{
					x_texture = xStart =0; xEnd = 0.875f;
					y_texture = yStart = yEnd = 0f;
					ret = YoloEngine.TRAP_STAND;
				}
			}
			
			if(ret == 4 )
			{
				scale_x+=0.1f;
				scale_y+=0.1f;
				x-=0.03f;
				y-=0.025f;
			}

			break;
		case 14:
			if(life<=0)
				ret = YoloEngine.WARMTH_DYING;
			else
			if(ret!=YoloEngine.WARMTH_ATACK)
				if(y_oponnent >y-y_radius/2f && y_oponnent - 1 < y + y_radius/2f && x_oponnent > x-x_radius/2f && x_oponnent - 1< x+x_radius/2f )
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
		int ID = YoloEngine.IDTracer + YoloEngine.TeamAB[YoloEngine.MyID].playerID;
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
    
    public byte[] serializeSkillNew() {
    	ByteBuffer bbf = ByteBuffer.allocate(20);
        bbf.putChar('s');  // 2 bajty
        bbf.putFloat(x); // 4 bajty
        bbf.putFloat(y); // 4 bajty
        bbf.putInt(sprite); // 4 bajty
        if(team) 			// 1 bajt
            bbf.put((byte)1);
        else
            bbf.put((byte)0);
        bbf.putInt(id); // 4 bajty
        return bbf.array();
    }
}

public class YoloGameRenderer implements Renderer {
	
	private YoloTexture TextureLoader ;
	private YoloBackground back= new YoloBackground(),load_back=new YoloBackground(),load_front = new YoloBackground(),mag = new YoloBackground(YoloEngine.TeamAB[YoloEngine.MyID].PlayerMagCapasity/30f);
	private YoloWeapon btn = new YoloWeapon(0,0);
	private Triangle roti0,roti1,roti2;
	
	public static YoloObject[] ObjectTab = new YoloObject[17];
	private YoloObject[] LaddreTab = new YoloObject[4];
	
	private static Vector<YoloWeapon> Weapontab  = new Vector<YoloWeapon>();
	private static YoloWeapon bullet,weapon;
	
	public static Skill[] skilltab = new Skill[3];
	public static Vector<Skill> skillTeamBVe = new Vector<Skill>();
	public static Vector<Skill> skillTeamAVe = new Vector<Skill>();
	public static Vector<HitBox> hitBoxs = new Vector<HitBox>();
	
	private final float MOVE_SIZE_X = 2*YoloEngine.MAX_VALUE_PLAYER_SPEED/YoloEngine.display_x/YoloEngine.xdpi; // 200/display_x
	private final float MOVE_SIZE_Y = YoloEngine.MOVE_SIZE_Y/YoloEngine.display_y/YoloEngine.xdpi; // 50/display_y
	private final float MOVE_SIZE_X1 = 160f/YoloEngine.display_x/YoloEngine.xdpi;
	private final float MOVE_SIZE_Y1 = 160f/YoloEngine.display_y/YoloEngine.xdpi;
	private final float MOVE_BALL_SIZE_X = YoloEngine.MOVE_SIZE_Y/YoloEngine.display_x/YoloEngine.xdpi; // 50/display_x
	private final float MOVE_POS_X = 25f/YoloEngine.display_x/YoloEngine.xdpi;//(YoloEngine.MOVE_X/YoloEngine.display_x - MOVE_SIZE_X/2);// /MOVE_SIZE_X;  (125-100)/display_x
	private final float MOVE_POS_Y = 50f/YoloEngine.display_y/YoloEngine.xdpi; //(YoloEngine.display_y - YoloEngine.MOVE_Y)/YoloEngine.display_y + MOVE_SIZE_Y/2; // 25/display_y == move_y/2/display_y
	private float Skill1BtnTx,Skill1BtnTy,Skill2BtnTx,Skill2BtnTy,Skill3BtnTx,Skill3BtnTy;
//	private final float MOVE_POS_X1= (25f/YoloEngine.display_x);// /MOVE_SIZE_X1 ;
//	private final float MOVE_POS_Y1= (25f/YoloEngine.display_y);// /MOVE_SIZE_Y1 ; 
	private final float LIVE_BAR_SIZE_X_0 = YoloEngine.LIVE_BAR_SIZE/YoloEngine.display_x/YoloEngine.xdpi;
	private float LIVE_BAR_SIZE_X_1 = LIVE_BAR_SIZE_X_0;
	private final float LIVE_BAR_SIZE_Y = 30f/YoloEngine.display_y/YoloEngine.xdpi;
    public static float half_fx,half_bx,half_fy,half_by ;
	
	private float cameraPosX,joyBallX =(YoloGame.x2-25f)/YoloEngine.display_x/YoloEngine.xdpi //(YoloGame.x2/YoloEngine.display_x - MOVE_BALL_SIZE_X/2)// /MOVE_BALL_SIZE_X, (x2-25)dis_x
			//,joyBallX2 =(YoloGame.x2-25f)/YoloEngine.display_x/YoloEngine.xdpi
			,jumpBtnX = 1-125f/YoloEngine.display_x/YoloEngine.xdpi // 1/(MOVE_BALL_SIZE_X*2)-1.5f
			//,shotBtnX = jumpBtnX
			,crouchBtnX = 250f/YoloEngine.display_x/YoloEngine.xdpi //2.75f
			,skillBtnX = .5f - 50f/YoloEngine.display_x/YoloEngine.xdpi //  1/(MOVE_BALL_SIZE_X*2)/2
			,liveBarX_0 = 25f/YoloEngine.display_x/YoloEngine.xdpi //(0.5f/(1f/LIVE_BAR_SIZE_Y))*(1/LIVE_BAR_SIZE_X_0);	
			,joyBallX1
			,joyBackX1
			,joyBallX3
			,joyBackX3
			,XADD = 0; 

	private float cameraPosY
			,jumpBtnY = 150f/YoloEngine.display_y/YoloEngine.xdpi
			//,shotBtnY = 25f/YoloEngine.display_y/YoloEngine.xdpi
			,crouchBtnY = 25f/YoloEngine.display_y/YoloEngine.xdpi
			,liveBarY = 1-55f/YoloEngine.display_y/YoloEngine.xdpi // 1f/LIVE_BAR_SIZE_Y -1.75f;// 1-(25+30)/dis_x
			,joyBallY1
			,joyBackY1
			,joyBallY3
			,joyBackY3
			,YADD = 0; 
	
	public static boolean toLoad = true,first = false;
	private int loading_faze=0,loadingStepsCout = 41;
	
	
	private int nextBullet = 1,platformOn=0;
	public static boolean onGround = true,contact = true;
	private int ClimbingOn;
	private int S1cooldown = 0,S2cooldown = 0,S3cooldown = 0,s1=0,s2=0,s3=0;
				
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
								platformOn = i;
								onGround = true;
								
							}
						break;
					}
					onGround = false;
				}
				if(IsCollidedTop(YoloEngine.TeamAB[YoloEngine.MyID],ObjectTab[platformOn]))
				{
					if(contact)
					{
						contact = false;
						YoloEngine.sp.play(YoloEngine.SoundInd[5], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
						YoloEngine.TeamAB[YoloEngine.MyID].setAction(7);
					}
				}
				else contact = true;
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
			
			
	//------------------------------------------------------DARBINY---------------------------------------------------------------
			
			for (int i = 0;i < LaddreTab.length;i++)
			{
				if(IsIn(YoloEngine.TeamAB[YoloEngine.MyID],LaddreTab[i]))
				{
					if(YoloEngine.TeamAB[YoloEngine.MyID].isClimbingUp)
					{
						if(YoloEngine.TeamAB[YoloEngine.MyID].y+1 < LaddreTab[i].y + LaddreTab[i].dy )
						{
							ClimbingOn = i;
							YoloEngine.TeamAB[YoloEngine.MyID].vy = YoloEngine.PLAYER_CLIMBING_SPEED;
						//	YoloEngine.canClimb = false;
							YoloEngine.TeamAB[YoloEngine.MyID].canMove =  false;
							YoloEngine.TeamAB[YoloEngine.MyID].setAction(4);
						}
					}
					else if(YoloEngine.TeamAB[YoloEngine.MyID].isClimbingDown)
						{
							ClimbingOn = i;
							if(YoloEngine.TeamAB[YoloEngine.MyID].y > LaddreTab[i].y )
								YoloEngine.TeamAB[YoloEngine.MyID].vy = -YoloEngine.PLAYER_CLIMBING_SPEED;
						//	YoloEngine.canClimb = false;
							YoloEngine.TeamAB[YoloEngine.MyID].canMove =  false;
							YoloEngine.TeamAB[YoloEngine.MyID].setAction(5);
						}
					
					break;
				}
				//YoloEngine.canClimb = true;
				
			}
			if(YoloEngine.TeamAB[YoloEngine.MyID].isClimbingUp && YoloEngine.TeamAB[YoloEngine.MyID].canMove ==  false)
			{
				//if(!IsIn(YoloEngine.TeamAB[YoloEngine.MyID],LaddreTab[ClimbingOn]))
				if(YoloEngine.TeamAB[YoloEngine.MyID].y > LaddreTab[ClimbingOn].y + LaddreTab[ClimbingOn].dy)
				{
					YoloEngine.TeamAB[YoloEngine.MyID].vy = 0;
					YoloEngine.TeamAB[YoloEngine.MyID].canMove = true;
					YoloEngine.TeamAB[YoloEngine.MyID].isClimbingDown = false;
					YoloEngine.TeamAB[YoloEngine.MyID].isClimbingUp = false;
					
				}
			}
			if(YoloEngine.TeamAB[YoloEngine.MyID].isClimbingDown  && YoloEngine.TeamAB[YoloEngine.MyID].canMove ==  false)
			{
				if(YoloEngine.TeamAB[YoloEngine.MyID].y < LaddreTab[ClimbingOn].y )
				{
					YoloEngine.TeamAB[YoloEngine.MyID].vy = 0;
					YoloEngine.TeamAB[YoloEngine.MyID].canMove = true;
					YoloEngine.TeamAB[YoloEngine.MyID].isClimbingDown = false;
					YoloEngine.TeamAB[YoloEngine.MyID].isClimbingUp = false;
					
				}
			}
	
// ------------------------- Multislayer BEGIN -----------------------	

			if (YoloEngine.MULTI_ACTIVE) {
                YoloEngine.mMultislayer.sendPlayerPosition(YoloEngine.TeamAB[YoloEngine.MyID].x, YoloEngine.TeamAB[YoloEngine.MyID].y, YoloEngine.TeamAB[YoloEngine.MyID].isCrouch);
			}	
			for(int i = 0; i < YoloEngine.TeamSize*2; i++) {
				if(i == YoloEngine.MyID) continue; //skippujemy jesli dotyczy 'naszego' gracza
				if(YoloEngine.TeamAB[i].changesMade < YoloEngine.MULTI_STEPS) {
					if(YoloEngine.TeamAB[i].changesMade == 0)
					{
						YoloEngine.TeamAB[i].x = YoloEngine.TeamAB[i].x_lastX;
						YoloEngine.TeamAB[i].y = YoloEngine.TeamAB[i].y_lastX;

						YoloEngine.TeamAB[i].x_lastX = YoloEngine.TeamAB[i].x_last;
						YoloEngine.TeamAB[i].y_lastX = YoloEngine.TeamAB[i].y_last;
					}

					YoloEngine.TeamAB[i].x +=YoloEngine.TeamAB[i].x_change;
					YoloEngine.TeamAB[i].y +=YoloEngine.TeamAB[i].y_change;

					YoloEngine.TeamAB[i].changesMade++;
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
			
			
			if(YoloEngine.TeamAB[YoloEngine.MyID].canSkill1 == false ){S1cooldown++;s1++;}
			if(YoloEngine.TeamAB[YoloEngine.MyID].canSkill2 == false ){S2cooldown++;s2++;}
			if(YoloEngine.TeamAB[YoloEngine.MyID].canSkill3 == false ){S3cooldown++;s3++;}
			
			if(YoloEngine.SKILL1_COOLDOWN <= S1cooldown){S1cooldown = 0; YoloEngine.TeamAB[YoloEngine.MyID].canSkill1 = true;s1=0;}
			if(YoloEngine.SKILL2_COOLDOWN <= S2cooldown){S2cooldown = 0; YoloEngine.TeamAB[YoloEngine.MyID].canSkill2 = true;s2=0;}
			if(YoloEngine.SKILL3_COOLDOWN <= S3cooldown){S3cooldown = 0; YoloEngine.TeamAB[YoloEngine.MyID].canSkill3 = true;s3=0;}
		
			if(s1 >= YoloEngine.SKILL1_COOLDOWN/8-YoloEngine.r1 && s1 < YoloEngine.SKILL1_COOLDOWN/8-YoloEngine.r1 + 30 )s1+=30/(2*YoloEngine.r1)-1;
			else if(s1 >= 3*(YoloEngine.SKILL1_COOLDOWN/8-YoloEngine.r1) + 30 && s1 < 3*(YoloEngine.SKILL1_COOLDOWN/8-YoloEngine.r1) + 60 )s1+=30/(2*YoloEngine.r1)-1;
			else if(s1 >= 5*(YoloEngine.SKILL1_COOLDOWN/8-YoloEngine.r1) + 60 && s1 < 5*(YoloEngine.SKILL1_COOLDOWN/8-YoloEngine.r1) + 90 )s1+=30/(2*YoloEngine.r1)-1;
			else if(s1 >= 7*(YoloEngine.SKILL1_COOLDOWN/8-YoloEngine.r1) + 90 && s1 < 7*(YoloEngine.SKILL1_COOLDOWN/8-YoloEngine.r1) + 120 )s1+=30/(2*YoloEngine.r1)-1;
			
			if(s2 >= YoloEngine.SKILL2_COOLDOWN/8-YoloEngine.r2 && s2 < (YoloEngine.SKILL2_COOLDOWN/8-YoloEngine.r2) + 30 )s2+=30/(2*YoloEngine.r2)-1;
			else if(s2 >= 3*(YoloEngine.SKILL2_COOLDOWN/8-YoloEngine.r2) + 30 && s2 < 3*(YoloEngine.SKILL2_COOLDOWN/8-YoloEngine.r2) + 60 )s2+=30/(2*YoloEngine.r2)-1;
			else if(s2 >= 5*(YoloEngine.SKILL2_COOLDOWN/8-YoloEngine.r2) + 60 && s2 < 5*(YoloEngine.SKILL2_COOLDOWN/8-YoloEngine.r2) + 90 )s2+=30/(2*YoloEngine.r2)-1;
			else if(s2 >= 7*(YoloEngine.SKILL2_COOLDOWN/8-YoloEngine.r2) + 90 && s2 < 7*(YoloEngine.SKILL2_COOLDOWN/8-YoloEngine.r2) + 120 )s2+=30/(2*YoloEngine.r2)-1;
			
			if(s3 >= YoloEngine.SKILL3_COOLDOWN/8-YoloEngine.r3 && s3 < (YoloEngine.SKILL3_COOLDOWN/8-YoloEngine.r3) + 30 )s3+=30/(2*YoloEngine.r3)-1;
			else if(s3 >= 3*(YoloEngine.SKILL3_COOLDOWN/8-YoloEngine.r3) + 30 && s3 < 3*(YoloEngine.SKILL3_COOLDOWN/8-YoloEngine.r3) + 60 )s3+=30/(2*YoloEngine.r3)-1;
			else if(s3 >= 5*(YoloEngine.SKILL3_COOLDOWN/8-YoloEngine.r3) + 60 && s3 < 5*(YoloEngine.SKILL3_COOLDOWN/8-YoloEngine.r3) + 90 )s3+=30/(2*YoloEngine.r3)-1;
			else if(s3 >= 7*(YoloEngine.SKILL3_COOLDOWN/8-YoloEngine.r3) + 90 && s3 < 7*(YoloEngine.SKILL3_COOLDOWN/8-YoloEngine.r3) + 120 )s3+=30/(2*YoloEngine.r3)-1;
			//--------------------------------------------------------------------------------------------------------------------------	
			
			drawBackground(gl);

			moveBullets(gl);
			
			if(YoloEngine.TeamAB[YoloEngine.MyID].playerTeam == YoloEngine.TeamA) 
			{
				YoloEngine.TeamAB[0].drawAlly(gl,YoloEngine.MyID==0?false:true);
				YoloEngine.TeamAB[1].drawAlly(gl,YoloEngine.MyID==1?false:true);
			}
			else
			{
				YoloEngine.TeamAB[2].drawAlly(gl,YoloEngine.MyID==2?false:true);
				YoloEngine.TeamAB[3].drawAlly(gl,YoloEngine.MyID==3?false:true);
			}
			hitBox();
			
			if(YoloEngine.TeamAB[YoloEngine.MyID].playerTeam == YoloEngine.TeamA)
			{
				YoloEngine.TeamAB[2].drawOpponent(gl);
				YoloEngine.TeamAB[3].drawOpponent(gl);
			}
			else
			{
				YoloEngine.TeamAB[0].drawOpponent(gl);
				YoloEngine.TeamAB[1].drawOpponent(gl);
			}
			
			drawOponentSkills(gl);
			if(nextBullet-- <= 0 && YoloEngine.TeamAB[YoloEngine.MyID].isShoting && YoloEngine.TeamAB[YoloEngine.MyID].playerMag>0)
			{
				YoloEngine.TeamAB[YoloEngine.MyID].playerMag--;
				nextBullet = YoloEngine.TeamAB[YoloEngine.MyID].firePause;
				playerFire(0.5f,YoloEngine.TeamAB[YoloEngine.MyID].fireSprite,YoloEngine.TeamAB[YoloEngine.MyID].fireCount,YoloEngine.TeamAB[YoloEngine.MyID].fireDamage,YoloEngine.TeamAB[YoloEngine.MyID].poiseDamage);					
			}
			if(YoloEngine.TeamAB[YoloEngine.MyID].playerMag==0)
			{
				if(YoloEngine.TeamAB[YoloEngine.MyID].reloading == 0)
					YoloEngine.sp.play(YoloEngine.SoundInd[11], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
			
				if(YoloEngine.TeamAB[YoloEngine.MyID].reloading++ == YoloEngine.TeamAB[YoloEngine.MyID].playerMagReloadTime)
				{
					YoloEngine.TeamAB[YoloEngine.MyID].reloading =0;
					YoloEngine.TeamAB[YoloEngine.MyID].playerMag = YoloEngine.TeamAB[YoloEngine.MyID].PlayerMagCapasity;
					YoloEngine.sp.play(YoloEngine.SoundInd[12], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
				}
			}
			drawPlayerSkills(gl);
			drawWeapon(gl);
			drawControls(gl);
			drawPlayerMag(gl);			
			drawButtons(gl);
			drawSkillCoolDown(gl);
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
		if(object1.x + object1.px + object1.dx <= object2.x || object1.x + object1.px >= object2.x + object2.dx)return false;
		if(object1.y + object1.py + object1.dy <= object2.y || object1.y + object1.py >= object2.y + object2.dy)return false;
		
		return true;
	}
	private boolean IsIn(YoloObject object1, YoloObject object2)
	{
		if(object1.x <= object2.x || object1.x + object1.dx >= object2.x + object2.dx)return false;
		if(object1.y + object1.dy < object2.y || object1.y > object2.y + object2.dy)return false;
		
		return true;
	}
	
	private void drawWeapon(GL10 gl)
	{

		for(int i =0 ;i<YoloEngine.TeamSize*2;i++)
		{
			weapon = new YoloWeapon(YoloEngine.TeamAB[i].x, YoloEngine.TeamAB[i].y);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();

			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(YoloEngine.TeamAB[i].x +.5f, YoloEngine.TeamAB[i].y+.5f, 0f);		
			
			switch(YoloEngine.TeamAB[i].aim)
			{
			case 1:
				gl.glRotatef(45, 0, 0, 1);
				break;
			case 2:
				gl.glRotatef(90, 0, 0, 1);
				break;
			case 3:
				gl.glScalef(1, -1, 1);
				gl.glRotatef(225, 0, 0, 1);
				break;
			case 4:
				gl.glScalef(1, -1, 1);
				gl.glRotatef(180, 0, 0, 1);
				break;
			case 5:
				gl.glScalef(1, -1, 1);
				gl.glRotatef(135, 0, 0, 1);
				break;
			case 6:
				gl.glScalef(1, -1, 1);
				gl.glRotatef(90, 0, 0, 1);
				break;
			case 7:
				gl.glRotatef(315, 0, 0, 1);
				break;
			}
			if(YoloEngine.TeamAB[i].weapon < 3)gl.glScalef(.5f, .25f, 1);
			else gl.glScalef(1, 0.25f, 1);
			gl.glTranslatef(-.5f, -.5f, 0);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(YoloEngine.TeamAB[i].weaponTextureX, YoloEngine.TeamAB[i].weaponTextureY, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			weapon.draw(gl,YoloEngine.spriteSheets,0);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
	}
	
	private void drawBullet(GL10 gl, YoloWeapon bullet)
	{
		if(bullet.count ==0)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			
			gl.glTranslatef(bullet.x +.5f, bullet.y+.5f, 0f);
			switch(bullet.Aim)
			{
			case 1:
				gl.glRotatef(45, 0, 0, 1);
				break;
			case 2:
				gl.glRotatef(90, 0, 0, 1);
				break;
			case 3:
				gl.glRotatef(135, 0, 0, 1);
				break;
			case 4:
				if(bullet.sprite ==0)
					gl.glRotatef(180, 0, 0, 1);
				break;
			case 5:
				gl.glRotatef(225, 0, 0, 1);
				break;
			case 6:
				gl.glRotatef(270, 0, 0, 1);
				break;
			case 7:
				gl.glRotatef(315, 0, 0, 1);
				break;
			}
			gl.glTranslatef(-.5f, -.5f, 0);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			//if(bullet.isLeft)gl.glTranslatef(bullet.x_texture, bullet.y_texture, 0f);
			gl.glTranslatef(bullet.x_texture + .125f, bullet.y_texture, 0f);
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
			gl.glTranslatef(bullet.x +.5f, bullet.y +.5f, 0f);
			switch(bullet.Aim)
			{
			case 1:
				gl.glRotatef(45, 0, 0, 1);
				break;
			case 2:
				gl.glRotatef(90, 0, 0, 1);
				break;
			case 3:
				gl.glRotatef(135, 0, 0, 1);
				break;
			case 4:
				gl.glRotatef(180, 0, 0, 1);
				break;
			case 5:
				gl.glRotatef(225, 0, 0, 1);
				break;
			case 6:
				gl.glRotatef(270, 0, 0, 1);
				break;
			case 7:
				gl.glRotatef(315, 0, 0, 1);
				break;
			}
			gl.glTranslatef(-.5f, -.5f, 0);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			if(bullet.sprite != 0)
				if(bullet.isLeft)
					gl.glTranslatef(bullet.x_texture, bullet.y_texture, 0f);
				else
					gl.glTranslatef(bullet.x_texture +.125f, bullet.y_texture, 0f);
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
			
			switch( Weapontab.get(i).Aim)
			{
			case 0:
				Weapontab.get(i).x += Weapontab.get(i).bulletSpeed;
				break;
			case 1:
				Weapontab.get(i).x += Weapontab.get(i).bulletSpeedD;
				Weapontab.get(i).y += Weapontab.get(i).bulletSpeedD;
				break;
			case 2:
				Weapontab.get(i).y += Weapontab.get(i).bulletSpeed;
				break;
			case 3:
				Weapontab.get(i).x -= Weapontab.get(i).bulletSpeedD;
				Weapontab.get(i).y += Weapontab.get(i).bulletSpeedD;
				break;
			case 4:
				Weapontab.get(i).x -= Weapontab.get(i).bulletSpeed;
				break;
			case 5:
				Weapontab.get(i).x -= Weapontab.get(i).bulletSpeedD;
				Weapontab.get(i).y -= Weapontab.get(i).bulletSpeedD;
				break;
			case 6:
				Weapontab.get(i).y -= Weapontab.get(i).bulletSpeed;
				break;
			case 7:
				Weapontab.get(i).x += Weapontab.get(i).bulletSpeedD;
				Weapontab.get(i).y -= Weapontab.get(i).bulletSpeedD;
				break;
			}  
			drawBullet(gl, Weapontab.get(i));
			
//---------------------------------------Bullet Collision--------------------------------------------------------------
			if(Weapontab.get(i).x + 1f < 0)
			{
				Weapontab.remove(i--);
				continue;
			}
			else if(Weapontab.get(i).x > YoloEngine.LEVEL_X/YoloEngine.TX)
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
									float VolumeScale =1,lx = Math.abs(Weapontab.get(i).x-YoloEngine.TeamAB[YoloEngine.MyID].x),ly =Math.abs(Weapontab.get(i).y-YoloEngine.TeamAB[YoloEngine.MyID].y);
									if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
										VolumeScale =0;
									else
									{
										VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
									}
									if(Weapontab.get(i).sprite != YoloEngine.TeamAB[YoloEngine.MyID].fireSprite)
										YoloEngine.sp.play(YoloEngine.SoundInd[9], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
									else if(YoloEngine.TeamAB[YoloEngine.MyID].race == 0)
										YoloEngine.sp.play(YoloEngine.SoundInd[6], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
									else if(YoloEngine.TeamAB[YoloEngine.MyID].race == 1)
										YoloEngine.sp.play(YoloEngine.SoundInd[8], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
									else if(YoloEngine.TeamAB[YoloEngine.MyID].race == 2)
										YoloEngine.sp.play(YoloEngine.SoundInd[7], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
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
				
				if(Weapontab.get(i).team != YoloEngine.TeamA)
				{
					for (int x =0 ;x < skillTeamAVe.size();x++)
						if(skillTeamAVe.elementAt(x).sprite>=6 && skillTeamAVe.elementAt(x).sprite <= 12 )
							if(skillTeamAVe.elementAt(x).ret!=4)
							if(IsCollided(Weapontab.get(i), skillTeamAVe.elementAt(x)))
							{
								
								skillTeamAVe.elementAt(x).damage_buffor += Weapontab.get(i).damage;
								skillTeamAVe.elementAt(x).poiseDamageBuffor += Weapontab.get(i).poiseDamage;
								if(skillTeamAVe.elementAt(x).poiseDamageBuffor > 1)
								{
									skillTeamAVe.elementAt(x).ret = 3;
									skillTeamAVe.elementAt(x).poiseDamageBuffor =0;
								}
								Weapontab.remove(i--);
								continue out;
							}
				}
				else
				{
					for (int x =0 ;x < skillTeamBVe.size();x++)
						if(skillTeamBVe.elementAt(x).sprite>=6 && skillTeamBVe.elementAt(x).sprite <= 12 )
							if(skillTeamBVe.elementAt(x).ret!=4)
							if(IsCollided(Weapontab.get(i), skillTeamBVe.elementAt(x)))
							{
								
								skillTeamBVe.elementAt(x).damage_buffor += Weapontab.get(i).damage;
								skillTeamBVe.elementAt(x).poiseDamageBuffor += Weapontab.get(i).poiseDamage;
								if(skillTeamBVe.elementAt(x).poiseDamageBuffor > 1)
								{
									skillTeamBVe.elementAt(x).ret = 3;
									skillTeamBVe.elementAt(x).poiseDamageBuffor =0;
								}
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
			joyBallX1 = (YoloGame.x_old+YoloGame.x2-40/YoloEngine.xdpi)/YoloEngine.display_x; // MOVE_POS_X1*2 + .5f - ((YoloGame.x_old-YoloGame.x2)/YoloEngine.display_x)/MOVE_SIZE_X1;//(x2+25-80)/dis_x
			joyBallY1 = (YoloGame.y_old+YoloGame.y2-40/YoloEngine.xdpi)/YoloEngine.display_y; // MOVE_SIZE_Y1 +.5f + YoloGame.y2/YoloEngine.display_y/MOVE_SIZE_Y1;// (y2+25+40)/dis_y
			joyBackX1 = (YoloGame.x_old-80/YoloEngine.xdpi)/YoloEngine.display_x; 
			joyBackY1 = (YoloGame.y_old-80/YoloEngine.xdpi)/YoloEngine.display_y;
			drawSt(gl, joyBackX1 + XADD, joyBackY1 + YADD, MOVE_SIZE_X1, MOVE_SIZE_Y1, .25f, .125f,true);
			drawSt(gl, joyBallX1 + XADD, joyBallY1 + YADD, MOVE_SIZE_X1/2, MOVE_SIZE_Y1/2, .375f, .125f,true);
			
			joyBallX3 = (YoloGame.x_old3+YoloGame.x3-40/YoloEngine.xdpi)/YoloEngine.display_x; 
			joyBallY3 = (YoloGame.y_old3+YoloGame.y3-40/YoloEngine.xdpi)/YoloEngine.display_y; 
			joyBackX3 = (YoloGame.x_old3-80/YoloEngine.xdpi)/YoloEngine.display_x; 
			joyBackY3 = (YoloGame.y_old3-80/YoloEngine.xdpi)/YoloEngine.display_y;
			drawSt(gl, joyBackX3 + XADD, joyBackY3 + YADD, MOVE_SIZE_X1, MOVE_SIZE_Y1, .25f, .125f,true);
			drawSt(gl, joyBallX3 + XADD, joyBallY3 + YADD, MOVE_SIZE_X1/2, MOVE_SIZE_Y1/2, .375f, .125f,true);
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
		//drawSt(gl, shotBtnX + XADD, shotBtnY + YADD, MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, YoloEngine.TeamAB[YoloEngine.MyID].isShoting? .25f : .375f , 0, true);

		drawSt(gl, skillBtnX + XADD - 100f/YoloEngine.display_x/YoloEngine.xdpi, YADD, MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2,  Skill1BtnTx , Skill1BtnTy, true);
		drawSt(gl, skillBtnX + XADD, YADD, MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2,  Skill2BtnTx , Skill2BtnTy, true);
		drawSt(gl, skillBtnX + XADD + 100f/YoloEngine.display_x/YoloEngine.xdpi, YADD, MOVE_BALL_SIZE_X*2, MOVE_SIZE_Y*2, Skill3BtnTx , Skill3BtnTy, true);
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
			YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.OPPONENT_TEXTURE, YoloEngine.context, 3); 
			drawLoadingSrean(gl, 1f/loadingStepsCout);
			break;
		case 2:			
			YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.WEAPON_SPRITE, YoloEngine.context, 0);
			drawLoadingSrean(gl, 2f/loadingStepsCout);
			break;
		case 3:
			YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.BUTTON_TEXTURE, YoloEngine.context, 1);
			YoloEngine.SoundInd[59]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff1, 1);
			YoloEngine.SoundInd[55]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_inv, 1);
			drawLoadingSrean(gl, 3f/loadingStepsCout);
			break;
		case 4:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.POISON_SKILL, YoloEngine.context, 4);
				YoloEngine.SoundInd[16]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_poiosn, 1);
			}
			drawLoadingSrean(gl, 4f/loadingStepsCout);
			break;
		case 5:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.THUNDER_V_SKILL, YoloEngine.context, 5);
				YoloEngine.SoundInd[52]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_thunder, 1);
			}
			drawLoadingSrean(gl, 5f/loadingStepsCout);
			break;
		case 6:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.ARCHER_SPRITE, YoloEngine.context, 6);
				YoloEngine.SoundInd[17]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_archer_step, 1);
				YoloEngine.SoundInd[18]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_archer_shot, 1);
				YoloEngine.SoundInd[19]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_archer_hit, 1);
				YoloEngine.SoundInd[20]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_archer_death, 1);
			}
			drawLoadingSrean(gl, 6f/loadingStepsCout);
			break;
		case 7:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.WARRIOR_SPRITE, YoloEngine.context, 7);
				YoloEngine.SoundInd[21]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_step1, 1);
				YoloEngine.SoundInd[22]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_step2, 1);
				YoloEngine.SoundInd[23]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_atack, 1);
				YoloEngine.SoundInd[24]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_hit, 1);
				YoloEngine.SoundInd[25]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_death, 1);
			}
				drawLoadingSrean(gl, 7f/loadingStepsCout);
			break;
		case 8:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.MUMMY_SPRITE, YoloEngine.context, 8);
				YoloEngine.SoundInd[26]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_mummy_step1, 1);
				YoloEngine.SoundInd[27]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_mummy_step2, 1);
				YoloEngine.SoundInd[28]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_mummy_atack, 1);
				YoloEngine.SoundInd[29]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_hit, 1);
				YoloEngine.SoundInd[30]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_mummy_death, 1);
			}
				drawLoadingSrean(gl, 8f/loadingStepsCout);
			break;
		case 9:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.HAND_SPRITE, YoloEngine.context, 9);
				YoloEngine.SoundInd[31]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_hand, 1);
				YoloEngine.SoundInd[32]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_hand_hit, 1);
				YoloEngine.SoundInd[33]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_hand_death, 1);
			}
				drawLoadingSrean(gl, 9f/loadingStepsCout);
			break;
		case 10:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.BARREL_SPRITE, YoloEngine.context, 10);
				YoloEngine.SoundInd[34]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_barrel_roll, 1);
				YoloEngine.SoundInd[35]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_barrel_explosion, 1);
			}
				drawLoadingSrean(gl, 10f/loadingStepsCout);
			break;
		case 11:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.TOWER_SPRITE, YoloEngine.context, 11);
				YoloEngine.SoundInd[36]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_build, 1);
				YoloEngine.SoundInd[37]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_tower, 1);
				YoloEngine.SoundInd[38]= YoloEngine.sp.load(YoloEngine.context, R.raw.hit_wall, 1);
			}
				drawLoadingSrean(gl, 11f/loadingStepsCout);
			break;
		case 12:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.WALL_SPRITE, YoloEngine.context, 12);
				YoloEngine.SoundInd[39]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_build, 1);
				YoloEngine.SoundInd[40]= YoloEngine.sp.load(YoloEngine.context, R.raw.hit_wall, 1);
			}
				drawLoadingSrean(gl, 12f/loadingStepsCout);
			break;
		case 13:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.TRAP_SPRITE, YoloEngine.context, 13);
				YoloEngine.SoundInd[53]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_ice_trap, 1);
				YoloEngine.SoundInd[55]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_inv, 1);
			}
				drawLoadingSrean(gl, 13f/loadingStepsCout);
			break;
		case 14:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.HEAL_LONG_RAD_SPRITE, YoloEngine.context, 14);
				YoloEngine.SoundInd[54]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warmth, 1);
				YoloEngine.SoundInd[55]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_inv, 1);
			}
				drawLoadingSrean(gl, 14f/loadingStepsCout);
			break;
		case 15:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.SPIKES_SPRITE, YoloEngine.context, 15);
				YoloEngine.SoundInd[41]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_spike_trap, 1);
			}
				drawLoadingSrean(gl, 15f/loadingStepsCout);
			break;
		case 16:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.SLOW_DOWN_SPRITE, YoloEngine.context, 16);
				YoloEngine.SoundInd[42]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_slowdown, 1);
			}
				drawLoadingSrean(gl, 16f/loadingStepsCout);
			break;
		case 17:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.HEAL_SPRITE, YoloEngine.context, 17);
				YoloEngine.SoundInd[56]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_heal, 1);
				YoloEngine.SoundInd[62]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_heal_more, 1);
			}
				drawLoadingSrean(gl, 17f/loadingStepsCout);
			break;
		case 18:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.SHOCK_WAVE_SPRITE, YoloEngine.context, 18);
				YoloEngine.SoundInd[53]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_ice_trap, 1);
			}
				drawLoadingSrean(gl, 18f/loadingStepsCout);
			break;
		case 19:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.ICICLE_SPRITE, YoloEngine.context, 19);
				YoloEngine.SoundInd[57]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_icicle, 1);
			}
				drawLoadingSrean(gl, 19f/loadingStepsCout);
			break;
		case 20:
		
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.FOG_SPRITE, YoloEngine.context, 20);
				YoloEngine.SoundInd[58]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_smoke, 1);
			}
				drawLoadingSrean(gl, 20f/loadingStepsCout);
			break;
		case 21:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.LIVE_DRAIN_SPRITE, YoloEngine.context, 21);
				YoloEngine.SoundInd[43]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_drain, 1);
			}
				drawLoadingSrean(gl, 21f/loadingStepsCout);
			break;
		case 22:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.RESURECTION_SPRITE, YoloEngine.context, 22);
				YoloEngine.SoundInd[44]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_resurect, 1);
			}
				drawLoadingSrean(gl, 22f/loadingStepsCout);
			break;
		case 26:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.THUNDER_H_SPRITE, YoloEngine.context, 26);
				YoloEngine.SoundInd[60]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_lightning_h, 1);
			}
				drawLoadingSrean(gl, 26f/loadingStepsCout);
			break;
		case 27:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.HEAL_LONG_SPRITE, YoloEngine.context, 27);
				YoloEngine.SoundInd[54]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warmth, 1);
				YoloEngine.SoundInd[62]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_heal_more, 1);
			}
				drawLoadingSrean(gl, 27f/loadingStepsCout);
			break;
		case 28:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.DENIAL_SPRITE, YoloEngine.context, 28);
				YoloEngine.SoundInd[61]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_denail, 1);
			}
				drawLoadingSrean(gl, 28f/loadingStepsCout);
			break;
		case 29:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.HEALLONG_SPRITE, YoloEngine.context, 29);
				YoloEngine.SoundInd[54]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warmth, 1);
			}
				drawLoadingSrean(gl, 29f/loadingStepsCout);
			break;
		case 30:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.FIRE_BALL_SPRITE, YoloEngine.context, 30);
				YoloEngine.SoundInd[45]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_fireball, 1);
			}
				drawLoadingSrean(gl, 30f/loadingStepsCout);
			break;
		case 31:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.TELE_SPRITE, YoloEngine.context, 31);
				YoloEngine.SoundInd[46]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_teleport, 1);
			}
				drawLoadingSrean(gl, 31f/loadingStepsCout);
			break;
		case 32:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.BUFF_EFFECE_SPRITE, YoloEngine.context, 32);
				YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			}
				drawLoadingSrean(gl, 32f/loadingStepsCout);
			break;
		case 33:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.LAVA_SPRITE, YoloEngine.context, 33);
				YoloEngine.SoundInd[47]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_lava, 1);
			}
				drawLoadingSrean(gl, 33f/loadingStepsCout);
			break;
		case 39:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.THIEF_SPRITE, YoloEngine.context, 39);
				YoloEngine.SoundInd[49]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_coins, 1);
			}
				drawLoadingSrean(gl, 39f/loadingStepsCout);
			break;
		case 40:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.STAMINA_SPRITE, YoloEngine.context, 40);
				YoloEngine.SoundInd[50]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_stamina, 1);
			}
				drawLoadingSrean(gl, 40f/loadingStepsCout);
			break;
		case 41:
			if(YoloEngine.sprite_load[loading_faze])
			{
				YoloEngine.spriteSheets = TextureLoader.loadTexture(gl, YoloEngine.EARTHWAVE_SPRITE, YoloEngine.context, 41);
				YoloEngine.SoundInd[51]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_earthquake, 1);
			}
				drawLoadingSrean(gl, 41f/loadingStepsCout);
			break;
		case 42:
			back.loadTexture(gl, YoloEngine.BACKGROUND, YoloEngine.context);
			toLoad = false;
			break;
		}
		loading_faze ++;
	}
	
	/*private void playerFire(float bulletSpeed)
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
	*/
	public static void playerFire(float bulletSpeed,int sprite,int count,float damage,float poiseDamage)
	{
			bullet = new YoloWeapon(YoloEngine.TeamAB[YoloEngine.MyID].x,
				!YoloEngine.TeamAB[YoloEngine.MyID].isCrouch?YoloEngine.TeamAB[YoloEngine.MyID].y:YoloEngine.TeamAB[YoloEngine.MyID].y ,bulletSpeed);
			bullet.damage = damage;
			bullet.poiseDamage = poiseDamage;
			bullet.team = YoloEngine.TeamAB[YoloEngine.MyID].playerTeam; 
			bullet.sprite = sprite;
			bullet.x_texture = 0f;
			bullet.y_texture = 0f;
			bullet.count = count;
		//	bullet.size = 0.25f;
			bullet.isLeft = YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft;
			bullet.Aim = YoloEngine.TeamAB[YoloEngine.MyID].aim;
			Weapontab.add(bullet);
			
			float VolumeScale =1,lx = Math.abs(bullet.x-YoloEngine.TeamAB[YoloEngine.MyID].x),ly =Math.abs(bullet.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
			if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
				VolumeScale =0;
			else
			{
				VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
			}
			if(sprite == 0)
				YoloEngine.sp.play(YoloEngine.SoundInd[0], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			else if(sprite == 30)
				YoloEngine.sp.play(YoloEngine.SoundInd[45], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			else if(sprite == 19)
				YoloEngine.sp.play(YoloEngine.SoundInd[57], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			else if(sprite == 26)
				YoloEngine.sp.play(YoloEngine.SoundInd[60], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			
			if(YoloEngine.MULTI_ACTIVE)
				YoloEngine.mMultislayer.sendOpponentFire(bullet.x, bullet.y, YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft, YoloEngine.TeamAB[YoloEngine.MyID].isCrouch, sprite, count, damage, YoloEngine.TeamAB[YoloEngine.MyID].playerTeam,YoloEngine.TeamAB[YoloEngine.MyID].aim,poiseDamage);
	}
	
	
	public static void OpponentFire(float x, float y, boolean isLeft, boolean isCrouch,int sprite,int count,float damage, boolean team, int aim,float poiseDamge) //XXX oppfire nie potrzebuje isCrouch
	{
		bullet = new YoloWeapon(x,y,0.2f);
		bullet.damage = damage;
		bullet.poiseDamage = poiseDamge;
		bullet.count = count;
		bullet.team = team;
		bullet.sprite = sprite;
		bullet.x_texture = 0f;
		bullet.y_texture = 0f;
		//bullet.size = 0.25f;
		bullet.isLeft = isLeft;
		bullet.Aim = aim;
		Weapontab.add(bullet);
	}
	
	
	public static void AIFire(float x,float y,boolean isLeft,int sprite,float x_texture,float y_texture, float damage, boolean team)
	{
		
		bullet = new YoloWeapon(x,y,0.05f);//FIXME AI bullet speed
		bullet.damage = damage;
		bullet.team = team;
		bullet.sprite = sprite;
		bullet.x_texture = x_texture ;
		bullet.y_texture = y_texture;
		if(isLeft)
			bullet.Aim = 4;
		else
			bullet.Aim =0;
		bullet.isLeft = isLeft;
		Weapontab.add(bullet);
		
	}
	
	private boolean AIDraw(GL10 gl,int i,boolean Team,int sprite)
	{
		Vector<Skill> Ve = Team==YoloEngine.TeamA?skillTeamAVe:skillTeamBVe;
		boolean da = false;

		if(Ve.elementAt(i).canMove)Ve.elementAt(i).move();
		
		Ve.elementAt(i).aniSlowCounter++;
		end :
		if(Ve.elementAt(i).aniSlowCounter == Ve.elementAt(i).animation_slowdown)
		{
			Ve.elementAt(i).aniSlowCounter = -1;
			
			if(Ve.elementAt(i).y_texture == Ve.elementAt(i).yEnd && Ve.elementAt(i).x_texture == Ve.elementAt(i).xEnd)
			{
				if(Ve.elementAt(i).ret==4)
				{ 
					if(Ve.elementAt(i).resurestion_count-- < 0)
						Ve.remove(i);
					return true;
				}		
				
				Ve.elementAt(i).x_texture = Ve.elementAt(i).xStart;
				Ve.elementAt(i).y_texture = Ve.elementAt(i).yStart;
				
			//	if(isMy)
			//	{
//-------------------------------------------------Tworzenie HitBoxï¿½w----------------------------------------------------------------------------
					Ve.elementAt(i).frameCounter=0;
					switch (sprite)
					{
					case 6:
						if(Ve.elementAt(i).ret == YoloEngine.ARCHER_FIRE)
						{
							AIFire(Ve.elementAt(i).x, Ve.elementAt(i).y, Ve.elementAt(i).isLeft,6,0f,.5f,Ve.elementAt(i).damage,Ve.elementAt(i).team);
							Ve.elementAt(i).ret = YoloEngine.ARCHER_NULL;
							//if(YoloEngine.MULTI_ACTIVE)
					          //  YoloEngine.mMultislayer.sendAIFire(Ve.elementAt(i).x, Ve.elementAt(i).y, Ve.elementAt(i).isLeft,6,0f,.5f,Ve.elementAt(i).damage,Ve.elementAt(i).team);
						}
						break;
					case 11:
						if(Ve.elementAt(i).ret == YoloEngine.TOWER_FIRE)
						{
							Ve.elementAt(i).fireCounter+=YoloEngine.GAME_SKIPED_FRAMES;
							if(Ve.elementAt(i).fireCounter >= Ve.elementAt(i).fire_rate)
							{
								AIFire(Ve.elementAt(i).x, Ve.elementAt(i).y, Ve.elementAt(i).isLeft,11,0f,.125f, Ve.elementAt(i).damage,Ve.elementAt(i).team);
								Ve.elementAt(i).ret = YoloEngine.TOWER_STAND;
								Ve.elementAt(i).fireCounter =0;
							}
							//if(YoloEngine.MULTI_ACTIVE)
					            //YoloEngine.mMultislayer.sendAIFire(Ve.elementAt(i).x, Ve.elementAt(i).y, Ve.elementAt(i).isLeft,11,0f,.125f, Ve.elementAt(i).damage,Ve.elementAt(i).team);
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
						YoloEngine.sp.play(YoloEngine.SoundInd[54], YoloEngine.Volume*Ve.elementAt(i).VolumeScale, YoloEngine.Volume*Ve.elementAt(i).VolumeScale, 1, 0, 1f);
						findingSkillinMy(i, Team);
						hitBoxs.add(new HitBox(Ve.elementAt(i).x - Ve.elementAt(i).x_radius/2 + .5f ,
								Ve.elementAt(i).y - Ve.elementAt(i).y_radius/2 + .5f , Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, 1f,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,YoloEngine.TeamAB[YoloEngine.MyID].playerTeam,true, Ve.elementAt(i).id));
						YoloEngine.mMultislayer.sendHitBox(Ve.elementAt(i).x - Ve.elementAt(i).x_radius/2 + .5f,
								Ve.elementAt(i).y - Ve.elementAt(i).y_radius/2 + .5f, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, 1f,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,true, Ve.elementAt(i).id);
						break;
					default:
						Ve.elementAt(i).ret = 100;
						break;
						
					}
				//}
				break end;
			}
			if(Team == YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
			if(sprite == 7)
			{
				if(Ve.elementAt(i).frameCounter==2)
					if(Ve.elementAt(i).ret == YoloEngine.WARRIOR_ATTACK)
					{
						hitBoxs.add(new HitBox(Ve.elementAt(i).x + (Ve.elementAt(i).isLeft?-Ve.elementAt(i).x_radius/2:1),Ve.elementAt(i).y,
								Ve.elementAt(i).x_radius/2, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,Ve.elementAt(i).team,false, Ve.elementAt(i).id));
						YoloEngine.mMultislayer.sendHitBox(Ve.elementAt(i).x + (Ve.elementAt(i).isLeft?-Ve.elementAt(i).x_radius/2:1),
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius/2, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,false, Ve.elementAt(i).id);
					}
			}
			else if(sprite == 9)
			{
				if(Ve.elementAt(i).frameCounter==2)
					if(Ve.elementAt(i).ret == YoloEngine.HAND_ATTACK)
					{
						hitBoxs.add(new HitBox(Ve.elementAt(i).x + (Ve.elementAt(i).isLeft?-Ve.elementAt(i).x_radius/2:1),Ve.elementAt(i).y,
								Ve.elementAt(i).x_radius/2, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,Ve.elementAt(i).team,false, Ve.elementAt(i).id));
						YoloEngine.mMultislayer.sendHitBox(Ve.elementAt(i).x + (Ve.elementAt(i).isLeft?-Ve.elementAt(i).x_radius/2:1),
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius/2, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,false, Ve.elementAt(i).id);
					}
			}
			else if(sprite == 8)
			{
				if(Ve.elementAt(i).frameCounter==3||Ve.elementAt(i).frameCounter == 6)
					if(Ve.elementAt(i).ret == YoloEngine.MUMMY_ATTACK)
					{
						hitBoxs.add(new HitBox(Ve.elementAt(i).x + (Ve.elementAt(i).isLeft?-Ve.elementAt(i).x_radius/2:1),Ve.elementAt(i).y,
								Ve.elementAt(i).x_radius/2, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,Ve.elementAt(i).team,false, Ve.elementAt(i).id));
						YoloEngine.mMultislayer.sendHitBox(Ve.elementAt(i).x + (Ve.elementAt(i).isLeft?-Ve.elementAt(i).x_radius/2:1),
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius/2, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,false, Ve.elementAt(i).id);
					}
			}
			else if(sprite == 10)
			{
				if(Ve.elementAt(i).frameCounter==0)
					if(Ve.elementAt(i).ret == YoloEngine.BARREL_ATTACK)
					{   
						hitBoxs.add(new HitBox(Ve.elementAt(i).x + (Ve.elementAt(i).isLeft?-Ve.elementAt(i).x_radius/2-1:1),Ve.elementAt(i).y,
								Ve.elementAt(i).x_radius/2 +2, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,Ve.elementAt(i).team,false, Ve.elementAt(i).id));
						YoloEngine.mMultislayer.sendHitBox(Ve.elementAt(i).x + (Ve.elementAt(i).isLeft?-Ve.elementAt(i).x_radius/2-1:1),
								Ve.elementAt(i).y, Ve.elementAt(i).x_radius/2+2, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,false, Ve.elementAt(i).id);
					}
			}
			else if(sprite == 13)
			{
				if(Ve.elementAt(i).frameCounter==8)
					if(Ve.elementAt(i).ret == YoloEngine.TRAP_ATTACK)
					{
						hitBoxs.add(new HitBox(Ve.elementAt(i).x - Ve.elementAt(i).x_radius/2 + .5f,
								Ve.elementAt(i).y - Ve.elementAt(i).y_radius/2 + .5f , Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,Ve.elementAt(i).team,false, Ve.elementAt(i).id));
						YoloEngine.mMultislayer.sendHitBox(Ve.elementAt(i).x - Ve.elementAt(i).x_radius/2 + .5f,
								Ve.elementAt(i).y - Ve.elementAt(i).y_radius/2 + .5f, Ve.elementAt(i).x_radius, Ve.elementAt(i).y_radius, Ve.elementAt(i).damage, Ve.elementAt(i).frameDuration,
								Ve.elementAt(i).sprite,Ve.elementAt(i).isLeft,false, Ve.elementAt(i).id);
					}
			}
			
			if(sprite ==6 && Ve.elementAt(i).ret == YoloEngine.ARCHER_WALK)
			{
				if(Ve.elementAt(i).frameCounter == 1 || Ve.elementAt(i).frameCounter == 3)
					YoloEngine.sp.play(YoloEngine.SoundInd[17], YoloEngine.Volume*Ve.elementAt(i).VolumeScale, YoloEngine.Volume*Ve.elementAt(i).VolumeScale, 1, 0, 1f);
			}
			else if(sprite ==7 && Ve.elementAt(i).ret == YoloEngine.WARRIOR_WALK)
			{
				if(Ve.elementAt(i).frameCounter == 0)
					YoloEngine.sp.play(YoloEngine.SoundInd[21], YoloEngine.Volume*Ve.elementAt(i).VolumeScale, YoloEngine.Volume*Ve.elementAt(i).VolumeScale, 1, 0, 1f);
				if(Ve.elementAt(i).frameCounter == 3)
					YoloEngine.sp.play(YoloEngine.SoundInd[22], YoloEngine.Volume*Ve.elementAt(i).VolumeScale, YoloEngine.Volume*Ve.elementAt(i).VolumeScale, 1, 0, 1f);
			}
			else if(sprite ==8 && Ve.elementAt(i).ret == YoloEngine.MUMMY_WALK)
			{
				if(Ve.elementAt(i).frameCounter == 0)
					YoloEngine.sp.play(YoloEngine.SoundInd[26], YoloEngine.Volume*Ve.elementAt(i).VolumeScale, YoloEngine.Volume*Ve.elementAt(i).VolumeScale, 1, 0, 1f);
				if(Ve.elementAt(i).frameCounter == 4)
					YoloEngine.sp.play(YoloEngine.SoundInd[27], YoloEngine.Volume*Ve.elementAt(i).VolumeScale, YoloEngine.Volume*Ve.elementAt(i).VolumeScale, 1, 0, 1f);
			}
			else if(sprite ==10 && Ve.elementAt(i).ret == YoloEngine.BARREL_WALK)
			{
				if(Ve.elementAt(i).frameCounter == 0)
				{
					YoloEngine.sp.stop(YoloEngine.SoundInd[34]);
					YoloEngine.sp.play(YoloEngine.SoundInd[34], YoloEngine.Volume*Ve.elementAt(i).VolumeScale, YoloEngine.Volume*Ve.elementAt(i).VolumeScale, 1, 0, 1f);
				}
			}
			else if(sprite ==11 && Ve.elementAt(i).ret == YoloEngine.TOWER_NEW )
			{
				YoloEngine.sp.play(YoloEngine.SoundInd[36], YoloEngine.Volume*Ve.elementAt(i).VolumeScale, YoloEngine.Volume*Ve.elementAt(i).VolumeScale, 1, 0, 1f);
			}
			else if(sprite ==12 && Ve.elementAt(i).ret == YoloEngine.WALL_NEW)
			{
				YoloEngine.sp.play(YoloEngine.SoundInd[39], YoloEngine.Volume*Ve.elementAt(i).VolumeScale, YoloEngine.Volume*Ve.elementAt(i).VolumeScale, 1, 0, 1f);
			}
//--------------------------------------------------------------------------------------------------------------------------------------------		
			da = true;
			
		}
						
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(YoloEngine.TEXTURE_SIZE_X*Ve.elementAt(i).scale_x, YoloEngine.TEXTURE_SIZE_Y*Ve.elementAt(i).scale_y, 1f);
		gl.glTranslatef(Ve.elementAt(i).x/Ve.elementAt(i).scale_x + ((sprite==10)?.25f:0),
				Ve.elementAt(i).y/Ve.elementAt(i).scale_y -.25f, 0f); 
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
		gl.glTranslatef((Ve.elementAt(i).x + ((sprite==10)?.25f:0))/(Ve.elementAt(i).life/Ve.elementAt(i).MAXlife)
				,Ve.elementAt(i).y*10+8f, 0f);
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
			gl.glTranslatef(Ve.elementAt(i).x+ ((sprite==10)?.25f:0) ,
					Ve.elementAt(i).y -.25f, 0f);
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
			gl.glTranslatef(Ve.elementAt(i).x+ ((sprite==10)?.25f:0),
					Ve.elementAt(i).y -.25f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.125f, 0.875f, 0f);
			btn.draw(gl, YoloEngine.spriteSheets,Ve.elementAt(i).sprite);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		if(Ve.elementAt(i).isFrozen)
		{
			Ve.elementAt(i).canMove = false;
			if(Ve.elementAt(i).frozen_duration-- == 0)
			{
				Ve.elementAt(i).isFrozen = false;
				Ve.elementAt(i).canMove = true;
			}
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(Ve.elementAt(i).x+ ((sprite==10)?.25f:0),
					Ve.elementAt(i).y -.25f, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.75f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			btn.draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		
		if(Ve.elementAt(i).canMove && da)
		{
			Ve.elementAt(i).frameCounter++;
			Ve.elementAt(i).x_texture+=0.125f; //kolejna klatka texturki;	
			if(Ve.elementAt(i).x_texture >= 1){Ve.elementAt(i).y_texture+=0.125f; Ve.elementAt(i).x_texture=0f;}
		}
		return false;
	}
	
	private boolean LinearSkillDraw(GL10 gl,Skill skill)
	{
		if(skill.y_texture == skill.yEnd && skill.x_texture == skill.xEnd)
			return true;
		else
		{
			if(skill.sprite == 18)
			{
				skill.scale_x+=0.1f;
				skill.scale_y+=0.1f;
				skill.x-=0.05f;
				skill.y-=0.025f;
			}
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X*skill.scale_x, YoloEngine.TEXTURE_SIZE_Y*skill.scale_y, 1f);
			gl.glTranslatef(skill.x/skill.scale_x, skill.y/skill.scale_y - 0.25f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(skill.x_texture, skill.y_texture, 0f);
			btn.draw(gl, YoloEngine.spriteSheets,skill.sprite==42?41:skill.sprite);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		
		if(skill.aniSlowCounter++ == skill.animation_slowdown)
		{
			skill.aniSlowCounter = 0;
			if(skill.x_texture<0.875f)skill.x_texture+=0.125f;
			else{skill.y_texture+=0.125f; skill.x_texture=0f;}
		}	
		return false;
	}
	private void LoopSkillDraw(GL10 gl,Skill skill)
	{
		if(skill.y_texture >= skill.yEnd && skill.x_texture >= skill.xEnd)
		{
			skill.y_texture = 0;
			skill.x_texture = 0;
		}
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(YoloEngine.TEXTURE_SIZE_X*skill.scale_x, YoloEngine.TEXTURE_SIZE_Y*skill.scale_y, 1f);
		gl.glTranslatef((skill.x-.5f)/skill.scale_x, (skill.y-.5f)/skill.scale_y, 0f);
		gl.glColor4f(1f,1f,1f,1f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glTranslatef(skill.x_texture, skill.y_texture, 0f);
		btn.draw(gl, YoloEngine.spriteSheets,skill.sprite);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		if(skill.aniSlowCounter++ == skill.animation_slowdown)
		{
			skill.aniSlowCounter = 0;
			if(skill.x_texture<0.875f)skill.x_texture+=0.125f;
			else{skill.y_texture+=0.125f; skill.x_texture=0f;}
		}	
	}
	private void findingSkillinOpp(int i,boolean Team)
	{
		Vector<Skill> Ve;
		int p =0,q=YoloEngine.TeamSize;
		if(Team == YoloEngine.TeamA) Ve = skillTeamAVe;
		else Ve = skillTeamBVe;
		if(Team== YoloEngine.TeamA)
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
					Skill skill = new Skill(YoloEngine.TeamAB[j].x,YoloEngine.TeamAB[j].y,sprite-87,Team,-1);
					if(Team == YoloEngine.TeamA)
						skillTeamAVe.add(skill);
					else
						skillTeamBVe.add(skill);
				}
		}
		int k = Team == YoloEngine.TeamA? skillTeamBVe.size():skillTeamAVe.size();
		for(int j=0;j<k;j++)
		{
			if(Team == YoloEngine.TeamA)
			{
				if(skillTeamBVe.elementAt(j).sprite>=6 && skillTeamBVe.elementAt(j).sprite <= 12 )
					if(Math.abs(skillTeamBVe.elementAt(j).x-Ve.elementAt(i).x)<Ve.elementAt(i).x_radius)			
						if(Math.abs(skillTeamBVe.elementAt(j).y-Ve.elementAt(i).y)<Ve.elementAt(i).y_radius)
						{
							Skill skill = new Skill(skillTeamBVe.elementAt(j).x,skillTeamBVe.elementAt(j).y,sprite-87,Ve.elementAt(i).team,-1);
							if(Team == YoloEngine.TeamA)
								skillTeamAVe.add(skill);
							else
								skillTeamBVe.add(skill);
						}
			}
			else
			{
				if(skillTeamAVe.elementAt(j).sprite>=6 && skillTeamAVe.elementAt(j).sprite <= 12 )
					if(Math.abs(skillTeamAVe.elementAt(j).x-Ve.elementAt(i).x)<Ve.elementAt(i).x_radius)			
						if(Math.abs(skillTeamAVe.elementAt(j).y-Ve.elementAt(i).y)<Ve.elementAt(i).y_radius)
						{
							Skill skill = new Skill(skillTeamAVe.elementAt(j).x,skillTeamAVe.elementAt(j).y,sprite-87,Ve.elementAt(i).team,-1);
							if(Team == YoloEngine.TeamA)
								skillTeamAVe.add(skill);
							else
								skillTeamBVe.add(skill);
						}
			}
		}
	}
	
	private void findingSkillinMy(int i,boolean Team)
	{
		Vector<Skill> Ve;
		int p=0,q=YoloEngine.TeamSize;
		if(Team == YoloEngine.TeamA) Ve = skillTeamAVe;
		else Ve = skillTeamBVe;
		if(Team == YoloEngine.TeamB)
		{
			p=YoloEngine.TeamSize;
			q*=2;
		}
		
		int sprite =Ve.elementAt(i).sprite==14?114:Ve.elementAt(i).sprite;
		
		for(int j =p;j<q;j++)
		{
			if(Math.abs(YoloEngine.TeamAB[j].x-Ve.elementAt(i).x)<Ve.elementAt(i).x_radius)			
				if(Math.abs(YoloEngine.TeamAB[j].y-Ve.elementAt(i).y)<Ve.elementAt(i).y_radius) // rozrï¿½nianie
				{
					if(sprite==109)
					{
						if(YoloEngine.TeamAB[j].PlayerLive<=0)
						{
							Skill skill = new Skill(YoloEngine.TeamAB[j].x,YoloEngine.TeamAB[j].y,sprite-87,Ve.elementAt(i).team,-1);
							if(Team == YoloEngine.TeamA)
								skillTeamAVe.add(skill);
							else
								skillTeamBVe.add(skill);
						}
					}
					else
					{
						Skill skill = new Skill(YoloEngine.TeamAB[j].x,YoloEngine.TeamAB[j].y,sprite-87,Ve.elementAt(i).team,-1);
						skill.x = YoloEngine.TeamAB[j].x;
						skill.y = YoloEngine.TeamAB[j].y;
						if(Team == YoloEngine.TeamA)
							skillTeamAVe.add(skill);
						else
							skillTeamBVe.add(skill);
					}
				}
		}
		if(sprite != 119 && sprite != 121 && sprite != 122)
		{
		int k = Team == YoloEngine.TeamB? skillTeamBVe.size():skillTeamAVe.size();
		for(int j=0;j<k;j++)
		{	
			if(j!=i)
			if(Team == YoloEngine.TeamA)
			{
				if(skillTeamAVe.elementAt(j).sprite>=6 && skillTeamAVe.elementAt(j).sprite <= 12 )
					if(Math.abs(skillTeamAVe.elementAt(j).x-Ve.elementAt(i).x)<Ve.elementAt(i).x_radius)			
						if(Math.abs(skillTeamAVe.elementAt(j).y-Ve.elementAt(i).y)<Ve.elementAt(i).y_radius)
						{
							if(sprite==109)
							{
								if(skillTeamAVe.elementAt(j).life<=0)
								{
									Skill skill = new Skill(skillTeamAVe.elementAt(j).x,skillTeamAVe.elementAt(j).y,sprite-87,Ve.elementAt(i).team,-1);
									if(Team == YoloEngine.TeamA)
										skillTeamAVe.add(skill);
									else
										skillTeamBVe.add(skill);
								}
							}
							else
							{
								Skill skill = new Skill(skillTeamAVe.elementAt(j).x,skillTeamAVe.elementAt(j).y,sprite-87,Ve.elementAt(i).team,-1);
								skill.x = skillTeamBVe.elementAt(j).x;
								skill.y = skillTeamBVe.elementAt(j).y;
								if(Team == YoloEngine.TeamA)
									skillTeamAVe.add(skill);
								else
									skillTeamBVe.add(skill);
							}
						}
			}
			else
			{
				if(skillTeamBVe.elementAt(j).sprite>=6 && skillTeamBVe.elementAt(j).sprite <= 12 )
					if(Math.abs(skillTeamBVe.elementAt(j).x-Ve.elementAt(i).x)<Ve.elementAt(i).x_radius)			
						if(Math.abs(skillTeamBVe.elementAt(j).y-Ve.elementAt(i).y)<Ve.elementAt(i).y_radius)
						{
							if(sprite==109)
							{
								if(skillTeamBVe.elementAt(j).life<=0)
								{
									Skill skill = new Skill(skillTeamBVe.elementAt(j).x,skillTeamBVe.elementAt(j).y,sprite-87,Ve.elementAt(i).team,-1);
									if(Team == YoloEngine.TeamA)
										skillTeamAVe.add(skill);
									else
										skillTeamBVe.add(skill);
								}
							}
							else
							{
								Skill skill = new Skill(skillTeamBVe.elementAt(j).x,skillTeamBVe.elementAt(j).y,sprite-87,Ve.elementAt(i).team,-1);
								skill.x = skillTeamBVe.elementAt(j).x;
								skill.y = skillTeamBVe.elementAt(j).y;
								if(Team == YoloEngine.TeamA)
									skillTeamAVe.add(skill);
								else
									skillTeamBVe.add(skill);
							}
						}
			}
		}
		}
	}
	
	private void drawPlayerSkills(GL10 gl)
	{
		Vector<Skill> Ve;
		if(YoloEngine.TeamAB[YoloEngine.MyID].playerTeam == YoloEngine.TeamA)
			Ve = skillTeamAVe;
		else
			Ve = skillTeamBVe;
		int sprite;
		for(int i = 0; i < Ve.size(); i++)
		{
			sprite = Ve.elementAt(i).sprite;
			if(sprite>5 && sprite<15)
			{
				if(AIDraw(gl, i, YoloEngine.TeamAB[YoloEngine.MyID].playerTeam, Ve.elementAt(i).sprite))//Rysuje kolejne AI
					{
						i--;continue;
					}
			}
			else if(sprite == 33)
			{
				LoopSkillDraw(gl, Ve.elementAt(i));
				if(Ve.elementAt(i).lava_duration-- <= 0)
				{
					Ve.remove(i--);
					continue;
				}			
			}
			else
			{		
				if(Ve.elementAt(i).x_texture==0 && Ve.elementAt(i).y_texture==0)
				{
					if(sprite == 108||sprite == 103||sprite == 126||sprite == 127)
					{
						findingSkillinOpp(i,YoloEngine.TeamAB[YoloEngine.MyID].playerTeam);
						Ve.remove(i--);
						continue;
					}
					else if(sprite == 104||sprite == 109||sprite == 119||sprite == 121||sprite == 122)
					{
						findingSkillinMy(i,YoloEngine.TeamAB[YoloEngine.MyID].playerTeam);
						Ve.remove(i--);
						continue;
					}
					else if(sprite==19)
					{
						Ve.remove(i);
						continue;
					}
					else if(sprite == 26)
					{
						Ve.remove(i);
						continue;
					}
					else if(sprite == 120)
					{
						Ve.remove(i);
						continue;
					}
					else if(sprite == 30)
					{
						Ve.remove(i);
						continue;
					}

				}
				
				if(sprite == 41)
					Ve.elementAt(i).x+=YoloEngine.WARRIOR_SPEED*2;
				else if(sprite == 42)
					Ve.elementAt(i).x-=YoloEngine.WARRIOR_SPEED*2;
				if(LinearSkillDraw(gl, Ve.elementAt(i)))
				{
					Ve.remove(i--);
				}
						
			}		
		}
	}
	
	
	
	private void drawOponentSkills (GL10 gl)
	{		
		Vector<Skill> Ve;
		if(!YoloEngine.TeamAB[YoloEngine.MyID].playerTeam == YoloEngine.TeamA)
			Ve = skillTeamAVe;
		else
			Ve = skillTeamBVe;
		int sprite;
		for(int i = 0; i < Ve.size(); i++)
		{
			sprite = Ve.elementAt(i).sprite;
			if(sprite>5&&sprite<15)//Dla AI
			{
				if(AIDraw(gl, i, !YoloEngine.TeamAB[YoloEngine.MyID].playerTeam, Ve.elementAt(i).sprite)) 
				{
					i--;continue;
				}
			}
			else if(sprite == 28)
			{
				LoopSkillDraw(gl, Ve.elementAt(i));
				
				if(YoloEngine.TeamAB[YoloEngine.MyID].deniled-- == 0)
				{
					YoloEngine.TeamAB[YoloEngine.MyID].isPlayerDenialed = false;
					Ve.remove(i--);
					continue;
				}			
			}
			else if(sprite == 33)
			{
				LoopSkillDraw(gl, Ve.elementAt(i));
				if(Ve.elementAt(i).lava_duration-- <= 0)
				{
					Ve.remove(i--);
					continue;
				}			
			}
			else
			{	
				if(Ve.elementAt(i).x_texture==0 && Ve.elementAt(i).y_texture==0)
					if(sprite == 108||sprite == 103||sprite == 126||sprite == 127)
					{
						findingSkillinOpp(i,!YoloEngine.TeamAB[YoloEngine.MyID].playerTeam);
						Ve.remove(i);
						continue;
					}
					else if(sprite == 104||sprite == 109||sprite == 119||sprite == 121||sprite == 122)
					{
						findingSkillinMy(i,!YoloEngine.TeamAB[YoloEngine.MyID].playerTeam);
						Ve.remove(i);
						continue;
					}
					else if(sprite==19)
					{
						Ve.remove(i);
						continue;
					}
					else if(sprite == 26)
					{
						Ve.remove(i);
						continue;
					}
					else if(sprite == 120)
					{
						Ve.remove(i);
						continue;
					}
				
				if(sprite == 41)
					Ve.elementAt(i).x+=YoloEngine.WARRIOR_SPEED*2;
				else if(sprite == 42)
					Ve.elementAt(i).x-=YoloEngine.WARRIOR_SPEED*2;
				
				if(LinearSkillDraw(gl, Ve.elementAt(i)))
				{
					Ve.remove(i--);
				}
			}
		}
	}
	
	private int findSkillByID(int id,Vector<Skill> Ve)
	{
		int i =Ve.size()/2,p=0,k=Ve.size();
		while(Ve.elementAt(i).id != id && p!=k)
		{
			if(Ve.elementAt(i).id>id)
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
				int p = ((hitBoxs.elementAt(i).efectOnMySkill?hitBoxs.elementAt(i).team:!hitBoxs.elementAt(i).team) == YoloEngine.TeamA)?0:YoloEngine.TeamSize,
						k=YoloEngine.TeamSize+p;
				
				for(int j = p;j<k;j++)
				{
					if(IsCollided(hitBoxs.elementAt(i), YoloEngine.TeamAB[j]))
						if(!hitBoxs.elementAt(i).hitAIs.contains(YoloEngine.TeamAB[j].playerID))
						{
							switch((int)hitBoxs.elementAt(i).sprite)
							{
							case 4:
								YoloEngine.TeamAB[j].poisoned = 300;
								YoloEngine.TeamAB[j].isPlayerPoisoned = true;
								break;
							case 14:
								if(YoloEngine.TeamAB[j].PlayerLive + hitBoxs.elementAt(i).damage < YoloEngine.TeamAB[j].PLAYER_LIVE_MAX && YoloEngine.TeamAB[j].PlayerLive>0)
									YoloEngine.TeamAB[j].PlayerLive += hitBoxs.elementAt(i).damage;
								else
									YoloEngine.TeamAB[j].PlayerLive = YoloEngine.TeamAB[j].PLAYER_LIVE_MAX;
								break;
							case 18:
								YoloEngine.TeamAB[j].frozen = 100;
								YoloEngine.TeamAB[j].isPlayerFrozen = true;
								break;
							case 32:
								YoloEngine.TeamAB[j].buffed = YoloEngine.buffDuration;
								YoloEngine.TeamAB[j].PlayerDmgBuff = hitBoxs.elementAt(i).damage;
								YoloEngine.TeamAB[j].isPlayerBuff = true;
								break;
							case 34:
								YoloEngine.TeamAB[j].fireRated = YoloEngine.buffDuration;
								YoloEngine.TeamAB[j].isPlayerFireRateBuff = true;
								break;
							case 35:
								YoloEngine.TeamAB[j].reloadspeeded = YoloEngine.buffDuration;
								YoloEngine.TeamAB[j].isPlayerMagReloadBuff = true;
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
								YoloEngine.TeamAB[j].healBuffer = (int)hitBoxs.elementAt(i).damage;
								YoloEngine.TeamAB[j].isBeingHealed = true;
								break;
							case 119:
								YoloEngine.TeamAB[j].buffed = YoloEngine.buffDuration;
								YoloEngine.TeamAB[j].PlayerDmgBuff = hitBoxs.elementAt(i).damage;
								YoloEngine.TeamAB[j].isPlayerBuff = true;
								break;
							case 121:
								YoloEngine.TeamAB[j].fireRated = YoloEngine.buffDuration;
								YoloEngine.TeamAB[j].isPlayerFireRateBuff = true;
								break;
							case 122:
								YoloEngine.TeamAB[j].reloadspeeded = YoloEngine.buffDuration;
								YoloEngine.TeamAB[j].isPlayerMagReloadBuff = true;
								break;
							case 126:
								if(hitBoxs.elementAt(i).team == YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
								{
									YoloEngine.TeamAB[YoloEngine.MyID].coin += hitBoxs.elementAt(i).damage;
								}
								else
								{
									YoloEngine.TeamAB[j].coin -= hitBoxs.elementAt(i).damage;
								}
								break;
							case 127:
								YoloEngine.TeamAB[j].canSkill1 = false;
								YoloEngine.TeamAB[j].canSkill2 = false;
								YoloEngine.TeamAB[j].canSkill3 = false;
								break;
							default:
								if(!YoloEngine.TeamAB[j].isPlayerInvincible)
								{
									YoloEngine.sp.play(YoloEngine.SoundInd[10], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
									YoloEngine.TeamAB[j].PlayerLive -= hitBoxs.elementAt(i).damage*YoloEngine.TeamAB[j].Player_Dmg_reduction;
								}
							}
							if(hitBoxs.elementAt(i).sprite!=33)hitBoxs.elementAt(i).hitAIs.add(YoloEngine.TeamAB[j].playerID);
						}
				
				}
				Vector<Skill> Ve ;
				if(hitBoxs.elementAt(i).team == YoloEngine.TeamA)
					if(hitBoxs.elementAt(i).efectOnMySkill == true)
						Ve = skillTeamAVe;
					else 
						Ve = skillTeamBVe;
				else if(hitBoxs.elementAt(i).efectOnMySkill == true)
						Ve = skillTeamBVe;
					else
						Ve = skillTeamAVe;
				
						for(int j = 0; j<Ve.size();j++) 
							if(Ve.elementAt(j).sprite >= 6 && Ve.elementAt(j).sprite <= 12 && Ve.elementAt(j).sprite !=10 ) 
								if(IsCollided(hitBoxs.elementAt(i),Ve.elementAt(j)))
								{
									if(!hitBoxs.elementAt(i).hitAIs.contains(Ve.elementAt(j).id))
									{
										switch((int)hitBoxs.elementAt(i).sprite)
										{
										case 4:
											Ve.elementAt(j).poison_duration = 300;
											Ve.elementAt(j).isPoisoned =true;
											break;
										case 14:
											if(Ve.elementAt(j).life +hitBoxs.elementAt(i).damage < Ve.elementAt(j).MAXlife)
												Ve.elementAt(j).life +=hitBoxs.elementAt(i).damage;
											else
												Ve.elementAt(j).life=Ve.elementAt(j).MAXlife;
											break;
										case 18:
											Ve.elementAt(j).frozen_duration = 100;
											Ve.elementAt(j).isFrozen = true;
											break;
										case 32:
											break;
										case 103:
											Ve.elementAt(j).slowDown_duration = 300;
											Ve.elementAt(j).isSlowDown = true;
											break;
										case 104:
											if(Ve.elementAt(j).life +hitBoxs.elementAt(i).damage < Ve.elementAt(j).MAXlife && Ve.elementAt(j).life>0)
												Ve.elementAt(j).life +=hitBoxs.elementAt(i).damage;
											else
												Ve.elementAt(j).life=Ve.elementAt(j).MAXlife;
											break;
										case 109:
											if(Ve.elementAt(j).life<=0)
												Ve.elementAt(j).life = Ve.elementAt(j).MAXlife/2;
											break;
										default:
											Ve.elementAt(j).life -= hitBoxs.elementAt(i).damage;
										}
										hitBoxs.elementAt(i).hitAIs.add(Ve.elementAt(j).id);
									}
								}

			}
			else 
			{
				hitBoxs.remove(i);
				continue;
			}
			Vector<Skill> Ve = hitBoxs.elementAt(i).team == YoloEngine.TeamA?skillTeamAVe:skillTeamBVe;
			if(hitBoxs.elementAt(i).team != YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
			if(hitBoxs.elementAt(i).sprite >5 && hitBoxs.elementAt(i).sprite <11)//jeï¿½eli AI
			{
				int id = findSkillByID(hitBoxs.elementAt(i).ID,Ve);
				Ve.elementAt(id).isLeft = hitBoxs.elementAt(i).isLeft;
				Ve.elementAt(id).x = hitBoxs.elementAt(i).x - (Ve.elementAt(id).isLeft?-Ve.elementAt(i).x_radius/2:1);
				Ve.elementAt(id).y = hitBoxs.elementAt(i).y;
				
					
					switch(Ve.elementAt(id).sprite)
					{
					case 6:
						if(!Ve.elementAt(id).isLeft)
						{
							Ve.elementAt(id).x_texture = Ve.elementAt(id).xStart = 0;
							Ve.elementAt(id).y_texture = Ve.elementAt(id).yStart = 0.25f;
							Ve.elementAt(id).xEnd = .875f;
							Ve.elementAt(id).yEnd = 0.25f;
						}
						else
						{
							Ve.elementAt(id).x_texture = Ve.elementAt(id).xStart = 0;
							Ve.elementAt(id).y_texture = Ve.elementAt(id).yStart = 0.125f;
							Ve.elementAt(id).xEnd = .875f;
							Ve.elementAt(id).yEnd = 0.125f;
						}
						Ve.elementAt(id).ret = YoloEngine.ARCHER_FIRE;
						break;
					case 7:
						if(Ve.elementAt(id).isLeft)
						{
							Ve.elementAt(id).x_texture = Ve.elementAt(id).xStart = 0.5f;
							Ve.elementAt(id).y_texture = Ve.elementAt(id).yStart = 0.125f;
							Ve.elementAt(id).xEnd = .875f;
							Ve.elementAt(id).yEnd = 0.125f;
						}
						else
						{
							Ve.elementAt(id).x_texture = Ve.elementAt(id).xStart = 0;
							Ve.elementAt(id).y_texture = Ve.elementAt(id).yStart = 0.25f;
							Ve.elementAt(id).xEnd = .375f;
							Ve.elementAt(id).yEnd = 0.25f;
						}
						Ve.elementAt(id).ret = YoloEngine.WARRIOR_ATTACK;
						break;
					case 8:
						if(Ve.elementAt(id).isLeft)
						{
							Ve.elementAt(id).x_texture = Ve.elementAt(id).xStart = 0;
							Ve.elementAt(id).y_texture = Ve.elementAt(id).yStart = 0.25f;
							Ve.elementAt(id).xEnd = .75f;
							Ve.elementAt(id).yEnd = 0.25f;
						}
						else
						{
							Ve.elementAt(id).x_texture = Ve.elementAt(id).xStart = 0f;
							Ve.elementAt(id).y_texture = Ve.elementAt(id).yStart = 0.375f;
							Ve.elementAt(id).xEnd = .75f;
							Ve.elementAt(id).yEnd = 0.375f;
						}
						Ve.elementAt(id).ret = YoloEngine.MUMMY_ATTACK;
						break;
					case 10:
						Ve.elementAt(id).x_texture = Ve.elementAt(id).xStart = 0;
						Ve.elementAt(id).y_texture = Ve.elementAt(id).yStart = 0.25f;
						Ve.elementAt(id).xEnd = .875f;
						Ve.elementAt(id).yEnd = 0.5f;
						Ve.elementAt(id).ret = YoloEngine.BARREL_ATTACK;
						break;
					}
				}
		}
	}
	
	private void drawSkillCoolDown(GL10 gl)
	{
		if(YoloEngine.TeamAB[YoloEngine.MyID].isUsingSkill)
		{
			switch(YoloEngine.usedSkill)
			{
			case 0:
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glLoadIdentity();
				gl.glPushMatrix();
				gl.glTranslatef(XADD, YADD, 0);
				roti0.draw(gl, 0);
				gl.glPopMatrix();
				gl.glLoadIdentity();
				break;
			case 1:
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glLoadIdentity();
				gl.glPushMatrix();
				gl.glTranslatef(XADD, YADD, 0);
				roti1.draw(gl, 0);
				gl.glPopMatrix();
				gl.glLoadIdentity();
				break;
			case 2:
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glLoadIdentity();
				gl.glPushMatrix();
				gl.glTranslatef(XADD, YADD, 0);
				roti2.draw(gl, 0);
				gl.glPopMatrix();
				gl.glLoadIdentity();
				break;
			}
		}
		if(!YoloEngine.TeamAB[YoloEngine.MyID].canSkill1)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glTranslatef(XADD, YADD, 0);
			roti0.draw(gl, s1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		
		if(!YoloEngine.TeamAB[YoloEngine.MyID].canSkill2)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glTranslatef(XADD, YADD, 0);
			roti1.draw(gl, s2);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		
		if(!YoloEngine.TeamAB[YoloEngine.MyID].canSkill3)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glTranslatef(XADD, YADD, 0);
			roti2.draw(gl, s3);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		
	}
	private void drawPlayerMag(GL10 gl)
	{
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glTranslatef(25f/YoloEngine.display_x/YoloEngine.xdpi+XADD, 1-90f/YoloEngine.display_y/YoloEngine.xdpi+YADD, 0f);
		gl.glScalef((YoloEngine.LIVE_BAR_SIZE/YoloEngine.display_x/YoloEngine.xdpi)*(YoloEngine.TeamAB[YoloEngine.MyID].playerMag/30f),LIVE_BAR_SIZE_Y, 1f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glColor4f(1f,1f,1f,1f);
		gl.glTranslatef(0f, 0.875f, 0f);
		mag.drawPartial(gl,YoloEngine.TeamAB[YoloEngine.MyID].playerMag/30f);
		gl.glPopMatrix();
		gl.glLoadIdentity();
	}
	
	public static void weaponSelect()
	{
		//TODO load weapon statistics
		switch(YoloEngine.TeamAB[YoloEngine.MyID].weapon)
		{
		case 0:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .25f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = 0f;
			break;
		case 1:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .375f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = 0f;
			break;
		case 2:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .5f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = 0f;
			break;
		case 4:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .625f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = 0f;
			break;
		case 5:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .75f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = 0f;
			break;
		case 6:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .875f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = 0f;
			break;
		case 7:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .0f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = .125f;
			break;
		case 8:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .125f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = .125f;
			break;
		case 9:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .25f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = .125f;
			break;
		}
	}
	
	/*public static void givePlayerID()//wykonywane przy doï¿½aczeniu gracza
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
	*/
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0f, 1f, 0f, 1f, -10f, 10f);
		
	}
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	    gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glEnable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
//-------------------------------------------WCZYTYWANIE TEXTUREK----------------------------------------------
		TextureLoader = new YoloTexture(gl,45);
		back.loadTexture(gl, R.drawable.aniol_tlo_loading, YoloEngine.context);
		load_back.loadTexture(gl, R.drawable.pasek_back, YoloEngine.context);
		load_front.loadTexture(gl, R.drawable.pasek_wypelnienie, YoloEngine.context);
		
		YoloEngine.TeamAB[YoloEngine.MyID].race = YoloEngine.currentPlayerInfo.getRace();
		YoloEngine.TeamAB[YoloEngine.MyID].weapon = YoloEngine.currentPlayerInfo.getWEQ();
		weaponSelect();
		
		boolean test = false;
		if(test)
		{
			 DataInputStream inputStream ;
			  
		     try 
		     {
				 inputStream = new DataInputStream(YoloEngine.context.getResources().openRawResource(R.raw.lvl1));
				 YoloEngine.LEVEL_X = inputStream.readInt();
			     YoloEngine.LEVEL_Y = inputStream.readInt();
			     int l= inputStream.readInt();
			     ObjectTab = new YoloObject[l];
				 for(int i =0;i<l;i++)
				 {
				    ObjectTab[i] = new YoloObject(inputStream.readInt(), inputStream.readInt(), inputStream.readInt(), inputStream.readInt());
				 }
				 l = inputStream.readInt();
				 LaddreTab = new YoloObject[l];
				 for(int i =0;i<l;i++)
				 {
					 LaddreTab[i] = new YoloObject(inputStream.readInt(), inputStream.readInt(), inputStream.readInt(), inputStream.readInt());
				 }
						 
			     inputStream.close();		    
			 } catch (FileNotFoundException e) {System.out.println("xxxxxxxxxxxxxxxxxxxxxxx"+e.getMessage());} catch (IOException e) {System.out.println("xxxxxxxxxxxxxxxxxxxxxxx"+e.getMessage());}
		}
		else
		{
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
		
			LaddreTab[0]= new YoloObject(542, 616, 105, 331);
			LaddreTab[1]= new YoloObject(1381, 621, 105, 326);
			LaddreTab[2]= new YoloObject(1859, 947, 105, 340);
			LaddreTab[3]= new YoloObject(1865, 40, 105, 595);
		}
		
		YoloEngine.LEVEL_SIZE_X = YoloEngine.LEVEL_X/(YoloEngine.display_x/YoloEngine.LEVEL_scale); 
		YoloEngine.LEVEL_SIZE_Y = YoloEngine.LEVEL_Y/(YoloEngine.display_y/YoloEngine.LEVEL_scale); 
		YoloEngine.GAME_PROJECTION_X = YoloEngine.display_x/YoloEngine.LEVEL_SIZE_X;
		YoloEngine.GAME_PROJECTION_Y = YoloEngine.display_y/YoloEngine.LEVEL_SIZE_Y;
		YoloEngine.TEXTURE_SIZE_X = YoloEngine.TX/(YoloEngine.display_x/YoloEngine.LEVEL_scale);
		YoloEngine.TEXTURE_SIZE_Y = YoloEngine.TY/(YoloEngine.display_y/YoloEngine.LEVEL_scale);
		YoloEngine.LIFE_BAR_Y =YoloEngine.TY/10f/(YoloEngine.display_y/YoloEngine.LEVEL_scale);
		half_fx = (1f/YoloEngine.TEXTURE_SIZE_X/2f -.5f);
		half_bx = (1f/YoloEngine.TEXTURE_SIZE_X/2f +.5f);
		half_fy = (1f/YoloEngine.TEXTURE_SIZE_Y/2f -.5f);
		half_by = (1f/YoloEngine.TEXTURE_SIZE_Y/2f +.5f);

		roti0 = new Triangle(skillBtnX - 100/YoloEngine.display_x/YoloEngine.xdpi, 0, 100/YoloEngine.display_x/YoloEngine.xdpi, 100/YoloEngine.display_y/YoloEngine.xdpi, YoloEngine.SKILL1_COOLDOWN,YoloEngine.r1);
		roti1 = new Triangle(skillBtnX, 0, 100/YoloEngine.display_x/YoloEngine.xdpi, 100/YoloEngine.display_y/YoloEngine.xdpi, YoloEngine.SKILL2_COOLDOWN,YoloEngine.r2);
		roti2 = new Triangle(skillBtnX + 100/YoloEngine.display_x/YoloEngine.xdpi, 0, 100/YoloEngine.display_x/YoloEngine.xdpi, 100/YoloEngine.display_y/YoloEngine.xdpi, YoloEngine.SKILL3_COOLDOWN,YoloEngine.r3);
		//givePlayerID();
		
//------------------------------------------INICJOWANIE OBIEKTï¿½W FIZYCZNYCH----------------------------------		
		
		
		YoloEngine.sprite_load[0] = true;
		YoloEngine.sprite_load[1] = true;
		YoloEngine.sprite_load[2] = true;
		YoloEngine.sprite_load[3] = true;
	
		YoloEngine.sprite_load[YoloEngine.SkillSprite1<45?YoloEngine.SkillSprite1 : YoloEngine.SkillSprite1-87] = true;//Zaleï¿½y od playera
		YoloEngine.sprite_load[YoloEngine.SkillSprite2<45?YoloEngine.SkillSprite2 : YoloEngine.SkillSprite2-87] = true;//Zaleï¿½y od playera
		YoloEngine.sprite_load[YoloEngine.SkillSprite3<45?YoloEngine.SkillSprite3 : YoloEngine.SkillSprite3-87] = true;//Zaleï¿½y od playera
		if(YoloEngine.SkillSprite3==14||YoloEngine.SkillSprite2==14||YoloEngine.SkillSprite1==14)YoloEngine.sprite_load[27]=true;
		if(YoloEngine.SkillSprite3==36||YoloEngine.SkillSprite2==36||YoloEngine.SkillSprite1==36)YoloEngine.sprite_load[32]=true;
		if(YoloEngine.SkillSprite3==37||YoloEngine.SkillSprite2==37||YoloEngine.SkillSprite1==37)YoloEngine.sprite_load[32]=true;
		if(YoloEngine.SkillSprite3==38||YoloEngine.SkillSprite2==38||YoloEngine.SkillSprite1==38)YoloEngine.sprite_load[32]=true;
		if(YoloEngine.SkillSprite3==43||YoloEngine.SkillSprite2==43||YoloEngine.SkillSprite1==43)YoloEngine.sprite_load[41]=true;
		if(YoloEngine.SkillSprite3==120||YoloEngine.SkillSprite2==120||YoloEngine.SkillSprite1==120)YoloEngine.sprite_load[32]=true;
		if(YoloEngine.SkillSprite3==121||YoloEngine.SkillSprite2==121||YoloEngine.SkillSprite1==121)YoloEngine.sprite_load[32]=true;
		if(YoloEngine.SkillSprite3==122||YoloEngine.SkillSprite2==122||YoloEngine.SkillSprite1==122)YoloEngine.sprite_load[32]=true;
		if(YoloEngine.SkillSprite3==123||YoloEngine.SkillSprite2==123||YoloEngine.SkillSprite1==123)YoloEngine.sprite_load[32]=true;
		if(YoloEngine.SkillSprite3==124||YoloEngine.SkillSprite2==124||YoloEngine.SkillSprite1==124)YoloEngine.sprite_load[32]=true;
		
//---------------------------------------------INICJOWANIE DZWIEKOW------------------------------------------------
		YoloEngine.sp = new SoundPool(63, AudioManager.STREAM_MUSIC, 0);
		YoloEngine.SoundInd = new int[63];
		
		YoloEngine.SoundInd[0]= YoloEngine.sp.load(YoloEngine.context, R.raw.shot, 1);
		YoloEngine.SoundInd[1]= YoloEngine.sp.load(YoloEngine.context, R.raw.step_grass2, 1);
		YoloEngine.SoundInd[2]= YoloEngine.sp.load(YoloEngine.context, R.raw.step_grass1, 1);
		YoloEngine.SoundInd[3]= YoloEngine.sp.load(YoloEngine.context, R.raw.step_ladder1, 1);
		YoloEngine.SoundInd[4]= YoloEngine.sp.load(YoloEngine.context, R.raw.jump, 1);
		YoloEngine.SoundInd[5]= YoloEngine.sp.load(YoloEngine.context, R.raw.jump_landing, 1);
		YoloEngine.SoundInd[6]= YoloEngine.sp.load(YoloEngine.context, R.raw.hurt_angel_shot, 1);
		YoloEngine.SoundInd[7]= YoloEngine.sp.load(YoloEngine.context, R.raw.hurt_nekro_shot, 1);
		YoloEngine.SoundInd[8]= YoloEngine.sp.load(YoloEngine.context, R.raw.hurt_devil_shot, 1);
		YoloEngine.SoundInd[9]= YoloEngine.sp.load(YoloEngine.context, R.raw.hurt_arrow, 1);
		YoloEngine.SoundInd[10]= YoloEngine.sp.load(YoloEngine.context, R.raw.hurt_ai, 1);
		YoloEngine.SoundInd[11]= YoloEngine.sp.load(YoloEngine.context, R.raw.reload2, 1);
		YoloEngine.SoundInd[12]= YoloEngine.sp.load(YoloEngine.context, R.raw.reload1, 1);
		YoloEngine.SoundInd[13]= YoloEngine.sp.load(YoloEngine.context, R.raw.angel_death, 1);
		YoloEngine.SoundInd[14]= YoloEngine.sp.load(YoloEngine.context, R.raw.necromancer_death, 1);
		YoloEngine.SoundInd[15]= YoloEngine.sp.load(YoloEngine.context, R.raw.devil_death, 1);
	
		
//-----------------------------------------------------------------------------------------------------------------
		switch(YoloEngine.SkillSprite1)
		{
		case 4:
			Skill1BtnTx = 0.625f;
			Skill1BtnTy = 0.25f;
			YoloEngine.SoundInd[16]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_poiosn, 1);
			break;
		case 5:
			Skill1BtnTx = 0f;
			Skill1BtnTy = 0.625f;
			YoloEngine.SoundInd[52]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_thunder, 1);
			break;	
		case 6:
			Skill1BtnTx = 0f;
			Skill1BtnTy = 0.25f;
			YoloEngine.SoundInd[17]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_archer_step, 1);
			YoloEngine.SoundInd[18]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_archer_shot, 1);
			YoloEngine.SoundInd[19]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_archer_hit, 1);
			YoloEngine.SoundInd[20]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_archer_death, 1);
			break;	
		case 7:
			Skill1BtnTx = 0.375f;
			Skill1BtnTy = 0.375f;
			YoloEngine.SoundInd[21]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_step1, 1);
			YoloEngine.SoundInd[22]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_step2, 1);
			YoloEngine.SoundInd[23]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_atack, 1);
			YoloEngine.SoundInd[24]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_hit, 1);
			YoloEngine.SoundInd[25]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_death, 1);
			break;	
		case 8:
			Skill1BtnTx = 0.5f;
			Skill1BtnTy = 0.25f;
			YoloEngine.SoundInd[26]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_mummy_step1, 1);
			YoloEngine.SoundInd[27]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_mummy_step2, 1);
			YoloEngine.SoundInd[28]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_mummy_atack, 1);
			YoloEngine.SoundInd[29]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_hit, 1);
			YoloEngine.SoundInd[30]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_mummy_death, 1);
			break;	
		case 9:
			Skill1BtnTx = 0.25f;
			Skill1BtnTy = 0.25f;
			YoloEngine.SoundInd[31]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_hand, 1);
			YoloEngine.SoundInd[32]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_hand_hit, 1);
			YoloEngine.SoundInd[33]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_hand_death, 1);
			break;	
		case 10:
			Skill1BtnTx = 0.125f;
			Skill1BtnTy = 0.25f;
			YoloEngine.SoundInd[34]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_barrel_roll, 1);
			YoloEngine.SoundInd[35]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_barrel_explosion, 1);
			break;	
		case 11:
			Skill1BtnTx = 0.125f;
			Skill1BtnTy = 0.375f;
			YoloEngine.SoundInd[36]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_build, 1);
			YoloEngine.SoundInd[37]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_tower, 1);
			YoloEngine.SoundInd[38]= YoloEngine.sp.load(YoloEngine.context, R.raw.hit_wall, 1);
			break;	
		case 12:
			Skill1BtnTx = 0.25f;
			Skill1BtnTy = 0.375f;
			YoloEngine.SoundInd[39]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_build, 1);
			YoloEngine.SoundInd[40]= YoloEngine.sp.load(YoloEngine.context, R.raw.hit_wall, 1);
			break;	
		case 13:
			Skill1BtnTx = 0.625f;
			Skill1BtnTy = 0.5f;
			YoloEngine.SoundInd[53]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_ice_trap, 1);
			YoloEngine.SoundInd[55]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_inv, 1);
			break;	
		case 14:
			Skill1BtnTx = 0.75f;
			Skill1BtnTy = 0.5f;
			YoloEngine.SoundInd[54]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warmth, 1);
			YoloEngine.SoundInd[55]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_inv, 1);
			break;	
		case 15:
			Skill1BtnTx = 0f;
			Skill1BtnTy = 0.375f;
			YoloEngine.SoundInd[41]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_spike_trap, 1);
			break;
		case 17:
			Skill1BtnTx = 0.75f;
			Skill1BtnTy = 0.375f;
			YoloEngine.SoundInd[56]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_heal, 1);
			break;
		case 18:
			Skill1BtnTx = 0.375f;
			Skill1BtnTy = 0.5f;
			YoloEngine.SoundInd[53]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_ice_trap, 1);
			break;
		case 19:
			Skill1BtnTx = 0.875f;
			Skill1BtnTy = 0.375f;
			YoloEngine.SoundInd[57]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_icicle, 1);
			break;
		case 20:
			Skill1BtnTx = 0.5f;
			Skill1BtnTy = 0.5f;
			YoloEngine.SoundInd[58]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_smoke, 1);
			break;
		case 23:
			Skill1BtnTx = 0.875f;
			Skill1BtnTy = 0.5f;
			YoloEngine.SoundInd[59]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff1, 1);
			break;
		case 24:
			Skill1BtnTx = 0.5f;
			Skill1BtnTy = 0.375f;
			YoloEngine.SoundInd[59]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff1, 1);
			break;
		case 25:
			Skill1BtnTx = 0f;
			Skill1BtnTy = 0.5f;
			YoloEngine.SoundInd[55]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_inv, 1);
			break;
		case 26:
			Skill1BtnTx = 0.125f;
			Skill1BtnTy = 0.5f;
			YoloEngine.SoundInd[60]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_lightning_h, 1);
			break;
		case 28:
			Skill1BtnTx = 0.625f;
			Skill1BtnTy = 0.375f;
			YoloEngine.SoundInd[61]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_denail, 1);
			break;
		case 29:
			Skill1BtnTx = 0.25f;
			Skill1BtnTy = 0.5f;
			YoloEngine.SoundInd[54]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warmth, 1);
			break;
		case 30:
			Skill1BtnTx = 0.5f;
			Skill1BtnTy = 0.625f;
			YoloEngine.SoundInd[45]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_fireball, 1);
			break;
		case 31:
			Skill1BtnTx = 0.75f;
			Skill1BtnTy = 0.75f;
			YoloEngine.SoundInd[46]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_teleport, 1);
			break;
		case 33:
			Skill1BtnTx = 0.0f;
			Skill1BtnTy = 0.75f;
			YoloEngine.SoundInd[47]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_lava, 1);
			break;
		case 36:
			Skill1BtnTx = 0.25f;
			Skill1BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 37:
			Skill1BtnTx = 0.75f;
			Skill1BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 38:
			Skill1BtnTx = 0.125f;
			Skill1BtnTy = 0.75f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 43:
			Skill1BtnTx = 0.5f;
			Skill1BtnTy = 0.75f;
			YoloEngine.SoundInd[51]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_earthquake, 1);
			break;
		case 103:
			Skill1BtnTx = 0.875f;
			Skill1BtnTy = 0.25f;
			YoloEngine.SoundInd[42]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_slowdown, 1);
			break;
		case 104:
			Skill1BtnTx = 0.75f;
			Skill1BtnTy = 0.375f;
			YoloEngine.SoundInd[62]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_heal_more, 1);
			break;
		case 108:
			Skill1BtnTx = 0.375f;
			Skill1BtnTy = 0.25f;
			YoloEngine.SoundInd[43]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_drain, 1);
			break;
		case 109:
			Skill1BtnTx = 0.75f;
			Skill1BtnTy = 0.25f;
			YoloEngine.SoundInd[44]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_resurect, 1);
			break;
		case 119:
			Skill1BtnTx = 0.125f;
			Skill1BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 120:
			Skill1BtnTx = 0.375f;
			Skill1BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 121:
			Skill1BtnTx = 0.875f;
			Skill1BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 122:
			Skill1BtnTx = 0.25f;
			Skill1BtnTy = 0.75f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 123:
			Skill1BtnTx = 0.625f;
			Skill1BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 124:
			Skill1BtnTx = 0.375f;
			Skill1BtnTy = 0.75f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 126:
			Skill1BtnTx = 0.875f;
			Skill1BtnTy = 0.75f;
			YoloEngine.SoundInd[49]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_coins, 1);
			break;
		case 127:
			Skill1BtnTx = 0.625f;
			Skill1BtnTy = 0.75f;
			YoloEngine.SoundInd[50]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_stamina, 1);
			break;
		}
		switch(YoloEngine.SkillSprite2)
		{
		case 4:
			Skill2BtnTx = 0.625f;
			Skill2BtnTy = 0.25f;
			YoloEngine.SoundInd[16]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_poiosn, 1);
			break;
		case 5:
			Skill2BtnTx = 0f;
			Skill2BtnTy = 0.625f;
			YoloEngine.SoundInd[52]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_thunder, 1);
			break;	
		case 6:
			Skill2BtnTx = 0f;
			Skill2BtnTy = 0.25f;
			YoloEngine.SoundInd[17]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_archer_step, 1);
			YoloEngine.SoundInd[18]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_archer_shot, 1);
			YoloEngine.SoundInd[19]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_archer_hit, 1);
			YoloEngine.SoundInd[20]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_archer_death, 1);
			break;	
		case 7:
			Skill2BtnTx = 0.375f;
			Skill2BtnTy = 0.375f;
			YoloEngine.SoundInd[21]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_step1, 1);
			YoloEngine.SoundInd[22]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_step2, 1);
			YoloEngine.SoundInd[23]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_atack, 1);
			YoloEngine.SoundInd[24]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_hit, 1);
			YoloEngine.SoundInd[25]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_death, 1);
			break;	
		case 8:
			Skill2BtnTx = 0.5f;
			Skill2BtnTy = 0.25f;
			YoloEngine.SoundInd[26]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_mummy_step1, 1);
			YoloEngine.SoundInd[27]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_mummy_step2, 1);
			YoloEngine.SoundInd[28]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_mummy_atack, 1);
			YoloEngine.SoundInd[29]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_hit, 1);
			YoloEngine.SoundInd[30]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_mummy_death, 1);
			break;	
		case 9:
			Skill2BtnTx = 0.25f;
			Skill2BtnTy = 0.25f;
			YoloEngine.SoundInd[31]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_hand, 1);
			YoloEngine.SoundInd[32]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_hand_hit, 1);
			YoloEngine.SoundInd[33]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_hand_death, 1);
			break;	
		case 10:
			Skill2BtnTx = 0.125f;
			Skill2BtnTy = 0.25f;
			YoloEngine.SoundInd[34]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_barrel_roll, 1);
			YoloEngine.SoundInd[35]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_barrel_explosion, 1);
			break;	
		case 11:
			Skill2BtnTx = 0.125f;
			Skill2BtnTy = 0.375f;
			YoloEngine.SoundInd[36]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_build, 1);
			YoloEngine.SoundInd[37]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_tower, 1);
			YoloEngine.SoundInd[38]= YoloEngine.sp.load(YoloEngine.context, R.raw.hit_wall, 1);
			break;	
		case 12:
			Skill2BtnTx = 0.25f;
			Skill2BtnTy = 0.375f;
			YoloEngine.SoundInd[39]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_build, 1);
			YoloEngine.SoundInd[40]= YoloEngine.sp.load(YoloEngine.context, R.raw.hit_wall, 1);
			break;	
		case 13:
			Skill2BtnTx = 0.625f;
			Skill2BtnTy = 0.5f;
			YoloEngine.SoundInd[53]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_ice_trap, 1);
			YoloEngine.SoundInd[55]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_inv, 1);
			break;	
		case 14:
			Skill2BtnTx = 0.75f;
			Skill2BtnTy = 0.5f;
			YoloEngine.SoundInd[54]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warmth, 1);
			YoloEngine.SoundInd[55]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_inv, 1);
			break;	
		case 15:
			Skill2BtnTx = 0f;
			Skill2BtnTy = 0.375f;
			YoloEngine.SoundInd[41]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_spike_trap, 1);
			break;
		case 17:
			Skill2BtnTx = 0.75f;
			Skill2BtnTy = 0.375f;
			YoloEngine.SoundInd[56]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_heal, 1);
			break;
		case 18:
			Skill2BtnTx = 0.375f;
			Skill2BtnTy = 0.5f;
			YoloEngine.SoundInd[53]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_ice_trap, 1);
			break;
		case 19:
			Skill2BtnTx = 0.875f;
			Skill2BtnTy = 0.375f;
			YoloEngine.SoundInd[57]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_icicle, 1);
			break;
		case 20:
			Skill2BtnTx = 0.5f;
			Skill2BtnTy = 0.5f;
			YoloEngine.SoundInd[58]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_smoke, 1);
			break;
		case 23:
			Skill2BtnTx = 0.875f;
			Skill2BtnTy = 0.5f;
			YoloEngine.SoundInd[59]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff1, 1);
			break;
		case 24:
			Skill2BtnTx = 0.5f;
			Skill2BtnTy = 0.375f;
			YoloEngine.SoundInd[59]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff1, 1);
			break;
		case 25:
			Skill2BtnTx = 0f;
			Skill2BtnTy = 0.5f;
			YoloEngine.SoundInd[55]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_inv, 1);
			break;
		case 26:
			Skill2BtnTx = 0.125f;
			Skill2BtnTy = 0.5f;
			YoloEngine.SoundInd[60]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_lightning_h, 1);
			break;
		case 28:
			Skill2BtnTx = 0.625f;
			Skill2BtnTy = 0.375f;
			YoloEngine.SoundInd[61]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_denail, 1);
			break;
		case 29:
			Skill2BtnTx = 0.25f;
			Skill2BtnTy = 0.5f;
			YoloEngine.SoundInd[54]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warmth, 1);
			break;
		case 30:
			Skill2BtnTx = 0.5f;
			Skill2BtnTy = 0.625f;
			YoloEngine.SoundInd[45]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_fireball, 1);
			break;
		case 31:
			Skill2BtnTx = 0.75f;
			Skill2BtnTy = 0.75f;
			YoloEngine.SoundInd[46]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_teleport, 1);
			break;
		case 33:
			Skill2BtnTx = 0.0f;
			Skill2BtnTy = 0.75f;
			YoloEngine.SoundInd[47]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_lava, 1);
			break;
		case 36:
			Skill2BtnTx = 0.25f;
			Skill2BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 37:
			Skill2BtnTx = 0.75f;
			Skill2BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 38:
			Skill2BtnTx = 0.125f;
			Skill2BtnTy = 0.75f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 43:
			Skill2BtnTx = 0.5f;
			Skill2BtnTy = 0.75f;
			YoloEngine.SoundInd[51]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_earthquake, 1);
			break;
		case 103:
			Skill2BtnTx = 0.875f;
			Skill2BtnTy = 0.25f;
			YoloEngine.SoundInd[42]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_slowdown, 1);
			break;
		case 104:
			Skill2BtnTx = 0.75f;
			Skill2BtnTy = 0.375f;
			YoloEngine.SoundInd[62]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_heal_more, 1);
			break;
		case 108:
			Skill2BtnTx = 0.375f;
			Skill2BtnTy = 0.25f;
			YoloEngine.SoundInd[42]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_drain, 1);
			break;
		case 109:
			Skill2BtnTx = 0.75f;
			Skill2BtnTy = 0.25f;
			YoloEngine.SoundInd[42]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_resurect, 1);
			break;
		case 119:
			Skill2BtnTx = 0.125f;
			Skill2BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 120:
			Skill2BtnTx = 0.375f;
			Skill2BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 121:
			Skill2BtnTx = 0.875f;
			Skill2BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 122:
			Skill2BtnTx = 0.25f;
			Skill2BtnTy = 0.75f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 123:
			Skill2BtnTx = 0.625f;
			Skill2BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 124:
			Skill2BtnTx = 0.375f;
			Skill2BtnTy = 0.75f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 126:
			Skill2BtnTx = 0.875f;
			Skill2BtnTy = 0.75f;
			YoloEngine.SoundInd[49]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_coins, 1);
			break;
		case 127:
			Skill2BtnTx = 0.625f;
			Skill2BtnTy = 0.75f;
			YoloEngine.SoundInd[50]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_stamina, 1);
			break;
		}
		switch(YoloEngine.SkillSprite3)
		{
		case 4:
			Skill3BtnTx = 0.625f;
			Skill3BtnTy = 0.25f;
			YoloEngine.SoundInd[16]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_poiosn, 1);
			break;
		case 5:
			Skill3BtnTx = 0f;
			Skill3BtnTy = 0.625f;
			YoloEngine.SoundInd[52]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_thunder, 1);
			break;	
		case 6:
			Skill3BtnTx = 0f;
			Skill3BtnTy = 0.25f;
			YoloEngine.SoundInd[17]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_archer_step, 1);
			YoloEngine.SoundInd[18]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_archer_shot, 1);
			YoloEngine.SoundInd[19]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_archer_hit, 1);
			YoloEngine.SoundInd[20]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_archer_death, 1);
			break;	
		case 7:
			Skill3BtnTx = 0.375f;
			Skill3BtnTy = 0.375f;
			YoloEngine.SoundInd[21]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_step1, 1);
			YoloEngine.SoundInd[22]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_step2, 1);
			YoloEngine.SoundInd[23]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_atack, 1);
			YoloEngine.SoundInd[24]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_hit, 1);
			YoloEngine.SoundInd[25]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_death, 1);
			break;	
		case 8:
			Skill3BtnTx = 0.5f;
			Skill3BtnTy = 0.25f;
			YoloEngine.SoundInd[26]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_mummy_step1, 1);
			YoloEngine.SoundInd[27]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_mummy_step2, 1);
			YoloEngine.SoundInd[28]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_mummy_atack, 1);
			YoloEngine.SoundInd[29]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warrior_hit, 1);
			YoloEngine.SoundInd[30]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_mummy_death, 1);
			break;	
		case 9:
			Skill3BtnTx = 0.25f;
			Skill3BtnTy = 0.25f;
			YoloEngine.SoundInd[31]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_hand, 1);
			YoloEngine.SoundInd[32]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_hand_hit, 1);
			YoloEngine.SoundInd[33]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_hand_death, 1);
			break;	
		case 10:
			Skill3BtnTx = 0.125f;
			Skill3BtnTy = 0.25f;
			YoloEngine.SoundInd[34]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_barrel_roll, 1);
			YoloEngine.SoundInd[35]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_barrel_explosion, 1);
			break;	
		case 11:
			Skill3BtnTx = 0.125f;
			Skill3BtnTy = 0.375f;
			YoloEngine.SoundInd[36]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_build, 1);
			YoloEngine.SoundInd[37]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_tower, 1);
			YoloEngine.SoundInd[38]= YoloEngine.sp.load(YoloEngine.context, R.raw.hit_wall, 1);
			break;	
		case 12:
			Skill3BtnTx = 0.25f;
			Skill3BtnTy = 0.375f;
			YoloEngine.SoundInd[39]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_build, 1);
			YoloEngine.SoundInd[40]= YoloEngine.sp.load(YoloEngine.context, R.raw.hit_wall, 1);
			break;	
		case 13:
			Skill3BtnTx = 0.625f;
			Skill3BtnTy = 0.5f;
			YoloEngine.SoundInd[53]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_ice_trap, 1);
			YoloEngine.SoundInd[55]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_inv, 1);
			break;	
		case 14:
			Skill3BtnTx = 0.75f;
			Skill3BtnTy = 0.5f;
			YoloEngine.SoundInd[54]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warmth, 1);
			YoloEngine.SoundInd[55]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_inv, 1);
			break;	
		case 15:
			Skill3BtnTx = 0f;
			Skill3BtnTy = 0.375f;
			YoloEngine.SoundInd[41]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_spike_trap, 1);
			break;
		case 17:
			Skill3BtnTx = 0.75f;
			Skill3BtnTy = 0.375f;
			YoloEngine.SoundInd[56]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_heal, 1);
			break;
		case 18:
			Skill3BtnTx = 0.375f;
			Skill3BtnTy = 0.5f;
			YoloEngine.SoundInd[53]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_ice_trap, 1);
			break;
		case 19:
			Skill3BtnTx = 0.875f;
			Skill3BtnTy = 0.375f;
			YoloEngine.SoundInd[57]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_icicle, 1);
			break;
		case 20:
			Skill3BtnTx = 0.5f;
			Skill3BtnTy = 0.5f;
			YoloEngine.SoundInd[58]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_smoke, 1);
			break;
		case 23:
			Skill3BtnTx = 0.875f;
			Skill3BtnTy = 0.5f;
			YoloEngine.SoundInd[59]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff1, 1);
			break;
		case 24:
			Skill3BtnTx = 0.5f;
			Skill3BtnTy = 0.375f;
			YoloEngine.SoundInd[59]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff1, 1);
			break;
		case 25:
			Skill3BtnTx = 0f;
			Skill3BtnTy = 0.5f;
			YoloEngine.SoundInd[55]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_inv, 1);
			break;
		case 26:
			Skill3BtnTx = 0.125f;
			Skill3BtnTy = 0.5f;
			YoloEngine.SoundInd[60]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_lightning_h, 1);
			break;
		case 28:
			Skill3BtnTx = 0.625f;
			Skill3BtnTy = 0.375f;
			YoloEngine.SoundInd[61]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_denail, 1);
			break;
		case 29:
			Skill3BtnTx = 0.25f;
			Skill3BtnTy = 0.5f;
			YoloEngine.SoundInd[54]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_warmth, 1);
			break;
		case 30:
			Skill3BtnTx = 0.5f;
			Skill3BtnTy = 0.625f;
			YoloEngine.SoundInd[45]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_fireball, 1);
			break;
		case 31:
			Skill3BtnTx = 0.75f;
			Skill3BtnTy = 0.75f;
			YoloEngine.SoundInd[46]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_teleport, 1);
			break;
		case 33:
			Skill3BtnTx = 0.0f;
			Skill3BtnTy = 0.75f;
			YoloEngine.SoundInd[47]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_lava, 1);
			break;
		case 36:
			Skill3BtnTx = 0.25f;
			Skill3BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 37:
			Skill3BtnTx = 0.75f;
			Skill3BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 38:
			Skill3BtnTx = 0.125f;
			Skill3BtnTy = 0.75f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 43:
			Skill3BtnTx = 0.5f;
			Skill3BtnTy = 0.75f;
			YoloEngine.SoundInd[51]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_earthquake, 1);
			break;
		case 103:
			Skill3BtnTx = 0.875f;
			Skill3BtnTy = 0.25f;
			YoloEngine.SoundInd[42]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_slowdown, 1);
			break;
		case 104:
			Skill3BtnTx = 0.75f;
			Skill3BtnTy = 0.375f;
			YoloEngine.SoundInd[62]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_heal_more, 1);
			break;
		case 108:
			Skill3BtnTx = 0.375f;
			Skill3BtnTy = 0.25f;
			YoloEngine.SoundInd[42]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_drain, 1);
			break;
		case 109:
			Skill3BtnTx = 0.75f;
			Skill3BtnTy = 0.25f;
			YoloEngine.SoundInd[42]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_resurect, 1);
			break;
		case 119:
			Skill3BtnTx = 0.125f;
			Skill3BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 120:
			Skill3BtnTx = 0.375f;
			Skill3BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 121:
			Skill3BtnTx = 0.875f;
			Skill3BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 122:
			Skill3BtnTx = 0.25f;
			Skill3BtnTy = 0.75f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 123:
			Skill3BtnTx = 0.625f;
			Skill3BtnTy = 0.625f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 124:
			Skill3BtnTx = 0.375f;
			Skill3BtnTy = 0.75f;
			YoloEngine.SoundInd[48]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_buff, 1);
			break;
		case 126:
			Skill3BtnTx = 0.875f;
			Skill3BtnTy = 0.75f;
			YoloEngine.SoundInd[49]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_coins, 1);
			break;
		case 127:
			Skill3BtnTx = 0.625f;
			Skill3BtnTy = 0.75f;
			YoloEngine.SoundInd[50]= YoloEngine.sp.load(YoloEngine.context, R.raw.skill_stamina, 1);
			break;
		}
//-----------------------------------------------------------------------------------------------------------		
	}
}
