package com.kirman.hairrsvmanagerlocal;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class appointListAdapter extends RecyclerView.Adapter<appointListAdapter.MyViewHolder> {

    private List<appointmentEntity> mAppointEntityList;
    private appointListAdapter.OnItemSelectedListener listener;
    private Context mContext;
    private int anAppointIndex;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnCreateContextMenuListener, PopupMenu.OnMenuItemClickListener {

        public TextView textView1;
        public TextView textView2;
        public ConstraintLayout greenBox;

        public MyViewHolder(View v) {
            super(v);
            textView1 = v.findViewById(R.id.row_appoint_date_time);
            textView2 = v.findViewById(R.id.row_appoint_client);
            greenBox = v.findViewById(R.id.greenBox);

            //This is just to get moving text
            textView2.setSelected(true);

            v.setOnClickListener(this);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (listener != null) {
                listener.onSelected(position);
            }
        }

        //This function takes care of creation of context menu on long click of a client
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

            PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.getMenuInflater().inflate(R.menu.custom_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }

        //This function takes care of click of an item in the context menu
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (listener != null) {
                int appPos = getAdapterPosition();
                listener.onMenuAction(appPos, item);
            }
            return false;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public appointListAdapter(List<appointmentEntity> appointEntityList, appointListAdapter.OnItemSelectedListener listener, Context context) {
        this.listener = listener;
        this.mAppointEntityList = appointEntityList;
        this.mContext = context;

        anAppointIndex = 0;

        if(allAppointsFragment.appointList.size() > 0) {

            //To find which appointment is next, start from the beginning of the list and find the first that is placed after 'now'
            long nowInMillis = Calendar.getInstance().getTimeInMillis();
            appointmentEntity nextAppoint = allAppointsFragment.appointList.get(anAppointIndex);

            //When this loops finishes, the anAppointIndex will hold the position of the next Appointment in the global list
            while (nextAppoint.getDate().getTimeInMillis() < nowInMillis) {

                anAppointIndex++;

                //If no more future appointments on the list, break the loop
                if(anAppointIndex == allAppointsFragment.appointList.size()) {

                    break;
                }
                else {

                    nextAppoint = allAppointsFragment.appointList.get(anAppointIndex);
                }
            }
        }
    }

    //The interface to be implemented for the selections of the items and the menu items
    public interface OnItemSelectedListener {

        void onSelected(int pos);

        void onMenuAction(int appPos, MenuItem item);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public appointListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.appointment_row, parent, false);

        return new MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        appointmentEntity appoint = mAppointEntityList.get(position);

        int year = appoint.getDate().get(Calendar.YEAR);
        int month = appoint.getDate().get(Calendar.MONTH) + 1;
        int day = appoint.getDate().get(Calendar.DAY_OF_MONTH);

        String date = day + "/" + month + "/" + year;
        String time = String.format(Locale.getDefault(), "%02d:%02d", appoint.getDate().get(Calendar.HOUR_OF_DAY), appoint.getDate().get(Calendar.MINUTE));
        String dateAndTime = date + " | " + time;
        holder.textView1.setText(dateAndTime);

        holder.textView2.setText(appoint.getClient().getName());

        //If binding view holder on the row of the next appointment, display a nice green box around the row (only works for All Appointments List)
        int diff = allAppointsFragment.appointList.size() - mAppointEntityList.size();
        if(diff == 0 && position == anAppointIndex) {

            holder.greenBox.setVisibility(View.VISIBLE);
        }

        //If binding view holder on the row of a past appointment, make the text gray
        if(appoint.getIsPast()) {

            holder.textView1.setTextColor(mContext.getResources().getColor(R.color.gray));
            holder.textView2.setTextColor(mContext.getResources().getColor(R.color.gray));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mAppointEntityList.size();
    }



}
