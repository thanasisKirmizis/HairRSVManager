package com.kirman.hairrsvmanagerlocal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Locale;


public class appointInfoFragment extends Fragment {

    //UI Elements
    private TextView mClientName;
    private TextView mClientPhone;
    private TextView mAppointTime;
    private TextView mAppointDate;
    private TextView mNotes;
    private CardView mClientCard;
    private FloatingActionButton mEditAppoint;

    private int passedPos;
    private String whatToShow;
    private appointmentEntity theAppointment;

    public appointInfoFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Retrieve arguments passed to Fragment
        if (getArguments() != null) {
            passedPos = getArguments().getInt(appointmentPage.APPOINT_POSITION_EXTRA);
            whatToShow = getArguments().getString(appointmentPage.WHAT_TO_SHOW_APPOINT);
        }

        //This is to escape comparisons with null string
        if(whatToShow == null) {
            whatToShow = "something";
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment_info, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ///???
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //Bind UI Elements to Code
        mClientName = view.findViewById(R.id.infoName2);
        mClientPhone = view.findViewById(R.id.infoPhone2);
        mAppointTime = view.findViewById(R.id.appointTime2);
        mAppointDate = view.findViewById(R.id.appointDate);
        mNotes = view.findViewById(R.id.notesText2);
        mClientCard = view.findViewById(R.id.clientCard);
        mEditAppoint = view.findViewById(R.id.editAppointFAB);

        //Get the appointment instance
        if(whatToShow.equals("one")) {

            theAppointment = allAppointsFragment.appointList.get(passedPos);
        }
        else {

            theAppointment = allAppointsFragment.appointListClone.get(passedPos);
        }

        //Set a string to store the date into
        int year = theAppointment.getDate().get(Calendar.YEAR);
        int month = theAppointment.getDate().get(Calendar.MONTH) + 1;
        int day = theAppointment.getDate().get(Calendar.DAY_OF_MONTH);
        String theDate = day + "/" + month + "/" + year;

        //A string to store the time into
        String theTime = String.format(Locale.getDefault(), "%02d:%02d", theAppointment.getDate().get(Calendar.HOUR_OF_DAY), theAppointment.getDate().get(Calendar.MINUTE));

        //Set the appointment info text
        mClientName.setText(theAppointment.getClient().getName());
        mClientPhone.setText(theAppointment.getClient().getPhone());
        mAppointDate.setText(theDate);
        mAppointTime.setText(theTime);
        mNotes.setText(theAppointment.getNote());

        //This is just to get moving text
        mClientName.setSelected(true);

        //Set buttons functionality
        mEditAppoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //'Return' to the base activity and tell it to replace this fragment with the next one
                ((appointmentPage) getActivity()).changeFragments(appointmentPage.GO_TO_EDIT, passedPos);
            }
        });

        mClientCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Go to client's page
                Intent goToClientsIntent = new Intent(getActivity(), clientPage.class);
                goToClientsIntent.putExtra(clientPage.WHAT_TO_SHOW, "one");
                goToClientsIntent.putExtra(clientPage.COMING_FROM_EXTRA, "footer");
                goToClientsIntent.putExtra(clientPage.CLIENT_ID_EXTRA, theAppointment.getClient().getClientID());
                startActivity(goToClientsIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

}
