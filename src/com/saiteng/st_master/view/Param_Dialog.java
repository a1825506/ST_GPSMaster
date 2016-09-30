package com.saiteng.st_master.view;

import com.saiteng.st_master.R;
import com.saiteng.st_master.base.Config;
import com.saiteng.st_master.conn.ConnSocketServer;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
/**
 * 参数设置对话框
 * */
public class Param_Dialog extends Dialog{
	private Button positiveButton, negativeButton;
	private Spinner spinner_item;
	private Context context;
	private static Handler mhandler=null;

	public Param_Dialog(Context context) {
		super(context);
		this.context = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_normal_layout);
		initView();
		mhandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what==0){
					Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
					dismiss();
				}else{
					Toast.makeText(context, "设备不在线", Toast.LENGTH_LONG).show();
					dismiss();
				}
			}
		};
	}

	private void initView() {
		spinner_item = (Spinner) findViewById(R.id.setting_param);
		positiveButton = (Button) findViewById(R.id.positiveButton);
		negativeButton = (Button) findViewById(R.id.negativeButton);
		positiveButton.setOnClickListener(new View.OnClickListener(){
            //参数上传到服务器，再又服务器将参数下发到指定手机或信标控制端
			@Override
			public void onClick(View v) {
				String interval = spinner_item.getSelectedItem().toString();
				String len = null;
				if("10秒".equals(interval)){
					interval ="10";
					len="0009";
				}else if("1分钟".equals(interval)){
					interval ="1*60";
					len="0009";
				}else if("5分钟".equals(interval)){
					interval ="1*60*5";
					len="0009";
				}else if("10分钟".equals(interval)){
					interval ="1*60*50";
					len="0011";
				}
				ConnSocketServer.sendOrder("[3G*"+Config.diviceIMEI+"*"+len+"*UPLOAD,"+interval+"]");
			}
			
		});
		negativeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Param_Dialog.this.dismiss();
				
			}
		});
	}
	
	public static void setPassTime(String msg){
		if(mhandler!=null){
			mhandler.sendEmptyMessage(0);
		}
	}

}
