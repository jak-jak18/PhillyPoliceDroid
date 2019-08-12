package furious.phillypolicemobile;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CrimesAdapter extends BaseAdapter {

    Context context;
    ArrayList<CrimeObject> crimeList;
    LayoutInflater inflater;

    class viewHolder{
        public TextView CrimeName;
        public TextView CrimeAddress;
        public TextView CrimeDate;
        public TextView CrimePSA;
    }

    public CrimesAdapter(Context context, ArrayList<CrimeObject> list){
        this.context = context;
        this.crimeList = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return crimeList.size();
    }

    @Override
    public Object getItem(int i) {
        return crimeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View convertView = view;
        viewHolder viewHolder;

        if(view == null){
            viewHolder = new viewHolder();
            Log.i("LOOKING AT", Integer.toString(i));
            convertView = inflater.inflate(R.layout.crime_row_lay, viewGroup, false);
            viewHolder.CrimeName = (TextView) convertView.findViewById(R.id.crimeName);
            viewHolder.CrimeAddress = (TextView) convertView.findViewById(R.id.crimeAddress);
            viewHolder.CrimeDate = (TextView) convertView.findViewById(R.id.crimeDate);
            viewHolder.CrimePSA = (TextView) convertView.findViewById(R.id.crimePSA);
            convertView.setTag(viewHolder);


        }else{
            viewHolder = (viewHolder) convertView.getTag();
        }

        CrimeObject crObj = (CrimeObject) crimeList.get(i);
        viewHolder.CrimeName.setText(crObj.getCrimeName());
        viewHolder.CrimeDate.setText(crObj.getDispatchTime());
        viewHolder.CrimeAddress.setText(crObj.getAddressBlock());
        viewHolder.CrimePSA.setText(crObj.getPSArea());


        return convertView;
    }
}
