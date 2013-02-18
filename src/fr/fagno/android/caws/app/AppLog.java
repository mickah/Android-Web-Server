package fr.fagno.android.caws.app;

import fr.fagno.android.caws.constants.Constants;
import android.util.Log;

public class AppLog {
	public static final String LOG = "caws";
	public static final String WARNING = "cawsWarnning";
	
	public static void logString(String message){
		if(Constants.LOG_DEBUG)
			Log.i(LOG,message);
	}
	public static void logString(String message, String tag){
		if(Constants.LOG_DEBUG)
			Log.i(tag,message);
	}
	public static void logWarningString(String message){
		if(Constants.LOG_DEBUG)
			Log.i(WARNING,message);
	}
}
