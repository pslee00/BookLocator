package com.ragfilsc.booklocator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
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


public class ManageCategory extends ActionBarActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Manage Categories")));

        StrictMode.enableDefaults();

        SelectedIndex="";
        SearchRecords = false;
        DeleteFlag=false;
        start = true;
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 100);

        editText = (EditText) findViewById(R.id.txtSearch);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    SearchRecords = true;

                    search_string = editText.getText().toString();

                    start = true;
                    timer = new Timer();
                    myTimerTask = new MyTimerTask();
                    timer.schedule(myTimerTask, 100);

                }

                return false;
            }
        });

        listView = (ListView) findViewById(R.id.lstCategory);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String text = String.valueOf(listView.getItemAtPosition(position));

                String[] strChop = text.split("-");

                SelectedIndex = strChop[0].toString();
                SelectedCategory = strChop[1].toString();

            }
        });


    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable(){

                @Override
                public void run() {

                    ConnectivityManager cManager = (ConnectivityManager) getSystemService(ManageCategory.this.CONNECTIVITY_SERVICE);
                    NetworkInfo nInfo = cManager.getActiveNetworkInfo();

                    if (nInfo != null && nInfo.isConnected())
                    {
                        //pBar.setVisibility(View.VISIBLE);
                        start = true;
                        if (SearchRecords == true)
                        {
                            ipAddress =  getString(R.string.dbserver).toString() + getString(R.string.adminsearchcategory).toString() + "?search_string=" + search_string;
                        }
                        else if (DeleteFlag==true)
                        {
                            ipAddress =  getString(R.string.dbserver).toString() + getString(R.string.admindeletecategory).toString() + "?search_string=" + SelectedIndex;
                        }
                        else
                        {
                            ipAddress =  getString(R.string.dbserver).toString() + getString(R.string.adminviewcategory).toString() ;
                        }

                        getJSON(ipAddress);
                        start = false;

                        timer.cancel();
                    }
                    else

                    {
                        //pBar.setVisibility(View.GONE);
                        Toast.makeText(ManageCategory.this, "Connect to Wi-Fi Internet network or turn on Mobile Data.", Toast.LENGTH_SHORT).show();
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ManageCategory.this, android.R.layout.simple_spinner_item, dbRecords);

        ListView listView = (ListView) findViewById(R.id.lstCategory);
        listView.setAdapter(adapter);

        editText.setText("");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent i;

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.Edit:

                DeleteFlag=false;

                if (SelectedIndex.isEmpty()==true)
                {
                    Toast.makeText(getApplicationContext(), "No record selected for edit!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(ManageCategory.this, EditCategories.class);
                    intent.putExtra("Index", SelectedIndex.toString());
                    startActivity(intent);
                }

                return true;

            case R.id.Delete:

                if (SelectedIndex.isEmpty()==true)
                {
                    DeleteFlag=false;
                    Toast.makeText(getApplicationContext(), "No record selected for delete!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    builder.setMessage("Delete?");
                    builder.setPositiveButton("YES, I AM SURE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            StrictMode.enableDefaults();
                            SearchRecords=false;

                            DeleteFlag=true;
                            start = true;
                            timer = new Timer();
                            myTimerTask = new MyTimerTask();
                            timer.schedule(myTimerTask, 100);

                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if user select "No", just cancel this dialog and continue with app
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                return true;

            case R.id.Save:

                DeleteFlag=false;
                i = new Intent(getApplicationContext(), Categories.class);
                startActivity(i);

                return true;

            case R.id.Refresh:

                EditText ExitText = (EditText) findViewById(R.id.txtSearch);

                ExitText.setText("");

                Toast.makeText(getApplicationContext(), "Refresh record(s) in progress....!", Toast.LENGTH_SHORT).show();

                StrictMode.enableDefaults();

                SearchRecords = false;
                DeleteFlag=false;
                start = true;
                timer = new Timer();
                myTimerTask = new MyTimerTask();
                timer.schedule(myTimerTask, 100);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
