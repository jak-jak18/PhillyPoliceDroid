package furious.test.fragments.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import furious.phillypolicemobile.R;

public class TestFrag extends Fragment {
    final String LOG_TAG = getClass().getSimpleName();

    public static TestFrag newInstance(){
        return new TestFrag();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test_textview_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        TextView textView = view.findViewById(R.id.test_textview);
//        textView.setText("Test Frag");
    }
}
