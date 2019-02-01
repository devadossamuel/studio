package es.iessaladillo.pedrojoya.pr174.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import es.iessaladillo.pedrojoya.pr174.R;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            loadStartFragment();
        }
    }

    private void loadStartFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent,
            MainFragment.newInstance(), MainFragment.class.getSimpleName()).setTransition(
            FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

}
