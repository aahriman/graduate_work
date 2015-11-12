package cz.cuni.mff.vojta.mobile.activity;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.List;

import cz.cuni.mff.vojta.mobile.R;
import cz.cuni.mff.vojta.mobile.external.FlowLayout;
import cz.cuni.mff.vojta.mobile.models.PractiseModel;
import cz.cuni.mff.vojta.mobile.utils.IPractiseModelListener;


public class PractiseActivity extends Activity implements IPractiseModelListener {
    HashMap<Integer, Button> wordButtons = new HashMap<>();

    Button activeAlternative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PractiseModel.SINGLETON.addListener(this);
        PractiseModel.SINGLETON.downloadResponse();
        setContentView(R.layout.loading_data);
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

    public void clickPlayButton(View button){
        MediaPlayer mediaPlayer = PractiseModel.SINGLETON.getAudio(getApplicationContext());
        if(mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void clickWordButton(View buttonView, final int id){
        clearSuggest();
        List<String> alternatives = PractiseModel.SINGLETON.getAlternatives(id);
        Log.d("Info", "alternatives: " + alternatives);
        LinearLayout suggests = ((LinearLayout)findViewById(R.id.suggests));
        final Button originalButton = (Button) buttonView;
        int alternativePosition = 0;
        for (String s : alternatives){
            View view = getLayoutInflater().inflate(R.layout.practise_suggest, null);
            Button button = ((Button) view.findViewById(R.id.button));
            button.setText(s);
            if(s.equals(originalButton.getText())){
                button.setEnabled(false);
            }
            alternativePosition++;
            final int ap = alternativePosition;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickSuggestButton(view, id, ap);
                }
            });
            suggests.addView(view);
        }
        originalButton.setTextColor(Color.YELLOW);
        activeAlternative = originalButton;
    }

    public void clickSuggestButton(View buttonView, int id, final int alternativePosition){
        Button button = (Button) buttonView;
        wordButtons.get(id).setText(button.getText());
        PractiseModel.SINGLETON.wordChange(id, alternativePosition);
        clearSuggest();
    }

    public void clearSuggest(){
        ((LinearLayout)findViewById(R.id.suggests)).removeAllViews();
        if(activeAlternative != null) {
            activeAlternative.setTextColor(Color.WHITE);
        }
        activeAlternative = null;
    }

    @Override
    public void onPrepared(PractiseModel p) {
        setContentView(R.layout.practise);
        FlowLayout items = (FlowLayout)findViewById(R.id.items);
        for(final PractiseModel.Response.Word word : p.getWords()) {
            View view = getLayoutInflater().inflate(R.layout.practise_words, null);
            Button button = ((Button) view.findViewById(R.id.button));
            wordButtons.put(word.getId(), button);
            button.setText(word.getOrigin());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickWordButton(view, word.getId());
                }
            });
            items.addView(view);
        }
        MediaPlayer mediaPlayer = p.getAudio(getApplicationContext());
        //Log.d("mediaPlayer.isPlaying()", ""+mediaPlayer.isPlaying());

        if(mediaPlayer != null) {
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Button button = (Button) findViewById(R.id.play_button);
                    button.setEnabled(true);
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                    Log.d("Error in mediaplayer", "what: " + what + " \t extra:" + extra);
                    return true;
                }
            });
        }
    }
}
