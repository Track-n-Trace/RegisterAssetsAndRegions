package com.example.jasmeet.track_n_trace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    Button login;
    static EditText appName;
    EditText username;
    EditText password;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appName = (EditText)findViewById(R.id.appNameText);
        login = (Button)findViewById(R.id.loginButton);
        username = (EditText)findViewById(R.id.usernameText);
        password = (EditText)findViewById(R.id.passwordText);

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);


        Boolean isLoggedIn = sp.getBoolean("loggedIn", false);
        System.out.println("onMainCreate: "+isLoggedIn);

        if (isLoggedIn) {
            Intent launchactivity = new Intent(MainActivity.this, AddActivity.class);
            startActivity(launchactivity);
        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!appName.getText().toString().equals("")) {

                    url = "http://" + appName.getText().toString() + ".mybluemix.net";

                    //try to login
                    new PostNGet() {
                        @Override
                        protected void onPreExecute() {

                            try {
                                jsonToSend.put("username", username.getText().toString());
                                jsonToSend.put("password", password.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            urlToPost = url + "/logincheck";

                            mDialog = new ProgressDialog(MainActivity.this);
                            mDialog.setMessage("Logging In...");
                            mDialog.show();
                        }

                        @Override
                        protected void onPostExecute(String str) {
                            System.out.println("ResponseStr: " + responseStr);

                            SharedPreferences.Editor speditor = sp.edit();
                            speditor.putString("url", url);

                            if(responseStr.contains("404 Not Found")){
                                Toast.makeText(MainActivity.this, "App name incorrect!", Toast.LENGTH_LONG).show();
                            }

                            else{

                                try {
                                    JSONObject respJSON = new JSONObject(responseStr);

                                    if ((Boolean)respJSON.get("status")) {

                                        speditor.putBoolean("loggedIn", true).commit();
                                        Intent launchactivity = new Intent(MainActivity.this, AddActivity.class);
                                        startActivity(launchactivity);
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, (String)respJSON.get("error"), Toast.LENGTH_LONG).show();
                                    }
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            mDialog.dismiss();

                        }
                    }.execute();


                } else {
                    Toast.makeText(MainActivity.this, "Enter App Name first", Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
