package furious.viewfragments.uscrimes;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import furious.dataobjs.USCrimeObject;
import furious.objadapters.USCrimesAdapter;
import furious.phillypolicemobile.R;
import furious.utils.HttpClientInfo;

import static furious.utils.Utils.CVTdistrict;
import static furious.utils.Utils.addTH;


public class MainUSCrimesActivity extends AppCompatActivity {

    private RecyclerView listview;
    private RecyclerView.LayoutManager layoutManager;
    int TOTAL_COUNT = 0;
    int Srt = 0;
    int End = 3;
    ArrayList<USCrimeObject> listofCrimes;

    String district;
    RelativeLayout footer;
    TextView footerTxt;
    ProgressBar progress;
    TextView ismore;
    HttpURLConnection httpcon;
    USCrimesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usmurders_main);

        Bundle extras = getIntent().getExtras();
        district = CVTdistrict(extras.getString("DistrictNumber"));


        Toolbar mractionbar = (Toolbar) findViewById(R.id.mr_toolbar1);
        mractionbar.setSubtitle("Unsolved Crimes");
        mractionbar.setTitle(addTH(district)+" District");
        setSupportActionBar(mractionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        footer = (RelativeLayout) findViewById(R.id.usmurder_footer);
        footerTxt = (TextView) findViewById(R.id.usmurderfootertxt);
        listview = (RecyclerView) findViewById(R.id.usmurder_list);

        layoutManager = new LinearLayoutManager(this);
        listview.setLayoutManager(layoutManager);

        progress = (ProgressBar) findViewById(R.id.usm_progressBar);
        ismore = (TextView) findViewById(R.id.usm_nomore);

        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if(TOTAL_COUNT == newsAdapter.getItemCount()){
//                    Toast.makeText(MainUSCrimesActivity.this, "NO MORE", Toast.LENGTH_LONG).show();
//                }else{
//                    new getMoreUSMurders().execute();
//                }


            }

        });

        new getUSCrimes().execute();

    }



    class getUSCrimes extends AsyncTask<String, Void, ArrayList<USCrimeObject>> {

        @Override
        protected ArrayList<USCrimeObject> doInBackground(String... arg0) {

            try {
                String data = getListData(HttpClientInfo.URL,Srt,End);

                if(data.equals("No Data Connection") || data.isEmpty() || data.length() == 0){
                    Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_LONG).show();
                }else{

                    JSONObject jObj = new JSONObject(data);
                    JSONArray jArray = jObj.getJSONArray("Videos");

                    TOTAL_COUNT = jObj.getInt("TotalCount");
                    int count = jArray.length();
                    listofCrimes = new ArrayList<USCrimeObject>();

                    for(int i=0;i<count;i++){

                        JSONObject daObj = jArray.getJSONObject(i);
                        USCrimeObject nObj = new USCrimeObject();
                        nObj.setUSCrimeID(daObj.getString("UCVideoID"));
                        nObj.setNewsStoryID(daObj.getString("NewsStoryID"));
                        nObj.setVideoImgURL(daObj.getString("VideoImageURL"));
                        nObj.setVideoTitle(daObj.getString("VideoTitle"));
                        nObj.setYouTubeID(daObj.getString("TubeURL"));
                        nObj.setExcert(daObj.getString("Description"));
                        nObj.setDistrictNum(daObj.getString("DistrictNumber"));
                        nObj.setPubDate(daObj.getString("VideoDate"));
                        nObj.setCrimeType(daObj.getString("CrimeType"));
                        nObj.setDCNumber(daObj.getString("DCNumber"));


                        listofCrimes.add(nObj);
                    }



                }

            } catch (IOException e) {

                Log.e("LOG_TAG", "Connection Error", e);
                e.printStackTrace();

            } catch (JSONException e) {

                Log.e("LOG_TAG", "Connection Error 111", e);
                e.printStackTrace();
            }



            return listofCrimes;
        }

        protected void onPostExecute(final ArrayList<USCrimeObject> news_short_Objs) {


            adapter = new USCrimesAdapter(getApplicationContext(), news_short_Objs);

            if(news_short_Objs.size() == 0){
                ismore.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }else if(TOTAL_COUNT == news_short_Objs.size()){
//                listview.addHeaderView(addTheHeader("Latest Police News"));
//                listview.addFooterView(addTheFooter("No More News"));
                progress.setVisibility(View.GONE);
                listview.setAdapter(adapter);
                listview.setNestedScrollingEnabled(false);
                footer.setVisibility(View.VISIBLE);
                footerTxt.setText("No More Stories");

//                progress.setVisibility(View.GONE);
//                waitText.setVisibility(View.INVISIBLE);
//                isNoMore = true;
            }else{
//                listview.addHeaderView(addTheHeader("Latest Police News"));
                //listview.addFooterView(addTheFooter("More Stories "+"( "+news_short_Objs.size()+" of "+TOTAL_COUNT+" )"));
                listview.setAdapter(adapter);
                listview.setNestedScrollingEnabled(false);
                footer.setVisibility(View.VISIBLE);
                footerTxt.setText("More Stories "+"( "+news_short_Objs.size()+" of "+TOTAL_COUNT+" )");
//                progress.setVisibility(View.GONE);
//                waitText.setVisibility(View.INVISIBLE);
//                isNoMore = false;

                progress.setVisibility(View.GONE);
            }


        }



    }

    private String getListData(String uRL, int srt, int end) throws JSONException, UnsupportedEncodingException {

        String result = null;

        String macAddress = HttpClientInfo.getMacAddress(getApplicationContext());
        String deviceID = HttpClientInfo.getMD5(macAddress);

        try {

            JSONObject postObj = new JSONObject();
            postObj.put("UCVideos", "true");
            postObj.put("District", district);
            postObj.put("Start", srt);
            postObj.put("End", end);
            String data = postObj.toString();

            //Connect
            httpcon = (HttpURLConnection) ((new URL(uRL).openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("Content-Type", "application/json");
            httpcon.setRequestProperty("Accept", "application/json");
            httpcon.setRequestProperty("Accept-Language", "en-US");
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
        }

        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        finally{

            httpcon.disconnect();
        }

        if(result == null){
            result = "000";
        }


        return result;



    }





}
