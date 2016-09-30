package com.saiteng.st_master.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.map3d.demo.offlinemap.OfflineMapActivity;
import com.saiteng.st_interface.OnArticleSelectedListener;
import com.saiteng.st_master.R;

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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 主页菜单Fragment
 * @author nipengge
 *
 */
public class MenuFragment extends Fragment{
	private View view;
	private ListView mView_menuListView;
	private List<Map<String,Object>> mlist;
	
	private TextView aMapType,offLineMap;//卫星地图,离线地图
	
	//数据源准备
	private String[] title={"设备管理","设备跟踪","轨迹查询","用户管理"};
	private int[] img_id={R.drawable.menu_management,R.drawable.menu_tracking,
			R.drawable.menu_changepassword,R.drawable.user_management1};
	
	private OnArticleSelectedListener selectedlistener;//回调借口
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		selectedlistener = (OnArticleSelectedListener) activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		  view= inflater.inflate(R.layout.menu_listview,null);
		  mView_menuListView = (ListView) view.findViewById(R.id.Menu_listview);
		  aMapType = (TextView) view.findViewById(R.id.aMapType);
		  aMapType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String str  = aMapType.getText().toString();
				if("卫星地图".equals(str)){
					Message msg = Message.obtain();
					msg.what = 351;
					selectedlistener.onArticleSelected(msg);
					aMapType.setText("普通地图");
				}else if("普通地图".equals(str)){
					Message msg = Message.obtain();
					msg.what = 352;
					selectedlistener.onArticleSelected(msg);
					aMapType.setText("卫星地图");
				}
			}
		});
		  offLineMap = (TextView) view.findViewById(R.id.offLineMap);
		  offLineMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(), OfflineMapActivity.class));
			}
		});
		  
		  mlist=initData();
			//绑定数据源
		  mView_menuListView.setAdapter(new SimpleAdapter(getActivity(), mlist, R.layout.item_memu, 
                  new String[]{"img","title"}, new int[]{R.id.Menu_img,R.id.Menu_text}));
		  mView_menuListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					 TextView content=(TextView) view.findViewById(R.id.Menu_text);
				     if(title[0].equals(content.getText().toString())){
				    	 /*设备管理*/
				    	 replaceFragment(new Menu_ManageFragment());
					 }
					 if(title[1].equals(content.getText().toString())){
						 /*设备跟踪*/
						 replaceFragment(new Menu_TrackFragment());
					 }
					 if(title[2].equals(content.getText().toString())){
						 /*轨迹查询*/
						 replaceFragment(new Menu_GuiJiFragment());
						}
					 if(title[3].equals(content.getText().toString())){
						 /*设备管理*/
						 replaceFragment(new UserManagerFragment());
						}
				}
			});
		  
		  return view;
	}
	
	//这里的数据源为已知的四个。
	@SuppressWarnings("unchecked")
	private List<Map<String,Object>> initData() {
		List<Map<String,Object>> list  = new ArrayList<Map<String, Object>>();
			
		for(int i=0;i<img_id.length;i++){
			Map map=new HashMap<String, Object>();
			map.put("img",img_id[i]);
			map.put("title",title[i]);
			list.add(map);
		}
		return list;
	}
	
	
	//更换或添加Fragment
    public void replaceFragment(Fragment fragment){
		FragmentManager manager = getActivity().getFragmentManager();
		FragmentTransaction tran = manager.beginTransaction();
		tran.replace(R.id.mlin, fragment);
		tran.addToBackStack(null);
		tran.commit();
	}

}
