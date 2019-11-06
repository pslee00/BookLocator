package com.ragfilsc.booklocator;

import android.app.AlertDialog;
import android.content.DialogInterface;
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


public class RegAdmin extends ActionBarActivity {

    TextView txtAdminName;
    TextView txtAdminId;
    TextView txtPassword;
    TextView txtVPassword;

    String strAdminName;
    String strAdminId;
    String strPassword;
    String strVPassword;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_admin);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Register Admin")));

        txtAdminName = (TextView) findViewById(R.id.txtName);
        txtAdminId = (TextView) findViewById(R.id.txtAdminID);
        txtPassword = (TextView) findViewById(R.id.txtPassword);
        txtVPassword = (TextView) findViewById(R.id.txtConfirmPassword);
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable(){

                @Override
                public void run() {

                    ConnectivityManager cManager = (ConnectivityManager) getSystemService(RegAdmin.this.CONNECTIVITY_SERVICE);
                    NetworkInfo nInfo = cManager.getActiveNetworkInfo();

                    if (nInfo != null && nInfo.isConnected())
                    {

                        //pBar.setVisibility(View.VISIBLE);
                        start = true;
                        ipAddress =  getString(R.string.dbserver).toString()  + getString(R.string.adminregadmin).toString() + "?strAdminName=" + strAdminName + "&strAdminId=" + strAdminId + "&strPassword=" + strPassword + "&strVPassword=" + strVPassword;

                        getJSON(ipAddress);
                        start = false;

                        timer.cancel();
                    }
                    else

                    {
                        //pBar.setVisibility(View.GONE);
                        Toast.makeText(RegAdmin.this, "Connect to Wi-Fi Internet network or turn on Mobile Data.", Toast.LENGTH_SHORT).show();
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegAdmin.this, android.R.layout.simple_spinner_item, dbRecords);

        StatusRegister = adapter.getItem(0).toString();

        if (StatusRegister.equals("Register Successfully!")) {

            Toast.makeText(RegAdmin.this, StatusRegister, Toast.LENGTH_SHORT).show();

            txtAdminName.setText("");
            txtAdminId.setText("");
            txtPassword.setText("");
            txtVPassword.setText("");

        }
        else
        {
            Toast.makeText(RegAdmin.this, StatusRegister, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reg_admin, menu);
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

                strAdminName = txtAdminName.getText().toString();
                strAdminId = txtAdminId.getText().toString().toUpperCase();
                strPassword = txtPassword.getText().toString();
                strVPassword = txtVPassword.getText().toString();

                if (txtAdminName.length() == 0) {
                    Toast.makeText(RegAdmin.this, "Admin name is required!", Toast.LENGTH_LONG).show();
                } else if (txtAdminId.length() == 0) {
                    Toast.makeText(RegAdmin.this, "Admin id is required!", Toast.LENGTH_LONG).show();
                } else if (txtPassword.length() == 0) {
                    Toast.makeText(RegAdmin.this, "Password is required!", Toast.LENGTH_LONG).show();
                } else if (strVPassword.length() == 0) {
                    Toast.makeText(RegAdmin.this, "Verify password is required!", Toast.LENGTH_LONG).show();
                }
                else {
                    if (strPassword.equals(strVPassword)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegAdmin.this);
                        builder.setCancelable(false);
                        builder.setMessage("Save?");
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                StrictMode.enableDefaults();

                                strAdminName = txtAdminName.getText().toString();
                                strAdminId = txtAdminId.getText().toString().toUpperCase();
                                strPassword = txtPassword.getText().toString();
                                strVPassword = txtVPassword.getText().toString();

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
                    } else {
                        Toast.makeText(RegAdmin.this, "Password and Confirm Password Not Matched!", Toast.LENGTH_LONG).show();

                    }
                }
        }

        return super.onOptionsItemSelected(item);
    }
}
