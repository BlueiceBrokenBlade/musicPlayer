package com.example.musicplayer;

import java.text.SimpleDateFormat;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {
	private SimpleDateFormat simpleDataFormat=new SimpleDateFormat("mm:ss");
	private String[] musicNames=new String[]{"突然想起你-林宥嘉","魔鬼中的天使-田馥甄",
			"我总是一个人练习一个人-林宥嘉","我的歌声里-李代沫","灰色空间-罗志祥"};	
	Integer[] imgs ={ R.drawable.img1,R.drawable.img2, R.drawable.img3,
			R.drawable.img4,R.drawable.img5 };//获取R.drawable.img的值
	MyAnimation myAnimation=new MyAnimation();//动画操作类
	MyBroadcastReceiver myBc;
	private ImageView img_view;
	private TextView musicTime_tv;
	private TextView musicAllTime_tv;
	private SeekBar seekBar;
	private TextView musicName_tv;
	private TextView state_tv;
	private MyService myService=new MyService();
	private Button play_btn;
	private Button pre_btn;
	private Button next_btn;
	private boolean isPlaying=false;//判断音乐播放状态
	private int musicIndex;//获得service中musicIndex，改变歌曲名
	public Handler handler=new Handler();//更新UI（进度条）的handler
	public Runnable runnable=new Runnable() {		
		@Override
		public void run() {
			if(myService.mp.isPlaying()){//与service通信交互
				state_tv.setText("Playing");//getResources().getString(R.string.playing));
			}else{
				state_tv.setText("Pause");
			}
			//更新时间进度(把播放器当前播放毫秒数格式化为“mm:ss”格式的时间)
			musicTime_tv.setText(simpleDataFormat.format(myService.mp.getCurrentPosition()));
			musicAllTime_tv.setText(simpleDataFormat.format(myService.mp.getDuration()));
			seekBar.setProgress(myService.mp.getCurrentPosition());
			seekBar.setMax(myService.mp.getDuration());
			//进度条监听器反作用于播放
			seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {			
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {}			
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {}			
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					if(fromUser){
						myService.mp.seekTo(seekBar.getProgress());
						musicIndex=myService.getMusicIndex();
					}
				}
			});
			
			//延时0.1s后又将线程加入到线程队列中
			handler.postDelayed(runnable, 100);
		}
	}; 
	
	//定义广播接受者类，处理动画UI
	public class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int backFlag=intent.getExtras().getInt("backFlag");
			Log.i("info", "广播接受到动画处理");
			switch (backFlag) {
			case 0://停止动画
				myAnimation.pauseAnimation(img_view);
				break;
			case 1://启动动画
				myAnimation.startAnimation(img_view);
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//动态注册广播接受者
		myBc=new MyBroadcastReceiver();
		IntentFilter intentFilter=new IntentFilter("myBc");
		registerReceiver(myBc, intentFilter);
		
		//bind模式service开始服务
		binServiceConnection();
		

		img_view=(ImageView) findViewById(R.id.music_imgView);	
		musicTime_tv=(TextView) findViewById(R.id.musicTime_tv);
		musicAllTime_tv=(TextView) findViewById(R.id.musicAllTime_tv);
		musicName_tv=(TextView) findViewById(R.id.musicName_tv);
		state_tv=(TextView) findViewById(R.id.state_tv);
		seekBar=(SeekBar) findViewById(R.id.seekBar);
		play_btn=(Button) findViewById(R.id.play_btn);
		pre_btn=(Button) findViewById(R.id.pre_btn);
		next_btn=(Button) findViewById(R.id.next_btn);
		play_btn.setOnClickListener(new myListener());
		pre_btn.setOnClickListener(new myListener());
		next_btn.setOnClickListener(new myListener());
		
		
	}
	
	//以bind模式绑定服务
	private void binServiceConnection() {
		Intent intent=new Intent(MainActivity.this,MyService.class);
		startService(intent);
		bindService(intent, sc, this.BIND_AUTO_CREATE);//bindsService回调onServiceConnented函数	
	}

	//bindsService回调onServiceConnented函数
	private ServiceConnection sc=new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			myService=null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder iBinder) {
			myService=((MyService.MyBinder)iBinder).getService();
		}
	};
	
	class myListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.pre_btn://播放上一首歌曲
				myService.preMusic();
				if(musicIndex>0){
					musicIndex--;
				}else{
					musicIndex=musicNames.length-1;
				}	

				break;
				
			case R.id.play_btn://播放或者暂停歌曲
				myService.playOrPauseMusic();				
				break;
				
			case R.id.next_btn://播放下一首歌曲
				myService.nextMusic();
				if(musicIndex<musicNames.length-1){
					musicIndex++;
				}else{
					musicIndex=0;
				}			
				break;
			}
			
			//更新歌曲图片
			img_view.setImageResource(imgs[musicIndex]);
			//更新歌名
			musicName_tv.setText(musicNames[musicIndex]);		
		}		
	}
	
	//刚开始和重新进入应用时能更新UI
	@Override
	protected void onResume() {
		if(myService.mp.isPlaying()){
			state_tv.setText("Playing");
		}else{
			state_tv.setText("Pause");
		}
		
		musicName_tv.setText(musicNames[musicIndex]);
		seekBar.setProgress(myService.mp.getCurrentPosition());
		seekBar.setMax(myService.mp.getDuration());
		handler.post(runnable);//持续更新UI
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		unbindService(sc);
		unregisterReceiver(myBc);
		super.onDestroy();
	}
}
