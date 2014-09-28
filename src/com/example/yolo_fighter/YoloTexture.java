package com.example.yolo_fighter;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class YoloTexture {

	private int[]textures = new int[6];
	
	public YoloTexture(GL10 gl){
		gl.glGenTextures(6, textures,0);
	}
	
	public int[] loadTexture(GL10 gl, int texture, Context context, int TextureNumber)
	{
		InputStream imagestream = context.getResources().openRawResource(texture);
		Bitmap bitmap = null;
		try
		{
			bitmap = BitmapFactory.decodeStream(imagestream);
		}
		catch (Exception e){	
		}
		finally 
		{
			try
			{
				imagestream.close();
				imagestream = null;
			}
			catch (IOException e){
			}
		}
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[TextureNumber]);
		
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
		bitmap.recycle();
		
		return textures;
		
	}
	
}
