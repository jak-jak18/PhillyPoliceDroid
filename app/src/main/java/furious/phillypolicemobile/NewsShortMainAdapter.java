package furious.phillypolicemobile;


import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NewsShortMainAdapter extends BaseAdapter{
	Thread thread;

	Context context;
	ArrayList<NewsObject> newsList;
	LayoutInflater inflater;

	
	class NEWS_S_ViewHolder{
		public TextView MainNewsAlert;
		public TextView MainNewsTitle;
		public TextView MainNewsExcert;
		public TextView MainNewsAuthor;
		public TextView MainNewsDate;

	}
	
	
	public NewsShortMainAdapter(Context context, ArrayList<NewsObject> list){
		this.context = context;
		this.newsList = list;
		this.inflater = LayoutInflater.from(context);
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
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View convertView = arg1;
		NEWS_S_ViewHolder viewHolder;
		if(arg1 == null){
			viewHolder = new NEWS_S_ViewHolder();
			Log.i("LOOKING AT", Integer.toString(arg0));
			convertView = inflater.inflate(R.layout.news_short_row_obj, arg2, false);
			viewHolder.MainNewsAlert = (TextView) convertView.findViewById(R.id.MainNewsAlertTextView);
			viewHolder.MainNewsAuthor = (TextView) convertView.findViewById(R.id.MainNewsAuthorTextView);
			viewHolder.MainNewsExcert = (TextView) convertView.findViewById(R.id.MainNewsExcertTextView);
			viewHolder.MainNewsTitle = (TextView) convertView.findViewById(R.id.MainNewsTitleTextView);
			viewHolder.MainNewsDate = (TextView) convertView.findViewById(R.id.MainNewsDateTextView);
			
			
//			viewHolder.videoImg = (ImageView) convertView.findViewById(R.id.DistrictImageVideo);
//			viewHolder.videoTxtV = (TextView) convertView.findViewById(R.id.DistrictIsVideoTxt);
			convertView.setTag(viewHolder);
			
			
		}else{
			viewHolder = (NEWS_S_ViewHolder) convertView.getTag();
		}
			
		NewsObject newsObj = (NewsObject) newsList.get(arg0);
		
		//String stitle = ellipsize(newsObj.getStoryExcert(), 400);
		viewHolder.MainNewsAlert.setText(newsObj.getAlertType());
		viewHolder.MainNewsAuthor.setText("By: "+newsObj.getAuthor());
		viewHolder.MainNewsExcert.setText(newsObj.getStoryExcert());
		viewHolder.MainNewsTitle.setText(newsObj.getStoryTitle());
		viewHolder.MainNewsDate.setText(newsObj.getStoryDate());
			

		return convertView;
	}

	
	public void updateList(ArrayList<NewsObject> list) {
        this.newsList = list;
        notifyDataSetChanged();
    }
	
	


}