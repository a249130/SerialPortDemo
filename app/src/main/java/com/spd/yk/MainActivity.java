package com.spd.yk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;

import serial.utils.SerialPort;
import serial.utils.SerialPortConfigure;
import serial.utils.SerialPortInterface;
import serial.utils.SerialPortThread;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MainActivity extends Activity implements SerialPortInterface,OnClickListener {
    SerialPortThread serialThread = null;
    SerialPortConfigure configure = null;
    //定义控件
    private EditText et_shou = null; //接收数据
    private EditText et_fa = null; //发送数据
    private Spinner sp_code = null; //编码
    private Button but_send = null; //发送数据
    private Spinner sp_serial = null; //串口号
    private Spinner sp_baudrate = null; //波特率
    private Spinner sp_nVerify = null; //校验位
    private Spinner sp_nBits = null; //数据位
    private Spinner sp_nstop = null; //停止位
    private CheckBox cb_wrap = null; //自动换行显示
    private CheckBox cb_shou = null; //十六进制显示
    private CheckBox cb_fa = null; //十六进制发送
    private Button but_open = null; //打开串口
    private Button but_shou = null; //清空接收数据
    private Button but_fa = null; //清空发送数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configure = new SerialPortConfigure();
        Init();
        Init_Configure();
        configure.setOpen(false);
    }
    // 初始化控件
    private void Init(){
        et_shou = (EditText) findViewById(R.id.et_shou);
        et_fa = (EditText) findViewById(R.id.et_fa);
        but_send = (Button) findViewById(R.id.but_send);
        but_send.setOnClickListener(this);
        sp_code = (Spinner) findViewById(R.id.sp_code);
        sp_serial = (Spinner) findViewById(R.id.sp_serial);
        sp_baudrate = (Spinner) findViewById(R.id.sp_baudrate);
        sp_nVerify = (Spinner) findViewById(R.id.sp_nVerify);
        sp_nBits = (Spinner) findViewById(R.id.sp_nBits);
        sp_nstop = (Spinner) findViewById(R.id.sp_nstop);
        cb_wrap = (CheckBox) findViewById(R.id.cb_wrap);
        cb_wrap.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                configure.setWrap(isChecked);
            }
        });
        cb_shou = (CheckBox) findViewById(R.id.cb_shou);
        cb_shou.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                configure.setSixteenShou(isChecked);
            }
        });
        cb_fa = (CheckBox) findViewById(R.id.cb_fa);
        cb_fa.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                configure.setSixteenFa(isChecked);
                if(et_fa.getText().length()>0){
                    String et = et_fa.getText().toString();
                    if(isChecked){
                        et_fa.setText(SerialPortThread.str2HexStr(et));
                    }else{
                        et_fa.setText(SerialPortThread.hexStr2Str(et));
                    }
                }
            }
        });
        but_open = (Button) findViewById(R.id.but_open);
        but_open.setOnClickListener(this);
        but_shou = (Button) findViewById(R.id.but_shou);
        but_shou.setOnClickListener(this);
        but_fa = (Button) findViewById(R.id.but_fa);
        but_fa.setOnClickListener(this);
    }

    //初始化串口配置
    private void Init_Configure(){
        configure.setPath(sp_serial.getSelectedItem().toString());
        configure.setBaudrate(Integer.decode(sp_baudrate.getSelectedItem().toString()));
        configure.setnVerify(sp_nVerify.getSelectedItem().toString().charAt(0));
        configure.setNbits(Integer.decode(sp_nBits.getSelectedItem().toString().split("b")[0]));
        configure.setNstop(Integer.decode(sp_nstop.getSelectedItem().toString().split("b")[0]));
        configure.setWrap(cb_wrap.isChecked());
        configure.setSixteenShou(cb_shou.isChecked());
        configure.setSixteenFa(cb_fa.isChecked());
    }

    // View单击事件
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.but_send: // 发送
                if(et_fa.getText().length()>0){
                    if(configure.isOpen()){ //串口已打开
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                try {
                                    switch (sp_code.getSelectedItem().toString()) {
                                        case "zigbee":
                                            mOutputStream.write(SerialPortThread.hexStringToByte(et_fa.getText().toString()));
                                            break;
                                        default:
                                            mOutputStream.write(et_fa.getText().toString().getBytes(sp_code.getSelectedItem().toString()));
                                            break;
                                    }
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }else{
                        Toast.makeText(MainActivity.this, "串口未打开！", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "输入框不能为空！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.but_open: // 打开串口
                Init_Configure();
                if(but_open.getText().toString().equals("打开串口")){
                    but_open.setText("关闭串口");
                    configure.setOpen(true);
                    try {
                        // 打开串口
                        OpenPort(getPath(configure.getPath()),configure.getBaudrate(),configure.getNbits(),configure.getnVerify(),configure.getNstop());
                    } catch (SecurityException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }else{
                    but_open.setText("打开串口");
                    configure.setOpen(false);
                    // 关闭串口
                    closeSerialPort();
                }
                break;
            case R.id.but_shou: // 清空接收数据
                et_shou.setText("");
                break;
            case R.id.but_fa: // 清空发送数据
                et_fa.setText("");
                break;
        }

    }
    // 转换串口号
    private String getPath(String path){
        String str = "";
        switch (path) {
            case "COM1":
                str = "/dev/ttyAMA1";
                break;
            case "COM2":
                str = "/dev/ttyAMA2";
                break;
            case "COM3":
                str = "/dev/ttyAMA3";
                break;
            case "COM4":
                str = "/dev/ttyAMA4";
                break;
            case "COM5":
                str = "/dev/ttyAMA5";
                break;
            case "COM6":
                str = "/dev/ttyAMA6";
                break;
            case "COM7":
                str = "/dev/ttyAMA7";
                break;
            case "COM8":
                str = "/dev/ttyAMA8";
                break;
        }
        return str;
    }

    Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
            byte[] buffer = (byte[]) msg.obj;
//			String shou = SerialPortThread.bytesToHexString(buffer);
            String shou = "";
            try {
                shou = new String(buffer,"gbk");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(configure.isSixteenShou()){
                shou = SerialPortThread.bytesToHexString(buffer);
            }
            if (configure.isWrap()) {
                et_shou.append(shou + "\n");
            } else {
                et_shou.append(shou);
            }
        };
    };

    @Override
    public void onDataReceived(byte[] buffer, int size) {
        // TODO Auto-generated method stub
        System.out.println("buffer:"+SerialPortThread.bytesToHexString(buffer));
        Message msg = Message.obtain();
        msg.obj = buffer;
        myHandler.sendMessage(msg);
    }

    private SerialPort mSerialPort = null;
    private InputStream mInputStream=null;
    protected OutputStream mOutputStream=null;

    final static int IOCTRL_PMU_RFID_ON  =	0x03;
    final static int IOCTRL_PMU_RFID_OFF =	0x04;

    final static int IOCTRL_PMU_BARCODE_ON  = 0x05;
    final static int IOCTRL_PMU_BARCODE_OFF = 0x06;
    final static int IOCTRL_PMU_BARCODE_TRIG_LOW  = 0x12;
    private void OpenPort(String path,int baudrate,int nbits,char cVerify,int nstop) throws SecurityException, IOException
    {
        if(mSerialPort==null)
        {
//    		String path ="/dev/ttyAMA4";//4.0
//    		int baudrate = Integer.decode("38400");
//    		int nbits = Integer.decode("8");
//    		int nstop = Integer.decode("1");

//    		String sVerify ="N";
//    		char cVerify = sVerify.charAt(0);

    		/* Check parameters */
            if ( (path.length() == 0) || (baudrate == -1) || nbits == -1 ||nstop == -1 || cVerify == 'C')
            {
                Log.d("11111111","yyyyyyyyyyyyyy");
                throw new InvalidParameterException();
            }
            Log.d("11111111","111111111111111111111111111111111");
    		/* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, nbits, cVerify, nstop, 0);
            Log.d("11111111","2222222222222222222222222222222222");
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            mSerialPort.sri_Init();
            mSerialPort.sri_IOCTL(IOCTRL_PMU_BARCODE_TRIG_LOW);
            mSerialPort.sri_IOCTL(IOCTRL_PMU_BARCODE_ON);
            mSerialPort.sri_IOCTL(IOCTRL_PMU_RFID_ON);
            Log.d("11111111","333333333333333333333333333333333");
            serialThread = new SerialPortThread(mInputStream);
            serialThread.start();
            serialThread.setS(this);
            Log.d("11111111","44444444444444444444444444444444444");
        }
        else
        {
            //view1.setText("");
            Log.d("11111111","555555555555555555555555555555555");
            return;
        }

    }

    public void closeSerialPort() {
        if(serialThread != null){
            serialThread.interrupt();
        }
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort.sri_IOCTL(IOCTRL_PMU_RFID_OFF);
            mSerialPort.sri_IOCTL(IOCTRL_PMU_BARCODE_OFF);
            mSerialPort.sri_DeInit();
            mSerialPort = null;
        }
    }

    @Override
    protected void onDestroy() {
        closeSerialPort();
        super.onDestroy();
    }

}
