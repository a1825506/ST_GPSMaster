package com.saiteng.st_master.view;

import com.saiteng.st_master.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class HangOn_Dialog extends Dialog{
	private Context context;
	private Button btn_shakeOn,btn_shakeOff;
	private RadioButton btn_radioOn,btn_radioOff;
    private boolean ischeck,nocheck;
    private TextView txt_title,txt_on,txt_off;

	public HangOn_Dialog(Context context) {
		super(context);
		this.context = context;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_shake);
		initView();
	}
	private void initView() {
		txt_title   = (TextView) findViewById(R.id.shake_title);
		txt_title.setText("待机开关设置");
		txt_on      = (TextView) findViewById(R.id.shake_on);
		txt_on.setText("待机开关开启");
		txt_off     = (TextView) findViewById(R.id.shake_off);
		txt_off.setText("待机开关关闭");
		btn_shakeOn  =  (Button) findViewById(R.id.btn_shakeon);//确认
		btn_shakeOff = (Button) findViewById(R.id.btn_shakeoff);//取消
		btn_radioOn  =  (RadioButton) findViewById(R.id.shakeon);
		btn_radioOff = (RadioButton) findViewById(R.id.shakeoff);
		btn_radioOn.setChecked(true);
		ischeck=true;
		btn_radioOn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					if(nocheck){
						btn_radioOff.setChecked(false);
					}
					ischeck=true;
					
				}
			}
		});
		btn_radioOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				   //取消btn_radioOn的选中状态
					if(isChecked){
						if(ischeck){
							btn_radioOn.setChecked(false);
					    }
						nocheck=true;	
				}
				
			}
		});
		btn_shakeOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
		btn_shakeOff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HangOn_Dialog.this.dismiss();
				
			}
		});
	}

}
