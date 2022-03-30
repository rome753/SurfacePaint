package com.bn.Sample3_16;//������
import static com.bn.Sample3_16.ShaderUtil.createProgram;
import static com.bn.Sample3_16.ShaderUtil.createProgram_TransformFeedback;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.GLES30;
//����������
public class ParticleForDraw 
{	
	int mProgram;//�Զ�����Ⱦ���߳���id
    int muMVPMatrixHandle;//�ܱ任��������id
    int mmaxLifeSpan;//
    int muBj;//�������ӵİ뾶����id  
    int muStartColor;//��ʼ��ɫ����id
    int muEndColor;//��ֹ��ɫ����id
    int muCameraPosition;//�����λ��
    int muMMatrix;//�����任�����ܾ���
    int maPositionHandle; //����λ����������id  
    String mVertexShader;//������ɫ��    	 
    String mFragmentShader;//ƬԪ��ɫ��
	
    int mProgram0;//�Զ�����Ⱦ���߳���id
    int maPositionHandle0; //����λ����������id 
    int mtPositionHandle0;//
    int mGroupCountHandle0;
    int mCountHandle0;
    int mLifeSpanStepHandle0;
    
    String mVertexShader0;//������ɫ��    	 
    String mFragmentShader0;//ƬԪ��ɫ��
    
	FloatBuffer mVertexBuffer;//�������ݻ���
	FloatBuffer tmVertexBuffer;//�������ݻ���
	
    int vCount=0;   
    float halfSize;
    
	int mVertexBufferIds[]=new int[2];//�������ݻ��� id
	int mVertexBufferId0;
	
	int[] a={0,1};//���������������ֵ����
    int[] b={1,0};//���������������ֵ����
    int index=0;//����ֵ���������ֵ
    
    public ParticleForDraw(MySurfaceView mv,float halfSize,float x,float y)
    {    	
    	this.halfSize=halfSize;
    	//��ʼ����ɫ��        
    	initShader0(mv);
    	//��ʼ����ɫ��        
    	initShader(mv);
    }
   
    //��ʼ�������������ݵķ���
    public void initVertexData(float[] points,float[] tpoints)
    {
       	//����id����
    	int[] buffIds=new int[3];
    	//����3������id
    	GLES30.glGenBuffers(3, buffIds, 0);
    	//��������������ݻ��� id
    	this.mVertexBufferIds[0]=buffIds[0];
    	//��������������ݻ��� id
    	this.mVertexBufferIds[1]=buffIds[1];
    	//����̶��������ݻ��� id
    	this.mVertexBufferId0=buffIds[2];
    	
    	//�������ݵĳ�ʼ��================begin============================
    	this.vCount=points.length/4;//�������

        //������������������ݻ���
        //vertices.length*4����Ϊһ�������ĸ��ֽ�
        ByteBuffer vbb = ByteBuffer.allocateDirect(points.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        this.mVertexBuffer = vbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        this.mVertexBuffer.put(points);//�򻺳����з��붥�������������
        this.mVertexBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        
        //�󶨵���������������ݻ��� --���ڴ�Ŷ���ĵ�ǰ��������ֵ
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,this.mVertexBufferIds[0]);
    	//�򶥵�����������ݻ�����������
    	GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, points.length*4, this.mVertexBuffer, GLES30.GL_STATIC_DRAW);    
    	
    	//�󶨵���������������ݻ��� --���ڴ�Ŷ�����һλ�õĻ�������ֵ���任������������
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferIds[1]);
    	//�򶥵�����������ݻ�����������
    	GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, points.length*4, this.mVertexBuffer, GLES30.GL_STATIC_DRAW);    	
        //�������ݵĳ�ʼ��================end============================
       
    	///////////////////////////////////////////////////
		
    	//��������̶��������ݻ���
        //vertices.length*4����Ϊһ�������ĸ��ֽ�
        ByteBuffer vbb0 = ByteBuffer.allocateDirect(tpoints.length*4);
        vbb0.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        this.tmVertexBuffer = vbb0.asFloatBuffer();//ת��ΪFloat�ͻ���
        this.tmVertexBuffer.put(tpoints);//�򻺳����з��붥��̶���������
        this.tmVertexBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        
        //�󶨵�����̶��������ݻ��� 
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,this.mVertexBufferId0);
    	//�򶥵�̶��������ݻ�����������
    	GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, tpoints.length*4, this.tmVertexBuffer, GLES30.GL_STATIC_DRAW);    
    	//�󶨵�ϵͳĬ�ϻ���
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);
    }
    
    //��ʼ����ɫ��
    public void initShader0(MySurfaceView mv)
    {
    	//���ض�����ɫ���Ľű�����
        mVertexShader0=ShaderUtil.loadFromAssetsFile("vertex_TransformFeedback.sh", mv.getResources());
        //����ƬԪ��ɫ���Ľű�����
        mFragmentShader0=ShaderUtil.loadFromAssetsFile("frag_TransformFeedback.sh", mv.getResources());  
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram0 = createProgram_TransformFeedback(mVertexShader0, mFragmentShader0);
        //��ȡ�����ж���λ����������id  
        maPositionHandle0 = GLES30.glGetAttribLocation(mProgram0, "aPosition");
        //��ȡ�����ж���̶���������id
        mtPositionHandle0 = GLES30.glGetAttribLocation(mProgram0, "tPosition");
        //��ȡ�����м������ӵ�λ����������id
        mCountHandle0=GLES30.glGetUniformLocation(mProgram0, "count");
        //��ȡ������һ�����ӵ�������������id
        mGroupCountHandle0=GLES30.glGetUniformLocation(mProgram0, "groupCount");
        //��ȡ�����������������ڲ�����������id
        mLifeSpanStepHandle0=GLES30.glGetUniformLocation(mProgram0, "lifeSpanStep");
        
    }
    
    public void drawSelf0(int count,int groupCount,float lifeSpanStep)
    {//���Ʒ���
    	//ָ��ʹ��ĳ����ɫ������
   	 	GLES30.glUseProgram(mProgram0);  
   	 	//���ñ任�������������󣨰󶨵������һλ�õĶ����������ֵ�Ķ�������������ݻ��壩
   	 	GLES30.glBindBufferBase(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER, 0, mVertexBufferIds[b[index]]);
   	 	//������ֹդ�񻯣��򶥵���ɫ���е�out����������ƬԪ��ɫ��������д��任�������������
   	 	GLES30.glEnable(GLES30.GL_RASTERIZER_DISCARD);
   	 	//����������λ�õļ�����������Ⱦ����
   	 	GLES30.glUniform1i(mCountHandle0, count);
   	 	//��ÿ�����ӵĸ���������Ⱦ����
   	 	GLES30.glUniform1i(mGroupCountHandle0, groupCount);
   	 	//�����������ڲ���������Ⱦ����
   	 	GLES30.glUniform1f(mLifeSpanStepHandle0, lifeSpanStep);
   	 
		//���ö��������������
		GLES30.glEnableVertexAttribArray(maPositionHandle0);  
		//���ö���̶���������
		GLES30.glEnableVertexAttribArray(mtPositionHandle0);
		
		//�󶨵���ŵ�ǰ����������ԵĶ�������������ݻ��� 
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferIds[a[index]]); 
		//�����������������������Ⱦ����    	 
		GLES30.glVertexAttribPointer  
		(
				maPositionHandle0,   
				4, 
				GLES30.GL_FLOAT, 
				false,
				4*4,   
				0
		);   		
		
		//�󶨵�����̶��������ݻ��� 
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferId0); 
		//������̶���������������Ⱦ����    	    	 
		GLES30.glVertexAttribPointer  
		(
				mtPositionHandle0,   
				4, 
				GLES30.GL_FLOAT, 
				false,
				4*4,   
				0
		);  
		
		//�󶨵�ϵͳĬ�ϻ���
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
		
   	 	//���ñ任������������λ��
		
		//���ñ任������Ⱦ-��������GL_POINTS���㣩��֯��ʽ�����ָ���ı任������������
   	 	GLES30.glBeginTransformFeedback(GLES30.GL_POINTS);
   	 	//���Ƶ�--������ʵ�Ļ���
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, vCount);
        //�رձ任������Ⱦ
        GLES30.glEndTransformFeedback();
        //�رս�ֹդ��
        GLES30.glDisable(GLES30.GL_RASTERIZER_DISCARD);
        //ָ��ϵͳĬ�ϵ���ɫ������
        GLES30.glUseProgram(0);
        //���ñ任��������������--�󶨵�ϵͳĬ�ϵı任����������
        GLES30.glBindBufferBase(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER, 0, 0);
        
        index++;//����ֵ���������ֵ
		if(index>=2)
		{//����ֵ�������鳤��
			index=0;//����ֵ��Ϊ0
		}
    }
    //��ʼ����ɫ��
    public void initShader(MySurfaceView mv)
    {
    	//���ض�����ɫ���Ľű�����
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //����ƬԪ��ɫ���Ľű�����
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());  
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
        //��ȡ�����������������������id
        mmaxLifeSpan=GLES30.glGetUniformLocation(mProgram, "maxLifeSpan");
        //��ȡ�����а뾶����id
        muBj=GLES30.glGetUniformLocation(mProgram, "bj");
        //��ȡ��ʼ��ɫ����id
        muStartColor=GLES30.glGetUniformLocation(mProgram, "startColor");
        //��ȡ��ֹ��ɫ����id
        muEndColor=GLES30.glGetUniformLocation(mProgram, "endColor");
        //��ȡ�����λ������id
        muCameraPosition=GLES30.glGetUniformLocation(mProgram, "cameraPosition");
        //��ȡ�����任�����ܾ�������id
        muMMatrix=GLES30.glGetUniformLocation(mProgram, "uMMatrix");
    }
    public void drawSelf(int texId,float maxLifeSpan,float[] startColor,float[] endColor)
    {   
         GLES30.glUseProgram(mProgram);  
         //�����ձ任������shader����
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
         
         GLES30.glUniform1f(mmaxLifeSpan, maxLifeSpan);
         //���뾶����shader����
         GLES30.glUniform1f(muBj, halfSize);
         //����ʼ��ɫ������Ⱦ����
         GLES30.glUniform4fv(muStartColor, 1, startColor, 0);
         //����ֹ��ɫ������Ⱦ����
         GLES30.glUniform4fv(muEndColor, 1, endColor, 0);
         //�������λ�ô�����Ⱦ����
         GLES30.glUniform3f(muCameraPosition,MatrixState.cx, MatrixState.cy, MatrixState.cz);
         //�������任�����ܾ�������Ⱦ����
         GLES30.glUniformMatrix4fv(muMMatrix, 1, false, MatrixState.getMMatrix(), 0); 
         
         //���ö���λ����������
 		 GLES30.glEnableVertexAttribArray(maPositionHandle);  
 		 //�󶨵������������ݻ��� 
 		 GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferIds[a[index]]); 

 		 //ָ������λ������     	 
 		 GLES30.glVertexAttribPointer  
 		 (
 				maPositionHandle,   
 				4, 
 				GLES30.GL_FLOAT, 
 				false,
 				4*4,   
 				0
 		 ); 
 		 
         //������
         GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
         
         //�����������
         GLES30.glDrawArrays(GLES30.GL_POINTS, 0, vCount); 
         
         // �󶨵�ϵͳĬ�ϻ���
 		 GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
    }
}
