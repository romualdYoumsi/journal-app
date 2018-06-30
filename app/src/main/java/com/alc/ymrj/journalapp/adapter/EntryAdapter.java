package com.alc.ymrj.journalapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alc.ymrj.journalapp.R;
import com.alc.ymrj.journalapp.model.Entry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by netserve on 27/06/2018.
 */

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryAdapterViewHolder> {

    private final Context mContext;

    private List<Entry> mListEntries;

    final private EntryAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface EntryAdapterOnClickHandler {
        void onClick(Entry entry);
    }

    public EntryAdapter(@NonNull Context context, EntryAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mListEntries = new ArrayList<>();
    }

    @Override
    public EntryAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.entry_list_item, viewGroup, false);

        view.setFocusable(true);

        return new EntryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EntryAdapterViewHolder entryAdapterViewHolder, int position) {
        Entry itemEntry = mListEntries.get(position);

        /****************
         * Entry title *
         ****************/
        entryAdapterViewHolder.titleView.setText(itemEntry.title);

        /***********************
         * Entry content *
         ***********************/
        entryAdapterViewHolder.contentView.setText(itemEntry.content);

        /**************************
         * Entry hour and day *
         **************************/
        SimpleDateFormat dayformat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat hourformat = new SimpleDateFormat("HH:mm");
        Date date = new Date(itemEntry.date);

        entryAdapterViewHolder.dayView.setText(dayformat.format(date));
        entryAdapterViewHolder.hourView.setText(hourformat.format(date));

    }

    @Override
    public int getItemCount() {
        if (null == mListEntries) return 0;
        return mListEntries.size();
    }

    public void swap(List<Entry> newEntries) {
        mListEntries = newEntries;
        notifyDataSetChanged();
    }

    public void swapItem(Entry newEntry) {
        mListEntries.add(newEntry);
        notifyDataSetChanged();
    }

    class EntryAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView hourView;
        final TextView dayView;
        final TextView contentView;
        final TextView titleView;

        EntryAdapterViewHolder(View view) {
            super(view);

            hourView = (TextView) view.findViewById(R.id.entry_hour);
            dayView = (TextView) view.findViewById(R.id.entry_day);
            contentView = (TextView) view.findViewById(R.id.entry_content);
            titleView = (TextView) view.findViewById(R.id.entry_title);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            mClickHandler.onClick(mListEntries.get(adapterPosition));
        }
    }
}
