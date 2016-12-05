package com.example.autoa.assignment_4;

import android.content.Intent;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class Assignment4 extends AppCompatActivity {
    /**
     * This code is from a tutorial found at: http://androstock.com/tutorials/create-a-weather-app-on-android-android-studio.html
     * It is modified some by Nick Edwards for the purposes of a school assignment.
     */
    // Project Created by Ferdousur Rahman Shajib
    // www.androstock.com

    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;
    public String username_ = null;
    public String response = null;
    JSONArray route_array = null;
    JSONObject current_route = null;

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
                URL url = new URL(params[0]);
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
                Assignment4.this.route_array = my_array;
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

    }

    class LoginAsync extends AsyncTask <String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String TAG = "NME";
            String JsonResponse = null;
            String JsonDATA = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[1]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod(params[2]);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
//set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
// json data
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
//input stream
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                JsonResponse = buffer.toString();
//response data
                //Log.i(TAG, JsonResponse);

//send to post execute
                return JsonResponse;

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
            if(s == null){
                Assignment4.this.response = null;
                return;
            }
            if(s.contains("true")) {
                Assignment4.this.response = s;
            }
            else{
                Assignment4.this.response = null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getStringExtra("USERNAME") != null){
            this.username_ = getIntent().getStringExtra("USERNAME");
            view_locations();
        }
        else{

            Log.d("NME", "view_locations: ");
            getSupportActionBar().hide();
            login();
        }

        //asyncTask.execute(String.valueOf(gpsTracker.latitude), String.valueOf(gpsTracker.longitude)); //  asyncTask.execute("Latitude", "Longitude")

    }

    public void login() {
        setContentView(R.layout.login);
        final EditText username = (EditText) findViewById(R.id.login_username_text);
        final EditText password = (EditText) findViewById(R.id.login_password_text);
        Button submit_button;
        submit_button = (Button) findViewById(R.id.login_submit);
        submit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CheckBox delete_checkbox = (CheckBox) findViewById(R.id.login_delete_user);
                CheckBox create_checkbox = (CheckBox) findViewById(R.id.login_create_user);
                JSONObject post_dict = new JSONObject();

                try {
                    post_dict.put("username", username.getText());
                    post_dict.put("password", password.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(create_checkbox.isChecked() && !delete_checkbox.isChecked()){

                    try {
                        new LoginAsync().execute(String.valueOf(post_dict), "https://cs-495.appspot.com/books/API/user", "POST").get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    if(Assignment4.this.response == null){
                        Log.d("NME", "no username");
                    }
                    else {
                        username_ = String.valueOf(username.getText());
                        Log.d("LOOK", Assignment4.this.username_);
                        Assignment4.this.view_locations();
                    }
                }
                else if(delete_checkbox.isChecked() && !create_checkbox.isChecked()){
                    try {
                        new LoginAsync().execute(String.valueOf(post_dict), "https://cs-495.appspot.com/books/API/users", "DELETE").get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    if(Assignment4.this.response == null){
                        Log.d("NME", "no username");
                    }
                    else {
                        Assignment4.this.login();
                    }
                }
                else{
                    try {
                        new LoginAsync().execute(String.valueOf(post_dict), "https://cs-495.appspot.com/books/API/login", "POST").get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    if(Assignment4.this.response == null){
                        Log.d("NME", "no username");
                    }
                    else {
                        username_ = String.valueOf(username.getText());
                        Log.d("LOOK", Assignment4.this.username_);
                        Assignment4.this.view_locations();
                    }
                }
            }
        });
    }

    public void view_locations() {


        String[] my_array = new String[100];
        for (int i = 0; i < my_array.length; i++) {
            my_array[i] = "";
        }
        setContentView(R.layout.location_list);
        final android.content.Context my_context = this;
        Log.d("NME", "view_locations: ");
        Button add_button;
        add_button = (Button) findViewById(R.id.add_location);
        final LinearLayout my_view = (LinearLayout) findViewById(R.id.scrollViewLinear);
        Log.d("NME", "view_locations: ");
        GetLocations asyncTask = new GetLocations(new AsyncResponse() {
            public void processFinish(String[] names) {
                for (int i = 0; i < Assignment4.this.route_array.length(); i++) {
                    Button new_button = new Button(my_context);
                    try {
                        new_button.setText(Assignment4.this.route_array.getJSONObject(i).get("name").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new_button.setId(i);
                    new_button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            int id = view.getId();
                            try {
                                Assignment4.this.current_route = Assignment4.this.route_array.getJSONObject(id);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            Intent intent = new Intent(getApplicationContext(), Add_Location.class);
                            try {
                                Log.d("MMM", Assignment4.this.route_array.getJSONObject(id).toString());
                                intent.putExtra("CURRENT_ROUTE", Assignment4.this.route_array.getJSONObject(id).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            intent.putExtra("USERNAME", Assignment4.this.username_);
                            startActivity(intent);
                        }
                    });/****/
                    my_view.addView(new_button);
                    //my_array[i] = names[i];
                    //Log.d("NME", "view_locations: " + my_array[i]);
                }
            }
        });
        try {
            asyncTask.execute("https://cs-495.appspot.com/books/API/" + this.username_ + "/routes").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String id = String.valueOf(view.getId());
                try {
                    for(int i = 0; i < Assignment4.this.route_array.length(); i++){
                        if (id == Assignment4.this.route_array.getJSONObject(i).get("id").toString()){

                        }
                        Log.d("NME", Assignment4.this.route_array.getJSONObject(i).get("id").toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(), Add_Location.class);
                intent.putExtra("USERNAME", Assignment4.this.username_);
                startActivity(intent);
            }
        });
    }

}

