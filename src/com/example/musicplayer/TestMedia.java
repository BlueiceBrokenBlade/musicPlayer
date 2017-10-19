package com.example.musicplayer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

public class TestMedia extends Activity {
	private SeekBar skb_music=null;
	private Button startMusic_btn;
	private Button stopMusci_btn;
	
	private SeekBar skb_video=null;
	private Button startVideo_btn;
	private Button stopVideo_btn;
	private SurfaceView surfaceView;//它的特性是：可以在主线程之外的线程中向屏幕绘
	//图上。这样可以避免画图任务繁重的时候造成主线程阻塞，从而提高了程序的反应速度。
	private SurfaceHolder surfaceHolder;//SurfaceView控制器
	
	private MediaPlayer mediaPlayer;
	private Timer mTimer;
	private TimerTask mTimerTask;
	
	private boolean isChanging=false;//互斥变量，防止定时器与SeekBar拖动时进度冲突
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testmedia);
		
		mediaPlayer=new MediaPlayer();
		
		//播放结束之后弹出提示
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {			
			@Override
			public void onCompletion(MediaPlayer mp) {
				Toast.makeText(TestMedia.this, "结束", Toast.LENGTH_SHORT).show();
				mediaPlayer.release();//释放资源
			}
		});
		
		//定时器记录播放进度
		mTimer=new Timer();
		mTimerTask=new TimerTask() {			
			@Override
			public void run() {
				if(isChanging==true)
					return ;
				if(mediaPlayer.isPlaying()){
					if(mediaPlayer.getVideoHeight()==0){
						skb_music.setProgress(mediaPlayer.getCurrentPosition());
					}else{
						skb_video.setProgress(mediaPlayer.getCurrentPosition());
					}
				}
			}
		};
		
		mTimer.schedule(mTimerTask, 0 ,10);//从现在起过0毫秒以后，每隔10毫秒执行一次。  
		
		startMusic_btn=(Button) findViewById(R.id.button1);
		stopMusci_btn=(Button) findViewById(R.id.button2);
		startMusic_btn.setOnClickListener(new myListener());
		stopMusci_btn.setOnClickListener(new myListener());
		skb_music=(SeekBar) findViewById(R.id.seekBar1);
		skb_music.setOnSeekBarChangeListener(new SeekBarChangeEvent());
		
		startVideo_btn=(Button) findViewById(R.id.button3);
		stopVideo_btn=(Button) findViewById(R.id.button4);
		startVideo_btn.setOnClickListener(new myListener());
		stopVideo_btn.setOnClickListener(new myListener());
		skb_video=(SeekBar) findViewById(R.id.seekBar2);
		skb_video.setOnSeekBarChangeListener(new SeekBarChangeEvent());
		
		surfaceView=(SurfaceView) findViewById(R.id.SurfaceView1);
		surfaceHolder=surfaceView.getHolder();
		surfaceHolder.setFixedSize(100, 100);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
	}
	
	class myListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1://开始播放音乐
				
				mediaPlayer.reset();
				mediaPlayer=MediaPlayer.create(TestMedia.this, 
						R.raw.big);//读取音频
				skb_music.setMax(mediaPlayer.getDuration());
				mediaPlayer.start();
				break;
			case R.id.button2:
				mediaPlayer.stop();//停止播放音乐
				break;
			case R.id.button3://开始播放视频
				mediaPlayer.reset();
				mediaPlayer=MediaPlayer.create(TestMedia.this, R.raw.test);
				skb_video.setMax(mediaPlayer.getDuration());
				mediaPlayer.setDisplay(surfaceHolder);//设置屏障
				mediaPlayer.start();
				break;
			case R.id.button4:
				mediaPlayer.stop();//停止播放视频
				break;
			default:
				break;
			}
			
		}		
	}
	
	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			mediaPlayer.seekTo(seekBar.getProgress());
			isChanging=false;
		}
		
	}
}
