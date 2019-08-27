package furious.viewfragments.district;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import furious.dataobjs.PoliceDistricts;
import furious.phillypolicemobile.R;
import furious.utils.HttpClientInfo;

public class DistrictInfoFragment extends Fragment{
	
	private String DISTRICT_NUM;
	TextView districtNum;
	TextView districtAddress;
	TextView districtPhone;
	TextView districtEmail;
	TextView districtCity;
	TextView captainName;
	HttpURLConnection httpcon;
	RelativeLayout nothingSch;
	LinearLayout table;
	LinearLayout table1;
	Bitmap image;
	ImageView captainImage;
	ArrayList<PoliceDistricts> PSAList;
	ArrayList<PoliceDistricts> CalList;
	PoliceDistricts policeObj;
	
	
	static DistrictInfoFragment newInstance(String district) {
		DistrictInfoFragment frag = new DistrictInfoFragment();
        Bundle args = new Bundle();
        args.putString("DistrictNumber", district);
        frag.setArguments(args);

        return frag;
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		DISTRICT_NUM = Uri.encode(this.getArguments().getString("DistrictNumber"));
        
    }

	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);
		new getDistrictInfo().execute(HttpClientInfo.URL);

	}
	
	@Override
    public void onStart(){
    	super.onStart();

    }
	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	        View layout = inflater.inflate(R.layout.districtinfo, container, false);
	        districtNum = (TextView) layout.findViewById(R.id.DistrictInfoTextView);
	        districtAddress = (TextView) layout.findViewById(R.id.DistrictInfoAddress);
		 	districtCity = (TextView) layout.findViewById(R.id.DistrictCity);
	        districtEmail = (TextView) layout.findViewById(R.id.DistrictInfoEmail);
	        districtPhone = (TextView) layout.findViewById(R.id.DistictInfoPhone);
	        captainName = (TextView) layout.findViewById(R.id.DistrictInfoCaptain);
	        captainImage = (ImageView) layout.findViewById(R.id.DistrictInfoImageView);
	        table = (LinearLayout) layout.findViewById(R.id.DistrictInfoRowLayout);
	        table1 = (LinearLayout) layout.findViewById(R.id.DistrictInfoCalendar);
		 	nothingSch = (RelativeLayout) layout.findViewById(R.id.NothinSch);
	        
	        return layout;

	    }

	 public class getDistrictInfo extends AsyncTask<String, Void, PoliceDistricts>{
			
			@Override
			protected PoliceDistricts doInBackground(String... params) {
					try{
						String Data = getData(params[0]);
						JSONObject object = new JSONObject(Data);
						JSONObject Dobject = object.getJSONObject("DistrictInfo");
						policeObj = new PoliceDistricts();
							if(!Dobject.getString("DistrictNumber").equals("None")){
								policeObj.setDistrictNumber(Dobject.getString("DistrictNumber"));
								policeObj.setDistrictAddress(Dobject.getString("LocationAddress"));
								policeObj.setDistrictEmail(Dobject.getString("EmailAddress"));
								policeObj.setDistrictPhone(Dobject.getString("PhoneNumber"));
								policeObj.setCaptainName(Dobject.getString("CaptainName"));
								policeObj.setCaptainImageURL(Dobject.getString("CaptainImageURL"));
								image = getBitmapFromURL(Dobject.getString("CaptainImageURL"));
								
								JSONArray objectArray2 = object.getJSONArray("PSAInfo");
								int count = objectArray2.length();
								PSAList = new ArrayList<PoliceDistricts>();
									for(int i=0;i<count;i++){
										PoliceDistricts dis2 = new PoliceDistricts();
										JSONObject obj2 = objectArray2.getJSONObject(i);
										dis2.setPSAArea(obj2.getString("PSAAreaNumber"));
										dis2.setLTName(obj2.getString("LieutenantName"));
										dis2.setEmail(obj2.getString("EmailAddress"));
										PSAList.add(dis2);
									}
									
								 JSONArray objectArray3 = object.getJSONArray("CalenderInfo");
									int count1 = objectArray3.length();
									CalList = new ArrayList<PoliceDistricts>();
										for(int i=0;i<count1;i++){
											PoliceDistricts dis3 = new PoliceDistricts();
											JSONObject obj3 = objectArray3.getJSONObject(i);
											dis3.setMeetingDate(obj3.getString("MeetDate"));
											dis3.setMeetName(obj3.getString("Title"));
											dis3.setMeetingPlace(obj3.getString("MeetLocation"));
											CalList.add(dis3);
										}

//
							}else{
								policeObj.setDistrictNumber("None");
							}
					}	

						catch (IOException e) {e.printStackTrace();} 
						catch (JSONException e) {e.printStackTrace();}
				
				return policeObj;
			}
			
				protected void onPostExecute(final PoliceDistricts lockers) {

				if (lockers != null) {

					if (!lockers.getDistrictNumber().equals("None")) {
						for (int i = 0; i < PSAList.size(); i++) {
							PoliceDistricts hon = new PoliceDistricts();
							hon = PSAList.get(i);

							View v = getActivity().getLayoutInflater().inflate(R.layout.district_info_row, null);
							TextView area = (TextView) v.findViewById(R.id.PSAArea);
							TextView LTName = (TextView) v.findViewById(R.id.PSALieutenantTextView);
							TextView email = (TextView) v.findViewById(R.id.PSAEmail);

							area.setText("PSA Area " + hon.getPSAArea());
							LTName.setText(hon.getLTName());
							email.setText(hon.getEmail());
							table.addView(v);
						}

						int count1 = CalList.size();

						if (count1 <= 0) {
							nothingSch = (RelativeLayout) getActivity().findViewById(R.id.NothinSch);
							nothingSch.setVisibility(View.VISIBLE);
						} else {

							for (int i = 0; i < count1; i++) {
								PoliceDistricts hon1 = new PoliceDistricts();
								hon1 = CalList.get(i);
								View v = getActivity().getLayoutInflater().inflate(R.layout.district_cal_row, null);
								TextView meetName = (TextView) v.findViewById(R.id.MeetIngTextView);
								TextView meetDate = (TextView) v.findViewById(R.id.MeetingDate);
								TextView meetplace = (TextView) v.findViewById(R.id.MeetingPlace);

								meetName.setText(hon1.getMeetName());
								meetDate.setText(hon1.getMeetingDate());
								meetplace.setText(hon1.getMeetingPlace());
								table1.addView(v);
							}

						}


						PoliceDistricts obk = lockers;
						String[] part = obk.getDistrictAddress().split("(?=Philadelphia)");
						districtAddress.setText(part[0]);
						districtCity.setText(part[1]);
						districtEmail.setText(obk.getDistrictEmail());
						districtPhone.setText(obk.getDistrictPhone());
						captainName.setText(obk.getCaptainName());
						captainImage.setImageBitmap(image);
						districtNum.setText(DistrictNewsList.CVDistrict(obk.getDistrictNumber()) + " District");


					} else if (lockers.getDistrictNumber().equals("None")) {
						Toast.makeText(getActivity(), "District Info Unavailable at this time", Toast.LENGTH_LONG).show();
					}

				}

		}
		
	}
	 
	 private String getData(String uRL) throws IOException, JSONException{

		 String result = null;


		try{

			JSONObject postObj = new JSONObject();
			postObj.put("DistrictInfo", "true");
			postObj.put("DistrictNumber", DISTRICT_NUM);
			String data = postObj.toString();

			 //Connect
			 httpcon = (HttpURLConnection) ((new URL(uRL).openConnection()));
			 httpcon.setDoOutput(true);
			 httpcon.setRequestProperty("Content-Type", "application/json");
			 httpcon.setRequestProperty("Accept", "application/json");
			 httpcon.setRequestProperty("Accept-Language","en-US");
			 httpcon.setRequestMethod("POST");
			 httpcon.connect();

			 //Write
			 OutputStream os = httpcon.getOutputStream();
			 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			 writer.write(data);
			 writer.close();
			 os.close();

			 //Read
			 BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(),"UTF-8"));

			 String line = null;
			 StringBuilder sb = new StringBuilder();

			 while ((line = br.readLine()) != null) {
				 sb.append(line);
			 }

			 br.close();
			 result = sb.toString();

		 }catch (IOException e) {
			e.printStackTrace();
		}

        return result;
			
		}
	 
	 public static Bitmap getBitmapFromURL(String src){
		    try {
		        java.net.URL url = new java.net.URL(src);
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setDoInput(true);
		        connection.connect();
		        InputStream input = connection.getInputStream();
		        Bitmap myBitmap = BitmapFactory.decodeStream(input);
		        return myBitmap;
		    } catch (IOException e) {
		        e.printStackTrace();
		        return null;
		    }
		}
	 
	 
	
}