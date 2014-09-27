package com.example.yolo_fighter;

public class YoloObject {
	
	float min_x,max_x;
	float min_y,max_y;
	float dMax_y;
	
	
	YoloObject(float min_x,float min_y,float dMax_x,float dMax_y)
	{
		this.min_x = min_x*YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X/YoloEngine.LEVEL_X;
		this.max_x = (min_x+dMax_x)*YoloEngine.LEVEL_SIZE_X*YoloEngine.GAME_PROJECTION_X/YoloEngine.LEVEL_X;
		
		this.max_y = (YoloEngine.LEVEL_Y-min_y)*YoloEngine.LEVEL_SIZE_Y*YoloEngine.GAME_PROJECTION_Y/YoloEngine.LEVEL_Y;
		this.min_y = (YoloEngine.LEVEL_Y-(min_y+dMax_y))*YoloEngine.LEVEL_SIZE_Y*YoloEngine.GAME_PROJECTION_Y/YoloEngine.LEVEL_Y;
		
		this.dMax_y = dMax_y;

	}

}
