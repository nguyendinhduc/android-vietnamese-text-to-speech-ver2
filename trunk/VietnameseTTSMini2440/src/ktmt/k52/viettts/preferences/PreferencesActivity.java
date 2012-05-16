package ktmt.k52.viettts.preferences;

import ktmt.k52.viettts.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;



public class PreferencesActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.my_prefrences);
	}
	    
}
