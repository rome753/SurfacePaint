package com.bn.Sample3_16;//������

import static com.bn.Sample3_16.MySurfaceView.cx;
import static com.bn.Sample3_16.MySurfaceView.cz;
import static com.bn.Sample3_16.ParticleDataConstant.*;
import android.opengl.GLES30;

public class ParticleSystem implements Comparable<ParticleSystem> 
{
	//��ʼ��ɫ
	public float[] startColor;
	//��ֹ��ɫ
	public float[] endColor;
	//Դ�������
	public int srcBlend;
	//Ŀ��������
	public int dstBlend;
	//��Ϸ�ʽ
	public int blendFunc;
	//�������������
	public float maxLifeSpan;
	//���������ڲ���
	public float lifeSpanStep;
	//���Ӹ����߳�����ʱ����
	public int sleepSpan;
	//ÿ���緢����������
	public int groupCount;
	//���������
	public float sx;
	public float sy;
	//����λ��
	float positionX;
	float positionZ;
	//�����仯��Χ
	public float xRange;
	public float yRange;
	//���ӷ�����ٶ�
	public float vx;
	public float vy;
	//��ת�Ƕ�
	float yAngle=0;
	//������
	ParticleForDraw fpfd;
	//������־λ
	boolean flag=true;
	
	public float[] points;//���ӵĻ�����������
	public float[] tpoints;//���ӵĹ̶���������
	
	public long preCal=System.nanoTime();//��ȡ��ǰϵͳʱ��
	
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
    
    	this.points=initPoints(count);//����������
    	
    	fpfd.initVertexData(points,tpoints);//�������ӵ�λ������
    	
    }
    
  
    public float[] initPoints(int zcount)
    {//��ʼ�����Ӷ������ݵķ���
    	float[] points=new float[zcount*4];//���Ӷ�����������
    	
    	tpoints=new float[zcount*4];//���ӹ̶���������
    	
    	for(int i=0;i<zcount;i++)//groupCount
    	{//ѭ��������������
    		//�����ĸ��������������ӵ�λ��------**/
    		float px=(float) (sx+xRange*(Math.random()*2-1.0f));
            float py=(float) (sy+yRange*(Math.random()*2-1.0f));
            float vx=(sx-px)/150;
            points[i*4]=px;
            points[i*4+1]=py;
            points[i*4+2]=vx;
            points[i*4+3]=10.0f;
            
            tpoints[i*4]=px;//��������ʼλ�õ�x�������tpoints������
            tpoints[i*4+1]=py;//��������ʼλ�õ�y�������tpoints������
            tpoints[i*4+2]=vy;//������y������ٶȴ���tpoints������
            tpoints[i*4+3]=maxLifeSpan;//������������������ڴ���tpoints������
    	}
    	
    	for(int j=0;j<groupCount;j++)
        {
    		points[4*j+3]=lifeSpanStep;//���Ӵ��ڻ�Ծ״̬
        }
    	
		return points;//�����������Ӷ�����������
    }
    
//    int countt=0;//����֡���ʵ�ʱ��������--������
//	long timeStart=System.nanoTime();//��ʼʱ��
	
    public void drawSelf(int texId)
    {//���Ʒ���
//    	if(countt==19)//ÿʮ��һ����֡����
//    	{
//    		long timeEnd=System.nanoTime();//����ʱ��
//    		
//    		//����֡����
//    		float ps=(float)(1000000000.0/((timeEnd-timeStart)/20));
//    		System.out.println("ps="+ps);
//    		countt=0;//��������0
//    		timeStart=timeEnd;//��ʼʱ����Ϊ����ʱ��
//    	}
//    	countt=(countt+1)%20;//���¼�������ֵ
    	
    	//�ر���ȼ��
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
    	//�������
        GLES30.glEnable(GLES30.GL_BLEND);  
        //���û�Ϸ�ʽ
         GLES30.glBlendEquation(blendFunc);
        //���û������
        GLES30.glBlendFunc(srcBlend,dstBlend); 
 
        MatrixState.translate(positionX, 1, positionZ);
		MatrixState.rotate(yAngle, 0, 1, 0);
		
		MatrixState.pushMatrix();//�����ֳ�
		
		long currCal=System.nanoTime();//��ȡ��ǰϵͳʱ��
		if(currCal-preCal>sleepSpan*1000000)
		{//ʱ�������ڸ������ӵ���Ϣʱ��
			fpfd.drawSelf0(count,groupCount,lifeSpanStep);//���ñ任������������״̬
			preCal=currCal;//������ʼʱ��Ϊ��ǰʱ��
			update();//���¼�������λ�õļ�����
		}
    	fpfd.drawSelf(texId,maxLifeSpan,startColor,endColor);//��������Ⱥ   	
		MatrixState.popMatrix();//�ָ��ֳ�
		
    	//������ȼ��
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
    	//�رջ��
        GLES30.glDisable(GLES30.GL_BLEND);  
        
    }
    int count=1;//�������ӵ�λ�ü�����
    public void update()//���¼�������λ�õļ������ķ���
    {	
    	if(count>=(points.length/groupCount/4))//������������������λ��ʱ
    	{
    		count=0;//���¼���
    	}
    	//�´μ������ӵ�λ��
    	count++;
    }
    
	public void calculateBillboardDirection()
	{
		//���������λ�ü�����泯��
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
		//��д�ıȽ��������������������ķ���
		float xs=positionX-cx;
		float zs=positionZ-cz;
		
		float xo=another.positionX-cx;
		float zo=another.positionZ-cz;
		
		float disA=(float)(xs*xs+zs*zs);
		float disB=(float)(xo*xo+zo*zo);
		return ((disA-disB)==0)?0:((disA-disB)>0)?-1:1;  
	}

}
