package furious.objadapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import furious.dataobjs.USMurderObject;
import furious.phillypolicemobile.R;
import furious.utils.ImageLoader;

import static furious.utils.Utils.ellipsize;


public class USMurderAdapter  extends RecyclerView.Adapter<USMurderAdapter.ViewHolder> {

    Context context;
    ArrayList<USMurderObject> crimeList;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    private final static String READ_MORE = "[Click to Read More]";

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView victimName;
        public TextView descData;
        public ImageView vitimImg;
        public RelativeLayout layout;
        public View layoutview;

        public ViewHolder(View v) {
            super(v);
            layoutview = v;
            descData = (TextView) v.findViewById(R.id.usmurder_desc);
            victimName = (TextView) v.findViewById(R.id.usmurder_name);
            vitimImg = (ImageView) v.findViewById(R.id.usmurder_img);
            layout = (RelativeLayout) v.findViewById(R.id.NewsObjHeaderLayout119);

        }
    }


    public USMurderAdapter(Context context, ArrayList<USMurderObject> list){
        this.context = context;
        this.crimeList = list;
        this.inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return crimeList.size();
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


            USMurderObject crObj = (USMurderObject) crimeList.get(position);
            String stitle = ellipsize(crObj.getDesc(),400);
            holder.descData.setText(stitle + READ_MORE);
            holder.victimName.setText(crObj.getVictimName());

            if(crObj.isNewsStory()){
                holder.layout.setVisibility(View.VISIBLE);
            }

            if(crObj.getImageURL() == "0"){
                holder.vitimImg.setImageResource(R.drawable.badgelogo);
            }else{
                imageLoader.DisplayImage(crObj.getImageURL(), holder.vitimImg);


            }





    }


    @Override
    public USMurderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.usmurders_row, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;

    }





}
