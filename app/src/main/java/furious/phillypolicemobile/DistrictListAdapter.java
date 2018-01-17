package furious.phillypolicemobile;

import java.util.zip.Inflater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class DistrictListAdapter extends BaseExpandableListAdapter{
	
	private Context mcontext;
	private String[] mparent;
	private String[][] mchild;
	private LayoutInflater inflater;
	
	
	public DistrictListAdapter(Context context, String[] parent, String[][] child){
		super();
		mcontext = context;
		mparent = parent;
		mchild = child;
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return mchild[arg0][arg1];
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getChildView(int arg0, int arg1, boolean arg2, View convertView,
			ViewGroup parentView) {
		View v = convertView;
		
		 if (v == null) {
		        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService
		                  (Context.LAYOUT_INFLATER_SERVICE);
		        v = inflater.inflate(R.layout.district_list_row, parentView, false);
		    }
		 
		((TextView) v.findViewById(R.id.DistrictListNum)).setText(mchild[arg0][arg1]+" District");
		
//		TextView row = (TextView)convertView;
//			if(row == null){
//				row = new TextView(mcontext);
//			}
//			//Make the note italic
//			row.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
//			
//			//indent the child element a bit
//			row.setPadding(20, 0, 0, 20);
//			row.setText(mchild[arg0][arg1]);
//			
//		return row;
		
		return v;
	}

	@Override
	public int getChildrenCount(int arg0) {
		// TODO Auto-generated method stub
		return mchild[arg0].length;
	}

	@Override
	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
		return mparent[arg0];
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return mparent.length;
	}

	@Override
	public long getGroupId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressLint("NewApi")
	@Override
	public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {



		View v = arg2;

		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService
					(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.district_list_row_1, arg3, false);
		}

		((TextView) v.findViewById(R.id.listTitle)).setText(mparent[arg0]);

//		TextView row = (TextView)convertView;
//			if(row == null){
//				row = new TextView(mcontext);
//			}
//			//Make the note italic
//			row.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
//
//			//indent the child element a bit
//			row.setPadding(20, 0, 0, 20);
//			row.setText(mchild[arg0][arg1]);
//
//		return row;

		return v;







//		TextView row = (TextView) arg2;
//			if(row == null){
//				row = new TextView(mcontext);
//			}
//			row.setTypeface(Typeface.DEFAULT_BOLD);
//			row.setPadding(60, 10, 20, 20);
//			row.setLeft(20);
//			row.setTextSize(22);
//			row.setText(mparent[arg0]);
//
//		return row;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}
	
}