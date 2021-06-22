
package com.miyu.my_chat_core;

import android.content.Context;

import net.x52im.mobileimsdk.android.event.ChatBaseEvent;
import net.x52im.mobileimsdk.android.event.ChatMessageEvent;
import net.x52im.mobileimsdk.android.event.MessageQoSEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.content.Intent;

import io.flutter.plugin.common.EventChannel;

public class ChatMessageEventListener implements ChatMessageEvent, ChatBaseEvent, MessageQoSEvent,EventChannel.StreamHandler
{
	private EventChannel.EventSink events;

	public ChatMessageEventListener( )
	{

	}

	@Override
	public void onListen(Object arguments, EventChannel.EventSink events) {
		this.events = events;
	}


	@Override
	public void onRecieveMessage(String fingerId, String userId, String dataContent, int type)
	{
		Map map = new HashMap();
		map.put("fun", "onRecieveMessage");
		map.put("fingerId", fingerId);
		map.put("userId", userId);
		map.put("dataContent", dataContent);
		map.put("type", type);
		this.events.success(map);
	}



	@Override
	public void onErrorResponse(int errorCode, String errorMsg)
	{
		Map map = new HashMap();
		map.put("fun", "onErrorResponse");
		map.put("errorCode", errorCode);
		map.put("errorMsg", errorMsg);
		this.events.success(map);
	}

	@Override
	public void messagesLost(ArrayList arrayList) {
		Map map = new HashMap();
		map.put("fun", "messagesLost");
		map.put("arrayList", arrayList);
		this.events.success(map);
	}

	@Override
	public void messagesBeReceived(String message) {
		Map map = new HashMap();
		map.put("fun", "messagesBeReceived");
		map.put("message", message);
		this.events.success(map);
	}

	@Override
	public void onLoginResponse(int code) {
		Map map = new HashMap();
		map.put("fun", "messagesBeReceived");
		map.put("code", code);
		this.events.success(map);
	}

	@Override
	public void onLinkClose(int code) {
		Map map = new HashMap();
		map.put("fun", "onLinkClose");
		map.put("code", code);
		this.events.success(map);
	}

	@Override
	public void onCancel(Object arguments) {

	}



}
