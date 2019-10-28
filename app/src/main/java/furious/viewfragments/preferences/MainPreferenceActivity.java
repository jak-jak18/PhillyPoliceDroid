package furious.viewfragments.preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import furious.utils.Utils;


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
        Log.i("BACK PRESSED", "CHECKING IF BUTTON IS SET");
        Utils.checkforUpdate(this.getApplicationContext());
        finish();


    }

}