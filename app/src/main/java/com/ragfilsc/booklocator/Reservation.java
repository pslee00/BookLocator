package com.ragfilsc.booklocator;

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
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
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


public class Reservation extends ActionBarActivity {

    public boolean start;
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

    Boolean SearchRecords;
    String search_string;

    EditText editText;

    ListView listView;

    String SelectedIndex;
    String SelectedCategory;

    Boolean DeleteFlag;
    Boolean ReserveBookFlag;
    Boolean RefreshFlag;

    String LoginUserID;
    String LoginUserName;

    WebView webView;
    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Reservation")));

        URL = "http://www.ragfilsc.com/wawasan/lbllocadminreservation.php";

        webView = (WebView) findViewById(R.id.WebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.loadUrl(URL);

        webView.setWebChromeClient(new WebChromeClient() {

            // this will be called on page loading progress

            @Override

            public void onProgressChanged(WebView view, int newProgress) {

                super.onProgressChanged(view, newProgress);


            }

        });
        /*
        StrictMode.enableDefaults();

        SelectedIndex="";
        SearchRecords = false;
        ReserveBookFlag=false;
        RefreshFlag=false;
        DeleteFlag=false;
        start = true;
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 100);
        */
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable(){

                @Override
                public void run() {

                    ConnectivityManager cManager = (ConnectivityManager) getSystemService(Reservation.this.CONNECTIVITY_SERVICE);
                    NetworkInfo nInfo = cManager.getActiveNetworkInfo();

                    if (nInfo != null && nInfo.isConnected())
                    {
                        //pBar.setVisibility(View.VISIBLE);
                        start = true;
                        ipAddress =  getString(R.string.dbserver).toString() + getString(R.string.adminviewreservation).toString() ;
                        getJSON(ipAddress);
                        start = false;

                        timer.cancel();
                    }
                    else

                    {
                        //pBar.setVisibility(View.GONE);
                        Toast.makeText(Reservation.this, "Connect to Wi-Fi Internet network or turn on Mobile Data.", Toast.LENGTH_SHORT).show();
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
                        //String currentString = "Fruit, they taste good";
                        //String[] separated = currentString.split(",");
                        //dbRecords.add(separated[0] + "\n" + separated[1]);

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, dbRecords);

        ListView listView = (ListView) findViewById(R.id.lstCategory);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reservation, menu);
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

            case R.id.Refresh:


                Toast.makeText(getApplicationContext(), "Refresh record(s) in progress....!", Toast.LENGTH_SHORT).show();

                URL = "http://www.ragfilsc.com/wawasan/lbllocadminreservation.php";

                webView = (WebView) findViewById(R.id.WebView);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setDisplayZoomControls(false);
                webView.loadUrl(URL);

                webView.setWebChromeClient(new WebChromeClient() {

                    // this will be called on page loading progress

                    @Override

                    public void onProgressChanged(WebView view, int newProgress) {

                        super.onProgressChanged(view, newProgress);


                    }

                });
                /*
                StrictMode.enableDefaults();

                SelectedIndex="";
                SearchRecords = false;
                ReserveBookFlag=false;
                RefreshFlag=false;
                DeleteFlag=false;
                start = true;
                timer = new Timer();
                myTimerTask = new MyTimerTask();
                timer.schedule(myTimerTask, 100);
                */
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
