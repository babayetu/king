package com.king.datastructure;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * TopK data structure updater
 * Producer use queue ad async mechanism, it just put message in queue and return response to client
 * This consumer is in charge of update TopK structure
 * Http client will get high performance and throughput.
 * 
 * @author liujingjing
 *
 */
public class Consumer implements Runnable {
	private BlockingQueue<Message> queue;
	private String name;
	private ConcurrentHashMap<Long, TopK> scoreArray;
    
    public Consumer(String name, 
    				BlockingQueue<Message> q,
    				ConcurrentHashMap<Long, TopK> scoreArray){
    	this.name = name;
        this.queue=q;
        this.scoreArray = scoreArray;
    }
 
    @Override
    public void run() {
        try{
            Message msg;
            //获取并处理消息直到接收到“exit”消息
            while((msg = queue.take()).getName() !="exit"){
            	System.out.println(name + " Consumed "+msg);
            	
            	TopK topK = scoreArray.get(msg.getLevel());
            	
            	if (topK == null) {
            		TopK newTopK = new TopK();
            		newTopK.insert(msg.getUserId(), msg.getScore());
            		scoreArray.put(msg.getLevel(), newTopK);
            	} else {
            		topK.insert(msg.getUserId(), msg.getScore());         		
            	}
            }
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
