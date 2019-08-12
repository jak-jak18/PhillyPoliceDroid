package furious.phillypolicemobile;



import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.util.ArrayList;

public class CrimeFragment extends ListFragment {

    private String DISTRICT_NUM;
    ArrayList<CrimeObject> CR_Obj;
    int TOTAL_COUNT;
    CrimesAdapter adapter;

    private String addTH(String dNum){

        String fin = dNum+"th";

        if(dNum == "2" || dNum == "22"){
            fin = dNum+"nd";
        }if(dNum == "3"){
            fin = dNum+"rd";
        }
        if(dNum == "1"){
            fin = dNum+"st";
        }

        return fin;

    }
    
    static CrimeFragment newInstance(String district) {

        CrimeFragment frag = new CrimeFragment();
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
        new getCrimeList().execute(HttpClientInfo.URL);

    }


    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//		 	DISTRICT_DIVISION = this.getArguments().getString("Division");
        View layout = inflater.inflate(R.layout.crimelayout, container, false);
        TextView header = (TextView) layout.findViewById(R.id.CrimeHeader);
        String dis = addTH(DISTRICT_NUM);
        header.setText(dis+" District Crimes");



        return layout;

        // return super.onCreateView(inflater, container, savedInstanceState);
    }




    public class getCrimeList extends AsyncTask<String, Void, ArrayList<CrimeObject>> {

        @Override
        protected ArrayList<CrimeObject> doInBackground(String... params) {
            CR_Obj = new ArrayList<CrimeObject>();

            try{

                String Data = getListData(params[0],0,5);
                JSONObject object = new JSONObject(Data);
                String tcct = object.getString("TotalCount");
                TOTAL_COUNT = Integer.parseInt(tcct);
                JSONArray objectArray = object.getJSONArray("CrimeIncidents");
                int ct = objectArray.length();
                for(int i=0;i<ct;i++){
                    //	Log.i("THIS_MANY_STING", ct+" "+objectArray.getString(ct));
                    CrimeObject info = new CrimeObject();
                    JSONObject Dobject = objectArray.getJSONObject(i);
                    info.setDispatchTime(Dobject.getString("DispatchDate"));
                    info.setPSArea(Dobject.getString("PSAArea"));
                    info.setAddressBlock(Dobject.getString("Address"));
                    info.setCrimeName(Dobject.getString("CrimeType"));
                    
                    CR_Obj.add(info);
                }
             //   Log.i("THE ARRAY COUNT", Integer.toString(UC_Obj.size()));

                //	image = getBitmapFromURL(Dobject.getString("CaptainURL"));
            }
            catch (ClientProtocolException e) {e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}
            catch (JSONException e) {e.printStackTrace();}

            return CR_Obj;
        }

        protected void onPostExecute(ArrayList<CrimeObject> uc_vid_objs) {
            adapter = new CrimesAdapter(getActivity(), uc_vid_objs);
            //setListAdapter(adapter);

            if(uc_vid_objs.size() <= 0){
                //pDialog.dismiss();
                //			Toast.makeText(getActivity(), "No videos at this time", Toast.LENGTH_LONG).show();
//                noVideoTextView = (TextView) getActivity().findViewById(R.id.UCVideos_NoVid);
//                noVideoTextView.setVisibility(View.VISIBLE);
            }else{
                String ct = Integer.toString(TOTAL_COUNT);
                //String dc = Integer.toString(DISPLAY_COUNT);
                if(TOTAL_COUNT == uc_vid_objs.size()){
                    View title = Header("No More Videos "+"( "+uc_vid_objs.size()+" of "+TOTAL_COUNT+" )");
                    getListView().addFooterView(title);
                    getListView().setAdapter(adapter);
                }else{
                    View title = Header("More Videos "+"( "+uc_vid_objs.size()+" of "+ct+" )");
                    getListView().addFooterView(title);
                    getListView().setAdapter(adapter);
                }

            }


        }

        private View Header(String string) {
            View k = getActivity().getLayoutInflater().inflate(R.layout.uc_more_header, null);
            TextView title = (TextView) k.findViewById(R.id.MoreListUCTextView);
            title.setText(string);
            return k;
        }

    }


    private String getListData(String uRL, int srt, int end) throws ClientProtocolException, IOException, JSONException{

        HttpClient android = new DefaultHttpClient();
        HttpPost clientRequest = new HttpPost(uRL);


        JSONObject postObj = new JSONObject();
        postObj.put("Latest", "true");
        postObj.put("DistrictNumber", DISTRICT_NUM);
        postObj.put("CrimeIncidents", "true");
        postObj.put("Start", srt);
        postObj.put("End", end);

        clientRequest.setEntity(new StringEntity(postObj.toString(), "UTF-8"));
        clientRequest.setHeader("Content-Type","application/json");
        clientRequest.setHeader("Accept-Encoding","application/json");
        clientRequest.setHeader("Accept-Language","en-US");

        HttpResponse response = android.execute(clientRequest);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        response.getEntity().writeTo(os);
        String responseString = os.toString();

        return responseString;

    }







}
