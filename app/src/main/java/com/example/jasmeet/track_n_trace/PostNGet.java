package com.example.jasmeet.track_n_trace;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Created by Jasmeet on 7/3/2015.
 */
public class PostNGet extends AsyncTask<Void, Void, String> {
    //public static Context httpContext;
    public ProgressDialog mDialog;
    public JSONObject jsonToSend = new JSONObject();
    public String urlToPost;
    public String responseStr = "";

    @Override
    protected String doInBackground(Void... params) {
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlToPost);
            System.out.println("URL: " + urlToPost);
            httppost.setEntity(new StringEntity(jsonToSend.toString(), "UTF8"));
            httppost.setHeader("Content-type", "application/json");
            HttpResponse resp = httpclient.execute(httppost);
            responseStr = EntityUtils.toString(resp.getEntity());
            //responseStr = "{\"status\":true}";
            //responseStr = "{\"status\":false,\"error\":\"Username-Password mismatch!\"}";
            //responseStr = "{\"status\":false,\"error\":\"User doesn't exist!\"}";


        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
