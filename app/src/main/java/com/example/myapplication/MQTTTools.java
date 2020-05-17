package com.example.myapplication;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

class MQTTTools {
    private static  String timestamp = String.valueOf(System.currentTimeMillis());
    private static String productKey = "a1LuGPTn0Du";
    private static String deviceName = "light";
    private static String deviceSecret = "SE6MTPrbRJblOs4jzCAXYgVuOGhnBubr";
    private static String clientId = "java" + System.currentTimeMillis();
    private static String setTopic = "/sys/a1LuGPTn0Du/light/thing/service/property/set";
    private static String pubTopic = "/sys/a1LuGPTn0Du/light/thing/event/property/post";
    static MqttClient mqttClient ;

    private static Map<String, String> params = new HashMap<>(16);
    static void initAliyunIoTClient() {

        try {
            //连接所需要的信息：服务器地址，客户端id，用户名，密码
            params.put("productKey", productKey);
            params.put("deviceName", deviceName);
            params.put("clientId", clientId);
            params.put("timestamp", timestamp);
            // 这里阿里云的服务器地区为cn-shanghai
            String regionId="cn-shanghai";
            String mqttPassword =sign(params, deviceSecret);

            String targetServer = "tcp://" + productKey + ".iot-as-mqtt."+regionId+".aliyuncs.com:1883";
            String mqttclientId = clientId + "|securemode=3,signmethod=hmacsha1,timestamp=" + timestamp + "|";
            String mqttUsername = deviceName + "&" + productKey;
            connectMqtt(targetServer, mqttclientId, mqttUsername, mqttPassword);
        } catch (Exception e) {
            System.out.println("initAliyunIoTClient error " + e.getMessage());
        }
    }


    //连接属性的设置
    private static void connectMqtt(String url, String clientId, String mqttUsername, String mqttPassword) throws Exception {
        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient  mqttClient = new MqttClient(url, clientId, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        // MQTT 3.1.1对应MqttVersion(4)
        connOpts.setMqttVersion(4);
        connOpts.setAutomaticReconnect(false);
        connOpts.setCleanSession(false);
        //用户信息
        connOpts.setUserName(mqttUsername);
        connOpts.setPassword(mqttPassword.toCharArray());
        connOpts.setKeepAliveInterval(60);
        //开始连接
        mqttClient.connect(connOpts);
    }


    //加密用户secret
    private static String sign(Map<String, String> params, String deviceSecret) {
        //将参数Key按字典顺序排序
        String[] sortedKeys = params.keySet().toArray(new String[] {});
        Arrays.sort(sortedKeys);
        //生成规范化请求字符串
        StringBuilder canonicalizedQueryString = new StringBuilder();
        for (String key : sortedKeys) {
            if ("sign".equalsIgnoreCase(key)) {
                continue;
            }
            canonicalizedQueryString.append(key).append(params.get(key));
        }
        try {
            String key = deviceSecret;
            return encryptHMAC(canonicalizedQueryString.toString(), key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //HMACSHA1加密
    private static String encryptHMAC(String content, String key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key.getBytes("utf-8"), "hmacsha1");
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        byte[] data = mac.doFinal(content.getBytes("utf-8"));
        return bytesToHexString(data);
    }
    //数组转十六进制
    private static String bytesToHexString(byte[] bArray) {

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




    //物模型-属性上报payload
    private static final String payloadJson =
            "{" +
                    "    \"id\": %s," +
                    "    \"params\": {" +
                    "        \"LightSwitch\": %s," +
                    "    }," +
                    "    \"method\": \"thing.event.property.post\"" +
                    "}";


    static void postDeviceProperties(String status) {
        try {
            //上报数据
            String payload = String.format(payloadJson, System.currentTimeMillis(),status);
            System.out.println("post :"+payload);
            MqttMessage message = new MqttMessage(payload.getBytes("utf-8"));
            message.setQos(1);
            mqttClient.publish(pubTopic, message);
        } catch (Exception e) {}
    }

    //订阅消息
    static void getDeviceOrder() {
        try {
        // 设置回调
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("LOG::连接丢失");
                System.out.println("LOG::Reason=" + cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String str = new String(message.getPayload());
                System.out.println("LOG::收到信息=" + str);
                EventBus.getDefault().post(new MessageEvent(str));

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("LOG::delivery complete");
            }
        });

            MqttTopic topic= mqttClient.getTopic(setTopic);
           //订阅消息
            int[] Qos  = {1};
            String[] topic1 = {setTopic};
            mqttClient.subscribe(topic1, Qos);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
