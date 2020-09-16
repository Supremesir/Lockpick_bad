package com.supremesir.lockpick;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.supremesir.lockpick.databinding.ActivityMainBinding;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author fang
 */

public class MainActivity extends AppCompatActivity {

    final String TAG = "MQTT";
    final String CLIENT_ID = "LockPick_App";
    final String SERVER_URL = "tcp://39.96.177.143:1883";
    final String SUBSCRIPTION_TOPIC = "Instruction";
    final String PUBLISH_MESSAGE = "open_sesame";
    final int SERVICE_QOS = 0;
    MqttAndroidClient client;
    MyViewModel myViewModel;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        initClient();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                myViewModel.setNetworkStatus(true);
                Log.i(TAG, "已连接网络");
            }

            @Override
            public void onLost(Network network) {
                myViewModel.setNetworkStatus(false);
                Log.i(TAG, "无网络");
            }

        });

        myViewModel.getNetworkStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean) {
                    binding.networkStatusText.setText(R.string.network_offline);
                    binding.networkStatusLogo.setImageResource(R.drawable.offline);
                }
                // TODO: 重新连接网络时提示等待连接服务器
            }
        });

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
        disconnectServer();
    }

    private void initClient() {

        client = new MqttAndroidClient(this, SERVER_URL, CLIENT_ID);
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                try {
                    client.subscribe(SUBSCRIPTION_TOPIC, SERVICE_QOS);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                binding.networkStatusText.setText(R.string.network_online);
                binding.networkStatusLogo.setImageResource(R.drawable.online);
                Log.i(TAG, "成功到服务器");
            }

            @Override
            public void connectionLost(Throwable cause) {
                binding.networkStatusText.setText(R.string.network_offline);
                binding.networkStatusLogo.setImageResource(R.drawable.offline);
                Log.i(TAG, "与服务器连接丢失");
                connectServer();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                Toast.makeText(getApplication(), "指令发送成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        connectServer();
    }

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

    private void publishMsg() {
        MqttMessage message = new MqttMessage(PUBLISH_MESSAGE.getBytes());
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

    private void connectServer() {
        MqttConnectOptions connOpts = createConnectOptions();
        if (client.isConnected()) {
            disconnectServer();
        }
        try {
            client.connect(connOpts);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void disconnectServer() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}


