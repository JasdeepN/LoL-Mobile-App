package uoit.csci4100u.mobileapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    static final String TAG = "Login.java";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }


    //attach the firebase listener to our FirbaseAuth instance
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    //removes listener when we are done
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    /**
     * Creates toast with google play services version, useful for troubleshooting
     */
    private void showGooglePlayServicesStatus() {
        GoogleApiAvailability apiAvail = GoogleApiAvailability.getInstance();
        int errorCode = apiAvail.isGooglePlayServicesAvailable(this);
        String msg = "Play Services: " + apiAvail.getErrorString(errorCode);
        Log.d(TAG, msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    /**
     * onClick method for logging in normally, uses the signIn() method
     *
     * @param v current Android view
     */
    public void onLoginClick(View v) {
      sendSignInCredentials();
    }

    private void sendSignInCredentials(){
        EditText email = (EditText) findViewById(R.id.email_field);
        EditText pass = (EditText) findViewById(R.id.pass_field);

        if(!isEmpty(email) && !isEmpty(pass)) {
            signIn(email.getText().toString(), pass.getText().toString());
        }
    }


    /**
     * onClick method for creating account, uses the createAccount() method, and input from EditText
     * if either username or pass is empty this does nothing
     *
     * @param v current Android view
     */
    public void onCreateClick(View v) {
        EditText email = (EditText) findViewById(R.id.email_field);
        EditText pass = (EditText) findViewById(R.id.pass_field);

        if(!isEmpty(email) && !isEmpty(pass)) {
            createAccount(email.getText().toString(), pass.getText().toString());
            Toast.makeText(this, "Making new Account...", Toast.LENGTH_SHORT).show();
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
     * Gets the current Firebase user
     */
    private void getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }
    }


    /**
     * Sign in method used for Firebase authentication
     *
     * @param email User's email
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
                        } else {
                            Toast.makeText(getBaseContext(), R.string.auth_success, Toast.LENGTH_SHORT)
                                    .show();

                            Intent intent = new Intent(Login.this, Main.class);
                            intent.putExtra("UUID", FirebaseAuth.getInstance().getCurrentUser()
                                    .getUid());
                            startActivityForResult(intent, 1);
                        }

                        //...
                    }
                });
    }

    /**
     * Creates new Firebase user
     *
     * @param email New user's email
     * @param password New user's password
     */
    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(Login.this, R.string.new_account_failure,
                                    Toast.LENGTH_SHORT).show();
                        }

                        sendSignInCredentials();
                        //... other things
                    }
                });
    }
}
