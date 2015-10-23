package cz.cuni.mff.vojta.mobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import cz.cuni.mff.vojta.mobile.R;


public class MainMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_menu_res, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void clickStatisticButton(View button){
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    public void clickPractiseButton(View button){
        Intent intent = new Intent(this, PractiseActivity.class);
        startActivity(intent);
    }

    public void clickLogInButton(View button){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
