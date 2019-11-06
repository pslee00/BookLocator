package com.ragfilsc.booklocator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Login extends ActionBarActivity {

    String AdminPINNO = "123456";
    String StudentPINNO = "654321";

    EditText editTxtUsername;
    EditText editTxtPassword;
    String strUsername;
    String strUserPassword;
    String strLoginUserID;
    String strLoginUserName;

    private final static String LoginSetails = "wawasanLogin.txt";

    Timer timer;
    MyTimerTask myTimerTask;
    String StatusLogin;

    String id;

    String ipAddress;
    String line=null;
    String result=null;
    InputStream is=null;
    int i;

    int TotalRecords;
    int IndexCount;
    List dbRecords = new ArrayList();
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        btnLogin = (Button) findViewById(R.id.btnLogin);
        editTxtUsername = (EditText) findViewById(R.id.txtUsername);
        editTxtPassword = (EditText) findViewById(R.id.txtPassword);

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                strUsername = editTxtUsername.getText().toString();
                strUserPassword = editTxtPassword.getText().toString();

                strUsername =strUsername.toUpperCase();

                if (strUsername.length() == 0) {
                    Toast.makeText(Login.this, "USERNAME is required!", Toast.LENGTH_SHORT).show();
                }
                else if (strUserPassword.length() == 0) {
                    Toast.makeText(Login.this, "PIN is required!", Toast.LENGTH_SHORT).show();
                }
                else if (strUserPassword.length() <= 5) {
                    Toast.makeText(Login.this, "Invalid Password length!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    StrictMode.enableDefaults();

                    timer = new Timer();
                    myTimerTask = new MyTimerTask();
                    timer.schedule(myTimerTask, 100);
                }
                /*
                else if (strUserPassword.equals(AdminPINNO.toString())) {

                    SaveActivationAdmin();

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }
                else if (strUserPassword.equals(StudentPINNO.toString())) {

                    SaveActivationStd();

                    Intent i = new Intent(getApplicationContext(), MainActivityStd.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(Login.this, "INVALID USERNAME OR PIN!", Toast.LENGTH_SHORT).show();
                    editTxtUsername.setText("");
                    editTxtPassword.setText("");
                }
                */
            }
        });

    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable(){

                @Override
                public void run() {

                    ConnectivityManager cManager = (ConnectivityManager) getSystemService(Login.this.CONNECTIVITY_SERVICE);
                    NetworkInfo nInfo = cManager.getActiveNetworkInfo();

                    if (nInfo != null && nInfo.isConnected())
                    {
                        //pBar.setVisibility(View.VISIBLE);
                        ipAddress =  getString(R.string.dbserver).toString() + getString(R.string.login).toString() + "?strLoginID=" + strUsername + "&strLoginPassword=" + strUserPassword;
                        getJSON(ipAddress);
                        timer.cancel();


                    }
                    else

                    {
                        //pBar.setVisibility(View.GONE);
                        Toast.makeText(Login.this, "Connect to Wi-Fi Internet network or turn on Mobile Data.", Toast.LENGTH_SHORT).show();
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
                    //FlagTimeout=false;
                    //ErrMsg="";
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
                        //StatusLogin =json;

                        sb.append(json + "\n");
                        dbRecords.add(i, json);
                        i=i+1;
                    }

                    //TotalRecords=i;
                    return sb.toString().trim();

                } catch (java.net.SocketTimeoutException e) {
                    //FlagTimeout=true;
                    return null;
                }
                catch (Exception e) {
                    //ErrMsg=e.toString();
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void ChoppingData(String json) throws JSONException {

        Integer ttlRecRceived;
        ttlRecRceived = dbRecords.size() ;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Login.this, android.R.layout.simple_spinner_item, dbRecords);

        //String tmpStatusLogin = StatusLogin;

        String tmpStatusLogin;

        if (ttlRecRceived==0) {
            //pBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            //Toast.makeText(LoginActivate.this, "Server unavailable!", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage("Server unavailable to verify your account. Please try again later!");
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else
        {
            tmpStatusLogin = adapter.getItem(0).toString();
            strLoginUserID = adapter.getItem(1).toString();
            strLoginUserName = adapter.getItem(2).toString();

            if (tmpStatusLogin.equals("Successfully Login(Admin)!"))
            {
                //Toast.makeText(Login.this, "Admin Login Success!", Toast.LENGTH_SHORT).show();
                Toast.makeText(Login.this, "Welcome, " + strLoginUserName.toString(), Toast.LENGTH_SHORT).show();
                SaveActivationAdmin();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("LOGINID", strLoginUserID.toString());
                i.putExtra("LOGINUSERNAME", strLoginUserName.toString());
                startActivity(i);
            }
            else if (tmpStatusLogin.equals("Successfully Login(Student)!"))
            {
                //Toast.makeText(Login.this, "Student Login Success!", Toast.LENGTH_SHORT).show();
                Toast.makeText(Login.this, "Welcome, " + strLoginUserName.toString(), Toast.LENGTH_SHORT).show();
                SaveActivationStd();

                Intent i = new Intent(getApplicationContext(), MainActivityStd.class);
                i.putExtra("LOGINID", strLoginUserID.toString());
                i.putExtra("LOGINUSERNAME", strLoginUserName.toString());
                startActivity(i);
            }
            else
            {
                Toast.makeText(Login.this, StatusLogin, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    public void SaveActivationAdmin() {
        try {
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(LoginSetails, 0));
            out.write(("AdminLogin") + "\n");
            out.write((strLoginUserID) + "\n");
            out.write(("strLoginUserName") + "\n");
            out.close();

        } catch (Throwable t) {

        }
    }

    public void SaveActivationStd() {
        try {
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(LoginSetails, 0));
            out.write(("StudentLogin") + "\n");
            out.write((strLoginUserID) + "\n");
            out.write((strLoginUserName) + "\n");
            out.close();

        } catch (Throwable t) {

        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Exit Book Locator app?");
        builder.setPositiveButton("YES, I AM SURE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                moveTaskToBack(true);
                //finish();
                ActivityCompat.finishAffinity(Login.this);
                System.exit(0);
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
}
