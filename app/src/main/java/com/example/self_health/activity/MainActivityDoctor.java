package com.example.self_health.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.self_health.R;
import com.example.self_health.fragment.AddPatients;
import com.example.self_health.fragment.AssessmentFragment;
import com.example.self_health.fragment.AssignTasks;
import com.example.self_health.fragment.DHomeFragment;
import com.example.self_health.fragment.DoctorFragment;
import com.example.self_health.fragment.HomeFragment;
import com.example.self_health.fragment.MeasurementFragment;
import com.example.self_health.fragment.ScheduleFragment;
import com.example.self_health.fragment.SettingsFragment;
import com.example.self_health.fragment.ViewAssessments;
import com.example.self_health.other.DatabaseHelperInformation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;

/**
 * Created by pc on 12/4/2016.
 */

public class MainActivityDoctor extends AppCompatActivity{
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_ADD_PATIENT = "addpatient";
    private static final String TAG_ASSIGN_TASKS= "assigntasks";
    private static final String TAG_ASSESSMENTS = "assessment";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;
    public GoogleApiClient mClient =null;
    private GoogleSignInOptions mgso = null;
    private static final int RC_SIGN_IN = 9001;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private String mId;
    private String mFamilyName;
    private  String mname;
    Uri mphoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_doctor);

        //get info
        Intent i = getIntent();
        mname = i.getExtras().get("personName").toString();
        mFamilyName = i.getExtras().get("personFamilyName").toString();
        mId = i.getExtras().get("ID").toString();
        if(i.getExtras().get("uri") != null) {
            mphoto = Uri.parse(i.getExtras().get("uri").toString());
        }


        //for sign out
        mgso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.FITNESS_LOCATION_READ))
                .requestScopes(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .requestScopes(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .requestScopes(new Scope(Scopes.FITNESS_NUTRITION_READ_WRITE))
                .requestEmail()
                .build();

        //Connect to google API
        buildClient();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_doc);
        navigationView = (NavigationView) findViewById(R.id.nav_view_doc);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);


        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles_doc);

        fab.setImageResource(R.drawable.search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Check Symptoms", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                String url = "http://symptoms.webmd.com/symptomchecker";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);


            }
        });

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        txtName.setText(mname);





        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        // This ensures that if the user denies the permissions then uses Settings to re-enable
        // them, the app will start working.
        buildClient();
    }
    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Bundle bundle = new Bundle();
                bundle.putString("Familyname", mFamilyName);
                bundle.putString("Name", mname);
                bundle.putString("ID", mId);

                Fragment fragment = getHomeFragment();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();

            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // TODO:maybe change
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // add patient fragment
                AssignTasks assignFragment = new AssignTasks();
                return assignFragment;
            case 2:
                // photos
                AddPatients addFragment = new AddPatients();
                return addFragment;
            case 3:
                // movies fragment
                ViewAssessments viewFragment = new ViewAssessments();
                return viewFragment;
            case 4:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;

            default:
                return new HomeFragment();
        }
    }
    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_assign_tasks:
                        // launch new intent instead of loading fragment
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_ASSIGN_TASKS;
                        break;
                    case R.id.nav_add:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_ADD_PATIENT;
                        break;
                    case R.id.nav_assessment:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_ASSESSMENTS;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivityDoctor.this, AboutusActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivityDoctor.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;

                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.toolbar_menu_doc, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Auth.GoogleSignInApi.revokeAccess(mClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if(status.getStatus().isSuccess()){
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);

                            }else{
                                String hi = status.getStatusMessage();
                                Toast.makeText(getApplicationContext(),"Cant logout",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }
    /*
   * Function to connect to the google fit API
   */
    private void buildClient() {
        if (mClient == null){
            //&& checkPermissions()) {
            mClient = new GoogleApiClient.Builder(this)
                    .addApi(Fitness.SENSORS_API)
                    .addApi(Fitness.CONFIG_API)
                    .addApi(Fitness.HISTORY_API)
                    .addApi(Fitness.RECORDING_API)
                    //.addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                    //.addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                    //.addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                    .addApi(Auth.GOOGLE_SIGN_IN_API,mgso)
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
                                    Toast.makeText(getApplicationContext(), "Connected!!!",Toast.LENGTH_LONG).show();
                                    // Now you can make calls to the Fitness APIs.
                                    //findFitnessDataSources();
                                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mClient);
                                    startActivityForResult(signInIntent, RC_SIGN_IN);
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    // If your connection to the sensor gets lost at some point,
                                    // you'll be able to determine the reason and react to it here.
                                    if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        Toast.makeText(getApplicationContext(), "Connection lost.  Cause: Network Lost.",Toast.LENGTH_LONG).show();
                                    } else if (i
                                            == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                        Toast.makeText(getApplicationContext(), "Connection lost.  Reason: Service Disconnected.",Toast.LENGTH_LONG).show();


                                    }
                                }
                            }
                    )
                    .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            //  Log.i(TAG, "Google Play services connection failed. Cause: " +
                            //          result.toString());
                            //Snackbar.make(
                            //      this.findViewById(R.id.main_activity_view),
                            //    "Exception while connecting to Google Play services: " +
                            //          result.getErrorMessage(),
                            //Snackbar.LENGTH_INDEFINITE).show();
                        }
                    })
                    .build();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mClient.connect();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mClient.disconnect();
    }

}
