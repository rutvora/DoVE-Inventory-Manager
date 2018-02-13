package com.bphc.dove;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MainActivity extends Activity {

    public static FirebaseAuth auth;
    public static String UID;
    public static FirebaseUser currentUser;
    public static FirebaseFirestore db;

    boolean allPermissionsGranted = TRUE;
    boolean needExplanation = FALSE;


    PhoneVerification verify = new PhoneVerification();
    MainFragment mainFragment = new MainFragment();

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        int i;
        boolean b = TRUE;
        for (i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) b = FALSE;
        }

        // If request is cancelled, the result arrays are empty.
        if (b) {

            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS && GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE > 11000)
                menuInflate();

            else
                Toast.makeText(this, "Please install/update Google Play Services", Toast.LENGTH_LONG).show();

        } else {
            //Take Lite
        }
        return;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS};

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this,
                    permissions[i])
                    != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = FALSE;
                break;
            }
        }
        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permissions[i])) {
                needExplanation = TRUE;
                break;
            }
        }
        //Check if the app has all permissions
        if (allPermissionsGranted == FALSE) {

            // Should we show an explanation?
            if (needExplanation) {
                Toast toast = Toast.makeText(this, "Need permissions for OTP verification", Toast.LENGTH_SHORT);
                toast.show();

                ActivityCompat.requestPermissions(this,
                        permissions,
                        1);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        permissions,
                        0);
            }
        } else {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS && GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE > 11000) {
                menuInflate();

            } else
                Toast.makeText(this, "Please install/update Google Play Services", Toast.LENGTH_LONG).show();
        }


    }

    public void menuInflate() {

        auth = FirebaseAuth.getInstance();
        UID = auth.getUid();

        currentUser = auth.getCurrentUser();


        if (currentUser == null) {

            getFragmentManager().beginTransaction().replace(R.id.fragment, verify).commit();
            Log.w("currentUser", "null");
        } else {
            currentUser.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {


                                getFragmentManager().beginTransaction().replace(R.id.fragment, mainFragment).commit();
                                // Send token to your backend via HTTPS
                                // ...
                            } else {
                                // Handle error -> task.getException();
                            }
                        }
                    });
        }

        db = FirebaseFirestore.getInstance();
        //Make the app fullscreen
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main);

        //Add Toolbar and set it as an actionbar
        /**Toolbar myToolbar = (Toolbar) findViewById(R.id.);
         setSupportActionBar(myToolbar);
         getSupportActionBar().setIcon(R.mipmap.ic_launcher);
         getSupportActionBar().setTitle("   myPata"); */

    }

}
