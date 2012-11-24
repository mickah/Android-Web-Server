package com.android.aws.app;

import android.util.Log;

public class AppLog {
	public static final String LOG = "aws";
	public static final String WARNING = "awsWarnning";
	
	public static int logString(String message){
		return Log.i(LOG,message);
	}
	public static int logString(String message, String tag){
		return Log.i(tag,message);
	}
	public static int logWarningString(String message){
		return Log.i(WARNING,message);
	}
}
