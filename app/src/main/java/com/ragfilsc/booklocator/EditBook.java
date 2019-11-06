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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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


public class EditBook extends ActionBarActivity {

    TextView txtIndex;
    TextView txtTitle;
    TextView txtPublisher;
    TextView txtISBN;
    TextView txtAuthor;
    TextView txtPubDate;
    TextView txtPageNumbers;
    TextView txtLanguage;
    TextView txtSeries;
    TextView txtVol;
    TextView txtPrice;
    TextView txtCategory;
    TextView txtSummary;

    String strIndex;
    String strTitle;
    String strPublisher;
    String strISBN;
    String strAuthor;
    String strPubDate;
    String strPageNumbers;
    String strLanguage;
    String strSeries;
    String strVol;
    String strPrice;
    String strCategory;
    String strSummary;

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

    Boolean BindCategory;
    Spinner spinCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Update Book")));

        txtIndex = (TextView) findViewById(R.id.txtIndex);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtPublisher = (TextView) findViewById(R.id.txtPublisher);
        txtISBN = (TextView) findViewById(R.id.txtISBN);
        txtAuthor = (TextView) findViewById(R.id.txtAuthor);
        txtPubDate = (TextView) findViewById(R.id.txtPubDate);
        txtPageNumbers = (TextView) findViewById(R.id.txtPageNumbers);
        txtLanguage = (TextView) findViewById(R.id.txtLanguage);
        txtSeries = (TextView) findViewById(R.id.txtSeries);
        txtVol = (TextView) findViewById(R.id.txtVol);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        txtCategory = (TextView) findViewById(R.id.txtCategory);
        txtSummary = (TextView) findViewById(R.id.txtSummary);

        spinCategory = (Spinner) findViewById(R.id.spinCategory);

        Intent myIntent = getIntent();

        String strIndex = myIntent.getStringExtra("Index");
        txtIndex.setText(strIndex);

        SelectedIndex = strIndex.toString();

        StrictMode.enableDefaults();

        BindCategory = false;
        startEdit= true;
        start = true;
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 100);

        // Set the ClickListener for Spinner
        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO Auto-generated method stub
                //Toast.makeText(AddBook.this, dbRecords.get(i).toString() , Toast.LENGTH_SHORT).show();
                txtCategory.setText(dbRecords.get(i).toString());

            }

            // If no option selected
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable(){

                @Override
                public void run() {

                    ConnectivityManager cManager = (ConnectivityManager) getSystemService(EditBook.this.CONNECTIVITY_SERVICE);
                    NetworkInfo nInfo = cManager.getActiveNetworkInfo();

                    if (nInfo != null && nInfo.isConnected())
                    {
                        if (BindCategory==true)
                        {
                            //pBar.setVisibility(View.VISIBLE);
                            start = true;
                            ipAddress =  getString(R.string.dbserver).toString() + getString(R.string.adminbindcategory).toString();
                            getJSON(ipAddress);
                            start = false;
                            //BindCategory=false;
                            timer.cancel();
                        }
                        else {
                            if (startEdit == true) {
                                //pBar.setVisibility(View.VISIBLE);
                                start = true;
                                ipAddress = getString(R.string.dbserver).toString() + getString(R.string.admineditbook).toString() + "?search_index=" + SelectedIndex;
                                getJSON(ipAddress);
                                start = false;

                                timer.cancel();
                            } else {
                                //pBar.setVisibility(View.VISIBLE);
                                start = true;
                                ipAddress = getString(R.string.dbserver).toString() + getString(R.string.adminupdatebook).toString() + "?search_index=" + SelectedIndex + "&strtitle=" + strTitle + "&strPublisher=" + strPublisher + "&strISBN=" + strISBN + "&strAuthor=" + strAuthor + "&strPubDate=" + strPubDate + "&strPageNumbers=" + strPageNumbers + "&strLanguage=" + strLanguage + "&strSeries=" + strSeries + "&strVol=" + strVol + "&strPrice=" + strPrice + "&strCategory=" + strCategory + "&strSummary=" + strSummary;
                                getJSON(ipAddress);
                                start = false;

                                timer.cancel();
                            }
                        }

                    }
                    else

                    {
                        //pBar.setVisibility(View.GONE);
                        Toast.makeText(EditBook.this, "Connect to Wi-Fi Internet network or turn on Mobile Data.", Toast.LENGTH_SHORT).show();
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditBook.this, android.R.layout.simple_spinner_item, dbRecords);

        if (BindCategory==true) {

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dbRecords);

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinCategory.setAdapter(dataAdapter);
            BindCategory=false;




            while (i < dbRecords.size()) {

                if (dataAdapter.getItem(i).toString().equals(txtCategory.getText().toString()))
                {
                    spinCategory.setSelection(i);
                }
                i++;
            }


            //spinCategory.set
        }
        else {
            if (startEdit == true) {
                txtTitle.setText(adapter.getItem(1).toString());
                txtPublisher.setText(adapter.getItem(2).toString());
                txtISBN.setText(adapter.getItem(3).toString());
                txtAuthor.setText(adapter.getItem(4).toString());
                txtPubDate.setText(adapter.getItem(5).toString());
                txtPageNumbers.setText(adapter.getItem(6).toString());
                txtLanguage.setText(adapter.getItem(7).toString());
                txtSeries.setText(adapter.getItem(8).toString());
                txtVol.setText(adapter.getItem(9).toString());
                txtPrice.setText(adapter.getItem(10).toString());
                txtCategory.setText(adapter.getItem(11).toString());
                txtSummary.setText(adapter.getItem(12).toString());

                startEdit = false;
                BindCategory=true;
                startEdit= false;
                start = true;
                timer = new Timer();
                myTimerTask = new MyTimerTask();
                timer.schedule(myTimerTask, 100);



            } else {
                StatusRegister = adapter.getItem(0).toString();

                if (StatusRegister.equals("Update Successfully!")) {

                    Toast.makeText(EditBook.this, StatusRegister, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditBook.this, StatusRegister, Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_book, menu);
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
                    Toast.makeText(EditBook.this, "Category is required!", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditBook.this);
                    builder.setCancelable(false);
                    builder.setMessage("Update?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            StrictMode.enableDefaults();

                            strTitle = txtTitle.getText().toString();
                            strPublisher = txtPublisher.getText().toString();
                            strISBN = txtISBN.getText().toString();
                            strAuthor = txtAuthor.getText().toString();
                            strPubDate = txtPubDate.getText().toString();
                            strPageNumbers = txtPageNumbers.getText().toString();
                            strLanguage = txtLanguage.getText().toString();
                            strSeries = txtSeries.getText().toString();
                            strVol = txtVol.getText().toString();
                            strPrice = txtPrice.getText().toString();
                            strCategory = txtCategory.getText().toString();
                            strSummary = txtSummary.getText().toString();

                            if (strCategory.equals("Select"))
                            {
                                Toast.makeText(EditBook.this, "Category is required!", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                BindCategory = false;
                                startEdit = false;
                                start = true;
                                timer = new Timer();
                                myTimerTask = new MyTimerTask();
                                timer.schedule(myTimerTask, 100);
                            }


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
