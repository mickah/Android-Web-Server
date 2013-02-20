package fr.fagno.android.caws.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.fagno.android.caws.R;
import fr.fagno.android.caws.ads.Ads;
import fr.fagno.android.caws.app.AppSettings;
import fr.fagno.android.caws.constants.Constants;
import fr.fagno.android.caws.service.HTTPService;
import fr.fagno.android.caws.utility.Utility;

public class CAWSActivity extends Activity {
	private Ads ads = new Ads();//Class for manage ads (can be removed easily)
	
	private static final int PREFERENCE_REQUEST_CODE = 1001;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main); 
		setButtonHandlers();
		boolean isRunning = AppSettings.isServiceStarted(this);
		setButtonText(isRunning);
		setInfoText(isRunning);
		
		ads.OnCreateAds(this, (LinearLayout)findViewById(R.id.ad));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contextmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = true;

		switch (item.getItemId()) {
		case R.id.menuPreference:{
			startActivityForResult(new Intent(this,CAWSPreferenceActivity.class), PREFERENCE_REQUEST_CODE);
			break;
		}
		default:{
			result = super.onOptionsItemSelected(item);
		}
		}
		return result;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode){
		case PREFERENCE_REQUEST_CODE:{
			break;
		}
		}
	}

	private void setButtonHandlers() {
		((Button)findViewById(R.id.btnStartStop)).setOnClickListener(btnClick);
	}

	private void setButtonText(boolean isServiceRunning){
		((Button)findViewById(R.id.btnStartStop)).setText(
				getString(isServiceRunning ? R.string.stop_caption : R.string.start_caption));
	}

	private void setInfoText(boolean isServiceRunning){
		TextView txtLog = (TextView)findViewById(R.id.txtLog);
		String text = getString(R.string.log_notrunning);

		if(isServiceRunning){
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			text = getString(R.string.log_running) + "\nhttp://" + Utility.getLocalIpAddress() + ":" + pref.getString(Constants.PREF_SERVER_PORT, "" + Constants.DEFAULT_SERVER_PORT);
		}
		txtLog.setText(text);
	}

	private View.OnClickListener btnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btnStartStop:{
				Intent intent = new Intent(CAWSActivity.this,HTTPService.class);

				if(AppSettings.isServiceStarted(CAWSActivity.this)){
					stopService(intent);
					AppSettings.setServiceStarted(CAWSActivity.this, false);
					setButtonText(false);
					setInfoText(false);
				}
				else{
					startService(intent);
					AppSettings.setServiceStarted(CAWSActivity.this, true);
					setButtonText(true);
					setInfoText(true);
				}
				break;
			}
			}
		}
	};
	
	@Override
	  public void onDestroy() {
	    ads.onDestroy();
	    super.onDestroy();
	  }
}