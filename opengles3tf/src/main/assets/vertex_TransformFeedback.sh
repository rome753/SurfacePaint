#version 300 es
uniform int groupCount;//ÿ�����ӵĸ���
uniform int count;//��������λ�õļ�����
uniform float lifeSpanStep;//���������ڲ���
in vec4 tPosition;//����������ԣ���ʼλ��px,py,y�������ٶ�vy,�������������maxLifeSpan��
in vec4 aPosition;//����������ԣ���ǰλ��x,y,x������ٶ�vx,��ǰ������life��
out vec4 vPosition;//д�뵽�任���������������еĶ����������  
void main()     
{//������   
	vec4 position=aPosition;//��ȡ�����������
	if(aPosition.w==10.0)
	{//�ö���ĵ�ǰ������Ϊ10.0ʱ������δ����״̬
		int id=gl_VertexID;//��ȡ�ö����id��
		if((count*groupCount)<=id&&id<(count*groupCount+groupCount)) 
		{//�ö����id��λ��Ҫ�������λ�÷�Χ��
			position.w=lifeSpanStep;//���õ�ǰ������--����ö���
		}
		vPosition=position;//д�뵽�任����������������
	}else
	{//�ö���ĵ�ǰ�����ڲ�Ϊ10.0ʱ�����ڻ�Ծ״̬	
		position.w=position.w+lifeSpanStep;//���㵱ǰ������
		if(position.w>tPosition.w)
		{//��ǰ�����ڴ����������������-�������ö���Ļ�������
			position.x=tPosition.x;//���õ�ǰλ��x���꣨��ʼλ�ã�
			position.y=tPosition.y;//���õ�ǰλ��y���꣨��ʼλ�ã�
			position.w=10.0;//���õ�ǰ������--����δ����״̬
		}else
		{//��ǰ�����ڲ������������������ʱ--���㶥���˶�����һλ��
			position.x=position.x+position.z;//������һλ�õ�x����
			position.y=position.y+tPosition.z;//������һλ�õ�y����
		} 
		vPosition=position;//д�뵽�任����������������
	}
}                      