package com.saiteng.st_master.view;

import com.saiteng.st_master.R;
import com.saiteng.st_master.base.Config;
import com.saiteng.st_master.conn.ConnSocketServer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ST_DeleteDialog extends Dialog{
	private Context context;
	private Button mButtonOk, mButtonCancle;

	public ST_DeleteDialog(Context context) {
		super(context);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_deletedivece);
		mButtonOk = (Button) findViewById(R.id.delete_dialog_set_ok);
		mButtonCancle = (Button) findViewById(R.id.delete_dialog_set_cancel);
		mButtonOk.setOnClickListener(new View.OnClickListener() {
			
				@Override
				public void onClick(View v) {
					
					ConnSocketServer.sendOrder("[ST*"+Config.imei+"*DeleteDivice,"+Config.diviceIMEI+"*]");
					dismiss();
				}
		});
		mButtonCancle.setOnClickListener(new View.OnClickListener() {
		
				@Override
				public void onClick(View v) {
					dismiss();
				}
		});
	}

}
