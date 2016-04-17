package com.king.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.king.datastructure.Consumer;
import com.king.datastructure.Message;
import com.king.datastructure.TopK;
import com.sun.net.httpserver.HttpServer;

/**
 * HTTP service
 * main entrance of program
 * @author liujingjing
 *
 */
public class ScoreServer {
	private static BlockingQueue<Message> queue = new ArrayBlockingQueue<>(100);
	private static ConcurrentHashMap<Long, TopK>	scoreArray = new ConcurrentHashMap<Long, TopK>();
	private static ConcurrentHashMap<Long, String>	userToSession = new ConcurrentHashMap<Long, String>();
	
    public static void main(String[] args) throws IOException {
    	/**
    	 * create consumer event processors
    	 */
    	int consumerThreads = 10;
        ArrayList<Runnable> consumeri = new ArrayList<Runnable>();
        for (int i = 0; i < consumerThreads; i++) {
        	consumeri.add(new Consumer("consumer-" + i,queue, scoreArray));
		}
        for (int i = 0; i < consumerThreads; i++) {
        	new Thread(consumeri.get(i)).start();
		}

        /**
         * initilize raw http server
         */
        InetSocketAddress addr = new InetSocketAddress(18080);
        HttpServer server = HttpServer.create(addr, 0);

        server.createContext("/", new OverallHandler(queue,userToSession,scoreArray));
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server is listening on port 18080");
    }
    
   
}
