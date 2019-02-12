package es.iessaladillo.pedrojoya.pr147.ui.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import es.iessaladillo.pedrojoya.pr147.R;
import es.iessaladillo.pedrojoya.pr147.ui.main.v2.MainFragmentV2;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            loadInitialFragment();
        }
    }

    private void loadInitialFragment() {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.flContent, MainFragmentV2.newInstance(),
                MainFragmentV2.class.getSimpleName())
            .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnuSettings) {
            showSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettings() {
        Toast.makeText(this, getString(R.string.main_settings), Toast.LENGTH_SHORT).show();
    }

}
