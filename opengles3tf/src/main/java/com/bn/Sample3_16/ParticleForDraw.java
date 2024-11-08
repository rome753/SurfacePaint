package com.bn.Sample3_16;//声明包
import static com.bn.Sample3_16.ShaderUtil.createProgram;
import static com.bn.Sample3_16.ShaderUtil.createProgram_TransformFeedback;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.GLES30;
//纹理三角形
public class ParticleForDraw 
{	
	int mProgram;//自定义渲染管线程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int mmaxLifeSpan;//
    int muBj;//单个粒子的半径引用id  
    int muStartColor;//起始颜色引用id
    int muEndColor;//终止颜色引用id
    int muCameraPosition;//摄像机位置
    int muMMatrix;//基本变换矩阵总矩阵
    int maPositionHandle; //顶点位置属性引用id  
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器
	
    int mProgram0;//自定义渲染管线程序id
    int maPositionHandle0; //顶点位置属性引用id 
    int mtPositionHandle0;//
    int mGroupCountHandle0;
    int mCountHandle0;
    int mLifeSpanStepHandle0;
    
    String mVertexShader0;//顶点着色器    	 
    String mFragmentShader0;//片元着色器
    
	FloatBuffer mVertexBuffer;//顶点数据缓冲
	FloatBuffer tmVertexBuffer;//顶点数据缓冲
	
    int vCount=0;   
    float halfSize;
    
	int mVertexBufferIds[]=new int[2];//顶点数据缓冲 id
	int mVertexBufferId0;
	
	int[] a={0,1};//缓冲区数组的索引值数组
    int[] b={1,0};//缓冲区数组的索引值数组
    int index=0;//索引值数组的索引值
    
    public ParticleForDraw(MySurfaceView mv,float halfSize,float x,float y)
    {    	
    	this.halfSize=halfSize;
    	//初始化着色器        
    	initShader0(mv);
    	//初始化着色器        
    	initShader(mv);
    }
   
    //初始化顶点坐标数据的方法
    public void initVertexData(float[] points,float[] tpoints)
    {
       	//缓冲id数组
    	int[] buffIds=new int[3];
    	//生成3个缓冲id
    	GLES30.glGenBuffers(3, buffIds, 0);
    	//顶点基本属性数据缓冲 id
    	this.mVertexBufferIds[0]=buffIds[0];
    	//顶点基本属性数据缓冲 id
    	this.mVertexBufferIds[1]=buffIds[1];
    	//顶点固定属性数据缓冲 id
    	this.mVertexBufferId0=buffIds[2];
    	
    	//顶点数据的初始化================begin============================
    	this.vCount=points.length/4;//顶点个数

        //创建顶点基本属性数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(points.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        this.mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        this.mVertexBuffer.put(points);//向缓冲区中放入顶点基本属性数据
        this.mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        
        //绑定到顶点基本属性数据缓冲 --用于存放顶点的当前基本属性值
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,this.mVertexBufferIds[0]);
    	//向顶点基本属性数据缓冲送入数据
    	GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, points.length*4, this.mVertexBuffer, GLES30.GL_STATIC_DRAW);    
    	
    	//绑定到顶点基本属性数据缓冲 --用于存放顶点下一位置的基本属性值（变换反馈缓冲区）
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferIds[1]);
    	//向顶点基本属性数据缓冲送入数据
    	GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, points.length*4, this.mVertexBuffer, GLES30.GL_STATIC_DRAW);    	
        //顶点数据的初始化================end============================
       
    	///////////////////////////////////////////////////
		
    	//创建顶点固定属性数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb0 = ByteBuffer.allocateDirect(tpoints.length*4);
        vbb0.order(ByteOrder.nativeOrder());//设置字节顺序
        this.tmVertexBuffer = vbb0.asFloatBuffer();//转换为Float型缓冲
        this.tmVertexBuffer.put(tpoints);//向缓冲区中放入顶点固定属性数据
        this.tmVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        
        //绑定到顶点固定属性数据缓冲 
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,this.mVertexBufferId0);
    	//向顶点固定属性数据缓冲送入数据
    	GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, tpoints.length*4, this.tmVertexBuffer, GLES30.GL_STATIC_DRAW);    
    	//绑定到系统默认缓冲
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);
    }
    
    //初始化着色器
    public void initShader0(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader0=ShaderUtil.loadFromAssetsFile("vertex_TransformFeedback.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader0=ShaderUtil.loadFromAssetsFile("frag_TransformFeedback.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mProgram0 = createProgram_TransformFeedback(mVertexShader0, mFragmentShader0);
        //获取程序中顶点位置属性引用id  
        maPositionHandle0 = GLES30.glGetAttribLocation(mProgram0, "aPosition");
        //获取程序中顶点固定属性引用id
        mtPositionHandle0 = GLES30.glGetAttribLocation(mProgram0, "tPosition");
        //获取程序中激活粒子的位置属性引用id
        mCountHandle0=GLES30.glGetUniformLocation(mProgram0, "count");
        //获取程序中一层粒子的数量属性引用id
        mGroupCountHandle0=GLES30.glGetUniformLocation(mProgram0, "groupCount");
        //获取程序中粒子生命周期步进属性引用id
        mLifeSpanStepHandle0=GLES30.glGetUniformLocation(mProgram0, "lifeSpanStep");
        
    }
    
    public void drawSelf0(int count,int groupCount,float lifeSpanStep)
    {//绘制方法
    	//指定使用某套着色器程序
   	 	GLES30.glUseProgram(mProgram0);  
   	 	//设置变换反馈缓冲区对象（绑定到存放下一位置的顶点基本属性值的顶点基本属性数据缓冲）
   	 	GLES30.glBindBufferBase(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER, 0, mVertexBufferIds[b[index]]);
   	 	//开启禁止栅格化，则顶点着色器中的out变量不进入片元着色器，而是写入变换反馈缓冲对象中
   	 	GLES30.glEnable(GLES30.GL_RASTERIZER_DISCARD);
   	 	//将激活粒子位置的计算器送入渲染管线
   	 	GLES30.glUniform1i(mCountHandle0, count);
   	 	//将每批粒子的个数送入渲染管线
   	 	GLES30.glUniform1i(mGroupCountHandle0, groupCount);
   	 	//将粒子生命期步进送入渲染管线
   	 	GLES30.glUniform1f(mLifeSpanStepHandle0, lifeSpanStep);
   	 
		//启用顶点基本属性数据
		GLES30.glEnableVertexAttribArray(maPositionHandle0);  
		//启用顶点固定属性数据
		GLES30.glEnableVertexAttribArray(mtPositionHandle0);
		
		//绑定到存放当前顶点基本属性的顶点基本属性数据缓冲 
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferIds[a[index]]); 
		//将顶点基本属性数据送入渲染管线    	 
		GLES30.glVertexAttribPointer  
		(
				maPositionHandle0,   
				4, 
				GLES30.GL_FLOAT, 
				false,
				4*4,   
				0
		);   		
		
		//绑定到顶点固定属性数据缓冲 
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferId0); 
		//将顶点固定属性数据送入渲染管线    	    	 
		GLES30.glVertexAttribPointer  
		(
				mtPositionHandle0,   
				4, 
				GLES30.GL_FLOAT, 
				false,
				4*4,   
				0
		);  
		
		//绑定到系统默认缓冲
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
		
   	 	//利用变换反馈计算粒子位置
		
		//启用变换反馈渲染-顶点结果按GL_POINTS（点）组织形式输出到指定的变换反馈缓冲区中
   	 	GLES30.glBeginTransformFeedback(GLES30.GL_POINTS);
   	 	//绘制点--不是真实的绘制
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, vCount);
        //关闭变换反馈渲染
        GLES30.glEndTransformFeedback();
        //关闭禁止栅格化
        GLES30.glDisable(GLES30.GL_RASTERIZER_DISCARD);
        //指定系统默认的着色器程序
        GLES30.glUseProgram(0);
        //设置变换反馈缓冲区对象--绑定到系统默认的变换反馈缓冲区
        GLES30.glBindBufferBase(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER, 0, 0);
        
        index++;//索引值数组的索引值
		if(index>=2)
		{//索引值超出数组长度
			index=0;//索引值设为0
		}
    }
    //初始化着色器
    public void initShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
        //获取程序中最大允许生命期引用id
        mmaxLifeSpan=GLES30.glGetUniformLocation(mProgram, "maxLifeSpan");
        //获取程序中半径引用id
        muBj=GLES30.glGetUniformLocation(mProgram, "bj");
        //获取起始颜色引用id
        muStartColor=GLES30.glGetUniformLocation(mProgram, "startColor");
        //获取终止颜色引用id
        muEndColor=GLES30.glGetUniformLocation(mProgram, "endColor");
        //获取摄像机位置引用id
        muCameraPosition=GLES30.glGetUniformLocation(mProgram, "cameraPosition");
        //获取基本变换矩阵总矩阵引用id
        muMMatrix=GLES30.glGetUniformLocation(mProgram, "uMMatrix");
    }
    public void drawSelf(int texId,float maxLifeSpan,float[] startColor,float[] endColor)
    {   
         GLES30.glUseProgram(mProgram);  
         //将最终变换矩阵传入shader程序
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
         
         GLES30.glUniform1f(mmaxLifeSpan, maxLifeSpan);
         //将半径传入shader程序
         GLES30.glUniform1f(muBj, halfSize);
         //将起始颜色送入渲染管线
         GLES30.glUniform4fv(muStartColor, 1, startColor, 0);
         //将终止颜色送入渲染管线
         GLES30.glUniform4fv(muEndColor, 1, endColor, 0);
         //将摄像机位置传入渲染管线
         GLES30.glUniform3f(muCameraPosition,MatrixState.cx, MatrixState.cy, MatrixState.cz);
         //将基本变换矩阵总矩阵传入渲染管线
         GLES30.glUniformMatrix4fv(muMMatrix, 1, false, MatrixState.getMMatrix(), 0); 
         
         //启用顶点位置坐标数据
 		 GLES30.glEnableVertexAttribArray(maPositionHandle);  
 		 //绑定到顶点坐标数据缓冲 
 		 GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferIds[a[index]]); 

 		 //指定顶点位置数据     	 
 		 GLES30.glVertexAttribPointer  
 		 (
 				maPositionHandle,   
 				4, 
 				GLES30.GL_FLOAT, 
 				false,
 				4*4,   
 				0
 		 ); 
 		 
         //绑定纹理
         GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
         
         //绘制纹理矩形
         GLES30.glDrawArrays(GLES30.GL_POINTS, 0, vCount); 
         
         // 绑定到系统默认缓冲
 		 GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
    }
}
