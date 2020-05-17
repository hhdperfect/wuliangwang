package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {
    private Button button1,button2,button3,button4;
    private TextView textView;
    private Boolean isFirstStart=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1=findViewById(R.id.bt1);
        button1.setOnClickListener(new myButton1());
        button2=findViewById(R.id.bt2);
        button2.setOnClickListener(new myButton2());
        button3=findViewById(R.id.bt3);
        button3.setOnClickListener(new myButton3());
        button4=findViewById(R.id.bt4);
        button4.setOnClickListener(new myButton4());
        textView=findViewById(R.id.tv1);
    //第一次启动  
        isFirstStart=true;
    //关闭闪光灯不可用  
        button3.setEnabled(false);
        button3.setText("闪光灯已关闭");
    //按钮4未连接=不可用  
        button4.setEnabled(false);
        button4.setText("当前不可断开连接");
        //EventBus  
        EventBus.getDefault().register(this);
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            button1.setEnabled(false);
            button2.setEnabled(false);
            button3.setEnabled(false);
            button4.setEnabled(false);
            int requestCameraPermission = 0;
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},requestCameraPermission);
        }


    }

//摄像头权限判定与按钮启用
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        int requestCameraPermission = 0;
        if(requestCode==requestCameraPermission){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                button1.setEnabled(true);
                button2.setEnabled(true);
            }
            else {
                Toast.makeText(MainActivity.this,"手机相机权限未授权,无法使用。",Toast.LENGTH_SHORT).show();
                button1.setEnabled(false);
                button2.setEnabled(false);
                button3.setEnabled(false);
                button4.setEnabled(false);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }



//组装用户信息模块
   /* private static void initAliyunIoTClient() {
        try{
            //连接所需要的信息：服务器地址，客户端id，用户名，密码  
            String clientId = "java" + System.currentTimeMillis();
            Map<String, String> params = new HashMap<>(16);
            params.put("productKey", productKey);
            params.put("deviceName", deviceName);
            params.put("clientId", clientId);
            String timestamp = String.valueOf(System.currentTimeMillis());
            params.put("timestamp", timestamp);

            // 这里阿里云的服务器地区为cn-shanghai  
            String regionId="cn-shanghai";
            String targetServer = "tcp://" + productKey + ".iot-as-mqtt."+regionId+".aliyuncs.com:1883";
            String mqttclientId = clientId + "|securemode=3,signmethod=hmacsha1,timestamp=" + timestamp + "|";
            String mqttUsername = deviceName + "&" + productKey;
            String mqttPassword = AliyunIoTSignUtil.sign(params, deviceSecret, "hmacsha1");
            connectMqtt(targetServer, mqttclientId, mqttUsername, mqttPassword);
        }
        catch (Exception e) {
            System.out.println("initAliyunIoTClient error " + e.getMessage());
        }
    }*/

//设置连接属性模块
   /* public static void connectMqtt(String url, String clientId, String mqttUsername, String mqttPassword) throws Exception {
        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient mqttClient = new MqttClient(url, clientId, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();

        // MQTT 3.1.1对应MqttVersion(4)
        connOpts.setMqttVersion(4);
        connOpts.setAutomaticReconnect(true);
        connOpts.setCleanSession(true);

        //用户信息  
        connOpts.setUserName(mqttUsername);
        connOpts.setPassword(mqttPassword.toCharArray());
        connOpts.setKeepAliveInterval(60);

        //开始连接  
        mqttClient.connect(connOpts);
        Log.d(TAG, "connected " + url);
    }*/

//加密用户secret
  /*  public static String sign(Map<String, String> params, String deviceSecret, String signMethod) {
        //将参数Key按字典顺序排序  
        String[] sortedKeys = params.keySet().toArray(new String[] {});
        Arrays.sort(sortedKeys);
        //生成规范化请求字符串  
        StringBuilder canonicalizedQueryString = new StringBuilder();
        for (String key : sortedKeys) {
            if("sign".equalsIgnoreCase(key)) {
                continue;
            }
            canonicalizedQueryString.append(key).append(params.get(key));
        }
        try{
            String key = deviceSecret;
            return encryptHMAC(signMethod,canonicalizedQueryString.toString(), key);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //HMACSHA1加密  
    public static String encryptHMAC(String signMethod,String content, String key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key.getBytes("utf-8"), signMethod);
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        byte[] data = mac.doFinal(content.getBytes("utf-8"));
        return bytesToHexString(data);
    }
    //数组转十六进制  
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }
*/
//设备属性上报模块
   /* private static final String payloadJson =
            "{" +
                    "    \"id\": %s," +
                    "    \"params\": {" +
                    "        \"LightSwitch\": %s," +
                    "    }," +
                    "    \"method\": \"thing.event.property.post\"" +
                    "}";

    private static void postDeviceProperties(String status) {
        try{
            //上报数据  
            String payload = String.format(payloadJson, System.currentTimeMillis(),status);
            System.out.println("post :"+payload);
            MqttMessage message = new MqttMessage(payload.getBytes("utf-8"));
            message.setQos(1);

            mqttClient.publish(pubTopic, message);
        } catch (Exception e) {}

    }
*/
//订阅消息  
   /* private static void getDeviceOrder(){
       try{
    // 设置回调  

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("LOG::连接丢失");
                    System.out.println("LOG::Reason="+cause);
                }
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String str=new String(message.getPayload());
                    System.out.println("LOG::收到信息="+str);
                    EventBus.getDefault().post(new MessageEvent(str));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("LOG::delivery complete");
                }
            });
           MqttTopic topic = mqttClient.getTopic(setTopic);
            //订阅消息  
            int[] Qos = {1};
            String[] topic1 = {setTopic};
            mqttClient.subscribe(topic1, Qos);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
*/
//打开闪光灯  
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void openLight(){
        try {
    //判断API是否大于24（安卓7.0系统对应的API）  
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    //获取CameraManager  
                CameraManager mCameraManager = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
    //获取当前手机所有摄像头设备ID  
                assert mCameraManager != null;
                String[] ids = mCameraManager.getCameraIdList();
                for (String id : ids) {
                    CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
    //查询该摄像头组件是否包含闪光灯  
                    Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
                    if (flashAvailable != null && flashAvailable && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
    //打开手电筒  
                        mCameraManager.setTorchMode(id, true);
                    }
                }
            }

    //设定要发送的闪光灯状态，从而实现云端显示闪光灯的状态  
            MQTTTools.postDeviceProperties("1");
    //用户提示以及按钮属性设置  
            Toast.makeText(getApplicationContext(),"已打开闪光灯",Toast.LENGTH_SHORT).show();
            button2.setEnabled(false);
            button2.setText("闪光灯已打开");
            button3.setEnabled(true);
            button3.setText("关闭闪光灯");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//关闭闪光灯  
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void closeLight(){
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    //获取CameraManager  
                CameraManager mCameraManager = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
    //获取当前手机所有摄像头设备ID  
                assert mCameraManager != null;
                String[] ids = mCameraManager.getCameraIdList();
                for (String id : ids) {
                    CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
    //查询该摄像头组件是否包含闪光灯  
                    Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
                    if (flashAvailable != null && flashAvailable && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
    //关闭手电筒  
                        mCameraManager.setTorchMode(id, false);
                    }
                }
            }

    //设定要发送的闪光灯状态，从而实现云端显示闪光灯的状态  
            MQTTTools.postDeviceProperties("0");
    //用户提示以及按钮属性设置  
            Toast.makeText(getApplicationContext(),"已关闭闪光灯",Toast.LENGTH_SHORT).show();
            button2.setEnabled(true);
            button2.setText("打开闪光灯");
            button3.setEnabled(false);
            button3.setText("闪光灯已关闭");
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

 //onDestroy事件
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onDestroy(){
        super.onDestroy();
//取消注册事件  
        EventBus.getDefault().unregister(this);
        closeLight();
        try {
            if(MQTTTools.mqttClient.isConnected()){
                MQTTTools.mqttClient.close();
            }

            Camera m_Camera = new Camera();
            m_Camera.restore();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//onStop事件
    @Override
    protected void onStop(){
        super.onStop();
        Toast.makeText(getApplicationContext(),"程序仍在后台运行",Toast.LENGTH_SHORT).show();
    }

// onStart事件
    @Override
    protected void onStart(){
        super.onStart();
        if(!isFirstStart) {
            try {
                if(MQTTTools.mqttClient!=null){
                    if (MQTTTools.mqttClient.isConnected()) {
                        button1.setEnabled(false);
                        button4.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "仍在连接状态中，请继续操作。", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isFirstStart=false;
    }

//消息接收与处理
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleMessage(MessageEvent messageEvent) {
        System.out.println("LOG::"+messageEvent.getMessage());
        textView.setText(messageEvent.getMessage());
        String jsonData=messageEvent.getMessage();
        try {
            JSONObject jsonObject=new JSONObject(jsonData);
            Object deviceOrder=jsonObject.getJSONObject("params").get("LightSwitch");
            //ord是云平台下发的设备状态 1=打开，0=关闭  
            int ord=Integer.parseInt(deviceOrder.toString());

            if(ord==1){
                openLight();
            }
            else if(ord==0){
                closeLight();
            }
            else {
                System.out.println("LOG::"+ord);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//连接按钮
    class myButton1 implements View.OnClickListener{
        @Override
        public void onClick(View view){
            MQTTTools.initAliyunIoTClient();
            MQTTTools.getDeviceOrder();
            button1.setEnabled(false);
            button1.setText("已在连接状态中");
            button4.setEnabled(true);
            button4.setText("断开与阿里云的连接");
        }
    }

//断开连接按钮
    class myButton4 implements View.OnClickListener{
        @Override
        public void onClick(View view){
            try{
                if(MQTTTools.mqttClient.isConnected()){
                    MQTTTools.mqttClient.disconnect();
                    Toast.makeText(getApplicationContext(),"已断开连接",Toast.LENGTH_SHORT).show();
                    button1.setEnabled(true);
                    button1.setText("连接到阿里云");
                    button4.setEnabled(false);
                    button4.setText("当前不可断开连接");
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"发生错误，断开连接失败。",Toast.LENGTH_SHORT).show();
            }
        }
    }

//打开闪光灯按钮
    class myButton2 implements View.OnClickListener{
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View view){
            openLight();
        }
    }

//关闭闪光灯按钮
    class myButton3 implements View.OnClickListener{
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View view){
            closeLight();
        }
    }

}










