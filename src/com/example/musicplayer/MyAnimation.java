package com.example.musicplayer;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

public class MyAnimation {
	private RotateAnimation animation;
	public void startAnimation(View view){
		animation=new RotateAnimation(0, 359, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(20000);//转一圈的时间
		animation.setRepeatCount(Animation.INFINITE);//重复数量：设置无限循环
		animation.setRepeatMode(Animation.RESTART);//重复模式:
		view.startAnimation(animation);
	}
	
	public void pauseAnimation(View view){
		view.clearAnimation();
	}
	
	
}
