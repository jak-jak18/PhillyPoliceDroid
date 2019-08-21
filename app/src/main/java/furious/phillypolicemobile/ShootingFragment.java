package furious.phillypolicemobile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ShootingFragment extends ListFragment implements AdapterView.OnItemLongClickListener{


    ArrayList<ShootingObject> newsObjs;

    HttpURLConnection httpcon;
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


    }

    @Override
    public void onActivityCreated(Bundle savedState){
        super.onActivityCreated(savedState);

        new getDistrictNews().execute();
        this.getListView().setOnItemLongClickListener(this);
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub


            }
            
        });

    }


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
            super.onPreExecute();

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

            catch (IOException e) {e.printStackTrace();}
            catch (JSONException e) {e.printStackTrace();}


            return newsObjs;
        }

        protected void onPostExecute(ArrayList<ShootingObject> lockers) {

            adapter = new ShootingObjectAdapter(getActivity(),lockers);

            if(lockers.size() <= 0){
               noNewsTxt.setVisibility(View.VISIBLE);
            }else{
                String ct = Integer.toString(TOTAL_COUNT);

                if(TOTAL_COUNT == lockers.size()){
                    View title = Header("No More News");
                    getListView().addFooterView(title);
                    getListView().setAdapter(adapter);
                }else{
                    View title = Header("More News "+"( "+lockers.size()+" of "+ct+" )");
                    getListView().addFooterView(title);
                    getListView().setAdapter(adapter);
                }

            }


        }

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

    private String getListData() throws IOException, JSONException{

        String result = null;

        try{

            JSONObject postObj = new JSONObject();
            postObj.put("Shooting", "true");
            postObj.put("Latest", "true");
            String data = postObj.toString();
            //Connect
            httpcon = (HttpURLConnection) ((new URL(HttpClientInfo.URL).openConnection()));
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
        }

        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



            return result;


    }


    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.newsobject_menu, menu);

    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

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