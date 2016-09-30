package com.saiteng.st_master.conn;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.saiteng.st_master.LoginActivity;
import com.saiteng.st_master.MainActivity;
import com.saiteng.st_master.base.Config;
import com.saiteng.st_master.fragments.Menu_GuiJiFragment;
import com.saiteng.st_master.fragments.Menu_ManageFragment;
import com.saiteng.st_master.fragments.Menu_TrackFragment;
import com.saiteng.st_master.fragments.Menu_TrackManageFragment;
import com.saiteng.st_master.view.Param_Dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 *与服务器建立socket连接来接收参数
 *和下发指令 
 */
public class ConnSocketServer extends Thread{
	private  Socket s = null;
	private static DataOutputStream oWritter;
	private DataInputStream oReader = null; 
	StringBuilder msg = new StringBuilder(); 
	private String imei=null;
	private Context mcontext;
    public ConnSocketServer(String strimei,Context context){
    	this.imei = strimei;
    	this.mcontext = context;
    }
	@Override
	public void run() {
		super.run();
		try {
			s = new Socket(Config.ip, Config.port);
			oWritter = new DataOutputStream(s.getOutputStream()); // 获取Socket对象的输出流，并且在外边包一层DataOutputStream管道，方便输出数
			oReader = new DataInputStream(new BufferedInputStream(s.getInputStream()));
			  LoginActivity.getHandler().sendEmptyMessage(Config.SocketOk);
			oWritter.write(("[ST*"+imei+"*Connect]").getBytes("GB2312"));
			byte[] l_aryBuf = new byte[1024];
			int len = 0;
			while ((len=oReader.read(l_aryBuf)) != -1) {
				msg.append(new String(l_aryBuf, 0, len, "GB2312"));
				if("[ST*Login*OK]".equals(msg.toString())){
					LoginActivity.getHandler().sendEmptyMessage(Config.Socketlogin);
					msg.delete(0, msg.length());
				}else if("[ST*Login*failure]".equals(msg.toString())){
					LoginActivity.getHandler().sendEmptyMessage(0);
					msg.delete(0, msg.length());
				}else if("ST*ADDDivice*OK".equals(msg.toString())){
					if(Menu_ManageFragment.getHandler()!=null){
						Handler handler = Menu_ManageFragment.getHandler();
						Message message1 = handler.obtainMessage();
					    message1.obj= (msg.toString());
					    handler.sendMessage(message1);
					}
					msg.delete(0, msg.length());
				}else if("ST*ADDDivice*Failure".equals(msg.toString())){
					if(Menu_ManageFragment.getHandler()!=null){
						Handler handler = Menu_ManageFragment.getHandler();
						Message message1 = handler.obtainMessage();
					    message1.obj= (msg.toString());
					    handler.sendMessage(message1);
					}
					msg.delete(0, msg.length());
				}else if("ST*DeleteDivice*Failure".equals(msg.toString())){
					if(Menu_ManageFragment.getHandler()!=null){
						Handler handler = Menu_ManageFragment.getHandler();
						Message message1 = handler.obtainMessage();
					    message1.obj= (msg.toString());
					    handler.sendMessage(message1);
					}
					msg.delete(0, msg.length());
				}else if("ST*DeleteDivice*OK".equals(msg.toString())){
					if(Menu_ManageFragment.getHandler()!=null){
						Handler handler = Menu_ManageFragment.getHandler();
						Message message1 = handler.obtainMessage();
					    message1.obj= (msg.toString());
					    handler.sendMessage(message1);
					}
					msg.delete(0, msg.length());
				}else if("[ST*Connect*OK]".equals(msg.toString())){
					msg.delete(0, msg.length());
				}else if("[ST*Server_close]".equals(msg.toString())){
					//服务器断开
					s=null;
					oWritter.close();
					oReader.close();
					msg.delete(0, msg.length());
					LoginActivity.getHandler().sendEmptyMessage(2);
				}else if(msg.toString().contains("ST*SetDivice*OK")&&msg.toString().endsWith("]")){
					//从数据库获得设备列表数据
					if(Menu_ManageFragment.getHandler()!=null){
						Handler handler = Menu_ManageFragment.getHandler();
						Message message1 = handler.obtainMessage();
					    message1.obj= (msg.toString());
					    handler.sendMessage(message1);
					}
					if(Menu_TrackFragment.gethandler()!=null){
						Handler handler = Menu_TrackFragment.gethandler();
						Message message2 = handler.obtainMessage();
						message2.obj =  (msg.toString());
						handler.sendMessage(message2);
					}
					if(Menu_GuiJiFragment.gethandler()!=null){
						Handler handler = Menu_GuiJiFragment.gethandler();
						Message message2 = handler.obtainMessage();
						message2.obj =  (msg.toString());
						handler.sendMessage(message2);
					}
					msg.delete(0, msg.length());
				}else if("[ST*Divice*NotOnline]".equals(msg.toString())){
					MainActivity.setlatLng("");
					msg.delete(0, msg.length());
				}else if(msg.toString().contains("ST*Divice*GetLatLng")){
					MainActivity.setlatLng(msg.toString());
					msg.delete(0, msg.length());
				}else if(msg.toString().contains("ST*GetLocus*OK")&&msg.toString().endsWith("]")){
					Menu_TrackManageFragment.setLocus(msg.toString());
					if(msg.toString().endsWith("]")){
						msg.delete(0, msg.length());
					}
				}else if(msg.toString().contains("ST*LocusDetails*OK")&&msg.toString().endsWith("]")){
					MainActivity.setLocusDatils(msg.toString());
					if(msg.toString().endsWith("]")){
						msg.delete(0, msg.length());
					}
				}else if(msg.toString().contains("UPLOAD")){
					Param_Dialog.setPassTime(msg.toString());
					if(msg.toString().endsWith("]")){
						msg.delete(0, msg.length());
					}
				}else if(msg.toString().contains("NOTONLine")){
					MainActivity.setlatLng("NOTONLine");
					msg.delete(0, msg.length());
				}
			}
			s=null;
			oWritter.close();
			oReader.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			LoginActivity.getHandler().sendEmptyMessage(Config.Socketerror);
		} catch (IOException e) {
			e.printStackTrace();
			LoginActivity.getHandler().sendEmptyMessage(Config.Socketerror);
		}
	}
	public static void sendOrder(String msg){
		try {
			if(oWritter!=null){
				oWritter.write(msg.getBytes("GB2312"));
				oWritter.flush();
			}else{
				LoginActivity.getHandler().sendEmptyMessage(3);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
