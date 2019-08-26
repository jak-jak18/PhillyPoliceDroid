package furious.dataobjs;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import furious.phillypolicemobile.R;

public class ShootingObjectAdapter  extends BaseAdapter {
    Thread thread;

    Context context;
    ArrayList<ShootingObject> newsList;
    LayoutInflater inflater;


    class NEWS_S_ViewHolder{

        public TextView DCNumber;
        public TextView CrimeDate;
        public TextView Address;
        public TextView DistNumber;
        public TextView Description;
        public TextView isFatal;
        public TextView isOffInvolved;

    }


    public ShootingObjectAdapter(Context context, ArrayList<ShootingObject> list){
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
        ShootingObjectAdapter.NEWS_S_ViewHolder viewHolder;
        if(arg1 == null){
            viewHolder = new ShootingObjectAdapter.NEWS_S_ViewHolder();
            Log.i("LOOKING AT", Integer.toString(arg0));
            convertView = inflater.inflate(R.layout.shooting_layout, arg2, false);
            viewHolder.DCNumber = (TextView) convertView.findViewById(R.id.dcNumTitle);
            viewHolder.CrimeDate = (TextView) convertView.findViewById(R.id.shootDate);
            viewHolder.DistNumber = (TextView) convertView.findViewById(R.id.distNum);
            viewHolder.Address = (TextView) convertView.findViewById(R.id.shootAddress);
            viewHolder.Description = (TextView) convertView.findViewById(R.id.shootDesc);
            viewHolder.isFatal = (TextView) convertView.findViewById(R.id.isFatalText);
            viewHolder.isOffInvolved = (TextView) convertView.findViewById(R.id.isOffInv);

            convertView.setTag(viewHolder);


        }else{
            viewHolder = (ShootingObjectAdapter.NEWS_S_ViewHolder) convertView.getTag();
        }

        ShootingObject newsObj = (ShootingObject) newsList.get(arg0);

        String gender = newsObj.getGender();
        String age = newsObj.getAge();
        String wound = newsObj.getWound();
        String race = newsObj.getRace();

        if(gender == "M"){
            gender = "male";
        }else if(gender == "female"){
         gender = "female";
        }

        String desc = "A "+age+" y/o "+race+" "+gender+" was wounded in the "+wound;

        viewHolder.DCNumber.setText("DC# "+newsObj.getDCNumber());
        viewHolder.CrimeDate.setText(newsObj.getCrimeDate());
        viewHolder.DistNumber.setText(newsObj.getDistrictNumber()+" District");
        viewHolder.Address.setText(newsObj.getLocationAddress());
        viewHolder.Description.setText(desc);

        if(newsObj.isFatal){
            viewHolder.isFatal.setText("Fatal");
        }else{
            viewHolder.isFatal.setText("Non-Fatal");
        }
        if(newsObj.isOfficerInvolved){
            viewHolder.isOffInvolved.setText("Yes");
        }else{
            viewHolder.isOffInvolved.setText("No");
        }
        return convertView;
    }


    public void updateList(ArrayList<ShootingObject> list) {
        this.newsList = list;
        notifyDataSetChanged();
    }




}
