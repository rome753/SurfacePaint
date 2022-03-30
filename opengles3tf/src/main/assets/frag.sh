#version 300 es
precision mediump float;
uniform vec4 startColor;//��ʼ��ɫ
uniform vec4 endColor;//��ֹ��ɫ
uniform float bj;//�뾶
uniform sampler2D sTexture;//������������

in vec4 vPosition;
in float sjFactor;//˥������

out vec4 fragColor; 
void main()                         
{   
	if(vPosition.w==10.0)
	{
		fragColor=vec4(0.0,0.0,0.0,0.0);
	}else
	{
		vec2 texCoor=gl_PointCoord;   		//���ڽ�������ȡ��������
		vec4 colorTL = texture(sTexture, texCoor); 
    	vec4 colorT;
   	 	float disT=distance(vPosition.xyz,vec3(0.0,0.0,0.0));
    	float tampFactor=(1.0-disT/bj)*sjFactor;
    	vec4 factor4=vec4(tampFactor,tampFactor,tampFactor,tampFactor);
    	colorT=clamp(factor4,endColor,startColor);
    	colorT=colorT*colorTL.a;  
    	fragColor=colorT;
	}            
}              