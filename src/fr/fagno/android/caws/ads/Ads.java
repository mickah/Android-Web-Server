package fr.fagno.android.caws.ads;

import android.app.Activity;
import android.widget.LinearLayout;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import fr.fagno.android.caws.app.AppLog;

public class Ads implements AdListener{
	public final static Boolean AD_ACTIVATED = false;
	public final static String ID = "";
	private AdView adView = null;
	Activity act;
	
	public void OnCreateAds(Activity act, LinearLayout lay){
		if(AD_ACTIVATED){
			this.act=act;
			
			// Create the adView
		    adView = new AdView(act, AdSize.BANNER, Ads.ID);
		    adView.setAdListener(this);
	
		    // Add the adView to it
		    lay.addView(adView);
		    
		    AdRequest request = new AdRequest();
	
		    //request.addTestDevice(AdRequest.TEST_EMULATOR);
		    //request.addTestDevice("");    // My T-Mobile test phone
		    
		    // Initiate a generic request to load it with an ad
		    adView.loadAd(request);
		}
	}
	
	public void onDestroy(){
		if (adView != null) {
			adView.destroy();
		}
	}
	
	@Override
	public void onFailedToReceiveAd(Ad ad, AdRequest.ErrorCode errorCode) {
		AppLog.logString("failed to receive ad (" + errorCode + ")");
	}

	@Override
	public void onReceiveAd(Ad ad) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPresentScreen(Ad ad) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDismissScreen(Ad ad) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeaveApplication(Ad ad) {
		// TODO Auto-generated method stub
		
	}
	
}
