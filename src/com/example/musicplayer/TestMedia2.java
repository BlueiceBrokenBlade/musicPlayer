package com.example.musicplayer;

import java.io.IOException;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
/**
 * 
 * @author xhx12366
 *Android MediaPlayer����ʹ�÷�ʽ
 */
public class TestMedia2 extends Activity implements OnClickListener{
	private Button start_btn;
	private Button pause_btn;//��ͣ
	private Button stop_btn;
	private MediaPlayer m;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testmedia2);
		
		start_btn=(Button) findViewById(R.id.button1);
		start_btn.setOnClickListener(this);
		stop_btn=(Button) findViewById(R.id.button2);
		stop_btn.setOnClickListener(this);
		pause_btn=(Button) findViewById(R.id.button3);
		pause_btn.setOnClickListener(this);
		m=new MediaPlayer();
			
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1://��ʼ����
			try {
				m.setDataSource("/sdcard/big.mp3");
				m.prepare();
				m.start();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			m.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					m.release();
				}
			});
			break;
		case R.id.button2://ֹͣ����
			if(m!=null){
				m.stop();
			}
			break;
		case R.id.button3://��ͣ����
			if(m!=null){
				m.pause();
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(m!=null){
			m.release();
		}
		super.onDestroy();
	}
}
