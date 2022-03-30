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

class MySurfaceView extends GLSurfaceView 
{
     private SceneRenderer mRenderer;//������Ⱦ��    
     
     List<ParticleSystem> fps=new ArrayList<ParticleSystem>();
     ParticleForDraw[] fpfd;
     WallsForwDraw wallsForDraw;    
     LoadedObjectVertexNormalTexture brazier;
     
     static float direction=0;//���߷���
     static float cx=0;//�����x����
	 static float cy=18;//�����z����
	 static float cz=20;//�����z����  
     static float tx=0;//�۲�Ŀ���x����  
     static float ty=5;//�۲�Ŀ���y����
     static float tz=0;//�۲�Ŀ���z����   
     static float ux=-cx;//�۲�Ŀ���x����  
     static float uy=Math.abs((cx*tx+cz*tz-cx*cx-cz*cz)/(ty-cy));//�۲�Ŀ���y����
     static float uz=-cz;//�۲�Ŀ���z����   
     static final float DEGREE_SPAN=(float)(3.0/180.0f*Math.PI);//�����ÿ��ת���ĽǶ�
     
     float Offset=20;
     float preX;//���ص�x����
     float preY;//���ص�y����
     float x;
     float y;  
     
     int textureIdFire;//ϵͳ������������id
     
     int textureIdbrazier;//ϵͳ������������id
     int count;
   	 
     boolean flag=true;//�߳�ѭ���ı�־λ
     
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //����ʹ��OPENGL ES 3.0
        mRenderer = new SceneRenderer();	//����������Ⱦ��
        setRenderer(mRenderer);				//������Ⱦ��		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ   
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
							{	//��ǰ
								if(Math.abs(Offset-0.5f)>25||Math.abs(Offset-0.5f)<15)
								{
								   return;
								}
								Offset=Offset-0.5f;
							}
							else if(x>WIDTH/4&&x<3*WIDTH/4&&y>HEIGHT/2&&y<HEIGHT)
							{	//���
								if(Math.abs(Offset+0.5f)>25||Math.abs(Offset+0.5f)<15)
								{
								   return;
								}
								Offset=Offset+0.5f;
							}
							else if(x<WIDTH/4)
							{
								//˳ʱ����ת
								direction=direction-DEGREE_SPAN;
							}
							else if(x>WIDTH/4)
							{
								//��ʱ����ת
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
			 //�����µĹ۲�Ŀ���XZ����
			 cx=(float)(Math.sin(direction)*Offset);//�۲�Ŀ���x���� 
	         cz=(float)(Math.cos(direction)*Offset);//�۲�Ŀ���z���� 
	         //���¼���Up����
	         ux=-cx;//�۲�Ŀ���x����  
	         uy=Math.abs((cx*tx+cz*tz-cx*cx-cz*cz)/(ty-cy));//�۲�Ŀ���y����
	         uz=-cz;//�۲�Ŀ���z����   
	         //�������ӵĳ���
	         for(int i=0;i<count;i++)
	         {
	        	fps.get(i).calculateBillboardDirection();
	         }
			 Collections.sort(this.fps);
			 //���������������λ��
			 MatrixState.setCamera(cx,cy,cz,tx,ty,tz,ux,uy,uz);
			 //����������������ľ����������
		 return true;
	}

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
		
//		int countt=0;//����֡���ʵ�ʱ��������--������
//		long timeStart=System.nanoTime();//��ʼʱ��
        public void onDrawFrame(GL10 gl) 
        { 
//        	if(countt==19)//ÿʮ��һ����֡����
//        	{
//        		long timeEnd=System.nanoTime();//����ʱ��
//        		
//        		//����֡����
//        		float ps=(float)(1000000000.0/((timeEnd-timeStart)/20));
//        		System.out.println("pss="+ps);
//        		countt=0;//��������0
//        		timeStart=timeEnd;//��ʼʱ����Ϊ����ʱ��
//        	}
//        	countt=(countt+1)%20;//���¼�������ֵ
        	
        	//�����Ȼ�������ɫ����
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            MatrixState.pushMatrix();
            //����ǽ��
            wallsForDraw.drawSelf();
            MatrixState.translate(0, 2.5f, 0);
            for(int i=0;i<count;i++)
            {
            	MatrixState.pushMatrix();
            	MatrixState.translate(ParticleDataConstant.positionBrazierXZ[i][0],-2f,ParticleDataConstant.positionBrazierXZ[i][1]);
            	//�����ص����岿λ�����������
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
            //�����Ӵ���С��λ�� 
        	GLES30.glViewport(0, 0, width, height); 
        	//����GLSurfaceView�Ŀ�߱�
            float ratio = (float) width / height;
            //���ô˷����������͸��ͶӰ����
            MatrixState.setProjectFrustum(-0.3f*ratio, 0.3f*ratio, -1*0.3f, 1*0.3f, 1, 100);
            //���ô˷������������9����λ�þ���
            MatrixState.setCamera(cx,cy,cz,tx,ty,tz,ux,uy,uz);
            //��ʼ���任����
       	    MatrixState.setInitStack();
       	    //��ʼ����Դλ��   
            MatrixState.setLightLocation(0, 15, 0);
        }
        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {
            //��ʼ����������
            for(int i=0;i<walls.length;i++)
            {
            	ParticleDataConstant.walls[i]=initTexture(R.drawable.wall0+i);
            	
            }
            textureIdbrazier=initTexture(R.drawable.brazier);
        	count=ParticleDataConstant.START_COLOR.length;
    		fpfd=new ParticleForDraw[count];//4������ţ�4����ɫ
    		 //��������ϵͳ
            for(int i=0;i<count;i++)
            {
            	
            	CURR_INDEX=i;
            	fpfd[i]=new ParticleForDraw(MySurfaceView.this,RADIS[CURR_INDEX],0,0);  
            	//��������,������ĳ�ʼλ�ô���������
            	fps.add(new ParticleSystem(ParticleDataConstant.positionFireXZ[i][0],ParticleDataConstant.positionFireXZ[i][1],fpfd[i],COUNT[i]));
            }
    		wallsForDraw=new WallsForwDraw(MySurfaceView.this);
        	 //����Ҫ���Ƶ�����
        	 brazier=LoadUtil.loadFromFile("brazier.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            //������Ļ����ɫRGBA
            GLES30.glClearColor(0.6f,0.3f,0.0f, 1.0f);
            //����ȼ��
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //��ʼ������
            textureIdFire=initTexture(R.drawable.fire);
            
            //�رձ������   
            GLES30.glDisable(GLES30.GL_CULL_FACE);
           
        }
    }
	
	public int initTexture(int resId)
	{
		//��������ID
		int[] textures = new int[1];
		GLES30.glGenTextures
		(
				1,          //����������id������
				textures,   //����id������
				0           //ƫ����
		);    
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
        
     
        //ͨ������������ͼƬ===============begin===================
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
        //ͨ������������ͼƬ===============end=====================  
        //ʵ�ʼ�������
        GLUtils.texImage2D
        (
        		GLES30.GL_TEXTURE_2D,   //�������ͣ���OpenGL ES�б���ΪGL10.GL_TEXTURE_2D
        		0, 					  //����Ĳ�Σ�0��ʾ����ͼ��㣬�������Ϊֱ����ͼ
        		bitmapTmp, 			  //����ͼ��
        		0					  //����߿�ߴ�
        );
        bitmapTmp.recycle(); 		  //������سɹ����ͷ�ͼƬ
        return textureId;
	}
}
