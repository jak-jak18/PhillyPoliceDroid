package furious.objadapters;

import android.app.Activity;
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

import furious.dataobjs.USCrimeObject;
import furious.phillypolicemobile.R;
import furious.utils.ImageLoader;
import furious.viewfragments.bookmark.PoliceNews;

import static furious.utils.Utils.ellipsize;


public class USCrimesAdapter extends RecyclerView.Adapter<USCrimesAdapter.ViewHolder> implements View.OnClickListener {

    Context context;
    ArrayList<USCrimeObject> crimeList;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    private final static String READ_MORE = "[Click to Read More]";

    public USCrimesAdapter(Context context, ArrayList<USCrimeObject> list){
        this.context = context;
        this.crimeList = list;
        this.inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dcNumber;
        public TextView pubDate;
        public TextView address;
        public TextView daDesc;
        public TextView crimeType;
        public ImageView crmImg;
        public RelativeLayout layoutHolder;

        public ViewHolder(View itemView) {
            super(itemView);

            dcNumber = (TextView) itemView.findViewById(R.id.uscrimes_Dcnum);
            pubDate = (TextView) itemView.findViewById(R.id.us_crimedate);
            address = (TextView) itemView.findViewById(R.id.uscrimes_add);
            crimeType = (TextView) itemView.findViewById(R.id.us_cimrestype);
            daDesc = (TextView) itemView.findViewById(R.id.uscrimes_excert);
            crmImg = (ImageView) itemView.findViewById(R.id.uscrimes_imgview);
            layoutHolder = (RelativeLayout) itemView.findViewById(R.id.uscrime_layout);
        }
    }

    @Override
    public USCrimesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.us_crimes_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final USCrimesAdapter.ViewHolder holder, final int position) {

        final USCrimeObject crObj = (USCrimeObject) crimeList.get(position);
        String stitle = ellipsize(crObj.getExcert(),400);
        holder.daDesc.setText(stitle + READ_MORE);
        holder.pubDate.setText(crObj.getPubDate());
        holder.address.setText(crObj.getVideoTitle());
        holder.crimeType.setText(crObj.getCrimeType());
        holder.dcNumber.setText(crObj.getDCNumber());
        imageLoader.DisplayImage(crObj.getVideoImgURL(),holder.crmImg);
        holder.crmImg.buildDrawingCache();

        holder.layoutHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = holder.crmImg;
                v.setDrawingCacheEnabled(true);
                Bitmap capturedBitmap = Bitmap.createBitmap(v.getDrawingCache());
                v.setDrawingCacheEnabled(false);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                capturedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Intent intent = new Intent(view.getContext(), PoliceNews.class);
                intent.putExtra("URL", crObj.getYouTubeID());
                intent.putExtra("DCNumber", crObj.getDCNumber());
                intent.putExtra("CrimeType", crObj.getCrimeType());
                intent.putExtra("ImageURL", crObj.getVideoImgURL());
                intent.putExtra("PubDate", crObj.getPubDate());
                intent.putExtra("DistrictNumber", crObj.getDistrictNum());
                intent.putExtra("Description", crObj.getExcert());
                intent.putExtra("ItemPosition", position);
                intent.putExtra("StoryTitle", crObj.getVideoTitle());
                intent.putExtra("ParentActivity", "USCrimesAdapter");
                intent.putExtra("StoryID",crObj.getNewsStoryID());
                intent.putExtra("UCVideoID",crObj.getUSCrimeID());
                intent.putExtra("VictimImage", byteArray);
                intent.putExtra("isVideo", true);
                intent.putExtra("isUCVid", true);
                intent.putExtra("isAlrBk", false);


                ((Activity) view.getContext()).startActivityForResult(intent,3333);


            }
        });

    }

    @Override
    public int getItemCount() {
        return crimeList.size();
    }

    @Override
    public void onClick(View view) {

    }



    public void updateList(ArrayList<USCrimeObject> list){
        if (crimeList != null) {
            //crimeList.clear();
            crimeList.addAll(list);
        }
        else {
            crimeList = list;
        }
        notifyDataSetChanged();
    }



}
