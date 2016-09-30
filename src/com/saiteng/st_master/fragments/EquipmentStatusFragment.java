package com.saiteng.st_master.fragments;

import com.saiteng.st_interface.OnArticleSelectedListener;
import com.saiteng.st_master.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * 设备追踪 设备状态
 * @author nipengge
 *
 */
public class EquipmentStatusFragment extends Fragment implements OnArticleSelectedListener{
	
	private  TextView euqipenstatusText;
	
	
	private static EquipmentStatusFragment context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_equipen_status, null);
		
		euqipenstatusText = (TextView) view.findViewById(R.id.euqipenstatusText);
		
		context = this;
		return view;
	}
	
	
	public static EquipmentStatusFragment getFragmentContext(){
		return context;
	}
	
	public void replaceFragment(Fragment fragment){
		FragmentManager mangager = getFragmentManager();
		FragmentTransaction tran = mangager.beginTransaction();
		tran.replace(R.id.subMenuFragment, fragment);
		//tran.addToBackStack(getActivity().getPackageName());
		tran.commit();
	}
	
	/**
	 * 追踪成功后 回调接口
	 */
	@Override
	public void onArticleSelected(Message msg) {
		// TODO Auto-generated method stub
		euqipenstatusText.setText(msg.obj.toString());
	}
	
}
