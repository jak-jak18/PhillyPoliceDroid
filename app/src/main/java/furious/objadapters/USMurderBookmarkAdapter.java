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

import furious.dataobjs.USMurderObject;
import furious.phillypolicemobile.R;
import furious.utils.ImageLoader;

import static furious.utils.Utils.ellipsize;

public class USMurderBookmarkAdapter extends BaseAdapter {

    Context context;
    ArrayList<USMurderObject> usmList;
    LayoutInflater inflater;
    ImageLoader imageLoader;

    private final static String READ_MORE = "[Click to Read More]";

    public class ViewHolder {

        public TextView victimName;
        public TextView descData;
        public ImageView vitimImg;
        public TextView dc_number;
        public TextView mDate;
        public TextView distN;
        public RelativeLayout layout_holder;

    }

    public USMurderBookmarkAdapter(Context context, ArrayList<USMurderObject> list){
        this.context = context;
        this.usmList = list;
        this.inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return usmList.size();
    }

    @Override
    public Object getItem(int i) {
        return usmList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View convertView = view;
        ViewHolder viewHolder;

        if(view == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.constraint_usmurder_bmrk_row, viewGroup, false);

            viewHolder.descData = (TextView) convertView.findViewById(R.id.Description_bmrk_row);
            viewHolder.victimName = (TextView) convertView.findViewById(R.id.VictimName_bmrkrow);
            viewHolder.vitimImg = (ImageView) convertView.findViewById(R.id.Imageview_usm_bmrk_row);
            viewHolder.dc_number = (TextView) convertView.findViewById(R.id.DCnumber_bmrk_row);
            viewHolder.mDate = (TextView) convertView.findViewById(R.id.Date_usm_bmrk_row);
            viewHolder.distN = (TextView) convertView.findViewById(R.id.DistrictNum_bmrk_row);
//            viewHolder.layout_holder = (RelativeLayout) convertView.findViewById(R.id.itembmklayout);

            convertView.setTag(viewHolder);

        }else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        USMurderObject crObj = (USMurderObject) usmList.get(i);
        String stitle = ellipsize(crObj.getDesc(),400);
        viewHolder.descData.setText(stitle + READ_MORE);
        viewHolder.victimName.setText(crObj.getVictimName());
        viewHolder.dc_number.setText(crObj.getDCNumber());
        viewHolder.mDate.setText(crObj.getNewsStoryPubDate());
        if(!crObj.getImageURL().equals("0")){
            imageLoader.DisplayImage(crObj.getImageURL(), viewHolder.vitimImg);
        }else{
            viewHolder.vitimImg.setImageResource(R.drawable.badgelogo);
        }

        return convertView;
    }

    public void updateList(ArrayList<USMurderObject> list) {
        this.usmList = list;
        notifyDataSetChanged();
    }


}
