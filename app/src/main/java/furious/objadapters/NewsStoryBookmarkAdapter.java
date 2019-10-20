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

import furious.dataobjs.NewsStoryBookmarkObject;
import furious.phillypolicemobile.R;
import furious.utils.ImageLoader;
import furious.utils.Utils;

public class NewsStoryBookmarkAdapter extends BaseAdapter{
	
	Thread thread;
	private final static String NON_THIN = "[^iIl1\\.,']";
	private final static String READ_MORE = "[Click to Read More]";
	Context context;
	ArrayList<NewsStoryBookmarkObject> bookmark_list;
	LayoutInflater inflater;
	ImageLoader imageLoader;

	
	class Bookmark_ViewHolder{
		
		public TextView videoTitle;
		public TextView videoDescription;
		public ImageView captionURL;
		public TextView videoDate;
		public TextView districtNumber;
		public TextView videoCrimeType;
		public TextView dcNumber;
		//public ImageView videoURL;
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
	
	
	
	public NewsStoryBookmarkAdapter(Context context, ArrayList<NewsStoryBookmarkObject> list){
		this.context = context;
		this.bookmark_list = list;
		this.inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return bookmark_list.size();
	}
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return bookmark_list.get(arg0);
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
		Bookmark_ViewHolder viewHolder;
		
		if(arg1 == null){
			
			viewHolder = new Bookmark_ViewHolder();
			Log.i("LOOKING AT", Integer.toString(arg0));
			convertView = inflater.inflate(R.layout.constraint_bookmark_row, arg2, false);
			
			viewHolder.videoTitle = (TextView) convertView.findViewById(R.id.BookmarkTitle);
			viewHolder.videoDescription = (TextView) convertView.findViewById(R.id.BookmarkDescription);
			viewHolder.videoDate = (TextView) convertView.findViewById(R.id.BookmarkVideoDate);
			viewHolder.districtNumber = (TextView) convertView.findViewById(R.id.BookmarkRegion);
			viewHolder.videoCrimeType = (TextView) convertView.findViewById(R.id.BookmarkCrimeType);
			viewHolder.captionURL = (ImageView) convertView.findViewById(R.id.BookmarkImageView);
			viewHolder.dcNumber = (TextView) convertView.findViewById(R.id.boomark_row_news_dcnum);

			convertView.setTag(viewHolder);

		}else{
			
			viewHolder = (Bookmark_ViewHolder) convertView.getTag();
		}
			
			NewsStoryBookmarkObject News_Obj = (NewsStoryBookmarkObject) bookmark_list.get(arg0);
			String stitle = ellipsize(News_Obj.getDescription(), 400);
			viewHolder.videoDescription.setText(stitle+READ_MORE);
			viewHolder.videoTitle.setText(News_Obj.getTitle());
			viewHolder.videoCrimeType.setText(News_Obj.getCategory());
			viewHolder.videoDate.setText(News_Obj.getStoryDate());
			viewHolder.districtNumber.setText(Utils.addTH(News_Obj.getDistrict())+" District");

			String idDc = News_Obj.getDCNumber();
			if(idDc == "false"){
				viewHolder.dcNumber.setVisibility(View.GONE);
			}else{
				viewHolder.dcNumber.setText(News_Obj.getDCNumber());
			}





			
			imageLoader.DisplayImage(News_Obj.getImageURL(), viewHolder.captionURL);

			return convertView;
	}
	
	

	
	public void updateList(ArrayList<NewsStoryBookmarkObject> list) {
        this.bookmark_list = list;
        notifyDataSetChanged();
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
			ImageView Img = (ImageView) view.findViewById(R.id.BookmarkImageView);
			Img.setImageBitmap(result);
			this.cancel(true);
		}
		
	}
	

}