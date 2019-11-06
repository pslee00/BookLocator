package com.ragfilsc.booklocator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;


public class SplashScreen extends ActionBarActivity {

    Timer timer;

    private final static String LoginSetails = "wawasanLogin.txt";

    Boolean NewInstalation;
    String LoginStatus;
    Boolean ReadFile;
    String FirstTimeFlag;
    Boolean AccessVerified;
    String LoginUserID;
    String LoginUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        NewInstalation=true;
        AccessVerified=false;

        ReadLoginDetails();

        timer = new Timer();

        int delay = 3000;

        Timer timer1 = new Timer();

        timer1.schedule(new TimerTask() {
            public void run() {
                if(NewInstalation==true)
                {
                    Intent intent1 = new Intent(SplashScreen.this, Login.class);
                    startActivity(intent1);
                    finish();
                }
                else
                {
                    if (LoginStatus.equals("Logout")) {
                        Intent intent1 = new Intent(SplashScreen.this, Login.class);
                        startActivity(intent1);
                        finish();
                    }
                    else if (LoginStatus.equals("AdminLogin"))
                    {
                        Intent intent1 = new Intent(SplashScreen.this, MainActivity.class);
                        intent1.putExtra("LOGINID", LoginUserID.toString());
                        intent1.putExtra("LOGINUSERNAME", LoginUserName.toString());
                        startActivity(intent1);
                        finish();
                    }
                    else if (LoginStatus.equals("StudentLogin"))
                    {
                        Intent intent1 = new Intent(SplashScreen.this, MainActivityStd.class);
                        intent1.putExtra("LOGINID", LoginUserID.toString());
                        intent1.putExtra("LOGINUSERNAME", LoginUserName.toString());
                        startActivity(intent1);
                        finish();
                    }
                    }
                }
            }, delay);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
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

    public void ReadLoginDetails()

            {
                try {

                    InputStream in = openFileInput(LoginSetails);
                    NewInstalation=true;

                    if (in != null) {
                        InputStreamReader tmp=new InputStreamReader(in);
                        BufferedReader reader=new BufferedReader(tmp);
                        String str;
                        Integer tmpIndex;
                        tmpIndex =0;
                        StringBuilder buf=new StringBuilder();


                        while ((str = reader.readLine()) != null) {

                            buf.append(str + "n");
                            NewInstalation=false;

                            if(tmpIndex ==0)
                            {
                                LoginStatus = str;
                                AccessVerified=true;
                            }
                            else if(tmpIndex==1)
                            {
                                LoginUserID=str;
                            }
                            else if(tmpIndex==2)
                            {
                                LoginUserName=str;
                            }
                            FirstTimeFlag= "False";
                            tmpIndex=tmpIndex +1;
                            NewInstalation=false;
                        }

                        in.close();
                        ReadFile=true;
                        NewInstalation=false;
                    }
                }

                catch (java.io.FileNotFoundException e) {
                    FirstTimeFlag= "True";
                    NewInstalation=true;
                }

                catch (Throwable t) {
                    Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_SHORT).show();
                    FirstTimeFlag= "True";
                }
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
                ActivityCompat.finishAffinity(SplashScreen.this);
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
