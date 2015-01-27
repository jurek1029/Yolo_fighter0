package com.example.yolo_fighter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Triangle {

    private FloatBuffer vertexBuffer;

    static final int COORDS_PER_VERTEX = 3;
    float triangleCoords[] ;

    int ile;
    float r;
    float color[] = { 0f, 0f, 0f, 0.3f };

    public Triangle(float x, float y,float dx,float dy,int count, float r) {
    	
    	int a=0,b=0,c=0,d=0,e=0,pg=0,pd=0,ld=0,lg=0;
    	ile =(count/4);
    	this.r = r;
    	
    	double alfa = 2*Math.PI/4f-2*Math.PI/120f;
    	float xs = dx/2f + x
    		 ,ys = dy/2f + y;
    	float x1 = x + dx/2f
    		 ,x2 = x1 + dx/(count/4)
    		 ,y1 = y + dy
    		 ,y2 = y1;
    	float radiousx = dx/(count/4)*r
    		 ,radiousy = dy/(count/4)*r
    		 ,xba=x2,yba=y2-radiousy;
    	triangleCoords = new float[count*9+120*9-8*9*(int)r];
    	
    	for(int i =0;i<count*9-8*9*(int)r+120*9;i++)
    	{
    		triangleCoords[i++]=xs;
    		triangleCoords[i++]=ys;
    		triangleCoords[i++]=0f;
    		//System.out.println(xs+" "+ys+" "+0);
    		triangleCoords[i++]=x1;
    		triangleCoords[i++]=y1;
    		triangleCoords[i++]=0f;
    		//System.out.println(x1+" "+y1+" "+0);
    		triangleCoords[i++]=x2;
    		triangleCoords[i++]=y2;
    		triangleCoords[i]=0f;   		
    		//System.out.println(x2+" "+y2+" "+0);
    		
    		
    		
    		if(a++<ile/2-1-r)
    		{
    			y1 = y2;
    			x1 = x2;
    			x2 += dx/(count/4);
    			xba = x2;
    			yba = y2-radiousy;
    		}
    		else if(pg++<30)
    		{
    			x1 = x2;
    			y1 = y2;
    			x2 = xba + (float)(radiousx*Math.cos(alfa));
    			y2 = yba + (float)(radiousy*Math.sin(alfa));
    			alfa -= 2*Math.PI/120f;
    		}
    		else if(b++<ile-2*r)
    		{
    			x1 = x2;
    			y1 = y2;
    			y2 -= dy/(count/4); 
    			xba = x2-radiousx;
    			yba = y2;
    		}
    		else if(pd++<30)
    		{
    			x1 = x2;
    			y1 = y2;
    			x2 = xba + (float)(radiousx*Math.cos(alfa));
    			y2 = yba + (float)(radiousy*Math.sin(alfa));
    			alfa -= 2*Math.PI/120f;
    		}
    		else if(c++<ile-2*r)
    		{
    			y1 = y2;
    			x1 = x2;
    			x2 -= dx/(count/4);
    			xba = x2;
    			yba = y2 + radiousy;
    		}
    		else if(ld++<30)
    		{
    			x1 = x2;
    			y1 = y2;
    			x2 = xba + (float)(radiousx*Math.cos(alfa));
    			y2 = yba + (float)(radiousy*Math.sin(alfa));
    			alfa -= 2*Math.PI/120f;
    		}
    		else if(d++<ile-2*r)
    		{
    			x1 = x2;
    			y1 = y2;
    			y2 += dy/(count/4); 
    			xba = x2 + radiousx;
    			yba = y2 ;
    		}
    		else if(lg++<30)
    		{
    			x1 = x2;
    			y1 = y2;
    			x2 = xba + (float)(radiousx*Math.cos(alfa));
    			y2 = yba + (float)(radiousy*Math.sin(alfa));
    			alfa -= 2*Math.PI/120f;
    		}
    		else if(e++<ile-r)
    		{
    			y1 = y2;
    			x1 = x2;
    			x2 += dx/(count/4);	
    		}
    	}

        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);
        
    }

    public void draw(GL10 gl,int count ) {
    	//System.out.println(count*COORDS_PER_VERTEX+" "+ triangleCoords.length/3);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glColor4f(color[0], color[1],color[2], color[3]);
        gl.glVertexPointer(COORDS_PER_VERTEX,GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLES, count*COORDS_PER_VERTEX,triangleCoords.length/COORDS_PER_VERTEX-count*COORDS_PER_VERTEX);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
