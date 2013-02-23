package fr.fagno.android.caws.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.HttpException;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import fr.fagno.android.caws.app.AppLog;
import fr.fagno.android.caws.constants.Constants;

public class WebServer extends Thread {
	private static final String SERVER_NAME = "AndWebServer";
	private static final String ALL_PATTERN = "*";
	private static final String CHAT_PATTERN = "/chat*";
	private static final String ASSETS_PATTERN = "/assets*";

	private boolean isRunning = false;
	private Context context = null;
	private int serverPort = 0;

	private BasicHttpProcessor httpproc = null;
	private BasicHttpContext httpContext = null;
	private HttpService httpService = null;
	private HttpRequestHandlerRegistry registry = null;
	private NotificationManager notifyManager = null;

	private ServerSocket serverSocket;

	public WebServer(Context context, NotificationManager notifyManager){
		super(SERVER_NAME);

		this.setContext(context);
		this.setNotifyManager(notifyManager);

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

		serverPort = Integer.parseInt(pref.getString(Constants.PREF_SERVER_PORT, "" + Constants.DEFAULT_SERVER_PORT));
		httpproc = new BasicHttpProcessor();
		httpContext = new BasicHttpContext();

		httpproc.addInterceptor(new ResponseDate());
		httpproc.addInterceptor(new ResponseServer());
		httpproc.addInterceptor(new ResponseContent());
		httpproc.addInterceptor(new ResponseConnControl());

		httpService = new HttpService(httpproc, 
				new DefaultConnectionReuseStrategy(),
				new DefaultHttpResponseFactory());

		registry = new HttpRequestHandlerRegistry();
		registry.register(ALL_PATTERN, new HomePageHandler(context));
		registry.register(CHAT_PATTERN, new ChatHandler(context));
		registry.register(ASSETS_PATTERN, new AssetsHandler(context));
		httpService.setHandlerResolver(registry);
	}

	@Override
	public void run() {
		super.run();
		Looper.prepare();

		try {		
			serverSocket = new ServerSocket(serverPort);
			serverSocket.setReuseAddress(true);

			AppLog.logString("Webserver: open");
			while(isRunning){
				try {
					final Socket socket = serverSocket.accept();
					Thread proceedRequest = new Thread(){
						@Override
						public void run() {
							DefaultHttpServerConnection serverConnection = new DefaultHttpServerConnection();				        	
							try {
								AppLog.logString("Webserver: bind");
								serverConnection.bind(socket, new BasicHttpParams());
								httpService.handleRequest(serverConnection, httpContext);
								serverConnection.shutdown();
								AppLog.logString("Webserver: shutdown");
							} catch (IOException e) {
								e.printStackTrace();
							}
							catch (HttpException e) {
								e.printStackTrace();
							}
						}
					};
					proceedRequest.start();
				} catch (IOException e) {
					e.printStackTrace();
					AppLog.logString("Webserver: error thread");
				} 
			}
			serverSocket.close();
			AppLog.logString("Webserver: close");
		} 
		catch (IOException e) {
			e.printStackTrace();
			AppLog.logString("Webserver: error socket");
		}

		Looper.loop();
	}

	public synchronized void startThread() {
		isRunning = true;
		super.start();
	}

	public synchronized void stopThread(){
		isRunning = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setNotifyManager(NotificationManager notifyManager) {
		this.notifyManager = notifyManager;
	}

	public NotificationManager getNotifyManager() {
		return notifyManager;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}
}
