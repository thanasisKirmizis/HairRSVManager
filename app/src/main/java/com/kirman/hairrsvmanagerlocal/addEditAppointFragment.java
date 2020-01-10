package com.kirman.hairrsvmanagerlocal;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;


public class addEditAppointFragment extends Fragment {

    //UI Elements
    private EditText mClient;
    private EditText mDate;
    private EditText mTime;
    private EditText mNote;
    private TextInputLayout mLayClient;
    private TextInputLayout mLayDate;
    private TextInputLayout mLayTime;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private Button mSave;
    private Button mDelete;
    private Button mCancel;

    private String addOrEdit;
    private int pos;
    private appointmentEntity theAppointment;
    private Calendar tempCal = Calendar.getInstance();
    private clientEntity tempClient;

    public addEditAppointFragment() {
        // Required empty public constructor
         addOrEdit = "add";
         pos = 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit_appointment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //Retrieve arguments passed to Fragment
        Bundle gimme = getArguments();
        if (gimme != null) {

            addOrEdit = gimme.getString(appointmentPage.ADD_OR_EDIT_APPOINT_EXTRA);
            pos = gimme.getInt(appointmentPage.APPOINT_POSITION_EXTRA);
        }

        //Bind UI Elements to Code
        mClient = view.findViewById(R.id.editClient);
        mDate = view.findViewById(R.id.editDate);
        mTime = view.findViewById(R.id.editTime);
        mNote = view.findViewById(R.id.editNote2);
        mLayClient = view.findViewById(R.id.clientLayout);
        mLayDate = view.findViewById(R.id.dateLayout);
        mLayTime = view.findViewById(R.id.timeLayout);
        mSave = view.findViewById(R.id.saveBtn2);
        mDelete = view.findViewById(R.id.deleteBtn);
        mCancel = view.findViewById(R.id.cancelBtn2);

        //If in 'EDIT' mode
        if(addOrEdit.equals("edit")) {

            theAppointment = allAppointsFragment.appointListClone.get(pos);
            tempClient = theAppointment.getClient();
            tempCal = theAppointment.getDate();

            //Pre-complete the text fields
            mClient.setText(theAppointment.getClient().getName());
            int year = theAppointment.getDate().get(Calendar.YEAR);
            int month = theAppointment.getDate().get(Calendar.MONTH) + 1;
            int day = theAppointment.getDate().get(Calendar.DAY_OF_MONTH);
            String date = day + "/" + month + "/" + year;
            mDate.setText(date);
            String time = String.format(Locale.getDefault(), "%02d:%02d", tempCal.get(Calendar.HOUR_OF_DAY), tempCal.get(Calendar.MINUTE));
            mTime.setText(time);
            mNote.setText(theAppointment.getNote());
        }
        //If in 'ADD' mode
        else {

            //Make new appointment entity
            theAppointment = new appointmentEntity();

            //Also make the 'Delete' button invisible
            mDelete.setVisibility(View.GONE);
        }

        //Here set click Listeners to the editTexts so that corresponding dialogs pop up when tapped
        mClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), clientPage.class);
                i.putExtra(clientPage.COMING_FROM_EXTRA, "editAppoint");
                i.putExtra(clientPage.WHAT_TO_SHOW, "all");
                startActivityForResult(i, 1);
            }
        });

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dialog;
                int year, month, day;
                Calendar cal;

                //If 'edit' mode, set the pre-defined date to the dialog
                if(addOrEdit.equals("edit")) {

                    cal = theAppointment.getDate();
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH);
                    day = cal.get(Calendar.DAY_OF_MONTH);
                }
                //Else, set current date to the dialog
                 else {

                    cal = Calendar.getInstance();
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH);
                    day = cal.get(Calendar.DAY_OF_MONTH);
                }

                 //Initialize datePickerDialog and show it (past dates are disabled)
                dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_DeviceDefault_Dialog_MinWidth, mDateSetListener, year, month, day);
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                //When the user chooses date and presses OK, set the EditText's text and save the chosen date to the object beneath
                tempCal.set(Calendar.DAY_OF_MONTH, day);
                tempCal.set(Calendar.MONTH, month);
                tempCal.set(Calendar.YEAR, year);
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                mDate.setText(date);
            }
        };

        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog dialog;
                int hours, minutes;
                Calendar cal;

                //If 'edit' mode, set the pre-defined date to the dialog
                if(addOrEdit.equals("edit")) {

                    cal = theAppointment.getDate();
                    hours = cal.get(Calendar.HOUR_OF_DAY);
                    minutes = cal.get(Calendar.MINUTE);
                }
                //Else, set current date to the dialog
                else {

                    cal = Calendar.getInstance();
                    hours = cal.get(Calendar.HOUR_OF_DAY);
                    minutes = cal.get(Calendar.MINUTE);
                }
                dialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog, mTimeSetListener, hours, minutes, true);
                dialog.show();
            }
        });

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                //When the user chooses time and presses OK, set the EditText's text and save the chosen time to the object beneath
                String time = String.format(Locale.getDefault(),  "%02d:%02d", hour, minute);
                mTime.setText(time);
                tempCal.set(Calendar.HOUR_OF_DAY, hour);
                tempCal.set(Calendar.MINUTE, minute);
            }
        };

        //Set buttons functionality
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check if all conditions are met!
                boolean flag = checkAllConditions();

                //If all necessary fields are OK
                if(flag) {

                    //Save all the inputs to a new appointmentEntity
                    if(addOrEdit.equals("add")) {

                        //Add Appointment info
                        Calendar cloneCal = (Calendar) tempCal.clone();
                        theAppointment.setAppointID(allAppointsFragment.appointIDGenerator++);
                        theAppointment.setDate(cloneCal);
                        theAppointment.setClient(tempClient);
                        theAppointment.setNote(mNote.getText().toString());
                        theAppointment.setIsPast(false);

                        //Add new appointment to the global list of appointments
                        allAppointsFragment.appointList.add(theAppointment);

                        //Set last came date to the client of the appointment
                        theAppointment.getClient().setLastCame(cloneCal);

                        //Put the ones that are of past date in the end of the list
                        (new SplashActivity()).reorderAppoints();

                        //Keep the global list size under 500
                        if(allAppointsFragment.appointList.size() > allAppointsFragment.MAX_APPOINT_LIST_SIZE) {

                            allAppointsFragment.appointList.remove(allAppointsFragment.MAX_APPOINT_LIST_SIZE);
                        }

                        //Schedule a notification to ring 5 minutes before the appointment
                        int fiveMinsInMillis = 1000 * 60 * 5;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            scheduleOrCancelNotification(getNotification(theAppointment.getClient().getName(), getActivity()), theAppointment.getDate().getTimeInMillis() - fiveMinsInMillis, false, getActivity());
                        }

                        //Confirmation Toast
                        Toast.makeText(getActivity(), "New Appointment Added!", Toast.LENGTH_SHORT).show();

                        //Reset the EditText fields
                        mClient.setText("");
                        mClient.setHint("Tap to choose client");
                        mDate.setText("");
                        mDate.setHint("Tap to choose date");
                        mTime.setText("");
                        mTime.setHint("Tap to choose time");
                        mNote.setText("");
                        mNote.setHint("Enter note");
                        mLayClient.setErrorEnabled(false);
                        mLayDate.setErrorEnabled(false);
                        mLayTime.setErrorEnabled(false);

                        //Create new entity
                        theAppointment = new appointmentEntity();
                    }
                    //Edit existing appointment
                    else if(addOrEdit.equals("edit")) {

                        //Edit appointment info
                        Calendar cloneCal = (Calendar) tempCal.clone();
                        theAppointment.setDate(cloneCal);
                        theAppointment.setClient(tempClient);
                        theAppointment.setNote(mNote.getText().toString());
                        theAppointment.setIsPast(false);

                        //Sort and put the ones that are of past date in the end of the list
                        (new SplashActivity()).reorderAppoints();

                        //Re-schedule the notification to ring 5 minutes before the edited appointment
                        int fiveMinsInMillis = 1000 * 60 * 5;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            scheduleOrCancelNotification(getNotification(theAppointment.getClient().getName(), getActivity()), theAppointment.getDate().getTimeInMillis() - fiveMinsInMillis, false, getActivity());
                        }

                        //Confirmation Toast
                        Toast.makeText(getActivity(), "Appointment Info Edited!", Toast.LENGTH_SHORT).show();

                        //Go to the "refreshed" appointment's info page
                        ((appointmentPage) getActivity()).changeFragments(appointmentPage.GO_TO_INFO, pos);
                    }
                }

            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Prompt confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Hair RSV Manager");
                builder.setMessage("Do you really want to delete this appointment?");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        properlyRemoveAppointment(getContext(), pos);
                    }
                });
                builder.show();

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //If 'Add' mode, simply return to all clients page
                if(addOrEdit.equals("add")) {

                    ((appointmentPage) getActivity()).changeFragments(appointmentPage.GO_TO_ALL, -1);
                }
                //Go back to client info page
                else if(addOrEdit.equals("edit")) {

                    ((appointmentPage) getActivity()).changeFragments(appointmentPage.GO_TO_INFO, pos);
                }

            }
        });

    }

    /** Checks all conditions of the inputs before saving the new client info and prompts corresponding error messages in case of something wrong **/
    private boolean checkAllConditions() {

        boolean flag = true;

        //Condition for client
        if( mClient.getText().toString().equals("") ) {

            mLayClient.setError("Enter a client");
            mLayClient.setErrorEnabled(true);
            flag = false;
        }
        else {
            //Reset error labeling
            mLayClient.setErrorEnabled(false);
        }

        //Condition for date
        if( mDate.getText().toString().equals("") ) {

            mLayDate.setError("Enter a date");
            mLayDate.setErrorEnabled(true);
            flag = false;
        }
        else {
            //Reset error labeling
            mLayDate.setErrorEnabled(false);
        }

        //Condition for time
        if( mTime.getText().toString().equals("") ) {

            mLayTime.setError("Enter time");
            mLayTime.setErrorEnabled(true);
            flag = false;
        }
        else {
            //Reset error labeling
            mLayTime.setErrorEnabled(false);
        }

        return flag;
    }

    /** Properly removes the chosen client
     * @param con Context of application
     * @param position The position of the appointment to be removed in the global list
     * **/
    public void properlyRemoveAppointment(Context con, int position) {

        theAppointment = allAppointsFragment.appointListClone.get(position);

        //Firstly cancel the notification alarm previously set
        scheduleOrCancelNotification(getNotification("something", con), 0, true, con);

        //Remove the appointment from the global list
        allAppointsFragment.appointList.remove(theAppointment);

        //Show confirmation Toast
        Toast.makeText(con, "Appointment successfully deleted!", Toast.LENGTH_SHORT).show();

        //Finally return to all appointments page
        ((appointmentPage) con).changeFragments(appointmentPage.GO_TO_ALL, -1);
    }

    /** Schedules a notification to show on the phone five minutes before the appointment time
     * @param notification A Notification object setup with the necessary values
     * @param futureInMillis The time in millis that the notification will show up
     * **/
    private void scheduleOrCancelNotification(Notification notification, long futureInMillis, boolean cancel, Context context) {

        //Setup the pending intent for the notification
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        //Each notification must have a unique ID, so use the appointmentID
        final int intentID = theAppointment.getAppointID();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, intentID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Get the alarm manager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //Set the alarm to fire up in set time
        if(!cancel) {

            alarmManager.set(AlarmManager.RTC, futureInMillis, pendingIntent);
        }
        //Cancel the previously set alarm
        else {

            alarmManager.cancel(pendingIntent);
        }
    }

    /** A custom notification builder
     * @param content The notification description to show up
     * **/
    private Notification getNotification(String content, Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("Appointment Reminder");
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher_foreground);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(App.CHANNEL_1_ID);
        }
        return builder.build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1) {

            if(resultCode == Activity.RESULT_OK){

                int pos = data.getIntExtra("result", 0);
                tempClient = allClientsFragment.clListClone.get(pos);
                mClient.setText(tempClient.getName());
            }
            if (resultCode == Activity.RESULT_CANCELED) {

                //Nothing for time being...
            }
        }
    }
}
