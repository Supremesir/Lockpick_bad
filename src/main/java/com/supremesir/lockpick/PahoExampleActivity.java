//package com.supremesir.lockpick;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//import org.eclipse.paho.android.service.MqttAndroidClient;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//
//public class PahoExampleActivity extends AppCompatActivity {
//
//    private static MqttAndroidClient mqttAndroidClient;
//
//    String CLIENT_ID = "LockPick_Hardware";
//    final String SERVER_URL = "tcp://39.96.177.143:1883";
//
//    final String SUBSCRIPTION_TOPIC = "LockPick_App";
//    final String PUBLISH_TOPIC = "tourist_enter";
//    final String RESPONSE_TOPIC = "message_arrived";
//    final String PUBLISH_MESSAGE = "open_sesame";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_paho_example);
//    }
//
//
//
//    public static void publish(String message) {
//        String topic = PUBLISH_TOPIC;
//        Integer qos = 2;
//        Boolean retained = false;
//        try {
//            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
//            mqttAndroidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }
//}