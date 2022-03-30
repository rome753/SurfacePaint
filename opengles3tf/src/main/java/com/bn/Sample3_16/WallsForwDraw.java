package com.bn.Sample3_16;

public class WallsForwDraw {
	
	MySurfaceView mv;
	Wall wall;
	public WallsForwDraw(MySurfaceView mv)
	{
		this.mv=mv;
		wall=new Wall(this.mv,ParticleDataConstant.wallsLength);
	}
	
	public void drawSelf()
	{
		//绘制第一面墙-底
		MatrixState.pushMatrix();
		MatrixState.translate(0, 0, 0);
		wall.drawSelf(ParticleDataConstant.walls[0]);
		MatrixState.popMatrix();
		
		//绘制第二面墙-上
		MatrixState.pushMatrix();
		MatrixState.translate(0,2*ParticleDataConstant.wallsLength, 0);
		wall.drawSelf(ParticleDataConstant.walls[1]);
		MatrixState.popMatrix();
		
		//绘制第三面墙-左
		MatrixState.pushMatrix();
		MatrixState.translate(-ParticleDataConstant.wallsLength, ParticleDataConstant.wallsLength, 0);
		MatrixState.rotate(90, 0, 0, 1);
		MatrixState.rotate(-90, 0, 1, 0);
		wall.drawSelf(ParticleDataConstant.walls[2]);
		MatrixState.popMatrix();
		
		//绘制第四面墙-右
		MatrixState.pushMatrix();
		MatrixState.translate(ParticleDataConstant.wallsLength, ParticleDataConstant.wallsLength, 0);
		MatrixState.rotate(-90, 0, 0, 1);
		MatrixState.rotate(90, 0, 1, 0);
		wall.drawSelf(ParticleDataConstant.walls[3]);
		MatrixState.popMatrix();
		
		//绘制第五面墙-前
		MatrixState.pushMatrix();
		MatrixState.translate(0, ParticleDataConstant.wallsLength,ParticleDataConstant.wallsLength);
		MatrixState.rotate(90, 1, 0, 0);
		wall.drawSelf(ParticleDataConstant.walls[4]);
		MatrixState.popMatrix();
		
		//绘制第六面墙-后
		MatrixState.pushMatrix();
		MatrixState.translate(0, ParticleDataConstant.wallsLength,-ParticleDataConstant.wallsLength);
		MatrixState.rotate(90, 1, 0, 0);
		MatrixState.rotate(180, 0, 0, 1);
		wall.drawSelf(ParticleDataConstant.walls[5]);
		MatrixState.popMatrix();
	}

}
