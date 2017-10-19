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
		animation.setDuration(20000);//תһȦ��ʱ��
		animation.setRepeatCount(Animation.INFINITE);//�ظ���������������ѭ��
		animation.setRepeatMode(Animation.RESTART);//�ظ�ģʽ:
		view.startAnimation(animation);
	}
	
	public void pauseAnimation(View view){
		view.clearAnimation();
	}
	
	
}
