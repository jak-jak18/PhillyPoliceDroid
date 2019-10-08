package furious.viewfragments.bookmark;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
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

import furious.dataobjs.NewsStoryBookmarkObject;
import furious.objadapters.NewsStoryBookmarkAdapter;
import furious.phillypolicemobile.R;
import furious.utils.HttpClientInfo;

import static android.app.Activity.RESULT_OK;


public class NewsStoryBookmark extends ListFragment {

	ArrayList<NewsStoryBookmarkObject> newsObjs;
	NewsStoryBookmarkAdapter adapter;
	HttpURLConnection httpcon;
	TextView noBookmark;

	int TOTAL_COUNT;
	ProgressBar progress;
	View daLayout;
	View header;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new fetchBokmarks().execute();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		daLayout = inflater.inflate(R.layout.bookmark, container, false);
		noBookmark = (TextView) daLayout.findViewById(R.id.BookmarkNoView);
		progress = (ProgressBar) daLayout.findViewById(R.id.progressBar1Bookmark);

		return daLayout;

	}


	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);


		this.getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  // FIX ME I; WHEN YOU CLICK ON MORE BOOKMARK VIDEOS NOTHING
									long arg3) {

				if (arg1.findViewById(R.id.MoreListTextView) != null) {
					TextView ismore = (TextView) arg1.findViewById(R.id.MoreListTextView);
					if (!ismore.getText().equals("No More Bookmarks")) {
						Toast.makeText(getActivity(), "Need top write more code", Toast.LENGTH_SHORT).show();
					} else {

					}

				} else {

					ImageView image = (ImageView) arg1.findViewById(R.id.BookmarkImageView);
					image.setDrawingCacheEnabled(true);
					Bitmap capturedBitmap = Bitmap.createBitmap(image.getDrawingCache());
					image.setDrawingCacheEnabled(false);
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					capturedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byte[] byteArray = stream.toByteArray();


					NewsStoryBookmarkObject lObj = (NewsStoryBookmarkObject) arg0.getItemAtPosition(arg2);
					boolean isVid = false;
					if (!lObj.getVideoURL().equals(0) || !lObj.getVideoURL().equals(null)) {
						isVid = true;
					}

					Intent policeNews = new Intent(getActivity(), PoliceNews.class);
					Bundle bundle = new Bundle();
					bundle.putString("Description", lObj.getDescription());
					bundle.putString("CrimeType", lObj.getCategory());
					bundle.putString("StoryTitle", lObj.getTitle());
					bundle.putString("URL", lObj.getVideoURL());
					bundle.putString("StoryID", lObj.getID());
					bundle.putString("District", lObj.getDistrict());
					bundle.putString("ImageURL", lObj.getImageURL());
					bundle.putInt("ItemPosition", arg2);
					bundle.putString("ParentActivity", "NewsStoryBookmark");
					bundle.putBoolean("isVideo", isVid);
					bundle.putBoolean("isUCVid", false);

					policeNews.putExtra("VictimImage", byteArray);
					policeNews.putExtras(bundle);

					startActivityForResult(policeNews, 1111);


				}


			}
		});

	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);


		if (requestCode == 1111 && resultCode == RESULT_OK) {

			newsObjs.remove(data.getIntExtra("ItemPosition", 0));
			adapter.notifyDataSetChanged();
			TextView txt = (TextView) getListView().findViewById(R.id.MoreListTextView);
			int NT = --TOTAL_COUNT;
			txt.setText("More News " + "( " + newsObjs.size() + " of " + NT + " )");


			Toast.makeText(getActivity(), "Record Deleted", Toast.LENGTH_LONG).show();

			// TODO: Do something with your extra data
		}


	}


	public class fetchBokmarks extends AsyncTask<String, Void, ArrayList<NewsStoryBookmarkObject>> {


		@Override
		protected ArrayList<NewsStoryBookmarkObject> doInBackground(String... params) {
			newsObjs = new ArrayList<NewsStoryBookmarkObject>();

			try {

				String Data = getBookmarkListData();

				JSONObject object = new JSONObject(Data);
				JSONObject bookMarks = object.getJSONObject("Bookmarks");
				JSONArray news_story_Array = bookMarks.getJSONArray("NewsStory");

				int news_story_count = news_story_Array.length();


				for (int i = 0; i < news_story_count; i++) {

					NewsStoryBookmarkObject item = new NewsStoryBookmarkObject();
					JSONObject news_object = news_story_Array.getJSONObject(i);
					item.setDistrict(news_object.getString("District"));
					item.setTitle(news_object.getString("Title"));
					item.setID(news_object.getString("NewsID"));
					item.setDescription(news_object.getString("Description"));
					item.setAuthor(news_object.getString("Author"));
					item.setStoryDate(news_object.getString("PubDate"));
					item.setImageURL(news_object.getString("ImageURL"));
					item.setDivision(news_object.getString("Division"));
					item.setCategory(news_object.getString("Category"));
					item.setVideoURL(news_object.getString("TubeURL"));
					if (news_object.getString("DCNumber") == "0") {
						item.setDCNumber("false");
					} else {
						item.setDCNumber(news_object.getString("DCNumber"));
					}

					newsObjs.add(item);
				}


				TOTAL_COUNT = object.getInt("TotalCount");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}


			return newsObjs;
		}

		protected void onPostExecute(ArrayList<NewsStoryBookmarkObject> lockers) {

			adapter = new NewsStoryBookmarkAdapter(getActivity(), lockers);

			if (lockers.size() <= 0) {
				noBookmark.setVisibility(View.VISIBLE);
			} else {
				String ct = Integer.toString(TOTAL_COUNT);
				if (lockers.size() == 1) {
					getListView().setAdapter(adapter);

				} else if (TOTAL_COUNT == lockers.size()) {
					header = Header("No More Bookmarks");
					getListView().addFooterView(header);
					getListView().setAdapter(adapter);

				} else {
					header = Header("More News " + "( " + lockers.size() + " of " + ct + " )");
					getListView().addFooterView(header);
					getListView().setAdapter(adapter);

				}

			}

			progress.setVisibility(View.INVISIBLE);


		}

	}


	private View Header(String string) {

		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View kfoot = inflater.inflate(R.layout.news_more_header, null);
		TextView title = (TextView) kfoot.findViewById(R.id.MoreListTextView);
		title.setText(string);
		return kfoot;
	}


	private String getBookmarkListData() throws IOException, JSONException {

		String result = null;
		try {

			String macAddss = HttpClientInfo.getMacAddress(getActivity());
			String deviceID = HttpClientInfo.getMD5(macAddss);


			JSONObject postObj = new JSONObject();
			postObj.put("Bookmark", "true");
			postObj.put("DeviceID", deviceID);
			postObj.put("Bookmark_UCVideos", "false");
			postObj.put("Bookmark_NewsStory", "true");
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