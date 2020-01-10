package com.kirman.hairrsvmanagerlocal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class allClientsFragment extends Fragment implements clientListAdapter.OnItemSelectedListener {

    public static List<clientEntity> clList = new ArrayList<>();
    public static List<clientEntity> clListClone = new ArrayList<>();
    public static int clientIDGenerator = 0;

    private FloatingActionButton mAddClientBtn;
    private RecyclerView recyclerView;
    private SearchView mSearchView;
    private ConstraintLayout mSearchConstr;
    private clientListAdapter mAdapter;
    private String whereFrom;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Here reset clListClone with all the elements of the original list
        clListClone.clear();
        clListClone.addAll(clList);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.client_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //Retrieve arguments passed to Fragment
        Bundle gimme = getArguments();
        if (gimme != null) {

            whereFrom = gimme.getString(clientPage.COMING_FROM_EXTRA);
        }

        //Bind UI Elements to code
        mAddClientBtn = view.findViewById(R.id.addClientFAB);
        recyclerView = view.findViewById(R.id.clients_recycler);
        mSearchConstr = view.findViewById(R.id.searchLayout);
        mSearchView = view.findViewById(R.id.clientsSearchView);

        //Base activity to use as context
        Activity base = Objects.requireNonNull(getActivity());

        //Set the RecyclerView adapter
        mAdapter = new clientListAdapter(clListClone, this, base);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(base.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(base, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        //FAB item functionality
        mAddClientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Go to add new client page by passing the value -1 as extra
                ((clientPage) getActivity()).changeFragments(clientPage.GO_TO_ADD, -1);
            }
        });

        //Search container click functionality
        mSearchConstr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSearchView.setIconified(false);
            }
        });

        //Search functionality
        int searchIconId = mSearchView.getContext().getResources().getIdentifier("android:id/search_button",null, null);
        ImageView icon = mSearchView.findViewById(searchIconId);
        icon.setColorFilter(R.color.colorPrimaryDark);

        //Search functionality
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                mAdapter.getFilter().filter(s);
                return false;
            }
        });

        //If coming from 'Search' button on home page, automatically expand SearchView
         if(whereFrom.equals("search")) {

            mSearchView.setIconified(false);
        }

        mAdapter.notifyDataSetChanged();
    }

    /** Overrides the listener for selection of an item from the recycler view
     * @param pos The position of the item in the recycler view
     * **/
    @Override
    public void onSelected(int pos) {

        //If want to just see client info
        if(whereFrom.equals("footer") || whereFrom.equals("search")) {

            //Go to selected client page by passing client's position in the ArrayList
            ((clientPage) getActivity()).changeFragments(clientPage.GO_TO_INFO, pos);
        }
        //If want to add client to an appointment
        else if(whereFrom.equals("editAppoint")) {

            //Return the activity result
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", pos);
            getActivity().setResult(Activity.RESULT_OK, returnIntent);
            getActivity().finish();
        }
    }

    /** Overrides the listener for selection of an item from the Context Menu (long click)
     * @param clPos The position of the item in the recycler view that was long clicked to display the Context Menu
     * @param item The context menu item that is being selected
     * **/
    @Override
    public void onMenuAction(final int clPos, MenuItem item) {
        switch (item.getItemId()) {

            //Go to edit screen
            case R.id.menu_custom_edit:

                //'Return' to the base activity and tell it to replace this fragment with the next one
                ((clientPage) getActivity()).changeFragments(clientPage.GO_TO_EDIT, clPos);
                break;

            //Do a proper delete
            case R.id.menu_custom_delete:

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

                        (new addEditClientFragment()).properlyRemoveClient(getContext(), clPos);
                    }
                });
                builder.show();
                break;
        }
    }

}
