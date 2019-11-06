package com.ragfilsc.booklocator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.OutputStreamWriter;


public class MainActivityStd extends ActionBarActivity {

    private final static String LoginSetails = "wawasanLogin.txt";
    String LoginUserID;
    String LoginUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_std);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Student Mode")));

        Button btnReservation = (Button) findViewById(R.id.btnMenu1);
        Button btnManageReservation = (Button) findViewById(R.id.btnMenu2);
        Button btnManageHistory = (Button) findViewById(R.id.btnMenu3);
        Button btnManageCanHistory = (Button) findViewById(R.id.btnMenu4);

       LoginUserID = (String) getIntent().getStringExtra("LOGINID");
       LoginUserName = (String) getIntent().getStringExtra("LOGINUSERNAME");

        btnReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), std_reservation.class);
                i.putExtra("LOGINID", LoginUserID.toString());
                i.putExtra("LOGINUSERNAME", LoginUserName.toString());
                startActivity(i);
            }
        });

        btnManageReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), std_manage_reservation.class);
                i.putExtra("LOGINID", LoginUserID.toString());
                i.putExtra("LOGINUSERNAME", LoginUserName.toString());
                startActivity(i);
            }
        });

        btnManageHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), std_histroy.class);
                i.putExtra("LOGINID", LoginUserID.toString());
                i.putExtra("LOGINUSERNAME", LoginUserName.toString());
                startActivity(i);
            }
        });

        btnManageCanHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), stdCancelledReservation.class);
                i.putExtra("LOGINID", LoginUserID.toString());
                i.putExtra("LOGINUSERNAME", LoginUserName.toString());
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_std, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.action_logout)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage("Logout Library Book Locator app?");
            builder.setPositiveButton("YES, I AM SURE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SaveActivation();
                    //finish();
                    Intent i=new Intent(getApplicationContext(),Login.class);
                    startActivity(i);
                    finish();
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
        if (id == R.id.action_about) {
            Intent intent1 = new Intent(this, About.class);
            startActivity(intent1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Exit Library Book Locator app?");
        builder.setPositiveButton("YES, I AM SURE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                moveTaskToBack(true);
                //finish();
                ActivityCompat.finishAffinity(MainActivityStd.this);
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

    public void SaveActivation() {
        try {
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(LoginSetails, 0));
            out.write(("Logout") + "\n");
            out.close();

        } catch (Throwable t) {

        }
    }
}
