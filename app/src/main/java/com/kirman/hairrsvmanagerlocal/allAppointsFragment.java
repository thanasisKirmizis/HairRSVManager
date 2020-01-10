package com.kirman.hairrsvmanagerlocal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class allAppointsFragment extends Fragment implements appointListAdapter.OnItemSelectedListener {

    public static List<appointmentEntity> appointList = new ArrayList<>();
    public static List<appointmentEntity> appointListClone = new ArrayList<>();
    public static int appointIDGenerator = 0;
    public static int MAX_APPOINT_LIST_SIZE = 500;

    //Used in filtering
    private String filter = "";
    private int passedID;

    private FloatingActionButton mAddAppointBtn;
    private RecyclerView recyclerView;
    private TextView mDescription;
    private appointListAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Every time on create, clear appointListClone
        appointListClone.clear();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Retrieve arguments passed to Fragment
        if (getArguments() != null) {
            filter = getArguments().getString(appointmentPage.APPOINT_FILTER);
            passedID = getArguments().getInt(appointmentPage.APPOINT_CLIENT_ID);
        }

        return inflater.inflate(R.layout.appointment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //Bind UI Elements to code
        mAddAppointBtn = view.findViewById(R.id.addAppointFAB);
        recyclerView = view.findViewById(R.id.appointments_recycler);
        mDescription = view.findViewById(R.id.description);

        //Base activity to use as context
        Activity base = Objects.requireNonNull(getActivity());

        //Decide whether to use filter or not
        switch (filter) {

            case "nofilter":

                appointListClone.addAll(appointList);
                mDescription.setText("All Appointments");
                break;

            case "today":

                mDescription.setText("Today's Appointments");

                //Fill todaysList
                for (int i = 0; i < appointList.size(); i++) {

                    if (DateUtils.isToday(appointList.get(i).getDate().getTimeInMillis())) {

                        appointListClone.add(appointList.get(i));
                    }
                }
                break;

            case "tomorrow":

                mDescription.setText("Tomorrow's Appointments");

                //Fill tomorrowsList
                for (int i = 0; i < appointList.size(); i++) {

                    if (DateUtils.isToday(appointList.get(i).getDate().getTimeInMillis() - DateUtils.DAY_IN_MILLIS)) {

                        appointListClone.add(appointList.get(i));
                    }
                }
                break;

            case "client":

                mDescription.setText("Specific Client's Appointments");

                //Fill specific client's list
                for (int i = 0; i < appointList.size(); i++) {

                    if (appointList.get(i).getClient().getClientID() == passedID) {

                        appointListClone.add(appointList.get(i));
                    }
                }
                break;
        }
        mAdapter = new appointListAdapter(appointListClone, this, base);

        //Set the RecyclerView adapter
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(base.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(base, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        //FAB item functionality
        mAddAppointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Go to add new client page by passing the value -1 as extra
                ((appointmentPage) getActivity()).changeFragments(appointmentPage.GO_TO_ADD, -1);
            }
        });

        mAdapter.notifyDataSetChanged();
    }

    /** Overrides the listener for selection of an item from the recycler view
     * @param pos The position of the item in the recycler view
     * **/
    @Override
    public void onSelected(int pos) {

        //Go to selected appointment page by passing appointment's position in the ArrayList
        ((appointmentPage) getActivity()).changeFragments(appointmentPage.GO_TO_INFO, pos);
    }

    /** Overrides the listener for selection of an item from the Context Menu (long click)
     * @param appPos The position of the item in the recycler view that was long clicked to display the Context Menu
     * @param item The context menu item that is being selected
     * **/
    @Override
    public void onMenuAction(final int appPos, MenuItem item) {
        switch (item.getItemId()) {

            //Go to edit screen
            case R.id.menu_custom_edit:

                //'Return' to the base activity and tell it to replace this fragment with the next one
                ((appointmentPage) getActivity()).changeFragments(appointmentPage.GO_TO_EDIT, appPos);
                break;

            //Do a proper delete
            case R.id.menu_custom_delete:

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

                        (new addEditAppointFragment()).properlyRemoveAppointment(getContext(), appPos);
                    }
                });
                builder.show();
                break;
        }
    }

}
