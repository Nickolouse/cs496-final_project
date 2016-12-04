package com.example.autoa.assignment_4;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetLocations extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate = null;

    public GetLocations(AsyncResponse asyncResponse){
        delegate = asyncResponse;
    }

    @Override
    protected String doInBackground(String... params) {

        String TAG = "NME";
        String JsonResponse = null;
        //String JsonDATA = params[0];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("https://cs-495.appspot.com/books/API/locations");
            urlConnection = (HttpURLConnection) url.openConnection();
            Log.d(TAG, "doInBackground: ");
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);

            urlConnection.setDoOutput(true);

            urlConnection.connect();

            BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));

            char[] buffer = new char[1024];

            String jsonString = new String();

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            br.close();

            jsonString = sb.toString();

            Log.d(TAG, "doInBackground: " + jsonString);

            return jsonString;

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        String[] names = new String[100];
        JSONArray my_array = null;
        try {
            my_array = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            for(int i = 0; i < my_array.length(); i++){
                names[i] = my_array.getJSONObject(i).get("name").toString();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        delegate.processFinish(names);
    }

    /**@Override
    protected void onPostExecute(JSONObject json) {
        String[] names = new String[100];
        try {
            if(json != null){
                JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                JSONObject main = json.getJSONObject("main");
                //DateFormat df = DateFormat.getDateTimeInstance();


                String name = json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country");
                 String description = details.getString("description").toUpperCase(Locale.US);
                 String temperature = String.format("%.2f", main.getDouble("temp"))+ "°";
                 String humidity = main.getString("humidity") + "%";
                 String pressure = main.getString("pressure") + " hPa";
                 String updatedOn = df.format(new Date(json.getLong("dt")*1000));
                 String iconText = setWeatherIcon(details.getInt("id"),
                 json.getJSONObject("sys").getLong("sunrise") * 1000,
                 json.getJSONObject("sys").getLong("sunset") * 1000);

                delegate.processFinish(names);

            }
        } catch (JSONException e) {
            //Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
    }**/
}
