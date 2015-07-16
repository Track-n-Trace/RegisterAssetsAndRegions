package com.example.jasmeet.track_n_trace;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.util.ArrayList;


public class AddRegActivity extends ActionBarActivity {

    double latitude, longitude = 0;
    static EditText regName;
    static EditText regId;

    Button reset;
    static Button addPoints;
    static Button deletePrevPoint;
    Button goToAddGates;

    static TextView numOfPoints;

    static int count = 0;
    static ArrayList<Double> lats = new ArrayList<>();
    static ArrayList<Double> longs = new ArrayList<>();

    static String[] gatewayList;

    SharedPreferences sp;
    String urlToGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reg);

        sp = PreferenceManager.getDefaultSharedPreferences(AddRegActivity.this);
        urlToGet = sp.getString("url", "can't get URL") + "/addRegions";

        regName = (EditText)findViewById(R.id.regName);
        regId = (EditText)findViewById(R.id.regId);

        reset = (Button)findViewById(R.id.button1);
        addPoints = (Button)findViewById(R.id.button2);
        deletePrevPoint = (Button)findViewById(R.id.button3);
        goToAddGates = (Button)findViewById(R.id.button4);
        numOfPoints = (TextView)findViewById(R.id.textViewGPS);

        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener mlocListener = new MyLocationListener();
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);


        if(count == 0) {
            deletePrevPoint.setEnabled(false);
            reset.setEnabled(false);
        }

        if(count < 3) goToAddGates.setEnabled(false);


        addPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lats.add(latitude);
                longs.add(longitude);
                count++;

                int latsSize = lats.size();
                numOfPoints.setText("Num of points: " + latsSize);

                if(latsSize == 1) {
                    deletePrevPoint.setEnabled(true);
                    reset.setEnabled(true);
                }

                if(latsSize > 2) goToAddGates.setEnabled(true);

                System.out.println("count: "+count);

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lats = new ArrayList<>();
                longs = new ArrayList<>();
                gatewayList = new String[]{};
                count = 0;

                int latsSize = lats.size();
                numOfPoints.setText("Num of points: " + latsSize);

                addPoints.setEnabled(true);
                deletePrevPoint.setEnabled(false);
                goToAddGates.setEnabled(false);

                System.out.println("count: " + count);
            }
        });

        deletePrevPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numPoints = lats.size();
                if(numPoints>0) {
                    lats.remove(numPoints-1);
                    longs.remove(numPoints-1);
                    count--;
                }

                if(numPoints == 0) deletePrevPoint.setEnabled(false);
                System.out.println("count: " + count);
            }
        });

        goToAddGates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!regName.getText().toString().equals("") && !regId.getText().toString().equals("")) {

                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    new PostNGet() {
                        @Override
                        protected void onPreExecute() {

                            try {
                                jsonToSend.put("gatewayReguest", "");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            urlToPost = urlToGet;

                            mDialog = new ProgressDialog(AddRegActivity.this);
                            mDialog.setMessage("Requesting gateways...");
                            mDialog.show();
                        }

                        @Override
                        protected void onPostExecute(String str) {
                            System.out.println("ResponseStr: " + responseStr);
                            System.out.println(jsonToSend.toString());

                            if (!responseStr.equals("")) {

                                responseStr = responseStr.replaceAll("\\[","");
                                responseStr = responseStr.replaceAll("]","");
                                responseStr = responseStr.replaceAll("\"", "");
                                gatewayList = responseStr.split(",");

                                Intent launchactivity = new Intent(AddRegActivity.this, AddGatewaysActivity.class);
                                startActivity(launchactivity);
                            }

                            else {
                                Toast.makeText(getApplicationContext(), "No connectivity",
                                        Toast.LENGTH_LONG).show();
                            }
                            mDialog.dismiss();

                        }
                    }.execute();

                }

                else{
                    Toast.makeText(getApplicationContext(), "Enter all info first",
                            Toast.LENGTH_LONG).show();
                }
                System.out.println("count: "+count);


            }
        });



    }

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

            latitude = loc.getLatitude();
            longitude = loc.getLongitude();

            String Text = "Latitude = " + loc.getLatitude() + " Longitude = " + loc.getLongitude();

            System.out.println(Text);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_reg, menu);
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

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AddRegActivity.this);
            sp.edit().clear().commit();

            Intent launchactivity = new Intent(AddRegActivity.this, MainActivity.class);
            startActivity(launchactivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
