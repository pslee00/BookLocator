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
import android.widget.EditText;
import android.widget.Toast;

import java.io.OutputStreamWriter;


public class MainActivity extends ActionBarActivity {

    private final static String LoginSetails = "wawasanLogin.txt";
    String LoginUserID;
    String LoginUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Admin Mode")));

        Button btnRegStd = (Button) findViewById(R.id.btnMenu1);
        Button btnRegAdmin = (Button) findViewById(R.id.btnMenu2);
        Button btnRegBook = (Button) findViewById(R.id.btnMenu3);
        Button btnRegCategories = (Button) findViewById(R.id.btnMenu4);

        Button btnManageStd = (Button) findViewById(R.id.btnMenu5);
        Button btnManageAdmin = (Button) findViewById(R.id.btnMenu6);
        Button btnManageBook = (Button) findViewById(R.id.btnMenu7);
        Button btnManageCategories = (Button) findViewById(R.id.btnMenu8);
        Button btnManageReservation = (Button) findViewById(R.id.btnMenu9);
        Button btnCancelledReservation = (Button) findViewById(R.id.btnMenu10);

        LoginUserID = (String) getIntent().getStringExtra("LoginUserID");
        LoginUserName = (String) getIntent().getStringExtra("LoginUserName");

        btnRegStd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), RegStd.class);
                startActivity(i);
            }
        });

        btnRegAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), RegAdmin.class);
                startActivity(i);
            }
        });

        btnRegBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), AddBook.class);
                startActivity(i);
            }
        });

        btnRegCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), Categories.class);
                startActivity(i);
            }
        });

        btnManageStd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), ManageStd.class);
                startActivity(i);
            }
        });

        btnManageAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), ManageAdmin.class);
                startActivity(i);
            }
        });

        btnManageBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), ManageBook.class);
                startActivity(i);
            }
        });

        btnManageCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), ManageCategory.class);
                startActivity(i);
            }
        });

        btnManageReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), Reservation.class);
                startActivity(i);
            }
        });

        btnCancelledReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), CanceleldReservation.class);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        builder.setMessage("Exit Book Locator app?");
        builder.setPositiveButton("YES, I AM SURE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                moveTaskToBack(true);
                //finish();
                ActivityCompat.finishAffinity(MainActivity.this);
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
