package com.kirman.hairrsvmanagerlocal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class SplashActivity extends AppCompatActivity {

    //This variable is meant to check for app resources
    public static boolean initialized;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Means that app has enough resources to go on
        initialized = true;

        //App has been closed and now opened again, so retrieve saved data
        retrieveSavedData();

        //This Activity is only used to display the icon as splash screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void retrieveSavedData() {

        //Retrieve the buffer pointer from Shared Prefs
        SharedPreferences prefs1 = getSharedPreferences(clientPage.SAVE_CLIENTS_KEY, MODE_PRIVATE);
        int listSize1 = prefs1.getInt(clientPage.NMBR_OF_CLIENTS_KEY, 0);

        //Retrieve the client ID Generator from Shared Prefs
        allClientsFragment.clientIDGenerator = prefs1.getInt(clientPage.CLIENT_ID_GEN, 0);

        //Retrieve the clients from Shared Prefs
        Gson gson1 = new Gson();
        for(int i = 0; i< listSize1; i++){

            String json = prefs1.getString("client"+i, "");
            allClientsFragment.clList.add(gson1.fromJson(json, clientEntity.class));
        }

        //Retrieve the buffer pointer from Shared Prefs
        SharedPreferences prefs2 = getSharedPreferences(appointmentPage.SAVE_APPOINTS_KEY, MODE_PRIVATE);
        int listSize2 = prefs2.getInt(appointmentPage.NMBR_OF_APPOINTS_KEY, 0);

        //Retrieve the appointment ID Generator from Shared Prefs
        allAppointsFragment.appointIDGenerator = prefs2.getInt(appointmentPage.APPOINT_ID_GEN, 0);

        //Retrieve the appointments from Shared Prefs
        Gson gson2 = new Gson();
        for(int i = 0; i< listSize2; i++){

            String json = prefs2.getString("appoint"+i, "");
            allAppointsFragment.appointList.add(gson2.fromJson(json, appointmentEntity.class));
        }

        //Make a connection between appointments and clients objects again after restarting app
        makeConnectionBetweenObjects();

        //Re-order appointments so that past dates are on the bottom of the list
        reorderAppoints();

        //For some reason initialization inside the classes wouldn't work
        //So let's fill the clone lists here!
        allAppointsFragment.appointListClone.addAll(allAppointsFragment.appointList);
        allClientsFragment.clListClone.addAll(allClientsFragment.clList);

    }

    //Possibly TOO WASTEFUL TASK
    private void makeConnectionBetweenObjects() {

        for(int i=0; i<allAppointsFragment.appointList.size(); i++) {

            appointmentEntity appoint = allAppointsFragment.appointList.get(i);
            clientEntity tempClient = appoint.getClient();

            for(int j=0; j<allClientsFragment.clList.size(); j++) {

                clientEntity theRightOne = allClientsFragment.clList.get(j);
                if(tempClient.getClientID() == theRightOne.getClientID()) {

                    appoint.setClient(theRightOne);
                    allAppointsFragment.appointList.set(i, appoint);
                }
            }
        }
    }

    public void reorderAppoints() {

        //Firstly, re-sort the list based on date
        Collections.sort(allAppointsFragment.appointList, new Comparator<appointmentEntity>() {
            @Override
            public int compare(appointmentEntity t1, appointmentEntity t2) {

                Calendar date1 = t1.getDate();
                Calendar date2 = t2.getDate();

                return date1.compareTo(date2);
            }
        });

        int listSize = allAppointsFragment.appointList.size();

        //Then, put past appointment on the end of the list in "newer first" order
        if(listSize > 0) {

            appointmentEntity appoint = allAppointsFragment.appointList.get(0);
            long todayInMillis = Calendar.getInstance().getTimeInMillis();
            int pastCounter = -1;

            //If appointment is more than half a day past, put it in the end of future appointments
            while(todayInMillis > appoint.getDate().getTimeInMillis() + DateUtils.DAY_IN_MILLIS/2) {

                //Set as past appointment
                appoint.setIsPast(true);
                pastCounter++;

                //If past pointer = global list size, it means there are no future appointments set yet, so break the loop
                if(pastCounter == listSize) {

                    break;
                }

                //Add the past appointment to its proper place on the bottom of the list (newer appointments go to the top of the past appoints list)
                allAppointsFragment.appointList.add(listSize - pastCounter, appoint);
                //Delete the past appointment on the top of the list
                allAppointsFragment.appointList.remove(0);

                //Get the next date that is now placed at position 0 and restart the loop
                appoint = allAppointsFragment.appointList.get(0);
            }
        }
    }
}
