package furious.objadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import furious.dataobjs.NewStoryObject;
import furious.phillypolicemobile.R;
import furious.utils.ImageLoader;

import static furious.utils.Utils.ellipsize;


public class NewsAdapter extends BaseAdapter{

	Thread thread;
	private final static String READ_MORE = "[Click to Read More]";
	Context context;
	ImageLoader imageLoader;
	ArrayList<NewStoryObject> newsList;
	LayoutInflater inflater;
	
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

	
	public NewsAdapter(Context context, ArrayList<NewStoryObject> list){
		this.context = context;
		this.newsList = list;
		this.inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context);
	}
	
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
			
		NewStoryObject newsObj = (NewStoryObject) newsList.get(arg0);
		String stitle = ellipsize(newsObj.getDescription(), 400);
		viewHolder.author.setText("By: "+newsObj.getStoryAuthor());
		viewHolder.storyExcert.setText(stitle+READ_MORE);
		viewHolder.storyTitle.setText(newsObj.getTitle());
		viewHolder.alertType.setText(newsObj.getCategory());
		viewHolder.storyDate.setText(newsObj.getPubDate());
			
			if(newsObj.getTubeURL().equals("No Video")){
				
				viewHolder.layout.setVisibility(View.GONE);
			
			}else if(newsObj.isUC_Vid()){
				
				viewHolder.titleView.setText("Unsolved Crime Video");
				viewHolder.layout.setVisibility(View.VISIBLE);
			
			}else if(!newsObj.getTubeURL().isEmpty()){
				
				viewHolder.layout.setVisibility(View.VISIBLE);
				
			}else{
				viewHolder.layout.setVisibility(View.INVISIBLE);
			}


		imageLoader.DisplayImage(newsObj.getImageURL(), viewHolder.caption);

		
		return convertView;
	}
	

	
	public void updateList(ArrayList<NewStoryObject> list) {
        this.newsList = list;
        notifyDataSetChanged();
    }
	
	

	

}