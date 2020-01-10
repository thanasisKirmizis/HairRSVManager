package com.kirman.hairrsvmanagerlocal;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;


public class HomeFragment extends Fragment {

    //UI Elements
    private CardView mCard;
    private TextView mClientName;
    private TextView mClientPhone;
    private TextView mAppointTime;
    private Button mSearchBtn;
    private ConstraintLayout mTodayBtn;
    private ConstraintLayout mTomorrowBtn;

    //An index used to track next appointment
    private int appointIndex = 0;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ///???
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //Bind UI Elements to Code
        mCard = view.findViewById(R.id.cardView);
        mClientName = view.findViewById(R.id.clientName);
        mClientPhone = view.findViewById(R.id.clientPhone);
        mAppointTime = view.findViewById(R.id.appointTime);
        mSearchBtn = view.findViewById(R.id.searchBtn);
        mTodayBtn = view.findViewById(R.id.todayBtn);
        mTomorrowBtn = view.findViewById(R.id.tomorrowBtn);

        //Set Card's text
        if(allAppointsFragment.appointList.size() > 0) {

            //To find which appointment is next, start from the beginning of the list and find the first that is placed after 'now'
            long nowInMillis = Calendar.getInstance().getTimeInMillis();
            appointmentEntity nextAppoint = allAppointsFragment.appointList.get(appointIndex);

            while(nextAppoint.getDate().getTimeInMillis() < nowInMillis) {

                appointIndex++;

                //If no more future appointments on the list, break the loop
                if(appointIndex == allAppointsFragment.appointList.size()) {

                    break;
                }
                else {

                    nextAppoint = allAppointsFragment.appointList.get(appointIndex);
                }
            }

            //If no future appointments are set, display placeholder text
            if(appointIndex == allAppointsFragment.appointList.size()) {

                mClientName.setText("Name Of Client");
                mClientPhone.setText("69XXXXXXXX");
                mAppointTime.setText("Time Of Appointment");
            }
            //Else display the info of the next appointment
            else {

                mClientName.setText(nextAppoint.getClient().getName());
                mClientPhone.setText(nextAppoint.getClient().getPhone());
                String time = String.format(Locale.getDefault(), "%02d:%02d", nextAppoint.getDate().get(Calendar.HOUR_OF_DAY), nextAppoint.getDate().get(Calendar.MINUTE));
                mAppointTime.setText(time);
            }
        }
        else {

            mClientName.setText("Name Of Client");
            mClientPhone.setText("69XXXXXXXX");
            mAppointTime.setText("Time Of Appointment");
        }

        //Set buttons functionality
        mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(allAppointsFragment.appointList.size() > 0 && appointIndex != allAppointsFragment.appointList.size()) {
                    //Go to the appointment's page
                    Intent goToAppointmentsIntent = new Intent(getActivity(), appointmentPage.class);
                    goToAppointmentsIntent.putExtra(appointmentPage.WHAT_TO_SHOW_APPOINT, "one");
                    goToAppointmentsIntent.putExtra(appointmentPage.APPOINT_POSITION_EXTRA, appointIndex);
                    goToAppointmentsIntent.putExtra(appointmentPage.APPOINT_FILTER, "nofilter");
                    startActivity(goToAppointmentsIntent);
                    getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out);
                }
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent goToClientsIntent = new Intent(getActivity(), clientPage.class);
                goToClientsIntent.putExtra(clientPage.WHAT_TO_SHOW, "all");
                goToClientsIntent.putExtra(clientPage.COMING_FROM_EXTRA, "search");
                startActivity(goToClientsIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
            }
        });

        mTodayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent goToAppointsToday = new Intent(getActivity(), appointmentPage.class);
                goToAppointsToday.putExtra(appointmentPage.WHAT_TO_SHOW_APPOINT, "all");
                goToAppointsToday.putExtra(appointmentPage.APPOINT_FILTER, "today");
                startActivity(goToAppointsToday);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out);
            }
        });

        mTomorrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent goToAppointsTomorrow = new Intent(getActivity(), appointmentPage.class);
                goToAppointsTomorrow.putExtra(appointmentPage.WHAT_TO_SHOW_APPOINT, "all");
                goToAppointsTomorrow.putExtra(appointmentPage.APPOINT_FILTER, "tomorrow");
                startActivity(goToAppointsTomorrow);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out);
            }
        });

    }

}
