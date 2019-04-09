package furious.phillypolicemobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class ShootingFragment extends ListFragment implements AdapterView.OnItemLongClickListener{


    String DISTRICT;
    ListView listview;
    ArrayList<ShootingObject> newsObjs;
    ArrayList<String> items;
    ProgressBar progressM;
//    ProgressDialog pDialog;
    HttpClient client;
    TextView footerTxt;
    TextView headerTxt;
    TextView noNewsTxt;
    ShootingObjectAdapter adapter;
    private int TOTAL_COUNT;
    private int DISPLAY_COUNT;


    static ShootingFragment newInstance(String district){
        ShootingFragment frag = new ShootingFragment();
        Bundle args = new Bundle();
        args.putString("DistrictNumber", district);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        DISTRICT = Uri.encode(this.getArguments().getString("DistrictNumber"));
        new getDistrictNews().execute();

    }

    @Override
    public void onActivityCreated(Bundle savedState){
        super.onActivityCreated(savedState);

        this.getListView().setOnItemLongClickListener(this);
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub

//                if(arg1.findViewById(R.id.MoreListTextView) != null){
//                    if(TOTAL_COUNT != newsObjs.size()){
//                        footerTxt = (TextView) getActivity().findViewById(R.id.MoreListTextView);
//                        progressM = (ProgressBar) getActivity().findViewById(R.id.HeaderLoadMore);
//                        progressM.setVisibility(View.VISIBLE);
//                        footerTxt.setVisibility(View.INVISIBLE);
//
//                        new fetchMoreNews().execute(HttpClientInfo.URL);
//                    }else{
//                        Toast.makeText(getActivity(), "No more news at this time", Toast.LENGTH_LONG).show();
//                    }
//                }else{
//                    ShootingObject lObj = (ShootingObject) arg0.getItemAtPosition(arg2);
//                    boolean isVid = false;
//                    if(!lObj.getTubeURL().equals(0) || !lObj.getTubeURL().equals(null)){
//                        isVid = true;
//                    }
//                    Intent policeNews = new Intent(getActivity(),PoliceNews.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("Description", lObj.getDescription());
//                    bundle.putString("StoryTitle", lObj.getTitle());
//                    bundle.putString("URL", lObj.getTubeURL());
//                    bundle.putString("StoryID", lObj.getNewsStoryID());
//                    bundle.putString("ImageURL", lObj.getImageURL());
//                    bundle.putBoolean("isVideo", isVid);
//                    bundle.putBoolean("isUCVid", false);
//                    bundle.putBoolean("isAlrBk", false);
//
//                    policeNews.putExtras(bundle);
//
//                    startActivity(policeNews);
//
//                }

            }
            
        });

    }



//	@Override
//    public void onStart(){
//    	super.onStart();
//    	
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        View layout = inflater.inflate(R.layout.shooting, container, false);
        noNewsTxt = (TextView) layout.findViewById(R.id.NoNewsTxtViewS);
        headerTxt = (TextView) layout.findViewById(R.id.ShootHeaderTextView);
        headerTxt.setText("Lastest Shootings");
        headerTxt.setVisibility(View.VISIBLE);

        return layout;

    }

    public static String CVDistrict(String dnum){
        String nwdrt = null;

        if(dnum.equals("2") || dnum.equals("22")){
            nwdrt = dnum.concat("nd");
        }else if(dnum.equals("1") || dnum.equals("21")){
            nwdrt = dnum.concat("st");
        }else if(dnum.equals("3") || dnum.equals("23")){
            nwdrt = dnum.concat("rd");
        }else{
            nwdrt = dnum.concat("th");
        }

        return nwdrt;

    }

    public class getDistrictNews extends AsyncTask<String, Void, ArrayList<ShootingObject>> {

        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog  = ProgressDialog.show(getActivity(), "Loading...", "Please wait...", false);
//            pDialog.setCancelable(true);
        }

        @Override
        protected ArrayList<ShootingObject> doInBackground(String... params) {
            newsObjs = new ArrayList<ShootingObject>();

            try{
                String Data = getListData();
                JSONObject object = new JSONObject(Data);
                JSONArray objectArray = object.getJSONArray("Shootings");
                int count = objectArray.length();

                for(int i=0;i<count;i++){
                    ShootingObject item = new ShootingObject();
                    JSONObject newobject = objectArray.getJSONObject(i);
                    item.setShootingID(newobject.getString("ShootingID"));
                    item.setDistrictNumber(newobject.getString("DistrictNumber"));
                    item.setCrimeDate(newobject.getString("CrimeDate"));
                    item.setDCNumber(newobject.getString("DCNumber"));
                    item.setLocationAddress(newobject.getString("LocationAddress"));
                    item.setRace(newobject.getString("Race"));
                    item.setGender(newobject.getString("Gender"));
                    item.setWound(newobject.getString("Wound"));
                    item.setAge(newobject.getString("Age"));

                    if(newobject.getString("isOfficerInvolved").equalsIgnoreCase("false")){
                        item.setOfficerInvolved(false);
                    }else if(newobject.getString("isOfficerInvolved").equalsIgnoreCase("true")){
                        item.setOfficerInvolved(true);
                    }

                    if(newobject.getString("isFatal").equalsIgnoreCase("true")){
                        item.setFatal(true);
                    }else if(newobject.getString("isFatal").equalsIgnoreCase("false")){
                        item.setFatal(false);
                    }
                    newsObjs.add(item);
                }
              //  TOTAL_COUNT = object.getInt("TotalCount");
            }
            catch (ClientProtocolException e) {e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}
            catch (JSONException e) {e.printStackTrace();}
            finally {
                client.getConnectionManager().shutdown();
            }

            return newsObjs;
        }

        protected void onPostExecute(ArrayList<ShootingObject> lockers) {

            adapter = new ShootingObjectAdapter(getActivity(),lockers);
            //Madapter = new MergeAdapter();
            //Madapter.addAdapter(adapter);
            //items = new ArrayList<String>();
            //items.add("More News");
            //Madapter.addAdapter(new OptionAdapter(getActivity(), R.layout.morenewsheading, items));
            if(lockers.size() <= 0){
               // pDialog.dismiss();
                //Toast.makeText(getActivity(), "No news at this time", Toast.LENGTH_LONG).show();
                noNewsTxt.setVisibility(View.VISIBLE);
            }else{
                String ct = Integer.toString(TOTAL_COUNT);
                //String dc = Integer.toString(DISPLAY_COUNT);
                if(TOTAL_COUNT == lockers.size()){
                    View title = Header("No More News");
                    getListView().addFooterView(title);
                    getListView().setAdapter(adapter);
                   // pDialog.dismiss();
                }else{
                    View title = Header("More News "+"( "+lockers.size()+" of "+ct+" )");
                    getListView().addFooterView(title);
                    getListView().setAdapter(adapter);
                    //pDialog.dismiss();
                }

            }


        }

    }


//    public class fetchMoreNews extends AsyncTask<String, Void, ArrayList<ShootingObject>>{
//
//        @Override
//        protected ArrayList<ShootingObject> doInBackground(String... params) {
//            // TODO Auto-generated method stub
//            try {
//                String rdata = null;
//                if((TOTAL_COUNT - newsObjs.size()) <=5){
//                    rdata = getListData(DISTRICT,newsObjs.size(),TOTAL_COUNT);
//                }else if((TOTAL_COUNT - newsObjs.size())>5){
//                    rdata = getListData(DISTRICT,newsObjs.size(),5);
//                }
//                //String rdata = getListData(DISTRICT,newsObjs.size(),TOTAL_COUNT);
//                JSONObject jObj = new JSONObject(rdata);
//                Log.i("Counts", DISTRICT);
//                JSONArray objectArray = jObj.getJSONArray("Articles");
//                int count = objectArray.length();
//
//                for(int i=0;i<count;i++){
//                    ShootingObject item = new ShootingObject();
//                    JSONObject newobject = objectArray.getJSONObject(i);
//                    item.setNewsStoryID(newobject.getString("NewsStoryID"));
//                    item.setDistrictNumber(newobject.getString("DistrictNumber"));
//                    item.setDescription(newobject.getString("Description"));
//                    item.setPubDate(newobject.getString("PubDate"));
//                    item.setImageURL(newobject.getString("ImageURL"));
//                    item.setURL(newobject.getString("URL"));
//                    item.setTitle(newobject.getString("Title"));
//                    item.setTubeURL(newobject.getString("TubeURL"));
//                    //item.setDescription(newobject.getString("Description"));
//                    item.setCategory(newobject.getString("Category"));
//                    item.setStoryAuthor(newobject.getString("StoryAuthor"));
//                    newsObjs.add(item);
//                }
//                TOTAL_COUNT = jObj.getInt("TotalCount");
//
//            } catch (ClientProtocolException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (JSONException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//            return newsObjs;
//        }
//
//        protected void onPostExecute(ArrayList<ShootingObject> newsSty){
//
//            adapter.updateList(newsSty);
//            adapter.notifyDataSetChanged();
//            progressM.setVisibility(View.INVISIBLE);
//            if(newsSty.size() == TOTAL_COUNT){
//                footerTxt.setText("No More News "+"( "+newsSty.size()+" of "+TOTAL_COUNT+" )");
//                footerTxt.setVisibility(View.VISIBLE);
//            }else{
//                footerTxt.setText("More News "+"( "+newsSty.size()+" of "+TOTAL_COUNT+" )");
//                footerTxt.setVisibility(View.VISIBLE);
//            }
//
//        }
//
//    }


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

    private String getListData() throws ClientProtocolException, IOException, JSONException{

        //client = AndroidHttpClient.newInstance("ComboNation");
        client = new DefaultHttpClient();
        HttpPost post = new HttpPost(HttpClientInfo.URL);

        JSONObject postObj = new JSONObject();
        postObj.put("Shooting", "true");
        postObj.put("Latest", "true");


        post.setEntity(new StringEntity(postObj.toString(), "UTF-8"));
        post.setHeader("Content-Type","application/json");
        post.setHeader("Accept-Encoding","application/json");
        post.setHeader("Accept-Language","en-US");

        HttpResponse res = client.execute(post);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        res.getEntity().writeTo(os);

//			HttpClient android = new DefaultHttpClient();
//			HttpGet clientRequest = new HttpGet(uRL);
//			HttpResponse response = android.execute(clientRequest);
//
//			ByteArrayOutputStream os = new ByteArrayOutputStream(); 
//			response.getEntity().writeTo(os); 
//			String responseString = os.toString();
//			
//			return responseString;

        return os.toString();




    }



//	 @Override
//	    public void onAttach(Activity activity) {
//	        super.onAttach(activity);
//	        try {
//	            listener = (OnWeekSelected) activity;
//	            Log.i("onAttach_RAN",activity.toString());
//	            
//	        } catch (ClassCastException e) {
//	            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
//	        }
//	    }


    //	 public interface OnWeekSelected{
//		 public void OnWeekSelected(String week);
// 
//	 }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.newsobject_menu, menu);
        // menu.add(Menu.NONE, R.id.a_item, Menu.NONE, "Menu A");
        //  menu.add(Menu.NONE, R.id.b_item, Menu.NONE, "Menu B");
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //  mSelectedPosition = position;
//  		LockerObject lkObj = (LockerObject) parent.getAdapter().getItem(position);
//  		LOCKER_ID = lkObj.getLockerID();
//  		LOCKER_NUMBER = lkObj.getLockerNumber();
        registerForContextMenu(this.getListView());
        getListView().showContextMenu();
        return true;
    }

    private View Header(String string) {
        View k = getActivity().getLayoutInflater().inflate(R.layout.news_more_header, null);
        TextView title = (TextView) k.findViewById(R.id.MoreListTextView);
        title.setText(string);
        return k;
    }


}