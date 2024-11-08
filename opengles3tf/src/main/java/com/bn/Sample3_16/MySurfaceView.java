package com.bn.Sample3_16;

import static com.bn.Sample3_16.ParticleDataConstant.COUNT;
import static com.bn.Sample3_16.ParticleDataConstant.CURR_INDEX;
import static com.bn.Sample3_16.ParticleDataConstant.RADIS;
import static com.bn.Sample3_16.ParticleDataConstant.walls;
import static com.bn.Sample3_16.Sample2_12Activity.HEIGHT;
import static com.bn.Sample3_16.Sample2_12Activity.WIDTH;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;

import com.example.opengles3tf.R;

class MySurfaceView extends GLSurfaceView 
{
     private SceneRenderer mRenderer;//场景渲染器    
     
     List<ParticleSystem> fps=new ArrayList<ParticleSystem>();
     ParticleForDraw[] fpfd;
     WallsForwDraw wallsForDraw;    
     LoadedObjectVertexNormalTexture brazier;
     
     static float direction=0;//视线方向
     static float cx=0;//摄像机x坐标
	 static float cy=18;//摄像机z坐标
	 static float cz=20;//摄像机z坐标  
     static float tx=0;//观察目标点x坐标  
     static float ty=5;//观察目标点y坐标
     static float tz=0;//观察目标点z坐标   
     static float ux=-cx;//观察目标点x坐标  
     static float uy=Math.abs((cx*tx+cz*tz-cx*cx-cz*cz)/(ty-cy));//观察目标点y坐标
     static float uz=-cz;//观察目标点z坐标   
     static final float DEGREE_SPAN=(float)(3.0/180.0f*Math.PI);//摄像机每次转动的角度
     
     float Offset=20;
     float preX;//触控点x坐标
     float preY;//触控点y坐标
     float x;
     float y;  
     
     int textureIdFire;//系统火焰分配的纹理id
     
     int textureIdbrazier;//系统火盆分配的纹理id
     int count;
   	 
     boolean flag=true;//线程循环的标志位
     
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES 3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		x=event.getX();
		y=event.getY();
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				flag=true;
				new Thread()
				{
					@Override
					public void run()
					{
						while(flag)
						{
							if(x>WIDTH/4&&x<3*WIDTH/4&&y>0&&y<HEIGHT/2)
							{	//向前
								if(Math.abs(Offset-0.5f)>25||Math.abs(Offset-0.5f)<15)
								{
								   return;
								}
								Offset=Offset-0.5f;
							}
							else if(x>WIDTH/4&&x<3*WIDTH/4&&y>HEIGHT/2&&y<HEIGHT)
							{	//向后
								if(Math.abs(Offset+0.5f)>25||Math.abs(Offset+0.5f)<15)
								{
								   return;
								}
								Offset=Offset+0.5f;
							}
							else if(x<WIDTH/4)
							{
								//顺时针旋转
								direction=direction-DEGREE_SPAN;
							}
							else if(x>WIDTH/4)
							{
								//逆时针旋转
								direction=direction+DEGREE_SPAN;
							}
							try
							{
								Thread.sleep(100);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				}.start();
				break;
			case MotionEvent.ACTION_UP:
				flag=false;
				break;
			}
			 //设置新的观察目标点XZ坐标
			 cx=(float)(Math.sin(direction)*Offset);//观察目标点x坐标 
	         cz=(float)(Math.cos(direction)*Offset);//观察目标点z坐标 
	         //重新计算Up向量
	         ux=-cx;//观察目标点x坐标  
	         uy=Math.abs((cx*tx+cz*tz-cx*cx-cz*cz)/(ty-cy));//观察目标点y坐标
	         uz=-cz;//观察目标点z坐标   
	         //计算粒子的朝向
	         for(int i=0;i<count;i++)
	         {
	        	fps.get(i).calculateBillboardDirection();
	         }
			 Collections.sort(this.fps);
			 //重新设置摄像机的位置
			 MatrixState.setCamera(cx,cy,cz,tx,ty,tz,ux,uy,uz);
			 //根据粒子与摄像机的距离进行排序
		 return true;
	}

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
		
//		int countt=0;//计算帧速率的时间间隔次数--计算器
//		long timeStart=System.nanoTime();//开始时间
        public void onDrawFrame(GL10 gl) 
        { 
//        	if(countt==19)//每十次一计算帧速率
//        	{
//        		long timeEnd=System.nanoTime();//结束时间
//        		
//        		//计算帧速率
//        		float ps=(float)(1000000000.0/((timeEnd-timeStart)/20));
//        		System.out.println("pss="+ps);
//        		countt=0;//计算器置0
//        		timeStart=timeEnd;//起始时间置为结束时间
//        	}
//        	countt=(countt+1)%20;//更新计数器的值
        	
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            MatrixState.pushMatrix();
            //绘制墙体
            wallsForDraw.drawSelf();
            MatrixState.translate(0, 2.5f, 0);
            for(int i=0;i<count;i++)
            {
            	MatrixState.pushMatrix();
            	MatrixState.translate(ParticleDataConstant.positionBrazierXZ[i][0],-2f,ParticleDataConstant.positionBrazierXZ[i][1]);
            	//若加载的物体部位空则绘制物体
                if(brazier!=null)
	            {
	           	  brazier.drawSelf(textureIdbrazier);
                }   
                MatrixState.popMatrix();
            } 
            MatrixState.translate(0, 0.65f, 0);
        	for(int i=0;i<count;i++)
            {
        		MatrixState.pushMatrix();
                fps.get(i).drawSelf(textureIdFire); 
                MatrixState.popMatrix();
            }
            MatrixState.popMatrix();
        }  
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-0.3f*ratio, 0.3f*ratio, -1*0.3f, 1*0.3f, 1, 100);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(cx,cy,cz,tx,ty,tz,ux,uy,uz);
            //初始化变换矩阵
       	    MatrixState.setInitStack();
       	    //初始化光源位置   
            MatrixState.setLightLocation(0, 15, 0);
        }
        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {
            //初始化纹理坐标
            for(int i=0;i<walls.length;i++)
            {
            	ParticleDataConstant.walls[i]=initTexture(R.drawable.wall0+i);
            	
            }
            textureIdbrazier=initTexture(R.drawable.brazier);
        	count=ParticleDataConstant.START_COLOR.length;
    		fpfd=new ParticleForDraw[count];//4组绘制着，4种颜色
    		 //创建粒子系统
            for(int i=0;i<count;i++)
            {
            	
            	CURR_INDEX=i;
            	fpfd[i]=new ParticleForDraw(MySurfaceView.this,RADIS[CURR_INDEX],0,0);  
            	//创建对象,将火焰的初始位置传给构造器
            	fps.add(new ParticleSystem(ParticleDataConstant.positionFireXZ[i][0],ParticleDataConstant.positionFireXZ[i][1],fpfd[i],COUNT[i]));
            }
    		wallsForDraw=new WallsForwDraw(MySurfaceView.this);
        	 //加载要绘制的物体
        	 brazier=LoadUtil.loadFromFile("brazier.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.6f,0.3f,0.0f, 1.0f);
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //初始化纹理
            textureIdFire=initTexture(R.drawable.fire);
            
            //关闭背面剪裁   
            GLES30.glDisable(GLES30.GL_CULL_FACE);
           
        }
    }
	
	public int initTexture(int resId)
	{
		//生成纹理ID
		int[] textures = new int[1];
		GLES30.glGenTextures
		(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量
		);    
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
        
     
        //通过输入流加载图片===============begin===================
        InputStream is = this.getResources().openRawResource(resId);
        Bitmap bitmapTmp;
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } 
        finally 
        {
            try 
            {
                is.close();
            } 
            catch(IOException e) 
            {
                e.printStackTrace();
            }
        }
        //通过输入流加载图片===============end=====================  
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES30.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        return textureId;
	}
}
