#version 300 es
uniform mat4 uMVPMatrix; //�ܱ任����
uniform mat4 uMMatrix; //�任����
uniform vec3 uLightLocation;//��Դλ��
uniform vec3 uCamera;	//�����λ��
in vec3 aPosition;  //����λ��
in vec3 aNormal;    //���㷨����
in vec2 aTexCoor;    //������������
//���ڴ��ݸ�ƬԪ��ɫ���ı���
out vec4 ambient;
out vec4 diffuse;
out vec2 vTextureCoord;  //���ڴ��ݸ�ƬԪ��ɫ���ı���

//��λ����ռ���ķ���
void pointLight(				 //��λ����ռ���ķ���
  in vec3 normal,				 //������
  inout vec4 ambient,			 //����������ǿ��
  inout vec4 diffuse,			 //ɢ�������ǿ��
  in vec3 lightLocation,		 //��Դλ��
  in vec4 lightAmbient,			 //������ǿ��
  in vec4 lightDiffuse		 //ɢ���ǿ��
){
  ambient=lightAmbient;			//ֱ�ӵó������������ǿ��  
  vec3 normalTarget=aPosition+normal;	//����任��ķ�����
  vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(aPosition,1)).xyz;
  newNormal=normalize(newNormal); 	//�Է��������
  //����ӱ���㵽�����������
  vec3 eye= normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);  
  //����ӱ���㵽��Դλ�õ�����vp
  vec3 vp= normalize(lightLocation-(uMMatrix*vec4(aPosition,1)).xyz);  
  vp=normalize(vp);//��ʽ��vp
  vec3 halfVector=normalize(vp+eye);	//����������ߵİ�����    
  float shininess=50.0;				//�ֲڶȣ�ԽСԽ�⻬
  float nDotViewPosition=max(0.0,dot(newNormal,vp)); 	//��������vp�ĵ����0�����ֵ
  diffuse=lightDiffuse*nDotViewPosition;				//����ɢ��������ǿ��
}
void main()     
{                            		
   gl_Position = uMVPMatrix * vec4(aPosition,1); //�����ܱ任�������˴λ��ƴ˶���λ��
   vec4 ambientTemp, diffuseTemp;   //��Ż����⡢ɢ������ʱ����  
   pointLight(normalize(aNormal),ambientTemp,diffuseTemp,uLightLocation,vec4(0.05,0.05,0.05,1.0),vec4(0.5,0.5,0.5,1.0)); 
   
   ambient=ambientTemp;//�����յĻ����⴫��ƬԪ��ɫ��
   diffuse=diffuseTemp;//�����յ�ɢ���ǿ�ȴ���ƬԪ��ɫ��
   vTextureCoord = aTexCoor;//�����յ��������괫�ݸ�ƬԪ��ɫ��
}                      