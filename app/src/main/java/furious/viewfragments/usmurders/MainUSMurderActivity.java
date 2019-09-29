package furious.viewfragments.usmurders;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
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

import furious.dataobjs.USMurderObject;
import furious.objadapters.USMurderAdapter;
import furious.phillypolicemobile.R;
import furious.utils.HttpClientInfo;
import furious.viewfragments.bookmark.BookmarkFragmentActivity;
import furious.viewfragments.preferences.MainPreferenceActivity;

import static furious.utils.Utils.CVTdistrict;
import static furious.utils.Utils.addTH;

public class MainUSMurderActivity extends AppCompatActivity {

    //ListView listview;
    private RecyclerView listview;
    private USMurderAdapter newsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    RelativeLayout footer;
    TextView footerTxt;
    HttpURLConnection httpcon;
    int Srt = 0;
    int End = 3;
    int TOTAL_COUNT = 0;
    ArrayList<USMurderObject> listofMurders;
    //USMurderAdapter newsAdapter;
    String district;
    ProgressBar progress;
    TextView ismore;
    TextView Htitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usmurders_main);

        Bundle extras = getIntent().getExtras();
        district = CVTdistrict(extras.getString("DistrictNumber"));

        Toolbar mractionbar = (Toolbar) findViewById(R.id.mr_toolbar1);
        mractionbar.setSubtitle("Unsolved Murders");
        mractionbar.setTitle(addTH(district)+" District");
        setSupportActionBar(mractionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //listview = (ListView) findViewById(R.id.usmurder_list);
        footer = (RelativeLayout) findViewById(R.id.usmurder_footer);
        footerTxt = (TextView) findViewById(R.id.usmurderfootertxt);
        listview = (RecyclerView) findViewById(R.id.usmurder_list);
        //listview.setHasFixedSize(true);
        // use a linear layout manager

        layoutManager = new LinearLayoutManager(this);
        listview.setLayoutManager(layoutManager);

        progress = (ProgressBar) findViewById(R.id.usm_progressBar);
        ismore = (TextView) findViewById(R.id.usm_nomore);

        

        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TOTAL_COUNT == newsAdapter.getItemCount()){
                    Toast.makeText(MainUSMurderActivity.this, "NO MORE", Toast.LENGTH_LONG).show();
                }else{
                    new getMoreUSMurders().execute();
                }


            }

        });


        new getUSMurders().execute();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_settings:
                Intent intent = new Intent();
                intent.setClass(MainUSMurderActivity.this, MainPreferenceActivity.class);
                startActivity(intent);

                return true;

            case R.id.action_bookmark:
                Intent bookIntent = new Intent();
                bookIntent.setClass(MainUSMurderActivity.this, BookmarkFragmentActivity.class);
                startActivity(bookIntent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
              //  break;
        }

        //return false;
    }




    class getMoreUSMurders extends AsyncTask<String, Void, ArrayList<USMurderObject>> {

        @Override
        protected void onPreExecute() {
            // Runs on the UI thread before doInBackground
            // Good for toggling visibility of a progress indicator
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<USMurderObject> doInBackground(String... arg0) {

            try {

                String data =null;

                if((TOTAL_COUNT - newsAdapter.getItemCount()) <= 5){
                    data = getListData(HttpClientInfo.URL,newsAdapter.getItemCount(),TOTAL_COUNT);
                }else if((TOTAL_COUNT - newsAdapter.getItemCount()) > 5){
                    data = getListData(HttpClientInfo.URL,newsAdapter.getItemCount(),5);
                }

                if(data.equals("No Data Connection") || data.isEmpty() || data.length() == 0){
                    Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_LONG).show();
                }else{

                    JSONObject jObj = new JSONObject(data);
                    JSONArray jArray = jObj.getJSONArray("Unsolved Murders");

                    TOTAL_COUNT = jObj.getInt("TotalCount");
                    int count = jArray.length();
                    listofMurders = new ArrayList<USMurderObject>();

                    for(int i=0;i<count;i++){

                        JSONObject daObj = jArray.getJSONObject(i);
                        USMurderObject nObj = new USMurderObject();
                        nObj.setDCNumber(daObj.getString("DCNumber"));
                        nObj.setDesc(daObj.getString("Description"));
                        nObj.setNewsURL(daObj.getString("NewsURL"));
                        nObj.setVictimName(daObj.getString("VictimName"));
                        int newCT = daObj.getJSONArray("NewsStory").length();
//                        if(newCT >= 1){
//
//                        }else{
//
//                        }
                        int imgCT = daObj.getJSONArray("Images").length();
                        if(imgCT >= 1){
                            nObj.setImageURL(daObj.getJSONArray("Images").getString(0));
                        }else if(newCT >= 1){

                            String imgUrl = daObj.getJSONArray("NewsStory").getJSONObject(0).getString("ImageURL");
                            String title = daObj.getJSONArray("NewsStory").getJSONObject(0).getString("Title");
                            String descTxt = daObj.getJSONArray("NewsStory").getJSONObject(0).getString("Description");
                            String pubDate = daObj.getJSONArray("NewsStory").getJSONObject(0).getString("PubDate");
                            nObj.setNewsStory(true);
                            nObj.setNewsStoryTitle(title);
                            nObj.setNewsStoryDesc(descTxt);
                            nObj.setNewsStoryPubDate(pubDate);



                            if(!imgUrl.isEmpty()){
                                nObj.setImageURL(imgUrl);
                            }else{
                                nObj.setImageURL("0");
                            }
                        }else{
                            nObj.setImageURL("0");
                        }


                        listofMurders.add(nObj);
                    }



                }

            } catch (IOException e) {

                Log.e("LOG_TAG", "Connection Error", e);
                e.printStackTrace();

            } catch (JSONException e) {

                Log.e("LOG_TAG", "Connection Error 111", e);
                e.printStackTrace();
            }



            return listofMurders;
        }

        protected void onPostExecute(final ArrayList<USMurderObject> news_short_Objs) {


            //newsAdapter = new USMurderAdapter(getApplicationContext(), news_short_Objs);
            newsAdapter.updateList(news_short_Objs);

//            if(newsAdapter.getItemCount() == 0){
//                ismore.setVisibility(View.VISIBLE);
//                progress.setVisibility(View.GONE);
//
//            }

                if(TOTAL_COUNT == newsAdapter.getItemCount()){


                listview.setNestedScrollingEnabled(false);
                footerTxt.setText("No More Stories");
                footer.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);

            }else{
;
                listview.setNestedScrollingEnabled(false);
                footerTxt.setText("More Stories "+"( "+newsAdapter.getItemCount()+" of "+TOTAL_COUNT+" )");
                footer.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }


        }



    }



    class getUSMurders extends AsyncTask<String, Void, ArrayList<USMurderObject>> {

        @Override
        protected ArrayList<USMurderObject> doInBackground(String... arg0) {

            try {
                String data = getListData(HttpClientInfo.URL,Srt,End);

                if(data.equals("No Data Connection") || data.isEmpty() || data.length() == 0){
                    Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_LONG).show();
                }else{

                    JSONObject jObj = new JSONObject(data);
                    JSONArray jArray = jObj.getJSONArray("Unsolved Murders");

                    TOTAL_COUNT = jObj.getInt("TotalCount");
                    int count = jArray.length();
                    listofMurders = new ArrayList<USMurderObject>();

                    for(int i=0;i<count;i++){

                        JSONObject daObj = jArray.getJSONObject(i);
                        USMurderObject nObj = new USMurderObject();
                        nObj.setDCNumber(daObj.getString("DCNumber"));
                        nObj.setDesc(daObj.getString("Description"));
                        nObj.setNewsURL(daObj.getString("NewsURL"));
                        nObj.setVictimName(daObj.getString("VictimName"));
                        int newCT = daObj.getJSONArray("NewsStory").length();
//                        if(newCT >= 1){
//
//                        }else{
//
//                        }
                        int imgCT = daObj.getJSONArray("Images").length();
                            if(imgCT >= 1){
                               nObj.setImageURL(daObj.getJSONArray("Images").getString(0));
                            }else if(newCT >= 1){

                                String imgUrl = daObj.getJSONArray("NewsStory").getJSONObject(0).getString("ImageURL");
                                String title = daObj.getJSONArray("NewsStory").getJSONObject(0).getString("Title");
                                String descTxt = daObj.getJSONArray("NewsStory").getJSONObject(0).getString("Description");
                                String pubDate = daObj.getJSONArray("NewsStory").getJSONObject(0).getString("PubDate");
                                nObj.setNewsStory(true);
                                nObj.setNewsStoryTitle(title);
                                nObj.setNewsStoryDesc(descTxt);
                                nObj.setNewsStoryPubDate(pubDate);



                                if(!imgUrl.isEmpty()){
                                   nObj.setImageURL(imgUrl);
                               }else{
                                   nObj.setImageURL("0");
                               }
                            }else{
                                nObj.setImageURL("0");
                            }


                        listofMurders.add(nObj);
                    }
                    
                    

                }
            
            } catch (IOException e) {

                Log.e("LOG_TAG", "Connection Error", e);
                e.printStackTrace();

            } catch (JSONException e) {

                Log.e("LOG_TAG", "Connection Error 111", e);
                e.printStackTrace();
            }



            return listofMurders;
        }

        protected void onPostExecute(final ArrayList<USMurderObject> news_short_Objs) {


            newsAdapter = new USMurderAdapter(getApplicationContext(), news_short_Objs);

            if(news_short_Objs.size() == 0){
                ismore.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }else if(TOTAL_COUNT == news_short_Objs.size()){
//                listview.addHeaderView(addTheHeader("Latest Police News"));
//                listview.addFooterView(addTheFooter("No More News"));
                progress.setVisibility(View.GONE);
                listview.setAdapter(newsAdapter);
                listview.setNestedScrollingEnabled(false);
                footer.setVisibility(View.VISIBLE);
                footerTxt.setText("No More Stories");

//                progress.setVisibility(View.GONE);
//                waitText.setVisibility(View.INVISIBLE);
//                isNoMore = true;
            }else{
//                listview.addHeaderView(addTheHeader("Latest Police News"));
                //listview.addFooterView(addTheFooter("More Stories "+"( "+news_short_Objs.size()+" of "+TOTAL_COUNT+" )"));
                listview.setAdapter(newsAdapter);
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
            postObj.put("USMurders", "true");
            postObj.put("DistrictNumber", district);
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



    @Override
    protected void onStart() {
        super.onStart();
     //   Log.d(LogTagName.LOG_TAG_UI, "Main activity start.");
    }

    @Override
    protected void onStop() {
        super.onStop();
     //   Log.d(LogTagName.LOG_TAG_UI, "Main activity stop.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
     //   Log.d(LogTagName.LOG_TAG_UI, "Main activity destroy.");
    }

    @Override
    protected void onPause() {
        super.onPause();
     //   Log.d(LogTagName.LOG_TAG_UI, "Main activity pause.");
    }

    @Override
    protected void onResume() {
        super.onResume();
     //   Log.d(LogTagName.LOG_TAG_UI, "Main activity resume.");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
     //   Log.d(LogTagName.LOG_TAG_UI, "Main activity restart.");
    }



}
