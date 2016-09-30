package com.saiteng.st_master.fragments;

import com.saiteng.st_master.R;
import com.saiteng.st_master.base.Config;
import com.saiteng.st_master.conn.ConnSocketServer;
import com.saiteng.st_master.conn.SendMSG;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 设备管理 设置参数 下的Fragment
 * @author nipengge
 *
 */
public class SubDeviceManagementFragment extends Fragment implements OnClickListener {
	
	private Button parameterButton; //确定按钮
	
	private Spinner setting_param; //上传间隔时间
	
	private RadioGroup vibrationGroup,standbyGroup,GPSGroup,WIFIGroup;//振动,待机,GPS,WIFI
	
	private String send_GPS_MSg = null;
	
	private String send_WIFI_MSg = null;
	
	private SharedPreferences shared;
	
	private Editor edit;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_sub_device_management, null);
		
		/*确定按钮*/
		parameterButton = (Button) view.findViewById(R.id.parameterButton);
		parameterButton.setOnClickListener(this);
		/*上传间隔*/
		setting_param = (Spinner) view.findViewById(R.id.setting_param);
		/*振动,待机,GPS,WIFI*/
		vibrationGroup = (RadioGroup) view.findViewById(R.id.vibrationGroup);
		standbyGroup = (RadioGroup) view.findViewById(R.id.standbyGroup);
		GPSGroup = (RadioGroup) view.findViewById(R.id.GPSGroup);
		WIFIGroup = (RadioGroup) view.findViewById(R.id.WIFIGroup);
		
		shared = getActivity().getSharedPreferences(Config.diviceIMEI+"Watchstatus",Context.MODE_PRIVATE);
		 
		edit = shared.edit();
		
		if(shared.getBoolean("GPS",false)){
			GPSGroup.check(R.id.GPSon);
		}else{
			GPSGroup.check(R.id.GPSoff);
		}
		
		if(shared.getBoolean("WIFI",false)){
			WIFIGroup.check(R.id.WIFIon);
		}else{
			WIFIGroup.check(R.id.WIFIoff);
		}
		
		if(shared.getBoolean("vibration",false)){
			vibrationGroup.check(R.id.vibrationon);
		}else{
			vibrationGroup.check(R.id.vibrationoff);
		}
		
		if(shared.getBoolean("standby",false)){
			standbyGroup.check(R.id.standbyon);
		}else{
			standbyGroup.check(R.id.standbyoff);
		}
		
		setting_param.setSelection(shared.getInt("settingIndex", 0));
		
		GPSGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if(arg0.getCheckedRadioButtonId()==R.id.GPSon){
					//发送打开GPS命令
					send_GPS_MSg = Config.order_gpson;
					edit.putBoolean("GPS", true);
				}else if(arg0.getCheckedRadioButtonId()==R.id.GPSoff){
					//发送关闭GPS命令
					send_GPS_MSg = Config.order_gpsoff;
					edit.putBoolean("GPS", false);
				}
				
			}
		});
		
		
		WIFIGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if(arg0.getCheckedRadioButtonId()==R.id.WIFIon){
					//发送打开wifi命令
					send_WIFI_MSg = Config.order_wifion;
					edit.putBoolean("WIFI", true);
				}else if(arg0.getCheckedRadioButtonId()==R.id.WIFIoff){
					//发送关闭wifi命令
					send_WIFI_MSg = Config.order_wifioff;
					edit.putBoolean("WIFI", false);
				}
				
				
			}
		});
		return view;
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view == parameterButton){
			 String codeString=  setting_param.getSelectedItem().toString();
			 System.out.println(codeString);
			 
			 String len = null;
				if("10秒".equals(codeString)){
					codeString ="10";
					len="0009";
					edit.putInt("settingIndex",0);
				}else if("1分钟".equals(codeString)){
					codeString ="1*60";
					len="0009";
					edit.putInt("settingIndex",1);
				}else if("5分钟".equals(codeString)){
					codeString ="1*60*5";
					len="0009";
					edit.putInt("settingIndex",2);
				}else if("10分钟".equals(codeString)){
					codeString ="1*60*50";
					len="0011";
					edit.putInt("settingIndex",3);
				}
				ConnSocketServer.sendOrder("[ST*"+Config.diviceIMEI+"*"+len+"*UPLOAD,"+codeString+"]");
				
				//发送相关命令短信
				if(send_GPS_MSg!=null){
					new SendMSG(getActivity(),send_GPS_MSg,Config.divicenum).start();
					send_GPS_MSg = null;
				} if(send_WIFI_MSg!=null){
					new SendMSG(getActivity(),send_WIFI_MSg,Config.divicenum).start();
					send_WIFI_MSg = null;
				}
				
				edit.commit();
				
			}
		
		}


}
