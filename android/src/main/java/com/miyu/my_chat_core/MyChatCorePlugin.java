package com.miyu.my_chat_core;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.BinaryMessenger;

import net.x52im.mobileimsdk.android.core.LocalDataSender;
import net.x52im.mobileimsdk.android.conf.ConfigEntity;
import net.x52im.mobileimsdk.android.ClientCoreSDK;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;
import android.os.Handler;

/**
 * MyChatCorePlugin
 */
public class MyChatCorePlugin implements FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;
    private EventChannel eventChannel;

    private ChatMessageEventListener listener;
    private Context context;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        context = flutterPluginBinding.getApplicationContext();
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "my_chat_core");
        eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "my_chat_core_status");
        listener = new ChatMessageEventListener();
        eventChannel.setStreamHandler(listener);
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        final Result result1 = result;
        Map map = (Map) call.arguments;
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("init")) {
            initMobileIMSDK((String) map.get("ip"), (int) map.get("port"));
            result.success(true);
        } else if (call.method.equals("login")) {
            new LocalDataSender.SendLoginDataAsync((String) map.get("name"), (String) map.get("token")) {
                @Override
                protected void fireAfterSendLogin(int code) {
                    result1.success(code);
                }
            }.execute();
        }  else if (call.method.equals("loginOut")) {
            int code = LocalDataSender.getInstance().sendLoginout();


            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 释放IM核心库资源
                    ClientCoreSDK.getInstance().release();

                    // 清空设置的回调
            ClientCoreSDK.getInstance().setChatBaseEvent(null);
            ClientCoreSDK.getInstance().setChatMessageEvent(null);
            ClientCoreSDK.getInstance().setMessageQoSEvent(null);

                }
            }, 3000);//3秒后执行Runnable中的run方法
            this.init = false;


            result.success(code);
        }else if (call.method.equals("sendMassage")) {

            LocalDataSender.getInstance().sendCommonData((String) map.get("message"), (String) map.get("uid"), (String) map.get("fingerId"), (int) map.get("type"));
            result.success(true);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        eventChannel.setStreamHandler(null);
        channel = null;
        eventChannel = null;
    }

    private boolean init;

    private void initMobileIMSDK(String ip, int port) {
        // 设置IM聊天服务端IP地址或域名
        ConfigEntity.serverIP = ip;
        ConfigEntity.serverPort = port;
        // 初始化
        if (!this.init) {
            ClientCoreSDK.getInstance().init(context);
            ClientCoreSDK.getInstance().setChatBaseEvent(listener);
            ClientCoreSDK.getInstance().setChatMessageEvent(listener);
            ClientCoreSDK.getInstance().setMessageQoSEvent(listener);
            this.init = true;
        }
    }



}