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

    double latitude, longitude, altitude = 0;
    static EditText regName;
    static EditText regId;
    static EditText height;

    Button reset;
    static Button addPoints;
    static Button deletePrevPoint;
    Button goToAddGates;

    static TextView numOfPoints;

    static int count = 0;
    //double points[];
    static ArrayList<Double> lats = new ArrayList<>();
    static ArrayList<Double> longs = new ArrayList<>();
    static Double altLow = 0.0;
    static Double altHigh = 0.0;
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
        height = (EditText)findViewById(R.id.regHeight);

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

                //System.out.println(latitude + ", " + longitude);
                lats.add(latitude);
                longs.add(longitude);
                count++;

                //count = lats.size();

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

                //showGPS.setText("");

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

                if(!regName.getText().toString().equals("") && !height.getText().toString().equals("") && !regId.getText().toString().equals("")) {

                    //showGPS.setText("");
                    //for(int i=0; i<lats.size(); i++)
                     //   showGPS.append(lats.get(i).toString() +" , "+longs.get(i).toString() + "\n");


                    altitude = altitude/lats.size();
                    altLow = altitude - 1;

                    altHigh = altLow + Integer.parseInt(height.getText().toString());

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
                            //Log.wtf("", "Received! " + json);
                            System.out.println("ResponseStr: " + responseStr);
                            System.out.println(jsonToSend.toString());

                            if (!responseStr.equals("")) {

                                //responseStr = responseStr.split("\\[")[1];
                                //responseStr = responseStr.split("]")[0];

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


                    //new postData2().start();

                    //Intent launchactivity = new Intent(AddRegActivity.this, AddGatewaysActivity.class);
                    //startActivity(launchactivity);


                }

                else{
                    Toast.makeText(getApplicationContext(), "Enter all info first",
                            Toast.LENGTH_LONG).show();
                }
                System.out.println("count: "+count);


            }
        });



    }

    public class getGateways extends Thread {

        public void run(){

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(urlToGet);


            try {

                //String jsonToSend = "";
                //httpget.setEntity(new StringEntity(jsonToSend.toString(), "UTF8"));
                httpget.setHeader("Content-type", "application/json");
                HttpResponse resp = httpclient.execute(httpget);

                String responseStr = EntityUtils.toString(resp.getEntity());

                System.out.println(responseStr);

                responseStr = responseStr.replaceAll("\\[","");
                responseStr = responseStr.replaceAll("]","");
                responseStr = responseStr.replaceAll("\"", "");
                gatewayList = responseStr.split(",");

                //Log.d("Status line", "" + resp.getStatusLine().getStatusCode());

                if (resp != null) {

                    System.out.println("resp recvd...!!!");

                    for(int i=0; i<gatewayList.length; i++){
                        System.out.println("gateways-- "+ gatewayList[i]);
                    }

                }

                else System.out.println("NO RESP");


            }
            catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

            latitude = loc.getLatitude();
            longitude = loc.getLongitude();
            altitude = altitude + loc.getAltitude();

            String Text = "Latitude = " + loc.getLatitude() + " Longitude = " + loc.getLongitude() + " Altitude = " + loc.getAltitude();

            System.out.println(Text);
            System.out.println("alti-- " + loc.hasAltitude());
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
