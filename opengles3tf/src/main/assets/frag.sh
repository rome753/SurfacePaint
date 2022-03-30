#version 300 es
precision mediump float;
uniform vec4 startColor;//起始颜色
uniform vec4 endColor;//终止颜色
uniform float bj;//半径
uniform sampler2D sTexture;//纹理内容数据

in vec4 vPosition;
in float sjFactor;//衰减因子

out vec4 fragColor; 
void main()                         
{   
	if(vPosition.w==10.0)
	{
		fragColor=vec4(0.0,0.0,0.0,0.0);
	}else
	{
		vec2 texCoor=gl_PointCoord;   		//从内建变量获取纹理坐标
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