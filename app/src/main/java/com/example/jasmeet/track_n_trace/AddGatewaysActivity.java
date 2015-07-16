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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AddGatewaysActivity extends ActionBarActivity {

    Button done;
    ScrollView scrollView;
    LinearLayout linlay;

    ArrayList<String> sendGateways = new ArrayList<>();

    ArrayList<Double[]> latlongs = new ArrayList<>();

    SharedPreferences sp;
    String urlToGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gateways);

        sp = PreferenceManager.getDefaultSharedPreferences(AddGatewaysActivity.this);
        urlToGet = sp.getString("url", "can't get URL") + "/addRegions";

        done = (Button)findViewById(R.id.buttonDone);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        linlay = (LinearLayout)findViewById(R.id.linlay);
        linlay.setPadding(0, 20, 0, 0);

        System.out.println("GATES ACCESSING");

        for(int i=0; i<AddRegActivity.gatewayList.length; i++){
            CheckBox ch = new CheckBox(this);
            final String tempGateway = AddRegActivity.gatewayList[i];
            ch.setId(i+10);
            ch.setText(tempGateway);
            ch.setTextSize(20);
            ch.setPadding(20, 30, 0, 30);
            //linlay.setPadding(10, 80, 0, 80);
            linlay.addView(ch);

            ch.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (((CheckBox) v).isChecked()) {
                        sendGateways.add(tempGateway);
                    }
                    else{
                        sendGateways.remove(tempGateway);
                    }
                }
            });
        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //new postData().start();

                new PostNGet() {
                    @Override
                    protected void onPreExecute() {

                        for(int i=0; i<AddRegActivity.lats.size(); i++){
                            Double[] temp = {AddRegActivity.lats.get(i), AddRegActivity.longs.get(i)};
                            latlongs.add(temp);
                        }

                        try {
                            ArrayList<ArrayList<Double[]>> pointArrOfArr = new ArrayList<>();
                            pointArrOfArr.add(latlongs);

                            jsonToSend.put("gateways", new JSONArray(sendGateways));
                            jsonToSend.put("points", new JSONArray(pointArrOfArr));
                            jsonToSend.put("altLow", AddRegActivity.altLow);
                            jsonToSend.put("altHigh", AddRegActivity.altHigh);
                            jsonToSend.put("regionName", AddRegActivity.regName.getText().toString());
                            jsonToSend.put("regionId", AddRegActivity.regId.getText().toString());

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                        urlToPost = urlToGet;

                        mDialog = new ProgressDialog(AddGatewaysActivity.this);
                        mDialog.setMessage("Registering Region...");
                        mDialog.show();
                    }

                    @Override
                    protected void onPostExecute(String str) {
                        //Log.wtf("", "Received! " + json);
                        System.out.println("ResponseStr: " + responseStr);
                        System.out.println(jsonToSend.toString());

                        if (responseStr.equals("true")) {
                            System.out.println("region register success");
                            Toast.makeText(AddGatewaysActivity.this, "Success", Toast.LENGTH_LONG).show();


                        } else {
                            System.out.println("region FAILED");
                            Toast.makeText(AddGatewaysActivity.this, "Failed", Toast.LENGTH_LONG).show();

                        }
                        mDialog.dismiss();

                        Intent launchactivity = new Intent(AddGatewaysActivity.this, AddActivity.class);
                        startActivity(launchactivity);

                    }
                }.execute();

                //Intent launchactivity = new Intent(AddGatewaysActivity.this, AddActivity.class);
                //startActivity(launchactivity);

            }
        });



    }

    public class postData extends Thread {

        public void run(){

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlToGet);

            JSONObject jsonToSend = new JSONObject();

            JSONArray newArr = new JSONArray();
            JSONArray newArr2 = new JSONArray();

            for(int i=0; i<AddRegActivity.lats.size(); i++){
                Double[] temp = {AddRegActivity.lats.get(i), AddRegActivity.longs.get(i)};
                latlongs.add(temp);
            }
/*
            for(int i=0; i<MainActivity.lats.size(); i++){
                JSONObject newObj = new JSONObject();
                try {
                    newObj.put("latitude", MainActivity.lats.get(i));
                    newObj.put("longitude", MainActivity.longs.get(i));

                    newArr.put(newObj);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for(int i=0; i<sendGateways.size(); i++){
                JSONObject newObj = new JSONObject();
                try {
                    newObj.put("", sendGateways.get(i));
                    newArr2.put(newObj);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
*/
            try {

                ArrayList<ArrayList<Double[]>> pointArrOfArr = new ArrayList<>();
                pointArrOfArr.add(latlongs);

                jsonToSend.put("gateways", new JSONArray(sendGateways));
                jsonToSend.put("points", new JSONArray(pointArrOfArr));
                jsonToSend.put("altLow", AddRegActivity.altLow);
                jsonToSend.put("altHigh", AddRegActivity.altHigh);
                jsonToSend.put("regionName", AddRegActivity.regName.getText().toString());
                jsonToSend.put("regionId", AddRegActivity.regId.getText().toString());

                httppost.setEntity(new StringEntity(jsonToSend.toString(), "UTF8"));
                httppost.setHeader("Content-type", "application/json");
                HttpResponse resp = httpclient.execute(httppost);

                Log.d("Status line", "" + resp.getStatusLine().getStatusCode());

                if (resp != null) {

                    Log.wtf("dscds", "resp recvd...!!!");
                }

                else Log.wtf("dscds", "NO RESPPP...!!!");

            }
            catch (Exception e) {
                e.printStackTrace();
            }

            //MainActivity.showGPS.append(altLow + " , " + altHigh);

            //MainActivity.regName.setText("");
            //MainActivity.height.setText("");

            //Toast.makeText(AddGateways.this,"Data Posted", Toast.LENGTH_LONG).show();

            //MainActivity.addPoints.setEnabled(true);
            //MainActivity.deletePrevPoint.setEnabled(false);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_gateways, menu);
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

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AddGatewaysActivity.this);
            sp.edit().clear().commit();

            Intent launchactivity = new Intent(AddGatewaysActivity.this, MainActivity.class);
            startActivity(launchactivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
