package cz.cuni.mff.vojta.mobile.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by vojta on 5. 11. 2015.
 */
public class RequestTask extends AsyncTask<String, String, String> {
    private IRequestCreator creator;
    public RequestTask(IRequestCreator creator){
        this.creator = creator;
    }
    @Override
    protected String doInBackground(String... uris) {
        String responseString = null;
        for(String uri : uris) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(uri);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                responseString = "";
                String s;
                while((s = in.readLine()) != null){
                    responseString += s;
                }
                in.close();
            } catch (MalformedURLException e) {
                Log.e("PractiseModel", e.getMessage(), e);
            } catch (IOException e) {
                Log.e("PractiseModel", e.getMessage(), e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        creator.requestDone(result);
    }
}
