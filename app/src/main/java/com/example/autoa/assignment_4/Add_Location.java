package com.example.autoa.assignment_4;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.concurrent.ExecutionException;

import static android.R.attr.value;

public class Add_Location extends AppCompatActivity {
    JSONObject current_route = null;
    String username = null;

    class DeleteAsync extends AsyncTask<String,String,String> {

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

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        create_location();
        String s = getIntent().getStringExtra("CURRENT_ROUTE");
        this.username = getIntent().getStringExtra("USERNAME");
        if(s != null){
            try {
                this.current_route = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            EditText description_edit = (EditText) findViewById(R.id.create_description_edit);
            EditText establishment_edit = (EditText) findViewById(R.id.create_establishment_edit);
            EditText name_edit = (EditText) findViewById(R.id.create_name_edit);
            EditText latitude_edit = (EditText) findViewById(R.id.create_latitude_edit);
            EditText longitude_edit = (EditText) findViewById(R.id.create_longitude_edit);

            try {
                description_edit.setText(this.current_route.get("notes").toString());
                establishment_edit.setText(this.current_route.get("grade").toString());
                latitude_edit.setText(this.current_route.get("latitude").toString());
                longitude_edit.setText(this.current_route.get("longitude").toString());
                name_edit.setText(this.current_route.get("name").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    public void create_location(){
        setContentView(R.layout.activity_assignment4);
        GPSTracker gpsTracker = new GPSTracker(this);
        Button submit_button;
        submit_button = (Button) findViewById(R.id.create_submit);

        Button delete_button;
        delete_button = (Button) findViewById(R.id.delete);

        EditText latitude_edit = (EditText) findViewById(R.id.create_latitude_edit);
        latitude_edit.setText(String.valueOf(gpsTracker.latitude));

        EditText longitude_edit = (EditText) findViewById(R.id.create_longitude_edit);
        longitude_edit.setText(String.valueOf(gpsTracker.longitude));

        submit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText name_edit = (EditText) findViewById(R.id.create_name_edit);
                if(!name_edit.getText().toString().trim().isEmpty()){
                    senddatatoserver();
                    Intent intent = new Intent(getApplicationContext(), Assignment4.class);
                    intent.putExtra("USERNAME", Add_Location.this.username);
                    startActivity(intent);
                }
                else{
                    TextView name_text = (TextView) findViewById(R.id.create_name);
                    name_text.setTextColor(Color.RED);
                }

            }
        });
        delete_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText name_edit = (EditText) findViewById(R.id.create_name_edit);
                String route_id = null;
                try {
                    route_id = Add_Location.this.current_route.get("id").toString();
                } catch (JSONException e) {
                    route_id = null;
                }
                if(route_id != null){
                    JSONObject post_dict = new JSONObject();

                    try {
                        post_dict.put("username", Add_Location.this.username);
                        post_dict.put("id", Add_Location.this.current_route.get("id").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        new DeleteAsync().execute(String.valueOf(post_dict), "https://cs-495.appspot.com/books/API/" + Add_Location.this.username + "/delete", "DELETE").get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(getApplicationContext(), Assignment4.class);
                    intent.putExtra("USERNAME", Add_Location.this.username);
                    startActivity(intent);
                }
                else{
                    TextView name_text = (TextView) findViewById(R.id.create_name);
                    name_text.setTextColor(Color.RED);
                }

            }
        });
    }

    public void senddatatoserver() {
        Log.d("NME", "senddatatoserver: ");
        //function in the activity that corresponds to the layout button

        EditText name_edit = (EditText) findViewById(R.id.create_name_edit);
        String name = name_edit.getText().toString();

        EditText description_edit = (EditText) findViewById(R.id.create_description_edit);
        String description = description_edit.getText().toString();

        EditText establishment_edit = (EditText) findViewById(R.id.create_establishment_edit);
        String grade = establishment_edit.getText().toString();

        EditText latitude_edit = (EditText) findViewById(R.id.create_latitude_edit);
        String latitude = latitude_edit.getText().toString();

        EditText longitude_edit = (EditText) findViewById(R.id.create_longitude_edit);
        String longitude = longitude_edit.getText().toString();

        JSONObject post_dict = new JSONObject();


        try {
            post_dict.put("name", name);
            post_dict.put("grade", grade);
            post_dict.put("notes", description);
            post_dict.put("latitude", latitude);
            post_dict.put("longitude", longitude);
            //post_dict.put("id", this.current_route.get("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (post_dict.length() > 0) {
            if(this.current_route != null){
                try {
                    new ServerInterface().execute(String.valueOf(post_dict), "https://cs-495.appspot.com/books/API/" + this.username + "/" + this.current_route.get("id").toString(), "PUT");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                new ServerInterface().execute(String.valueOf(post_dict), "https://cs-495.appspot.com/books/API/" + this.username + "/routes", "POST");
            }
            //call to async class
        }
    }
}

