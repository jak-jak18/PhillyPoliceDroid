package furious.phillypolicemobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.util.Log;


public class PoliceBroadcastReceiver extends BroadcastReceiver{

    boolean isEnabled;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        isEnabled = prefs.getBoolean("checkbox_preference", false);

		if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)){


            if(isEnabled){

                Log.i("START SERVICE X", "SETTING IS ENABLED");
                Intent service = new Intent(context,PoliceUpdateService.class);
                service.putExtra("PoliceServiceCode", 99);
                context.startService(service);


            }else{

                Log.i("START SERVICE X", "NO SERIVE BEGIN STARTED, check BOX FALSE");

            }

		}
	}
	
}