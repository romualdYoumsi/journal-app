package com.alc.ymrj.journalapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alc.ymrj.journalapp.fragment.EntriesFragment;
import com.alc.ymrj.journalapp.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity implements EntriesFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener {

    private FrameLayout mainContent;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_entries:
                    inflateEntriesFragment();
                    return true;
                case R.id.navigation_profile:
                    inflateProfileFragment();
                    return true;
//                case R.id.navigation_dashboard:
//                    mTextMessage.setText(R.string.title_dashboard);
//                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        inflateEntriesFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Get the ID of the clicked item */
        int id = item.getItemId();

        /* Add Entry menu item clicked */
        if (id == R.id.action_add_entry) {
            startActivity(new Intent(MainActivity.this, AddEntryActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void inflateEntriesFragment() {
        EntriesFragment entriesFragment = new EntriesFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, entriesFragment).commit();

    }

    private void inflateProfileFragment() {
        ProfileFragment profileFragment = new ProfileFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, profileFragment).commit();

    }

}
