package com.alc.ymrj.journalapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alc.ymrj.journalapp.model.Entry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddEntryActivity extends AppCompatActivity {
    private static final String TAG = AddEntryActivity.class.getSimpleName();

    private Button mBtnSaveEntry;
    private EditText mETEntryTitle;
    private EditText mETEntryContent;

    //    Update entry actions
    private LinearLayout mUpdateActions;
    private TextView mTvUpdateDateEntry;
    private Button mBtnUpdateEntry;

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;

    private Entry mEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

//        If no user is authentofcated, finish this activity
        if (mFirebaseUser == null) {
            finish();
        }

//        Log.e(TAG, "onCreate mFirebaseUser: getDisplayName=" + mFirebaseUser.getDisplayName() + " getEmail=" + mFirebaseUser.getEmail());


        mETEntryTitle = (EditText) findViewById(R.id.et_entry_title);
        mETEntryContent = (EditText) findViewById(R.id.et_entry_content);
        mBtnSaveEntry = (Button) findViewById(R.id.btn_save_entry);


//        Getting Entry pass in Extra
        Intent intent = getIntent();
        String firebaseKey = intent.getStringExtra("entry_firebaseKey");
        String title = intent.getStringExtra("entry_title");
        String content = intent.getStringExtra("entry_content");
        long dateEntry = intent.getLongExtra("entry_date", 0);

        Log.e(TAG, "onCreate: extra data | firebaseKey=" + firebaseKey + " title=" + title + " content=" + content + " dateEntry=" + dateEntry);
        if (firebaseKey != null) {
            mEntry = new Entry(title, content, dateEntry, firebaseKey);

            mUpdateActions = (LinearLayout) findViewById(R.id.update_entry_actions);
            mTvUpdateDateEntry = (TextView) findViewById(R.id.tv_date_last_edited);
            mBtnUpdateEntry = (Button) findViewById(R.id.btn_update_entry);

            hideSaveBtn();


//        Update entry click listener
            mBtnUpdateEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = mETEntryTitle.getText().toString();
                    String content = mETEntryContent.getText().toString();

                    if (title.equals("") && content.equals("")) {
                        Toast.makeText(AddEntryActivity.this, "Invalid Entry, please enter all fields.", Toast.LENGTH_SHORT).show();
                    }

                    Log.e(TAG, "mBtnUpdateEntry onClick: title=" + title + " content=" + content + " firebaseKey=" + mEntry.firebaseKey);
                    updateEntry(mEntry.firebaseKey, title, content);
                }
            });
        }

//        Save Entry clicked listener
        mBtnSaveEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mETEntryTitle.getText().toString();
                String content = mETEntryContent.getText().toString();

                if (title.equals("") && content.equals("")) {
                    Toast.makeText(AddEntryActivity.this, "Invalid Entry, please enter all fields.", Toast.LENGTH_SHORT).show();
                }

                Log.e(TAG, "mBtnSaveEntry onClick: title=" + title + " content=" + content);
                writeNewEntry(title, content);
            }
        });
    }

    private void hideSaveBtn() {
        mBtnSaveEntry.setVisibility(View.GONE);
        mUpdateActions.setVisibility(View.VISIBLE);

//        Set the date of last edited
        SimpleDateFormat dateformat = new SimpleDateFormat("MMMM dd, yyyy HH:mm");
        Date date = new Date(mEntry.date);

        mTvUpdateDateEntry.setText(dateformat.format(date));
        mETEntryTitle.setText(mEntry.title);
        mETEntryContent.setText(mEntry.content);
    }

    //    Write a new Entry on FirebaseDatabase '.', '#', '$', '[', or ']'
    private void writeNewEntry(String title, String content) {
        Date date = new Date();

//        Escape Firebase specials key
        String userKey = mFirebaseUser.getEmail().replace(".", ",")
                .replace("#", "*")
                .replace("$", "&")
                .replace("[", "(")
                .replace("]", ")");
        DatabaseReference databaseRefEntry = mFirebaseDatabase.getReference().child("" + userKey).push();

        Entry user = new Entry(title, content, date.getTime(), databaseRefEntry.getKey());
        databaseRefEntry.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddEntryActivity.this, "Entry Saved in your Journal.", Toast.LENGTH_LONG).show();
                mETEntryContent.setText("");
                mETEntryTitle.setText("");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddEntryActivity.this, "Failed to Save Entry in your Journal. Please try again later.", Toast.LENGTH_LONG).show();
                    }
                });

    }

    //    Update an Entry on FirebaseDatabase '.', '#', '$', '[', or ']'
    private void updateEntry(String firebaseEntryKey, String title, String content) {
        Date date = new Date();

//        Escape Firebase specials key
        String userKey = mFirebaseUser.getEmail().replace(".", ",")
                .replace("#", "*")
                .replace("$", "&")
                .replace("[", "(")
                .replace("]", ")");
        DatabaseReference databaseRefEntry = mFirebaseDatabase.getReference().child("" + userKey);

        Entry entry = new Entry(title, content, date.getTime(), firebaseEntryKey);
        Map<String, Object> entryValues = entry.toMap();

        Map<String, Object> entryUpdates = new HashMap<>();
        entryUpdates.put("/" + firebaseEntryKey, entryValues);

        databaseRefEntry.updateChildren(entryUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddEntryActivity.this, "Entry Updated successfully in your Journal.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddEntryActivity.this, "Failed to Update Entry in your Journal. Please try again later.", Toast.LENGTH_LONG).show();
                    }
                });

    }

}
