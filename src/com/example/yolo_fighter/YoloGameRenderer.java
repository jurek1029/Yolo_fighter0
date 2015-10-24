package com.example.yolo_fighter;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView.Renderer;

class Particle
{
	float x,y,velocity,x_distance,a,b,x_texture = 0,y_texture = 0,x_end,y_end,pom,y_pos;
	int liveTime,velecityDirection,couter =0;
	
	public Particle(float x_pos,float y_pos,boolean isSmoke,Random rng,float a0,float a1)
	{
		//Random rng = new Random();
		if(isSmoke)
		{
			this.y_pos = y_pos;
			x_end=0.40f;
			y_end=0.75f;
			a=4f;b=2f;
			x = rng.nextFloat()*2*a - a;
			pom = (float) Math.sqrt(-((x*x - a*a)*b*b)/(a*a));
			y = rng.nextFloat()*2*pom - pom;
			
			velecityDirection =rng.nextBoolean()? -1:1;
			velocity = .015625f + rng.nextFloat() * 0.0078125f;
				
			x_distance = (float) (Math.sqrt(-((y*y - b*b)*a*a)/(b*b))+ velecityDirection > 0? Math.abs(x):-Math.abs(x));
			liveTime = (int) (x_distance/velocity);		
			
			couter=(int)(Math.asin(y/b)/3.1415f*90f);
			
			x+= x_pos;
			y+= y_pos;
			
		}
		else
		{
			x_end=0.375f;
			y_end=0.125f;
			a=2;b=0.3f;
			/*
			float a0,a1;
			
			if(On<0)
			{
				float maxy =0;
				for(int q=0;q<YoloGameRenderer.ObjectTab.length;q++)
				{
					if(x_pos>YoloGameRenderer.ObjectTab[q].x && x_pos<YoloGameRenderer.ObjectTab[q].x + YoloGameRenderer.ObjectTab[q].dx)
					{
						if(y_pos+.5f>=YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy)
							if(YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy>maxy)
							{
								maxy = YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy;
								On =q;
							}
					}
				}
			}
			if(On<0)
				a0 = a1 = a;
			else
			{
				if(x_pos - YoloGameRenderer.ObjectTab[On].x  > a) a0=a;
				else if(x_pos - YoloGameRenderer.ObjectTab[On].x  < 0) a0 = 0;
				else a0 = x_pos - YoloGameRenderer.ObjectTab[On].x ;
				if(YoloGameRenderer.ObjectTab[On].x - x_pos + YoloGameRenderer.ObjectTab[On].dx  > a) a1=a;
				else if(YoloGameRenderer.ObjectTab[On].x  - x_pos + YoloGameRenderer.ObjectTab[On].dx  < 0) a1 = 0;
				else a1 = YoloGameRenderer.ObjectTab[On].x  - x_pos + YoloGameRenderer.ObjectTab[On].dx;
			}		
			*/
			x = rng.nextFloat()*(a0+a1) - a0;
			velecityDirection =rng.nextBoolean()? -1:1;
			velocity = .015625f + rng.nextFloat() * 0.0078125f;
			if(velecityDirection < 0)
			{
				if(x + a0 > b)y =rng.nextFloat()*b;
				else y = rng.nextFloat()*(x + a0);
				x_distance = -y + a0 + x;
			}
			else 
			{
				if(-x + a1 > b)y = rng.nextFloat()*b;
				else y = rng.nextFloat()*(-x + a1);
				x_distance =  Math.abs(-y + a1) - x;
				
			}	
			
			liveTime = (int) (x_distance/velocity);		
			
			//System.out.println(x+" "+y+" "+x_distance+" "+liveTime+" "+velocity+" "+velecityDirection+" "+a0+" "+a1);
			
			x += x_pos - .5f;
			y += y_pos - .5f;
			
			
		}
	}
	
	public boolean drawSmoke(GL10 gl)
	{
		if(liveTime-- <= 0)
			return true;
		couter++;
		x += velecityDirection*velocity;
		y = (float) (pom*Math.sin(couter *(6.284f/360.0f)))+y_pos;
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(YoloEngine.TEXTURE_SIZE_X*2f, YoloEngine.TEXTURE_SIZE_Y*2f, 1f);
		gl.glTranslatef(x/2f, y/2f-.5f, 0f);
		gl.glColor4f(1f,1f,1f,1f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glTranslatef(x_texture, y_texture, 0f);
		YoloGameRenderer.smo.draw(gl, YoloEngine.spriteSheets,20);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		if(x_texture < 0.86f)x_texture += 0.142857f;
		else {x_texture = 0;y_texture += .142857f;}
		
		if(x_texture > x_end && y_texture > y_end)
		{
			x_texture = 0;
			y_texture = 0;
		}
		return false;
	}
	
	public boolean drawLava(GL10 gl)
	{
		if(liveTime-- <= 0)
			return true;
		x += velecityDirection*velocity;
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
		gl.glTranslatef(x, y, 0f);
		gl.glColor4f(1f,1f,1f,1f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glTranslatef(x_texture, y_texture, 0f);
		YoloGameRenderer.btn.draw(gl, YoloEngine.spriteSheets,33);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		if(x_texture < 0.875f)x_texture += .125f;
		else {x_texture = 0;y_texture += .125f;}
		
		if(x_texture > x_end && y_texture > y_end)
		{
			x_texture = 0;
			y_texture = 0;
		}
		return false;
	}
}
class ParticleObject
{
	Particle particleTab[];// = new Particle[200];
	boolean isSmoke;
	float x,y,a0,a1;
	int lifeTime,On;
	Random rng;
	
	public ParticleObject(float x, float y,boolean isSmoke,int liveTime,float a0,float a1) 
	{
		this.isSmoke = isSmoke;
		this.x = x;
		this.y = y;
		this.lifeTime = liveTime;
		rng = new Random();
		this.a0 = a0;
		this.a1 = a1;
		
		if(isSmoke)
		{
			particleTab = new Particle[200];
			for(int i=0;i<particleTab.length;i++)
					particleTab[i] = new Particle(x, y, isSmoke,rng,a0,a1);
		}
		else
		{
			particleTab = new Particle[70];
			for(int i=0;i<particleTab.length;i++)
					particleTab[i] = new Particle(x, y, isSmoke,rng,a0,a1);
		}
	}
}
class PowerUP extends YoloWeapon
{
	int effect,j;
	PowerUP(float x, float y, int effect) {
		super(0f,0f,.5f,.5f);
		this.effect = effect;
		this.x = x;
		this.y = y;
		vy = 0;
		j=0;
		
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
			x_texture = 0.875f;
			y_texture = 0.375f;
			break;
			
		}
				
	}
	
	PowerUP()
	{	
		super(0f,0f,.5f,.5f);
		Random rng = new Random();
		x=(YoloEngine.LEVEL_X/YoloEngine.TX)* rng.nextFloat();
		y=(YoloEngine.LEVEL_Y/YoloEngine.TY)* rng.nextFloat();
		effect = rng.nextInt(8);
		vy = 0;
		j=0;
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
			x_texture = 0.875f;
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
	int creatorID = YoloEngine.MyID;
	
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
    		 if(this.creatorID == YoloEngine.MyID)
    			 setAIXY();
    		isLeft = YoloEngine.TeamAB[this.creatorID].isPlayerLeft;
			//this.x++;
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
    		if(ID == -1)
    		{
	     		YoloEngine.TeamAB[YoloEngine.MyID].fireSprite = sprite;
	    		YoloEngine.TeamAB[YoloEngine.MyID].fireDamage = damage;
	    		YoloEngine.TeamAB[YoloEngine.MyID].fireCount = 8;
	    		YoloEngine.TeamAB[YoloEngine.MyID].icice = YoloEngine.icicleDuration;
    		}
    		else
    		{
    			YoloEngine.TeamAB[ID].fireSprite = sprite;
	    		YoloEngine.TeamAB[ID].fireDamage = damage;
	    		YoloEngine.TeamAB[ID].fireCount = 8;
	    		YoloEngine.TeamAB[ID].icice = YoloEngine.icicleDuration;
    		}
    		break;
    	case 20://Smoke weed everyday
    		if(ID < 0)
    		{
    			setX();setY();	
    			YoloGameRenderer.particleObjectTabMy.add(new ParticleObject(this.x, this.y, true, 300,-1,-1));
    		}
    		else
    			YoloGameRenderer.particleObjectTabOp.add(new ParticleObject(x, y, true, 300,-1,-1));
    		
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
    		if(ID == -1)
    		{
	    		YoloEngine.TeamAB[YoloEngine.MyID].fireSprite = sprite;
	    		YoloEngine.TeamAB[YoloEngine.MyID].fireDamage = damage;
	    		YoloEngine.TeamAB[YoloEngine.MyID].fireCount = 4;
	    		YoloEngine.TeamAB[YoloEngine.MyID].thunder_h = YoloEngine.thunderDuration;
    		}
    		else
    		{
    			YoloEngine.TeamAB[ID].fireSprite = sprite;
	    		YoloEngine.TeamAB[ID].fireDamage = damage;
	    		YoloEngine.TeamAB[ID].fireCount = 4;
	    		YoloEngine.TeamAB[ID].thunder_h = YoloEngine.thunderDuration;
    		}
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
    		if(ID == -1)
    		{
	    		YoloEngine.TeamAB[YoloEngine.MyID].fireSprite = sprite;
	    		YoloEngine.TeamAB[YoloEngine.MyID].fireDamage = damage;
	    		YoloEngine.TeamAB[YoloEngine.MyID].fireCount = 8;
	    		YoloEngine.TeamAB[YoloEngine.MyID].thunder_h = YoloEngine.thunderDuration;
    		}
    		else
    		{
    			YoloEngine.TeamAB[ID].fireSprite = sprite;
	    		YoloEngine.TeamAB[ID].fireDamage = damage;
	    		YoloEngine.TeamAB[ID].fireCount = 8;
	    		YoloEngine.TeamAB[ID].thunder_h = YoloEngine.thunderDuration;
    		}
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
    		x_radius = 10f;
    		y_radius = 0.1f;
    		frameDuration = lava_duration = 300;
    		life = 0;MAXlife = life;
    		animation_slowdown = 0;
    		damage = 0.03f;
    		scale_x = 15f;scale_y = 1f;
    		float a=2,a0,a1;
    		int On=-1;
    		if(ID<0)
    		{
	    		setX();setY();
	    		float maxy=0;
				for(int q=0;q<YoloGameRenderer.ObjectTab.length;q++)
				{
					if(this.x>YoloGameRenderer.ObjectTab[q].x && this.x<YoloGameRenderer.ObjectTab[q].x + YoloGameRenderer.ObjectTab[q].dx)
					{
						if(this.y+.5f>=YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy)
							if(YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy>maxy)
							{
								maxy = YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy;
								On =q;
							}
						
					}
				}
				if(On<0)
					a0 = a1 = a;
				else
				{
					if(this.x - YoloGameRenderer.ObjectTab[On].x  > a) a0=a;
					else if(this.x - YoloGameRenderer.ObjectTab[On].x  < 0) a0 = 0;
					else a0 = this.x - YoloGameRenderer.ObjectTab[On].x ;
					if(YoloGameRenderer.ObjectTab[On].x - this.x + YoloGameRenderer.ObjectTab[On].dx  > a) a1=a;
					else if(YoloGameRenderer.ObjectTab[On].x  - this.x + YoloGameRenderer.ObjectTab[On].dx  < 0) a1 = 0;
					else a1 = YoloGameRenderer.ObjectTab[On].x  - this.x + YoloGameRenderer.ObjectTab[On].dx;
				}					
				this.y = maxy;
				//this.x -= 6.5f;
				YoloGameRenderer.particleObjectTabOp.add(new ParticleObject(this.x, this.y, false, 300,a0,a1));
				x_radius = a0+a1;
    		}
	    	else
	    	{
	    		
				float maxy =0;
				for(int q=0;q<YoloGameRenderer.ObjectTab.length;q++)
				{
					if(x>YoloGameRenderer.ObjectTab[q].x && x<YoloGameRenderer.ObjectTab[q].x + YoloGameRenderer.ObjectTab[q].dx)
					{
						if(y+.5f>=YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy)
							if(YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy>maxy)
							{
								maxy = YoloGameRenderer.ObjectTab[q].y + YoloGameRenderer.ObjectTab[q].dy;
								On =q;
							}
					}
				}
				
				if(On<0)
					a0 = a1 = a;
				else
				{
					if(x - YoloGameRenderer.ObjectTab[On].x  > a) a0=a;
					else if(x - YoloGameRenderer.ObjectTab[On].x  < 0) a0 = 0;
					else a0 = x - YoloGameRenderer.ObjectTab[On].x ;
					if(YoloGameRenderer.ObjectTab[On].x - x + YoloGameRenderer.ObjectTab[On].dx  > a) a1=a;
					else if(YoloGameRenderer.ObjectTab[On].x  - x + YoloGameRenderer.ObjectTab[On].dx  < 0) a1 = 0;
					else a1 = YoloGameRenderer.ObjectTab[On].x  - x + YoloGameRenderer.ObjectTab[On].dx;
				}		
				YoloGameRenderer.particleObjectTabOp.add(new ParticleObject(x, y, false, 300,a0,a1));
				
				x_radius = a0+a1;
	    	}
    		lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
    		ly = Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
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
    			if(ID == -1)
    			{
    				setX();setY();
    			}
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
					Skill skill = new Skill(YoloEngine.TeamAB[j].x,YoloEngine.TeamAB[j].y+0.25f,32,team,ID);
					skill.x = YoloEngine.TeamAB[j].x;
					skill.y = YoloEngine.TeamAB[j].y+0.25f;
					YoloGameRenderer.hitBoxs.add(new HitBox(skill.x - x_radius/2 +.5f,skill.y - y_radius/2 +.5f, x_radius, y_radius, damage,1,32,isLeft,team,true,id));
					if(team == YoloEngine.TeamA)
						YoloGameRenderer.skillTeamAVe.add(skill);
					else
						YoloGameRenderer.skillTeamBVe.add(skill);
				}
				if(ID==-1)
				{
				lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
	    		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
				}
				else
				{
					lx = Math.abs(this.x-YoloEngine.TeamAB[ID].x);
		    		ly =Math.abs(this.y-YoloEngine.TeamAB[ID].y);
				}
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
    		if(ID==-1)
    		{
    			setX();setY();
    			lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
        		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
    		}
    		else
    		{
    			lx = Math.abs(this.x-YoloEngine.TeamAB[ID].x);
        		ly =Math.abs(this.y-YoloEngine.TeamAB[ID].y);
    		}
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
    		{
    			setX();setY();
    			lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
        		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
    		}
    		else
    		{
    			lx = Math.abs(this.x-YoloEngine.TeamAB[ID].x);
        		ly =Math.abs(this.y-YoloEngine.TeamAB[ID].y);
    		}
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
    		{
    			setX();setY();
    			lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
        		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
    		}
    		else
    		{
    			lx = Math.abs(this.x-YoloEngine.TeamAB[ID].x);
        		ly =Math.abs(this.y-YoloEngine.TeamAB[ID].y);
    		}
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
    		{
    			setX();setY();
    			lx = Math.abs(this.x-YoloEngine.TeamAB[YoloEngine.MyID].x);
        		ly =Math.abs(this.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
    		}
    		else
    		{
    			lx = Math.abs(this.x-YoloEngine.TeamAB[ID].x);
        		ly =Math.abs(this.y-YoloEngine.TeamAB[ID].y);
    		}
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
			x = YoloEngine.TeamAB[YoloEngine.MyID].x - 1f ;
		else
			x = YoloEngine.TeamAB[YoloEngine.MyID].x + 1f;
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
    
    public byte[] serializeSkillNew() {
    	ByteBuffer bbf = ByteBuffer.allocate(25);
        bbf.putChar('s');  // 2 bajty
        bbf.putFloat(x); // 4 bajty
        bbf.putFloat(y); // 4 bajty
        bbf.putInt(sprite); // 4 bajty
        if(team) 			// 1 bajt
            bbf.put((byte)1);
        else
            bbf.put((byte)0);
        bbf.putInt(id); // 4 bajty
        bbf.putInt(creatorID); //4 bajty
        return bbf.array();
    }
}

public class YoloGameRenderer implements Renderer {
	
	private YoloTexture TextureLoader ;
	private YoloBackground back= new YoloBackground(),load_back=new YoloBackground(),load_front = new YoloBackground(),mag = new YoloBackground(YoloEngine.TeamAB[YoloEngine.MyID].PlayerMagCapasity/30f),
			death = new YoloBackground(),win = new YoloBackground(),lose = new YoloBackground(),gray = new YoloBackground(),liveBarOut = new YoloBackground(),liveBarIn = new YoloBackground(),creditBarInG = new YoloBackground();
	public static YoloWeapon btn = new YoloWeapon(0,0),smo = new YoloWeapon(0,0,true);
	private static YoloWeapon bullet,weapon;
	private Triangle roti0,roti1,roti2;
	
	public static YoloObject[] ObjectTab = new YoloObject[17];
	public static YoloObject[] LaddreTab = new YoloObject[4];
	
	public static Vector<YoloWeapon> Weapontab  = new Vector<YoloWeapon>();
	public static Vector<PowerUP> PowerUPtab = new Vector<PowerUP>();
	//public static Skill[] skilltab = new Skill[3];
	public static Vector<Skill> skillTeamBVe = new Vector<Skill>();
	public static Vector<Skill> skillTeamAVe = new Vector<Skill>();
	public static Vector<HitBox> hitBoxs = new Vector<HitBox>();
	public static Vector<ParticleObject> particleObjectTabMy = new Vector<ParticleObject>();
	public static Vector<ParticleObject> particleObjectTabOp = new Vector<ParticleObject>();
	
	private final float MOVE_SIZE_X = 2*YoloEngine.MAX_VALUE_PLAYER_SPEED/YoloEngine.display_x/YoloEngine.xdpi; // 200/display_x
	private final float MOVE_SIZE_Y = YoloEngine.MOVE_SIZE_Y/YoloEngine.display_y/YoloEngine.xdpi; // 50/display_y
	private final float MOVE_SIZE_X1 = 160f/YoloEngine.display_x/YoloEngine.xdpi;
	private final float MOVE_SIZE_Y1 = 160f/YoloEngine.display_y/YoloEngine.xdpi;
	private final float MOVE_BALL_SIZE_X = YoloEngine.MOVE_SIZE_Y/YoloEngine.display_x/YoloEngine.xdpi; // 50/display_x
	private final float MOVE_POS_X = 25f/YoloEngine.display_x/YoloEngine.xdpi;//(YoloEngine.MOVE_X/YoloEngine.display_x - MOVE_SIZE_X/2);// /MOVE_SIZE_X;  (125-100)/display_x
	private final float MOVE_POS_Y = 50f/YoloEngine.display_y/YoloEngine.xdpi; //(YoloEngine.display_y - YoloEngine.MOVE_Y)/YoloEngine.display_y + MOVE_SIZE_Y/2; // 25/display_y == move_y/2/display_y
	private float Skill1BtnTx,Skill1BtnTy,Skill2BtnTx,Skill2BtnTy,Skill3BtnTx,Skill3BtnTy;
	private final float LIVE_BAR_SIZE_X_0 = YoloEngine.LIVE_BAR_SIZE/YoloEngine.display_x/YoloEngine.xdpi;
	private float LIVE_BAR_SIZE_X_1 = LIVE_BAR_SIZE_X_0;
	private final float LIVE_BAR_SIZE_Y = 30f/YoloEngine.display_y/YoloEngine.xdpi;
    public static float half_fx,half_bx,half_fy,half_by;
    private float creditBarSizeX0 = 50f/YoloEngine.display_x/YoloEngine.xdpi,creditBarSizeY0 = 25f/YoloEngine.display_y/YoloEngine.xdpi,creditBarSizeX1 = creditBarSizeX0;
	
	private float cameraPosX,joyBallX =(YoloGame.x2-25f)/YoloEngine.display_x/YoloEngine.xdpi //(YoloGame.x2/YoloEngine.display_x - MOVE_BALL_SIZE_X/2)// /MOVE_BALL_SIZE_X, (x2-25)dis_x
			,jumpBtnX = 1-125f/YoloEngine.display_x/YoloEngine.xdpi // 1/(MOVE_BALL_SIZE_X*2)-1.5f
			,crouchBtnX = 250f/YoloEngine.display_x/YoloEngine.xdpi //2.75f
			,skillBtnX = .5f - 50f/YoloEngine.display_x/YoloEngine.xdpi //  1/(MOVE_BALL_SIZE_X*2)/2
			,liveBarX_0 = 25f/YoloEngine.display_x/YoloEngine.xdpi //(0.5f/(1f/LIVE_BAR_SIZE_Y))*(1/LIVE_BAR_SIZE_X_0);	
			,joyBallX1
			,joyBackX1
			,joyBallX3
			,joyBackX3
			,XADD = 0
			,creditBarPosX = 1 - 75/YoloEngine.display_x/YoloEngine.xdpi
			,creditBarPosX1 = creditBarPosX + 2.8f/YoloEngine.display_x/YoloEngine.xdpi ; 

	private float cameraPosY
			,jumpBtnY = 150f/YoloEngine.display_y/YoloEngine.xdpi
			,crouchBtnY = 25f/YoloEngine.display_y/YoloEngine.xdpi
			,liveBarY = 1-55f/YoloEngine.display_y/YoloEngine.xdpi // 1f/LIVE_BAR_SIZE_Y -1.75f;// 1-(25+30)/dis_x
			,joyBallY1
			,joyBackY1
			,joyBallY3
			,joyBackY3
			,YADD = 0; 
	
	public static boolean toLoad = true,first = false;
	private int loading_faze=0,loadingStepsCout = 41;
	
	public static int platformOn=0;
	private int nextBullet = 1;
	public static boolean onGround = true,contact = true;
	private int ClimbingOn;
	private int S1cooldown = 0,S2cooldown = 0,S3cooldown = 0,s1=0,s2=0,s3=0;
	private int powerUpCoutdown =0,powerUpInterval = 1000,doubleTapInterval = 9,dashInterval = 14;
	private int deathIntervalCounter = 0;
	private float fadeIn = 0f;
				
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
			
			//---------------------------------------------------DoubleTap--------------------------------------------------------------
			if(YoloGame.doubleTap != 0 && doubleTapInterval-- <= 0)
			{
				YoloGame.doubleTap = 0;
				doubleTapInterval = 40;
			}
			if(YoloGame.currentMovePointer2 == -1 && dashInterval-- <= 0)
			{
				YoloGame.lastMovePointer2 =-1;
				dashInterval = 20;
			}
			
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
					YoloEngine.isDoubleTaped = false;
					
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
					YoloEngine.isDoubleTaped = false;
					
				}
			}
	
// ------------------------- Multislayer BEGIN -----------------------	

			if (YoloEngine.mGameProperties.gameType != GameProperties.OFFLINE) {
                YoloEngine.mMultislayer.sendPlayerPosition(YoloEngine.TeamAB[YoloEngine.MyID].x, YoloEngine.TeamAB[YoloEngine.MyID].y, YoloEngine.TeamAB[YoloEngine.MyID].isCrouch, YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft);
			}
			continue_point:
			for(int i = 0; i < YoloEngine.TeamSize*2; i++) {
				for(int z : YoloEngine.AIToManage)
					if(z == i)continue continue_point;
				if(i == YoloEngine.MyID ) continue; //skippujemy jesli dotyczy 'naszego' gracza
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
			manageYourAI();
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
			handleParticles(gl,true);
			
			if(YoloEngine.TeamAB[YoloEngine.MyID].playerTeam == YoloEngine.TeamA) 
			{
				for(int i =0;i<YoloEngine.TeamSize;i++)
					YoloEngine.TeamAB[i].drawAlly(gl,YoloEngine.MyID==i?false:true);
			}
			else
			{
				for(int i = YoloEngine.TeamSize; i< YoloEngine.TeamSize*2;i++)
					YoloEngine.TeamAB[i].drawAlly(gl,YoloEngine.MyID==i?false:true);
			}
			hitBox();
			drawWeapon(gl);
			
			if(YoloEngine.TeamAB[YoloEngine.MyID].playerTeam == YoloEngine.TeamA)
			{
				for(int i = YoloEngine.TeamSize; i< YoloEngine.TeamSize*2;i++)
					YoloEngine.TeamAB[i].drawOpponent(gl);
			}
			else
			{
				for(int i =0;i<YoloEngine.TeamSize;i++)
					YoloEngine.TeamAB[i].drawOpponent(gl);
			}
			
			handleParticles(gl,false);
			drawOponentSkills(gl);
			if(nextBullet-- <= 0 && YoloEngine.TeamAB[YoloEngine.MyID].isShoting && YoloEngine.TeamAB[YoloEngine.MyID].playerMag>0)
			{
				YoloEngine.TeamAB[YoloEngine.MyID].playerMag--;
				nextBullet = YoloEngine.TeamAB[YoloEngine.MyID].firePause;
				playerFire(0.12f,YoloEngine.TeamAB[YoloEngine.MyID].fireSprite,YoloEngine.TeamAB[YoloEngine.MyID].fireCount,YoloEngine.TeamAB[YoloEngine.MyID].fireDamage,YoloEngine.TeamAB[YoloEngine.MyID].poiseDamage);					
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
			
			spanMovePowerUPs();
			checkForPowerUPs();
			drawPowerUPs(gl);
			drawControls(gl);
			drawPlayerMag(gl);			
			drawButtons(gl);
			drawSkillCoolDown(gl);
			
			//-------------------------------------------------DeathManagment-----------------------------------------------------------
			if(YoloEngine.TeamAB[YoloEngine.MyID].PlayerLive < 0 || YoloEngine.TeamAB[YoloEngine.MyID].y < 0 )
			{
				YoloEngine.TeamAB[YoloEngine.MyID].canMove = false;
				YoloEngine.TeamAB[YoloEngine.MyID].deathCount++;
				
				SplashText(gl, 0);
				if(deathIntervalCounter++ >= YoloEngine.deathSpanInterval)
				{
					if(YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
					{
						gl.glMatrixMode(GL10.GL_PROJECTION);
						gl.glLoadIdentity();
						gl.glOrthof(0f, 1f, 0f, 1f, -1f, 1f);
						YoloEngine.TeamAB[YoloEngine.MyID].x = YoloEngine.LEVEL_X/YoloEngine.TX -1;
						XADD = (YoloEngine.LEVEL_X/YoloEngine.TX-half_bx+0.5f)*YoloEngine.TEXTURE_SIZE_X- 0.5f;
						cameraPosX = -XADD;
						YADD = 0;
						cameraPosY = 0;
						gl.glTranslatef(cameraPosX,0,0f);
					}
					else
					{
						gl.glMatrixMode(GL10.GL_PROJECTION);
						gl.glLoadIdentity();
						gl.glOrthof(0f, 1f, 0f, 1f, -1f, 1f);
						YoloEngine.TeamAB[YoloEngine.MyID].x = 2;
						XADD = 0;
						cameraPosX = 0;
						YADD = 0;
						cameraPosY = 0;
						gl.glTranslatef(cameraPosX,0,0f);
					}
					YoloEngine.TeamAB[YoloEngine.MyID].y = 2.5f;
					YoloEngine.TeamAB[YoloEngine.MyID].PlayerLive = YoloEngine.TeamAB[YoloEngine.MyID].PLAYER_LIVE_MAX;
					YoloEngine.TeamAB[YoloEngine.MyID].canMove = true;
					fadeIn = 0f;
					deathIntervalCounter =0;
				}
			}
			//--------------------------------------------------EndGameCheck------------------------------------------------------------
			if(YoloEngine.creditAllyCount <= 0 )
			{
				SplashText(gl, 1);
				//TODO end screan 
				//TODO wpisanie  xp,coin z player do bazy
			}
			else if(YoloEngine.creditOppinentCount <= 0)
			{
				SplashText(gl, 2);
				//TODO end screan 
				YoloEngine.TeamAB[YoloEngine.MyID].xp *= YoloEngine.winMultiplay;
				YoloEngine.TeamAB[YoloEngine.MyID].coin *= YoloEngine.winMultiplay;
				//TODO wpisanie  xp,coin z player do bazy
			}
			
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
									if(YoloEngine.TeamAB[j].PlayerLive <= 0)
										if(YoloEngine.TeamAB[j].playerTeam == YoloEngine.TeamAB[YoloEngine.MyID].playerTeam)
											YoloEngine.creditAllyCount--;
										else
										{
											YoloEngine.creditOppinentCount--;
											if(Weapontab.get(i).playerID == YoloEngine.MyID)
											{
												YoloEngine.TeamAB[YoloEngine.MyID].xp += YoloEngine.xpForKill;	
											}
										}
									if(Weapontab.get(i).playerID == YoloEngine.MyID)
									{
										YoloEngine.TeamAB[YoloEngine.MyID].xp += Weapontab.get(i).damage*YoloEngine.TeamAB[j].Player_Dmg_reduction;
										YoloEngine.TeamAB[YoloEngine.MyID].coin += Weapontab.get(i).damage;
									}
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
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glTranslatef(liveBarX_0 + XADD,liveBarY +YADD, 0f);
		gl.glScalef(LIVE_BAR_SIZE_X_0, LIVE_BAR_SIZE_Y, 1f);
		gl.glTranslatef(.5f, .5f, 0);
		gl.glRotatef(180, 0, 0, 1);
		gl.glTranslatef(-.5f, -.5f, 0);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glColor4f(1f,1f,1f,1f);
		liveBarOut.draw(gl);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glTranslatef((liveBarX_0 + XADD + 14f/YoloEngine.display_x/YoloEngine.xdpi),(liveBarY +YADD + 4.3f/YoloEngine.display_y/YoloEngine.xdpi), 0f);
		gl.glScalef(LIVE_BAR_SIZE_X_1*0.921875f, LIVE_BAR_SIZE_Y*0.6875f, 1f);
		gl.glTranslatef(.5f, .5f, 0);
		gl.glRotatef(180, 0, 0, 1);
		gl.glTranslatef(-.5f, -.5f, 0);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glColor4f(1f,1f,1f,1f);
		liveBarIn.draw(gl);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		drawCredits(gl);
		
		//drawSt(gl, liveBarX_0 + XADD, liveBarY +YADD, LIVE_BAR_SIZE_X_0, LIVE_BAR_SIZE_Y, .75f, .125f,true);
		//drawSt(gl, liveBarX_0 + XADD, liveBarY +YADD, LIVE_BAR_SIZE_X_1,LIVE_BAR_SIZE_Y, .875f, .125f,false);
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
			death.loadTexture(gl, R.drawable.you_are_dead, YoloEngine.context);
			win.loadTexture(gl, R.drawable.you_win, YoloEngine.context);
			lose.loadTexture(gl, R.drawable.you_lose, YoloEngine.context);
			gray.loadTexture(gl, R.drawable.gray, YoloEngine.context);
			liveBarOut.loadTexture(gl, R.drawable.emptybar, YoloEngine.context);
			liveBarIn.loadTexture(gl, R.drawable.barfilling, YoloEngine.context);
			creditBarInG.loadTexture(gl, R.drawable.barfillingg, YoloEngine.context);
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
			bullet.damage = YoloEngine.TeamAB[YoloEngine.MyID].fireDamage;
			bullet.poiseDamage = poiseDamage;
			bullet.team = YoloEngine.TeamAB[YoloEngine.MyID].playerTeam; 
			bullet.sprite = YoloEngine.TeamAB[YoloEngine.MyID].fireSprite;
			bullet.x_texture = 0f;
			bullet.y_texture = 0f;
			bullet.count = count;
		//	bullet.size = 0.25f;
			bullet.isLeft = YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft;
			bullet.Aim = YoloEngine.TeamAB[YoloEngine.MyID].aim;
			bullet.playerID = YoloEngine.MyID;
			Weapontab.add(bullet);
			
			float VolumeScale =1,lx = Math.abs(bullet.x-YoloEngine.TeamAB[YoloEngine.MyID].x),ly =Math.abs(bullet.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
			if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
				VolumeScale =0;
			else
			{
				VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
			}
			if(YoloEngine.TeamAB[YoloEngine.MyID].fireSprite == 0)
				YoloEngine.sp.play(YoloEngine.SoundInd[0], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			else if(YoloEngine.TeamAB[YoloEngine.MyID].fireSprite == 30)
				YoloEngine.sp.play(YoloEngine.SoundInd[45], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			else if(YoloEngine.TeamAB[YoloEngine.MyID].fireSprite == 19)
				YoloEngine.sp.play(YoloEngine.SoundInd[57], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			else if(YoloEngine.TeamAB[YoloEngine.MyID].fireSprite == 26)
				YoloEngine.sp.play(YoloEngine.SoundInd[60], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
			
			if(YoloEngine.mGameProperties.gameType != GameProperties.OFFLINE)
				YoloEngine.mMultislayer.sendOpponentFire(bullet.x, bullet.y, YoloEngine.TeamAB[YoloEngine.MyID].isPlayerLeft, YoloEngine.TeamAB[YoloEngine.MyID].isCrouch, YoloEngine.TeamAB[YoloEngine.MyID].fireSprite, count, YoloEngine.TeamAB[YoloEngine.MyID].fireDamage, YoloEngine.TeamAB[YoloEngine.MyID].playerTeam,YoloEngine.TeamAB[YoloEngine.MyID].aim,poiseDamage);
	}
	
	public static void OpponentFire(float x, float y, boolean isLeft, boolean isCrouch,int sprite,int count,float damage, boolean team, int aim,float poiseDamge) //XXX oppfire nie potrzebuje isCrouch
	{
		bullet = new YoloWeapon(x,y,0.12f);
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
			else /*if(sprite == 33)
			{
				LoopSkillDraw(gl, Ve.elementAt(i));
				if(Ve.elementAt(i).lava_duration-- <= 0)
				{
					Ve.remove(i--);
					continue;
				}			
			}
			else*/
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
					else if(sprite == 20)
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
		gl.glTranslatef(25f/YoloEngine.display_x/YoloEngine.xdpi+XADD, 1-80f/YoloEngine.display_y/YoloEngine.xdpi+YADD, 0f);
		gl.glScalef((YoloEngine.LIVE_BAR_SIZE/1.5f/YoloEngine.display_x/YoloEngine.xdpi)*(YoloEngine.TeamAB[YoloEngine.MyID].playerMag/30f),LIVE_BAR_SIZE_Y/1.5f, 1f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glColor4f(1f,1f,1f,1f);
		gl.glTranslatef(0f, 0.875f, 0f);
		mag.drawPartial(gl,YoloEngine.TeamAB[YoloEngine.MyID].playerMag/30f);
		gl.glPopMatrix();
		gl.glLoadIdentity();
	}
	
	private void drawPowerUPs(GL10 gl)
	{
		synchronized (PowerUPtab) {
			for (PowerUP UP : PowerUPtab) {
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glLoadIdentity();
				gl.glPushMatrix();
				gl.glScalef(YoloEngine.TEXTURE_SIZE_X / 2, YoloEngine.TEXTURE_SIZE_Y / 2, 1f);
				gl.glTranslatef(UP.x * 2, UP.y * 2, 0f);
				gl.glMatrixMode(GL10.GL_TEXTURE);
				gl.glTranslatef(0f, 0.25f, 0f);
				UP.draw(gl, YoloEngine.spriteSheets, 0);
				gl.glPopMatrix();
				gl.glLoadIdentity();

				gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glLoadIdentity();
				gl.glPushMatrix();
				gl.glScalef(YoloEngine.TEXTURE_SIZE_X / 2, YoloEngine.TEXTURE_SIZE_Y / 2, 1f);
				gl.glTranslatef(UP.x * 2, UP.y * 2, 0f);
				gl.glMatrixMode(GL10.GL_TEXTURE);
				gl.glTranslatef(UP.x_texture, UP.y_texture, 0f);
				UP.draw(gl, YoloEngine.spriteSheets, 0);
				gl.glPopMatrix();
				gl.glLoadIdentity();
			}
		}
	}
	
	private void spanMovePowerUPs()
	{
		synchronized (PowerUPtab) {
			if (powerUpCoutdown-- <= 0 && YoloEngine.TeamAB[YoloEngine.MyID].isServer) {
				powerUpCoutdown = powerUpInterval;
				PowerUP mPowerUP = new PowerUP();
				YoloEngine.mMultislayer.sendPowerUp(true, mPowerUP.x, mPowerUP.y, mPowerUP.effect);
				PowerUPtab.add(mPowerUP);
				System.out.println("adding powerup");
			}

			for (PowerUP UP : PowerUPtab) {
				UP.vy -= YoloEngine.GAME_ACCELERATION;
				UP.y += UP.vy;

				if (IsCollidedTop(UP, ObjectTab[UP.j])) {
					UP.y = ObjectTab[UP.j].y + ObjectTab[UP.j].dy;
					UP.vy = 0;
				} else
					for (int i = 0; i < ObjectTab.length; i++) {
						if (IsCollidedTop(UP, ObjectTab[i])) {
							UP.y = ObjectTab[i].y + ObjectTab[i].dy;
							UP.vy = 0;
							UP.j = i;
							break;
						}
					}

			}
		}
	}
	
	private void checkForPowerUPs()
	{
		synchronized (PowerUPtab) {
			for (int i = 0; i < PowerUPtab.size(); i++) {
				if (IsCollided(PowerUPtab.elementAt(i), YoloEngine.TeamAB[YoloEngine.MyID])) {
					YoloEngine.mMultislayer.sendPowerUp(false, i, 0, 0);
					PowerUPtab.elementAt(i).Activate();
					PowerUPtab.remove(i--);
				}
			}
		}
	}
	
	private void handleParticles(GL10 gl,boolean My)
	{
		Vector<ParticleObject> particleObjectTab;
		if(My)particleObjectTab = particleObjectTabMy;
		else particleObjectTab = particleObjectTabOp;
			
			
		for (int i = 0; i < particleObjectTab.size();i++)
		{
			if(particleObjectTab.elementAt(i).lifeTime-- <= 0)
			{
				particleObjectTab.remove(i--);
				continue;
			}
				
			if(particleObjectTab.elementAt(i).isSmoke)
				for(int j = 0;j < particleObjectTab.elementAt(i).particleTab.length; j++)
				{
					if(particleObjectTab.elementAt(i).particleTab[j].drawSmoke(gl))
						{
							particleObjectTab.elementAt(i).particleTab[j] = new Particle(particleObjectTab.elementAt(i).x, particleObjectTab.elementAt(i).y,
									particleObjectTab.elementAt(i).isSmoke, particleObjectTab.elementAt(i).rng,-1,-1);
							particleObjectTab.elementAt(i).particleTab[j].drawSmoke(gl);
						}
				}
			else
			{
				for(int j = 0;j < particleObjectTab.elementAt(i).particleTab.length; j++)
				{
					if(particleObjectTab.elementAt(i).particleTab[j].drawLava(gl))
						{
							particleObjectTab.elementAt(i).particleTab[j] = new Particle(particleObjectTab.elementAt(i).x, particleObjectTab.elementAt(i).y,
									particleObjectTab.elementAt(i).isSmoke, particleObjectTab.elementAt(i).rng,particleObjectTab.elementAt(i).a0,particleObjectTab.elementAt(i).a1);
							particleObjectTab.elementAt(i).particleTab[j].drawLava(gl);
						}
				}
			}
			
		}
	}
	
	private void SplashText(GL10 gl,int wchatTxt)
	{
		if(fadeIn <= 0.7f)
			fadeIn+=0.01;
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glTranslatef(XADD, YADD, 0);
		gl.glScalef(1, 1, 1);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glColor4f(0f,0f,0f,fadeIn);
		gray.draw(gl);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		switch(wchatTxt)
		{
		case 0://death
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glTranslatef(XADD, YADD, 0);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glColor4f(1f,1f,1f,1f);
			death.draw(gl);
			gl.glPopMatrix();
			gl.glLoadIdentity();
			break;
		case 1://lose
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glTranslatef(XADD, YADD, 0);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glColor4f(1f,1f,1f,1f);
			lose.draw(gl);
			gl.glPopMatrix();
			gl.glLoadIdentity();
			break;
		case 2://win
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glTranslatef(XADD, YADD, 0);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glColor4f(1f,1f,1f,1f);
			win.draw(gl);
			gl.glPopMatrix();
			gl.glLoadIdentity();
			break;
		}
	}
	
	private void drawCredits(GL10 gl)
	{
		creditBarSizeX1 = creditBarSizeX0*YoloEngine.creditAllyCount/YoloEngine.creditStartValue;
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glTranslatef(creditBarPosX + XADD,liveBarY +YADD, 0f);
		gl.glScalef(creditBarSizeX0, creditBarSizeY0, 1f);
		gl.glTranslatef(.5f, .5f, 0);
		gl.glRotatef(180, 0, 0, 1);
		gl.glTranslatef(-.5f, -.5f, 0);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glColor4f(1f,1f,1f,1f);
		liveBarOut.draw(gl);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glTranslatef((creditBarPosX1 + XADD),(liveBarY +YADD + 4.3f/YoloEngine.display_y/YoloEngine.xdpi), 0f);
		gl.glScalef(creditBarSizeX1*0.921875f, creditBarSizeY0*0.6875f, 1f);
		gl.glTranslatef(.5f, .5f, 0);
		gl.glRotatef(180, 0, 0, 1);
		gl.glTranslatef(-.5f, -.5f, 0);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glColor4f(1f,1f,1f,1f);
		liveBarIn.draw(gl);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		creditBarSizeX1 = creditBarSizeX0*YoloEngine.creditOppinentCount/YoloEngine.creditStartValue;
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glTranslatef(creditBarPosX + XADD,liveBarY +YADD - 30/YoloEngine.display_y/YoloEngine.xdpi, 0f);
		gl.glScalef(creditBarSizeX0, creditBarSizeY0, 1f);
		gl.glTranslatef(.5f, .5f, 0);
		gl.glRotatef(180, 0, 0, 1);
		gl.glTranslatef(-.5f, -.5f, 0);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glColor4f(1f,1f,1f,1f);
		liveBarOut.draw(gl);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glTranslatef((creditBarPosX1 + XADD),(liveBarY +YADD -26f/YoloEngine.display_y/YoloEngine.xdpi), 0f);
		gl.glScalef(creditBarSizeX1*0.921875f, creditBarSizeY0*0.6875f, 1f);
		gl.glTranslatef(.5f, .5f, 0);
		gl.glRotatef(180, 0, 0, 1);
		gl.glTranslatef(-.5f, -.5f, 0);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glColor4f(1f,1f,1f,1f);
		creditBarInG.draw(gl);
		gl.glPopMatrix();
		gl.glLoadIdentity();
	}
	
	enum acction
	{
		goLeft, goRight, ladderDown, ladderUP, jump, jumpLeft, jumpRight, stand;
		
		public static acction fromInt(int i)
		{
			switch(i)
			{
				case 0:
				return goLeft;
				case 1:
				return goRight;
				case 2:
				return ladderDown;
				case 3:
				return ladderUP;
				case 4:
				return jump;
				case 5:
				return jumpLeft;
				case 6:
				return jumpRight;
			}
			
			return goLeft;
		}
	}

	class NodeDistanceComperator implements Comparator<Node>
	{
		@Override
	    public int compare(Node x, Node y)
	    {
	        if (x.distance < y.distance)
	        {
	            return -1;
	        }
	        if (x.distance > y.distance)
	        {
	            return 1;
	        }
	        return 0;
	    }
	}

	class NodeInfo
	{
		int subNode,subNodeValue;
		acction subNodeAcction;
		public NodeInfo(int NodeID, acction NodeAcction, int value)
		{
			subNode = NodeID;
			subNodeAcction = NodeAcction;
			subNodeValue = value;
		}
	}
		
	class Node 
	{
		int ID;
		float x0,y0,x1,y1;
		NodeInfo connections[];
		int distance = 10000;
		int previousNode = -1;
		
		public Node(int count)
		{
			connections = new NodeInfo[count];
		}
		
		public void reSet()
		{
			distance = 10000;
			previousNode = -1;
		}
	}

	class Map
	{
		Comparator<Node> comparato;
		PriorityQueue<Node> queue;
		Node[] nodes;
		int[] IDtoIndex;
		
		private float calculateXFromInt(int i)
		{
			return (float)(i / YoloEngine.TX);
		}
		
		private float calculateYFromInt(int i)
		{
			return (float)(i / YoloEngine.TY);
		}
		
		public Map(int ID)
		{
			 DataInputStream inputStream; 
			 try 
			 {
				 int NodeCount,NodeICount;
				 inputStream = new DataInputStream(YoloEngine.context.getResources().openRawResource(ID));
				 IDtoIndex = new int[inputStream.readInt()];
				 NodeCount = inputStream.readInt();
				 nodes = new Node[NodeCount];
				 for(int i = 0; i < NodeCount; i++)
				 {
					 
					 NodeICount = inputStream.readInt();
					 nodes[i] = new Node(NodeICount);
					 for(int j = 0; j < NodeICount; j++)
					 {
						 nodes[i].connections[j] = new NodeInfo(inputStream.readInt(), acction.fromInt(inputStream.readInt()), inputStream.readInt()); 
					 }
					 nodes[i].x0 = calculateXFromInt(inputStream.readInt());
					 nodes[i].y0 = calculateYFromInt(inputStream.readInt());
					 nodes[i].x1 = calculateXFromInt(inputStream.readInt());
					 nodes[i].y1 = calculateYFromInt(inputStream.readInt());
					 nodes[i].ID = inputStream.readInt();
					 IDtoIndex[nodes[i].ID] = i;
				 }
				 inputStream.close();		    
			 } catch (FileNotFoundException e) {System.out.println("Loading NodeMap faild becouse of:"+e.getMessage());} catch (IOException e) {System.out.println("Loading NodeMap faild becouse of:"+e.getMessage());}
			 comparato = new NodeDistanceComperator();
			 queue = new PriorityQueue<Node>(nodes.length,comparato);
		}
	}
	
	private void initAI(int AIID)
	{
		Random rng = new Random();
		int cooldownMultiplayer = 1;
		YoloEngine.TeamAB[AIID].race = rng.nextInt(3);
		YoloEngine.TeamAB[AIID].playerTeam = !YoloEngine.TeamAB[YoloEngine.MyID].playerTeam; //TODO undo Test
		if(!YoloEngine.TeamAB[AIID].playerTeam )
		{
			YoloEngine.TeamAB[AIID].x = YoloEngine.LEVEL_X/YoloEngine.TX -1;
		}
		else
		{
			YoloEngine.TeamAB[AIID].x = 2;
		}
		YoloEngine.TeamAB[AIID].y = 2.5f;
		YoloEngine.TeamAB[AIID].difficulty = YoloEngine.AIDificulty;
		
		switch(YoloEngine.AIDificulty)
		{
		case 0:
			YoloEngine.TeamAB[AIID].weapon = rng.nextInt(3);
			YoloEngine.TeamAB[AIID].fireXRadius = YoloEngine.fireXRadius0;
			YoloEngine.TeamAB[AIID].fireYRadius = YoloEngine.fireYRadius0;
			YoloEngine.TeamAB[AIID].skillXRadius = YoloEngine.skillXRadius0;
			YoloEngine.TeamAB[AIID].skillYRadius = YoloEngine.skillYRadius0;
			cooldownMultiplayer = 6;
			switch(YoloEngine.TeamAB[AIID].race)
			{
			case 0:
				YoloEngine.TeamAB[AIID].skill1 = YoloEngine.skill00A[rng.nextInt(YoloEngine.skill00A.length)];
				YoloEngine.TeamAB[AIID].skill2 = YoloEngine.skill00D[rng.nextInt(YoloEngine.skill00D.length)];
				YoloEngine.TeamAB[AIID].skill3 = YoloEngine.skill00B[rng.nextInt(YoloEngine.skill00B.length)];
				break;
			case 1:
				YoloEngine.TeamAB[AIID].skill1 = YoloEngine.skill01A[rng.nextInt(YoloEngine.skill01A.length)];
				YoloEngine.TeamAB[AIID].skill2 = YoloEngine.skill01D[rng.nextInt(YoloEngine.skill01D.length)];
				YoloEngine.TeamAB[AIID].skill3 = YoloEngine.skill01B[rng.nextInt(YoloEngine.skill01B.length)];
				break;
			case 2:
				YoloEngine.TeamAB[AIID].skill1 = YoloEngine.skill02A[rng.nextInt(YoloEngine.skill02A.length)];
				YoloEngine.TeamAB[AIID].skill2 = YoloEngine.skill02D[rng.nextInt(YoloEngine.skill02D.length)];
				YoloEngine.TeamAB[AIID].skill3 = YoloEngine.skill02B[rng.nextInt(YoloEngine.skill02B.length)];
				break;
			}
			break;
		case 1:
			YoloEngine.TeamAB[AIID].weapon = rng.nextInt(4) + 2;
			YoloEngine.TeamAB[AIID].fireXRadius = YoloEngine.fireXRadius1;
			YoloEngine.TeamAB[AIID].fireYRadius = YoloEngine.fireYRadius1;
			YoloEngine.TeamAB[AIID].skillXRadius = YoloEngine.skillXRadius1;
			YoloEngine.TeamAB[AIID].skillYRadius = YoloEngine.skillYRadius1;
			cooldownMultiplayer = 4;
			switch(YoloEngine.TeamAB[AIID].race)
			{
			case 0:
				YoloEngine.TeamAB[AIID].skill1 = YoloEngine.skill10A[rng.nextInt(YoloEngine.skill10A.length)];
				YoloEngine.TeamAB[AIID].skill2 = YoloEngine.skill10D[rng.nextInt(YoloEngine.skill10D.length)];
				YoloEngine.TeamAB[AIID].skill3 = YoloEngine.skill10B[rng.nextInt(YoloEngine.skill10B.length)];
				break;
			case 1:
				YoloEngine.TeamAB[AIID].skill1 = YoloEngine.skill11A[rng.nextInt(YoloEngine.skill11A.length)];
				YoloEngine.TeamAB[AIID].skill2 = YoloEngine.skill11D[rng.nextInt(YoloEngine.skill11D.length)];
				YoloEngine.TeamAB[AIID].skill3 = YoloEngine.skill11B[rng.nextInt(YoloEngine.skill11B.length)];
				break;
			case 2:
				YoloEngine.TeamAB[AIID].skill1 = YoloEngine.skill12A[rng.nextInt(YoloEngine.skill12A.length)];
				YoloEngine.TeamAB[AIID].skill2 = YoloEngine.skill12D[rng.nextInt(YoloEngine.skill12D.length)];
				YoloEngine.TeamAB[AIID].skill3 = YoloEngine.skill12B[rng.nextInt(YoloEngine.skill12B.length)];
				break;
			}
			break;
		case 2:
			YoloEngine.TeamAB[AIID].weapon = rng.nextInt(4) + 5;
			YoloEngine.TeamAB[AIID].fireXRadius = YoloEngine.fireXRadius2;
			YoloEngine.TeamAB[AIID].fireYRadius = YoloEngine.fireYRadius2;
			YoloEngine.TeamAB[AIID].skillXRadius = YoloEngine.skillXRadius2;
			YoloEngine.TeamAB[AIID].skillYRadius = YoloEngine.skillYRadius2;
			cooldownMultiplayer = 3;
			switch(YoloEngine.TeamAB[AIID].race)
			{
			case 0:
				YoloEngine.TeamAB[AIID].skill1 = YoloEngine.skill20A[rng.nextInt(YoloEngine.skill20A.length)];
				YoloEngine.TeamAB[AIID].skill2 = YoloEngine.skill20D[rng.nextInt(YoloEngine.skill20D.length)];
				YoloEngine.TeamAB[AIID].skill3 = YoloEngine.skill20B[rng.nextInt(YoloEngine.skill20B.length)];
				break;
			case 1:
				YoloEngine.TeamAB[AIID].skill1 = YoloEngine.skill21A[rng.nextInt(YoloEngine.skill21A.length)];
				YoloEngine.TeamAB[AIID].skill2 = YoloEngine.skill21D[rng.nextInt(YoloEngine.skill21D.length)];
				YoloEngine.TeamAB[AIID].skill3 = YoloEngine.skill21B[rng.nextInt(YoloEngine.skill21B.length)];
				break;
			case 2:
				YoloEngine.TeamAB[AIID].skill1 = YoloEngine.skill22A[rng.nextInt(YoloEngine.skill22A.length)];
				YoloEngine.TeamAB[AIID].skill2 = YoloEngine.skill22D[rng.nextInt(YoloEngine.skill22D.length)];
				YoloEngine.TeamAB[AIID].skill3 = YoloEngine.skill22B[rng.nextInt(YoloEngine.skill22B.length)];
				break;
			}
			break;
		}
		
		YoloEngine.TeamAB[AIID].skill1OrginalCooldown = YoloEngine.cooldownsTab[YoloEngine.TeamAB[AIID].skill1]*cooldownMultiplayer;
		YoloEngine.TeamAB[AIID].skill2OrginalCooldown = YoloEngine.cooldownsTab[YoloEngine.TeamAB[AIID].skill2]*cooldownMultiplayer;
		YoloEngine.TeamAB[AIID].skill3OrginalCooldown = YoloEngine.cooldownsTab[YoloEngine.TeamAB[AIID].skill3]*cooldownMultiplayer;
		
		YoloEngine.sprite_load[YoloEngine.TeamAB[AIID].skill1<45?YoloEngine.TeamAB[AIID].skill1 : YoloEngine.TeamAB[AIID].skill1-87] = true;//Zaleï¿½y od playera
		YoloEngine.sprite_load[YoloEngine.TeamAB[AIID].skill2<45?YoloEngine.TeamAB[AIID].skill2 : YoloEngine.TeamAB[AIID].skill2-87] = true;//Zaleï¿½y od playera
		YoloEngine.sprite_load[YoloEngine.TeamAB[AIID].skill3<45?YoloEngine.TeamAB[AIID].skill3 : YoloEngine.TeamAB[AIID].skill3-87] = true;//Zaleï¿½y od playera
		if(YoloEngine.TeamAB[AIID].skill3==14||YoloEngine.TeamAB[AIID].skill2==14||YoloEngine.TeamAB[AIID].skill1==14)YoloEngine.sprite_load[27]=true;
		if(YoloEngine.TeamAB[AIID].skill3==36||YoloEngine.TeamAB[AIID].skill2==36||YoloEngine.TeamAB[AIID].skill1==36)YoloEngine.sprite_load[32]=true;
		if(YoloEngine.TeamAB[AIID].skill3==37||YoloEngine.TeamAB[AIID].skill2==37||YoloEngine.TeamAB[AIID].skill1==37)YoloEngine.sprite_load[32]=true;
		if(YoloEngine.TeamAB[AIID].skill3==38||YoloEngine.TeamAB[AIID].skill2==38||YoloEngine.TeamAB[AIID].skill1==38)YoloEngine.sprite_load[32]=true;
		if(YoloEngine.TeamAB[AIID].skill3==43||YoloEngine.TeamAB[AIID].skill2==43||YoloEngine.TeamAB[AIID].skill1==43)YoloEngine.sprite_load[41]=true;
		if(YoloEngine.TeamAB[AIID].skill3==120||YoloEngine.TeamAB[AIID].skill2==120||YoloEngine.TeamAB[AIID].skill1==120)YoloEngine.sprite_load[32]=true;
		if(YoloEngine.TeamAB[AIID].skill3==121||YoloEngine.TeamAB[AIID].skill2==121||YoloEngine.TeamAB[AIID].skill1==121)YoloEngine.sprite_load[32]=true;
		if(YoloEngine.TeamAB[AIID].skill3==122||YoloEngine.TeamAB[AIID].skill2==122||YoloEngine.TeamAB[AIID].skill1==122)YoloEngine.sprite_load[32]=true;
		if(YoloEngine.TeamAB[AIID].skill3==123||YoloEngine.TeamAB[AIID].skill2==123||YoloEngine.TeamAB[AIID].skill1==123)YoloEngine.sprite_load[32]=true;
		if(YoloEngine.TeamAB[AIID].skill3==124||YoloEngine.TeamAB[AIID].skill2==124||YoloEngine.TeamAB[AIID].skill1==124)YoloEngine.sprite_load[32]=true;
		
		//TODO XXX przesylanie do wszystkich jakie skille za�adowac chyba dziala lae trzeba jeszcze sprawdzic 
		
		YoloEngine.TeamAB[AIID].NodesToGo.add(-1);
		YoloEngine.TeamAB[AIID].acction = acction.jump;
		YoloEngine.TeamAB[AIID].nextAcction = acction.jump;
		
	}

	private void searchForClosestPlayer(int AIID)
	{
		float distance = 1000,p;
		int closestIndex = 0,start=0;
		if(!YoloEngine.TeamAB[AIID].playerTeam) start = YoloEngine.TeamSize;
		for(int i = start; i < YoloEngine.TeamSize + start; i++)
		{
			p = (float)Math.sqrt((YoloEngine.TeamAB[AIID].x-YoloEngine.TeamAB[i].x)*(YoloEngine.TeamAB[AIID].x-YoloEngine.TeamAB[i].x) + (YoloEngine.TeamAB[AIID].y-YoloEngine.TeamAB[i].y)*(YoloEngine.TeamAB[AIID].y-YoloEngine.TeamAB[i].y));
			if(distance > p)
			{
				distance = p;
				closestIndex = i;
			}
		}
		YoloEngine.TeamAB[AIID].currentTrackedPlayerID = closestIndex;
		YoloEngine.TeamAB[AIID].targetDistance = distance;
	}

	private int checkTargetNode(int AIID)//return ID 
	{
		float x = YoloEngine.TeamAB[YoloEngine.TeamAB[AIID].currentTrackedPlayerID].x, y = YoloEngine.TeamAB[YoloEngine.TeamAB[AIID].currentTrackedPlayerID].y;
		
		for(Node n : YoloEngine.map.nodes)
		{
			if(x + .5f >= n.x0 && x +.5f < n.x1)
				if(y + .5f >= n.y0 && y + .5f < n.y1)
				{
					YoloEngine.TeamAB[AIID].findingRodeOutOfNodeIntervalCounter = YoloEngine.findingRodeOutOfNodeInterval;
					return n.ID; 
				}	
		}
		return -1;
	}

	private int checkTargetClostestNode(int AIID)
	{
		float x = YoloEngine.TeamAB[YoloEngine.TeamAB[AIID].currentTrackedPlayerID].x, y = YoloEngine.TeamAB[YoloEngine.TeamAB[AIID].currentTrackedPlayerID].y,
				min = Float.MAX_VALUE,pom;
		int ID = 0;
		for(Node n : YoloEngine.map.nodes)
		{
			pom = ((n.x0 + n.x1)/2f - x)*((n.x0 + n.x1)/2f - x) + ((n.y0 + n.y1)/2f - y)*((n.y0 + n.y1)/2f - y)/4f;
			if(pom < min)
			{
				min = pom;
				ID = n.ID;
			}
		}
		return ID;
	}
	
	private int checkAIClosestNode(int AIID)
	{
		float x = YoloEngine.TeamAB[AIID].x, y = YoloEngine.TeamAB[AIID].y,
				min = Float.MAX_VALUE,pom;
		int ID = 0;
		for(Node n : YoloEngine.map.nodes)
		{
			pom = ((n.x0 + n.x1)/2f - x)*((n.x0 + n.x1)/2f - x) + ((n.y0 + n.y1)/2f - y)*((n.y0 + n.y1)/2f - y)/4f;
			if(pom < min)
			{
				min = pom;
				ID = n.ID;
			}
		}
		return ID;
	}
	
	private int checkAINodeTranslate(int AIID)//return ID 
	{
		float x = YoloEngine.TeamAB[AIID].x, y = YoloEngine.TeamAB[AIID].y;
		
		for(Node n : YoloEngine.map.nodes)
		{
			if(x + 1f >= n.x0 && x < n.x1)
				if(y + 1.5f >= n.y0 && y+.5f < n.y1)
				{
					return n.ID; 
				}			
		}
		return -1;
	}
	
	private int checkAINodeMiddle(int AIID)//return ID 
	{
		float x = YoloEngine.TeamAB[AIID].x, y = YoloEngine.TeamAB[AIID].y;
		
		for(Node n : YoloEngine.map.nodes)
		{
			if(x + .5f >= n.x0 && x +.5f < n.x1)
				if(y + .5f >= n.y0 && y +.5f < n.y1)
				{
					return n.ID; 
				}	
		}
		return -1;
	}

	private boolean checkTargetChangeNode(int AIID)//set ID
	{
		int last = YoloEngine.TeamAB[AIID].NodesToGo.elementAt(0),current = checkTargetNode(AIID);
		if( current == last || current == -1)
		{
			if(current == -1)
			{
				if(YoloEngine.TeamAB[YoloEngine.TeamAB[AIID].currentTrackedPlayerID].vy == 0)
				{
					YoloEngine.TeamAB[AIID].NodesToGo.set(0,current);
					return true;
				}
				else if(YoloEngine.TeamAB[AIID].findingRodeOutOfNodeIntervalCounter-- <= 0)
				{
					YoloEngine.TeamAB[AIID].NodesToGo.set(0,current);
					return true;
				}
			}
			return false;
		}
		else
		{
			YoloEngine.TeamAB[AIID].NodesToGo.set(0,current);
			return true;
		}
	}

	private boolean checkTargetChange(int AIID)
	{
		int last = YoloEngine.TeamAB[AIID].currentTrackedPlayerID;
		searchForClosestPlayer(AIID);
		if(last == YoloEngine.TeamAB[AIID].currentTrackedPlayerID)
			return false;
		else
			return true;
	}

	private void fillDistanceFrom(int AINodeId)
	{
		Node n;
		int dist;
		YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[AINodeId]].distance = 0;
		
		for(int i = 0; i < YoloEngine.map.nodes.length; i++ )
		{
			YoloEngine.map.queue.add(YoloEngine.map.nodes[i]);
		}
		n = YoloEngine.map.queue.poll();
		while(n != null)
		{
			//System.out.println(q++);
			for(int j = 0; j < n.connections.length; j++)
			{
				dist = n.distance + n.connections[j].subNodeValue;
				if(dist < YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[n.connections[j].subNode]].distance)
				{
					
					YoloEngine.map.queue.remove(YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[n.connections[j].subNode]]);
					YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[n.connections[j].subNode]].distance = dist;
					YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[n.connections[j].subNode]].previousNode = n.ID;
					YoloEngine.map.queue.add(YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[n.connections[j].subNode]]);
				}
			}
			n = YoloEngine.map.queue.poll();
		}
	}

	private void saveNodeRode(int AINodeId, int targetNodeId, int AIID)//out put ID nodestogo
	{
		int currentNodeIndex = YoloEngine.map.IDtoIndex[targetNodeId];
		AINodeId = YoloEngine.map.IDtoIndex[AINodeId];
		YoloEngine.TeamAB[AIID].NodesToGo.clear();
		while(currentNodeIndex != AINodeId)
		{
			YoloEngine.TeamAB[AIID].NodesToGo.add(YoloEngine.map.nodes[currentNodeIndex].ID);
			currentNodeIndex = YoloEngine.map.IDtoIndex[YoloEngine.map.nodes[currentNodeIndex].previousNode];
		}
		if(YoloEngine.TeamAB[AIID].NodesToGo.size() > 0)
		{
			YoloEngine.TeamAB[AIID].acction = indexToAcction(AIID, YoloEngine.map.IDtoIndex[YoloEngine.TeamAB[AIID].NodesToGo.lastElement()],YoloEngine.TeamAB[AIID].currentNode);
			if(YoloEngine.TeamAB[AIID].NodesToGo.size() > 1)
				YoloEngine.TeamAB[AIID].nextAcction = indexToAcction(AIID, YoloEngine.map.IDtoIndex[YoloEngine.TeamAB[AIID].NodesToGo.elementAt(YoloEngine.TeamAB[AIID].NodesToGo.size()-2)],
						YoloEngine.TeamAB[AIID].NodesToGo.lastElement());
		}
		else
			YoloEngine.TeamAB[AIID].acction = acction.goLeft;
		YoloEngine.TeamAB[AIID].NodesToGo.add(YoloEngine.map.nodes[AINodeId].ID);

	} 

	private void reSetNodeDistances()
	{
		for(Node n : YoloEngine.map.nodes)
			n.reSet();
	}

	private void findShortestRode(int AIID, int targetNodeId)
	{
		fillDistanceFrom(YoloEngine.TeamAB[AIID].currentNode);
		saveNodeRode(YoloEngine.TeamAB[AIID].currentNode,targetNodeId,AIID);
		reSetNodeDistances();
	}	

	private acction indexToAcction(int AIID, int indexOfNextNode, int AINode)
	{
	//	int nextNodeIndex = YoloEngine.map.nodes[index].previousNode;
		for(NodeInfo n : YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[AINode]].connections)
		{
			
			if(YoloEngine.map.IDtoIndex[n.subNode] == indexOfNextNode)
				return n.subNodeAcction;
		}
		return acction.goLeft;
	}

	private void makeInsideMove(int AIID, acction a)
	{
		if(YoloEngine.TeamAB[AIID].x < -.5f)
			YoloEngine.TeamAB[AIID].x = YoloEngine.LEVEL_X/YoloEngine.TX-0.5f;
		else if(YoloEngine.TeamAB[AIID].x > YoloEngine.LEVEL_X/YoloEngine.TX-0.5f)
			YoloEngine.TeamAB[AIID].x = -0.5f;
		switch(a)
		{
			case goLeft:
			YoloEngine.TeamAB[AIID].x -= 0.08f;
			break;
			case goRight:
			YoloEngine.TeamAB[AIID].x += 0.08f;
			break;
			case ladderUP:
			YoloEngine.TeamAB[AIID].y +=YoloEngine.PLAYER_CLIMBING_SPEED;
			YoloEngine.TeamAB[AIID].isClimbingUp = true;
			break;
			case ladderDown:
			YoloEngine.TeamAB[AIID].y -=YoloEngine.PLAYER_CLIMBING_SPEED;
			YoloEngine.TeamAB[AIID].isClimbingUp = true;
			break;
			case jump:
			if(YoloEngine.TeamAB[AIID].vy <= -0.06f || YoloEngine.TeamAB[AIID].onGround)
			{
				if(YoloEngine.TeamAB[AIID].jumps++ < 2)
					YoloEngine.TeamAB[AIID].vy = 0.24f;
			}
			break;
			case jumpLeft:
			YoloEngine.TeamAB[AIID].x -= 0.08f;
			break;
			case jumpRight:
			YoloEngine.TeamAB[AIID].x += 0.08f;
			break;
			case stand:
				break;
		}
	}

	private void makeOutsideMove(int AIID, acction a)
	{
		if(YoloEngine.TeamAB[AIID].x < -.5f)
			YoloEngine.TeamAB[AIID].x = YoloEngine.LEVEL_X/YoloEngine.TX-0.5f;
		else if(YoloEngine.TeamAB[AIID].x > YoloEngine.LEVEL_X/YoloEngine.TX-0.5f)
			YoloEngine.TeamAB[AIID].x = -0.5f;
		switch(a)
		{
			case goLeft:
			YoloEngine.TeamAB[AIID].x -= 0.08f;
			break;
			case goRight:
			YoloEngine.TeamAB[AIID].x += 0.08f;
			break;
			case ladderUP:
			YoloEngine.TeamAB[AIID].y +=YoloEngine.PLAYER_CLIMBING_SPEED;
			YoloEngine.TeamAB[AIID].isClimbingUp = true;
			break;
			case ladderDown:
			YoloEngine.TeamAB[AIID].y -=YoloEngine.PLAYER_CLIMBING_SPEED;
			YoloEngine.TeamAB[AIID].isClimbingUp = true;
			break;
			case jump:
			if(YoloEngine.TeamAB[AIID].vy <= -0.06f || YoloEngine.TeamAB[AIID].onGround)
			{
				if(YoloEngine.TeamAB[AIID].jumps++ < 2)
					YoloEngine.TeamAB[AIID].vy = 0.24f;
			}
			break;
			case jumpLeft:
			if(YoloEngine.TeamAB[AIID].NodesToGo.size() > 1)
				if(YoloEngine.TeamAB[AIID].x +.5f < YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[YoloEngine.TeamAB[AIID].NodesToGo.elementAt(YoloEngine.TeamAB[AIID].NodesToGo.size() -2)]].x0
						|| YoloEngine.TeamAB[AIID].x +.5f > YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[YoloEngine.TeamAB[AIID].NodesToGo.elementAt(YoloEngine.TeamAB[AIID].NodesToGo.size() -2)]].x1)
				{
					if(YoloEngine.TeamAB[AIID].vy <= -0.14f || YoloEngine.TeamAB[AIID].onGround)
					{
						if(YoloEngine.TeamAB[AIID].jumps++ < 2)
							YoloEngine.TeamAB[AIID].vy = 0.24f;
					}
					YoloEngine.TeamAB[AIID].x -= 0.08f;
				}
			break;
			case jumpRight:
			if(YoloEngine.TeamAB[AIID].NodesToGo.size() > 1)
				if(YoloEngine.TeamAB[AIID].x +.5f < YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[YoloEngine.TeamAB[AIID].NodesToGo.elementAt(YoloEngine.TeamAB[AIID].NodesToGo.size() -2)]].x0
						|| YoloEngine.TeamAB[AIID].x +.5f > YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[YoloEngine.TeamAB[AIID].NodesToGo.elementAt(YoloEngine.TeamAB[AIID].NodesToGo.size() -2)]].x1)
				{
					if(YoloEngine.TeamAB[AIID].vy <= -0.14f || YoloEngine.TeamAB[AIID].onGround)
					{
						if(YoloEngine.TeamAB[AIID].jumps++ < 2)
							YoloEngine.TeamAB[AIID].vy = 0.24f;
					}
					YoloEngine.TeamAB[AIID].x += 0.08f;
				}
			break;
			case stand:
				break;
		}
	}

	private void folowTarget(int AIID)
	{
		if(YoloEngine.TeamAB[AIID].followDelayCounter-- < 0)//op�neinie przy zmianie kierunko celu
		{
			if(YoloEngine.TeamAB[YoloEngine.TeamAB[AIID].currentTrackedPlayerID].x > YoloEngine.TeamAB[AIID].x)
				YoloEngine.TeamAB[AIID].followXbuffer = .08f;
			else
				YoloEngine.TeamAB[AIID].followXbuffer = -.08f;
			
			switch(YoloEngine.AIDificulty)
			{
			case 0:
				YoloEngine.TeamAB[AIID].followDelayCounter = YoloEngine.followDelay0;
			break;
			case 1:
				YoloEngine.TeamAB[AIID].followDelayCounter = YoloEngine.followDelay1;
			break;
			case 2:
				YoloEngine.TeamAB[AIID].followDelayCounter = YoloEngine.followDelay2;
			break;
			}
		}
		
		if(YoloEngine.AIDificulty != 2)
			YoloEngine.TeamAB[AIID].x += YoloEngine.TeamAB[AIID].followXbuffer;
		else
		{
			if(YoloEngine.TeamAB[AIID].followXbuffer > 0)
				if(YoloEngine.TeamAB[YoloEngine.TeamAB[AIID].currentTrackedPlayerID].x > YoloEngine.TeamAB[AIID].x + YoloEngine.TeamAB[AIID].followXbuffer)
					YoloEngine.TeamAB[AIID].x += YoloEngine.TeamAB[AIID].followXbuffer;
				else
					YoloEngine.TeamAB[AIID].x = YoloEngine.TeamAB[YoloEngine.TeamAB[AIID].currentTrackedPlayerID].x;
			else
				if(YoloEngine.TeamAB[YoloEngine.TeamAB[AIID].currentTrackedPlayerID].x < YoloEngine.TeamAB[AIID].x + YoloEngine.TeamAB[AIID].followXbuffer)
					YoloEngine.TeamAB[AIID].x += YoloEngine.TeamAB[AIID].followXbuffer;
				else
					YoloEngine.TeamAB[AIID].x = YoloEngine.TeamAB[YoloEngine.TeamAB[AIID].currentTrackedPlayerID].x;
		}
		
	}

	private int searchForPlayerInFireRadius(int AIID)
	{
		int start = 0;
		float x0 = YoloEngine.TeamAB[AIID].x + .5f - YoloEngine.TeamAB[AIID].fireXRadius/2f,y0 = YoloEngine.TeamAB[AIID].y + .5f - YoloEngine.TeamAB[AIID].fireYRadius/2f,
				x1 = x0 + YoloEngine.TeamAB[AIID].fireXRadius,y1 = y0 + YoloEngine.TeamAB[AIID].fireYRadius;
		if(!YoloEngine.TeamAB[AIID].playerTeam) start = YoloEngine.TeamSize;
		for(int i = start; i < YoloEngine.TeamSize + start;i++)
		{
			if(YoloEngine.TeamAB[i].x + .5f >= x0 && YoloEngine.TeamAB[i].x + .5f <= x1)
				if(YoloEngine.TeamAB[i].y + .5f >=y0 && YoloEngine.TeamAB[i].y + .5f <= y1)
					return i;
		}
		return -1;
	}

	private int searchForSkillAIInFireRadius(int AIID)
	{
		float x0 = YoloEngine.TeamAB[AIID].x + .5f -  YoloEngine.TeamAB[AIID].fireXRadius/2f,y0 = YoloEngine.TeamAB[AIID].y + .5f - YoloEngine.TeamAB[AIID].fireYRadius/2f,
				x1 = x0 + YoloEngine.TeamAB[AIID].fireXRadius,y1 = y0 + YoloEngine.TeamAB[AIID].fireYRadius;
		
		if(YoloEngine.TeamAB[AIID].playerTeam != YoloEngine.TeamA ) 
		for(int i = 0; i < skillTeamAVe.size();i++)
		{
			if(skillTeamAVe.elementAt(i).x + .5f >= x0 && skillTeamAVe.elementAt(i).x + .5f <= x1)
				if(skillTeamAVe.elementAt(i).y + .5f >=y0 && skillTeamAVe.elementAt(i).y + .5f <= y1)
					return i;
		}
		else
		for(int i = 0; i < skillTeamBVe.size();i++)
		{
			if(skillTeamBVe.elementAt(i).x + .5f >= x0 && skillTeamBVe.elementAt(i).x + .5f <= x1)
				if(skillTeamBVe.elementAt(i).y + .5f >=y0 && skillTeamBVe.elementAt(i).y + .5f <= y1)
					return i;
		}
		
		return -1;
	}

	private int searchForPlayerInSkillRadius(int AIID)
	{
		int start = 0;
		float x0 = YoloEngine.TeamAB[AIID].x + .5f - YoloEngine.TeamAB[AIID].skillXRadius/2f,y0 = YoloEngine.TeamAB[AIID].y + .5f - YoloEngine.TeamAB[AIID].skillYRadius/2f,
				x1 = x0 + YoloEngine.TeamAB[AIID].skillXRadius,y1 = y0 + YoloEngine.TeamAB[AIID].skillYRadius;
		
		if(!YoloEngine.TeamAB[AIID].playerTeam) start = YoloEngine.TeamSize;
		for(int i = start; i < YoloEngine.TeamSize + start;i++)
		{
			if(YoloEngine.TeamAB[i].x + .5f >= x0 && YoloEngine.TeamAB[i].x + .5f <= x1)
				if(YoloEngine.TeamAB[i].y + .5f >=y0 && YoloEngine.TeamAB[i].y + .5f <= y1)
					return i;
		}
		return -1;
	}

	private int searchForSkillAIInSkillRadius(int AIID)
	{
		float x0 = YoloEngine.TeamAB[AIID].x + .5f - YoloEngine.TeamAB[AIID].skillXRadius/2f,y0 = YoloEngine.TeamAB[AIID].y + .5f - YoloEngine.TeamAB[AIID].skillYRadius/2f,
				x1 = x0 + YoloEngine.TeamAB[AIID].skillXRadius,y1 = y0 + YoloEngine.TeamAB[AIID].skillYRadius;
		
		if(YoloEngine.TeamAB[AIID].playerTeam != YoloEngine.TeamA ) 
		for(int i = 0; i < skillTeamAVe.size();i++)
		{
			if(skillTeamAVe.elementAt(i).x + .5f >= x0 && skillTeamAVe.elementAt(i).x + .5f <= x1)
				if(skillTeamAVe.elementAt(i).y + .5f >=y0 && skillTeamAVe.elementAt(i).y + .5f <= y1)
					return i;
		}
		else
		for(int i = 0; i < skillTeamBVe.size();i++)
		{
			if(skillTeamBVe.elementAt(i).x + .5f >= x0 && skillTeamBVe.elementAt(i).x + .5f <= x1)
				if(skillTeamBVe.elementAt(i).y + .5f >=y0 && skillTeamBVe.elementAt(i).y + .5f <= y1)
					return i;
		}
		
		return -1;
	}

	private void playerAIFire(int AIID)
	{
		bullet = new YoloWeapon(YoloEngine.TeamAB[AIID].x,
				!YoloEngine.TeamAB[AIID].isCrouch?YoloEngine.TeamAB[AIID].y:YoloEngine.TeamAB[AIID].y ,0.12f);
		bullet.damage = YoloEngine.TeamAB[AIID].fireDamage;
		bullet.poiseDamage = YoloEngine.TeamAB[AIID].poiseDamage;
		bullet.team = YoloEngine.TeamAB[AIID].playerTeam; 
		bullet.sprite = YoloEngine.TeamAB[AIID].fireSprite;
		bullet.x_texture = 0f;
		bullet.y_texture = 0f;
		bullet.count = YoloEngine.TeamAB[AIID].fireCount;
		bullet.isLeft = YoloEngine.TeamAB[AIID].isPlayerLeft;
		bullet.Aim = YoloEngine.TeamAB[AIID].aim;
		bullet.playerID = AIID;
		Weapontab.add(bullet);
		
		float VolumeScale =1,lx = Math.abs(bullet.x-YoloEngine.TeamAB[YoloEngine.MyID].x),ly =Math.abs(bullet.y-YoloEngine.TeamAB[YoloEngine.MyID].y);
		if(lx>1/YoloEngine.TEXTURE_SIZE_X || ly>1/YoloEngine.TEXTURE_SIZE_Y )
			VolumeScale =0;
		else
		{
			VolumeScale = Math.min(1f-(lx/(1/YoloEngine.TEXTURE_SIZE_X )),1f-(ly/(1/YoloEngine.TEXTURE_SIZE_Y )));
		}
		YoloEngine.sp.play(YoloEngine.SoundInd[0], YoloEngine.Volume*VolumeScale, YoloEngine.Volume*VolumeScale, 1, 0, 1f);
		
		if(YoloEngine.mGameProperties.gameType != GameProperties.OFFLINE)
			YoloEngine.mMultislayer.sendOpponentFire(bullet.x, bullet.y, YoloEngine.TeamAB[AIID].isPlayerLeft, YoloEngine.TeamAB[AIID].isCrouch, 0, YoloEngine.TeamAB[AIID].fireCount,
					YoloEngine.TeamAB[AIID].fireDamage, YoloEngine.TeamAB[AIID].playerTeam,YoloEngine.TeamAB[AIID].aim, YoloEngine.TeamAB[AIID].poiseDamage);
	}

	private boolean isTargetInStraightLineOfFire(int AIID, float x1, float y1)
	{
		/*
		int j = 0;
		float[] tab = new float[8], aim = new float[4];
		tab[0] = x1 - YoloEngine.TeamAB[AIID].x;
		tab[1] = y1 - YoloEngine.TeamAB[AIID].y;
		tab[2] = tab[0] + 1;
		tab[3] = tab[1];
		tab[4] = tab[2];
		tab[5] = tab[1] + 1;
		tab[6] = tab[0];
		tab[7] = tab[5];
		float degree;
		for(int i = 0; i < 8; i+=2)
		{
			if(tab[i] != 0)
			degree = tab[i + 1]/tab[i];
			else
			degree = 3;
		
			if(tab[i + 1]!=0 && tab[i] != 0)
			if( tab[i + 1] >= 0)
			{
				if(degree > 1)
					aim[j] = 1;
				else if(degree > 0)
					
					aim[j] = 0;
				else if(degree > -1)
					aim[j] = 3;
				else
					aim[j] = 2;
			}
			else
			{
				if(degree > 1)
					aim[j] = 5;
				else if(degree > 0)
					aim[j] = 4;
				else if(degree > -1)
					aim[j] = 7;
				else
					aim[j] = 6;
			}		
			j++;
		}
		
		if(aim[0] == aim[1])
			if(aim[1] == aim[2])
				if(aim[2] == aim[3])
				{
					// nie jest w lini prostej
					return false;
				}
		
		// jest w lini prostej;
		 */
		float x = x1 - YoloEngine.TeamAB[AIID].x ,y = y1 - YoloEngine.TeamAB[AIID].y;
		
		if(y < 2.4f && y > 0.75f) // po prostej ale trzeba skoczyc 
			return false;
		if(y > 0)
			if((Math.abs(y-x) < 2.4f && Math.abs(y-x) > 1.4f)|| ( Math.abs(y+x) < 2.4f && Math.abs(y+x) > 1.4f ) ) // 
				return false;
		
		return true;
	}

	private boolean isTargetInJumpHorizontalLineOfFire(int AIID, float y1)
	{
		float y = y1 - YoloEngine.TeamAB[AIID].y;
		
		if(y < 2.4f && y > 0.75f)
			return true;
		
		return false;
	}

	private void fireAt(int AIID,int difficulty, boolean toAI, float x1, float y1)
	{
		switch(difficulty)
		{
			case 2:
			if(!toAI && !isTargetInStraightLineOfFire(AIID,x1,y1) )
				if(YoloEngine.TeamAB[AIID].vy <= -0.14f || YoloEngine.TeamAB[AIID].onGround)
				{
					YoloEngine.TeamAB[AIID].shouldCalculateRode = false;
					if(YoloEngine.TeamAB[AIID].jumps++ < 2)
						YoloEngine.TeamAB[AIID].vy = 0.24f;
				}
		break;
			case 1:
			if(!toAI && isTargetInJumpHorizontalLineOfFire(AIID,y1))
				if(YoloEngine.TeamAB[AIID].vy <= -0.14f || YoloEngine.TeamAB[AIID].onGround)
				{
					YoloEngine.TeamAB[AIID].shouldCalculateRode = false;
					if(YoloEngine.TeamAB[AIID].jumps++ < 2)
						YoloEngine.TeamAB[AIID].vy = 0.24f;
				}
		break;
		}
		
		float x = x1 - YoloEngine.TeamAB[AIID].x, y = y1 - YoloEngine.TeamAB[AIID].y + 0.000001f ,degree;
		
		if(x != 0 )
			degree = y/x;
		else
			degree = 3;
		
		//if(y!=0 && x != 0)
		if( y >= 0)
		{
			if(degree < -2.4142)
				YoloEngine.TeamAB[AIID].aim = 2;
			else if(degree < -0.4142)
			{
				YoloEngine.TeamAB[AIID].aim = 3;
				YoloEngine.TeamAB[AIID].isPlayerLeft = true;
			}
			else if(degree < -0)
			{
				YoloEngine.TeamAB[AIID].aim = 4;
				YoloEngine.TeamAB[AIID].isPlayerLeft = true;
			}
			else if(degree < 0.4142)
			{
				YoloEngine.TeamAB[AIID].aim = 0;
				YoloEngine.TeamAB[AIID].isPlayerLeft = false;
			}
			else if(degree < 2.4142)
			{
				YoloEngine.TeamAB[AIID].aim = 1;
				YoloEngine.TeamAB[AIID].isPlayerLeft = false;
			}
			else
				YoloEngine.TeamAB[AIID].aim = 2;
		}
		else
		{
			if(degree < -2.4142)
				YoloEngine.TeamAB[AIID].aim = 6;
			else if(degree < -0.4142)
			{
				YoloEngine.TeamAB[AIID].aim = 7;
				YoloEngine.TeamAB[AIID].isPlayerLeft = false;
			}
			else if(degree <= 0)
			{
				YoloEngine.TeamAB[AIID].aim = 0;
				YoloEngine.TeamAB[AIID].isPlayerLeft = false;
			}
			else if(degree < 0.4142)
			{
				YoloEngine.TeamAB[AIID].aim = 4;
				YoloEngine.TeamAB[AIID].isPlayerLeft = true;
			}
			else if(degree < 2.4142)
			{
				YoloEngine.TeamAB[AIID].aim = 5;
				YoloEngine.TeamAB[AIID].isPlayerLeft = true;
			}
			else
				YoloEngine.TeamAB[AIID].aim = 6;
		}				
		playerAIFire(AIID);
	}

	private void useOffensiveSkillAt(int AIID, float x, float y, Random rng)
	{
		if(YoloEngine.TeamAB[AIID].difficulty == 0)
		{
			x += (rng.nextFloat() -.5f)*2f * YoloEngine.skillXDispersion0;
			y += (rng.nextFloat() -.5f)*2f * YoloEngine.skillYDispersion0;
		}
		else if(YoloEngine.TeamAB[AIID].difficulty == 1)
		{
			x += (rng.nextFloat() -.5f)*2f * YoloEngine.skillXDispersion1;
			y += (rng.nextFloat() -.5f)*2f * YoloEngine.skillYDispersion1;
		}
		Skill newSkill = new Skill(x,y,YoloEngine.TeamAB[AIID].skill1,YoloEngine.TeamAB[AIID].playerTeam,AIID);
		if(YoloEngine.TeamAB[AIID].playerTeam==YoloEngine.TeamA)
			YoloGameRenderer.skillTeamAVe.add(newSkill);
		else
			YoloGameRenderer.skillTeamBVe.add(newSkill);
	}

	private void useDefensiveSkill(int AIID, float x, float y)
	{
		Skill newSkill = new Skill(x,y,YoloEngine.TeamAB[AIID].skill2,YoloEngine.TeamAB[AIID].playerTeam,AIID);
		if(YoloEngine.TeamAB[AIID].playerTeam==YoloEngine.TeamA)
			YoloGameRenderer.skillTeamAVe.add(newSkill);
		else
			YoloGameRenderer.skillTeamBVe.add(newSkill);
	}

	private void useBuffSkill(int AIID)
	{
		Skill newSkill = new Skill(YoloEngine.TeamAB[AIID].x,YoloEngine.TeamAB[AIID].y,YoloEngine.TeamAB[AIID].skill3,YoloEngine.TeamAB[AIID].playerTeam,AIID);
		if(YoloEngine.TeamAB[AIID].playerTeam==YoloEngine.TeamA)
			YoloGameRenderer.skillTeamAVe.add(newSkill);
		else
			YoloGameRenderer.skillTeamBVe.add(newSkill);
	}
	
	private int whereToDash(int AIID)
	{
		for(YoloWeapon b : Weapontab)
		{
			if(b.team != YoloEngine.TeamAB[AIID].playerTeam)
			if(b.isLeft)
			{
				if(b.x - YoloEngine.TeamAB[AIID].x > 0 && b.x - YoloEngine.TeamAB[AIID].x < 2f)
					if(b.y - YoloEngine.TeamAB[AIID].y > -.5f && b.y - YoloEngine.TeamAB[AIID].y < 1.5f)
						return -1;// w lewo
			}
			else
				if(b.x - YoloEngine.TeamAB[AIID].x > -1f && b.x - YoloEngine.TeamAB[AIID].x < 0)
					if(b.y - YoloEngine.TeamAB[AIID].y > -.5f && b.y - YoloEngine.TeamAB[AIID].y < 1.5f)
						return 1; // w prawo
		}
		
		return 0;// niema nic w zasiegu 
	}
	
	private void manageYourAI()
	{	
		int firePlayerID,skillPlayerID;
		Random rng = new Random();
		for(int i : YoloEngine.AIToManage)
		{
			//System.out.println("currNode: "+YoloEngine.TeamAB[i].currentNode +" currAcction: "+YoloEngine.TeamAB[i].acction+" targetNode: "+YoloEngine.TeamAB[i].NodesToGo.elementAt(0)+" nextNode: "+YoloEngine.TeamAB[i].NodesToGo.lastElement()+ " checkTNode: " + checkTargetNode(i)+ " checkTCNode: " + checkTargetClostestNode(i) + " shouldCalRode: "+YoloEngine.TeamAB[i].shouldCalculateRode+ " AIvy: "+YoloEngine.TeamAB[i].vy);
			//for(int j = 0;j<YoloEngine.TeamAB[i].NodesToGo.size();j++)
			//	System.out.println("      "+YoloEngine.TeamAB[i].NodesToGo.elementAt(j));
			//------------------------------------DEATH CHECK-----------------------------------------------
			
			if(YoloEngine.TeamAB[i].PlayerLive < 0 || YoloEngine.TeamAB[i].y < 0 )
			{
				YoloEngine.TeamAB[i].canMove = false;
				YoloEngine.TeamAB[i].deathCount++;
				
				if(YoloEngine.TeamAB[i].deathIntervalCounter++ >= YoloEngine.deathSpanInterval)
				{
					if(YoloEngine.TeamAB[i].playerTeam)
						YoloEngine.TeamAB[i].x = YoloEngine.LEVEL_X/YoloEngine.TX -1;
					else
						YoloEngine.TeamAB[i].x = 2;
					YoloEngine.TeamAB[i].y = 2.5f;
					YoloEngine.TeamAB[i].PlayerLive = YoloEngine.TeamAB[i].PLAYER_LIVE_MAX;
					YoloEngine.TeamAB[i].canMove = true;
					YoloEngine.TeamAB[i].deathIntervalCounter =0;
				}
			}
			//---------------------------Walk------------------------------
			if(YoloEngine.TeamAB[i].canMove)
			{
				if(!YoloEngine.TeamAB[i].isClimbingUp)// jesli sie nie wspina to licz grawitacje
				{
				YoloEngine.TeamAB[i].x += YoloEngine.TeamAB[i].vx;
				YoloEngine.TeamAB[i].y += YoloEngine.TeamAB[i].vy;
				YoloEngine.TeamAB[i].vy -= YoloEngine.GAME_ACCELERATION;
				
					for(int j = 0; j < ObjectTab.length; j++)
					{
						if(IsCollidedTop(YoloEngine.TeamAB[i],ObjectTab[j]))
						{
							if(YoloEngine.TeamAB[i].vy > 0)
								{
									YoloEngine.TeamAB[i].onGround = false;
								}
							else
								{
									//YoloGame.flying = YoloEngine.TeamAB[i].isPlayerFlying?10:2;
									YoloEngine.TeamAB[i].y = ObjectTab[j].y + ObjectTab[j].dy;
									YoloEngine.TeamAB[i].vy = 0;
									YoloEngine.TeamAB[i].platformOn = j;
									YoloEngine.TeamAB[i].shouldCalculateRode = true;
									YoloEngine.TeamAB[i].onGround = true;
									YoloEngine.TeamAB[i].jumps =0;
									
								}
							break;
						}
						YoloEngine.TeamAB[i].onGround = false;
					}
					if(IsCollidedTop(YoloEngine.TeamAB[i],ObjectTab[platformOn]))
					{
						if(YoloEngine.TeamAB[i].contact)
						{
							YoloEngine.TeamAB[i].contact = false;
							YoloEngine.sp.play(YoloEngine.SoundInd[5], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
							YoloEngine.TeamAB[i].setAction(7);
						}
					}
					else YoloEngine.TeamAB[i].contact = true;
				}
				YoloEngine.TeamAB[i].currentNode = checkAINodeMiddle(i);//sprawdza noda 
				
				if(YoloEngine.TeamAB[i].AICheckInterval-- <= 0 && !YoloEngine.TeamAB[i].isClimbingUp)
				{
					if(YoloEngine.TeamAB[i].shouldCalculateRode)
					if(YoloEngine.TeamAB[i].currentNode > -1)
					{
						if(checkTargetChange(i))
						{
							//jest bli�szy cel
							int NID = checkTargetNode(i);
							if(NID > -1)
								findShortestRode(i,NID);//YoloEngine.testNode
							else
								findShortestRode(i,checkTargetClostestNode(i));//YoloEngine.testNode
						}
						else if(checkTargetChangeNode(i))
						{
							//cel zmieni� noda
							if(YoloEngine.TeamAB[i].NodesToGo.elementAt(0) > -1)
								findShortestRode(i,YoloEngine.TeamAB[i].NodesToGo.elementAt(0));//YoloEngine.testNode
							else
								findShortestRode(i,checkTargetClostestNode(i));//YoloEngine.testNode
						}
						
						switch(YoloEngine.TeamAB[i].difficulty)
						{
							case 0:
							YoloEngine.TeamAB[i].AICheckInterval = YoloEngine.AICheckInterval0;
						break;
							case 1:
							YoloEngine.TeamAB[i].AICheckInterval = YoloEngine.AICheckInterval1;
						break;
							case 2:
							YoloEngine.TeamAB[i].AICheckInterval = YoloEngine.AICheckInterval2;
						break;
						}
					}
					/*else if(YoloEngine.TeamAB[i].NodesToGo.size() < 2)// jesli skonczyl droge a jest poza nodem to niech mu przypisze najblizszy
					{
						System.out.println("liczy bo jest poza");
						//YoloEngine.TeamAB[i].currentNode = checkAIClosestNode(i);
					}*/
				}
				
				if(YoloEngine.TeamAB[i].NodesToGo.size() > 1)
				{
					//sprawdza czy kolejny node to drabina jesli tak to wykrywanie nodow jest poszerzoen o .5 do gory
					if(YoloEngine.TeamAB[i].nextAcction == acction.ladderDown)
						YoloEngine.TeamAB[i].currentNode = checkAINodeTranslate(i);
					
					if(YoloEngine.TeamAB[i].currentNode == YoloEngine.TeamAB[i].NodesToGo.lastElement())
					{
						//jest w aktualnym nodzie
						makeInsideMove(i,YoloEngine.TeamAB[i].acction);
						if(YoloEngine.TeamAB[i].isClimbingUp)// wlaczenie grawitacji jesli doszed do konca drabiny
						{
							YoloEngine.TeamAB[i].isClimbingUp = false;
							YoloEngine.TeamAB[i].vy = 0;
						}
					}
					else if(YoloEngine.TeamAB[i].currentNode == YoloEngine.TeamAB[i].NodesToGo.elementAt(YoloEngine.TeamAB[i].NodesToGo.size() -2))
					{
						//jest w nast�pnym nodzie
						//if(YoloEngine.TeamAB[i].acction != acction.jumpLeft || YoloEngine.TeamAB[i].acction != acction.jumpRight || YoloEngine.TeamAB[i].onGround)
							
						YoloEngine.TeamAB[i].NodesToGo.remove(YoloEngine.TeamAB[i].NodesToGo.size() -1);
						YoloEngine.TeamAB[i].jumps =0;
						if(YoloEngine.TeamAB[i].NodesToGo.size() > 1)
						{
							YoloEngine.TeamAB[i].acction = YoloEngine.TeamAB[i].nextAcction;
							//YoloEngine.TeamAB[i].acction = indexToAcction(i, YoloEngine.map.IDtoIndex[YoloEngine.TeamAB[i].NodesToGo.elementAt(YoloEngine.TeamAB[i].NodesToGo.size() -2)]);
							//jesli istnieje kolejne acction to jaki jest
							if(YoloEngine.TeamAB[i].NodesToGo.size() > 2)
								YoloEngine.TeamAB[i].nextAcction = indexToAcction(i, YoloEngine.map.IDtoIndex[YoloEngine.TeamAB[i].NodesToGo.elementAt(YoloEngine.TeamAB[i].NodesToGo.size() -3)],
										YoloEngine.TeamAB[i].NodesToGo.elementAt(YoloEngine.TeamAB[i].NodesToGo.size() -2));
							
							makeInsideMove(i,YoloEngine.TeamAB[i].acction);
							
							// jesli sie wspina to go przytula do drabniy(nie ma zadnego powiazania miedzy nodami a drabinami dlatego wyszujuje najblzsza)
							if(YoloEngine.TeamAB[i].acction == acction.ladderUP || YoloEngine.TeamAB[i].acction == acction.ladderDown)
							{
								for(int q=0;q<YoloGameRenderer.LaddreTab.length;q++)
								{
									if(YoloEngine.TeamAB[i].x + YoloEngine.xRadiusLadder/2 + .5f> YoloGameRenderer.LaddreTab[q].x &&
											YoloEngine.TeamAB[i].x - YoloEngine.xRadiusLadder/2 +.5f < YoloGameRenderer.LaddreTab[q].x + YoloGameRenderer.LaddreTab[q].dx)
									{
										if(YoloEngine.TeamAB[i].y + YoloEngine.yRadiusLadder/2 +.5f> YoloGameRenderer.LaddreTab[q].y && 
												YoloEngine.TeamAB[i].y - YoloEngine.yRadiusLadder/2 + .5f < YoloGameRenderer.LaddreTab[q].y + YoloGameRenderer.LaddreTab[q].dy)
										{
											if(YoloEngine.TeamAB[i].y > YoloGameRenderer.LaddreTab[q].y + YoloGameRenderer.LaddreTab[q].dy/2)
											{	
												YoloEngine.TeamAB[i].isClimbingDown = true;
												YoloEngine.TeamAB[i].x = YoloGameRenderer.LaddreTab[q].x + 0.01f;
												break;
											}
											else
											{
												YoloEngine.TeamAB[i].isClimbingUp = true;
												YoloEngine.TeamAB[i].x = YoloGameRenderer.LaddreTab[q].x + 0.01f;
												break;
											}
										}
									}
								}
							}
								
							if(YoloEngine.TeamAB[i].isClimbingUp)// wlaczenie grawitacji jesli doszed do konca drabiny
							{
								YoloEngine.TeamAB[i].isClimbingUp = false;
								YoloEngine.TeamAB[i].vy = 0;
							}
						}
						
							
					}
					else if(YoloEngine.TeamAB[i].currentNode == -1)
					{
						//jest poza nodami
							makeOutsideMove(i,YoloEngine.TeamAB[i].acction);
					}
					else
					{
						//zboczyl z drogi
						if(!YoloEngine.TeamAB[i].isClimbingUp)
							findShortestRode(i,YoloEngine.TeamAB[i].NodesToGo.elementAt(0)); //YoloEngine.testNode
						else
							makeOutsideMove(i,YoloEngine.TeamAB[i].acction);
					}
					// attak na SkillAI----------------------------------
					if(YoloEngine.TeamAB[i].difficulty == 2)
					{
						//szuka SkillAI do strza�u lub skilla
						if(YoloEngine.TeamAB[i].nextBullet-- <=0 && YoloEngine.TeamAB[i].playerMag > 0)
						{
							int indexSkillaFire = searchForSkillAIInFireRadius(i);
							if(indexSkillaFire > -1)
							{
								fireAt(i,2,true,skillTeamAVe.elementAt(indexSkillaFire).x,skillTeamAVe.elementAt(indexSkillaFire).y);
								YoloEngine.TeamAB[i].nextBullet = YoloEngine.TeamAB[i].firePause;
								YoloEngine.TeamAB[i].playerMag--;
							}
						}
						if(YoloEngine.TeamAB[i].playerMag == 0)
						{
							if(YoloEngine.TeamAB[i].reloading == 0)
									YoloEngine.sp.play(YoloEngine.SoundInd[11], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
							
								if(YoloEngine.TeamAB[i].reloading++ == YoloEngine.TeamAB[i].playerMagReloadTime)
								{
									YoloEngine.TeamAB[i].reloading =0;
									YoloEngine.TeamAB[i].playerMag = YoloEngine.TeamAB[i].PlayerMagCapasity;
									YoloEngine.sp.play(YoloEngine.SoundInd[12], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
								}
						}
						if(YoloEngine.TeamAB[i].skill1cooldown-- <= 0)
						{
							int indexSkillaSkill = searchForSkillAIInSkillRadius(i);
							if(indexSkillaSkill > -1)
							{
								useOffensiveSkillAt(i,skillTeamAVe.elementAt(indexSkillaSkill).x,skillTeamAVe.elementAt(indexSkillaSkill).y,rng);
								YoloEngine.TeamAB[i].skill1cooldown = YoloEngine.TeamAB[i].skill1OrginalCooldown;
							}
						}
					}
					else if(YoloEngine.TeamAB[i].difficulty == 1)
					{
						//szuka SkillAI do strza�u 
						if(YoloEngine.TeamAB[i].nextBullet-- <=0 && YoloEngine.TeamAB[i].playerMag > 0)
						{
							int indexSkilla = searchForSkillAIInFireRadius(i);
							if(indexSkilla > -1)
							{
								fireAt(i,1,true,skillTeamAVe.elementAt(indexSkilla).x,skillTeamAVe.elementAt(indexSkilla).y);
								YoloEngine.TeamAB[i].nextBullet = YoloEngine.TeamAB[i].firePause;
								YoloEngine.TeamAB[i].playerMag--;
							}
						}
						if(YoloEngine.TeamAB[i].playerMag == 0)
						{
							if(YoloEngine.TeamAB[i].reloading == 0)
									YoloEngine.sp.play(YoloEngine.SoundInd[11], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
							
								if(YoloEngine.TeamAB[i].reloading++ == YoloEngine.TeamAB[i].playerMagReloadTime)
								{
									YoloEngine.TeamAB[i].reloading =0;
									YoloEngine.TeamAB[i].playerMag = YoloEngine.TeamAB[i].PlayerMagCapasity;
									YoloEngine.sp.play(YoloEngine.SoundInd[12], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
								}
						}
					}
					//---------------------------------------------------
				}
				else if(YoloEngine.TeamAB[i].currentNode == YoloEngine.TeamAB[i].NodesToGo.firstElement())
				{
					//jest w tym samym nodzie co cel
					if(YoloEngine.TeamAB[i].acction != acction.stand)
						YoloEngine.TeamAB[i].acction = acction.stand;
					folowTarget(i);
					if(YoloEngine.TeamAB[i].isClimbingUp)
					{
						YoloEngine.TeamAB[i].isClimbingUp = false;
						YoloEngine.TeamAB[i].vy = 0;
					}
				}
				else 
				{
					//powinien byc w tym samym co gracz ale nie jest
					if(YoloEngine.TeamAB[i].shouldCalculateRode)
					{
						int pomi = checkAIClosestNode(i);
						if(YoloEngine.TeamAB[i].y + .5f < YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[pomi]].y0)
							makeOutsideMove(i, acction.jump);
						if(YoloEngine.TeamAB[i].x + .5f < (YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[pomi]].x0 + YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[pomi]].x0)/2f)
							YoloEngine.TeamAB[i].x += 0.08f;
						else
							YoloEngine.TeamAB[i].x -= 0.08f;
						YoloEngine.TeamAB[i].currentNode = checkAINodeMiddle(i);
						if( YoloEngine.TeamAB[i].currentNode > -1)
							findShortestRode(i, checkTargetClostestNode(i));
					}
				}
				if(YoloEngine.TeamAB[i].y > YoloEngine.LEVEL_Y/YoloEngine.TY)
				{
					if(YoloEngine.TeamAB[i].outOfBoundCounter-- < 0)
					{
						int pomi = checkAIClosestNode(i);
						if(YoloEngine.TeamAB[i].y + .5f < YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[pomi]].y0)
							makeOutsideMove(i, acction.jump);
						if(YoloEngine.TeamAB[i].x + .5f < (YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[pomi]].x0 + YoloEngine.map.nodes[YoloEngine.map.IDtoIndex[pomi]].x0)/2f)
							YoloEngine.TeamAB[i].x += 0.08f;
						else
							YoloEngine.TeamAB[i].x -= 0.08f;
						YoloEngine.TeamAB[i].currentNode = checkAINodeMiddle(i);
						if( YoloEngine.TeamAB[i].currentNode > -1)
							findShortestRode(i, checkTargetClostestNode(i));
						
						YoloEngine.TeamAB[i].outOfBoundCounter = YoloEngine.outOfBoundInterval;
					}
				}
				else
					YoloEngine.TeamAB[i].outOfBoundCounter = YoloEngine.outOfBoundInterval;
				
				//--------------------------Offensive--------------------------
				
				if(YoloEngine.TeamAB[i].nextBullet-- <=0 && YoloEngine.TeamAB[i].playerMag > 0)
				{
					firePlayerID = searchForPlayerInFireRadius(i);
					if(firePlayerID > -1)
					{
						fireAt(i,YoloEngine.TeamAB[i].difficulty,false,YoloEngine.TeamAB[firePlayerID].x,YoloEngine.TeamAB[firePlayerID].y);
						YoloEngine.TeamAB[i].nextBullet = YoloEngine.TeamAB[i].firePause;
						YoloEngine.TeamAB[i].playerMag--;
					}
				}
				
				if(YoloEngine.TeamAB[i].playerMag == 0)
				{
					if(YoloEngine.TeamAB[i].reloading == 0)
							YoloEngine.sp.play(YoloEngine.SoundInd[11], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
					
						if(YoloEngine.TeamAB[i].reloading++ == YoloEngine.TeamAB[i].playerMagReloadTime)
						{
							YoloEngine.TeamAB[i].reloading =0;
							YoloEngine.TeamAB[i].playerMag = YoloEngine.TeamAB[i].PlayerMagCapasity;
							YoloEngine.sp.play(YoloEngine.SoundInd[12], YoloEngine.Volume, YoloEngine.Volume, 1, 0, 1f);
						}
				}
				if(YoloEngine.TeamAB[i].skill1cooldown-- <= 0)
				{
					skillPlayerID = searchForPlayerInSkillRadius(i);
					if(skillPlayerID > -1)
					{
						useOffensiveSkillAt(i,YoloEngine.TeamAB[skillPlayerID].x + .5f,YoloEngine.TeamAB[skillPlayerID].y + .5f,rng);
						YoloEngine.TeamAB[i].skill1cooldown = YoloEngine.TeamAB[i].skill1OrginalCooldown;
					}
				}
				
				//--------------------------Deffensive-------------------------
				if(YoloEngine.TeamAB[i].targetDistance <= YoloEngine.deffensiveDistance)
				{
					if(YoloEngine.TeamAB[i].deffensiveCooldown-- <= 0)
						if(YoloEngine.TeamAB[i].skill2cooldown-- <= 0)
						{
							useDefensiveSkill(i,YoloEngine.TeamAB[YoloEngine.TeamAB[i].currentTrackedPlayerID].x, YoloEngine.TeamAB[YoloEngine.TeamAB[i].currentTrackedPlayerID].y);
							switch(YoloEngine.TeamAB[i].difficulty)
							{
								case 0:
								YoloEngine.TeamAB[i].deffensiveCooldown = 120;
							break;
								case 1:
								YoloEngine.TeamAB[i].deffensiveCooldown = 30;
							break;
								case 2:
								YoloEngine.TeamAB[i].deffensiveCooldown = 0;
							break;
							}
							YoloEngine.TeamAB[i].skill2cooldown = YoloEngine.TeamAB[i].skill2OrginalCooldown;
						}
					//YoloEngine.TeamAB[i].skill2cooldown--;
				}
				
				//----------------------------Buff-----------------------------
				if(YoloEngine.TeamAB[i].buffCooldown-- <= 0)
					if(YoloEngine.TeamAB[i].skill3cooldown-- <=0)
					{
						useBuffSkill(i);
							switch(YoloEngine.TeamAB[i].difficulty)
							{
								case 0:
								YoloEngine.TeamAB[i].buffCooldown = (int) (rng.nextFloat()*120);
							break;
								case 1:
								YoloEngine.TeamAB[i].buffCooldown = (int) (rng.nextFloat()*30);
							break;
								case 2:
								YoloEngine.TeamAB[i].buffCooldown = 0;
							break;
							}
							YoloEngine.TeamAB[i].skill3cooldown = YoloEngine.TeamAB[i].skill3OrginalCooldown;
					}
				//YoloEngine.TeamAB[i].skill3cooldown--;
				
				//----------------------------Dodge----------------------------
				if(YoloEngine.TeamAB[i].dashIntervalCounter-- < 0)
				{
					switch(whereToDash(i))
					{
					case -1:
						YoloEngine.TeamAB[i].shouldCalculateRode = false;
						YoloEngine.TeamAB[i].vx = 0.32f;
						YoloEngine.TeamAB[i].dashDuration = 10;
						YoloEngine.TeamAB[i].isPlayerInvincible = true;
						YoloEngine.TeamAB[i].invice = 10;
						
						switch(YoloEngine.AIDificulty)
						{
						case 0:
							YoloEngine.TeamAB[i].dashIntervalCounter = YoloEngine.dashInterval0;
							break;
						case 1:
							YoloEngine.TeamAB[i].dashIntervalCounter = YoloEngine.dashInterval1;
							break;
						case 2:
							YoloEngine.TeamAB[i].dashIntervalCounter = YoloEngine.dashInterval2;
							break;
						}
						break;
					case 1:
						YoloEngine.TeamAB[i].shouldCalculateRode = false;
						YoloEngine.TeamAB[i].vx = -0.32f;
						YoloEngine.TeamAB[i].dashDuration = 10;
						YoloEngine.TeamAB[i].isPlayerInvincible = true;
						YoloEngine.TeamAB[i].invice = 10;
						
						switch(YoloEngine.AIDificulty)
						{
						case 0:
							YoloEngine.TeamAB[i].dashIntervalCounter = YoloEngine.dashInterval0;
							break;
						case 1:
							YoloEngine.TeamAB[i].dashIntervalCounter = YoloEngine.dashInterval1;
							break;
						case 2:
							YoloEngine.TeamAB[i].dashIntervalCounter = YoloEngine.dashInterval2;
							break;
						}
						break;	
					}		
				}
				//-------------------------------------------------------------
			}
		}
	}
	
	public static void weaponSelect()
	{
		//TODO load weapon statistics
		switch(YoloEngine.TeamAB[YoloEngine.MyID].weapon)
		{
		case 0:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .25f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = 0f;
			YoloEngine.TeamAB[YoloEngine.MyID].firePause = 20;
			YoloEngine.TeamAB[YoloEngine.MyID].fireDamage = 1;
			YoloEngine.TeamAB[YoloEngine.MyID].playerMagReloadTime = 100;
			YoloEngine.TeamAB[YoloEngine.MyID].PlayerMagCapasity = 19;
			break;
		case 1:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .375f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = 0f;
			YoloEngine.TeamAB[YoloEngine.MyID].firePause = 20;
			YoloEngine.TeamAB[YoloEngine.MyID].fireDamage = 1.5f;
			YoloEngine.TeamAB[YoloEngine.MyID].playerMagReloadTime = 100;
			YoloEngine.TeamAB[YoloEngine.MyID].PlayerMagCapasity = 17;
			break;
		case 2:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .5f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = 0f;
			YoloEngine.TeamAB[YoloEngine.MyID].firePause = 30;
			YoloEngine.TeamAB[YoloEngine.MyID].fireDamage = 2.5f;
			YoloEngine.TeamAB[YoloEngine.MyID].playerMagReloadTime = 60;
			YoloEngine.TeamAB[YoloEngine.MyID].PlayerMagCapasity = 8;
			break;
		case 4:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .625f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = 0f;
			YoloEngine.TeamAB[YoloEngine.MyID].firePause = 35;
			YoloEngine.TeamAB[YoloEngine.MyID].fireDamage = 4f;
			YoloEngine.TeamAB[YoloEngine.MyID].playerMagReloadTime =70;
			YoloEngine.TeamAB[YoloEngine.MyID].PlayerMagCapasity = 5;
			break;
		case 5:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .75f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = 0f;
			YoloEngine.TeamAB[YoloEngine.MyID].firePause = 30;
			YoloEngine.TeamAB[YoloEngine.MyID].fireDamage = 4.5f;
			YoloEngine.TeamAB[YoloEngine.MyID].playerMagReloadTime = 100;
			YoloEngine.TeamAB[YoloEngine.MyID].PlayerMagCapasity = 5;
			break;
		case 6:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .875f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = 0f;
			YoloEngine.TeamAB[YoloEngine.MyID].firePause = 15;
			YoloEngine.TeamAB[YoloEngine.MyID].fireDamage = 3.5f;
			YoloEngine.TeamAB[YoloEngine.MyID].playerMagReloadTime = 100;
			YoloEngine.TeamAB[YoloEngine.MyID].PlayerMagCapasity = 15;
			break;
		case 7:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .0f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = .125f;
			YoloEngine.TeamAB[YoloEngine.MyID].firePause = 12;
			YoloEngine.TeamAB[YoloEngine.MyID].fireDamage = 1;
			YoloEngine.TeamAB[YoloEngine.MyID].playerMagReloadTime = 60;
			YoloEngine.TeamAB[YoloEngine.MyID].PlayerMagCapasity = 20;
			break;
		case 8:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .125f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = .125f;
			YoloEngine.TeamAB[YoloEngine.MyID].firePause = 10;
			YoloEngine.TeamAB[YoloEngine.MyID].fireDamage = 1.7f;
			YoloEngine.TeamAB[YoloEngine.MyID].playerMagReloadTime = 50;
			YoloEngine.TeamAB[YoloEngine.MyID].PlayerMagCapasity = 30;
			break;
		case 9:
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureX = .25f;
			YoloEngine.TeamAB[YoloEngine.MyID].weaponTextureY = .125f;
			YoloEngine.TeamAB[YoloEngine.MyID].firePause = 20;
			YoloEngine.TeamAB[YoloEngine.MyID].fireDamage = 7;
			YoloEngine.TeamAB[YoloEngine.MyID].playerMagReloadTime = 100;
			YoloEngine.TeamAB[YoloEngine.MyID].PlayerMagCapasity = 12;
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
		
//------------------------------------------INICJOWANIE OBIEKTï¿½W FIZYCZNYCH----------------------------------		
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
			ObjectTab = new YoloObject[17];
			
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
	
		
//----------------------------------------------------AI STUFF--------------------------------------------------------
		YoloEngine.map = new Map(R.raw.node3);
		YoloEngine.AIToManage = new int[YoloEngine.AICount];
		YoloEngine.AIToManage[0] = 3;
		initAI(3);
		//for(int i = 0;) inicjowaniae AI w odpowiednim miejscu
//--------------------------------------------------------------------------------------------------------------------
		
		YoloEngine.SKILL1_COOLDOWN = (int)(YoloEngine.cooldownsTab[YoloEngine.SkillSprite1] * YoloEngine.Skill1ButtonCooldownMultiplay);
		YoloEngine.SKILL2_COOLDOWN = (int)(YoloEngine.cooldownsTab[YoloEngine.SkillSprite2] * YoloEngine.Skill2ButtonCooldownMultiplay);
		YoloEngine.SKILL3_COOLDOWN = (int)(YoloEngine.cooldownsTab[YoloEngine.SkillSprite3] * YoloEngine.Skill3ButtonCooldownMultiplay);
		
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
	public void clear()
	{
		
		Weapontab.clear();
		skillTeamBVe.clear();
		skillTeamAVe.clear();
		PowerUPtab.clear();
		hitBoxs.clear();
		particleObjectTabMy.clear();
		particleObjectTabOp.clear();
		
		toLoad = true;
		first = false;
		onGround = true;
		contact = true;
		
		YoloEngine.TeamAB[YoloEngine.MyID].weapon = YoloEngine.currentPlayerInfo.getWEQ();
	}
}
