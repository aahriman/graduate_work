package cz.cuni.mff.vojta.mobile.models;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vojta on 1. 10. 2015.
 */
public class PractiseModel {

    public class Response{
        public class Word{
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
        String url;
        ArrayList<Word> words;
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
    private Response response = new ResponseTest();
    private MediaPlayer audio = null;
    private List<String> words = null;
    public static final PractiseModel SINGLETON = new PractiseModel();


    public MediaPlayer getAudio(Context c){
        if(audio == null){
            downloadAudio(c, response.url);
        }
        return audio;
    }

    public List<String> getAlternatives(int wordId){
        return response.words.get(wordId).alternatives;
    }

    public List<Response.Word> getWords(){
        return response.words;
    }

    private void downloadAudio(Context c, String url){
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        FileInputStream fis = null;
        try {
            Uri uri = Uri.parse(url);
            //fis = new FileInputStream(new File(uri.getPath()));
            //mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.setDataSource(c, uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
            audio = mediaPlayer;
        }catch (IOException e){
            Log.d("Error", e.getMessage());
        }finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignore) {
                }
            }

        }
    }

    private void downloadResponse(){

    }
}
