package com.example.yolo_fighter;

public class YoloObject {
	
	float x,dx,px=0;
	float y,vy,dy,py=0;
	
	YoloObject(int x,int y,int dMax_x,int dMax_y)
	{
		/*
		this.min_x = min_x*YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X/YoloEngine.LEVEL_X;
		this.max_x = (min_x+dMax_x)*YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X/YoloEngine.LEVEL_X;
		
		this.max_y = (YoloEngine.LEVEL_Y-min_y)*YoloEngine.LEVEL_SIZE_Y*YoloEngine.GAME_PROJECTION_Y/YoloEngine.LEVEL_Y;
		this.min_y = (YoloEngine.LEVEL_Y-(min_y+dMax_y))*YoloEngine.LEVEL_SIZE_Y*YoloEngine.GAME_PROJECTION_Y/YoloEngine.LEVEL_Y;
		*/
		this.x = x/YoloEngine.TX;
		//this.max_x = (x+dMax_x)/YoloEngine.TX;
		this.y = (YoloEngine.LEVEL_Y-(y+dMax_y))/YoloEngine.TY;
		//this.max_y = (YoloEngine.LEVEL_Y-y)/YoloEngine.TY;
		dx = dMax_x/YoloEngine.TX;
		dy = dMax_y/YoloEngine.TY;
		

	}
	
	YoloObject(float x,float y)
	{
		this.x = x;
		this.y = y;
		dx=1f;
		dy=1f;
	}
	YoloObject(float x,float y,int dx,int dy)
	{
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}
	YoloObject(float x,float y,float dx,float dy,float px,float py)
	{
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.px = px;
		this.py = py;
	}
}
