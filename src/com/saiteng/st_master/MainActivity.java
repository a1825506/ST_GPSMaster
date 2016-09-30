package com.saiteng.st_master;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.NaviPara;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.saiteng.st_interface.OnArticleSelectedListener;
import com.saiteng.st_interface.OnLocateCheckListener;
import com.saiteng.st_master.base.Config;
import com.saiteng.st_master.base.ToastUtil;
import com.saiteng.st_master.base.Utils;
import com.saiteng.st_master.conn.ConnSocketServer;
import com.saiteng.st_master.fragments.DeviceManagementFragment;
import com.saiteng.st_master.fragments.EquipmentStatusFragment;
import com.saiteng.st_master.fragments.MenuFragment;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/* 
  
/**
 *
 */
public class MainActivity extends Activity implements LocationSource,AMapLocationListener,OnArticleSelectedListener
,OnMapLoadedListener,OnGeocodeSearchListener,OnLocateCheckListener,InputtipsListener,OnPoiSearchListener,OnMarkerClickListener,InfoWindowAdapter{
	
	private long exitTime = 0;
	
	/*右边地图相关*/
	private Context context;
	private static AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private AMapLocationClient mlocationClient;
	private AMapLocationClientOption mLocationOption;
	private LinearLayout mlin;
	private String Address;
	
	/*获取手表位置相关*/
	private static double longitude;
	private static double latitude;
	private static LatLng latlng =new LatLng(Config.mLatitude,Config.mLongitude);
	private static LatLonPoint latLonPoint =null; 
	private static PolylineOptions mLatLonOptins;
	private int trackNumber = 0;
	
	private static String[] arr_data=null;
	private GeocodeSearch geocoderSearch;
	String locateway=null;
	String time = null;
	private Marker marker=null;
	/*获取位置成功回调*/
	//private Handler mhandler;
	
	private static Handler acivityHandler;
	
	/*轨迹播放相关*/
	private Polyline mVirtureRoad;
	private Marker mMoveMarker;
    static PolylineOptions points;
	Integer index = 0;
	boolean flag,isGetData;
	// 通过设置间隔时间和距离可以控制速度和图标移动的距离
	private static final int TIME_INTERVAL = 80;
	private static final double DISTANCE = 0.5;
	
	
	/*地址手工定位*/
	private  AutoCompleteTextView searchText;// 输入搜索关键字
	private PoiResult poiResult; // poi返回的结果
	private PoiSearch.Query query;// Poi查询条件类
	private CheckBox mcheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_main);
		mlin = (LinearLayout) findViewById(R.id.mlin);
		mcheck= (CheckBox) findViewById(R.id.zoomButton);
		mcheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1 == true){
					mlin.setVisibility(View.VISIBLE);
				}else{
					mlin.setVisibility(View.GONE);
				}
			}
		});
		
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		initMap();
		context = this;
		/*适配Menu菜单的宽度*/
		AdapterWidth();
		/*添加Menu Fragment*/
		replaceFragment(new MenuFragment());
		acivityHandler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				System.out.println(msg);
				if(msg.what==0){
					//设备返回定位结果
					initOverlay();
					setTextInfo();
					if(trackNumber !=0){
						initLatLngOverlayTrackplay();
						//moveLooper();//画轨迹
					}
					trackNumber++;
				}else if(msg.what==1){
					//设备没有返回定位结果
					Toast.makeText(context,"设备离线暂无定位数据...",Toast.LENGTH_LONG).show();
				}else if(msg.what == 101){
					/*向服务器发送定位请求*/
					setUpMap();
					setAmapLister();
					ConnSocketServer.sendOrder("[ST*"+Config.imei+"*"+Config.diviceIMEI+"*GetlatLng]");
					Toast.makeText(context, "正在追踪..", Toast.LENGTH_LONG).show();
				}else if(msg.what == 102){
					
				}else if(msg.what == 103){
					/*清除地图覆盖物*/
					if (marker!=null) {
						aMap.clear();
					}
				}else if(msg.what == 201){
					/*接受到用户进行轨迹播放命令*/
					time = msg.obj.toString();
					ConnSocketServer.sendOrder("[ST*"+Config.imei+"*"+Config.diviceIMEI+"*"+time+"*LocusDetails]");//向服务器发送请求轨迹命令
				}else if(msg.what==202){
					Toast.makeText(context, "轨迹数据太少无法绘制", Toast.LENGTH_LONG).show();
				}else if (msg.what == 204){
					if(mMoveMarker!=null){
						mMoveMarker.remove();
						aMap.addMarker(null);
						aMap.clear();
					}
					if(marker!=null){
						aMap.clear();
					}
					if(null != mlocationClient && mlocationClient.isStarted()){
						mlocationClient.stopLocation();
					}
					initOverlayTrackplay();
					//moveLooper();//画轨迹
					Toast.makeText(context, "获取轨迹信息成功", Toast.LENGTH_SHORT).show();;
				}else if(msg.what == 302){
					/*坐标定位*/
					if(aMap != null){
						aMap = mapView.getMap();
						aMap.getUiSettings().setScaleControlsEnabled(true);
						aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
						 LatLonPoint mLatLonPoint = (LatLonPoint) msg.obj;
						 new LatLng(mLatLonPoint.getLatitude(),mLatLonPoint.getLongitude());
						 RegeocodeQuery query = new RegeocodeQuery((LatLonPoint)msg.obj, 200,
									GeocodeSearch.GPS);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
						 	geocoderSearch = new GeocodeSearch(MainActivity.this);
							geocoderSearch.setOnGeocodeSearchListener(MainActivity.this);
							geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求*/	
					}
				}else if(msg.what == 351){
					aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 设置卫星地图模式
				}else if(msg.what == 352){
					aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 设置普通地图模式
				}
				
			};
		};
	}
	
	
	/**
     * 获得屏幕宽的33%
     * @return
     */
	/*根据不同屏幕适配左边列表的宽度*/
	private void AdapterWidth(){
		DisplayMetrics metric = new DisplayMetrics();  
   	   	getWindowManager().getDefaultDisplay().getMetrics(metric);  
   	   	mlin.getLayoutParams().width = (int) (metric.widthPixels*0.30);
	}
	
	 //更换或添加Fragment
	public  void replaceFragment(Fragment fragment){
		 FragmentManager manager = getFragmentManager();
	     FragmentTransaction tran = manager.beginTransaction();
	     tran.add(R.id.mlin, fragment);
	     tran.commit();
	}
	
	
	private void initMap(){
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
			aMap.getUiSettings().setScaleControlsEnabled(true);
			setUpMap();
		}
	}
	private void setUpMap() {
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
	}
	
	private void setAmapLister(){
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
		aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
	}
		
	
	
	public void initOverlay() {
//		if(marker!=null){
//			aMap.clear();
//		}
        //添加定位点到地图
		 marker = aMap.addMarker(new MarkerOptions()
				.position(latlng)
				.title(	Config.divicename)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.draggable(true)
				.setFlat(true));
	
		marker.showInfoWindow();// 设置默认显示一个infowinfow
		aMap.moveCamera(CameraUpdateFactory.zoomBy(15));
		//地图中心点
		aMap.moveCamera(CameraUpdateFactory.changeLatLng(latlng));
	}
	
	
	
	private void setTextInfo() {
		//添加textview的相关信息
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		time=df.format(new Date());// new Date()为获取当前系统时间
		//设备定位策略 GPS->wifi->基站
		if(Integer.parseInt(arr_data[12])==0){
			int n =Integer.parseInt(arr_data[18]);
			if(Integer.parseInt(arr_data[25+3*n])==0){
				locateway="基站定位";
			}else
				locateway="wifi定位";
		}else{
			locateway = "GPS定位";
		}
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
		
		
		new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				OnArticleSelectedListener selectedlistener = EquipmentStatusFragment.getFragmentContext();
				if(msg.what==0){
					
					msg.obj = Config.divicename+",电量："+arr_data[14]+",定位方式："+locateway+",时间："+time+"\n"
					+"地址："+Address;
					selectedlistener.onArticleSelected(msg);
				}else
					msg.obj=Config.divicename+",电量："+arr_data[14]+",定位方式："+locateway+",时间："+time+"\n"
							+"暂无地址信息";
					selectedlistener.onArticleSelected(msg);
			}
		};
		
		
	}
	
	
	/**
	   [3G*3568497510*016B*UD,150716,020644,V,31.19919274,N,121.4640069,E,2.16,
	   184.9,0.0,0,100,91,0,0,00000010,7,255,460,0,6243,53825,165, 6243,53363,
	   155,6243,53826,154,6243,53827,153,6243,53361,149,6243,55377,148,6243,
	   53362,148,5,jamestplink,c0:61:18:b:95:ce,-37,abcd,1a:ee:65:30:0:53,-68,
	   TP-LINK_F6702A,8c:21:a:f6:70:2a,-73,VANS_PRIVATE,c0:3f:e:b:46:35,-78,
	   EUFOTON,78:44:76:94:50:24,-80,35.6] 
		
	 * 121.4640069； 纬度：31.19919274
	 */
	public static void setlatLng(String latLng){
		if(acivityHandler!=null){
			if(latLng==null||"".equals(latLng)){
				acivityHandler.sendEmptyMessage(1);
			}else{
				if("NOTONLine".equals(latLng)){
					acivityHandler.sendEmptyMessage(1);
				}else{
					
						if(mLatLonOptins == null){
							mLatLonOptins = new PolylineOptions();
						}
						arr_data = latLng.split(",");
						latitude=Double.parseDouble(arr_data[5]);
						longitude =Double.parseDouble(arr_data[7]);
			            latlng = new LatLng(latitude, longitude);
			            latlng = Utils.transformFromWGSToGCJ(latlng);
			            latLonPoint = new LatLonPoint(latlng.latitude, latlng.longitude);
			            mLatLonOptins.add(latlng);
			            aMap.moveCamera(CameraUpdateFactory.changeLatLng(latlng));
			            //保存导航数据
			            Config.mGZLongitude =latlng.longitude;
			            Config.mGZLatitude  = latlng.latitude;
			            acivityHandler.sendEmptyMessage(0);
					
				}
			}
		}
	}
		
		
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK){
	    	mLatLonOptins = null;
	    	trackNumber = 0;
	    	if(mMoveMarker!=null){
				mMoveMarker.remove();
				aMap.clear();
			}
	    	if(marker!=null){
				aMap.clear();
			}
	    	if(mlocationClient!=null &&mlocationClient.isStarted()){
	    		initMap();
				mlocationClient.startLocation();
	    	}
	    	
	    	
	    	if(getFragmentManager().getBackStackEntryCount()==1){
	    		if(DeviceManagementFragment.getHandler() != null){
		             DeviceManagementFragment.getHandler().sendEmptyMessage(3);
		         }
	    	}
	    	
	    	if(getFragmentManager().getBackStackEntryCount()<=0){
	    		 if(DeviceManagementFragment.getHandler() != null){
		             DeviceManagementFragment.getHandler().sendEmptyMessage(3);
		         }
		        if((System.currentTimeMillis()-exitTime) > 2000){  
		            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                
		            exitTime = System.currentTimeMillis();
		        } else {
		            finish();
		            System.exit(0);
		        }
		        return true; 
	    	}
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null
					&& amapLocation.getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				Config.mLatitude = amapLocation.getLatitude();//31.198986
				Config.mLongitude = amapLocation.getLongitude();//121.46426
			} else {
				String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
				Log.e("AmapErr",errText);
			    Toast.makeText(context, errText, Toast.LENGTH_LONG).show();
			}
		}
		
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mlocationClient == null) {
			mlocationClient = new AMapLocationClient(this);
			mLocationOption = new AMapLocationClientOption();
			//设置定位监听
			mlocationClient.setLocationListener(this);
			//设置为高精度定位模式
			mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
			//设置定位参数
			mlocationClient.setLocationOption(mLocationOption);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用onDestroy()方法
			// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
			mlocationClient.startLocation();
		}
		
	}
    /**
     *停止定位 
     */
	@Override
	public void deactivate() {
		mListener = null;
		if (mlocationClient != null) {
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;
		
	}
	
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		//mhandler = null;
    	acivityHandler =null;
		super.onDestroy();
		mapView.onDestroy();
		if(null != mlocationClient){
			mlocationClient.onDestroy();
		}
	}
	
	/**
	 * Fragment与Activity通讯的回调方法
	 * @param what
	 */
	@Override
	public void onArticleSelected(Message msg) {
		acivityHandler.sendMessage(msg);
	}

	 /**
     *地图图加载成功 
     */
	@Override
	public void onMapLoaded() {
	

	}
	/*轨迹播放初始化*/
	private void initOverlayTrackplay() {
		aMap.moveCamera(CameraUpdateFactory.changeLatLng(points.getPoints().get(0)));
		points.width(10);
		points.color(Color.RED);
		mVirtureRoad = aMap.addPolyline(points);
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.setFlat(true);
		markerOptions.anchor(0.5f, 0.5f);
		markerOptions.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.hong1));
		markerOptions.position(points.getPoints().get(0));
		mMoveMarker = aMap.addMarker(markerOptions);
		mMoveMarker.setRotateAngle((float) getAngle(0));
	}
	
	/*追踪定位 画红线*/
	private void initLatLngOverlayTrackplay() {
		//aMap.moveCamera(CameraUpdateFactory.changeLatLng(points.getPoints().get(0)));
		mLatLonOptins.width(10);
		mLatLonOptins.color(Color.RED);
		mVirtureRoad = aMap.addPolyline(mLatLonOptins);
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.setFlat(true);
		markerOptions.anchor(0.5f, 0.5f);
//		markerOptions.icon(BitmapDescriptorFactory
//				.fromResource(R.drawable.marker));
		markerOptions.position(mLatLonOptins.getPoints().get(0));
		mMoveMarker = aMap.addMarker(markerOptions);
		mMoveMarker.setRotateAngle((float) getAngle(0));
	}

		
	/**
	 * 根据点获取图标转的角度
	 */
	private double getAngle(int startIndex) {
		if ((startIndex + 1) >= mVirtureRoad.getPoints().size()) {
			throw new RuntimeException("index out of bonds");
		}
		LatLng startPoint = mVirtureRoad.getPoints().get(startIndex);
		LatLng endPoint = mVirtureRoad.getPoints().get(startIndex + 1);
		return getAngle(startPoint, endPoint);
	}
	/**
	* 根据两点算取图标转的角度
	 */
	private double getAngle(LatLng fromPoint, LatLng toPoint) {
		double slope = getSlope(fromPoint, toPoint);
		if (slope == Double.MAX_VALUE) {
			if (toPoint.latitude > fromPoint.latitude) {
				return 0;
			} else {
				return 180;
			}
		}
		float deltAngle = 0;
		if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
			deltAngle = 180;
		}
		double radio = Math.atan(slope);
		double angle = 180 * (radio / Math.PI) + deltAngle - 90;
		return angle;
	}

		/**
		 * 根据点和斜率算取截距
		 */
		private double getInterception(double slope, LatLng point) {

			double interception = point.latitude - slope * point.longitude;
			return interception;
		}

		/**
		 * 算斜率
		 */
		private double getSlope(LatLng fromPoint, LatLng toPoint) {
			if (toPoint.longitude == fromPoint.longitude) {
				return Double.MAX_VALUE;
			}
			double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
			return slope;

		}
		
		
		/**
		 * 计算x方向每次移动的距离
		 */
		private double getXMoveDistance(double slope) {
			if (slope == Double.MAX_VALUE) {
				return DISTANCE;
			}
			return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
		}

			/**
			 * 循环进行移动逻辑
			 */
			public void moveLooper() {
				new Thread() {
					public void run() {
						while (true) {
							for (int i = 0; i < mVirtureRoad.getPoints().size() - 1; i++) {

								
								LatLng startPoint = mVirtureRoad.getPoints().get(i);
								LatLng endPoint = mVirtureRoad.getPoints().get(i + 1);
								mMoveMarker
								.setPosition(startPoint);

								mMoveMarker.setRotateAngle((float) getAngle(startPoint,
										endPoint));

								double slope = getSlope(startPoint, endPoint);
								//是不是正向的标示（向上设为正向）
								boolean isReverse = (startPoint.latitude > endPoint.latitude);

								double intercept = getInterception(slope, startPoint);

								double xMoveDistance = isReverse ? getXMoveDistance(slope)
										: -1 * getXMoveDistance(slope);
								
								for (double j = startPoint.latitude;
										!((j > endPoint.latitude)^ isReverse);
										
										j = j
										- xMoveDistance) {
									LatLng latLng = null;
									if (slope != Double.MAX_VALUE) {
										latLng = new LatLng(j, (j - intercept) / slope);
									} else {
										latLng = new LatLng(j, startPoint.longitude);
									}
									mMoveMarker.setPosition(latLng);
									try {
										Thread.sleep(TIME_INTERVAL);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}

							}
						}
					}

				}.start();
			}
		public static void setLocusDatils(String strDatils){
			if(acivityHandler!=null){
				String [] arr_datils = strDatils.substring(20).replace("]","").split(",");
		        if(arr_datils.length<=1){
		        	acivityHandler.sendEmptyMessage(202);
				}else{
					if(arr_datils[0]==null||"".equals(arr_datils[0])){
						acivityHandler.sendEmptyMessage(202);
					}else{
						points = new PolylineOptions();
						for(int i=0;i<arr_datils.length;i++){
							String[] LatLng=arr_datils[i].split("&");
							longitude=Double.parseDouble(LatLng[1]);
				            latitude=Double.parseDouble(LatLng[0]);
				            latlng= Utils.transformFromWGSToGCJ(new LatLng(latitude,longitude));
				            points.add(new LatLng(latlng.latitude,latlng.longitude));
				            
						}
						acivityHandler.sendEmptyMessage(204);
					}
				}
			}	
		}
	
	public  boolean IsEmptyOrNullString(String s) {
		return (s == null) || (s.trim().length() == 0);
    }

	@Override
	public void OnLocateListener(boolean check) {
		mLatLonOptins = null;
		trackNumber = 0;
	}


	@Override
	public void onGetInputtips(List<Tip> tipList, int rCode) {

		if (rCode == 1000) {// 正确返回
			List<String> listString = new ArrayList<String>();
			for (int i = 0; i < tipList.size(); i++) {
				listString.add(tipList.get(i).getName());
			}
			ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
					getApplicationContext(),
					R.layout.route_inputs, listString);
			searchText.setAdapter(aAdapter);
			aAdapter.notifyDataSetChanged();
		} else {
			ToastUtil.showerror(this, rCode);
		}
	}


	@Override
	public void onPoiItemSearched(PoiItem arg0, int arg1) {

	}


	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
	
		if (rCode == 1000) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					if(mlocationClient!=null &&mlocationClient.isStarted()){
						mlocationClient.stopLocation();
			    	}
					poiResult = result;
					// 取得搜索到的poiitems有多少页
					List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

					if (poiItems != null && poiItems.size() > 0) {
						aMap.clear();// 清理之前的图标
						PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
						poiOverlay.removeFromMap();
						poiOverlay.addToMap();
						poiOverlay.zoomToSpan();
					} else if (suggestionCities != null
							&& suggestionCities.size() > 0) {
					
					} else {
						Toast.makeText(MainActivity.this,
								"对不起，没有搜索到相关数据！",Toast.LENGTH_SHORT).show();
					}
				}
			} else {
				Toast.makeText(MainActivity.this,
						"对不起，没有搜索到相关数据！",Toast.LENGTH_SHORT).show();
			}
		} else {
			ToastUtil.showerror(this, rCode);
		}
	}


	@Override
	public View getInfoContents(Marker arg0) {
		return null;
	}


	@Override
	public View getInfoWindow(final Marker arg0) {
		View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri,
				null);
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(arg0.getTitle());

		TextView snippet = (TextView) view.findViewById(R.id.snippet);
		snippet.setText(arg0.getSnippet());
		ImageButton button = (ImageButton) view
				.findViewById(R.id.start_amap_app);
		// 调起高德地图app
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startAMapNavi(arg0);
			}
		});
		return view;
	}
	
	/**
	 * 调起高德地图导航功能，如果没安装高德地图，会进入异常，可以在异常中处理，调起高德地图app的下载页面
	 */
	@SuppressWarnings("deprecation")
	public void startAMapNavi(Marker marker) {
		// 构造导航参数
		NaviPara naviPara = new NaviPara();
		// 设置终点位置
		naviPara.setTargetPoint(marker.getPosition());
		// 设置导航策略，这里是避免拥堵
		naviPara.setNaviStyle(NaviPara.DRIVING_AVOID_CONGESTION);

		// 调起高德地图导航
		try {
			AMapUtils.openAMapNavi(naviPara, getApplicationContext());
		} catch (AMapException e) {

			// 如果没安装会进入异常，调起下载页面
			AMapUtils.getLatestAMapApp(getApplicationContext());

		}

	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		arg0.showInfoWindow();
		return false;
	}


	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

	}


	@Override
	public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {

		
	}
	
}
