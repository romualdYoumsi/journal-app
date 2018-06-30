package com.alc.ymrj.journalapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alc.ymrj.journalapp.AddEntryActivity;
import com.alc.ymrj.journalapp.R;
import com.alc.ymrj.journalapp.adapter.EntryAdapter;
import com.alc.ymrj.journalapp.model.Entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EntriesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EntriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntriesFragment extends Fragment implements EntryAdapter.EntryAdapterOnClickHandler {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = EntriesFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EntryAdapter mEntriesAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    private ProgressBar mLoadingIndicator;

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabaseUser;
    private ValueEventListener mUserValueEntriesListener;
    private ChildEventListener mUserChildEntriesListener;
    private DatabaseReference mDatabaseRefUser;

    public EntriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EntriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EntriesFragment newInstance(String param1, String param2) {
        EntriesFragment fragment = new EntriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mFirebaseDatabaseUser = FirebaseDatabase.getInstance();

        if (mFirebaseUser == null){
            return;
        }

//        Escape Firebase specials key
        String key = mFirebaseUser.getEmail().replace(".", ",")
                .replace("#", "*")
                .replace("$", "&")
                .replace("[", "(")
                .replace("]", ")");
        mDatabaseRefUser = mFirebaseDatabaseUser.getReference().child(key);

        mUserValueEntriesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Entry> list = new ArrayList<>();

                for (DataSnapshot dataSnapshotItem : dataSnapshot.getChildren()) {

                    // A new comment has been added, add it to the displayed list
                    Entry entry = dataSnapshotItem.getValue(Entry.class);
                    Log.e(TAG, "loadPost:onDataChange item title=" + entry.title +
                            " content=" + entry.content +
                            " date=" + entry.date +
                            " firebaseKey=" + entry.firebaseKey +
                            " key=" + dataSnapshot.getKey());

//                SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
//                Date date = new Date(entry.date);
//                String stringEntry = entry.title+"\n"+entry.content+"\n\n"+dateformat.format(date)+"\n\n\n";

                    list.add(entry);
                }

                Collections.reverse(list);

                mEntriesAdapter.swap(list);
                if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
                mRecyclerView.smoothScrollToPosition(mPosition);
                if (list.size() != 0) showWeatherDataView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
//        Firebase child event listener
        mUserChildEntriesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                // A new comment has been added, add it to the displayed list
                Entry entry = dataSnapshot.getValue(Entry.class);
                Log.e(TAG, "loadPost:onChildAdded title=" + entry.title +
                        " content=" + entry.content +
                        " date=" + entry.date +
                        " firebaseKey=" + entry.firebaseKey +
                        " key=" + dataSnapshot.getKey());

//                SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
//                Date date = new Date(entry.date);
//                String stringEntry = entry.title+"\n"+entry.content+"\n\n"+dateformat.format(date)+"\n\n\n";


                mEntriesAdapter.swapItem(entry);
                if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
                mRecyclerView.smoothScrollToPosition(mPosition);
//                if (data.getCount() != 0) showWeatherDataView();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                Entry newComment = dataSnapshot.getValue(Entry.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                Entry movedComment = dataSnapshot.getValue(Entry.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(getContext(), "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        Get reference of xml view
        View view = inflater.inflate(R.layout.fragment_entries, container, false);

//        Reference of RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_entries);

//        Reference of ProgressBar
        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        mEntriesAdapter = new EntryAdapter(getContext(), this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mEntriesAdapter);

//        showLoading();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mDatabaseRefUser == null) return;
//        Getting entries order by date
        Query myTopPostsQuery = mDatabaseRefUser.orderByChild("date");
//        mDatabaseRefUser.addChildEventListener(mUserChildEntriesListener);
        myTopPostsQuery.addValueEventListener(mUserValueEntriesListener);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(Entry entry) {
        Log.e(TAG, "onClick: title=" + entry.title +
                " content=" + entry.content +
                " date=" + entry.date +
                " firebaseKey=" + entry.firebaseKey);

        Intent intent = new Intent(getContext(), AddEntryActivity.class);
        intent.putExtra("entry_firebaseKey", entry.firebaseKey);
        intent.putExtra("entry_title", entry.title);
        intent.putExtra("entry_content", entry.content);
        intent.putExtra("entry_date", entry.date);
        startActivity(intent);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void showLoading() {
        /* Then, hide the weather data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showWeatherDataView() {
        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Finally, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
