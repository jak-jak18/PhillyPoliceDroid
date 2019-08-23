package furious.objadapters;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import furious.dataobjs.PoliceUCVideoObject;
import furious.phillypolicemobile.R;
import furious.utils.ImageLoader;


public class PoliceUCVideosAdapter extends BaseAdapter{
	Thread thread;
	private final static String NON_THIN = "[^iIl1\\.,']";
	private final static String READ_MORE = "[Click to Read More]";
	Context context;
	ArrayList<PoliceUCVideoObject> ucv_list;
	LayoutInflater inflater;
	ImageLoader imageLoader;

	
	class UCV_ViewHolder{

		public TextView videoTitle;
		public TextView videoDescription;
		public ImageView captionURL;
		public TextView videoDate;
		public TextView videoRegion;
		public TextView videoCrimeType;
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
	
	
	
	public PoliceUCVideosAdapter(Context context, ArrayList<PoliceUCVideoObject> list){
		this.context = context;
		this.ucv_list = list;
		this.inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ucv_list.size();
	}
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return ucv_list.get(arg0);
	}
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View convertView = arg1;
		UCV_ViewHolder viewHolder;
		if(arg1 == null){
			viewHolder = new UCV_ViewHolder();
			Log.i("LOOKING AT", Integer.toString(arg0));
			convertView = inflater.inflate(R.layout.uc_vid_row, arg2, false);
			
			viewHolder.videoTitle = (TextView) convertView.findViewById(R.id.UCVideoTitle);
			viewHolder.videoDescription = (TextView) convertView.findViewById(R.id.UCVideoDescription);
			viewHolder.videoDate = (TextView) convertView.findViewById(R.id.UCVideoDate);
			viewHolder.videoRegion = (TextView) convertView.findViewById(R.id.UCVideoRegion);
			viewHolder.videoCrimeType = (TextView) convertView.findViewById(R.id.UCVideoCrimeType);
			viewHolder.captionURL = (ImageView) convertView.findViewById(R.id.UCVideoImageView);

			convertView.setTag(viewHolder);

		}else{
			viewHolder = (UCV_ViewHolder) convertView.getTag();
		}
			
		PoliceUCVideoObject UC_Obj = (PoliceUCVideoObject) ucv_list.get(arg0);
		String stitle = ellipsize(UC_Obj.getDescription(), 300);
		viewHolder.videoDescription.setText(replaceHTMLStuff(stitle+READ_MORE));
		viewHolder.videoTitle.setText(UC_Obj.getVideoTitle());
		viewHolder.videoCrimeType.setText(UC_Obj.getCrimeType());
		viewHolder.videoDate.setText(UC_Obj.getVideoDate());
		viewHolder.videoRegion.setText(UC_Obj.getDistrictDivision()+" Detective Division");

		imageLoader.DisplayImage(UC_Obj.getVideoImageURL(), viewHolder.captionURL);
		return convertView;
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
	
	public void updateList(ArrayList<PoliceUCVideoObject> list) {
        this.ucv_list = list;
        notifyDataSetChanged();
    }

	public String replaceHTMLStuff(String stry){

		String rQuoat = "&#8217;";
		String dPrime = "&#8243;";


		//right single quotation mark
		if(stry.contains(rQuoat)){
			stry = stry.replace(rQuoat,"'");
		}
		if(stry.contains(dPrime)){
			stry = stry.replace(dPrime,"″");
		}


		return stry;
	}
	
	
	private class LoadImage extends AsyncTask<String, Void, Bitmap>{

		private View view;
		public LoadImage(View view){
			this.view = view;
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			String daURL = params[0];
			Bitmap pic = null;
			
			try {
				InputStream in = new URL(daURL).openStream();
				pic = BitmapFactory.decodeStream(in);
			} catch (MalformedURLException e) {e.printStackTrace();} 
				catch (IOException e) {e.printStackTrace();}
			
			return pic;
		}
		
		protected void onPostExecute(Bitmap result){
			ImageView Img = (ImageView) view.findViewById(R.id.UCVideoImageView);
			Img.setImageBitmap(result);
			this.cancel(true);
		}
		
	}
	

}