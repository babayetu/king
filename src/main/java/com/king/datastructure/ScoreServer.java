package com.king.datastructure;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ScoreServer {
	public static void main(String[] args) {
        //创建大小为10的 BlockingQueue 
        BlockingQueue<Message> queue = new ArrayBlockingQueue<>(10);
        Producer producer = new Producer(queue);
        
        int consumerThreads = 10;
        ArrayList<Runnable> consumeri = new ArrayList<Runnable>();
        for (int i = 0; i < consumerThreads; i++) {
        	consumeri.add(new Consumer("con-" + i,queue));
		}

        //开启 producer线程向队列中生产消息
        new Thread(producer).start();
        //开启 consumer线程 中队列中消费消息
        for (int i = 0; i < consumerThreads; i++) {
        	new Thread(consumeri.get(i)).start();
		}
        System.out.println("Producer and Consumer has been started");
    }
}
