package com.ragfilsc.booklocator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class EditCategories extends ActionBarActivity {

    TextView txtIndex;
    TextView txtCategory;

    String strCategory;

    public boolean start;
    public boolean startEdit;

    Timer timer;
    MyTimerTask myTimerTask;
    String StatusRegister;

    String id;

    String ipAddress;
    String line=null;
    String result=null;
    InputStream is=null;
    int i;


    Boolean FlagTimeout;
    String ErrMsg;

    int IndexCount;
    List dbRecords = new ArrayList();
    int TotalRecords;

    String SelectedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_categories);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Update Categories")));

        txtIndex = (TextView) findViewById(R.id.txtIndex);
        txtCategory = (TextView) findViewById(R.id.txtCategory);

        Intent myIntent = getIntent();

        String strIndex = myIntent.getStringExtra("Index");
        txtIndex.setText(strIndex);

        SelectedIndex = strIndex.toString();

        StrictMode.enableDefaults();

        startEdit= true;
        start = true;
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 100);


    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable(){

                @Override
                public void run() {

                    ConnectivityManager cManager = (ConnectivityManager) getSystemService(EditCategories.this.CONNECTIVITY_SERVICE);
                    NetworkInfo nInfo = cManager.getActiveNetworkInfo();

                    if (nInfo != null && nInfo.isConnected())
                    {
                        if (startEdit==true)
                        {
                            //pBar.setVisibility(View.VISIBLE);
                            start = true;
                            ipAddress =  getString(R.string.dbserver).toString() + getString(R.string.admineditcategory).toString() + "?search_index=" + SelectedIndex;
                            getJSON(ipAddress);
                            start = false;

                            timer.cancel();
                        }
                        else
                        {
                            //pBar.setVisibility(View.VISIBLE);
                            start = true;
                            ipAddress =  getString(R.string.dbserver).toString() + getString(R.string.adminupdatecategory).toString() + "?search_index=" + SelectedIndex + "&strcategory=" + strCategory;
                            getJSON(ipAddress);
                            start = false;

                            timer.cancel();
                        }

                    }
                    else

                    {
                        //pBar.setVisibility(View.GONE);
                        Toast.makeText(EditCategories.this, "Connect to Wi-Fi Internet network or turn on Mobile Data.", Toast.LENGTH_SHORT).show();
                        start = false;
                        start = false;
                    }
                }});
        }

    }

    private void getJSON(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                try {
                    ChoppingData(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    FlagTimeout=false;
                    ErrMsg="";
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setConnectTimeout(15000);

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpParams params = httpclient.getParams();

                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    IndexCount =0;
                    Integer i=0;
                    dbRecords.clear();

                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                        dbRecords.add(i, json);
                        i=i+1;
                    }

                    TotalRecords=i;
                    return sb.toString().trim();

                } catch (java.net.SocketTimeoutException e) {
                    FlagTimeout=true;
                    return null;
                }
                catch (Exception e) {
                    ErrMsg=e.toString();
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void ChoppingData(String json) throws JSONException {
        Integer ttlRecRceived;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditCategories.this, android.R.layout.simple_spinner_item, dbRecords);


        if (startEdit==true)
        {
            txtCategory.setText(adapter.getItem(1).toString());
            startEdit=false;
        }
        else
        {
            StatusRegister = adapter.getItem(0).toString();

            if (StatusRegister.equals("Update Successfully!")) {

                Toast.makeText(EditCategories.this, StatusRegister, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(EditCategories.this, StatusRegister, Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_categories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.Save:

                strCategory = txtCategory.getText().toString();

                if (txtCategory.length() == 0) {
                    Toast.makeText(EditCategories.this, "Category is required!", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditCategories.this);
                    builder.setCancelable(false);
                    builder.setMessage("Update?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            StrictMode.enableDefaults();

                            strCategory = txtCategory.getText().toString();

                            startEdit=false;
                            start = true;
                            timer = new Timer();
                            myTimerTask = new MyTimerTask();
                            timer.schedule(myTimerTask, 100);

                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if user select "No", just cancel this dialog and continue with app
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

        }

        return super.onOptionsItemSelected(item);
    }
}
