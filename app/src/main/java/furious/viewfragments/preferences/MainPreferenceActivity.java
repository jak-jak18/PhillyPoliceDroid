package furious.viewfragments.preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import furious.viewfragments.main.MainStart;


public class MainPreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new MainPreferenceFragment())
                .commit();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(MainPreferenceActivity.this, MainStart.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}