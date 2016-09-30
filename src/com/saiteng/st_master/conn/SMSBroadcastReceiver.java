package com.saiteng.st_master.conn;

import com.saiteng.st_master.base.Config;
import com.saiteng.st_master.fragments.Menu_ManageFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSBroadcastReceiver  extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		 Object[] pduses= (Object[])intent.getExtras().get("pdus");  
	        for(Object pdus: pduses){  
	            byte[] pdusmessage = (byte[])pdus;  
	            SmsMessage sms = SmsMessage.createFromPdu(pdusmessage);  
	            String mobile = sms.getOriginatingAddress();//发送短信的手机号码  
	            String content = sms.getMessageBody(); //短信内容 
	            Handler handler = Menu_ManageFragment.getHandler();
	            if(handler!=null){
	            	if(Config.revice_gpson.equals(content)){
	            		handler.sendEmptyMessage(2);
	            		Toast.makeText(context, "GPS OPen11", Toast.LENGTH_LONG).show();
	                }else if(Config.revice_gpsoff.equals(content)){
	                	handler.sendEmptyMessage(3);
	                }else if(Config.revice_wifioff.equals(content)){
	                	handler.sendEmptyMessage(4);
	                }else if(Config.revice_wifioff.equals(content)){
	                	handler.sendEmptyMessage(5);
	                }
	            }
	            
	        }
		
	}
}

	
