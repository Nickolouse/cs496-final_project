package com.example.autoa.assignment_4;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.value;

public class Add_Location extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        create_location();
}
    public void create_location(){
        setContentView(R.layout.activity_assignment4);
        GPSTracker gpsTracker = new GPSTracker(this);
        Button submit_button;
        submit_button = (Button) findViewById(R.id.create_submit);

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
        String establishment = establishment_edit.getText().toString();

        EditText latitude_edit = (EditText) findViewById(R.id.create_latitude_edit);
        String latitude = latitude_edit.getText().toString();

        EditText longitude_edit = (EditText) findViewById(R.id.create_longitude_edit);
        String longitude = longitude_edit.getText().toString();

        JSONObject post_dict = new JSONObject();


        try {
            post_dict.put("name", name);
            post_dict.put("description", description);
            post_dict.put("establishment", establishment);
            post_dict.put("latitude", latitude);
            post_dict.put("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (post_dict.length() > 0) {
            new ServerInterface().execute(String.valueOf(post_dict));
            //call to async class
        }
    }
}
