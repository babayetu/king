package com.king.utils;

import java.util.Date;
import java.util.Map.Entry;

/**
 * Can not use Junit4 because forbidden any framework
 * @author liujingjing
 *
 */

public class EncodeAlgorithmTest {

	public void testEncodeDecode(EncodeAlgorithm encodeAlgorithm) throws Exception {
		
		String cypherString = encodeAlgorithm.encode(123L);
		
		Entry<Long, Date> decode = encodeAlgorithm.decode(cypherString);
		
		System.out.println(decode.getKey() == 123L);

	}
	
	public static void main(String[] args) throws Exception {
		EncodeAlgorithmTest encodeAlgorithmTest = new EncodeAlgorithmTest();
		EncodeAlgorithm encodeAlgorithm = new EncodeAlgorithm();
		encodeAlgorithmTest.testEncodeDecode(encodeAlgorithm);		
	}
}
