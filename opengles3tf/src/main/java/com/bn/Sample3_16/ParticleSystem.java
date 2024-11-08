package com.bn.Sample3_16;//声明包

import static com.bn.Sample3_16.MySurfaceView.cx;
import static com.bn.Sample3_16.MySurfaceView.cz;
import static com.bn.Sample3_16.ParticleDataConstant.*;
import android.opengl.GLES30;

public class ParticleSystem implements Comparable<ParticleSystem> 
{
	//起始颜色
	public float[] startColor;
	//终止颜色
	public float[] endColor;
	//源混合因子
	public int srcBlend;
	//目标混合因子
	public int dstBlend;
	//混合方式
	public int blendFunc;
	//粒子最大生命期
	public float maxLifeSpan;
	//粒子生命期步进
	public float lifeSpanStep;
	//粒子更新线程休眠时间间隔
	public int sleepSpan;
	//每次喷发的例子数量
	public int groupCount;
	//基础发射点
	public float sx;
	public float sy;
	//绘制位置
	float positionX;
	float positionZ;
	//发射点变化范围
	public float xRange;
	public float yRange;
	//粒子发射的速度
	public float vx;
	public float vy;
	//旋转角度
	float yAngle=0;
	//绘制者
	ParticleForDraw fpfd;
	//工作标志位
	boolean flag=true;
	
	public float[] points;//粒子的基本属性数组
	public float[] tpoints;//粒子的固定属性数组
	
	public long preCal=System.nanoTime();//获取当前系统时间
	
    public ParticleSystem(float positionx,float positionz,ParticleForDraw fpfd,int count)
    {
    	this.positionX=positionx;
    	this.positionZ=positionz;
    	this.startColor=START_COLOR[CURR_INDEX];
    	this.endColor=END_COLOR[CURR_INDEX];
    	this.srcBlend=SRC_BLEND[CURR_INDEX]; 
    	this.dstBlend=DST_BLEND[CURR_INDEX];
    	this.blendFunc=BLEND_FUNC[CURR_INDEX];
    	this.maxLifeSpan=MAX_LIFE_SPAN[CURR_INDEX];
    	this.lifeSpanStep=LIFE_SPAN_STEP[CURR_INDEX];
    	this.groupCount=GROUP_COUNT[CURR_INDEX];
    	this.sleepSpan=THREAD_SLEEP[CURR_INDEX];
    	this.sx=0;
    	this.sy=0;
    	this.xRange=X_RANGE[CURR_INDEX];
    	this.yRange=Y_RANGE[CURR_INDEX];
    	this.vx=0;
    	this.vy=VY[CURR_INDEX];
    	this.fpfd=fpfd;
    
    	this.points=initPoints(count);//创建粒子数
    	
    	fpfd.initVertexData(points,tpoints);//更新粒子的位置坐标
    	
    }
    
  
    public float[] initPoints(int zcount)
    {//初始化粒子顶点数据的方法
    	float[] points=new float[zcount*4];//粒子顶点数据数组
    	
    	tpoints=new float[zcount*4];//粒子固定属性数组
    	
    	for(int i=0;i<zcount;i++)//groupCount
    	{//循环遍历所有粒子
    		//在中心附近产生产生粒子的位置------**/
    		float px=(float) (sx+xRange*(Math.random()*2-1.0f));
            float py=(float) (sy+yRange*(Math.random()*2-1.0f));
            float vx=(sx-px)/150;
            points[i*4]=px;
            points[i*4+1]=py;
            points[i*4+2]=vx;
            points[i*4+3]=10.0f;
            
            tpoints[i*4]=px;//将粒子起始位置的x坐标存入tpoints数组中
            tpoints[i*4+1]=py;//将粒子起始位置的y坐标存入tpoints数组中
            tpoints[i*4+2]=vy;//将粒子y方向的速度存入tpoints数组中
            tpoints[i*4+3]=maxLifeSpan;//将粒子最大允许生命期存入tpoints数组中
    	}
    	
    	for(int j=0;j<groupCount;j++)
        {
    		points[4*j+3]=lifeSpanStep;//粒子处于活跃状态
        }
    	
		return points;//返回所有粒子顶点属性数据
    }
    
//    int countt=0;//计算帧速率的时间间隔次数--计算器
//	long timeStart=System.nanoTime();//开始时间
	
    public void drawSelf(int texId)
    {//绘制方法
//    	if(countt==19)//每十次一计算帧速率
//    	{
//    		long timeEnd=System.nanoTime();//结束时间
//    		
//    		//计算帧速率
//    		float ps=(float)(1000000000.0/((timeEnd-timeStart)/20));
//    		System.out.println("ps="+ps);
//    		countt=0;//计算器置0
//    		timeStart=timeEnd;//起始时间置为结束时间
//    	}
//    	countt=(countt+1)%20;//更新计数器的值
    	
    	//关闭深度检测
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
    	//开启混合
        GLES30.glEnable(GLES30.GL_BLEND);  
        //设置混合方式
         GLES30.glBlendEquation(blendFunc);
        //设置混合因子
        GLES30.glBlendFunc(srcBlend,dstBlend); 
 
        MatrixState.translate(positionX, 1, positionZ);
		MatrixState.rotate(yAngle, 0, 1, 0);
		
		MatrixState.pushMatrix();//保护现场
		
		long currCal=System.nanoTime();//获取当前系统时间
		if(currCal-preCal>sleepSpan*1000000)
		{//时间间隔大于更新粒子的休息时间
			fpfd.drawSelf0(count,groupCount,lifeSpanStep);//利用变换反馈更新粒子状态
			preCal=currCal;//设置起始时间为当前时间
			update();//更新激活粒子位置的计算器
		}
    	fpfd.drawSelf(texId,maxLifeSpan,startColor,endColor);//绘制粒子群   	
		MatrixState.popMatrix();//恢复现场
		
    	//开启深度检测
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
    	//关闭混合
        GLES30.glDisable(GLES30.GL_BLEND);  
        
    }
    int count=1;//激活粒子的位置计算器
    public void update()//更新激活粒子位置的计算器的方法
    {	
    	if(count>=(points.length/groupCount/4))//计算器超过激活粒子位置时
    	{
    		count=0;//重新计数
    	}
    	//下次激活粒子的位置
    	count++;
    }
    
	public void calculateBillboardDirection()
	{
		//根据摄像机位置计算火焰朝向
		float xspan=positionX-MySurfaceView.cx;
		float zspan=positionZ-MySurfaceView.cz;
		
		if(zspan<=0)
		{
			yAngle=(float)Math.toDegrees(Math.atan(xspan/zspan));	
		}
		else
		{
			yAngle=180+(float)Math.toDegrees(Math.atan(xspan/zspan));
		}
	}
	@Override
	public int compareTo(ParticleSystem another) {
		//重写的比较两个火焰离摄像机距离的方法
		float xs=positionX-cx;
		float zs=positionZ-cz;
		
		float xo=another.positionX-cx;
		float zo=another.positionZ-cz;
		
		float disA=(float)(xs*xs+zs*zs);
		float disB=(float)(xo*xo+zo*zo);
		return ((disA-disB)==0)?0:((disA-disB)>0)?-1:1;  
	}

}
