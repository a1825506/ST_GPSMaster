package com.saiteng.st_master.fragments;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jingchen.pulltorefresh.MyListener;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.saiteng.st_interface.OnArticleSelectedListener;
import com.saiteng.st_interface.OnLocateCheckListener;
import com.saiteng.st_master.R;
import com.saiteng.st_master.adapter.ManageAdapter;
import com.saiteng.st_master.base.Config;
import com.saiteng.st_master.conn.ConnSocketServer;
import com.saiteng.st_master.fragments.BottomGenzongFragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Menu_TrackFragment extends Fragment{
	
	private TextView action_bar_preview_txt,base_back_left;
	private static ListView genzong_listView;
	private PullToRefreshLayout ptrl;
	
	private static List<Map<String, Object>> data;
	private static ManageAdapter manageadapter;
	public String[] arr;
	private BottomGenzongFragment XinBiao;
	private String[] msg_arr;
	private static ProgressDialog dialog;//提示框
	private static String[] arr_data;
	private static Handler handler;
	
	private OnLocateCheckListener locatelistner;
	
	private OnArticleSelectedListener onselectelistener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view =	inflater.inflate(R.layout.activity_genzong, null);
		
		Config.mManagecontext=getActivity();
		locatelistner = (OnLocateCheckListener) getActivity();
		onselectelistener = (OnArticleSelectedListener) getActivity();
		genzong_listView = (ListView) view.findViewById(R.id.genzong_listview);
		
		action_bar_preview_txt = (TextView) view.findViewById(R.id.action_bar_preview_txt);
		action_bar_preview_txt.setText("设备跟踪（下拉可刷新）");
		base_back_left = (TextView) view.findViewById(R.id.base_back_left);
		base_back_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getActivity().getFragmentManager().popBackStack();
				locatelistner.OnLocateListener(false);
				Message msg = Message.obtain();
				msg.what = 103;
				onselectelistener.onArticleSelected(msg);
			
			}
		});
		
		 //根据点击不同的按钮弹出不同的底部菜单
	    handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.obj.toString().contains("ST*SetDivice*OK")){
					setDiviceData(msg.obj.toString());
				}else{
					msg_arr = null;
					msg_arr = msg.obj.toString().split(",");
					Config.divicename = msg_arr[0];
					Config.diviceIMEI = msg_arr[1];
					FragmentManager fm = getFragmentManager();
					FragmentTransaction transaction = fm.beginTransaction();
					XinBiao = new BottomGenzongFragment("Menu_TrackFragment");
					transaction.replace(R.id.genzong_fragment, XinBiao);
					transaction.commit();
				}
			}
		};
	    Config.mhandler = handler;
	    ptrl = ((PullToRefreshLayout) view.findViewById(R.id.refresh_view));
		ptrl.setOnRefreshListener(new MyListener(){
			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				// TODO Auto-generated method stub
				new Thread(){public void run() {
					try {
						//下拉刷新时请求数据
						Thread.sleep(2000);
						((Activity) getActivity()).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ConnSocketServer.sendOrder("[ST*"+Config.imei+"*GetDivice]");
								manageadapter.notifyDataSetChanged();
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				};}.start();
				pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}
		});
		ConnSocketServer.sendOrder("[ST*"+Config.imei+"*GetDivice]");
		dialog = new ProgressDialog(getActivity());
		dialog.setTitle("提示！");
		dialog.setMessage("正在加载设备列表...");
		dialog.show();
		
		return view;
	}
		
	public  void setDiviceData(String divicedata) {
		if (dialog != null) {
			dialog.dismiss();
		}
			if (divicedata.length() < 20) {
				if (dialog != null) {
					dialog.dismiss();
					Toast.makeText(getActivity(), "暂无设备", Toast.LENGTH_LONG).show();
				}
			} else {
				arr_data = divicedata.substring(17, divicedata.length()-1).split(",");
				data = getData(arr_data);
				manageadapter = new ManageAdapter(getActivity(), data);
				genzong_listView.setAdapter(manageadapter);
			}
		}

		//根据访问数据库得到的数据设置数据源
	private static List<Map<String, Object>> getData(String[] arr1) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list_online = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		
			for (int i = 0; i < arr1.length; i++) {
				map = new HashMap<String, Object>();
				String[] arr_group = null;
				arr_group = arr1[i].split("#");
				map.put("image", R.drawable.xinbiao);
				map.put("divicename", arr_group[0]);
				map.put("divicenum", arr_group[1]);
				map.put("diviceIMEI", arr_group[2]);
				if("1".equals(arr_group[3])){
					map.put("diviceonline", "（在线）");
					list_online.add(map);
				}else{
					map.put("diviceonline", "（离线）");
					list.add(map);
				}
		}
			//设置在线的置顶
			list_online.addAll(list);	
			return list_online;
	}
	
	public static Handler gethandler(){
		return handler;
		
	}
	@Override
	public void onDestroy() {
		handler=null;
		super.onDestroy();
	}
	
}
