package com.ragfilsc.booklocator;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


public class About extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "About")));

        TextView txtDevID = (TextView) findViewById(R.id.Title9);
        TextView txtDevIP = (TextView) findViewById(R.id.Title3);

        Button btnAppInfo = (Button) findViewById(R.id.appInfo);

        txtDevID.setText("Device Id : " + Build.SERIAL);
        if (getIPAddress().equals("INVALID"))
            txtDevIP.setText("IP Address : unavailable");
        else
            txtDevIP.setText("IP address : " + getIPAddress());

        btnAppInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String packageName = "com.ragfilsc.booklocator";

                try {
                    //Open the specific App Info page:
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    //Open the generic Apps page:
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivity(intent);

                }
            }
        });
    }

    private String getIPAddress() {
        try {
            String ipv4;
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();

                for (Enumeration<InetAddress> enumIPAdd = intf.getInetAddresses(); enumIPAdd.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIPAdd.nextElement();
                    //inetAddress.isLoopbackAddress=ipv6
                    //InetAddressUtils.isIPv4Address(ipv4 = inetAddress.getHostAddress()= ipv4
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = inetAddress.getHostAddress())) {
                        //String firstSubString="";
                        //String secondSubString="";
                        String s = inetAddress.getHostAddress().toString();
                        //String[] split = s.split("%");
                        //firstSubString = split[0];
                        //secondSubString = split[1];

                        return s;//inetAddress.getHostAddress().toString();
                    }

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "INVALID";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
