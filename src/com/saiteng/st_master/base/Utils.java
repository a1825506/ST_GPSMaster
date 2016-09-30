package com.saiteng.st_master.base;

import com.amap.api.maps.model.LatLng;
import com.saiteng.st_master.MainActivity;
import com.saiteng.st_master.MyApplication;
import com.saiteng.st_master.conn.DelDiviceTask;
import com.saiteng.st_master.conn.DeleteTrackTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class Utils {
	private static Context mcontext=null;
	private static Handler mhandler=null;
	private static Map<String, String> map = new HashMap<String, String>();
	private static String path = Environment
			.getExternalStorageDirectory().getPath()+"/kml";
	private static double a = 6378245.0;
	private static double ee = 0.00669342162296594323;
	public static void DeleteDialog(final Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提示").setMessage(message)
				.setPositiveButton("确定", new OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						//((Activity) context).finish();
						if(Config.diviceIMEI==null){
							Toast.makeText(context, "无法删除!", Toast.LENGTH_SHORT).show();
						}else
							Toast.makeText(context, "删除"+Config.diviceIMEI, Toast.LENGTH_SHORT).show();
							new DelDiviceTask().execute();
					}
				}).setNegativeButton("取消", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
					}).show();

	}
	public static void ExitDialog(final Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提示").setMessage(message)
				.setPositiveButton("确定", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						MyApplication.getInstance().exit();  
						
					}
				}).setNegativeButton("取消", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}).show();

	}
	public static void deleteTrack(final Context context, String message,final String tracktime) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提示").setMessage(message)
				.setPositiveButton("确定", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(tracktime==null){
							Toast.makeText(context, "请先查看轨迹", Toast.LENGTH_SHORT).show();
						}else{
							new DeleteTrackTask().execute(tracktime);
							Toast.makeText(context, "轨技数据删除成功", Toast.LENGTH_SHORT).show();
						}
						}
				}).setNegativeButton("取消", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}).show();
	}
	//创建kml文件，先从数据库查询到该设备的在指定日为期段的定位数据，再生成kml文件。
	public static void createKml(final Context context,String time){
		map.clear();
		mcontext = context;
	    new InitLatlngTask1().execute(time);
		mhandler = new Handler(){
			   @Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if("true".equals(msg.obj.toString())){
					createkmlfile(map);//创建kml文件
					
				}
			}
		 };
	}
	static class InitLatlngTask1 extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... params) {
			String timedate = params[0];
			String result=null;
			HttpGet get = new HttpGet(Config.url+"locusdetails?time="+timedate+"&phonenum="+Config.diviceIMEI);
			HttpClient client = new DefaultHttpClient();
			StringBuilder builder = null;
			try {
				HttpResponse response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == 200) {
					InputStream inputStream = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inputStream));
					builder = new StringBuilder();
					String s = null;
					for (s = reader.readLine(); s != null; s = reader.readLine()) {
						builder.append(s);
					}
					result=builder.toString();
				}else{
					result ="NetworkException";
				}
			} catch (Exception e) {
				e.printStackTrace();
				result="Exception";
			}
			return result;
		}
		@Override
		public void onPostExecute(String result) {
			if("".equals(result)){
				Toast.makeText(mcontext, "暂无轨迹数据", Toast.LENGTH_SHORT).show();
			}if("NetworkException".equals(result)){
				Toast.makeText(mcontext, "服务器错误", Toast.LENGTH_SHORT).show();
			}if("Exception".equals(result)){
				Toast.makeText(mcontext, "访问出现问题", Toast.LENGTH_SHORT).show();
			}
			if(result!=null){
				String[] arr_LatLng=result.split(",");
				if(arr_LatLng.length<2){
					Toast.makeText(mcontext, "轨迹数据太少，无法绘制", Toast.LENGTH_SHORT).show();
				}
				else{
					for(int i=0;i<arr_LatLng.length;i++){
						//117.70401156231&32.544050387869
						String[] LatLng=arr_LatLng[i].split("&");
						map.put("point"+i, LatLng[0]+","+LatLng[1]);
					}
					Message message = new Message();
					message.obj ="true";
					mhandler.sendMessage(message);
				}
			}
		}
	}
	protected static void createkmlfile(Map map) {
		 // 创建根节点 并设置它的属性 ;     
		Element root = new Element("Document").setAttribute("count",map.size()+"");     
        // 将根节点添加到文档中；     
        Document Doc = new Document(root); 
        for (int i = 0; i < map.size(); i++) {    
            // 创建节点 book;     
            Element elements = new Element("Placemark");  
            Element point = new Element("Point");
            point.addContent(new Element("coordinates").setText(map.get("point"+i).toString()));
            // 给 book 节点添加子节点并赋值；     
            elements.addContent(new Element("name").setText("point"+i));    
            elements.addContent(point); 
           
            //    
            root.addContent(elements);    
        }    
        // 输出 books.xml 文件；    
        // 使xml文件 缩进效果  
        Format format = Format.getPrettyFormat();  
        XMLOutputter XMLOut = new XMLOutputter(format);  
        try {
			XMLOut.output(Doc, new FileOutputStream(path+"new.kml"));
			Toast.makeText(mcontext, "轨迹导出成功"+path, Toast.LENGTH_LONG).show();
		} catch (FileNotFoundException e) {
			Toast.makeText(mcontext, "轨迹导出失败"+path, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(mcontext, "轨迹导出失败"+path, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
	}
	
	/**
	 *进行GPS坐标或火星坐标的转换。
	 *高德地图是火星坐标，定位模块是gps坐标需进行转换 
	 */
	  public static LatLng transformFromWGSToGCJ(LatLng wgLoc) {

          //如果在国外，则默认不进行转换
          if (outOfChina(wgLoc.latitude, wgLoc.longitude)) {
                  return new LatLng(wgLoc.latitude, wgLoc.longitude);
          }
          double dLat = transformLat(wgLoc.longitude - 105.0,
                          wgLoc.latitude - 35.0);
          double dLon = transformLon(wgLoc.longitude - 105.0,
                          wgLoc.latitude - 35.0);
          double radLat = wgLoc.latitude / 180.0 * Math.PI;
          double magic = Math.sin(radLat);
          magic = 1 - ee * magic * magic;
          double sqrtMagic = Math.sqrt(magic);
          dLat = (dLat * 180.0)/ ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
          dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);

          return new LatLng(wgLoc.latitude + dLat, wgLoc.longitude + dLon);
  }
	  public static double transformLat(double x, double y) {
          double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                          + 0.2 * Math.sqrt(x > 0 ? x : -x);
          ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x
                          * Math.PI)) * 2.0 / 3.0;
          ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0
                          * Math.PI)) * 2.0 / 3.0;
          ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y
                          * Math.PI / 30.0)) * 2.0 / 3.0;
          return ret;
  }

  public static double transformLon(double x, double y) {
          double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                          * Math.sqrt(x > 0 ? x : -x);
          ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x
                          * Math.PI)) * 2.0 / 3.0;
          ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0
                          * Math.PI)) * 2.0 / 3.0;
          ret += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x
                          / 30.0 * Math.PI)) * 2.0 / 3.0;
          return ret;
  }
  
  public static boolean outOfChina(double lat, double lon) {
          if (lon < 72.004 || lon > 137.8347)
                  return true;
          if (lat < 0.8293 || lat > 55.8271)
                  return true;
          return false;
  }

}
