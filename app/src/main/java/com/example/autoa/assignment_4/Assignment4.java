package com.example.autoa.assignment_4;

import android.content.Intent;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("NME", "view_locations: ");
        getSupportActionBar().hide();
        view_locations();

        //asyncTask.execute(String.valueOf(gpsTracker.latitude), String.valueOf(gpsTracker.longitude)); //  asyncTask.execute("Latitude", "Longitude")

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
                for (int i = 0; i < names.length; i++) {
                    TextView new_text = new TextView(my_context);

                    new_text.setText(names[i]);
                    new_text.setTextSize(30);
                    my_view.addView(new_text);
                    //my_array[i] = names[i];
                    //Log.d("NME", "view_locations: " + my_array[i]);
                }
            }
        });
        try {
            asyncTask.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("NME", "view_locations: Setting new views");
        for (int i = 0; i < my_array.length; i++) {
            Log.d("NME", "view_locations: " + my_array[i]);
            if (my_array[i] != "") {
                TextView new_text = new TextView(this);

                new_text.setText(my_array[i]);
                my_view.addView(new_text);
            }

        }
        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Add_Location.class);
                startActivity(intent);
            }
        });/****/
    }

}

