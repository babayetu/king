package com.king.learn;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapDemo {
	public static void main(String[] args) {
		ConcurrentHashMap<Long, String> sessionKey = new ConcurrentHashMap<Long, String>();
		
		sessionKey.put(111L, "aaa");
		sessionKey.putIfAbsent(111L, "bbb");
		
		System.out.println(sessionKey.get(111L));
	}
}
