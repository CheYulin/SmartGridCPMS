package com.example.asg;
import com.example.asg.services.meteraccess.Connector;
import com.example.asg.services.meteraccess.MeterFather;

import android.app.Application;
/**
 * ����һ��ȫ�ֱ����Ĺ����࣬�����е�activity�д��ݹ�����Ϣ
 * @author Administrator
 *
 */
public class ASGApplication extends Application{
	Connector connector = null;
	MeterFather meter = null;
	
	public void setConnector(Connector connector){
		this.connector = connector;
	}
	
	public Connector getConnector(){
		return this.connector;
	}
	
	public MeterFather getMeter(){
		return this.meter;
	}
	
	public void setMeter(MeterFather meter){
		this.meter = meter;
	}
}
