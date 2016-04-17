package com.king.learn;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import com.king.datastructure.TopK;

/**
 * syntax sandbox 
 * @author liujingjing
 *
 */
@Deprecated
public class ConcurrentHashMapDemo {
	static ConcurrentHashMap<Long, TopK> sessionKey = new ConcurrentHashMap<Long, TopK>();

	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new Thread(new Read());
		Thread t2 = new Thread(new WriteA());
		Thread t3 = new Thread(new WriteB());
		t1.start();
		t2.start();
		t3.start();
		t1.join();
		t2.join();
		t3.join();
	}

	static class Read implements Runnable {

		public void run() {
			for (int i = 0; i <= 50; i++) {
				try {
					Thread.sleep(1000);
					TopK content = sessionKey.get(111L);
					System.out.println("ReadThread "
							+ Thread.currentThread().getId() + " : " + content);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	static class WriteA implements Runnable {

		public void run() {
			for (int i = 0; i <= 10; i++) {
				try {
					Thread.sleep(2000);
					TopK topk = sessionKey.get(111L);
					if (topk == null) {
						TopK newTopk = new TopK();
						newTopk.insert(i, i*10);
						sessionKey.put(111L, newTopk);
					} else {
						topk.insert(i, i*10);
					}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	static class WriteB implements Runnable {

		public void run() {
			for (int i = 10; i <= 20; i++) {
				try {
					Thread.sleep(2000);
					TopK topk = sessionKey.get(111L);
					if (topk == null) {
						TopK newTopk = new TopK();
						newTopk.insert(i, i*10);
						sessionKey.put(111L, newTopk);
					} else {
						topk.insert(i, i*10);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
