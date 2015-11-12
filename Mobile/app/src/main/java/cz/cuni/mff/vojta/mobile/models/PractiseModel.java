package cz.cuni.mff.vojta.mobile.models;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.vojta.mobile.utils.IPractiseModelListener;
import cz.cuni.mff.vojta.mobile.utils.IRequestCreator;
import cz.cuni.mff.vojta.mobile.utils.RequestTask;

/**
 * Created by vojta on 1. 10. 2015.
 */
public class PractiseModel implements IRequestCreator{

    boolean responseDownloading = false;

    static public class Response{
        static public class Word{
            String origin;
            int id;
            List<String> alternatives;

            Word(int id, String origin, String ... alternatives){
                this.id = id;
                this.origin = origin;
                this.alternatives = Arrays.asList(alternatives);
            }

            public String getOrigin(){
                return origin;
            }

            public int getId(){
                return id;
            }
        }
        protected String url;
        protected ArrayList<Word> words;
    }

    class ResponseTest extends Response{
        ResponseTest(){
            url = "http://atrey.karlin.mff.cuni.cz/~andokajn/test/pisnicky/do_stanice_ceskolipska.wav";
            words = new ArrayList<>();
            words.add(new Word(words.size(), "Hello", "Hello", "Hi"));
            words.add(new Word(words.size(), "my", "my", "your", "her", "his", "him", "our", "their"));
            words.add(new Word(words.size(), "name", "name"));
            words.add(new Word(words.size(), "is", "is", "are", "am"));
            words.add(new Word(words.size(), "Vojta.", "Vojta."));
        }
    }

    class CorrectedWord{
        int id;
        int correctTo;

        public CorrectedWord(int id, int correctTo){
            this.id = id;
            this.correctTo = correctTo;
        }
    }

    private Response response = new ResponseTest();
    private MediaPlayer audio = null;
    public static final PractiseModel SINGLETON = new PractiseModel();

    List<IPractiseModelListener> listeners = new LinkedList<>();
    List<CorrectedWord> correctedWordList = new ArrayList<>();

    public boolean addListener(IPractiseModelListener listener){
        return listeners.add(listener);
    }

    public boolean removeListener(IPractiseModelListener listener){
        return listeners.remove(listener);
    }


    public void wordChange(int wordId, int alternativePosition){
        correctedWordList.add(new CorrectedWord(wordId, alternativePosition));
    }

    public MediaPlayer getAudio(Context c){
        if(response == null){
            downloadResponse();
        }
        if(audio == null){
            downloadAudio(c, response.url);
        }
        return audio;
    }

    public List<String> getAlternatives(int wordId){
        if(response == null){
            downloadResponse();
            return null;
        }
        return response.words.get(wordId).alternatives;
    }

    public List<Response.Word> getWords(){
        if(response == null) {
            downloadResponse();
            return null;
        }
        return response.words;
    }

    private void downloadAudio(Context c, String url){
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            Uri uri = Uri.parse(url);
            mediaPlayer.setDataSource(c, uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
            audio = mediaPlayer;
        }catch (IOException e){
            Log.e("PractiseModel", e.getMessage() ,e);
        }
    }

    public void downloadResponse(){
        AsyncTask requestTask = new RequestTask(this).execute("http://10.0.2.2:8000/practise");
    }

    @Override
    public void requestDone(String json)  {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if(response == null) {
                response = new Response();
            }
            response.url = jsonObject.getString("url");
            if(response.words == null) {
                response.words = new ArrayList<>();
            }else{
                response.words.clear();
            }
            JSONArray jsonArray = jsonObject.getJSONArray("words");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObjectWord = jsonArray.getJSONObject(i);
                JSONArray jsonArrayAlternatives = jsonObjectWord.getJSONArray("alternatives");
                String[] alternatives = new String[jsonArrayAlternatives.length()];
                for (int j = 0; j < alternatives.length; j++){
                    alternatives[i] = jsonArrayAlternatives.getString(i);
                }
                response.words.add(i, new Response.Word(i, jsonObjectWord.getString("origin"), alternatives));
            }
            Log.d("PractiseModel", jsonArray.toString());
            callListeners();
        }catch(JSONException e){
            Log.e("PractiseModel", "Error parse json.", e);
        }
    }

    private void callListeners(){
        for(IPractiseModelListener p : listeners){
            p.onPrepared(this);
        }
    }
}
