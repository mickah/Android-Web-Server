package fr.fagno.android.caws.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import fr.fagno.android.caws.R;

public class CAWSPreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
	}
}
