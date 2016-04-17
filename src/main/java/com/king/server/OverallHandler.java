package com.king.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import com.king.datastructure.Message;
import com.king.datastructure.TopK;
import com.king.utils.Constant;
import com.king.utils.UrlRouting;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class OverallHandler implements HttpHandler {
	private BlockingQueue<Message> queue;
	private ConcurrentHashMap<Integer, TopK> scoreArray;
	
    public OverallHandler(BlockingQueue<Message> queue, ConcurrentHashMap<Integer, TopK> scoreArray) {
		this.queue = queue;
		this.scoreArray = scoreArray;
	}

	public void handle(HttpExchange exchange) throws IOException {
    	URI requestURI = exchange.getRequestURI();
    	
    	String urlPath = requestURI.getPath();
    	
    	String[] pathArray = urlPath.split("/");
    	
    	if (pathArray != null && pathArray.length == 3) {
    		if (pathArray[2].equals(UrlRouting.LOGIN)) {
    			loginHandler(exchange);
    		} else if (pathArray[2].equals(UrlRouting.SCORE)) {
    	    	String param = requestURI.getQuery();
    	    	String[] params = param.split("=");
    	    	if (params != null && params.length == 2) {
    	    		if (params[0].equals(Constant.SESSIONKEY))  {
    	    			String sessionKey = params[1];
    	    			
    	    			int levelId = Integer.parseInt(pathArray[1]);
    	    			
    	    			//acquire post body
    	    			InputStream requestBody = exchange.getRequestBody();
    	    			
    	    			byte[] buffer = new byte[32];
    	    	        int readBytes = 0;
    	    	        if ((readBytes = requestBody.read(buffer)) > 0){
    	    	            String bodyString = new String(buffer, 0, readBytes);
    	    	            try {
        	    	        	long score  = Long.parseLong(bodyString);
        	    	        	if (score > Constant.UNSIGNINTMAX) {
        	    	        		errorResponse(exchange,"exceed unsigned int range");
        	    	        		return;
        	    	        	}
//        	    	        	System.out.println("score:"+score);
//            	    			System.out.println("sessionKey:"+sessionKey);
//            	    			System.out.println("levelId:"+levelId);
            	    			postScoreHandler(exchange, levelId, score, sessionKey);
    						} catch ( NumberFormatException e) {
    							errorResponse(exchange,"score not numberic");
    							return;
    						} catch (InterruptedException e) {
								e.printStackTrace();
							}
    	    	        } else {
    	    	        	errorResponse(exchange,"do post body found");
    	    	        	return;
    	    	        }	    			
    	    		} else {
    	    			errorResponse(exchange,"sessionkey parameter not found");
    	    			return;
    	    		}
    	    	} else {
    	    		errorResponse(exchange,"wrong parameters");
    	    		return;
    	    	}

    		} else if (pathArray[2].equals(UrlRouting.HIGHSCORELIST)) {
    			getHighScoreHandler(exchange);
    		}
    	} else {
    		errorResponse(exchange,"wrong url path");
    	}    	
    }
    
    private void loginHandler(HttpExchange exchange) {
    	
    }
    
    private void postScoreHandler(HttpExchange exchange, int levelId, long score, String sessionkey) throws IOException, InterruptedException {
//    	queue.put(msg);
    	Message message = new Message(sessionkey, 123, score, levelId);
    	queue.put(message);
    	
    	Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(200, 0);
    	OutputStream responseBody = exchange.getResponseBody();
    	responseBody.write("".getBytes());
    	responseBody.close();
    }
    
    private void getHighScoreHandler(HttpExchange exchange) {
    	
    }
    
    private void errorResponse(HttpExchange exchange, String msg) throws IOException {
    	Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(200, 0);
    	OutputStream responseBody = exchange.getResponseBody();
    	responseBody.write(msg.getBytes());
    	responseBody.close();
    }
}
