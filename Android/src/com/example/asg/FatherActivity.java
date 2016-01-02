package com.example.asg;

import com.example.asg.R;
import com.example.asg.services.meteraccess.Connector;
import com.example.asg.services.meteraccess.MeterFather;

import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Toast;
/**
 * this is the father for all the activities
 * 
 * @author Owen
 * 
 */
public class FatherActivity extends Activity {
	public Connector connector = null;
	public MeterFather meter = null;
	private long mExitTime;// ������Ļ�˳�����ʱ���
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				connector = ((ASGApplication)getApplication()).getConnector();
				//meter = ((ASGApplication)getApplication()).getMeter();
				if(connector != null){
					connector.disconnect();
					((ASGApplication)getApplication()).setConnector(null);
				}
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_father, menu);
		return true;
	}
}
