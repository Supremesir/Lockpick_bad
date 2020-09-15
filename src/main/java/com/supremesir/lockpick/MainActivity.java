package com.supremesir.lockpick;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.supremesir.lockpick.databinding.ActivityMainBinding;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 * @author fang
 */

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    final String TAG = "MQTT";
    final String CLIENT_ID = "LockPick_App";
    final String SERVER_URL = "tcp://39.96.177.143:1883";
    final String SUBSCRIPTION_TOPIC = "Instruction";
    final String PUBLISH_MESSAGE = "open_sesame";
    final int SERVICE_QOS = 0;
    MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initClient();
        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishMsg();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void initClient() {

            client = new MqttAndroidClient(this ,SERVER_URL, CLIENT_ID);
            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    binding.networkStatusText.setText(R.string.network_online);
                    binding.networkStatusLogo.setImageResource(R.drawable.online);
                    Toast.makeText(getApplication(),"连接成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void connectionLost(Throwable cause) {
                    binding.networkStatusText.setText(R.string.network_offline);
                    binding.networkStatusLogo.setImageResource(R.drawable.offline);
                    Toast.makeText(getApplication(),"连接失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    Toast.makeText(getApplication(),"指令发送成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            MqttConnectOptions connOpts = createConnectOptions();
            try {
                client.connect(connOpts, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "连接成功 ");
            try {
                //订阅主题，参数：主题、服务质量
                client.subscribe(SUBSCRIPTION_TOPIC, SERVICE_QOS);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            Log.i(TAG, "连接失败 ");
        }
    };




    private MqttConnectOptions createConnectOptions() {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setAutomaticReconnect(true);
        // 设置连接超时时间, 单位为秒,默认30
        connOpts.setConnectionTimeout(30);
        // 设置会话心跳时间,单位为秒,默认20
        connOpts.setKeepAliveInterval(20);
        return connOpts;
    }

    public void publishMsg() {
        //此处消息体需要传入byte数组
        MqttMessage message = new MqttMessage(PUBLISH_MESSAGE.getBytes());
        //设置质量级别
        message.setQos(SERVICE_QOS);
        if (client == null) {
            Toast.makeText(getApplication(), "空对象", Toast.LENGTH_SHORT).show();
        }
        try {
            client.publish(SUBSCRIPTION_TOPIC, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


}
