package com.example.vamshedhar.androidpos.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vamshedhar.androidpos.R;
import com.example.vamshedhar.androidpos.adapters.ItemListAdapter;
import com.example.vamshedhar.androidpos.fragments.CurrentOrdersFragment;
import com.example.vamshedhar.androidpos.fragments.CustomersFragment;
import com.example.vamshedhar.androidpos.fragments.ItemsFragment;
import com.example.vamshedhar.androidpos.fragments.OrderHistoryFragment;
import com.example.vamshedhar.androidpos.fragments.PastOrdersFragment;
import com.example.vamshedhar.androidpos.fragments.SellFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements CurrentOrdersFragment.OnFragmentInteractionListener, PastOrdersFragment.OnFragmentInteractionListener {

    public static final String TAG = "AndroidPOS";

    public static final String ITEMS_FRAGMENT = "ITEMS_FRAGMENT";
    public static final String SELL_FRAGMENT = "SELL_FRAGMENT";
    public static final String ORDER_HISTORY_FRAGMENT = "ORDER_HISTORY_FRAGMENT";
    public static final String CUSTOMERS_FRAGMENT = "CUSTOMERS_FRAGMENT";


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.left_navigation);
        welcomeText = findViewById(R.id.welcomeText);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (currentUser == null){
            goToLogin();
        }

        setWelcomeMessage();

        setupDrawer();
        loadNavigationListener();

        loadDefaultFragment();

    }

    private void setWelcomeMessage(){
        String username = currentUser.getEmail().substring(0, currentUser.getEmail().indexOf('@'));
        welcomeText.setText(getString(R.string.welcome) + " " + username.toUpperCase() + ",");
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void loadDefaultFragment(){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_container, new ItemsFragment(), MainActivity.ITEMS_FRAGMENT)
                .commit();
    }

    private void loadFragment(Fragment fragment, String FRAGMENT_TAG){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragment, FRAGMENT_TAG)
                .commit();
    }

    private void loadNavigationListener(){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.nav_logout:
                        mAuth.signOut();
                        Toast.makeText(MainActivity.this, "Successfully logged out!", Toast.LENGTH_SHORT).show();
                        goToLogin();
                        break;
                    case R.id.nav_items:
                        loadFragment(new ItemsFragment(), MainActivity.ITEMS_FRAGMENT);
                        break;
                    case R.id.nav_sell:
                        loadFragment(new SellFragment(), MainActivity.SELL_FRAGMENT);
                        break;
                    case R.id.nav_history:
                        loadFragment(new OrderHistoryFragment(), MainActivity.ORDER_HISTORY_FRAGMENT);
                        break;
                    case R.id.nav_customers:
                        loadFragment(new CustomersFragment(), MainActivity.CUSTOMERS_FRAGMENT);
                        break;
                }

                menuItem.setChecked(true);

                mDrawerLayout.closeDrawers();

                return false;
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_settings) {
//            return true;
//        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
