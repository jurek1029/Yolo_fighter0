package com.example.yolo_fighter;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class YoloGameView extends GLSurfaceView{
	
	public static YoloGameRenderer renderer;

	public YoloGameView(Context context) {
		super(context);
		
		renderer = new YoloGameRenderer();
		//this.setEGLConfigChooser(true);
		this.setRenderer(renderer);
		
	}
	
}
