package furious.viewfragments.bookmark;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import furious.dataobjs.USMurderObject;
import furious.objadapters.USMurderBookmarkAdapter;
import furious.phillypolicemobile.R;
import furious.utils.HttpClientInfo;
import furious.viewfragments.usmurders.USMurderStories;

import static android.app.Activity.RESULT_OK;

public class USMurderBookmark extends ListFragment implements AdapterView.OnItemLongClickListener {

//    private RecyclerView listview;
    private ProgressBar progress;
    private TextView isnomore;
    private RelativeLayout footer;
    private HttpURLConnection httpcon;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<USMurderObject> newsObjs;
    USMurderBookmarkAdapter adapter;
    int TOTAL_COUNT;

    static USMurderBookmark newInstance(String jsonData){

        USMurderBookmark frag = new USMurderBookmark();
        Bundle args = new Bundle();
        args.putString("JSON_DATA", jsonData);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        new fetchBookmarks().execute();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        View layout = inflater.inflate(R.layout.usmurder_bmrk, container, false);
        //listview = (RecyclerView) layout.findViewById(R.id.usmurder_list_bmrk);
        progress = (ProgressBar) layout.findViewById(R.id.progressBar_usmuder_bmrk);
        isnomore = (TextView) layout.findViewById(R.id.BookmarkNoView_usm_bmrk);
        footer = (RelativeLayout) layout.findViewById(R.id.usmurder_footer_brk);

        //layoutManager = new LinearLayoutManager(getContext());
       // listview.setLayoutManager(layoutManager);

        return layout;

    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        registerForContextMenu(getListView());

        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final USMurderObject crObj = (USMurderObject) adapterView.getItemAtPosition(i);
                ImageView v = (ImageView) view.findViewById(R.id.Imageview_usm_bmrk_row);
                v.setDrawingCacheEnabled(true);
                Bitmap capturedBitmap = Bitmap.createBitmap(v.getDrawingCache());
                v.setDrawingCacheEnabled(false);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                capturedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Intent intent = new Intent(getActivity(), USMurderStories.class);
                intent.putExtra("VictimName", crObj.getVictimName());
                intent.putExtra("Description", crObj.getDesc());
                intent.putExtra("NewsStoryDesc", crObj.getNewsStoryDesc());
                intent.putExtra("ItemPosition", i);
                intent.putExtra("NewsStoryTitle", crObj.getNewsStoryTitle());
                intent.putExtra("ParentActivity", "USMurderBookmark");
                intent.putExtra("isNewsStory",crObj.isNewsStory());
                intent.putExtra("USMurderID",crObj.getUSMurderID());
                intent.putExtra("VictimImage", byteArray);

                startActivityForResult(intent,0000);
            }
        });

    }


    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.newsobject_menu, menu);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        registerForContextMenu(this.getListView());
        getListView().showContextMenu();
        return true;
    }



    public class fetchBookmarks extends AsyncTask<String, Void, ArrayList<USMurderObject>> {


        @Override
        protected ArrayList<USMurderObject> doInBackground(String... params) {
            newsObjs = new ArrayList<USMurderObject>();

            try{

                String Data = getBookmarkListData();

                JSONObject object = new JSONObject(Data);
                JSONObject bookMarks = object.getJSONObject("Bookmarks");
                JSONArray news_story_Array = bookMarks.getJSONArray("USMurders");

                int news_story_count = news_story_Array.length();


                for(int i=0;i<news_story_count;i++){

                    USMurderObject item = new USMurderObject();
                    JSONObject news_object = news_story_Array.getJSONObject(i);
                    item.setDesc(news_object.getString("Description"));
                    item.setDCNumber(news_object.getString("DCNumber"));
                    item.setNewsStoryPubDate(news_object.getString("MurderDate"));
                    item.setVictimName(news_object.getString("VictimName"));
                    item.setUSMurderID(news_object.getString("USMurderID"));
                    item.setImageURL(news_object.getString("ImageURL"));

                    newsObjs.add(item);
                }



                TOTAL_COUNT = object.getInt("TotalCount");
            }

            catch (IOException e) {e.printStackTrace();}
            catch (JSONException e) {e.printStackTrace();}


            return newsObjs;
        }

        protected void onPostExecute(ArrayList<USMurderObject> lockers) {

            adapter = new USMurderBookmarkAdapter(getActivity(),lockers);

            if(lockers.size() <= 0){
                isnomore.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }else{
                String ct = Integer.toString(TOTAL_COUNT);

                if(TOTAL_COUNT == lockers.size()){
                    getListView().setAdapter(adapter);
                   // getListView().setNestedScrollingEnabled(false);
                    progress.setVisibility(View.GONE);
//                    View title = Header("No More News "+"( "+lockers.size()+" of "+TOTAL_COUNT+" )");
//                    getListView().addFooterView(title);
//                    getListView().setAdapter(adapter);


                }else{
//                    View title = Header("More News "+"( "+lockers.size()+" of "+ct+" )");
//                    getListView().addFooterView(title);
                    getListView().setAdapter(adapter);
                   // getListView().setNestedScrollingEnabled(false);
                    progress.setVisibility(View.GONE);

                }

            }

           //progress.setVisibility(View.INVISIBLE);



        }

    }

    private String getBookmarkListData() throws IOException, JSONException {

        String result = null;
        try{

            String macAddss = HttpClientInfo.getMacAddress(getActivity());
            String deviceID = HttpClientInfo.getMD5(macAddss);


            JSONObject postObj = new JSONObject();
            postObj.put("Bookmark", "true");
            postObj.put("DeviceID", deviceID);
            postObj.put("Bookmark_UCVideos", "false");
            postObj.put("Bookmark_NewsStory", "false");
            postObj.put("BookmarkUSMurderList","true");
            String data = postObj.toString();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // Log.e("ERROR","child HIT ON ACTIVITY RESULTS "+Integer.toString(data.getIntExtra("ItemPosition",99)));


        if (requestCode == 0000 && resultCode == RESULT_OK){

            newsObjs.remove(data.getIntExtra("ItemPosition",0));
            adapter.notifyDataSetChanged();

           // int cout = newsObjs.size();

            Toast.makeText(getActivity(), "Record Deleted", Toast.LENGTH_LONG).show();

            // TODO: Do something with your extra data
        }


    }


}
