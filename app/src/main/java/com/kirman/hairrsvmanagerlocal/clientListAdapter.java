package com.kirman.hairrsvmanagerlocal;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class clientListAdapter extends RecyclerView.Adapter<clientListAdapter.MyViewHolder> implements Filterable {

    private List<clientEntity> mClientEntityList;
    private List<clientEntity> mClientEntityListFull;
    private clientListAdapter.OnItemSelectedListener listener;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnCreateContextMenuListener, PopupMenu.OnMenuItemClickListener {

        public TextView textView;
        public TextView textView2;

        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.row_client_name);
            textView2 = v.findViewById(R.id.row_client_phone);

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

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (listener != null) {
                int clPos = getAdapterPosition();
                listener.onMenuAction(clPos, item);
            }
            return false;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public clientListAdapter(List<clientEntity> clientEntityList, clientListAdapter.OnItemSelectedListener listener, Context context) {
        this.listener = listener;
        this.mClientEntityList = clientEntityList;
        mClientEntityListFull = new ArrayList<>(mClientEntityList);
        this.mContext = context;
    }

    //The interface to be implemented for the selections of the items and the menu items
    public interface OnItemSelectedListener {

        void onSelected(int pos);

        void onMenuAction(int clPos, MenuItem item);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public clientListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.client_row, parent, false);

        return new MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        clientEntity cl = mClientEntityList.get(position);
        holder.textView.setText(cl.getName());
        holder.textView2.setText(cl.getPhone());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mClientEntityList.size();
    }

    //This is where the filtering is done in case of searching for a client
    @Override
    public Filter getFilter() {
        return clientFilter;
    }

    private Filter clientFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<clientEntity> filteredList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0) {

                filteredList.addAll(mClientEntityListFull);
            }
            else {

                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(clientEntity item : mClientEntityListFull) {

                    //If input is phone number, compare to phones of clients
                    if(PhoneNumberUtils.isGlobalPhoneNumber(filterPattern)) {

                        if(item.getPhone().toLowerCase().contains(filterPattern)) {

                            filteredList.add(item);
                        }
                    }
                    //Else compare to names
                    else {

                        if(item.getName().toLowerCase().contains(filterPattern)) {

                            filteredList.add(item);
                        }
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            mClientEntityList.clear();
            mClientEntityList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
