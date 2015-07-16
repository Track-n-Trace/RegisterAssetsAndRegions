package com.example.jasmeet.track_n_trace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class AddActivity extends ActionBarActivity {

    Button addRegs;
    Button addAssets;

    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        sp = PreferenceManager.getDefaultSharedPreferences(AddActivity.this);

        addRegs = (Button)findViewById(R.id.addRegsButton);
        addAssets = (Button)findViewById(R.id.addAssetsButton);

        addRegs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent launchactivity = new Intent(AddActivity.this, AddRegActivity.class);
                startActivity(launchactivity);

            }
        });

        addAssets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent launchactivity = new Intent(AddActivity.this, AddAssetsActivity.class);
                startActivity(launchactivity);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
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

            sp.edit().clear().commit();

            Intent launchactivity = new Intent(AddActivity.this, MainActivity.class);
            startActivity(launchactivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
