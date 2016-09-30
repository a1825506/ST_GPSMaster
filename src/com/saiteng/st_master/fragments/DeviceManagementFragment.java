package com.saiteng.st_master.fragments;

import com.saiteng.st_master.R;
import com.saiteng.st_master.base.Config;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


/**
 * 设备管理 设置参数
 * @author nipengge
 *
 */
public class DeviceManagementFragment extends Fragment implements OnClickListener{
	
	
	
	private RadioButton EquipmentListButton,TargetStateButton; // 设备列表,目标状态
	
	private LinearLayout hideLayout;
	
	private TextView hideButton;
	
	private static Handler handler;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view =inflater.inflate(R.layout.fragment_device_managerment, null);
		
		EquipmentListButton = (RadioButton) view.findViewById(R.id.EquipmentListButton);
		TargetStateButton = (RadioButton) view.findViewById(R.id.TargetStateButton);
		
		
		
		hideLayout = (LinearLayout) view.findViewById(R.id.hideLayout);
		hideButton = (TextView) view.findViewById(R.id.hideButton);
		
	
		EquipmentListButton.setOnClickListener(this);
		TargetStateButton.setOnClickListener(this);
		hideButton.setOnClickListener(this);
		
		EquipmentListButton.setChecked(true);
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what == 1){
					EquipmentListButton.setChecked(true);
					replaceFragment(new SubDeviceManagementFragment());
				}else if(msg.what == 2){
					TargetStateButton.setChecked(true);
					replaceFragment(new EquipmentStatusFragment());
				}else if(msg.what == 3){
					if(hideLayout.VISIBLE == View.VISIBLE){
				    	hideLayout.setVisibility(View.INVISIBLE);
				    	hideButton.setText("显示");
					}
				}
			}
		};
		
		return view;
	}

	@Override
	public void onClick(View view) {
		if(view == EquipmentListButton){
			replaceFragment(new  Menu_TrackFragment());
		}else if(view == TargetStateButton){
			replaceFragment(new EquipmentStatusFragment());
		}else if(hideButton == view){
			if(hideLayout.getVisibility() == View.VISIBLE){
				hideLayout.setVisibility(View.INVISIBLE);
				hideButton.setText("显示");
			}else{
				hideLayout.setVisibility(View.VISIBLE);
				hideButton.setText("隐藏");
			}
		}
	}
	
	
	public void replaceFragment(Fragment fragment){
		FragmentManager mangager = getFragmentManager();
		FragmentTransaction tran = mangager.beginTransaction();
		tran.replace(R.id.subMenuFragment, fragment);
		tran.commit();
	}
	
	public static Handler getHandler() {
		return handler;
	}


	
}
