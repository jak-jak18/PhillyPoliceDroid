package furious.utils;

	
	import android.app.ActivityManager;
	import android.content.Context;
	import android.content.Intent;
	import android.content.SharedPreferences;
	import android.graphics.Bitmap;
	import android.graphics.BitmapFactory;
	import android.preference.PreferenceManager;
	import android.support.v4.view.ViewPager;
	import android.util.Log;
	import android.view.LayoutInflater;
	import android.view.View;
	import android.widget.TextView;

	import java.io.IOException;
	import java.io.InputStream;
	import java.io.OutputStream;
	import java.net.HttpURLConnection;
	import java.net.URL;

	import furious.phillypolicemobile.PoliceUpdateService;
	import furious.phillypolicemobile.R;

public class Utils {

		private final static String NON_THIN = "[^iIl1\\.,']";

	    public static void CopyStream(InputStream is, OutputStream os)
	    {
	        final int buffer_size=1024;
	        try
	        {
	        	
	            byte[] bytes=new byte[buffer_size];
	            for(;;)
	            {
	              //Read byte from input stream
	            	
	              int count=is.read(bytes, 0, buffer_size);
	              if(count==-1)
	                  break;
	              
	              //Write byte from output stream
	              os.write(bytes, 0, count);
	            }
	        }
	        catch(Exception ex){}
	    }

		private static int textWidth(String str) {
			return (int) (str.length() - str.replaceAll(NON_THIN, "").length() / 2);
		}

		public static String ellipsize(String text, int max) {

			if (textWidth(text) <= max)
				return text;

			// Start by chopping off at the word before max
			// This is an over-approximation due to thin-characters...
			int end = text.lastIndexOf(' ', max - 3);

			// Just one long word. Chop it off.
			if (end == -1)
				return text.substring(0, max-3) + "...";

			// Step forward as long as textWidth allows.
			int newEnd = end;
			do {
				end = newEnd;
				newEnd = text.indexOf(' ', end + 1);

				// No more spaces.
				if (newEnd == -1)
					newEnd = text.length();

			} while (textWidth(text.substring(0, newEnd) + "...") < max);

			return text.substring(0, end) + "...";
		}


	public static Bitmap getBitmapFromURL(String src){
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	public static String CVTdistrict(String string) {

		if (string.equals("1st")) {
			return "1";
		} else if (string.equals("3rd")) {
			return "3";
		} else if (string.equals("17th")) {
			return "17";
		} else if (string.equals("2nd")) {
			return "2";
		} else if (string.equals("7th")) {
			return "7";
		} else if (string.equals("8th")) {
			return "8";
		} else if (string.equals("15th")) {
			return "15";
		} else if (string.equals("19th")) {
			return "19";
		} else if (string.equals("18th")) {
			return "18";
		} else if (string.equals("16th")) {
			return "16";
		} else if (string.equals("12th")) {
			return "12";
		} else if (string.equals("5th")) {
			return "5";
		} else if (string.equals("39th")) {
			return "39";
		} else if (string.equals("35th")) {
			return "35";
		} else if (string.equals("14th")) {
			return "14";
		} else if (string.equals("6th")) {
			return "6";
		} else if (string.equals("9th")) {
			return "9";
		} else if (string.equals("22nd")) {
			return "22";
		} else if (string.equals("25th")) {
			return "25";
		} else if (string.equals("24th")) {
			return "24";
		} else if (string.equals("26th")) {
			return "26";
		}else{
			return string;
		}






	}

	public static String addTH(String string){

		if (string.equals("1")) {
			return "1st";
		} else if (string.equals("3")) {
			return "3rd";
		} else if (string.equals("22")) {
			return "22nd";
		} else if (string.equals("2")) {
			return "2nd";
		} else{
			return string + "th";
		}
	}


	public static class DepthPageTransformer implements ViewPager.PageTransformer {
		private static final float MIN_SCALE = 0.75f;

		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0f);

			} else if (position <= 0) { // [-1,0]
				// Use the default slide transition when moving to the left page
				view.setAlpha(1f);
				view.setTranslationX(0f);
				view.setScaleX(1f);
				view.setScaleY(1f);

			} else if (position <= 1) { // (0,1]
				// Fade the page out.
				view.setAlpha(1 - position);

				// Counteract the default slide transition
				view.setTranslationX(pageWidth * -position);

				// Scale the page down (between MIN_SCALE and 1)
				float scaleFactor = MIN_SCALE
						+ (1 - MIN_SCALE) * (1 - Math.abs(position));
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0f);
			}
		}
	}

	public static void checkforUpdate(Context mContext){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean isEnabled = prefs.getBoolean("checkbox_preference", false);

		if(isEnabled){

			Log.i("YES THIS IS SERVICE","START IT UPP");
			Intent service = new Intent(mContext, PoliceUpdateService.class);
			service.putExtra("PoliceServiceCode", 888);
			mContext.startService(service);

		}else{

			Log.i("NO SERVICE ENABLED","NOTJING TO START");
		}


	}


	public static boolean isMyServiceRunning(Class<?> serviceClass, Context mCont) {
		ActivityManager manager = (ActivityManager) mCont.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}


	}
	
	
	
	
