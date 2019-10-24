package furious.viewfragments.preferences;


import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.util.Log;

import furious.phillypolicemobile.PoliceUpdateService;
import furious.phillypolicemobile.R;
import furious.utils.HttpClientInfo;
import furious.utils.Utils;


public class MainPreferenceFragment extends PreferenceFragment {

	CheckBoxPreference EN_ableSound, checkBox, checkBox1;
	Preference devID_key;
	MultiSelectListPreference district_list;

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
				if(checkBox.isChecked()){
					Log.i("SETTING INFO","YOU CHECKED A BOX");
					setItems(checkBox.isChecked());
				}else{
					Log.i("SETTING INFO","NO NO BOX CHECKED");
					if(Utils.isMyServiceRunning(PoliceUpdateService.class, getActivity())){
                        Intent intent = new Intent(getActivity(), PoliceUpdateService.class);
                        intent.putExtra("PoliceServiceCode", 9911);
                        getActivity().startService(intent);
                        getActivity().stopService(intent);

                        Log.i("KILL, KILL, KILL","STOPPING SERVICE STUFF");
                    }else{
                        Log.i("SERVICE NO RUNNING","NOT CHECKED, NO SERVICE");
                    }


				}

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