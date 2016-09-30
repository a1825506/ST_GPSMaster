package com.saiteng.st_master;



import com.saiteng.st_master.base.Config;
import com.saiteng.st_master.conn.ConnSocketServer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{

    private EditText server_ip,server_port;
    private Button btn_login;
    private CheckBox checkbox_remember;
    private SharedPreferences shared;
    private SharedPreferences.Editor edit;
    private static Handler mhandler=null;
    private Context context=null;
    private String imei=null;
    private  ConnSocketServer connserver=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getDeviceId();
        Config.imei = imei;
        context = LoginActivity.this;
        connserver = new ConnSocketServer(imei,context);
        initView();
        mhandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==Config.SocketOk){
                    btn_login.setText("登录");
                }else if(msg.what == Config.Socketerror){
                    btn_login.setText("再试一次");
                }else if(msg.what==Config.Socketlogin){
                    //登录成功，跳转到主页面
                    Intent intent = new Intent();
                    intent.setClass(context,MainActivity.class);
                    context.startActivity(intent);
                    finish();
                }
            }
        };

    }
    //控件初始化
    public void initView(){
        btn_login         = (Button) findViewById(R.id.login);
        server_ip         = (EditText) findViewById(R.id.serverip);
        server_port       = (EditText) findViewById(R.id.serverport);
        checkbox_remember = (CheckBox) findViewById(R.id.remember_pwd);
        btn_login.setOnClickListener(this);
        shared = getSharedPreferences("lasthistory", Context.MODE_APPEND);
        edit = shared.edit();
        if(shared.getString("serverip","")!=null){
            server_ip.setText(shared.getString("serverip",""));
        }
        if(shared.getString("serverport","")!=null){
            server_port.setText(shared.getString("serverport",""));
        }
        checkbox_remember.setChecked(shared.getBoolean("isCheck",false));
        checkbox_remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    edit.putBoolean("isCheck",isChecked);
                    edit.putString("serverip",server_ip.getText().toString());
                    edit.putString("serverport",server_port.getText().toString());
                    edit.commit();
                }else{
                    edit.putBoolean("isCheck",isChecked);
                    edit.putString("serverip",null);
                    edit.putString("serverport",null);
                    edit.commit();
                }

            }
        });
    }
    //登录操作
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.login:
                Config.ip = server_ip.getText().toString();
                Config.port =Integer.parseInt(server_port.getText().toString());
                edit.putString("serverip",server_ip.getText().toString());
                edit.putString("serverport",server_port.getText().toString());
                edit.commit();
                if(!"".equals(server_ip.getText().toString())&&!"".equals(server_port.getText().toString())){
                    if(connserver!=null){
                        connserver = null;
                    }
                    connserver = new ConnSocketServer(imei,context);
                    connserver.start();
                }else{
                    Toast.makeText(LoginActivity.this,"服务器和地址不能为空", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public static Handler getHandler(){
        return mhandler;
    }
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public void ExieActivity(){
		finish();
	}
	
}
