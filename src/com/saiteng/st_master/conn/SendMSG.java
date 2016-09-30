package com.saiteng.st_master.conn;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SendMSG extends Thread{
	
	private Context mcontext;
	
	private String msg=null;
	
	private String number = null;
	
	public SendMSG(Context context,String message,String num){
		
		this.mcontext = context;
		
		this.msg = message;
		
		this.number = num;
		
	}
	
	@Override
	public void run() {
		
		super.run();
		
		if("".equals(number)||isMobile(number)){
			
			Toast.makeText(mcontext, "请正确的电话号码", Toast.LENGTH_SHORT).show();
			
		}else{
			
			   SmsManager manager = SmsManager.getDefault();  
	          
	            ArrayList<String> list = manager.divideMessage(msg);  //因为一条短信有字数限制，因此要将长短信拆分  
	         
	            for(String text:list){  
	           
	            	manager.sendTextMessage(number, null, text, null, null);  
	            }  
			
			}
	}

	protected boolean isMobile(String phone) {
		Pattern pattern = Pattern.compile("1[0-9]{10}");
		Matcher matcher = pattern.matcher(phone);
		if (matcher.matches()) {
			return false;
		} else {
			return true;
		}
	}

}
