package com.king.utils;

import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;

public class EncodeAlgorithm {
	public String encode(long uid) {
		StringBuffer sb = new StringBuffer();
		while (uid > 0) {
			long num = uid % 10;
			uid = uid / 10;
			sb.append((char)('A' + num));
		}
		
		sb.append('W');
		
		long t = new Date().getTime();
		
		while (t > 0) {
			long num = t % 10;
			t = t / 10;
			sb.append((char)('K' + num));
		}
		
		return sb.toString();
	}
	
	public Map.Entry<Long, Date> decode(String input) {
		int seperator = input.indexOf('W');
		
		long uidLong = 0;
		long timestampLong = 0;
		for (int i = seperator - 1; i >= 0; i--) {
			uidLong = uidLong  * 10 + (input.charAt(i) - 'A' );			
		}
		
		for (int j = input.length() - 1; j >= seperator + 1; j--) {
			timestampLong = timestampLong  * 10 + (input.charAt(j) - 'K' );	
		}
		
		return new AbstractMap.SimpleEntry<Long, Date>(uidLong, new Date(timestampLong));
	}
}

