package furious.viewfragments.preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import furious.viewfragments.main.MainStart;


public class MainPreferenceActivity extends Activity {

@Override
 protected void onCreate(Bundle savedInstanceState) {
  // TODO Auto-generated method stub
  super.onCreate(savedInstanceState);
  
  android.app.FragmentManager mFragmentManager = getFragmentManager();
  android.app.FragmentTransaction mFragmentTransaction = mFragmentManager
                          .beginTransaction();
  MainPreferenceFragment mPrefsFragment = new MainPreferenceFragment();
  mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
  mFragmentTransaction.commit();
  

 }
 
 @Override
 public void onBackPressed(){
   super.onBackPressed();
   	Intent intent = new Intent(MainPreferenceActivity.this, MainStart.class);
	intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	startActivity(intent);
	finish();
 }
 

}