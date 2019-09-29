package furious.viewfragments.district;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import furious.dataobjs.CrimeObject;
import furious.dataobjs.NewStoryObject;
import furious.objadapters.CrimesAdapter;
import furious.phillypolicemobile.R;
import furious.utils.HttpClientInfo;
import furious.viewfragments.main.MainNews;

public class CrimeFragment extends ListFragment {

    private String DISTRICT_NUM;
    ArrayList<CrimeObject> CR_Obj;
    int TOTAL_COUNT;
    CrimesAdapter adapter;
    HttpURLConnection httpcon;
    TextView Ftitle;

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

       // registerForContextMenu(getListView());
        new getCrimeList().execute(HttpClientInfo.URL);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(view.findViewById(R.id.MoreListUCTextView) != null){
                    TextView txtV = (TextView) view.findViewById(R.id.MoreListUCTextView);
                    String no = (String) txtV.getText();

                    if(no.equals("More Crime Incidents")){
                        Toast.makeText(getActivity(), "TIme to write", Toast.LENGTH_SHORT).show();


                    }else{
                        new getMoreCrimesData().execute();
                    }



                }


            }
        });

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

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.setHeaderTitle("Context Menu");
//        menu.add(0, v.getId(), 0, "Upload");
//        menu.add(0, v.getId(), 0, "Search");
//        menu.add(0, v.getId(), 0, "Share");
//        menu.add(0, v.getId(), 0, "Bookmark");
//    }
//
//    public boolean onContextItemSelected(MenuItem item) {
//        Toast.makeText(getActivity(), "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
//        return true;
//    }




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

                if(TOTAL_COUNT == uc_vid_objs.size()){
                    View title = Header("More Crime Incidents");
                    getListView().addFooterView(title);
                    getListView().setAdapter(adapter);
                }else{
                    View title = Header("More Incidents "+"( "+uc_vid_objs.size()+" of "+ct+" )");
                    getListView().addFooterView(title);
                    getListView().setAdapter(adapter);
                }

            }


        }


    }


    class getMoreCrimesData extends AsyncTask<String, Void, ArrayList<CrimeObject>>{

        @Override
        protected ArrayList<CrimeObject> doInBackground(String... arg0) {

            try {

                String data = null;
                if((TOTAL_COUNT - CR_Obj.size()) <=10){
                    data = getListData(HttpClientInfo.URL, CR_Obj.size(),TOTAL_COUNT);
                }else if((TOTAL_COUNT - CR_Obj.size()) > 10){
                    data = getListData(HttpClientInfo.URL, CR_Obj.size(),10);
                }


                if(data.equals("No Data Connection") || data.equals(null)){
                    Toast.makeText(getActivity(), "No Data", Toast.LENGTH_LONG).show();
                }else{

                    JSONObject jObj = new JSONObject(data);
                    JSONArray jArray = jObj.getJSONArray("CrimeIncidents");
                    TOTAL_COUNT = jObj.getInt("TotalCount");
                    int count = jArray.length();

                    for(int i=0;i<count;i++){

                        CrimeObject info = new CrimeObject();
                        JSONObject Dobject = jArray.getJSONObject(i);
                        info.setDispatchTime(Dobject.getString("DispatchDate"));
                        info.setPSArea(Dobject.getString("PSAArea"));
                        info.setAddressBlock(Dobject.getString("Address"));
                        info.setCrimeName(Dobject.getString("CrimeType"));

                        CR_Obj.add(info);
                    }

                }

            } catch (IOException e) {

                Log.e("LOG_TAG", "Connection Error", e);
                e.printStackTrace();

            } catch (JSONException e) {

                e.printStackTrace();
            }




            return CR_Obj;
        }

        protected void onPostExecute(final ArrayList<CrimeObject> news_short_Objs) {

            adapter.updateList(news_short_Objs);

            if(TOTAL_COUNT == news_short_Objs.size()){

                Ftitle.setText("More Crime Incidents");

            }else if(news_short_Objs == null){
                Toast.makeText(getActivity(),"Hello",Toast.LENGTH_LONG).show();
            }

            else{

                Ftitle.setText("More Incidents "+"( "+news_short_Objs.size()+" of "+TOTAL_COUNT+" )");

            }




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


    private View Header(String string) {
        View k = getActivity().getLayoutInflater().inflate(R.layout.uc_more_header, null);
        Ftitle = (TextView) k.findViewById(R.id.MoreListUCTextView);
        Ftitle.setText(string);
        return k;
    }

}
