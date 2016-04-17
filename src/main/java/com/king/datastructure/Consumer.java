package com.king.datastructure;

import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {
	private BlockingQueue<Message> queue;
	private String name;
    
    public Consumer(String name, BlockingQueue<Message> q){
    	this.name = name;
        this.queue=q;
    }
 
    @Override
    public void run() {
        try{
            Message msg;
            //获取并处理消息直到接收到“exit”消息
            while((msg = queue.take()).getName() !="exit"){
            Thread.sleep(10);
            System.out.println(name + " Consumed "+msg);
            }
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
