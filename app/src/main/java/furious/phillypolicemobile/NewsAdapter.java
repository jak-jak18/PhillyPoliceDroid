package furious.phillypolicemobile;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter{
	Thread thread;
	private final static String NON_THIN = "[^iIl1\\.,']";
	private final static String READ_MORE = "[Click to Read More]";
	Context context;
	ImageLoader imageLoader;
	ArrayList<NewsObject> newsList;
	LayoutInflater inflater;
//	TextView storyExcert;
//	TextView storyTitle;
//	TextView alertType;
//	ImageView caption;
//	ImageView videoImg;
//	TextView videoTxtV;
	
	class NEWS_ViewHolder{
		
		public TextView storyExcert;
		public TextView storyTitle;
		public ImageView caption;
		public TextView alertType;
		public TextView videoTxtV;
		public ImageView videoImg;
		public RelativeLayout layout;
		public TextView author;
		public TextView storyDate;
		public TextView titleView;
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
	
	
	
	public NewsAdapter(Context context, ArrayList<NewsObject> list){
		this.context = context;
		this.newsList = list;
		this.inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context);
	}
//	@Override
//	public int getViewTypeCount() {       
//	    return getCount();
//	}
//	@Override
//	public int getItemViewType(int position) {
//	    return position;
//	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return newsList.size();
	}
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return newsList.get(arg0);
	}
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	
	@SuppressLint("NewApi") @Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View convertView = arg1;
		NEWS_ViewHolder viewHolder;
		
		if(arg1 == null){

			viewHolder = new NEWS_ViewHolder();
			
			convertView = inflater.inflate(R.layout.news_obj_row, arg2, false);
			viewHolder.storyTitle = (TextView) convertView.findViewById(R.id.DistrictNewsTitle);
			viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.NewsObjHeaderLayout);
			viewHolder.titleView = (TextView) convertView.findViewById(R.id.textView1);
			viewHolder.caption = (ImageView) convertView.findViewById(R.id.DistrictNewsImageView);
			viewHolder.storyExcert = (TextView) convertView.findViewById(R.id.DistrictNewsExcert);
			viewHolder.alertType = (TextView) convertView.findViewById(R.id.NewsObjectALertType);
			viewHolder.author = (TextView) convertView.findViewById(R.id.DistrictNewsAuthor);
			viewHolder.storyDate = (TextView) convertView.findViewById(R.id.DistrictNewsDate);
		
			convertView.setTag(viewHolder);
							
		}else{
			
			viewHolder = (NEWS_ViewHolder) convertView.getTag();
		}
			
		NewsObject newsObj = (NewsObject) newsList.get(arg0);	
		String stitle = ellipsize(newsObj.getStoryExcert(), 400);
		viewHolder.author.setText("By: "+newsObj.getAuthor());
		viewHolder.storyExcert.setText(stitle+READ_MORE);
		//viewHolder.storyExcert.setText(newsObj.getStoryExcert());
		viewHolder.storyTitle.setText(newsObj.getStoryTitle());
		viewHolder.alertType.setText(newsObj.getAlertType());
		viewHolder.storyDate.setText(newsObj.getStoryDate());
			
			if(newsObj.getVideoURL().equals("No Video")){
				
				viewHolder.layout.setVisibility(View.GONE);
			
			}else if(newsObj.isUC_Vid()){
				
				viewHolder.titleView.setText("Unsolved Crime Video");
				viewHolder.layout.setVisibility(View.VISIBLE);
			
			}else if(!newsObj.getVideoURL().isEmpty()){
				
				viewHolder.layout.setVisibility(View.VISIBLE);
				
			}else{
				viewHolder.layout.setVisibility(View.INVISIBLE);
			}
			
			

		//new LoadImage(viewHolder.caption).execute(newsObj.getCaptionURL());
		imageLoader.DisplayImage(newsObj.getCaptionURL(), viewHolder.caption);
		//notifyDataSetChanged();

		
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
	
	public void updateList(ArrayList<NewsObject> list) {
        this.newsList = list;
        notifyDataSetChanged();
    }
	
	

	

}