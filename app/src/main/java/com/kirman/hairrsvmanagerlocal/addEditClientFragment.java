package com.kirman.hairrsvmanagerlocal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;


public class addEditClientFragment extends Fragment {

    //UI Elements
    private EditText mClientName;
    private EditText mClientPhone;
    private EditText mTimesFree;
    private EditText mNote;
    private TextInputLayout mLayName;
    private TextInputLayout mLayPhone;
    private TextInputLayout mLayFree;
    private Button mSave;
    private Button mDelete;
    private Button mCancel;

    private String addOrEdit;
    private String whereFrom;
    private int pos;
    private clientEntity theClient;

    public addEditClientFragment() {

        // Required empty public constructor
         addOrEdit = "add";
         pos = 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit_client, container, false);
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
            addOrEdit = gimme.getString(clientPage.ADD_OR_EDIT_EXTRA);
            pos = gimme.getInt(clientPage.CLIENT_POSITION_EXTRA);
            whereFrom = gimme.getString(clientPage.COMING_FROM_EXTRA);
        }

        //Bind UI Elements to Code
        mClientName = view.findViewById(R.id.editClient);
        mClientPhone = view.findViewById(R.id.editDate);
        mTimesFree = view.findViewById(R.id.editTime);
        mNote = view.findViewById(R.id.editNote2);
        mLayName = view.findViewById(R.id.clientLayout);
        mLayPhone = view.findViewById(R.id.dateLayout);
        mLayFree = view.findViewById(R.id.timeLayout);
        mSave = view.findViewById(R.id.saveBtn2);
        mDelete = view.findViewById(R.id.deleteBtn);
        mCancel = view.findViewById(R.id.cancelBtn2);

        //If in 'EDIT' mode
        if(addOrEdit.equals("edit")) {

            //Pre-complete the text fields
            theClient = allClientsFragment.clListClone.get(pos);
            mClientName.setText(theClient.getName());
            mClientPhone.setText(theClient.getPhone());
            mTimesFree.setText(String.valueOf(theClient.getTimesFree()));
            mNote.setText(theClient.getNote());

        }
        //If in 'ADD' mode
        else {

            //Make new client entity
            theClient = new clientEntity();

            //Also make the 'Delete' button invisible
            mDelete.setVisibility(View.GONE);
        }

        //Set buttons functionality
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check if all conditions are met!
                boolean flag = checkAllConditions();

                //If all necessary fields are OK
                if(flag) {

                    //Save all the inputs to a new clientEntity
                    if(addOrEdit.equals("add")) {

                        //Add client info
                        theClient.setClientID(allClientsFragment.clientIDGenerator++);
                        theClient.setName(mClientName.getText().toString());
                        theClient.setPhone(mClientPhone.getText().toString());
                        theClient.setTimesFree(Integer.valueOf(mTimesFree.getText().toString()));
                        theClient.setNote(mNote.getText().toString());
                        theClient.setFirstAdded(Calendar.getInstance());
                        theClient.setLastCame(null);

                        //Add new client to the global list of clients
                        allClientsFragment.clList.add(theClient);

                        //Re-sort the list alphabetically
                        Collections.sort(allClientsFragment.clList, new Comparator<clientEntity>() {
                            @Override
                            public int compare(clientEntity t1, clientEntity t2) {

                                String name1 = t1.getName();
                                String name2 = t2.getName();

                                return name1.compareToIgnoreCase(name2);
                            }
                        });

                        //Also add new client to the clone list because it may be returned on appointment activity
                        allClientsFragment.clListClone.add(theClient);

                        //Re-sort the clone list alphabetically
                        Collections.sort(allClientsFragment.clListClone, new Comparator<clientEntity>() {
                            @Override
                            public int compare(clientEntity t1, clientEntity t2) {

                                String name1 = t1.getName();
                                String name2 = t2.getName();

                                return name1.compareToIgnoreCase(name2);
                            }
                        });

                        //Confirmation Toast
                        Toast.makeText(getActivity(), "New Client Added!", Toast.LENGTH_SHORT).show();

                        //Reset the EditText fields
                        mClientName.setText("");
                        mClientName.setHint("Name");
                        mClientPhone.setText("");
                        mClientPhone.setHint("Phone");
                        mTimesFree.setText("");
                        mTimesFree.setHint("0...4");
                        mNote.setText("");
                        mNote.setHint("Enter note");
                        mLayName.setErrorEnabled(false);
                        mLayPhone.setErrorEnabled(false);
                        mLayFree.setErrorEnabled(false);

                        //If coming from an appointment editor
                        if(whereFrom.equals("editAppoint")) {

                            //Return the activity result
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result", allClientsFragment.clListClone.indexOf(theClient));
                            getActivity().setResult(Activity.RESULT_OK, returnIntent);
                            getActivity().finish();
                        }

                        //Create new entity
                        theClient = new clientEntity();
                    }
                    //Edit existing client
                    else if(addOrEdit.equals("edit")) {

                        //Edit client info
                        theClient.setName(mClientName.getText().toString());
                        theClient.setPhone(mClientPhone.getText().toString());
                        theClient.setTimesFree(Integer.valueOf(mTimesFree.getText().toString()));
                        theClient.setNote(mNote.getText().toString());

                        //Re-sort the list alphabetically
                        Collections.sort(allClientsFragment.clList, new Comparator<clientEntity>() {
                            @Override
                            public int compare(clientEntity t1, clientEntity t2) {

                                String name1 = t1.getName();
                                String name2 = t2.getName();

                                return name1.compareToIgnoreCase(name2);
                            }
                        });

                        //Confirmation Toast
                        Toast.makeText(getActivity(), "Client Info Edited!", Toast.LENGTH_SHORT).show();

                        //Go to the "refreshed" client's info page
                        ((clientPage) getActivity()).changeFragments(clientPage.GO_TO_INFO, pos);
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
                builder.setMessage("Do you really want to delete this client?");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        properlyRemoveClient(getContext(), pos);
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

                    ((clientPage) getActivity()).changeFragments(clientPage.GO_TO_ALL, -1);
                }
                //Go back to client info page
                else if(addOrEdit.equals("edit")) {

                    ((clientPage) getActivity()).changeFragments(clientPage.GO_TO_INFO, pos);
                }

            }
        });

    }

    /** Checks all conditions of the inputs before saving the new client info and prompts corresponding error messages in case of something wrong **/
    private boolean checkAllConditions() {

        boolean flag = true;

        String newName = mClientName.getText().toString();
        String newPhone = mClientPhone.getText().toString();
        String newFree = mTimesFree.getText().toString();

        //Conditions for name
        if( newName.equals("") ) {

            mLayName.setError("Enter a name");
            mLayName.setErrorEnabled(true);
            flag = false;
        }
        else {

            //Reset error labeling
            mLayName.setErrorEnabled(false);
        }

        if(addOrEdit.equals("add")) {

            boolean innerFlag = true;

            for(int i=0; i<allClientsFragment.clList.size(); i++) {

                clientEntity client =  allClientsFragment.clList.get(i);

                if( newName.equals(client.getName())) {

                    mLayName.setError("Client already exists");
                    mLayName.setErrorEnabled(true);
                    flag = false;
                    innerFlag = false;
                    break;
                }
            }
            if(innerFlag) {

                //Reset error labeling
                mLayName.setErrorEnabled(false);
            }
        }

        //Condition for phone
        if( newPhone.equals("") ) {

            theClient.setPhone(" ");
        }

        //Conditions for free
        if( newFree.equals("") ) {

            mTimesFree.setText("0");
        }
        else if(Integer.valueOf(newFree) < 0 || Integer.valueOf(newFree) > 4) {

            mLayFree.setError("Enter a number between 0 and 4");
            mLayFree.setErrorEnabled(true);
            flag = false;
        }
        else {

            //Reset error labeling
            mLayFree.setErrorEnabled(false);
        }

        return flag;
    }

    /** Properly removes the chosen client
     * @param con Context of application
     * @param position The position of the client to be removed in the global list
     * **/
    public void properlyRemoveClient(Context con, int position) {

        //Remove all appointments from that client
        int initSize = allAppointsFragment.appointList.size();
        int clID = allClientsFragment.clListClone.get(position).getClientID();
        int counter = 0;
        for(int i=0; i<initSize; i++) {

            if(allAppointsFragment.appointList.get(counter).getClient().getClientID() == clID) {

                allAppointsFragment.appointList.remove(counter);
            }
            else {
                counter++;
            }
        }

        //Remove the client from the global list
        clientEntity clToRemove = allClientsFragment.clListClone.get(position);
        allClientsFragment.clList.remove(clToRemove);

        //Finally remove client from clone list too
        allClientsFragment.clListClone.remove(position);

        //Show confirmation Toast
        Toast.makeText(con, "Client successfully deleted!", Toast.LENGTH_SHORT).show();

        //Finally return to all clients page
        ((clientPage) con).changeFragments(clientPage.GO_TO_ALL, -1);
    }

}
