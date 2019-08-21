package furious.phillypolicemobile;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CrimeFragment extends ListFragment {

    private String DISTRICT_NUM;
    ArrayList<CrimeObject> CR_Obj;
    int TOTAL_COUNT;
    CrimesAdapter adapter;
    HttpURLConnection httpcon;

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

        View layout = inflater.inflate(R.layout.crimelayout, container, false);
        TextView header = (TextView) layout.findViewById(R.id.CrimeHeader);
        String dis = addTH(DISTRICT_NUM);
        header.setText(dis+" District Crimes");



        return layout;

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

            }

            catch (IOException e) {e.printStackTrace();}
            catch (JSONException e) {e.printStackTrace();}

            return CR_Obj;
        }

        protected void onPostExecute(ArrayList<CrimeObject> uc_vid_objs) {
            adapter = new CrimesAdapter(getActivity(), uc_vid_objs);

            if(uc_vid_objs.size() <= 0){


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


    private String getListData(String uRL, int srt, int end) throws IOException, JSONException {

        String result = null;

        try {

            JSONObject postObj = new JSONObject();
            postObj.put("Latest", "true");
            postObj.put("DistrictNumber", DISTRICT_NUM);
            postObj.put("CrimeIncidents", "true");
            postObj.put("Start", srt);
            postObj.put("End", end);
            String data = postObj.toString();

            httpcon = (HttpURLConnection) ((new URL(uRL).openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("Content-Type", "application/json");
            httpcon.setRequestProperty("Accept", "application/json");
            httpcon.setRequestMethod("POST");
            httpcon.connect();

            //Write
            OutputStream os = httpcon.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(data);
            writer.close();
            os.close();

            //Read
            BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(), "UTF-8"));

            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            result = sb.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }



}
