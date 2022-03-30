package com.bn.Sample3_16;

import android.opengl.GLES30;
     
public class ParticleDataConstant 
{       
	//ǽ��ĳ������ű�
	public static float wallsLength=30;
	//��ǰ����  
    public static int CURR_INDEX=3;
    //����ĳ�ʼ��λ��  
     public static float distancesFireXZ=6;
    //����ĳ�ʼ��λ��  
     public static float distancesBrazierXZ=6;
    public static float[][] positionFireXZ={{distancesFireXZ,distancesFireXZ},{distancesFireXZ,-distancesFireXZ},{-distancesFireXZ,distancesFireXZ},{-distancesFireXZ,-distancesFireXZ}};
    public static float[][] positionBrazierXZ={{distancesBrazierXZ,distancesBrazierXZ},{distancesBrazierXZ,-distancesBrazierXZ},{-distancesBrazierXZ,distancesBrazierXZ},{-distancesBrazierXZ,-distancesBrazierXZ}};
    public static int walls[]=new int[6];
    //��ʼ��ɫ
    public static final float[][] START_COLOR=
	{
    	{0.7569f,0.2471f,0.1176f,1.0f},	//0-��ͨ����
    	{0.7569f,0.2471f,0.1176f,1.0f},	//1-��������
    	{0.6f,0.6f,0.6f,1.0f},			//2-��ͨ��
    	{0.6f,0.6f,0.6f,1.0f},			//3-������
	};
    
    //��ֹ��ɫ
    public static final float[][] END_COLOR=
	{
    	{0.0f,0.0f,0.0f,0.0f},	//0-��ͨ����
    	{0.0f,0.0f,0.0f,0.0f},	//1-��������
    	{0.0f,0.0f,0.0f,0.0f},	//2-��ͨ��
    	{0.0f,0.0f,0.0f,0.0f},	//3-������
	};
    
    //Դ�������
    public static final int[] SRC_BLEND=
	{
    	GLES30.GL_SRC_ALPHA,   				//0-��ͨ����
    	GLES30.GL_ONE,   					//1-��������
    	GLES30.GL_SRC_ALPHA,				//2-��ͨ��
    	GLES30.GL_ONE,						//3-������
	};
    
    //Ŀ��������
    public static final int[] DST_BLEND=
	{
    	GLES30.GL_ONE,      				//0-��ͨ����
    	GLES30.GL_ONE,      				//1-��������
    	GLES30.GL_ONE_MINUS_SRC_ALPHA,		//2-��ͨ��
    	GLES30.GL_ONE,						//3-������
	};
    
    //��Ϸ�ʽ
    public static final int[] BLEND_FUNC=
	{
    	GLES30.GL_FUNC_ADD,    				//0-��ͨ����
    	GLES30.GL_FUNC_ADD,    				//1-��������
    	GLES30.GL_FUNC_ADD,    				//2-��ͨ��
    	GLES30.GL_FUNC_REVERSE_SUBTRACT,	//3-������
	};
    
    //
    public static final int[] COUNT=
	{
    	340,   				//0-��ͨ����
    	340,   					//1-��������
    	99,				//2-��ͨ��
    	99,						//3-������
	};
    //�������Ӱ뾶
    public static final float[] RADIS=
    {
    	60*0.5f,		//0-��ͨ����
    	60*0.5f,		//1-��������
    	60*0.8f,		//2-��ͨ��
    	60*0.8f,		//3-������
    };
    
    //�������������
    public static final float[] MAX_LIFE_SPAN=
    {
    	5.0f,		//0-��ͨ����6
    	5.0f,		//1-��������6
    	6.0f,		//2-��ͨ��7
    	6.0f,		//3-������7
    };
    
    //�����������ڲ���
    public static final float[] LIFE_SPAN_STEP=
    {
    	0.07f,		//0-��ͨ����0.07
    	0.07f,		//1-��������
    	0.07f,		//2-��ͨ��
    	0.07f,		//3-������
    };
    
    //���ӷ����X���ҷ�Χ
    public static final float[] X_RANGE=
	{
	    0.5f,		//0-��ͨ����
	    0.5f,		//1-��������
	    0.5f,		//2-��ͨ��
	    0.5f,		//3-������
	};
    
    //���ӷ����Y���·�Χ
    public static final float[] Y_RANGE=
	{
	    0.3f,		//0-��ͨ����
	    0.3f,		//1-��������
	    0.15f,		//2-��ͨ��
	    0.15f,		//3-������
	};
    
    //ÿ���緢���������
    public static final int[] GROUP_COUNT=
	{
    	4,			//0-��ͨ����
    	4,			//1-��������
    	1,			//2-��ͨ��
    	1,			//3-������
	};
    
    //����Y�������ڵ��ٶ�
    public static final float[] VY=
	{
    	0.05f,		//0-��ͨ����
    	0.05f,		//1-��������
    	0.04f,		//2-��ͨ��
    	0.04f,		//3-������
	};
  
    //���Ӹ��������߳���Ϣʱ��
    public static final int[] THREAD_SLEEP=
    {
    	60,		//0-��ͨ����
    	60,		//1-��������
    	30,		//2-��ͨ��
    	30,		//3-������
    };
}
