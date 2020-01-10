package com.kirman.hairrsvmanagerlocal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;


public class clientInfoFragment extends Fragment {

    //UI Elements
    private TextView mClientName;
    private TextView mClientPhone;
    private TextView mTimesFree;
    private TextView mAddedDate;
    private TextView mLastDate;
    private TextView mListLabel;
    private TextView mNotes;
    private FloatingActionButton mEditClient;

    private int passedPos;
    private clientEntity theClient;
    private String whatToShow;

    public clientInfoFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Retrieve arguments passed to Fragment
        if (getArguments() != null) {
            passedPos = getArguments().getInt(clientPage.CLIENT_POSITION_EXTRA);
            whatToShow = getArguments().getString(clientPage.WHAT_TO_SHOW);
        }

        if(whatToShow == null) {
            whatToShow = "something";
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_client_info, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ///???
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //Bind UI Elements to Code
        mClientName = view.findViewById(R.id.infoName);
        mClientPhone = view.findViewById(R.id.infoPhone);
        mTimesFree = view.findViewById(R.id.infoFree);
        mAddedDate = view.findViewById(R.id.infoAddedDate);
        mLastDate = view.findViewById(R.id.infoLastDate);
        mListLabel = view.findViewById(R.id.appointListLabel);
        mNotes = view.findViewById(R.id.notesText);
        mEditClient = view.findViewById(R.id.editClientFAB);

        //Set the client info
        if(whatToShow.equals("one")) {

            theClient = allClientsFragment.clList.get(passedPos);
        }
        else {

            theClient = allClientsFragment.clListClone.get(passedPos);
        }

        //Set the client info text
        mClientName.setText(theClient.getName());
        mClientPhone.setText(theClient.getPhone());
        mTimesFree.setText(String.valueOf(theClient.getTimesFree()));
        mNotes.setText(theClient.getNote());

        //Set a string to store the added date into
        int year = theClient.getFirstAdded().get(Calendar.YEAR);
        int month = theClient.getFirstAdded().get(Calendar.MONTH) + 1;
        int day = theClient.getFirstAdded().get(Calendar.DAY_OF_MONTH);
        String addedDate = day + "/" + month + "/" + year;

        mAddedDate.setText(addedDate);

        //Set a string to store the last came date into
        if(theClient.getLastCame() == null) {

            mLastDate.setText("---");
        }
        else {

            year = theClient.getLastCame().get(Calendar.YEAR);
            month = theClient.getLastCame().get(Calendar.MONTH) + 1;
            day = theClient.getLastCame().get(Calendar.DAY_OF_MONTH);
            String lastCameDate = day + "/" + month + "/" + year;

            mLastDate.setText(lastCameDate);
        }

        //Set buttons functionality
        mEditClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //'Return' to the base activity and tell it to replace this fragment with the next one
                ((clientPage) getActivity()).changeFragments(clientPage.GO_TO_EDIT, passedPos);
            }
        });

        mClientPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Copy phone number to clipboard
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("phoneCopy", mClientPhone.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Phone number copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        mListLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Display all appointments by this client
                Intent goToClientsAppointmentsIntent = new Intent(getActivity(), appointmentPage.class);
                goToClientsAppointmentsIntent.putExtra(appointmentPage.WHAT_TO_SHOW_APPOINT, "all");
                goToClientsAppointmentsIntent.putExtra(appointmentPage.APPOINT_FILTER, "client");
                goToClientsAppointmentsIntent.putExtra(appointmentPage.APPOINT_CLIENT_ID, theClient.getClientID());
                startActivity(goToClientsAppointmentsIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });

    }

}
