#version 300 es
precision mediump float;
uniform sampler2D sTexture;//������������
//���մӶ�����ɫ�������Ĳ���
in vec4 ambient;
in vec4 diffuse;
in vec2 vTextureCoord; 
out vec4 fragColor; 
void main()                         
{        
	//�����������ɫ����ƬԪ
   vec4 finalColor=texture(sTexture, vTextureCoord);     
   fragColor = finalColor*ambient+finalColor*diffuse; 
}              