package com.example.yolo_fighter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class YoloPlayer extends YoloObject {
	
	private static FloatBuffer vertexBuffer;
	private static FloatBuffer textureBuffer;
	private static ByteBuffer indexBuffer;
	
	public boolean isPlayerPoisoned = false;
	public boolean isPlayerSlowDown = false;
	public boolean isPlayerFlying = false;
	public boolean isPlayerDenialed = false;
	public boolean isPlayerInvincible = false;
	public boolean isPlayerDef = false;
	public boolean isShoting = false;
	public boolean isJumping = false;
	public boolean isPlayerLeft = false;
	public boolean isCrouch = false;
	public boolean isUsingSkill = false;
	public boolean isClimbingUp = false;
	public boolean isClimbingDown = false;
	public boolean isMoving = false;
	public boolean onGround = false;
	public boolean canMove = true;
	
	//public float x =3f;
	//public float y =5f;
	
	public boolean playerTeam = false; // 0 - teamA, 1 - teamB
	
	//public float vy =0;
	public float vx = 0f;
	public float PlayerLive = 100;
	public final int PLAYER_BULLET_FREQUENCY = 10; 
	public final float PLAYER_LIVE_MAX = 100;
	public float Player_Dmg_reduction = 1f;
	
	public int poisoned = 0,slowDowned=0,flying =0,defed =0,invice =0,deniled =YoloEngine.denialDuration;
	
	
	private static float vertices[] = {
			0.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f
	};
	
	private static float texture[] = {
			0.0f, 0.0f,
			.125f, 0.0f,
			.125f, .125f,
			0.0f, .125f
	};
	
	private static byte indices[]={
		0, 1, 2,
		0, 2, 3
	};
	public YoloPlayer(float x,float y)
	{
		super(x,y);
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuf.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuf.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);
		
		indexBuffer = ByteBuffer.allocateDirect(indices.length);
		indexBuffer.put(indices);
		indexBuffer.position(0);
	}
	
	public void drawAlly(GL10 gl,boolean livebar)
	{
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
		gl.glTranslatef(x, y-.25f, 0f);
		gl.glColor4f(1f,1f,1f,1f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		if(isCrouch) gl.glTranslatef(0f, 0f, 0f);
		else gl.glTranslatef(0.125f, 0f, 0f);
		gl.glTranslatef(0f, 0f, 0f);
		draw(gl,YoloEngine.spriteSheets,2);
		gl.glPopMatrix();
		gl.glLoadIdentity();
	
		if(livebar)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X*(PlayerLive/PLAYER_LIVE_MAX), YoloEngine.LIFE_BAR_Y, 1f);
			gl.glTranslatef(x/(PlayerLive/PLAYER_LIVE_MAX),y*10+8f, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glColor4f(1f,1f,1f,1f);
			gl.glTranslatef(0.875f, 0.125f, 0); 
			draw(gl,YoloEngine.spriteSheets,1);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
     
		
		if(isPlayerPoisoned)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x, y-.25f, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,2);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		if(isPlayerFlying )
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x, y-.25f, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glColor4f(1f,1f,1f,1f);
			gl.glTranslatef(0.375f,0.875f, 0f);
			draw(gl,YoloEngine.spriteSheets,2);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		if(isPlayerInvincible)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x, y-.25f, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.25f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,2);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		
		if(isPlayerDef )
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x, y-.25f, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.625f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,2);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		
		}
	}
	public void drawOpponent(GL10 gl)
	{
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
		gl.glTranslatef(x, y-.25f, 0f);
		gl.glColor4f(1f,1f,1f,1f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		if(isCrouch) gl.glTranslatef(0f, 0f, 0f);
		else gl.glTranslatef(0.125f, 0f, 0f);
		gl.glTranslatef(0f, 0f, 0f);
		draw(gl,YoloEngine.spriteSheets,3);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glScalef(YoloEngine.TEXTURE_SIZE_X*(PlayerLive/PLAYER_LIVE_MAX), YoloEngine.LIFE_BAR_Y, 1f);
		gl.glTranslatef(x/(PlayerLive/PLAYER_LIVE_MAX),y*10+8f, 0f);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glColor4f(1f,1f,1f,1f);
		gl.glTranslatef(0.875f, 0.125f, 0); 
		draw(gl,YoloEngine.spriteSheets,1);
		gl.glPopMatrix();
		gl.glLoadIdentity();
		
		if(isPlayerPoisoned)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x, y-.25f, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,3);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		if(isPlayerFlying )
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x, y-.25f, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glColor4f(1f,1f,1f,1f);
			gl.glTranslatef(0.375f,0.875f, 0f);
			draw(gl,YoloEngine.spriteSheets,3);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		if(isPlayerInvincible)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x, y-.25f, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.25f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,3);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		}
		
		if(isPlayerDef )
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glScalef(YoloEngine.TEXTURE_SIZE_X, YoloEngine.TEXTURE_SIZE_Y, 1f);
			gl.glTranslatef(x, y-.25f, 0f);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glTranslatef(0.625f,0.875f, 0f);
			gl.glColor4f(1f,1f,1f,1f);
			draw(gl,YoloEngine.spriteSheets,3);
			gl.glPopMatrix();
			gl.glLoadIdentity();
		
		}
	}
	public void draw (GL10 gl, int[] spriteSheet,int number)
	{
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, spriteSheet[number]);
		gl.glFrontFace(GL10.GL_CCW);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}

}
