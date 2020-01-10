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

public class clientPage extends AppCompatActivity {

    //UI Elements
    ConstraintLayout mHeader;
    ConstraintLayout mBody;
    ImageButton mGoAppointments;
    ImageButton mHome;
    ImageButton mGoClients;
    ConstraintLayout mFooter;

    //Number to keep track of which fragment is displayed (Info: 0, Add: 1, Edit: 2, All: 3)
    int currentlySelected = 0;

    //Number to keep track of which client is displayed (based on position of client in the global list)
    int currentClientDisplayed = 0;

    //Some arguments bundle for properly switching between fragments
    Bundle bundle;
    public final static String ADD_OR_EDIT_EXTRA = "addEdit";
    public final static String CLIENT_POSITION_EXTRA = "clPos";
    public final static String CLIENT_ID_EXTRA = "clID";
    public final static String CLIENT_ID_GEN = "clientGenerator";
    public final static String WHAT_TO_SHOW = "allOrOne";
    public final static String COMING_FROM_EXTRA = "searchOrNot";

    String whatToShow;
    String whereFrom;

    //Extras for the Shared Preferences
    public static String SAVE_CLIENTS_KEY = "saveClients";
    public static String NMBR_OF_CLIENTS_KEY = "listSize";

    //Static numbering of fragments
    public final static int GO_TO_INFO = 0;
    public final static int GO_TO_ADD = 1;
    public final static int GO_TO_EDIT = 2;
    public final static int GO_TO_ALL = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Condition to go back to SplashActivity if RAM has been cleared so to prevent crashes
        if(!SplashActivity.initialized) {
            Intent splashIntent = new Intent(clientPage.this, SplashActivity.class);
            startActivity(splashIntent);
            finish();
        }

        //'Refresh' the args bundle
        bundle = new Bundle();

        //Get args passed to the activity as extras
        Intent intent = this.getIntent();
        whatToShow = intent.getStringExtra(WHAT_TO_SHOW);
        whereFrom = intent.getStringExtra(COMING_FROM_EXTRA);
        int passedIDFromAppoint = intent.getIntExtra(CLIENT_ID_EXTRA, 0);

        //Bind UI Elements to code
        mHeader = findViewById(R.id.headerLayout);
        mGoAppointments = findViewById(R.id.goAppointmentsButton);
        mGoClients = findViewById(R.id.goClientsButton);
        mHome = findViewById(R.id.goHomeButton);
        mFooter = findViewById(R.id.footerLayout);

        //Update bottom bar UI
        updateBottomUI();

        //If passed args is "all", then initialize the allClients Fragment to be displayed
        if(whatToShow.equals("all")){

            changeFragments(GO_TO_ALL, -1);
        }
        //If passed args is "one", then initialize the ClientInfo Fragment to be displayed
        else if(whatToShow.equals("one")){

            int pos = 0;

            //Find the position of the client with the passed ID
            for(int i=0; i<allClientsFragment.clList.size(); i++) {

                clientEntity cl = allClientsFragment.clList.get(i);
                if(cl.getClientID() == passedIDFromAppoint) {

                    pos = i;
                    break;
                }
            }

            changeFragments(GO_TO_INFO, pos);
        }

        //Go To Appointments Button functionality
        mGoAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Go to all appointments page
                Intent goToAppointmentsIntent = new Intent(clientPage.this, appointmentPage.class);
                goToAppointmentsIntent.putExtra(appointmentPage.WHAT_TO_SHOW_APPOINT, "all");
                goToAppointmentsIntent.putExtra(appointmentPage.APPOINT_FILTER, "nofilter");
                startActivity(goToAppointmentsIntent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        //Go To Home Button functionality
        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Go to home page
                Intent goToHomeIntent = new Intent(clientPage.this, MainActivity.class);
                startActivity(goToHomeIntent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        //Go To Clients Button functionality
        mGoClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Maybe do nothing?
            }
        });

    }

    /** Used to save clients before closing app **/
    @Override
    protected void onStop() {
        super.onStop();

        //Save all the clients to Shared Prefs
        SharedPreferences prefs = clientPage.this.getSharedPreferences(SAVE_CLIENTS_KEY, 0);

        Gson gson = new Gson();
        for(int i = 0; i<allClientsFragment.clList.size(); i++) {

            String json = gson.toJson(allClientsFragment.clList.get(i));
            prefs.edit().putString("client"+i, json).apply();
        }

        //Save the number of clients
        prefs.edit().putInt(NMBR_OF_CLIENTS_KEY, allClientsFragment.clList.size()).apply();

        //Save the client ID Generator
        prefs.edit().putInt(CLIENT_ID_GEN, allClientsFragment.clientIDGenerator).apply();

    }

    /** Back button functionality **/
    @Override
    public void onBackPressed() {

        //If client info page is displayed, go back to all clients
        if (currentlySelected == 0 || currentlySelected == 1) {

            changeFragments(GO_TO_ALL,-1);
        }
        //If edit page is displayed, go back to client's page
        else if (currentlySelected == 2) {

            changeFragments(GO_TO_INFO, currentClientDisplayed);
        }
        else {

            //Go to home page
            Intent goToHomeIntent = new Intent(clientPage.this, MainActivity.class);
            startActivity(goToHomeIntent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

    }

    /** Updates the color of the footer icons **/
    private void updateBottomUI() {

        //Clear all first
        mGoAppointments.setImageDrawable(getResources().getDrawable(R.drawable.custom_size_appointments));
        mHome.setImageDrawable(getResources().getDrawable(R.drawable.custom_size_home));
        mGoClients.setImageDrawable(getResources().getDrawable(R.drawable.custom_size_clients));

        //Specify the clients as the selected one
        mGoClients.setImageDrawable(getResources().getDrawable(R.drawable.custom_size_red_clients));
    }

    /** Fragment changing
     * @param f An integer corresponding to the fragment the user wants to change to
     * @param position The position in the global list of the specific client the app is displaying
     * **/
    public void changeFragments(int f, int position) {

        //Prepare Fragment Manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();

        //Swap to the All Clients fragment
        if(f == GO_TO_ALL){

            allClientsFragment clFrag = new allClientsFragment();
            bundle.putString(COMING_FROM_EXTRA, whereFrom);
            clFrag.setArguments(bundle);
            trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            trans.replace(R.id.body, clFrag);
            currentlySelected = 3;

        }
        //Swap to the Info fragment
        else if(f == GO_TO_INFO){

            clientInfoFragment clFrag = new clientInfoFragment();
            bundle.putInt(CLIENT_POSITION_EXTRA, position);
            bundle.putString(WHAT_TO_SHOW, whatToShow);
            clFrag.setArguments(bundle);
            trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            trans.replace(R.id.body, clFrag);

            //Set some values
            currentlySelected = 0;
            currentClientDisplayed = position;

        }
        //Swap to the Add fragment
        else if(f == GO_TO_ADD){

            addEditClientFragment clFrag = new addEditClientFragment();
            bundle.putString(ADD_OR_EDIT_EXTRA, "add");
            clFrag.setArguments(bundle);
            trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            trans.replace(R.id.body, clFrag);

            //Set some values
            currentlySelected = 1;

        }
        //Swap to the Edit fragment
        else if (f == GO_TO_EDIT){

            addEditClientFragment clFrag = new addEditClientFragment();
            bundle.putInt(CLIENT_POSITION_EXTRA, position);
            bundle.putString(ADD_OR_EDIT_EXTRA, "edit");
            clFrag.setArguments(bundle);
            trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            trans.replace(R.id.body, clFrag);

            //Set some values
            currentlySelected = 2;
            currentClientDisplayed = position;

        }
        trans.commit();

    }
}
