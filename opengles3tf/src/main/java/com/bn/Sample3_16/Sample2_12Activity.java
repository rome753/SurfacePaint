package com.bn.Sample3_16;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class Sample2_12Activity extends Activity {
	private MySurfaceView mGLSurfaceView;
	//��Ļ��Ӧ�Ŀ�Ⱥ͸߶�
	static float WIDTH;
	static float HEIGHT;
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);         
        //����Ϊȫ��
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//���ϵͳ�Ŀ���Լ��߶�
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if(dm.widthPixels>dm.heightPixels)
        {
        	WIDTH=dm.widthPixels;
        	HEIGHT=dm.heightPixels;
        }
        else
        {
        	WIDTH=dm.heightPixels;
        	HEIGHT=dm.widthPixels;
        }
		//����Ϊ����ģʽ
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//��ʼ��GLSurfaceView
        mGLSurfaceView = new MySurfaceView(this);
        //�л���������
        setContentView(mGLSurfaceView);	
        mGLSurfaceView.requestFocus();//��ȡ����
        mGLSurfaceView.setFocusableInTouchMode(true);//����Ϊ�ɴ���  
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }    
}



