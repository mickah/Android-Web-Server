package com.android.aws.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.android.aws.R;

public class AWSPreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preference);
		
	}
}
