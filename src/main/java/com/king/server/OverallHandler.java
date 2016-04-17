package com.king.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.AbstractMap;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import com.king.datastructure.Message;
import com.king.datastructure.TopK;
import com.king.utils.Constant;
import com.king.utils.EncodeAlgorithm;
import com.king.utils.UrlRouting;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class OverallHandler implements HttpHandler {
	private BlockingQueue<Message> queue;
	private ConcurrentHashMap<Long, String> userToSession;
	private ConcurrentHashMap<Long, TopK>	scoreArray;
	private EncodeAlgorithm encoder;
	
    public OverallHandler(BlockingQueue<Message> queue, 
    		ConcurrentHashMap<Long, String> userToSession,
    		ConcurrentHashMap<Long, TopK>	scoreArray) {
		this.queue = queue;
		this.userToSession = userToSession;
		this.encoder = new EncodeAlgorithm();
		this.scoreArray = scoreArray;
	}
    
    /**
     * Overall routing dispatcher, check input url, parameter and body correctness
     */
	public void handle(HttpExchange exchange) throws IOException {
    	URI requestURI = exchange.getRequestURI();
    	
    	String urlPath = requestURI.getPath();
    	String method = exchange.getRequestMethod();
    	
    	System.out.println("method:" + method);
    	
    	String[] pathArray = urlPath.split("/");
    	
    	if (pathArray != null && pathArray.length == 3) {
    		if (pathArray[2].equals(UrlRouting.LOGIN) && method.equals(Constant.HTTPGET)) {
    			try {
    				long userId = Long.parseLong(pathArray[1]);
        			loginHandler(exchange, userId);
				} catch (NumberFormatException e) {
					errorResponse(exchange,"userid not numberic");
					return;
				}
    			
    		} else if (pathArray[2].equals(UrlRouting.SCORE) && method.equals(Constant.HTTPPOST)) {
    	    	String param = requestURI.getQuery();
    	    	String[] params = param.split("=");
    	    	if (params != null && params.length == 2) {
    	    		if (params[0].equals(Constant.SESSIONKEY))  {
    	    			String sessionKey = params[1];
    	    			
    	    			long levelId = Long.parseLong(pathArray[1]);
    	    			if (levelId > Constant.UNSIGNINTMAX) {
	    	        		errorResponse(exchange,"levelId exceed unsigned int range");
	    	        		return;
	    	        	}
    	    			
    	    			//acquire post body
    	    			InputStream requestBody = exchange.getRequestBody();
    	    			
    	    			byte[] buffer = new byte[32];
    	    	        int readBytes = 0;
    	    	        if ((readBytes = requestBody.read(buffer)) > 0){
    	    	            String bodyString = new String(buffer, 0, readBytes);
    	    	            try {
        	    	        	long score  = Long.parseLong(bodyString);
        	    	        	if (score > Constant.UNSIGNINTMAX) {
        	    	        		errorResponse(exchange,"score exceed unsigned int range");
        	    	        		return;
        	    	        	}
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

    		} else if (pathArray[2].equals(UrlRouting.HIGHSCORELIST) && method.equals(Constant.HTTPGET)) {
    			try {
    				long levelId = Long.parseLong(pathArray[1]);
    				getHighScoreHandler(exchange, levelId);
				} catch (NumberFormatException e) {
					errorResponse(exchange,"level id not numberic");
					return;
				}
    			
    		}
    	} else {
    		errorResponse(exchange,"wrong url path");
    	}    	
    }
    
	/**
	 * handle login function, return session key which is 10 minutes valid
	 * @param exchange
	 * @param userId
	 * @throws IOException 
	 */
    private void loginHandler(HttpExchange exchange, long userId) throws IOException {
    	String sessionKey = userToSession.get(userId);
    	if (sessionKey == null) {
    		String newSessionKey = encoder.encode(userId);
    		userToSession.put(userId, newSessionKey);

    		buildResponse(exchange, newSessionKey);
    		return;

    	} else {
    		Date now = new Date();
    		Map.Entry<Long, Date> decode = encoder.decode(sessionKey);
    		
    		if ((now.getTime() - decode.getValue().getTime()) > Constant.SESSIONTIMEOUT) {
    			String newSessionKey = encoder.encode(userId);
    			userToSession.put(userId, newSessionKey);
        		buildResponse(exchange, newSessionKey);
        		return;
    		} else {
    			buildResponse(exchange, sessionKey);
        		return;
    		}
    	}
    }
    
    /**
     * handler post user score function, asynchronize request to consumer function to process hashmap
     * @param exchange - long, to hold unsigned int value
     * @param levelId - long, to hold unsigned int value
     * @param score - long, to hold unsigned int value
     * @param sessionkey
     * @throws IOException
     * @throws InterruptedException
     */
    private void postScoreHandler(HttpExchange exchange, long levelId, long score, String sessionkey) throws IOException, InterruptedException {
    	//fetch session
    	Map.Entry<Long, Date> decodeResult = encoder.decode(sessionkey);
    	String dbSessionKey = userToSession.get(decodeResult.getKey());
    	if (dbSessionKey == null || ! dbSessionKey.equals(sessionkey)) {
    		buildResponse(exchange, "rejected");
    		return;
    	}
    	
    	Message message = new Message(sessionkey, decodeResult.getKey(), score, levelId);
    	queue.put(message);
    	
    	buildResponse(exchange, "");
    	return;
    }
    
    private void getHighScoreHandler(HttpExchange exchange, long levelId) throws IOException {
    	TopK topK = scoreArray.get(levelId);
    	
    	if (topK == null) {
    		buildResponse(exchange,"");
    		return;
    	} else {
    		long[] userId = topK.getUserId();
    		long[] scoreId = topK.getScore();
    		HashSet<Long> filterUser = new HashSet<Long>();
    		
    		StringBuffer sb = new StringBuffer();
    		for (int i = userId.length - 1; i >= 0; i--) {
				if (userId[i] != Integer.MIN_VALUE) {
					filterUser.add(userId[i]);
					sb.append(userId[i]).append("=").append(scoreId[i]).append(",");
				}
			}
    		
    		//remove last comma
    		String retStr = sb.toString();
    		String newRet = null;
    		if (! retStr.equals("") && retStr.charAt(retStr.length() - 1) == ',') {
    			newRet = retStr.substring(0, retStr.length() - 2);
    		}
    		buildResponse(exchange,newRet);
    		return;
    	}
    }
    
    
    private void buildResponse(HttpExchange exchange, String resp) throws IOException {
		Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(200, 0);
    	OutputStream responseBody = exchange.getResponseBody();
    	responseBody.write(resp.getBytes());
    	responseBody.close();
    }
    /**
     * common error response handler
     * @param exchange
     * @param msg
     * @throws IOException
     */
    private void errorResponse(HttpExchange exchange, String msg) throws IOException {
    	Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(200, 0);
    	OutputStream responseBody = exchange.getResponseBody();
    	responseBody.write(msg.getBytes());
    	responseBody.close();
    }
}
