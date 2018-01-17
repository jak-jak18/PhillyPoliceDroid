package furious.phillypolicemobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class PoliceBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)){
			Intent service = new Intent(context,PoliceUpdateService.class);
			service.putExtra("PoliceServiceCode", 99);
			context.startService(service);
			Log.i("Start Police Service()", "Hello!");
			
		}
	}
	
}