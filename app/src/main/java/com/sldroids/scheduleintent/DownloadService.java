package com.sldroids.scheduleintent;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sldroids.scheduleintent.sqlite.DataBaseAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;

/**
 * Copyright(c) SLDroid Creations (Pvt) Ltd.  All Rights Reserved.
 * This software is the proprietary information of SLDroid Creations (Pvt) Ltd.
 * .
 * Created by dilanka on 5/9/17.
 */

public class DownloadService extends IntentService {

    private static final String TAG = "DownloadService";
    private static final String JSON_URL = "http://sldroid.com/fmms/index.php";
    private static final String JSON_ARRAY ="result";
    private static final String NUMBER = "number";
    private static final String NAME = "name";
    private static final String CONTACT = "contact";
    private static final String ITEM = "item";
    private static final String DESCRIPTION = "descript";
    private static final String QUANTITY = "qty";
    private static final String RATE = "rate";
    private static final String SRC_LAT = "src_lat";
    private static final String SRC_LON = "src_long";
    private static final String DES_LAT = "des_lat";
    private static final String DES_LON = "des_long";
    private static final int STATUS_ACTIVE = 0;
    private static final int STATUS_DEACTIVE = 1;
    private DataBaseAdapter dbAdapter;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        dbAdapter = new DataBaseAdapter(this);
        dbAdapter.createDatabase();
        dbAdapter.open();

        BufferedReader bufferedReader;
        try {
            URL url = new URL(JSON_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            HashMap<String,String> postDataParams = new HashMap<>();
            postDataParams.put(ITEM,"Diesel");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK){
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String json;
                while((json = bufferedReader.readLine())!= null){
                    sb.append(json+"\n");
                }

                JSONArray users = new JSONObject(sb.toString().trim()).getJSONArray(JSON_ARRAY);

                for (int i = 0; i < users.length(); i++){

                    JSONObject jsonObject = users.getJSONObject(i);
                    long status = dbAdapter.addOrder(
                            jsonObject.getInt(NUMBER),
                            jsonObject.getString(NAME),
                            jsonObject.getString(CONTACT),
                            jsonObject.getString(ITEM),
                            jsonObject.getString(DESCRIPTION),
                            jsonObject.getDouble(QUANTITY),
                            jsonObject.getDouble(RATE),
                            jsonObject.getDouble(SRC_LAT),
                            jsonObject.getDouble(SRC_LON),
                            jsonObject.getDouble(DES_LAT),
                            jsonObject.getDouble(DES_LON),
                            STATUS_ACTIVE
                    );

                    if (status != -1)
                        notifyThis(this, "You have new order" , "Order Number: #" + String.valueOf(jsonObject.getInt(NUMBER)));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public void notifyThis(Context context, String title, String message) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("{your tiny message}")
                .setContentTitle(title)
                .setContentText(message)
                .setContentInfo("INFO");

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NotificationID.getID(), b.build());
    }

    public static class NotificationID {
        private final static AtomicInteger c = new AtomicInteger(0);
        public static int getID() {
            return c.incrementAndGet();
        }
    }
}
