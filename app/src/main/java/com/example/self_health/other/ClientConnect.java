package com.example.self_health.other;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

/**
 * Created by pc on 12/25/2016.
 */

public class ClientConnect {
    private Context mcontext;
   public ClientConnect(Context context) {
        mcontext = context;
    }

    public GoogleApiClient build(GoogleApiClient mClient,  GoogleSignInOptions mgso) {
        if (mClient == null) {
            //&& checkPermissions()) {
            mClient = new GoogleApiClient.Builder(mcontext)
                    .addApi(Fitness.SENSORS_API)
                    .addApi(Fitness.CONFIG_API)
                    .addApi(Fitness.HISTORY_API)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, mgso)
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
                                    Toast.makeText(mcontext, "Connected!!!", Toast.LENGTH_LONG).show();
                                    // Now you can make calls to the Fitness APIs.
                                    //findFitnessDataSources();
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    // If your connection to the sensor gets lost at some point,
                                    // you'll be able to determine the reason and react to it here.
                                    if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        Toast.makeText(mcontext, "Connection lost.  Cause: Network Lost.", Toast.LENGTH_LONG).show();
                                    } else if (i
                                            == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                        Toast.makeText(mcontext, "Connection lost.  Reason: Service Disconnected.", Toast.LENGTH_LONG).show();


                                    }
                                }
                            }
                    )
                    .build();
        }
        return mClient;
    }
}
