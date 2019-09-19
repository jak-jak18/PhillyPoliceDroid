package furious.objadapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import furious.dataobjs.USMurderObject;
import furious.phillypolicemobile.R;
import furious.utils.ImageLoader;
import furious.viewfragments.usmurders.USMurderStories;

import static furious.utils.Utils.ellipsize;


public class USMurderAdapter extends RecyclerView.Adapter<USMurderAdapter.ViewHolder> {

    Context context;
    ArrayList<USMurderObject> crimeList;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    private final static String READ_MORE = "[Click to Read More]";

    public void updateList(ArrayList<USMurderObject> list){
        if (crimeList != null) {
            //crimeList.clear();
            crimeList.addAll(list);
        }
        else {
            crimeList = list;
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView victimName;
        public TextView descData;
        public ImageView vitimImg;
        public RelativeLayout layout;
        public RelativeLayout layout_holder;
        public View layoutview;

        public ViewHolder(View v) {
            super(v);
            layoutview = v;
            descData = (TextView) v.findViewById(R.id.usmurder_desc);
            victimName = (TextView) v.findViewById(R.id.usmurder_name);
            vitimImg = (ImageView) v.findViewById(R.id.usmurder_img);
            layout = (RelativeLayout) v.findViewById(R.id.NewsObjHeaderLayout119);
            layout_holder = (RelativeLayout) v.findViewById(R.id.item_usmurder_layout);


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
    public void onBindViewHolder(final ViewHolder holder, final int position) {


            final USMurderObject crObj = (USMurderObject) crimeList.get(position);
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
                holder.vitimImg.buildDrawingCache();

            }


            holder.layout_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    View v = holder.vitimImg;
                    v.setDrawingCacheEnabled(true);
                    Bitmap capturedBitmap = Bitmap.createBitmap(v.getDrawingCache());
                    v.setDrawingCacheEnabled(false);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    capturedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    Intent intent = new Intent(view.getContext(), USMurderStories.class);
                    intent.putExtra("VictimName", crObj.getVictimName());
                    intent.putExtra("Description", crObj.getDesc());
                    intent.putExtra("NewsStoryDesc", crObj.getNewsStoryDesc());
                    intent.putExtra("NewsStoryTitle", crObj.getNewsStoryTitle());
                    intent.putExtra("isNewsStory",crObj.isNewsStory());
                    intent.putExtra("VictimImage", byteArray);


                    view.getContext().startActivity(intent);
                }
            });



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
