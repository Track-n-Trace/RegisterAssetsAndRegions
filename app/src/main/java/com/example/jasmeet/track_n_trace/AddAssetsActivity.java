package com.example.jasmeet.track_n_trace;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;


public class AddAssetsActivity extends ActionBarActivity {

    EditText assetName, assetId;
    Spinner assetType;
    Button registerAsset;

    SharedPreferences sp;
    String urlToGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assets);

        sp = PreferenceManager.getDefaultSharedPreferences(AddAssetsActivity.this);
        urlToGet = sp.getString("url", "can't get URL") + "/addAssets";

        assetName = (EditText)findViewById(R.id.assetName);
        assetId = (EditText)findViewById(R.id.assetId);
        assetType = (Spinner)findViewById(R.id.assetType);

        registerAsset = (Button)findViewById(R.id.regAssetButton);

        registerAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!assetName.getText().toString().equals("") && !assetId.getText().toString().equals("")) {

                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    new PostNGet() {
                        @Override
                        protected void onPreExecute() {

                            try {
                                jsonToSend.put("assetName", assetName.getText().toString());
                                jsonToSend.put("assetId", assetId.getText().toString());
                                jsonToSend.put("assetType", assetType.getSelectedItem().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            urlToPost = urlToGet;

                            mDialog = new ProgressDialog(AddAssetsActivity.this);
                            mDialog.setMessage("Registering Asset...");
                            mDialog.show();
                        }

                        @Override
                        protected void onPostExecute(String str) {
                            System.out.println("ResponseStr: " + responseStr);
                            System.out.println(jsonToSend.toString());

                            if (responseStr.equals("true")) {
                                System.out.println("asset register success");
                                Toast.makeText(AddAssetsActivity.this, "Success", Toast.LENGTH_LONG).show();

                            } else {
                                System.out.println("asset FAILED");
                                Toast.makeText(AddAssetsActivity.this, "Failed", Toast.LENGTH_LONG).show();

                            }
                            mDialog.dismiss();

                            Intent launchactivity = new Intent(AddAssetsActivity.this, AddActivity.class);
                            startActivity(launchactivity);

                        }
                    }.execute();

                }

                else{
                    Toast.makeText(AddAssetsActivity.this, "Enter Name and ID first", Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_assets, menu);
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

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AddAssetsActivity.this);
            sp.edit().clear().commit();

            Intent launchactivity = new Intent(AddAssetsActivity.this, MainActivity.class);
            startActivity(launchactivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
