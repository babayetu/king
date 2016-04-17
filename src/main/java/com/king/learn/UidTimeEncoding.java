package com.king.learn;

import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;

public class UidTimeEncoding {
	public static void main(String[] args) {
		System.out.println((char)('A' + 1));
		System.out.println((char)('A' + 2));
		System.out.println((char)('A' + 3));
		
		UidTimeEncoding uidTimeEncoding = new UidTimeEncoding();
		String enString = uidTimeEncoding.encode(123L);
		Map.Entry<Long, Date> deString = uidTimeEncoding.decode(enString);
		System.out.println("enString:" + enString);
		System.out.println("uid:" + deString.getKey());
		System.out.println("timestamp:" + deString.getValue());
	}
	
	private String encode(long uid) {
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
	
	private Map.Entry<Long, Date> decode(String input) {
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
