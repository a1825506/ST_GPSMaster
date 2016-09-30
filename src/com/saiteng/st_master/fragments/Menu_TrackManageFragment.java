package com.saiteng.st_master.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.saiteng.st_interface.OnArticleSelectedListener;
import com.saiteng.st_master.R;
import com.saiteng.st_master.adapter.ManageTrackAdapter;
import com.saiteng.st_master.base.Config;
import com.saiteng.st_master.conn.ConnSocketServer;
import com.saiteng.st_master.fragments.BottomTrackFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Menu_TrackManageFragment extends Fragment{
	private static ListView mlistView_trackmanage;
	public String[] arr;
	private static Context context;
	private static List<Map<String, Object>> data;
	private BottomTrackFragment mTrack;
	private static ManageTrackAdapter mTrackAdapter;
	private static ProgressDialog dialog;//提示框
	private FragmentManager fm;
	private FragmentTransaction transaction;
	private static Handler handler =null;
	
	private OnArticleSelectedListener selectedlistener;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_trackmanage, null);
		
		context = getActivity();
		selectedlistener = (OnArticleSelectedListener) getActivity();
		mlistView_trackmanage = (ListView) view.findViewById(R.id.track_manage);
		 
		mlistView_trackmanage.setOnItemLongClickListener(new OnItemLongClickListener() {
			/**
			 * ListView的item监听事件，点击item启动一个activity 进行轨迹播放
			 */

			@Override //在此处通知Activity进行轨迹播放
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
				TextView time = (TextView) view.findViewById(R.id.track_manage_txt);
				Config.tacketime = time.getText().toString();
				/*通知Activtiy的Handler进行轨迹播放 */
				Message msg = Message.obtain();
				msg.what = 201;
				msg.obj = Config.tacketime;
				selectedlistener.onArticleSelected(msg);
				
				return false;
			}
		});
		 //根据cheeckbox的勾选与否弹出底部菜单
		handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what==0){
					setAdapter();
				}else if(msg.what==1){
					Toast.makeText(context, "设备暂无轨迹数据", Toast.LENGTH_LONG).show();
				}else{
					fm = getFragmentManager(); 
					 // 开启Fragment事务  切换底部的菜单栏
			        transaction = fm.beginTransaction();
					if (msg.what==2) {
						mTrack = new BottomTrackFragment(context, Config.tacketime);
						transaction.replace(R.id.track_fragment, mTrack);
					} else if(msg.what==3)
						transaction.hide(mTrack);
					    transaction.commit();
				}

			}
		};
	   Config.mTrackContext =handler;
	   ConnSocketServer.sendOrder("[ST*"+Config.imei+"*"+Config.diviceIMEI+"*GetLocus]");
	   dialog = new ProgressDialog(context);
		dialog.setTitle("提示！");
		dialog.setMessage("正在加载设备列表...");
		dialog.show();
		
		return view;
	}
	

	public static Handler getHandler(){
		return handler;
		
	}
	@Override
	public void onDestroy() {
		handler=null;
		super.onDestroy();
	}
	
	public static void setLocus(String strlocus){
		if(handler!=null){
			strlocus.substring(16);
			String[] arr_locus = strlocus.substring(16).replace("]","").split(",");
			if(arr_locus.length<1){
				handler.sendEmptyMessage(1);
			}else{
				if(arr_locus[0]==null||"".equals(arr_locus[0])){
					handler.sendEmptyMessage(1);
				}else{
					data = getData(arr_locus);
					handler.sendEmptyMessage(0);
				}
			}
			if (dialog != null) {
				dialog.dismiss();
			}
		}
	}
	private static List<Map<String, Object>> getData(String[] arr) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map ;
		for(int i=0;i<arr.length;i++){
			map = new HashMap<String, Object>();
			map.put("image", R.drawable.file);
			map.put("title",arr[i]);
			list.add(map);
		}
		return list;
	}
	protected void setAdapter() {
		mTrackAdapter = new ManageTrackAdapter(context,data);
		mlistView_trackmanage.setAdapter(mTrackAdapter);
	}
}
