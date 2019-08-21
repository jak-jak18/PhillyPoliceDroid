package furious.viewfragments;


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

	CheckBoxPreference EN_ableSound;
	CheckBoxPreference checkBox;
	CheckBoxPreference checkBox1;
	Preference devID_key;
	MultiSelectListPreference district_list;
	
	 @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	  // TODO Auto-generated method stub
	  super.onCreate(savedInstanceState);
	  
	  // Load the preferences from an XML resource
	        addPreferencesFromResource(R.xml.preference);
	        
	        checkBox = (CheckBoxPreference) findPreference("checkbox_preference");
	        checkBox1 = (CheckBoxPreference) findPreference("UCV_checkbox_preference");
	        district_list = (MultiSelectListPreference) findPreference("district_preference");
		 	EN_ableSound = (CheckBoxPreference) findPreference("EN_ableSound");
		 	devID_key = (Preference) findPreference("devID_key");

		 	String macADD = HttpClientInfo.getMacAddress(getActivity());
		 	String finADD = HttpClientInfo.getMD5(macADD);


		 	if(checkBox.isChecked()){
	        	district_list.setEnabled(true);
				district_list.setSelectable(true);
				EN_ableSound.setSelectable(true);
				EN_ableSound.setEnabled(true);
				checkBox1.setEnabled(true);
				checkBox1.setSelectable(true);
	        }else if(!checkBox.isChecked()){
	        	district_list.setEnabled(false);
				checkBox1.setEnabled(false);
				EN_ableSound.setEnabled(false);
				district_list.setSelectable(false);
				checkBox1.setSelectable(false);
				EN_ableSound.setSelectable(false);
	        }


		 devID_key.setTitle("System Device ID");
		 devID_key.setSummary(finADD+"\n"+isActiveDevice());
		 devID_key.setSelectable(false);



		 EN_ableSound.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			 @Override
			 public boolean onPreferenceClick(Preference preference) {



				 return false;
			 }
		 });

	        checkBox.setOnPreferenceClickListener(new OnPreferenceClickListener(){

				@Override
				public boolean onPreferenceClick(Preference preference) {
					// TODO Auto-generated method stub
					if(checkBox.isChecked()){
						// IS CHECKED
						district_list.setEnabled(true);
						district_list.setSelectable(true);
						checkBox1.setEnabled(true);
						checkBox1.setSelectable(true);
						EN_ableSound.setEnabled(true);
						EN_ableSound.setSelectable(true);

					}else{
						// IS UNCHECKED
						district_list.setEnabled(false);
						checkBox1.setEnabled(false);
						district_list.setSelectable(false);
						checkBox1.setSelectable(false);
						EN_ableSound.setEnabled(false);
						EN_ableSound.setSelectable(false);
					}
					
					return true;
				}
	        	
	        });
	        

	        
	        
	 }


	public String isActiveDevice() {
		return "Active";
	}
}