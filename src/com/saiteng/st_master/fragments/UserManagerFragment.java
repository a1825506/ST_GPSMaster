package com.saiteng.st_master.fragments;

import com.saiteng.st_master.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 用户管理
 * @author nipengge
 *
 */
public class UserManagerFragment  extends Fragment{
	
	private TextView action_bar_preview_txt,base_back_left;
	
	private ListView userListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View  view = inflater.inflate(R.layout.fragment_usermanager, null);
		
		initView(view);
		
		return view;
	}
	
	/**
	 * 初始化View
	 * @param view
	 */
	public void initView(View view){
		/*标题*/
		action_bar_preview_txt = (TextView) view.findViewById(R.id.action_bar_preview_txt);
		action_bar_preview_txt.setText("子用户管理");
		/*回退按钮*/
		base_back_left = (TextView) view.findViewById(R.id.base_back_left);
		base_back_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getActivity().getFragmentManager().popBackStack();//回退Fragment
			}
		});
		
		/*账号列表*/
		userListView = (ListView) view.findViewById(R.id.userListView);
	}
	
}
