package com.example.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
	private String[] musicDir=new String[]{"/sdcard/music/song1.mp3",
			"/sdcard/music/song2.mp3","/sdcard/music/song3.mp3",
			"/sdcard/music/song4.mp3","/sdcard/music/song5.mp3"};
//	private String[] musicDir=new String[]{
//			Environment.getExternalStorageDirectory().getAbsolutePath()+"/Music/song1.mp3",
//			Environment.getExternalStorageDirectory().getAbsolutePath()+"/Music/song2.mp3",
//			Environment.getExternalStorageDirectory().getAbsolutePath()+"/Music/song3.mp3",
//			Environment.getExternalStorageDirectory().getAbsolutePath()+"/Music/song4.mp3",
//			Environment.getExternalStorageDirectory().getAbsolutePath()+"/Music/song5.mp3"};

			
	private int musicIndex;
	private Intent intent;
	private Bundle bundle;
	
	public MediaPlayer mp=new MediaPlayer();//ʵ��һ��ý�岥����
	public MyService(){
		try {
			musicIndex=0;
			mp.setDataSource(musicDir[musicIndex]);
			mp.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	//���ֵĲ��ź���ͣ
	public void playOrPauseMusic(){
		if(mp.isPlaying()){
			mp.pause();
			sendAnimationUi(0);
		}else{
			mp.start();
			sendAnimationUi(1);
		}
	}
	
	
	//������һ�׸���
	public void nextMusic(){
		if(mp!=null){
			mp.stop();
			mp.reset();
			try {	
				//��������ѭ���б���
				if(musicIndex<musicDir.length-1){
					musicIndex++;
				}else{//��musicIndexΪ�±����ֵʱ����Ϊ0
					musicIndex=0;
				}
				mp.setDataSource(musicDir[musicIndex]);							
				mp.prepare();
				mp.start();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		sendAnimationUi(1);
	}
	
	//������һ�׸���
	public void preMusic(){
		if(mp!=null){
			mp.stop();
			mp.reset();
			try {
				if(musicIndex>0){
					musicIndex--;
				}else{//��musicIndex=0ʱ����һ�׹�Ϊ�±����ֵ
					musicIndex=musicDir.length-1;
				}
				mp.setDataSource(musicDir[musicIndex]);				
				mp.prepare();
				mp.start();
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		
		sendAnimationUi(1);
	}
	
	//��������һֱ������ȷ��ͣ����ת����
	public void sendAnimationUi(int flag){
		intent=new Intent();
		bundle=new Bundle();
		bundle.putInt("backFlag", flag);
		intent.putExtras(bundle);
		intent.setAction("myBc");
		sendBroadcast(intent);
	}
	
	public class MyBinder extends Binder{
		public MyService getService(){
			return  MyService.this;		
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i("MyService","onBind");
		return new MyBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("MyService","onCreate");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("MyService","onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		Log.i("MyService","onDestroy");
		super.onDestroy();
	}

	public int getMusicIndex() {
		return musicIndex;
	}

	public void setMusicIndex(int musicIndex) {
		this.musicIndex = musicIndex;
	}
	
	
}
