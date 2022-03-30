#version 300 es
uniform int groupCount;//每批粒子的个数
uniform int count;//激活粒子位置的计算器
uniform float lifeSpanStep;//粒子生命期步进
in vec4 tPosition;//顶点固有属性（起始位置px,py,y方法的速度vy,最大允许生命期maxLifeSpan）
in vec4 aPosition;//顶点基本属性（当前位置x,y,x方向的速度vx,当前生命期life）
out vec4 vPosition;//写入到变换反馈缓冲区对象中的顶点基本属性  
void main()     
{//主函数   
	vec4 position=aPosition;//获取顶点基本属性
	if(aPosition.w==10.0)
	{//该顶点的当前生命期为10.0时，处于未激活状态
		int id=gl_VertexID;//获取该顶点的id号
		if((count*groupCount)<=id&&id<(count*groupCount+groupCount)) 
		{//该顶点的id号位于要被激活的位置范围内
			position.w=lifeSpanStep;//设置当前生命期--激活该顶点
		}
		vPosition=position;//写入到变换反馈缓冲区对象中
	}else
	{//该顶点的当前生命期不为10.0时，处于活跃状态	
		position.w=position.w+lifeSpanStep;//计算当前生命期
		if(position.w>tPosition.w)
		{//当前生命期大于最大允许生命期-重新设置顶点的基本属性
			position.x=tPosition.x;//设置当前位置x坐标（起始位置）
			position.y=tPosition.y;//设置当前位置y坐标（起始位置）
			position.w=10.0;//设置当前生命期--处于未激活状态
		}else
		{//当前生命期不大于最大允许生命期时--计算顶点运动的下一位置
			position.x=position.x+position.z;//计算下一位置的x坐标
			position.y=position.y+tPosition.z;//计算下一位置的y坐标
		} 
		vPosition=position;//写入到变换反馈缓冲区对象中
	}
}                      