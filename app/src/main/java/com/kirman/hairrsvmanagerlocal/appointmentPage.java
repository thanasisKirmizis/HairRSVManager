package com.kirman.hairrsvmanagerlocal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.gson.Gson;

public class appointmentPage extends AppCompatActivity {

    //UI Elements
    ConstraintLayout mHeader;
    ConstraintLayout mBody;
    ImageButton mGoAppointments;
    ImageButton mHome;
    ImageButton mGoClients;
    ConstraintLayout mFooter;

    //Number to keep track of which fragment is displayed (Info: 0, Add: 1, Edit: 2, All: 3)
    int currentlySelected = 0;

    //Number to keep track of which appointment is displayed (based on position of appointment in the global list)
    int currentAppointDisplayed = 0;

    //Some arguments bundle for properly switching between fragments
    Bundle bundle;
    public final static String ADD_OR_EDIT_APPOINT_EXTRA = "addEditAppoint";
    public final static String APPOINT_POSITION_EXTRA = "appointPos";
    public final static String WHAT_TO_SHOW_APPOINT = "AppointAllOrOne";
    public final static String APPOINT_FILTER = "filter";
    public final static String APPOINT_CLIENT_ID = "specificClient";

    String whatToShow;
    String filter;
    int clientPassedID;
    int appointPassedPos;

    //Extras for the Shared Preferences
    public static String SAVE_APPOINTS_KEY = "saveAppoints";
    public static String NMBR_OF_APPOINTS_KEY = "AppointListSize";
    public static String APPOINT_ID_GEN = "appointGenerator";

    //Static numbering of fragments
    public final static int GO_TO_ALL = 0;
    public final static int GO_TO_INFO = 1;
    public final static int GO_TO_ADD = 2;
    public final static int GO_TO_EDIT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Condition to go back to SplashActivity if RAM has been cleared so to prevent crashes
        if(!SplashActivity.initialized) {
            Intent splashIntent = new Intent(appointmentPage.this, SplashActivity.class);
            startActivity(splashIntent);
            finish();
        }

        //'Refresh' the args bundle
        bundle = new Bundle();

        //Get args passed to the activity as extras
        Intent intent = this.getIntent();
        whatToShow = intent.getStringExtra(WHAT_TO_SHOW_APPOINT);
        filter = intent.getStringExtra(APPOINT_FILTER);
        clientPassedID = intent.getIntExtra(APPOINT_CLIENT_ID, 0);
        appointPassedPos = intent.getIntExtra(APPOINT_POSITION_EXTRA, 0);

        //Bind UI Elements to code
        mHeader = findViewById(R.id.headerLayout);
        mGoAppointments = findViewById(R.id.goAppointmentsButton);
        mGoClients = findViewById(R.id.goClientsButton);
        mHome = findViewById(R.id.goHomeButton);
        mFooter = findViewById(R.id.footerLayout);

        //Update bottom bar UI
        updateBottomUI();

        //If passed args is "all", then initialize the allAppointments Fragment to be displayed
        if(whatToShow.equals("all")){

            changeFragments(GO_TO_ALL, -1);
        }
        //If passed args is "one", then initialize the appointmentInfo Fragment to be displayed
        else if(whatToShow.equals("one")){

            changeFragments(GO_TO_INFO, appointPassedPos);
        }

        //Go To Appointments Button functionality
        mGoAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               //Maybe do nothing?
            }
        });

        //Go To Home Button functionality
        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Go to home page
                Intent goToHomeIntent = new Intent(appointmentPage.this, MainActivity.class);
                startActivity(goToHomeIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //Go To Clients Button functionality
        mGoClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Go to all clients page
                Intent goToClientsIntent = new Intent(appointmentPage.this, clientPage.class);
                goToClientsIntent.putExtra(clientPage.WHAT_TO_SHOW, "all");
                goToClientsIntent.putExtra(clientPage.COMING_FROM_EXTRA, "footer");
                startActivity(goToClientsIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    /** Used to save appointments before closing app **/
    @Override
    protected void onStop() {
        super.onStop();

        //Save all the appointments to Shared Prefs
        SharedPreferences prefs = appointmentPage.this.getSharedPreferences(SAVE_APPOINTS_KEY, 0);

        Gson gson = new Gson();
        for(int i = 0; i<allAppointsFragment.appointList.size(); i++) {

            String json = gson.toJson(allAppointsFragment.appointList.get(i));
            prefs.edit().putString("appoint"+i, json).apply();
        }

        //Save the number of appointments
        prefs.edit().putInt(NMBR_OF_APPOINTS_KEY, allAppointsFragment.appointList.size()).apply();

        //Save the appointment ID Generator
        prefs.edit().putInt(APPOINT_ID_GEN, allAppointsFragment.appointIDGenerator).apply();
    }

    /** Back button functionality **/
    @Override
    public void onBackPressed() {

        //If appointment info page is displayed, go back to all appointments
        if (currentlySelected == 1 || currentlySelected == 2) {

            changeFragments(GO_TO_ALL,-1);
        }
        //If edit page is displayed, go back to appointments's page
        else if (currentlySelected == 3) {

            changeFragments(GO_TO_INFO, currentAppointDisplayed);
        }
        else {

            //Go to home page
            Intent goToHomeIntent = new Intent(appointmentPage.this, MainActivity.class);
            startActivity(goToHomeIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }

    /** Updates the color of the footer icons **/
    private void updateBottomUI() {

        //Clear all first
        mGoAppointments.setImageDrawable(getResources().getDrawable(R.drawable.custom_size_appointments));
        mHome.setImageDrawable(getResources().getDrawable(R.drawable.custom_size_home));
        mGoClients.setImageDrawable(getResources().getDrawable(R.drawable.custom_size_clients));

        //Specify the appointments as the selected one
        mGoAppointments.setImageDrawable(getResources().getDrawable(R.drawable.custom_size_red_appointments));
    }

    /** Fragment changing
     * @param f An integer corresponding to the fragment the user wants to change to
     * @param position The position in the global list of the specific client the app is displaying
     * **/
    public void changeFragments(int f, int position) {

        //Prepare Fragment Manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();

        //Swap to the All Appointments fragment
        if(f == GO_TO_ALL){

            allAppointsFragment appointFrag = new allAppointsFragment();
            bundle.putString(APPOINT_FILTER, filter);
            bundle.putInt(APPOINT_CLIENT_ID, clientPassedID);
            appointFrag.setArguments(bundle);
            trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            trans.replace(R.id.body, appointFrag);
            currentlySelected = 0;

        }
        //Swap to the Info fragment
        else if(f == GO_TO_INFO){

            appointInfoFragment appointFrag = new appointInfoFragment();
            bundle.putInt(APPOINT_POSITION_EXTRA, position);
            bundle.putString(WHAT_TO_SHOW_APPOINT, whatToShow);
            appointFrag.setArguments(bundle);
            trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            trans.replace(R.id.body, appointFrag);

            //Set some values
            currentlySelected = 1;
            currentAppointDisplayed = position;

        }
        //Swap to the Add fragment
        else if(f == GO_TO_ADD){

            addEditAppointFragment appointFrag = new addEditAppointFragment();
            bundle.putString(ADD_OR_EDIT_APPOINT_EXTRA, "add");
            appointFrag.setArguments(bundle);
            trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            trans.replace(R.id.body, appointFrag);

            //Set some values
            currentlySelected = 2;

        }
        //Swap to the Edit fragment
        else if (f == GO_TO_EDIT){

            addEditAppointFragment appointFrag = new addEditAppointFragment();
            bundle.putInt(APPOINT_POSITION_EXTRA, position);
            bundle.putString(ADD_OR_EDIT_APPOINT_EXTRA, "edit");
            appointFrag.setArguments(bundle);
            trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            trans.replace(R.id.body, appointFrag);

            //Set some values
            currentlySelected = 3;
            currentAppointDisplayed = position;

        }
        trans.commit();

    }
}
