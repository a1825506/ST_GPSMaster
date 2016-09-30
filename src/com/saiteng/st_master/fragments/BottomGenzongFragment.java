package com.saiteng.st_master.fragments;

import com.saiteng.st_interface.OnArticleSelectedListener;
import com.saiteng.st_interface.OnLocateCheckListener;
import com.saiteng.st_master.BasicNaviActivity;
import com.saiteng.st_master.R;
import com.saiteng.st_master.base.Config;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
/**设备跟踪菜单里面点击按钮的底部弹框*/
public class BottomGenzongFragment extends Fragment implements OnClickListener{
	
	private OnArticleSelectedListener mListener; 
	
	private OnLocateCheckListener mCheckListener;
	
	private Button mBtn_navigation,mBtn_Genzong,mbtn_Guiji;
	
	private View view;
	
	private String mtype;
	
	public BottomGenzongFragment(String type){
		this.mtype = type;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 view= inflater.inflate(R.layout.fragment_bottomgenzong,null);
		 return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		mBtn_navigation = (Button) view.findViewById(R.id.navigation);
		mBtn_Genzong    = (Button) view.findViewById(R.id.genzong);
		mbtn_Guiji      = (Button)view.findViewById(R.id.guiji);
		if("Menu_TrackFragment".equals(mtype)){
			mbtn_Guiji.setVisibility(View.GONE);
			mBtn_navigation.setVisibility(View.VISIBLE);
			mBtn_Genzong.setVisibility(View.VISIBLE);
		}else{
			mbtn_Guiji.setVisibility(View.VISIBLE);
			mBtn_navigation.setVisibility(View.GONE);
			mBtn_Genzong.setVisibility(View.GONE);
		}
		mbtn_Guiji.setOnClickListener(this);
		mBtn_navigation.setOnClickListener(this);
		mBtn_Genzong.setOnClickListener(this);
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try{  
	         mListener =(OnArticleSelectedListener)activity;  
	     }catch(ClassCastException e){  
	         throw new ClassCastException(activity.toString()+"must implement OnArticleSelectedListener");  
	     }  
		try{  
			mCheckListener = (OnLocateCheckListener) activity;
	     }catch(ClassCastException e){  
	         throw new ClassCastException(activity.toString()+"must implement OnLocateCheckListener");  
	     }
		
		
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch(v.getId()){
		case R.id.navigation://导航
			intent.setClass(Config.mManagecontext, BasicNaviActivity.class);
			Config.mManagecontext.startActivity(intent);
			break;
		case R.id.genzong:
			Message msg = Message.obtain();
			msg.what = 101;
			mListener.onArticleSelected(msg);
			break;
		case R.id.guiji://轨迹管理
			replaceFragment(new Menu_TrackManageFragment());
			
			/*不接收定位命令*/
			mCheckListener.OnLocateListener(false);
			break;
		}
	}
	/*切换Fragment*/
	  public void replaceFragment(Fragment fragment){
			FragmentManager manager = getActivity().getFragmentManager();
			FragmentTransaction tran = manager.beginTransaction();
			tran.replace(R.id.mlin, fragment);
			tran.addToBackStack(null);
			tran.commit();
	}
}
