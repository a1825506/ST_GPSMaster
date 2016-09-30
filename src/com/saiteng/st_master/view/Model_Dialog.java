package com.saiteng.st_master.view;

import com.saiteng.st_master.R;
import com.saiteng.st_master.base.Config;
import com.saiteng.st_master.conn.SendMSG;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class Model_Dialog extends Dialog implements android.view.View.OnClickListener{
	
	private Context context;
	private Button btn_modelOn,btn_modelOff;
	private RadioGroup btn_gps,btn_wifi;
    private String send_GPS_MSg = null;
    private String send_WIFI_MSg = null;

	public Model_Dialog(Context context) {
		
		super(context);
		
		this.context=context;
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_model);
		initView();
	}

	private void initView() {
		btn_modelOn=(Button) findViewById(R.id.btn_modelon);
		
		btn_modelOff = (Button) findViewById(R.id.btn_modeloff);
		btn_gps = (RadioGroup) findViewById(R.id.RadioGroup_gps);
		btn_wifi =(RadioGroup) findViewById(R.id.RadioGroup_wifi);
		btn_modelOn.setOnClickListener(this);
		btn_modelOff.setOnClickListener(this);
		btn_gps.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if(arg0.getCheckedRadioButtonId()==R.id.gps_on){
					//发送打开GPS命令
					send_GPS_MSg = Config.order_gpson;
				}else if(arg0.getCheckedRadioButtonId()==R.id.gps_off){
					//发送关闭GPS命令
					send_GPS_MSg = Config.order_gpsoff;
				}
				
			}
		});
		btn_wifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if(arg0.getCheckedRadioButtonId()==R.id.wifi_on){
					//发送打开wifi命令
					send_WIFI_MSg = Config.order_wifion;
				}else if(arg0.getCheckedRadioButtonId()==R.id.wifi_on){
					//发送关闭wifi命令
					send_WIFI_MSg = Config.order_wifioff;
				}
				
				
			}
		});
		
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_modelon:
			//发送相关命令短信
			if(send_GPS_MSg!=null){
				new SendMSG(context,send_GPS_MSg,Config.divicenum).start();
			} if(send_WIFI_MSg!=null){
				new SendMSG(context,send_WIFI_MSg,Config.divicenum).start();
			}
			dismiss();
			break;
			
		case R.id.btn_modeloff:
			
			dismiss();
			break;

		default:
			break;
		}
		
	}

}
