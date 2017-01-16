package com.example.self_health.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.self_health.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.ConfigApi;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataTypeCreateRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.DataTypeResult;
import com.google.android.gms.fitness.FitnessStatusCodes;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by pc on 11/20/2016.
 */

public class LoginActivity extends AppCompatActivity {
    public GoogleApiClient mClient = null;
    private GoogleSignInOptions mgso;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;
    private String LoginId ,personName,personGivenName,personFamilyName,personEmail;
    private Uri uri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        
        //new code

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });



        // Connect with Gmail
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN
         mgso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                 //.requestScopes(new Scope(Scopes.FITNESS_LOCATION_READ))
                 //.requestScopes(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                 //.requestScopes(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                 .requestEmail()
                 .build();

        //Connect to google API
        buildClient();
        //Gmail sign in button
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    /*
    * Function to sign with Gmail
    */
    private void signIn() {
        boolean k = mClient.isConnected();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    /*
     * Function to handle sign in result of gmail
     */

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            mStatusTextView.setText("Sign in "+ acct.getDisplayName().toString());
            LoginId = acct.getId();
            personName= acct.getDisplayName();
            personGivenName = acct.getGivenName();
            personFamilyName = acct.getFamilyName();
            personEmail = acct.getEmail();
            uri = acct.getPhotoUrl();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.putExtra("personName",personName);
            intent.putExtra("personGivenName",personGivenName);
            intent.putExtra("personFamilyName",personFamilyName);
            intent.putExtra("personEmail",personEmail);
            intent.putExtra("ID",LoginId);
            intent.putExtra("uri",uri);
            //startActivity(intent);
            boolean j = mClient.isConnected();
            //ADDID();
            checkUserType();


        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this,"Wrong username or password",Toast.LENGTH_LONG).show();
        }
    }
    /*
    private void deleteStepDataOnGoogleFit() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataDeleteRequest request = new DataDeleteRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .addDataType(mPatientType)
                .build();

        Fitness.HistoryApi.deleteData(mClient, request).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

            }
        });
    }
*/


    /*
     * Function to connect to the google fit API
     */
    private void buildClient() {
        if (mClient == null){
            //&& checkPermissions()) {
            mClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API,mgso)
                    .addApi(Fitness.CONFIG_API)
                    .addApi(Fitness.HISTORY_API)
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
                                    Toast.makeText(getApplicationContext(), "Connected!!!",Toast.LENGTH_LONG).show();

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
                            Log.e(TAG, "Google Play services connection failed. Cause: " +
                                    result.toString());
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
    /*
     *  Authority use only
     */
    private void ADDID(){
        String ID;
        String Type;
        DataTypeCreateRequest request = new DataTypeCreateRequest.Builder()
                .setName("com.example.self_health.UserType")
                .addField("ID", Field.FORMAT_STRING)
                .addField("Type", Field.FORMAT_STRING)
                .build();
        // Checking if the CLient is connected
        if ( !mClient.isConnected() ){
            mClient.connect();
        }
        PendingResult<DataTypeResult> pendingResult =
                Fitness.ConfigApi.createCustomDataType(mClient, request);

        // 3. Check the result asynchronously
        // (The result may not be immediately available)
        pendingResult.setResultCallback(
                new ResultCallback<DataTypeResult>() {
                    @Override
                    public void onResult(DataTypeResult dataTypeResult) {
                        // Retrieve the created data type
                        DataType type = dataTypeResult.getDataType();
                        //creating a new data source
                        DataSource datasource = new DataSource.Builder()
                                .setAppPackageName(getApplicationContext())
                                .setDataType(type)
                                .setStreamName("type")
                                .setType(DataSource.TYPE_RAW)
                                .build();
                        String Type ="1";
                        DataSet dataSet = DataSet.create(datasource);
                        Calendar cal = Calendar.getInstance();
                        Date now = new Date();
                        cal.setTime(now);
                        long endTime = cal.getTimeInMillis();
                        cal.add(Calendar.HOUR_OF_DAY, -1);
                        long startTime = cal.getTimeInMillis();
                        DataPoint dataPoint = DataPoint.create(datasource);
                        dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(0)).setString(LoginId);
                        dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(1)).setString(Type);
                        dataPoint.setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
                        dataSet.add(dataPoint);

                        Toast.makeText(LoginActivity.this, "Adding ID", Toast.LENGTH_SHORT).show();
                        Fitness.HistoryApi.insertData(mClient, dataSet).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                // Before querying the data, check to see if the insertion succeeded.
                                if (!status.isSuccess()) {
                                    Toast.makeText(LoginActivity.this, "There was a problem with the adding the ID.", Toast.LENGTH_LONG).show();

                                } else {

                                    // At this point, the data has been inserted and can be read.
                                    Toast.makeText(LoginActivity.this, "Adding ID was successful!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });


                    }
                }
        );
    }

    /*
     * Check Type on user
     */
    private void checkUserType(){


        PendingResult<DataTypeResult> pendingResult =
                Fitness.ConfigApi.readDataType(mClient, "com.example.self_health.UserType");

       // 2. Check the result asynchronously
       // (The result may not be immediately available)
        pendingResult.setResultCallback(
                new ResultCallback<DataTypeResult>() {
                    @Override
                    public void onResult(DataTypeResult dataTypeResult) {
                        // Retrieve the custom data type
                        // If there's an error with API access (Status code unknown, from debugging)
                        if(dataTypeResult.getStatus().getStatusCode() ==  FitnessStatusCodes.DATA_TYPE_NOT_FOUND){
                            Toast.makeText(LoginActivity.this, "There was a problem with Login\nCheck OAuth 2.0 in Google API  ", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Problem with Logging in - OAuth Account");
                            return;
                        }
                        final DataType customType = dataTypeResult.getDataType();
                        Calendar cal = Calendar.getInstance();
                        Date now = new Date();
                        cal.setTime(now);
                        long endTime = cal.getTimeInMillis();
                        cal.add(Calendar.YEAR, -1);
                        long startTime = cal.getTimeInMillis();
                        DataReadRequest readRequest = new DataReadRequest.Builder()
                                                .read(customType)
                                                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();

                        Fitness.HistoryApi.readData(mClient, readRequest).setResultCallback(new ResultCallback<DataReadResult>() {
                           @Override
                           public void onResult(@NonNull DataReadResult dataReadResult) {
                               List<DataPoint> points =  dataReadResult.getDataSet(customType).getDataPoints();
                               String type = "";
                               for (DataPoint point : points){
                                   List<Field> fields = point.getDataType().getFields();
                                   Field field1 = fields.get(0);
                                   Field field2 = fields.get(1);
                                   String id = point.getValue(field1).asString();
                                   if(point.getValue(field1).asString().equals(LoginId)) {
                                       type = point.getValue(field2).toString();
                                       break;
                                   }
                              }
                               switch(type){

                                   case "0":{
                                              Intent intent = new Intent(getApplicationContext(),MainActivityDoctor.class);
                                              intent.putExtra("personName",personName);
                                              intent.putExtra("personGivenName",personGivenName);
                                              intent.putExtra("personFamilyName",personFamilyName);
                                              intent.putExtra("personEmail",personEmail);
                                              intent.putExtra("ID",LoginId);
                                              intent.putExtra("uri",uri);
                                              startActivity(intent);
                                              break;
                                          }
                                          case "1":{
                                              Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                              intent.putExtra("personName",personName);
                                              intent.putExtra("personGivenName",personGivenName);
                                              intent.putExtra("personFamilyName",personFamilyName);
                                              intent.putExtra("personEmail",personEmail);
                                              intent.putExtra("ID",LoginId);
                                              intent.putExtra("uri",uri);
                                              startActivity(intent);
                                              break;
                                          }
                                          default:{
                                              Toast.makeText(getApplicationContext(),"Your ID is not Valid Give to Doctor "+LoginId,Toast.LENGTH_LONG).show();
                                              break;
                                          }

                                      }
                                  }
                        });

                    }
                });
    }
    @Override
    protected void onResume() {
        super.onResume();

        // This ensures that if the user denies the permissions then uses Settings to re-enable
        // them, the app will start working.
        buildClient();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mClient.connect();
        /* @TODO crashes here when new user
        boolean k = mClient.isConnected();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
           Log.d(TAG, "Got cached sign-in");
           GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
           showProgressDialog();
           opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
               public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }*/
    }
    @Override
    protected void onStop() {
        super.onStop();
        mClient.disconnect();
    }
}
