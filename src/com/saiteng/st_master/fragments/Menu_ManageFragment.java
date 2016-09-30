package com.saiteng.st_master.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jingchen.pulltorefresh.MyListener;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.saiteng.st_master.MyApplication;
import com.saiteng.st_master.R;
import com.saiteng.st_master.adapter.ManageAdapter;
import com.saiteng.st_master.base.Config;
import com.saiteng.st_master.conn.ConnSocketServer;
import com.saiteng.st_master.fragments.BottomDanBingFragment;
import com.saiteng.st_master.fragments.BottomFragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**点击 设备管理 后展示的设备列表和默认的底部导航栏*/
/**
 * 设备管理
 * @author nipengge
 *
 */
public class Menu_ManageFragment extends Fragment{

	private TextView action_bar_preview_txt,base_back_left;
	
	private static ListView mView_menuManagelistView;
	private PullToRefreshLayout ptrl;
	
	private static List<Map<String, Object>> data=null;
	private static ManageAdapter manageadapter;
	private static Context context;
	private BottomFragment bottom;
	private static ProgressDialog dialog;//提示框
	private String[] msg_arr;
	private static String[] arr_data=null;
	private static Handler handler; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		MyApplication.getInstance().addActivity(getActivity());
		context=getActivity();
		Config.mManagecontext=context;
		Config.diviceIMEI=null;
		View view = inflater.inflate(R.layout.activity_menu_manage, null);
		/*初始化标题回退按钮*/
		action_bar_preview_txt = (TextView) view.findViewById(R.id.action_bar_preview_txt);
		action_bar_preview_txt.setText("设备管理（下拉可刷新）");
		base_back_left = (TextView) view.findViewById(R.id.base_back_left);
		base_back_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getActivity().getFragmentManager().popBackStack();
				/**
				 * 隐藏设备参数Fragment 
				 */
				if(DeviceManagementFragment.getHandler() != null){
		             DeviceManagementFragment.getHandler().sendEmptyMessage(3);
		        }
			}
		});
		/*下拉刷新控件*/
		ptrl = ((PullToRefreshLayout) view.findViewById(R.id.refresh_view));
		ptrl.setOnRefreshListener(new MyListener());
		mView_menuManagelistView = (ListView) view.findViewById(R.id.menu_manage_listview);
		
        setDefaultFragment(); 
        //根据点击不同的按钮弹出不同的底部菜单
      		handler = new Handler() {
      			@Override
      			public void handleMessage(Message msg) {
      				super.handleMessage(msg);
      				if (msg.obj.toString().contains("ST*SetDivice*OK")) {
      					setDiviceData(msg.obj.toString());
      				}else if(msg.obj.toString().contains("ST*ADDDivice*OK")){
      					Toast.makeText(context, "设备添加成功", Toast.LENGTH_LONG).show();
      				}else if(msg.obj.toString().contains("ST*ADDDivice*Failure")){
      					Toast.makeText(context, "设备添加失败", Toast.LENGTH_LONG).show();
      				} else if(msg.obj.toString().contains("ST*DeleteDivice*Failure")){
      					Toast.makeText(context, "设备删除失败", Toast.LENGTH_LONG).show();
      				} else if(msg.obj.toString().contains("ST*DeleteDivice*OK")){
      					Toast.makeText(context, "设备删除成功", Toast.LENGTH_LONG).show();
      				} else {
      					if(msg.what==2){
      						Toast.makeText(context, "GPS OPen", Toast.LENGTH_LONG).show();
      					}else if(msg.what==3){
      						Toast.makeText(context, "GPS Close", Toast.LENGTH_LONG).show();
      					}else if(msg.what==4){
      						Toast.makeText(context, "WIFI OPen", Toast.LENGTH_LONG).show();
      					}else if(msg.what==5){
      						Toast.makeText(context, "WIFI Close", Toast.LENGTH_LONG).show();
      					}else{
      						msg_arr = null;
      						msg_arr = msg.obj.toString().split(",");
      						Config.diviceIMEI = msg_arr[1];
      						Config.divicenum = msg_arr[2];
      					}
      					
      				}

      			}
      		};
    		Config.mhandler = handler;
    		ptrl.setOnRefreshListener(new MyListener(){
    			/*下拉加载*/
    			@Override
    			public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
    				// TODO Auto-generated method stub
    				super.onLoadMore(pullToRefreshLayout);
    			}
    			/*上啦刷新*/
    			@Override
    			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
    				// TODO Auto-generated method stub
    				new Thread(){public void run() {
    					try {
    						//联网
    						Thread.sleep(2000);
    						getActivity().runOnUiThread(new Runnable() {
    							@Override
    							public void run() {
    								ConnSocketServer.sendOrder("[ST*"+Config.imei+"*GetDivice]");
    								if(manageadapter!=null){
    									
    									manageadapter.notifyDataSetChanged();
    								}
    							
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
	
	
	// 设置默认的Fragment 底部导航栏
	public void setDefaultFragment() {
		FragmentManager fm = getActivity().getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		bottom = new BottomFragment();
		transaction.replace(R.id.buttom_fragment, bottom);
		transaction.commit();
		
	}
	
	
	
	public void setDiviceData(String divicedata) {
		if (context != null) {
			if (dialog != null) {
				dialog.dismiss();
			}
			if (divicedata.length() < 20) {
				if (dialog != null) {
					dialog.dismiss();
					Toast.makeText(context, "暂无设备,请添加", Toast.LENGTH_LONG).show();
				}

			} else {
				arr_data = divicedata.substring(17, divicedata.length() - 1).split(",");
				data = getData(arr_data);
				manageadapter = new ManageAdapter(context, data);
				mView_menuManagelistView.setAdapter(manageadapter);
				
			}
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

	public static Handler getHandler(){
		
		return handler;
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		handler=null;
		super.onDestroy();
	}
}
