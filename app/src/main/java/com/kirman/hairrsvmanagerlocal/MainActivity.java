package com.kirman.hairrsvmanagerlocal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    //UI Elements
    ConstraintLayout mHeader;
    ConstraintLayout mBody;
    ImageButton mGoAppointments;
    ImageButton mHome;
    ImageButton mGoClients;
    ConstraintLayout mFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Condition to go back to SplashActivity if RAM has been cleared so to prevent crashes
        if(!SplashActivity.initialized) {
            Intent splashIntent = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(splashIntent);
            finish();
        }

        //Bind UI Elements to code
        mHeader = findViewById(R.id.headerLayout);
        mGoAppointments = findViewById(R.id.goAppointmentsButton);
        mGoClients = findViewById(R.id.goClientsButton);
        mHome = findViewById(R.id.goHomeButton);
        mFooter = findViewById(R.id.footerLayout);

        //Update bottom bar UI
        updateBottomUI();

        //Setup an initial fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();
        //Initialize the Home Fragment to be displayed
        HomeFragment homeFrag = new HomeFragment();
        trans.replace(R.id.body, homeFrag);
        trans.commit();

        //Go To Appointments Button functionality
        mGoAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Go to all appointments page
                Intent goToAppointmentsIntent = new Intent(MainActivity.this, appointmentPage.class);
                goToAppointmentsIntent.putExtra(appointmentPage.WHAT_TO_SHOW_APPOINT, "all");
                goToAppointmentsIntent.putExtra(appointmentPage.APPOINT_FILTER, "nofilter");
                startActivity(goToAppointmentsIntent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out);
            }
        });

        //Go To Home Button functionality
        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               //Maybe do nothing?
            }
        });

        //Go To Clients Button functionality
        mGoClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Go to all clients page
                Intent goToClientsIntent = new Intent(MainActivity.this, clientPage.class);
                goToClientsIntent.putExtra(clientPage.WHAT_TO_SHOW, "all");
                goToClientsIntent.putExtra(clientPage.COMING_FROM_EXTRA, "footer");
                startActivity(goToClientsIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
            }
        });

    }

    /** Back button functionality **/
    @Override
    public void onBackPressed() {

        //Show a confirmation of app exit dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hair RSV Manager");
        builder.setMessage("Do you really want to exit?");
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finishAffinity();
                System.exit(0);
            }
        });
        builder.setPositiveButton("Stay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /** Updates the color of the footer icons **/
    private void updateBottomUI() {

        //Clear all first
        mGoAppointments.setImageDrawable(getResources().getDrawable(R.drawable.custom_size_appointments));
        mHome.setImageDrawable(getResources().getDrawable(R.drawable.custom_size_home));
        mGoClients.setImageDrawable(getResources().getDrawable(R.drawable.custom_size_clients));

        //Specify Home as the selected one
        mHome.setImageDrawable(getResources().getDrawable(R.drawable.custom_size_red_home));
    }
}
