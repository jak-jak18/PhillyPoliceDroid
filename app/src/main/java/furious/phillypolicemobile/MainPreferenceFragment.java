package furious.phillypolicemobile;


import java.util.Set;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainPreferenceFragment extends PreferenceFragment {

	CheckBoxPreference checkBox;
	CheckBoxPreference checkBox1;
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
	        
	        if(checkBox.isChecked()){
	        	district_list.setEnabled(true);
				district_list.setSelectable(true);
				checkBox1.setEnabled(true);
				checkBox1.setSelectable(true);
	        }else if(!checkBox.isChecked()){
	        	district_list.setEnabled(false);
				checkBox1.setEnabled(false);
				district_list.setSelectable(false);
				checkBox1.setSelectable(false);
	        }

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
						
					}else{
						// IS UNCHECKED
						district_list.setEnabled(false);
						checkBox1.setEnabled(false);
						district_list.setSelectable(false);
						checkBox1.setSelectable(false);
					}
					
					return true;
				}
	        	
	        });
	        

	        
	        
	 }
	 
	 
	 
	 
	}