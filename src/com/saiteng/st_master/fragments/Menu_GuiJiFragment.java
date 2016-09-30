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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**轨迹查询管理页面*/
public class Menu_GuiJiFragment extends Fragment{
	private TextView action_bar_preview_txt,base_back_left;
	private static ListView guiji_listView;
	private PullToRefreshLayout ptrl;
	private OnLocateCheckListener locatelistner;
	private static ProgressDialog dialog;//提示框
	private OnArticleSelectedListener onselectelistener;
	private static List<Map<String, Object>> data;
	private BottomGenzongFragment XinBiao;
	private static ManageAdapter manageadapter;
	public String[] arr;
	private String[] msg_arr;
	private static String[] arr_data;
	private View view;
    private static Handler handler;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		view = inflater.inflate(R.layout.fragment_guiji, null);
		locatelistner = (OnLocateCheckListener) getActivity();
		onselectelistener = (OnArticleSelectedListener) getActivity();
		guiji_listView = (ListView) view.findViewById(R.id.guiji_listview);
		action_bar_preview_txt = (TextView) view.findViewById(R.id.action_bar_preview_txt);
		action_bar_preview_txt.setText("设备轨迹（下拉可刷新）");
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
					XinBiao = new BottomGenzongFragment("Menu_GuiJiFragment");
					transaction.replace(R.id.guiji_fragment, XinBiao);
					transaction.commit();
					
				}
			}
		};
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
				guiji_listView.setAdapter(manageadapter);
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
