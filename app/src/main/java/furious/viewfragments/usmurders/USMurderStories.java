package furious.viewfragments.usmurders;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import furious.phillypolicemobile.R;
import furious.utils.HttpClientInfo;
import furious.viewfragments.bookmark.BookmarkFragmentActivity;
import furious.viewfragments.preferences.MainPreferenceActivity;

public class USMurderStories extends AppCompatActivity {

   // LinearLayout layout;
    HttpURLConnection httpcon;
    String mID;
    String actparent;
    int itemPositon;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usmurder_story);
        Bundle extras = getIntent().getExtras();


        String vitName = extras.getString("VictimName");
        String desc = extras.getString("Description");
        String nwDesc = extras.getString("NewsStoryDesc");
        String nwTitle = extras.getString("NewsStoryTitle");
        mID = extras.getString("USMurderID");
        itemPositon = extras.getInt("ItemPosition",0);
        boolean isNewsStory = extras.getBoolean("isNewsStory");
        actparent = extras.getString("ParentActivity");

        Toolbar mractionbar = (Toolbar) findViewById(R.id.mr_toolbar14);

        if(actparent.equals("USMurderBookmark")){
            mractionbar.setTitle("Saved Bookmark");
            mractionbar.setSubtitle("Unsolved Murder");
        }else{
            mractionbar.setTitle("Unsolved Murder");
            mractionbar.setSubtitle(vitName);
        }


        setSupportActionBar(mractionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.usmurders_row, null);
        TextView vName = (TextView) view.findViewById(R.id.usmurder_name);
        TextView excert = (TextView) view.findViewById(R.id.usmurder_desc);
        vName.setText(vitName);
        excert.setText(desc);

        if(getIntent().hasExtra("VictimImage")){
            ImageView imgView = (ImageView) view.findViewById(R.id.usmurder_img);
            byte[] byteArray = getIntent().getByteArrayExtra("VictimImage");
            Bitmap capturedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imgView.setImageBitmap(capturedBitmap);
        }



        ViewGroup main = (ViewGroup) findViewById(R.id.usmurder_storyLayout);

        if(isNewsStory){

            LayoutInflater inflater1 = getLayoutInflater();
            View view1 = inflater1.inflate(R.layout.news_obj_row, null);
            TextView vTitle = (TextView) view1.findViewById(R.id.DistrictNewsTitle);
            TextView vexcert = (TextView) view1.findViewById(R.id.DistrictNewsExcert);
            vTitle.setText(nwTitle);
            vexcert.setText(nwDesc);
            //ViewGroup main = (ViewGroup) findViewById(R.id.usmurder_storyLayout);
            main.addView(view1, 0);
        }


        main.addView(view, 0);




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(actparent.equals("USMurderBookmark")){
            getMenuInflater().inflate(R.menu.bookmarkstory, menu);
            return true;
        }else{
            getMenuInflater().inflate(R.menu.policemenu, menu);
            return true;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_settings:
                Intent intent = new Intent();
                intent.setClass(USMurderStories.this, MainPreferenceActivity.class);
                startActivity(intent);

                return true;

            case R.id.action_bookmark_p:
                Intent bookIntent = new Intent();
                bookIntent.setClass(USMurderStories.this, BookmarkFragmentActivity.class);
                startActivity(bookIntent);

                return true;

            case R.id.action_bookmark_s:
                alertTwoButtons("ADD");

                return true;

            case R.id.action_rm_bmk:
                alertTwoButtons("REMOVE");

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void alertTwoButtons(final String action) {
        new AlertDialog.Builder(USMurderStories.this)
                .setTitle("Add Bookmark")
                .setMessage("Are you sure you want to add this bookmark?")
                .setIcon(R.drawable.bookmark_b)
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                //showToast("Thank you! You're awesome too!");
                                if(action.equals("ADD")){
                                    new addBookmark().execute();
                                    dialog.cancel();
                                }else if(action.equals("REMOVE")){
                                    new deleteBookmark().execute();
                                    dialog.cancel();
                                }

                            }
                        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        //	showToast("Mike is not awesome for you. :(");
                        dialog.cancel();
                    }
                }).show();
    }


    public class addBookmark extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // diaLog.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            String finalData = null;

            try{

                String jsonData = saveBookmark();
                JSONObject jsonObj = new JSONObject(jsonData);

                String isError = jsonObj.getString("error");

                if(isError.equals("false")){
                    finalData = jsonObj.getString("msg");
                }else if(isError.equals("true")){
                    finalData = jsonObj.getString("msg");
                }


            }
            catch (IOException e) {e.printStackTrace();}
            catch (JSONException e) {e.printStackTrace();}


            return finalData;
        }

        protected void onPostExecute(String lockers) {

            //diaLog.dismiss();
            Toast.makeText(getApplicationContext(), lockers, Toast.LENGTH_LONG).show();
//            diaLog.setVisibility(View.INVISIBLE);

        }

    }

    public class deleteBookmark extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String data = null;
            try {

                data = deleteListData();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }



            return data;
        }

        protected void onPostExecute(String jsonData) {

            JSONObject jsonObj;
            try {

                jsonObj = new JSONObject(jsonData);
                String isErr = jsonObj.getString("error");

                if(isErr.equals("false")){
                    //Toast.makeText(getApplicationContext(), jsonObj.getString("msg"), Toast.LENGTH_LONG).show();
                    Intent bIntent = new Intent();
                    bIntent.putExtra("ItemPosition",itemPositon);
                    setResult(RESULT_OK,bIntent);
                    finish();

                }else if(isErr.equals("true")){
                    Log.e("NETWORK_ERROR", jsonObj.getString("msg"));
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }




        }

    }


    private String deleteListData() throws IOException, JSONException{

        String result = null;
        //client = AndroidHttpClient.newInstance("ComboNation");
        String macAddss = HttpClientInfo.getMacAddress(getApplication());
        String deviceID = HttpClientInfo.getMD5(macAddss);

        try {
            JSONObject postObj = new JSONObject();
            postObj.put("USMurderID", mID);
            postObj.put("Bookmark", "true");
            postObj.put("DeviceID", deviceID);
            postObj.put("BookmarkRemove", "true");
            postObj.put("News", "false");
            postObj.put("Video", "false");
            postObj.put("USMurder","true");
            String data = postObj.toString();
            Log.e("THIS IS SENDING", postObj.toString());


            //Connect
            httpcon = (HttpURLConnection) ((new URL(HttpClientInfo.URL).openConnection()));
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
        }

        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;


    }

    private String saveBookmark() throws IOException, JSONException {

        String macAddss = HttpClientInfo.getMacAddress(getApplicationContext());
        String deviceID = HttpClientInfo.getMD5(macAddss);


        String result = null;

        try {

            JSONObject postObj = new JSONObject();
            postObj.put("Bookmark", "true");
            postObj.put("Bookmark_Submit", "true");
            postObj.put("DeviceID", deviceID);
            postObj.put("Bookmark_UCVideos", "false");
            postObj.put("Bookmark_NewsStory", "false");
            postObj.put("BookmarkUSMurderNews","true");
            postObj.put("BookmarkID",mID);

//            postObj.put("BookmarkID", storyID);
//            postObj.put("BookmarkNews", BOOKMARK_NEWS);
//            postObj.put("BookmarkVideos", BOOKMARK_VIDEOS);
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

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }




}
