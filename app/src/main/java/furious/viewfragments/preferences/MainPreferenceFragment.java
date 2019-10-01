package furious.viewfragments.preferences;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import furious.phillypolicemobile.R;
import furious.utils.HttpClientInfo;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainPreferenceFragment extends PreferenceFragment {

	CheckBoxPreference EN_ableSound, checkBox, checkBox1;
	Preference devID_key;
	MultiSelectListPreference district_list;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preference);

		checkBox = (CheckBoxPreference) findPreference("checkbox_preference");
		checkBox1 = (CheckBoxPreference) findPreference("UCV_checkbox_preference");
		district_list = (MultiSelectListPreference) findPreference("district_preference");
		EN_ableSound = (CheckBoxPreference) findPreference("EN_ableSound");
		devID_key = (Preference) findPreference("devID_key");

		String finADD = HttpClientInfo.getMD5(HttpClientInfo.getMacAddress(getActivity()));

		setItems(checkBox.isChecked());

		devID_key.setTitle("System Device ID");
		devID_key.setSummary(finADD+"\n"+isActiveDevice());
		devID_key.setSelectable(false);


		EN_ableSound.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
			//	TODO: add result when preference clicked
				return false;
			}
		});

		checkBox.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference preference) {
				setItems(checkBox.isChecked());
				return true;
			}

		});
	}

	void setItems(boolean isChecked){
		district_list.setEnabled(isChecked);
		district_list.setSelectable(isChecked);
		checkBox1.setEnabled(isChecked);
		checkBox1.setSelectable(isChecked);
		EN_ableSound.setEnabled(isChecked);
		EN_ableSound.setSelectable(isChecked);
	}

	public String isActiveDevice() {
		return "Active";
	}
}