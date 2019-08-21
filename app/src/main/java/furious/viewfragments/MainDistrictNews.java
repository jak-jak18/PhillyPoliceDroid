package furious.viewfragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import furious.objadapters.DistrictListAdapter;
import furious.phillypolicemobile.R;

public class MainDistrictNews extends Fragment{
	
	public String[] Districts = {"Northeast","Northwest","East","Central","South","Southwest"};
	public String[] South = {"1st","3rd","17th"};
	public String[] Northeast = {"2nd","7th","8th","15th"};
	public String[] Southwest = {"19th","18th","16th","12th"};
	public String[] Northwest = {"5th","39th","35th","14th"};
	public String[] ROCSC = {"6th","9th","22nd"};
	public String[] ROCNE = {"25th","24th","26th"};
	public String[][] districtNums = {Northeast,Northwest,ROCNE,ROCSC,South,Southwest};
	
	ExpandableListView listview;
	DistrictListAdapter adapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
    }
	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	        View layout = inflater.inflate(R.layout.main_news, container, false);
	        listview  = (ExpandableListView) layout.findViewById(R.id.expandableListView1);

	        
	        return layout;
	 }
	
	@Override
 	public void onActivityCreated(Bundle savedState) {
 	    super.onActivityCreated(savedState);
 	    adapter = new DistrictListAdapter(getActivity(), Districts, districtNums);
 	    listview.setAdapter(adapter);
 	    listview.setOnChildClickListener(new OnChildClickListener(){

			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,
					int arg2, int arg3, long arg4) {
				String division = arg0.getExpandableListAdapter().getGroup(arg2).toString();
				
				String district = (String) adapter.getChild(arg2, arg3);
				Intent intent = new Intent(getActivity(), DistrictFragmentActivity.class);
				intent.putExtra("DistrictNumber", district);
				intent.putExtra("Division", getDivision(division));
				startActivity(intent);
				return true;
			}
       	
       });
 	   
	}
	
	@Override
    public void onStart(){
    	super.onStart();
    	
    }
	
	public String getDivision(String division){
    	String rval = division;
    	if(division == "ROC North East"){
    		rval = "East";
    	}else if(division == "ROC South Central"){
    		rval = "Central";
    	}else{
    		rval = division;
    	}
    	return rval;
    }
	
	
	
}