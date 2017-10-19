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
	private String[] musicNames=new String[]{"ͻȻ������-��嶼�","ħ���е���ʹ-�����",
			"������һ������ϰһ����-��嶼�","�ҵĸ�����-���ĭ","��ɫ�ռ�-��־��"};	
	Integer[] imgs ={ R.drawable.img1,R.drawable.img2, R.drawable.img3,
			R.drawable.img4,R.drawable.img5 };//��ȡR.drawable.img��ֵ
	MyAnimation myAnimation=new MyAnimation();//����������
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
	private boolean isPlaying=false;//�ж����ֲ���״̬
	private int musicIndex;//���service��musicIndex���ı������
	public Handler handler=new Handler();//����UI������������handler
	public Runnable runnable=new Runnable() {		
		@Override
		public void run() {
			if(myService.mp.isPlaying()){//��serviceͨ�Ž���
				state_tv.setText("Playing");//getResources().getString(R.string.playing));
			}else{
				state_tv.setText("Pause");
			}
			//����ʱ�����(�Ѳ�������ǰ���ź�������ʽ��Ϊ��mm:ss����ʽ��ʱ��)
			musicTime_tv.setText(simpleDataFormat.format(myService.mp.getCurrentPosition()));
			musicAllTime_tv.setText(simpleDataFormat.format(myService.mp.getDuration()));
			seekBar.setProgress(myService.mp.getCurrentPosition());
			seekBar.setMax(myService.mp.getDuration());
			//�������������������ڲ���
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
			
			//��ʱ0.1s���ֽ��̼߳��뵽�̶߳�����
			handler.postDelayed(runnable, 100);
		}
	}; 
	
	//����㲥�������࣬������UI
	public class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int backFlag=intent.getExtras().getInt("backFlag");
			Log.i("info", "�㲥���ܵ���������");
			switch (backFlag) {
			case 0://ֹͣ����
				myAnimation.pauseAnimation(img_view);
				break;
			case 1://��������
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
		
		//��̬ע��㲥������
		myBc=new MyBroadcastReceiver();
		IntentFilter intentFilter=new IntentFilter("myBc");
		registerReceiver(myBc, intentFilter);
		
		//bindģʽservice��ʼ����
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
	
	//��bindģʽ�󶨷���
	private void binServiceConnection() {
		Intent intent=new Intent(MainActivity.this,MyService.class);
		startService(intent);
		bindService(intent, sc, this.BIND_AUTO_CREATE);//bindsService�ص�onServiceConnented����	
	}

	//bindsService�ص�onServiceConnented����
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
			case R.id.pre_btn://������һ�׸���
				myService.preMusic();
				if(musicIndex>0){
					musicIndex--;
				}else{
					musicIndex=musicNames.length-1;
				}	

				break;
				
			case R.id.play_btn://���Ż�����ͣ����
				myService.playOrPauseMusic();				
				break;
				
			case R.id.next_btn://������һ�׸���
				myService.nextMusic();
				if(musicIndex<musicNames.length-1){
					musicIndex++;
				}else{
					musicIndex=0;
				}			
				break;
			}
			
			//���¸���ͼƬ
			img_view.setImageResource(imgs[musicIndex]);
			//���¸���
			musicName_tv.setText(musicNames[musicIndex]);		
		}		
	}
	
	//�տ�ʼ�����½���Ӧ��ʱ�ܸ���UI
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
		handler.post(runnable);//��������UI
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		unbindService(sc);
		unregisterReceiver(myBc);
		super.onDestroy();
	}
}
