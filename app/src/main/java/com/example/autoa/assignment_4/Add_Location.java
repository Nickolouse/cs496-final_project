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
    JSONObject current_route = null;
    String username = null;
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
            EditText name_edit = (EditText) findViewById(R.id.create_name_edit);
            try {
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

