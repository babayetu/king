package com.king.datastructure;

import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {
	private BlockingQueue<Message> queue;
	
	
	public Producer(BlockingQueue<Message> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		for(int i=0; i<100; i++){
            Message msg = new Message(""+i,i,i,i);
            try {
                Thread.sleep(20);
                queue.put(msg);
                System.out.println("Produced "+msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //添加退出消息
        Message msg = new Message("exit",-1,-1,-1);
        try {
            queue.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }		
		
	}

	public BlockingQueue<Message> getQueue() {
		return queue;
	}

	public void setQueue(BlockingQueue<Message> queue) {
		this.queue = queue;
	}

}
