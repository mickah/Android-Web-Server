package com.android.aws.webserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.SparseArray;

import com.android.aws.app.AppLog;

public class ChatHandler implements HttpRequestHandler {
	//private Context context = null;
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition newMessageReceived = lock.newCondition();

	private int counter = -1;
	private final int MAX_MESSAGES_SAVED = 20;
	SparseArray<String> Lastmessages = new SparseArray<String>();
	

	Map<String, String> newContent = new HashMap<String, String>();
	
	public ChatHandler(Context context){
		//this.context = context;
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext httpContext) throws HttpException, IOException {
		String bodyResponse = new String();
		AppLog.logString("CH:UpdateHandling: " + request.getRequestLine().getMethod());

		if (request instanceof HttpEntityEnclosingRequest) { //test if request is a POST
		    HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
		    String body = EntityUtils.toString(entity); //here you have the POST body
			try {
				// Parsing JSon request
				JSONObject mjsReceived = new JSONObject(body);
			    
			    String command = mjsReceived.getString("command");
			    if(command != null ){
			    	AppLog.logString("CH: command: " + command);
			    	if(command.equals("newMessage")){
			    		registerNewMessage(mjsReceived.getString("message"),mjsReceived.getString("author"));			    		
			    	}else if(command.equals("waitingUpdate")){
			    		bodyResponse = getUpdate(mjsReceived.getInt("lastMsgId"));
			    	}
			    }

				response.setEntity(new StringEntity(bodyResponse));
				AppLog.logString("Respond Post: " + bodyResponse);

			} catch (JSONException e) {
				e.printStackTrace();
			} 
		}
			
		
	}
private Boolean registerNewMessage(String message, String author){

	lock.lock();
	if(message != null){
		AppLog.logString("New content Post: " + message);
		if(counter == Integer.MAX_VALUE)
    		counter = 1;
    	else
    		counter++;
		
		if(counter - MAX_MESSAGES_SAVED > 0)
			Lastmessages.delete(counter - MAX_MESSAGES_SAVED);
		Lastmessages.put(counter, author + ": " + message);
		
		// Release all handler waiting a message
		newMessageReceived.signalAll();
		lock.unlock();
		return true;
	}else{
		AppLog.logWarningString("Null message");
		lock.unlock();
		return false;
	}
	
    	
}

private String getUpdate(int lastMessageNumberReceived){

	String messageToSend = new String();
	int currentCounter = counter;
	
	if(lastMessageNumberReceived > currentCounter){
		lastMessageNumberReceived = -1;
	}
	
	if(currentCounter == lastMessageNumberReceived){
		// Waiting for new messages
		lock.lock();
		try {
			while(counter == currentCounter){
				newMessageReceived.await();
			}
			currentCounter = counter;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	       lock.unlock();
	     }
	}
	AppLog.logString("currentCounter: " + currentCounter + " :: lastMessageNumberReceived: " +lastMessageNumberReceived);
	if(currentCounter - lastMessageNumberReceived > MAX_MESSAGES_SAVED)
		lastMessageNumberReceived = currentCounter - MAX_MESSAGES_SAVED;
	for (int i=lastMessageNumberReceived+1;i <= currentCounter; i++)
		messageToSend += Lastmessages.get(i, "Plop System ^^") + "\n";

	AppLog.logString("Stop Awaiting ...");
	newContent.put("updateContent",messageToSend);
	newContent.put("msgId","" + currentCounter);
	JSONObject mjsToSend = new JSONObject(newContent);
	
	return mjsToSend.toString();
}

}
