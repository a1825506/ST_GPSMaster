package com.saiteng.st_master.fragments;
import com.saiteng.st_master.R;
import com.saiteng.st_master.base.Config;
import com.saiteng.st_master.view.ST_DeleteDialog;
import com.saiteng.st_master.view.ST_InfoDialog;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class BottomFragment extends Fragment implements OnClickListener{
	private Button mBtn_addDivice,mBtn_aboutDivice;
	private View view;
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    view= inflater.inflate(R.layout.fragment_bottmpreview,null);
		return view;
	}
	@Override
	public void onStart() {
		super.onStart();
		mBtn_addDivice = (Button) view.findViewById(R.id.add_divice);
		mBtn_aboutDivice = (Button) view.findViewById(R.id.about_divice);
		mBtn_addDivice.setOnClickListener(this);
		mBtn_aboutDivice.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.add_divice:
			ST_InfoDialog dialog = new ST_InfoDialog(getActivity());
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.show();
			break;
		case  R.id.about_divice:
			if(Config.diviceIMEI!=null){
				ST_DeleteDialog dialog1 = new ST_DeleteDialog(getActivity());
				dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog1.show();
			}else
				Toast.makeText(getActivity(), "请先选择设备", Toast.LENGTH_SHORT).show();
			
			break;
		}
		
	}

}
