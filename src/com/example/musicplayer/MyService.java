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
	
	public MediaPlayer mp=new MediaPlayer();//实例一个媒体播放器
	public MyService(){
		try {
			musicIndex=0;
			mp.setDataSource(musicDir[musicIndex]);
			mp.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	//音乐的播放和暂停
	public void playOrPauseMusic(){
		if(mp.isPlaying()){
			mp.pause();
			sendAnimationUi(0);
		}else{
			mp.start();
			sendAnimationUi(1);
		}
	}
	
	
	//播放下一首歌曲
	public void nextMusic(){
		if(mp!=null){
			mp.stop();
			mp.reset();
			try {	
				//控制音乐循环列表播放
				if(musicIndex<musicDir.length-1){
					musicIndex++;
				}else{//当musicIndex为下标最大值时，归为0
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
	
	//播放上一首歌曲
	public void preMusic(){
		if(mp!=null){
			mp.stop();
			mp.reset();
			try {
				if(musicIndex>0){
					musicIndex--;
				}else{//当musicIndex=0时，上一首归为下标最大值
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
	
	//更新我那一直不能正确动停的旋转动画
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
