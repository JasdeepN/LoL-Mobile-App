package uoit.csci4100u.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uoit.csci4100u.mobileapp.util.DatabaseHelperUtil;
import uoit.csci4100u.mobileapp.util.NetworkTask;
import uoit.csci4100u.mobileapp.util.OnGetDataListener;
import uoit.csci4100u.mobileapp.util.PermissionChecker;

public class Login extends AppCompatActivity {
    static final String TAG = "Login.java";
    static final int REQUEST_MAIN = 1;
    static final int RESULT_LOGOUT = 0;
    static final String SUMMONER_REGEX = "^[0-9/\\/\\p{L} _\\/\\/.]+$";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static String current_version, region, mUUID;
    RiotApiTaskGetSummoner SummonerTask;
    RiotApiTaskGetLiveVersion VersionTask;
    String[] networkParam, locales;
    RiotApi riotApi;
    LinearLayout summoner_line, button_bar;
    EditText email, pass, summoner;
    TextView progress_label;
    ProgressBar progressBar;
    private Summoner userSummoner;
    boolean can_play;
    Button btnLogin;

    //database helper
    private DatabaseHelperUtil dbHelper;

    private static final int UPDATE_INTERVAL = 1000 * 60 * 5; //5 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.login);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        //checks for permissions
        new PermissionChecker(getBaseContext(), this).getPermissions();
        dbHelper = new DatabaseHelperUtil();

        progress_label = (TextView) findViewById(R.id.progress_label);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(3);
        progressBar.setVisibility(View.GONE);
        progress_label.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.email_field);
        pass = (EditText) findViewById(R.id.pass_field);
        summoner = (EditText) findViewById(R.id.summoner);

        summoner_line = (LinearLayout) findViewById(R.id.summoner_line);
        button_bar = (LinearLayout) findViewById(R.id.button_bar);
        summoner_line.setVisibility(View.GONE);
        btnLogin = (Button) findViewById(R.id.login_button);

        locales = getResources().getStringArray(R.array.Region_Array);
        setupSpinner();

        SummonerTask = new RiotApiTaskGetSummoner();
        VersionTask = new RiotApiTaskGetLiveVersion();
        riotApi = NetworkTask.getAPI();
        networkParam = new String[2];

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    mUUID = user.getUid();
                    dbHelper.setUUID(mUUID);
                    checkIfUserExists(mUUID);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * sets up spinner with resources from file, then puts the selected locale into the
     * networkParams array
     */
    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, locales);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner sItems = (Spinner) findViewById(R.id.locale_spinner);
        //Create listener for spinner including on item selected method
        sItems.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        region = (String) sItems.getSelectedItem();
                        Log.d(TAG, "id= " + region);
                        networkParam[0] = region;
                    }

                    //nothing will never be selected
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                        setResult(Main.CANCEL);
                    }
                });
        sItems.setAdapter(adapter);
    }

  /*  *//**
     * Creates toast with google play services version, useful for troubleshooting
     *//*
    private void showGooglePlayServicesStatus() {
        GoogleApiAvailability apiAvail = GoogleApiAvailability.getInstance();
        int errorCode = apiAvail.isGooglePlayServicesAvailable(this);
        String msg = "Play Services: " + apiAvail.getErrorString(errorCode);
        Log.d(TAG, msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }*/

    /**
     * onClick method for logging in normally, uses the signIn() method
     *
     * @param v current Android view
     * @see Login#signIn(String, String)
     */
    public void onLoginClick(View v) {
        sendSignInCredentials();

    }

    /**
     * Method handler for signIn method
     *
     * @see Login#signIn(String, String)
     */
    private void sendSignInCredentials() {
        if (!isEmpty(email) && !isEmpty(pass)) {
            signIn(email.getText().toString(), pass.getText().toString());
        }
    }

    /**
     * onClick method for creating account, uses the createAccount() method, and input from EditText
     * if either username or pass is empty this does nothing
     *
     * @param v current Android view
     * @see Login#createAccount(String, String)
     */
    public void onCreateClick(View v) {
        summoner_line.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        if (!isEmpty(email) && !isEmpty(pass) && !isEmpty(summoner)) {
            createAccount(email.getText().toString(), pass.getText().toString());
            Toast.makeText(this, "Making new Account...", Toast.LENGTH_SHORT).show();
        } else if (!isEmpty(email) && !isEmpty(pass) && isEmpty(summoner)) {
            Toast.makeText(this, "Enter a summoner name", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * checks if edit text field is empty
     *
     * @param etText EditTextField to be checked
     * @return True if the field is empty and False if it is
     */
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    /**
     * Sign in method used for Firebase authentication
     *
     * @param email    User's email
     * @param password User's password
     */
    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(getBaseContext(), R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * starts the async task to update the Summoner object from Riot's servers
     *
     * @param inputSummoner Summoner to update
     * @param Region String region of the Summoner
     */
    protected void updateSummoner(Summoner inputSummoner, String Region) {
        Log.d("updating", "updating existing user");
        networkParam[0] = Region;
        getSummoner(inputSummoner.getName());
    }

    /**
     * runs the network task to get Summoner object from Riot Games server
     *
     * @param name name of the Summoner
     * @return Summoner object from Riot's servers
     */
    public Summoner getSummoner(String name) {
        name = name.trim();
        if (name.matches(SUMMONER_REGEX)) {
            networkParam[1] = name;
            //start the async task with blocking for result
            try {
                return userSummoner = (SummonerTask.execute().get());
            } catch (java.util.concurrent.ExecutionException e) {
                Log.d(TAG, e.toString());
            } catch (java.lang.InterruptedException i) {
                Log.d(TAG + ":getSummoner", i.toString());
            }
        } else {
            Log.d(TAG, "regex fail");
        }
        Log.d("user update", "failed");
        return userSummoner;
    }

    /**
     * creates a map object with updated values for firebase
     *
     * @param updated updated summoner object
     * @param mRegion region for the user
     * @see   DatabaseHelperUtil#updateUser(Map<String, Object>)
     */
    protected void updateDB(Summoner updated, String mRegion) {
        Map<String, Object> temp = new HashMap<>();
        temp.put("summoner", updated);
        temp.put("can_play", can_play);
        temp.put("region", mRegion);
        temp.put("version", current_version);
        temp.put("last_update", System.currentTimeMillis());
        dbHelper.updateUser(temp);
    }

    /**
     * starts the async task to get game versions
     *
     * @param locale for the game version
     */
    private void updateVersion(String locale) {
        try {
            current_version = (VersionTask.execute(locale).get());
        } catch (java.util.concurrent.ExecutionException e) {
            Log.d(TAG, e.toString());
        } catch (java.lang.InterruptedException i) {
            Log.d(TAG + ":getVersion", i.toString());
        }
    }

    /**
     * Checks Firebase to see if there is a Summoner object saved with the users UUID, if it
     * finds it launches the run() method
     *
     * @param UUID unique user id
     * @see Login#updateSummoner(Summoner, String)
     * @see DatabaseHelperUtil#readDataSummoner(String, OnGetDataListener)
     * @see Login#startMain()
     */
    private void checkIfUserExists(final String UUID) {
        dbHelper.readDataSummoner(UUID, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Log.d("checkUser:db", "finish database read");
                Summoner temp = dataSnapshot.child("summoner").getValue(Summoner.class);
                try {
                    can_play = dataSnapshot.child("can_play").getValue(Boolean.class);
                } catch (java.lang.NullPointerException ne) {
                    can_play = false;
                }
                Long time = dataSnapshot.child("last_update").getValue(Long.class);
                region = dataSnapshot.child("region").getValue(String.class);
                current_version = dataSnapshot.child("version").getValue(String.class);
                Log.d("region", "got back " + region);
                Log.d("version", "got back " + current_version);

                Log.d("time", time + "");
                if (time != null && temp != null) {
                    if ((System.currentTimeMillis() - time) > UPDATE_INTERVAL) {
                        Log.d("update time", System.currentTimeMillis() - time + "");
                        updateSummoner(temp, region);
                        return;
                    } else {
                        Log.d("existing user", "found user; no update");
                        userSummoner = temp;
                        //for auto login uncomment this
                        startMain();
                    }
                } else if (time == null) {
                    Log.d("new user", "adding new user");
                    //TODO: update version here
                }
//                if (userSummoner != null) {
//                    Log.d("checkUser", "Starting main from check if user exists");
//                    startMain();
//                }
            }

            @Override
            public void onStart() {
                //when starting
                Log.d("checkUser:db", "Started database read");
            }

            @Override
            public void onFailure() {
                Log.d("onFailure", "Failed");
            }
        });
    }

    /**
     * Creates new Firebase user
     *
     * @param email    New user's email
     * @param password New user's password
     */
    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    Summoner newSummoner;

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(Login.this, R.string.new_account_failure,
                                    Toast.LENGTH_SHORT).show();
                            //email.setText("");
                            pass.setText("");
                            summoner.setText("");
                           summoner_line.setVisibility(View.INVISIBLE);
                           btnLogin.setEnabled(true);
                        } else {

                            mUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            dbHelper.setUUID(mUUID);
                            updateVersion(networkParam[0]);
                            newSummoner = getSummoner(summoner.getText().toString());

                            dbHelper.addUser(newSummoner, networkParam[0], current_version);
                            Log.d(TAG + " version", current_version + "");

                            userSummoner = newSummoner;
//                            Log.d(TAG + "createAccoun", "error no matching summoner");
                            //... other things
                        }
                    }
                });
    }

    /**
     * Extends NetworkTask so we can have access to the methods in it
     *
     * Queries the Riot API if a valid Summoner name is provided returns the Summoner
     */
    public class RiotApiTaskGetSummoner extends NetworkTask<Void, Integer, Summoner> {
        Summoner summoner;

        //TODO: remove logs when done testing this method

        @Override
        protected Summoner doInBackground(Void... input) {
            publishProgress(1);
            try {
                Log.d("networkParam 1", networkParam[0] + "");
                Log.d("networkParam 2", networkParam[1] + "");
                publishProgress(2);
                summoner = riot_api.getSummonerByName(checkPlatform(networkParam[0]), networkParam[1]);

            } catch (net.rithms.riot.api.RiotApiException e) {
                Log.d("RIOT API", e.toString());
            }
            publishProgress(3);
            return summoner;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
//            txt.setText("Running..."+ values[0]);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPreExecute() {
            Log.d("async:preExecute", "Starting async task get summoner");

            progressBar.setProgress(0);
            progress_label.setText(R.string.net_start);
            button_bar.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            progress_label.setVisibility(View.VISIBLE);
        }


        @Override
        public void onPostExecute(Summoner result) {
            Log.d("async:postExecute", "finished async task get summoner");
            if (result != null) {
                userSummoner = result;
                progress_label.setText(R.string.net_complete);

                button_bar.setVisibility(View.VISIBLE);
                updateDB(userSummoner, networkParam[0]);

                //Entry point to main
                Log.d("async:postExecute", "have everything; stating main");
                startMain();
            } else {
                progress_label.setText(R.string.net_error);
                button_bar.setVisibility(View.VISIBLE);
                Toast.makeText(getBaseContext(), "could not find that summoner", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Extends network task so we can access the internal methods
     *
     * gets all versions of the game in List<String> form
     */
    public class RiotApiTaskGetLiveVersion extends NetworkTask<String, Void, String> {
        List<String> versions;

        //TODO: remove logs when done testing this method

        @Override
        protected String doInBackground(String... input) {
            try {
                String locale = input[0];
                Log.d("networkParam 1", locale + "");
                versions = riot_api.getDataVersions(checkPlatform(networkParam[0]));
            } catch (net.rithms.riot.api.RiotApiException e) {
                Log.d("RIOT API", e.toString());
            }
            return versions.get(0);
        }

        @Override
        public void onPostExecute(String result) {
            Log.d("async:postExecute", "finished async task get version");
            if (result != null) {
                progress_label.setText(R.string.net_complete);

                button_bar.setVisibility(View.VISIBLE);
                current_version = result;

                Log.d("Version", current_version + "");
//                updateDB(userSummoner, networkParam[0]);
            } else {
                progress_label.setText(R.string.net_error);
                Toast.makeText(getBaseContext(), "could not complete get version", Toast
                        .LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            Log.d("async:preExecute", "Starting async task get version");
        }

    }

    //continue program execution
    private void startMain() {
        Intent intent = new Intent(Login.this, Main.class);
        intent.putExtra("UUID", mUUID);
        intent.putExtra("locale", networkParam[0]);
        intent.putExtra("version", current_version);
        Main.setDBHelper(dbHelper);
        Main.setSummoner(userSummoner);
//        Main.setDBRef(dbHelper.getUserRef(mUUID));
        startActivityForResult(intent, REQUEST_MAIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_LOGOUT) {
            mAuth.signOut();
        } else {
            Log.d(TAG, "other result");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
